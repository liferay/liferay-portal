/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

/**
 * @author Jürgen Kappler
 */
public class SelectLayoutMasterLayoutVerticalCard
	extends BaseMasterLayoutVerticalCard {

	public SelectLayoutMasterLayoutVerticalCard(
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		super(layoutPageTemplateEntry, renderRequest, renderResponse);
	}

	@Override
	protected String getAddLayoutURL() {
		return PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCRenderCommandName(
			"/layout_admin/add_layout"
		).setRedirect(
			ParamUtil.getString(httpServletRequest, "redirect")
		).setParameter(
			"groupId", ParamUtil.getLong(httpServletRequest, "groupId")
		).setParameter(
			"masterLayoutPlid", layoutPageTemplateEntry.getPlid()
		).setParameter(
			"privateLayout",
			ParamUtil.getBoolean(httpServletRequest, "privateLayout")
		).setParameter(
			"selPlid", ParamUtil.getLong(httpServletRequest, "selPlid")
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

}