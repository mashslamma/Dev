package com.example.digitalcardenterprise.util

object WalletPassManager {
    fun generatePassUrl(profile: com.example.digitalcardenterprise.data.UserProfile): String =
        "https://wallet.digitalcardenterprise.com/pass/${profile.uid}"
}
