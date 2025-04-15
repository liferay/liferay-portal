/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.settings.ArchivedSettings;
import com.liferay.portal.kernel.settings.ArchivedSettingsFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.configuration.web.internal.constants.PortletConfigurationPortletKeys;
import com.liferay.portlet.configuration.web.internal.constants.PortletConfigurationWebKeys;
import com.liferay.portlet.configuration.web.internal.servlet.taglib.util.ArchivedSettingsActionDropdownItemsProvider;
import com.liferay.portlet.configuration.web.internal.util.comparator.ArchivedSettingsModifiedDateComparator;
import com.liferay.portlet.configuration.web.internal.util.comparator.ArchivedSettingsNameComparator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class PortletConfigurationTemplatesDisplayContext {

	public PortletConfigurationTemplatesDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_moduleName = (String)renderRequest.getAttribute(
			PortletConfigurationWebKeys.MODULE_NAME);
		_archivedSettingsFactory =
			(ArchivedSettingsFactory)renderRequest.getAttribute(
				PortletConfigurationWebKeys.SETTINGS_FACTORY);
	}

	public List<DropdownItem> getActionDropdownItems(
		ArchivedSettings archivedSettings) {

		ArchivedSettingsActionDropdownItemsProvider
			archivedSettingsActionDropdownItemsProvider =
				new ArchivedSettingsActionDropdownItemsProvider(
					archivedSettings, _renderRequest, _renderResponse);

		return archivedSettingsActionDropdownItemsProvider.
			getActionDropdownItems();
	}

	public SearchContainer<ArchivedSettings>
		getArchivedSettingsSearchContainer() {

		if (_archivedSettingsSearchContainer != null) {
			return _archivedSettingsSearchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<ArchivedSettings> archivedSettingsSearchContainer =
			new SearchContainer<>(
				_renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM,
				SearchContainer.DEFAULT_DELTA, getPortletURL(), null,
				"there-are-no-configuration-templates");

		archivedSettingsSearchContainer.setOrderByCol(getOrderByCol());

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		OrderByComparator<ArchivedSettings> orderByComparator = null;

		if (Objects.equals(getOrderByCol(), "modified-date")) {
			orderByComparator =
				ArchivedSettingsModifiedDateComparator.getInstance(orderByAsc);
		}
		else {
			orderByComparator = ArchivedSettingsNameComparator.getInstance(
				orderByAsc);
		}

		archivedSettingsSearchContainer.setOrderByComparator(orderByComparator);
		archivedSettingsSearchContainer.setOrderByType(getOrderByType());

		Portlet selPortlet = PortletLocalServiceUtil.getPortletById(
			themeDisplay.getCompanyId(), getPortletResource());

		archivedSettingsSearchContainer.setResultsAndTotal(
			ListUtil.sort(
				_archivedSettingsFactory.getPortletInstanceArchivedSettingsList(
					themeDisplay.getScopeGroupId(),
					selPortlet.getRootPortletId()),
				archivedSettingsSearchContainer.getOrderByComparator()));

		archivedSettingsSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		_archivedSettingsSearchContainer = archivedSettingsSearchContainer;

		return _archivedSettingsSearchContainer;
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest,
			PortletConfigurationPortletKeys.PORTLET_CONFIGURATION, "list");

		return _displayStyle;
	}

	public String getModuleName() {
		return _moduleName;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest,
			PortletConfigurationPortletKeys.PORTLET_CONFIGURATION, "name");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest,
			PortletConfigurationPortletKeys.PORTLET_CONFIGURATION, "asc");

		return _orderByType;
	}

	public String getPortletResource() {
		if (_portletResource != null) {
			return _portletResource;
		}

		_portletResource = ParamUtil.getString(
			_httpServletRequest, "portletResource");

		return _portletResource;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/edit_configuration_templates.jsp"
		).setRedirect(
			getRedirect()
		).setPortletResource(
			getPortletResource()
		).setParameter(
			"displayStyle",
			() -> {
				String displayStyle = getDisplayStyle();

				if (Validator.isNotNull(displayStyle)) {
					return displayStyle;
				}

				return null;
			}
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
		).setParameter(
			"returnToFullPageURL", getReturnToFullPageURL()
		).buildPortletURL();
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	public String getReturnToFullPageURL() {
		if (_returnToFullPageURL != null) {
			return _returnToFullPageURL;
		}

		_returnToFullPageURL = ParamUtil.getString(
			_httpServletRequest, "returnToFullPageURL");

		return _returnToFullPageURL;
	}

	private final ArchivedSettingsFactory _archivedSettingsFactory;
	private SearchContainer<ArchivedSettings> _archivedSettingsSearchContainer;
	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private final String _moduleName;
	private String _orderByCol;
	private String _orderByType;
	private String _portletResource;
	private String _redirect;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private String _returnToFullPageURL;

}