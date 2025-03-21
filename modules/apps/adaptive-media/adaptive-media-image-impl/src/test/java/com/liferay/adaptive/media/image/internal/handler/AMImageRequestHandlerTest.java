/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.handler;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.exception.AMException;
import com.liferay.adaptive.media.exception.AMRuntimeException;
import com.liferay.adaptive.media.finder.AMQuery;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.finder.AMImageFinder;
import com.liferay.adaptive.media.image.finder.AMImageQueryBuilder;
import com.liferay.adaptive.media.image.internal.configuration.AMImageAttributeMapping;
import com.liferay.adaptive.media.image.internal.configuration.AMImageConfigurationEntryImpl;
import com.liferay.adaptive.media.image.internal.finder.AMImageQueryBuilderImpl;
import com.liferay.adaptive.media.image.internal.processor.AMImage;
import com.liferay.adaptive.media.image.internal.util.Tuple;
import com.liferay.adaptive.media.image.processor.AMImageAttribute;
import com.liferay.adaptive.media.processor.AMAsyncProcessor;
import com.liferay.adaptive.media.processor.AMAsyncProcessorLocator;
import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 * @author Alejandro Tardín
 */
public class AMImageRequestHandlerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws PortalException {
		Mockito.doReturn(
			_amAsyncProcessor
		).when(
			_amAsyncProcessorLocator
		).locateForClass(
			FileVersion.class
		);

		ReflectionTestUtil.setFieldValue(
			_amImageRequestHandler, "_amAsyncProcessorLocator",
			_amAsyncProcessorLocator);
		ReflectionTestUtil.setFieldValue(
			_amImageRequestHandler, "_amImageConfigurationHelper",
			_amImageConfigurationHelper);
		ReflectionTestUtil.setFieldValue(
			_amImageRequestHandler, "_amImageFinder", _amImageFinder);
		ReflectionTestUtil.setFieldValue(
			_amImageRequestHandler, "_pathInterpreter", _pathInterpreter);

		_fileVersion = _getFileVersion();
	}

	@Test(expected = AMRuntimeException.class)
	public void testFinderFailsWithMediaProcessorException() throws Exception {
		AMImageConfigurationEntry amImageConfigurationEntry =
			_createAMImageConfigurationEntry(
				_fileVersion.getCompanyId(), 200, 500);

		HttpServletRequest httpServletRequest = _createRequestFor(
			_fileVersion, amImageConfigurationEntry);

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenThrow(
			AMException.AMNotFound.class
		);

		_amImageRequestHandler.handleRequest(httpServletRequest);
	}

	@Test(expected = AMRuntimeException.class)
	public void testFinderFailsWithPortalException() throws Exception {
		AMImageConfigurationEntry getConfigurationEntryFilter =
			_createAMImageConfigurationEntry(
				_fileVersion.getCompanyId(), 200, 500);

		HttpServletRequest httpServletRequest = _createRequestFor(
			_fileVersion, getConfigurationEntryFilter);

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenThrow(
			PortalException.class
		);

		_amImageRequestHandler.handleRequest(httpServletRequest);
	}

	@Test
	public void testInvalidPath() throws Exception {
		Mockito.when(
			_pathInterpreter.interpretPath(Mockito.anyString())
		).thenReturn(
			null
		);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia =
			_amImageRequestHandler.handleRequest(httpServletRequest);

		Assert.assertNull(adaptiveMedia);
	}

	@Test(expected = NullPointerException.class)
	public void testNullRequest() throws Exception {
		_amImageRequestHandler.handleRequest(null);
	}

	@Test
	public void testPathInterpreterFailure() throws Exception {
		Mockito.when(
			_pathInterpreter.interpretPath(Mockito.anyString())
		).thenThrow(
			AMRuntimeException.IOException.class
		);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia =
			_amImageRequestHandler.handleRequest(httpServletRequest);

		Assert.assertNull(adaptiveMedia);
	}

	@Test
	public void testReturnsTheClosestMatchByWidthIfNoExactMatchPresentAndRunsTheProcess()
		throws Exception {

		AMImageConfigurationEntry getConfigurationEntryFilter =
			_createAMImageConfigurationEntry(
				_fileVersion.getCompanyId(), 200, 500);

		AMImageConfigurationEntry closestAMImageConfigurationEntry =
			_createAMImageConfigurationEntry(
				_fileVersion.getCompanyId(), 201, 501);

		AMImageConfigurationEntry fartherAMImageConfigurationEntry =
			_createAMImageConfigurationEntry(
				_fileVersion.getCompanyId(), 301, 501);

		AMImageConfigurationEntry farthestAMImageConfigurationEntry =
			_createAMImageConfigurationEntry(
				_fileVersion.getCompanyId(), 401, 501);

		AdaptiveMedia<AMProcessor<FileVersion>> closestAdaptiveMedia =
			_createAdaptiveMedia(
				_fileVersion, closestAMImageConfigurationEntry);

		AdaptiveMedia<AMProcessor<FileVersion>> fartherAdaptiveMedia =
			_createAdaptiveMedia(
				_fileVersion, fartherAMImageConfigurationEntry);

		AdaptiveMedia<AMProcessor<FileVersion>> farthestAdaptiveMedia =
			_createAdaptiveMedia(
				_fileVersion, farthestAMImageConfigurationEntry);

		_mockClosestMatch(
			_fileVersion, getConfigurationEntryFilter,
			Arrays.asList(
				farthestAdaptiveMedia, closestAdaptiveMedia,
				fartherAdaptiveMedia));

		HttpServletRequest httpServletRequest = _createRequestFor(
			_fileVersion, getConfigurationEntryFilter);

		Assert.assertEquals(
			closestAdaptiveMedia,
			_amImageRequestHandler.handleRequest(httpServletRequest));

		Mockito.verify(
			_amAsyncProcessor
		).triggerProcess(
			_fileVersion, String.valueOf(_fileVersion.getFileVersionId())
		);
	}

	@Test
	public void testReturnsTheExactMatchIfPresentAndDoesNotRunTheProcess()
		throws Exception {

		AMImageConfigurationEntry amImageConfigurationEntry =
			_createAMImageConfigurationEntry(
				_fileVersion.getCompanyId(), 200, 500);

		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia =
			_createAdaptiveMedia(_fileVersion, amImageConfigurationEntry);

		_mockExactMatch(_fileVersion, amImageConfigurationEntry, adaptiveMedia);

		HttpServletRequest httpServletRequest = _createRequestFor(
			_fileVersion, amImageConfigurationEntry);

		Assert.assertEquals(
			adaptiveMedia,
			_amImageRequestHandler.handleRequest(httpServletRequest));

		Mockito.verify(
			_amAsyncProcessor, Mockito.never()
		).triggerProcess(
			_fileVersion, String.valueOf(_fileVersion.getFileVersionId())
		);
	}

	@Test
	public void testReturnsTheRealImageIfThereAreNoAdaptiveMediasAndRunsTheProcess()
		throws Exception {

		AMImageConfigurationEntry amImageConfigurationEntry =
			_createAMImageConfigurationEntry(
				_fileVersion.getCompanyId(), 200, 500);

		HttpServletRequest httpServletRequest = _createRequestFor(
			_fileVersion, amImageConfigurationEntry);

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> Collections.emptyList()
		);

		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia =
			_amImageRequestHandler.handleRequest(httpServletRequest);

		Assert.assertNotNull(adaptiveMedia);
		Assert.assertEquals(
			_fileVersion.getContentStream(false),
			adaptiveMedia.getInputStream());
		Assert.assertEquals(
			_fileVersion.getFileName(),
			adaptiveMedia.getValue(AMAttribute.getFileNameAMAttribute()));
		Assert.assertEquals(
			_fileVersion.getMimeType(),
			adaptiveMedia.getValue(AMAttribute.getContentTypeAMAttribute()));
		Assert.assertEquals(
			_fileVersion.getSize(),
			(long)adaptiveMedia.<Long>getValue(
				AMAttribute.getContentLengthAMAttribute()));

		Mockito.verify(
			_amAsyncProcessor
		).triggerProcess(
			_fileVersion, String.valueOf(_fileVersion.getFileVersionId())
		);
	}

	private AdaptiveMedia<AMProcessor<FileVersion>> _createAdaptiveMedia(
			FileVersion fileVersion,
			AMImageConfigurationEntry amImageConfigurationEntry)
		throws Exception {

		Map<String, String> configurationEntryProperties =
			amImageConfigurationEntry.getProperties();

		Map<String, String> properties = HashMapBuilder.put(
			() -> {
				AMAttribute<Object, String> configurationUuidAMAttribute =
					AMAttribute.getConfigurationUuidAMAttribute();

				return configurationUuidAMAttribute.getName();
			},
			amImageConfigurationEntry.getUUID()
		).put(
			() -> {
				AMAttribute<Object, Long> contentLengthAMAttribute =
					AMAttribute.getContentLengthAMAttribute();

				return contentLengthAMAttribute.getName();
			},
			String.valueOf(fileVersion.getSize())
		).put(
			() -> {
				AMAttribute<Object, String> contentTypeAMAttribute =
					AMAttribute.getContentTypeAMAttribute();

				return contentTypeAMAttribute.getName();
			},
			fileVersion.getMimeType()
		).put(
			() -> {
				AMAttribute<Object, String> fileNameAMAttribute =
					AMAttribute.getFileNameAMAttribute();

				return fileNameAMAttribute.getName();
			},
			fileVersion.getFileName()
		).put(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT.getName(),
			configurationEntryProperties.get("max-height")
		).put(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH.getName(),
			configurationEntryProperties.get("max-width")
		).build();

		return new AMImage(
			() -> {
				try {
					return fileVersion.getContentStream(false);
				}
				catch (PortalException portalException) {
					throw new AMRuntimeException.IOException(portalException);
				}
			},
			AMImageAttributeMapping.fromProperties(properties), null);
	}

	private AMImageConfigurationEntry _createAMImageConfigurationEntry(
		long companyId, int width, int height) {

		String uuid = "testUuid" + Math.random();

		AMImageConfigurationEntryImpl amImageConfigurationEntryImpl =
			new AMImageConfigurationEntryImpl(
				uuid, uuid,
				HashMapBuilder.put(
					"configuration-uuid", uuid
				).put(
					"max-height", String.valueOf(height)
				).put(
					"max-width", String.valueOf(width)
				).build());

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				companyId, amImageConfigurationEntryImpl.getUUID())
		).thenReturn(
			amImageConfigurationEntryImpl
		);

		return amImageConfigurationEntryImpl;
	}

	private HttpServletRequest _createRequestFor(
		FileVersion fileVersion,
		AMImageConfigurationEntry amImageConfigurationEntry) {

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getPathInfo()
		).thenReturn(
			"pathInfo"
		);

		Mockito.when(
			_pathInterpreter.interpretPath(httpServletRequest.getPathInfo())
		).thenReturn(
			Tuple.of(
				fileVersion,
				HashMapBuilder.put(
					"configuration-uuid", amImageConfigurationEntry.getUUID()
				).build())
		);

		return httpServletRequest;
	}

	private FileVersion _getFileVersion() throws PortalException {
		FileVersion fileVersion = Mockito.mock(FileVersion.class);

		Mockito.when(
			fileVersion.getCompanyId()
		).thenReturn(
			1234L
		);

		Mockito.when(
			fileVersion.getContentStream(false)
		).thenReturn(
			Mockito.mock(InputStream.class)
		);

		Mockito.when(
			fileVersion.getMimeType()
		).thenReturn(
			"image/jpg"
		);

		Mockito.when(
			fileVersion.getFileName()
		).thenReturn(
			"fileName"
		);

		Mockito.when(
			fileVersion.getSize()
		).thenReturn(
			2048L
		);

		return fileVersion;
	}

	private void _mockClosestMatch(
			FileVersion fileVersion,
			AMImageConfigurationEntry amImageConfigurationEntry,
			List<AdaptiveMedia<AMProcessor<FileVersion>>> adaptiveMedias)
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> {
				Function<AMImageQueryBuilder, AMQuery<?, ?>>
					amImageQueryBuilderFunction = invocation.getArgument(
						0, Function.class);

				AMImageQueryBuilderImpl amImageQueryBuilderImpl =
					new AMImageQueryBuilderImpl();

				AMQuery<?, ?> amQuery = amImageQueryBuilderFunction.apply(
					amImageQueryBuilderImpl);

				Map<AMAttribute<AMProcessor<FileVersion>, ?>, Object>
					amAttributes = amImageQueryBuilderImpl.getAMAttributes();

				Object queryBuilderWidth = amAttributes.get(
					AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH);

				Object queryBuilderHeight = amAttributes.get(
					AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT);

				Map<String, String> properties =
					amImageConfigurationEntry.getProperties();

				int configurationWidth = GetterUtil.getInteger(
					properties.get("max-width"));

				int configurationHeight = GetterUtil.getInteger(
					properties.get("max-height"));

				if (AMImageQueryBuilderImpl.AM_QUERY.equals(amQuery) &&
					fileVersion.equals(
						amImageQueryBuilderImpl.getFileVersion()) &&
					(amImageQueryBuilderImpl.getConfigurationUuid() == null) &&
					queryBuilderWidth.equals(configurationWidth) &&
					queryBuilderHeight.equals(configurationHeight)) {

					return adaptiveMedias;
				}

				return Collections.emptyList();
			}
		);
	}

	private void _mockExactMatch(
			FileVersion fileVersion,
			AMImageConfigurationEntry amImageConfigurationEntry,
			AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia)
		throws Exception {

		Mockito.when(
			_amImageFinder.getAdaptiveMedias(Mockito.any(Function.class))
		).thenAnswer(
			invocation -> {
				Function<AMImageQueryBuilder, AMQuery<?, ?>>
					amImageQueryBuilderFunction = invocation.getArgument(
						0, Function.class);

				AMImageQueryBuilderImpl amImageQueryBuilderImpl =
					new AMImageQueryBuilderImpl();

				AMQuery<?, ?> amQuery = amImageQueryBuilderFunction.apply(
					amImageQueryBuilderImpl);

				if (!AMImageQueryBuilderImpl.AM_QUERY.equals(amQuery)) {
					return Collections.emptyList();
				}

				if (fileVersion.equals(
						amImageQueryBuilderImpl.getFileVersion())) {

					Predicate<AMImageConfigurationEntry>
						amImageConfigurationEntryFilter =
							amImageQueryBuilderImpl.
								getConfigurationEntryFilter();

					if (amImageConfigurationEntryFilter.test(
							amImageConfigurationEntry)) {

						return Arrays.asList(adaptiveMedia);
					}
				}

				return Collections.emptyList();
			}
		);
	}

	private final AMAsyncProcessor<FileVersion, ?> _amAsyncProcessor =
		Mockito.mock(AMAsyncProcessor.class);
	private final AMAsyncProcessorLocator _amAsyncProcessorLocator =
		Mockito.mock(AMAsyncProcessorLocator.class);
	private final AMImageConfigurationHelper _amImageConfigurationHelper =
		Mockito.mock(AMImageConfigurationHelper.class);
	private final AMImageFinder _amImageFinder = Mockito.mock(
		AMImageFinder.class);
	private final AMImageRequestHandler _amImageRequestHandler =
		new AMImageRequestHandler();
	private FileVersion _fileVersion;
	private final PathInterpreter _pathInterpreter = Mockito.mock(
		PathInterpreter.class);

}