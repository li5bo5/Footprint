package com.footprint.ui.screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.footprint.service.LocationTrackingService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

/**
 * 地图追踪屏幕
 * 显示实时位置和轨迹路径
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 位置权限
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // 追踪状态
    val isTracking by LocationTrackingService.isTracking.collectAsState()
    val currentLocation by LocationTrackingService.currentLocation.collectAsState()
    val trackingPath by LocationTrackingService.trackingPath.collectAsState()

    // 相机位置
    val cameraPositionState = rememberCameraPositionState {
        // 默认位置：北京
        position = CameraPosition.fromLatLngZoom(
            LatLng(39.9042, 116.4074),
            12f
        )
    }

    // 更新相机跟随当前位置
    LaunchedEffect(currentLocation) {
        currentLocation?.let { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            cameraPositionState.animate(
                update = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    15f
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (locationPermissions.allPermissionsGranted) {
            // 地图视图
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = true
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = true,
                    compassEnabled = true
                )
            ) {
                // 当前位置标记
                currentLocation?.let { location ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(location.latitude, location.longitude)
                        ),
                        title = "当前位置",
                        snippet = "精度: ${location.accuracy.toInt()}m"
                    )
                }

                // 轨迹路径
                if (trackingPath.isNotEmpty()) {
                    val pathPoints = trackingPath.map {
                        LatLng(it.latitude, it.longitude)
                    }

                    Polyline(
                        points = pathPoints,
                        color = androidx.compose.ui.graphics.Color(0xFF2196F3),
                        width = 8f
                    )

                    // 起点标记
                    trackingPath.firstOrNull()?.let { start ->
                        Marker(
                            state = MarkerState(
                                position = LatLng(start.latitude, start.longitude)
                            ),
                            title = "起点",
                            icon = com.google.android.gms.maps.model.BitmapDescriptorFactory
                                .defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN)
                        )
                    }
                }
            }

            // 统计信息卡片
            if (isTracking && trackingPath.isNotEmpty()) {
                TrackingStatsCard(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    pointCount = trackingPath.size,
                    distance = calculatePathDistance(trackingPath)
                )
            }

            // 控制按钮
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 开始/停止追踪按钮
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            if (isTracking) {
                                LocationTrackingService.stopTracking(context)
                            } else {
                                LocationTrackingService.startTracking(context)
                            }
                        }
                    },
                    containerColor = if (isTracking) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                ) {
                    Icon(
                        imageVector = if (isTracking) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isTracking) "停止追踪" else "开始追踪"
                    )
                }

                // 追踪状态文本
                Text(
                    text = if (isTracking) "追踪中..." else "点击开始追踪",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            // 权限请求界面
            PermissionRequestScreen(
                onRequestPermissions = { locationPermissions.launchMultiplePermissionRequest() }
            )
        }
    }
}

@Composable
fun TrackingStatsCard(
    modifier: Modifier = Modifier,
    pointCount: Int,
    distance: Float
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 记录点数
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$pointCount",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "位置点",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 总距离
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formatDistance(distance),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "总距离",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PermissionRequestScreen(
    onRequestPermissions: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.LocationOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "需要位置权限",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "为了追踪您的足迹，需要访问设备位置",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRequestPermissions) {
            Text("授予权限")
        }
    }
}

// 辅助函数：计算路径距离
private fun calculatePathDistance(locations: List<android.location.Location>): Float {
    if (locations.size < 2) return 0f
    var totalDistance = 0f
    for (i in 0 until locations.size - 1) {
        totalDistance += locations[i].distanceTo(locations[i + 1])
    }
    return totalDistance
}

// 辅助函数：格式化距离显示
private fun formatDistance(meters: Float): String {
    return when {
        meters < 1000 -> "${meters.toInt()} 米"
        else -> String.format("%.2f 公里", meters / 1000)
    }
}
