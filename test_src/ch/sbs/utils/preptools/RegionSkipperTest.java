package ch.sbs.utils.preptools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class RegionSkipperTest {
	@Test
	public void testRegionSkipper() {
		final RegionSkipper literalSkipper = RegionSkipper.getLiteralSkipper();
		final String theText = "\nhallo\n<brl:literal>\ndu\n</brl:literal>\nhier\n";
		literalSkipper.findRegionsToSkip(theText);
		assertFalse(literalSkipper.inSkipRegion(makeMatcher("hallo", theText)));
		assertTrue(literalSkipper.inSkipRegion(makeMatcher("du", theText)));
	}

	private static Matcher makeMatcher(final String thePattern,
			final String theText) {
		final Matcher matcher = Pattern.compile(thePattern).matcher(theText);
		matcher.find();
		return matcher;
	}
}