/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.io;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ByteArrayFileInputStreamTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_testDir = new File("ByteArrayFileInputStreamTest.testDir");

		_testDir.mkdir();

		_testFile = new File("ByteArrayFileInputStreamTest.testFile");

		try (FileOutputStream fileOutputStream = new FileOutputStream(
				_testFile)) {

			for (int i = 0; i < 1024; i++) {
				fileOutputStream.write(i);
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		_testDir.delete();
		_testFile.delete();
	}

	@Test
	public void testBlockRead() throws IOException {

		// byte[]

		ByteArrayFileInputStream byteArrayFileInputStream =
			new ByteArrayFileInputStream(_testFile, 2048);

		byte[] buffer = new byte[17];

		int index = 0;
		int length = 0;

		while ((length = byteArrayFileInputStream.read(buffer)) != -1) {
			for (int i = 0; i < length; i++) {
				Assert.assertEquals(index++ & 0xff, buffer[i] & 0xff);
			}
		}

		byteArrayFileInputStream.close();

		byteArrayFileInputStream = new ByteArrayFileInputStream(
			_testFile, 2048);

		// 0 length

		Assert.assertEquals(0, byteArrayFileInputStream.read(null, -1, 0));

		buffer = new byte[48];

		index = 0;
		length = 0;

		while ((length = byteArrayFileInputStream.read(buffer, 16, 16)) != -1) {
			for (int i = 0; i < length; i++) {
				Assert.assertEquals(index++ & 0xff, buffer[i + 16] & 0xff);
			}
		}

		byteArrayFileInputStream.close();

		// FileInputStream

		byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);

		buffer = new byte[17];

		index = 0;
		length = 0;

		while ((length = byteArrayFileInputStream.read(buffer)) != -1) {
			for (int i = 0; i < length; i++) {
				Assert.assertEquals(index++ & 0xff, buffer[i] & 0xff);
			}
		}

		byteArrayFileInputStream.close();

		byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);

		// 0 length

		Assert.assertEquals(0, byteArrayFileInputStream.read(null, -1, 0));

		buffer = new byte[48];

		index = 0;
		length = 0;

		while ((length = byteArrayFileInputStream.read(buffer, 16, 16)) != -1) {
			for (int i = 0; i < length; i++) {
				Assert.assertEquals(index++ & 0xff, buffer[i + 16] & 0xff);
			}
		}

		byteArrayFileInputStream.close();
	}

	@Test
	public void testClose() throws Exception {

		// Do not delete on close

		ByteArrayFileInputStream byteArrayFileInputStream =
			new ByteArrayFileInputStream(_testFile, 512);

		byteArrayFileInputStream.read();

		byteArrayFileInputStream.close();

		Assert.assertNull(_dataField.get(byteArrayFileInputStream));
		Assert.assertNull(_fileField.get(byteArrayFileInputStream));
		Assert.assertNull(_fileInputStreamField.get(byteArrayFileInputStream));

		Assert.assertTrue(_testFile.exists());

		// Delete on close

		byteArrayFileInputStream = new ByteArrayFileInputStream(
			_testFile, 512, true);

		byteArrayFileInputStream.close();

		Assert.assertNull(_dataField.get(byteArrayFileInputStream));
		Assert.assertNull(_fileField.get(byteArrayFileInputStream));
		Assert.assertNull(_fileInputStreamField.get(byteArrayFileInputStream));

		Assert.assertFalse(_testFile.exists());

		byteArrayFileInputStream.close();
	}

	@Test
	public void testConstructor() throws Exception {

		// File is a dir

		try {
			new ByteArrayFileInputStream(_testDir, 1024);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}

		// File does not exist

		try {
			new ByteArrayFileInputStream(new File("No Such File"), 1024);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}

		// Constructor 1

		ByteArrayFileInputStream byteArrayFileInputStream =
			new ByteArrayFileInputStream(_testFile, 512);

		Assert.assertEquals(
			_testFile, _fileField.get(byteArrayFileInputStream));
		Assert.assertEquals(
			1024, _fileSizeField.getLong(byteArrayFileInputStream));
		Assert.assertEquals(
			512, _thresholdField.getInt(byteArrayFileInputStream));
		Assert.assertFalse(
			_deleteOnCloseField.getBoolean(byteArrayFileInputStream));

		// Constructor 2, do not delete on close

		byteArrayFileInputStream = new ByteArrayFileInputStream(
			_testFile, 512, false);

		Assert.assertEquals(
			_testFile, _fileField.get(byteArrayFileInputStream));
		Assert.assertEquals(
			1024, _fileSizeField.getLong(byteArrayFileInputStream));
		Assert.assertEquals(
			512, _thresholdField.getInt(byteArrayFileInputStream));
		Assert.assertFalse(
			_deleteOnCloseField.getBoolean(byteArrayFileInputStream));

		// Constructor 2, delete on close

		byteArrayFileInputStream = new ByteArrayFileInputStream(
			_testFile, 512, true);

		Assert.assertEquals(
			_testFile, _fileField.get(byteArrayFileInputStream));
		Assert.assertEquals(
			1024, _fileSizeField.getLong(byteArrayFileInputStream));
		Assert.assertEquals(
			512, _thresholdField.getInt(byteArrayFileInputStream));
		Assert.assertTrue(
			_deleteOnCloseField.getBoolean(byteArrayFileInputStream));
	}

	@Test
	public void testGetFile() throws Exception {
		ByteArrayFileInputStream byteArrayFileInputStream =
			new ByteArrayFileInputStream(_testFile, 512);

		Assert.assertSame(_testFile, byteArrayFileInputStream.getFile());

		byteArrayFileInputStream.close();
	}

	@Test
	public void testMark() throws IOException {

		// byte[]

		ByteArrayFileInputStream byteArrayFileInputStream =
			new ByteArrayFileInputStream(_testFile, 2048);

		Assert.assertTrue(byteArrayFileInputStream.markSupported());

		for (int i = 0; i < 512; i++) {
			Assert.assertEquals(i & 0xff, byteArrayFileInputStream.read());
		}

		byteArrayFileInputStream.mark(0);

		for (int i = 512; i < 1024; i++) {
			Assert.assertEquals(i & 0xff, byteArrayFileInputStream.read());
		}

		Assert.assertEquals(-1, byteArrayFileInputStream.read());

		// In memory reset to index 512

		byteArrayFileInputStream.reset();

		for (int i = 512; i < 1024; i++) {
			Assert.assertEquals(i & 0xff, byteArrayFileInputStream.read());
		}

		byteArrayFileInputStream.close();

		// FileInputStream

		byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);

		Assert.assertFalse(byteArrayFileInputStream.markSupported());

		for (int i = 0; i < 1024; i++) {
			Assert.assertEquals(i & 0xff, byteArrayFileInputStream.read());
		}

		Assert.assertEquals(-1, byteArrayFileInputStream.read());

		// FileInputStream reset to index 0

		byteArrayFileInputStream.reset();

		// Calling reset twice does not cause a NullPointerException

		byteArrayFileInputStream.reset();

		for (int i = 0; i < 1024; i++) {
			Assert.assertEquals(i & 0xff, byteArrayFileInputStream.read());
		}

		byteArrayFileInputStream.close();
	}

	@Test
	public void testSkip() throws IOException {

		// byte[]

		ByteArrayFileInputStream byteArrayFileInputStream =
			new ByteArrayFileInputStream(_testFile, 2048);

		// Negative length

		Assert.assertEquals(0, byteArrayFileInputStream.skip(-1));

		int count = 1024 / 17;

		for (int i = 0; i < count; i++) {
			Assert.assertEquals(17, byteArrayFileInputStream.skip(17));
		}

		Assert.assertEquals(1024 % 17, byteArrayFileInputStream.skip(17));

		Assert.assertEquals(0, byteArrayFileInputStream.skip(17));

		byteArrayFileInputStream.close();

		// FileInputStream

		byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);

		// 0 length

		Assert.assertEquals(0, byteArrayFileInputStream.skip(0));

		for (int i = 0; i < 1024; i++) {
			Assert.assertEquals(17, byteArrayFileInputStream.skip(17));
		}

		// Skip EOF

		byteArrayFileInputStream.skip(17);

		Assert.assertEquals(-1, byteArrayFileInputStream.read());

		byteArrayFileInputStream.close();
	}

	@Test
	public void testaAailable() throws Exception {

		// Uninitialized

		ByteArrayFileInputStream byteArrayFileInputStream =
			new ByteArrayFileInputStream(_testFile, 512);

		Assert.assertEquals(0, byteArrayFileInputStream.available());

		// byte[]

		byteArrayFileInputStream = new ByteArrayFileInputStream(
			_testFile, 2048);

		byteArrayFileInputStream.read();

		Assert.assertNotNull(_dataField.get(byteArrayFileInputStream));
		Assert.assertNull(_fileInputStreamField.get(byteArrayFileInputStream));
		Assert.assertEquals(1, _indexField.getInt(byteArrayFileInputStream));
		Assert.assertEquals(1023, byteArrayFileInputStream.available());

		byteArrayFileInputStream.close();

		// FileInputStream

		byteArrayFileInputStream = new ByteArrayFileInputStream(_testFile, 512);

		byteArrayFileInputStream.read();

		Assert.assertNull(_dataField.get(byteArrayFileInputStream));

		FileInputStream fileInputStream =
			(FileInputStream)_fileInputStreamField.get(
				byteArrayFileInputStream);

		Assert.assertNotNull(fileInputStream);

		Assert.assertEquals(0, _indexField.getInt(byteArrayFileInputStream));
		Assert.assertEquals(
			fileInputStream.available(), byteArrayFileInputStream.available());

		byteArrayFileInputStream.close();
	}

	private static final Field _dataField = ReflectionTestUtil.getField(
		ByteArrayFileInputStream.class, "_data");
	private static final Field _deleteOnCloseField =
		ReflectionTestUtil.getField(
			ByteArrayFileInputStream.class, "_deleteOnClose");
	private static final Field _fileField = ReflectionTestUtil.getField(
		ByteArrayFileInputStream.class, "_file");
	private static final Field _fileInputStreamField =
		ReflectionTestUtil.getField(
			ByteArrayFileInputStream.class, "_fileInputStream");
	private static final Field _fileSizeField = ReflectionTestUtil.getField(
		ByteArrayFileInputStream.class, "_fileSize");
	private static final Field _indexField = ReflectionTestUtil.getField(
		ByteArrayFileInputStream.class, "_index");
	private static final Field _thresholdField = ReflectionTestUtil.getField(
		ByteArrayFileInputStream.class, "_threshold");

	private File _testDir;
	private File _testFile;

}