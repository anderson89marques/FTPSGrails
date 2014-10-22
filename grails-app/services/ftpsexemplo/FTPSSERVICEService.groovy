package ftpsexemplo

import groovy.lang.Closure;

import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPSClient
import org.apache.commons.net.ftp.FTPReply

import sun.net.www.protocol.ftp.FtpURLConnection.FtpInputStream

class FTPSSERVICEService {

    boolean transactional = true
	
	String server = '192.168.1.61'
	String username = 'andersonmarques'
	String passwd = 'andersonmarques'
	
	String remoteBaseDir = '/home/andersonmarques/repositorio/'
	
     def save(inputStream, fileName) {
		 log.debug "Entrei aqui Já é"
		 connect {ftp ->
			 try {
				 ftp.storeFile("${remoteBaseDir}/${fileName}", inputStream)
				 inputStream.close()
				 log.debug "replay code... ${ftp.getReplyCode()}"
				 log.debug "replay code... ${ftp.getReplyString()}"
			 } catch(Exception e){
			 	//log.debug "${e.message}"
				log.debug ">OOOOOO :${e.stackTrace}" 
			 }
			 
		 }
	 }
	 
	 def load(outputStream, fileName) {
		 connect ({ftp ->
		 def success = ftp.retrieveFile(fileName, outputStream)
		 if (success) {
			 return success
		 }
		 else
		 	return false
		 })
		 }
	 
	 private def connect(Closure c, boolean discOnFinish = true) {
		 log.debug "Entrei aqui"
		 def ftp = new FTPSClient("SSL")
		 def ft = ftp.connect server, 990
		 
		 log.debug "aqui2 ${ft}"
		 if(!FTPReply.isPositiveCompletion(ftp.replyCode)) {
			 ftp.disconnect()
			 log.debug "Não Completou"
		 }
		 
		 assert FTPReply.isPositiveCompletion(ftp.replyCode)
		 log.debug "replay code... ${ftp.getReplyCode()}"
		 log.debug "replay code... ${ftp.getReplyString()}"
		 
		 log.debug "Logando..."
		 def ff = ftp.login username, passwd
		 log.debug  "${ff}"
		 
		 // Set protection buffer size
		 ftp.execPBSZ(0);
		 // Set data channel protection to private
		 ftp.execPROT("P");
		 
		 ftp.fileType = FTP.BINARY_FILE_TYPE//IMAGE_FILE_TYPE 
		 ftp.enterLocalPassiveMode();
		 log.debug "replay code d login... ${ftp.getReplyCode()}"
		 log.debug "replay code d login... ${ftp.getReplyString()}"
		 
		 assert ftp.sendNoOp()
		 log.debug  "Workdir antes>>" +
		 ftp.printWorkingDirectory()
		 assert ftp.changeWorkingDirectory("${remoteBaseDir}")
		 log.debug  "Workdir >>" +
			 ftp.printWorkingDirectory()
		 log.debug ftp.pwd()
		 
		 try {
			 return c?.call(ftp)
		 } finally {
		 	if (discOnFinish) {
				 ftp.logout()
				 ftp.disconnect()
			 }
		 }
	}
}
