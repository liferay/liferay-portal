/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.display.context;

import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.ThemeCSSCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.constants.StyleBookActionKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.StyleBookUtil;
import com.liferay.style.book.web.internal.security.permissions.resource.StyleBookPermission;

import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class StyleBookManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public StyleBookManagementToolbarDisplayContext(
		CETManager cetManager,
		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry,
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<StyleBookEntry> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_cetManager = cetManager;
		_frontendTokenDefinitionRegistry = frontendTokenDefinitionRegistry;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		if (!StyleBookPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES)) {

			return Collections.emptyList();
		}

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData(
								"action", "exportSelectedStyleBookEntries");
							dropdownItem.setIcon("upload");
							dropdownItem.setLabel(
								LanguageUtil.get(httpServletRequest, "export"));
							dropdownItem.setQuickAction(true);
						}
					).add(
						dropdownItem -> {
							dropdownItem.putData(
								"action", "copySelectedStyleBookEntries");
							dropdownItem.setIcon("copy");
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest, "make-a-copy"));
							dropdownItem.setQuickAction(true);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData(
								"action", "deleteSelectedStyleBookEntries");
							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(httpServletRequest, "delete"));
							dropdownItem.setQuickAction(true);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"addStyleBookEntryURL",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/style_book/add_style_book_entry"
			).buildString()
		).put(
			"copyStyleBookEntryURL",
			() -> PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/style_book/copy_style_book_entry"
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).buildString()
		).put(
			"exportStyleBookEntriesURL",
			() -> {
				ResourceURL exportStyleBookEntriesURL =
					liferayPortletResponse.createResourceURL();

				exportStyleBookEntriesURL.setResourceID(
					"/style_book/export_style_book_entries");

				return exportStyleBookEntriesURL.toString();
			}
		).put(
			"frontendTokenDefinitionProviders",
			() -> _getFrontendTokenDefinitionProviders()
		).build();
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "styleBookManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "addStyleBookEntry");

				dropdownItem.putData(
					"addStyleBookEntryURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/style_book/add_style_book_entry"
					).buildString());

				dropdownItem.putData(
					"title",
					LanguageUtil.get(httpServletRequest, "add-style-book"));
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add"));
			}
		).build();
	}

	@Override
	public Boolean isDisabled() {
		if (getItemsTotal() > 1) {
			return false;
		}

		return true;
	}

	@Override
	public Boolean isShowCreationMenu() {
		return StyleBookPermission.contains(
			_themeDisplay.getPermissionChecker(),
			_themeDisplay.getScopeGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"name", "create-date"};
	}

	private List<Map<String, Object>> _getFrontendTokenDefinitionProviders() {
		List<Map<String, Object>> frontendTokenDefinitionProviders =
			new ArrayList<>();

		for (FrontendTokenDefinition frontendTokenDefinition :
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinitions(
					_themeDisplay.getCompanyId())) {

			if (Objects.equals(
					frontendTokenDefinition.getThemeType(),
					FrontendTokenDefinitionConstants.
						THEME_TYPE_THEME_CSS_CET)) {

				CET cet = _cetManager.getCET(
					_themeDisplay.getCompanyId(),
					frontendTokenDefinition.getThemeId());

				ThemeCSSCET themeCSSCET = (ThemeCSSCET)cet;

				if (StringUtil.equalsIgnoreCase(
						themeCSSCET.getScope(), "controlPanel")) {

					continue;
				}
			}

			frontendTokenDefinitionProviders.add(
				HashMapBuilder.<String, Object>put(
					"name",
					StyleBookUtil.getThemeName(
						_themeDisplay.getCompanyId(), _themeDisplay.getLocale(),
						frontendTokenDefinition.getThemeId())
				).put(
					"themeId", frontendTokenDefinition.getThemeId()
				).build());
		}

		return frontendTokenDefinitionProviders;
	}

	private final CETManager _cetManager;
	private final FrontendTokenDefinitionRegistry
		_frontendTokenDefinitionRegistry;
	private final ThemeDisplay _themeDisplay;

}