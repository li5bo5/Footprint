package com.footprint.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onSave: (GoalDraft) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("想要达成的体验…") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(System.currentTimeMillis())

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("取消") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val selectedDate = datePickerState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    } ?: LocalDate.now().plusWeeks(2)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        GoalDraft(
                            title = title,
                            location = location,
                            date = selectedDate,
                            notes = notes
                        )
                    )
                },
                enabled = title.isNotBlank() && location.isNotBlank()
            ) { Text("保存目标") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
        title = { Text("创建旅程目标") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("目标名称") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("目的地") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("灵感/资源") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                Button(onClick = { showDatePicker = true }) {
                    Text("目标日期：$selectedDate")
                }
            }
        }
    )
}

data class GoalDraft(
    val title: String,
    val location: String,
    val date: LocalDate,
    val notes: String
)
