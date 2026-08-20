/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.content.compatibility.converter;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.journal.article.dynamic.data.mapping.form.field.type.constants.JournalArticleDDMFormFieldTypeConstants;
import com.liferay.journal.content.compatibility.converter.JournalContentCompatibilityConverter;
import com.liferay.layout.dynamic.data.mapping.form.field.type.constants.LayoutDDMFormFieldTypeConstants;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = JournalContentCompatibilityConverter.class)
public class JournalContentCompatibilityConverterImpl
	implements JournalContentCompatibilityConverter {

	@Override
	public String convert(String content) {
		try {
			Document document = _convert(SAXReaderUtil.read(content));

			return document.formattedString(StringPool.DOUBLE_SPACE);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return content;
		}
	}

	private Document _convert(Document document) {
		if (_isLatestVersion(document)) {
			return document;
		}

		Element rootElement = document.getRootElement();

		rootElement.addAttribute("version", _LATEST_CONTENT_VERSION);

		_convertDDMFields(_getDefaultLocale(document), rootElement);

		if (!_hasNestedFields(rootElement)) {
			return document;
		}

		Document oldDocument = document.clone();

		document = SAXReaderUtil.createDocument();

		Element newRootElement = document.addElement("root");

		Element oldRootElement = oldDocument.getRootElement();

		newRootElement.addAttribute(
			"available-locales",
			oldRootElement.attributeValue("available-locales"));
		newRootElement.addAttribute(
			"default-locale", oldRootElement.attributeValue("default-locale"));

		newRootElement.addAttribute("version", _LATEST_CONTENT_VERSION);

		_convertNestedFields(newRootElement, oldDocument.getRootElement());

		return document;
	}

	private void _convertDDMFields(Locale defaultLocale, Element element) {
		String type = element.attributeValue("type");

		if (Validator.isNotNull(type)) {
			element.addAttribute("type", _convertDDMFieldType(type));
		}

		_convertDDMFieldValue(element, type, defaultLocale);

		List<Element> dynamicElements = element.elements("dynamic-element");

		for (Element dynamicElement : dynamicElements) {
			_convertDDMFields(defaultLocale, dynamicElement);
		}
	}

	private String _convertDDMFieldType(String ddmFieldType) {
		if (Objects.equals(ddmFieldType, "boolean")) {
			return DDMFormFieldTypeConstants.CHECKBOX;
		}

		if (Objects.equals(ddmFieldType, "ddm-color")) {
			return DDMFormFieldTypeConstants.COLOR;
		}

		if (Objects.equals(ddmFieldType, "ddm-date")) {
			return DDMFormFieldTypeConstants.DATE;
		}

		if (Objects.equals(ddmFieldType, "ddm-decimal")) {
			return DDMFormFieldTypeConstants.NUMERIC;
		}

		if (Objects.equals(ddmFieldType, "ddm-geolocation")) {
			return DDMFormFieldTypeConstants.GEOLOCATION;
		}

		if (Objects.equals(ddmFieldType, "ddm-journal-article")) {
			return JournalArticleDDMFormFieldTypeConstants.JOURNAL_ARTICLE;
		}

		if (Objects.equals(ddmFieldType, "ddm-image")) {
			return DDMFormFieldTypeConstants.IMAGE;
		}

		if (Objects.equals(ddmFieldType, "ddm-integer")) {
			return DDMFormFieldTypeConstants.NUMERIC;
		}

		if (Objects.equals(ddmFieldType, "ddm-link-to-page")) {
			return LayoutDDMFormFieldTypeConstants.LINK_TO_LAYOUT;
		}

		if (Objects.equals(ddmFieldType, "ddm-number")) {
			return DDMFormFieldTypeConstants.NUMERIC;
		}

		if (Objects.equals(ddmFieldType, "document_library")) {
			return DDMFormFieldTypeConstants.DOCUMENT_LIBRARY;
		}

		if (Objects.equals(ddmFieldType, "text_area")) {
			return DDMFormFieldTypeConstants.RICH_TEXT;
		}

		if (Objects.equals(ddmFieldType, "text_box")) {
			return DDMFormFieldTypeConstants.TEXT;
		}

		if (Objects.equals(ddmFieldType, "list")) {
			return DDMFormFieldTypeConstants.SELECT;
		}

		if (Objects.equals(ddmFieldType, "selection_break")) {
			return DDMFormFieldTypeConstants.SEPARATOR;
		}

		if (Objects.equals(ddmFieldType, "text")) {
			return DDMFormFieldTypeConstants.TEXT;
		}

		return ddmFieldType;
	}

	private void _convertDDMFieldValue(
		Element element, String ddmFieldType, Locale defaultLocale) {

		List<Element> dynamicContentElements = element.elements(
			"dynamic-content");

		for (Element dynamicContentElement : dynamicContentElements) {
			if (Objects.equals(ddmFieldType, "list") ||
				Objects.equals(ddmFieldType, "multi-list")) {

				continue;
			}

			String text = dynamicContentElement.getText();

			dynamicContentElement.clearContent();

			dynamicContentElement.addCDATA(
				_convertDDMFieldValue(defaultLocale, ddmFieldType, text));
		}
	}

	private String _convertDDMFieldValue(
		Locale defaultLocale, String ddmFieldType, String value) {

		if (Objects.equals(ddmFieldType, "ddm-link-to-page") ||
			Objects.equals(
				ddmFieldType, LayoutDDMFormFieldTypeConstants.LINK_TO_LAYOUT)) {

			return _convertLinkToLayoutValue(defaultLocale, value);
		}

		return value;
	}

	private String _convertLinkToLayoutValue(
		Locale defaultLocale, String value) {

		if (JSONUtil.isJSONObject(value)) {
			return value;
		}

		String[] values = StringUtil.split(value, CharPool.AT);

		if (ArrayUtil.isEmpty(values)) {
			return StringPool.BLANK;
		}

		JSONObject jsonObject = _jsonFactorys.createJSONObject();

		long layoutId = GetterUtil.getLong(values[0]);
		boolean privateLayout = !Objects.equals(values[1], "public");

		if (values.length > 2) {
			long groupId = GetterUtil.getLong(values[2]);

			jsonObject.put("groupId", groupId);

			Layout layout = _layoutLocalService.fetchLayout(
				groupId, privateLayout, layoutId);

			if (layout != null) {
				jsonObject.put(
					"id", layout.getUuid()
				).put(
					"name", layout.getName(defaultLocale)
				).put(
					"value", layout.getFriendlyURL(defaultLocale)
				);
			}
		}

		jsonObject.put(
			"layoutId", layoutId
		).put(
			"privateLayout", privateLayout
		);

		return jsonObject.toString();
	}

	private void _convertNestedFields(Element newElement, Element oldElement) {
		for (Element dynamicElement : oldElement.elements("dynamic-element")) {
			List<Element> nestedFieldsElements = dynamicElement.elements(
				"dynamic-element");

			if (nestedFieldsElements.isEmpty()) {
				newElement.add(dynamicElement.createCopy());

				continue;
			}

			Element fieldSetDynamicElement = newElement.addElement(
				"dynamic-element");

			fieldSetDynamicElement.addAttribute("index-type", StringPool.BLANK);
			fieldSetDynamicElement.addAttribute(
				"instance-id", StringUtil.randomString());
			fieldSetDynamicElement.addAttribute(
				"name", dynamicElement.attributeValue("name") + "FieldSet");

			Element newDynamicElement = fieldSetDynamicElement.addElement(
				"dynamic-element");

			for (Attribute attribute : dynamicElement.attributes()) {
				newDynamicElement.addAttribute(
					attribute.getName(), attribute.getValue());
			}

			for (Element dynamicContent :
					dynamicElement.elements("dynamic-content")) {

				newDynamicElement.add(dynamicContent.createCopy());
			}

			_convertNestedFields(fieldSetDynamicElement, dynamicElement);
		}
	}

	private Locale _getDefaultLocale(Document document) {
		Element rootElement = document.getRootElement();

		String defaultLanguageId = rootElement.attributeValue("default-locale");

		if (defaultLanguageId == null) {
			return LocaleUtil.getSiteDefault();
		}

		return LocaleUtil.fromLanguageId(defaultLanguageId);
	}

	private boolean _hasNestedFields(Element element) {
		for (Element dynamicElement : element.elements("dynamic-element")) {
			List<Element> nestedFieldsElements = dynamicElement.elements(
				"dynamic-element");

			if (!nestedFieldsElements.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	private boolean _isLatestVersion(Document document) {
		Element rootElement = document.getRootElement();

		String version = rootElement.attributeValue("version");

		if (Validator.isNotNull(version) &&
			Objects.equals(version, _LATEST_CONTENT_VERSION)) {

			return true;
		}

		return false;
	}

	private static final String _LATEST_CONTENT_VERSION = "1.0";

	private static final Log _log = LogFactoryUtil.getLog(
		JournalContentCompatibilityConverterImpl.class);

	@Reference
	private JSONFactory _jsonFactorys;

	@Reference(unbind = "-")
	private LayoutLocalService _layoutLocalService;

}