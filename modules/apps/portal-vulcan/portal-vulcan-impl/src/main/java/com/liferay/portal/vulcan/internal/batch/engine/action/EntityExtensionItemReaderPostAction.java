/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.batch.engine.action;

import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.action.ItemReaderPostAction;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.extension.EntityExtensionHandler;
import com.liferay.portal.vulcan.extension.ExtensionProviderRegistry;
import com.liferay.portal.vulcan.extension.util.ExtensionUtil;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Correa
 */
@Component(service = ItemReaderPostAction.class)
public class EntityExtensionItemReaderPostAction
	implements ItemReaderPostAction {

	@Override
	public void run(
			BatchEngineImportTask batchEngineImportTask,
			Map<String, Serializable> extendedProperties, Object item)
		throws Exception {

		EntityExtensionHandler entityExtensionHandler =
			ExtensionUtil.getEntityExtensionHandler(
				batchEngineImportTask.getClassName(),
				batchEngineImportTask.getCompanyId(),
				_extensionProviderRegistry);

		if (entityExtensionHandler == null) {
			if (MapUtil.isNotEmpty(extendedProperties)) {
				throw new NoSuchFieldException(
					String.valueOf(extendedProperties.keySet()));
			}

			return;
		}

		entityExtensionHandler.validate(
			batchEngineImportTask.getCompanyId(), extendedProperties,
			_isPartialUpdate(batchEngineImportTask));

		ExtensionUtil.setExtendedProperties(item, extendedProperties);
	}

	private boolean _isPartialUpdate(
		BatchEngineImportTask batchEngineImportTask) {

		BatchEngineTaskOperation batchEngineTaskOperation =
			BatchEngineTaskOperation.valueOf(
				batchEngineImportTask.getOperation());

		String createStrategy = batchEngineImportTask.getParameterValue(
			"createStrategy");
		String updateStrategy = batchEngineImportTask.getParameterValue(
			"updateStrategy");

		if ((batchEngineTaskOperation == BatchEngineTaskOperation.CREATE) &&
			StringUtil.equals(createStrategy, "UPSERT") &&
			StringUtil.equals(updateStrategy, "PARTIAL_UPDATE")) {

			return true;
		}

		if ((batchEngineTaskOperation == BatchEngineTaskOperation.UPDATE) &&
			StringUtil.equals(updateStrategy, "PARTIAL_UPDATE")) {

			return true;
		}

		return false;
	}

	@Reference
	private ExtensionProviderRegistry _extensionProviderRegistry;

}