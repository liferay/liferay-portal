/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.template.constants.TemplatePortletKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TemplatePortletKeys.TEMPLATE,
		"mvc.command.name=/template/get_ddm_template"
	},
	service = MVCResourceCommand.class
)
public class GetDDMTemplateMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			long ddmTemplateId = ParamUtil.getLong(
				resourceRequest, "ddmTemplateId");

			DDMTemplate ddmTemplate = _ddmTemplateService.getTemplate(
				ddmTemplateId);

			String script = ddmTemplate.getScript();

			PortletResponseUtil.sendFile(
				resourceRequest, resourceResponse, null, script.getBytes(),
				ContentTypes.TEXT_PLAIN_UTF8);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		return false;
	}

	@Reference
	private DDMTemplateService _ddmTemplateService;

}