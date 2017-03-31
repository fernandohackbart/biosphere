package org.biosphere.tissue.utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.math.BigInteger;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.tissue.TissueManager;

import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;

public class KeystoreManager
{
  public KeystoreManager()
  {
    super();
    logger=new Logger();
  }  
  
  private Logger logger;
  
  public KeyStore getKeyStore(String cellName,String subjectName,String keyStorePass) throws NoSuchAlgorithmException, InvalidKeySpecException, OperatorCreationException, IOException, CertificateException, KeyStoreException
  {
    //this.dumpKeystore(this.generateKeyStore("biosphere",keyStorePass),keyStorePass);
    //this.showAlgorithm();

    //return this.getFileKeyStore("E:\\mywork\\Keystore\\testkey.jks",keyStorePass);
    //return this.generateKeyStore(cellName,subjectName,keyStorePass); 
    return this.generateKeyStoreSelf(cellName,subjectName,keyStorePass);
  }

 
  private String getCellDN(String subjectName)
  {
    logger.info("KeystoreGenerator.getCellDN()","Cell DN: CN="+subjectName+TissueManager.OUDN);
    return "CN="+subjectName+TissueManager.OUDN;
  }
  
  private String getCellCADN(String subjectName)
  {
    logger.info("KeystoreGenerator.getCellCADN()","Cell CA DN: CN="+subjectName+"-CA"+TissueManager.OUDN);
    //return "CN="+subjectName+"-CA"+OUDN;
    return "CN="+subjectName+TissueManager.OUDN;
  }
  
  public String dumpKeystore(KeyStore ks,String password)
  {
    logger.debug("KeystoreGenerator.dumpKeystore()","Dumping in memory keystore:");
    StringBuffer output = new StringBuffer();
    try
    {    
      Enumeration<String> e = ks.aliases();
      while(e.hasMoreElements())
      {
        String alias = e.nextElement();
        output.append("Alias: "+alias+"\n");    
        try
        {
          KeyStore.ProtectionParameter protParam=null;
          if (ks.isKeyEntry(alias))
          {
            protParam = new KeyStore.PasswordProtection(password.toCharArray());
          }
          KeyStore.Entry entry = ks.getEntry(alias, protParam);
          Set<KeyStore.Entry.Attribute> attrs = entry.getAttributes();
          for (KeyStore.Entry.Attribute attr: attrs)
          {
            output.append("  Attribute: "+attr.getName()+"="+attr.getValue()+"\n");
          }              
        }
        catch (UnrecoverableEntryException f)
        {
          logger.debug("KeystoreGenerator.dumpKeystore()","UnrecoverableEntryException:"+f.getLocalizedMessage());
        }
        Key key = ks.getKey(alias,password.toCharArray());
        String b64 = new String(Base64.encode(key.getEncoded()));
        logger.debug("KeystoreGenerator.dumpKeystore()","-----BEGIN PRIVATE KEY-----\n"+b64+"-----END PRIVATE KEY-----\n");
      }
    }
    catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e)
    {
      TissueExceptionHandler.handleGenericException(e,"KeystoreManager.dumpKeystore()","Error dumping the keystore: "+e.getLocalizedMessage());
    }
    return output.toString();
  }
  
  private void showAlgorithm()
  {
    for (Provider p: Security.getProviders())
    {
      for (Object o: p.keySet())
      {
        logger.info("KeystoreGenerator.showAlgorithm()","Security.getProviders():"+o);
      }
    }  

    try
    {
      SSLContext c = SSLContext.getDefault();
      SSLEngine engine = c.createSSLEngine();      
      for (String s: engine.getEnabledCipherSuites())
      {
        logger.info("KeystoreGenerator.showAlgorithm()","SSLEngine.getEnabledCipherSuites():"+s);
      }       
      for (String s: engine.getEnabledProtocols())
      {
        logger.info("KeystoreGenerator.showAlgorithm()","SSLEngine.getEnabledCipherSuites():"+s);
      }             
    }
    catch (NoSuchAlgorithmException e)
    {
      System.out.println(e.getMessage());
    }
  }

  private KeyStore getFileKeyStore(String file, String password) throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException
  {
    //keytool -genkey -keyalg RSA -alias selfsigned -keystore /tmp/testkey.jks -storepass password -validity 360 -keysize 2048
    KeyStore ks = null;
    ks = KeyStore.getInstance("JKS");
    FileInputStream fis = new FileInputStream(file);
    ks.load(fis,password.toCharArray());      
    return ks;
  }

  private KeyStore generateKeyStore(String cellName,String subjectName, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, OperatorCreationException, IOException, CertificateException, KeyStoreException
  {
    KeyStore ks = null;
    KeyPair cakp = generateKeyPair();
    CertificationRequest cacr = generateRequest(cakp.getPublic(),cakp.getPrivate(),TissueManager.CADN,cellName+"-CA");
    X509Certificate caCertificate = signRequest(cacr,cakp.getPrivate(),new X500Name(TissueManager.CADN),TissueManager.validityCA);
    KeyPair kp = generateKeyPair();
    CertificationRequest cr = generateRequest(kp.getPublic(),kp.getPrivate(),getCellDN(subjectName),cellName);
    X509Certificate certificate = signRequest(cr,cakp.getPrivate(),new JcaX509CertificateHolder(caCertificate).getSubject(),TissueManager.validity);     
    ks = KeyStore.getInstance("JKS");
    ks.load(null, null);
    Certificate[] caCertChain = new Certificate[1];
    caCertChain[0] = caCertificate;
    ks.setKeyEntry("CA", cakp.getPrivate(), password.toCharArray(), caCertChain);
    ks.setCertificateEntry("CA-cert", caCertificate);      
    Certificate[] certChain = new Certificate[1];
    certChain[0] = certificate;
    ks.setKeyEntry(subjectName, kp.getPrivate(), password.toCharArray(), certChain);
    ks.setCertificateEntry(subjectName+"-cert", certificate);      
    return ks;
  }

  private KeyStore generateKeyStoreSelf(String cellName,String subjectName, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, OperatorCreationException, IOException, CertificateException, KeyStoreException
  {
    KeyPair kp = generateKeyPair();
    X500Name subject = new X500Name(getCellCADN(subjectName));
    CertificationRequest cr = generateRequest(kp.getPublic(),kp.getPrivate(),getCellDN(subjectName),cellName);
    X509Certificate certificate = signRequest(cr,kp.getPrivate(),subject,TissueManager.validity);
    Certificate[] certChain = new Certificate[1];
    certChain[0] = certificate;
    
    KeyStore ks = null;
    ks = KeyStore.getInstance("JKS");
    ks.load(null, null);
    ks.setKeyEntry(cellName,kp.getPrivate(),password.toCharArray(), certChain); 
    //ks.setCertificateEntry(subjectName,certificate); 
    return ks;
  }
  
  private KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidKeySpecException
  {
    AsymmetricCipherKeyPair ackp=null;
    SecureRandom sr = new SecureRandom();
    RSAKeyPairGenerator rsakpg = new RSAKeyPairGenerator();
    RSAKeyGenerationParameters params = new RSAKeyGenerationParameters(new BigInteger("35"),sr,TissueManager.keyStrenght,8);
    rsakpg.init(params);
    ackp = rsakpg.generateKeyPair();
    RSAKeyParameters publicKey = (RSAKeyParameters) ackp.getPublic();
    RSAPrivateCrtKeyParameters privateKey = (RSAPrivateCrtKeyParameters) ackp.getPrivate();   
    PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(publicKey.getModulus(), publicKey.getExponent()));
    PrivateKey privKey = KeyFactory.getInstance("RSA").generatePrivate(new RSAPrivateCrtKeySpec(publicKey.getModulus(), publicKey.getExponent(),privateKey.getExponent(), privateKey.getP(), privateKey.getQ(),privateKey.getDP(), privateKey.getDQ(), privateKey.getQInv()));      
    return new KeyPair(pubKey,privKey);    
  }
  
  private CertificationRequest generateRequest(PublicKey pubKey,PrivateKey privKey,String subjectDN,String cellName) throws OperatorCreationException, IOException
  {
    X500Name subject= new X500Name(subjectDN);
    PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(subject, pubKey);
    JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder(TissueManager.SignerBuilderName);
    ContentSigner signer = csBuilder.build(privKey);
    
    //Add alternativeName to the certificate with the name of the Cell
    
    //List<GeneralName> namesList = new ArrayList<>();
    //namesList.add(GeneralNameTool.toGeneralName(cellName));
    //ExtensionsGenerator extGen = new ExtensionsGenerator();
    //GeneralNames subjectAltNames = new GeneralNames(namesList.toArray(new GeneralName [] {}));
    //extGen.addExtension(Extension.subjectAlternativeName, false, subjectAltNames);
    //p10Builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extGen.generate());
                
    CertificationRequest csr = p10Builder.build(signer).toASN1Structure();
    return csr;
  }

  private X509Certificate signRequest(CertificationRequest inputCSR, PrivateKey caPrivKey,X500Name issuer,int validity) throws OperatorCreationException, IOException, CertificateException
  {
    Date startDate = new Date();
    Calendar c = Calendar.getInstance();
    c.setTime(startDate);
    c.add(Calendar.YEAR,validity);
    Date expiryDate = c.getTime();
    BigInteger serialNumber= BigInteger.valueOf(TissueManager.defaultSerialNumber);
    PKCS10CertificationRequest pk10Holder = new PKCS10CertificationRequest(inputCSR);
    X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(issuer,serialNumber,startDate,expiryDate,pk10Holder.getSubject(),pk10Holder.getSubjectPublicKeyInfo());
    JcaContentSignerBuilder builder = new JcaContentSignerBuilder(TissueManager.SignerBuilderName);
    ContentSigner signer = builder.build(caPrivKey);
    byte[] certBytes = certBuilder.build(signer).getEncoded();
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    X509Certificate certificate = (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(certBytes));
    return certificate;
  }
}
