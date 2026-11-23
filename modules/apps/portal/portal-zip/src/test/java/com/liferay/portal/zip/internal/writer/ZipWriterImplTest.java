/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.zip.internal.writer;

import com.liferay.portal.kernel.test.util.DependenciesTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.zip.internal.reader.ZipReaderImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Manuel de la Peña
 */
public class ZipWriterImplTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_expectedEntryContent = StringUtil.read(
			DependenciesTestUtil.getDependencyAsInputStream(
				ZipWriterImplTest.class, _ENTRY_FILE_PATH));
	}

	@Before
	public void setUp() throws IOException {
		Path tempZipFilePath = Files.createTempFile(
			Paths.get(SystemProperties.get(SystemProperties.TMP_DIR)), null,
			"-file.zip");

		Files.delete(tempZipFilePath);

		_tempZipFilePath = tempZipFilePath.toString();
	}

	@Test
	public void testAddEntryFromBytes() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(new File(_tempZipFilePath));

		zipWriter.addEntry(
			_ENTRY_FILE_PATH,
			FileUtil.getBytes(
				DependenciesTestUtil.getDependencyAsFile(
					getClass(), _ENTRY_FILE_PATH)));

		File file = zipWriter.getFile();

		Assert.assertTrue(file.exists());

		ZipReader zipReader = new ZipReaderImpl(file);

		Assert.assertEquals(
			_expectedEntryContent,
			zipReader.getEntryAsString(_ENTRY_FILE_PATH));

		zipReader.close();

		file.delete();
	}

	@Test
	public void testAddEntryFromBytesThatAreEmpty() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(new File(_tempZipFilePath));

		zipWriter.addEntry("empty.txt", new byte[0]);

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		Assert.assertEquals("", zipReader.getEntryAsString("empty.txt"));

		zipReader.close();

		file.delete();
	}

	@Test
	public void testAddEntryFromInputStream() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(new File(_tempZipFilePath));

		zipWriter.addEntry(
			_ENTRY_FILE_PATH,
			DependenciesTestUtil.getDependencyAsInputStream(
				getClass(), _ENTRY_FILE_PATH));

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		Assert.assertEquals(
			_expectedEntryContent,
			zipReader.getEntryAsString(_ENTRY_FILE_PATH));

		zipReader.close();

		file.delete();
	}

	@Test
	public void testAddEntryFromInputStreamThatIsNull() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(new File(_tempZipFilePath));

		zipWriter.addEntry("null.txt", (InputStream)null);

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		Assert.assertNull(zipReader.getEntryAsString("null.txt"));

		zipReader.close();

		file.delete();
	}

	@Test
	public void testAddEntryFromInputStreamThatStartsWithSlash()
		throws Exception {

		ZipWriter zipWriter = new ZipWriterImpl(new File(_tempZipFilePath));

		zipWriter.addEntry(
			"/" + _ENTRY_FILE_PATH,
			DependenciesTestUtil.getDependencyAsInputStream(
				getClass(), _ENTRY_FILE_PATH));

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		Assert.assertEquals(
			_expectedEntryContent,
			zipReader.getEntryAsString(_ENTRY_FILE_PATH));

		zipReader.close();

		file.delete();
	}

	@Test
	public void testAddEntryFromString() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(new File(_tempZipFilePath));

		zipWriter.addEntry("string.txt", "This is a string.");

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		Assert.assertEquals(
			"This is a string.", zipReader.getEntryAsString("string.txt"));

		zipReader.close();

		file.delete();
	}

	@Test
	public void testAddEntryFromStringBuilder() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(new File(_tempZipFilePath));

		StringBuilder sb = new StringBuilder();

		sb.append("This is a string.");

		zipWriter.addEntry("string.txt", sb);

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		Assert.assertEquals(
			"This is a string.", zipReader.getEntryAsString("string.txt"));

		zipReader.close();

		file.delete();
	}

	@Test
	public void testAddEntryFromStringBuilderThatIsEmpty() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(new File(_tempZipFilePath));

		zipWriter.addEntry("empty.txt", new StringBuilder());

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		Assert.assertEquals("", zipReader.getEntryAsString("empty.txt"));

		zipReader.close();

		file.delete();
	}

	@Test
	public void testAddEntryFromStringBuilderThatIsNull() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(new File(_tempZipFilePath));

		zipWriter.addEntry("null.txt", (StringBuilder)null);

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		Assert.assertNull(zipReader.getEntryAsString("null.txt"));

		zipReader.close();

		file.delete();
	}

	@Test
	public void testAddEntryFromStringThatIsEmpty() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(new File(_tempZipFilePath));

		zipWriter.addEntry("empty.txt", "");

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		Assert.assertEquals("", zipReader.getEntryAsString("empty.txt"));

		zipReader.close();

		file.delete();
	}

	@Test
	public void testAddEntryFromStringThatIsNull() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(new File(_tempZipFilePath));

		zipWriter.addEntry("null.txt", (String)null);

		File file = zipWriter.getFile();

		ZipReader zipReader = new ZipReaderImpl(file);

		Assert.assertNull(zipReader.getEntryAsString("null.txt"));

		zipReader.close();

		file.delete();
	}

	@Test
	public void testConstructor() {
		ZipWriter zipWriter1 = new ZipWriterImpl();

		File file1 = zipWriter1.getFile();

		Assert.assertNotNull(file1);

		file1.delete();

		File zipFile = new File(_tempZipFilePath);

		ZipWriter zipWriter2 = new ZipWriterImpl(zipFile);

		File file2 = zipWriter2.getFile();

		Assert.assertNotNull(file2);
		Assert.assertTrue(file2.exists());
		Assert.assertEquals(zipFile.getPath(), file2.getPath());

		file2.delete();
	}

	@Test
	public void testConstructorWithSpecialCharacters() throws IOException {
		Path path = Files.createTempDirectory("A B ");

		File zipFile = new File(path.toFile(), "C D .zip");

		ZipWriter zipWriter = new ZipWriterImpl(zipFile);

		File file = zipWriter.getFile();

		try {
			Assert.assertNotNull(file);
			Assert.assertTrue(file.exists());
			Assert.assertEquals(zipFile.getPath(), file.getPath());
		}
		finally {
			file.delete();

			Files.delete(path);
		}
	}

	@Test
	public void testFinish() throws Exception {
		File tempZipFile = new File(_tempZipFilePath);

		ZipWriter zipWriter = new ZipWriterImpl(tempZipFile);

		StringBuilder sb = new StringBuilder();

		sb.append("This is a string.");

		zipWriter.addEntry("string.txt", sb);

		byte[] bytes = zipWriter.finish();

		Assert.assertArrayEquals(FileUtil.getBytes(tempZipFile), bytes);

		File file = zipWriter.getFile();

		file.delete();
	}

	/**
	 * Tests that {@link ZipWriter#finish()} can execute without error on a ZIP
	 * writer that's been created by the default constructor and that has no
	 * entries.
	 *
	 * @throws Exception
	 */
	@Test
	public void testFinishIfZipFileIsNotSet() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl();

		zipWriter.finish();

		File file = zipWriter.getFile();

		file.delete();
	}

	/**
	 * Tests that {@link ZipWriter#finish()} can execute without error on a ZIP
	 * writer that's been created for an existing ZIP file and that has no
	 * entries.
	 *
	 * @throws Exception if an exception occurred
	 */
	@Test
	public void testFinishIfZipFileIsSet() throws Exception {
		ZipWriter zipWriter = new ZipWriterImpl(new File(_tempZipFilePath));

		zipWriter.finish();

		File file = zipWriter.getFile();

		file.delete();
	}

	private static final String _ENTRY_FILE_PATH = "entry.txt";

	private static String _expectedEntryContent;

	private String _tempZipFilePath;

}