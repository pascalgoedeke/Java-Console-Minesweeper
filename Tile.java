abstract class Tile
{
  private int x;
  private int y;
  private boolean visible;

  public Tile(int x, int y)
  {
    this.x = x;
    this.y = y;
    this.visible = false;
  }

  public int x()
  {
    return this.x;
  }

  public int y()
  {
    return this.y;
  }

  public boolean isVisible()
  {
    return this.visible;
  }

  public void setVisible(boolean visible)
  {
    this.visible = visible;
  }

  abstract char c();
}