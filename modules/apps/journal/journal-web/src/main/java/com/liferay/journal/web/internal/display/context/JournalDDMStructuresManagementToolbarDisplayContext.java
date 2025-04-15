/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.web.internal.security.permission.resource.DDMStructurePermission;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class JournalDDMStructuresManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public JournalDDMStructuresManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			JournalDDMStructuresDisplayContext
				journalDDMStructuresDisplayContext)
		throws Exception {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			journalDDMStructuresDisplayContext.
				getDDMStructureSearchContainer());
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteDDMStructures");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public String getAvailableActions(DDMStructure ddmStructure)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (DDMStructurePermission.contains(
				themeDisplay.getPermissionChecker(), ddmStructure,
				ActionKeys.DELETE)) {

			return "deleteDDMStructures";
		}

		return StringPool.BLANK;
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
		return "ddmStructureManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(), "mvcPath",
					"/edit_data_definition.jsp", "redirect",
					themeDisplay.getURLCurrent());

				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add"));
			}
		).build();
	}

	@Override
	public String getSearchContainerId() {
		return "ddmStructures";
	}

	@Override
	public Boolean isShowCreationMenu() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if ((stagingGroupHelper.isLocalLiveGroup(group) ||
			 stagingGroupHelper.isRemoteLiveGroup(group)) &&
			stagingGroupHelper.isStagedPortlet(
				group, JournalPortletKeys.JOURNAL)) {

			return false;
		}

		try {
			if (DDMStructurePermission.containsAddDDMStructurePermission(
					themeDisplay.getPermissionChecker(),
					themeDisplay.getScopeGroupId(),
					PortalUtil.getClassNameId(
						JournalArticle.class.getName()))) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"name", "modified-date", "id"};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalDDMStructuresManagementToolbarDisplayContext.class);

}