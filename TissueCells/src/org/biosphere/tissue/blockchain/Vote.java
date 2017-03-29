package org.biosphere.tissue.blockchain;


public class Vote
{
  public Vote()
  {
    super();
  }
  
  public Vote(String cellID,boolean accepted)
  {
    setCellID(cellID);
    setAccepted(accepted);
  }
  
  private String cellID;
  private boolean accepted;

  public final void setCellID(String cellID)
  {
    this.cellID = cellID;
  }

  public final String getCellID()
  {
    return cellID;
  }

  public final void setAccepted(boolean accepted)
  {
    this.accepted = accepted;
  }

  public final boolean isAccepted()
  {
    return accepted;
  }
}
