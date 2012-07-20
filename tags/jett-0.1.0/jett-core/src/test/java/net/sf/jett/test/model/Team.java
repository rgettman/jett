package net.sf.jett.test.model;

/**
 * A <code>Team</code> represents a Team city, name, wins and losses.
 */
public class Team
{
   private String myCity;
   private String myName;
   private int myWins;
   private int myLosses;

   /**
    * Construct a <code>Team</code>, initializing things to empty/0.
    */
   public Team()
   {
      myCity = "";
      myName = "";
      myWins = 0;
      myLosses = 0;
   }

   /**
    * Returns the city name.
    * @return The city name.
    */
   public String getCity()
   {
      return myCity;
   }

   /**
    * Sets the city name.
    * @param city The city name.
    */
   public void setCity(String city)
   {
      myCity = city;
   }

   /**
    * Returns the team name.
    * @return The team name.
    */
   public String getName()
   {
      return myName;
   }

   /**
    * Sets the team name.
    * @param name The team name.
    */
   public void setName(String name)
   {
      myName = name;
   }

   /**
    * Returns the number of wins.
    * @return The number of wins.
    */
   public int getWins()
   {
      return myWins;
   }

   /**
    * Sets the number of wins.
    * @param wins The number of wins.
    */
   public void setWins(int wins)
   {
      myWins = wins;
   }

   /**
    * Returns the number of losses.
    * @return The number of losses.
    */
   public int getLosses()
   {
      return myLosses;
   }

   /**
    * Sets the number of losses.
    * @param losses The number of losses.
    */
   public void setLosses(int losses)
   {
      myLosses = losses;
   }

   /**
    * Returns the winning percentage.
    * @return The winning percentage, or 0 if wins + losses &lt;= 0.
    */
   public double getPct()
   {
      if (myWins + myLosses <= 0)
         return 0;
      return (double) myWins / ((double) myWins + myLosses);
   }
}
