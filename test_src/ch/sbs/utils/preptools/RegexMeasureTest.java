package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import ch.sbs.plugin.preptools.PrepToolLoader;

/**
	* Copyright (C) 2010 Swiss Library for the Blind, Visually Impaired and Print Disabled
	*
	* This file is part of dtbook-preptools.
	* 	
	* dtbook-preptools is free software: you can redistribute it
	* and/or modify it under the terms of the GNU Lesser General Public
	* License as published by the Free Software Foundation, either
	* version 3 of the License, or (at your option) any later version.
	* 	
	* This program is distributed in the hope that it will be useful,
	* but WITHOUT ANY WARRANTY; without even the implied warranty of
	* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
	* Lesser General Public License for more details.
	* 	
	* You should have received a copy of the GNU Lesser General Public
	* License along with this program. If not, see
	* <http://www.gnu.org/licenses/>.
	*/

public class RegexMeasureTest {

	@Test
	public void testMeasure() {
		final Pattern pattern = Pattern.compile(PrepToolLoader.MEASURE_SEARCH_REGEX);
		assertTrue(pattern.matcher("5.6km weg").find());
		assertTrue(pattern.matcher("5.6\nkm weg").find());
		assertTrue(pattern.matcher("5'300.62 k dabei").find());
		assertFalse(pattern.matcher("2343").find());
	}

	@Test
	public void testMeasureAngstrom() {
		final Pattern pattern = Pattern.compile(PrepToolLoader.MEASURE_SEARCH_REGEX);
		assertTrue(pattern.matcher("Die Länge beträgt 50 Å.").find());
	}

	@Test
	public void testMeasureKmh() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "4 km/h";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasurekwH() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "4.5 kwH";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasureCm() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "4'555'555.00 cm";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasureMW() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "1 MW";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasurekN() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "3,4kN";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testExcludeMinus() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "-3,4kN";
		final String input = "bla " + focus + " blu";
		final String expected = "bla -_" + focus.substring(1) + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testMeasurekWh() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "10000 kWh";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasureAngstroem() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "1 Å";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasureL() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "45 l";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasureMg() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "34.46 mg";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasuremol() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "400.000.000 mol";
		final String input = "bla " + focus + " blu";
		assertEquals("bla _" + focus + "_ blu", pattern.matcher(input)
				.replaceAll("_$1_"));
	}

	@Test
	public void testMeasuremikrom() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "345 μm";
		final String input = "bla " + focus + " blu";
		final String expected = "bla _" + focus + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testMeasureDontMatch() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "4. September 1923 in Zürich geboren 4 34 dsdsfsf 4 /g und 1/2 Jogurt level2 g 34 Seifen 138f. sfsde44";
		final String input = "bla " + focus + " blu";
		assertEquals(input, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testMeasureEL() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "2 EL";
		final String input = "bla " + focus + " blu";
		final String expected = "bla _" + focus + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testMeasureEL2() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "2.7'000 EL";
		final String input = "bla " + focus + " blu";
		final String expected = "bla _" + focus + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

 	@Test
	public void testMeasureTL() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "2 TL";
		final String input = "bla " + focus + " blu";
		final String expected = "bla _" + focus + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

 	@Test
	public void testMeasureDM() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "2,5 DM";
		final String input = "bla " + focus + " blu";
		final String expected = "bla _" + focus + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

    @Test
	public void testExponents() {

		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "45,00 km²/m³";
		final String input = "bla " + focus + " blu";
		final String expected = "bla _" + focus + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testExponents2() {

		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "34 km²";
		final String input = "bla " + focus + " blu";
		final String expected = "bla _" + focus + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testOhm() {

		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "25 kΩ";
		final String input = "bla " + focus + " blu";
		final String expected = "bla _" + focus + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testExcludeF() {

		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "138f.";
		final String input = "bla " + focus + " blu";
		assertEquals(input, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testMin() {

		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "1899 min";
		final String input = "bla " + focus + " blu";
		final String expected = "bla _" + focus + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testSec() {

		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "2,0 sec";
		final String input = "bla " + focus + " blu";
		final String expected = "bla _" + focus + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testSek() {

		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "9 sek";
		final String input = "bla " + focus + " blu";
		final String expected = "bla _" + focus + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testMmol() {

		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "5 mmol";
		final String input = "bla " + focus + " blu";
		final String expected = "bla _" + focus + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testKmol() {

		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "5 kmol";
		final String input = "bla " + focus + " blu";
		final String expected = "bla _" + focus + "_ blu";
		assertEquals(expected, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testDontMatch1253() {
		final Pattern pattern = Pattern.compile("("
				+ PrepToolLoader.MEASURE_SEARCH_REGEX + ")");
		final String focus = "4. September 1923 in Zürich geboren 4 34 dsdsfsf 4 /g und 1/2 Jogurt level2 g 34 Seifen 138f. sfsde44";
		final String input = "bla " + focus + " blu";
		assertEquals(input, pattern.matcher(input).replaceAll("_$1_"));
	}

	@Test
	public void testBug1256no() {
		final RegionSkipper theRegionSkipper = RegionSkipper
				.makeMarkupRegionSkipper(PrepToolLoader.MEASURE_SKIP_REGEX);
		final String focus = "<brl:num role=\"measure\">1 MW</brl:num>";
		final String input = "bla " + focus + " blu";
		theRegionSkipper.findRegionsToSkip(input);
		assertTrue(theRegionSkipper.inSkipRegion(makeMatcher("1 MW", input)));
		assertFalse(theRegionSkipper.inSkipRegion(makeMatcher("bla", input)));
	}

	@Test
	public void testBug1256() {
		final RegionSkipper theRegionSkipper = RegionSkipper
				.makeMarkupRegionSkipper(PrepToolLoader.MEASURE_SKIP_REGEX);
		final String focus = "<brl:num\nrole=\"measure\">1 MW</brl:num>";
		final String input = "bla " + focus + " blu";
		theRegionSkipper.findRegionsToSkip(input);
		assertTrue(theRegionSkipper.inSkipRegion(makeMatcher("1 MW", input)));
		assertFalse(theRegionSkipper.inSkipRegion(makeMatcher("bla", input)));
	}

	@Test
	public void testBug1270() {
		final RegionSkipper theRegionSkipper = RegionSkipper
				.makeMarkupRegionSkipper(PrepToolLoader.MEASURE_SKIP_REGEX);
		final String focus = "<brl:num\nrole=\"measure\">1 MW</brl:num>";
		final String input = "bla " + focus + " blu";
		theRegionSkipper.findRegionsToSkip(input);
		assertTrue(theRegionSkipper.inSkipRegion(makeMatcher("1 MW", input)));
		assertFalse(theRegionSkipper.inSkipRegion(makeMatcher("bla", input)));
	}

	private static Matcher makeMatcher(final String thePattern,
			final String theText) {
		final Matcher matcher = Pattern.compile(thePattern).matcher(theText);
		matcher.find();
		return matcher;
	}
}
