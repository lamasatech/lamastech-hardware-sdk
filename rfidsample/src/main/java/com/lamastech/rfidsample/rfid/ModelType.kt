package com.lamastech.rfidsample.rfid


sealed class ModelType(
    val name: List<String>,
    val serial: SerialPath
) {

    companion object{
        fun detect(): ModelType {
            val model = android.os.Build.MODEL
            if (RK3568.name.any { it == model }) {
                return RK3568
            }
            if (Visipoint15.name.any { it == model }) {
                return Visipoint15
            }
            if (S3568.name.any { it == model } ||
                model.contains("3568",true)) {
                return S3568
            }
            if (RK3576.name.any { it == model } ||
                model.contains("RK3576", true)) {
                return RK3576
            }
            if (Zentron.name.any { it == model } ||
                model.startsWith("rk3288") ||
                model.contains("zentron", true)
            ) {
                return Zentron
            }
            return DefaultSMDT
        }
    }

    data object Zentron : ModelType(
        listOf("rk3288", "LT-Zentron8", "LT-Zentron15", "LD-AITemp", "rk3288_tdx"),
        SerialPath.ttyS1
    )

    data object Visipoint15 : ModelType(listOf("Visipoint 15"), SerialPath.ttyS4)
    data object DefaultSMDT : ModelType(listOf("SMDT"), SerialPath.ttyS3)

    data object S3568 : ModelType(
        listOf(
            "VersiV1s3568",
            "VersiV2s3568",
            "MuroDv1s3568",
            "MuroDv2s3568",
            "CanvasV2s3568",
            "MuroM2-43-V3s3568"
        ),
        SerialPath.ttyS3
    )

    object RK3568 : ModelType(listOf("Zentron_5"), SerialPath.ttyS3)
    object RK3576 : ModelType(listOf("LT-ACCRK3576-poe"), SerialPath.ttyS3)
}


