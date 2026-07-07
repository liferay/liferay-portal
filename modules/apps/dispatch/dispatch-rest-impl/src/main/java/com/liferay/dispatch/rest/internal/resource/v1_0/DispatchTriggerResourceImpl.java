/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.rest.internal.resource.v1_0;

import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.rest.dto.v1_0.DispatchTrigger;
import com.liferay.dispatch.rest.internal.dto.v1_0.util.DispatchTriggerUtil;
import com.liferay.dispatch.rest.resource.v1_0.DispatchTriggerResource;
import com.liferay.dispatch.service.DispatchTriggerService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.vulcan.pagination.Page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Nilton Vieira
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/dispatch-trigger.properties",
	scope = ServiceScope.PROTOTYPE, service = DispatchTriggerResource.class
)
public class DispatchTriggerResourceImpl
	extends BaseDispatchTriggerResourceImpl {

	@Override
	public Page<DispatchTrigger> getDispatchTriggersPage() throws Exception {
		return Page.of(
			transform(
				_dispatchTriggerService.getDispatchTriggers(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				dispatchTrigger -> DispatchTriggerUtil.toDispatchTrigger(
					dispatchTrigger)));
	}

	@Override
	public DispatchTrigger postDispatchTrigger(DispatchTrigger dispatchTrigger)
		throws Exception {

		return DispatchTriggerUtil.toDispatchTrigger(
			_dispatchTriggerService.addDispatchTrigger(
				dispatchTrigger.getExternalReferenceCode(),
				contextUser.getUserId(),
				dispatchTrigger.getDispatchTaskExecutorType(),
				DispatchTriggerUtil.toSettingsUnicodeProperties(
					dispatchTrigger.getDispatchTaskSettings()),
				dispatchTrigger.getName()));
	}

	@Override
	public void postDispatchTriggerRun(Long dispatchTriggerId)
		throws Exception {

		_dispatchTriggerModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(), dispatchTriggerId,
			ActionKeys.UPDATE);

		Message message = new Message();

		com.liferay.dispatch.model.DispatchTrigger
			serviceBuilderDispatchTrigger =
				_dispatchTriggerService.getDispatchTrigger(dispatchTriggerId);

		message.put("companyId", serviceBuilderDispatchTrigger.getCompanyId());

		message.setPayload(
			JSONUtil.put(
				"dispatchTriggerId", dispatchTriggerId
			).toString());

		_messageBus.sendMessage(
			DispatchConstants.EXECUTOR_DESTINATION_NAME, message);
	}

	@Reference(
		target = "(model.class.name=com.liferay.dispatch.model.DispatchTrigger)"
	)
	private ModelResourcePermission<com.liferay.dispatch.model.DispatchTrigger>
		_dispatchTriggerModelResourcePermission;

	@Reference
	private DispatchTriggerService _dispatchTriggerService;

	@Reference
	private MessageBus _messageBus;

}