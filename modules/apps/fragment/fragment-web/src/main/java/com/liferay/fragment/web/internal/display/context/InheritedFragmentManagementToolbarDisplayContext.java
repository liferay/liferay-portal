/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.web.internal.security.permission.resource.FragmentPermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Jürgen Kappler
 */
public class InheritedFragmentManagementToolbarDisplayContext
	extends FragmentManagementToolbarDisplayContext {

	public InheritedFragmentManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		FragmentDisplayContext fragmentDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			fragmentDisplayContext.getFragmentEntriesSearchContainer(),
			fragmentDisplayContext);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData(
					"action", "exportFragmentCompositionsAndFragmentEntries");
				dropdownItem.setIcon("export");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "export"));
				dropdownItem.setQuickAction(true);
			}
		).add(
			() -> FragmentPermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES),
			dropdownItem -> {
				dropdownItem.putData("action", "copyToSelectedFragmentEntries");
				dropdownItem.setIcon("copy");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "copy-to"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@Override
	public Map<String, Object> getComponentContext() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"copyFragmentEntryURL",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return PortletURLBuilder.createActionURL(
					liferayPortletResponse
				).setActionName(
					"/fragment/copy_fragment_entry"
				).setRedirect(
					themeDisplay.getURLCurrent()
				).buildString();
			}
		).put(
			"exportFragmentCompositionsAndFragmentEntriesURL",
			() -> {
				ResourceURL exportFragmentEntriesURL =
					liferayPortletResponse.createResourceURL();

				exportFragmentEntriesURL.setResourceID(
					"/fragment" +
						"/export_fragment_compositions_and_fragment_entries");

				return exportFragmentEntriesURL.toString();
			}
		).put(
			"fragmentCollectionId",
			ParamUtil.getLong(liferayPortletRequest, "fragmentCollectionId")
		).build();
	}

}