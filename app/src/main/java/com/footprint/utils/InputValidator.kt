package com.footprint.utils

/**
 * 输入验证结果
 */
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    fun errorMessage(): String? {
        return if (this is Error) message else null
    }
}

/**
 * 输入验证器
 */
object InputValidator {

    /**
     * 验证非空字符串
     */
    fun validateNotEmpty(value: String, fieldName: String): ValidationResult {
        return if (value.isBlank()) {
            ValidationResult.Error("$fieldName 不能为空")
        } else {
            ValidationResult.Success
        }
    }

    /**
     * 验证标题
     */
    fun validateTitle(title: String): ValidationResult {
        return when {
            title.isBlank() -> ValidationResult.Error("请输入标题")
            title.length > 100 -> ValidationResult.Error("标题长度不能超过100个字符")
            else -> ValidationResult.Success
        }
    }

    /**
     * 验证位置
     */
    fun validateLocation(location: String): ValidationResult {
        return when {
            location.isBlank() -> ValidationResult.Error("请输入位置")
            location.length > 200 -> ValidationResult.Error("位置长度不能超过200个字符")
            else -> ValidationResult.Success
        }
    }

    /**
     * 验证距离
     */
    fun validateDistance(distance: Float): ValidationResult {
        return when {
            distance < 0 -> ValidationResult.Error("距离不能为负数")
            distance > 10000 -> ValidationResult.Error("距离过大，请检查输入")
            else -> ValidationResult.Success
        }
    }

    /**
     * 验证体力等级 (1-5)
     */
    fun validateEnergyLevel(energy: Int): ValidationResult {
        return when {
            energy !in 1..5 -> ValidationResult.Error("体力等级必须在1-5之间")
            else -> ValidationResult.Success
        }
    }

    /**
     * 验证标签
     */
    fun validateTags(tags: List<String>): ValidationResult {
        return when {
            tags.size > 10 -> ValidationResult.Error("标签数量不能超过10个")
            tags.any { it.length > 20 } -> ValidationResult.Error("单个标签长度不能超过20个字符")
            else -> ValidationResult.Success
        }
    }

    /**
     * 组合多个验证结果
     */
    fun combine(vararg results: ValidationResult): ValidationResult {
        val errors = results.filterIsInstance<ValidationResult.Error>()
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors.joinToString("\n") { it.message })
        }
    }
}
