package de.advent.days.d20grovepositioningsystem.part2

import de.advent.utils.output
import de.advent.utils.readLines

private data class Num(val id: Int, val value: Long)

private fun Int.getAsByIndex(nums: List<Num>): Long = nums[this % nums.size].value

private const val DECRYPTION_KEY = 811589153L
private const val REPS = 10

fun main() {
    var zeroNum: Num? = null
    val initialNums = readLines().mapIndexed { index, line ->
        val num = Num(index, line.toLong() * DECRYPTION_KEY)
        if (num.value == 0L) {
            zeroNum = num
        }
        num
    }
    check(initialNums.size == initialNums.toSet().size)
    check(initialNums.size > 2)
    val nums = initialNums.map { it }.toMutableList()
    repeat(REPS) {
        for (num in initialNums) {
            if (num.value == 0L) continue
            var index = nums.indexOf(num)
            if (num.value > 0) {
                var shift = num.value
                if (index == 0 || index == nums.size - 1) {
                    nums.remove(num)
                    nums.add(1, num)
                    index = 1
                    shift--
                }
                val realShift = shift % (initialNums.size - 1)
                if (realShift == 0L) continue
                var newIndex = index + realShift
                if (newIndex > initialNums.size - 1) {
                    newIndex -= (initialNums.size - 1)
                }
                nums.remove(num)
                nums.add(newIndex.toInt(), num)
            } else {
                var absShift = -num.value
                if (index == 0 || index == nums.size - 1) {
                    nums.remove(num)
                    val nextIndex = initialNums.size - 2
                    nums.add(nextIndex, num)
                    index = nextIndex
                    absShift--
                }
                val realAbsShift = absShift % (initialNums.size - 1)
                if (realAbsShift == 0L) continue
                var newIndex = index - realAbsShift
                if (newIndex <= 0) newIndex += initialNums.size - 1
                nums.remove(num)
                nums.add(newIndex.toInt(), num)
            }
        }
    }
    val zeroIndex = nums.indexOf(zeroNum!!)
    val result = (1000 + zeroIndex).getAsByIndex(nums) +
            (2000 + zeroIndex).getAsByIndex(nums) + (3000 + zeroIndex).getAsByIndex(nums)
    output(result)
}