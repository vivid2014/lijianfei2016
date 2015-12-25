/**
 *
 */
package com.util;

import java.lang.Integer;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;



/**
 * RSA �����ࡣ�ṩ���ܣ����ܣ�������Կ�Եȷ�����
 * ��Ҫ��http://www.bouncycastle.org����bcprov-jdk14-123.jar��
 *
 */
public class RSAutil {
	private static char[] HEXCHAR = { '0', '1', '2', '3', '4', '5', '6', '7','8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	static KeyPair kp = null;
	static String keypath = "e:/test.dat";

	public static String getKeyPath() {
		return keypath;
	}
	public static void setKeyPath(String temp) {
		keypath = temp;
	}

	/**
	 * * ������Կ�� *
	 *
	 * @return KeyPair *
	 * @throws EncryptException
	 */
	public static KeyPair generateKeyPair() {
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			final int KEY_SIZE = 512; //sun RSA��key size����512
			keyPairGen.initialize(KEY_SIZE, new SecureRandom());
			kp = keyPairGen.generateKeyPair();
			saveKeyPair(kp);
		} catch (Exception e) {
			System.err.println("[RSAUtil][generateKeyPair]error:"+e.getMessage());
		}
		return kp;
	}

	public static KeyPair getKeyPair()throws Exception{
		if (kp == null) {
			File file = new File(keypath);
			if(file.exists()) { //key�ļ����ڣ�����ļ���ȡ��
				FileInputStream fis = new FileInputStream(keypath);
				ObjectInputStream oos = new ObjectInputStream(fis);
				kp= (KeyPair) oos.readObject();
				oos.close();
				fis.close();
			} else { //key�ļ������ڣ���������key
				generateKeyPair();
			}
		}
		return kp;
	}

	public static void saveKeyPair(KeyPair kp)throws Exception{
		 FileOutputStream fos = new FileOutputStream(keypath);
     ObjectOutputStream oos = new ObjectOutputStream(fos);
     //������ļ�
     oos.writeObject(kp);
     oos.close();
     fos.close();
	}

	/**
	 * * ���ɹ�Կ �������������ȷ��*
	 *
	 * @param modulus *
	 * @param publicExponent *
	 * @return RSAPublicKey *
	 * @throws Exception
	 */
	public static RSAPublicKey generateRSAPublicKey(byte[] modulus,
			byte[] publicExponent) throws Exception {
		KeyFactory keyFac = null;
		try {
			keyFac = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException ex) {
			throw new Exception(ex.getMessage());
		}

		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(
				modulus), new BigInteger(publicExponent));
		try {
			return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
		} catch (InvalidKeySpecException ex) {
			throw new Exception(ex.getMessage());
		}
	}

	//�������������ȷ��
	public static PublicKey generatePublicKey(byte[] modulus,
			byte[] publicExponent) throws Exception {
		
		KeyFactory keyFac = null;
		try {
			keyFac = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException ex) {
			throw new Exception(ex.getMessage());
		}

		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(
				modulus), new BigInteger(publicExponent));
		try {
			return  keyFac.generatePublic(pubKeySpec);
		} catch (InvalidKeySpecException ex) {
			throw new Exception(ex.getMessage());
		}
		
	}
	
	//�������������ȷ��
	public static PublicKey generatePublicKey(BigInteger modulus,
			BigInteger publicExponent) throws Exception {
		
		KeyFactory keyFac = null;
		try {
			keyFac = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException ex) {
			throw new Exception(ex.getMessage());
		}

		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus,publicExponent);
		try {
			return  keyFac.generatePublic(pubKeySpec);
		} catch (InvalidKeySpecException ex) {
			throw new Exception(ex.getMessage());
		}
		
	}
	/**
	 * * ����˽Կ *
	 *
	 * @param modulus *
	 * @param privateExponent *
	 * @return RSAPrivateKey *
	 * @throws Exception
	 */
	public static RSAPrivateKey generateRSAPrivateKey(byte[] modulus,
			byte[] privateExponent) throws Exception {
		KeyFactory keyFac = null;
		try {
			keyFac = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException ex) {
			throw new Exception(ex.getMessage());
		}

		RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(new BigInteger(
				modulus), new BigInteger(privateExponent));
		try {
			return (RSAPrivateKey) keyFac.generatePrivate(priKeySpec);
		} catch (InvalidKeySpecException ex) {
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * * ���� *
	 *
	 * @param key
	 *            ���ܵ���Կ *
	 * @param data
	 *            �����ܵ��������� *
	 * @return ���ܺ������ *
	 * @throws Exception
	 */
	public static byte[] encrypt(PublicKey pk, byte[] data) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pk);
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * * ���� *
	 *
	 * @param key
	 *            ���ܵ���Կ *
	 * @param raw
	 *            �Ѿ����ܵ����� *
	 * @return ���ܺ������ *
	 * @throws Exception
	 */
	public static byte[] decrypt(PrivateKey pk, byte[] raw) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(cipher.DECRYPT_MODE, pk);
			return cipher.doFinal(raw);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	
	//toHexString���ֽ������ΪASSIC���ʾ���ַ���
    public static String toHexString(byte[] b) {
    	
    	//����һ�������κ��ַ����ַ��������������ʼ������ capacity ����ָ��
  		  StringBuilder sb = new StringBuilder(b.length * 2);
  		  for (int i = 0; i < b.length; i++) {
  		  sb.append(HEXCHAR[(b[i] & 0xf0) >>> 4]); 
  		// append ����ʼ�ս���Щ�ַ���ӵ���������ĩ�ˣ��� insert ��������ָ���ĵ�����ַ���
  		//����HEXCHAR����  
  		  sb.append(HEXCHAR[b[i] & 0x0f]);   
  		  }   
  		  return sb.toString();   
    }
    
   // ��ʮ�����������ַ�����ʽ��ת��Ϊ��ʮ���������ַ���
    public static String toDecString(String HexString){
  
    	int curInt = 0;
    	int len = HexString.length();
    	final BigInteger BIGING16 = new BigInteger("16"); 
    	BigInteger sum = new BigInteger("0");
    	for(int i = 0;i < len;i++){
    		 String curStr = HexString.substring(i, i+1);
    		 curInt = Integer.valueOf(curStr, 16);
    		 BigInteger curInteger = new BigInteger(Integer.toString(curInt));
    	 	 sum = sum.add(curInteger.multiply(BIGING16.pow(len-i-1)));
    	}
    	return sum.toString();
    	
    }
	/**
	 * * *
	 *
	 * @param args *
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		//*******************************������һ�������ת������**********************************************//
		
		int i = 11;
		String int16 = 	Integer.toHexString(i);
		String int8 = Integer.toOctalString(i);
		String int2 = Integer.toBinaryString(i);
		String int10 = Integer.valueOf("a0", 16).toString();
		String ints10 = Integer.valueOf(i).toString();
		i= Integer.parseInt("-FF",16);
		String aa1 = "123456";
		String aa2 = "abcdef";
		byte[] aaa1byte1 = aa1.getBytes();
		byte[] aaa1byte2 = aa2.getBytes();
		
	
		//*******************************�����ǲ���RSA��Կ�����ݹ�Կ������ã����ܡ�˽Կ���ܵĹ���**********************************************//
		
		// ��Կ����moduleHUANG��empoentHUANG��16���Ʊ�ʾ�������ַ���
		String moduleHUANG ="8099df2c3f092c05cccb6f24d5173860bf1772d7f7b8b60f5079a1b80700045" +
	    "d6f1b1c06d8b664515a4add8f3925e4fb053da7cd567b6be7c0d9f11218cf4e91";
		String empoentHUANG = "10001";
		
		moduleHUANG = toDecString(moduleHUANG);//����ԿתΪ10���Ʊ�ʾ�������ַ���
		empoentHUANG = toDecString(empoentHUANG);
		
		BigInteger moduleInt = new BigInteger(moduleHUANG); //ת��Ϊ��Կ����
		BigInteger empoentInt = new BigInteger(empoentHUANG);
		
		//����Կ
		PublicKey getPublicKeyFeiFei =  generatePublicKey(moduleInt,empoentInt);
		System.out.println(getPublicKeyFeiFei.getFormat());
		System.out.println(getPublicKeyFeiFei.toString());//��ʾһ�¹�Կ��Ϣ
		String testljf = "ljf629629629";
		byte[] encodeljf = encrypt(getPublicKeyFeiFei,testljf.getBytes());
		byte[] decodeljf = decrypt(getKeyPair().getPrivate(),encodeljf);//����Ե�˽Կ����
		System.out.println(new String(decodeljf));
		

		
		//***********************************����һ���µĹ�Կ��˽Կ�ԣ�Ȼ����ܽ���*******************************************////
		System.out.println("\n\n");
		String test = "hello world";
		byte[] en_test = encrypt(getKeyPair().getPublic(),test.getBytes());
		byte[] de_test = decrypt(getKeyPair().getPrivate(),en_test);
		System.out.println(en_test.toString());
		System.out.println(new String(en_test));
		System.out.println(new String(de_test));
		
		System.out.println(toHexString(en_test));
		System.out.println(new String(en_test));
		System.out.println(new String(de_test));
	}
}
