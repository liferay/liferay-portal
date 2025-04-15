/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.knowledge.base.service.KBTemplateService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"mvc.command.name=/knowledge_base/update_kb_template"
	},
	service = MVCActionCommand.class
)
public class UpdateKBTemplateMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		String title = ParamUtil.getString(actionRequest, "title");
		String content = ParamUtil.getString(actionRequest, "content");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			KBTemplate.class.getName(), actionRequest);

		if (cmd.equals(Constants.ADD)) {
			_kbTemplateService.addKBTemplate(
				_portal.getPortletId(actionRequest), title, content,
				serviceContext);
		}
		else if (cmd.equals(Constants.UPDATE)) {
			long kbTemplateId = ParamUtil.getLong(
				actionRequest, "kbTemplateId");

			_kbTemplateService.updateKBTemplate(
				kbTemplateId, title, content, serviceContext);
		}
	}

	@Reference
	private KBTemplateService _kbTemplateService;

	@Reference
	private Portal _portal;

}