/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.action.executor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

/**
 * @author Guilherme Camacho
 */
public abstract class BaseObjectActionExecutor implements ObjectActionExecutor {

	@Override
	public void execute(
			long companyId, long objectActionId,
			UnicodeProperties parametersUnicodeProperties,
			JSONObject payloadJSONObject, long userId)
		throws Exception {

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				doExecute(
					companyId, objectActionId, parametersUnicodeProperties,
					payloadJSONObject, userId);

				return null;
			});
	}

	protected abstract void doExecute(
			long companyId, long objectActionId,
			UnicodeProperties parametersUnicodeProperties,
			JSONObject payloadJSONObject, long userId)
		throws Exception;

}