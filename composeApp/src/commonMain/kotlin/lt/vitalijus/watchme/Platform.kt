package lt.vitalijus.watchme

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform