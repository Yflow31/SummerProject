package com.somaiya.summer_project.utils

import kotlin.random.Random

class RandomPhotoUrlGenerator {

    fun randomUsernameExtension(): String {
        val chars = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9/:'`abcdefghijklmnopqrstuvwxyz".toCharArray()
        val sb = StringBuilder()
        val random = Random
        val randomLength = getRandomNumber(1, 20)
        for (i in 0 until 8) {
            val c = chars[random.nextInt(chars.size)]
            sb.append(c)
        }
        return sb.toString()
    }

    fun getRandomNumber(min: Int, max: Int): Int {
        return (Random.nextDouble() * (max - min)).toInt() + min
    }
}