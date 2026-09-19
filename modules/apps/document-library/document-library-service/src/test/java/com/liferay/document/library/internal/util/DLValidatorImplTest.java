/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.util;

import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.configuration.DLFileEntryMimeTypeConfiguration;
import com.liferay.document.library.internal.configuration.helper.DLSizeLimitConfigurationHelper;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileMimeTypeException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProvider;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class DLValidatorImplTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		DLValidatorImpl dlValidatorImpl = new DLValidatorImpl();

		dlValidatorImpl.setConfigurationProvider(_configurationProvider);
		dlValidatorImpl.setDLConfiguration(_dlConfiguration);
		dlValidatorImpl.setGroupLocalService(_groupLocalService);
		dlValidatorImpl.setUploadServletRequestConfigurationHelper(
			_uploadServletRequestConfigurationProvider);

		_dlValidator = dlValidatorImpl;

		ReflectionTestUtil.setFieldValue(
			dlValidatorImpl, "_dlSizeLimitConfigurationHelper",
			_dlSizeLimitConfigurationHelper);
	}

	@Test
	public void testCompanyMimeTypeSizeLimitTakesPrecedenceOverGroupMimeTypeSizeLimit()
		throws Exception {

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getCompanyMimeTypeSizeLimit(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			10L
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getGroupMimeTypeSizeLimit(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			15L
		);

		Assert.assertEquals(
			10,
			_dlValidator.getMaxAllowableSize(
				RandomTestUtil.randomInt(), "image/png"));
	}

	@Test
	public void testCompanyMimeTypeSizeLimitTakesPrecedenceOverSystemMimeTypeSizeLimit()
		throws Exception {

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getCompanyMimeTypeSizeLimit(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			5L
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getSystemMimeTypeSizeLimit(
				Mockito.anyString())
		).thenReturn(
			10L
		);

		Assert.assertEquals(
			5,
			_dlValidator.getMaxAllowableSize(
				RandomTestUtil.randomInt(), "image/png"));
	}

	@Test
	public void testGetMimeTypeSizeLimit() {
		Mockito.when(
			_dlSizeLimitConfigurationHelper.getCompanyMimeTypeSizeLimit(
				Mockito.anyLong())
		).thenReturn(
			Map.of("image/png", 10L, "text/plain", 50L)
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getGroupMimeTypeSizeLimit(
				Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			Map.of("image/png", 15L)
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getSystemMimeTypeSizeLimit()
		).thenReturn(
			Map.of("application/pdf", 100L, "image/png", 5L)
		);

		Map<String, Long> mimeTypeSizeLimit = _dlValidator.getMimeTypeSizeLimit(
			RandomTestUtil.randomInt());

		Assert.assertEquals(
			Long.valueOf(100L), mimeTypeSizeLimit.get("application/pdf"));
		Assert.assertEquals(
			Long.valueOf(5L), mimeTypeSizeLimit.get("image/png"));
		Assert.assertEquals(
			Long.valueOf(50L), mimeTypeSizeLimit.get("text/plain"));
	}

	@Test
	public void testGroupMimeTypeSizeLimitTakesPrecedenceOverCompanyMimeTypeSizeLimit()
		throws Exception {

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getCompanyMimeTypeSizeLimit(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			10L
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getGroupMimeTypeSizeLimit(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			5L
		);

		Assert.assertEquals(
			5,
			_dlValidator.getMaxAllowableSize(
				RandomTestUtil.randomInt(), "image/png"));
	}

	@Test
	public void testGroupMimeTypeSizeLimitTakesPrecedenceOverSystemMimeTypeSizeLimit()
		throws Exception {

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getGroupMimeTypeSizeLimit(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			5L
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getSystemMimeTypeSizeLimit(
				Mockito.anyString())
		).thenReturn(
			10L
		);

		Assert.assertEquals(
			5,
			_dlValidator.getMaxAllowableSize(
				RandomTestUtil.randomInt(), "image/png"));
	}

	@Test(expected = FileExtensionException.class)
	public void testInvalidExtension() throws Exception {
		_validateFileExtension("test.gıf");
	}

	@Test
	public void testMaxAllowableSizeDLFileMaxSizeTakesPrecedenceOverMimeTypeSizeLimit()
		throws Exception {

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getCompanyFileMaxSize(
				Mockito.anyLong())
		).thenReturn(
			10L
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getCompanyMimeTypeSizeLimit(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			15L
		);

		Assert.assertEquals(
			10,
			_dlValidator.getMaxAllowableSize(
				RandomTestUtil.randomInt(), "image/png"));
	}

	@Test
	public void testMaxAllowableSizeGroupMimeTypeSizeLimit() throws Exception {
		Mockito.when(
			_dlSizeLimitConfigurationHelper.getGroupMimeTypeSizeLimit(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			5L
		);

		Assert.assertEquals(
			5,
			_dlValidator.getMaxAllowableSize(
				RandomTestUtil.randomInt(), "image/png"));
	}

	@Test
	public void testMaxAllowableSizeMimeTypeSizeLimit() throws Exception {
		Mockito.when(
			_uploadServletRequestConfigurationProvider.getMaxSize()
		).thenReturn(
			15L
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getCompanyFileMaxSize(
				Mockito.anyLong())
		).thenReturn(
			10L
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getCompanyMimeTypeSizeLimit(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			5L
		);

		Assert.assertEquals(
			5,
			_dlValidator.getMaxAllowableSize(
				RandomTestUtil.randomInt(), "image/png"));
	}

	@Test
	public void testMaxAllowableSizeSystemMimeTypeSizeLimit() throws Exception {
		Mockito.when(
			_dlSizeLimitConfigurationHelper.getSystemMimeTypeSizeLimit(
				Mockito.anyString())
		).thenReturn(
			5L
		);

		Assert.assertEquals(
			5,
			_dlValidator.getMaxAllowableSize(
				RandomTestUtil.randomInt(), "image/png"));
	}

	@Test
	public void testMaxAllowableSizeUploadServletRequestFileMaxSizeTakesPrecedenceOverDLFileMaxSize()
		throws Exception {

		Mockito.when(
			_uploadServletRequestConfigurationProvider.getMaxSize()
		).thenReturn(
			10L
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getCompanyFileMaxSize(
				Mockito.anyLong())
		).thenReturn(
			15L
		);

		Assert.assertEquals(
			10,
			_dlValidator.getMaxAllowableSize(
				RandomTestUtil.randomInt(), RandomTestUtil.randomString()));
	}

	@Test
	public void testSystemFileMaxSizeTakesPrecedenceOverCompanyFileMaxSize()
		throws Exception {

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getCompanyFileMaxSize(
				Mockito.anyLong())
		).thenReturn(
			10L
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getGroupFileMaxSize(
				Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			15L
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getSystemFileMaxSize()
		).thenReturn(
			5L
		);

		Assert.assertEquals(
			5,
			_dlValidator.getMaxAllowableSize(
				RandomTestUtil.randomInt(), RandomTestUtil.randomString()));
	}

	@Test
	public void testSystemMimeTypeSizeLimitTakesPrecedenceOverCompanyMimeTypeSizeLimit()
		throws Exception {

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getCompanyMimeTypeSizeLimit(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			10L
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getGroupMimeTypeSizeLimit(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			15L
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getSystemMimeTypeSizeLimit(
				Mockito.anyString())
		).thenReturn(
			5L
		);

		Assert.assertEquals(
			5,
			_dlValidator.getMaxAllowableSize(
				RandomTestUtil.randomInt(), "image/png"));
	}

	@Test
	public void testSystemMimeTypeSizeLimitTakesPrecedenceOverGroupMimeTypeSizeLimit()
		throws Exception {

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getGroupMimeTypeSizeLimit(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			10L
		);

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getSystemMimeTypeSizeLimit(
				Mockito.anyString())
		).thenReturn(
			5L
		);

		Assert.assertEquals(
			5,
			_dlValidator.getMaxAllowableSize(
				RandomTestUtil.randomInt(), "image/png"));
	}

	@Test
	public void testValidLowerCaseExtension() throws Exception {
		_validateFileExtension("test.gif");
	}

	@Test
	public void testValidMixedCaseExtension() throws Exception {
		_validateFileExtension("test.GiF");
	}

	@Test
	public void testValidUpperCaseExtension() throws Exception {
		_validateFileExtension("test.GIF");
	}

	@Test(expected = FileMimeTypeException.class)
	public void testValidateFileMimeType() throws Exception {
		_validateFileMimeType(new String[] {"*"}, "text/plain");
		_validateFileMimeType(new String[] {"text/plain"}, "application/pdf");
		_validateFileMimeType(new String[] {"text/plain"}, "text/plain");
	}

	@Test
	public void testValidateFileSize() throws Exception {
		Mockito.when(
			_uploadServletRequestConfigurationProvider.getMaxSize()
		).thenReturn(
			40L
		);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			_dlSizeLimitConfigurationHelper.getGroupMimeTypeSizeLimit(
				CompanyThreadLocal.getCompanyId(), groupId, "image/png")
		).thenReturn(
			10L
		);

		String fileName = RandomTestUtil.randomString();

		_dlValidator.validateFileSize(groupId, fileName, "image/png", 10L);

		try {
			_dlValidator.validateFileSize(groupId, fileName, "image/png", 20L);

			Assert.fail();
		}
		catch (FileSizeException fileSizeException) {
			Assert.assertEquals(10L, fileSizeException.getMaxSize());
			Assert.assertEquals(
				"20 exceeds the mime type \"image/png\" maximum permitted " +
					"size of 10 for file " + fileName,
				fileSizeException.getMessage());
			Assert.assertEquals("image/png", fileSizeException.getMimeType());
		}

		String mimeType = RandomTestUtil.randomString();

		_dlValidator.validateFileSize(groupId, fileName, mimeType, 20L);

		try {
			_dlValidator.validateFileSize(groupId, fileName, mimeType, 50L);

			Assert.fail();
		}
		catch (FileSizeException fileSizeException) {
			Assert.assertEquals(40L, fileSizeException.getMaxSize());
			Assert.assertEquals(
				"50 exceeds the global maximum permitted size of 40 for file " +
					fileName,
				fileSizeException.getMessage());
			Assert.assertNull(fileSizeException.getMimeType());
		}
	}

	private void _validateFileExtension(String fileName) throws Exception {
		Mockito.when(
			_dlConfiguration.fileExtensions()
		).thenReturn(
			new String[] {".gif"}
		);

		_dlValidator.validateFileExtension(fileName);

		Mockito.when(
			_dlConfiguration.fileExtensions()
		).thenReturn(
			new String[] {"gif"}
		);

		_dlValidator.validateFileExtension(fileName);
	}

	private void _validateFileMimeType(
			String[] allowedMimeTypes, String mimeType)
		throws Exception {

		DLFileEntryMimeTypeConfiguration dlFileEntryMimeTypeConfiguration =
			Mockito.mock(DLFileEntryMimeTypeConfiguration.class);

		Mockito.when(
			_configurationProvider.getCompanyConfiguration(
				DLFileEntryMimeTypeConfiguration.class, 123456L)
		).thenReturn(
			dlFileEntryMimeTypeConfiguration
		);

		Mockito.when(
			dlFileEntryMimeTypeConfiguration.fileMimeTypes()
		).thenReturn(
			allowedMimeTypes
		);

		_dlValidator.validateFileMimeType(123456L, mimeType);
	}

	private final ConfigurationProvider _configurationProvider = Mockito.mock(
		ConfigurationProvider.class);
	private final DLConfiguration _dlConfiguration = Mockito.mock(
		DLConfiguration.class);
	private final DLSizeLimitConfigurationHelper
		_dlSizeLimitConfigurationHelper = Mockito.mock(
			DLSizeLimitConfigurationHelper.class);
	private DLValidator _dlValidator;
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final UploadServletRequestConfigurationProvider
		_uploadServletRequestConfigurationProvider = Mockito.mock(
			UploadServletRequestConfigurationProvider.class);

}