/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.audit;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Michael C. Han
 */
public class AuditRequestContextThreadLocal {

	public static AuditRequestContext getAuditRequestContext() {
		return _auditRequestContext.get();
	}

	public static void removeAuditRequestContext() {
		_auditRequestContext.remove();
	}

	public static SafeCloseable setAuditRequestContextWithSafeCloseable(
		AuditRequestContext auditRequestContext) {

		return _auditRequestContext.setWithSafeCloseable(auditRequestContext);
	}

	private static final CentralizedThreadLocal<AuditRequestContext>
		_auditRequestContext = new CentralizedThreadLocal<>(
			AuditRequestContextThreadLocal.class + "._auditRequestContext",
			AuditRequestContext::new);

}