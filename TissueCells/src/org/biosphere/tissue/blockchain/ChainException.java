package org.biosphere.tissue.blockchain;


public class ChainException extends Exception
{
  @SuppressWarnings("compatibility:1513698579894150770")
  private static final long serialVersionUID = 1L;

  public ChainException()
  {
    super();
  }

  public ChainException(String string, Throwable throwable, boolean b, boolean b1)
  {
    super(string, throwable, b, b1);
  }

  public ChainException(Throwable throwable)
  {
    super(throwable);
  }

  public ChainException(String message, String module, Throwable throwable)
  {
    super("BlockException at: " + module + " = " + message, throwable);
  }

  public ChainException(String string, Throwable throwable)
  {
    super(string, throwable);
  }

  public ChainException(String string)
  {
    super(string);
  }

}
