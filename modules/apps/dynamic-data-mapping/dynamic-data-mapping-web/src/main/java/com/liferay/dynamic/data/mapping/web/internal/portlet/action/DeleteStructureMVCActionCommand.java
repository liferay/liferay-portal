/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING,
		"mvc.command.name=/dynamic_data_mapping/delete_structure"
	},
	service = MVCActionCommand.class
)
public class DeleteStructureMVCActionCommand extends BaseDDMMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] deleteStructureIds = null;

		long structureId = ParamUtil.getLong(actionRequest, "classPK");

		if (structureId > 0) {
			deleteStructureIds = new long[] {structureId};
		}
		else {
			deleteStructureIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "deleteStructureIds"), 0L);
		}

		for (long deleteStructureId : deleteStructureIds) {
			_ddmStructureService.deleteStructure(deleteStructureId);
		}

		setRedirectAttribute(actionRequest);
	}

	@Reference
	private DDMStructureService _ddmStructureService;

}