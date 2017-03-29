package org.biosphere.tissue.services;

import java.util.Hashtable;

public class ServiceDefinition
{
  private String serviceDefinitionName;
  private String serviceDefinitionClass;
  private String serviceDefinitionType;
  private boolean serviceDefinitionDaemon = true;
  private String serviceDefinitionVersion;
  private Hashtable<String,Object> serviceDefinitionParameters;
  
  public ServiceDefinition()
  {
    super();
    serviceDefinitionParameters = new Hashtable<String,Object>();
  }

  public final void addServiceDefinitionParameter(String key,Object value)
  {
    serviceDefinitionParameters.put(key,value);
  }
  
  public final void removeServiceDefinitionParameter(String key)
  {
    serviceDefinitionParameters.remove(key);
  }

  public final Hashtable<String,Object> getServiceDefinitionParameters()
  {
    return serviceDefinitionParameters;
  }
  
  public void setServiceDefinitionParameters(Hashtable<String, Object> serviceDefinitionParameters)
  {
    this.serviceDefinitionParameters = serviceDefinitionParameters;
  }  
  public final void setServiceDefinitionName(String serviceDefinitionName)
  {
    this.serviceDefinitionName = serviceDefinitionName;
  }

  public final String getServiceDefinitionName()
  {
    return serviceDefinitionName;
  }
  
  public final void setServiceDefinitionClass(String serviceDefinitionClass)
  {
    this.serviceDefinitionClass = serviceDefinitionClass;
  }

  public final String getServiceDefinitionClass()
  {
    return serviceDefinitionClass;
  }

  public final void setServiceDefinitionType(String serviceDefinitionType)
  {
    this.serviceDefinitionType = serviceDefinitionType;
  }

  public final String getServiceDefinitionType()
  {
    return serviceDefinitionType;
  }

  public final void setServiceDefinitionVersion(String serviceDefinitionVersion)
  {
    this.serviceDefinitionVersion = serviceDefinitionVersion;
  }

  public final String getServiceDefinitionVersion()
  {
    return serviceDefinitionVersion;
  }

  public final void setServiceDefinitionDaemon(boolean serviceDefinitionDaemon)
  {
    this.serviceDefinitionDaemon = serviceDefinitionDaemon;
  }

  public final boolean isServiceDefinitionDaemon()
  {
    return serviceDefinitionDaemon;
  }
}
