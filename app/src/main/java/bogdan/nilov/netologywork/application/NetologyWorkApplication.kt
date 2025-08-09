
package bogdan.nilov.netologywork.application
import android.app.Application
import bogdan.nilov.netologywork.BuildConfig.MAPS_API_KEY
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NetologyWorkApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAPS_API_KEY)
    }
}



