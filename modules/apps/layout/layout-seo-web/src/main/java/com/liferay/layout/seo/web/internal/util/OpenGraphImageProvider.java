/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.web.internal.util;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.type.WebImage;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.model.LayoutSEOSite;
import com.liferay.layout.seo.service.LayoutSEOSiteLocalService;
import com.liferay.layout.seo.template.LayoutSEOTemplateProcessor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.Locale;

/**
 * @author Alejandro Tardín
 */
public class OpenGraphImageProvider {

	public OpenGraphImageProvider(
		DDMFieldLocalService ddmFieldLocalService,
		DDMStructureLocalService ddmStructureLocalService,
		DLAppLocalService dlAppLocalService,
		DLFileEntryMetadataLocalService dlFileEntryMetadataLocalService,
		DLURLHelper dlurlHelper,
		LayoutSEOSiteLocalService layoutSEOSiteLocalService,
		LayoutSEOTemplateProcessor layoutSEOTemplateProcessor, Portal portal) {

		_dlAppLocalService = dlAppLocalService;
		_dlurlHelper = dlurlHelper;
		_layoutSEOSiteLocalService = layoutSEOSiteLocalService;
		_layoutSEOTemplateProcessor = layoutSEOTemplateProcessor;

		_fileEntryMetadataOpenGraphTagsProvider =
			new FileEntryMetadataOpenGraphTagsProvider(
				ddmFieldLocalService, ddmStructureLocalService,
				dlFileEntryMetadataLocalService, portal);
	}

	public OpenGraphImage getOpenGraphImage(
		InfoItemFieldValues infoItemFieldValues, Layout layout,
		LayoutSEOEntry layoutSEOEntry, ThemeDisplay themeDisplay) {

		OpenGraphImage openGraphImage = _getMappedOpenGraphImage(
			infoItemFieldValues, layout, layoutSEOEntry, themeDisplay);

		if (openGraphImage == null) {
			return _getFileEntryOpenGraphImage(
				infoItemFieldValues, layout, layoutSEOEntry, themeDisplay);
		}

		return openGraphImage;
	}

	public interface OpenGraphImage {

		public String getAlt();

		public Iterable<KeyValuePair> getMetadataTagKeyValuePairs();

		public String getMimeType();

		public String getURL();

	}

	private String _getAbsoluteURL(ThemeDisplay themeDisplay, String url) {
		if (url.startsWith("http")) {
			return url;
		}

		return themeDisplay.getPortalURL() + url;
	}

	private OpenGraphImage _getFileEntryOpenGraphImage(
		InfoItemFieldValues infoItemFieldValues, Layout layout,
		LayoutSEOEntry layoutSEOEntry, ThemeDisplay themeDisplay) {

		try {
			long openGraphImageFileEntryId = _getOpenGraphImageFileEntryId(
				layout, layoutSEOEntry);

			if (openGraphImageFileEntryId == 0) {
				return null;
			}

			FileEntry fileEntry = _dlAppLocalService.getFileEntry(
				openGraphImageFileEntryId);

			if ((fileEntry == null) || fileEntry.isInTrash()) {
				return null;
			}

			Iterable<KeyValuePair> fileEntryMetadataOpenGraphTagKeyValuePairs =
				_fileEntryMetadataOpenGraphTagsProvider.
					getFileEntryMetadataOpenGraphTagKeyValuePairs(fileEntry);

			String imagePreviewURL = _dlurlHelper.getImagePreviewURL(
				fileEntry, themeDisplay);

			return new OpenGraphImage() {

				@Override
				public String getAlt() {
					return _getImageAltTagValue(
						infoItemFieldValues, layout, layoutSEOEntry,
						themeDisplay.getLocale());
				}

				@Override
				public Iterable<KeyValuePair> getMetadataTagKeyValuePairs() {
					return fileEntryMetadataOpenGraphTagKeyValuePairs;
				}

				@Override
				public String getMimeType() {
					return fileEntry.getMimeType();
				}

				@Override
				public String getURL() {
					return imagePreviewURL;
				}

			};
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private String _getImageAltTagValue(
		InfoItemFieldValues infoItemFieldValues, Layout layout,
		LayoutSEOEntry layoutSEOEntry, Locale locale) {

		String mappedImageAltTagValue = _getMappedStringValue(
			null, "openGraphImageAlt", infoItemFieldValues, layout, locale);

		if (Validator.isNotNull(mappedImageAltTagValue)) {
			return mappedImageAltTagValue;
		}

		if ((layoutSEOEntry != null) &&
			(layoutSEOEntry.getOpenGraphImageFileEntryId() > 0)) {

			return layoutSEOEntry.getOpenGraphImageAlt(locale);
		}

		LayoutSEOSite layoutSEOSite =
			_layoutSEOSiteLocalService.fetchLayoutSEOSiteByGroupId(
				layout.getGroupId());

		if ((layoutSEOSite != null) &&
			(layoutSEOSite.getOpenGraphImageFileEntryId() > 0)) {

			return layoutSEOSite.getOpenGraphImageAlt(locale);
		}

		String imageAltMappingFieldKey = layout.getTypeSettingsProperty(
			"mapped-openGraphImageAlt", null);

		if (Validator.isNull(imageAltMappingFieldKey)) {
			return null;
		}

		return _layoutSEOTemplateProcessor.processTemplate(
			imageAltMappingFieldKey, infoItemFieldValues, locale);
	}

	private OpenGraphImage _getMappedOpenGraphImage(
		InfoItemFieldValues infoItemFieldValues, Layout layout,
		LayoutSEOEntry layoutSEOEntry, ThemeDisplay themeDisplay) {

		Object mappedImageObject = _getMappedValue(
			null, "openGraphImage", infoItemFieldValues, layout,
			themeDisplay.getLocale());

		if ((!(mappedImageObject instanceof String) ||
			 !Validator.isUri((String)mappedImageObject)) &&
			!(mappedImageObject instanceof WebImage)) {

			return null;
		}

		return new OpenGraphImage() {

			@Override
			public String getAlt() {
				String openGraphImageAlt = _getImageAltTagValue(
					infoItemFieldValues, layout, layoutSEOEntry,
					themeDisplay.getLocale());

				if (Validator.isNotNull(openGraphImageAlt)) {
					return openGraphImageAlt;
				}

				if (!(mappedImageObject instanceof WebImage)) {
					return null;
				}

				WebImage mappedWebImage = (WebImage)mappedImageObject;

				InfoLocalizedValue<String> altInfoLocalizedValue =
					mappedWebImage.getAltInfoLocalizedValue();

				if (altInfoLocalizedValue == null) {
					return null;
				}

				return altInfoLocalizedValue.getValue(themeDisplay.getLocale());
			}

			@Override
			public Iterable<KeyValuePair> getMetadataTagKeyValuePairs() {
				return Collections.emptyList();
			}

			@Override
			public String getMimeType() {
				return null;
			}

			@Override
			public String getURL() {
				String url = StringPool.BLANK;

				if (mappedImageObject instanceof WebImage) {
					WebImage mappedWebImage = (WebImage)mappedImageObject;

					url = mappedWebImage.getURL();
				}
				else {
					url = mappedImageObject.toString();
				}

				return _getAbsoluteURL(themeDisplay, url);
			}

		};
	}

	private String _getMappedStringValue(
		String defaultFieldName, String fieldName,
		InfoItemFieldValues infoItemFieldValues, Layout layout, Locale locale) {

		Object mappedValueObject = _getMappedValue(
			defaultFieldName, fieldName, infoItemFieldValues, layout, locale);

		if (mappedValueObject != null) {
			return String.valueOf(mappedValueObject);
		}

		return null;
	}

	private Object _getMappedValue(
		String defaultFieldName, String fieldName,
		InfoItemFieldValues infoItemFieldValues, Layout layout, Locale locale) {

		if (infoItemFieldValues == null) {
			return null;
		}

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue(
				layout.getTypeSettingsProperty(
					"mapped-" + fieldName, defaultFieldName));

		if (infoFieldValue != null) {
			return infoFieldValue.getValue(locale);
		}

		return null;
	}

	private long _getOpenGraphImageFileEntryId(
		Layout layout, LayoutSEOEntry layoutSEOEntry) {

		if ((layoutSEOEntry != null) &&
			(layoutSEOEntry.getOpenGraphImageFileEntryId() > 0)) {

			return layoutSEOEntry.getOpenGraphImageFileEntryId();
		}

		LayoutSEOSite layoutSEOSite =
			_layoutSEOSiteLocalService.fetchLayoutSEOSiteByGroupId(
				layout.getGroupId());

		if ((layoutSEOSite == null) ||
			(layoutSEOSite.getOpenGraphImageFileEntryId() == 0)) {

			return 0;
		}

		return layoutSEOSite.getOpenGraphImageFileEntryId();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenGraphImageProvider.class);

	private final DLAppLocalService _dlAppLocalService;
	private final DLURLHelper _dlurlHelper;
	private final FileEntryMetadataOpenGraphTagsProvider
		_fileEntryMetadataOpenGraphTagsProvider;
	private final LayoutSEOSiteLocalService _layoutSEOSiteLocalService;
	private final LayoutSEOTemplateProcessor _layoutSEOTemplateProcessor;

}