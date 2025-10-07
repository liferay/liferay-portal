/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.validator;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.image.mime.type.AMImageMimeTypeProvider;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.adaptive.media.image.validator.AMImageValidator;
import com.liferay.document.library.configuration.DLFileEntryConfigurationProvider;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.processor.RawMetadataProcessor;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueValidationException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.dynamic.data.mapping.util.comparator.StructureStructureKeyComparator;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 * @author Roberto Díaz
 */
@Component(service = AMImageValidator.class)
public class AMImageValidatorImpl implements AMImageValidator {

	@Override
	public <T> boolean isProcessingRequired(
		AdaptiveMedia<T> adaptiveMedia, FileVersion fileVersion) {

		String configurationUuid = adaptiveMedia.getValue(
			AMAttribute.getConfigurationUuidAMAttribute());

		if ((configurationUuid != null) &&
			_amImageEntryLocalService.hasAMImageEntryContent(
				configurationUuid, fileVersion)) {

			return false;
		}

		return isProcessingSupported(fileVersion);
	}

	@Override
	public boolean isProcessingSupported(FileVersion fileVersion) {
		if (!isValid(fileVersion) ||
			Objects.equals(
				fileVersion.getMimeType(), ContentTypes.IMAGE_SVG_XML)) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isProcessingSupported(String mimeType) {
		return !StringUtil.equalsIgnoreCase(
			mimeType, ContentTypes.IMAGE_SVG_XML);
	}

	@Override
	public boolean isValid(FileVersion fileVersion) {
		long previewableProcessorMaxSize =
			_dlFileEntryConfigurationProvider.
				getGroupPreviewableProcessorMaxSize(fileVersion.getGroupId());

		if ((previewableProcessorMaxSize != -1) &&
			((previewableProcessorMaxSize == 0) ||
			 (fileVersion.getSize() == 0) ||
			 (fileVersion.getSize() >= previewableProcessorMaxSize))) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"File " + fileVersion.getFileName() +
						" has an invalid size");
			}

			return false;
		}

		if (!_amImageMimeTypeProvider.isMimeTypeSupported(
				fileVersion.getMimeType()) ||
			!_isFileVersionStoredMetadataSupported(fileVersion)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"File " + fileVersion.getFileName() +
						"has an invalid mime type or metada");
			}

			return false;
		}

		return true;
	}

	protected void setAMImageEntryLocalService(
		AMImageEntryLocalService amImageEntryLocalService) {

		_amImageEntryLocalService = amImageEntryLocalService;
	}

	private boolean _isFileVersionStoredMetadataSupported(
		FileVersion fileVersion) {

		List<DDMStructure> ddmStructures =
			_ddmStructureLocalService.getClassStructures(
				fileVersion.getCompanyId(),
				_portal.getClassNameId(RawMetadataProcessor.class),
				StructureStructureKeyComparator.getInstance(false));

		for (DDMStructure ddmStructure : ddmStructures) {
			DLFileEntryMetadata fileEntryMetadata =
				_dlFileEntryMetadataLocalService.fetchFileEntryMetadata(
					ddmStructure.getStructureId(),
					fileVersion.getFileVersionId());

			if (fileEntryMetadata == null) {
				continue;
			}

			try {
				DDMFormValues ddmFormValues =
					_ddmStorageEngineManager.getDDMFormValues(
						fileEntryMetadata.getDDMStorageId());

				if (ddmFormValues == null) {
					continue;
				}

				Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
					ddmFormValues.getDDMFormFieldValuesMap(true);

				for (Map.Entry<String, List<DDMFormFieldValue>> entry :
						ddmFormFieldValuesMap.entrySet()) {

					if (Objects.equals(entry.getKey(), "TIFF_IMAGE_LENGTH") &&
						!_isValidDimension(
							entry.getValue(),
							PropsValues.IMAGE_TOOL_IMAGE_MAX_HEIGHT)) {

						if (_log.isDebugEnabled()) {
							_log.debug(
								entry.getValue() + " has an invalid height");
						}

						return false;
					}
					else if (Objects.equals(
								entry.getKey(), "TIFF_IMAGE_WIDTH") &&
							 !_isValidDimension(
								 entry.getValue(),
								 PropsValues.IMAGE_TOOL_IMAGE_MAX_WIDTH)) {

						if (_log.isDebugEnabled()) {
							_log.debug(
								entry.getValue() + " has an invalid width");
						}

						return false;
					}
				}
			}
			catch (DDMFormFieldValueValidationException
						ddmFormFieldValueValidationException) {

				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to validate dynamic data mapping form values " +
							"for file version " +
								fileVersion.getFileVersionId());
				}

				if (_log.isDebugEnabled()) {
					_log.debug(ddmFormFieldValueValidationException);
				}
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to find dynamic data mapping form values ",
							"for ", fileVersion.getFileVersionId(),
							" in structure ", ddmStructure.getStructureKey()));
				}

				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return true;
	}

	private boolean _isValidDimension(
			List<DDMFormFieldValue> ddmFormFieldValues,
			long imageToolImageMaxValue)
		throws DDMFormFieldValueValidationException {

		if (imageToolImageMaxValue <= 0) {
			return true;
		}

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Value value = ddmFormFieldValue.getValue();

		String valueString = value.getString(value.getDefaultLocale());

		if (Validator.isNull(valueString)) {
			for (Locale availableLocale : value.getAvailableLocales()) {
				valueString = value.getString(availableLocale);

				if (Validator.isNotNull(valueString)) {
					break;
				}
			}
		}

		if (Validator.isNull(valueString)) {
			throw new DDMFormFieldValueValidationException();
		}

		if (Long.valueOf(valueString) >= imageToolImageMaxValue) {
			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AMImageValidatorImpl.class);

	@Reference
	private AMImageEntryLocalService _amImageEntryLocalService;

	@Reference
	private AMImageMimeTypeProvider _amImageMimeTypeProvider;

	@Reference
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DLFileEntryConfigurationProvider _dlFileEntryConfigurationProvider;

	@Reference
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Reference
	private Portal _portal;

}