/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.document.library.thumbnails.internal.processor;

import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.document.library.thumbnails.internal.configuration.AMSystemImagesConfiguration;
import com.liferay.adaptive.media.image.finder.AMImageFinder;
import com.liferay.adaptive.media.image.mime.type.AMImageMimeTypeProvider;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.adaptive.media.image.validator.AMImageValidator;
import com.liferay.adaptive.media.processor.AMAsyncProcessor;
import com.liferay.adaptive.media.processor.AMAsyncProcessorLocator;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.InputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Sergio González
 */
public class AMImageEntryProcessorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_amImageEntryProcessor = new AMImageEntryProcessor();

		Mockito.doReturn(
			_amAsyncProcessor
		).when(
			_amAsyncProcessorLocator
		).locateForClass(
			FileVersion.class
		);

		ReflectionTestUtil.setFieldValue(
			_amImageEntryProcessor, "_amAsyncProcessorLocator",
			_amAsyncProcessorLocator);
		ReflectionTestUtil.setFieldValue(
			_amImageEntryProcessor, "_amImageEntryLocalService",
			_amImageEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_amImageEntryProcessor, "_amImageFinder", _amImageFinder);
		ReflectionTestUtil.setFieldValue(
			_amImageEntryProcessor, "_amImageMimeTypeProvider",
			_amImageMimeTypeProvider);
		ReflectionTestUtil.setFieldValue(
			_amImageEntryProcessor, "_amImageValidator", _amImageValidator);
		ReflectionTestUtil.setFieldValue(
			_amImageEntryProcessor, "_amSystemImagesConfiguration",
			_amSystemImagesConfiguration);
		ReflectionTestUtil.setFieldValue(
			PrefsPropsUtil.class, "_prefsProps", _prefsProps);
	}

	@Test
	public void testCleanUp() throws Exception {
		_testCleanUpWhenFileVersionIsExternal();
		_testCleanUpWhenFileVersionIsLocal();
	}

	@Test
	public void testGetPreviewAsStreamDoesNotTriggerAMProcessorWhenAMImageExists()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Arrays.asList(_adaptiveMedia)
		);

		_amImageEntryProcessor.getPreviewAsStream(_fileVersion);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetPreviewAsStreamDoesNotTriggerAMProcessorWhenInvalidSize()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageValidator.isValid(_fileVersion)
		).thenReturn(
			false
		);

		_amImageEntryProcessor.getPreviewAsStream(_fileVersion);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetPreviewAsStreamDoesNotTriggerAMProcessorWhenNotSupported()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			false
		);

		_amImageEntryProcessor.getPreviewAsStream(_fileVersion);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetPreviewAsStreamReturnsTheOriginalStreamWhenNoAMImageExists()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageValidator.isValid(_fileVersion)
		).thenReturn(
			true
		);

		InputStream originalInputStream = Mockito.mock(InputStream.class);

		Mockito.when(
			_fileVersion.getContentStream(false)
		).thenReturn(
			originalInputStream
		);

		Assert.assertEquals(
			originalInputStream,
			_amImageEntryProcessor.getPreviewAsStream(_fileVersion));
	}

	@Test
	public void testGetPreviewAsStreamTriggersAMProcessorWhenNoAMImageExists()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageValidator.isValid(_fileVersion)
		).thenReturn(
			true
		);

		_amImageEntryProcessor.getPreviewAsStream(_fileVersion);

		Mockito.verify(
			_amAsyncProcessor
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetPreviewFileSizeDoesNotTriggerAMProcessorWhenAMImageExists()
		throws Exception {

		Mockito.when(
			_adaptiveMedia.getValue(Mockito.any())
		).thenReturn(
			null
		);

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Arrays.asList(_adaptiveMedia)
		);

		_amImageEntryProcessor.getPreviewFileSize(_fileVersion);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetPreviewFileSizeDoesNotTriggerAMProcessorWhenInvalidSize()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageValidator.isValid(_fileVersion)
		).thenReturn(
			false
		);

		_amImageEntryProcessor.getPreviewFileSize(_fileVersion);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetPreviewFileSizeDoesNotTriggerAMProcessorWhenNotSupported()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			false
		);

		_amImageEntryProcessor.getPreviewFileSize(_fileVersion);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetPreviewFileSizeReturnsTheOriginalSizeWhenNoAMImageExists()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageValidator.isValid(_fileVersion)
		).thenReturn(
			true
		);

		Random random = new Random();

		long originalSize = random.nextLong();

		Mockito.when(
			_fileVersion.getSize()
		).thenReturn(
			originalSize
		);

		Assert.assertEquals(
			originalSize,
			_amImageEntryProcessor.getPreviewFileSize(_fileVersion));
	}

	@Test
	public void testGetPreviewFileSizeTriggersAMProcessorWhenNoAMImageExists()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageValidator.isValid(_fileVersion)
		).thenReturn(
			true
		);

		_amImageEntryProcessor.getPreviewFileSize(_fileVersion);

		Mockito.verify(
			_amAsyncProcessor
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetThumbnailAsStreamDoesNotTriggerAMProcessorWhenAMImageExists()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Arrays.asList(_adaptiveMedia)
		);

		_amImageEntryProcessor.getThumbnailAsStream(_fileVersion, 0);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetThumbnailAsStreamDoesNotTriggerAMProcessorWhenInvalidSize()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageValidator.isValid(_fileVersion)
		).thenReturn(
			false
		);

		_amImageEntryProcessor.getThumbnailAsStream(_fileVersion, 0);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetThumbnailAsStreamDoesNotTriggerAMProcessorWhenNotSupported()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			false
		);

		_amImageEntryProcessor.getThumbnailAsStream(_fileVersion, 0);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetThumbnailAsStreamTriggersAMProcessorWhenNoAMImageExists()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageValidator.isValid(_fileVersion)
		).thenReturn(
			true
		);

		_amImageEntryProcessor.getThumbnailAsStream(_fileVersion, 0);

		Mockito.verify(
			_amAsyncProcessor
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetThumbnailFileSizeDoesNotTriggerAMProcessorWhenAMImageExists()
		throws Exception {

		Mockito.when(
			_adaptiveMedia.getValue(Mockito.any())
		).thenReturn(
			null
		);

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Arrays.asList(_adaptiveMedia)
		);

		_amImageEntryProcessor.getThumbnailFileSize(_fileVersion, 0);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetThumbnailFileSizeDoesNotTriggerAMProcessorWhenInvalidSize()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageValidator.isValid(_fileVersion)
		).thenReturn(
			false
		);

		_amImageEntryProcessor.getThumbnailFileSize(_fileVersion, 0);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetThumbnailFileSizeDoesNotTriggerAMProcessorWhenNotSupported()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			false
		);

		_amImageEntryProcessor.getThumbnailFileSize(_fileVersion, 0);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testGetThumbnailFileSizeTriggersAMProcessorWhenNoAMImageExists()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageValidator.isValid(_fileVersion)
		).thenReturn(
			true
		);

		_amImageEntryProcessor.getThumbnailFileSize(_fileVersion, 0);

		Mockito.verify(
			_amAsyncProcessor
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testHasImagesDoesNotTriggerAMProcessorWhenAMImageExists()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Arrays.asList(_adaptiveMedia)
		);

		_amImageEntryProcessor.hasImages(_fileVersion);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testHasImagesDoesNotTriggerAMProcessorWhenInvalidSize()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageValidator.isValid(_fileVersion)
		).thenReturn(
			false
		);

		_amImageEntryProcessor.hasImages(_fileVersion);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testHasImagesDoesNotTriggerAMProcessorWhenNotSupported()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			false
		);

		_amImageEntryProcessor.hasImages(_fileVersion);

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	@Test
	public void testHasImagesTriggersAMProcessorWhenNoAMImageExists()
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		Mockito.when(
			_amImageMimeTypeProvider.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageValidator.isValid(_fileVersion)
		).thenReturn(
			true
		);

		_amImageEntryProcessor.hasImages(_fileVersion);

		Mockito.verify(
			_amAsyncProcessor
		).triggerProcess(
			Mockito.any(FileVersion.class), Mockito.anyString()
		);
	}

	private void _testCleanUpWhenFileVersionIsExternal() throws Exception {
		FileVersion fileVersion = Mockito.mock(FileVersion.class);

		_amImageEntryProcessor.cleanUp(fileVersion);

		Mockito.verify(
			_amImageEntryLocalService
		).deleteAMImageEntryFileVersion(
			fileVersion
		);
	}

	private void _testCleanUpWhenFileVersionIsLocal() throws Exception {
		FileVersion fileVersion = Mockito.mock(FileVersion.class);

		Mockito.when(
			fileVersion.getModel()
		).thenReturn(
			Mockito.mock(DLFileVersion.class)
		);

		_amImageEntryProcessor.cleanUp(fileVersion);

		Mockito.verify(
			_amImageEntryLocalService, Mockito.never()
		).deleteAMImageEntryFileVersion(
			fileVersion
		);
	}

	private final AdaptiveMedia<?> _adaptiveMedia = Mockito.mock(
		AdaptiveMedia.class);
	private final AMAsyncProcessor<FileVersion, ?> _amAsyncProcessor =
		Mockito.mock(AMAsyncProcessor.class);
	private final AMAsyncProcessorLocator _amAsyncProcessorLocator =
		Mockito.mock(AMAsyncProcessorLocator.class);
	private final AMImageEntryLocalService _amImageEntryLocalService =
		Mockito.mock(AMImageEntryLocalService.class);
	private AMImageEntryProcessor _amImageEntryProcessor;
	private final AMImageFinder _amImageFinder = Mockito.mock(
		AMImageFinder.class);
	private final AMImageMimeTypeProvider _amImageMimeTypeProvider =
		Mockito.mock(AMImageMimeTypeProvider.class);
	private final AMImageValidator _amImageValidator = Mockito.mock(
		AMImageValidator.class);
	private final AMSystemImagesConfiguration _amSystemImagesConfiguration =
		Mockito.mock(AMSystemImagesConfiguration.class);
	private final FileVersion _fileVersion = Mockito.mock(FileVersion.class);
	private final PrefsProps _prefsProps = Mockito.mock(PrefsProps.class);

}