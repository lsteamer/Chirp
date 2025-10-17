import com.android.build.api.dsl.ApplicationExtension
import com.anjegonz.chirp.convention.configureKotlinAndroid
import com.anjegonz.chirp.convention.libs
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }


            extensions.configure<ApplicationExtension>() {
                namespace = "com.anjegonz.chirp"

                defaultConfig {
                    applicationId = libs.findVersion("projectApplicationId").get().toString()

                    targetSdk = libs.findVersion("android-targetSdk").get().toString().toInt()
                    versionCode = libs.findVersion("projectVersionCode").get().toString().toInt()
                    versionName = libs.findVersion("projectVersionName").get().toString()
                }
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                    }
                }

                configureKotlinAndroid(this)
            }
        }
    }
}