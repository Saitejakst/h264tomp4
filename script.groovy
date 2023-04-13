import java.io.*

def flowFile = session.get()

if(!flowFile) return

// Set the input file path and name
def inputPath = flowFile.getAttribute('input.path')
def inputName = flowFile.getAttribute('input.name')
def inputFile = new File(inputPath, inputName)

// Set the output file path and name
def outputPath = flowFile.getAttribute('output.path')
def outputName = inputName.replaceAll(/.h264/, '.mp4')
def outputFile = new File(outputPath, outputName)

// Build the FFmpeg command
def ffmpegCommand = ['/usr/bin/ffmpeg', '-i', inputFile.path, '-c:v', 'libx264', '-crf', '20', '-preset', 'medium', '-c:a', 'aac', '-b:a', '192k', '-movflags', '+faststart', outputFile.path]

// Run the FFmpeg command
def process = ffmpegCommand.execute()

// Wait for the FFmpeg process to finish
process.waitFor()

// Check the exit status of the FFmpeg process
if (process.exitValue() != 0) {
    // FFmpeg conversion failed
    session.transfer(flowFile, REL_FAILURE)
} else {
    // FFmpeg conversion successful
    flowFile = session.putAttribute(flowFile, 'output.name', outputName)
    flowFile = session.putAttribute(flowFile, 'output.path', outputPath)
    session.transfer(flowFile, REL_SUCCESS)
}
