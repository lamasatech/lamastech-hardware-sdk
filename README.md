# Lamasa Kiosk Hardware SDK

Android SDK for controlling Lamasa kiosk hardware. Provides a unified API for device management, display control, networking, GPIO, and peripherals across all supported kiosk models.

## Table of Contents

- [How to Add Lib to Your Project](#how-to-add-lib-to-your-project)
- [How to Use](#how-to-use)
- [API Reference](#api-reference)
  - [Power Management](#power-management)
  - [Display & Brightness](#display--brightness)
  - [LED Indicators](#led-indicators)
  - [System UI](#system-ui)
  - [GPIO & Relay Control](#gpio--relay-control)
  - [Package Management](#package-management)
  - [Launcher & Boot App](#launcher--boot-app)
  - [Network](#network)
  - [Ethernet](#ethernet)
  - [USB & Storage](#usb--storage)
  - [Screenshot](#screenshot)
  - [Firmware & System](#firmware--system)
  - [System Settings](#system-settings)
- [Supported Devices](#supported-devices)
- [Support Matrix](#support-matrix)
- [Error Handling](#error-handling)
- [Error Codes](#error-codes)
- [ProGuard](#proguard)
- [Troubleshooting](#troubleshooting)

---

## How to Add Lib to Your Project

1. Add the AAR file to your app's `libs/` directory
2. Inside `build.gradle` add:

```kotlin
implementation(files("libs/kioskhardware-release.aar"))
```

3. Create an `App` class to initialize the SDK:

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DeviceManager.instance?.init(this)
    }
}
```

Or you can use AndroidX Startup:

```kotlin
class MainStartup : Initializer<Unit> {
    override fun create(context: Context) {
        DeviceManager.instance?.init(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
```

---

## How to Use

```kotlin
DeviceManager.instance?.device?.// ANY Function
```

We recommend using the Device object with dependency injection:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @[Singleton Provides]
    fun provideDevice(): Device {
        return DeviceManager.instance?.device!!
    }
}
```

---

## API Reference

### Power Management

---

```kotlin
fun reboot(context: Context): Int
```
Restart the device immediately.

---

```kotlin
fun turnOff(context: Context): Int
```
Shut down the device.

---

```kotlin
fun scheduleReboot(context: Context, delay: Long)
```
Schedule a reboot after a delay.

| Parameter | Description |
|:--|:--|
| delay | Delay in **seconds** |

---

```kotlin
fun setTurnOffOnAlarm(offTime: String, onTime: String)
```
Set a daily power off/on schedule.

| Parameter | Description |
|:--|:--|
| offTime | Turn off time in 24h format, e.g. `"22:00"` |
| onTime | Turn on time in 24h format, e.g. `"08:00"` |

---

```kotlin
fun setAutoPowerOnOff(
    enable: Boolean,
    week: IntArray?,
    onHour: Int, onMinute: Int,
    offHour: Int, offMinute: Int
)
```
Set scheduled power on/off with weekly recurrence.

| Parameter | Description |
|:--|:--|
| enable | `true` to enable, `false` to disable |
| week | 7 elements (Sun-Sat), `1` = enabled, `0` = disabled. Example: `intArrayOf(1,1,1,1,1,0,0)` for Mon-Fri |
| onHour / onMinute | Power on time (24h format) |
| offHour / offMinute | Power off time (24h format) |

---

### Display & Brightness

---

```kotlin
fun setBrightness(context: Context, brightness: Int)
```
Set screen brightness.

| Parameter | Description |
|:--|:--|
| brightness | Brightness value. Range depends on device: some models accept **0-100**, others accept **0-255**. Higher value = brighter. |

---

```kotlin
fun setLcdBrightness(screenId: Int, brightness: Int): Int
```
Set brightness per screen (dual-screen support).

| Parameter | Description |
|:--|:--|
| screenId | `0`: Main screen, `1`: Secondary screen |
| brightness | Brightness value (**0-255**). Higher value = brighter. |

---

```kotlin
fun setLcdBackLight(brightness: Int): Int
```
Set primary screen backlight brightness.

| Parameter | Description |
|:--|:--|
| brightness | Backlight brightness value (**0-255**) |

---

```kotlin
fun setEDPBackLight(brightness: Int): Int
```
Set secondary screen (EDP) backlight brightness.

| Parameter | Description |
|:--|:--|
| brightness | Backlight brightness value (**0-255**) |

---

```kotlin
fun setRotation(rotationDegree: String?)
```
Set screen rotation.

| Parameter | Description |
|:--|:--|
| rotationDegree | `"0"`, `"90"`, `"180"`, or `"270"` |

---

```kotlin
fun setDisplayRotation(screenId: Int, degree: Int)
```
Set rotation per screen.

| Parameter | Description |
|:--|:--|
| screenId | `0`: Main screen, `1`: Secondary screen |
| degree | `0`, `90`, `180`, or `270` |

---

```kotlin
fun getDisplayRotation(screenId: Int): Int
```
Get current rotation angle for a screen.

| Return | Description |
|:--|:--|
| Int | Rotation angle: `0`, `90`, `180`, or `270` |

---

```kotlin
fun setLcdBackLightEnable(screenId: Int, enable: Boolean): Int
```
Turn screen backlight on or off.

| Parameter | Description |
|:--|:--|
| screenId | `0`: Main screen, `1`: Secondary screen |
| enable | `true`: on, `false`: off |

---

```kotlin
fun getLcdBackLightEnable(screenId: Int): Int
```
Get screen backlight status.

| Return | Description |
|:--|:--|
| Int | `1`: on, `0`: off |

---

```kotlin
fun setTimeOut(timeout: Int, callback: (Result<Boolean>) -> Unit)
```
Set screen timeout (auto-sleep).

| Parameter | Description |
|:--|:--|
| timeout | Value in milliseconds (e.g. `60000` = 1 minute). `0` = never sleep |
| callback | Result callback |

---

### LED Indicators

> Check `Model.isSupportLed` before calling LED methods.

---

```kotlin
fun toggleBlueLight(value: Int): Int
fun toggleRedLight(value: Int): Int
fun toggleWhiteLight(value: Int): Int
```
Toggle individual LED lights. `1` = on, `0` = off.

---

```kotlin
fun turnOnGreenLight()
fun turnOnRedLight()
fun turnOnWhiteLight()
```
Turn on a specific LED color.

---

```kotlin
fun turnOffLight()
```
Turn off all LED lights.

---

```kotlin
fun turnOffRedGreenLight()
```
Turn off red and green lights only.

---

```kotlin
fun isSupportBlue(): Boolean
fun isSupportRed(): Boolean
```
Check if blue/red LED is supported by the device firmware.

---

### System UI

---

```kotlin
fun setStatusBar(context: Context, enable: Boolean): Int
```
Show or hide the status bar. `true` = show, `false` = hide.

---

```kotlin
fun setStatusBarDrag(enable: Boolean): Int
```
Enable or disable status bar pull-down. `true` = allow drag, `false` = disable.

---

```kotlin
fun setNavigationBar(enable: Boolean): Int
```
Show or hide the navigation bar. `true` = show, `false` = hide.

---

```kotlin
fun setGestureBar(enable: Boolean): Int
```
Enable or disable gesture navigation. `true` = enable, `false` = disable.

---

### GPIO & Relay Control

---

```kotlin
fun getIOPortStatus(port: Int): Int
```
Read the status of a GPIO port.

---

```kotlin
fun getGpioDirection(gpioNumber: Int): Int
```
Get the direction of a GPIO pin.

---

```kotlin
fun setGpioDirection(port: Int, direction: Int, value: Int): Int
```
Set GPIO direction and value.

---

```kotlin
fun setRelayIoMode(mode: Int, delay: Int): Int
```
Set relay I/O mode.

| Parameter | Description |
|:--|:--|
| mode | Relay mode |
| delay | Delay in seconds |

---

```kotlin
fun getRelayIoMode(): Int
fun setRelayIoValue(value: Int): Int
fun getRelayIoValue(): Int
```
Get/set relay I/O state.

---

```kotlin
fun applyOpenRelay(mode: ModeOptions, delaySec: Int)
```
Open relay (e.g. unlock a door) for a duration.

| Parameter | Description |
|:--|:--|
| mode | `ModeOptions.MODE0_LOW`, `MODE0_HIGH`, `MODE1`, or `MODE2` |
| delaySec | Duration in seconds |

---

```kotlin
fun applyCloseRelay(mode: ModeOptions)
```
Close relay (e.g. lock a door).

---

### Package Management

---

```kotlin
fun silentInstall(context: Context, path: String)
```
Install an APK silently.

| Parameter | Description |
|:--|:--|
| path | Full path to the APK file |

---

```kotlin
fun silentUninstall(packageId: String)
```
Uninstall an app silently.

| Parameter | Description |
|:--|:--|
| packageId | Package name of the app to uninstall |

---

```kotlin
fun setAllowUninstall(allowed: Boolean)
```
Allow or block app uninstallation on the device. `false` for kiosk lockdown.

---

### Launcher & Boot App

---

```kotlin
fun setDefaultLauncher(context: Context, packageName: String): Int
```
Set an app as the default home launcher.

---

```kotlin
fun setDefaultLauncher(componentName: ComponentName): Int
```
Set a specific activity as the default home launcher.

---

```kotlin
fun getDefaultLauncher(context: Context): String?
```
Get the current default launcher package name.

---

```kotlin
fun setSystemBootApp(packageName: String): Int
```
Set an app to auto-start on device boot.

- If the app has no launcher icon, specify the class name: `com.example.app/.MainActivity`

---

```kotlin
fun getSystemBootApp(): String?
```
Get the current auto-start app package name.

---

```kotlin
fun setDaemonsActivity(packageName: String?, timeMillisecond: Long, broadcastEnable: Boolean)
```
Set up a daemon to keep an app alive (auto-restart if killed).

| Parameter | Description |
|:--|:--|
| packageName | Package name to guard. Use `/` to specify class: `com.example.app/.MainActivity` |
| timeMillisecond | Interval to check and restart the app (in milliseconds) |
| broadcastEnable | Whether to send a broadcast (`android.app.smdt.PROTECT_CHECK`) when the app exits |

---

```kotlin
fun getDaemonsActivity(): String?
```
Get the currently guarded app package name.

---

### Network

---

```kotlin
fun netGetCurrentNetType(): String?
```
Get the current network connection type.

| Return | Description |
|:--|:--|
| `"WIFI"` | Connected via WiFi |
| `"ETH"` | Connected via Ethernet |
| `"MOBILE"` | Connected via mobile network |
| `"UNKNOWN"` | Unknown or not connected |

---

```kotlin
fun netGetMacAddress(type: String?): String?
```
Get the MAC address of a network interface.

| Parameter | Description |
|:--|:--|
| type | `"eth0"`: Ethernet, `"eth1"`: Ethernet 1, `"wlan0"`: WiFi |

---

```kotlin
fun netSetNetWork(type: String?, enable: Boolean): Int
```
Enable or disable a network interface.

| Parameter | Description |
|:--|:--|
| type | `"eth0"`, `"eth1"`, `"wlan0"`, or `"mobile"` |
| enable | `true`: enable, `false`: disable |

---

```kotlin
fun netGetNetWork(type: String?): Int
```
Get network switch status. Returns `1` = open, `0` = closed.

---

```kotlin
fun netSetNetWorkModel(type: String?, model: Int, ip: String?, gaw: String?, mask: String?, dns1: String?, dns2: String?): Int
```
Set network connection mode (static or dynamic).

| Parameter | Description |
|:--|:--|
| type | `"eth0"`, `"eth1"`, `"wlan0"`, or `"mobile"` |
| model | `0`: dynamic (DHCP), `1`: static |
| ip, gaw, mask, dns1, dns2 | Static mode parameters |

> For WiFi, this modifies the currently connected network. WiFi must be connected first.

---

```kotlin
fun netGetNetWorkModel(type: String?): Int
```
Get network connection mode. Returns `0` = dynamic, `1` = static.

---

```kotlin
fun netGetWifiRssi(level: Int): Int
```
Get WiFi signal strength level.

| Parameter | Description |
|:--|:--|
| level | Number of levels to divide signal into (e.g. `5` for 5 levels) |

---

```kotlin
fun netSetWifiAp(enable: Boolean): Int
```
Enable or disable WiFi hotspot.

> WiFi hotspot and WiFi are mutually exclusive. Enabling hotspot will automatically turn off WiFi.

---

```kotlin
fun netGetWifiAp(): Int
```
Get WiFi hotspot status. Returns `1` = open, `0` = closed.

---

```kotlin
fun netSetWifiConnect(account: String?, pwd: String?, type: Int, mode: Int, info: NetworkInfo?): Int
```
Connect to a WiFi network.

| Parameter | Description |
|:--|:--|
| account | WiFi SSID |
| pwd | Password |
| type | Encryption: `0` = Open, `1` = WEP, `2` = WPA/WPA2 |
| mode | `0`: dynamic (DHCP), `1`: static |
| info | Static IP configuration (required when mode = 1) |

---

```kotlin
fun netGetNetWorkInf(type: String?): NetworkInfo?
```
Get current network information (IP, gateway, mask, DNS).

---

```kotlin
fun netGetImeiNumber(): String?
fun netGetIccidNumber(): String?
fun netGetImsiNumber(): String?
```
Get mobile network identifiers (IMEI, ICCID, IMSI).

---

```kotlin
fun setMobileDataEnabled(context: Context?, enabled: Boolean): Boolean
```
Enable or disable mobile data.

---

```kotlin
fun netSetNetworkProtect(enable: Boolean, type: Int, time: Long, ipInternet: String?, ipIntranet: String?, logPath: String?, reboot: Boolean): Int
```
Set up network guard/watchdog to automatically detect and repair network issues.

| Parameter | Description |
|:--|:--|
| enable | `true`: enable, `false`: disable |
| type | `0`: Automatic, `1`: Ethernet, `2`: WiFi, `3`: Mobile |
| time | Re-diagnosis interval (min 3 minutes) |
| ipInternet | External IP to ping |
| ipIntranet | Internal IP to ping |
| logPath | Log file path |
| reboot | `true`: restart device on repair failure, `false`: enter sleep mode |

---

```kotlin
fun netGetNetworkProtectEnable(): Int
```
Get network guard status. Returns `1` = enabled, `0` = disabled.

---

```kotlin
fun netGetNetworkProtectConfig(): List<String?>?
```
Get network guard config. Returns array: `[type, interval, external IP, internal IP, log path, reboot flag]`.

---

```kotlin
fun netSetNetworkPriority(types: Array<String?>?): Int
```
Set network priority (takes effect after reboot).

| Parameter | Description |
|:--|:--|
| types | Priority from high to low, e.g. `arrayOf("eth0", "wlan0", "mobile")` |

---

```kotlin
fun netGetNetworkPriority(): Array<String?>?
```
Get current network priority order.

---

```kotlin
fun netSetNetworkMultiEnable(enable: Boolean, callback: (Result<Int>) -> Unit)
```
Enable or disable multi-network coexistence (takes effect after reboot).

> Use with network priority. Put the external network type first.

---

```kotlin
fun netGetNetworkMultiEnable(callback: (Result<Int>) -> Unit)
```
Get multi-network coexistence status.

---

### Ethernet

---

```kotlin
fun setEthernetState(enable: Boolean)
```
Enable or disable the ethernet interface.

---

```kotlin
fun getEthernetState(): Boolean
```
Get ethernet state. Returns `true` = enabled.

---

```kotlin
fun getEthIPAddress(): String?
```
Get the current ethernet IP address.

---

```kotlin
fun getEthMacAddress(): String?
```
Get the ethernet MAC address.

---

```kotlin
fun setEthIPAddress(mIpaddr: String?, mMask: String?, mGw: String?, mDns: String?)
```
Set static ethernet IP configuration.

| Parameter | Description |
|:--|:--|
| mIpaddr | IP address |
| mMask | Subnet mask |
| mGw | Gateway |
| mDns | DNS server |

---

```kotlin
fun setTimeFromNetwork(autoTimeStatus: Boolean, context: Context?): Boolean
```
Enable or disable automatic time sync from network.

---

### USB & Storage

---

```kotlin
fun setUsbPower(value: Int): Int
```
Control USB port power. `1` = on, `0` = off.

---

```kotlin
fun getSDCardPath(context: Context): String?
```
Get the SD card mount path.

---

```kotlin
fun setFanOnOff(isEnabled: Int)
```
Control the cooling fan. `1` = on, `0` = off.

---

### Screenshot

---

```kotlin
fun getScreenShot(filePath: String): Int
```
Take a screenshot and save to the specified path.

| Parameter | Description |
|:--|:--|
| filePath | File path to save the image |

---

```kotlin
fun getScreenShotBitmap(): Bitmap?
```
Take a screenshot and return it as a Bitmap.

---

### Firmware & System

---

```kotlin
fun updateFirmware(filePath: String)
```
Start an OTA firmware update.

| Parameter | Description |
|:--|:--|
| filePath | Path to the firmware update file |

---

```kotlin
fun setTimeZone(zone: String): Boolean
```
Set the device timezone (e.g. `"Asia/Riyadh"`).

---

```kotlin
fun getNtpServer(): String?
fun setNtpServer(url: String)
```
Get or set the NTP server URL (e.g. `"pool.ntp.org"`).

---

```kotlin
fun setVolume(context: Context?, volume: Int): Int
```
Set the device volume.

---

### System Settings

Read and write Android system, secure, and global settings via the Enterprise Agent. All calls are asynchronous with callbacks.

---

```kotlin
// System settings
fun sysSettingPutInt(name: String?, value: Int, callback: (Result<Boolean>) -> Unit)
fun sysSettingGetInt(name: String?, callback: (Result<Int>) -> Unit)
fun sysSettingPutString(name: String?, value: String?, callback: (Result<Boolean>) -> Unit)
fun sysSettingGetString(name: String?, callback: (Result<String>) -> Unit)
// Also available: PutLong, GetLong, PutFloat, GetFloat

// Secure settings
fun secureSettingPutInt(name: String?, value: Int, callback: (Result<Boolean>) -> Unit)
fun secureSettingGetInt(name: String?, callback: (Result<Int>) -> Unit)
// Also available: PutLong, GetLong, PutString, GetString, PutFloat, GetFloat

// Global settings
fun globalSettingPutInt(name: String?, value: Int, callback: (Result<Boolean>) -> Unit)
fun globalSettingGetInt(name: String?, callback: (Result<Int>) -> Unit)
// Also available: PutLong, GetLong, PutString, GetString, PutFloat, GetFloat
```

---

```kotlin
fun grantAccessibilityPermission(componentName: String?, callback: (Result<Boolean>) -> Unit)
```
Grant accessibility permission to a service.

| Parameter | Description |
|:--|:--|
| componentName | e.g. `"com.your.app/.YourAccessibilityService"` |

---

## Supported Devices

| Model | Devices |
|:--|:--|
| **RK3576** | LT-ACCRK3576-poe |
| **RK3568** | Zentron_5 |
| **S3568** | VersiV1s3568, VersiV2s3568, MuroDv1s3568, MuroDv2s3568, CanvasV2s3568 |
| **Zentron** | rk3288, LT-Zentron8, LT-Zentron15, LD-AITemp |

The SDK auto-detects the device model at runtime. You do not need to specify the model manually.

---

## Support Matrix

The table below shows which functions are available on each device model. **Yes** = supported, **-** = not supported (will throw `NotSupportedMethodException`).

### Power

| Method | RK3576 | RK3568 | S3568 | Zentron |
|:--|:--:|:--:|:--:|:--:|
| `reboot` | Yes | Yes | Yes | Yes |
| `turnOff` | Yes | Yes | Yes | Yes |
| `scheduleReboot` | Yes | Yes | Yes | Yes |
| `setTurnOffOnAlarm` | Yes | - | Yes | Yes |
| `setAutoPowerOnOff` | Yes | - | Yes | - |

### Display & Brightness

| Method | RK3576 | RK3568 | S3568 | Zentron |
|:--|:--:|:--:|:--:|:--:|
| `setBrightness` | Yes | Yes | Yes | Yes |
| `setLcdBrightness` | Yes | - | Yes | - |
| `setRotation` | Yes | Yes | Yes | Yes |
| `setDisplayRotation` | Yes | Yes | Yes | Yes |
| `getDisplayRotation` | - | Yes | Yes | Yes |
| `setLcdBackLight` | Yes | - | Yes | Yes |
| `setEDPBackLight` | Yes | - | Yes | Yes |
| `setLcdBackLightEnable` | Yes | - | Yes | - |
| `getLcdBackLightEnable` | Yes | - | Yes | - |
| `setTimeOut` | Yes | Yes | Yes | Yes |

### LED

| Method | RK3576 | RK3568 | S3568 | Zentron |
|:--|:--:|:--:|:--:|:--:|
| `toggleBlueLight` | - | Yes | Yes | Yes |
| `toggleRedLight` | - | Yes | Yes | Yes |
| `toggleWhiteLight` | - | - | Yes | Yes |
| `turnOnGreenLight` | - | Yes | Yes | Yes |
| `turnOnRedLight` | - | Yes | Yes | Yes |
| `turnOnWhiteLight` | - | Yes | Yes | Yes |
| `turnOffLight` | - | Yes | Yes | Yes |

### System UI

| Method | RK3576 | RK3568 | S3568 | Zentron |
|:--|:--:|:--:|:--:|:--:|
| `setStatusBar` | Yes | Yes | Yes | Yes |
| `setStatusBarDrag` | Yes | Yes | Yes | - |
| `setNavigationBar` | Yes | Yes | Yes | - |
| `setGestureBar` | Yes | Yes | Yes | - |

### GPIO & Relay

| Method | RK3576 | RK3568 | S3568 | Zentron |
|:--|:--:|:--:|:--:|:--:|
| `getIOPortStatus` | Yes | Yes | Yes | Yes |
| `getGpioDirection` | Yes | - | Yes | Yes |
| `setGpioDirection` | Yes | - | Yes | Yes |
| `setRelayIoMode` | - | Yes | Yes | Yes |
| `getRelayIoMode` | - | Yes | Yes | Yes |
| `setRelayIoValue` | - | Yes | Yes | Yes |
| `getRelayIoValue` | - | Yes | Yes | Yes |
| `applyOpenRelay` | - | Yes | Yes | Yes |
| `applyCloseRelay` | - | Yes | Yes | Yes |

### Package Management

| Method | RK3576 | RK3568 | S3568 | Zentron |
|:--|:--:|:--:|:--:|:--:|
| `silentInstall` | Yes | Yes | Yes | Yes |
| `silentUninstall` | Yes | Yes | Yes | Yes |
| `setAllowUninstall` | Yes | Yes | Yes | Yes |

### Launcher & Boot App

| Method | RK3576 | RK3568 | S3568 | Zentron |
|:--|:--:|:--:|:--:|:--:|
| `setDefaultLauncher` | Yes | Yes | Yes | Yes |
| `getDefaultLauncher` | Yes | Yes | Yes | Yes |
| `setSystemBootApp` | Yes | Yes | Yes | - |
| `getSystemBootApp` | - | - | Yes | - |
| `setDaemonsActivity` | Yes | Yes | Yes | - |
| `getDaemonsActivity` | - | - | Yes | - |

### Network

| Method | RK3576 | RK3568 | S3568 | Zentron |
|:--|:--:|:--:|:--:|:--:|
| `netGetCurrentNetType` | Yes | Yes | Yes | Yes |
| `netGetMacAddress` | Yes | Yes | Yes | Yes |
| `netSetNetWork` | Yes | Yes | Yes | Yes |
| `netGetNetWork` | Yes | Yes | Yes | Yes |
| `netSetNetWorkModel` | Yes | - | Yes | Yes |
| `netGetNetWorkModel` | Yes | Yes | Yes | Yes |
| `netGetWifiRssi` | Yes | Yes | Yes | Yes |
| `netSetWifiAp` | Yes | Yes | Yes | Yes |
| `netGetWifiAp` | Yes | Yes | Yes | Yes |
| `netSetWifiConnect` | Yes | Yes | Yes | Yes |
| `netGetNetWorkInf` | Yes | Yes | Yes | Yes |
| `netGetImeiNumber` | - | Yes | Yes | - |
| `netGetIccidNumber` | Yes | Yes | Yes | Yes |
| `netGetImsiNumber` | Yes | Yes | Yes | Yes |
| `setMobileDataEnabled` | Yes | Yes | Yes | Yes |
| `netSetNetworkProtect` | Yes | Yes | Yes | Yes |
| `netGetNetworkProtectEnable` | - | - | Yes | - |
| `netGetNetworkProtectConfig` | - | - | Yes | - |
| `netSetNetworkPriority` | - | - | Yes | - |
| `netGetNetworkPriority` | - | - | Yes | - |
| `netSetNetworkMultiEnable` | Yes | Yes | Yes | Yes |
| `netGetNetworkMultiEnable` | Yes | Yes | Yes | Yes |

### Ethernet

| Method | RK3576 | RK3568 | S3568 | Zentron |
|:--|:--:|:--:|:--:|:--:|
| `setEthernetState` | Yes | Yes | Yes | Yes |
| `getEthernetState` | Yes | Yes | Yes | Yes |
| `getEthIPAddress` | Yes | Yes | Yes | Yes |
| `getEthMacAddress` | Yes | Yes | Yes | Yes |
| `setEthIPAddress` | Yes | Yes | Yes | Yes |
| `setTimeFromNetwork` | - | - | Yes | Yes |

### USB, Storage & Misc

| Method | RK3576 | RK3568 | S3568 | Zentron |
|:--|:--:|:--:|:--:|:--:|
| `setUsbPower` | Yes | Yes | Yes | Yes |
| `getSDCardPath` | Yes | Yes | Yes | Yes |
| `setFanOnOff` | - | - | Yes | Yes |

### Screenshot

| Method | RK3576 | RK3568 | S3568 | Zentron |
|:--|:--:|:--:|:--:|:--:|
| `getScreenShot` | Yes | Yes | Yes | - |
| `getScreenShotBitmap` | Yes | Yes | Yes | - |

### Firmware & System

| Method | RK3576 | RK3568 | S3568 | Zentron |
|:--|:--:|:--:|:--:|:--:|
| `updateFirmware` | Yes | Yes | Yes | - |
| `setTimeZone` | Yes | Yes | Yes | Yes |
| `getNtpServer` | Yes | Yes | Yes | Yes |
| `setNtpServer` | Yes | Yes | Yes | Yes |
| `setVolume` | Yes | Yes | Yes | Yes |

### System Settings

| Method | RK3576 | RK3568 | S3568 | Zentron |
|:--|:--:|:--:|:--:|:--:|
| `sysSettingPut/Get` | Yes | Yes | Yes | Yes |
| `secureSettingPut/Get` | Yes | Yes | Yes | Yes |
| `globalSettingPut/Get` | Yes | Yes | Yes | Yes |
| `grantAccessibilityPermission` | Yes | Yes | Yes | Yes |

---

## Error Handling

Some functions are not available on every device model. These will throw `NotSupportedMethodException`. Always handle this:

```kotlin
// Option 1: Check feature support
if (Model.isSupportLed) {
    device.turnOnGreenLight()
}

// Option 2: Catch the exception
try {
    device.toggleBlueLight(1)
} catch (e: NotSupportedMethodException) {
    Log.w("KioskSDK", "Not supported on this device")
}
```

---

## Error Codes

Functions that return `Int` use these standard error codes:

| Code | Name | Description |
|:--|:--|:--|
| `0` | RET_API_OK | Success |
| `-1` | RET_API_ERR_NG | Not supported |
| `-2` | RET_API_ERR_PARA | Wrong parameter |
| `-3` | RET_API_ERR_FILE_EXISTS | File does not exist |
| `-4` | RET_API_ERR_PERMISSION_DENIED | Permission denied |
| `-5` | RET_API_ERR_EXCEPTION | Exception thrown |
| `-6` | RET_API_ERR_PROPERTIES_EXISTS | Property does not exist |
| `-7` | RET_API_ERR_METHOD | Method does not exist |

---

## ProGuard

If your app enables code minification, add to `proguard-rules.pro`:

```proguard
-keep class com.lamasatech.kioskhardware.** { *; }
-keep class com.topjohnwu.superuser.** { *; }
```

---

## Troubleshooting

| Issue | Solution |
|:--|:--|
| **"Not supported device" on init** | Verify the device is a supported Lamasa kiosk. Log `android.os.Build.MODEL` to check. |
| **System settings methods fail** | The Lamasa Enterprise Agent app must be installed on the device (pre-installed on Lamasa kiosks). |
| **WiFi methods need permissions** | Request `ACCESS_FINE_LOCATION` at runtime on Android 6.0+. |
| **IMEI/ICCID returns null** | Request `READ_PHONE_STATE` at runtime. |
| **Reboot/timezone fails** | These require root access, available on Lamasa kiosk devices. |
| **AAR not found at build time** | Verify the AAR is in `app/libs/` and the path in `implementation(files(...))` is correct. |
