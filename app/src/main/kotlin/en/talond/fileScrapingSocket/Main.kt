
package en.talond.fileScrapingSocket


import java.io.File;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.Random;
import en.talond.fileScrapingSocket.*


val targetDir = File(System.getProperty("user.home")+"/Desktop/SembaPro Logs")



fun main(args : Array<String>) {
    val falsify = args.contains("fake")
    if(falsify) {
        val server1 = TextServer(51842,FakeLogProtocol(48))
        val server2 = TextServer(51843,FakeLogProtocol(56));
        Thread(server1).start()
        Thread(server2).start()
    } else {
        val server = TextServer(51842,LogTransmissionProtocol(targetDir))
        Thread(server).start()
    }
}




private class LogTransmissionProtocol(targetDir : File) : TextualProtocol {
    override val entryCode = "1asfg34r23rfwdfg"
    override val exitCode = "44ttgqrdsfreg4t"
    val requestCode = "brtgregre4teg"
    val badRequestResponse = "INVALID REQUEST"
    val targetDir = targetDir
    override fun interpret(request : String) : String {
        if(request.equals(requestCode))
            return scrapeLogData();
        else return badRequestResponse;
    }

    /**
     * Scrapes the last line of the most recent log file
     */
    private fun scrapeLogData() : String {
        var production : String;
        try {
            //Find the most recent log file in the target directory
            val children = targetDir.listFiles().filter() {isLog(it) && !it.isDirectory()};
            var mostRecentlyModified = children[0]
            var bestTime = Long.MIN_VALUE
            children.forEach() {
                val lastModified = it.lastModified()
                if(lastModified > bestTime) {
                    bestTime = lastModified
                    mostRecentlyModified = it
                }
            }
            //extract line
            production = mostRecentlyModified.readLines().last();
        } catch (e : Exception) {
            production = "Error!";
        }
        return production
    }

    /**
     * Checks the filename to see if it matches the expected pattern
     */
    val LOG_PATTERN : Regex = Pattern.compile("[0-9]{4}_[0-9]{2}_[0-9]{2}_[0-9]{2}_[0-9]{2}\\.log").toRegex()
    fun isLog(possibleLog : File) : Boolean {
        if(possibleLog.getName().matches(LOG_PATTERN))
            return true
        else return false
    }   
}


/**
 * Used to test the LabView socket this script interacts with
 * without requiring the real-world equipment to be set up and
 * running.
 */
private class FakeLogProtocol(maxStep : Int) : TextualProtocol {
    private val system = Machine(maxStep)
    override val entryCode : String
    override val exitCode : String
    val requestCode : String
    val badRequestResponse : String
    init {
        val parent = LogTransmissionProtocol(File(""));
        entryCode = parent.entryCode
        exitCode = parent.exitCode
        requestCode = parent.requestCode
        badRequestResponse = parent.badRequestResponse
    }

    override fun interpret(request : String) : String {
        if(request.equals(requestCode))
            return system.getSimulatedLine()
        else return badRequestResponse
    }

    /**
     * Simulated equipment object
     */
    private class Machine(maxStep : Int) {
        val maxStep = maxStep
        val randomizer = Random()
        var step = 0
        fun getSimulatedLine() : String {
            val isStepUpdate =  randomizer.nextBoolean()
            if(isStepUpdate) {
                step++;
                if(step >= maxStep)
                    step = 0;
                return getRandomizedString()+" "+step;
            } else return getRandomizedString();
        }

        
        private fun getRandomizedString(): String {
            val length = randomizer.nextInt(100 - 30) + 30
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf(' ','#','/')
            return (1..length).map {allowedChars.random()}.joinToString("")
        }
    }
}