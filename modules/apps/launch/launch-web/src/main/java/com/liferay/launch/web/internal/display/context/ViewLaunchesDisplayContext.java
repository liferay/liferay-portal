/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.frontend.data.set.model.FDSSortItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.launch.web.internal.constants.LaunchFDSNames;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class ViewLaunchesDisplayContext {

	public ViewLaunchesDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public String getAPIURL() {
		return "/o/launch-sets";
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"unscheduleLaunchURL", _getUnscheduleLaunchURL()
		).put(
			"viewLaunchURL", _getViewLaunchURL()
		).build();
	}

	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "new-launch"))
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return List.of(
			new FDSActionDropdownItem(
				_getViewLaunchURL(), "view", "view",
				LanguageUtil.get(_httpServletRequest, "view"), "get", null,
				null),
			new FDSActionDropdownItem(
				"#", "date-time", "unschedule",
				LanguageUtil.get(_httpServletRequest, "unschedule"), "get",
				"update", null));
	}

	public String getFDSName() {
		return LaunchFDSNames.LAUNCH_SETS;
	}

	public FDSSortItemList getFDSSortItemList() {
		return FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setDirection(
				"asc"
			).setKey(
				"name"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "name")
			).build()
		).add(
			FDSSortItemBuilder.setDirection(
				"desc"
			).setKey(
				"dateModified"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "last-modified")
			).build()
		).build();
	}

	private String _getUnscheduleLaunchURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/launch/unschedule_launch"
		).setParameter(
			"launchSetId", "{id}"
		).buildString();
	}

	private String _getViewLaunchURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/launch/view_launch"
		).setParameter(
			"launchSetId", "{id}"
		).setParameter(
			"launchSetName", "{name}"
		).buildString();
	}

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;

}