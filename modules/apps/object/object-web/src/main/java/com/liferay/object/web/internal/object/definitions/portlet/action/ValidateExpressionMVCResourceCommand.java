/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.dynamic.data.mapping.expression.CreateExpressionRequest;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionException;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/validate_expression"
	},
	service = MVCResourceCommand.class
)
public class ValidateExpressionMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		boolean valid = true;

		try {
			_ddmExpressionFactory.createExpression(
				CreateExpressionRequest.Builder.newBuilder(
					ParamUtil.getString(resourceRequest, "expression")
				).build());
		}
		catch (DDMExpressionException ddmExpressionException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ddmExpressionException);
			}

			valid = false;
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, JSONUtil.put("valid", valid));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ValidateExpressionMVCResourceCommand.class);

	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;

}