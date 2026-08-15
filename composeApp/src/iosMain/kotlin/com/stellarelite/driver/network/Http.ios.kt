package com.stellarelite.driver.network

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import platform.Foundation.setValue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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
