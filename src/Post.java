import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Post {
	static PrivateKey privateKey;
	static PublicKey publicKey;

	public static void main(String[] args) throws Exception {
		if(args.length!=2)
		{
			System.out.println("Incorrect number of arguments");
			System.exit(1);
		}
		Security.addProvider(new BouncyCastleProvider());
		String strURL = "https://test.lgaming.net/external/extended";
		String strXMLFilename = args[0]+"\\res\\"+args[1];
		String st = convertXMLFileToString(args[0]+"\\res\\"+args[1]);
		// st = st.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
		privateKey = Sign.getPrivateKey(args[0]+"\\res\\private.pem");
		publicKey = Sign.getPublicKey(args[0]+"\\res\\public.pem");
		String sign = Sign.sign(privateKey, st);
		File input = new File(strXMLFilename);
		PostMethod post = new PostMethod(strURL);
		post.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(
				input), input.length()));
		
		post.setRequestHeader("PayLogic-Signature", sign);
		post.setRequestHeader("Content-type", "text/xml; charset=UTF-8");
		HttpClient httpclient = new HttpClient();
		try {
			System.out.println("Request PayLogic-Signature header: \n"+post.getRequestHeader("PayLogic-Signature").getValue()+"\n\n\n");
			System.out.println("Request body: \n"+st+"\n\n\n\n");
			int result = httpclient.executeMethod(post);
			System.out.println("Response status code: \n" + result+"\n");
			try {
				System.out.println("Response from this server:" + Sign.verify(publicKey,post.getResponseBodyAsString(), post.getRequestHeader("PayLogic-Header").getValue())+"\n");
			} catch (NullPointerException e) {
				System.out.println("Response haven't PayLogic-Signature header\n");
			}
			System.out.println("Response body: \n"+post.getResponseBodyAsString());
		} finally {
			post.releaseConnection();
		}

	}

	public static String convertXMLFileToString(String fileName) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			FileInputStream inputStream = new FileInputStream(
					new File(fileName));
			org.w3c.dom.Document doc = documentBuilderFactory
					.newDocumentBuilder().parse(inputStream);
			StringWriter stw = new StringWriter();
			Transformer serializer = TransformerFactory.newInstance()
					.newTransformer();
			serializer.transform(new DOMSource(doc), new StreamResult(stw));
			return stw.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
