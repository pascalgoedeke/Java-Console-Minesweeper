class Marker extends Tile
{
  public Marker(int x, int y)
  {
    super(x, y);
    this.setVisible(true);
  }

  public char c()
  {
    return 'X';
  }
}