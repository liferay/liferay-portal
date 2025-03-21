/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.servlet.taglib.DynamicIncludeUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Basto
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/admin/edit_element_set"
	},
	service = MVCRenderCommand.class
)
public class EditElementSetMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		DynamicIncludeUtil.include(
			portal.getHttpServletRequest(renderRequest),
			portal.getHttpServletResponse(renderResponse),
			"com.liferay.dynamic.data.mapping.form.web#" +
				"EditElementSetMVCRenderCommand#render",
			true);

		long structureId = ParamUtil.getLong(renderRequest, "structureId");

		if (structureId > 0) {
			CTTimelineUtil.setCTTimelineKeys(
				renderRequest, DDMStructure.class, structureId);
		}

		return "/admin/edit_element_set.jsp";
	}

	@Reference
	protected Portal portal;

}