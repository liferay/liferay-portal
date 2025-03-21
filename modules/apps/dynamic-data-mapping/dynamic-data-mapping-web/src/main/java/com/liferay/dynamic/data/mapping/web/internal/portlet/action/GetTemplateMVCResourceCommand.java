/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING,
		"mvc.command.name=/dynamic_data_mapping/get_template"
	},
	service = MVCResourceCommand.class
)
public class GetTemplateMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long templateId = ParamUtil.getLong(resourceRequest, "templateId");

		DDMTemplate template = _ddmTemplateService.getTemplate(templateId);

		String script = template.getScript();

		String contentType = ContentTypes.TEXT_PLAIN_UTF8;

		String type = template.getType();

		if (type.equals(DDMTemplateConstants.TEMPLATE_TYPE_FORM)) {
			contentType = ContentTypes.APPLICATION_JSON;
		}

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, null, script.getBytes(),
			contentType);
	}

	@Reference
	private DDMTemplateService _ddmTemplateService;

}