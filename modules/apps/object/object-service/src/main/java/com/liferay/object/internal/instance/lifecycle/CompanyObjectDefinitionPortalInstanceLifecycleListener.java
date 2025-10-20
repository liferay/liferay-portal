/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.instance.lifecycle;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PortalInstances;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author István András Dézsi
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class CompanyObjectDefinitionPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstancePreunregistered(Company company)
		throws Exception {

		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		List<ObjectDefinition> objectDefinitions =
			_objectDefinitionLocalService.getObjectDefinitions(
				company.getCompanyId(), WorkflowConstants.STATUS_APPROVED);

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			_objectDefinitionLocalService.undeployObjectDefinition(
				objectDefinition);
		}
	}

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (!PropsValues.DATABASE_PARTITION_ENABLED ||
			!PortalInstances.isCompanyInCopyProcess()) {

			return;
		}

		List<ObjectDefinition> objectDefinitions =
			_objectDefinitionLocalService.getObjectDefinitions(
				company.getCompanyId(), WorkflowConstants.STATUS_APPROVED);

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			if (objectDefinition.isActive()) {
				_objectDefinitionLocalService.deployObjectDefinition(
					objectDefinition);
			}
			else {
				_objectDefinitionLocalService.deployInactiveObjectDefinition(
					objectDefinition);
			}
		}
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}