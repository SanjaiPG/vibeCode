package com.runanywhere.data

object DI {
    // Repository singleton - must be initialized from Application.onCreate()
    // by calling Repository.initialize(context)
    val repo = Repository   // use Firebase-enabled Repository singleton
}
