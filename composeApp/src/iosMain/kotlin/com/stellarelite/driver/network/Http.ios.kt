package com.stellarelite.driver.network

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import platform.Foundation.setValue
import platform.posix.memcpy
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData {
    if (isEmpty()) return NSData()
    return usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
    }
}

actual suspend fun httpRequest(
    url: String,
    method: String,
    headers: Map<String, String>,
    body: String?
): HttpResponse = suspendCancellableCoroutine { cont ->
    val nsUrl = NSURL(string = url)
    if (nsUrl == null) {
        cont.resumeWithException(Exception("Invalid URL"))
        return@suspendCancellableCoroutine
    }
    val request = NSMutableURLRequest(uRL = nsUrl)
    request.HTTPMethod = method
    headers.forEach { (k, v) -> request.setValue(v, forHTTPHeaderField = k) }
    if (body != null) {
        request.HTTPBody = (body as NSString).dataUsingEncoding(NSUTF8StringEncoding)
    }
    val task = NSURLSession.sharedSession.dataTaskWithRequest(request) { data, response, error ->
        if (error != null) {
            cont.resumeWithException(Exception(error.localizedDescription))
        } else {
            val status = (response as? NSHTTPURLResponse)?.statusCode?.toInt() ?: 0
            val text = data?.let { NSString.create(data = it, encoding = NSUTF8StringEncoding) as String? } ?: ""
            cont.resume(HttpResponse(status, text))
        }
    }
    task.resume()
    cont.invokeOnCancellation { task.cancel() }
}

actual suspend fun httpUpload(
    url: String,
    headers: Map<String, String>,
    body: ByteArray
): HttpResponse = suspendCancellableCoroutine { cont ->
    val nsUrl = NSURL(string = url)
    if (nsUrl == null) {
        cont.resumeWithException(Exception("Invalid URL"))
        return@suspendCancellableCoroutine
    }
    val request = NSMutableURLRequest(uRL = nsUrl)
    request.HTTPMethod = "POST"
    headers.forEach { (k, v) -> request.setValue(v, forHTTPHeaderField = k) }
    request.HTTPBody = body.toNSData()
    val task = NSURLSession.sharedSession.dataTaskWithRequest(request) { data, response, error ->
        if (error != null) {
            cont.resumeWithException(Exception(error.localizedDescription))
        } else {
            val status = (response as? NSHTTPURLResponse)?.statusCode?.toInt() ?: 0
            val text = data?.let { NSString.create(data = it, encoding = NSUTF8StringEncoding) as String? } ?: ""
            cont.resume(HttpResponse(status, text))
        }
    }
    task.resume()
    cont.invokeOnCancellation { task.cancel() }
}
