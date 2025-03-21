/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.display.context;

import com.liferay.dispatch.web.internal.display.context.helper.DispatchRequestHelper;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Matija Petanjek
 */
public abstract class BaseDisplayContext {

	public BaseDisplayContext(RenderRequest renderRequest) {
		dispatchRequestHelper = new DispatchRequestHelper(renderRequest);
	}

	public List<NavigationItem> getNavigationItems() {
		HttpServletRequest httpServletRequest =
			dispatchRequestHelper.getRequest();

		LiferayPortletResponse liferayPortletResponse =
			dispatchRequestHelper.getLiferayPortletResponse();

		String tabs1 = ParamUtil.getString(
			httpServletRequest, "tabs1", "dispatch-trigger");

		return NavigationItemList.of(
			NavigationItemBuilder.setActive(
				tabs1.equals("dispatch-trigger")
			).setHref(
				liferayPortletResponse.createRenderURL(), "tabs1",
				"dispatch-trigger", "mvcRenderCommandName",
				"/dispatch/view_dispatch_trigger"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "dispatch-triggers")
			).build(),
			NavigationItemBuilder.setActive(
				tabs1.equals("scheduler-response")
			).setHref(
				liferayPortletResponse.createRenderURL(), "tabs1",
				"scheduler-response", "mvcRenderCommandName",
				"/dispatch/edit_scheduler_response"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "scheduled-jobs")
			).build());
	}

	protected final DispatchRequestHelper dispatchRequestHelper;

}