/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/delete_ddm_template"
	},
	service = MVCActionCommand.class
)
public class DeleteDDMTemplateMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] deleteDDMTemplateIds = null;

		long ddmTemplateId = ParamUtil.getLong(actionRequest, "ddmTemplateId");

		if (ddmTemplateId > 0) {
			deleteDDMTemplateIds = new long[] {ddmTemplateId};
		}
		else {
			deleteDDMTemplateIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		for (long deleteDDMTemplateId : deleteDDMTemplateIds) {
			_ddmTemplateService.deleteTemplate(deleteDDMTemplateId);
		}
	}

	@Reference
	private DDMTemplateService _ddmTemplateService;

}