/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.tools.ToolDependencies;
import com.liferay.portal.upload.ServletFileUpload;
import com.liferay.portal.upload.UploadServletRequestImpl;
import com.liferay.portal.util.FastDateFormatFactoryImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Roberto Díaz
 */
public class UploadServletRequestImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ToolDependencies.wireCaches();

		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());

		_fileItems.add(_getFileItem("A", 12));
		_fileItems.add(_getFileItem("B", 92));
		_fileItems.add(_getFileItem("F", 80));
		_fileItems.add(_getFileItem("FIRST_ELEMENT_FIELD_NAME", 1));
		_fileItems.add(_getFileItem("G", 80));
		_fileItems.add(_getFileItem("LAST_ELEMENT_FIELD_NAME", 999));
		_fileItems.add(_getFileItem("REPEATED_ELEMENT_FIELD_NAME", 2));
		_fileItems.add(_getFileItem("REPEATED_ELEMENT_FIELD_NAME", 1));
		_fileItems.add(_getFileItem("S", 65));
		_fileItems.add(_getFileItem("T", 34));

		_httpServletRequest = Mockito.mock(HttpServletRequest.class);

		Mockito.when(
			_httpServletRequest.getSession()
		).thenReturn(
			Mockito.mock(HttpSession.class)
		);

		_tempDir = FileUtil.createTempFolder();
	}

	@After
	public void tearDown() {
		FileUtil.deltree(_tempDir);
	}

	@Test
	public void testDeletesFileItemExceedingMaximumUploadSize()
		throws Exception {

		LiferayFileItem liferayFileItem = _createLiferayFileItem(false, _VALUE);

		File tempFile = liferayFileItem.getTempFile();

		Assert.assertTrue(tempFile.getAbsolutePath(), tempFile.exists());

		UploadServletRequestImpl uploadServletRequestImpl =
			_createUploadServletRequestImpl(1, liferayFileItem);

		Mockito.verify(
			_httpServletRequest
		).setAttribute(
			Mockito.eq(WebKeys.UPLOAD_EXCEPTION),
			Mockito.any(UploadException.class)
		);

		Map<String, FileItem[]> multipartParameterMap =
			uploadServletRequestImpl.getMultipartParameterMap();

		Assert.assertTrue(
			multipartParameterMap.toString(), multipartParameterMap.isEmpty());

		_assertTempDirIsEmpty();
	}

	@Test
	public void testDeletesFormFieldFileItemAfterReadingItsValue()
		throws Exception {

		LiferayFileItem liferayFileItem = _createLiferayFileItem(true, _VALUE);

		File tempFile = liferayFileItem.getTempFile();

		Assert.assertTrue(tempFile.getAbsolutePath(), tempFile.exists());

		UploadServletRequestImpl uploadServletRequestImpl =
			_createUploadServletRequestImpl(_MAX_SIZE, liferayFileItem);

		Assert.assertEquals(
			_VALUE, uploadServletRequestImpl.getParameter(_FIELD_NAME));

		_assertTempDirIsEmpty();
	}

	@Test
	public void testSort() {
		List<FileItem> sortedFileItems = ReflectionTestUtil.invoke(
			new UploadServletRequestImpl(
				ProxyFactory.newDummyInstance(HttpServletRequest.class), null,
				null),
			"_sort", new Class<?>[] {List.class}, _fileItems);

		Assert.assertEquals(
			sortedFileItems.toString(), 10, sortedFileItems.size());

		String previousFieldName = StringPool.BLANK;
		long previousSize = 0;

		for (FileItem sortedFileItem : sortedFileItems) {
			String fieldName = sortedFileItem.getFieldName();
			long size = sortedFileItem.getSize();

			if (!previousFieldName.equals(fieldName)) {
				Assert.assertTrue(previousSize <= size);
			}

			previousFieldName = fieldName;
			previousSize = size;
		}
	}

	@Test
	public void testSortKeepsOriginalOrderWithSameParameterName() {
		List<FileItem> sortedFileItems = ReflectionTestUtil.invoke(
			new UploadServletRequestImpl(
				ProxyFactory.newDummyInstance(HttpServletRequest.class), null,
				null),
			"_sort", new Class<?>[] {List.class}, _fileItems);

		FileItem fileItem1 = sortedFileItems.get(1);

		Assert.assertEquals(
			"REPEATED_ELEMENT_FIELD_NAME", fileItem1.getFieldName());
		Assert.assertEquals(2, fileItem1.getSize());

		FileItem fileItem2 = sortedFileItems.get(2);

		Assert.assertEquals(
			"REPEATED_ELEMENT_FIELD_NAME", fileItem2.getFieldName());
		Assert.assertEquals(1, fileItem2.getSize());
	}

	private void _assertTempDirIsEmpty() {
		String[] fileNames = _tempDir.list();

		Assert.assertEquals(Arrays.toString(fileNames), 0, fileNames.length);
	}

	private LiferayFileItem _createLiferayFileItem(
			boolean formField, String value)
		throws Exception {

		LiferayFileItem liferayFileItem = new LiferayFileItem(
			_FIELD_NAME, ContentTypes.TEXT_PLAIN, formField, "test.txt", 1,
			_tempDir, "UTF-8");

		try (OutputStream outputStream = liferayFileItem.getOutputStream()) {
			outputStream.write(value.getBytes());
		}

		return liferayFileItem;
	}

	private UploadServletRequestImpl _createUploadServletRequestImpl(
			long maxSize, FileItem... fileItems)
		throws Exception {

		Snapshot<ServletFileUpload> snapshot = new Snapshot<>(
			UploadServletRequestImpl.class, ServletFileUpload.class);

		ServletFileUpload servletFileUpload =
			(httpServletRequest, location, fileSizeThreshold) -> Arrays.asList(
				fileItems);

		ReflectionTestUtil.setFieldValue(
			snapshot, "_serviceSupplier",
			(Supplier<ServletFileUpload>)() -> servletFileUpload);

		try (MockedStatic<UploadServletRequestConfigurationProviderUtil>
				mockedStatic = Mockito.mockStatic(
					UploadServletRequestConfigurationProviderUtil.class);
			AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					UploadServletRequestImpl.class,
					"_servletFileUploadSnapshot", snapshot)) {

			mockedStatic.when(
				UploadServletRequestConfigurationProviderUtil::
					getMaxSizeWithPadding
			).thenReturn(
				maxSize
			);

			return new UploadServletRequestImpl(_httpServletRequest, 0, null);
		}
	}

	private FileItem _getFileItem(String fieldName, long size) {
		FileItem fileItem = new LiferayFileItem(
			fieldName, null, false, null, 0, null, null);

		ReflectionTestUtil.setFieldValue(fileItem, "size", size);

		return fileItem;
	}

	private static final String _FIELD_NAME = "file";

	private static final long _MAX_SIZE = 1024;

	private static final String _VALUE = "Enterprise. Open Source. For Life.";

	private final List<FileItem> _fileItems = new ArrayList<>();
	private HttpServletRequest _httpServletRequest;
	private File _tempDir;

}