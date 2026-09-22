/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.instance.lifecycle;

import com.liferay.notification.type.NotificationType;
import com.liferay.osb.faro.web.internal.constants.FaroNotificationTemplateConstants;
import com.liferay.osb.faro.web.internal.helper.FaroNotificationTemplateProvisioner;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Caio Pinheiro
 */
@Component(
	property = "service.ranking:Integer=" + Integer.MIN_VALUE,
	service = PortalInstanceLifecycleListener.class
)
public class AddFaroNotificationTemplatesPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		long companyId = company.getCompanyId();

		Map<String, Map<String, String>> defaultNotificationTemplates =
			FaroNotificationTemplateConstants.getDefaultNotificationTemplates();

		for (String externalReferenceCode :
				defaultNotificationTemplates.keySet()) {

			try {
				_faroNotificationTemplateProvisioner.verifyNotificationTemplate(
					companyId, externalReferenceCode);
			}
			catch (Exception exception) {
				_log.error(
					String.format(
						"Unable to provision notification template %s for " +
							"company %s",
						externalReferenceCode, companyId),
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddFaroNotificationTemplatesPortalInstanceLifecycleListener.class);

	@Reference(
		target = "(component.name=com.liferay.notification.internal.type.EmailNotificationType)"
	)
	private NotificationType _emailNotificationType;

	@Reference
	private FaroNotificationTemplateProvisioner
		_faroNotificationTemplateProvisioner;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}