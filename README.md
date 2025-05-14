



## How to Add Lib to you project

1. Add aar to libs dir
2. Inside build.gradle add
` implementation(files("libs/kioskhardware-lamasatech-0.0.55.aar")) `
3. Create App class
```
class App : Application() {  
  override fun onCreate() {  
	super.onCreate()  
	DeviceManager.instance?.init(this)  
  }  
}
```

or you can use startup 

```
class MainStartup : Initializer<Unit> {   
  override fun create(context: Context) {
	DeviceManager.instance?.init(this)
  }
}
```

## How to use

```
DeviceManager.instance?.device?.//ANY Fun
```

we recommend to use Device object with Injection 

```
@Module  
@InstallIn(SingletonComponent::class)  
class AppModule {  
	@[Singleton Provides]  
	fun provideDevice(): Device {  
		return DeviceManager.instance?.device!!  
	}
}
```

## Supported Devices

| Category | Model |
|--|--|
| Zentron | All Zentron series |
|S3568 | VersiV1s3568 , VersiV2s3568 , MuroDv1s3568 , MuroDv2s3568 |


## Avaliable Function 

| Fun | Supported Device | Doc |
|--|--|--|
| setTurnOffOnAlarm(offTime :String, onTime :String)| All | turn device off and on in time , use 24 hours time format like "11:00" , "20:00"  |
| reboot(context: Context): Int | All | restart device now |
| setBrightness(context: Context,brightness: Int) | All | change device brightness value should be in range from 0 to 100 |
| scheduleReboot(context: Context, delay: Long) | All | reboot device in schedule time [ delay unit is SECONDS ] |
| turnOff(context: Context): Int | All | turn off device |
---

``` fun getCurrentNetType(): String ``` *supported by* **ALL**

Get the currently net type

| Return | Description |
|:--|:--|
| String | |

---

``` fun setTimeFromNetwork(autotiestatus: Boolean, context: Context?): Boolean ``` *supported by* **ALL**

Set time from network

| Parameters | Description |
|:--|:--|
| autotiestatus |  |

<br>

| Return | Description |
|:--|:--|
| Boolean |  |

---

``` fun setMobileDataEnabled(context: Context?, enabled: Boolean): Boolean ``` *supported by* **ALL**

enable and disable mobile data

| Parameters | Description |
|:--|:--|
| enabled | true to enable false to disable |

<br>

| Return | Description |
|:--|:--|
| Boolean |  |

---

``` fun getEthIPAddress(): String? ``` *supported by* **ALL**

Get ethernet ip address

| Return | Description |
|:--|:--|
| String | ip address |

---

``` fun getEthMacAddress(): String? ``` *supported by* **ALL**

Get ethernet ip mac address

| Return | Description |
|:--|:--|
| String | mac address |

---

``` fun getWifiInterface(): WifiUtils? ``` *supported by* **ALL**

Get 

| Return | Description |
|:--|:--|
| WifiUtils? |  |

---

``` fun setEthernetState(enable: Boolean)``` *supported by* **ALL**

Get the currently guarded application package name

| Return | Description |
|:--|:--|
| String | Guarded application package name |

---

``` fun getEthernetState(): Boolean ``` *supported by* **ALL**

Get ethernet state

| Return | Description |
|:--|:--|
| Boolean |  |

---

``` fun setEthIPAddress(mIpaddr: String?, mMask: String?, mGw: String?, mDns: String?)``` *supported by* **ALL**

 

| Parameters | Description |
|:--|:--|
| mIpaddr | |
| mMask | |
| mGw | |
| mDns | |

---

``` fun netGetMacAddress(type: String?): String? ```  *supported by* **S3568**

 Get the MAC address of the network card device
 -   The obtained Wifi MAC address is the local MAC address
 
| Parameters | Description |
|:--|:--|
| type | Network Type -> `eth0`: Ethernet `eth1`: Ethernet 1 `wlan0`:WIFI|

<br>

| Return | Description |
|:--|:--|
| String | MAC address |

---
``` fun netGetCurrentNetType(): String? ``` *supported by* **S3568**

Get the type of current network connection

| Return | Description |
|:--|:--|
| String? | `WIFI`:WIFI `ETH`: Ethernet `MOBILE`: mobile network `UNKNOWN`: unknown type |

---
``` fun netSetNetWork(type: String?, enable: Boolean): Int ``` *supported by* **S3568**

Set network switch status

| Parameters | Description |
|:--|:--|
| type | Network Type -> `eth0` : Ethernet `eth1` : Ethernet 1 `wlan0` :WIFI `mobile` : mobile network |
| enable | true:open false:close |

<br> 

| Return | Description |
|:--|:--|
| Int | Call result, refer to error code |

---
``` fun netGetNetWork(type: String?): Int ``` *supported by* **S3568**

Get network switch status

| Parameters | Description |
|:--|:--|
| type | Network Type -> `eth0` : Ethernet `eth1` : Ethernet 1 `wlan0` :WIFI `mobile` : mobile network |

<br>

| Return | Description |
|:--|:--|
| Int | 1:Open 0:Close |

---
``` fun netSetNetWorkModel(type: String?, model: Int, ip: String?, gaw: String?, mask: String?, dns1: String?, dns2: String? ): Int ``` *supported by* **S3568**

Set network connection mode
- This interface WIFI modifies the currently connected WIFI. If WIFI is not connected, the mode cannot be switched.

| Parameters | Description |
|:--|:--|
| type | Network Type -> `eth0` : Ethernet `eth1` : Ethernet 1 `wlan0` :WIFI `mobile` : mobile network |
| model | Network mode 0: dynamic 1: static |
| ip | Set IP address in static mode |
| gaw | Set gateway in static mode |
| mask | Set subnet mask in static mode |
| dns1 | Static mode settings DNS1 |
| dns2 | Static mode settings DNS2 |

<br>

| Return | Description |
|:--|:--|
| Int | Call result, refer to error code |

---
``` fun netGetNetWorkModel(var1: String?): Int ``` *supported by* **S3568**

Get network connection mode

| Parameters | Description |
|:--|:--|
| type | Network Type -> `eth0` : Ethernet `eth1` : Ethernet 1 `wlan0` :WIFI `mobile` : mobile network |

<br>

| Return | Description |
|:--|:--|
| Int | Network mode: 0 dynamic 1 static |
---
``` fun netGetWifiRssi(level: Int): Int ``` *supported by* **S3568**

Get WIFI connection signal

| Parameters | Description |
|:--|:--|
| level | Grading, such as: pass 5, it is divided into 5 levels |

<br>

| Return | Description |
|:--|:--|
| Int | Current signal level |
---
``` fun netSetWifiAp(enable: Boolean): Int ``` *supported by* **S3568**

Set WIFI hotspot switch status
- WIFI hotspot and WIFI switch are mutually exclusive. After turning on the hotspot, WIFI will be automatically turned off.

| Parameters | Description |
|:--|:--|
| enable | true:open false:close |

<br>

| Return | Description |
|:--|:--|
| Int | Call result, refer to error code |

---
``` fun netGetWifiAp(): Int ``` *supported by* **S3568**

| Return | Description |
|:--|:--|
| Int | 1:Open 0:Close |

---
``` fun netSetNetworkProtect( enable: Boolean, type: Int, time: Long, ipInternet: String?, ipIntranet: String?, logPath: String?, reboot: Boolean): Int ``` *supported by* **S3568**

Set network guard switch status
-   The type type defaults to automatic: when it detects that the current network is unavailable, it will try to repair all network types.
    
-   The time interval defaults to 15 minutes: the interval for re-diagnosis after diagnosis (including repair after problems occur), which cannot be less than three minutes. It is not recommended to set too short a time to cause frequent detection.
    
-   ip_internet The external IP address defaults to Baidu: used to detect whether there is communication on the external network
    
-   ip_intranet The internal IP address defaults to Baidu: used to detect whether there is communication on the external network
    
-   log_path The log saving path defaults to the system root directory/NetworkProtectLog
    
-   reboot Whether to restart after repairing the network failure. Restart by default. Restart: After the repair fails, restart the system and re-diagnose. Multiple consecutive restarts will cause it to enter hibernation. After each restart, the waiting time for re-detection will increase. No restart: After the repair fails, it will enter sleep mode.
    
-   Sleep mode: When network changes are detected, the system will resume guarding and re-diagnose. If the diagnosis fails, it will continue to sleep.

| Parameters | Description |
|:--|:--|
| enable | true:open false:close |
| type | Type 0: Automatic 1: Ethernet 2:WIFI 3:Mobile network |
| time | The interval between re-diagnosis after diagnosis is completed |
| ipInternet | External IP address |
| ipIntranet | Intranet IP address |
| logPath | Log saving path  |
| reboot | Whether to restart after network repair failure true: Restart false: Do not restart |

<br>

| Return | Description |
|:--|:--|
| Int | Call result, refer to error code |

---
``` fun netGetNetworkProtectEnable(): Int ``` *supported by* **S3568**

Get network guard switch status

| Return | Description |
|:--|:--|
| Int | 1:Open 0:Close |

---
``` fun netGetNetworkProtectConfig(): List<String?>? ``` *supported by* **S3568**

Get network daemon configuration information

| Return | Description |
|:--|:--|
| List<String?>? | array order 0:Type 1: Interval time 2: External IP address 3: Intranet IP address 4:Log path 5: Is it possible to restart after repairing the network failure? |

---
``` fun netGetImeiNumber(): String? ``` *supported by* **S3568**

Get IMEI number

| Return | Description |
|:--|:--|
| String | IMEI number |

---
```fun netGetIccidNumber(): String? ``` *supported by* **S3568**

Get ICCID number

| Return | Description |
|:--|:--|
| String | ICCID number |

---
``` fun netGetImsiNumber(): String? ``` *supported by* **S3568**

Get IMSI number

| Return | Description |
|:--|:--|
| String | IMSI number |

---
``` fun netSetNetworkPriority(types: Array<String?>?): Int ``` *supported by* **S3568**

Set network priority
-   types sorts network type strings from high to low, for example: new String[]{"eth0", "wlan0", "mobile"}; Ethernet/WIFI/mobile network
    
-   You can also use new String[]{ TYPE_ETH0, TYPE_WLAN, TYPE_MOBILE} in the constant class;
    
-   It will take effect after restarting after setting.

| Parameters | Description |
|:--|:--|
| types | Network prioritization |

<br>

| Return | Description |
|:--|:--|
| Int | Call result, refer to error code |

---
``` fun netGetNetworkPriority(): Array<String?>? ``` *supported by* **S3568**

Get network priority
- Return value Sort network type strings from high to low, for example: new String[]{"eth0", "wlan0", "mobile"}; Ethernet/WIFI/mobile network

| Return | Description |
|:--|:--|
| Array<String?>? | Network prioritization |

---
``` fun netGetNetworkMultiEnable(): Int ``` *supported by* **S3568**

Get the status of multi-network coexistence switch

| Return | Description |
|:--|:--|
| Int | 1:Open 0:Close |
---
``` fun netSetNetworkMultiEnable(enable: Boolean): Int ``` *supported by* **S3568**

Set the multi-network coexistence switch status
-   Pay attention to use it in conjunction with network priority. The network type for accessing the external network is put first.
    
-   It will take effect after restarting after setting.

| Parameters | Description |
|:--|:--|
| enable | true:open false:close |

<br>

| Return | Description |
|:--|:--|
| Int | Call result, refer to error code |

---
``` fun netGetNetWorkInf(type: String?): NetworkInfoData? ``` *supported by* **S3568**

Get currently connected network information

| Parameters | Description |
|:--|:--|
| type | Network Type -> `eth0` : Ethernet `eth1` : Ethernet 1 `wlan0` :WIFI |

<br>

| Return | Description |
|:--|:--|
| NetworkInfoData | Object that holds network information |

---
```fun netSetWifiConnect(account: String?, pwd: String?, type: Int, mode: Int, info: NetworkInfoData?): Int ``` *supported by* **S3568**

Set WiFi connection account password
-   It takes effect in real time and will connect to WIFI after calling.

| Parameters | Description |
|:--|:--|
| account | account name |
| pwd | password |
| type | Encryption method 0: No password 1: WEP 2: WPA |
| mode | Connection mode 0: dynamic 1: static |
| info | Configuration required when the connection mode is static |

<br>

| Return | Description |
|:--|:--|
| Int | Call result, refer to error code |

---

``` fun setSystemBootApp(packageName:String): Int ``` *supported by* **S3568**

Set up applications that need to start automatically at boot

-   If the set application does not have a desktop icon, you need to specify the class name
    
-   Example: Specify the package name com.google.android.gallery3d Specify the class name com.google.android.gallery3d/.MainActivity

| Parameters | Description |
|:--|:--|
| packageName | Application package name (when you need to specify a class name, add "/" in the middle followed by the class name) |

<br>

| Return | Description |
|:--|:--|
| Int | Call result, refer to error code |

---

``` fun getSystemBootApp(): String? ``` *supported by* **S3568**

Get applications that need to start automatically at boot

| Return | Description |
|:--|:--|
| String | Application package name |

---

``` fun setDaemonsActivity(packageName: String, timeMillisecond: Long, broadcastEnable: Boolean) ``` *supported by* **S3568**

Set up daemon

-   If the guarded application does not have a desktop icon, you need to specify the class name
    
-   Example: Specify the package name com.google.android.gallery3d Specify the class name com.google.android.gallery3d/.MainActivity
    
-   The global broadcast is "android.app.smdt.PROTECT_CHECK"

| Parameters | Description |
|:--|:--|
| packageName | Set the application package name that needs to be protected (when you need to specify a class name, add "/" in the middle followed by the class name) |
| timeMillisecond | How long does it take to re-hang the application after it exits the front end, in milliseconds |
| broadcastEnable | Whether a global broadcast needs to be issued when the application exits |

<br>

| Return | Description |
|:--|:--|
| Int | Call result, refer to error code |

---

``` fun getDaemonsActivity(): String ``` *supported by* **S3568**

Get the currently guarded application package name

| Return | Description |
|:--|:--|
| String | Guarded application package name |


---

``` fun setStatusBar(context: Context, enable: Boolean): Int ``` *supported by* **ALL**

Show or hide status bar

| Parameters | Description |
|:--|:--|
| Boolean | true for show, false for hide |

---

``` fun setStatusBarDrag(enable: Boolean): Int ``` *supported by* **S3568**

Enable or disable status bar drag

| Parameters | Description |
|:--|:--|
| Boolean | true to enable drag , false to disable |

---

``` fun setNavigationBar(enable: Boolean): Int ``` *supported by* **S3568**

Show or hide navigation bar

| Parameters | Description |
|:--|:--|
| Boolean | true for show, false for hide |

---

``` fun setDisplayRotation(screenId: Int, degree: Int) ``` *supported by* **ALL**

Set screen rotation angle

| Parameters | Description |
|:--|:--|
| Int | Screen ID 0: Main screen 1: Secondary screen |
| Int | Rotation angle:0/90/180/270 |

---

``` fun getDisplayRotation(screenId: Int) : Int ``` *supported by* **ALL**

 Get screen rotation angle

 The main and secondary screens depend on the system and screen parameter definitions, not on the number of screens. For example, set the main screen LVDS and the secondary screen HDMI. Connect to 1 HDMI screen, HDMI is also a secondary screen, and the parameters that need to be passed in are also 1 secondary screen.

| Parameters | Description |
|:--|:--|
| Int | Screen ID 0: Main screen 1: Secondary screen |

| Return | Description |
|:--|:--|
| Int | Rotation angle:0/90/180/270 |

---

``` fun setLcdBackLightEnable(screenId: Int, enable: Boolean) : Int ``` *supported by* **S3568**

 Set the screen backlight switch
 
| Parameters | Description |
|:--|:--|
| Int | Screen ID 0: Main screen 1: Secondary screen |
| Boolean | true: on, false: off |

| Return | Description |
|:--|:--|
| Int | Rotation angle:0/90/180/270 |

---
``` fun getLcdBackLightEnable(screenId: Int) : Int ``` *supported by* **S3568**

Get the screen backlight switch status

| Parameters | Description |
|:--|:--|
| Int | Screen ID 0: Main screen 1: Secondary screen |

| Return | Description |
|:--|:--|
| Int | 1:on 0:off |

---
``` fun setLcdBrightness(screenId: Int, brightness: Int): Int ``` *supported by* **S3568**

Set the screen backlight brightness

- The frequency parameter is used for the external PWM interface. You can first obtain the default frequency and set it directly.
- On some platforms, such as RK3568, the second backlight does not use the external PWM (MCU) interface, but uses the system default interface. There is no need to pass in the frequency, and it will not change even if it is passed in.
- Before setting, obtain the maximum and minimum brightness values ​​of the corresponding interface and then set them.
- Set the system default interface. It is recommended to use the parameter save to save the data to the database after the operation. This can reduce the number of database operations.


| Parameters | Description |
|:--|:--|
| Int | Screen ID 0: Main screen 1: Secondary screen |
| Int | Brightness value |

| Return | Description |
|:--|:--|
| Int | refer to error code |

---


## Global error code

| name | code | description |
|:--|:--|:--|
| RET_API_OK | 0 | success |
| RET_API_ERR_NG | -1 | not support|
| RET_API_ERR_PARA | -2 | Wrong parameter |
| RET_API_ERR_FILE_EXISTS | -3 | file does not exist |
| RET_API_ERR_PERMISSION_DENIED | -4 | permission denied |
| RET_API_ERR_EXCEPTION | -5 | Exception thrown |
| RET_API_ERR_PROPERTIES_EXISTS | -6 | Property does not exist |
| RET_API_ERR_METHOD | -7 | method does not exist |



