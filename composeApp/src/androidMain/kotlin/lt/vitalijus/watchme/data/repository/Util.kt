package lt.vitalijus.watchme.data.repository

import kotlinx.coroutines.CancellationException
import kotlin.coroutines.cancellation.CancellationException as CoroutineCancellationException

/**
 * A custom `runCatching` that does not catch `CancellationException`.
 * This prevents the accidental swallowing of cancellation signals in coroutines.
 *
 * @param block The suspendable block of code to execute.
 * @return A `Result` object that is either a `Success` containing the result of the block,
 * or a `Failure` containing the exception, unless it was a `CancellationException`.
 */
@Suppress("TooGenericExceptionCaught")
inline fun <T> runCatchingCancellable(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CoroutineCancellationException) {
        // Re-throw CancellationException to ensure coroutine cancellation is handled correctly.
        throw e
    } catch (e: CancellationException) {
        // Also handle the Java `CancellationException`
        throw e
    } catch (e: Throwable) {
        // Catch all other exceptions and wrap them in a Failure result.
        Result.failure(e)
    }
}
