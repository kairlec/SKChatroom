package cn.skstudio.utils

import kotlin.math.max

object StringExtend {
    private fun pSimilar(str1: String, str2: String): Float {
        val len1 = str1.length
        val len2 = str2.length
        val dif = Array(len1 + 1) { IntArray(len2 + 1) }
        (0..len1).forEach { a -> dif[a][0] = a }
        (0..len2).forEach { a -> dif[0][a] = a }
        (1..len1).forEach { i ->
            run {
                (1..len2).forEach { j ->
                    run {
                        dif[i][j] = fun(nums: IntArray): Int {
                            var min = Int.MAX_VALUE
                            nums.forEach {
                                min = if (min > it) it else min
                            }
                            return min
                        }(intArrayOf(dif[i - 1][j - 1] + if (str1[i - 1] == str2[j - 1]) 0 else 1, dif[i][j - 1] + 1, dif[i - 1][j] + 1))
                    }
                }
            }
        }
        return 1 - dif[len1][len2].toFloat() / max(str1.length, str2.length)
    }
    fun String?.similar(targetString: String?): Float {
        return if (this == null || targetString == null) 0F else pSimilar(this, targetString)
    }
}

