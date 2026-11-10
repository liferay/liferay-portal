/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type;

import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(
	configurationPid = "com.liferay.notification.internal.configuration.FunctionNotificationTypeConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class FunctionNotificationTypeFactory {

	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		String externalReferenceCode =
			ConfigurationFactoryUtil.getExternalReferenceCode(properties);

		_componentInstance = _componentFactory.newInstance(
			HashMapDictionaryBuilder.<String, Object>put(
				"notification.type", "function#" + externalReferenceCode
			).putAll(
				properties
			).remove(
				Constants.SERVICE_PID
			).build());
	}

	@Deactivate
	protected void deactivate() {
		if (_componentInstance != null) {
			_componentInstance.dispose();
		}
	}

	@Reference(
		target = "(component.factory=com.liferay.notification.internal.type.FunctionNotificationType)"
	)
	private ComponentFactory<FunctionNotificationType> _componentFactory;

	private ComponentInstance<FunctionNotificationType> _componentInstance;

}