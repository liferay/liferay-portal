/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.web.internal.configuration.persistence.listener;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.multi.factor.authentication.web.internal.system.configuration.MFASystemConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marta Medio
 */
@Component(
	property = "model.class.name=com.liferay.multi.factor.authentication.email.otp.configuration.MFAEmailOTPConfiguration",
	service = ConfigurationModelListener.class
)
public class MFAEmailOTPConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onAfterSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		try {
			MFASystemConfiguration mfaSystemConfiguration =
				_configurationProvider.getSystemConfiguration(
					MFASystemConfiguration.class);

			if (!mfaSystemConfiguration.disableGlobally() ||
				!GetterUtil.getBoolean(properties.get("enabled")) ||
				!ClusterInvokeThreadLocal.isEnabled()) {

				return;
			}

			long userId = PrincipalThreadLocal.getUserId();

			_userNotificationEventLocalService.sendUserNotificationEvents(
				userId, ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
				UserNotificationDeliveryConstants.TYPE_WEBSITE, false,
				JSONUtil.put(
					"classPK", ConfigurationAdminPortletKeys.INSTANCE_SETTINGS
				).put(
					"mfaDisableGlobally",
					mfaSystemConfiguration.disableGlobally()
				).put(
					"userId", userId
				));
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to get multi-factor authentication configuration",
				configurationException);

			throw new ConfigurationModelListenerException(
				configurationException.getMessage(),
				ConfigurationException.class,
				MFASystemConfigurationModelListener.class, properties);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to send user notification events", portalException);

			throw new ConfigurationModelListenerException(
				portalException.getMessage(), PortalException.class,
				MFASystemConfigurationModelListener.class, properties);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MFAEmailOTPConfigurationModelListener.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}