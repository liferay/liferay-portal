/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.redirect.model.RedirectNotFoundEntry;
import com.liferay.redirect.service.RedirectNotFoundEntryLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class RedirectNotFoundEntriesManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public RedirectNotFoundEntriesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		RedirectNotFoundEntryLocalService redirectNotFoundEntryLocalService,
		SearchContainer<RedirectNotFoundEntry> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_redirectNotFoundEntryLocalService = redirectNotFoundEntryLocalService;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData(
					"action", "ignoreSelectedRedirectNotFoundEntries");
				dropdownItem.setIcon("hidden");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "ignore"));
				dropdownItem.setQuickAction(true);
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData(
					"action", "unignoreSelectedRedirectNotFoundEntries");
				dropdownItem.setIcon("view");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "unignore"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"editRedirectNotFoundEntriesURL",
			() -> PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/redirect/edit_redirect_not_found_entry"
			).setRedirect(
				() -> {
					ThemeDisplay themeDisplay =
						(ThemeDisplay)httpServletRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					return themeDisplay.getURLCurrent();
				}
			).buildString()
		).build();
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setNavigation(
			"404-urls"
		).buildString();
	}

	@Override
	public String getDefaultEventHandler() {
		return "redirectNotFoundEntriesManagementToolbarDefaultEventHandler";
	}

	@Override
	public List<DropdownItem> getFilterDropdownItems() {
		List<DropdownItem> filterNavigationDropdownItems =
			getFilterNavigationDropdownItems();

		DropdownItemList filterDropdownItems = DropdownItemListBuilder.addGroup(
			() -> filterNavigationDropdownItems != null,
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					filterNavigationDropdownItems);
				dropdownGroupItem.setLabel(
					getFilterNavigationDropdownItemsLabel());
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterDateDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "filter-by-date"));
			}
		).build();

		if (filterDropdownItems.isEmpty()) {
			return null;
		}

		return filterDropdownItems;
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		return LabelItemListBuilder.add(
			() -> !StringUtil.equals(getNavigation(), "active-urls"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						getNavigationParam(), (String)null
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(
					String.format(
						"%s: %s", LanguageUtil.get(httpServletRequest, "type"),
						LanguageUtil.get(httpServletRequest, getNavigation())));
			}
		).add(
			() -> _getFilterDate() != 0,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"filterDate", (String)null
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(
					String.format(
						"%s: %s", LanguageUtil.get(httpServletRequest, "date"),
						_getFilterDateLabel(_getFilterDate())));
			}
		).build();
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).buildString();
	}

	@Override
	public Boolean isDisabled() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		int redirectNotFoundEntriesCount =
			_redirectNotFoundEntryLocalService.getRedirectNotFoundEntriesCount(
				themeDisplay.getScopeGroupId(), null, null);

		return redirectNotFoundEntriesCount == 0;
	}

	@Override
	protected String getFilterNavigationDropdownItemsLabel() {
		return LanguageUtil.get(httpServletRequest, "filter-by-type");
	}

	@Override
	protected String getNavigation() {
		return ParamUtil.getString(
			liferayPortletRequest, getNavigationParam(), "active-urls");
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"all", "active-urls", "ignored-urls"};
	}

	@Override
	protected String getNavigationParam() {
		return "filterType";
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"modified-date", "requests"};
	}

	private int _getFilterDate() {
		return ParamUtil.getInteger(httpServletRequest, "filterDate");
	}

	private List<DropdownItem> _getFilterDateDropdownItems() {
		return DropdownItemListBuilder.add(
			_getFilterDateDropdownItemUnsafeConsumer(0)
		).add(
			_getFilterDateDropdownItemUnsafeConsumer(1)
		).add(
			_getFilterDateDropdownItemUnsafeConsumer(7)
		).add(
			_getFilterDateDropdownItemUnsafeConsumer(30)
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getFilterDateDropdownItemUnsafeConsumer(int days) {

		return dropdownItem -> {
			dropdownItem.setActive(days == _getFilterDate());
			dropdownItem.setHref(
				PortletURLBuilder.create(
					getPortletURL()
				).setParameter(
					"filterDate", days
				).buildPortletURL());
			dropdownItem.setLabel(_getFilterDateLabel(days));
		};
	}

	private String _getFilterDateLabel(int days) {
		if (days == 0) {
			return LanguageUtil.get(httpServletRequest, "all");
		}

		if (days == 1) {
			return LanguageUtil.format(httpServletRequest, "x-day", days);
		}

		return LanguageUtil.format(httpServletRequest, "x-days", days);
	}

	private final RedirectNotFoundEntryLocalService
		_redirectNotFoundEntryLocalService;

}