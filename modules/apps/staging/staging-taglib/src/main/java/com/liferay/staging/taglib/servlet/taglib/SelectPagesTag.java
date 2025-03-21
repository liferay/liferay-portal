/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.servlet.taglib;

import com.liferay.petra.string.StringPool;
import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Máté Thurzó
 */
public class SelectPagesTag extends IncludeTag {

	public String getAction() {
		return _action;
	}

	public long getExportImportConfigurationId() {
		return _exportImportConfigurationId;
	}

	public long getGroupId() {
		return _groupId;
	}

	public String getTreeId() {
		return _treeId;
	}

	public boolean isDisableInputs() {
		return _disableInputs;
	}

	public boolean isPrivateLayout() {
		return _privateLayout;
	}

	public void setAction(String action) {
		if (action == null) {
			_action = StringPool.BLANK;
		}
		else {
			_action = action;
		}
	}

	public void setDisableInputs(boolean disableInputs) {
		_disableInputs = disableInputs;
	}

	public void setExportImportConfigurationId(
		long exportImportConfigurationId) {

		_exportImportConfigurationId = exportImportConfigurationId;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public void setLayoutSetBranchId(long layoutSetBranchId) {
		_layoutSetBranchId = layoutSetBranchId;
	}

	public void setLayoutSetSettings(boolean layoutSetSettings) {
		_layoutSetSettings = layoutSetSettings;
	}

	public void setLogo(boolean logo) {
		_logo = logo;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPrivateLayout(boolean privateLayout) {
		_privateLayout = privateLayout;
	}

	public void setSelectedLayoutIds(String selectedLayoutIds) {
		_selectedLayoutIds = selectedLayoutIds;
	}

	public void setShowDeleteMissingLayouts(boolean showDeleteMissingLayouts) {
		_showDeleteMissingLayouts = showDeleteMissingLayouts;
	}

	public void setThemeReference(boolean themeReference) {
		_themeReference = themeReference;
	}

	public void setTreeId(String treeId) {
		_treeId = treeId;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_action = StringPool.BLANK;
		_disableInputs = false;
		_exportImportConfigurationId = 0;
		_groupId = 0;
		_layoutSetBranchId = 0;
		_layoutSetSettings = true;
		_logo = true;
		_privateLayout = false;
		_selectedLayoutIds = StringPool.BLANK;
		_showDeleteMissingLayouts = false;
		_themeReference = true;
		_treeId = StringPool.BLANK;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-staging:select-pages:action", _action);
		httpServletRequest.setAttribute(
			"liferay-staging:select-pages:disableInputs", _disableInputs);
		httpServletRequest.setAttribute(
			"liferay-staging:select-pages:exportImportConfigurationId",
			_exportImportConfigurationId);
		httpServletRequest.setAttribute(
			"liferay-staging:select-pages:groupId", _groupId);
		httpServletRequest.setAttribute(
			"liferay-staging:select-pages:layoutSetBranchId",
			_layoutSetBranchId);
		httpServletRequest.setAttribute(
			"liferay-staging:select-pages:layoutSetSettings",
			_layoutSetSettings);
		httpServletRequest.setAttribute(
			"liferay-staging:select-pages:logo", _logo);
		httpServletRequest.setAttribute(
			"liferay-staging:select-pages:privateLayout", _privateLayout);
		httpServletRequest.setAttribute(
			"liferay-staging:select-pages:selectedLayoutIds",
			_selectedLayoutIds);
		httpServletRequest.setAttribute(
			"liferay-staging:select-pages:showDeleteMissingLayouts",
			_showDeleteMissingLayouts);
		httpServletRequest.setAttribute(
			"liferay-staging:select-pages:themeReference", _themeReference);
		httpServletRequest.setAttribute(
			"liferay-staging:select-pages:treeId", _treeId);
	}

	private static final String _PAGE = "/select_pages/page.jsp";

	private String _action = StringPool.BLANK;
	private boolean _disableInputs;
	private long _exportImportConfigurationId;
	private long _groupId;
	private long _layoutSetBranchId;
	private boolean _layoutSetSettings = true;
	private boolean _logo = true;
	private boolean _privateLayout;
	private String _selectedLayoutIds = StringPool.BLANK;
	private boolean _showDeleteMissingLayouts;
	private boolean _themeReference = true;
	private String _treeId = StringPool.BLANK;

}