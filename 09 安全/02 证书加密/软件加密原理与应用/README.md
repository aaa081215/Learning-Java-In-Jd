#  软件加密原理与应用
作为一名程序猿/媛，编写安全的代码比编写优雅的代码更重要，因为安全是一切应用的基础。所有开发者都应该掌握一些常用的加密与解密技术，尽可能不让你自己编写的代码
给别有用心的人留下可乘之机。
## 加密算法的分类
加密算法通常分为两大类：“对称式加密算法”和“非对称式加密算法”。另外还有两类严格来说不算加密算法，但是开发中也经常会用到的算法：消息摘要算法以及消息编码算法。
### 对称加密算法
指加密和解密使用相同密钥的加密算法，常见的对称加密算法有：AES、DES、3DES、DESX、Blowfish、IDEA、RC4、RC5、RC6等。
### 非对称加密算法
指加密和解密使用不同密钥的加密算法，通常有两个密钥，称为“公钥”和“私钥”，“公钥”是指可以对外公布的，“私钥”则只能由持有人一个人知道，它们两个必需配对使用，一个用于加密而另一个用于解密。常见的非对称加密算法有：RSA、ECC、Diffie-Hellman、El Gamal、DSA等。
### 消息摘要算法
消息摘要算法是一种单向加密算法，只能加密不能解密，因此消息摘要算法常用在不可还原的密码存储、信息完整性校验等场景。常见的消息摘要算法有：MD2、MD4、MD5、HAVAL、SHA、SHA-1、HMAC、HMAC-MD5、HMAC-SHA1等。
### 消息编码算法
消息编码算法可以将二进制数据编码为字符，可用于在HTTP环境下传递较长的标识信息，常用的算法有：Base64编码。

## 常用的加密算法
### DES（Data Encryption Standard）数据加密标准
- [x] 对称加密算法，速度较快，适用于加密大量数据的场合

加密算法的由来：1977年1月，美国政府颁布，采纳IBM公司设计的方案作为非机密数据的正式数据加密标准，即DES（Data Encryption Standard）。
DES算法把64位的明文输入块变为64位的密文输出块，它所使用的密钥也是64位，其功能是把输入的64位数据块按位重新组合，并把输出分為L0、R0两部分，每部分各长32位。

DES算法的入口参数有三个：Key、Data、Mode。其中Key为8个字节共64位,是DES算法的工作密钥；Data也为8个字节64位，是要被加密或被解密的数据；Mode为DES的工作方式，有两种：加密或解密[Java代码就是2个常量]。
javax.crypto.Cipher 是实现加密解密的类。
一个具体 Cipher对象通过调用静态方法 getInstance()来获得。
上述方法需要一个变换字符串做为参数： algorithm/mode/padding (例如， "DES/ECB/PKCS5Padding") 
使用密钥或另加一个算法参数对象进行初始化。
通过调用update()对传递进来的数据进行加密或解密。
最后调用doFinal()来完成加密或解密过程。

代码示例：

```
/**
* 参数加密，返回DES密文
* @return
* @throws Exception
*/
public String desEncrypt() throws Exception {
	String seed = "5e722b6f247136da3fba0c356fea0817";
	String key = key(seed);  //见生成密钥方法和加密种子
	System.out.println(key);
	String inputStr = "{\"productCode\":\"01006\",\"acctType\":\"00\"}"; //参数json
	String encrypt = encrypt(inputStr, key, "UTF-8"); //见DES加密方法
	return encrypt;
}

/**
  * 生成密钥方法
  * @param seed 密钥种子
  * @return 密钥 BASE64
   * @throws Exception
   */
public static String key(String seed) throws Exception {
	byte[] seedBase64DecodeBytes = Base64.decode(seed);
	
	SecureRandom secureRandom = new SecureRandom(seedBase64DecodeBytes);
	KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
	keyGenerator.init(secureRandom);
	SecretKey secretKey = keyGenerator.generateKey();
	byte[] bytes = secretKey.getEncoded();
	return Base64.encode(bytes);
}

/**
* 加密方法
* @param text 明文
* @param key 密钥 BASE64
* @param charset 字符集
* @return 密文
* @throws Exception
*/
public static String encrypt(String text, String key, String charset) throws Exception {
	byte[] keyBase64DecodeBytes = Base64.decode(key);
		
	DESKeySpec desKeySpec = new DESKeySpec(keyBase64DecodeBytes);
	SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
    Cipher cipher = Cipher.getInstance("DES");
	cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	byte[] textBytes = text.getBytes(charset);
	byte[] bytes = cipher.doFinal(textBytes);
	return Base64.encode(bytes);
}

/**
* 解密方法
* 
* @param text 密文
* @param key 密钥 BASE64 
* @param charset 字符集
* @return 明文
* @throws Exception
*/
public static String decrypt(String text, String key, String charset) throws Exception {
    byte[] keyBase64DecodeBytes = Base64.decode(key);
		
    DESKeySpec desKeySpec = new DESKeySpec(keyBase64DecodeBytes);
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
    SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
	    
    Cipher cipher = Cipher.getInstance("DES");
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    byte[] textBytes = Base64.decode(text);
    byte[] bytes = cipher.doFinal(textBytes);
	    
    return new String(bytes, charset);
}

```



#### 3DES（Triple DES）
- [x] 是基于DES的对称算法，对一块数据用三个不同的密钥进行三次加密，强度更高


### AES（Advanced Encryption Standard）：高级加密标准
- [x] 对称算法，是下一代的加密算法标准，速度快，安全级别高

AES家族有AES-128、AES-192、AES-256三种加密方式，数字表示的是密钥长度，比如AES-128说的是密钥是128位，也就是16个字节长度的字符串。

**代码示例：**

```
import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AesUtil {

    private static byte[] iv = new byte[] { 85, 60, 12, 116, 99, -67, -83, 19, -118, -73, -24, -8, 82, -24, -56, -14 };

    private String secrity_key =Config.getProperty("mrd.ticket.aes.key"); //配置文件中的密钥

    private static class AesUtilHolder {
        private static final AesUtil INSTANCE = new AesUtil();
    }

    private static Cipher cipher_encrypt;
    private static Cipher cipher_decrypt;
    private static Key key;

    private AesUtil() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            key = new SecretKeySpec(secrity_key.getBytes("ASCII"), "AES");
            cipher_decrypt = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cipher_decrypt.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            cipher_encrypt = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cipher_encrypt.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static final AesUtil getInstance() {
        return AesUtilHolder.INSTANCE;
    }

    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @return
     */
    public static byte[] encrypt(String content) {
        try {
            byte[] enc = cipher_encrypt.doFinal(content.getBytes());
            return enc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content 待解密内容
     * @return
     */
    public static byte[] decrypt(byte[] content) {
        try {
            byte[] result = cipher_decrypt.doFinal(content);
            return result; // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getBASE64(byte[] s) throws Exception {
        if (s == null) {
            return null;
        }
        return new String(Base64.encodeBase64(s));
    }

    public static byte[] getFromBASE64(String s) {
        if (s == null) {
            return null;
        }
        try {
            byte[] b = Base64.decodeBase64(s.replaceAll(" ", "").getBytes());
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    public static String encryptStr(String content) {
        try {
            byte[] encryptResult = encrypt(content);
            return getBASE64(encryptResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decryptStr(String content) {
        byte[] b = getFromBASE64(content);
        return new String(decrypt(b));
    }
}

```

### RSA
- [x] 非对称加密算法，速度慢，一般来说只用于少量数据加密。

RSA是1977年由Ron Rivest、Adi Shamir和Leonard Adleman一起提出的，当时他们三人都在麻省理工学院工作，RSA就是他们三人姓氏开头字母拼在一起组成的。

![image](rsa.jpg)

RSA是第一个既能用于数据加密也能用于数字签名的算法。它易于理解和操作，也很流行。RSA是被研究得最广泛的公钥算法，从提出到现今的三十多年里，经历了各种攻击的考验，逐渐为人们接受，截止2017年被普遍认为是最优秀的公钥方案之一。

RSA算法基于一个十分简单的数论事实：将两个大质数相乘十分容易，但是想要对其乘积进行因式分解却极其困难，因此可以将乘积公开作为加密密钥。

**代码示例：**
```
import org.apache.commons.codec.binary.Base64;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
 
/**
 * 需要依赖 commons-codec 包 
 */
public class RSACoder {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
 
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";
 
    public static byte[] decryptBASE64(String key) {
        return Base64.decodeBase64(key);
    }
 
    public static String encryptBASE64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }
 
    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       加密数据
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        // 解密由base64编码的私钥
        byte[] keyBytes = decryptBASE64(privateKey);
        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);
        return encryptBASE64(signature.sign());
    }
 
    /**
     * 校验数字签名
     *
     * @param data      加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return 校验成功返回true 失败返回false
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        // 解密由base64编码的公钥
        byte[] keyBytes = decryptBASE64(publicKey);
        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 取公钥匙对象
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);
        // 验证签名是否正常
        return signature.verify(decryptBASE64(sign));
    }
 
    public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception{
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }
 
    /**
     * 解密<br>
     * 用私钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(String data, String key)
            throws Exception {
        return decryptByPrivateKey(decryptBASE64(data),key);
    }
 
    /**
     * 解密<br>
     * 用公钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, String key)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }
 
    /**
     * 加密<br>
     * 用公钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(String data, String key)
            throws Exception {
        // 对公钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }
 
    /**
     * 加密<br>
     * 用私钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }
 
    /**
     * 取得私钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Key> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return encryptBASE64(key.getEncoded());
    }
 
    /**
     * 取得公钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Key> keyMap)
            throws Exception {
        Key key = keyMap.get(PUBLIC_KEY);
        return encryptBASE64(key.getEncoded());
    }
 
    /**
     * 初始化密钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Key> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        Map<String, Key> keyMap = new HashMap(2);
        keyMap.put(PUBLIC_KEY, keyPair.getPublic());// 公钥
        keyMap.put(PRIVATE_KEY, keyPair.getPrivate());// 私钥
        return keyMap;
    }
    
    public static void main(String[] args) throws Exception {
        Map<String, Key> keyMap = initKey();
        String publicKey = getPublicKey(keyMap);
        String privateKey = getPrivateKey(keyMap);
        
        System.out.println(keyMap);
        System.out.println("-----------------------------------");
        System.out.println(publicKey);
        System.out.println("-----------------------------------");
        System.out.println(privateKey);
        System.out.println("-----------------------------------");
        byte[] encryptByPrivateKey = encryptByPrivateKey("123456".getBytes(),privateKey);
        byte[] encryptByPublicKey = encryptByPublicKey("123456",publicKey);
        System.out.println(new String(encryptByPrivateKey));
        System.out.println("-----------------------------------");
        System.out.println(new String(encryptByPublicKey));
        System.out.println("-----------------------------------");
        String sign = sign(encryptByPrivateKey,privateKey);
        System.out.println(sign);
        System.out.println("-----------------------------------");
        boolean verify = verify(encryptByPrivateKey,publicKey,sign);
        System.out.println(verify);
        System.out.println("-----------------------------------");
        byte[] decryptByPublicKey = decryptByPublicKey(encryptByPrivateKey,publicKey);
        byte[] decryptByPrivateKey = decryptByPrivateKey(encryptByPublicKey,privateKey);
        System.out.println(new String(decryptByPublicKey));
        System.out.println("-----------------------------------");
        System.out.println(new String(decryptByPrivateKey));
        
    }
}

```

### MD5（Message Digest Algorithm 5）消息摘要算法
一种单向散列算法，非可逆，相同的明文产生相同的密文，严格来说不算加密算法。在90年代初由MIT的计算机科学实验室和RSA Data Security Inc发明，经MD2、MD3和MD4发展而来。
    
MD5的典型应用是防“篡改”。举个栗子：你用MD5算法对一段文本生成一个MD5的值并记录在案，然后你可以传输这段文本给别人，别人如果修改了文本中的任何内容，你对这个文本重新计算MD5时就会发现它被篡改了。
    
MD5还广泛用于对用户的密码进行加密，在很多系统中，用户的密码都是以MD5值的方式保存在数据库里的。 用户Login的时候，系统是把用户输入的密码计算成MD5值，然后再去和系统中保存的MD5值进行比较，而系统并不“知道”用户的密码是什么。正是因为MD5是一种单向加密算法，无法进行解密，即使黑客入侵了数据库，他也不知道用户的密码是什么。

- [ ] 破解方法

    常见的破解方法是一种被称为"跑字典"的方法。就是用穷举的方式，将所有可能的密码排列组合先算出它们的MD5值，然后用key（MD5值）-value（密码）的方式存到字典表里，最后根据要破解的MD5值在这个字典表里进行查询，获得对应的密码。
    但即使假设密码的最大长度为8，同时密码只能是大小写字母和数字，共26+26+10=62个字符，排列组合出的字典的项数则是P(62,1)+P(62,2)....+P(62,8)，那也已经是一个很天文的数字了，存储这个字典就需要TB级的磁盘组，而且这种方法还有一个前提，就是能获得目标密码的MD5值的情况下才可以。

- [x] 防御方法

    常用的防御方法是一种被称为“加盐”的方法。所谓加盐（salt），就是在对用户的密码进行MD5加密前给加点“佐料”：插入一个随机字符串，然后再加密。这个随机字符串系统也会一块保存下来，当用户登录时，系统对用户输入的密码以同样的方式撒上同样的“佐料”，然后再MD5加密进行比较，以确定密码是否正确。这里的“佐料”被称作“Salt值”，这个值是由系统随机生成的，并且只有系统知道。这样，黑客如果只拿到了密码的MD5值，是没有办法破解的。即使MD5值和Salt值都拿到了，他也得知道这个盐是加在什么位置的，因为Salt不一定是加在最前面或最后面，也可以插在中间，也可以分开插入，也可以倒序，等等，程序设计时可以灵活调整，这样可以使破解的难度指数级增长，被破解的概率大大降低。

**加密步骤**
```
//初始化加密类
MessageDigest   md5=MessageDigest.getInstance(“MD5”);

//更新源数据
md5.update(data);

//生成摘要（加密）
byte[] result=md5.digest();
```
生成的字节数组可读性差，一般会转换为16进制字符串用于显示：

```
public static String fromBytesToHex(byte[] resultBytes){
    StringBuilder builder=new StringBuilder();
    for(int i=0;i<resultBytes.length;i++){
	    if(Integer.toHexString(0XFF&resultBytes[i]).length()==1){
		    builder.append("0").append(Integer.toHexString(0XFF&resultBytes[i]));
	    }else{
		    builder.append(Integer.toHexString(0XFF&resultBytes[i]));
	    }
    }
    return builder.toString();
}

```

### SHA（Secure Hash Algorithm）安全哈希算法
主要适用于数字签名标准里面定义的数字签名算法
#### SHA算法分类
- SHA-1：为任意长度数据生成160位的摘要信息
- SHA-256
- SHA-384
- SHA-512

SHA-后面的数字代表生成的摘要长度（以位为单位），生成的摘要长度越长，安全性越高，不同文件的加密结果重复的机率越小。

- [x] SHA-1与MD5的比较

因为二者均由MD4导出，SHA-1和MD5彼此很相似。相应的，他们的强度和其他特性也是相似，但还有以下几点不同：

- SHA-1对强行攻击有更大的强度：最显著和最重要的区别是SHA-1摘要比MD5摘要长32 位。

- 对密码分析的安全性：由于MD5的设计，易受密码分析的攻击，SHA-1显得不易受这样的攻击。

- 速度：在相同的硬件上，SHA-1的运行速度比MD5慢
 
**实现步骤：**

```
//初始化
MessageDigest.getInstance(“SHA”);

//更新
sha.update(byte[] data)

//生成摘要
sha.digest();

//TODO:转换为16进制便于显示 (请参考MD5的代码fromBytesToHex方法)
```

### Base64

Base64就是一种基于64个可打印字符来表示二进制数据的方法，可用于在HTTP环境下传递较长的标识信息，也会经常用作一个简单的“加密”来保护某些数据。采用Base64编码不仅比较简短，同时也具有不可读性，即所编码的数据不会被人用肉眼所直接看到。

```
//加密
new  BASE64Encoder().encode(byte[] data)

//解密
new  BASE64Decoder().decodeBuffer(String data)

```
==注意： BASE64不能作为真正的数据加密，因为任何一个BASE64Decoder都可以解开数据。==

## 加密算法的选择
以上介绍了那么多种加密算法，那么我们在实际使用的过程中究竟该使用哪一种比较好呢？我们应该根据自己的使用场景和特点来确定。

- 由于非对称加密算法的运行速度比对称加密算法的速度慢很多，当我们需要加密大量的数据时，建议采用对称加密算法，提高加解密速度。

- 对称加密算法不能实现签名，因此签名只能采用非对称加密算法。

- 在实际的操作过程中，我们通常采用的方式是：采用非对称加密算法管理对称算法的密钥，然后用对称加密算法加密数据，这样我们就集成了两类加密算法的优点，既实现了加密速度快的优点，又实现了安全方便管理密钥的优点。

如果在选定了加密算法后，那采用多少位的密钥呢？一般来说，密钥越长，运行的速度就越慢，应该根据的我们实际需要的安全级别来选择，一般来说，RSA建议采用1024位的数字，ECC建议采用160位，AES采用128为即可。