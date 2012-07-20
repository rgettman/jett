package net.sf.jett.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This JUnit Test class tests the evaluation of the "agg" tag.
 */
public class AggTagTest extends TestCase
{
   /**
    * Tests the .xls template spreadsheet.
    * @throws java.io.IOException If an I/O error occurs.
    * @throws org.apache.poi.openxml4j.exceptions.InvalidFormatException If the input spreadsheet is invalid.
    */
   @Test
   public void testXls() throws IOException, InvalidFormatException
   {
      super.testXls();
   }

   /**
    * Tests the .xlsx template spreadsheet.
    * @throws IOException If an I/O error occurs.
    * @throws InvalidFormatException If the input spreadsheet is invalid.
    */
   @Test
   public void testXlsx() throws IOException, InvalidFormatException
   {
      super.testXlsx();
   }

   /**
    * Returns the Excel name base for the template and resultant spreadsheets
    * for this test.
    * @return The Excel name base for this test.
    */
   protected String getExcelNameBase()
   {
      return "AggTag";
   }

   /**
    * Validate the newly created resultant <code>Workbook</code> with JUnit
    * assertions.
    * @param workbook A <code>Workbook</code>.
    */
   protected void check(Workbook workbook)
   {
      MathContext mc = new MathContext(100, RoundingMode.HALF_EVEN);
      Sheet agg = workbook.getSheetAt(0);
      assertEquals("A", getStringCellValue(agg, 2, 0));
      assertEquals(3, getNumericCellValue(agg, 2, 1), DELTA);
      assertEquals(1582165, getNumericCellValue(agg, 2, 2), DELTA);
      double caAvgA = (1911 + 1914 + 1536) / 3;
      double caStdDevA = Math.sqrt(((1911 - caAvgA)*(1911 - caAvgA) + (1914 - caAvgA)*(1914 - caAvgA) + (1536 - caAvgA)*(1536 - caAvgA)) / 2);
      assertEquals(caStdDevA, getNumericCellValue(agg, 2, 3), DELTA);
      assertEquals("B", getStringCellValue(agg, 3, 0));
      assertEquals(1, getNumericCellValue(agg, 3, 1), DELTA);
      assertEquals(220407, getNumericCellValue(agg, 3, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 3, 3), DELTA);
      assertEquals("C", getStringCellValue(agg, 4, 0));
      assertEquals(3, getNumericCellValue(agg, 4, 1), DELTA);
      assertEquals(1119711, getNumericCellValue(agg, 4, 2), DELTA);
      double caAvgC = (2642 + 2981 + 1865) / 3;
      double caStdDevC = Math.sqrt(((2642 - caAvgC)*(2642 - caAvgC) + (2981 - caAvgC)*(2981 - caAvgC) + (1865 - caAvgC)*(1865 - caAvgC)) / 2);
      assertEquals(caStdDevC, getNumericCellValue(agg, 4, 3), DELTA);
      assertEquals("D", getStringCellValue(agg, 5, 0));
      assertEquals(1, getNumericCellValue(agg, 5, 1), DELTA);
      assertEquals(29419, getNumericCellValue(agg, 5, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 5, 3), DELTA);
      assertEquals("E", getStringCellValue(agg, 6, 0));
      assertEquals(1, getNumericCellValue(agg, 6, 1), DELTA);
      assertEquals(179722, getNumericCellValue(agg, 6, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 6, 3), DELTA);
      assertEquals("F", getStringCellValue(agg, 7, 0));
      assertEquals(1, getNumericCellValue(agg, 7, 1), DELTA);
      assertEquals(931098, getNumericCellValue(agg, 7, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 7, 3), DELTA);
      assertEquals("G", getStringCellValue(agg, 8, 0));
      assertEquals(1, getNumericCellValue(agg, 8, 1), DELTA);
      assertEquals(29195, getNumericCellValue(agg, 8, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 8, 3), DELTA);
      assertEquals("H", getStringCellValue(agg, 9, 0));
      assertEquals(1, getNumericCellValue(agg, 9, 1), DELTA);
      assertEquals(132821, getNumericCellValue(agg, 9, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 9, 3), DELTA);
      assertEquals("I", getStringCellValue(agg, 10, 0));
      assertEquals(2, getNumericCellValue(agg, 10, 1), DELTA);
      assertEquals(194310, getNumericCellValue(agg, 10, 2), DELTA);
      double caAvgI = (10813 + 26397) / 2;
      double caStdDevI = Math.sqrt(((10813 - caAvgI)*(10813 - caAvgI) + (26397 - caAvgI)*(26397 - caAvgI)) / 1);
      assertEquals(caStdDevI, getNumericCellValue(agg, 10, 3), DELTA);
      assertEquals("K", getStringCellValue(agg, 11, 0));
      assertEquals(2, getNumericCellValue(agg, 11, 1), DELTA);
      assertEquals(971951, getNumericCellValue(agg, 11, 2), DELTA);
      double caAvgK = (21088 + 3600) / 2;
      double caStdDevK = Math.sqrt(((21088 - caAvgK)*(21088 - caAvgK) + (3600 - caAvgK)*(3600 - caAvgK)) / 1);
      assertEquals(caStdDevK, getNumericCellValue(agg, 11, 3), DELTA);
      assertEquals("L", getStringCellValue(agg, 12, 0));
      assertEquals(3, getNumericCellValue(agg, 12, 1), DELTA);
      assertEquals(10463666, getNumericCellValue(agg, 12, 2), DELTA);
      double caAvgL = (3258 + 11805 + 10515) / 3;
      double caStdDevL = Math.sqrt(((3258 - caAvgL)*(3258 - caAvgL) + (11805 - caAvgL)*(11805 - caAvgL) + (10515 - caAvgL)*(10515 - caAvgL)) / 2);
      assertEquals(caStdDevL, getNumericCellValue(agg, 12, 3), DELTA);
      assertEquals("M", getStringCellValue(agg, 13, 0));
      assertEquals(8, getNumericCellValue(agg, 13, 1), DELTA);
      assertEquals(1224122, getNumericCellValue(agg, 13, 2), DELTA);
      // Too much precision gets lost in floating-point arithmetic with "double".
      // Use BigDecimals with a very high precision.
      BigDecimal caM1 = new BigDecimal("5537");
      BigDecimal caM2 = new BigDecimal("1347");
      BigDecimal caM3 = new BigDecimal("3758");
      BigDecimal caM4 = new BigDecimal("9088");
      BigDecimal caM5 = new BigDecimal("4996");
      BigDecimal caM6 = new BigDecimal("10215");
      BigDecimal caM7 = new BigDecimal("7884");
      BigDecimal caM8 = new BigDecimal("8604");
      BigDecimal bdCaAvgM = caM1.add(caM2, mc).add(caM3, mc).add(caM4, mc).add(caM5, mc).add(caM6, mc).add(caM7, mc).add(caM8, mc).divide(new BigDecimal(8), mc);
      BigDecimal bdCaStdDevM = caM1.subtract(bdCaAvgM, mc).pow(2, mc);
      bdCaStdDevM = bdCaStdDevM.add(caM2.subtract(bdCaAvgM, mc).pow(2, mc), mc);
      bdCaStdDevM = bdCaStdDevM.add(caM3.subtract(bdCaAvgM, mc).pow(2, mc), mc);
      bdCaStdDevM = bdCaStdDevM.add(caM4.subtract(bdCaAvgM, mc).pow(2, mc), mc);
      bdCaStdDevM = bdCaStdDevM.add(caM5.subtract(bdCaAvgM, mc).pow(2, mc), mc);
      bdCaStdDevM = bdCaStdDevM.add(caM6.subtract(bdCaAvgM, mc).pow(2, mc), mc);
      bdCaStdDevM = bdCaStdDevM.add(caM7.subtract(bdCaAvgM, mc).pow(2, mc), mc);
      bdCaStdDevM = bdCaStdDevM.add(caM8.subtract(bdCaAvgM, mc).pow(2, mc), mc);
      bdCaStdDevM = bdCaStdDevM.divide(new BigDecimal("7"), mc);
      double caStdDevM = Math.sqrt(bdCaStdDevM.doubleValue());
      assertEquals(caStdDevM, getNumericCellValue(agg, 13, 3), DELTA);
      assertEquals("N", getStringCellValue(agg, 14, 0));
      assertEquals(2, getNumericCellValue(agg, 14, 1), DELTA);
      assertEquals(235890, getNumericCellValue(agg, 14, 2), DELTA);
      double caAvgN = (1953 + 2481) / 2;
      double caStdDevN = Math.sqrt(((1953 - caAvgN)*(1953 - caAvgN) + (2481 - caAvgN)*(2481 - caAvgN)) / 1);
      assertEquals(caStdDevN, getNumericCellValue(agg, 14, 3), DELTA);
      assertEquals("O", getStringCellValue(agg, 15, 0));
      assertEquals(1, getNumericCellValue(agg, 15, 1), DELTA);
      assertEquals(3121251, getNumericCellValue(agg, 15, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 15, 3), DELTA);
      assertEquals("P", getStringCellValue(agg, 16, 0));
      assertEquals(2, getNumericCellValue(agg, 16, 1), DELTA);
      assertEquals(354318, getNumericCellValue(agg, 16, 2), DELTA);
      double caAvgP = (3893 + 6615) / 2;
      double caStdDevP = Math.sqrt(((3893 - caAvgP)*(3893 - caAvgP) + (6615 - caAvgP)*(6615 - caAvgP)) / 1);
      assertEquals(caStdDevP, getNumericCellValue(agg, 16, 3), DELTA);
      assertEquals("R", getStringCellValue(agg, 17, 0));
      assertEquals(1, getNumericCellValue(agg, 17, 1), DELTA);
      assertEquals(2088322, getNumericCellValue(agg, 17, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 17, 3), DELTA);
      assertEquals("S", getStringCellValue(agg, 18, 0));
      assertEquals(18, getNumericCellValue(agg, 18, 1), DELTA);
      assertEquals(13521108, getNumericCellValue(agg, 18, 2), DELTA);
      // Too much precision gets lost in floating-point arithmetic with "double".
      // Use BigDecimals with a very high precision.
      BigDecimal caS1 = new BigDecimal("2502");
      BigDecimal caS2 = new BigDecimal("3597");
      BigDecimal caS3 = new BigDecimal("51960");
      BigDecimal caS4 = new BigDecimal("10888");
      BigDecimal caS5 = new BigDecimal("122");
      BigDecimal caS6 = new BigDecimal("3623");
      BigDecimal caS7 = new BigDecimal("8557");
      BigDecimal caS8 = new BigDecimal("1163");
      BigDecimal caS9 = new BigDecimal("7091");
      BigDecimal caS10 = new BigDecimal("3344");
      BigDecimal caS11 = new BigDecimal("1155");
      BigDecimal caS12 = new BigDecimal("9806");
      BigDecimal caS13 = new BigDecimal("2468");
      BigDecimal caS14 = new BigDecimal("16283");
      BigDecimal caS15 = new BigDecimal("2145");
      BigDecimal caS16 = new BigDecimal("4082");
      BigDecimal caS17 = new BigDecimal("3872");
      BigDecimal caS18 = new BigDecimal("1562");
      BigDecimal bdCaAvgS = caS1.add(caS2, mc).add(caS3, mc).add(caS4, mc).add(caS5, mc).add(caS6, mc).add(caS7, mc).add(caS8, mc).add(caS9, mc).
                                 add(caS10, mc).add(caS11, mc).add(caS12, mc).add(caS13, mc).add(caS14, mc).add(caS15, mc).add(caS16, mc).add(caS17, mc).add(caS18, mc).divide(new BigDecimal(18), mc);
      BigDecimal bdCaStdDevS = caS1.subtract(bdCaAvgS, mc).pow(2, mc);
      bdCaStdDevS = bdCaStdDevS.add(caS2.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS3.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS4.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS5.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS6.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS7.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS8.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS9.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS10.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS11.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS12.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS13.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS14.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS15.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS16.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS17.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.add(caS18.subtract(bdCaAvgS, mc).pow(2, mc), mc);
      bdCaStdDevS = bdCaStdDevS.divide(new BigDecimal("17"), mc);
      double caStdDevS = Math.sqrt(bdCaStdDevS.doubleValue());
      assertEquals(caStdDevS, getNumericCellValue(agg, 18, 3), DELTA);
      assertEquals("T", getStringCellValue(agg, 19, 0));
      assertEquals(4, getNumericCellValue(agg, 19, 1), DELTA);
      assertEquals(568438, getNumericCellValue(agg, 19, 2), DELTA);
      // Too much precision gets lost in floating-point arithmetic with "double".
      // Use BigDecimals with a very high precision.
      BigDecimal caT1 = new BigDecimal("7643");
      BigDecimal caT2 = new BigDecimal("8234");
      BigDecimal caT3 = new BigDecimal("12494");
      BigDecimal caT4 = new BigDecimal("5791");
      BigDecimal bdCaAvgT = caT1.add(caT2, mc).add(caT3, mc).add(caT4, mc).divide(new BigDecimal(4), mc);
      BigDecimal bdCaStdDevT = caT1.subtract(bdCaAvgT, mc).pow(2, mc);
      bdCaStdDevT = bdCaStdDevT.add(caT2.subtract(bdCaAvgT, mc).pow(2, mc), mc);
      bdCaStdDevT = bdCaStdDevT.add(caT3.subtract(bdCaAvgT, mc).pow(2, mc), mc);
      bdCaStdDevT = bdCaStdDevT.add(caT4.subtract(bdCaAvgT, mc).pow(2, mc), mc);
      bdCaStdDevT = bdCaStdDevT.divide(new BigDecimal("3"), mc);
      double caStdDevT = Math.sqrt(bdCaStdDevT.doubleValue());
      assertEquals(caStdDevT, getNumericCellValue(agg, 19, 3), DELTA);
      assertEquals("V", getStringCellValue(agg, 20, 0));
      assertEquals(1, getNumericCellValue(agg, 20, 1), DELTA);
      assertEquals(831587, getNumericCellValue(agg, 20, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 20, 3), DELTA);
      assertEquals("Y", getStringCellValue(agg, 21, 0));
      assertEquals(2, getNumericCellValue(agg, 21, 1), DELTA);
      assertEquals(270995, getNumericCellValue(agg, 21, 2), DELTA);
      // Too much precision gets lost in floating-point arithmetic with "double".
      // Use BigDecimals with a very high precision.
      BigDecimal caY1 = new BigDecimal("2621");
      BigDecimal caY2 = new BigDecimal("1632");
      BigDecimal bdCaAvgY = caY1.add(caY2, mc).divide(new BigDecimal(2), mc);
      BigDecimal bdCaStdDevY = caY1.subtract(bdCaAvgY, mc).pow(2, mc);
      bdCaStdDevY = bdCaStdDevY.add(caY2.subtract(bdCaAvgY, mc).pow(2, mc), mc);
      double caStdDevY = Math.sqrt(bdCaStdDevY.doubleValue());
      assertEquals(caStdDevY, getNumericCellValue(agg, 21, 3), DELTA);

      assertEquals("C", getStringCellValue(agg, 24, 0));
      assertEquals(3, getNumericCellValue(agg, 24, 1), DELTA);
      assertEquals(1452204, getNumericCellValue(agg, 24, 2), DELTA);
      // Too much precision gets lost in floating-point arithmetic with "double".
      // Use BigDecimals with a very high precision.
      BigDecimal nvC1 = new BigDecimal("373");
      BigDecimal nvC2 = new BigDecimal("12766");
      BigDecimal nvC3 = new BigDecimal("20489");
      BigDecimal bdNvAvgC = nvC1.add(nvC2, mc).add(nvC3, mc).divide(new BigDecimal(3), mc);
      BigDecimal bdNvStdDevC = nvC1.subtract(bdNvAvgC, mc).pow(2, mc);
      bdNvStdDevC = bdNvStdDevC.add(nvC2.subtract(bdNvAvgC, mc).pow(2, mc), mc);
      bdNvStdDevC = bdNvStdDevC.add(nvC3.subtract(bdNvAvgC, mc).pow(2, mc), mc);
      bdNvStdDevC = bdNvStdDevC.divide(new BigDecimal("2"), mc);
      double nvStdDvC = Math.sqrt(bdNvStdDevC.doubleValue());
      assertEquals(nvStdDvC, getNumericCellValue(agg, 24, 3), DELTA);
      assertEquals("D", getStringCellValue(agg, 25, 0));
      assertEquals(1, getNumericCellValue(agg, 25, 1), DELTA);
      assertEquals(41259, getNumericCellValue(agg, 25, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 25, 3), DELTA);
      assertEquals("E", getStringCellValue(agg, 26, 0));
      assertEquals(3, getNumericCellValue(agg, 26, 1), DELTA);
      assertEquals(47913, getNumericCellValue(agg, 26, 2), DELTA);
      // Too much precision gets lost in floating-point arithmetic with "double".
      // Use BigDecimals with a very high precision.
      BigDecimal nvE1 = new BigDecimal("44501");
      BigDecimal nvE2 = new BigDecimal("9295");
      BigDecimal nvE3 = new BigDecimal("10816");
      BigDecimal bdNvAvgE = nvE1.add(nvE2, mc).add(nvE3, mc).divide(new BigDecimal(3), mc);
      BigDecimal bdNvStdDevE = nvE1.subtract(bdNvAvgE, mc).pow(2, mc);
      bdNvStdDevE = bdNvStdDevE.add(nvE2.subtract(bdNvAvgE, mc).pow(2, mc), mc);
      bdNvStdDevE = bdNvStdDevE.add(nvE3.subtract(bdNvAvgE, mc).pow(2, mc), mc);
      bdNvStdDevE = bdNvStdDevE.divide(new BigDecimal("2"), mc);
      double nvStdDvE = Math.sqrt(bdNvStdDevE.doubleValue());
      assertEquals(nvStdDvE, getNumericCellValue(agg, 26, 3), DELTA);
      assertEquals("H", getStringCellValue(agg, 27, 0));
      assertEquals(1, getNumericCellValue(agg, 27, 1), DELTA);
      assertEquals(16106, getNumericCellValue(agg, 27, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 27, 3), DELTA);
      assertEquals("L", getStringCellValue(agg, 28, 0));
      assertEquals(3, getNumericCellValue(agg, 28, 1), DELTA);
      assertEquals(44460, getNumericCellValue(agg, 28, 2), DELTA);
      double nvAvgL = (14229 + 27545 + 5164) / 3;
      double nvStdDvL = Math.sqrt(((14229 - nvAvgL)*(14229 - nvAvgL) + (27545 - nvAvgL)*(27545 - nvAvgL) + (5164 - nvAvgL)*(5164 - nvAvgL)) / 2);
      assertEquals(nvStdDvL, getNumericCellValue(agg, 28, 3), DELTA);
      assertEquals("M", getStringCellValue(agg, 29, 0));
      assertEquals(1, getNumericCellValue(agg, 29, 1), DELTA);
      assertEquals(5071, getNumericCellValue(agg, 29, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 29, 3), DELTA);
      assertEquals("N", getStringCellValue(agg, 30, 0));
      assertEquals(1, getNumericCellValue(agg, 30, 1), DELTA);
      assertEquals(32485, getNumericCellValue(agg, 30, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 30, 3), DELTA);
      assertEquals("P", getStringCellValue(agg, 31, 0));
      assertEquals(1, getNumericCellValue(agg, 31, 1), DELTA);
      assertEquals(6693, getNumericCellValue(agg, 31, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 31, 3), DELTA);
      assertEquals("S", getStringCellValue(agg, 32, 0));
      assertEquals(1, getNumericCellValue(agg, 32, 1), DELTA);
      assertEquals(3399, getNumericCellValue(agg, 32, 2), DELTA);
      assertEquals(0, getNumericCellValue(agg, 32, 3), DELTA);
      assertEquals("W", getStringCellValue(agg, 33, 0));
      assertEquals(2, getNumericCellValue(agg, 33, 1), DELTA);
      assertEquals(348667, getNumericCellValue(agg, 33, 2), DELTA);
      // Too much precision gets lost in floating-point arithmetic with "double".
      // Use BigDecimals with a very high precision.
      BigDecimal nvW1 = new BigDecimal("16426");
      BigDecimal nvW2 = new BigDecimal("22991");
      BigDecimal bdNvAvgW = nvW1.add(nvW2, mc).divide(new BigDecimal(2), mc);
      BigDecimal bdNvStdDevW = nvW1.subtract(bdNvAvgW, mc).pow(2, mc);
      bdNvStdDevW = bdNvStdDevW.add(nvW2.subtract(bdNvAvgW, mc).pow(2, mc), mc);
      double nvStdDvW = Math.sqrt(bdNvStdDevW.doubleValue());
      assertEquals(nvStdDvW, getNumericCellValue(agg, 33, 3), DELTA);
   }

   /**
    * This test is a single map test.
    * @return <code>false</code>.
    */
   protected boolean isMultipleBeans()
   {
      return false;
   }

   /**
    * For single beans map tests, return the <code>Map</code> of bean names to
    * bean values.
    * @return A <code>Map</code> of bean names to bean values.
    */
   protected Map<String, Object> getBeansMap()
   {
      return TestUtility.getStateData();
   }
}