/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.display.context;

import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.web.internal.export.background.task.UADExportBackgroundTaskManagerUtil;
import com.liferay.user.associated.data.web.internal.util.UADLanguageUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

/**
 * @author Pei-Jung Lan
 */
public class UADExportProcessDisplayContext {

	public UADExportProcessDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
	}

	public int getBackgroundTaskStatus(String status) {
		if (status.equals("failed")) {
			return BackgroundTaskConstants.STATUS_FAILED;
		}
		else if (status.equals("in-progress")) {
			return BackgroundTaskConstants.STATUS_IN_PROGRESS;
		}
		else if (status.equals("successful")) {
			return BackgroundTaskConstants.STATUS_SUCCESSFUL;
		}

		return 0;
	}

	public Comparator<BackgroundTask> getComparator(
		Locale locale, String orderByCol, String orderByType) {

		Comparator<BackgroundTask> comparator = Comparator.comparing(
			BackgroundTask::getCreateDate);

		if (orderByCol.equals("name")) {
			comparator =
				(BackgroundTask backgroundTask1,
				 BackgroundTask backgroundTask2) -> {

					Map<String, Serializable> taskContextMap1 =
						backgroundTask1.getTaskContextMap();
					Map<String, Serializable> taskContextMap2 =
						backgroundTask2.getTaskContextMap();

					String applicationName1 =
						UADLanguageUtil.getApplicationName(
							(String)taskContextMap1.get("applicationKey"),
							locale);
					String applicationName2 =
						UADLanguageUtil.getApplicationName(
							(String)taskContextMap2.get("applicationKey"),
							locale);

					return applicationName1.compareTo(applicationName2);
				};
		}

		if (orderByType.equals("desc")) {
			comparator = comparator.reversed();
		}

		return comparator;
	}

	public String getNavigation() {
		if (_navigation != null) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(
			_httpServletRequest, "navigation", "all");

		return _navigation;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest,
			UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
			"export-order-by-col", "name");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest,
			UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
			"export-order-by-type", "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() throws PortalException {
		PortletRequest portletRequest =
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);
		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		return PortletURLBuilder.create(
			PortletURLUtil.getCurrent(
				PortalUtil.getLiferayPortletRequest(portletRequest),
				PortalUtil.getLiferayPortletResponse(portletResponse))
		).setMVCRenderCommandName(
			"/user_associated_data/view_uad_export_processes"
		).setNavigation(
			getNavigation()
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).setParameter(
			"p_u_i_d",
			() -> {
				User selectedUser = PortalUtil.getSelectedUser(
					_httpServletRequest);

				return selectedUser.getUserId();
			}
		).buildPortletURL();
	}

	public SearchContainer<BackgroundTask> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		PortletRequest portletRequest =
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		SearchContainer<BackgroundTask> searchContainer = new SearchContainer(
			portletRequest, getPortletURL(), null,
			"no-personal-data-export-processes-were-found");

		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByType(getOrderByType());

		String navigation = getNavigation();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		User selectedUser = PortalUtil.getSelectedUser(_httpServletRequest);

		if (navigation.equals("failed") || navigation.equals("in-progress") ||
			navigation.equals("successful")) {

			int status = getBackgroundTaskStatus(navigation);

			searchContainer.setResultsAndTotal(
				ListUtil.sort(
					UADExportBackgroundTaskManagerUtil.getBackgroundTasks(
						themeDisplay.getScopeGroupId(),
						selectedUser.getUserId(), status),
					getComparator(
						themeDisplay.getLocale(),
						searchContainer.getOrderByCol(),
						searchContainer.getOrderByType())));
		}
		else {
			searchContainer.setResultsAndTotal(
				ListUtil.sort(
					UADExportBackgroundTaskManagerUtil.getBackgroundTasks(
						themeDisplay.getScopeGroupId(),
						selectedUser.getUserId()),
					getComparator(
						themeDisplay.getLocale(),
						searchContainer.getOrderByCol(),
						searchContainer.getOrderByType())));
		}

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	private final HttpServletRequest _httpServletRequest;
	private String _navigation;
	private String _orderByCol;
	private String _orderByType;
	private final RenderResponse _renderResponse;
	private SearchContainer<BackgroundTask> _searchContainer;

}