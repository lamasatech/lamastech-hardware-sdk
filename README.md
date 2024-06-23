
## How to Add Lib to you project

1. Add aar to libs dir
2. Inside build.gradle add
` implementation(files("libs/kioskhardware-0.0.17.aar")) `
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

## Avaliable Function 

| Fun | Doc |
|--|--|
| setTurnOffOnAlarm(offTime :String, onTime :String) | turn device off and on in time , use 24 hours time format like "11:00" , "20:00"  |
| reboot(context: Context): Int | restart device now |
| setBrightness(context: Context,brightness: Int) | change device brightness value should be in range from 0 to 100 |
| scheduleReboot(context: Context, delay: Long) | reboot device in schedule time [ delay unit is SECONDS ] |
| turnOff(context: Context): Int | turn off device |



pls use application context with all functions
