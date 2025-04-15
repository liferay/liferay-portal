/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TemplatePortletKeys.TEMPLATE,
		"mvc.command.name=/template/delete_template_entry"
	},
	service = MVCActionCommand.class
)
public class DeleteTemplateEntryMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] templateEntryIds = null;

		long templateEntryId = ParamUtil.getLong(
			actionRequest, "templateEntryId");

		if (templateEntryId > 0) {
			templateEntryIds = new long[] {templateEntryId};
		}
		else {
			templateEntryIds = ParamUtil.getLongValues(actionRequest, "rowIds");
		}

		for (long deleteTemplateEntryId : templateEntryIds) {
			TemplateEntry templateEntry =
				_templateEntryLocalService.deleteTemplateEntry(
					deleteTemplateEntryId);

			_ddmTemplateLocalService.deleteTemplate(
				templateEntry.getDDMTemplateId());
		}
	}

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private TemplateEntryLocalService _templateEntryLocalService;

}