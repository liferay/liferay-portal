/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.batch.engine.action;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.action.ImportTaskPostAction;
import com.liferay.batch.engine.context.ImportTaskContext;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.headless.portal.instances.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.internal.notifications.PortalInstanceNotificationUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Mariano Álvaro Sáiz
 */
@Component(service = ImportTaskPostAction.class)
public class PortalInstanceImportTaskPostAction
	implements ImportTaskPostAction {

	@Override
	public void run(
		BatchEngineImportTask batchEngineImportTask,
		BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
		ImportTaskContext importTaskContext, Object item,
		Object persistedItem) {

		if (!Objects.equals(
				batchEngineImportTask.getOperation(),
				BatchEngineTaskOperation.CREATE.name()) ||
			!(persistedItem instanceof PortalInstance)) {

			return;
		}

		PortalInstanceNotificationUtil.addPortalInstance(
			batchEngineImportTask.getBatchEngineImportTaskId(), item,
			(PortalInstance)persistedItem);
	}

}