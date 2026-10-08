/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.configuration;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.io.IOException;

import java.util.Dictionary;

import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = {})
public class OAuth2AuthorizationServerConfigurationGenerator {

	@Activate
	protected void activate() throws IOException {
		Configuration configuration = _configurationAdmin.getConfiguration(
			OAuth2AuthorizationServerConfiguration.class.getName(),
			StringPool.QUESTION);

		Dictionary<String, Object> dictionary = configuration.getProperties();

		if (dictionary != null) {
			return;
		}

		configuration.update(
			HashMapDictionaryBuilder.<String, Object>put(
				"oauth2.authorization.server.issue.jwt.access.token", true
			).put(
				"oauth2.authorization.server.jwt.access.token.signing.json." +
					"web.key",
				() -> {
					RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(
						2048);

					rsaJsonWebKey.setAlgorithm("RS256");
					rsaJsonWebKey.setKeyId("authServer");

					return _secretResolver.store(
						CompanyConstants.SYSTEM,
						StringBundler.concat(
							"config/",
							OAuth2AuthorizationServerConfiguration.class.
								getName(),
							StringPool.SLASH, CompanyConstants.SYSTEM,
							"/oauth2.authorization.server.jwt.access.token.",
							"signing.json.web.key"),
						rsaJsonWebKey.toJson(
							JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE));
				}
			).build());
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private SecretResolver _secretResolver;

}