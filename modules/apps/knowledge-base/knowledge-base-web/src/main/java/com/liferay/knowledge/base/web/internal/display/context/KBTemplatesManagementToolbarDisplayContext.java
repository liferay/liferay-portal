/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.knowledge.base.web.internal.security.permission.resource.AdminPermission;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class KBTemplatesManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public KBTemplatesManagementToolbarDisplayContext(
			String deleteKBTemplatesURL, HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			SearchContainer<KBTemplate> searchContainer)
		throws PortalException {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_deleteKBTemplatesURL = deleteKBTemplatesURL;

		_currentURLObj = PortletURLUtil.getCurrent(
			liferayPortletRequest, liferayPortletResponse);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteKBTemplates");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"deleteKBTemplatesURL", _deleteKBTemplatesURL
		).build();
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/knowledge_base/view_kb_templates"
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		if (Validator.isNotNull(_getKeywords()) ||
			!AdminPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				KBActionKeys.ADD_KB_TEMPLATE)) {

			return null;
		}

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCPath(
						"/admin/common/edit_kb_template.jsp"
					).setRedirect(
						PortalUtil.getCurrentURL(httpServletRequest)
					).buildPortletURL());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add-template"));
			}
		).build();
	}

	@Override
	public int getItemsTotal() {
		return searchContainer.getTotal();
	}

	public List<DropdownItem> getOrderByDropdownItems() {
		return new DropdownItemList() {
			{
				Map<String, String> orderColumnsMap = HashMapBuilder.put(
					"create-date", "create-date"
				).put(
					"modified-date", "modified-date"
				).put(
					"title", "title"
				).put(
					"user-name", "user-name"
				).build();

				for (Map.Entry<String, String> orderByColEntry :
						orderColumnsMap.entrySet()) {

					add(
						dropdownItem -> {
							String orderByCol = orderByColEntry.getKey();

							dropdownItem.setActive(
								orderByCol.equals(_getOrderByCol()));

							dropdownItem.setHref(
								_getCurrentSortingURL(), "orderByCol",
								orderByColEntry.getValue());
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest, orderByCol));
						});
				}
			}
		};
	}

	@Override
	public String getOrderByType() {
		return searchContainer.getOrderByType();
	}

	@Override
	public String getSearchActionURL() {
		return getClearResultsURL();
	}

	@Override
	public String getSortingURL() {
		return PortletURLBuilder.create(
			_getCurrentSortingURL()
		).setParameter(
			"orderByType",
			() -> {
				if (Objects.equals(getOrderByType(), "asc")) {
					return "desc";
				}

				return "asc";
			}
		).buildString();
	}

	@Override
	public Boolean isDisabled() {
		return !searchContainer.hasResults();
	}

	@Override
	public Boolean isSelectable() {
		return true;
	}

	private PortletURL _getCurrentSortingURL() {
		try {
			return PortletURLBuilder.create(
				PortletURLUtil.clone(_currentURLObj, liferayPortletResponse)
			).setMVCRenderCommandName(
				"/knowledge_base/view_kb_templates"
			).buildPortletURL();
		}
		catch (PortletException portletException) {
			return ReflectionUtil.throwException(portletException);
		}
	}

	private String _getKeywords() {
		return ParamUtil.getString(httpServletRequest, "keywords");
	}

	private String _getOrderByCol() {
		return searchContainer.getOrderByCol();
	}

	private final PortletURL _currentURLObj;
	private final String _deleteKBTemplatesURL;
	private final ThemeDisplay _themeDisplay;

}