import java.util.concurrent.TimeUnit
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.PumpStreamHandler

def flowFile = session.get()
if(!flowFile) return

def h264FilePath = flowFile.getAttribute('filepath')
def mp4FilePath = h264FilePath.replace('.h264', '.mp4')

def cmdLine = CommandLine.parse('/bin/bash')
cmdLine.addArgument('-c')
cmdLine.addArgument("ffmpeg -i ${h264FilePath} -codec copy ${mp4FilePath}")

def executor = new DefaultExecutor()
def pump = new PumpStreamHandler()
executor.setStreamHandler(pump)

executor.execute(cmdLine)

session.transfer(flowFile, REL_SUCCESS)
