/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.values.factory.internal;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRequestParameterRetriever;
import com.liferay.dynamic.data.mapping.form.field.type.DefaultDDMFormFieldValueRequestParameterRetriever;
import com.liferay.dynamic.data.mapping.form.renderer.constants.DDMFormRendererConstants;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldParameterNameUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesFactoryUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(service = DDMFormValuesFactory.class)
public class DDMFormValuesFactoryImpl implements DDMFormValuesFactory {

	@Override
	public DDMFormValues create(
		HttpServletRequest httpServletRequest, DDMForm ddmForm) {

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		_setDDMFormValuesAvailableLocales(
			httpServletRequest, ddmForm, ddmFormValues);
		_setDDMFormValuesDefaultLocale(
			httpServletRequest, ddmForm, ddmFormValues);
		_setDDMFormFieldValues(httpServletRequest, ddmFormValues);

		return ddmFormValues;
	}

	@Override
	public DDMFormValues create(
		PortletRequest portletRequest, DDMForm ddmForm) {

		return create(_portal.getHttpServletRequest(portletRequest), ddmForm);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DDMFormFieldValueRequestParameterRetriever.class,
			"ddm.form.field.type.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private void _checkDDMFormFieldParameterNames(
		DDMForm ddmForm, Set<String> ddmFormFieldParameterNames) {

		if (ddmFormFieldParameterNames.isEmpty()) {
			ddmFormFieldParameterNames.addAll(
				_createDefaultDDMFormFieldParameterNames(ddmForm));

			return;
		}

		Set<String> checkedDDMFormFieldNames = new HashSet<>();

		_checkDDMFormFieldParameterNames(
			checkedDDMFormFieldNames, ddmForm.getDDMFormFields(),
			StringPool.BLANK, ddmFormFieldParameterNames);
	}

	private void _checkDDMFormFieldParameterNames(
		Set<String> checkedDDMFormFieldNames, List<DDMFormField> ddmFormFields,
		String parentDDMFormFieldParameterName,
		Set<String> ddmFormFieldParameterNames) {

		for (DDMFormField ddmFormField : ddmFormFields) {
			if (checkedDDMFormFieldNames.contains(ddmFormField.getName())) {
				continue;
			}

			Set<String> filteredDDMFormFieldParameterNames =
				_filterDDMFormFieldParameterNames(
					ddmFormField, ddmFormFieldParameterNames);

			boolean containsDefaultDDMFormFieldParameterName =
				_containsDefaultDDMFormFieldParameterName(
					filteredDDMFormFieldParameterNames,
					_getDDMFormFieldParameterPrefix(
						ddmFormField, parentDDMFormFieldParameterName));

			if (!containsDefaultDDMFormFieldParameterName) {
				String defaultDDMFormFieldParameterName =
					_createDefaultDDMFormFieldParameterName(
						ddmFormField, parentDDMFormFieldParameterName);

				ddmFormFieldParameterNames.add(
					defaultDDMFormFieldParameterName);

				_checkDDMFormFieldParameterNames(
					checkedDDMFormFieldNames,
					ddmFormField.getNestedDDMFormFields(), StringPool.BLANK,
					ddmFormFieldParameterNames);
			}

			for (String filteredDDMFormFieldParameterName :
					filteredDDMFormFieldParameterNames) {

				_checkDDMFormFieldParameterNames(
					checkedDDMFormFieldNames,
					ddmFormField.getNestedDDMFormFields(),
					filteredDDMFormFieldParameterName,
					ddmFormFieldParameterNames);
			}

			checkedDDMFormFieldNames.add(ddmFormField.getName());
		}
	}

	private boolean _containsDefaultDDMFormFieldParameterName(
		Set<String> filteredDDMFormFieldParameterNames,
		String ddmFormFieldParameterPrefix) {

		for (String filteredDDMFormFieldParameterName :
				filteredDDMFormFieldParameterNames) {

			if (filteredDDMFormFieldParameterName.startsWith(
					ddmFormFieldParameterPrefix)) {

				return true;
			}
		}

		return false;
	}

	private DDMFormFieldValue _createDDMFormFieldValue(
		HttpServletRequest httpServletRequest, DDMFormValues ddmFormValues,
		String ddmFormFieldParameterName,
		Map<String, DDMFormField> ddmFormFieldsMap) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		String[] lastDDMFormFieldParameterNameParts =
			DDMFormFieldParameterNameUtil.getLastDDMFormFieldParameterNameParts(
				ddmFormFieldParameterName);

		String fieldName = lastDDMFormFieldParameterNameParts
			[DDMFormFieldParameterNameUtil.DDM_FORM_FIELD_NAME_INDEX];

		ddmFormFieldValue.setName(fieldName);

		ddmFormFieldValue.setInstanceId(
			lastDDMFormFieldParameterNameParts
				[DDMFormFieldParameterNameUtil.
					DDM_FORM_FIELD_INSTANCE_ID_INDEX]);

		DDMFormField ddmFormField = ddmFormFieldsMap.get(fieldName);

		if (ddmFormField == null) {
			return ddmFormFieldValue;
		}

		ddmFormFieldValue.setFieldReference(ddmFormField.getFieldReference());

		if (ddmFormField.isTransient()) {
			return ddmFormFieldValue;
		}

		if (ddmFormField.isLocalizable()) {
			_setDDMFormFieldValueLocalizedValue(
				httpServletRequest, ddmFormField.getType(),
				ddmFormFieldParameterName, ddmFormField.getPredefinedValue(),
				ddmFormFieldValue, ddmFormValues.getAvailableLocales(),
				ddmFormValues.getDefaultLocale());
		}
		else {
			_setDDMFormFieldValueUnlocalizedValue(
				httpServletRequest, ddmFormField.getType(),
				ddmFormFieldParameterName, ddmFormField.getPredefinedValue(),
				ddmFormFieldValue, ddmFormValues.getDefaultLocale());
		}

		return ddmFormFieldValue;
	}

	private Map<String, DDMFormFieldValue> _createDDMFormFieldValuesMap(
		HttpServletRequest httpServletRequest, DDMForm ddmForm,
		DDMFormValues ddmFormValues) {

		Map<String, DDMFormFieldValue> ddmFormFieldValuesMap = new HashMap<>();

		Set<String> ddmFormFieldParameterNames = _getDDMFormFieldParameterNames(
			httpServletRequest, ddmForm);

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (String ddmFormFieldParameterName : ddmFormFieldParameterNames) {
			DDMFormFieldValue ddmFormFieldValue = _createDDMFormFieldValue(
				httpServletRequest, ddmFormValues, ddmFormFieldParameterName,
				ddmFormFieldsMap);

			ddmFormFieldValuesMap.put(
				ddmFormFieldParameterName, ddmFormFieldValue);
		}

		return ddmFormFieldValuesMap;
	}

	private String _createDefaultDDMFormFieldParameterName(
		DDMFormField ddmFormField,
		String parentDefaultDDMFormFieldParameterName) {

		StringBundler sb = new StringBundler(7);

		if (Validator.isNotNull(parentDefaultDDMFormFieldParameterName)) {
			sb.append(parentDefaultDDMFormFieldParameterName);
			sb.append(DDMFormRendererConstants.DDM_FORM_FIELDS_SEPARATOR);
		}

		sb.append(ddmFormField.getName());
		sb.append(DDMFormRendererConstants.DDM_FORM_FIELD_PARTS_SEPARATOR);
		sb.append(StringUtil.randomString());
		sb.append(DDMFormRendererConstants.DDM_FORM_FIELD_PARTS_SEPARATOR);
		sb.append(0);

		return sb.toString();
	}

	private Set<String> _createDefaultDDMFormFieldParameterNames(
		DDMForm ddmForm) {

		Set<String> defaultDDMFormFieldParameterNames = new TreeSet<>();

		_poupulateDefaultDDMFormFieldParameterNames(
			ddmForm.getDDMFormFields(), StringPool.BLANK,
			defaultDDMFormFieldParameterNames);

		return defaultDDMFormFieldParameterNames;
	}

	private String _extractPrefix(String ddmFormFieldParameterName) {
		return StringUtil.extractLast(
			ddmFormFieldParameterName,
			DDMFormRendererConstants.DDM_FORM_FIELD_NAME_PREFIX);
	}

	private String _extractSuffix(String ddmFormFieldParameterName) {
		int pos = ddmFormFieldParameterName.lastIndexOf(
			DDMFormRendererConstants.DDM_FORM_FIELD_LANGUAGE_ID_SEPARATOR);

		return ddmFormFieldParameterName.substring(0, pos);
	}

	private Set<String> _filterDDMFormFieldParameterNames(
		DDMFormField ddmFormField, Set<String> ddmFormFieldParameterNames) {

		Set<String> filteredDDMFormFieldParameterNames = new HashSet<>();

		for (String ddmFormFieldParameterName : ddmFormFieldParameterNames) {
			String[] ddmFormFieldParameterNameParts =
				DDMFormFieldParameterNameUtil.
					getLastDDMFormFieldParameterNameParts(
						ddmFormFieldParameterName);

			String fieldName = ddmFormFieldParameterNameParts
				[DDMFormFieldParameterNameUtil.DDM_FORM_FIELD_NAME_INDEX];

			if (fieldName.equals(ddmFormField.getName())) {
				filteredDDMFormFieldParameterNames.add(
					ddmFormFieldParameterName);
			}
		}

		return filteredDDMFormFieldParameterNames;
	}

	private Set<String> _getDDMFormFieldParameterNames(
		HttpServletRequest httpServletRequest, DDMForm ddmForm) {

		Set<String> ddmFormFieldParameterNames = new TreeSet<>();

		Map<String, String[]> parameterMap =
			httpServletRequest.getParameterMap();

		for (String parameterName : parameterMap.keySet()) {
			if (!_isDDMFormFieldParameter(parameterName)) {
				continue;
			}

			ddmFormFieldParameterNames.addAll(
				_getDDMFormFieldParameterNames(parameterName));
		}

		_checkDDMFormFieldParameterNames(ddmForm, ddmFormFieldParameterNames);

		return ddmFormFieldParameterNames;
	}

	private Set<String> _getDDMFormFieldParameterNames(
		String ddmFormFieldParameterName) {

		Set<String> ddmFormFieldParameterNames = new TreeSet<>();

		ddmFormFieldParameterName = _extractPrefix(ddmFormFieldParameterName);
		ddmFormFieldParameterName = _extractSuffix(ddmFormFieldParameterName);

		ddmFormFieldParameterNames.add(ddmFormFieldParameterName);

		int pos = ddmFormFieldParameterName.indexOf(
			DDMFormRendererConstants.DDM_FORM_FIELDS_SEPARATOR);

		while (pos != -1) {
			ddmFormFieldParameterNames.add(
				ddmFormFieldParameterName.substring(0, pos));

			pos = ddmFormFieldParameterName.indexOf(
				DDMFormRendererConstants.DDM_FORM_FIELDS_SEPARATOR, pos + 1);
		}

		return ddmFormFieldParameterNames;
	}

	private String _getDDMFormFieldParameterPrefix(
		DDMFormField ddmFormField, String parentDDMFormFieldParameterName) {

		if (Validator.isNull(parentDDMFormFieldParameterName)) {
			return ddmFormField.getName();
		}

		return StringBundler.concat(
			parentDDMFormFieldParameterName, StringPool.POUND,
			ddmFormField.getName());
	}

	private String _getDDMFormFieldParameterValue(
		String defaultDDMFormFieldParameterValue, String fieldType,
		String fullDDMFormFieldParameterName,
		HttpServletRequest httpServletRequest) {

		DDMFormFieldValueRequestParameterRetriever
			ddmFormFieldValueRequestParameterRetriever =
				_getDDMFormFieldValueRequestParameterRetriever(fieldType);

		return ddmFormFieldValueRequestParameterRetriever.get(
			httpServletRequest, fullDDMFormFieldParameterName,
			GetterUtil.getString(defaultDDMFormFieldParameterValue));
	}

	private DDMFormFieldValueRequestParameterRetriever
		_getDDMFormFieldValueRequestParameterRetriever(String fieldType) {

		if (!_serviceTrackerMap.containsKey(fieldType)) {
			return _defaultDDMFormFieldValueRequestParameterRetriever;
		}

		return _serviceTrackerMap.getService(fieldType);
	}

	private List<DDMFormFieldValue> _getDDMFormFieldValues(
		HttpServletRequest httpServletRequest, DDMForm ddmForm,
		DDMFormValues ddmFormValues) {

		Map<String, DDMFormFieldValue> ddmFormFieldValuesMap =
			_createDDMFormFieldValuesMap(
				httpServletRequest, ddmForm, ddmFormValues);

		return DDMFormValuesFactoryUtil.getDDMFormFieldValues(
			ddmFormFieldValuesMap, ddmForm.getDDMFormFields());
	}

	private Locale _getDefaultLocale(
		HttpServletRequest httpServletRequest, Locale defaultLocale,
		Set<Locale> availableLocales) {

		String defaultLanguageId = ParamUtil.getString(
			httpServletRequest, "defaultLanguageId");

		if (Validator.isNotNull(defaultLanguageId)) {
			return LocaleUtil.fromLanguageId(defaultLanguageId);
		}

		Locale httpServletRequestLocale = LocaleUtil.fromLanguageId(
			_language.getLanguageId(httpServletRequest));

		if (availableLocales.contains(httpServletRequestLocale)) {
			return httpServletRequestLocale;
		}

		return defaultLocale;
	}

	private String _getFullDDMFormFieldParameterName(
		String ddmFormFieldParameterName, Locale locale) {

		return StringBundler.concat(
			DDMFormRendererConstants.DDM_FORM_FIELD_NAME_PREFIX,
			ddmFormFieldParameterName,
			DDMFormRendererConstants.DDM_FORM_FIELD_LANGUAGE_ID_SEPARATOR,
			LocaleUtil.toLanguageId(locale));
	}

	private boolean _isDDMFormFieldParameter(String parameterName) {
		return parameterName.startsWith(
			DDMFormRendererConstants.DDM_FORM_FIELD_NAME_PREFIX);
	}

	private void _poupulateDefaultDDMFormFieldParameterNames(
		List<DDMFormField> ddmFormFields,
		String parentDefaultDDMFormFieldParameterName,
		Set<String> defaultDDMFormFieldParameterNames) {

		for (DDMFormField ddmFormField : ddmFormFields) {
			String defaultDDMFormFieldParameterName =
				_createDefaultDDMFormFieldParameterName(
					ddmFormField, parentDefaultDDMFormFieldParameterName);

			defaultDDMFormFieldParameterNames.add(
				defaultDDMFormFieldParameterName);

			_poupulateDefaultDDMFormFieldParameterNames(
				ddmFormField.getNestedDDMFormFields(),
				defaultDDMFormFieldParameterName,
				defaultDDMFormFieldParameterNames);
		}
	}

	private void _setDDMFormFieldValueLocalizedValue(
		HttpServletRequest httpServletRequest, String fieldType,
		String ddmFormFieldParameterName, LocalizedValue predefinedValue,
		DDMFormFieldValue ddmFormFieldValue, Set<Locale> availableLocales,
		Locale defaultLocale) {

		Value value = new LocalizedValue(defaultLocale);

		boolean persistDefaultValues = ParamUtil.getBoolean(
			httpServletRequest, "persistDefaultValues", true);

		for (Locale availableLocale : availableLocales) {
			String fullDDMFormFieldParameterName =
				_getFullDDMFormFieldParameterName(
					ddmFormFieldParameterName, availableLocale);

			String parameterValue = httpServletRequest.getParameter(
				fullDDMFormFieldParameterName);

			if (persistDefaultValues ||
				(GetterUtil.getBoolean(
					httpServletRequest.getParameter(
						fullDDMFormFieldParameterName + "_edited"),
					true) &&
				 (parameterValue != null))) {

				String ddmFormFieldParameterValue =
					_getDDMFormFieldParameterValue(
						predefinedValue.getString(availableLocale), fieldType,
						fullDDMFormFieldParameterName, httpServletRequest);

				value.addString(availableLocale, ddmFormFieldParameterValue);
			}
		}

		ddmFormFieldValue.setValue(value);
	}

	private void _setDDMFormFieldValues(
		HttpServletRequest httpServletRequest, DDMFormValues ddmFormValues) {

		List<DDMFormFieldValue> ddmFormFieldValues = _getDDMFormFieldValues(
			httpServletRequest, ddmFormValues.getDDMForm(), ddmFormValues);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);
	}

	private void _setDDMFormFieldValueUnlocalizedValue(
		HttpServletRequest httpServletRequest, String fieldType,
		String ddmFormFieldParameterName, LocalizedValue predefinedValue,
		DDMFormFieldValue ddmFormFieldValue, Locale defaultLocale) {

		String ddmFormFieldParameterValue = _getDDMFormFieldParameterValue(
			predefinedValue.getString(defaultLocale), fieldType,
			_getFullDDMFormFieldParameterName(
				ddmFormFieldParameterName, defaultLocale),
			httpServletRequest);

		Value value = new UnlocalizedValue(ddmFormFieldParameterValue);

		ddmFormFieldValue.setValue(value);
	}

	private void _setDDMFormValuesAvailableLocales(
		HttpServletRequest httpServletRequest, DDMForm ddmForm,
		DDMFormValues ddmFormValues) {

		String[] availableLocaleStrings = ParamUtil.getStringValues(
			httpServletRequest, "availableLocales");

		if (ArrayUtil.isEmpty(availableLocaleStrings)) {
			for (Locale availableLocale : ddmForm.getAvailableLocales()) {
				ddmFormValues.addAvailableLocale(availableLocale);
			}
		}
		else {
			long groupId = ParamUtil.getLong(httpServletRequest, "groupId");

			Set<Locale> siteAvailableLocales = _language.getAvailableLocales(
				groupId);

			String[] siteAvailableLocaleStrings = LocaleUtil.toLanguageIds(
				siteAvailableLocales);

			for (String availableLocaleString : availableLocaleStrings) {
				if (ArrayUtil.contains(
						siteAvailableLocaleStrings, availableLocaleString,
						false)) {

					ddmFormValues.addAvailableLocale(
						LocaleUtil.fromLanguageId(availableLocaleString));
				}
			}
		}
	}

	private void _setDDMFormValuesDefaultLocale(
		HttpServletRequest httpServletRequest, DDMForm ddmForm,
		DDMFormValues ddmFormValues) {

		ddmFormValues.setDefaultLocale(
			_getDefaultLocale(
				httpServletRequest, ddmForm.getDefaultLocale(),
				ddmForm.getAvailableLocales()));
	}

	private final DDMFormFieldValueRequestParameterRetriever
		_defaultDDMFormFieldValueRequestParameterRetriever =
			new DefaultDDMFormFieldValueRequestParameterRetriever();

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap
		<String, DDMFormFieldValueRequestParameterRetriever> _serviceTrackerMap;

}