/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.lists.form.web.internal.display.context;

import com.liferay.dynamic.data.lists.constants.DDLActionKeys;
import com.liferay.dynamic.data.lists.constants.DDLWebKeys;
import com.liferay.dynamic.data.lists.form.web.configuration.DDLFormWebConfiguration;
import com.liferay.dynamic.data.lists.form.web.internal.constants.DDLFormPortletKeys;
import com.liferay.dynamic.data.lists.form.web.internal.display.context.util.DDLFormAdminRequestHelper;
import com.liferay.dynamic.data.lists.form.web.internal.instance.lifecycle.AddDefaultSharedFormLayoutPortalInstanceLifecycleListener;
import com.liferay.dynamic.data.lists.form.web.internal.search.RecordSetSearch;
import com.liferay.dynamic.data.lists.model.DDLFormRecord;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordSetConstants;
import com.liferay.dynamic.data.lists.model.DDLRecordSetSettings;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalService;
import com.liferay.dynamic.data.lists.service.DDLRecordSetService;
import com.liferay.dynamic.data.lists.service.permission.DDLPermission;
import com.liferay.dynamic.data.lists.service.permission.DDLRecordSetPermission;
import com.liferay.dynamic.data.lists.util.comparator.DDLRecordSetCreateDateComparator;
import com.liferay.dynamic.data.lists.util.comparator.DDLRecordSetModifiedDateComparator;
import com.liferay.dynamic.data.lists.util.comparator.DDLRecordSetNameComparator;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesTracker;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.io.DDMFormFieldTypesJSONSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormJSONSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutJSONSerializer;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.StorageEngine;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowEngineManager;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;

import java.util.List;
import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Bruno Basto
 */
public class DDLFormAdminDisplayContext {

	public DDLFormAdminDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse,
		AddDefaultSharedFormLayoutPortalInstanceLifecycleListener
			addDefaultSharedFormLayoutPortalInstanceLifecycleListener,
		DDLFormWebConfiguration ddlFormWebConfiguration,
		DDLRecordLocalService ddlRecordLocalService,
		DDLRecordSetService ddlRecordSetService,
		DDMDataProviderInstanceLocalService ddmDataProviderInstanceLocalService,
		Servlet ddmFormEvaluatorServlet,
		DDMFormFieldTypeServicesTracker ddmFormFieldTypeServicesTracker,
		DDMFormFieldTypesJSONSerializer ddmFormFieldTypesJSONSerializer,
		DDMFormJSONSerializer ddmFormJSONSerializer,
		DDMFormLayoutJSONSerializer ddmFormLayoutJSONSerializer,
		DDMFormRenderer ddmFormRenderer,
		DDMFormValuesFactory ddmFormValuesFactory,
		DDMFormValuesMerger ddmFormValuesMerger,
		DDMStructureLocalService ddmStructureLocalService,
		JSONFactory jsonFactory, StorageEngine storageEngine,
		WorkflowEngineManager workflowEngineManager) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_addDefaultSharedFormLayoutPortalInstanceLifecycleListener =
			addDefaultSharedFormLayoutPortalInstanceLifecycleListener;
		_ddlFormWebConfiguration = ddlFormWebConfiguration;
		_ddlRecordLocalService = ddlRecordLocalService;
		_ddlRecordSetService = ddlRecordSetService;
		_ddmDataProviderInstanceLocalService =
			ddmDataProviderInstanceLocalService;
		_ddmFormEvaluatorServlet = ddmFormEvaluatorServlet;
		_ddmFormFieldTypeServicesTracker = ddmFormFieldTypeServicesTracker;
		_ddmFormFieldTypesJSONSerializer = ddmFormFieldTypesJSONSerializer;
		_ddmFormJSONSerializer = ddmFormJSONSerializer;
		_ddmFormLayoutJSONSerializer = ddmFormLayoutJSONSerializer;
		_ddmFormRenderer = ddmFormRenderer;
		_ddmFormValuesFactory = ddmFormValuesFactory;
		_ddmFormValuesMerger = ddmFormValuesMerger;
		_ddmStructureLocalService = ddmStructureLocalService;
		_jsonFactory = jsonFactory;
		_storageEngine = storageEngine;
		_workflowEngineManager = workflowEngineManager;

		_ddlFormAdminRequestHelper = new DDLFormAdminRequestHelper(
			renderRequest);
	}

	public String getCSVExport() {
		return _ddlFormWebConfiguration.csvExport();
	}

	public DDLFormViewRecordDisplayContext
		getDDLFormViewRecordDisplayContext() {

		return new DDLFormViewRecordDisplayContext(
			PortalUtil.getHttpServletRequest(_renderRequest),
			PortalUtil.getHttpServletResponse(_renderResponse),
			_ddlRecordLocalService, _ddmFormRenderer, _ddmFormValuesFactory,
			_ddmFormValuesMerger, _ddmStructureLocalService);
	}

	public DDLFormViewRecordsDisplayContext
			getDDLFormViewRecordsDisplayContext()
		throws PortalException {

		return new DDLFormViewRecordsDisplayContext(
			_renderRequest, _renderResponse, getRecordSet(),
			_ddlRecordLocalService, _ddmFormFieldTypeServicesTracker,
			_storageEngine);
	}

	public String getDDMFormEvaluatorServletURL() {
		String servletContextPath = getServletContextPath(
			_ddmFormEvaluatorServlet);

		return servletContextPath.concat(
			"/dynamic-data-mapping-form-evaluator/");
	}

	public JSONArray getDDMFormFieldTypesJSONArray() throws PortalException {
		List<DDMFormFieldType> ddmFormFieldTypes =
			_ddmFormFieldTypeServicesTracker.getDDMFormFieldTypes();

		String serializedDDMFormFieldTypes =
			_ddmFormFieldTypesJSONSerializer.serialize(ddmFormFieldTypes);

		return _jsonFactory.createJSONArray(serializedDDMFormFieldTypes);
	}

	public String getDDMFormHTML(RenderRequest renderRequest)
		throws PortalException {

		DDLFormViewRecordDisplayContext ddlFormViewRecordDisplayContext =
			getDDLFormViewRecordDisplayContext();

		return ddlFormViewRecordDisplayContext.getDDMFormHTML(renderRequest);
	}

	public DDMStructure getDDMStructure() throws PortalException {
		if (_ddmStructure != null) {
			return _ddmStructure;
		}

		DDLRecordSet recordSet = getRecordSet();

		if (recordSet == null) {
			return null;
		}

		_ddmStructure = _ddmStructureLocalService.getStructure(
			recordSet.getDDMStructureId());

		return _ddmStructure;
	}

	public String getDisplayStyle() {
		if (_displayStyle == null) {
			_displayStyle = getDisplayStyle(
				_renderRequest, _ddlFormWebConfiguration, getDisplayViews());
		}

		return _displayStyle;
	}

	public String[] getDisplayViews() {
		return _DISPLAY_VIEWS;
	}

	public String getOrderByCol() {
		String orderByCol = ParamUtil.getString(
			_renderRequest, "orderByCol", "modified-date");

		return orderByCol;
	}

	public String getOrderByType() {
		String orderByType = ParamUtil.getString(
			_renderRequest, "orderByType", "asc");

		return orderByType;
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = _renderResponse.createRenderURL();

		portletURL.setParameter("mvcPath", "/admin/view.jsp");
		portletURL.setParameter(
			"groupId",
			String.valueOf(_ddlFormAdminRequestHelper.getScopeGroupId()));

		return portletURL;
	}

	public String getPreviewFormURL() throws PortalException {
		String publishedFormURL = getPublishedFormURL();

		if (Validator.isNull(publishedFormURL)) {
			return publishedFormURL;
		}

		return publishedFormURL.concat("/preview");
	}

	public String getPublishedFormURL() {
		if (_recordSet == null) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(2);

		ThemeDisplay themeDisplay =
			_ddlFormAdminRequestHelper.getThemeDisplay();

		sb.append(
			_addDefaultSharedFormLayoutPortalInstanceLifecycleListener.
				getFormLayoutURL(themeDisplay, false));

		sb.append(_recordSet.getRecordSetId());

		return sb.toString();
	}

	public DDLRecordSet getRecordSet() throws PortalException {
		if (_recordSet != null) {
			return _recordSet;
		}

		long recordSetId = ParamUtil.getLong(_renderRequest, "recordSetId");

		if (recordSetId > 0) {
			_recordSet = _ddlRecordSetService.fetchRecordSet(recordSetId);
		}
		else {
			DDLRecord ddlRecord = getRecord();

			if (ddlRecord != null) {
				_recordSet = ddlRecord.getRecordSet();
			}
		}

		return _recordSet;
	}

	public RecordSetSearch getRecordSetSearch() throws PortalException {
		String displayStyle = getDisplayStyle();

		PortletURL portletURL = getPortletURL();

		portletURL.setParameter("displayStyle", displayStyle);

		RecordSetSearch recordSetSearch = new RecordSetSearch(
			_renderRequest, portletURL);

		String orderByCol = getOrderByCol();
		String orderByType = getOrderByType();

		OrderByComparator<DDLRecordSet> orderByComparator =
			getDDLRecordSetOrderByComparator(orderByCol, orderByType);

		recordSetSearch.setOrderByCol(orderByCol);
		recordSetSearch.setOrderByComparator(orderByComparator);
		recordSetSearch.setOrderByType(orderByType);

		if (recordSetSearch.isSearch()) {
			recordSetSearch.setEmptyResultsMessage("no-forms-were-found");
		}
		else {
			recordSetSearch.setEmptyResultsMessage("there-are-no-forms");
		}

		setRecordSetSearchResults(recordSetSearch);
		setRecordSetSearchTotal(recordSetSearch);

		return recordSetSearch;
	}

	public DDLRecordVersion getRecordVersion() throws PortalException {
		DDLRecord record = getRecord();

		return record.getLatestRecordVersion();
	}

	public String getSerializedDDMDataProviders() throws PortalException {
		ThemeDisplay themeDisplay =
			_ddlFormAdminRequestHelper.getThemeDisplay();

		List<DDMDataProviderInstance> ddmDataProviderInstances =
			_ddmDataProviderInstanceLocalService.getDataProviderInstances(
				PortalUtil.getCurrentAndAncestorSiteGroupIds(
					themeDisplay.getScopeGroupId()));

		return serialize(ddmDataProviderInstances, themeDisplay.getLocale());
	}

	public String getSerializedDDMForm() throws PortalException {
		String definition = ParamUtil.getString(_renderRequest, "definition");

		if (Validator.isNotNull(definition)) {
			return definition;
		}

		DDMStructure ddmStructure = getDDMStructure();

		DDMForm ddmForm = new DDMForm();

		if (ddmStructure != null) {
			ddmForm = ddmStructure.getDDMForm();
		}

		return _ddmFormJSONSerializer.serialize(ddmForm);
	}

	public String getSerializedDDMFormLayout() throws PortalException {
		String layout = ParamUtil.getString(_renderRequest, "layout");

		if (Validator.isNotNull(layout)) {
			return layout;
		}

		DDMStructure ddmStructure = getDDMStructure();

		DDMFormLayout ddmFormLayout = new DDMFormLayout();

		if (ddmStructure != null) {
			ddmFormLayout = ddmStructure.getDDMFormLayout();
		}

		return _ddmFormLayoutJSONSerializer.serialize(ddmFormLayout);
	}

	public boolean isDDLRecordWorkflowHandlerDeployed() {
		if (!_workflowEngineManager.isDeployed()) {
			return false;
		}

		WorkflowHandler<DDLRecord> ddlRecordWorkflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				DDLRecord.class.getName());

		if (ddlRecordWorkflowHandler != null) {
			return true;
		}

		return false;
	}

	public boolean isFormPublished() throws PortalException {
		DDLRecordSet recordSet = getRecordSet();

		if (recordSet == null) {
			return false;
		}

		DDLRecordSetSettings recordSetSettings = recordSet.getSettingsModel();

		return recordSetSettings.published();
	}

	public boolean isShowAddRecordSetButton() {
		return DDLPermission.contains(
			_ddlFormAdminRequestHelper.getPermissionChecker(),
			_ddlFormAdminRequestHelper.getScopeGroupId(),
			DDLActionKeys.ADD_RECORD_SET);
	}

	public boolean isShowDeleteRecordSetIcon(DDLRecordSet recordSet) {
		return DDLRecordSetPermission.contains(
			_ddlFormAdminRequestHelper.getPermissionChecker(), recordSet,
			ActionKeys.DELETE);
	}

	public boolean isShowEditRecordSetIcon(DDLRecordSet recordSet) {
		return DDLRecordSetPermission.contains(
			_ddlFormAdminRequestHelper.getPermissionChecker(), recordSet,
			ActionKeys.UPDATE);
	}

	public boolean isShowExportRecordSetIcon(DDLRecordSet recordSet) {
		return DDLRecordSetPermission.contains(
			_ddlFormAdminRequestHelper.getPermissionChecker(), recordSet,
			ActionKeys.VIEW);
	}

	public boolean isShowPermissionsIcon(DDLRecordSet recordSet) {
		return DDLRecordSetPermission.contains(
			_ddlFormAdminRequestHelper.getPermissionChecker(), recordSet,
			ActionKeys.PERMISSIONS);
	}

	public boolean isShowSearch() throws PortalException {
		if (hasResults()) {
			return true;
		}

		if (isSearch()) {
			return true;
		}

		return false;
	}

	public boolean isShowViewEntriesRecordSetIcon(DDLRecordSet recordSet) {
		return DDLRecordSetPermission.contains(
			_ddlFormAdminRequestHelper.getPermissionChecker(), recordSet,
			ActionKeys.VIEW);
	}

	protected OrderByComparator<DDLRecordSet> getDDLRecordSetOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<DDLRecordSet> orderByComparator = null;

		if (orderByCol.equals("create-date")) {
			orderByComparator = new DDLRecordSetCreateDateComparator(
				orderByAsc);
		}
		else if (orderByCol.equals("modified-date")) {
			orderByComparator = new DDLRecordSetModifiedDateComparator(
				orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			orderByComparator = new DDLRecordSetNameComparator(orderByAsc);
		}

		return orderByComparator;
	}

	protected String getDisplayStyle(
		PortletRequest portletRequest,
		DDLFormWebConfiguration ddlFormWebConfiguration,
		String[] displayViews) {

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(portletRequest);

		String displayStyle = ParamUtil.getString(
			portletRequest, "displayStyle");

		if (Validator.isNull(displayStyle)) {
			displayStyle = portalPreferences.getValue(
				DDLFormPortletKeys.DYNAMIC_DATA_LISTS_FORM_ADMIN,
				"display-style", ddlFormWebConfiguration.defaultDisplayView());
		}
		else if (ArrayUtil.contains(displayViews, displayStyle)) {
			portalPreferences.setValue(
				DDLFormPortletKeys.DYNAMIC_DATA_LISTS_FORM_ADMIN,
				"display-style", displayStyle);
		}

		if (!ArrayUtil.contains(displayViews, displayStyle)) {
			displayStyle = displayViews[0];
		}

		return displayStyle;
	}

	protected String getKeywords() {
		return ParamUtil.getString(_renderRequest, "keywords");
	}

	protected DDLRecord getRecord() throws PortalException {
		long recordId = ParamUtil.getLong(_renderRequest, "recordId");

		if (recordId > 0) {
			return _ddlRecordLocalService.fetchRecord(recordId);
		}

		HttpServletRequest httpServletRequest =
			_ddlFormAdminRequestHelper.getRequest();

		Object record = httpServletRequest.getAttribute(
			DDLWebKeys.DYNAMIC_DATA_LISTS_RECORD);

		if (record instanceof DDLFormRecord) {
			DDLFormRecord formRecord = (DDLFormRecord)record;

			return formRecord.getDDLRecord();
		}
		else {
			return (DDLRecord)record;
		}
	}

	protected String getServletContextPath(Servlet servlet) {
		String proxyPath = PortalUtil.getPathProxy();

		ServletConfig servletConfig = servlet.getServletConfig();

		ServletContext servletContext = servletConfig.getServletContext();

		return proxyPath.concat(servletContext.getContextPath());
	}

	protected int getTotal() throws PortalException {
		RecordSetSearch recordSetSearch = getRecordSetSearch();

		return recordSetSearch.getTotal();
	}

	protected boolean hasResults() throws PortalException {
		if (getTotal() > 0) {
			return true;
		}

		return false;
	}

	protected boolean isSearch() {
		if (Validator.isNotNull(getKeywords())) {
			return true;
		}

		return false;
	}

	protected String serialize(
		List<DDMDataProviderInstance> ddmDataProviderInstances, Locale locale) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (DDMDataProviderInstance ddmDataProviderInstance :
				ddmDataProviderInstances) {

			JSONObject jsonObject = toJSONObject(
				ddmDataProviderInstance, locale);

			jsonArray.put(jsonObject);
		}

		return jsonArray.toString();
	}

	protected void setRecordSetSearchResults(RecordSetSearch recordSetSearch) {
		List<DDLRecordSet> results = _ddlRecordSetService.search(
			_ddlFormAdminRequestHelper.getCompanyId(),
			_ddlFormAdminRequestHelper.getScopeGroupId(), getKeywords(),
			DDLRecordSetConstants.SCOPE_FORMS, recordSetSearch.getStart(),
			recordSetSearch.getEnd(), recordSetSearch.getOrderByComparator());

		recordSetSearch.setResults(results);
	}

	protected void setRecordSetSearchTotal(RecordSetSearch recordSetSearch) {
		int total = _ddlRecordSetService.searchCount(
			_ddlFormAdminRequestHelper.getCompanyId(),
			_ddlFormAdminRequestHelper.getScopeGroupId(), getKeywords(),
			DDLRecordSetConstants.SCOPE_FORMS);

		recordSetSearch.setTotal(total);
	}

	protected JSONObject toJSONObject(
		DDMDataProviderInstance ddmDataProviderInstance, Locale locale) {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		jsonObject.put(
			"id", ddmDataProviderInstance.getDataProviderInstanceId());
		jsonObject.put("name", ddmDataProviderInstance.getName(locale));

		return jsonObject;
	}

	private static final String[] _DISPLAY_VIEWS = {"descriptive", "list"};

	private final AddDefaultSharedFormLayoutPortalInstanceLifecycleListener
		_addDefaultSharedFormLayoutPortalInstanceLifecycleListener;
	private final DDLFormAdminRequestHelper _ddlFormAdminRequestHelper;
	private final DDLFormWebConfiguration _ddlFormWebConfiguration;
	private final DDLRecordLocalService _ddlRecordLocalService;
	private final DDLRecordSetService _ddlRecordSetService;
	private final DDMDataProviderInstanceLocalService
		_ddmDataProviderInstanceLocalService;
	private final Servlet _ddmFormEvaluatorServlet;
	private final DDMFormFieldTypeServicesTracker
		_ddmFormFieldTypeServicesTracker;
	private final DDMFormFieldTypesJSONSerializer
		_ddmFormFieldTypesJSONSerializer;
	private final DDMFormJSONSerializer _ddmFormJSONSerializer;
	private final DDMFormLayoutJSONSerializer _ddmFormLayoutJSONSerializer;
	private final DDMFormRenderer _ddmFormRenderer;
	private final DDMFormValuesFactory _ddmFormValuesFactory;
	private final DDMFormValuesMerger _ddmFormValuesMerger;
	private DDMStructure _ddmStructure;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private String _displayStyle;
	private final JSONFactory _jsonFactory;
	private DDLRecordSet _recordSet;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final StorageEngine _storageEngine;
	private final WorkflowEngineManager _workflowEngineManager;

}