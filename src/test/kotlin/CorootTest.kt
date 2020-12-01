import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.Scanner

class CoTests {

   @Test
   fun dotTest() {
      runBlocking {
         val jobs = List(100_000) {
            launch {
               delay(1000)
               println(". ${Thread.currentThread().name}")
            }
         }
         jobs.forEach { it.join() }
         val  sc = Scanner(System.`in`);
         val pause = sc.nextLine();


      }
   }

}
