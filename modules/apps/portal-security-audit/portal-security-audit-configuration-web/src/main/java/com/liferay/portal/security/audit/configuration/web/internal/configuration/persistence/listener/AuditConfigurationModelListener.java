/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.configuration.web.internal.util.AuditConfigurationFormThreadLocal;

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
		Boolean previousPseudonymizationEnabled =
			AuditConfigurationFormThreadLocal.
				isPreviousPseudonymizationEnabled();

		AuditConfigurationFormThreadLocal.
			removePreviousPseudonymizationEnabled();

		if (previousPseudonymizationEnabled == null) {
			return;
		}

		boolean pseudonymizationEnabled = GetterUtil.getBoolean(
			properties.get("pseudonymizationEnabled"),
			previousPseudonymizationEnabled);

		if (pseudonymizationEnabled == previousPseudonymizationEnabled) {
			return;
		}

		try {
			long companyId = GetterUtil.getLong(
				properties.get("companyId"), CompanyThreadLocal.getCompanyId());

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			User user = permissionChecker.getUser();

			_auditRouter.route(
				new AuditMessage(
					companyId, user.getUserId(), user.getFullName(),
					JSONUtil.put(
						"attributes",
						JSONUtil.putAll(
							JSONUtil.put(
								"name", "pseudonymizationEnabled"
							).put(
								"newValue", pseudonymizationEnabled
							).put(
								"oldValue", previousPseudonymizationEnabled
							))),
					AuditConfiguration.class.getName(),
					String.valueOf(companyId), "AUDIT_CONFIG_CHANGE", null));
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