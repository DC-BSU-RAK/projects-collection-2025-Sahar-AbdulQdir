import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

fun wrapContext(context: Context, language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    } else {
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        return context
    }
}
