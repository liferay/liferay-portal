/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.renderer.internal;

import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluator;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.form.renderer.internal.helper.DDMFormTemplateContextFactoryHelper;
import com.liferay.dynamic.data.mapping.form.validation.DDMValidation;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.dynamic.data.mapping.util.SettingsDDMFormFieldsUtil;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(service = DDMFormTemplateContextFactory.class)
public class DDMFormTemplateContextFactoryImpl
	implements DDMFormTemplateContextFactory {

	@Override
	public Map<String, Object> create(
			DDMForm ddmForm, DDMFormLayout ddmFormLayout,
			DDMFormRenderingContext ddmFormRenderingContext)
		throws PortalException {

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (DDMFormField ddmFormLayoutDDMFormField :
				ddmFormLayout.getDDMFormFields()) {

			DDMFormField ddmFormField = ddmFormFieldsMap.get(
				ddmFormLayoutDDMFormField.getName());

			if (!ddmFormField.isRequired()) {
				ddmFormField.setRequired(
					ddmFormLayoutDDMFormField.isRequired());
			}

			Map<String, DDMFormField> settingsDDMFormFieldsMap =
				SettingsDDMFormFieldsUtil.getSettingsDDMFormFields(
					_ddmFormFieldTypeServicesRegistry,
					ddmFormLayoutDDMFormField.getType());

			for (DDMFormField visualPropertyDDMFormField :
					ListUtil.filter(
						new ArrayList<>(settingsDDMFormFieldsMap.values()),
						visualPropertyDDMFormField1 ->
							visualPropertyDDMFormField1.isVisualProperty() &&
							!StringUtil.equals(
								visualPropertyDDMFormField1.getName(),
								"required"))) {

				Object value = ddmFormLayoutDDMFormField.getProperty(
					visualPropertyDDMFormField.getName());

				if ((value == null) ||
					(Objects.equals(
						visualPropertyDDMFormField.getName(), "label") &&
					 GetterUtil.getBoolean(
						 ddmFormField.getProperty("labelAtStructureLevel")))) {

					continue;
				}

				if (visualPropertyDDMFormField.isLocalizable()) {
					LocalizedValue localizedValue = (LocalizedValue)value;

					if (MapUtil.isEmpty(localizedValue.getValues())) {
						continue;
					}
				}

				ddmFormField.setProperty(
					visualPropertyDDMFormField.getName(), value);
			}
		}

		return _create(ddmForm, ddmFormLayout, ddmFormRenderingContext);
	}

	@Override
	public Map<String, Object> create(
			DDMForm ddmForm, DDMFormRenderingContext ddmFormRenderingContext)
		throws PortalException {

		return _create(
			ddmForm, _ddm.getDefaultDDMFormLayout(ddmForm),
			ddmFormRenderingContext);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, DDMValidation.class, null,
			new PropertyServiceReferenceMapper<>("ddm.validation.data.type"),
			Collections.reverseOrder(
				new PropertyServiceReferenceComparator<>(
					"ddm.validation.ranking")));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	protected ResourceBundle getResourceBundle(Locale locale) {
		List<ResourceBundle> resourceBundles = new ArrayList<>();

		resourceBundles.add(_portal.getResourceBundle(locale));

		_collectResourceBundles(getClass(), resourceBundles, locale);

		return new AggregateResourceBundle(
			resourceBundles.<ResourceBundle>toArray(new ResourceBundle[0]));
	}

	protected String getTemplateNamespace(DDMFormLayout ddmFormLayout) {
		String paginationMode = ddmFormLayout.getPaginationMode();

		if (Objects.equals(paginationMode, DDMFormLayout.SETTINGS_MODE)) {
			return "ddm.settings_form";
		}

		if (Objects.equals(paginationMode, DDMFormLayout.SINGLE_PAGE_MODE)) {
			return "ddm.simple_form";
		}
		else if (Objects.equals(paginationMode, DDMFormLayout.TABBED_MODE)) {
			return "ddm.tabbed_form";
		}
		else if (Objects.equals(paginationMode, DDMFormLayout.WIZARD_MODE)) {
			return "ddm.wizard_form";
		}

		return "ddm.paginated_form";
	}

	private void _collectResourceBundles(
		Class<?> clazz, List<ResourceBundle> resourceBundles, Locale locale) {

		for (Class<?> interfaceClass : clazz.getInterfaces()) {
			_collectResourceBundles(interfaceClass, resourceBundles, locale);
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, clazz.getClassLoader());

		if (resourceBundle != null) {
			resourceBundles.add(resourceBundle);
		}
	}

	private Map<String, Object> _create(
			DDMForm ddmForm, DDMFormLayout ddmFormLayout,
			DDMFormRenderingContext ddmFormRenderingContext)
		throws PortalException {

		Map<String, Object> templateContext = new HashMap<>();

		String containerId = ddmFormRenderingContext.getContainerId();

		if (Validator.isNull(containerId)) {
			containerId = StringUtil.randomId();
		}

		templateContext.put(
			"activePage",
			ParamUtil.getInteger(
				ddmFormRenderingContext.getHttpServletRequest(), "activePage"));

		_setDDMFormFieldsEvaluableProperty(ddmForm, ddmFormLayout);

		Locale locale = ddmFormRenderingContext.getLocale();

		if (locale == null) {
			locale = LocaleThreadLocal.getSiteDefaultLocale();
		}

		ResourceBundle resourceBundle = getResourceBundle(locale);

		String cancelLabel = GetterUtil.getString(
			ddmFormRenderingContext.getCancelLabel(),
			_language.get(resourceBundle, "cancel"));

		templateContext.put("cancelLabel", cancelLabel);

		templateContext.put("containerId", containerId);

		String contentType = GetterUtil.getString(
			ddmFormRenderingContext.getProperty("contentType"));

		if (Validator.isNotNull(contentType)) {
			templateContext.put("contentType", contentType);
		}

		templateContext.put(
			"currentPage",
			ParamUtil.getString(
				ddmFormRenderingContext.getHttpServletRequest(), "currentPage",
				"1"));
		templateContext.put(
			"ddmStructureLayoutId",
			ddmFormRenderingContext.getDDMStructureLayoutId());

		String defaultLanguageId = GetterUtil.getString(
			ddmFormRenderingContext.getProperty("defaultLanguageId"));

		if (Validator.isNotNull(defaultLanguageId)) {
			templateContext.put("defaultLanguageId", defaultLanguageId);
		}
		else {
			templateContext.put(
				"defaultLanguageId",
				_language.getLanguageId(ddmForm.getDefaultLocale()));
		}

		templateContext.put(
			"defaultSiteLanguageId",
			_language.getLanguageId(LocaleUtil.getSiteDefault()));
		templateContext.put(
			"disableFieldRepetition",
			ddmFormRenderingContext.isDisableFieldRepetition());
		templateContext.put(
			"editingLanguageId", _language.getLanguageId(locale));
		templateContext.put(
			"evaluatorURL", _getDDMFormContextProviderServletURL());
		templateContext.put("groupId", ddmFormRenderingContext.getGroupId());
		templateContext.put(
			"pages",
			_getPages(ddmForm, ddmFormLayout, ddmFormRenderingContext));
		templateContext.put(
			"paginationMode", ddmFormLayout.getPaginationMode());
		templateContext.put(
			"persistDefaultValues",
			GetterUtil.getBoolean(
				(Boolean)ddmFormRenderingContext.getProperty(
					"persistDefaultValues"),
				true));
		templateContext.put(
			"portletNamespace", ddmFormRenderingContext.getPortletNamespace());
		templateContext.put("readOnly", ddmFormRenderingContext.isReadOnly());

		String redirectURL = ddmFormRenderingContext.getRedirectURL();

		templateContext.put("redirectURL", redirectURL);

		List<DDMFormRule> ddmFormRules = ddmFormLayout.getDDMFormRules();

		if (ListUtil.isEmpty(ddmFormRules)) {
			ddmFormRules = ddmForm.getDDMFormRules();
		}

		templateContext.put("rules", _toObjectList(ddmFormRules));

		boolean showCancelButton = ddmFormRenderingContext.isShowCancelButton();

		if (ddmFormRenderingContext.isReadOnly() ||
			Validator.isNull(redirectURL)) {

			showCancelButton = false;
		}

		templateContext.put("showCancelButton", showCancelButton);

		templateContext.put(
			"showPartialResultsToRespondents",
			GetterUtil.getBoolean(
				(Boolean)ddmFormRenderingContext.getProperty(
					"showPartialResultsToRespondents")));
		templateContext.put(
			"showRequiredFieldsWarning",
			ddmFormRenderingContext.isShowRequiredFieldsWarning());

		boolean showSubmitButton = ddmFormRenderingContext.isShowSubmitButton();

		if (ddmFormRenderingContext.isReadOnly()) {
			showSubmitButton = false;
		}

		templateContext.put("showSubmitButton", showSubmitButton);

		templateContext.put("strings", _getLanguageStringsMap(resourceBundle));

		String submitLabel = GetterUtil.getString(
			ddmFormRenderingContext.getSubmitLabel(),
			_language.get(resourceBundle, "submit-form"));

		templateContext.put("submitLabel", submitLabel);

		templateContext.put(
			"submittable", ddmFormRenderingContext.isSubmittable());
		templateContext.put(
			"templateNamespace", getTemplateNamespace(ddmFormLayout));
		templateContext.put("validations", _getValidations(locale));
		templateContext.put("viewMode", ddmFormRenderingContext.isViewMode());

		return templateContext;
	}

	private String _getDDMFormContextProviderServletURL() {
		String servletContextPath = _getServletContextPath();

		return servletContextPath.concat(
			"/dynamic-data-mapping-form-context-provider/");
	}

	private Map<String, String> _getLanguageStringsMap(
		ResourceBundle resourceBundle) {

		return HashMapBuilder.put(
			"next", _language.get(resourceBundle, "next")
		).put(
			"previous", _language.get(resourceBundle, "previous")
		).build();
	}

	private List<Object> _getPages(
		DDMForm ddmForm, DDMFormLayout ddmFormLayout,
		DDMFormRenderingContext ddmFormRenderingContext) {

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			new DDMFormPagesTemplateContextFactory(
				ddmForm, ddmFormLayout, ddmFormRenderingContext,
				_ddmStructureLayoutLocalService, _ddmStructureLocalService,
				_groupLocalService, _htmlParser, _jsonFactory);

		ddmFormPagesTemplateContextFactory.setDDMFormEvaluator(
			_ddmFormEvaluator);
		ddmFormPagesTemplateContextFactory.setDDMFormFieldTypeServicesRegistry(
			_ddmFormFieldTypeServicesRegistry);

		return ddmFormPagesTemplateContextFactory.create();
	}

	private String _getServletContextPath() {
		String proxyPath = _portal.getPathProxy();

		ServletConfig servletConfig =
			_ddmFormContextProviderServlet.getServletConfig();

		ServletContext servletContext = servletConfig.getServletContext();

		return proxyPath.concat(servletContext.getContextPath());
	}

	private HashMap<String, Object> _getValidations(Locale locale) {
		HashMap<String, Object> map = new HashMap<>();

		for (String key : _serviceTrackerMap.keySet()) {
			map.put(
				key,
				TransformUtil.transformToArray(
					_serviceTrackerMap.getService(key),
					ddmValidation -> HashMapBuilder.put(
						"label", ddmValidation.getLabel(locale)
					).put(
						"name", ddmValidation.getName()
					).put(
						"parameterMessage",
						ddmValidation.getParameterMessage(locale)
					).put(
						"template", ddmValidation.getTemplate()
					).build(),
					Object.class));
		}

		return map;
	}

	private void _setDDMFormFieldsEvaluableProperty(
		DDMForm ddmForm, DDMFormLayout ddmFormLayout) {

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (String evaluableDDMFormFieldName :
				_ddmFormTemplateContextFactoryHelper.
					getEvaluableDDMFormFieldNames(ddmForm, ddmFormLayout)) {

			DDMFormField ddmFormField = ddmFormFieldsMap.get(
				evaluableDDMFormFieldName);

			ddmFormField.setProperty("evaluable", true);
		}
	}

	private Map<String, Object> _toMap(DDMFormRule ddmFormRule) {
		return HashMapBuilder.<String, Object>put(
			"actions", ddmFormRule.getActions()
		).put(
			"condition", ddmFormRule.getCondition()
		).put(
			"enable", ddmFormRule.isEnabled()
		).build();
	}

	private List<Object> _toObjectList(List<DDMFormRule> ddmFormRules) {
		if (ddmFormRules == null) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(ddmFormRules, this::_toMap);
	}

	@Reference
	private DDM _ddm;

	@Reference(
		target = "(osgi.http.whiteboard.servlet.name=com.liferay.dynamic.data.mapping.form.renderer.internal.servlet.DDMFormContextProviderServlet)"
	)
	private Servlet _ddmFormContextProviderServlet;

	@Reference
	private DDMFormEvaluator _ddmFormEvaluator;

	@Reference
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;

	private final DDMFormTemplateContextFactoryHelper
		_ddmFormTemplateContextFactoryHelper =
			new DDMFormTemplateContextFactoryHelper();

	@Reference
	private DDMStructureLayoutLocalService _ddmStructureLayoutLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, List<DDMValidation>> _serviceTrackerMap;

}