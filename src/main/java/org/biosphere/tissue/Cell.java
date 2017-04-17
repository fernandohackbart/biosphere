package org.biosphere.tissue;

import java.io.IOException;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import org.biosphere.tissue.DNA.DNA;
import org.biosphere.tissue.blockchain.Chain;
import org.biosphere.tissue.cell.CellManager;
import org.biosphere.tissue.exceptions.CellException;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.tissue.TissueManager;

import org.bouncycastle.operator.OperatorCreationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cell {

	private boolean tissueMember;
	private DNA dna;
	private Logger logger;
	private int tissuePort;
	private String cellName;
	private String cellNetworkName;
	private String cellKeystorePWD;
	private KeyStore cellKeystore;
	private String cellCertificate;
	private Chain chain;

	public Cell() {
		logger = LoggerFactory.getLogger(Cell.class);
	}

	public static void main(String[] args) {
		Cell cell = new Cell();
		cell.start();
	}

	public synchronized void setTissueMember(boolean tissueMember) {
		this.tissueMember = tissueMember;
	}

	public boolean isTissueMember() {
		return tissueMember;
	}
	
	public final DNA getDna() {
		return dna;
	}

	public final void setDna(DNA dna) {
		this.dna = dna;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}

	public String getCellName() {
		return cellName;
	}

	public void setTissuePort(int tissuePort) {
		this.tissuePort = tissuePort;
	}

	public int getTissuePort() {
		return tissuePort;
	}

	public void start() {
		CellManager.setupCell(this);
		try {
			this.setCellKeystore(CellManager.generateCellKeystore(this));
			CellManager.setDefaultSSLSocketFactory(this);
			this.setCellCertificate(CellManager.getCellCertificateFromKeystore(this));
			CellManager.startTissueListenerService(this);
			TissueManager.joinTissue(this);
		} catch (CertificateException | IOException | InvalidKeySpecException | KeyStoreException
				| NoSuchAlgorithmException | OperatorCreationException | CellException e) {
			TissueExceptionHandler.handleGenericException(e, "Cell.start()", "Failed to start, exiting.");
			CellManager.stopCell();
		}
		CellManager.loadServicesDNA(this);
		CellManager.startServicesDNA(this);
		logger.info("Cell.start() ####################################################################################");
		logger.info("Cell.start() Cell " + getCellName() + " is running!  Tissue listener at:" + getCellNetworkName()
		+ ":" + getTissuePort());
		logger.info("Cell.start() Log level: trace="+logger.isTraceEnabled()+" debug="+logger.isDebugEnabled()+" info="+logger.isInfoEnabled()+" warn="+logger.isWarnEnabled()+" error="+logger.isErrorEnabled());
		logger.info("Cell.start() Log level: "+TissueManager.logLevelParameter+"="+System.getProperty(TissueManager.logLevelParameter));
		logger.info("Cell.start() Log level: "+TissueManager.logOutputParameter+"="+System.getProperty(TissueManager.logOutputParameter));
		logger.info("Cell.start() Log level: "+TissueManager.logShowDateTimeParameter+"="+System.getProperty(TissueManager.logShowDateTimeParameter));
		logger.info("Cell.start() ####################################################################################");
	}

	public final void setCellKeystorePWD(String cellKeystorePWD) {
		this.cellKeystorePWD = cellKeystorePWD;
	}

	public final String getCellKeystorePWD() {
		return cellKeystorePWD;
	}

	public final void setCellKeystore(KeyStore cellKeystore) {
		this.cellKeystore = cellKeystore;
	}

	public final KeyStore getCellKeystore() {
		return cellKeystore;
	}

	public final void setCellNetworkName(String cellNetworkName) {
		this.cellNetworkName = cellNetworkName;
	}

	public final String getCellNetworkName() {
		return cellNetworkName;
	}

	public final void setCellCertificate(String cellCertificate) {
		this.cellCertificate = cellCertificate;
	}

	public final String getCellCertificate() {
		return cellCertificate;
	}

	public final void setChain(Chain chain) {
		this.chain = chain;
	}

	public final Chain getChain() {
		return chain;
	}
}
