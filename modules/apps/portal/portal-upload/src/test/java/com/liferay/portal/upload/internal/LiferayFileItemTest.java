/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Manuel de la Peña
 */
public class LiferayFileItemTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws IOException {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());

		Mockito.when(
			MimeTypesUtil.getContentType(
				Mockito.any(InputStream.class), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> {
				String fileName = invocationOnMock.getArgument(1, String.class);

				if (fileName.endsWith(".txt")) {
					return ContentTypes.TEXT_PLAIN;
				}

				throw new IOException();
			}
		);

		_tempDir = FileUtil.createTempFolder();

		_liferayFileItemFactory = new LiferayFileItemFactory(
			_tempDir, 0, "UTF-8");
	}

	@AfterClass
	public static void tearDownClass() {
		_mimeTypesUtilMockedStatic.close();
	}

	@Test
	public void testCreateItem() {
		String fieldName = RandomTestUtil.randomString();
		String fileName = RandomTestUtil.randomString();

		LiferayFileItem liferayFileItem = _liferayFileItemFactory.createItem(
			fieldName, RandomTestUtil.randomString(), false, fileName);

		Assert.assertEquals(fieldName, liferayFileItem.getFieldName());
		Assert.assertEquals(fileName, liferayFileItem.getFullFileName());
		Assert.assertFalse(liferayFileItem.isFormField());
	}

	@Test
	public void testDelete() throws Exception {
		LiferayFileItem liferayFileItem = _liferayFileItemFactory.createItem(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString() + ".txt");

		liferayFileItem.getOutputStream();

		File tempFile = liferayFileItem.getTempFile();

		FileUtil.write(tempFile, RandomTestUtil.randomString());

		Assert.assertTrue(tempFile.getAbsolutePath(), tempFile.exists());

		liferayFileItem.delete();

		Assert.assertFalse(tempFile.getAbsolutePath(), tempFile.exists());

		Assert.assertNotEquals(tempFile, liferayFileItem.getTempFile());
	}

	@Test
	public void testGetContentType() throws IOException {
		LiferayFileItem liferayFileItem = _liferayFileItemFactory.createItem(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString() + ".txt");

		Assert.assertNotNull(liferayFileItem);

		liferayFileItem.getOutputStream();

		Assert.assertEquals(
			ContentTypes.TEXT_PLAIN, liferayFileItem.getContentType());

		liferayFileItem = _liferayFileItemFactory.createItem(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString());

		Assert.assertNotNull(liferayFileItem);

		liferayFileItem.getOutputStream();

		Assert.assertEquals(
			ContentTypes.APPLICATION_OCTET_STREAM,
			liferayFileItem.getContentType());
	}

	@Test
	public void testGetFileNameExtension() {
		LiferayFileItem liferayFileItem = _liferayFileItemFactory.createItem(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString() + ".txt");

		Assert.assertEquals("txt", liferayFileItem.getFileNameExtension());
	}

	@Test
	public void testGetFileNameExtensionWithNullValue() {
		LiferayFileItem liferayFileItem = _liferayFileItemFactory.createItem(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			"theFile");

		Assert.assertEquals("", liferayFileItem.getFileNameExtension());
	}

	@Test
	public void testGetTempFileIgnoresFileNameExtension() {
		LiferayFileItem liferayFileItem = _liferayFileItemFactory.createItem(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			StringBundler.concat(
				RandomTestUtil.randomString(), StringPool.PERIOD,
				RandomTestUtil.randomString(), "/../../",
				RandomTestUtil.randomString()));

		File tempFile = liferayFileItem.getTempFile();

		Assert.assertEquals(_tempDir, tempFile.getParentFile());

		String name = tempFile.getName();

		Assert.assertTrue(name, name.startsWith("upload_"));
		Assert.assertTrue(name, name.endsWith(".tmp"));
	}

	@Test
	public void testSetStringRequiresCharacterEncoding() throws Exception {
		LiferayFileItem liferayFileItem = _liferayFileItemFactory.createItem(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString() + ".txt");

		liferayFileItem.getOutputStream();

		Assert.assertEquals("", liferayFileItem.getString());
	}

	@Test
	public void testWriteRequiresCallingGetOutputStream() throws Exception {
		LiferayFileItem liferayFileItem = _liferayFileItemFactory.createItem(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString() + ".txt");

		liferayFileItem.getOutputStream();

		liferayFileItem.write(FileUtil.createTempFile());

		Assert.assertEquals("", liferayFileItem.getString());
	}

	private static LiferayFileItemFactory _liferayFileItemFactory;
	private static final MockedStatic<MimeTypesUtil>
		_mimeTypesUtilMockedStatic = Mockito.mockStatic(MimeTypesUtil.class);
	private static File _tempDir;

}