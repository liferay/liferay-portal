/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.util;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.audit.AuditMessage;

/**
 * @author Álvaro Saugar
 */
public class AuditConfigurationThreadLocal {

	public static AuditMessage getAuditMessage() {
		return _auditMessage.get();
	}

	public static void removeAuditMessage() {
		_auditMessage.remove();
	}

	public static void setAuditMessage(AuditMessage auditMessage) {
		_auditMessage.set(auditMessage);
	}

	private static final ThreadLocal<AuditMessage> _auditMessage =
		new CentralizedThreadLocal<>(
			AuditConfigurationThreadLocal.class + "._auditMessage");

}