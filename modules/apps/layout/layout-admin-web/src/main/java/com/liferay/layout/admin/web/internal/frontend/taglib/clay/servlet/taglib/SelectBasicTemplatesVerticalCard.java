/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

/**
 * @author Eudaldo Alonso
 */
public class SelectBasicTemplatesVerticalCard
	extends BaseMasterLayoutVerticalCard {

	public SelectBasicTemplatesVerticalCard(
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		super(layoutPageTemplateEntry, renderRequest, renderResponse);
	}

	@Override
	protected String getAddLayoutURL() {
		long selPlid = ParamUtil.getLong(httpServletRequest, "selPlid");

		PortletURL addLayoutURL = PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCRenderCommandName(
			"/layout_admin/add_layout"
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "redirect")
		).setParameter(
			"masterLayoutPlid", layoutPageTemplateEntry.getPlid()
		).setParameter(
			"privateLayout",
			ParamUtil.getBoolean(httpServletRequest, "privateLayout")
		).setParameter(
			"selPlid", selPlid
		).setParameter(
			"type", LayoutConstants.TYPE_CONTENT
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildPortletURL();

		if (selPlid != LayoutConstants.DEFAULT_PLID) {
			Layout layout = LayoutLocalServiceUtil.fetchLayout(selPlid);

			if ((layout != null) && layout.isTypeEmpty()) {
				addLayoutURL.setParameter(
					"emptyLayout",
					String.valueOf(
						ParamUtil.getBoolean(
							httpServletRequest, "emptyLayout")));
				addLayoutURL.setParameter(
					"externalReferenceCode", layout.getExternalReferenceCode());
			}
		}

		return addLayoutURL.toString();
	}

}