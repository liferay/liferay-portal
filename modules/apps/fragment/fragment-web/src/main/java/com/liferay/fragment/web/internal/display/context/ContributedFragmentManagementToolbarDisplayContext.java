/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.web.internal.security.permission.resource.FragmentPermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class ContributedFragmentManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public ContributedFragmentManagementToolbarDisplayContext(
		FragmentDisplayContext fragmentDisplayContext,
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			fragmentDisplayContext.getContributedEntriesSearchContainer());

		_fragmentDisplayContext = fragmentDisplayContext;
		_liferayPortletResponse = liferayPortletResponse;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return DropdownItemListBuilder.add(
			() -> FragmentPermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES),
			dropdownItem -> {
				dropdownItem.putData(
					"action", "copyContributedEntriesToFragmentCollection");
				dropdownItem.setIcon("copy");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "make-a-copy"));
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

	public Map<String, Object> getComponentContext() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HashMapBuilder.<String, Object>put(
			"addFragmentCollectionURL",
			() -> {
				LiferayPortletURL addFragmentCollectionURL =
					(LiferayPortletURL)
						_liferayPortletResponse.createResourceURL();

				addFragmentCollectionURL.setCopyCurrentRenderParameters(false);
				addFragmentCollectionURL.setResourceID(
					"/fragment/add_fragment_collection");

				return addFragmentCollectionURL.toString();
			}
		).put(
			"copyContributedEntryURL",
			() -> PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/fragment/copy_fragment_entry"
			).setRedirect(
				themeDisplay.getURLCurrent()
			).buildString()
		).put(
			"fragmentCollections",
			() -> {
				JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

				for (FragmentCollection fragmentCollection :
						FragmentCollectionLocalServiceUtil.
							getFragmentCollections(
								themeDisplay.getScopeGroupId(),
								QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

					jsonArray.put(
						JSONUtil.put(
							"fragmentCollectionId",
							fragmentCollection.getFragmentCollectionId()
						).put(
							"name", fragmentCollection.getName()
						));
				}

				return jsonArray;
			}
		).build();
	}

	@Override
	public String getComponentId() {
		return "contributedFragmentEntriesManagementToolbar" +
			_fragmentDisplayContext.getFragmentCollectionKey();
	}

	@Override
	public String getSortingURL() {
		return null;
	}

	private final FragmentDisplayContext _fragmentDisplayContext;
	private final LiferayPortletResponse _liferayPortletResponse;

}