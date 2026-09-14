/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.batch.engine.action;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.action.ImportTaskPostAction;
import com.liferay.batch.engine.context.ImportTaskContext;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.portal.vulcan.extension.EntityExtensionHandler;
import com.liferay.portal.vulcan.extension.EntityExtensionThreadLocal;
import com.liferay.portal.vulcan.extension.ExtensionProviderRegistry;
import com.liferay.portal.vulcan.extension.util.ExtensionUtil;
import com.liferay.portal.vulcan.internal.batch.engine.util.BatchEngineImportTaskUtil;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matija Petanjek
 */
@Component(service = ImportTaskPostAction.class)
public class EntityExtensionImportTaskPostAction
	implements ImportTaskPostAction {

	@Override
	public void run(
			BatchEngineImportTask batchEngineImportTask,
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
			ImportTaskContext importTaskContext, Object item,
			Object persistedItem)
		throws Exception {

		EntityExtensionHandler entityExtensionHandler =
			ExtensionUtil.getEntityExtensionHandler(
				batchEngineImportTask.getClassName(),
				batchEngineImportTask.getCompanyId(),
				_extensionProviderRegistry);

		if (entityExtensionHandler == null) {
			return;
		}

		try {
			Map<String, Serializable> extendedProperties =
				EntityExtensionThreadLocal.getExtendedProperties();

			if (extendedProperties == null) {
				return;
			}

			entityExtensionHandler.setExtendedProperties(
				batchEngineImportTask.getCompanyId(),
				batchEngineImportTask.getUserId(), persistedItem,
				extendedProperties,
				BatchEngineImportTaskUtil.isPartialUpdate(
					batchEngineImportTask));
		}
		finally {
			EntityExtensionThreadLocal.clearExtendedProperties();
		}
	}

	@Reference
	private ExtensionProviderRegistry _extensionProviderRegistry;

}