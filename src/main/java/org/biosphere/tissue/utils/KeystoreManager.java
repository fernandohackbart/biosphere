package org.biosphere.tissue.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

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
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.tissue.TissueManager;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeystoreManager {
	public KeystoreManager() {
		super();
		logger = LoggerFactory.getLogger(KeystoreManager.class);
	}

	private Logger logger;

	public KeyStore getKeyStore(String cellName, String subjectName, String keyStorePass)
			throws NoSuchAlgorithmException, InvalidKeySpecException, OperatorCreationException, IOException,
			CertificateException, KeyStoreException {
		// return this.generateKeyStore(cellName,subjectName,keyStorePass);
		return this.generateKeyStoreSelf(cellName, subjectName, keyStorePass);
	}

	/*
	private KeyStore generateKeyStore(String cellName, String subjectName, String password)
			throws NoSuchAlgorithmException, InvalidKeySpecException, OperatorCreationException, IOException,
			CertificateException, KeyStoreException {

		KeyPair cakp = generateKeyPair();
		PKCS10CertificationRequest cacr = generateRequest(cakp.getPublic(), cakp.getPrivate(), getCellCADN(cellName),
				cellName + "-CA");
		X509Certificate caCertificate = signRequest(cacr, cakp.getPrivate(), new X500Name(getCellCADN(cellName)),cellName,subjectName,
				TissueManager.validityCA);

		KeyPair kp = generateKeyPair();
		PKCS10CertificationRequest cr = generateRequest(kp.getPublic(), kp.getPrivate(), getCellDN(subjectName),
				cellName);
		X509Certificate certificate = signRequest(cr, cakp.getPrivate(),
				new JcaX509CertificateHolder(caCertificate).getSubject(),cellName,subjectName, TissueManager.validity);

		KeyStore ks = null;
		ks = KeyStore.getInstance("JKS");
		ks.load(null, null);

		Certificate[] caCertChain = new Certificate[1];
		caCertChain[0] = caCertificate;
		ks.setKeyEntry("CA", cakp.getPrivate(), password.toCharArray(), caCertChain);
		ks.setCertificateEntry("CA-cert", caCertificate);
		Certificate[] certChain = new Certificate[1];
		certChain[0] = certificate;
		ks.setKeyEntry(subjectName, kp.getPrivate(), password.toCharArray(), certChain);
		ks.setCertificateEntry(subjectName + "-cert", certificate);
		return ks;
	}
	*/

	private KeyStore generateKeyStoreSelf(String cellName, String subjectName, String password)
			throws NoSuchAlgorithmException, InvalidKeySpecException, OperatorCreationException, IOException,
			CertificateException, KeyStoreException {
		KeyPair kp = generateKeyPair();
		PKCS10CertificationRequest cr = generateRequest(kp.getPublic(), kp.getPrivate(), getCellDN(subjectName),
				cellName);
		X509Certificate certificate = signRequest(cr, kp.getPrivate(), new X500Name(getCellCADN(cellName)),cellName
				,subjectName,TissueManager.validity);

		Certificate[] certChain = new Certificate[1];
		certChain[0] = certificate;

		KeyStore ks = null;
		ks = KeyStore.getInstance("JKS");
		ks.load(null, null);
		ks.setKeyEntry(cellName, kp.getPrivate(), password.toCharArray(), certChain);
		// ks.setCertificateEntry(subjectName,certificate);
		return ks;
	}

	private KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidKeySpecException {
		AsymmetricCipherKeyPair ackp = null;
		SecureRandom sr = new SecureRandom();
		RSAKeyPairGenerator rsakpg = new RSAKeyPairGenerator();
		RSAKeyGenerationParameters params = new RSAKeyGenerationParameters(new BigInteger(TissueManager.keyBigInteger),
				sr, TissueManager.keyStrenght, 8);
		rsakpg.init(params);
		ackp = rsakpg.generateKeyPair();
		RSAKeyParameters publicKey = (RSAKeyParameters) ackp.getPublic();
		RSAPrivateCrtKeyParameters privateKey = (RSAPrivateCrtKeyParameters) ackp.getPrivate();
		PublicKey pubKey = KeyFactory.getInstance("RSA")
				.generatePublic(new RSAPublicKeySpec(publicKey.getModulus(), publicKey.getExponent()));
		PrivateKey privKey = KeyFactory.getInstance("RSA")
				.generatePrivate(new RSAPrivateCrtKeySpec(publicKey.getModulus(), publicKey.getExponent(),
						privateKey.getExponent(), privateKey.getP(), privateKey.getQ(), privateKey.getDP(),
						privateKey.getDQ(), privateKey.getQInv()));
		return new KeyPair(pubKey, privKey);
	}

	private PKCS10CertificationRequest generateRequest(PublicKey pubKey, PrivateKey privKey, String subjectDN,
			String cellNameASN) throws OperatorCreationException, IOException {
		X500Name subject = new X500Name(subjectDN);
		PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(subject, pubKey);
		
		logger.debug("KeystoreManager.generateRequest() subjectDN: " + cellNameASN);
		ExtensionsGenerator extGen = new ExtensionsGenerator();
		extGen.addExtension(Extension.subjectAlternativeName, false,
				new GeneralNames(new GeneralName(GeneralName.dNSName, cellNameASN)));
		p10Builder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extGen.generate());

		JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder(TissueManager.SignerBuilderName);
		ContentSigner signer = csBuilder.build(privKey);
		PKCS10CertificationRequest req = p10Builder.build(signer);
		formatRequest(req);
		return req;
	}

	private X509Certificate signRequest(PKCS10CertificationRequest inputCSR, PrivateKey caPrivKey, X500Name issuer,String cellName,String subjectName,
			int validity) throws OperatorCreationException, IOException, CertificateException {
		Date startDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		c.add(Calendar.YEAR, validity);
		Date expiryDate = c.getTime();
		BigInteger serialNumber = BigInteger.valueOf(TissueManager.defaultSerialNumber);
		X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(issuer, serialNumber, startDate, expiryDate,
				inputCSR.getSubject(), inputCSR.getSubjectPublicKeyInfo());
		
		/*
		Attribute[] attrs = inputCSR.getAttributes(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest);
		for(int i = 0; i < attrs.length; i++)
		{
			logger.debug("KeystoreManager.signRequest()", "Attribute :"+attrs[i].toASN1Primitive());
			ASN1Encodable[] attrVals=attrs[i].getAttributeValues();
			for(int j= 0; j < attrVals.length; j++)
			{
				logger.debug("KeystoreManager.signRequest()", "Attribute value:"+attrVals[j].toASN1Primitive());
				//certBuilder.addExtension(Extension.subjectAlternativeName, true, attrVals[j].toASN1Primitive());
			}
		}
		*/
		
		GeneralName[] gnArray = {new GeneralName(GeneralName.dNSName,cellName),new GeneralName(GeneralName.dNSName,subjectName)};
		GeneralNames gns = new GeneralNames(gnArray);
		certBuilder.addExtension(Extension.subjectAlternativeName, false,gns);
		KeyUsage usage = new KeyUsage(KeyUsage.keyCertSign
	            | KeyUsage.digitalSignature | KeyUsage.keyEncipherment
	            | KeyUsage.dataEncipherment | KeyUsage.cRLSign);
		certBuilder.addExtension(Extension.keyUsage, false, usage);
		
		JcaContentSignerBuilder builder = new JcaContentSignerBuilder(TissueManager.SignerBuilderName);
		ContentSigner signer = builder.build(caPrivKey);
		byte[] certBytes = certBuilder.build(signer).getEncoded();
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) certificateFactory
				.generateCertificate(new ByteArrayInputStream(certBytes));
		return certificate;
	}

	public String dumpKeystore(KeyStore ks, String password, String cellName) {
		logger.trace("KeystoreManager.dumpKeystore() Dumping in memory keystore:");

		StringBuffer output = new StringBuffer();
		try {
			output.append("  Cell key: \n" + getKey(ks, password, cellName) + "\n");

			Enumeration<String> e = ks.aliases();
			while (e.hasMoreElements()) {
				String alias = e.nextElement();
				output.append("Alias: " + alias + "\n");
				try {
					X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
					output.append("  Certificate issuer DN: " + cert.getIssuerDN() + "\n");
					output.append("  Certificate subject DN: " + cert.getSubjectDN() + "\n");
					output.append("  Certificate serial number: " + cert.getSerialNumber() + "\n");
					output.append("  Certificate not before: " + cert.getNotBefore() + "\n");
					output.append("  Certificate not after: " + cert.getNotAfter() + "\n");
					output.append("  Certificate signing alg: " + cert.getSigAlgName() + "\n");
					output.append("  Certificate type: " + cert.getType() + "\n");

					if (cert.getSubjectAlternativeNames() != null) {
						Iterator it = cert.getSubjectAlternativeNames().iterator();
						while (it.hasNext()) {
							List list = (List) it.next();
							output.append("  Certificate ASN: " +getGeneralName((int)list.get(0))+":"+list.get(1).toString()+"\n");
						}
					}
					output.append("  Certificate: \n" + getCertificate(ks, alias) + "\n");
				} catch (IOException f) {
					logger.error("KeystoreManager.dumpKeystore() IOException:" + f.getLocalizedMessage());
				} catch (CertificateParsingException f) {
					logger.error("KeystoreManager.dumpKeystore() CertificateParsingException:" + f.getLocalizedMessage());
				}
			}
		} catch (NoSuchAlgorithmException | KeyStoreException e) {
			TissueExceptionHandler.handleGenericException(e, "KeystoreManager.dumpKeystore()",
					"Error dumping the keystore: " + e.getLocalizedMessage());
		} catch (IOException | UnrecoverableKeyException e) {
			logger.error("KeystoreManager.dumpKeystore()", "UnrecoverableEntryException:" + e.getLocalizedMessage());
		}
		return output.toString();
	}

	public String showAlgorithm() {
		StringBuffer output = new StringBuffer();
		for (Provider p : Security.getProviders()) {
			for (Object o : p.keySet()) {
				output.append("Security.getProviders():" + o + "\n");
			}
		}
		try {
			SSLContext c = SSLContext.getDefault();
			SSLEngine engine = c.createSSLEngine();
			for (String s : engine.getEnabledCipherSuites()) {
				output.append("SSLEngine.getEnabledCipherSuites():" + s + "\n");
			}
			for (String s : engine.getEnabledProtocols()) {
				output.append("SSLEngine.getEnabledCipherSuites():" + s + "\n");
			}
		} catch (NoSuchAlgorithmException e) {
			TissueExceptionHandler.handleGenericException(e, "KeystoreManager.showAlgorithm()",
					"Error showAlgorithm: " + e.getLocalizedMessage());
		}
		return output.toString();
	}

	public String getKey(KeyStore ks, String password, String keyAlias)
			throws KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
		logger.info("KeystoreManager.getKey() KeyAlias:" + keyAlias);
		Key key = ks.getKey(keyAlias, password.toCharArray());
		StringWriter sw = new StringWriter();
		PemWriter pw = new PemWriter(sw);
		pw.writeObject(new JcaMiscPEMGenerator(key));
		pw.flush();
		pw.close();
		String pemEncodedCert = sw.toString();
		logger.trace("KeystoreManager.getKey() \n" + pemEncodedCert);
		return pemEncodedCert;
	}

	public String getCertificate(KeyStore ks, String certAlias) throws KeyStoreException, IOException {
		logger.info("KeystoreManager.getCertificate() CertAlias:" + certAlias);
		Certificate cert = ks.getCertificate(certAlias);
		StringWriter sw = new StringWriter();
		PemWriter pw = new PemWriter(sw);
		pw.writeObject(new JcaMiscPEMGenerator(cert));
		pw.flush();
		pw.close();
		String pemEncodedCert = sw.toString();
		logger.trace("KeystoreManager.getCertificate() \n" + pemEncodedCert);
		return pemEncodedCert;
	}
	
	public String formatRequest(PKCS10CertificationRequest inputCSR) throws IOException {
		StringWriter sw = new StringWriter();
		PemWriter pw = new PemWriter(sw);
		pw.writeObject(new JcaMiscPEMGenerator(inputCSR));
		pw.flush();
		pw.close();
		String pemEncodedCert = sw.toString();
		logger.trace("KeystoreManager.formatRequest() \n" + pemEncodedCert);
		return pemEncodedCert;
	}

	private String getCellDN(String subjectName) {
		logger.info("KeystoreManager.getCellDN() Cell DN: CN=" + subjectName + TissueManager.OUDN);
		return "CN=" + subjectName + TissueManager.OUDN;
	}

	private String getCellCADN(String subjectName) {
		logger.info("KeystoreManager.getCellCADN() Cell CA DN: CN=" + subjectName + "-CA" + TissueManager.OUDN);
		return "CN=" + subjectName + "-CA" + TissueManager.OUDN;
	}
	
	private String getGeneralName(int index)
	{
        String generalName="unknown";
        switch (index) {
            case 0:  generalName = "otherName";
                     break;
            case 1:  generalName = "rfc822Name";
                    break;
            case 2:  generalName = "dNSName";
                     break;
            case 3:  generalName = "x400Address";
                     break;
            case 4:  generalName = "directoryName";
                     break;
            case 5:  generalName = "ediPartyName";
                     break;
            case 6:  generalName = "uniformResourceIdentifier";
                     break;
            case 7:  generalName = "iPAddress";
                     break;
            case 8:  generalName = "registeredID";
                     break;
        }
        return generalName;
	}
}
