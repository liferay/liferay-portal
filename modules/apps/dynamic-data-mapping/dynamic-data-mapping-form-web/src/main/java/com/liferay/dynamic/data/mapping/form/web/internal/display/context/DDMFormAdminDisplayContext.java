/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormBuilderContextFactory;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormBuilderContextRequest;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormBuilderContextResponse;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializer;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializerRequest;
import com.liferay.dynamic.data.mapping.form.builder.settings.DDMFormBuilderSettingsRequest;
import com.liferay.dynamic.data.mapping.form.builder.settings.DDMFormBuilderSettingsResponse;
import com.liferay.dynamic.data.mapping.form.builder.settings.DDMFormBuilderSettingsRetriever;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.form.web.internal.configuration.DDMFormWebConfiguration;
import com.liferay.dynamic.data.mapping.form.web.internal.constants.DDMFormWebKeys;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.helper.DDMFormAdminRequestHelper;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.helper.FormInstancePermissionCheckerHelper;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.util.DDMFormAdminActionDropdownItemsProvider;
import com.liferay.dynamic.data.mapping.form.web.internal.search.DDMFormInstanceRowChecker;
import com.liferay.dynamic.data.mapping.form.web.internal.search.DDMFormInstanceSearch;
import com.liferay.dynamic.data.mapping.form.web.internal.util.DDMLayoutUtil;
import com.liferay.dynamic.data.mapping.io.DDMFormFieldTypesSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormFieldTypesSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormFieldTypesSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterRegistry;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapter;
import com.liferay.dynamic.data.mapping.storage.DDMStorageAdapterRegistry;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormLayoutFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.dynamic.data.mapping.util.comparator.DDMFormInstanceModifiedDateComparator;
import com.liferay.dynamic.data.mapping.util.comparator.DDMFormInstanceNameComparator;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.frontend.taglib.servlet.taglib.util.EmptyResultMessageKeys;
import com.liferay.journal.article.dynamic.data.mapping.form.field.type.constants.JournalArticleDDMFormFieldTypeConstants;
import com.liferay.layout.dynamic.data.mapping.form.field.type.constants.LayoutDDMFormFieldTypeConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.servlet.PipingServletResponseFactory;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Bruno Basto
 */
public class DDMFormAdminDisplayContext {

	public DDMFormAdminDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse,
		DDMFormBuilderContextFactory ddmFormBuilderContextFactory,
		DDMFormBuilderSettingsRetriever ddmFormBuilderSettingsRetriever,
		DDMFormContextDeserializer<DDMFormValues> ddmFormContextToDDMFormValues,
		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry,
		DDMFormFieldTypesSerializer ddmFormFieldTypesSerializer,
		DDMFormInstanceLocalService ddmFormInstanceLocalService,
		DDMFormInstanceRecordLocalService ddmFormInstanceRecordLocalService,
		DDMFormInstanceRecordWriterRegistry ddmFormInstanceRecordWriterRegistry,
		DDMFormInstanceService ddmFormInstanceService,
		DDMFormInstanceVersionLocalService ddmFormInstanceVersionLocalService,
		DDMFormRenderer ddmFormRenderer,
		DDMFormTemplateContextFactory ddmFormTemplateContextFactory,
		DDMFormValuesFactory ddmFormValuesFactory,
		DDMFormValuesMerger ddmFormValuesMerger,
		DDMFormWebConfiguration ddmFormWebConfiguration,
		DDMStorageAdapterRegistry ddmStorageAdapterRegistry,
		DDMStructureLocalService ddmStructureLocalService,
		DDMStructureService ddmStructureService, JSONFactory jsonFactory,
		NPMResolver npmResolver,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		Portal portal) {

		_ddmFormBuilderContextFactory = ddmFormBuilderContextFactory;
		_ddmFormBuilderSettingsRetriever = ddmFormBuilderSettingsRetriever;
		_ddmFormContextToDDMFormValues = ddmFormContextToDDMFormValues;
		_ddmFormFieldTypeServicesRegistry = ddmFormFieldTypeServicesRegistry;
		_ddmFormFieldTypesSerializer = ddmFormFieldTypesSerializer;
		_ddmFormInstanceLocalService = ddmFormInstanceLocalService;
		_ddmFormInstanceRecordLocalService = ddmFormInstanceRecordLocalService;
		_ddmFormInstanceRecordWriterRegistry =
			ddmFormInstanceRecordWriterRegistry;
		_ddmFormInstanceService = ddmFormInstanceService;
		_ddmFormInstanceVersionLocalService =
			ddmFormInstanceVersionLocalService;
		_ddmFormTemplateContextFactory = ddmFormTemplateContextFactory;
		_ddmFormValuesFactory = ddmFormValuesFactory;
		_ddmFormValuesMerger = ddmFormValuesMerger;
		_ddmFormWebConfiguration = ddmFormWebConfiguration;
		_ddmStorageAdapterRegistry = ddmStorageAdapterRegistry;
		_ddmStructureLocalService = ddmStructureLocalService;
		_ddmStructureService = ddmStructureService;
		_npmResolver = npmResolver;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_portal = portal;

		this.renderRequest = renderRequest;
		this.renderResponse = renderResponse;
		this.ddmFormRenderer = ddmFormRenderer;
		this.jsonFactory = jsonFactory;

		ddmFormAdminRequestHelper = new DDMFormAdminRequestHelper(
			renderRequest);

		_formInstancePermissionCheckerHelper =
			new FormInstancePermissionCheckerHelper(ddmFormAdminRequestHelper);
	}

	public List<DropdownItem> getActionDropdownItems(
			DDMFormInstance ddmFormInstance)
		throws PortalException {

		boolean invalidDDMFormInstance = false;

		if (!hasValidDDMFormFields(ddmFormInstance) ||
			!hasValidStorageType(ddmFormInstance)) {

			invalidDDMFormInstance = true;
		}

		DDMFormAdminActionDropdownItemsProvider
			ddmFormAdminActionDropdownItemsProvider =
				new DDMFormAdminActionDropdownItemsProvider(
					getAutocompleteUserURL(), ddmFormInstance,
					getExportFormURL(ddmFormInstance.getFormInstanceId()),
					_formInstancePermissionCheckerHelper,
					isFormPublished(ddmFormInstance),
					ddmFormAdminRequestHelper.getRequest(),
					invalidDDMFormInstance,
					getFormLocalizedNameJSONObject(ddmFormInstance),
					getPublishedFormURL(ddmFormInstance), renderResponse,
					getScopeGroupId(),
					getShareFormInstanceURL(ddmFormInstance));

		return ddmFormAdminActionDropdownItemsProvider.getActionDropdownItems();
	}

	public List<DropdownItem> getActionItemsDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteFormInstances");
				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					LanguageUtil.get(
						ddmFormAdminRequestHelper.getRequest(), "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public String getAutocompleteUserURL() {
		LiferayPortletURL autocompleteUserURL =
			(LiferayPortletURL)renderResponse.createResourceURL();

		autocompleteUserURL.setCopyCurrentRenderParameters(false);
		autocompleteUserURL.setResourceID("/admin/autocomplete_user");

		return autocompleteUserURL.toString();
	}

	public int getAutosaveInterval() {
		return _ddmFormWebConfiguration.autosaveInterval();
	}

	public Set<String> getAvailableExportTypes() {
		return _ddmFormInstanceRecordWriterRegistry.
			getDDMFormInstanceRecordWriterTypes();
	}

	public JSONArray getAvailableLanguageIdsJSONArray() {
		JSONArray availableLanguageIdsJSONArray = jsonFactory.createJSONArray();

		for (Locale availableLocale : getAvailableLocales()) {
			availableLanguageIdsJSONArray.put(
				LocaleUtil.toLanguageId(availableLocale));
		}

		return availableLanguageIdsJSONArray;
	}

	public Locale[] getAvailableLocales() {
		Locale[] availableLocales = _getFormBuilderContextAvailableLocales();

		if (availableLocales != null) {
			return availableLocales;
		}

		availableLocales = _getFormAvailableLocales();

		if (availableLocales != null) {
			return availableLocales;
		}

		return new Locale[] {getDefaultLocale()};
	}

	public String getClearResultsURL() throws PortletException {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(getPortletURL(), renderResponse)
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public long getCompanyId() {
		return ddmFormAdminRequestHelper.getCompanyId();
	}

	public CreationMenu getCreationMenu() {
		if (!_formInstancePermissionCheckerHelper.isShowAddButton()) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			_getAddFormDropdownItem()
		).build();
	}

	public String getCSVExport() {
		return _ddmFormWebConfiguration.csvExport();
	}

	public String getDataEngineModule() {
		return _npmResolver.resolveModuleName("data-engine-js-components-web");
	}

	public String getDataProviderInstanceParameterSettingsURL()
		throws PortalException {

		DDMFormBuilderSettingsResponse ddmFormBuilderSettingsResponse =
			_getDDMFormBuilderSettingsResponse();

		return ddmFormBuilderSettingsResponse.
			getDataProviderInstanceParameterSettingsURL();
	}

	public String getDataProviderInstancesURL() throws PortalException {
		DDMFormBuilderSettingsResponse ddmFormBuilderSettingsResponse =
			_getDDMFormBuilderSettingsResponse();

		return ddmFormBuilderSettingsResponse.getDataProviderInstancesURL();
	}

	public Map<String, Object> getDDMFormContext(RenderRequest renderRequest)
		throws Exception {

		return getDDMFormContext(renderRequest, true);
	}

	public Map<String, Object> getDDMFormContext(
			RenderRequest renderRequest, boolean readOnly)
		throws Exception {

		DDMFormViewFormInstanceRecordDisplayContext
			ddmFormViewFormInstanceRecordDisplayContext =
				getDDMFormViewFormInstanceRecordDisplayContext();

		return ddmFormViewFormInstanceRecordDisplayContext.getDDMFormContext(
			renderRequest, readOnly);
	}

	public JSONArray getDDMFormFieldTypesJSONArray() throws PortalException {
		List<DDMFormFieldType> availableDDMFormFieldTypes =
			_filterDDMFormFieldTypes(
				_ddmFormFieldTypeServicesRegistry.getDDMFormFieldTypes());

		String serializedFormFieldTypes = _serialize(
			availableDDMFormFieldTypes);

		JSONArray jsonArray = jsonFactory.createJSONArray(
			serializedFormFieldTypes);

		HttpServletRequest httpServletRequest =
			ddmFormAdminRequestHelper.getRequest();

		HttpServletResponse httpServletResponse =
			PortalUtil.getHttpServletResponse(renderResponse);

		for (int i = 0; i < jsonArray.length(); i++) {
			DDMFormFieldType ddmFormFieldType = availableDDMFormFieldTypes.get(
				i);

			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Class<?> ddmFormFieldTypeSettings =
				ddmFormFieldType.getDDMFormFieldTypeSettings();

			DDMForm ddmForm = DDMFormFactory.create(ddmFormFieldTypeSettings);

			DDMFormLayout ddmFormLayout = DDMFormLayoutFactory.create(
				ddmFormFieldTypeSettings);

			DDMFormRenderingContext ddmFormRenderingContext =
				new DDMFormRenderingContext();

			ddmFormRenderingContext.setHttpServletRequest(httpServletRequest);
			ddmFormRenderingContext.setHttpServletResponse(httpServletResponse);
			ddmFormRenderingContext.setContainerId("settings");
			ddmFormRenderingContext.setGroupId(getScopeGroupId());
			ddmFormRenderingContext.setLocale(
				LocaleThreadLocal.getThemeDisplayLocale());
			ddmFormRenderingContext.setPortletNamespace(
				renderResponse.getNamespace());

			try {
				Map<String, Object> ddmFormTemplateContext =
					_ddmFormTemplateContextFactory.create(
						ddmForm, ddmFormLayout, ddmFormRenderingContext);

				jsonObject.put(
					"settingsContext",
					jsonFactory.createJSONObject(
						jsonFactory.looseSerializeDeep(
							ddmFormTemplateContext)));

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				if ((themeDisplay != null) &&
					StringUtil.equals(
						ddmFormFieldType.getName(), "rich_text")) {

					EditorConfiguration editorConfiguration =
						EditorConfigurationFactoryUtil.getEditorConfiguration(
							StringPool.BLANK, ddmFormFieldType.getName(),
							FeatureFlagManagerUtil.isEnabled("LPD-11235") ?
								"ckeditor5_classic" : "ckeditor_classic",
							new HashMap<String, Object>(), themeDisplay,
							RequestBackedPortletURLFactoryUtil.create(
								httpServletRequest));

					Map<String, Object> editorConfigurationData =
						editorConfiguration.getData();

					jsonObject.put(
						"editorConfig",
						editorConfigurationData.get("editorConfig"));
				}
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		return jsonArray;
	}

	public DDMFormInstance getDDMFormInstance() throws PortalException {
		if (_ddmFormInstance != null) {
			return _ddmFormInstance;
		}

		long formInstanceId = ParamUtil.getLong(
			renderRequest, "formInstanceId");

		if (formInstanceId > 0) {
			_ddmFormInstance = _ddmFormInstanceService.fetchFormInstance(
				formInstanceId);
		}
		else {
			DDMFormInstanceRecord ddmFormInstanceRecord =
				getDDMFormInstanceRecord();

			if (ddmFormInstanceRecord != null) {
				_ddmFormInstance = ddmFormInstanceRecord.getFormInstance();
			}
		}

		return _ddmFormInstance;
	}

	public DDMFormInstanceRecordVersion getDDMFormInstanceRecordVersion()
		throws PortalException {

		DDMFormInstanceRecord ddmFormInstanceRecord =
			getDDMFormInstanceRecord();

		return ddmFormInstanceRecord.getLatestFormInstanceRecordVersion();
	}

	public JSONArray getDDMFormRulesJSONArray() throws PortalException {
		DDMFormBuilderSettingsResponse ddmFormBuilderSettingsResponse =
			_getDDMFormBuilderSettingsResponse();

		return jsonFactory.createJSONArray(
			ddmFormBuilderSettingsResponse.getSerializedDDMFormRules());
	}

	public Map<String, Object> getDDMFormSettingsContext(
			PageContext pageContext)
		throws Exception {

		DDMForm ddmForm = createSettingsDDMForm(
			ParamUtil.getLong(renderRequest, "formInstanceId"),
			(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY));

		DDMFormLayout ddmFormLayout = DDMFormLayoutFactory.create(
			DDMFormInstanceSettings.class);

		ddmFormLayout.setPaginationMode(DDMFormLayout.TABBED_MODE);

		DDMFormRenderingContext ddmFormRenderingContext =
			createDDMFormRenderingContext(pageContext, renderRequest);

		ddmFormRenderingContext.setDDMFormValues(
			getDDMFormRenderingContextDDMFormValues());

		return ddmFormRenderer.getDDMFormTemplateContext(
			ddmForm, ddmFormLayout, ddmFormRenderingContext);
	}

	public DDMFormViewFormInstanceRecordDisplayContext
		getDDMFormViewFormInstanceRecordDisplayContext() {

		return new DDMFormViewFormInstanceRecordDisplayContext(
			ddmFormAdminRequestHelper.getRequest(),
			PortalUtil.getHttpServletResponse(renderResponse),
			_ddmFormInstanceRecordLocalService,
			_ddmFormInstanceVersionLocalService, ddmFormRenderer,
			_ddmFormValuesFactory, _ddmFormValuesMerger);
	}

	public DDMFormViewFormInstanceRecordsDisplayContext
			getDDMFormViewFormInstanceRecordsDisplayContext()
		throws PortalException {

		return new DDMFormViewFormInstanceRecordsDisplayContext(
			renderRequest, renderResponse, getDDMFormInstance(),
			_ddmFormInstanceRecordLocalService,
			_ddmFormFieldTypeServicesRegistry);
	}

	public DDMStructure getDDMStructure() throws PortalException {
		if (_ddmStructure != null) {
			return _ddmStructure;
		}

		DDMFormInstance ddmFormInstance = getDDMFormInstance();

		if (ddmFormInstance == null) {
			return null;
		}

		_ddmStructure = _ddmStructureLocalService.getStructure(
			ddmFormInstance.getStructureId());

		return _ddmStructure;
	}

	public long getDDMStructureId() throws PortalException {
		DDMStructure structure = getDDMStructure();

		if (structure == null) {
			return 0;
		}

		return structure.getStructureId();
	}

	public String getDefaultLanguageId() {
		String defaultLanguageId = _getFormBuilderContextDefaultLanguageId();

		if ((defaultLanguageId != null) &&
			LanguageUtil.isAvailableLocale(defaultLanguageId)) {

			return defaultLanguageId;
		}

		defaultLanguageId = _getFormDefaultLanguageId();

		if ((defaultLanguageId != null) &&
			LanguageUtil.isAvailableLocale(defaultLanguageId)) {

			return defaultLanguageId;
		}

		return LocaleUtil.toLanguageId(getDefaultLocale());
	}

	public String getDisplayStyle() {
		if (_displayStyle == null) {
			_displayStyle = getDisplayStyle(
				renderRequest, _ddmFormWebConfiguration, getDisplayViews());
		}

		return _displayStyle;
	}

	public String[] getDisplayViews() {
		return _DISPLAY_VIEWS;
	}

	public List<NavigationItem> getElementSetBuilderNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(true);

				HttpServletRequest httpServletRequest =
					ddmFormAdminRequestHelper.getRequest();

				navigationItem.setLabel(
					LanguageUtil.get(httpServletRequest, "builder"));
			}
		).build();
	}

	public List<DropdownItem> getEmptyResultMessageActionItemsDropdownItems() {
		if (!_formInstancePermissionCheckerHelper.isShowAddButton() ||
			isSearch()) {

			return null;
		}

		return DropdownItemListBuilder.add(
			_getAddFormDropdownItem()
		).build();
	}

	public EmptyResultMessageKeys.AnimationType
		getEmptyResultMessageAnimationType() {

		if (isSearch()) {
			return EmptyResultMessageKeys.AnimationType.SUCCESS;
		}

		return EmptyResultMessageKeys.AnimationType.EMPTY;
	}

	public String getEmptyResultMessageDescription() {
		if (isSearch()) {
			return StringPool.BLANK;
		}

		HttpServletRequest httpServletRequest =
			ddmFormAdminRequestHelper.getRequest();

		return LanguageUtil.get(
			httpServletRequest, "create-forms-to-start-collecting-data");
	}

	public String getEmptyResultsMessage() {
		SearchContainer<?> searchContainer = getSearchContainer();

		return LanguageUtil.get(
			ddmFormAdminRequestHelper.getRequest(),
			searchContainer.getEmptyResultsMessage());
	}

	public String getExportFormURL(long formInstanceId) {
		LiferayPortletURL liferayPortletURL =
			(LiferayPortletURL)renderResponse.createResourceURL();

		liferayPortletURL.setCopyCurrentRenderParameters(false);
		liferayPortletURL.setParameter(
			"formInstanceId", String.valueOf(formInstanceId));
		liferayPortletURL.setResourceID(
			"/dynamic_data_mapping_form/export_form_instance");

		return liferayPortletURL.toString();
	}

	public String getFieldSetDefinitionURL() throws PortalException {
		DDMFormBuilderSettingsResponse ddmFormBuilderSettingsResponse =
			_getDDMFormBuilderSettingsResponse();

		return ddmFormBuilderSettingsResponse.getFieldSetDefinitionURL();
	}

	public JSONArray getFieldSetsJSONArray() throws PortalException {
		DDMFormBuilderSettingsResponse ddmFormBuilderSettingsResponse =
			_getDDMFormBuilderSettingsResponse();

		return ddmFormBuilderSettingsResponse.getFieldSets();
	}

	public JSONObject getFormBuilderContextJSONObject() throws PortalException {
		String serializedFormBuilderContext = ParamUtil.getString(
			renderRequest, "serializedFormBuilderContext");

		ThemeDisplay themeDisplay = ddmFormAdminRequestHelper.getThemeDisplay();

		if (Validator.isNotNull(serializedFormBuilderContext)) {
			JSONObject jsonObject = jsonFactory.createJSONObject(
				serializedFormBuilderContext);

			_escape(themeDisplay.getLanguageId(), "description", jsonObject);
			_escape(themeDisplay.getLanguageId(), "name", jsonObject);

			return jsonObject;
		}

		DDMFormBuilderContextRequest ddmFormBuilderContextRequest =
			DDMFormBuilderContextRequest.with(
				null, themeDisplay.getRequest(), themeDisplay.getResponse(),
				LocaleUtil.fromLanguageId(themeDisplay.getLanguageId()), true);

		ddmFormBuilderContextRequest.addProperty(
			"ddmStructureVersion", getLatestDDMStructureVersion());
		ddmFormBuilderContextRequest.addProperty(
			"portletNamespace", renderResponse.getNamespace());

		DDMFormBuilderContextResponse ddmFormBuilderContextResponse =
			_ddmFormBuilderContextFactory.create(ddmFormBuilderContextRequest);

		return jsonFactory.createJSONObject(
			jsonFactory.looseSerializeDeep(
				ddmFormBuilderContextResponse.getContext()));
	}

	public List<NavigationItem> getFormBuilderNavigationItems()
		throws PortalException {

		DDMFormInstance ddmFormInstance = getDDMFormInstance();

		HttpServletRequest httpServletRequest =
			ddmFormAdminRequestHelper.getRequest();

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.putData("action", "showForm");

				navigationItem.setLabel(
					LanguageUtil.get(httpServletRequest, "form"));
			}
		).add(
			navigationItem -> {
				navigationItem.putData("action", "showRules");

				navigationItem.setLabel(
					LanguageUtil.get(httpServletRequest, "rules"));
			}
		).add(
			() ->
				!((ddmFormInstance != null) &&
				  Objects.equals(ddmFormInstance.getStorageType(), "object")),
			navigationItem -> {
				navigationItem.putData("action", "showReport");

				navigationItem.setLabel(
					LanguageUtil.get(httpServletRequest, "entries"));
			}
		).build();
	}

	public String getFormDescription() throws PortalException {
		DDMFormInstance ddmFormInstance = getDDMFormInstance();

		if (ddmFormInstance != null) {
			return LocalizationUtil.getLocalization(
				ddmFormInstance.getDescription(), _getFormDefaultLanguageId());
		}

		return getJSONObjectLocalizedPropertyFromRequest("description");
	}

	public JSONObject getFormLocalizedDescriptionJSONObject()
		throws PortalException {

		JSONObject jsonObject = jsonFactory.createJSONObject();

		DDMFormInstance ddmFormInstance = getDDMFormInstance();

		if (ddmFormInstance == null) {
			jsonObject.put(getDefaultLanguageId(), getFormDescription());
		}
		else {
			Map<Locale, String> descriptionMap =
				ddmFormInstance.getDescriptionMap();

			for (Map.Entry<Locale, String> entry : descriptionMap.entrySet()) {
				jsonObject.put(
					LocaleUtil.toLanguageId(entry.getKey()), entry.getValue());
			}
		}

		return jsonObject;
	}

	public <T> JSONObject getFormLocalizedNameJSONObject(T object)
		throws PortalException {

		DDMFormInstance ddmFormInstance = (DDMFormInstance)object;

		JSONObject jsonObject = jsonFactory.createJSONObject();

		if (ddmFormInstance == null) {
			jsonObject.put(getDefaultLanguageId(), getFormName());
		}
		else {
			Map<Locale, String> nameMap = ddmFormInstance.getNameMap();

			for (Map.Entry<Locale, String> entry : nameMap.entrySet()) {
				jsonObject.put(
					LocaleUtil.toLanguageId(entry.getKey()), entry.getValue());
			}
		}

		return jsonObject;
	}

	public String getFormName() throws PortalException {
		DDMFormInstance ddmFormInstance = getDDMFormInstance();

		if (ddmFormInstance != null) {
			return LocalizationUtil.getLocalization(
				ddmFormInstance.getName(), _getFormDefaultLanguageId());
		}

		return getJSONObjectLocalizedPropertyFromRequest("name");
	}

	public JSONObject getFunctionsMetadataJSONObject() throws PortalException {
		DDMFormBuilderSettingsResponse ddmFormBuilderSettingsResponse =
			_getDDMFormBuilderSettingsResponse();

		return jsonFactory.createJSONObject(
			ddmFormBuilderSettingsResponse.getFunctionsMetadata());
	}

	public String getFunctionsURL() throws PortalException {
		DDMFormBuilderSettingsResponse ddmFormBuilderSettingsResponse =
			_getDDMFormBuilderSettingsResponse();

		return ddmFormBuilderSettingsResponse.getFunctionsURL();
	}

	public String getInvalidDDMFormFieldType(DDMFormInstance ddmFormInstance) {
		try {
			String invalidDDMFormFieldType = _invalidDDMFormFieldTypes.get(
				ddmFormInstance.getFormInstanceId());

			if (invalidDDMFormFieldType != null) {
				return invalidDDMFormFieldType;
			}

			DDMForm ddmForm = ddmFormInstance.getDDMForm();

			for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
				DDMFormFieldType ddmFormFieldType =
					_ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(
						ddmFormField.getType());

				if (ddmFormFieldType == null) {
					_invalidDDMFormFieldTypes.put(
						ddmFormInstance.getFormInstanceId(),
						ddmFormField.getType());

					return ddmFormField.getType();
				}
			}

			_invalidDDMFormFieldTypes.put(
				ddmFormInstance.getFormInstanceId(), StringPool.BLANK);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	public DDMStructureVersion getLatestDDMStructureVersion()
		throws PortalException {

		DDMStructure structure = getDDMStructure();

		if (structure == null) {
			return null;
		}

		return structure.getLatestStructureVersion();
	}

	public long getLatestDDMStructureVersionId() throws PortalException {
		DDMStructureVersion latestDDMStructureVersion =
			getLatestDDMStructureVersion();

		if (latestDDMStructureVersion == null) {
			return 0;
		}

		return latestDDMStructureVersion.getStructureVersionId();
	}

	public String getLexiconIconsPath() {
		ThemeDisplay themeDisplay = ddmFormAdminRequestHelper.getThemeDisplay();

		String spritemap = themeDisplay.getPathThemeSpritemap();

		return spritemap + "#";
	}

	public List<NavigationItem> getNavigationItems() {
		HttpServletRequest httpServletRequest =
			ddmFormAdminRequestHelper.getRequest();

		String currentTab = ParamUtil.getString(
			httpServletRequest, "currentTab", "forms");

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(currentTab.equals("forms"));
				navigationItem.setHref(
					renderResponse.createRenderURL(), "currentTab", "forms");
				navigationItem.setLabel(
					LanguageUtil.get(httpServletRequest, "forms"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(currentTab.equals("element-set"));
				navigationItem.setHref(
					renderResponse.createRenderURL(), "currentTab",
					"element-set");
				navigationItem.setLabel(
					LanguageUtil.get(httpServletRequest, "element-sets"));
			}
		).add(
			navigationItem -> _populateDDMDataProviderNavigationItem(
				navigationItem)
		).build();
	}

	public ObjectDefinition getObjectDefinition(DDMFormInstance ddmFormInstance)
		throws PortalException {

		return _objectDefinitionLocalService.fetchObjectDefinition(
			ddmFormInstance.getObjectDefinitionId());
	}

	public String getObjectDefinitionLabel(
			DDMFormInstance ddmFormInstance, Locale locale)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				ddmFormInstance.getObjectDefinitionId());

		return objectDefinition.getLabel(locale);
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			renderRequest, DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
			"modified-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			renderRequest, DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
			"desc");

		return _orderByType;
	}

	public List<DropdownItem> getOrderItemsDropdownItems() {
		return DropdownItemListBuilder.add(
			getOrderByDropdownItem("modified-date")
		).add(
			getOrderByDropdownItem("name")
		).build();
	}

	public PermissionChecker getPermissionChecker() {
		return ddmFormAdminRequestHelper.getPermissionChecker();
	}

	public <T> T getPermissionCheckerHelper() {
		return (T)_formInstancePermissionCheckerHelper;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCPath(
			"/admin/view.jsp"
		).setKeywords(
			() -> {
				String keywords = getKeywords();

				if (Validator.isNotNull(keywords)) {
					return keywords;
				}

				return null;
			}
		).setParameter(
			"currentTab", "forms"
		).setParameter(
			"delta",
			() -> {
				String delta = ParamUtil.getString(renderRequest, "delta");

				if (Validator.isNotNull(delta)) {
					return delta;
				}

				return null;
			}
		).setParameter(
			"displayStyle",
			() -> {
				String displayStyle = ParamUtil.getString(
					renderRequest, "displayStyle");

				if (Validator.isNotNull(displayStyle)) {
					return getDisplayStyle();
				}

				return null;
			}
		).setParameter(
			"groupId", getScopeGroupId()
		).setParameter(
			"orderByCol",
			() -> {
				String orderByCol = getOrderByCol();

				if (Validator.isNotNull(orderByCol)) {
					return orderByCol;
				}

				return null;
			}
		).setParameter(
			"orderByType",
			() -> {
				String orderByType = getOrderByType();

				if (Validator.isNotNull(orderByType)) {
					return orderByType;
				}

				return null;
			}
		).buildPortletURL();
	}

	public String getPublishedFormURL() throws PortalException {
		return getPublishedFormURL(_ddmFormInstance);
	}

	public String getPublishedFormURL(DDMFormInstance ddmFormInstance)
		throws PortalException {

		if (!isFormPublished(ddmFormInstance)) {
			return StringPool.BLANK;
		}

		return getSharedFormURL() +
			String.valueOf(ddmFormInstance.getFormInstanceId());
	}

	public String getRolesURL() throws PortalException {
		DDMFormBuilderSettingsResponse ddmFormBuilderSettingsResponse =
			_getDDMFormBuilderSettingsResponse();

		return ddmFormBuilderSettingsResponse.getRolesURL();
	}

	public long getScopeGroupId() {
		return ddmFormAdminRequestHelper.getScopeGroupId();
	}

	public String getSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCPath(
			"/admin/view.jsp"
		).setParameter(
			"currentTab", "forms"
		).setParameter(
			"groupId", getScopeGroupId()
		).buildString();
	}

	public SearchContainer<?> getSearchContainer() {
		PortletURL portletURL = PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"displayStyle", getDisplayStyle()
		).buildPortletURL();

		DDMFormInstanceSearch ddmFormInstanceSearch = new DDMFormInstanceSearch(
			renderRequest, portletURL);

		if (ddmFormInstanceSearch.isSearch()) {
			ddmFormInstanceSearch.setEmptyResultsMessage("no-forms-were-found");
		}
		else {
			ddmFormInstanceSearch.setEmptyResultsMessage("there-are-no-forms");
		}

		ddmFormInstanceSearch.setOrderByCol(getOrderByCol());
		ddmFormInstanceSearch.setOrderByComparator(
			_getDDMFormInstanceOrderByComparator(
				getOrderByCol(), getOrderByType()));
		ddmFormInstanceSearch.setOrderByType(getOrderByType());
		ddmFormInstanceSearch.setResultsAndTotal(
			() -> _ddmFormInstanceService.search(
				ddmFormAdminRequestHelper.getCompanyId(),
				ddmFormAdminRequestHelper.getScopeGroupId(), getKeywords(),
				ddmFormInstanceSearch.getStart(),
				ddmFormInstanceSearch.getEnd(),
				ddmFormInstanceSearch.getOrderByComparator()),
			_ddmFormInstanceService.searchCount(
				ddmFormAdminRequestHelper.getCompanyId(),
				ddmFormAdminRequestHelper.getScopeGroupId(), getKeywords()));
		ddmFormInstanceSearch.setRowChecker(
			new DDMFormInstanceRowChecker(renderResponse));

		return ddmFormInstanceSearch;
	}

	public String getSearchContainerId() {
		return "formInstance";
	}

	public String getSharedFormURL() {
		return DDMLayoutUtil.getFormLayoutURL(
			ddmFormAdminRequestHelper.getThemeDisplay());
	}

	public String getShareFormInstanceURL(DDMFormInstance ddmFormInstance) {
		if (ddmFormInstance == null) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.createActionURL(
			renderResponse
		).setActionName(
			"/admin/share_form_instance"
		).setParameter(
			"formInstanceId",
			() -> {
				if (ddmFormInstance != null) {
					return ddmFormInstance.getFormInstanceId();
				}

				return null;
			}
		).buildString();
	}

	public String getSortingURL() throws Exception {
		return PortletURLBuilder.create(
			PortletURLUtil.clone(getPortletURL(), renderResponse)
		).setParameter(
			"orderByType",
			() -> {
				String orderByType = ParamUtil.getString(
					renderRequest, "orderByType");

				if (orderByType.equals("asc")) {
					return "desc";
				}

				return "asc";
			}
		).buildString();
	}

	public DDMStructureService getStructureService() {
		return _ddmStructureService;
	}

	public int getTotalItems() {
		SearchContainer<?> searchContainer = getSearchContainer();

		return searchContainer.getTotal();
	}

	public List<ViewTypeItem> getViewTypesItems() throws Exception {
		PortletURL portletURL = PortletURLUtil.clone(
			getPortletURL(), renderResponse);

		return new ViewTypeItemList(portletURL, getDisplayStyle()) {
			{
				String[] viewTypes = getDisplayViews();

				for (String viewType : viewTypes) {
					if (viewType.equals("descriptive")) {
						addListViewTypeItem();
					}
					else {
						addTableViewTypeItem();
					}
				}
			}
		};
	}

	public boolean hasResults() {
		if (getTotalItems() > 0) {
			return true;
		}

		return false;
	}

	public boolean hasValidDDMFormFields(DDMFormInstance ddmFormInstance) {
		return Validator.isNull(getInvalidDDMFormFieldType(ddmFormInstance));
	}

	public boolean hasValidMappedObject(
			DDMFormInstance ddmFormInstance, ObjectDefinition objectDefinition)
		throws PortalException {

		if (!Objects.equals(ddmFormInstance.getStorageType(), "object")) {
			return true;
		}

		if (objectDefinition == null) {
			return false;
		}

		return objectDefinition.isActive();
	}

	public boolean hasValidStorageType(DDMFormInstance ddmFormInstance) {
		try {
			DDMStorageAdapter ddmStorageAdapter =
				_ddmStorageAdapterRegistry.getDDMStorageAdapter(
					ddmFormInstance.getStorageType());

			if (ddmStorageAdapter != null) {
				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	public boolean isDisabledManagementBar() {
		if (hasResults() || isSearch()) {
			return false;
		}

		return true;
	}

	public boolean isDisplayChartAsTable() throws PortalException {
		DDMFormInstance ddmFormInstance = getDDMFormInstance();

		if (ddmFormInstance == null) {
			return false;
		}

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		return ddmFormInstanceSettings.displayChartAsTable();
	}

	public boolean isFormPublished() throws PortalException {
		return isFormPublished(getDDMFormInstance());
	}

	public boolean isFormPublished(DDMFormInstance ddmFormInstance)
		throws PortalException {

		if (ddmFormInstance == null) {
			return false;
		}

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		return ddmFormInstanceSettings.published();
	}

	public boolean isShowPartialResultsToRespondents(
			DDMFormInstance ddmFormInstance)
		throws PortalException {

		if (ddmFormInstance == null) {
			return false;
		}

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		return ddmFormInstanceSettings.showPartialResultsToRespondents();
	}

	public boolean isShowPublishAlert() {
		return ParamUtil.getBoolean(renderRequest, "showPublishAlert");
	}

	protected DDMFormRenderingContext createDDMFormRenderingContext(
		PageContext pageContext, RenderRequest renderRequest) {

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.setHttpServletRequest(
			_portal.getHttpServletRequest(renderRequest));
		ddmFormRenderingContext.setHttpServletResponse(
			PipingServletResponseFactory.createPipingServletResponse(
				pageContext));
		ddmFormRenderingContext.setContainerId("settingsDDMForm");

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ddmFormRenderingContext.setLocale(themeDisplay.getLocale());

		ddmFormRenderingContext.setPortletNamespace(
			renderResponse.getNamespace());

		return ddmFormRenderingContext;
	}

	protected DDMForm createSettingsDDMForm(
			long formInstanceId, ThemeDisplay themeDisplay)
		throws PortalException {

		DDMForm ddmForm = DDMFormFactory.create(DDMFormInstanceSettings.class);

		ddmForm.addAvailableLocale(themeDisplay.getLocale());
		ddmForm.setDefaultLocale(themeDisplay.getLocale());

		if (formInstanceId > 0) {
			Map<String, DDMFormField> ddmFormFieldsMap =
				ddmForm.getDDMFormFieldsMap(false);

			DDMFormField ddmFormField = ddmFormFieldsMap.get(
				"objectDefinitionId");

			ddmFormField.setReadOnly(true);

			ddmFormField = ddmFormFieldsMap.get("storageType");

			ddmFormField.setReadOnly(true);
		}

		return ddmForm;
	}

	protected DDMForm getDDMForm() throws PortalException {
		DDMStructure structure = getDDMStructure();

		DDMForm form = new DDMForm();

		if (structure != null) {
			form = structure.getDDMForm();
		}

		return form;
	}

	protected DDMFormInstanceRecord getDDMFormInstanceRecord() {
		long formInstanceRecordId = ParamUtil.getLong(
			renderRequest, "formInstanceRecordId");

		if (formInstanceRecordId > 0) {
			return _ddmFormInstanceRecordLocalService.fetchFormInstanceRecord(
				formInstanceRecordId);
		}

		HttpServletRequest httpServletRequest =
			ddmFormAdminRequestHelper.getRequest();

		return (DDMFormInstanceRecord)httpServletRequest.getAttribute(
			DDMFormWebKeys.DYNAMIC_DATA_MAPPING_FORM_INSTANCE_RECORD);
	}

	protected DDMFormValues getDDMFormRenderingContextDDMFormValues()
		throws PortalException {

		DDMFormInstance ddmFormInstance = getDDMFormInstance();

		if (ddmFormInstance != null) {
			return ddmFormInstance.getSettingsDDMFormValues();
		}

		String serializedSettingsContext = ParamUtil.getString(
			renderRequest, "serializedSettingsContext");

		if (Validator.isNull(serializedSettingsContext)) {
			return null;
		}

		return _ddmFormContextToDDMFormValues.deserialize(
			DDMFormContextDeserializerRequest.with(
				DDMFormFactory.create(DDMFormInstanceSettings.class),
				serializedSettingsContext));
	}

	protected Locale getDefaultLocale() {
		ThemeDisplay themeDisplay = ddmFormAdminRequestHelper.getThemeDisplay();

		Locale defaultLocale = themeDisplay.getSiteDefaultLocale();

		if (defaultLocale == null) {
			return themeDisplay.getLocale();
		}

		return defaultLocale;
	}

	protected String getDisplayStyle(
		PortletRequest portletRequest,
		DDMFormWebConfiguration ddmFormWebConfiguration,
		String[] displayViews) {

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(portletRequest);

		String displayStyle = ParamUtil.getString(
			portletRequest, "displayStyle");

		if (Validator.isNull(displayStyle)) {
			displayStyle = portalPreferences.getValue(
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN, "display-style",
				"list");
		}
		else if (ArrayUtil.contains(displayViews, displayStyle)) {
			portalPreferences.setValue(
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN, "display-style",
				displayStyle);
		}

		if (!ArrayUtil.contains(displayViews, displayStyle)) {
			displayStyle = displayViews[0];
		}

		return displayStyle;
	}

	protected String getJSONObjectLocalizedPropertyFromRequest(
		String propertyName) {

		String propertyValue = ParamUtil.getString(
			ddmFormAdminRequestHelper.getRequest(), propertyName);

		if (Validator.isNull(propertyValue)) {
			return StringPool.BLANK;
		}

		ThemeDisplay themeDisplay = ddmFormAdminRequestHelper.getThemeDisplay();

		try {
			JSONObject jsonObject = jsonFactory.createJSONObject(propertyValue);

			String languageId = themeDisplay.getLanguageId();

			if (jsonObject.has(languageId)) {
				return jsonObject.getString(languageId);
			}

			return jsonObject.getString(getDefaultLanguageId());
		}
		catch (JSONException jsonException) {
			_log.error(
				String.format(
					"Unable to deserialize JSON localized property \"%s\" " +
						"from request",
					propertyName),
				jsonException);
		}

		return StringPool.BLANK;
	}

	protected String getKeywords() {
		return ParamUtil.getString(renderRequest, "keywords");
	}

	protected UnsafeConsumer<DropdownItem, Exception> getOrderByDropdownItem(
		String orderByCol) {

		return dropdownItem -> {
			dropdownItem.setActive(orderByCol.equals(getOrderByCol()));
			dropdownItem.setHref(getPortletURL(), "orderByCol", orderByCol);
			dropdownItem.setLabel(
				LanguageUtil.get(
					ddmFormAdminRequestHelper.getRequest(), orderByCol));
		};
	}

	protected boolean isSearch() {
		return Validator.isNotNull(getKeywords());
	}

	protected final DDMFormAdminRequestHelper ddmFormAdminRequestHelper;
	protected final DDMFormRenderer ddmFormRenderer;
	protected final JSONFactory jsonFactory;
	protected final RenderRequest renderRequest;
	protected final RenderResponse renderResponse;

	private void _escape(
		String languageId, String propertyName,
		JSONObject serializedFormBuilderContextJSONObject) {

		if (!serializedFormBuilderContextJSONObject.has(propertyName)) {
			return;
		}

		JSONObject jsonObject =
			serializedFormBuilderContextJSONObject.getJSONObject(propertyName);

		jsonObject.put(
			languageId, HtmlUtil.escape(jsonObject.getString(languageId)));
	}

	private List<DDMFormFieldType> _filterDDMFormFieldTypes(
		List<DDMFormFieldType> ddmFormFieldTypes) {

		List<String> outOfScopeDDMFormFieldTypes = Arrays.asList(
			DDMFormFieldTypeConstants.DDM_IMAGE,
			DDMFormFieldTypeConstants.GEOLOCATION,
			JournalArticleDDMFormFieldTypeConstants.JOURNAL_ARTICLE,
			LayoutDDMFormFieldTypeConstants.LINK_TO_LAYOUT);

		return ListUtil.filter(
			ddmFormFieldTypes,
			ddmFormFieldType -> !outOfScopeDDMFormFieldTypes.contains(
				ddmFormFieldType.getName()));
	}

	private UnsafeConsumer<DropdownItem, Exception> _getAddFormDropdownItem() {
		return dropdownItem -> {
			HttpServletRequest httpServletRequest =
				ddmFormAdminRequestHelper.getRequest();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			dropdownItem.setHref(
				renderResponse.createRenderURL(), "mvcRenderCommandName",
				"/admin/edit_form_instance", "redirect",
				PortalUtil.getCurrentURL(httpServletRequest), "groupId",
				String.valueOf(themeDisplay.getScopeGroupId()));

			dropdownItem.setLabel(
				LanguageUtil.get(httpServletRequest, "new-form"));
		};
	}

	private DDMFormBuilderSettingsResponse _getDDMFormBuilderSettingsResponse()
		throws PortalException {

		if (_ddmFormBuilderSettingsResponse != null) {
			return _ddmFormBuilderSettingsResponse;
		}

		ThemeDisplay themeDisplay = ddmFormAdminRequestHelper.getThemeDisplay();

		_ddmFormBuilderSettingsResponse =
			_ddmFormBuilderSettingsRetriever.getSettings(
				DDMFormBuilderSettingsRequest.with(
					themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
					PortalUtil.getClassNameId(DDMFormInstance.class),
					getDDMForm(), themeDisplay.getLocale()));

		return _ddmFormBuilderSettingsResponse;
	}

	private OrderByComparator<DDMFormInstance>
		_getDDMFormInstanceOrderByComparator(
			String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<DDMFormInstance> orderByComparator = null;

		if (orderByCol.equals("modified-date")) {
			orderByComparator = new DDMFormInstanceModifiedDateComparator(
				orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			orderByComparator = DDMFormInstanceNameComparator.getInstance(
				orderByAsc);
		}

		return orderByComparator;
	}

	private Locale[] _getFormAvailableLocales() {
		try {
			DDMStructure structure = getDDMStructure();

			if (structure == null) {
				return null;
			}

			DDMForm form = structure.getDDMForm();

			Set<Locale> availableLocales = form.getAvailableLocales();

			return availableLocales.toArray(new Locale[0]);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private Locale[] _getFormBuilderContextAvailableLocales() {
		String serializedFormBuilderContext = ParamUtil.getString(
			renderRequest, "serializedFormBuilderContext");

		if (Validator.isNull(serializedFormBuilderContext)) {
			return null;
		}

		try {
			JSONObject jsonObject = jsonFactory.createJSONObject(
				serializedFormBuilderContext);

			JSONArray jsonArray = jsonObject.getJSONArray(
				"availableLanguageIds");

			Locale[] locales = new Locale[jsonArray.length()];

			for (int i = 0; i < jsonArray.length(); i++) {
				locales[i] = LocaleUtil.fromLanguageId(jsonArray.getString(i));
			}

			return locales;
		}
		catch (JSONException jsonException) {
			_log.error("Unable to deserialize form context", jsonException);

			return null;
		}
	}

	private String _getFormBuilderContextDefaultLanguageId() {
		String serializedFormBuilderContext = ParamUtil.getString(
			renderRequest, "serializedFormBuilderContext");

		if (Validator.isNull(serializedFormBuilderContext)) {
			return null;
		}

		try {
			JSONObject jsonObject = jsonFactory.createJSONObject(
				serializedFormBuilderContext);

			return jsonObject.getString("defaultLanguageId");
		}
		catch (JSONException jsonException) {
			_log.error("Unable to deserialize form context", jsonException);

			return null;
		}
	}

	private String _getFormDefaultLanguageId() {
		try {
			DDMStructure structure = getDDMStructure();

			if (structure == null) {
				return null;
			}

			DDMForm form = structure.getDDMForm();

			return LocaleUtil.toLanguageId(form.getDefaultLocale());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private void _populateDDMDataProviderNavigationItem(
		NavigationItem navigationItem) {

		navigationItem.setActive(false);
		navigationItem.setHref(
			PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					ddmFormAdminRequestHelper.getLiferayPortletRequest(),
					PortletProviderUtil.getPortletId(
						DDMDataProviderInstance.class.getName(),
						PortletProvider.Action.EDIT),
					PortletRequest.RENDER_PHASE)
			).setMVCPath(
				"/view.jsp"
			).setBackURL(
				ddmFormAdminRequestHelper.getCurrentURL()
			).setParameter(
				"groupId", ddmFormAdminRequestHelper.getScopeGroupId()
			).setParameter(
				"refererPortletName",
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN
			).setParameter(
				"showBackIcon", false
			).buildString());

		ResourceBundle moduleResourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", ddmFormAdminRequestHelper.getLocale(),
			getClass());

		navigationItem.setLabel(
			LanguageUtil.get(moduleResourceBundle, "data-providers"));
	}

	private String _serialize(List<DDMFormFieldType> ddmFormFieldTypes) {
		DDMFormFieldTypesSerializerSerializeRequest.Builder builder =
			DDMFormFieldTypesSerializerSerializeRequest.Builder.newBuilder(
				ddmFormFieldTypes);

		DDMFormFieldTypesSerializerSerializeResponse
			ddmFormFieldTypesSerializerSerializeResponse =
				_ddmFormFieldTypesSerializer.serialize(builder.build());

		return ddmFormFieldTypesSerializerSerializeResponse.getContent();
	}

	private static final String[] _DISPLAY_VIEWS = {"descriptive", "list"};

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormAdminDisplayContext.class);

	private final DDMFormBuilderContextFactory _ddmFormBuilderContextFactory;
	private DDMFormBuilderSettingsResponse _ddmFormBuilderSettingsResponse;
	private final DDMFormBuilderSettingsRetriever
		_ddmFormBuilderSettingsRetriever;
	private final DDMFormContextDeserializer<DDMFormValues>
		_ddmFormContextToDDMFormValues;
	private final DDMFormFieldTypeServicesRegistry
		_ddmFormFieldTypeServicesRegistry;
	private final DDMFormFieldTypesSerializer _ddmFormFieldTypesSerializer;
	private DDMFormInstance _ddmFormInstance;
	private final DDMFormInstanceLocalService _ddmFormInstanceLocalService;
	private final DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;
	private final DDMFormInstanceRecordWriterRegistry
		_ddmFormInstanceRecordWriterRegistry;
	private final DDMFormInstanceService _ddmFormInstanceService;
	private final DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService;
	private final DDMFormTemplateContextFactory _ddmFormTemplateContextFactory;
	private final DDMFormValuesFactory _ddmFormValuesFactory;
	private final DDMFormValuesMerger _ddmFormValuesMerger;
	private final DDMFormWebConfiguration _ddmFormWebConfiguration;
	private final DDMStorageAdapterRegistry _ddmStorageAdapterRegistry;
	private DDMStructure _ddmStructure;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private final DDMStructureService _ddmStructureService;
	private String _displayStyle;
	private final FormInstancePermissionCheckerHelper
		_formInstancePermissionCheckerHelper;
	private final Map<Long, String> _invalidDDMFormFieldTypes = new HashMap<>();
	private final NPMResolver _npmResolver;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private String _orderByCol;
	private String _orderByType;
	private final Portal _portal;

}