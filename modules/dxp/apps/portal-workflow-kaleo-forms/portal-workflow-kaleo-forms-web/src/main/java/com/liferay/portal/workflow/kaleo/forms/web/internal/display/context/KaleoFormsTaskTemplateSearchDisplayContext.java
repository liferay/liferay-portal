/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.display.context;

import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.workflow.kaleo.forms.constants.KaleoFormsWebKeys;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoTaskFormPair;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoTaskFormPairs;
import com.liferay.portal.workflow.kaleo.forms.web.internal.util.KaleoFormsUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Mariano Álvaro Sáiz
 */
public class KaleoFormsTaskTemplateSearchDisplayContext {

	public KaleoFormsTaskTemplateSearchDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		RenderRequest renderRequest) {

		_httpServletRequest = httpServletRequest;
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

		_backURL = ParamUtil.getString(_httpServletRequest, "backURL");

		return _backURL;
	}

	public KaleoTaskFormPair getInitialStateKaleoTaskFormPair()
		throws Exception {

		if (_initialStateKaleoTaskFormPair != null) {
			return _initialStateKaleoTaskFormPair;
		}

		_initialStateKaleoTaskFormPair =
			KaleoFormsUtil.getInitialStateKaleoTaskFormPair(
				_getKaleoProcessId(), _getDDMStructureId(),
				_getWorkflowDefinition(),
				KaleoFormsUtil.getInitialStateName(
					_themeDisplay.getCompanyId(), _getWorkflowDefinition()),
				_renderRequest.getPortletSession());

		return _initialStateKaleoTaskFormPair;
	}

	public SearchContainer<KaleoTaskFormPair> getSearchContainer()
		throws Exception {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			_renderRequest, _getPortletURL(), null, "no-tasks-were-found");

		KaleoTaskFormPairs kaleoTaskFormPairs =
			KaleoFormsUtil.getKaleoTaskFormPairs(
				_themeDisplay.getCompanyId(), _getKaleoProcessId(),
				_getDDMStructureId(), _getWorkflowDefinition(),
				_renderRequest.getPortletSession());

		kaleoTaskFormPairs.add(0, getInitialStateKaleoTaskFormPair());

		_searchContainer.setResultsAndTotal(
			kaleoTaskFormPairs::list, kaleoTaskFormPairs.size());

		return _searchContainer;
	}

	private long _getDDMStructureId() throws Exception {
		if (_ddmStructureId != null) {
			return _ddmStructureId;
		}

		_ddmStructureId = KaleoFormsUtil.getKaleoProcessDDMStructureId(
			_getKaleoProcessId(), _renderRequest.getPortletSession());

		return _ddmStructureId;
	}

	private long _getKaleoProcessId() {
		if (_kaleoProcessId != null) {
			return _kaleoProcessId;
		}

		KaleoProcess kaleoProcess =
			(KaleoProcess)_httpServletRequest.getAttribute(
				KaleoFormsWebKeys.KALEO_PROCESS);

		_kaleoProcessId = BeanParamUtil.getLong(
			kaleoProcess, _httpServletRequest, "kaleoProcessId");

		return _kaleoProcessId;
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
		).buildPortletURL();

		return _portletURL;
	}

	private String _getWorkflowDefinition() {
		if (_workflowDefinition != null) {
			return _workflowDefinition;
		}

		_workflowDefinition = ParamUtil.getString(
			_httpServletRequest, "workflowDefinition");

		return _workflowDefinition;
	}

	private String _backURL;
	private Long _ddmStructureId;
	private final HttpServletRequest _httpServletRequest;
	private KaleoTaskFormPair _initialStateKaleoTaskFormPair;
	private Long _kaleoProcessId;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private SearchContainer<KaleoTaskFormPair> _searchContainer;
	private final ThemeDisplay _themeDisplay;
	private String _workflowDefinition;

}