/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.date;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.field.type.internal.util.DDMFormFieldTypeUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldTemplateContextContributorUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldValueUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.CalendarUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.time.DayOfWeek;
import java.time.temporal.WeekFields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"ddm.form.field.type.name=" + DDMFormFieldTypeConstants.DATE,
		"ddm.form.field.type.name=" + DDMFormFieldTypeConstants.DATE_TIME
	},
	service = DDMFormFieldTemplateContextContributor.class
)
public class DateDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		DDMForm ddmForm = ddmFormField.getDDMForm();
		boolean localizedObjectField = GetterUtil.getBoolean(
			ddmFormField.getProperty("localizedObjectField"));

		return HashMapBuilder.<String, Object>put(
			"editOnlyInDefaultLanguage",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("editOnlyInDefaultLanguage"))
		).put(
			"firstDayOfWeek",
			_getFirstDayOfWeek(ddmFormFieldRenderingContext.getLocale())
		).put(
			"htmlAutocompleteAttribute",
			GetterUtil.getString(
				ddmFormField.getProperty("htmlAutocompleteAttribute"))
		).put(
			"isLocalizationSupported",
			GetterUtil.getBoolean(
				ddmFormField.getProperty("isLocalizationSupported"))
		).put(
			"localizedObjectField", localizedObjectField
		).put(
			"months",
			Arrays.asList(
				CalendarUtil.getMonths(
					ddmFormFieldRenderingContext.getLocale()))
		).put(
			"predefinedValue",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, ddmFormFieldRenderingContext.getLocale(),
				"predefinedValue")
		).put(
			"tooltip",
			DDMFormFieldTypeUtil.getPropertyValue(
				ddmFormField, ddmFormFieldRenderingContext.getLocale(),
				"tooltip")
		).put(
			"value",
			() -> {
				if (localizedObjectField) {
					return DDMFormFieldValueUtil.getValueJSONObject(
						ddmFormFieldRenderingContext);
				}

				return DDMFormFieldTypeUtil.getValue(
					ddmFormFieldRenderingContext.getValue());
			}
		).put(
			"weekdaysShort",
			TransformUtil.transformToList(
				CalendarUtil.DAYS_ABBREVIATION,
				day -> _language.get(
					ddmFormFieldRenderingContext.getLocale(), day))
		).put(
			"years", _getYears()
		).putAll(
			DDMFormFieldTemplateContextContributorUtil.getLocaleMap(
				ddmForm.getDefaultLocale())
		).build();
	}

	private int _getFirstDayOfWeek(Locale locale) {
		WeekFields weekFields = WeekFields.of(locale);

		DayOfWeek dayOfWeek = weekFields.getFirstDayOfWeek();

		return dayOfWeek.getValue() % 7;
	}

	private List<Integer> _getYears() {
		List<Integer> years = new ArrayList<>();

		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.YEAR, -4);

		for (int i = 0; i < 5; i++) {
			years.add(calendar.get(Calendar.YEAR));

			calendar.add(Calendar.YEAR, 1);
		}

		return years;
	}

	@Reference
	private Language _language;

}