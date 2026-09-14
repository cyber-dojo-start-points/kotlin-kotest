package dojo

fun fizzBuzz(n: Int): String {
    return when {
        n % 15 == 0 -> "fizzbuzz"
        n % 3 == 0 -> "fizz"
        n % 5 == 0 -> "buzz"
        else -> n.toString()
    }
}
