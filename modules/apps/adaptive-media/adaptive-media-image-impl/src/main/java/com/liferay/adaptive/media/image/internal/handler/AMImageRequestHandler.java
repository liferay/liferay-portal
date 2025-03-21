/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.handler;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.exception.AMRuntimeException;
import com.liferay.adaptive.media.handler.AMRequestHandler;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.finder.AMImageFinder;
import com.liferay.adaptive.media.image.internal.configuration.AMImageAttributeMapping;
import com.liferay.adaptive.media.image.internal.processor.AMImage;
import com.liferay.adaptive.media.image.internal.util.Tuple;
import com.liferay.adaptive.media.image.processor.AMImageAttribute;
import com.liferay.adaptive.media.processor.AMAsyncProcessor;
import com.liferay.adaptive.media.processor.AMAsyncProcessorLocator;
import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author Alejandro Tardín
 */
@Component(
	property = "adaptive.media.handler.pattern=image",
	service = AMRequestHandler.class
)
public class AMImageRequestHandler
	implements AMRequestHandler<AMProcessor<FileVersion>> {

	@Override
	public AdaptiveMedia<AMProcessor<FileVersion>> handleRequest(
		HttpServletRequest httpServletRequest) {

		Tuple<FileVersion, AMImageAttributeMapping> interpretedPath =
			_interpretPath(httpServletRequest.getPathInfo());

		if (interpretedPath == null) {
			return null;
		}

		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia =
			_getAdaptiveMedia(interpretedPath.first, interpretedPath.second);

		if (adaptiveMedia != null) {
			_processAMImage(
				adaptiveMedia, interpretedPath.first, interpretedPath.second);
		}

		return adaptiveMedia;
	}

	@Activate
	protected void activate() {
		_pathInterpreter = new PathInterpreter(
			_amImageConfigurationHelper, _dlAppLocalService);
	}

	private AdaptiveMedia<AMProcessor<FileVersion>> _createRawAdaptiveMedia(
		FileVersion fileVersion) {

		return new AMImage(
			() -> {
				try {
					return fileVersion.getContentStream(false);
				}
				catch (PortalException portalException) {
					throw new AMRuntimeException.IOException(portalException);
				}
			},
			AMImageAttributeMapping.fromFileVersion(fileVersion), null);
	}

	private AdaptiveMedia<AMProcessor<FileVersion>> _findAdaptiveMedia(
			FileVersion fileVersion,
			AMImageConfigurationEntry amImageConfigurationEntry)
		throws PortalException {

		List<AdaptiveMedia<AMProcessor<FileVersion>>> adaptiveMedias =
			_amImageFinder.getAdaptiveMedias(
				amImageQueryBuilder -> amImageQueryBuilder.forFileVersion(
					fileVersion
				).forConfiguration(
					amImageConfigurationEntry.getUUID()
				).done());

		if (!adaptiveMedias.isEmpty()) {
			return adaptiveMedias.get(0);
		}

		Map<String, String> properties =
			amImageConfigurationEntry.getProperties();

		Integer configurationWidth = GetterUtil.getInteger(
			properties.get("max-width"));

		Integer configurationHeight = GetterUtil.getInteger(
			properties.get("max-height"));

		try {
			adaptiveMedias = _amImageFinder.getAdaptiveMedias(
				amImageQueryBuilder -> amImageQueryBuilder.forFileVersion(
					fileVersion
				).with(
					AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH,
					configurationWidth
				).with(
					AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT,
					configurationHeight
				).done());

			if (adaptiveMedias.isEmpty()) {
				return null;
			}

			adaptiveMedias.sort(_getComparator(configurationWidth));

			return adaptiveMedias.get(0);
		}
		catch (PortalException portalException) {
			throw new AMRuntimeException.IOException(portalException);
		}
	}

	private AdaptiveMedia<AMProcessor<FileVersion>> _getAdaptiveMedia(
		FileVersion fileVersion,
		AMImageAttributeMapping amImageAttributeMapping) {

		try {
			String configurationUuid = amImageAttributeMapping.getValue(
				AMAttribute.getConfigurationUuidAMAttribute());

			if (configurationUuid == null) {
				return null;
			}

			AMImageConfigurationEntry amImageConfigurationEntry =
				_amImageConfigurationHelper.getAMImageConfigurationEntry(
					fileVersion.getCompanyId(), configurationUuid);

			if (amImageConfigurationEntry == null) {
				return null;
			}

			AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia =
				_findAdaptiveMedia(fileVersion, amImageConfigurationEntry);

			if (adaptiveMedia != null) {
				return adaptiveMedia;
			}

			return _createRawAdaptiveMedia(fileVersion);
		}
		catch (PortalException portalException) {
			throw new AMRuntimeException.IOException(portalException);
		}
	}

	private Comparator<AdaptiveMedia<AMProcessor<FileVersion>>> _getComparator(
		Integer configurationWidth) {

		return Comparator.comparingInt(
			adaptiveMedia -> _getDistance(configurationWidth, adaptiveMedia));
	}

	private Integer _getDistance(
		int width, AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia) {

		Integer imageWidth = adaptiveMedia.getValue(
			AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH);

		if (imageWidth == null) {
			return Integer.MAX_VALUE;
		}

		return Math.abs(imageWidth - width);
	}

	private Tuple<FileVersion, AMImageAttributeMapping> _interpretPath(
		String pathInfo) {

		try {
			Tuple<FileVersion, Map<String, String>> fileVersionPropertiesTuple =
				_pathInterpreter.interpretPath(pathInfo);

			if (fileVersionPropertiesTuple == null) {
				return null;
			}

			FileVersion fileVersion = fileVersionPropertiesTuple.first;

			if (fileVersion.getStatus() == WorkflowConstants.STATUS_IN_TRASH) {
				return null;
			}

			Map<String, String> properties = fileVersionPropertiesTuple.second;

			AMAttribute<Object, Long> contentLengthAMAttribute =
				AMAttribute.getContentLengthAMAttribute();

			properties.put(
				contentLengthAMAttribute.getName(),
				String.valueOf(fileVersion.getSize()));

			AMAttribute<Object, String> contentTypeAMAttribute =
				AMAttribute.getContentTypeAMAttribute();

			properties.put(
				contentTypeAMAttribute.getName(), fileVersion.getMimeType());

			AMAttribute<Object, String> fileNameAMAttribute =
				AMAttribute.getFileNameAMAttribute();

			properties.put(
				fileNameAMAttribute.getName(), fileVersion.getFileName());

			AMImageAttributeMapping amImageAttributeMapping =
				AMImageAttributeMapping.fromProperties(properties);

			return Tuple.of(fileVersion, amImageAttributeMapping);
		}
		catch (AMRuntimeException | NumberFormatException exception) {
			_log.error(exception);

			return null;
		}
	}

	private void _processAMImage(
		AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia,
		FileVersion fileVersion,
		AMImageAttributeMapping amImageAttributeMapping) {

		String adaptiveMediaConfigurationUuid = adaptiveMedia.getValue(
			AMAttribute.getConfigurationUuidAMAttribute());

		String attributeMappingConfigurationUuid =
			amImageAttributeMapping.getValue(
				AMAttribute.getConfigurationUuidAMAttribute());

		if (Objects.equals(
				adaptiveMediaConfigurationUuid,
				attributeMappingConfigurationUuid)) {

			return;
		}

		try {
			AMAsyncProcessor<FileVersion, ?> amAsyncProcessor =
				_amAsyncProcessorLocator.locateForClass(FileVersion.class);

			amAsyncProcessor.triggerProcess(
				fileVersion, String.valueOf(fileVersion.getFileVersionId()));
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to create lazy adaptive media for file version " +
					fileVersion.getFileVersionId(),
				portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AMImageRequestHandler.class);

	@Reference
	private AMAsyncProcessorLocator _amAsyncProcessorLocator;

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Reference
	private AMImageFinder _amImageFinder;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	private PathInterpreter _pathInterpreter;

}