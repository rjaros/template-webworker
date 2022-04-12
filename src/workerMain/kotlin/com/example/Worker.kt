import com.example.jslib.Sql
import org.w3c.dom.DedicatedWorkerGlobalScope
import org.w3c.dom.MessageEvent

external val self: DedicatedWorkerGlobalScope

fun main() {
    println("Worker is running...")
    val sql = Sql()
    self.onmessage = { m: MessageEvent ->
        console.log("Worker got message:")
        console.log(m)
        println("Worker sending message: test")
        self.postMessage("test")
    }
}
