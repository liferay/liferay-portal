/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util;

import com.liferay.ai.hub.internal.mcp.tool.provider.MCPToolProviderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.function.Consumer;

/**
 * @author Feliphe Marinho
 */
public class OnErrorConsumerUtil {

	public static Consumer<Throwable> create(String sseEventSinkKey) {
		return throwable -> {
			MCPToolProviderUtil.close(sseEventSinkKey);

			_log.error(throwable);
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OnErrorConsumerUtil.class);

}