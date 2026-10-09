/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.annotations.DDMForm;
import com.liferay.dynamic.data.mapping.annotations.DDMFormRule;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderInputParametersSettings;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderOutputParametersSettings;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Marcellus Tavares
 */
public class DDMFormFactoryHelper {

	public DDMFormFactoryHelper(Class<?> clazz) {
		_clazz = clazz;

		_ddmForm = clazz.getAnnotation(DDMForm.class);

		_availableLocales = getAvailableLocales();
		_defaultLocale = getDefaultLocale();
	}

	public com.liferay.dynamic.data.mapping.model.DDMForm createDDMForm() {
		com.liferay.dynamic.data.mapping.model.DDMForm ddmForm =
			new com.liferay.dynamic.data.mapping.model.DDMForm();

		ddmForm.setAvailableLocales(_availableLocales);
		ddmForm.setDefaultLocale(_defaultLocale);
		ddmForm.setDDMFormFields(getDDMFormFields());
		ddmForm.setDDMFormRules(getDDMFormRules());

		return ddmForm;
	}

	protected void collectDDMFormFieldMethodsMap(
		Class<?> clazz, Map<String, Method> methodsMap) {

		for (Class<?> interfaceClass : clazz.getInterfaces()) {
			collectDDMFormFieldMethodsMap(interfaceClass, methodsMap);
		}

		for (Method method : clazz.getDeclaredMethods()) {
			if (!method.isAnnotationPresent(_DDM_FORM_FIELD_ANNOTATION)) {
				continue;
			}

			methodsMap.put(method.getName(), method);
		}
	}

	protected void collectResourceBundles(
		Class<?> clazz, List<ResourceBundle> resourceBundles, Locale locale) {

		for (Class<?> interfaceClass : clazz.getInterfaces()) {
			collectResourceBundles(interfaceClass, resourceBundles, locale);
		}

		String resourceBundleBaseName = getResourceBundleBaseName();

		if (Validator.isNull(resourceBundleBaseName)) {
			return;
		}

		try {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				resourceBundleBaseName, locale, clazz.getClassLoader());

			if (resourceBundle != null) {
				resourceBundles.add(resourceBundle);
			}
		}
		catch (MissingResourceException missingResourceException) {
			if (_log.isDebugEnabled()) {
				_log.debug(missingResourceException);
			}
		}
	}

	protected Set<Locale> getAvailableLocales() {
		if (Validator.isNull(_ddmForm.availableLanguageIds())) {
			return SetUtil.fromArray(getDefaultLocale());
		}

		Set<Locale> availableLocales = new HashSet<>();

		for (String availableLanguageId :
				StringUtil.split(_ddmForm.availableLanguageIds())) {

			availableLocales.add(
				LocaleUtil.fromLanguageId(availableLanguageId));
		}

		return availableLocales;
	}

	protected Collection<Method> getDDMFormFieldMethods() {
		Map<String, Method> methodsMap = new LinkedHashMap<>();

		collectDDMFormFieldMethodsMap(_clazz, methodsMap);

		String className = _clazz.getName();

		if (className.equals(
				DDMDataProviderInputParametersSettings.class.getName()) ||
			className.equals(
				DDMDataProviderOutputParametersSettings.class.getName())) {

			return getSortedMethods(methodsMap);
		}

		return methodsMap.values();
	}

	protected List<com.liferay.dynamic.data.mapping.model.DDMFormField>
		getDDMFormFields() {

		return TransformUtil.transform(
			getDDMFormFieldMethods(),
			method -> {
				DDMFormFieldFactoryHelper ddmFormFieldFactoryHelper =
					new DDMFormFieldFactoryHelper(this, method);

				ddmFormFieldFactoryHelper.setAvailableLocales(
					_availableLocales);
				ddmFormFieldFactoryHelper.setDefaultLocale(_defaultLocale);

				return ddmFormFieldFactoryHelper.createDDMFormField();
			});
	}

	protected List<com.liferay.dynamic.data.mapping.model.DDMFormRule>
		getDDMFormRules() {

		return TransformUtil.transformToList(
			_ddmForm.rules(),
			ddmFormRule ->
				new com.liferay.dynamic.data.mapping.model.DDMFormRule(
					ListUtil.fromArray(ddmFormRule.actions()),
					ddmFormRule.condition()));
	}

	protected Locale getDefaultLocale() {
		if (Validator.isNull(_ddmForm.defaultLanguageId())) {
			Locale themeDisplayLocale =
				LocaleThreadLocal.getThemeDisplayLocale();

			if (themeDisplayLocale != null) {
				return themeDisplayLocale;
			}

			Locale siteDefaultLocale = LocaleThreadLocal.getSiteDefaultLocale();

			if (siteDefaultLocale != null) {
				return siteDefaultLocale;
			}

			return LocaleUtil.getDefault();
		}

		return LocaleUtil.fromLanguageId(_ddmForm.defaultLanguageId());
	}

	protected ResourceBundle getResourceBundle(Locale locale) {
		if (_resourceBundles == null) {
			_resourceBundles = new HashMap<>();
		}

		return _resourceBundles.computeIfAbsent(
			locale,
			key -> {
				List<ResourceBundle> resourceBundles = new ArrayList<>();

				ResourceBundle portalResourceBundle =
					PortalUtil.getResourceBundle(locale);

				resourceBundles.add(portalResourceBundle);

				collectResourceBundles(_clazz, resourceBundles, locale);

				ResourceBundle[] resourceBundlesArray = resourceBundles.toArray(
					new ResourceBundle[0]);

				return new AggregateResourceBundle(resourceBundlesArray);
			});
	}

	protected String getResourceBundleBaseName() {
		if (!_clazz.isAnnotationPresent(DDMForm.class)) {
			return null;
		}

		DDMForm ddmForm = _clazz.getAnnotation(DDMForm.class);

		if (Validator.isNotNull(ddmForm.localization())) {
			return ddmForm.localization();
		}

		return "content.Language";
	}

	protected Collection<Method> getSortedMethods(
		Map<String, Method> methodsMap) {

		Map<String, Method> sortedMethodsMap = new LinkedHashMap<>();

		SortedSet<String> keys = new TreeSet<>(methodsMap.keySet());

		for (String key : keys) {
			sortedMethodsMap.put(key, methodsMap.get(key));
		}

		moveInputParameterRequiredToLastPosition(sortedMethodsMap);

		return sortedMethodsMap.values();
	}

	protected void moveInputParameterRequiredToLastPosition(
		Map<String, Method> methodsMap) {

		Method inputParameterRequiredMethod = methodsMap.get(
			"inputParameterRequired");

		if (inputParameterRequiredMethod == null) {
			return;
		}

		methodsMap.remove("inputParameterRequired");

		methodsMap.put("inputParameterRequired", inputParameterRequiredMethod);
	}

	private static final Class<? extends Annotation>
		_DDM_FORM_FIELD_ANNOTATION =
			com.liferay.dynamic.data.mapping.annotations.DDMFormField.class;

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormFactoryHelper.class);

	private final Set<Locale> _availableLocales;
	private final Class<?> _clazz;
	private final DDMForm _ddmForm;
	private final Locale _defaultLocale;
	private Map<Locale, ResourceBundle> _resourceBundles;

}