package ftpsexemplo

import groovy.lang.Closure;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPSClient
import org.apache.commons.net.ftp.FTPReply
import sun.net.www.protocol.ftp.FtpURLConnection.FtpInputStream

class FTPSSERVICEService {

    boolean transactional = true
	
	String server = '192.168.0.101'
	String username = 'usuario'
	String passwd = 'usuario'
	
	String remoteBaseDir = '/home/usuario'
	
     def save(inputStream, fileName) {
		 log.debug "Entrei aqui Já é"
		 connect {ftp ->
			 assert ftp.storeFile("${remoteBaseDir}/${fileName}", inputStream)
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
		 def ft = ftp.connect server, 2121
		 
		 log.debug "aqui2 ${ft}"
		 if(!FTPReply.isPositiveCompletion(ftp.replyCode)) {
			 ftp.disconnect()
			 log.debug "Não Completou"
		 }
		 assert FTPReply.isPositiveCompletion(ftp.replyCode)
		 	
		 log.debug "Logando..."
		 def ff = ftp.login username, passwd
		 log.debug  "${ff}"
		 ftp.fileType = FTP.BINARY_FILE_TYPE//IMAGE_FILE_TYPE 
		 ftp.enterLocalPassiveMode();
		 
		 assert ftp.sendNoOp()
		 assert ftp.changeWorkingDirectory("/public")
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
