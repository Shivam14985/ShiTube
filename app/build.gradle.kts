plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.firebase.firebase.perf)
}

android {
    namespace = "com.example.shivambhardwaj.shitube"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.shivambhardwaj.shitube"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.play.services.ads)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.inappmessaging.display)
    implementation(libs.gridlayout)
    implementation(libs.firebase.perf)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //CircularImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")
    //LoadImages
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    //timeAgo
    implementation("com.github.marlonlom:timeago:4.0.3")
    //ShimmerEffect
    implementation("com.facebook.shimmer:shimmer:0.5.0@aar")
    //animation
    implementation("com.airbnb.android:lottie:6.5.2")
    //ExoPlayer3
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.4.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("com.google.ads.interactivemedia.v3:interactivemedia:3.35.0")
    implementation("androidx.media3:media3-exoplayer-ima:1.4.1")
    //Loader
    implementation("com.github.ybq:Android-SpinKit:1.4.0")
    //material Design
    implementation("com.google.android.material:material:1.12.0")
    //SwipeRefresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    //google signin
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation("com.razorpay:checkout:1.6.41")

}