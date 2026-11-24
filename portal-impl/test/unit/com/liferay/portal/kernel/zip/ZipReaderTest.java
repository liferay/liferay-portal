/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.zip;

import com.liferay.petra.io.StreamUtil;
import com.liferay.portal.kernel.test.util.DependenciesTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.Charset;

import java.util.List;
import java.util.zip.ZipException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Manuel de la Peña
 */
public class ZipReaderTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());

		_expectedContent0 = StringUtil.read(
			DependenciesTestUtil.getDependencyAsInputStream(
				ZipReaderTest.class, _FILE_PATH_0));
		_expectedContent1 = StringUtil.read(
			DependenciesTestUtil.getDependencyAsInputStream(
				ZipReaderTest.class, _FILE_PATH_1));
		_expectedContent2 = StringUtil.read(
			DependenciesTestUtil.getDependencyAsInputStream(
				ZipReaderTest.class, _FILE_PATH_2));
		_expectedContent3 = StringUtil.read(
			DependenciesTestUtil.getDependencyAsInputStream(
				ZipReaderTest.class, _FILE_PATH_3));
	}

	@Before
	public void setUp() throws Exception {
		_zipReader = _getZipReader(
			DependenciesTestUtil.getDependencyAsInputStream(
				getClass(), _ZIP_FILE_PATH));
	}

	@After
	public void tearDown() {
		if (_zipReader != null) {
			_zipReader.close();
		}
	}

	@Test
	public void testCloseClosesEntryInputStream() throws Exception {
		ZipReader zipReader = _getZipReader(
			DependenciesTestUtil.getDependencyAsInputStream(
				getClass(), "file.zip"));

		InputStream inputStream = zipReader.getEntryAsInputStream("0.txt");

		zipReader.close();

		try {
			inputStream.read();

			Assert.fail();
		}
		catch (ZipException zipException) {
			Assert.assertEquals("ZipFile closed", zipException.getMessage());
		}
	}

	@Test
	public void testConstructor() throws Exception {
		ZipReader zipReader1 = _getZipReader(
			DependenciesTestUtil.getDependencyAsInputStream(
				getClass(), _ZIP_FILE_PATH));

		zipReader1.close();

		ZipReader zipReader2 = _getZipReader(
			DependenciesTestUtil.getDependencyAsFile(
				getClass(), _ZIP_FILE_PATH));

		zipReader2.close();
	}

	@Test
	public void testConstructorNullFile() {
		ZipReader zipReader = _getZipReader((File)null);

		zipReader.close();
	}

	@Test
	public void testConstructorNullInputStream() {
		Assert.assertThrows(
			"Input stream is null", NullPointerException.class,
			() -> _getZipReader((InputStream)null));
	}

	@Test
	public void testGetEntries() {
		List<String> entries = _zipReader.getEntries();

		Assert.assertEquals(entries.toString(), 5, entries.size());
		Assert.assertEquals(_FILE_PATH_0, entries.get(0));
		Assert.assertEquals(_FILE_PATH_1, entries.get(1));
		Assert.assertEquals(_FILE_PATH_2, entries.get(2));
		Assert.assertEquals(_FILE_PATH_3, entries.get(3));
		Assert.assertEquals(_FILE_PATH_4, entries.get(4));
	}

	@Test
	public void testGetEntryAsByteArray() {
		Assert.assertArrayEquals(
			_expectedContent0.getBytes(_UTF_8),
			_zipReader.getEntryAsByteArray(_FILE_PATH_0));
		Assert.assertArrayEquals(
			_expectedContent1.getBytes(_UTF_8),
			_zipReader.getEntryAsByteArray(_FILE_PATH_1));
		Assert.assertArrayEquals(
			_expectedContent2.getBytes(_UTF_8),
			_zipReader.getEntryAsByteArray(_FILE_PATH_2));
		Assert.assertArrayEquals(
			_expectedContent3.getBytes(_UTF_8),
			_zipReader.getEntryAsByteArray(_FILE_PATH_3));
	}

	@Test
	public void testGetEntryAsByteArrayThatDoesNotExist() {
		Assert.assertNull(_zipReader.getEntryAsByteArray("foo.txt"));
	}

	@Test
	public void testGetEntryAsByteArrayThatIsADirectory() {
		Assert.assertNull(_zipReader.getEntryAsByteArray("1"));
		Assert.assertNull(_zipReader.getEntryAsByteArray("/1"));
	}

	@Test
	public void testGetEntryAsByteArrayWithEmptyName() {
		Assert.assertNull(_zipReader.getEntryAsByteArray(""));
		Assert.assertNull(_zipReader.getEntryAsByteArray(null));
	}

	@Test
	public void testGetEntryAsInputStream() throws Exception {
		_testGetEntryAsInputStream(_expectedContent0, _FILE_PATH_0);
		_testGetEntryAsInputStream(_expectedContent1, _FILE_PATH_1);
		_testGetEntryAsInputStream(_expectedContent2, _FILE_PATH_2);
		_testGetEntryAsInputStream(_expectedContent3, _FILE_PATH_3);
	}

	@Test
	public void testGetEntryAsInputStreamThatDoesNotExist() {
		Assert.assertNull(_zipReader.getEntryAsInputStream("foo.txt"));
	}

	@Test
	public void testGetEntryAsInputStreamThatIsADirectory() {
		Assert.assertNull(_zipReader.getEntryAsInputStream("1"));
		Assert.assertNull(_zipReader.getEntryAsInputStream("/1"));
	}

	@Test
	public void testGetEntryAsInputStreamWithEmptyName() {
		Assert.assertNull(_zipReader.getEntryAsInputStream(""));
		Assert.assertNull(_zipReader.getEntryAsInputStream(null));
	}

	@Test
	public void testGetEntryAsString() {
		Assert.assertEquals(
			_expectedContent0, _zipReader.getEntryAsString("/" + _FILE_PATH_0));
		Assert.assertEquals(
			_expectedContent0, _zipReader.getEntryAsString(_FILE_PATH_0));
		Assert.assertEquals(
			_expectedContent1, _zipReader.getEntryAsString(_FILE_PATH_1));
		Assert.assertEquals(
			_expectedContent2, _zipReader.getEntryAsString(_FILE_PATH_2));
		Assert.assertEquals(
			_expectedContent3, _zipReader.getEntryAsString(_FILE_PATH_3));
	}

	@Test
	public void testGetEntryAsStringThatDoesNotExist() {
		Assert.assertNull(_zipReader.getEntryAsString("foo.txt"));
	}

	@Test
	public void testGetEntryAsStringThatIsADirectory() {
		Assert.assertNull(_zipReader.getEntryAsString("1"));
		Assert.assertNull(_zipReader.getEntryAsString("/1"));
	}

	@Test
	public void testGetEntryAsStringWithEmptyName() {
		Assert.assertNull(_zipReader.getEntryAsString(""));
		Assert.assertNull(_zipReader.getEntryAsString(null));
	}

	@Test
	public void testGetFolderEntries() {
		List<String> entries1 = _zipReader.getFolderEntries("");

		Assert.assertNotNull(entries1);
		Assert.assertTrue(entries1.toString(), entries1.isEmpty());

		List<String> entries2 = _zipReader.getFolderEntries("/");

		Assert.assertEquals(entries2.toString(), 1, entries2.size());
		Assert.assertEquals(_FILE_PATH_0, entries2.get(0));

		List<String> entries3 = _zipReader.getFolderEntries("1");

		Assert.assertEquals(entries3.toString(), 2, entries3.size());
		Assert.assertEquals(_FILE_PATH_1, entries3.get(0));
		Assert.assertEquals(_FILE_PATH_4, entries3.get(1));

		List<String> entries4 = _zipReader.getFolderEntries("1/2");

		Assert.assertEquals(entries4.toString(), 2, entries4.size());
		Assert.assertEquals(_FILE_PATH_2, entries4.get(0));
		Assert.assertEquals(_FILE_PATH_3, entries4.get(1));
	}

	@Test
	public void testGetFolderEntriesThatDoesNotExist() {
		List<String> entries = _zipReader.getFolderEntries("foo");

		Assert.assertNotNull(entries);
		Assert.assertTrue(entries.toString(), entries.isEmpty());
	}

	private ZipReader _getZipReader(File file) {
		return new ZipReader(file);
	}

	private ZipReader _getZipReader(InputStream inputStream)
		throws IOException {

		return new ZipReader(inputStream);
	}

	private void _testGetEntryAsInputStream(
			String expectedContent, String filePath)
		throws Exception {

		try (InputStream inputStream = _zipReader.getEntryAsInputStream(
				filePath)) {

			Assert.assertEquals(
				expectedContent, StreamUtil.toString(inputStream));
		}
	}

	private static final String _FILE_PATH_0 = "0.txt";

	private static final String _FILE_PATH_1 = "1/1.txt";

	private static final String _FILE_PATH_2 = "1/2/2.txt";

	private static final String _FILE_PATH_3 = "1/2/3.txt";

	private static final String _FILE_PATH_4 = "1/4.txt";

	private static final Charset _UTF_8 = Charset.forName("UTF-8");

	private static final String _ZIP_FILE_PATH = "file.zip";

	private static String _expectedContent0;
	private static String _expectedContent1;
	private static String _expectedContent2;
	private static String _expectedContent3;

	private ZipReader _zipReader;

}