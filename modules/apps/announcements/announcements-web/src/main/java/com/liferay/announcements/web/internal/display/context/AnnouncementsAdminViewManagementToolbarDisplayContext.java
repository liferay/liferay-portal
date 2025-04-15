/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class AnnouncementsAdminViewManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public AnnouncementsAdminViewManagementToolbarDisplayContext(
		AnnouncementsAdminViewDisplayContext
			announcementsAdminViewDisplayContext,
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			announcementsAdminViewDisplayContext.getSearchContainer());

		_announcementsAdminViewDisplayContext =
			announcementsAdminViewDisplayContext;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteEntries");
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
			"deleteEntriesURL",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/announcements/edit_entry"
			).buildString()
		).put(
			"inputId", Constants.CMD
		).put(
			"inputValue", Constants.DELETE
		).build();
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setNavigation(
			_announcementsAdminViewDisplayContext.getNavigation()
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				String navigation =
					_announcementsAdminViewDisplayContext.getNavigation();

				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCRenderCommandName(
						"/announcements/edit_entry"
					).setRedirect(
						PortalUtil.getCurrentURL(httpServletRequest)
					).setParameter(
						"alert", navigation.equals("alerts")
					).setParameter(
						"distributionScope",
						_announcementsAdminViewDisplayContext.
							getDistributionScope()
					).buildPortletURL());

				String label = null;

				if (navigation.equals("alerts")) {
					label = "add-alert";
				}
				else {
					label = "add-announcement";
				}

				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, label));
			}
		).build();
	}

	@Override
	public List<DropdownItem> getFilterDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterNavigationDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "filter-by"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		return LabelItemListBuilder.add(
			() -> Validator.isNotNull(
				_announcementsAdminViewDisplayContext.getDistributionScope()),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"distributionScope", (String)null
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(
					_announcementsAdminViewDisplayContext.
						getCurrentDistributionScopeLabel());
			}
		).build();
	}

	@Override
	public String getSearchContainerId() {
		return _announcementsAdminViewDisplayContext.getSearchContainerId();
	}

	@Override
	public Boolean isShowSearch() {
		return false;
	}

	private List<DropdownItem> _getFilterNavigationDropdownItems()
		throws Exception {

		return new DropdownItemList() {
			{
				String currentDistributionScopeLabel =
					_announcementsAdminViewDisplayContext.
						getCurrentDistributionScopeLabel();

				Map<String, String> distributionScopes =
					_announcementsAdminViewDisplayContext.
						getDistributionScopes();

				for (Map.Entry<String, String> distributionScopeEntry :
						distributionScopes.entrySet()) {

					add(
						dropdownItem -> {
							dropdownItem.setActive(
								currentDistributionScopeLabel.equals(
									distributionScopeEntry.getKey()));
							dropdownItem.setHref(
								getPortletURL(), "distributionScope",
								distributionScopeEntry.getValue());
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest,
									distributionScopeEntry.getKey()));
						});
				}
			}
		};
	}

	private final AnnouncementsAdminViewDisplayContext
		_announcementsAdminViewDisplayContext;

}