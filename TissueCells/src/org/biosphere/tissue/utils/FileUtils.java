package org.biosphere.tissue.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.biosphere.tissue.exceptions.TissueExceptionHandler;

public class FileUtils
{
  public FileUtils()
  {
    logger = new Logger();
  }
  
  private Logger logger;
  
  public InputStream getFile(String fileURL)
  {
    InputStream is = null;

    if (fileURL.startsWith("https"))
    {
      is = getFileHTTP(fileURL);
    }
    else
    {
      is = getFileFileSystem(fileURL);
    }
    return is;
  }

  private InputStream getFileFileSystem(String fileURL)
  {
    try
    {
      return new FileInputStream(fileURL);
    }
    catch (FileNotFoundException e)
    {
      TissueExceptionHandler.handleGenericException(e,"FileUtils.getFileFileSystem()","IOException:");
      return null;
    }
  }

  private InputStream getFileHTTP(String fileURL)
  {
    try
    {
      URL urlDocument = new URL(fileURL);
      HttpsURLConnection httpConnectionDocument = (HttpsURLConnection) urlDocument.openConnection();
      httpConnectionDocument.connect();
      return httpConnectionDocument.getInputStream();
    }
    catch (IOException e)
    {
      TissueExceptionHandler.handleGenericException(e,"FileUtils.getFileHTTP()","IOException:");
      return null;
    }
  }
}
