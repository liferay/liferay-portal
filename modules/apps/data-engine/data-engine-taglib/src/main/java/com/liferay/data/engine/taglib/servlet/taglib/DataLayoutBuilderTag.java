/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.taglib.servlet.taglib;

import com.liferay.data.engine.content.type.DataDefinitionContentType;
import com.liferay.data.engine.field.type.util.LocalizedValueUtil;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.dto.v2_0.util.DataDefinitionDDMFormUtil;
import com.liferay.data.engine.rest.resource.v2_0.DataLayoutResource;
import com.liferay.data.engine.taglib.internal.servlet.taglib.util.DataLayoutTaglibUtil;
import com.liferay.data.engine.taglib.servlet.taglib.base.BaseDataLayoutBuilderTag;
import com.liferay.data.engine.taglib.servlet.taglib.definition.DataLayoutBuilderDefinition;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.spi.form.builder.settings.DDMFormBuilderSettingsRetrieverHelper;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormLayoutFactory;
import com.liferay.frontend.js.loader.modules.extender.esm.ESImportUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.servlet.taglib.aui.ESImport;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Jeyvison Nascimento
 * @author Leonardo Barros
 */
public class DataLayoutBuilderTag extends BaseDataLayoutBuilderTag {

	@Override
	public int doStartTag() throws JspException {
		int result = super.doStartTag();

		try {
			HttpServletRequest httpServletRequest = getRequest();

			if (Validator.isNotNull(getDataDefinitionId()) &&
				Validator.isNull(getDataLayoutId())) {

				setDataLayoutId(
					_getDefaultDataLayoutId(
						getDataDefinitionId(), httpServletRequest));
			}

			setNamespacedAttribute(
				httpServletRequest, "fieldTypes",
				DataLayoutTaglibUtil.getFieldTypesJSONArray(
					httpServletRequest, getScopes(),
					getSearchableFieldsDisabled()));
			setNamespacedAttribute(
				httpServletRequest, "fieldTypesModules",
				_resolveFieldTypesModules());
			setNamespacedAttribute(
				httpServletRequest, "sidebarPanels", _getSidebarPanels());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return result;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		Set<Locale> availableLocales = _getAvailableLocales(
			getDataDefinitionId(), getDataLayoutId(), httpServletRequest);

		setNamespacedAttribute(
			httpServletRequest, "availableLanguageIds",
			TransformUtil.transformToArray(
				availableLocales, LanguageUtil::getLanguageId, String.class));
		setNamespacedAttribute(
			httpServletRequest, "availableLocales",
			availableLocales.toArray(new Locale[0]));

		HttpServletRequest tagHttpServletRequest = getRequest();

		setNamespacedAttribute(
			httpServletRequest, "config",
			_getDataLayoutConfigJSONObject(
				getContentType(), tagHttpServletRequest.getLocale()));

		setNamespacedAttribute(
			httpServletRequest, "contentTypeConfig",
			_getContentTypeConfigJSONObject(getContentType()));
		setNamespacedAttribute(
			httpServletRequest, "dataLayout",
			_getDataLayoutJSONObject(
				availableLocales, getContentType(), getDataDefinitionId(),
				getDataLayoutId(), httpServletRequest,
				(HttpServletResponse)pageContext.getResponse()));
		setNamespacedAttribute(
			httpServletRequest, "defaultLanguageId", _getDefaultLanguageId());
		setNamespacedAttribute(
			httpServletRequest, "moduleServletContext",
			_getModuleServletContext());
	}

	private Set<Locale> _getAvailableLocales(
		Long dataDefinitionId, Long dataLayoutId,
		HttpServletRequest httpServletRequest) {

		if (Validator.isNull(dataDefinitionId) &&
			Validator.isNull(dataLayoutId)) {

			return SetUtil.fromArray(LocaleThreadLocal.getSiteDefaultLocale());
		}

		try {
			Set<Locale> availableLocales = new HashSet<>();

			DataDefinition dataDefinition = null;

			if (Validator.isNotNull(dataDefinitionId)) {
				dataDefinition = DataLayoutTaglibUtil.getDataDefinition(
					dataDefinitionId, httpServletRequest);
			}
			else {
				DataLayout dataLayout = _getDataLayout(
					dataLayoutId, httpServletRequest);

				dataDefinition = DataLayoutTaglibUtil.getDataDefinition(
					dataLayout.getDataDefinitionId(), httpServletRequest);
			}

			for (String languageId : dataDefinition.getAvailableLanguageIds()) {
				availableLocales.add(LocaleUtil.fromLanguageId(languageId));
			}

			return availableLocales;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return SetUtil.fromArray(LocaleThreadLocal.getSiteDefaultLocale());
	}

	private JSONObject _getContentTypeConfigJSONObject(String contentType) {
		DataDefinitionContentType dataDefinitionContentType =
			_dataDefinitionContentTypeServiceTrackerMap.getService(contentType);

		if (dataDefinitionContentType == null) {
			dataDefinitionContentType =
				_dataDefinitionContentTypeServiceTrackerMap.getService(
					"default");
		}

		return JSONUtil.put(
			"allowInvalidAvailableLocalesForProperty",
			dataDefinitionContentType.allowInvalidAvailableLocalesForProperty()
		).put(
			"allowReferencedDataDefinitionDeletion",
			dataDefinitionContentType.allowReferencedDataDefinitionDeletion()
		);
	}

	private DataLayout _getDataLayout(
			Long dataLayoutId, HttpServletRequest httpServletRequest)
		throws Exception {

		DataLayoutResource.Factory dataLayoutResourceFactory =
			_dataLayoutResourceFactorySnapshot.get();

		DataLayoutResource.Builder dataLayoutResourceBuilder =
			dataLayoutResourceFactory.create();

		DataLayoutResource dataLayoutResource =
			dataLayoutResourceBuilder.httpServletRequest(
				httpServletRequest
			).user(
				PortalUtil.getUser(httpServletRequest)
			).build();

		return dataLayoutResource.getDataLayout(dataLayoutId);
	}

	private DataLayoutBuilderDefinition _getDataLayoutBuilderDefinition(
		String contentType) {

		DataLayoutBuilderDefinition dataLayoutBuilderDefinition =
			_dataLayoutBuilderDefinitionserviceTrackerMap.getService(
				contentType);

		if (dataLayoutBuilderDefinition == null) {
			return _dataLayoutBuilderDefinitionserviceTrackerMap.getService(
				"default");
		}

		return dataLayoutBuilderDefinition;
	}

	private JSONObject _getDataLayoutConfigJSONObject(
		String contentType, Locale locale) {

		DataLayoutBuilderDefinition dataLayoutBuilderDefinition =
			_getDataLayoutBuilderDefinition(contentType);

		JSONObject dataLayoutConfigJSONObject = JSONUtil.put(
			"allowFieldSets", dataLayoutBuilderDefinition.allowFieldSets()
		).put(
			"allowMultiplePages",
			dataLayoutBuilderDefinition.allowMultiplePages()
		).put(
			"allowNestedFields", dataLayoutBuilderDefinition.allowNestedFields()
		).put(
			"allowRules", dataLayoutBuilderDefinition.allowRules()
		).put(
			"allowSuccessPage", dataLayoutBuilderDefinition.allowSuccessPage()
		).put(
			"disabledProperties",
			dataLayoutBuilderDefinition.getDisabledProperties()
		).put(
			"disabledTabs", dataLayoutBuilderDefinition.getDisabledTabs()
		).put(
			"visibleProperties",
			dataLayoutBuilderDefinition.getVisibleProperties()
		);

		if (dataLayoutBuilderDefinition.allowRules()) {
			dataLayoutConfigJSONObject.put(
				"ruleSettings",
				JSONUtil.put(
					"dataProviderInstanceParameterSettingsURL",
					() -> {
						DDMFormBuilderSettingsRetrieverHelper
							ddmFormBuilderSettingsRetrieverHelper =
								_ddmFormBuilderSettingsRetrieverHelperSnapshot.
									get();

						return ddmFormBuilderSettingsRetrieverHelper.
							getDDMDataProviderInstanceParameterSettingsURL();
					}
				).put(
					"dataProviderInstancesURL",
					() -> {
						DDMFormBuilderSettingsRetrieverHelper
							ddmFormBuilderSettingsRetrieverHelper =
								_ddmFormBuilderSettingsRetrieverHelperSnapshot.
									get();

						return ddmFormBuilderSettingsRetrieverHelper.
							getDDMDataProviderInstancesURL();
					}
				).put(
					"functionsMetadata",
					() -> {
						DDMFormBuilderSettingsRetrieverHelper
							ddmFormBuilderSettingsRetrieverHelper =
								_ddmFormBuilderSettingsRetrieverHelperSnapshot.
									get();

						return JSONFactoryUtil.createJSONObject(
							ddmFormBuilderSettingsRetrieverHelper.
								getSerializedDDMExpressionFunctionsMetadata(
									locale));
					}
				).put(
					"functionsURL",
					() -> {
						DDMFormBuilderSettingsRetrieverHelper
							ddmFormBuilderSettingsRetrieverHelper =
								_ddmFormBuilderSettingsRetrieverHelperSnapshot.
									get();

						return ddmFormBuilderSettingsRetrieverHelper.
							getDDMFunctionsURL();
					}
				));
		}

		dataLayoutConfigJSONObject.put(
			"unimplementedProperties",
			dataLayoutBuilderDefinition.getUnimplementedProperties());

		return dataLayoutConfigJSONObject;
	}

	private JSONObject _getDataLayoutJSONObject(
		Set<Locale> availableLocales, String contentType, Long dataDefinitionId,
		Long dataLayoutId, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			String dataLayoutString = ParamUtil.getString(
				httpServletRequest, "dataLayout");

			if (Validator.isNotNull(dataLayoutString)) {
				DataLayoutDDMFormAdapter dataLayoutDDMFormAdapter =
					new DataLayoutDDMFormAdapter(
						availableLocales, contentType,
						DataLayout.toDTO(dataLayoutString), httpServletRequest,
						httpServletResponse);

				return dataLayoutDDMFormAdapter.toJSONObject();
			}

			DataLayout dataLayout = null;

			if (Validator.isNotNull(dataLayoutId)) {
				dataLayout = _getDataLayout(dataLayoutId, httpServletRequest);
			}
			else {
				DataDefinition dataDefinition =
					DataLayoutTaglibUtil.getDataDefinition(
						dataDefinitionId, httpServletRequest);

				dataLayout = dataDefinition.getDefaultDataLayout();
			}

			DataLayoutDDMFormAdapter dataLayoutDDMFormAdapter =
				new DataLayoutDDMFormAdapter(
					availableLocales, contentType, dataLayout,
					httpServletRequest, httpServletResponse);

			return dataLayoutDDMFormAdapter.toJSONObject();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return JSONFactoryUtil.createJSONObject();
		}
	}

	private Long _getDefaultDataLayoutId(
			Long dataDefinitionId, HttpServletRequest httpServletRequest)
		throws Exception {

		DataDefinition dataDefinition = DataLayoutTaglibUtil.getDataDefinition(
			dataDefinitionId, httpServletRequest);

		if (dataDefinition == null) {
			return 0L;
		}

		DataLayout dataLayout = dataDefinition.getDefaultDataLayout();

		if (dataLayout == null) {
			return 0L;
		}

		return dataLayout.getId();
	}

	private String _getDefaultLanguageId() {
		Long dataDefinitionId = getDataDefinitionId();

		String languageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		if ((dataDefinitionId == null) || (dataDefinitionId <= 0)) {
			return languageId;
		}

		DDMStructure ddmStructure =
			DDMStructureLocalServiceUtil.fetchDDMStructure(dataDefinitionId);

		if (ddmStructure == null) {
			return languageId;
		}

		return ddmStructure.getDefaultLanguageId();
	}

	private String _getESModule(
		String esModuleSpecifier, HttpServletRequest httpServletRequest) {

		ESImport esImport = ESImportUtil.getESImport(
			_absolutePortalURLBuilderFactorySnapshot.get(
			).getAbsolutePortalURLBuilder(
				httpServletRequest
			),
			esModuleSpecifier);

		return StringBundler.concat(
			"{", esImport.getSymbol(), "} from ", esImport.getModule());
	}

	private ServletContext _getModuleServletContext() {
		if (getModuleServletContext() == null) {
			return getServletContext();
		}

		return getModuleServletContext();
	}

	private Map<String, Object> _getSidebarPanels() {
		HttpServletRequest httpServletRequest = getRequest();

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", httpServletRequest.getLocale(), getClass());

		Map<String, Object> sidebarPanels =
			LinkedHashMapBuilder.<String, Object>put(
				"fields",
				HashMapBuilder.<String, Object>put(
					"icon", "forms"
				).put(
					"isLink", false
				).put(
					"label", LanguageUtil.get(resourceBundle, "builder")
				).put(
					"pluginEntryPoint",
					_getESModule(
						"{FieldsSidebar} from data-engine-taglib",
						httpServletRequest)
				).put(
					"sidebarPanelId", "fields"
				).build()
			).put(
				"rules",
				() -> {
					JSONObject dataLayoutConfigJSONObject =
						_getDataLayoutConfigJSONObject(
							getContentType(), httpServletRequest.getLocale());

					if (!dataLayoutConfigJSONObject.getBoolean("allowRules")) {
						return null;
					}

					return HashMapBuilder.<String, Object>put(
						"icon", "rules"
					).put(
						"isLink", false
					).put(
						"label", LanguageUtil.get(resourceBundle, "rules")
					).put(
						"pluginEntryPoint",
						_getESModule(
							"{RulesSidebar} from data-engine-taglib",
							httpServletRequest)
					).put(
						"sidebarPanelId", "rules"
					).build();
				}
			).build();

		List<Map<String, Object>> additionalPanels = getAdditionalPanels();

		if (ListUtil.isEmpty(additionalPanels)) {
			return sidebarPanels;
		}

		for (Map<String, Object> additionalPanel : additionalPanels) {
			sidebarPanels.put(
				(String)additionalPanel.get("sidebarPanelId"), additionalPanel);
		}

		return sidebarPanels;
	}

	private String _resolveFieldTypesModules() {
		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry =
			_ddmFormFieldTypeServicesRegistrySnapshot.get();

		return StringUtil.merge(
			TransformUtil.transform(
				ddmFormFieldTypeServicesRegistry.getDDMFormFieldTypeNames(),
				name -> {
					DDMFormFieldType ddmFormFieldType =
						ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(
							name);

					if (Validator.isNull(ddmFormFieldType.getModuleName())) {
						return null;
					}

					if (ddmFormFieldType.isCustomDDMFormFieldType()) {
						return ddmFormFieldType.getModuleName();
					}

					return _resolveModule(ddmFormFieldType.getModuleName());
				}),
			StringPool.COMMA);
	}

	private String _resolveModule(String moduleName) {
		NPMResolver npmResolver = _npmResolverSnapshot.get();

		return npmResolver.resolveModuleName(moduleName);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DataLayoutBuilderTag.class);

	private static final Snapshot<AbsolutePortalURLBuilderFactory>
		_absolutePortalURLBuilderFactorySnapshot = new Snapshot<>(
			DataLayoutBuilderTag.class, AbsolutePortalURLBuilderFactory.class);
	private static final ServiceTrackerMap<String, DataDefinitionContentType>
		_dataDefinitionContentTypeServiceTrackerMap;
	private static final ServiceTrackerMap<String, DataLayoutBuilderDefinition>
		_dataLayoutBuilderDefinitionserviceTrackerMap;
	private static final Snapshot<DataLayoutResource.Factory>
		_dataLayoutResourceFactorySnapshot = new Snapshot<>(
			DataLayoutBuilderTag.class, DataLayoutResource.Factory.class);
	private static final Snapshot<DDMFormBuilderSettingsRetrieverHelper>
		_ddmFormBuilderSettingsRetrieverHelperSnapshot = new Snapshot<>(
			DataLayoutBuilderTag.class,
			DDMFormBuilderSettingsRetrieverHelper.class);
	private static final Snapshot<DDMFormFieldTypeServicesRegistry>
		_ddmFormFieldTypeServicesRegistrySnapshot = new Snapshot<>(
			DataLayoutBuilderTag.class, DDMFormFieldTypeServicesRegistry.class);
	private static final Snapshot<DDMFormTemplateContextFactory>
		_ddmFormTemplateContextFactorySnapshot = new Snapshot<>(
			DataLayoutBuilderTag.class, DDMFormTemplateContextFactory.class);
	private static final Snapshot<DDMStructureLayoutLocalService>
		_ddmStructureLayoutLocalServiceSnapshot = new Snapshot<>(
			DataLayoutBuilderTag.class, DDMStructureLayoutLocalService.class);
	private static final Snapshot<DDMStructureLocalService>
		_ddmStructureLocalServiceSnapshot = new Snapshot<>(
			DataLayoutBuilderTag.class, DDMStructureLocalService.class);
	private static final Snapshot<DDMFormLayoutDeserializer>
		_jsonDDMFormLayoutDeserializerSnapshot = new Snapshot<>(
			DataLayoutBuilderTag.class, DDMFormLayoutDeserializer.class,
			"(ddm.form.layout.deserializer.type=json)");
	private static final Snapshot<NPMResolver> _npmResolverSnapshot =
		new Snapshot<>(DataLayoutBuilderTag.class, NPMResolver.class);

	static {
		Bundle bundle = FrameworkUtil.getBundle(DataLayoutBuilderTag.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_dataLayoutBuilderDefinitionserviceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, DataLayoutBuilderDefinition.class,
				"content.type");
		_dataDefinitionContentTypeServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, DataDefinitionContentType.class, "content.type");
	}

	private class DataLayoutDDMFormAdapter {

		public DataLayoutDDMFormAdapter(
			Set<Locale> availableLocales, String contentType,
			DataLayout dataLayout, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			_availableLocales = availableLocales;
			_contentType = contentType;
			_dataLayout = dataLayout;
			_httpServletRequest = httpServletRequest;
			_httpServletResponse = httpServletResponse;
		}

		public JSONObject toJSONObject() throws Exception {
			DDMForm ddmForm = null;

			if (_dataLayout.getId() == null) {
				DataDefinition dataDefinition = DataDefinition.toDTO(
					_httpServletRequest.getParameter("dataDefinition"));

				ddmForm = DataDefinitionDDMFormUtil.toDDMForm(
					dataDefinition,
					_ddmFormFieldTypeServicesRegistrySnapshot.get());
			}
			else {
				ddmForm = _getDDMForm();
			}

			Locale defaultLocale = ddmForm.getDefaultLocale();

			DDMFormTemplateContextFactory ddmFormTemplateContextFactory =
				_ddmFormTemplateContextFactorySnapshot.get();

			Map<String, Object> ddmFormTemplateContext =
				ddmFormTemplateContextFactory.create(
					ddmForm, _getDDMFormLayout(),
					new DDMFormRenderingContext() {
						{
							setHttpServletRequest(_httpServletRequest);
							setHttpServletResponse(_httpServletResponse);
							setLocale(defaultLocale);
							setPortletNamespace(StringPool.BLANK);
						}
					});

			_populateDDMFormFieldSettingsContext(
				ddmForm.getDDMFormFieldsMap(true), ddmFormTemplateContext,
				defaultLocale);

			ddmFormTemplateContext.put(
				"rules",
				JSONUtil.toJSONArray(
					_dataLayout.getDataRules(),
					dataRule -> JSONUtil.put(
						"actions",
						JSONUtil.toJSONArray(
							(Map<String, Object>[])dataRule.getActions(),
							action -> {
								JSONObject jsonObject =
									JSONFactoryUtil.createJSONObject();

								action.forEach(jsonObject::put);

								return jsonObject;
							})
					).put(
						"conditions",
						JSONUtil.toJSONArray(
							(Map<String, Object>[])dataRule.getConditions(),
							condition -> {
								JSONObject jsonObject =
									JSONFactoryUtil.createJSONObject();

								condition.forEach(jsonObject::put);

								return jsonObject;
							})
					).put(
						"logicalOperator", dataRule.getLogicalOperator()
					).put(
						"name",
						LocalizedValueUtil.toJSONObject(dataRule.getName())
					)));

			return JSONFactoryUtil.createJSONObject(
				JSONFactoryUtil.looseSerializeDeep(ddmFormTemplateContext));
		}

		private Map<String, Object> _createDDMFormFieldSettingContext(
				DDMFormField ddmFormField, Locale defaultLocale)
			throws Exception {

			DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry =
				_ddmFormFieldTypeServicesRegistrySnapshot.get();

			DDMFormFieldType ddmFormFieldType =
				ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(
					ddmFormField.getType());

			DDMForm ddmForm = DDMFormFactory.create(
				ddmFormFieldType.getDDMFormFieldTypeSettings());

			DDMFormLayout ddmFormLayout = DDMFormLayoutFactory.create(
				ddmFormFieldType.getDDMFormFieldTypeSettings());

			_removeDisabledProperties(ddmForm, ddmFormLayout);

			DDMFormTemplateContextFactory ddmFormTemplateContextFactory =
				_ddmFormTemplateContextFactorySnapshot.get();

			return ddmFormTemplateContextFactory.create(
				ddmForm, ddmFormLayout,
				new DDMFormRenderingContext() {
					{
						setContainerId("settings");
						setDDMFormValues(
							_createDDMFormFieldSettingContextDDMFormValues(
								ddmFormField, ddmForm));
						setHttpServletRequest(_httpServletRequest);
						setHttpServletResponse(_httpServletResponse);
						setLocale(defaultLocale);
						setPortletNamespace(StringPool.BLANK);
					}
				});
		}

		private DDMFormValues _createDDMFormFieldSettingContextDDMFormValues(
				DDMFormField ddmFormField,
				DDMForm ddmFormFieldTypeSettingsDDMForm)
			throws Exception {

			DDMFormValues ddmFormValues = new DDMFormValues(
				ddmFormFieldTypeSettingsDDMForm);

			DDMForm ddmForm = ddmFormField.getDDMForm();
			Map<String, Object> ddmFormFieldProperties =
				ddmFormField.getProperties();

			for (DDMFormField ddmFormFieldTypeSetting :
					ddmFormFieldTypeSettingsDDMForm.getDDMFormFields()) {

				ddmFormValues.addDDMFormFieldValue(
					new DDMFormFieldValue() {
						{
							setName(ddmFormFieldTypeSetting.getName());
							setValue(
								_createDDMFormFieldValue(
									ddmForm.getAvailableLocales(),
									ddmFormFieldTypeSetting,
									ddmFormFieldProperties.get(
										ddmFormFieldTypeSetting.getName())));
						}
					});
			}

			return ddmFormValues;
		}

		private Value _createDDMFormFieldValue(
			DDMFormFieldValidation ddmFormFieldValidation) {

			if (ddmFormFieldValidation == null) {
				return new UnlocalizedValue(StringPool.BLANK);
			}

			DDMFormFieldValidationExpression ddmFormFieldValidationExpression =
				ddmFormFieldValidation.getDDMFormFieldValidationExpression();

			return new UnlocalizedValue(
				JSONUtil.put(
					"errorMessage",
					LocalizedValueUtil.toJSONObject(
						LocalizedValueUtil.toLocalizedValuesMap(
							ddmFormFieldValidation.
								getErrorMessageLocalizedValue()))
				).put(
					"expression",
					JSONUtil.put(
						"name", ddmFormFieldValidationExpression.getName()
					).put(
						"value", ddmFormFieldValidationExpression.getValue()
					)
				).put(
					"parameter",
					LocalizedValueUtil.toJSONObject(
						LocalizedValueUtil.toLocalizedValuesMap(
							ddmFormFieldValidation.
								getParameterLocalizedValue()))
				).toString());
		}

		private Value _createDDMFormFieldValue(
				Set<Locale> availableLocales,
				DDMFormField ddmFormFieldTypeSetting, Object propertyValue)
			throws Exception {

			if (ddmFormFieldTypeSetting.isLocalizable()) {
				return (LocalizedValue)propertyValue;
			}

			if (Objects.equals(
					ddmFormFieldTypeSetting.getDataType(), "ddm-options")) {

				if (propertyValue == null) {
					propertyValue = new DDMFormFieldOptions();
				}

				return _createDDMFormFieldValue(
					availableLocales, (DDMFormFieldOptions)propertyValue);
			}

			if (Objects.equals(
					ddmFormFieldTypeSetting.getName(), "requiredDescription") &&
				(propertyValue == null)) {

				return new UnlocalizedValue(Boolean.TRUE.toString());
			}

			if (Objects.equals(
					ddmFormFieldTypeSetting.getType(), "validation")) {

				return _createDDMFormFieldValue(
					(DDMFormFieldValidation)propertyValue);
			}

			return new UnlocalizedValue(String.valueOf(propertyValue));
		}

		private Value _createDDMFormFieldValue(
				Set<Locale> availableLocales,
				DDMFormFieldOptions ddmFormFieldOptions)
			throws Exception {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			for (Locale availableLocale : availableLocales) {
				jsonObject.put(
					LocaleUtil.toLanguageId(availableLocale),
					JSONUtil.toJSONArray(
						ddmFormFieldOptions.getOptionsValues(),
						optionValue -> {
							LocalizedValue localizedValue =
								ddmFormFieldOptions.getOptionLabels(
									optionValue);

							return JSONUtil.put(
								"label",
								localizedValue.getString(availableLocale)
							).put(
								"reference",
								ddmFormFieldOptions.getOptionReference(
									optionValue)
							).put(
								"value", optionValue
							);
						}));
			}

			return new UnlocalizedValue(jsonObject.toString());
		}

		private DDMFormLayout _deserializeDDMFormLayout(String content) {
			DDMFormLayoutDeserializerDeserializeRequest.Builder builder =
				DDMFormLayoutDeserializerDeserializeRequest.Builder.newBuilder(
					content);

			DDMFormLayoutDeserializer jsonDDMFormLayoutDeserializer =
				_jsonDDMFormLayoutDeserializerSnapshot.get();

			DDMFormLayoutDeserializerDeserializeResponse
				ddmFormLayoutDeserializerDeserializeResponse =
					jsonDDMFormLayoutDeserializer.deserialize(builder.build());

			return ddmFormLayoutDeserializerDeserializeResponse.
				getDDMFormLayout();
		}

		private DDMForm _getDDMForm() throws Exception {
			DDMStructureLocalService ddmStructureLocalService =
				_ddmStructureLocalServiceSnapshot.get();

			DDMStructure ddmStructure = ddmStructureLocalService.getStructure(
				_dataLayout.getDataDefinitionId());

			String dataDefinitionJSON = ddmStructure.getDefinition();

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				StringUtil.replace(
					dataDefinitionJSON, "defaultValue", "predefinedValue"));

			ddmStructure.setDefinition(
				jsonObject.put(
					"availableLanguageIds",
					JSONUtil.toJSONArray(
						_availableLocales,
						availableLocale -> LanguageUtil.getLanguageId(
							availableLocale))
				).put(
					"defaultLanguageId", ddmStructure.getDefaultLanguageId()
				).toString());

			return ddmStructure.getDDMForm();
		}

		private DDMFormLayout _getDDMFormLayout() throws Exception {
			String definition = null;

			if (_dataLayout.getId() == null) {
				definition = _dataLayout.toString();
			}
			else {
				DDMStructureLayoutLocalService ddmStructureLayoutLocalService =
					_ddmStructureLayoutLocalServiceSnapshot.get();

				DDMStructureLayout ddmStructureLayout =
					ddmStructureLayoutLocalService.getStructureLayout(
						_dataLayout.getId());

				definition = ddmStructureLayout.getDefinition();
			}

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				StringUtil.replace(
					definition,
					new String[] {
						"columnSize", "dataLayoutColumns", "dataLayoutPages",
						"dataLayoutRows"
					},
					new String[] {"size", "columns", "pages", "rows"}));

			return _deserializeDDMFormLayout(jsonObject.toString());
		}

		private List<Map<String, Object>> _getNestedFields(
			Map<String, Object> field) {

			List<Map<String, Object>> nestedFields = new ArrayList<>();

			List<Map<String, Object>> fieldNestedFields =
				(List<Map<String, Object>>)field.get("nestedFields");

			if (fieldNestedFields == null) {
				return nestedFields;
			}

			for (Map<String, Object> nestedField : fieldNestedFields) {
				nestedFields.add(nestedField);

				nestedFields.addAll(_getNestedFields(nestedField));
			}

			return nestedFields;
		}

		private boolean _isFieldSet(Map<String, Object> field) {
			return Objects.equals(field.get("type"), "fieldset");
		}

		private void _populateDDMFormFieldSettingsContext(
				Map<String, DDMFormField> ddmFormFieldsMap,
				Map<String, Object> ddmFormTemplateContext,
				Locale defaultLocale)
			throws Exception {

			UnsafeConsumer<Map<String, Object>, Exception> unsafeConsumer =
				field -> {
					DDMFormField ddmFormField = ddmFormFieldsMap.get(
						MapUtil.getString(field, "fieldName"));

					if (_isFieldSet(field)) {
						ddmFormField.setProperty("rows", field.get("rows"));
					}

					field.put(
						"settingsContext",
						_createDDMFormFieldSettingContext(
							ddmFormField, defaultLocale));
				};

			List<Map<String, Object>> pages =
				(List<Map<String, Object>>)ddmFormTemplateContext.get("pages");

			for (Map<String, Object> page : pages) {
				List<Map<String, Object>> rows =
					(List<Map<String, Object>>)page.get("rows");

				for (Map<String, Object> row : rows) {
					List<Map<String, Object>> columns =
						(List<Map<String, Object>>)row.get("columns");

					for (Map<String, Object> column : columns) {
						List<Map<String, Object>> fields =
							(List<Map<String, Object>>)column.get("fields");

						for (Map<String, Object> field : fields) {
							unsafeConsumer.accept(field);

							List<Map<String, Object>> nestedFields =
								_getNestedFields(field);

							for (Map<String, Object> nestedField :
									nestedFields) {

								unsafeConsumer.accept(nestedField);
							}
						}
					}
				}
			}
		}

		private void _removeDisabledProperties(
			DDMForm ddmForm, DDMFormLayout ddmFormLayout) {

			DataLayoutBuilderDefinition dataLayoutBuilderDefinition =
				_getDataLayoutBuilderDefinition(_contentType);

			String[] disabledProperties =
				dataLayoutBuilderDefinition.getDisabledProperties();

			if (ArrayUtil.isEmpty(disabledProperties)) {
				return;
			}

			for (String disabledProperty : disabledProperties) {
				Map<String, DDMFormField> ddmFormFieldsMap =
					ddmForm.getDDMFormFieldsMap(true);

				List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

				ddmFormFields.remove(ddmFormFieldsMap.get(disabledProperty));

				for (DDMFormLayoutPage ddmFormLayoutPage :
						ddmFormLayout.getDDMFormLayoutPages()) {

					for (DDMFormLayoutRow ddmFormLayoutRow :
							ddmFormLayoutPage.getDDMFormLayoutRows()) {

						for (DDMFormLayoutColumn ddmFormLayoutColumn :
								ddmFormLayoutRow.getDDMFormLayoutColumns()) {

							List<String> ddmFormFieldNames =
								ddmFormLayoutColumn.getDDMFormFieldNames();

							ddmFormFieldNames.remove(disabledProperty);
						}
					}
				}
			}
		}

		private final Set<Locale> _availableLocales;
		private final String _contentType;
		private final DataLayout _dataLayout;
		private final HttpServletRequest _httpServletRequest;
		private final HttpServletResponse _httpServletResponse;

	}

}