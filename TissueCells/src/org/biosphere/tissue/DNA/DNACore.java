package org.biosphere.tissue.DNA;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.biosphere.tissue.services.ServiceDefinition;
import org.biosphere.tissue.tissue.TissueManager;
import org.biosphere.tissue.exceptions.TissueExceptionHandler;
import org.biosphere.tissue.utils.FileUtils;
import org.biosphere.tissue.utils.Logger;

import org.w3c.dom.Document;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DNACore
{

  public DNACore()
  {
    logger = new Logger();
  }

  private Logger logger;
  private ArrayList<DNAEntry> dnaEntries;
  private String DNACoreEntryName = "DNACore";
  private String DNACoreEntryType = "DNADocument";

  private String serializeObject(Object object)
  {
    String serializedObject = "";
    try 
    {
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      ObjectOutputStream so = new ObjectOutputStream(bo);
      so.writeObject(object);
      so.flush();
      so.close();
      serializedObject = Base64.getEncoder().encodeToString(bo.toByteArray());
    } 
    catch (IOException e) 
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.serializeObject()","IOException:"+e.getMessage());
      serializedObject="ERROR";
    }
    return serializedObject;
  }
  
  private Object deserializeObject(String serializedObject)
  {
    Object object = "";
    try 
    {   
      byte b[] = Base64.getDecoder().decode(serializedObject); 
      ObjectInputStream si = new ObjectInputStream(new ByteArrayInputStream(b));
      object = si.readObject();
    } 
    catch (ClassNotFoundException e) 
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.deserializeObject()","ClassNotFoundException:"+e.getMessage());
      object = null;
    }
    catch (IOException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.deserializeObject()","IOException:"+e.getMessage());
      object = null;
    }
    return object;
  }
 
  private void addDNACoreEntry(Document doc)
  {
    logger.debug("DNACore.addDNACoreEntry()","Add the DNA Core document to the DNA cache: " + DNACoreEntryName + " (" + DNACoreEntryType + ")");
    DNAEntry entry = new DNAEntry();
    entry.setEntryName(DNACoreEntryName);
    entry.setEntryType(DNACoreEntryType);
    if (doc == null)
    {
      logger.debug("DNACore.addDNACoreEntry()", "Doc is null!");
    }
    entry.setEntryValue(doc);
    logger.debug("DNACore.addDNACoreEntry()", "Creating new ArrayList<DNAEntry>()!");
    dnaEntries = new ArrayList<DNAEntry>();
    dnaEntries.add(entry);
  }

  private DNAEntry getDNAEntry(String entryName, String EntryType)
  {
    if (this.dnaEntries.isEmpty())
    {
      logger.debug("DNACore.getDNAEntry()", "dnaEntries.isEmpty()");
      return null;
    }
    for (int i = 0; i < this.dnaEntries.size(); i++)
    {
      if ((dnaEntries.get(i).getEntryName() == entryName) && (dnaEntries.get(i).getEntryType() == EntryType))
      {
        return dnaEntries.get(i);
      }
    }
    logger.debug("DNACore.getDNAEntry()", "Entry " + entryName + " (" + EntryType + ") not found");
    return null;
  }

  private void updateDNACoreEntry(Document doc)
  {
    if (this.dnaEntries.isEmpty())
    {
      logger.debug("DNACore.updateDNACoreEntry()", "dnaEntries.isEmpty()");
    }
    else
    {
      for (int i = 0; i < this.dnaEntries.size(); i++)
      {
        if ((dnaEntries.get(i).getEntryName() == DNACoreEntryName) &&
            (dnaEntries.get(i).getEntryType() == DNACoreEntryType))
        {
          DNAEntry entry = dnaEntries.get(i);
          entry.setEntryValue(doc);
          dnaEntries.set(i, entry);
          logger.debug("DNACore.updateDNACoreEntry()",
                       "Updated DNACoreEntry: " + DNACoreEntryName + " (" + DNACoreEntryType + ")");
          return;
        }
      }
    }
    logger.debug("DNACore.updateDNACoreEntry()", "Entry " + DNACoreEntryName + " (" + DNACoreEntryType + ") not found");
  }

  private Document getDNACoreAsDocument()
  {
    return (Document) getDNAEntry(DNACoreEntryName, DNACoreEntryType).getEntryValue();
  }

  public String getDNACoreAsString()
  {
    StringWriter writer = new StringWriter();
    Document doc = getDNACoreAsDocument();
    try
    {
      TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(writer));
    }
    catch (TransformerConfigurationException e)
    {
      logger.exception("DNACore.getDNACoreAsString()", "TransformerConfigurationException: " + e.getMessage());
    }
    catch (TransformerException e)
    {
      logger.exception("DNACore.incept()", "TransformerException: " + e.getMessage());
    }
    return writer.toString();
  }
  
  public String getDNACoreAsPrettyString()
  {
    String output = "FAIL";
    try
    {
      DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(getDNACoreAsString()));
      Document doc = db.parse(is);
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      StreamResult result = new StreamResult(new StringWriter());
      DOMSource source = new DOMSource(doc);
      transformer.transform(source,result);      
      output=result.getWriter().toString();
    }
    catch (ParserConfigurationException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.getDNACoreAsPrettyString()","ParserConfigurationException:");
    }
    catch (IOException | SAXException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.getDNACoreAsPrettyString()","IOException | SAXException:");
    }
    catch (TransformerConfigurationException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.getDNACoreAsPrettyString()","TransformerConfigurationException:");
    }
    catch (TransformerException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.getDNACoreAsPrettyString()","TransformerException:");
    }

    return output;
  }

  private Tissue unmarshallDNACore()
  {
    Document doc = getDNACoreAsDocument();
    Unmarshaller um;
    JAXBContext jaxbContext;
    try
    {
      jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
      um = jaxbContext.createUnmarshaller();
      Tissue tissue = (Tissue) um.unmarshal(doc.getDocumentElement());
      return tissue;
    }
    catch (JAXBException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.unmarshallDNACore()","JAXBException:");
      return null;
    }
    catch (Exception e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.unmarshallDNACore()","Exception:");
      return null;
    }
  }

  private Document marshallDNACore(Tissue tissue)
  {
    try
    {
      JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document document = db.newDocument();
      Marshaller marshaller = jc.createMarshaller();
      marshaller.marshal(tissue, document);
      return document;
    }
    catch (JAXBException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.marshallDNACore()","JAXBException:");
      return null;
    }
    catch (Exception e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.marshallDNACore()","Exception:");
      return null;
    }
  }
  
  public void incept()
  {
    try
    {
      logger.debug("DNACore.incept()", "Creating DNA!");
      Tissue tissue = new Tissue();
      tissue.setDNAversion(1);
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document document = db.newDocument();
      JAXBContext jaxbContext;
      jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.marshal(tissue, document);
      addDNACoreEntry(document);
      setTissueName(TissueManager.generateTissueName());
      //setTissuepublickey(tissuepublickey);
    }
    catch (ParserConfigurationException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.incept()","ParserConfigurationException:");
    }
    catch (JAXBException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.incept()","JAXBException:");
    }
    catch (Exception e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.incept()","Exception:");
    }
  }

  public String getTissueName()
  {
    String response = "ERROR: TissueName not found!";
    Tissue tissue = unmarshallDNACore();
    response = tissue.getTissuename();
    logger.debug("DNACore.getTissueName()", "Tissue.getTissueName()=\"" + response + "\"");
    return response;
  }

  private void setTissueName(String tissueName)
  {
    Tissue tissue = unmarshallDNACore();
    tissue.setTissuename(tissueName);
    updateDNACoreEntry(marshallDNACore(tissue));
    logger.debug("DNACore.setTissueName()", "Tissue.setTissuename(\"" + tissueName + "\")");
  }
  
  public List<CellInterface> getTissueCellsInterfaces()
  {
     List<CellInterface> interfacesList = new ArrayList<CellInterface>();
    logger.debug("DNACore.getCellInterfaces()","Getting the list of the cells from the DNA");
    // get the list of the cells
    Tissue.Tissuecells cells = getCells();
    Iterator cellsIterator = cells.getCell().iterator();
    while (cellsIterator.hasNext())
    {
      Celltype DNAcell=(Celltype)cellsIterator.next();
      for (int i = 0; i < DNAcell.getCellinterface().getCelladdresses().getCelladdress().size(); i++) 
      {
        logger.debug("DNACore.getCellInterfaces()","Adding cell: "+DNAcell.getCellname()+" ("+DNAcell.getCellinterface().getCelladdresses().getCelladdress().get(i)+":"+DNAcell.getCellinterface().getCellport().intValue()+") to the list");
        interfacesList.add(new CellInterface(DNAcell.getCellname(),DNAcell.getCellinterface().getCelladdresses().getCelladdress().get(i),DNAcell.getCellinterface().getCellport().intValue()));
      }
    }
    return interfacesList;
  }
  
  public int getTissueSize()
  {

    logger.debug("DNACore.getTissueSize()","Getting the size of the tissue from the DNA: "+getCells().getCell().size());
    return getCells().getCell().size();
  }
  
  private boolean contains(String cellName)
  {
    boolean cellPresent=false;
    logger.debug("DNACore.contains()","Getting the list of the cells from the DNA");
    // get the list of the cells
    Tissue.Tissuecells cells = getCells();
    Iterator cellsIterator = cells.getCell().iterator();
    while (cellsIterator.hasNext())
    {
      Celltype DNAcell= (Celltype)cellsIterator.next();      
      if((DNAcell.getCellname().equals(cellName)))
      {
        logger.debug("DNACore.contains()","Cell: "+cellName+" present in the local DNA!");
        cellPresent=true;       
      }
    }  
    return cellPresent;
  }

  private void ensureTissueCells()
  {
    Tissue tissue = unmarshallDNACore();
    Tissue.Tissuecells cells = tissue.getTissuecells();
    if (cells == null)
    {
      logger.debug("DNACore.ensureTissueCells()","Adding new Tissue.Tissuecells to the local DNA!");
      cells = new Tissue.Tissuecells();
      tissue.setTissuecells(cells);
      updateDNACoreEntry(marshallDNACore(tissue));
    }
  }
  
  public void addCell(String cellName, String cellpublickey,String cellNetworkName, int cellTissuePort)
  {
    ensureTissueCells();
    Tissue tissue = unmarshallDNACore();
    Tissue.Tissuecells cells = tissue.getTissuecells();
    List<Celltype> cellsList = cells.getCell();
    if (!contains(cellName))
    {
      logger.debug("DNACore.addCell()"," Adding Cell: (\"" + cellName + "\",\""+cellNetworkName+"\","+cellTissuePort+")");
      Celltype addingCell = new Celltype();
      addingCell.setCellname(cellName);
      addingCell.setCellpublickey(cellpublickey);
      Cellinterfacetype cit = new Cellinterfacetype();
      Cellinterfacetype.Celladdresses ca = new Cellinterfacetype.Celladdresses();
      List<String> addrs = new ArrayList<String>();
      addrs.add(cellNetworkName);
      ca.celladdress = addrs;
      cit.setCelladdresses(ca);
      cit.setCellport(BigInteger.valueOf(cellTissuePort));
      addingCell.setCellinterface(cit);
      cellsList.add(addingCell);
      tissue.setTissuecells(cells);
      updateDNACoreEntry(marshallDNACore(tissue));      
    }
    else
    {
      logger.warn("DNACore.addCell()"," Adding Cell: (\"" + cellName + "\",\""+cellNetworkName+"\","+cellTissuePort+") already present in the DNA!");
    }
  }

  public Tissue.Tissuecells getCells()
  {
    Tissue tissue = unmarshallDNACore();
    Tissue.Tissuecells cells = tissue.getTissuecells();
    logger.debug("DNACore.getCells()", "Tissue.getCells()");
    return cells;
  }
  
  public void addService(ServiceDefinition serviceDefinition)
  {
    if(!serviceAdded(serviceDefinition.getServiceDefinitionName()))
    {
      Tissue tissue = unmarshallDNACore();
      Tissue.Tissueservices services = tissue.getTissueservices();
      if (services == null)
      {
        services = new Tissue.Tissueservices();
      }
      List<Tissueservicetype> servicesList = services.getTissueservice();
      Tissueservicetype addingService = new Tissueservicetype();
      addingService.setServicename(serviceDefinition.getServiceDefinitionName());
      addingService.setServiceclass(serviceDefinition.getServiceDefinitionClass());
      addingService.setServiceversion(serviceDefinition.getServiceDefinitionVersion());
      addingService.setServicetype(serviceDefinition.getServiceDefinitionType());
      addingService.setServicedaemon(serviceDefinition.isServiceDefinitionDaemon());
      Tissueservicetype.Serviceparameters serviceParameters = new Tissueservicetype.Serviceparameters();
      List<Tissueservicetype.Serviceparameters.Serviceparameter> serviceParametersList = serviceParameters.getServiceparameter();
      Enumeration parameterKeys = serviceDefinition.getServiceDefinitionParameters().keys();
      while(parameterKeys.hasMoreElements())
      {
        String parameterName = (String)parameterKeys.nextElement();
        Tissueservicetype.Serviceparameters.Serviceparameter svp = new Tissueservicetype.Serviceparameters.Serviceparameter();
        svp.setServiceparametername(parameterName); 
        svp.setServiceparametervalue(serializeObject(serviceDefinition.getServiceDefinitionParameters().get(parameterName)));  
        serviceParametersList.add(svp);
      }
      addingService.setServiceparameters(serviceParameters);
      servicesList.add(addingService);
      tissue.setTissueservices(services);
      updateDNACoreEntry(marshallDNACore(tissue));
      logger.debug("DNACore.addService()", "DNACore.addService(\"" + serviceDefinition.getServiceDefinitionName() + "\")");
    }
    else
    {
      logger.debug("DNACore.addService()", "DNACore.addService(\"" + serviceDefinition.getServiceDefinitionName() + "\") already added, skipping operation!");      
    }
  }

  public Tissue.Tissueservices getServices()
  {
    Tissue tissue = unmarshallDNACore();
    Tissue.Tissueservices services = tissue.getTissueservices();
    if (services != null)
    {
      logger.debug("DNACore.getServices()", "DNACore.getServices() returned "+services.tissueservice.size()+" services");
    }
    else
    {
      logger.debug("DNACore.getServices()", "DNACore.getServices() no services added!");
    }
    return services;
  }

  private boolean serviceAdded(String serviceName)
  {
    boolean servicePresent=false;
    Tissue.Tissueservices services = getServices();
    if (services != null)
    {
      Iterator servicesIterator = services.getTissueservice().iterator();
      while(servicesIterator.hasNext())
      {
        Tissueservicetype service = (Tissueservicetype)servicesIterator.next();
        if(service.getServicename().equals(serviceName))
        {
          logger.debug("DNACore.serviceAdded()","Service "+service.getServicename()+" present in DNA");
          servicePresent=true;
          break;
        }
      }      
    }
    return servicePresent;
  }
  
  public ServiceDefinition getServiceDefinition(String serviceName)
  {
    ServiceDefinition sd = null;
    Tissue tissue = unmarshallDNACore();
    Tissue.Tissueservices services = tissue.getTissueservices();
    //services.getTissueservice().
    Iterator servicesIterator = services.getTissueservice().iterator();
    while(servicesIterator.hasNext())
    {
      Tissueservicetype service = (Tissueservicetype)servicesIterator.next();
      if (service.getServicename().equals(serviceName))
      {
        sd = new ServiceDefinition();
        sd.setServiceDefinitionName(service.getServicename());
        sd.setServiceDefinitionType(service.getServicetype()); //Add the element in the XML
        sd.setServiceDefinitionDaemon(service.isServicedaemon()); //Add the element in the XML
        sd.setServiceDefinitionVersion(service.getServiceversion());
        sd.setServiceDefinitionClass(service.getServiceclass());
        
        List<Tissueservicetype.Serviceparameters.Serviceparameter> parametersList= service.getServiceparameters().getServiceparameter();
        Iterator<Tissueservicetype.Serviceparameters.Serviceparameter> parametersIterator = parametersList.iterator();
        
        while(parametersIterator.hasNext())
        {
          Tissueservicetype.Serviceparameters.Serviceparameter svp = parametersIterator.next();
          sd.addServiceDefinitionParameter(svp.getServiceparametername(),deserializeObject(svp.getServiceparametervalue()));
        }
        break;
      }
    }   
    return sd;
  }
  
  public void persist(String fileName)
  {
  }

  public void load(String DNACoreURL,int DNAContentServerPort)
  {
    try
    {
      logger.debug("DNACore.load()", "Loading DNACore from: " +DNACoreURL);
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      builderFactory.setNamespaceAware(true);
      DocumentBuilder parser = builderFactory.newDocumentBuilder();
      FileUtils fileUtils = new FileUtils();
      Document document = parser.parse(fileUtils.getFile(DNACoreURL));
      //String localSchemaURL = "http://localhost:"+DNAContentServerPort+"/org/biosphere/tissue/DNA/TissueDNA-1.0.xsd";
      //logger.debug("DNACore.load()", "Validating DNACore from: " +DNACoreURL+" against: "+localSchemaURL);
      //validateFile(localSchemaURL,document);
      addDNACoreEntry(document);    
    }
    catch (ParserConfigurationException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.load()","ParserConfigurationException:");
    }
    catch (IOException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.load()","IOException:");
    }
    catch (SAXException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.load()","SAXException:");
    }
  }

  private void validateFile(String schemaFileName, Document document)
  {
    try
    {
      logger.debug("DNACore.validateFile()", "Encoding of the XML file: " + document.getXmlEncoding());
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

      // To get the schema from inside one jar in the CLASSPATH using a resolver
      // factory.setResourceResolver(new XMLSchemaResolver());
      // Source schemaFile = new StreamSource(getClass().getClassLoader().getResourceAsStream(schemaFileName));

      Source schemaFile = new StreamSource(new FileUtils().getFile(schemaFileName));
      Schema schema = factory.newSchema(schemaFile);
      Validator validator = schema.newValidator();
      validator.validate(new DOMSource(document));
    }
    catch (IOException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.validateFile()","IOException:");
    }
    catch (SAXException e)
    {
      TissueExceptionHandler.handleGenericException(e,"DNACore.validateFile()","SAXException:");
    }
  }
}
