/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.display.context;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureServiceUtil;
import com.liferay.dynamic.data.mapping.util.DDMDisplay;
import com.liferay.dynamic.data.mapping.util.comparator.StructureModifiedDateComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsWebKeys;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;
import com.liferay.portal.workflow.kaleo.forms.web.internal.util.KaleoFormsUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Mariano Álvaro Sáiz
 */
public class KaleoFormsAdminFieldsDisplayContext {

	public KaleoFormsAdminFieldsDisplayContext(
		HttpServletRequest httpServletRequest,
		KaleoFormsAdminDisplayContext kaleoFormsAdminDisplayContext,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		RenderRequest renderRequest) {

		_httpServletRequest = httpServletRequest;
		_kaleoFormsAdminDisplayContext = kaleoFormsAdminDisplayContext;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_renderRequest = renderRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		_backURL = HttpComponentsUtil.setParameter(
			PortalUtil.getCurrentURL(_httpServletRequest),
			_liferayPortletResponse.getNamespace() + "historyKey", "fields");

		return _backURL;
	}

	public long getDDMStructureId() throws Exception {
		if (_ddmStructureId != null) {
			return _ddmStructureId;
		}

		KaleoProcess kaleoProcess =
			(KaleoProcess)_httpServletRequest.getAttribute(
				KaleoFormsWebKeys.KALEO_PROCESS);

		_ddmStructureId = KaleoFormsUtil.getKaleoProcessDDMStructureId(
			kaleoProcess, _renderRequest.getPortletSession());

		return _ddmStructureId;
	}

	public String getDDMStructureName() throws Exception {
		if (_ddmStructureName != null) {
			return _ddmStructureName;
		}

		_ddmStructureName = StringPool.BLANK;

		long ddmStructureId = getDDMStructureId();

		if (ddmStructureId > 0) {
			DDMStructure ddmStructure =
				DDMStructureLocalServiceUtil.fetchDDMStructure(ddmStructureId);

			if (ddmStructure != null) {
				_ddmStructureName = ddmStructure.getName(
					_themeDisplay.getLocale());
			}
		}

		return _ddmStructureName;
	}

	public SearchContainer<DDMStructure> getSearchContainer() throws Exception {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		DisplayTerms displayTerms = new DisplayTerms(_httpServletRequest);

		_searchContainer = new SearchContainer(
			_renderRequest, displayTerms, null,
			SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA,
			_getPortletURL(), null, "there-are-no-results");

		_searchContainer.setResultsAndTotal(
			() -> DDMStructureServiceUtil.search(
				_themeDisplay.getCompanyId(),
				PortalUtil.getCurrentAndAncestorSiteGroupIds(
					_themeDisplay.getScopeGroupId()),
				_getScopeClassNameId(), displayTerms.getKeywords(),
				WorkflowConstants.STATUS_ANY, _searchContainer.getStart(),
				_searchContainer.getEnd(),
				new StructureModifiedDateComparator(true)),
			DDMStructureServiceUtil.searchCount(
				_themeDisplay.getCompanyId(),
				PortalUtil.getCurrentAndAncestorSiteGroupIds(
					_themeDisplay.getScopeGroupId()),
				_getScopeClassNameId(), displayTerms.getKeywords(),
				WorkflowConstants.STATUS_ANY));

		return _searchContainer;
	}

	private PortletURL _getPortletURL() throws Exception {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.create(
			PortletURLUtil.clone(
				PortletURLUtil.getCurrent(
					_liferayPortletRequest, _liferayPortletResponse),
				_liferayPortletResponse)
		).setMVCRenderCommandName(
			"/admin/edit_kaleo_process.jsp"
		).setRedirect(
			ParamUtil.getString(_httpServletRequest, "redirect")
		).setParameter(
			"historyKey", "fields"
		).setParameter(
			"kaleoProcessId",
			BeanParamUtil.getLong(
				(KaleoProcess)_httpServletRequest.getAttribute(
					KaleoFormsWebKeys.KALEO_PROCESS),
				_httpServletRequest, "kaleoProcessId")
		).buildPortletURL();

		return _portletURL;
	}

	private long _getScopeClassNameId() {
		if (_scopeClassNameId != null) {
			return _scopeClassNameId;
		}

		DDMDisplay ddmDisplay = _kaleoFormsAdminDisplayContext.getDDMDisplay();

		_scopeClassNameId = PortalUtil.getClassNameId(
			ddmDisplay.getStructureType());

		return _scopeClassNameId;
	}

	private String _backURL;
	private Long _ddmStructureId;
	private String _ddmStructureName;
	private final HttpServletRequest _httpServletRequest;
	private final KaleoFormsAdminDisplayContext _kaleoFormsAdminDisplayContext;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private Long _scopeClassNameId;
	private SearchContainer<DDMStructure> _searchContainer;
	private final ThemeDisplay _themeDisplay;

}