/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.service.base.KaleoTaskInstanceTokenServiceBaseImpl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Victor Kammerer
 */
@Component(
	property = {
		"json.web.service.context.name=kaleo",
		"json.web.service.context.path=KaleoTaskInstanceToken"
	},
	service = AopService.class
)
public class KaleoTaskInstanceTokenServiceImpl
	extends KaleoTaskInstanceTokenServiceBaseImpl {

	@Override
	public KaleoTaskInstanceToken getKaleoTaskInstanceToken(long workflowTaskId)
		throws PortalException {

		_kaleoTaskInstanceTokenModelResourcePermission.check(
			getPermissionChecker(), workflowTaskId, null);

		return kaleoTaskInstanceTokenPersistence.findByPrimaryKey(
			workflowTaskId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken)"
	)
	private ModelResourcePermission<KaleoTaskInstanceToken>
		_kaleoTaskInstanceTokenModelResourcePermission;

}