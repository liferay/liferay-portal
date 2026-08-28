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
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporter;
import com.liferay.translation.info.field.TranslationInfoFieldChecker;
import com.liferay.translation.internal.util.XLIFFExporterUtil;
import com.liferay.translation.internal.util.XLIFFInlineCodeUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.Part;
import net.sf.okapi.lib.xliff2.core.Segment;
import net.sf.okapi.lib.xliff2.core.StartFileData;
import net.sf.okapi.lib.xliff2.core.Unit;
import net.sf.okapi.lib.xliff2.writer.XLIFFWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "content.type=application/xliff+xml",
	service = TranslationInfoItemFieldValuesExporter.class
)
public class XLIFF20InfoFormTranslationExporter
	implements TranslationInfoItemFieldValuesExporter {

	@Override
	public InputStream exportInfoItemFieldValues(
			InfoItemFieldValues infoItemFieldValues, Locale sourceLocale,
			Locale targetLocale)
		throws IOException {

		InfoItemReference infoItemReference =
			infoItemFieldValues.getInfoItemReference();

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			return null;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)infoItemIdentifier;

		String className = StringUtil.replace(
			infoItemReference.getClassName(), CharPool.POUND,
			CharPool.UNDERLINE);

		String fileId =
			className + StringPool.COLON +
				classPKInfoItemIdentifier.getClassPK();

		Map<String, List<InfoFieldValue<Object>>> infoFieldValuesMap =
			_getInfoFieldValuesMap(infoItemFieldValues);

		if (_hasProtectedHTMLInfoField(infoFieldValuesMap)) {
			return _exportInlineCodes(
				fileId, infoFieldValuesMap, sourceLocale, targetLocale);
		}

		return _exportCDATA(
			fileId, infoFieldValuesMap, sourceLocale, targetLocale);
	}

	@Override
	public String getMimeType() {
		return "application/xliff+xml";
	}

	private void _addInfoFieldValue(
		InfoFieldValue<Object> infoFieldValue, Element unitElement,
		Locale sourceLocale, Locale targetLocale) {

		Element segmentElement = unitElement.addElement("segment");

		Element sourceElement = segmentElement.addElement("source");

		sourceElement.addCDATA(
			_getStringValue(infoFieldValue.getValue(sourceLocale)));

		Element targetElement = segmentElement.addElement("target");

		XLIFFExporterUtil.addTargetValue(
			targetElement, infoFieldValue, targetLocale);
	}

	private InputStream _exportCDATA(
			String fileId,
			Map<String, List<InfoFieldValue<Object>>> infoFieldValuesMap,
			Locale sourceLocale, Locale targetLocale)
		throws IOException {

		Document document = SAXReaderUtil.createDocument();

		Element xliffElement = document.addElement(
			"xliff", "urn:oasis:names:tc:xliff:document:2.0");

		xliffElement.addAttribute(
			"srcLang", LocaleUtil.toBCP47LanguageId(sourceLocale));
		xliffElement.addAttribute(
			"trgLang", LocaleUtil.toBCP47LanguageId(targetLocale));
		xliffElement.addAttribute("version", "2.0");

		Element fileElement = xliffElement.addElement("file");

		fileElement.addAttribute("id", fileId);

		for (Map.Entry<String, List<InfoFieldValue<Object>>> entry :
				infoFieldValuesMap.entrySet()) {

			Element unitElement = fileElement.addElement("unit");

			unitElement.addAttribute("id", entry.getKey());

			for (InfoFieldValue<Object> infoFieldValue : entry.getValue()) {
				_addInfoFieldValue(
					infoFieldValue, unitElement, sourceLocale, targetLocale);
			}
		}

		String formattedString = document.formattedString();

		return new ByteArrayInputStream(
			formattedString.getBytes(StandardCharsets.UTF_8));
	}

	private InputStream _exportInlineCodes(
			String fileId,
			Map<String, List<InfoFieldValue<Object>>> infoFieldValuesMap,
			Locale sourceLocale, Locale targetLocale)
		throws IOException {

		StringWriter stringWriter = new StringWriter();

		XLIFFWriter xliffWriter = new XLIFFWriter();

		xliffWriter.setLineBreak(StringPool.NEW_LINE);
		xliffWriter.setUseIndentation(true);
		xliffWriter.setUseInsignificantParts(false);
		xliffWriter.setWithOriginalData(true);

		xliffWriter.create(
			stringWriter, LocaleUtil.toBCP47LanguageId(sourceLocale),
			LocaleUtil.toBCP47LanguageId(targetLocale));

		xliffWriter.writeStartFile(new StartFileData(fileId));

		for (Map.Entry<String, List<InfoFieldValue<Object>>> entry :
				infoFieldValuesMap.entrySet()) {

			xliffWriter.writeUnit(
				_getUnit(
					entry.getKey(), entry.getValue(), sourceLocale,
					targetLocale));
		}

		xliffWriter.writeEndFile();
		xliffWriter.writeEndDocument();
		xliffWriter.close();

		String xliff = stringWriter.toString();

		return new ByteArrayInputStream(xliff.getBytes(StandardCharsets.UTF_8));
	}

	private Map<String, List<InfoFieldValue<Object>>> _getInfoFieldValuesMap(
		InfoItemFieldValues infoItemFieldValues) {

		Map<String, List<InfoFieldValue<Object>>> infoFieldValuesMap =
			new LinkedHashMap<>();

		for (InfoFieldValue<Object> infoFieldValue :
				infoItemFieldValues.getInfoFieldValues()) {

			InfoField infoField = infoFieldValue.getInfoField();

			if (_translationInfoFieldChecker.isTranslatable(infoField)) {
				List<InfoFieldValue<Object>> infoFieldValues =
					infoFieldValuesMap.computeIfAbsent(
						infoField.getUniqueId(), uniqueId -> new ArrayList<>());

				infoFieldValues.add(infoFieldValue);
			}
		}

		return infoFieldValuesMap;
	}

	private String _getStringValue(Object value) {
		if (value == null) {
			return StringPool.BLANK;
		}

		return value.toString();
	}

	private Unit _getUnit(
		String id, List<InfoFieldValue<Object>> infoFieldValues,
		Locale sourceLocale, Locale targetLocale) {

		Unit unit = new Unit(id);

		InfoFieldValue<Object> firstInfoFieldValue = infoFieldValues.get(0);

		boolean protectedHTMLInfoField =
			XLIFFExporterUtil.isProtectedHTMLInfoField(
				firstInfoFieldValue.getInfoField());

		int startId = 1;

		for (InfoFieldValue<Object> infoFieldValue : infoFieldValues) {
			Segment segment = unit.appendSegment();

			Fragment sourceFragment = segment.getSource();
			Fragment targetFragment = segment.getTarget(
				Part.GetTarget.CREATE_EMPTY);

			String sourceStringValue = _getStringValue(
				infoFieldValue.getValue(sourceLocale));
			String targetStringValue = _getStringValue(
				XLIFFExporterUtil.getTargetStringValue(
					infoFieldValue, targetLocale));

			if (!protectedHTMLInfoField) {
				sourceFragment.append(sourceStringValue);
				targetFragment.append(targetStringValue);

				continue;
			}

			TextFragment sourceTextFragment =
				XLIFFInlineCodeUtil.toTextFragment(sourceStringValue);
			TextFragment targetTextFragment =
				XLIFFInlineCodeUtil.toTextFragment(targetStringValue);

			startId = XLIFFInlineCodeUtil.renumberCodes(
				startId, sourceTextFragment, targetTextFragment);

			XLIFFInlineCodeUtil.appendXLIFF20InlineCodes(
				sourceFragment, sourceTextFragment);
			XLIFFInlineCodeUtil.appendXLIFF20InlineCodes(
				targetFragment, targetTextFragment);
		}

		return unit;
	}

	private boolean _hasProtectedHTMLInfoField(
		Map<String, List<InfoFieldValue<Object>>> infoFieldValuesMap) {

		if (!FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-102730")) {

			return false;
		}

		for (List<InfoFieldValue<Object>> infoFieldValues :
				infoFieldValuesMap.values()) {

			InfoFieldValue<Object> firstInfoFieldValue = infoFieldValues.get(0);

			if (XLIFFExporterUtil.isProtectedHTMLInfoField(
					firstInfoFieldValue.getInfoField())) {

				return true;
			}
		}

		return false;
	}

	@Reference
	private TranslationInfoFieldChecker _translationInfoFieldChecker;

}