/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.info.item.provider;

import com.liferay.asset.info.item.provider.AssetEntryInfoItemFieldSetProvider;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.document.library.web.internal.info.item.FileEntryInfoItemFields;
import com.liferay.dynamic.data.mapping.info.item.provider.DDMFormValuesInfoFieldValuesProvider;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.expando.info.item.provider.ExpandoInfoItemFieldSetProvider;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.field.reader.InfoItemFieldReaderFieldSetProvider;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.type.WebImage;
import com.liferay.layout.page.template.info.item.provider.DisplayPageInfoItemFieldSetProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.template.info.item.provider.TemplateInfoItemFieldSetProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 * @author Jorge Ferrer
 */
@Component(
	property = Constants.SERVICE_RANKING + ":Integer=10",
	service = InfoItemFieldValuesProvider.class
)
public class FileEntryInfoItemFieldValuesProvider
	implements InfoItemFieldValuesProvider<FileEntry> {

	@Override
	public InfoItemFieldValues getInfoItemFieldValues(FileEntry fileEntry) {
		try {
			return InfoItemFieldValues.builder(
			).infoFieldValues(
				_getFileEntryInfoFieldValues(fileEntry)
			).infoFieldValues(
				_getAssetEntryInfoFieldValues(fileEntry)
			).infoFieldValues(
				_getDDMStructureInfoFieldValues(fileEntry)
			).infoFieldValues(
				_getDisplayPageInfoFieldValues(fileEntry)
			).infoFieldValues(
				_getExpandoInfoFieldValues(fileEntry)
			).infoFieldValues(
				_infoItemFieldReaderFieldSetProvider.getInfoFieldValues(
					FileEntry.class.getName(), fileEntry)
			).infoFieldValues(
				_templateInfoItemFieldSetProvider.getInfoFieldValues(
					FileEntry.class.getName(),
					_getInfoItemFormVariationKey(fileEntry), fileEntry)
			).infoItemReference(
				new InfoItemReference(
					FileEntry.class.getName(), fileEntry.getFileEntryId())
			).build();
		}
		catch (PortalException portalException) {
			throw new RuntimeException(
				"Caught unexpected exception", portalException);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private List<InfoFieldValue<Object>> _getAssetEntryInfoFieldValues(
			FileEntry fileEntry)
		throws NoSuchInfoItemException {

		if (fileEntry.getModel() instanceof DLFileEntry) {
			DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

			return _assetEntryInfoItemFieldSetProvider.getInfoFieldValues(
				dlFileEntry.getCompanyId(), DLFileEntry.class.getName(),
				dlFileEntry.getFileEntryId());
		}

		return Collections.emptyList();
	}

	private List<InfoFieldValue<Object>> _getDDMStructureInfoFieldValues(
		FileEntry fileEntry) {

		if (fileEntry.getModel() instanceof DLFileEntry) {
			try {
				List<InfoFieldValue<Object>> infoFieldValues =
					new ArrayList<>();

				DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

				DLFileEntryType dlFileEntryType =
					_dlFileEntryTypeLocalService.getFileEntryType(
						dlFileEntry.getFileEntryTypeId());

				List<DDMStructure> ddmStructures =
					DLFileEntryTypeUtil.getDDMStructures(dlFileEntryType);

				for (DDMStructure ddmStructure : ddmStructures) {
					FileVersion fileVersion = fileEntry.getFileVersion();

					DLFileEntryMetadata dlFileEntryMetadata =
						_dlFileEntryMetadataLocalService.fetchFileEntryMetadata(
							ddmStructure.getStructureId(),
							fileVersion.getFileVersionId());

					if (dlFileEntryMetadata == null) {
						continue;
					}

					infoFieldValues.addAll(
						_ddmFormValuesInfoFieldValuesProvider.
							getInfoFieldValues(
								fileEntry,
								_ddmStorageEngineManager.getDDMFormValues(
									dlFileEntryMetadata.getDDMStorageId())));
				}

				return infoFieldValues;
			}
			catch (PortalException portalException) {
				throw new RuntimeException(portalException);
			}
		}

		return Collections.emptyList();
	}

	private List<InfoFieldValue<Object>> _getDisplayPageInfoFieldValues(
			FileEntry fileEntry)
		throws Exception {

		if (fileEntry.getModel() instanceof DLFileEntry) {
			DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

			return _displayPageInfoItemFieldSetProvider.getInfoFieldValues(
				new InfoItemReference(
					FileEntry.class.getName(), fileEntry.getFileEntryId()),
				String.valueOf(dlFileEntry.getFileEntryTypeId()),
				FileEntry.class.getSimpleName(), fileEntry, _getThemeDisplay());
		}

		return Collections.emptyList();
	}

	private List<InfoFieldValue<Object>> _getExpandoInfoFieldValues(
			FileEntry fileEntry)
		throws PortalException {

		if (fileEntry.getModel() instanceof DLFileEntry) {
			DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

			DLFileVersion dlFileVersion = dlFileEntry.getLatestFileVersion(
				true);

			return _expandoInfoItemFieldSetProvider.getInfoFieldValues(
				DLFileEntryConstants.getClassName(), dlFileVersion);
		}

		return Collections.emptyList();
	}

	private List<InfoFieldValue<Object>> _getFileEntryInfoFieldValues(
		FileEntry fileEntry) {

		ThemeDisplay themeDisplay = _getThemeDisplay();

		try {
			List<InfoFieldValue<Object>> fileEntryFieldValues =
				new ArrayList<>();

			fileEntryFieldValues.add(
				new InfoFieldValue<>(
					FileEntryInfoItemFields.fileNameInfoField,
					fileEntry.getFileName()));

			String mimeType = fileEntry.getMimeType();

			fileEntryFieldValues.add(
				new InfoFieldValue<>(
					FileEntryInfoItemFields.mimeTypeInfoField, mimeType));

			if (mimeType.startsWith("image")) {
				WebImage fileURLWebImage = new WebImage(
					_dlURLHelper.getDownloadURL(
						fileEntry, fileEntry.getFileVersion(), themeDisplay,
						StringPool.BLANK),
					new InfoItemReference(
						FileEntry.class.getName(),
						new ClassPKInfoItemIdentifier(
							fileEntry.getFileEntryId())));

				fileURLWebImage.setAlt(fileEntry.getDescription());

				fileEntryFieldValues.add(
					new InfoFieldValue<>(
						FileEntryInfoItemFields.fileURLInfoField,
						fileURLWebImage));
			}

			fileEntryFieldValues.add(
				new InfoFieldValue<>(
					FileEntryInfoItemFields.titleInfoField,
					fileEntry.getTitle()));
			fileEntryFieldValues.add(
				new InfoFieldValue<>(
					FileEntryInfoItemFields.descriptionInfoField,
					fileEntry.getDescription()));
			fileEntryFieldValues.add(
				new InfoFieldValue<>(
					FileEntryInfoItemFields.versionInfoField,
					fileEntry.getVersion()));
			fileEntryFieldValues.add(
				new InfoFieldValue<>(
					FileEntryInfoItemFields.sizeInfoField,
					fileEntry.getSize()));
			fileEntryFieldValues.add(
				new InfoFieldValue<>(
					FileEntryInfoItemFields.createDateInfoField,
					fileEntry.getCreateDate()));
			fileEntryFieldValues.add(
				new InfoFieldValue<>(
					FileEntryInfoItemFields.modifiedDateInfoField,
					fileEntry.getModifiedDate()));

			User user = _userLocalService.fetchUser(fileEntry.getUserId());

			if (user != null) {
				fileEntryFieldValues.add(
					new InfoFieldValue<>(
						FileEntryInfoItemFields.authorNameInfoField,
						user.getFullName()));

				if (themeDisplay != null) {
					WebImage webImage = new WebImage(
						user.getPortraitURL(themeDisplay));

					webImage.setAlt(user.getFullName());

					fileEntryFieldValues.add(
						new InfoFieldValue<>(
							FileEntryInfoItemFields.authorProfileImageInfoField,
							webImage));
				}
			}

			fileEntryFieldValues.add(
				new InfoFieldValue<>(
					FileEntryInfoItemFields.publishDateInfoField,
					fileEntry.getModifiedDate()));

			String downloadURL = _dlURLHelper.getDownloadURL(
				fileEntry, fileEntry.getFileVersion(), themeDisplay,
				StringPool.BLANK);

			if (Validator.isNotNull(downloadURL)) {
				fileEntryFieldValues.add(
					new InfoFieldValue<>(
						FileEntryInfoItemFields.downloadURLInfoField,
						downloadURL));
			}

			String previewURL = _dlURLHelper.getPreviewURL(
				fileEntry, fileEntry.getFileVersion(), themeDisplay,
				StringPool.BLANK, false, true);

			if (Validator.isNotNull(previewURL)) {
				fileEntryFieldValues.add(
					new InfoFieldValue<>(
						FileEntryInfoItemFields.previewURLInfoField,
						previewURL));
			}

			WebImage imagePreviewURLWebImage = new WebImage(
				_dlURLHelper.getImagePreviewURL(fileEntry, themeDisplay),
				new InfoItemReference(
					FileEntry.class.getName(),
					new ClassPKInfoItemIdentifier(fileEntry.getFileEntryId())));

			imagePreviewURLWebImage.setAlt(fileEntry.getDescription());

			fileEntryFieldValues.add(
				new InfoFieldValue<>(
					FileEntryInfoItemFields.previewImageInfoField,
					imagePreviewURLWebImage));

			return fileEntryFieldValues;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _getInfoItemFormVariationKey(FileEntry fileEntry) {
		if (fileEntry.getModel() instanceof DLFileEntry) {
			DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

			return String.valueOf(dlFileEntry.getFileEntryTypeId());
		}

		return StringPool.BLANK;
	}

	private ThemeDisplay _getThemeDisplay() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			return serviceContext.getThemeDisplay();
		}

		return null;
	}

	@Reference
	private AssetEntryInfoItemFieldSetProvider
		_assetEntryInfoItemFieldSetProvider;

	@Reference
	private DDMFormValuesInfoFieldValuesProvider
		_ddmFormValuesInfoFieldValuesProvider;

	@Reference
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Reference
	private DisplayPageInfoItemFieldSetProvider
		_displayPageInfoItemFieldSetProvider;

	@Reference
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private ExpandoInfoItemFieldSetProvider _expandoInfoItemFieldSetProvider;

	@Reference
	private InfoItemFieldReaderFieldSetProvider
		_infoItemFieldReaderFieldSetProvider;

	@Reference
	private TemplateInfoItemFieldSetProvider _templateInfoItemFieldSetProvider;

	@Reference
	private UserLocalService _userLocalService;

}