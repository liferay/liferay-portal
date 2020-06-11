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

package com.liferay.dynamic.data.lists.web.internal.display.context;

import com.liferay.dynamic.data.lists.constants.DDLActionKeys;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.constants.DDLWebKeys;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalService;
import com.liferay.dynamic.data.lists.service.permission.DDLPermission;
import com.liferay.dynamic.data.lists.service.permission.DDLRecordSetPermission;
import com.liferay.dynamic.data.lists.util.DDL;
import com.liferay.dynamic.data.lists.util.comparator.DDLRecordSetCreateDateComparator;
import com.liferay.dynamic.data.lists.util.comparator.DDLRecordSetModifiedDateComparator;
import com.liferay.dynamic.data.lists.util.comparator.DDLRecordSetNameComparator;
import com.liferay.dynamic.data.lists.web.configuration.DDLWebConfiguration;
import com.liferay.dynamic.data.lists.web.internal.display.context.util.DDLRequestHelper;
import com.liferay.dynamic.data.lists.web.internal.search.RecordSetSearch;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.permission.DDMTemplatePermission;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageEngine;
import com.liferay.dynamic.data.mapping.util.DDMDisplay;
import com.liferay.dynamic.data.mapping.util.DDMDisplayRegistry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;
import java.util.Locale;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Marcellus Tavares
 */
public class DDLDisplayContext {

	public DDLDisplayContext(
		HttpServletRequest request, DDL ddl,
		DDLRecordSetLocalService ddlRecordSetLocalService,
		DDLWebConfiguration ddlWebConfiguration,
		DDMDisplayRegistry ddmDisplayRegistry,
		DDMTemplateLocalService ddmTemplateLocalService,
		StorageEngine storageEngine) {

		_ddl = ddl;
		_ddlRecordSetLocalService = ddlRecordSetLocalService;
		_ddlWebConfiguration = ddlWebConfiguration;
		_ddmDisplayRegistry = ddmDisplayRegistry;
		_ddmTemplateLocalService = ddmTemplateLocalService;
		_storageEngine = storageEngine;

		_ddlRequestHelper = new DDLRequestHelper(request);

		if (Validator.isNotNull(getPortletResource())) {
			return;
		}

		DDLRecordSet recordSet = getRecordSet();

		if ((recordSet == null) || !hasViewPermission()) {
			RenderRequest renderRequest = _ddlRequestHelper.getRenderRequest();

			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}
	}

	public boolean changeableDefaultLanguage() {
		return _ddlWebConfiguration.changeableDefaultLanguage();
	}

	public String getAddDDMTemplateTitle() throws PortalException {
		DDMDisplay ddmDisplay = getDDMDisplay();

		return ddmDisplay.getEditTemplateTitle(
			_recordSet.getDDMStructure(), null, getLocale());
	}

	public String getAddRecordLabel() throws PortalException {
		DDLRecordSet recordSet = getRecordSet();

		String structureName = StringPool.BLANK;

		if (recordSet != null) {
			DDMStructure ddmStructure = recordSet.getDDMStructure();

			structureName = ddmStructure.getName(_ddlRequestHelper.getLocale());
		}

		return LanguageUtil.format(
			_ddlRequestHelper.getRequest(), "add-x",
			HtmlUtil.escape(structureName), false);
	}

	public String getCSVExport() {
		return _ddlWebConfiguration.csvExport();
	}

	public String getDDLRecordSetDisplayStyle() {
		if (_ddlRecordDisplayStyle == null) {
			PortalPreferences portalPreferences =
				PortletPreferencesFactoryUtil.getPortalPreferences(
					_ddlRequestHelper.getRenderRequest());

			_ddlRecordDisplayStyle = ParamUtil.getString(
				_ddlRequestHelper.getRenderRequest(), "displayStyle");

			if (Validator.isNull(_ddlRecordDisplayStyle)) {
				_ddlRecordDisplayStyle = portalPreferences.getValue(
					DDLPortletKeys.DYNAMIC_DATA_LISTS, "display-style",
					_ddlWebConfiguration.defaultDisplayView());
			}
			else if (ArrayUtil.contains(
						getDDLRecordSetDisplayViews(),
						_ddlRecordDisplayStyle)) {

				portalPreferences.setValue(
					DDLPortletKeys.DYNAMIC_DATA_LISTS, "display-style",
					_ddlRecordDisplayStyle);
			}

			if (!ArrayUtil.contains(
					getDDLRecordSetDisplayViews(), _ddlRecordDisplayStyle)) {

				_ddlRecordDisplayStyle = getDDLRecordSetDisplayViews()[0];
			}
		}

		return _ddlRecordDisplayStyle;
	}

	public String[] getDDLRecordSetDisplayViews() {
		return _DISPLAY_VIEWS;
	}

	public OrderByComparator<DDLRecordSet> getDDLRecordSetOrderByComparator(
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

	public DDMFormValues getDDMFormValues(long classPK) throws PortalException {
		return _storageEngine.getDDMFormValues(classPK);
	}

	public long getDisplayDDMTemplateId() {
		return PrefsParamUtil.getLong(
			_ddlRequestHelper.getPortletPreferences(),
			_ddlRequestHelper.getRenderRequest(), "displayDDMTemplateId");
	}

	public String getEditDisplayDDMTemplateTitle() throws PortalException {
		DDLRecordSet recordSet = getRecordSet();

		if (recordSet == null) {
			return StringPool.BLANK;
		}

		DDMDisplay ddmDisplay = getDDMDisplay();

		return ddmDisplay.getEditTemplateTitle(
			recordSet.getDDMStructure(), fetchDisplayDDMTemplate(),
			getLocale());
	}

	public String getEditFormDDMTemplateTitle() throws PortalException {
		DDLRecordSet recordSet = getRecordSet();

		if (recordSet == null) {
			return LanguageUtil.get(getLocale(), "add-list");
		}

		DDMDisplay ddmDisplay = getDDMDisplay();

		return ddmDisplay.getEditTemplateTitle(
			recordSet.getDDMStructure(), fetchFormDDMTemplate(), getLocale());
	}

	public long getFormDDMTemplateId() {
		return PrefsParamUtil.getLong(
			_ddlRequestHelper.getPortletPreferences(),
			_ddlRequestHelper.getRenderRequest(), "formDDMTemplateId");
	}

	public String getOrderByCol() {
		String orderByCol = ParamUtil.getString(
			_ddlRequestHelper.getRenderRequest(), "orderByCol",
			"modified-date");

		return orderByCol;
	}

	public String getOrderByType() {
		String orderByType = ParamUtil.getString(
			_ddlRequestHelper.getRenderRequest(), "orderByType", "asc");

		return orderByType;
	}

	public DDLRecordSet getRecordSet() {
		if (_recordSet != null) {
			return _recordSet;
		}

		RenderRequest renderRequest = _ddlRequestHelper.getRenderRequest();

		_recordSet = (DDLRecordSet)renderRequest.getAttribute(
			DDLWebKeys.DYNAMIC_DATA_LISTS_RECORD_SET);

		if (_recordSet != null) {
			return _recordSet;
		}

		_recordSet = _ddlRecordSetLocalService.fetchDDLRecordSet(
			getRecordSetId());

		return _recordSet;
	}

	public long getRecordSetId() {
		return PrefsParamUtil.getLong(
			_ddlRequestHelper.getPortletPreferences(),
			_ddlRequestHelper.getRenderRequest(), "recordSetId");
	}

	public JSONArray getRecordSetJSONArray(
			DDLRecordSet recordSet, Locale locale)
		throws Exception {

		return _ddl.getRecordSetJSONArray(recordSet, locale);
	}

	public RecordSetSearch getRecordSetSearch(PortletURL portletURL) {
		RecordSetSearch recordSetSearch = new RecordSetSearch(
			_ddlRequestHelper.getRenderRequest(), portletURL);

		String orderByCol = getOrderByCol();
		String orderByType = getOrderByType();

		OrderByComparator<DDLRecordSet> orderByComparator =
			getDDLRecordSetOrderByComparator(orderByCol, orderByType);

		recordSetSearch.setOrderByCol(orderByCol);
		recordSetSearch.setOrderByComparator(orderByComparator);
		recordSetSearch.setOrderByType(orderByType);

		return recordSetSearch;
	}

	public JSONArray getRecordsJSONArray(
			List<DDLRecord> records, boolean latestRecordVersion, Locale locale)
		throws Exception {

		return _ddl.getRecordsJSONArray(records, latestRecordVersion, locale);
	}

	public boolean isAdminPortlet() {
		String portletName = getPortletName();

		return portletName.equals(DDLPortletKeys.DYNAMIC_DATA_LISTS);
	}

	public boolean isDisplayPortlet() {
		return !isAdminPortlet();
	}

	public boolean isEditable() {
		if (isAdminPortlet()) {
			return true;
		}

		return PrefsParamUtil.getBoolean(
			_ddlRequestHelper.getPortletPreferences(),
			_ddlRequestHelper.getRenderRequest(), "editable", true);
	}

	public boolean isFormView() {
		return PrefsParamUtil.getBoolean(
			_ddlRequestHelper.getPortletPreferences(),
			_ddlRequestHelper.getRenderRequest(), "formView");
	}

	public boolean isShowAddDDMDisplayTemplateIcon() throws PortalException {
		if (isShowAddDDMTemplateIcon() && !isFormView()) {
			return true;
		}

		return false;
	}

	public boolean isShowAddDDMFormTemplateIcon() throws PortalException {
		return isShowAddDDMTemplateIcon();
	}

	public boolean isShowAddRecordButton() {
		if (isFormView() || isSpreadsheet()) {
			return false;
		}

		if (isEditable() && hasAddRecordPermission()) {
			return true;
		}

		return false;
	}

	public boolean isShowAddRecordSetIcon() {
		if (_hasAddRecordSetPermission != null) {
			return _hasAddRecordSetPermission;
		}

		_hasAddRecordSetPermission = DDLPermission.contains(
			getPermissionChecker(), getScopeGroupId(), getPortletId(),
			DDLActionKeys.ADD_RECORD_SET);

		return _hasAddRecordSetPermission;
	}

	public boolean isShowCancelButton() {
		if (isFormView()) {
			return false;
		}

		return true;
	}

	public boolean isShowConfigurationIcon() throws PortalException {
		if (_showConfigurationIcon != null) {
			return _showConfigurationIcon;
		}

		_showConfigurationIcon = PortletPermissionUtil.contains(
			getPermissionChecker(), getLayout(), getPortletId(),
			ActionKeys.CONFIGURATION);

		return _showConfigurationIcon;
	}

	public boolean isShowEditDisplayDDMTemplateIcon() throws PortalException {
		if (_hasEditDisplayDDMTemplatePermission != null) {
			return _hasEditDisplayDDMTemplatePermission;
		}

		_hasEditDisplayDDMTemplatePermission = Boolean.FALSE;

		DDMTemplate displayDDMTemplate = fetchDisplayDDMTemplate();

		if (displayDDMTemplate == null) {
			return _hasEditDisplayDDMTemplatePermission;
		}

		_hasEditDisplayDDMTemplatePermission = DDMTemplatePermission.contains(
			getPermissionChecker(), getScopeGroupId(),
			getDisplayDDMTemplateId(), DDLPortletKeys.DYNAMIC_DATA_LISTS,
			ActionKeys.UPDATE);

		return _hasEditDisplayDDMTemplatePermission;
	}

	public boolean isShowEditFormDDMTemplateIcon() throws PortalException {
		if (_hasEditFormDDMTemplatePermission != null) {
			return _hasEditFormDDMTemplatePermission;
		}

		_hasEditFormDDMTemplatePermission = Boolean.FALSE;

		if (getFormDDMTemplateId() == 0) {
			return _hasEditFormDDMTemplatePermission;
		}

		_hasEditFormDDMTemplatePermission = DDMTemplatePermission.contains(
			getPermissionChecker(), getScopeGroupId(), getFormDDMTemplateId(),
			DDLPortletKeys.DYNAMIC_DATA_LISTS, ActionKeys.UPDATE);

		return _hasEditFormDDMTemplatePermission;
	}

	public boolean isShowEditRecordSetIcon() {
		DDLRecordSet recordSet = getRecordSet();

		if (recordSet == null) {
			return false;
		}

		return DDLRecordSetPermission.contains(
			getPermissionChecker(), recordSet, ActionKeys.UPDATE);
	}

	public boolean isShowIconsActions() throws PortalException {
		if (isSpreadsheet()) {
			return false;
		}

		if (_hasShowIconsActionPermission != null) {
			return _hasShowIconsActionPermission;
		}

		_hasShowIconsActionPermission = Boolean.FALSE;

		ThemeDisplay themeDisplay = getThemeDisplay();

		if (!themeDisplay.isSignedIn()) {
			return _hasShowIconsActionPermission;
		}

		Layout layout = themeDisplay.getLayout();

		if (layout.isLayoutPrototypeLinkActive()) {
			return _hasShowIconsActionPermission;
		}

		if (isShowConfigurationIcon() || isShowAddDDMDisplayTemplateIcon() ||
			isShowAddDDMFormTemplateIcon() ||
			isShowEditDisplayDDMTemplateIcon() ||
			isShowEditFormDDMTemplateIcon()) {

			_hasShowIconsActionPermission = Boolean.TRUE;
		}

		return _hasShowIconsActionPermission;
	}

	public boolean isShowPublishRecordButton() {
		if (isEditable() && hasAddRecordPermission()) {
			return true;
		}

		return false;
	}

	public boolean isShowSaveRecordButton() {
		if (isFormView()) {
			return false;
		}

		if (isEditable() && hasAddRecordPermission()) {
			return true;
		}

		return false;
	}

	public boolean isSpreadsheet() {
		return PrefsParamUtil.getBoolean(
			_ddlRequestHelper.getPortletPreferences(),
			_ddlRequestHelper.getRenderRequest(), "spreadsheet");
	}

	protected DDMTemplate fetchDisplayDDMTemplate() {
		if (_displayDDMTemplate != null) {
			return _displayDDMTemplate;
		}

		_displayDDMTemplate = _ddmTemplateLocalService.fetchDDMTemplate(
			getDisplayDDMTemplateId());

		return _displayDDMTemplate;
	}

	protected DDMTemplate fetchFormDDMTemplate() {
		if (_formDDMTemplate != null) {
			return _formDDMTemplate;
		}

		_formDDMTemplate = _ddmTemplateLocalService.fetchDDMTemplate(
			getFormDDMTemplateId());

		return _formDDMTemplate;
	}

	protected DDMDisplay getDDMDisplay() {
		return _ddmDisplayRegistry.getDDMDisplay(
			DDLPortletKeys.DYNAMIC_DATA_LISTS);
	}

	protected Layout getLayout() {
		return _ddlRequestHelper.getLayout();
	}

	protected Locale getLocale() {
		return _ddlRequestHelper.getLocale();
	}

	protected PermissionChecker getPermissionChecker() {
		return _ddlRequestHelper.getPermissionChecker();
	}

	protected String getPortletId() {
		return _ddlRequestHelper.getPortletId();
	}

	protected String getPortletName() {
		return _ddlRequestHelper.getPortletName();
	}

	protected String getPortletResource() {
		return _ddlRequestHelper.getPortletResource();
	}

	protected long getScopeGroupId() {
		return _ddlRequestHelper.getScopeGroupId();
	}

	protected long getStructureTypeClassNameId() {
		DDMDisplay ddmDisplay = getDDMDisplay();

		return PortalUtil.getClassNameId(ddmDisplay.getStructureType());
	}

	protected ThemeDisplay getThemeDisplay() {
		return _ddlRequestHelper.getThemeDisplay();
	}

	protected boolean hasAddRecordPermission() {
		if (_hasAddRecordPermission != null) {
			return _hasAddRecordPermission;
		}

		_hasAddRecordPermission = false;

		if (_recordSet != null) {
			_hasAddRecordPermission = DDLRecordSetPermission.contains(
				getPermissionChecker(), _recordSet, DDLActionKeys.ADD_RECORD);
		}

		return _hasAddRecordPermission;
	}

	protected boolean hasViewPermission() {
		if (_hasViewPermission != null) {
			return _hasViewPermission;
		}

		_hasViewPermission = true;

		if (_recordSet != null) {
			_hasViewPermission = DDLRecordSetPermission.contains(
				getPermissionChecker(), _recordSet, ActionKeys.VIEW);
		}

		return _hasViewPermission;
	}

	protected boolean isShowAddDDMTemplateIcon() throws PortalException {
		if (_hasAddDDMTemplatePermission != null) {
			return _hasAddDDMTemplatePermission;
		}

		_hasAddDDMTemplatePermission = Boolean.FALSE;

		DDLRecordSet recordSet = getRecordSet();

		if (recordSet == null) {
			return _hasAddDDMTemplatePermission;
		}

		_hasAddDDMTemplatePermission =
			DDMTemplatePermission.containsAddTemplatePermission(
				getPermissionChecker(), getScopeGroupId(),
				getStructureTypeClassNameId(), getStructureTypeClassNameId());

		return _hasAddDDMTemplatePermission;
	}

	private static final String[] _DISPLAY_VIEWS = {"descriptive", "list"};

	private final DDL _ddl;
	private String _ddlRecordDisplayStyle;
	private final DDLRecordSetLocalService _ddlRecordSetLocalService;
	private final DDLRequestHelper _ddlRequestHelper;
	private final DDLWebConfiguration _ddlWebConfiguration;
	private final DDMDisplayRegistry _ddmDisplayRegistry;
	private final DDMTemplateLocalService _ddmTemplateLocalService;
	private DDMTemplate _displayDDMTemplate;
	private DDMTemplate _formDDMTemplate;
	private Boolean _hasAddDDMTemplatePermission;
	private Boolean _hasAddRecordPermission;
	private Boolean _hasAddRecordSetPermission;
	private Boolean _hasEditDisplayDDMTemplatePermission;
	private Boolean _hasEditFormDDMTemplatePermission;
	private Boolean _hasShowIconsActionPermission;
	private Boolean _hasViewPermission;
	private DDLRecordSet _recordSet;
	private Boolean _showConfigurationIcon;
	private final StorageEngine _storageEngine;

}