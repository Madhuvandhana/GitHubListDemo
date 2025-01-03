import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dagger.hilt.android)
}
val properties = Properties().apply {
    rootProject.file("local.properties").reader().use(::load)
}
val githubApiToken = properties["githubApiToken"] as? String?: ""
android {
    namespace = "com.example.githubtoprepos"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.githubtoprepos"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "GITHUB_API_TOKEN", "\"$githubApiToken\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += listOf("META-INF/INDEX.LIST", "META-INF/io.netty.versions.properties")
        }
    }
    hilt {
        enableAggregatingTask = false
    }


    lint {
        // Turns off checks for the issue IDs you specify.
        disable += "TypographyFractions" + "TypographyQuotes"
        // Turns on checks for the issue IDs you specify. These checks are in
        // addition to the default lint checks.
        enable += "RtlHardcoded" + "RtlCompat" + "RtlEnabled"
        // To enable checks for only a subset of issue IDs and ignore all others,
        // list the issue IDs with the 'check' property instead. This property overrides
        // any issue IDs you enable or disable using the properties above.
        checkOnly += "NewApi" + "InlinedApi"
        // If set to true, turns off analysis progress reporting by lint.
        quiet = true
        // If set to true (default), stops the build if errors are found.
        abortOnError = true
        // If set to true, lint only reports errors.
        ignoreWarnings = true
        // If set to true, lint also checks all dependencies as part of its analysis.
        // Recommended for projects consisting of an app with library dependencies.
        checkDependencies = true

        warningsAsErrors = true
        //checkOnly += "NewApi" + "HandlerLeak"
        baseline = file("lint-baseline.xml")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.room.ktx)

    //Jetpack compose dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.adaptive.android)
    implementation(libs.androidx.compose.adaptive.navigation.android)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.constraintlayout.compose)

    // Android Studio Preview support
    implementation(libs.androidx.compose.ui)
    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    implementation(libs.androidx.compose.material.icons.core)
    // Optional - Add full set of material icons
    implementation(libs.androidx.compose.material.icons.extended)
    // Optional - Add window size utils
    implementation(libs.androidx.compose.material3.window)
    // Optional - Integration with activities
    implementation(libs.androidx.compose.activity)

    // Compatible with Compose Material 3, includes Mdc3Theme
    implementation(libs.androidx.compose.material.theme.adapter)
    // Optional - Integration with ViewModels
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Jetpack Compose Navigation
    implementation(libs.androidx.navigation.compose)

    //Convert API - Retrofit2
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.adapters)
    implementation(libs.moshi.kotlin)
    implementation(libs.kotlinx.serialization.json)

    //Dagger - Hilt
    implementation(libs.dagger.hilt.android)
//    implementation(libs.dagger.hilt.lifecycle.viewmodel)
//    implementation(libs.dagger.hilt.android.compiler)
    implementation(libs.dagger.hilt.navigation.compose)
    ksp(libs.dagger.hilt.compiler)

    //full layout without status bar
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.activity)

    // loading network image
    implementation(libs.coil.compose)
    // Support SVG images
    implementation(libs.coil.svg)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}