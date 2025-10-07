/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.util.PatcherProductVersionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class PatcherProjectVersionsManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public PatcherProjectVersionsManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<PatcherProjectVersion> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setParameter(
			"patcherProductVersionId", (String)null
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin()) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName", "/patcher/add_project_versions",
					"redirect", themeDisplay.getURLCurrent());
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "create-project-version"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		return LabelItemListBuilder.add(
			() -> _getPatcherProductVersionId() > 0,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"patcherProductVersionId", (String)null
					).buildString());

				labelItem.setDismissible(true);

				PatcherProductVersion patcherProductVersion =
					PatcherProductVersionLocalServiceUtil.
						fetchPatcherProductVersion(
							_getPatcherProductVersionId());

				labelItem.setLabel(patcherProductVersion.getName());
			}
		).build();
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"patcherProductVersionId", _getPatcherProductVersionId()
		).buildString();
	}

	@Override
	public String getSortingURL() {
		return null;
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected List<DropdownItem> getFilterNavigationDropdownItems() {
		return new DropdownItemList() {
			{
				add(
					dropdownItem -> {
						dropdownItem.setActive(
							_getPatcherProductVersionId() == 0);
						dropdownItem.setHref(
							getPortletURL(), "patcherProductVersionId", 0);
						dropdownItem.setLabel(
							LanguageUtil.get(httpServletRequest, "any"));
					});

				for (PatcherProductVersion patcherProductVersion :
						PatcherProductVersionUtil.getPatcherProductVersions()) {

					add(
						dropdownItem -> {
							dropdownItem.setActive(
								_getPatcherProductVersionId() ==
									patcherProductVersion.
										getPatcherProductVersionId());
							dropdownItem.setHref(
								getPortletURL(), "patcherProductVersionId",
								patcherProductVersion.
									getPatcherProductVersionId());
							dropdownItem.setLabel(
								patcherProductVersion.getName());
						});
				}
			}
		};
	}

	private long _getPatcherProductVersionId() {
		if (_patcherProductVersionId != null) {
			return _patcherProductVersionId;
		}

		_patcherProductVersionId = ParamUtil.getLong(
			httpServletRequest, "patcherProductVersionId");

		return _patcherProductVersionId;
	}

	private Long _patcherProductVersionId;

}