class Number extends Tile
{
  private int number;

  public Number(int x, int y, int number)
  {
    super(x, y);

    this.number = number;
  }

  public char c()
  {
    return String.valueOf(number).charAt(0);
  }

  public void setNumber(int number)
  {
    this.number = number;
  }
}