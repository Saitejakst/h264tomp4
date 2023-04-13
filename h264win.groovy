import java.io.*
import org.apache.commons.io.*

def ffMpegPath = "C:\\ffmpeg\\bin\\ffmpeg.exe"

def flowFile = session.get()

if (!flowFile) {
    return
}

def inputFile = flowFile.getAttribute("filename")
def outputFile = inputFile.replaceAll(/\.h264/, ".mp4")

def inputFilePath = flowFile.getAttribute("path")
def outputFilePath = inputFilePath.replaceAll(inputFile, outputFile)

def command = "${ffMpegPath} -i ${inputFilePath} -codec:v copy -codec:a copy ${outputFilePath}"

def process = command.execute()
process.waitFor()

if (process.exitValue() != 0) {
    session.transfer(flowFile, REL_FAILURE)
    return
}

def outputFileBytes = FileUtils.readFileToByteArray(new File(outputFilePath))

flowFile = session.write(flowFile, { outputStream ->
    outputStream.write(outputFileBytes, 0, outputFileBytes.length)
} as OutputStreamCallback)

flowFile = session.putAttribute(flowFile, "filename", outputFile)

session.transfer(flowFile, REL_SUCCESS)
