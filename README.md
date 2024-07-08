

## How to Add Lib to you project

1. Add aar to libs dir
2. Inside build.gradle add
` implementation(files("libs/kioskhardware-lamasatech-0.0.19.aar")) `
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

| Name | Model |
|--|--|
| Zentron | rk3288 , LT-Zentron8 , LT-Zentron15 , LD-AITemp , rk3288_tdx |
| Visipoint15 | Visipoint 15 |
|S3568 | VersiV1s3568 , MuroDv1s3568 |
| OctopusA83 | Octopus A83 F1 |
| 3288 | 3288 |
| 3280 | 3280 |


## Avaliable Function 

| Fun | Supported Device | Doc |
|--|--|--|
| setTurnOffOnAlarm(offTime :String, onTime :String)| All | turn device off and on in time , use 24 hours time format like "11:00" , "20:00"  |
| reboot(context: Context): Int | All | restart device now |
| setBrightness(context: Context,brightness: Int) | All | change device brightness value should be in range from 0 to 100 |
| scheduleReboot(context: Context, delay: Long) | All | reboot device in schedule time [ delay unit is SECONDS ] |
| turnOff(context: Context): Int | All | turn off device |
| getCurrentNetType | All |  |
| setTimeFromNetwork | All |  |
| smdtSetMobileDataEnabled | All |  |
| smdtGetEthIPAddress | All |  |
| smdtGetEthMacAddress | All |  |
| getWifiInterface | All |  |
| smdtSetEthernetState | All |  |
| smdtGetEthernetState | All |  |
| smdtSetEthIPAddress | All |  |


#### Net Function for  S3568 only
``` fun netGetMacAddress(type: String?): String? ```

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
``` fun netGetCurrentNetType(): String? ```

Get the type of current network connection
| Return | Description |
|:--|:--|
| String? | `WIFI`:WIFI `ETH`: Ethernet `MOBILE`: mobile network `UNKNOWN`: unknown type |

---
``` fun netSetNetWork(type: String?, enable: Boolean): Int ```

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
``` fun netGetNetWork(type: String?): Int ```

Get network switch status

| Parameters | Description |
|:--|:--|
| type | Network Type -> `eth0` : Ethernet `eth1` : Ethernet 1 `wlan0` :WIFI `mobile` : mobile network |

<br>

| Return | Description |
|:--|:--|
| Int | 1:Open 0:Close |

---
``` fun netSetNetWorkModel(type: String?, model: Int, ip: String?, gaw: String?, mask: String?, dns1: String?, dns2: String? ): Int ```

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
``` fun netGetNetWorkModel(var1: String?): Int ```

Get network connection mode

| Parameters | Description |
|:--|:--|
| type | Network Type -> `eth0` : Ethernet `eth1` : Ethernet 1 `wlan0` :WIFI `mobile` : mobile network |

<br>

| Return | Description |
|:--|:--|
| Int | Network mode: 0 dynamic 1 static |
---
``` fun netGetWifiRssi(level: Int): Int ```

Get WIFI connection signal

| Parameters | Description |
|:--|:--|
| level | Grading, such as: pass 5, it is divided into 5 levels |

<br>

| Return | Description |
|:--|:--|
| Int | Current signal level |
---
``` fun netSetWifiAp(enable: Boolean): Int ```

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
``` fun netGetWifiAp(): Int ```

| Return | Description |
|:--|:--|
| Int | 1:Open 0:Close |

---
``` fun netSetNetworkProtect( enable: Boolean, type: Int, time: Long, ipInternet: String?, ipIntranet: String?, logPath: String?, reboot: Boolean): Int ```

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
``` fun netGetNetworkProtectEnable(): Int ```

Get network guard switch status

| Return | Description |
|:--|:--|
| Int | 1:Open 0:Close |

---
``` fun netGetNetworkProtectConfig(): List<String?>? ```

Get network daemon configuration information

| Return | Description |
|:--|:--|
| List<String?>? | array order 0:Type 1: Interval time 2: External IP address 3: Intranet IP address 4:Log path 5: Is it possible to restart after repairing the network failure? |

---
``` fun netGetImeiNumber(): String? ```

Get IMEI number

| Return | Description |
|:--|:--|
| String | IMEI number |

---
```fun netGetIccidNumber(): String? ```

Get ICCID number

| Return | Description |
|:--|:--|
| String | ICCID number |

---
``` fun netGetImsiNumber(): String? ```

Get IMSI number

| Return | Description |
|:--|:--|
| String | IMSI number |

---
``` fun netSetNetworkPriority(types: Array<String?>?): Int ```

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
``` fun netGetNetworkPriority(): Array<String?>? ```

Get network priority
- Return value Sort network type strings from high to low, for example: new String[]{"eth0", "wlan0", "mobile"}; Ethernet/WIFI/mobile network

| Return | Description |
|:--|:--|
| Array<String?>? | Network prioritization |

---
``` fun netGetNetworkMultiEnable(): Int ```

Get the status of multi-network coexistence switch

| Return | Description |
|:--|:--|
| Int | 1:Open 0:Close |
---
``` fun netSetNetworkMultiEnable(enable: Boolean): Int ```

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
``` fun netGetNetWorkInf(type: String?): NetworkInfoData? ```

Get currently connected network information

| Parameters | Description |
|:--|:--|
| type | Network Type -> `eth0` : Ethernet `eth1` : Ethernet 1 `wlan0` :WIFI |

<br>

| Return | Description |
|:--|:--|
| NetworkInfoData | Object that holds network information |

---
```fun netSetWifiConnect(account: String?, pwd: String?, type: Int, mode: Int, info: NetworkInfoData?): Int ```

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

- pls use application context with all functions





