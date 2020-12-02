import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

fun main() {
   val filesToExamine = mutableListOf<File>()
   val time = measureTimeMillis {
      val userDir = Paths.get("C:/jbdevstudio4.1.0")
      userDir.toFile().walk().filter { file -> file.extension == "java" }
         .toCollection(filesToExamine)
   }
   println("files: ${filesToExamine.size} - walk time: $time")

   //////////////////////////////////////////////////////////////////////////////
   // testing

   val kCounter = AtomicInteger(0)
   val readTime = measureTimeMillis {
      for (i in 1..50) {
         filesToExamine.forEachParallel { file ->
            kCounter.incrementAndGet()
            try {
               val fileLines = file.readLines()
               val info = "${file.name} - ${fileLines.size}"
               val count = "private".count { fileLines.joinToString().contains(it) }

               val pc = fileLines.joinToString().windowed(7) {
                  if (it.equals("private"))
                     1
                  else
                     0
               }.sum()

               println("${file.name} - ${fileLines.size} - privates: $pc")
            }
            catch (e: Exception) {
            }
         }
      }
   }
   println("IO files: $kCounter - time: $readTime")
   Thread.sleep(6000)
}

fun <A> Collection<A>.forEachParallel(f: suspend (A) -> Unit): Unit {
   val queueSize = this.size
   if (queueSize < 1) {
      return
   }
   val cores = Runtime.getRuntime().availableProcessors() - 1
   val threadCount = minOf(cores, queueSize)
   var index = 0
   val factory = ThreadFactory { r ->
      index++
      Thread(r, "RNA-KotlinParallelCollection-$index").apply {
         isDaemon = true
      }
   }
   // this is a line in the middle!
   val queue = ArrayBlockingQueue<Runnable>(queueSize)
	// added another line
   val x=0

   val cachedPool =
      ThreadPoolExecutor(threadCount, threadCount, 5L, TimeUnit.SECONDS, queue)
         .apply {
            allowCoreThreadTimeOut(true)
            threadFactory = factory
         }
         .asCoroutineDispatcher()

   runBlocking(cachedPool) {
      map {
         async { f(it) }
      }.forEach { it.await() }
   }
   cachedPool.close()
   // more testing
}
