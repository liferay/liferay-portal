/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.display.context;

import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.util.comparator.ExportImportConfigurationNameComparator;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Mariano Álvaro Sáiz
 */
public class ExportImportPublishTemplatesDisplayContext {

	public ExportImportPublishTemplatesDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletResponse liferayPortletResponse,
			RenderRequest renderRequest)
		throws PortalException {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_renderRequest = renderRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public long getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = ParamUtil.getLong(_httpServletRequest, "groupId");

		return _groupId;
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	public PortletURL getPortletURL() {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/export_import/publish_layouts"
		).setCMD(
			Constants.PUBLISH
		).setParameter(
			"groupId", getGroupId()
		).setParameter(
			"layoutSetBranchId",
			ParamUtil.getLong(_httpServletRequest, "layoutSetBranchId")
		).setParameter(
			"layoutSetBranchName",
			ParamUtil.getString(_httpServletRequest, "layoutSetBranchName")
		).setParameter(
			"privateLayout",
			ParamUtil.getBoolean(_httpServletRequest, "privateLayout")
		).setParameter(
			"publishConfigurationButtons", "saved"
		).buildPortletURL();

		return _portletURL;
	}

	public SearchContainer<ExportImportConfiguration> getSearchContainer()
		throws Exception {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			_renderRequest, getPortletURL(), null,
			"there-are-no-saved-publish-templates");

		_searchContainer.setOrderByCol("name");
		_searchContainer.setOrderByComparator(
			ExportImportConfigurationNameComparator.getInstance(true));
		_searchContainer.setOrderByType("asc");

		int exportImportConfigurationType = isLocalPublishing() ?
			ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_LOCAL :
				ExportImportConfigurationConstants.TYPE_PUBLISH_LAYOUT_REMOTE;

		_searchContainer.setResultsAndTotal(
			() ->
				ExportImportConfigurationLocalServiceUtil.
					getExportImportConfigurations(
						_themeDisplay.getCompanyId(), getGroupId(),
						getKeywords(), exportImportConfigurationType,
						_searchContainer.getStart(), _searchContainer.getEnd(),
						_searchContainer.getOrderByComparator()),
			ExportImportConfigurationLocalServiceUtil.
				getExportImportConfigurationsCount(
					_themeDisplay.getCompanyId(), getGroupId(), getKeywords(),
					exportImportConfigurationType));

		return _searchContainer;
	}

	public boolean isLocalPublishing() {
		if (_localPublishing != null) {
			return _localPublishing;
		}

		_localPublishing = ParamUtil.getBoolean(
			_httpServletRequest, "localPublishing");

		return _localPublishing;
	}

	private Long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private final LiferayPortletResponse _liferayPortletResponse;
	private Boolean _localPublishing;
	private PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private SearchContainer<ExportImportConfiguration> _searchContainer;
	private final ThemeDisplay _themeDisplay;

}