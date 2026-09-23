/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.configuration.persistence.listener;

import com.liferay.oauth2.provider.rest.internal.configuration.OAuth2InAssertionConfiguration;
import com.liferay.oauth2.provider.util.OAuth2JWKValidatorUtil;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "model.class.name=com.liferay.oauth2.provider.rest.internal.configuration.OAuth2InAssertionConfiguration",
	service = ConfigurationModelListener.class
)
public class OAuth2InAssertionConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		String jwks = GetterUtil.getString(
			properties.get("oauth2.in.assertion.signature.json.web.key.set"));

		if (Validator.isNull(jwks)) {
			return;
		}

		long companyId = GetterUtil.getLong(
			properties.get(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey()),
			CompanyConstants.SYSTEM);

		jwks = _secretResolver.resolve(companyId, jwks);

		try {
			OAuth2JWKValidatorUtil.validateJWKS(jwks);
		}
		catch (SecurityException securityException) {
			throw new ConfigurationModelListenerException(
				securityException, OAuth2InAssertionConfiguration.class,
				OAuth2InAssertionConfigurationModelListener.class, properties);
		}
	}

	@Reference
	private SecretResolver _secretResolver;

}