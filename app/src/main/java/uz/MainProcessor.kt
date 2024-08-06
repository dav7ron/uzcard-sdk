package uz

interface MainProcessor {

    fun startProcess(
        sdkId: String,
        deviceId: String,
        phoneNumber: String,
        maskedPan: String,
        listener: MainProcessorErrorListener,
    )

    fun checkStatus(
        deviceId: String,
        phoneNumber: String,
        maskedPan: String,
        listener: MainProcessorErrorListener,
    )
}