/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.internal.exporter;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporter;
import com.liferay.translation.info.field.TranslationInfoFieldChecker;
import com.liferay.translation.internal.util.XLIFFExporterUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "content.type=application/x-xliff+xml",
	service = TranslationInfoItemFieldValuesExporter.class
)
public class XLIFF12InfoFormTranslationExporter
	implements TranslationInfoItemFieldValuesExporter {

	@Override
	public InputStream exportInfoItemFieldValues(
			InfoItemFieldValues infoItemFieldValues, Locale sourceLocale,
			Locale targetLocale)
		throws IOException {

		Document document = SAXReaderUtil.createDocument();

		Element xliffElement = document.addElement(
			"xliff", "urn:oasis:names:tc:xliff:document:1.2");

		xliffElement.addAttribute("version", "1.2");

		Element fileElement = xliffElement.addElement("file");

		fileElement.addAttribute("datatype", "plaintext");

		InfoItemReference infoItemReference =
			infoItemFieldValues.getInfoItemReference();

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			return null;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)
				infoItemReference.getInfoItemIdentifier();

		String className = StringUtil.replace(
			infoItemReference.getClassName(), CharPool.POUND,
			CharPool.UNDERLINE);

		fileElement.addAttribute(
			"original",
			className + StringPool.COLON +
				classPKInfoItemIdentifier.getClassPK());

		fileElement.addAttribute(
			"source-language", LocaleUtil.toBCP47LanguageId(sourceLocale));
		fileElement.addAttribute(
			"target-language", LocaleUtil.toBCP47LanguageId(targetLocale));
		fileElement.addAttribute("tool", "Liferay");

		Element bodyElement = fileElement.addElement("body");

		Map<String, List<InfoFieldValue<Object>>> infoFieldValuesMap =
			new LinkedHashMap<>();

		for (InfoFieldValue<Object> infoFieldValue :
				infoItemFieldValues.getInfoFieldValues()) {

			InfoField infoField = infoFieldValue.getInfoField();

			if (_translationInfoFieldChecker.isTranslatable(infoField)) {
				List<InfoFieldValue<Object>> infoFieldValuesList =
					infoFieldValuesMap.computeIfAbsent(
						infoField.getUniqueId(), uniqueId -> new ArrayList<>());

				infoFieldValuesList.add(infoFieldValue);
			}
		}

		for (Map.Entry<String, List<InfoFieldValue<Object>>> entry :
				infoFieldValuesMap.entrySet()) {

			Element transUnitElement = bodyElement.addElement("trans-unit");

			transUnitElement.addAttribute("id", entry.getKey());

			_addCDATATransUnitContent(
				fileElement, entry.getValue(), sourceLocale, targetLocale,
				transUnitElement);
		}

		String xml = document.asXML();

		return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String getMimeType() {
		return "application/x-xliff+xml";
	}

	private void _addCDATATransUnitContent(
		Element fileElement, List<InfoFieldValue<Object>> infoFieldValues,
		Locale sourceLocale, Locale targetLocale, Element transUnitElement) {

		Element sourceElement = _addSourceElement(
			fileElement, transUnitElement);

		StringBundler sb = new StringBundler(infoFieldValues.size());

		for (InfoFieldValue<Object> infoFieldValue : infoFieldValues) {
			Object value = infoFieldValue.getValue(sourceLocale);

			sb.append((value != null) ? value.toString() : StringPool.BLANK);
		}

		sourceElement.addCDATA(_getStringValue(sb));

		if (infoFieldValues.size() > 1) {
			Element segSourceElement = transUnitElement.addElement(
				"seg-source");

			int mid = 0;

			for (InfoFieldValue<Object> infoFieldValue : infoFieldValues) {
				Element mrkElement = _addMrkElement(segSourceElement, mid++);

				mrkElement.addCDATA(
					(String)infoFieldValue.getValue(sourceLocale));
			}
		}

		Element targetElement = _addTargetElement(
			fileElement, transUnitElement);

		if (infoFieldValues.size() > 1) {
			int mid = 0;

			for (InfoFieldValue<Object> infoFieldValue : infoFieldValues) {
				XLIFFExporterUtil.addTargetValue(
					_addMrkElement(targetElement, mid++), infoFieldValue,
					targetLocale);
			}
		}
		else {
			XLIFFExporterUtil.addTargetValue(
				targetElement, infoFieldValues.get(0), targetLocale);
		}
	}

	private Element _addMrkElement(Element element, int mid) {
		Element mrkElement = element.addElement("mrk");

		mrkElement.addAttribute("mid", String.valueOf(mid));
		mrkElement.addAttribute("mtype", "seg");

		return mrkElement;
	}

	private Element _addSourceElement(
		Element fileElement, Element transUnitElement) {

		Element sourceElement = transUnitElement.addElement("source");

		sourceElement.addAttribute(
			"xml:lang", fileElement.attributeValue("source-language"));

		return sourceElement;
	}

	private Element _addTargetElement(
		Element fileElement, Element transUnitElement) {

		Element targetElement = transUnitElement.addElement("target");

		targetElement.addAttribute(
			"xml:lang", fileElement.attributeValue("target-language"));

		return targetElement;
	}

	private String _getStringValue(Object value) {
		if (value == null) {
			return null;
		}

		return value.toString();
	}

	@Reference
	private TranslationInfoFieldChecker _translationInfoFieldChecker;

}