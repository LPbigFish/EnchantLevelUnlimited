package me.lpbigfish.enchantLevelUnlimited.util

import org.bukkit.Bukkit

class ServerVersion(val major: Int, val minor: Int, val patch: Int = 0) : Comparable<ServerVersion> {
    override fun compareTo(other: ServerVersion): Int {
        val majorCmp = major.compareTo(other.major)
        if (majorCmp != 0) return majorCmp
        val minorCmp = minor.compareTo(other.minor)
        if (minorCmp != 0) return minorCmp
        return patch.compareTo(other.patch)
    }

    operator fun compareTo(pair: Pair<Int, Int>): Int = compareTo(ServerVersion(pair.first, pair.second))

    override fun toString(): String = if (patch > 0) "$major.$minor.$patch" else "$major.$minor"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ServerVersion) return false
        return major == other.major && minor == other.minor && patch == other.patch
    }

    override fun hashCode(): Int = 31 * (31 * major + minor) + patch

    companion object {
        private val cached: ServerVersion by lazy { detect() }

        fun current(): ServerVersion = cached

        fun detect(): ServerVersion {
            val version = Bukkit.getBukkitVersion()
            val match = Regex("(\\d+)\\.(\\d+)(?:\\.(\\d+))?").find(version)
            val major = match?.groupValues?.get(1)?.toIntOrNull() ?: 1
            val minor = match?.groupValues?.get(2)?.toIntOrNull() ?: 0
            val patch = match?.groupValues?.get(3)?.toIntOrNull() ?: 0
            return ServerVersion(major, minor, patch)
        }

        fun isAtLeast(major: Int, minor: Int): Boolean = current() >= ServerVersion(major, minor)
    }
}
