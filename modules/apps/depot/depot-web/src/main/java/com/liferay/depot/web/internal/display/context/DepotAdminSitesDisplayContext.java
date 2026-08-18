/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalServiceUtil;
import com.liferay.depot.web.internal.constants.DepotAdminWebKeys;
import com.liferay.depot.web.internal.util.DepotEntryURLUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.portlet.PortletURL;

import java.util.List;
import java.util.Locale;

/**
 * @author Cristina González
 */
public class DepotAdminSitesDisplayContext {

	public DepotAdminSitesDisplayContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_currentURL = PortletURLUtil.getCurrent(
			liferayPortletRequest, liferayPortletResponse);
		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public DropdownItemList getConnectedSiteDropdownItems(
		DepotEntryGroupRel depotEntryGroupRel) {

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setDisabled(
					_isLayoutSetPrototype(depotEntryGroupRel));
				dropdownItem.setHref(
					String.valueOf(
						DepotEntryURLUtil.getUpdateSearchableActionURL(
							depotEntryGroupRel.getDepotEntryGroupRelId(),
							!depotEntryGroupRel.isSearchable(),
							_currentURL.toString(), _liferayPortletResponse)));
				dropdownItem.setLabel(
					LanguageUtil.get(
						PortalUtil.getHttpServletRequest(
							_liferayPortletRequest),
						_getUpdateSearchableKey(depotEntryGroupRel)));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setData(
					HashMapBuilder.<String, Object>put(
						"action", "shareWebContentStructures"
					).put(
						"shared", depotEntryGroupRel.isDdmStructuresAvailable()
					).put(
						"url",
						String.valueOf(
							DepotEntryURLUtil.
								getUpdateDDMStructuresAvailableActionURL(
									depotEntryGroupRel.
										getDepotEntryGroupRelId(),
									!depotEntryGroupRel.
										isDdmStructuresAvailable(),
									_currentURL.toString(),
									_liferayPortletResponse))
					).build());
				dropdownItem.setDisabled(
					_isLayoutSetPrototype(depotEntryGroupRel));
				dropdownItem.setLabel(
					LanguageUtil.get(
						PortalUtil.getHttpServletRequest(
							_liferayPortletRequest),
						_getUpdateDDMStructuresAvailableKey(
							depotEntryGroupRel)));
			}
		).add(
			() -> GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				depotEntryGroupRel.getToGroupId(), ActionKeys.UPDATE),
			dropdownItem -> {
				dropdownItem.setData(
					HashMapBuilder.<String, Object>put(
						"action", "disconnect"
					).put(
						"url",
						() -> String.valueOf(
							DepotEntryURLUtil.getDisconnectSiteActionURL(
								depotEntryGroupRel.getDepotEntryGroupRelId(),
								_currentURL.toString(),
								_liferayPortletResponse))
					).build());
				dropdownItem.setDisabled(
					depotEntryGroupRel.isDdmStructuresAvailable());
				dropdownItem.setLabel(
					LanguageUtil.get(
						PortalUtil.getHttpServletRequest(
							_liferayPortletRequest),
						"disconnect"));
			}
		).build();
	}

	public List<DepotEntryGroupRel> getDepotEntryGroupRels() {
		return DepotEntryGroupRelLocalServiceUtil.getDepotEntryGroupRels(
			_getDepotEntry());
	}

	public PortletURL getItemSelectorURL() throws PortalException {
		ItemSelector itemSelector =
			(ItemSelector)_liferayPortletRequest.getAttribute(
				DepotAdminWebKeys.ITEM_SELECTOR);

		SiteItemSelectorCriterion siteItemSelectorCriterion =
			new SiteItemSelectorCriterion();

		siteItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new URLItemSelectorReturnType());

		DepotEntry depotEntry = _getDepotEntry();

		Group group = depotEntry.getGroup();

		siteItemSelectorCriterion.setIncludeLayoutSetPrototypes(
			!group.isStaged());

		return itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(_liferayPortletRequest),
			_liferayPortletResponse.getNamespace() + "selectSite",
			siteItemSelectorCriterion);
	}

	public String getSiteName(DepotEntryGroupRel depotEntryGroupRel)
		throws PortalException {

		Locale locale = LocaleUtil.fromLanguageId(
			LanguageUtil.getLanguageId(_liferayPortletRequest));

		Group group = GroupServiceUtil.getGroup(
			depotEntryGroupRel.getToGroupId());

		StringBuilder sb = new StringBuilder();

		sb.append(group.getDescriptiveName(locale));

		if (_isLayoutSetPrototype(depotEntryGroupRel)) {
			_appendSuffix(sb, "site-template");
		}
		else if (group.isStaged() && !group.isStagedRemotely() &&
				 group.isStagingGroup()) {

			_appendSuffix(sb, "staging");
		}

		return sb.toString();
	}

	public boolean isLiveDepotEntry() throws PortalException {
		DepotEntry depotEntry = _getDepotEntry();

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		return stagingGroupHelper.isLiveGroup(depotEntry.getGroup());
	}

	private void _appendSuffix(StringBuilder sb, String key) {
		sb.append(StringPool.SPACE);
		sb.append(StringPool.OPEN_PARENTHESIS);
		sb.append(
			LanguageUtil.get(
				PortalUtil.getHttpServletRequest(_liferayPortletRequest), key));
		sb.append(StringPool.CLOSE_PARENTHESIS);
	}

	private DepotEntry _getDepotEntry() {
		return (DepotEntry)_liferayPortletRequest.getAttribute(
			DepotAdminWebKeys.DEPOT_ENTRY);
	}

	private String _getUpdateDDMStructuresAvailableKey(
		DepotEntryGroupRel depotEntryGroupRel) {

		if (!depotEntryGroupRel.isDdmStructuresAvailable()) {
			return "make-structures-available";
		}

		return "make-structures-unavailable";
	}

	private String _getUpdateSearchableKey(
		DepotEntryGroupRel depotEntryGroupRel) {

		if (depotEntryGroupRel.isSearchable()) {
			return "make-unsearchable";
		}

		return "make-searchable";
	}

	private boolean _isLayoutSetPrototype(DepotEntryGroupRel depotEntryGroupRel)
		throws PortalException {

		Group group = GroupServiceUtil.getGroup(
			depotEntryGroupRel.getToGroupId());

		return group.isLayoutSetPrototype();
	}

	private final PortletURL _currentURL;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}