/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.renderer.internal;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluator;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorEvaluateRequest;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorEvaluateResponse;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.google.places.util.GooglePlacesUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Marcellus Tavares
 */
public class DDMFormPagesTemplateContextFactory {

	public DDMFormPagesTemplateContextFactory(
		DDMForm ddmForm, DDMFormLayout ddmFormLayout,
		DDMFormRenderingContext ddmFormRenderingContext,
		DDMStructureLayoutLocalService ddmStructureLayoutLocalService,
		DDMStructureLocalService ddmStructureLocalService,
		GroupLocalService groupLocalService, HtmlParser htmlParser,
		JSONFactory jsonFactory) {

		_ddmForm = ddmForm;
		_ddmFormLayout = ddmFormLayout;
		_ddmFormRenderingContext = ddmFormRenderingContext;
		_ddmStructureLayoutLocalService = ddmStructureLayoutLocalService;
		_ddmStructureLocalService = ddmStructureLocalService;
		_groupLocalService = groupLocalService;
		_htmlParser = htmlParser;
		_jsonFactory = jsonFactory;

		DDMFormValues ddmFormValues =
			ddmFormRenderingContext.getDDMFormValues();

		DefaultDDMFormValuesFactory defaultDDMFormValuesFactory =
			new DefaultDDMFormValuesFactory(ddmForm);

		if ((ddmFormValues == null) ||
			ListUtil.isEmpty(ddmFormValues.getDDMFormFieldValues())) {

			ddmFormValues = defaultDDMFormValuesFactory.create();
		}
		else {
			defaultDDMFormValuesFactory.populate(ddmFormValues);
		}

		_ddmFormValues = ddmFormValues;

		_ddmFormFieldsMap = ddmForm.getDDMFormFieldsMap(true);
		_ddmFormFieldValuesMap = ddmFormValues.getDDMFormFieldValuesMap(true);
		_locale = ddmFormRenderingContext.getLocale();
	}

	public List<Object> create() {
		_evaluate();

		return _createPagesTemplateContext(
			_ddmFormLayout.getDDMFormLayoutPages());
	}

	public void setDDMFormEvaluator(DDMFormEvaluator ddmFormEvaluator) {
		_ddmFormEvaluator = ddmFormEvaluator;
	}

	public void setDDMFormFieldTypeServicesRegistry(
		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry) {

		_ddmFormFieldTypeServicesRegistry = ddmFormFieldTypeServicesRegistry;
	}

	protected String getValue(
		DDMFormRenderingContext ddmFormRenderingContext, String value) {

		if (ddmFormRenderingContext.isViewMode()) {
			return _htmlParser.extractText(value);
		}

		return value;
	}

	protected boolean isShowRequiredFieldsWarning(
		List<DDMFormLayoutRow> ddmFormLayoutRows) {

		if (!_ddmFormRenderingContext.isShowRequiredFieldsWarning()) {
			return false;
		}

		for (DDMFormLayoutRow ddmFormLayoutRow : ddmFormLayoutRows) {
			for (DDMFormLayoutColumn ddmFormLayoutColumn :
					ddmFormLayoutRow.getDDMFormLayoutColumns()) {

				if (_containsRequiredField(
						ddmFormLayoutColumn.getDDMFormFieldNames())) {

					return true;
				}
			}
		}

		return false;
	}

	private boolean _containsRequiredField(List<String> ddmFormFieldNames) {
		for (String ddmFormFieldName : ddmFormFieldNames) {
			DDMFormField ddmFormField = _ddmFormFieldsMap.get(ddmFormFieldName);

			if (ddmFormField.isRequired()) {
				return true;
			}
		}

		return false;
	}

	private List<Object> _createColumnsTemplateContext(
		List<DDMFormLayoutColumn> ddmFormLayoutColumns) {

		return TransformUtil.transform(
			ddmFormLayoutColumns,
			ddmFormLayoutColumn -> _createColumnTemplateContext(
				ddmFormLayoutColumn.getDDMFormFieldNames(),
				ddmFormLayoutColumn.getSize()));
	}

	private Map<String, Object> _createColumnTemplateContext(
		List<String> ddmFormFiledNames, int size) {

		return HashMapBuilder.<String, Object>put(
			"fields", _createFieldsTemplateContext(ddmFormFiledNames)
		).put(
			"size", size
		).build();
	}

	private List<Object> _createFieldsTemplateContext(
		List<String> ddmFormFieldNames) {

		List<Object> fieldsTemplateContext = new ArrayList<>();

		for (String ddmFormFieldName : ddmFormFieldNames) {
			List<Object> fieldTemplateContexts = _createFieldTemplateContext(
				ddmFormFieldName);

			if (ListUtil.isNotEmpty(fieldTemplateContexts)) {
				fieldsTemplateContext.addAll(fieldTemplateContexts);
			}
		}

		return fieldsTemplateContext;
	}

	private List<Object> _createFieldTemplateContext(String ddmFormFieldName) {
		DDMFormFieldTemplateContextFactory ddmFormFieldTemplateContextFactory =
			new DDMFormFieldTemplateContextFactory(
				_ddmFormEvaluator, ddmFormFieldName, _ddmFormFieldsMap,
				_ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges(),
				_ddmFormFieldValuesMap.get(ddmFormFieldName),
				_ddmFormRenderingContext, _ddmStructureLayoutLocalService,
				_ddmStructureLocalService, _groupLocalService, _htmlParser,
				_jsonFactory, _pageEnabled, _ddmFormLayout);

		ddmFormFieldTemplateContextFactory.setDDMFormFieldTypeServicesRegistry(
			_ddmFormFieldTypeServicesRegistry);

		return ddmFormFieldTemplateContextFactory.create();
	}

	private List<Object> _createPagesTemplateContext(
		List<DDMFormLayoutPage> ddmFormLayoutPages) {

		List<Object> pagesTemplateContext = new ArrayList<>();

		int i = 0;

		for (DDMFormLayoutPage ddmFormLayoutPage : ddmFormLayoutPages) {
			pagesTemplateContext.add(
				_createPageTemplateContext(ddmFormLayoutPage, i++));
		}

		return pagesTemplateContext;
	}

	private Map<String, Object> _createPageTemplateContext(
		DDMFormLayoutPage ddmFormLayoutPage, int pageIndex) {

		Map<String, Object> pageTemplateContext = new HashMap<>();

		LocalizedValue description = ddmFormLayoutPage.getDescription();

		pageTemplateContext.put("description", description.getString(_locale));

		_pageEnabled = _isPageEnabled(pageIndex);

		pageTemplateContext.put("enabled", _pageEnabled);

		pageTemplateContext.put(
			"localizedDescription",
			_getLocalizedValueMap(description, _ddmFormRenderingContext));

		LocalizedValue title = ddmFormLayoutPage.getTitle();

		pageTemplateContext.put(
			"localizedTitle",
			_getLocalizedValueMap(title, _ddmFormRenderingContext));

		pageTemplateContext.put(
			"rows",
			_createRowsTemplateContext(
				ddmFormLayoutPage.getDDMFormLayoutRows()));

		boolean showRequiredFieldsWarning = isShowRequiredFieldsWarning(
			ddmFormLayoutPage.getDDMFormLayoutRows());

		pageTemplateContext.put(
			"showRequiredFieldsWarning", showRequiredFieldsWarning);

		pageTemplateContext.put("title", title.getString(_locale));

		return pageTemplateContext;
	}

	private List<Object> _createRowsTemplateContext(
		List<DDMFormLayoutRow> ddmFormLayoutRows) {

		return TransformUtil.transform(
			ddmFormLayoutRows,
			ddmFormLayoutRow -> _createRowTemplateContext(ddmFormLayoutRow));
	}

	private Map<String, Object> _createRowTemplateContext(
		DDMFormLayoutRow ddmFormLayoutRow) {

		return HashMapBuilder.<String, Object>put(
			"columns",
			_createColumnsTemplateContext(
				ddmFormLayoutRow.getDDMFormLayoutColumns())
		).build();
	}

	private void _evaluate() {
		try {
			HttpServletRequest httpServletRequest =
				_ddmFormRenderingContext.getHttpServletRequest();

			long companyId = PortalUtil.getCompanyId(httpServletRequest);

			DDMFormEvaluatorEvaluateRequest.Builder
				ddmFormEvaluatorEvaluateRequestBuilder =
					DDMFormEvaluatorEvaluateRequest.Builder.newBuilder(
						_ddmForm, _ddmFormValues, _locale);

			ddmFormEvaluatorEvaluateRequestBuilder.withCompanyId(
				companyId
			).withDDMFormInstanceId(
				_ddmFormRenderingContext.getDDMFormInstanceId()
			).withDDMFormLayout(
				_ddmFormLayout
			).withEditingFieldValue(
				Validator.isNotNull(httpServletRequest.getParameter("trigger"))
			).withGooglePlacesAPIKey(
				GooglePlacesUtil.getGooglePlacesAPIKey(
					companyId, _ddmFormRenderingContext.getGroupId(),
					_groupLocalService)
			).withGroupId(
				_ddmFormRenderingContext.getGroupId()
			).withObjectFieldsJSONArray(
				_ddmForm.getObjectFieldsJSONArray()
			).withTimeZoneId(
				_getTimeZoneId(httpServletRequest)
			).withUserId(
				PortalUtil.getUserId(httpServletRequest)
			).withViewMode(
				_isViewMode()
			);

			_ddmFormEvaluatorEvaluateResponse = _ddmFormEvaluator.evaluate(
				ddmFormEvaluatorEvaluateRequestBuilder.build());
		}
		catch (Exception exception) {
			_log.error("Unable to evaluate the form", exception);

			throw new IllegalStateException(
				"Unexpected error occurred during form evaluation", exception);
		}
	}

	private Map<String, String> _getLocalizedValueMap(
		LocalizedValue localizedValue,
		DDMFormRenderingContext ddmFormRenderingContext) {

		Map<String, String> map = new HashMap<>();

		Map<Locale, String> values = localizedValue.getValues();

		for (Map.Entry<Locale, String> entry : values.entrySet()) {
			String languageId = LocaleUtil.toLanguageId(entry.getKey());

			String keyValue = getValue(
				ddmFormRenderingContext, entry.getValue());

			map.put(languageId, keyValue);
		}

		return map;
	}

	private String _getTimeZoneId(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return StringPool.BLANK;
		}

		User user = themeDisplay.getUser();

		return user.getTimeZoneId();
	}

	private boolean _isPageEnabled(int pageIndex) {
		Set<Integer> disabledPagesIndexes =
			_ddmFormEvaluatorEvaluateResponse.getDisabledPagesIndexes();

		return !disabledPagesIndexes.contains(pageIndex);
	}

	private boolean _isViewMode() {
		Boolean viewMode = _ddmFormRenderingContext.getProperty("viewMode");

		if (viewMode != null) {
			return viewMode;
		}

		String portletNamespace =
			_ddmFormRenderingContext.getPortletNamespace();

		if ((portletNamespace != null) &&
			!StringUtil.equals(
				portletNamespace,
				PortalUtil.getPortletNamespace(
					DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN))) {

			return true;
		}

		return false;
	}

	private void _removeStaleDDMFormFieldValues(
		Map<String, DDMFormField> ddmFormFieldsMap,
		List<DDMFormFieldValue> ddmFormFieldValues) {

		Iterator<DDMFormFieldValue> iterator = ddmFormFieldValues.iterator();

		while (iterator.hasNext()) {
			DDMFormFieldValue ddmFormFieldValue = iterator.next();

			if (!ddmFormFieldsMap.containsKey(ddmFormFieldValue.getName())) {
				iterator.remove();
			}

			_removeStaleDDMFormFieldValues(
				ddmFormFieldsMap,
				ddmFormFieldValue.getNestedDDMFormFieldValues());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormPagesTemplateContextFactory.class);

	private final DDMForm _ddmForm;
	private DDMFormEvaluator _ddmFormEvaluator;
	private DDMFormEvaluatorEvaluateResponse _ddmFormEvaluatorEvaluateResponse;
	private final Map<String, DDMFormField> _ddmFormFieldsMap;
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;
	private final Map<String, List<DDMFormFieldValue>> _ddmFormFieldValuesMap;
	private final DDMFormLayout _ddmFormLayout;
	private final DDMFormRenderingContext _ddmFormRenderingContext;
	private final DDMFormValues _ddmFormValues;
	private final DDMStructureLayoutLocalService
		_ddmStructureLayoutLocalService;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private final GroupLocalService _groupLocalService;
	private final HtmlParser _htmlParser;
	private final JSONFactory _jsonFactory;
	private final Locale _locale;
	private boolean _pageEnabled;

}