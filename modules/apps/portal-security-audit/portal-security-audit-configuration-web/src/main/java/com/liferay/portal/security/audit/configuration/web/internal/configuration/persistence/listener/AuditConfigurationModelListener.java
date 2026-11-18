/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationThreadLocal;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Álvaro Saugar
 */
@Component(
	property = "model.class.name=com.liferay.portal.security.audit.configuration.AuditConfiguration",
	service = ConfigurationModelListener.class
)
public class AuditConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onAfterSave(String pid, Dictionary<String, Object> properties) {
		AuditMessage auditMessage =
			AuditConfigurationThreadLocal.getAuditMessage();

		if (auditMessage == null) {
			return;
		}

		AuditConfigurationThreadLocal.removeAuditMessage();

		try {
			_auditRouter.route(auditMessage);
		}
		catch (AuditException auditException) {
			_log.error(auditException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuditConfigurationModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

}