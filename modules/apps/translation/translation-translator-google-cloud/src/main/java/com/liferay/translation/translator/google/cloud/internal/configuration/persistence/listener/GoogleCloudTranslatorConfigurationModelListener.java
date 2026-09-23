/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.translator.google.cloud.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.translation.translator.google.cloud.internal.configuration.GoogleCloudTranslatorConfiguration;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.translation.translator.google.cloud.internal.configuration.GoogleCloudTranslatorConfiguration",
	service = ConfigurationModelListener.class
)
public class GoogleCloudTranslatorConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		boolean enabled = GetterUtil.getBoolean(properties.get("enabled"));

		String serviceAccountPrivateKey = GetterUtil.getString(
			properties.get("serviceAccountPrivateKey"));

		long companyId = GetterUtil.getLong(
			properties.get(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey()),
			CompanyConstants.SYSTEM);

		serviceAccountPrivateKey = _secretResolver.resolve(
			companyId, serviceAccountPrivateKey);

		if (enabled && !_isValid(serviceAccountPrivateKey)) {
			throw new ConfigurationModelListenerException(
				_language.get(
					LocaleThreadLocal.getThemeDisplayLocale(),
					"the-service-account-private-key-must-be-in-json-format"),
				GoogleCloudTranslatorConfiguration.class, getClass(),
				properties);
		}
	}

	private boolean _isValid(String serviceAccountPrivateKey) {
		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				serviceAccountPrivateKey);

			if (jsonObject.length() > 0) {
				return true;
			}

			return false;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GoogleCloudTranslatorConfigurationModelListener.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private SecretResolver _secretResolver;

}