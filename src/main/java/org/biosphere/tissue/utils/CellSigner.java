package org.biosphere.tissue.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.biosphere.tissue.Cell;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;

import sun.misc.BASE64Encoder;

public class CellSigner {

	public static String sign(Cell cell) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException,
			NoSuchProviderException, InvalidKeyException, SignatureException, CertificateEncodingException,
			CMSException, IOException, OperatorCreationException {
		Security.addProvider(new BouncyCastleProvider());
		KeyStore ks = cell.getCellKeystore();
		Key key = ks.getKey(cell.getCellName(), cell.getCellKeystorePWD().toCharArray());
		PrivateKey privKey = (PrivateKey) key;
		Signature signature = Signature.getInstance("SHA1WithRSA", "BC");
		signature.initSign(privKey);
		signature.update(cell.getCellName().getBytes());
		X509Certificate cert = (X509Certificate) ks.getCertificate(cell.getCellName());
		List certList = new ArrayList();
		CMSTypedData msg = new CMSProcessableByteArray(signature.sign());
		certList.add(cert);
		Store certs = new JcaCertStore(certList);
		CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
		ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privKey);
		gen.addSignerInfoGenerator(
				new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
						.build(sha1Signer, cert));
		gen.addCertificates(certs);
		CMSSignedData sigData = gen.generate(msg, false);
		String envelopedData = Base64.toBase64String(sigData.getEncoded());
		return envelopedData;
	}

	public static boolean verify(String cellName, Cell cell, String cellSignature) throws CMSException, CertificateException, OperatorCreationException {
		boolean verified = false;
		Security.addProvider(new BouncyCastleProvider());
		CMSSignedData cms = new CMSSignedData(Base64.decode(cellSignature.getBytes()));
		Store store = cms.getCertificates();
		SignerInformationStore signers = cms.getSignerInfos();
		Collection c = signers.getSigners();
		Iterator it = c.iterator();
		while (it.hasNext()) {
			SignerInformation signer = (SignerInformation) it.next();
			Collection certCollection = store.getMatches(signer.getSID());
			Iterator certIt = certCollection.iterator();
			X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();
			X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);
			if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert))) {
				verified=true;
			}
		}
		return verified;
	}

}
