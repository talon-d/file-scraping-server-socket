package en.talond.fileScrapingSocket

import java.net.ServerSocket
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter





public class TextServer(port : Int, interpreter : TextualProtocol) : Runnable {
    public val port = port
    private val interpreter = interpreter
    public override fun run() {
        val server = ServerSocket(port)
        var client : Socket
        println("$server is waiting for client...")
        client = server.accept();
        println("$client attempting to connect...")
        val reader = BufferedReader(InputStreamReader(client.getInputStream()))
        val writer = PrintWriter(client.getOutputStream(),true)
        var request = reader.readLine()
        if(request.contentEquals(interpreter.entryCode)) {
            println("Client accepted! Communicating...")
            writer.println(interpreter.entryCode)
            request = reader.readLine()
            while(!request.contentEquals(interpreter.exitCode)) {
                val response = interpreter.interpret(request)
                writer.println(response)
                request = reader.readLine()
            }
            writer.println(interpreter.exitCode)
            println("Connection with $client ended!")
        } else {
            println("Client rejected!")
            writer.println("AUTHENTICATION FAILED!")
        }
        client.close()
        reader.close()
        writer.close()
        server.close()
        println("Closed server $server")
    }

}




public interface TextualProtocol {
    public abstract val entryCode : String
    public abstract val exitCode : String
    public abstract fun interpret(request : String) : String;
}