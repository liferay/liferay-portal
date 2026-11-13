/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.action;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.action.ImportTaskPostAction;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.context.ImportTaskContext;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;

/**
 * @author Vendel Toreki
 * @author Petteri Karttunen
 */
@Component(service = ImportTaskPostAction.class)
public class ItemImportTaskPostAction implements ImportTaskPostAction {

	@Override
	public void run(
			BatchEngineImportTask batchEngineImportTask,
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
			ImportTaskContext importTaskContext, Object item,
			Object persistedItem)
		throws Exception {

		if (!StringUtil.equals(
				batchEngineImportTask.getParameterValue(
					"importCreatorStrategy"),
				BatchEngineImportTaskConstants.
					IMPORT_CREATOR_STRATEGY_KEEP_CREATOR)) {

			return;
		}

		if (importTaskContext.getOriginalUser() != null) {
			User user = importTaskContext.getOriginalUser();

			PrincipalThreadLocal.setName(user.getUserId());
			batchEngineTaskItemDelegate.setContextUser(user);

			importTaskContext.setOriginalUser(null);
		}
	}

}