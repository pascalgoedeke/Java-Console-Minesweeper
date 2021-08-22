import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

class Main
{
  private static int width = 15;
  private static int height = 9;
  private static int bombsAmount = 15;

  private static ArrayList<Tile> tiles = new ArrayList<>();
  private static ArrayList<Marker> markers = new ArrayList<>();
  private static ArrayList<Bomb> bombs = new ArrayList<>();

  private static boolean running = true;
  private static boolean won = false;
  private static int bombsLeft = bombsAmount;

  public static void main(String[] args)
  {
    while(bombs.size() < bombsAmount)
    {
      int x = randomInt(1, width);
      int y = randomInt(1, height);

      if(getBomb(x, y) == null)
        bombs.add(new Bomb(x, y));
    }
    
    for(int x = 1; x < width + 1; x++)
    {
      for(int y = 1; y < height + 1; y++)
      {
        if(getBomb(x, y) == null)
        {
          int amount = getNearBombAmount(x, y);
          if(amount > 0)
            tiles.add(new Number(x, y, amount));
          else
            tiles.add(new Safe(x, y));
        }
      }
    }

    Scanner scanner = new Scanner(System.in);

    while(running)
    {
      draw();

      System.out.println("Verbleibende Marker: " + bombsLeft);
      System.out.println("A - Aufdecken, M - Markieren");
      String input = scanner.nextLine().toUpperCase();

      if(input.equals(""))
        continue;

      char action = input.charAt(0);
      String[] sorted = input.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
      int inputX = (int)sorted[0].charAt(1) - 64;
      int inputY = Integer.valueOf(sorted[1]);

      if(action == 'A')
      {
        Bomb bomb = getBomb(inputX, inputY);
        if(bomb != null)
          running = false;

        recursiveShow(inputX, inputY);
      }
      else if(action == 'M')
      {
        Marker marker = getMarker(inputX, inputY);
        if(marker != null)
        {
          markers.remove(marker);
          bombsLeft++;
        }
        else
        {
          if(bombsLeft != 0)
          {
            markers.add(new Marker(inputX, inputY));
            bombsLeft--;
          }
        }
      }

      won = true;
      for(Bomb bomb : bombs)
        if(getMarker(bomb.x(), bomb.y()) == null)
          won = false;
      if(won)
      {
        running = false;

        for(Tile tile : tiles)
          if(!tile.isVisible())
            tile.setVisible(true);
      }
    }

    for(Bomb bomb : bombs)
    {
      tiles.add(bomb);
      bomb.setVisible(true);
    }
    
    draw();

    if(won)
      System.out.println("Gewonnen");
    else
      System.out.println("Game over");

    scanner.close();
  }

  public static void draw()
  {
    char borderUpperLeft = '╔';
    char borderUpperRight = '╗';
    char borderLowerLeft = '╚';
    char borderLowerRight = '╝';
    char borderHorizontal = '═';
    char borderVertical = '║'; 
    char borderHorizontalConnectDown = '╤';
    char borderHorizontalConnectUp = '╧';
    char borderVerticalConnectRight = '╟';
    char borderVerticalConnectLeft = '╢';
    char innerHorizontal = '─';
    char innerVertical = '│';
    char innerConnect = '┼';

    System.out.print("\033[H\033[2J");
    System.out.flush();

    StringBuilder letters = new StringBuilder();
    letters.append("   ");
    for(int i = 0; i < width; i++)
      letters.append(((char)(i + 65)) + " ");
    
    System.out.println(letters);
    
    StringBuilder top = new StringBuilder();
    top.append("  ").append(borderUpperLeft);
    for(int i = 0; i < width * 2 - 1; i++)
    {
      if(i % 2 == 0)
        top.append(borderHorizontal);
      else if(i % 2 == 1)
        top.append(borderHorizontalConnectDown);
    }
    top.append(borderUpperRight);

    System.out.println(top);

    StringBuilder spacer = new StringBuilder();
    spacer.append("  ").append(borderVerticalConnectRight);
    for(int i = 0; i < width * 2 - 1; i++)
    {
      if(i % 2 == 0)
        spacer.append(innerHorizontal);
      else if(i % 2 == 1)
        spacer.append(innerConnect);
    }
    spacer.append(borderVerticalConnectLeft);

    for(int i = 0; i < height * 2 - 1; i++)
    {
      if(i % 2 == 0)
      {
        StringBuilder line = new StringBuilder();
        line.append(i / 2 + 1).append(" ").append(borderVertical);
        for(int j = 0; j < width * 2 - 1; j++)
        {
          if(j % 2 == 0)
          {
            Marker marker = null;
            for(Marker m : markers)
              if(m.x() == j / 2 + 1 && m.y() == i / 2 + 1)
                marker = m;
            
            if(marker != null)
              line.append(marker.c());
            else
            {
              Tile tile = null;
              for(Tile t : tiles)
                if(t.x() == j / 2 + 1 && t.y() == i / 2 + 1)
                  tile = t;

              if(tile != null)
              {
                if(tile.isVisible())
                  line.append(tile.c());
                else
                  line.append(" ");
              }
              else
                line.append(" ");
            }
          }
          else if(j % 2 == 1)
            line.append(innerVertical);
        }
        line.append(borderVertical).append(" ").append(i / 2 + 1);

        System.out.println(line);
      }
      else if(i % 2 == 1)
        System.out.println(spacer);
    }

    StringBuilder bottom = new StringBuilder();
    bottom.append("  ").append(borderLowerLeft);
    for(int i = 0; i < width * 2 - 1; i++)
    {
      if(i % 2 == 0)
        bottom.append(borderHorizontal);
      else if(i % 2 == 1)
        bottom.append(borderHorizontalConnectUp);
    }
    bottom.append(borderLowerRight);

    System.out.println(bottom);

    System.out.println(letters);
  }

  public static Tile getTile(int x, int y)
  {
    for(Tile tile : tiles)
      if(tile.x() == x && tile.y() == y)
        return tile;
    return null;
  }

  public static Marker getMarker(int x, int y)
  {
    for(Marker marker : markers)
      if(marker.x() == x && marker.y() == y)
        return marker;
    return null;
  }

  public static Bomb getBomb(int x, int y)
  {
    for(Bomb bomb : bombs)
      if(bomb.x() == x && bomb.y() == y)
        return bomb;
    return null;
  }

  public static int getNearBombAmount(int x, int y)
  {
    int amount = 0;

    // oben links
    if(x > 1 && y > 1)
      if(getBomb(x - 1, y - 1) != null)
        amount++;
    
    // oben
    if(y > 1)
      if(getBomb(x, y - 1) != null)
        amount++;
    
    // oben rechts
    if(x < width && y > 1)
      if(getBomb(x + 1, y - 1) != null)
        amount++;
    
    // links
    if(x > 1)
      if(getBomb(x - 1, y) != null)
        amount++;
    
    // rechts
    if(x < width)
      if(getBomb(x + 1, y) != null)
        amount++;
    
    // unten links
    if(x > 1 && y < height)
      if(getBomb(x - 1, y + 1) != null)
        amount++;
    
    // unten
    if(y < height)
      if(getBomb(x, y + 1) != null)
        amount++;
    
    // unten rechts
    if(x < width && y < height)
      if(getBomb(x + 1, y + 1) != null)
        amount++;

    return amount;
  }

  public static void recursiveShow(int x, int y)
  {
    Tile tile = getTile(x, y);
    if(tile == null)
      return;
    tile.setVisible(true);

    if(tile instanceof Safe)
    {
      // oben links
      if(x > 1 && y > 1)
        if(!getTile(x - 1, y - 1).isVisible())
          recursiveShow(x - 1, y - 1);
      
      // oben
      if(y > 1)
        if(!getTile(x, y - 1).isVisible())
          recursiveShow(x, y - 1);
      
      // oben rechts
      if(x < width && y > 1)
        if(!getTile(x + 1, y - 1).isVisible())
          recursiveShow(x + 1, y - 1);
      
      // links
      if(x > 1)
        if(!getTile(x - 1, y).isVisible())
          recursiveShow(x - 1, y);
      
      // rechts
      if(x < width)
        if(!getTile(x + 1, y).isVisible())
          recursiveShow(x + 1, y);
      
      // unten links
      if(x > 1 && y < height)
        if(!getTile(x - 1, y + 1).isVisible())
          recursiveShow(x - 1, y + 1);
      
      // unten
      if(y < height)
        if(!getTile(x, y + 1).isVisible())
          recursiveShow(x, y + 1);
      
      // unten rechts
      if(x < width && y < height)
        if(!getTile(x + 1, y + 1).isVisible())
          recursiveShow(x + 1, y + 1);
    }
  }

  public static int randomInt(int min, int max)
  {
    Random random = new Random();
    return random.nextInt((max - min) + 1) + min;
  }
}