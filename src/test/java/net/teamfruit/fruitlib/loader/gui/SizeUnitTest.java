package net.teamfruit.fruitlib.loader.gui;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.teamfruit.fruitlib.loader.Log;

public class SizeUnitTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() {
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 0), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 1), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 2), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 3), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 4), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 5), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 6), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 7), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 8), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 9), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 10), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 11), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 12), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 13), 1));
		Log.log.info(SizeUnit.SPEED.getFormatSizeString(Math.pow(1024, 14), 1));
	}
}
