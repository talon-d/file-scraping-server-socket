package en.talond.fileScrapingSocket

import kotlin.collections.List;
import kotlin.test.Test
import kotlin.test.assertNotNull
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter



class Test {


    val protocol = TestProtocol()


    @Test fun canHoldAConversation() {
        val server = TextServer(15325, TestProtocol())
        val client = TestClient("localhost",15325)
        openTwoThreads(server,client)
        val responses = client.responses!!
        println(responses)
        var allMatching = responses[0].contentEquals(protocol.entryCode) &&
                responses[1].contentEquals(protocol.response) &&
                responses[2].contentEquals(protocol.exitCode);
        assert(allMatching)
    }


    @Test fun canRejectBadCaller() {
        val server = TextServer(4280,TestProtocol())
        val client = BadTestClient("localhost",4280)
        openTwoThreads(server,client)
        val response = client.response
        assert(response.equals("AUTHENTICATION FAILED!"))
    }


    private fun openTwoThreads(server : Runnable, client : Runnable) {
        val serverInstance = Thread(server)
        val clientInstance = Thread(client)
        serverInstance.start()
        Thread.sleep(100L)
        clientInstance.start()
        Thread.sleep(100L)
    }







    class TestProtocol : TextualProtocol {
        override val entryCode = "hi"
        override val exitCode = "bye"
        val response = "Is this thing on?"
        override fun interpret(request : String) : String {
            return response
        }
    }
    



    class TestClient (host : String, port : Int) : Runnable {
        val host = host
        val port = port
        val protocol = TestProtocol()
        @Volatile var responses : List<String>? = null;
    
        public override fun run() {
            val socket = Socket(host,port)
            val writer = PrintWriter(socket.getOutputStream(),false)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            writer.write(protocol.entryCode+eol)
            writer.flush()
            var response0 = reader.readLine()
            writer.write(""+eol)
            writer.flush()
            var response1 = reader.readLine()
            writer.write(protocol.exitCode+eol)
            writer.flush()
            var response2 = reader.readLine()
            responses = listOf(response0,response1,response2)
            socket.close()
            writer.close()
            reader.close()
        }
    }
    



    
    class BadTestClient(host : String, port : Int) : Runnable {
        val host = host
        val port = port
        val protocol = TestProtocol()
        @Volatile var response = ""
    
        public override fun run() {
            val socket = Socket(host,port)
            val writer = PrintWriter(socket.getOutputStream(),false)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            writer.write("This isnt the code!"+eol)
            writer.flush()
            response = reader.readLine()
            socket.close();
        }
    }
}






