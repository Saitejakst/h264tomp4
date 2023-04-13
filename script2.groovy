import java.io.*
import org.apache.commons.io.IOUtils

flowFile = session.get()

if (!flowFile) {
    return
}

def command = ['/bin/bash', '-c', 'ffmpeg -i ' + flowFile.getAttribute('filename') + ' -codec copy ' + flowFile.getAttribute('filename').replaceAll(/\.h264$/, '.mp4')]

def process = new ProcessBuilder(command).redirectErrorStream(true).start()

process.waitFor()

def output = IOUtils.toString(process.getInputStream(), 'UTF-8')
def error = IOUtils.toString(process.getErrorStream(), 'UTF-8')

if (error) {
    flowFile = session.penalize(flowFile)
    session.transfer(flowFile, REL_FAILURE)
} else {
    flowFile = session.putAttribute(flowFile, 'filename', flowFile.getAttribute('filename').replaceAll(/\.h264$/, '.mp4'))
    session.transfer(flowFile, REL_SUCCESS)
}
