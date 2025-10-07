/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.editable.internal.parser;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.fragment.entry.processor.editable.parser.EditableElementParser;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.type.WebImage;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(property = "type=image", service = EditableElementParser.class)
public class ImageEditableElementParser extends BaseEditableElementParser {

	@Override
	public JSONObject getFieldTemplateConfigJSONObject(
		String fieldName, Locale locale, Object fieldValue) {

		String alt = StringPool.BLANK;
		Object fileEntryId = 0;

		if (fieldValue instanceof JSONObject) {
			JSONObject fieldValueJSONObject = (JSONObject)fieldValue;

			alt = fieldValueJSONObject.getString("alt");

			if (Validator.isNotNull(alt) && JSONUtil.isJSONObject(alt)) {
				JSONObject altJSONObject = fieldValueJSONObject.getJSONObject(
					"alt");

				alt = altJSONObject.getString(LocaleUtil.toLanguageId(locale));
			}

			if (fieldValueJSONObject.has("className") &&
				fieldValueJSONObject.has("classPK")) {

				fileEntryId = _fragmentEntryProcessorHelper.getFileEntryId(
					fieldValueJSONObject.getString("className"),
					fieldValueJSONObject.getLong("classPK"));
			}
			else if (fieldValueJSONObject.has("fileEntryId")) {
				fileEntryId = fieldValueJSONObject.getLong("fileEntryId");
			}
		}
		else if (fieldValue instanceof WebImage) {
			WebImage webImage = (WebImage)fieldValue;

			InfoLocalizedValue<String> infoLocalizedValue =
				webImage.getAltInfoLocalizedValue();

			if (infoLocalizedValue != null) {
				alt = infoLocalizedValue.getValue(locale);
			}

			fileEntryId = _fragmentEntryProcessorHelper.getFileEntryId(
				webImage);
		}

		return JSONUtil.put(
			"alt", alt
		).put(
			"fileEntryId", fileEntryId
		);
	}

	@Override
	public String getValue(Element element) {
		List<Element> elements = element.getElementsByTag("img");

		if (ListUtil.isEmpty(elements)) {
			return StringPool.BLANK;
		}

		Element replaceableElement = elements.get(0);

		String src = replaceableElement.attr("src");

		if (Validator.isNull(src.trim())) {
			return StringBundler.concat(
				"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAJ",
				"CAYAAAA7KqwyAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs",
				"4c6QAAAARnQU1BAACxjwv8YQUAAAAkSURBVHgB7cxBEQAACAIwtH8P",
				"zw52kxD8OBZgNXsPQUOUwCIgAz0DHTyygaAAAAAASUVORK5CYII=");
		}

		return src;
	}

	@Override
	public String parseFieldValue(Object fieldValue) {
		if (fieldValue instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject)fieldValue;

			return GetterUtil.getString(jsonObject.getString("url"));
		}
		else if ((fieldValue instanceof String) &&
				 Validator.isNotNull(fieldValue)) {

			return GetterUtil.getString(fieldValue);
		}
		else if (fieldValue instanceof WebImage) {
			WebImage webImage = (WebImage)fieldValue;

			return GetterUtil.getString(webImage.getURL());
		}

		return StringPool.BLANK;
	}

	@Override
	public void replace(Element element, String value) {
		replace(element, value, null);
	}

	@Override
	public void replace(
		Element element, String value, JSONObject configJSONObject) {

		List<Element> elements = element.getElementsByTag("img");

		if (ListUtil.isEmpty(elements)) {
			return;
		}

		Element replaceableElement = elements.get(0);

		long fileEntryId = 0;

		if (JSONUtil.isJSONObject(value)) {
			try {
				JSONObject jsonObject = _jsonFactory.createJSONObject(value);

				fileEntryId = jsonObject.getLong("fileEntryId");
				value = jsonObject.getString("url");
			}
			catch (JSONException jsonException) {
				_log.error(
					"Unable to parse JSON value " + value, jsonException);

				value = StringPool.BLANK;
			}
		}
		else {
			fileEntryId = configJSONObject.getLong("fileEntryId");
		}

		value = value.trim();

		if (fileEntryId > 0) {
			String imagePreviewURL = _getImagePreviewURL(fileEntryId);

			if (Validator.isNotNull(imagePreviewURL)) {
				value = imagePreviewURL;
			}

			replaceableElement.attr(
				"data-fileentryid", String.valueOf(fileEntryId));

			if ((configJSONObject != null) &&
				configJSONObject.has("imageConfiguration")) {

				_setImageConfiguration(
					replaceableElement,
					configJSONObject.getJSONObject("imageConfiguration"));
			}
		}

		Matcher matcher = _pattern.matcher(replaceableElement.attr("src"));

		if (Validator.isNotNull(value) && !matcher.matches()) {
			replaceableElement.attr("src", HtmlUtil.unescape(value));
		}

		if (configJSONObject == null) {
			return;
		}

		String alt = configJSONObject.getString("alt");

		if (Validator.isNotNull(alt) && JSONUtil.isJSONObject(alt)) {
			JSONObject altJSONObject = configJSONObject.getJSONObject("alt");

			String languageId = LocaleUtil.toLanguageId(
				LocaleThreadLocal.getThemeDisplayLocale());

			if (!altJSONObject.has(languageId)) {
				languageId = LocaleUtil.toLanguageId(
					LocaleThreadLocal.getSiteDefaultLocale());
			}

			alt = altJSONObject.getString(languageId);
		}

		if (Validator.isNotNull(alt)) {
			replaceableElement.attr(
				"alt", StringUtil.trim(HtmlUtil.unescape(alt)));
		}

		if (configJSONObject.getBoolean("lazyLoading")) {
			replaceableElement.attr("loading", "lazy");
		}

		String imageLink = configJSONObject.getString("imageLink");

		if (Validator.isNull(imageLink)) {
			return;
		}

		String imageTarget = configJSONObject.getString("imageTarget");

		if (StringUtil.equalsIgnoreCase(imageTarget, "_parent") ||
			StringUtil.equalsIgnoreCase(imageTarget, "_top")) {

			imageTarget = "_self";
		}

		Element linkElement = new Element("a");

		linkElement.attr("href", imageLink);

		if (Validator.isNotNull(imageTarget)) {
			linkElement.attr("target", imageTarget);
		}

		linkElement.html(element.html());

		element.html(linkElement.outerHtml());
	}

	@Override
	public void validate(Element element) throws FragmentEntryContentException {
		List<Element> elements = element.getElementsByTag("img");

		if (elements.size() != 1) {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", getClass());

			throw new FragmentEntryContentException(
				_language.format(
					resourceBundle,
					"each-editable-image-element-must-contain-an-img-tag",
					new Object[] {"<em>", "</em>"}, false));
		}

		super.validate(element);
	}

	private String _getImagePreviewURL(long fileEntryId) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return StringPool.BLANK;
		}

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if (themeDisplay == null) {
			return StringPool.BLANK;
		}

		try {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			String mimeType = fileEntry.getMimeType();

			if (mimeType.startsWith("image")) {
				return _dlURLHelper.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), null,
					StringPool.BLANK);
			}

			return _dlURLHelper.getImagePreviewURL(
				fileEntry, fileEntry.getFileVersion(), themeDisplay,
				StringPool.BLANK, false, false);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	private void _setImageConfiguration(
		Element element, JSONObject imageConfigurationJSONObject) {

		for (ViewportSize viewportSize : _viewportSizes) {
			String imageConfiguration = imageConfigurationJSONObject.getString(
				viewportSize.getViewportSizeId());

			if (Validator.isNull(imageConfiguration) ||
				Objects.equals(imageConfiguration, "auto")) {

				continue;
			}

			element.attr(
				"data-" + viewportSize.getViewportSizeId() + "-configuration",
				imageConfiguration);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ImageEditableElementParser.class);

	private static final Pattern _pattern = Pattern.compile(
		"\\[resources:(.+?)\\]");
	private static final ViewportSize[] _viewportSizes = ViewportSize.values();

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private FragmentEntryProcessorHelper _fragmentEntryProcessorHelper;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}