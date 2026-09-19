/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.processor;

import com.liferay.adaptive.media.exception.AMRuntimeException;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.exception.DuplicateAMImageEntryException;
import com.liferay.adaptive.media.image.internal.configuration.AMImageConfigurationEntryImpl;
import com.liferay.adaptive.media.image.internal.scaler.AMImageScaledImageImpl;
import com.liferay.adaptive.media.image.model.AMImageEntry;
import com.liferay.adaptive.media.image.scaler.AMImageScaler;
import com.liferay.adaptive.media.image.scaler.AMImageScalerRegistry;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.adaptive.media.image.validator.AMImageValidator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.InputStream;

import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class AMImageProcessorImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_amImageAMProcessor, "_amImageConfigurationHelper",
			_amImageConfigurationHelper);
		ReflectionTestUtil.setFieldValue(
			_amImageAMProcessor, "_amImageEntryLocalService",
			_amImageEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_amImageAMProcessor, "_amImageScalerRegistry",
			_amImageScalerRegistry);
		ReflectionTestUtil.setFieldValue(
			_amImageAMProcessor, "_amImageValidator", _amImageValidator);
	}

	@Test
	public void testCleanUpFileVersion() throws Exception {
		Mockito.when(
			_amImageValidator.isValid(Mockito.any(FileVersion.class))
		).thenReturn(
			true
		);

		_amImageAMProcessor.cleanUp(_fileVersion);

		Mockito.verify(
			_amImageEntryLocalService
		).deleteAMImageEntryFileVersion(
			_fileVersion
		);
	}

	@Test(expected = AMRuntimeException.IOException.class)
	public void testCleanUpIOException() throws Exception {
		Mockito.when(
			_amImageValidator.isValid(Mockito.any(FileVersion.class))
		).thenReturn(
			true
		);

		Mockito.doThrow(
			AMRuntimeException.IOException.class
		).when(
			_amImageEntryLocalService
		).deleteAMImageEntryFileVersion(
			Mockito.any(FileVersion.class)
		);

		_amImageAMProcessor.cleanUp(_fileVersion);
	}

	@Test(expected = PortalException.class)
	public void testCleanUpPortalException() throws Exception {
		Mockito.when(
			_amImageValidator.isValid(Mockito.any(FileVersion.class))
		).thenReturn(
			true
		);

		Mockito.doThrow(
			PortalException.class
		).when(
			_amImageEntryLocalService
		).deleteAMImageEntryFileVersion(
			Mockito.any(FileVersion.class)
		);

		_amImageAMProcessor.cleanUp(_fileVersion);
	}

	@Test
	public void testCleanUpWhenNotSupported() throws Exception {
		Mockito.when(
			_amImageValidator.isValid(Mockito.any(FileVersion.class))
		).thenReturn(
			false
		);

		_amImageAMProcessor.cleanUp(_fileVersion);

		Mockito.verify(
			_amImageEntryLocalService, Mockito.never()
		).deleteAMImageEntryFileVersion(
			_fileVersion
		);
	}

	@Test
	public void testProcessConfigurationWhenAMImageEntryAlreadyExists()
		throws Exception {

		Mockito.when(
			_amImageValidator.isProcessingSupported(_fileVersion)
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.nullable(String.class))
		).thenReturn(
			new AMImageConfigurationEntryImpl(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				Collections.emptyMap())
		);

		Mockito.when(
			_amImageEntryLocalService.fetchAMImageEntry(
				Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			_amImageEntry
		);

		Mockito.when(
			_fileVersion.getFileEntry()
		).thenReturn(
			_fileEntry
		);

		Date creationDate = new Date();

		Date modifiedDate = new Date(
			creationDate.getYear(), creationDate.getMonth(),
			creationDate.getDate() - 1);

		Mockito.when(
			_amImageEntry.getCreateDate()
		).thenReturn(
			creationDate
		);

		Mockito.when(
			_fileVersion.getModifiedDate()
		).thenReturn(
			modifiedDate
		);

		Mockito.when(
			_fileEntry.isCheckedOut()
		).thenReturn(
			false
		);

		_amImageAMProcessor.process(
			_fileVersion, RandomTestUtil.randomString());

		Mockito.verify(
			_amImageScaler, Mockito.never()
		).scaleImage(
			Mockito.any(FileVersion.class),
			Mockito.any(AMImageConfigurationEntry.class)
		);
	}

	@Test
	public void testProcessConfigurationWhenAMImageEntryAlreadyExistsAndShouldBeUpdated()
		throws Exception {

		Mockito.when(
			_amImageValidator.isProcessingSupported(_fileVersion)
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.nullable(String.class))
		).thenReturn(
			new AMImageConfigurationEntryImpl(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				Collections.emptyMap())
		);

		Mockito.when(
			_amImageEntryLocalService.fetchAMImageEntry(
				Mockito.nullable(String.class), Mockito.anyLong())
		).thenReturn(
			_amImageEntry
		);

		Mockito.when(
			_fileVersion.getFileEntry()
		).thenReturn(
			_fileEntry
		);

		Date creationDate = new Date();

		Date modifiedDate = new Date(
			creationDate.getYear(), creationDate.getMonth(),
			creationDate.getDate() + 1);

		Mockito.when(
			_amImageEntry.getCreateDate()
		).thenReturn(
			creationDate
		);

		Mockito.when(
			_fileVersion.getModifiedDate()
		).thenReturn(
			modifiedDate
		);

		Mockito.when(
			_fileEntry.isCheckedOut()
		).thenReturn(
			false
		);

		Mockito.when(
			_amImageScalerRegistry.getAMImageScaler(
				Mockito.nullable(String.class))
		).thenReturn(
			_amImageScaler
		);

		Mockito.when(
			_amImageScaler.scaleImage(
				Mockito.any(FileVersion.class),
				Mockito.any(AMImageConfigurationEntry.class))
		).thenReturn(
			new AMImageScaledImageImpl(new byte[100], 100, null, 100)
		);

		_amImageAMProcessor.process(
			_fileVersion, RandomTestUtil.randomString());

		Mockito.verify(
			_amImageEntryLocalService
		).deleteAMImageEntry(
			Mockito.anyLong()
		);

		Mockito.verify(
			_amImageScaler
		).scaleImage(
			Mockito.any(FileVersion.class),
			Mockito.any(AMImageConfigurationEntry.class)
		);
	}

	@Test
	public void testProcessConfigurationWhenFileEntryIsCheckedOut()
		throws Exception {

		Mockito.when(
			_amImageValidator.isProcessingSupported(_fileVersion)
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.nullable(String.class))
		).thenReturn(
			new AMImageConfigurationEntryImpl(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				Collections.emptyMap())
		);

		Mockito.when(
			_amImageEntryLocalService.fetchAMImageEntry(
				Mockito.nullable(String.class), Mockito.anyLong())
		).thenReturn(
			Mockito.mock(AMImageEntry.class)
		);

		Mockito.when(
			_fileVersion.getFileEntry()
		).thenReturn(
			_fileEntry
		);

		Mockito.when(
			_fileEntry.isCheckedOut()
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageScalerRegistry.getAMImageScaler(
				Mockito.nullable(String.class))
		).thenReturn(
			_amImageScaler
		);

		Mockito.when(
			_amImageScaler.scaleImage(
				Mockito.any(FileVersion.class),
				Mockito.any(AMImageConfigurationEntry.class))
		).thenReturn(
			new AMImageScaledImageImpl(new byte[100], 100, null, 100)
		);

		_amImageAMProcessor.process(
			_fileVersion, RandomTestUtil.randomString());

		Mockito.verify(
			_amImageEntryLocalService
		).deleteAMImageEntry(
			Mockito.anyLong()
		);

		Mockito.verify(
			_amImageScaler
		).scaleImage(
			Mockito.any(FileVersion.class),
			Mockito.any(AMImageConfigurationEntry.class)
		);
	}

	@Test
	public void testProcessConfigurationWhenNoAMImageScalerAvailable()
		throws Exception {

		Mockito.when(
			_amImageValidator.isProcessingSupported(_fileVersion)
		).thenReturn(
			true
		);

		AMImageConfigurationEntry amImageConfigurationEntry =
			new AMImageConfigurationEntryImpl(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				Collections.emptyMap());

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				Mockito.anyLong())
		).thenReturn(
			Collections.singleton(amImageConfigurationEntry)
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.nullable(String.class))
		).thenReturn(
			amImageConfigurationEntry
		);

		Mockito.when(
			_amImageScalerRegistry.getAMImageScaler(
				Mockito.nullable(String.class))
		).thenReturn(
			null
		);

		_amImageAMProcessor.process(
			_fileVersion, RandomTestUtil.randomString());

		Mockito.verify(
			_amImageEntryLocalService, Mockito.never()
		).addAMImageEntry(
			Mockito.any(AMImageConfigurationEntry.class),
			Mockito.any(FileVersion.class), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.any(InputStream.class), Mockito.anyLong()
		);
	}

	@Test
	public void testProcessConfigurationWhenNoConfigurationEntry()
		throws Exception {

		Mockito.when(
			_amImageValidator.isProcessingSupported(_fileVersion)
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.nullable(String.class))
		).thenReturn(
			null
		);

		_amImageAMProcessor.process(
			_fileVersion, RandomTestUtil.randomString());

		Mockito.verify(
			_amImageEntryLocalService, Mockito.never()
		).fetchAMImageEntry(
			Mockito.nullable(String.class), Mockito.anyLong()
		);
	}

	@Test
	public void testProcessConfigurationWhenNotSupported() throws Exception {
		Mockito.when(
			_amImageValidator.isProcessingSupported(
				Mockito.any(FileVersion.class))
		).thenReturn(
			false
		);

		_amImageAMProcessor.process(
			_fileVersion, RandomTestUtil.randomString());

		Mockito.verify(
			_amImageConfigurationHelper, Mockito.never()
		).getAMImageConfigurationEntry(
			Mockito.anyLong(), Mockito.nullable(String.class)
		);
	}

	@Test
	public void testProcessDoesNotDeleteAMImageEntryWhenScaleFails()
		throws Exception {

		Mockito.when(
			_amImageValidator.isProcessingSupported(_fileVersion)
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.nullable(String.class))
		).thenReturn(
			new AMImageConfigurationEntryImpl(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				Collections.emptyMap())
		);

		Mockito.when(
			_amImageEntryLocalService.fetchAMImageEntry(
				Mockito.nullable(String.class), Mockito.anyLong())
		).thenReturn(
			_amImageEntry
		);

		Mockito.when(
			_amImageScalerRegistry.getAMImageScaler(
				Mockito.nullable(String.class))
		).thenReturn(
			_amImageScaler
		);

		Mockito.doThrow(
			AMRuntimeException.IOException.class
		).when(
			_amImageScaler
		).scaleImage(
			Mockito.any(FileVersion.class),
			Mockito.any(AMImageConfigurationEntry.class)
		);

		try {
			_amImageAMProcessor.process(
				_fileVersion, RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (AMRuntimeException.IOException ioException) {
		}

		Mockito.verify(
			_amImageEntryLocalService, Mockito.never()
		).deleteAMImageEntry(
			Mockito.anyLong()
		);

		Mockito.verify(
			_amImageEntryLocalService, Mockito.never()
		).addAMImageEntry(
			Mockito.any(AMImageConfigurationEntry.class),
			Mockito.any(FileVersion.class), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.any(InputStream.class), Mockito.anyLong()
		);
	}

	@Test(expected = DuplicateAMImageEntryException.class)
	public void testProcessDuplicateAMImageEntryExceptionInImageService()
		throws Exception {

		Mockito.when(
			_amImageValidator.isProcessingSupported(_fileVersion)
		).thenReturn(
			true
		);

		AMImageConfigurationEntry amImageConfigurationEntry =
			new AMImageConfigurationEntryImpl(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				Collections.emptyMap());

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				Mockito.anyLong())
		).thenReturn(
			Collections.singleton(amImageConfigurationEntry)
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.nullable(String.class))
		).thenReturn(
			amImageConfigurationEntry
		);

		Mockito.when(
			_amImageScalerRegistry.getAMImageScaler(
				Mockito.nullable(String.class))
		).thenReturn(
			_amImageScaler
		);

		Mockito.when(
			_amImageScaler.scaleImage(_fileVersion, amImageConfigurationEntry)
		).thenReturn(
			new AMImageScaledImageImpl(new byte[100], 150, null, 200)
		);

		Mockito.doThrow(
			DuplicateAMImageEntryException.class
		).when(
			_amImageEntryLocalService
		).addAMImageEntry(
			Mockito.any(AMImageConfigurationEntry.class),
			Mockito.any(FileVersion.class), Mockito.eq(150), Mockito.eq(200),
			Mockito.any(InputStream.class), Mockito.eq(100L)
		);

		_amImageAMProcessor.process(_fileVersion);
	}

	@Test
	public void testProcessFileVersion() throws Exception {
		Mockito.when(
			_amImageValidator.isProcessingSupported(_fileVersion)
		).thenReturn(
			true
		);

		AMImageConfigurationEntry amImageConfigurationEntry =
			new AMImageConfigurationEntryImpl(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				Collections.emptyMap());

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				Mockito.anyLong())
		).thenReturn(
			Collections.singleton(amImageConfigurationEntry)
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.nullable(String.class))
		).thenReturn(
			amImageConfigurationEntry
		);

		Mockito.when(
			_amImageScalerRegistry.getAMImageScaler(
				Mockito.nullable(String.class))
		).thenReturn(
			_amImageScaler
		);

		Mockito.when(
			_amImageScaler.scaleImage(_fileVersion, amImageConfigurationEntry)
		).thenReturn(
			new AMImageScaledImageImpl(new byte[100], 150, null, 200)
		);

		_amImageAMProcessor.process(_fileVersion);

		Mockito.verify(
			_amImageScaler
		).scaleImage(
			_fileVersion, amImageConfigurationEntry
		);

		Mockito.verify(
			_amImageEntryLocalService
		).addAMImageEntry(
			Mockito.any(AMImageConfigurationEntry.class),
			Mockito.any(FileVersion.class), Mockito.eq(150), Mockito.eq(200),
			Mockito.any(InputStream.class), Mockito.eq(100L)
		);
	}

	@Test(expected = AMRuntimeException.IOException.class)
	public void testProcessIOExceptionInImageProcessor() throws Exception {
		Mockito.when(
			_amImageValidator.isProcessingSupported(_fileVersion)
		).thenReturn(
			true
		);

		AMImageConfigurationEntry amImageConfigurationEntry =
			new AMImageConfigurationEntryImpl(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				Collections.emptyMap());

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				Mockito.anyLong())
		).thenReturn(
			Collections.singleton(amImageConfigurationEntry)
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.nullable(String.class))
		).thenReturn(
			amImageConfigurationEntry
		);

		Mockito.when(
			_amImageScalerRegistry.getAMImageScaler(
				Mockito.nullable(String.class))
		).thenReturn(
			_amImageScaler
		);

		Mockito.doThrow(
			AMRuntimeException.IOException.class
		).when(
			_amImageScaler
		).scaleImage(
			_fileVersion, amImageConfigurationEntry
		);

		_amImageAMProcessor.process(_fileVersion);
	}

	@Test(expected = AMRuntimeException.IOException.class)
	public void testProcessIOExceptionInStorage() throws Exception {
		Mockito.when(
			_amImageValidator.isProcessingSupported(_fileVersion)
		).thenReturn(
			true
		);

		AMImageConfigurationEntry amImageConfigurationEntry =
			new AMImageConfigurationEntryImpl(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				Collections.emptyMap());

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				Mockito.anyLong())
		).thenReturn(
			Collections.singleton(amImageConfigurationEntry)
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.nullable(String.class))
		).thenReturn(
			amImageConfigurationEntry
		);

		Mockito.when(
			_amImageScalerRegistry.getAMImageScaler(
				Mockito.nullable(String.class))
		).thenReturn(
			_amImageScaler
		);

		Mockito.when(
			_amImageScaler.scaleImage(_fileVersion, amImageConfigurationEntry)
		).thenReturn(
			new AMImageScaledImageImpl(new byte[100], 150, null, 200)
		);

		Mockito.doThrow(
			AMRuntimeException.IOException.class
		).when(
			_amImageEntryLocalService
		).addAMImageEntry(
			Mockito.any(AMImageConfigurationEntry.class),
			Mockito.any(FileVersion.class), Mockito.eq(150), Mockito.eq(200),
			Mockito.any(InputStream.class), Mockito.eq(100L)
		);

		_amImageAMProcessor.process(_fileVersion);
	}

	@Test(expected = AMRuntimeException.InvalidConfiguration.class)
	public void testProcessInvalidConfigurationException() throws Exception {
		Mockito.when(
			_amImageValidator.isProcessingSupported(_fileVersion)
		).thenReturn(
			true
		);

		Mockito.doThrow(
			AMRuntimeException.InvalidConfiguration.class
		).when(
			_amImageConfigurationHelper
		).getAMImageConfigurationEntries(
			Mockito.anyLong()
		);

		_amImageAMProcessor.process(_fileVersion);
	}

	@Test
	public void testProcessWhenNoConfigurationEntries() throws Exception {
		Mockito.when(
			_amImageValidator.isProcessingSupported(
				Mockito.any(FileVersion.class))
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				Mockito.anyLong())
		).thenReturn(
			Collections.emptyList()
		);

		_amImageAMProcessor.process(_fileVersion);

		Mockito.verify(
			_amImageScaler, Mockito.never()
		).scaleImage(
			Mockito.any(FileVersion.class),
			Mockito.any(AMImageConfigurationEntry.class)
		);

		Mockito.verify(
			_amImageEntryLocalService, Mockito.never()
		).addAMImageEntry(
			Mockito.any(AMImageConfigurationEntry.class),
			Mockito.any(FileVersion.class), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.any(InputStream.class), Mockito.anyLong()
		);
	}

	@Test
	public void testProcessWhenNotSupported() throws Exception {
		Mockito.when(
			_amImageValidator.isProcessingSupported(
				Mockito.any(FileVersion.class))
		).thenReturn(
			false
		);

		_amImageAMProcessor.process(_fileVersion);

		Mockito.verify(
			_amImageConfigurationHelper, Mockito.never()
		).getAMImageConfigurationEntries(
			Mockito.anyLong()
		);
	}

	private final AMImageAMProcessor _amImageAMProcessor =
		new AMImageAMProcessor();
	private final AMImageConfigurationHelper _amImageConfigurationHelper =
		Mockito.mock(AMImageConfigurationHelper.class);
	private final AMImageEntry _amImageEntry = Mockito.mock(AMImageEntry.class);
	private final AMImageEntryLocalService _amImageEntryLocalService =
		Mockito.mock(AMImageEntryLocalService.class);
	private final AMImageScaler _amImageScaler = Mockito.mock(
		AMImageScaler.class);
	private final AMImageScalerRegistry _amImageScalerRegistry = Mockito.mock(
		AMImageScalerRegistry.class);
	private final AMImageValidator _amImageValidator = Mockito.mock(
		AMImageValidator.class);
	private final FileEntry _fileEntry = Mockito.mock(FileEntry.class);
	private final FileVersion _fileVersion = Mockito.mock(FileVersion.class);

}