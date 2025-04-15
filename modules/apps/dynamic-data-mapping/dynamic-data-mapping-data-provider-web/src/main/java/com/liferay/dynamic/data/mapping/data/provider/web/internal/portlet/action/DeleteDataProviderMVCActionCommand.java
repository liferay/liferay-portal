/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
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
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_DATA_PROVIDER,
		"mvc.command.name=/dynamic_data_mapping_data_provider/delete_data_provider"
	},
	service = MVCActionCommand.class
)
public class DeleteDataProviderMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] deleteDataProviderInstanceIds = null;

		long dataProviderInstanceId = ParamUtil.getLong(
			actionRequest, "dataProviderInstanceId");

		if (dataProviderInstanceId > 0) {
			deleteDataProviderInstanceIds = new long[] {dataProviderInstanceId};
		}
		else {
			deleteDataProviderInstanceIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteDataProviderInstanceIds"),
				0L);
		}

		for (long deleteDataProviderInstanceId :
				deleteDataProviderInstanceIds) {

			_deleteDataProviderInstance(deleteDataProviderInstanceId);
		}
	}

	private void _deleteDataProviderInstance(long dataProviderInstanceId)
		throws Exception {

		_ddmDataProviderInstanceService.deleteDataProviderInstance(
			dataProviderInstanceId);
	}

	@Reference
	private DDMDataProviderInstanceService _ddmDataProviderInstanceService;

}