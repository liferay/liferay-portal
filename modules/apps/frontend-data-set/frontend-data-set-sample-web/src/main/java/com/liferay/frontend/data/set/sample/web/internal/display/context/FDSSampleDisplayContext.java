/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.frontend.data.set.model.FDSSortItemListBuilder;
import com.liferay.frontend.data.set.sample.web.internal.display.context.helper.FDSRequestHelper;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Arrays;
import java.util.List;

import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Javier Gamarra
 * @author Javier de Arcos
 */
public class FDSSampleDisplayContext {

	public FDSSampleDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_renderResponse = renderResponse;

		_fdsRequestHelper = new FDSRequestHelper(httpServletRequest);
	}

	public String getAPIURL() {
		return "/o/c/fdssamples?sort=title:asc";
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				"#", "document", "sampleBulkAction",
				LanguageUtil.get(_fdsRequestHelper.getRequest(), "label"), null,
				null, null));
	}

	public CreationMenu getCreationMenu() throws Exception {
		return new CreationMenu();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		HttpServletRequest httpServletRequest = _fdsRequestHelper.getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		String portalURL = company.getPortalURL(
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		FDSActionDropdownItem sidePanel1FDSActionDropdownItem =
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/side_panel/empty"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"rectangle-split", "open-side-panel-no-title",
				"Side Panel With Action Title", null, null, "sidePanel");

		sidePanel1FDSActionDropdownItem.putData("disableHeader", false);
		sidePanel1FDSActionDropdownItem.putData(
			"title", "Side Panel Title Provided by Action");

		FDSActionDropdownItem sidePanel2FDSActionDropdownItem =
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/side_panel/full"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"rectangle-split", "open-side-panel-title",
				"Side Panel With Action and Content Title", null, null,
				"sidePanel");

		sidePanel2FDSActionDropdownItem.putData("disableHeader", false);
		sidePanel2FDSActionDropdownItem.putData(
			"title", "Side Panel Title Provided by Action");

		FDSActionDropdownItem sidePanel3FDSActionDropdownItem =
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/side_panel/full"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"rectangle-split", "open-side-panel-title",
				"Side Panel With Content Title", null, null, "sidePanel");

		sidePanel3FDSActionDropdownItem.putData("disableHeader", true);

		return Arrays.asList(
			new FDSActionDropdownItem(
				null, "view", "sampleMessage", "Sample View", null, null, null),
			new FDSActionDropdownItem(
				"#test-pencil", "pencil", "sampleEditMessage", "Sample Edit",
				null, null, null),
			new FDSActionDropdownItem(
				"#test-delete", "times-circle", "sampleDeleteMessage",
				"Sample Delete", null, null, null),
			new FDSActionDropdownItem(
				"#test-copy", "copy", "sampleMoveFolderMessage", "Sample Copy",
				null, null, null),
			new FDSActionDropdownItem(
				portalURL, "truck", "asyncSuccess", "Async Success", "get",
				null, "async"),
			new FDSActionDropdownItem(
				"http://localhost", "times-circle",
				"asyncErrorConnectionRefused", "Async Connection Refused",
				"get", null, "async"),
			sidePanel1FDSActionDropdownItem, sidePanel2FDSActionDropdownItem,
			sidePanel3FDSActionDropdownItem,
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/side_panel/empty"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"rectangle-split", "open-side-panel-without-title",
				"Side Panel With No Title", null, null, "sidePanel"),
			new FDSActionDropdownItem(
				portalURL + "/abc", "staging", "asyncErrorResourceNotFound",
				"Async Resource Not Found", "get", null, "async"),
			new FDSActionDropdownItem(
				null, "reload", "reload", "Reload Data", null, null, null),
			new FDSActionDropdownItem(
				null, "rectangle-split", "openSidePanel", "Open Side Panel",
				null, null, null));
	}

	public FDSSortItemList getFDSSortItemList() {
		return FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setDirection(
				"asc"
			).setKey(
				"title"
			).build()
		).build();
	}

	private final FDSRequestHelper _fdsRequestHelper;
	private final RenderResponse _renderResponse;

}