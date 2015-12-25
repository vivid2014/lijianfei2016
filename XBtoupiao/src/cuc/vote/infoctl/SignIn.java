package cuc.vote.infoctl;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64DecoderStream;

public class SignIn {
	
	final static public String ADM_SESSION = "adm_session";
	public static int auth_header(HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpsession = request.getSession(false);
		if ((request.getHeader("authorization")!=null) && (!request.getHeader("authorization").equals(""))) {
			String	userid = "", authstr = "", passwd = "";
			authstr = request.getHeader("authorization");
			authstr = authstr.substring(authstr.indexOf(' ')+1, authstr.length());
			authstr = new String(BASE64DecoderStream.decode(authstr.getBytes()));
			userid = authstr.substring(0, authstr.indexOf(':'));
			passwd = authstr.substring(authstr.indexOf(':')+1, authstr.length());
			userid = userid.trim();
			passwd=passwd.trim();
			if (!passwd.equals("")&&!userid.equals("")){
				if (userid.equals("admin") && passwd.equals("xb65779410")) {
					httpsession.setAttribute(ADM_SESSION, userid);
					return 0;
				} else{
					return -1;
				}
			}
		}
		String admin = (String)httpsession.getAttribute(ADM_SESSION);
		if (admin==null){
			response.setHeader("WWW-authenticate: basic realm=Vote	Administrator Administrator\n", "");
			response.setHeader("AUTH_TYPE", "Basic");
			response.setStatus(401);
			return -1;
		}
		return 0;
	}
}