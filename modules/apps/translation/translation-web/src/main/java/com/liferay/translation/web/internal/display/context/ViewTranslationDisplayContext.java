/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.display.context;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.HTMLInfoFieldType;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.translation.info.field.TranslationInfoFieldChecker;
import com.liferay.translation.snapshot.TranslationSnapshot;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Adolfo Pérez
 */
public class ViewTranslationDisplayContext {

	public ViewTranslationDisplayContext(
		HttpServletRequest httpServletRequest, InfoForm infoForm,
		TranslationInfoFieldChecker translationInfoFieldChecker,
		TranslationSnapshot translationSnapshot) {

		_httpServletRequest = httpServletRequest;
		_infoForm = infoForm;
		_translationInfoFieldChecker = translationInfoFieldChecker;
		_translationSnapshot = translationSnapshot;
	}

	public boolean getBooleanValue(
		InfoField infoField,
		InfoFieldType.Attribute<TextInfoFieldType, Boolean> attribute) {

		return GetterUtil.getBoolean(infoField.getAttribute(attribute));
	}

	public String getInfoFieldLabel(InfoField infoField) {
		InfoLocalizedValue<String> labelInfoLocalizedValue =
			infoField.getLabelInfoLocalizedValue();

		return labelInfoLocalizedValue.getValue(
			PortalUtil.getLocale(_httpServletRequest));
	}

	public List<InfoField> getInfoFields(InfoFieldSetEntry infoFieldSetEntry) {
		if (infoFieldSetEntry instanceof InfoField) {
			InfoField infoField = (InfoField)infoFieldSetEntry;

			if (_translationInfoFieldChecker.isTranslatable(infoField)) {
				return Arrays.asList(infoField);
			}
		}
		else if (infoFieldSetEntry instanceof InfoFieldSet) {
			InfoFieldSet infoFieldSet = (InfoFieldSet)infoFieldSetEntry;

			return ListUtil.filter(
				infoFieldSet.getAllInfoFields(),
				_translationInfoFieldChecker::isTranslatable);
		}

		return Collections.emptyList();
	}

	public List<InfoFieldSetEntry> getInfoFieldSetEntries() {
		return _infoForm.getInfoFieldSetEntries();
	}

	public String getInfoFieldSetLabel(
		InfoFieldSetEntry infoFieldSetEntry, Locale locale) {

		if (infoFieldSetEntry instanceof InfoFieldSet) {
			InfoFieldSet infoFieldSet = (InfoFieldSet)infoFieldSetEntry;

			return infoFieldSet.getLabel(locale);
		}

		return null;
	}

	public String getLanguageIdTitle(String languageId) {
		return StringUtil.replace(
			languageId, CharPool.UNDERLINE, CharPool.DASH);
	}

	public String getSourceLanguageId() {
		return LanguageUtil.getLanguageId(getSourceLocale());
	}

	public Locale getSourceLocale() {
		return _translationSnapshot.getSourceLocale();
	}

	public List<String> getStringValues(InfoField infoField, Locale locale) {
		List<String> stringValues = new ArrayList<>();

		InfoItemFieldValues infoItemFieldValues =
			_translationSnapshot.getInfoItemFieldValues();

		for (InfoFieldValue<Object> infoFieldValue :
				infoItemFieldValues.getInfoFieldValues(
					infoField.getUniqueId())) {

			stringValues.add(
				GetterUtil.getString(infoFieldValue.getValue(locale)));
		}

		return stringValues;
	}

	public String getTargetLanguageId() {
		return LanguageUtil.getLanguageId(getTargetLocale());
	}

	public Locale getTargetLocale() {
		return _translationSnapshot.getTargetLocale();
	}

	public boolean isHTMLInfoFieldType(InfoField infoField) {
		if (infoField.getInfoFieldType() instanceof HTMLInfoFieldType) {
			return true;
		}

		return false;
	}

	private final HttpServletRequest _httpServletRequest;
	private final InfoForm _infoForm;
	private final TranslationInfoFieldChecker _translationInfoFieldChecker;
	private final TranslationSnapshot _translationSnapshot;

}