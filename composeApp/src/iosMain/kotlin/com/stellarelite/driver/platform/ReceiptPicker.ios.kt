package com.stellarelite.driver.platform

import kotlinx.cinterop.*
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.lastPathComponent
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTTypeData
import platform.darwin.NSObject
import platform.posix.memcpy

private class ImagePickerDelegate(
    private val onResult: (PickedReceipt?) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        val result: PickedReceipt? = if (image != null) {
            val data = UIImageJPEGRepresentation(image, 0.8)
            if (data != null) PickedReceipt("receipt.jpg", data.toByteArray(), "image/jpeg") else null
        } else null
        picker.dismissViewControllerAnimated(true, null)
        onResult(result)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, null)
        onResult(null)
    }
}

private class DocumentPickerDelegate(
    private val onResult: (PickedReceipt?) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>
    ) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        val result: PickedReceipt? = if (url != null) {
            val data = url.dataWithContentsOfURL()
            if (data != null) {
                PickedReceipt(url.lastPathComponent ?: "receipt", data.toByteArray(), "application/octet-stream")
            } else null
        } else null
        controller.dismissViewControllerAnimated(true, null)
        onResult(result)
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        controller.dismissViewControllerAnimated(true, null)
        onResult(null)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val bytes = ByteArray(size)
    if (size > 0) {
        bytes.usePinned { pinned ->
            memcpy(pinned.addressOf(0), this.bytes, length)
        }
    }
    return bytes
}

private var imageDelegate: ImagePickerDelegate? = null
private var documentDelegate: DocumentPickerDelegate? = null

actual fun launchReceiptPicker(source: ReceiptSource, onResult: (PickedReceipt?) -> Unit) {
    val root = UIApplication.sharedApplication.keyWindow?.rootViewController
    if (root == null) { onResult(null); return }

    when (source) {
        ReceiptSource.Camera, ReceiptSource.Gallery -> {
            val picker = UIImagePickerController().apply {
                sourceType = if (source == ReceiptSource.Camera)
                    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                else
                    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
            }
            val delegate = ImagePickerDelegate(onResult)
            imageDelegate = delegate
            picker.delegate = delegate
            root.presentViewController(picker, animated = true, completion = null)
        }
        ReceiptSource.File -> {
            val picker = UIDocumentPickerViewController(forOpeningContentTypes = listOf(UTTypeData))
            val delegate = DocumentPickerDelegate(onResult)
            documentDelegate = delegate
            picker.delegate = delegate
            root.presentViewController(picker, animated = true, completion = null)
        }
    }
}
