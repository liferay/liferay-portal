/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.util;

import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Jorge García Jiménez
 */
public class DynamicRegistrationAuditMessageUtil {

	public static void routeAuditMessage(AuditMessage auditMessage) {
		if (auditMessage == null) {
			return;
		}

		try {
			AuditRouterUtil.route(auditMessage);
		}
		catch (AuditException auditException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", auditException);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DynamicRegistrationAuditMessageUtil.class);

}