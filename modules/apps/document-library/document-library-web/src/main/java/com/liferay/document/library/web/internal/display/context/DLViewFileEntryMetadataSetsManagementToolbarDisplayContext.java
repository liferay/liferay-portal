/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper;
import com.liferay.document.library.web.internal.security.permission.resource.DDMStructurePermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Cristina González
 */
public class DLViewFileEntryMetadataSetsManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public DLViewFileEntryMetadataSetsManagementToolbarDisplayContext(
			DLViewFileEntryMetadataSetsDisplayContext
				dlViewFileEntryMetadataSetsDisplayContext,
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			dlViewFileEntryMetadataSetsDisplayContext.getStructureSearch());

		_dlViewFileEntryMetadataSetsDisplayContext =
			dlViewFileEntryMetadataSetsDisplayContext;

		_dlRequestHelper = new DLRequestHelper(httpServletRequest);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteMetadataSets");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
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
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)liferayPortletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName",
					"/document_library/edit_ddm_structure", "redirect",
					themeDisplay.getURLCurrent(), "groupId",
					String.valueOf(_dlRequestHelper.getScopeGroupId()));

				dropdownItem.setLabel(
					LanguageUtil.format(
						_dlRequestHelper.getRequest(), "new-x",
						"metadata-set"));
			}
		).build();
	}

	@Override
	public String getDefaultEventHandler() {
		return "dlDDMStructuresManagementToolbarDefaultEventHandler";
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setNavigation(
			"file_entry_metadata_sets"
		).setParameter(
			"groupId", _dlRequestHelper.getScopeGroupId()
		).buildString();
	}

	@Override
	public String getSearchContainerId() {
		return "ddmStructures";
	}

	@Override
	public Boolean isShowCreationMenu() {
		try {
			return _isShowAddButton();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get creation menu", portalException);
			}

			return false;
		}
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"modified-date", "id"};
	}

	private boolean _isShowAddButton() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		if ((!group.hasLocalOrRemoteStagingGroup() || group.isStagingGroup()) &&
			DDMStructurePermission.containsAddDDMStructurePermission(
				_dlRequestHelper.getPermissionChecker(),
				_dlRequestHelper.getScopeGroupId(),
				_dlViewFileEntryMetadataSetsDisplayContext.
					getStructureClassNameId())) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLViewFileEntryMetadataSetsManagementToolbarDisplayContext.class);

	private final DLRequestHelper _dlRequestHelper;
	private final DLViewFileEntryMetadataSetsDisplayContext
		_dlViewFileEntryMetadataSetsDisplayContext;

}