package com.example.eazyfind.ui.utils

fun normalizeCity(value: String): String {
    return value
        .lowercase()
        .trim()
        .replace("-", " ")
        .replace("_", " ")
        .replace(Regex("\\s+"), " ")
}

fun mapCityForApi(city: String): String {
    return when (normalizeCity(city)) {

        // Delhi NCR
        "delhi", "new delhi", "delhi ncr",
        "gurgaon", "noida", "faridabad" -> "delhi-ncr"

        // Chandigarh Tricity
        "chandigarh", "chandigarh tricity",
        "mohali", "panchkula" -> "chandigarh-tricity"

        else -> normalizeCity(city)
    }
}
