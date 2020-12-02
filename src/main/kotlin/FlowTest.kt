import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

fun foo() = flow {
   for (i in 1..100) {
      emit("A$i")
      emit("B$i")
      emit("C$i")
   }
}

fun fillin(): String {
   val whatever = "defabc"
   return whatever
}

fun main() {
   runBlocking {
      val flow = foo()
      flow.collect { x ->
         println(x)
         delay(500)
      }
   }
}
