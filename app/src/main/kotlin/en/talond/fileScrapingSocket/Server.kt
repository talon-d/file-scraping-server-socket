package en.talond.fileScrapingSocket

import java.net.ServerSocket
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter

val eol = if(System.getProperty("os.name").contains("Windows")) "\n\r"; else "\n";

public class TextServer(port : Int, interpreter : TextualProtocol) : Runnable {
    public val port = port
    private val interpreter = interpreter
    public override fun run() {
        /*DELETE*/ println(System.getProperty("os.name"))
        val server = ServerSocket(port)
        var client : Socket
        println("$server is waiting for client...")
        client = server.accept();
        println("$client attempting to connect...")
        val reader = BufferedReader(InputStreamReader(client.getInputStream()))
        val writer = PrintWriter(client.getOutputStream(),true)
        var request = reader.readLine()
        if(request.equals(interpreter.entryCode)) {
            println("Client accepted! Communicating...")
            writer.write(interpreter.entryCode+eol)
            writer.flush()
            request = reader.readLine()
            while(!request.equals(interpreter.exitCode)) {
                val response = interpreter.interpret(request)
                writer.write(response+eol)
                writer.flush()
                request = reader.readLine()
            }
            writer.write(interpreter.exitCode+eol)
            writer.flush()
            println("Connection with $client ended!")
        } else {
            println("Client rejected!")
            writer.write("AUTHENTICATION FAILED!"+eol)
            writer.flush()
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