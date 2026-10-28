/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.model.listener;

import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.internal.notifications.PortalInstanceNotificationUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.CompanyLocalService;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mariano Álvaro Sáiz
 */
@Component(service = ModelListener.class)
public class BatchEngineImportTaskModelListener
	extends BaseModelListener<BatchEngineImportTask> {

	@Override
	public void onAfterUpdate(
		BatchEngineImportTask originalBatchEngineImportTask,
		BatchEngineImportTask batchEngineImportTask) {

		if (!Objects.equals(
				batchEngineImportTask.getClassName(),
				PortalInstance.class.getName()) ||
			(!Objects.equals(
				batchEngineImportTask.getExecuteStatus(),
				BatchEngineTaskExecuteStatus.COMPLETED.name()) &&
			 !Objects.equals(
				 batchEngineImportTask.getExecuteStatus(),
				 BatchEngineTaskExecuteStatus.FAILED.name()))) {

			return;
		}

		for (PortalInstance portalInstance :
				PortalInstanceNotificationUtil.removePortalInstances(
					batchEngineImportTask.getBatchEngineImportTaskId())) {

			Company company = _companyLocalService.fetchCompany(
				portalInstance.getCompanyId());

			if (company == null) {
				continue;
			}

			PortalInstanceNotificationUtil.sendAddUserNotificationEvent(
				PortalInstancePool.getWebId(company.getCompanyId()) != null,
				company.getWebId(), batchEngineImportTask.getUserId());
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

}