package uz.connection.models

data class SDKRegisterCardResponse(
    val id: Int? = null,
    val pan: String? = null,
    val fullExpiryDate: String? = null,
    val token: String? = null,
    val tokenExpiryDate: String? = null,
    val name: String? = null,
    val isDefault: Boolean? = false,
    val atc: String? = null,
    val serviceCode: String? = null,
    val appAffectiveDate: String? = null,
    val balance: Long? = null,
    val currency: String? = null,
    val cardholderName: String? = null,
    val discretionaryData: String? = null,
    val discretionaryDataAes: String? = null,
    val activated: Boolean? = false,
    val par: String? = null,
    val cardType: String? = null,
    val issuerApplicationData: IccCertificateParametersResponse? = null,
    val status: String? = null,
    val pinSetRequired: Boolean? = false,
)

data class IccCertificateParametersResponse(
    var issuerCertificate: String? = null,
    var caCertificateIndex: String? = null,
    var issuerPublicKeyExponent: String? = null,
    var issuerPublicKeyRemainder: String? = null,
    var cardPrivateKeyExponentKek: String? = null,
    var cardPrivateKeyModulusKek: String? = null,
    var cardIccCertificate: String? = null,
    var cardIccPublicKeyExponent: String? = null,
    var cardIccPublicKeyModulus: String? = null,
)
