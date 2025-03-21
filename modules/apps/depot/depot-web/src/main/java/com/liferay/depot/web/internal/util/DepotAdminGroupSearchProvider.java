/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DepotAdminGroupSearchProvider.class)
public class DepotAdminGroupSearchProvider {

	public GroupSearch getGroupSearch(
			GroupItemSelectorCriterion groupItemSelectorCriterion,
			PortletRequest portletRequest, PortletURL portletURL)
		throws PortalException {

		if (Validator.isNull(ParamUtil.getString(portletRequest, "keywords")) &&
			!groupItemSelectorCriterion.isIncludeAllVisibleGroups()) {

			return _getGroupConnectedDepotGroupsGroupSearch(
				portletRequest, portletURL);
		}

		return _getGroupSearch(
			groupItemSelectorCriterion.getExcludedGroupIds(), portletRequest,
			portletURL);
	}

	public GroupSearch getGroupSearch(
			PortletRequest portletRequest, PortletURL portletURL)
		throws PortalException {

		return _getGroupSearch(null, portletRequest, portletURL);
	}

	private GroupSearch _getGroupConnectedDepotGroupsGroupSearch(
			PortletRequest portletRequest, PortletURL portletURL)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		GroupSearch groupSearch = new GroupSearch(portletRequest, portletURL);

		groupSearch.setEmptyResultsMessage(
			_language.get(
				portletRequest.getLocale(), "no-asset-libraries-were-found"));
		groupSearch.setResultsAndTotal(
			() -> {
				List<DepotEntry> depotEntries =
					_depotEntryService.getGroupConnectedDepotEntries(
						themeDisplay.getScopeGroupId(), groupSearch.getStart(),
						groupSearch.getEnd());

				return TransformUtil.transform(
					depotEntries, depotEntry -> depotEntry.getGroup());
			},
			_depotEntryService.getGroupConnectedDepotEntriesCount(
				themeDisplay.getScopeGroupId()));

		return groupSearch;
	}

	private GroupSearch _getGroupSearch(
			long[] excludedGroupIds, PortletRequest portletRequest,
			PortletURL portletURL)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		LinkedHashMap<String, Object> groupParams =
			LinkedHashMapBuilder.<String, Object>put(
				"actionId", ActionKeys.VIEW
			).put(
				"excludedGroupIds", ListUtil.fromArray(excludedGroupIds)
			).put(
				"site", Boolean.FALSE
			).build();

		GroupSearch groupSearch = new GroupSearch(portletRequest, portletURL);

		groupSearch.setEmptyResultsMessage(
			_language.get(
				portletRequest.getLocale(), "no-asset-libraries-were-found"));

		String keywords = ParamUtil.getString(portletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			groupSearch.setResultsAndTotal(
				() -> _processGroups(
					themeDisplay.getScopeGroup(),
					_groupService.search(
						company.getCompanyId(),
						new long[] {
							_portal.getClassNameId(DepotEntry.class.getName())
						},
						keywords, groupParams, groupSearch.getStart(),
						groupSearch.getEnd(),
						groupSearch.getOrderByComparator())),
				_groupService.searchCount(
					company.getCompanyId(),
					new long[] {
						_portal.getClassNameId(DepotEntry.class.getName())
					},
					keywords, groupParams));
		}
		else {
			groupSearch.setResultsAndTotal(
				() -> _processGroups(
					themeDisplay.getScopeGroup(),
					_groupService.search(
						company.getCompanyId(),
						new long[] {
							_portal.getClassNameId(DepotEntry.class.getName())
						},
						keywords, groupParams, groupSearch.getStart(),
						groupSearch.getEnd(),
						groupSearch.getOrderByComparator())),
				_groupService.searchCount(
					company.getCompanyId(),
					new long[] {
						_portal.getClassNameId(DepotEntry.class.getName())
					},
					keywords, groupParams));
		}

		return groupSearch;
	}

	private List<Group> _processGroups(Group group, List<Group> groups) {
		if (!group.isStagingGroup()) {
			return groups;
		}

		return TransformUtil.transform(
			groups,
			curGroup -> {
				if (curGroup.hasStagingGroup()) {
					return curGroup.getStagingGroup();
				}

				return curGroup;
			});
	}

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private GroupService _groupService;

	@Reference
	private Language _language;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Portal _portal;

}