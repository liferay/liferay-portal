/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/get_ddm_template"
	},
	service = MVCResourceCommand.class
)
public class GetDDMTemplateMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long ddmTemplateId = ParamUtil.getLong(
			resourceRequest, "ddmTemplateId");

		DDMTemplate ddmTemplate = _ddmTemplateService.getTemplate(
			ddmTemplateId);

		String script = ddmTemplate.getScript();

		String contentType = null;

		if (Objects.equals(
				ddmTemplate.getType(),
				DDMTemplateConstants.TEMPLATE_TYPE_FORM)) {

			contentType = ContentTypes.APPLICATION_JSON;
		}
		else {
			contentType = ContentTypes.TEXT_PLAIN_UTF8;
		}

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, null, script.getBytes(),
			contentType);
	}

	@Reference
	private DDMTemplateService _ddmTemplateService;

}