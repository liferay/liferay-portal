/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.background.image;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.entry.processor.util.AnalyticsAttributesUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DocumentFragmentEntryProcessor;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.type.WebImage;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "fragment.entry.processor.priority:Integer=5",
	service = DocumentFragmentEntryProcessor.class
)
public class BackgroundImageDocumentFragmentEntryProcessor
	implements DocumentFragmentEntryProcessor {

	@Override
	public void processFragmentEntryLinkHTML(
			FragmentEntryLink fragmentEntryLink, Document document,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		JSONObject jsonObject = fragmentEntryLink.getEditableValuesJSONObject();

		JSONObject editableValuesJSONObject = jsonObject.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR);

		if (editableValuesJSONObject == null) {
			return;
		}

		Map<InfoItemReference, InfoItemFieldValues> infoDisplaysFieldValues =
			new HashMap<>();

		for (Element element :
				document.getElementsByAttribute(
					"data-lfr-background-image-id")) {

			String id = element.attr("data-lfr-background-image-id");

			if (!editableValuesJSONObject.has(id)) {
				continue;
			}

			JSONObject editableValueJSONObject =
				editableValuesJSONObject.getJSONObject(id);

			String value = StringPool.BLANK;

			Object fieldValue = _fragmentEntryProcessorHelper.getFieldValue(
				editableValueJSONObject, infoDisplaysFieldValues,
				fragmentEntryProcessorContext);

			if (fieldValue != null) {
				value = _getImageURL(fieldValue);
			}

			if (Validator.isNull(value)) {
				value = _fragmentEntryProcessorHelper.getEditableValue(
					editableValueJSONObject,
					fragmentEntryProcessorContext.getLocale());
			}

			if (Validator.isNotNull(value)) {
				long fileEntryId = 0;

				if (JSONUtil.isJSONObject(value)) {
					JSONObject valueJSONObject = _jsonFactory.createJSONObject(
						value);

					fileEntryId = valueJSONObject.getLong("fileEntryId");

					if (fileEntryId == 0) {
						fileEntryId =
							_fragmentEntryProcessorHelper.getFileEntryId(
								valueJSONObject.getString("className"),
								valueJSONObject.getLong("classPK"));
					}

					value = _getImagePreviewURL(
						valueJSONObject.getString("url", value), fileEntryId);
				}

				StringBundler sb = new StringBundler(6);

				sb.append("background-image: url(");
				sb.append(value);
				sb.append("); background-size: cover;");

				if (fileEntryId == 0) {
					fileEntryId = _fragmentEntryProcessorHelper.getFileEntryId(
						editableValueJSONObject.getLong("classNameId"),
						editableValueJSONObject.getLong("classPK"),
						editableValueJSONObject.getString("fieldId"),
						fragmentEntryProcessorContext.getLocale());
				}

				InfoItemReference contextInfoItemReference =
					fragmentEntryProcessorContext.getContextInfoItemReference();

				if ((fileEntryId == 0) && (contextInfoItemReference != null)) {
					fileEntryId = _fragmentEntryProcessorHelper.getFileEntryId(
						contextInfoItemReference,
						editableValueJSONObject.getString("collectionFieldId"),
						fragmentEntryProcessorContext.getLocale());

					if (fileEntryId == 0) {
						fileEntryId =
							_fragmentEntryProcessorHelper.getFileEntryId(
								contextInfoItemReference,
								editableValueJSONObject.getString(
									"mappedField"),
								fragmentEntryProcessorContext.getLocale());
					}
				}

				if (fileEntryId > 0) {
					sb.append(" --background-image-file-entry-id: ");
					sb.append(fileEntryId);
					sb.append(StringPool.SEMICOLON);
				}

				element.attr("style", sb.toString());
			}

			if (fragmentEntryProcessorContext.isPreviewMode() ||
				fragmentEntryProcessorContext.isViewMode()) {

				element.removeAttr("data-lfr-background-image-id");
			}

			if (FeatureFlagManagerUtil.isEnabled(
					fragmentEntryLink.getCompanyId(), "LPD-39437") &&
				fragmentEntryProcessorContext.isViewMode()) {

				AnalyticsAttributesUtil.addAnalyticsAttributes(
					editableValueJSONObject, element,
					fragmentEntryProcessorContext,
					_fragmentEntryProcessorHelper, infoDisplaysFieldValues,
					_infoItemServiceRegistry);
			}
		}
	}

	private String _getImagePreviewURL(String defaultValue, long fileEntryId) {
		try {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			String mimeType = fileEntry.getMimeType();

			if (mimeType.startsWith("image")) {
				return _dlURLHelper.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), null,
					StringPool.BLANK);
			}

			return _dlURLHelper.getImagePreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return defaultValue;
	}

	private String _getImageURL(Object fieldValue) {
		if (fieldValue instanceof JSONObject) {
			JSONObject fieldValueJSONObject = (JSONObject)fieldValue;

			return fieldValueJSONObject.getString("url");
		}

		if (fieldValue instanceof WebImage) {
			WebImage webImage = (WebImage)fieldValue;

			return String.valueOf(webImage.toJSONObject());
		}

		return String.valueOf(fieldValue);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BackgroundImageDocumentFragmentEntryProcessor.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private FragmentEntryProcessorHelper _fragmentEntryProcessorHelper;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

}