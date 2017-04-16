package org.biosphere.tissue.utils;

import java.io.IOException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		CMSTypedData msg = new CMSProcessableByteArray(signature.sign());
		
		X509Certificate cert = (X509Certificate) ks.getCertificate(cell.getCellName());
		List certList = new ArrayList();
		certList.add(cert);
		Store certs = new JcaCertStore(certList);
		CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
		ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privKey);
		gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(sha1Signer, cert));
		gen.addCertificates(certs);
		
		CMSSignedData sigData = gen.generate(msg, true);
		String envelopedData = Base64.toBase64String(sigData.getEncoded());

		return envelopedData;
	}

	public static boolean verify(String cellName, Cell cell, String cellSignature) throws CMSException, CertificateException, OperatorCreationException {
		Logger logger = LoggerFactory.getLogger(CellSigner.class);
		boolean verified = false;
		boolean rightSigner = false;
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
			
			
			//Compare the local certificate for the cell and the certificate that comes with the signature, they should match.
			/*
			try {
				KeyStore ks = cell.getCellKeystore();
				X509Certificate localCert = (X509Certificate)ks.getCertificate(cell.getCellName());
				localCert.getPublicKey().
			} catch (KeyStoreException e) {
				TissueExceptionHandler.handleGenericException(e,"CellSigner.verify()","KeyStoreException");
			}
			*/

			
			
			logger.debug("CellSigner.verify()", "Signed for: " + cellName + " by: " + cert.getSubjectX500Principal());
			if (cert.getSubjectAlternativeNames() != null) {
				Iterator itasn = cert.getSubjectAlternativeNames().iterator();
				while (itasn.hasNext()) {
					List list = (List) itasn.next();
					logger.debug("CellSigner.verify()", "  Certificate ASN: " +getGeneralName((int)list.get(0))+":"+list.get(1).toString());
					if(list.get(1).toString().equals(cellName))
					{
						logger.debug("CellSigner.verify()", "  Expected signer: " +getGeneralName((int)list.get(0))+":"+list.get(1).toString());
						rightSigner=true;
					}
				}
			}

		}
		return verified&&rightSigner;
	}
	
/*
    public static String getCertificateFingerprint(X509Certificate cert) 
    {
    	byte[] der = cert.getEncoded();
    	byte[] sha1 = digestOf("SHA_256", der);
    	byte[] hexBytes = Hex.encode(sha1);
    	String hex = new String(hexBytes, "ASCII").toUpperCase();

    	StringBuffer fp = new StringBuffer();
    	int i = 0;
    	fp.append(hex.substring(i, i + 2));
    	while ((i += 2) < hex.length()) {
    		fp.append(':');
    		fp.append(hex.substring(i, i + 2));
    	}

    }
*/
	private static String getGeneralName(int index)
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

