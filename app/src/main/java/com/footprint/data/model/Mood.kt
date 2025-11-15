package com.footprint.data.model

import androidx.compose.ui.graphics.Color
import com.footprint.ui.theme.AccentOrange
import com.footprint.ui.theme.AccentPurple
import com.footprint.ui.theme.AccentTeal
import com.footprint.ui.theme.PrimaryBlue

enum class Mood(val label: String, val color: Color) {
    EXCITED("激情", AccentOrange),
    CURIOUS("探索", AccentTeal),
    RELAXED("放松", PrimaryBlue),
    REFLECTIVE("思考", AccentPurple);

    companion object {
        fun fromLabel(label: String): Mood = entries.firstOrNull { it.name == label || it.label == label }
            ?: RELAXED
    }
}
