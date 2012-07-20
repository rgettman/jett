package net.sf.jett.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;

import net.sf.jett.test.model.County;
import net.sf.jett.test.model.Division;
import net.sf.jett.test.model.State;
import net.sf.jett.test.model.Team;

/**
 * This utility class supplies beans maps for possibly multiple tests.
 */
@Ignore
public class TestUtility
{
   /**
    * Gets a beans map with <code>State</code> beans "california" and "nevada".
    * Each bean in turn is composed of many <code>County</code> beans.
    *
    * @return A <code>Map</code> of <code>State</code> beans.
    */
   public static Map<String, Object> getStateData()
   {
      Map<String, Object> beans = new HashMap<String, Object>();
      State california = new State();
      california.setName("California");
      california.addCounty(new County("Los Angeles", 10363850, 10515, 1850, "Los Angeles", "06037"));
      california.addCounty(new County("San Diego", 3146274, 10888, 1850, "San Diego", "06073"));
      california.addCounty(new County("Orange", 3121251, 2046, 1889, "Santa Ana", "06059"));
      california.addCounty(new County("Riverside", 2088322, 18669, 1893, "Riverside", "06065"));
      california.addCounty(new County("San Bernardino", 2055766, 51960, 1853, "San Bernardino", "06071"));
      california.addCounty(new County("Santa Clara", 1837075, 3344, 1850, "Santa Clara", "06085"));
      california.addCounty(new County("Alameda", 1543000, 1911, 1853, "Oakland", "06001"));
      california.addCounty(new County("Sacramento", 1424415, 2502, 1850, "Sacramento", "06067"));
      california.addCounty(new County("Contra Costa", 1051674, 1865, 1850, "Martinez", "06013"));
      california.addCounty(new County("Fresno", 931098, 15444, 1856, "Fresno", "06019"));
      california.addCounty(new County("San Francisco", 845559, 122, 1850, "San Francisco", "06075"));
      california.addCounty(new County("Ventura", 831587, 4781, 1872, "Ventura", "06111"));
      california.addCounty(new County("Kern", 817517, 21088, 1866, "Bakersfield", "06029"));
      california.addCounty(new County("San Mateo", 739469, 1163, 1856, "San Mateo", "06081"));
      california.addCounty(new County("San Joaquin", 685660, 3623, 1850, "Stockton", "06077"));
      california.addCounty(new County("Stanislaus", 525903, 3872, 1854, "Modesto", "06099"));
      california.addCounty(new County("Sonoma", 484470, 4082, 1850, "Santa Rosa", "06097"));
      california.addCounty(new County("Tulare", 435254, 12494, 1852, "Visalia", "06107"));
      california.addCounty(new County("Santa Barbara", 428655, 7091, 1850, "Santa Barbara", "06083"));
      california.addCounty(new County("Monterey", 428549, 8604, 1850, "Salinas", "06053"));
      california.addCounty(new County("Solano", 426757, 2145, 1850, "Fairfield", "06095"));
      california.addCounty(new County("Placer", 333401, 3893, 1851, "Auburn", "06061"));
      california.addCounty(new County("San Luis Obispo", 269337, 8557, 1850, "San Luis Obispo", "06079"));
      california.addCounty(new County("Santa Cruz", 266519, 1155, 1850, "Santa Cruz", "06087"));
      california.addCounty(new County("Marin", 257406, 1347, 1850, "San Rafael", "06041"));
      california.addCounty(new County("Merced", 255250, 4996, 1855, "Merced", "06047"));
      california.addCounty(new County("Butte", 220407, 4248, 1850, "Oroville", "06007"));
      california.addCounty(new County("Yolo", 199066, 2621, 1850, "Woodland", "06113"));
      california.addCounty(new County("Shasta", 182236, 9806, 1850, "Redding", "06089"));
      california.addCounty(new County("El Dorado", 179722, 4434, 1850, "Placerville", "06017"));
      california.addCounty(new County("Imperial", 176158, 10813, 1907, "El Centro", "06025"));
      california.addCounty(new County("Kings", 154434, 3600, 1893, "Hanford", "06031"));
      california.addCounty(new County("Madera", 150887, 5537, 1893, "Madera", "06039"));
      california.addCounty(new County("Napa", 136704, 1953, 1850, "Napa", "06055"));
      california.addCounty(new County("Humboldt", 132821, 9254, 1853, "Eureka", "06023"));
      california.addCounty(new County("Nevada", 99186, 2481, 1851, "Nevada City", "06057"));
      california.addCounty(new County("Sutter", 95878, 1562, 1850, "Sutter", "06101"));
      california.addCounty(new County("Mendocino", 90163, 9088, 1850, "Ukiah", "06045"));
      california.addCounty(new County("Yuba", 71929, 1632, 1850, "Yuba", "06115"));
      california.addCounty(new County("Lake", 64059, 3258, 1861, "Lakeport", "06033"));
      california.addCounty(new County("Tehama", 62419, 7643, 1856, "Red Bluff", "06103"));
      california.addCounty(new County("San Benito", 57784, 3597, 1874, "Hollister", "06069"));
      california.addCounty(new County("Tuolumne", 56799, 5791, 1850, "Tuolumne", "06109"));
      california.addCounty(new County("Calaveras", 46127, 2642, 1850, "San Andreas", "06009"));
      california.addCounty(new County("Siskiyou", 45971, 16283, 1852, "Yreka", "06093"));
      california.addCounty(new County("Amador", 37943, 1536, 1854, "Jackson", "06005"));
      california.addCounty(new County("Lassen", 35757, 11805, 1864, "Susanville", "06035"));
      california.addCounty(new County("Del Norte", 29419, 2611, 1850, "Crescent City", "06015"));
      california.addCounty(new County("Glenn", 29195, 3406, 1891, "Willows", "06021"));
      california.addCounty(new County("Colusa", 21910, 2981, 1850, "Colusa", "06011"));
      california.addCounty(new County("Plumas", 20917, 6615, 1854, "Quincy", "06063"));
      california.addCounty(new County("Mariposa", 18406, 3758, 1850, "Mariposa", "06043"));
      california.addCounty(new County("Inyo", 18152, 26397, 1866, "Independence", "06027"));
      california.addCounty(new County("Trinity", 13966, 8234, 1850, "Weaverville", "06105"));
      california.addCounty(new County("Mono", 13759, 7884, 1861, "Bridgeport", "06051"));
      california.addCounty(new County("Modoc", 9702, 10215, 1874, "Alturas", "06049"));
      california.addCounty(new County("Sierra", 3380, 2468, 1852, "Downieville", "06091"));
      california.addCounty(new County("Alpine", 1222, 1914, 1864, "Markleeville", "06003"));
      beans.put("california", california);

      State nevada = new State();
      nevada.setName("Nevada");
      nevada.addCounty(new County("Clark", 1375765, 20489, 1908, "Las Vegas", "32003"));
      nevada.addCounty(new County("Washoe", 339486, 16426, 1861, "Reno", "32031"));
      nevada.addCounty(new County("Carson City", 52457, 373, 1969, "Carson City", "32510"));
      nevada.addCounty(new County("Elko", 45291, 44501, 1869, "Elko", "32007"));
      nevada.addCounty(new County("Douglas", 41259, 1839, 1861, "Minden", "32005"));
      nevada.addCounty(new County("Lyon", 34501, 5164, 1861, "Yerington", "32019"));
      nevada.addCounty(new County("Nye", 32485, 47001, 1864, "Tonopah", "32023"));
      nevada.addCounty(new County("Churchill", 23982, 12766, 1861, "Fallon", "32001"));
      nevada.addCounty(new County("Humboldt", 16106, 24988, 1861, "Winnemucca", "32013"));
      nevada.addCounty(new County("White Pine", 9181, 22991, 1869, "Ely", "32033"));
      nevada.addCounty(new County("Pershing", 6693, 15563, 1919, "Lovelock", "32027"));
      nevada.addCounty(new County("Lander", 5794, 14229, 1861, "Battle Mountain", "32015"));
      nevada.addCounty(new County("Mineral", 5071, 9731, 1911, "Hawthorne", "32021"));
      nevada.addCounty(new County("Lincoln", 4165, 27545, 1866, "Pioche", "32017"));
      nevada.addCounty(new County("Storey", 3399, 684, 1861, "Virginia City", "32029"));
      nevada.addCounty(new County("Eureka", 1651, 10816, 1873, "Eureka", "32011"));
      nevada.addCounty(new County("Esmeralda", 971, 9295, 1861, "Goldfield", "32009"));
      beans.put("nevada", nevada);

      return beans;
   }

   /**
    * Gets a beans map with a <code>List</code> of division beans, most of
    * which contain <code>Team</code> beans.  The name is "divisionsList".
    *
    * @return A <code>Map</code> of  beans.
    */
   public static Map<String, Object> getDivisionData()
   {
      Map<String, Object> beans = new HashMap<String, Object>();
      List<Division> divisionsList = new ArrayList<Division>();

      divisionsList.add(getAtlanticDivision());
      divisionsList.add(getCentralDivision());
      divisionsList.add(getSoutheastDivision());
      divisionsList.add(getNorthwestDivision());
      divisionsList.add(getPacificDivision());
      divisionsList.add(getSouthwestDivision());
      divisionsList.add(getEmptyDivision());
      divisionsList.add(getOfTheirOwnDivision());

      beans.put("divisionsList", divisionsList);

      return beans;
   }

   /**
    * Get a beans map with only one <code>Division</code>, determined by the
    * <code>code</code> argument.  The name is "division".
    * @param code Determines with division, 0-7.
    * @return A <code>Division</code>, or <code>null</code> if out of range.
    */
   public static Map<String, Object> getSpecificDivisionData(int code)
   {
      Map<String, Object> beans = new HashMap<String, Object>();
      Division division = null;
      switch(code)
      {
      case 0:
         division = getAtlanticDivision();
         break;
      case 1:
         division = getCentralDivision();
         break;
      case 2:
         division = getSoutheastDivision();
         break;
      case 3:
         division = getNorthwestDivision();
         break;
      case 4:
         division = getPacificDivision();
         break;
      case 5:
         division = getSouthwestDivision();
         break;
      case 6:
         division = getEmptyDivision();
         break;
      case 7:
         division = getOfTheirOwnDivision();
         break;
      }
      beans.put("division", division);
      return beans;
   }

   /**
    * Return Atlantic Division statistics.
    * @return A <code>Division</code>.
    */
   private static Division getAtlanticDivision()
   {
      Division atlantic = new Division();
      atlantic.setName("Atlantic");
      Team boston = new Team();
      boston.setCity("Boston"); boston.setName("Celtics"); boston.setWins(51); boston.setLosses(21);
      atlantic.addTeam(boston);
      Team phila = new Team();
      phila.setCity("Philadelphia"); phila.setName("76ers"); phila.setWins(37); phila.setLosses(36);
      atlantic.addTeam(phila);
      Team newYork = new Team();
      newYork.setCity("New York"); newYork.setName("Knicks"); newYork.setWins(35); newYork.setLosses(38);
      atlantic.addTeam(newYork);
      Team newJersey = new Team();
      newJersey.setCity("New Jersey"); newJersey.setName("Nets"); newJersey.setWins(23); newJersey.setLosses(49);
      atlantic.addTeam(newJersey);
      Team toronto = new Team();
      toronto.setCity("Toronto"); toronto.setName("Raptors"); toronto.setWins(20); toronto.setLosses(53);
      atlantic.addTeam(toronto);
      return atlantic;
   }

   /**
    * Return Central Division statistics.
    * @return A <code>Division</code>.
    */
   private static Division getCentralDivision()
   {
      Division central = new Division();
      central.setName("Central");
      Team chicago = new Team();
      chicago.setCity("Chicago"); chicago.setName("Bulls"); chicago.setWins(53); chicago.setLosses(19);
      central.addTeam(chicago);
      Team indiana = new Team();
      indiana.setCity("Indiana"); indiana.setName("Pacers"); indiana.setWins(32); indiana.setLosses(42);
      central.addTeam(indiana);
      Team milwaukee = new Team();
      milwaukee.setCity("Milwaukee"); milwaukee.setName("Bucks"); milwaukee.setWins(29); milwaukee.setLosses(43);
      central.addTeam(milwaukee);
      Team detroit = new Team();
      detroit.setCity("Detroit"); detroit.setName("Pistons"); detroit.setWins(26); detroit.setLosses(47);
      central.addTeam(detroit);
      Team cleveland = new Team();
      cleveland.setCity("Cleveland"); cleveland.setName("Cavaliers"); cleveland.setWins(14); cleveland.setLosses(58);
      central.addTeam(cleveland);
      return central;
   }

   /**
    * Return Southeast Division statistics.
    * @return A <code>Division</code>.
    */
   private static Division getSoutheastDivision()
   {
      Division southeast = new Division();
      southeast.setName("Southeast");
      Team miami = new Team();
      miami.setCity("Miami"); miami.setName("Heat"); miami.setWins(51); miami.setLosses(22);
      southeast.addTeam(miami);
      Team orlando = new Team();
      orlando.setCity("Orlando"); orlando.setName("Magic"); orlando.setWins(47); orlando.setLosses(26);
      southeast.addTeam(orlando);
      Team atlanta = new Team();
      atlanta.setCity("Atlanta"); atlanta.setName("Hawks"); atlanta.setWins(42); atlanta.setLosses(32);
      southeast.addTeam(atlanta);
      Team charlotte = new Team();
      charlotte.setCity("Charlotte"); charlotte.setName("Bobcats"); charlotte.setWins(30); charlotte.setLosses(42);
      southeast.addTeam(charlotte);
      Team wash = new Team();
      wash.setCity("Washington"); wash.setName("Wizards"); wash.setWins(17); wash.setLosses(55);
      southeast.addTeam(wash);
      return southeast;
   }

   /**
    * Return Northwest Division statistics.
    * @return A <code>Division</code>.
    */
   private static Division getNorthwestDivision()
   {
      Division northwest = new Division();
      northwest.setName("Northwest");
      Team okCity = new Team();
      okCity.setCity("Oklahoma City"); okCity.setName("Thunder"); okCity.setWins(48); okCity.setLosses(24);
      northwest.addTeam(okCity);
      Team denver = new Team();
      denver.setCity("Denver"); denver.setName("Nuggets"); denver.setWins(44); denver.setLosses(29);
      northwest.addTeam(denver);
      Team portland = new Team();
      portland.setCity("Portland"); portland.setName("Trailblazers"); portland.setWins(42); portland.setLosses(31);
      northwest.addTeam(portland);
      Team utah = new Team();
      utah.setCity("Utah"); utah.setName("Jazz"); utah.setWins(36); utah.setLosses(38);
      northwest.addTeam(utah);
      Team minnesota = new Team();
      minnesota.setCity("Minnesota"); minnesota.setName("Timberwolves"); minnesota.setWins(17); minnesota.setLosses(57);
      northwest.addTeam(minnesota);
      return northwest;
   }

   /**
    * Return Pacific Division statistics.
    * @return A <code>Division</code>.
    */
   private static Division getPacificDivision()
   {
      Division pacific = new Division();
      pacific.setName("Pacific");
      Team lal = new Team();
      lal.setCity("Los Angeles"); lal.setName("Lakers"); lal.setWins(53); lal.setLosses(20);
      pacific.addTeam(lal);
      Team phoenix = new Team();
      phoenix.setCity("Phoenix"); phoenix.setName("Suns"); phoenix.setWins(36); phoenix.setLosses(36);
      pacific.addTeam(phoenix);
      Team gState = new Team();
      gState.setCity("Golden State"); gState.setName("Warriors"); gState.setWins(32); gState.setLosses(42);
      pacific.addTeam(gState);
      Team lac = new Team();
      lac.setCity("Los Angeles"); lac.setName("Clippers"); lac.setWins(29); lac.setLosses(45);
      pacific.addTeam(lac);
      Team sacramento = new Team();
      sacramento.setCity("Sacramento"); sacramento.setName("Kings"); sacramento.setWins(20); sacramento.setLosses(52);
      pacific.addTeam(sacramento);
      return pacific;
   }

   /**
    * Return Southwest Division statistics.
    * @return A <code>Division</code>.
    */
   private static Division getSouthwestDivision()
   {
      Division southwest = new Division();
      southwest.setName("Southwest");
      Team sanAnt = new Team();
      sanAnt.setCity("San Antonio"); sanAnt.setName("Spurs"); sanAnt.setWins(57); sanAnt.setLosses(16);
      southwest.addTeam(sanAnt);
      Team dallas = new Team();
      dallas.setCity("Dallas"); dallas.setName("Mavericks"); dallas.setWins(52); dallas.setLosses(21);
      southwest.addTeam(dallas);
      Team newOrl = new Team();
      newOrl.setCity("New Orleans"); newOrl.setName("Hornets"); newOrl.setWins(42); newOrl.setLosses(32);
      southwest.addTeam(newOrl);
      Team memphis = new Team();
      memphis.setCity("Memphis"); memphis.setName("Grizzlies"); memphis.setWins(41); memphis.setLosses(33);
      southwest.addTeam(memphis);
      Team houston = new Team();
      houston.setCity("Houston"); houston.setName("Rockets"); houston.setWins(38); houston.setLosses(35);
      southwest.addTeam(houston);
      return southwest;
   }

   /**
    * Return Empty Division statistics.
    * @return A <code>Division</code>.
    */
   private static Division getEmptyDivision()
   {
      Division empty = new Division();
      empty.setName("Empty");
      return empty;
   }

   /**
    * Return Of Their Own Division statistics.
    * @return A <code>Division</code>.
    */
   private static Division getOfTheirOwnDivision()
   {
      Division ofTheirOwn = new Division();
      ofTheirOwn.setName("Of Their Own");
      Team harlem = new Team();
      harlem.setCity("Harlem"); harlem.setName("Globetrotters"); harlem.setWins(21227); harlem.setLosses(341);
      ofTheirOwn.addTeam(harlem);
      return ofTheirOwn;
   }
}
