/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.credential;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.runtime.configuration.SamlConfiguration;
import com.liferay.saml.runtime.credential.KeyStoreManager;

import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class BaseKeyStoreManagerImpl implements KeyStoreManager {

	protected long getCompanyId() {
		return CompanyThreadLocal.getCompanyId();
	}

	protected String getSamlKeyStorePassword() {
		return samlConfiguration.keyStorePassword();
	}

	protected String getSamlKeyStorePath() {
		String keyStorePath = samlConfiguration.keyStorePath();

		String liferayHome = PropsUtil.get(PropsKeys.LIFERAY_HOME);

		if (Validator.isNull(keyStorePath)) {
			return liferayHome.concat("/data/keystore.p12");
		}

		return StringUtil.replace(keyStorePath, "${liferay.home}", liferayHome);
	}

	protected String getSamlKeyStoreType() {
		return samlConfiguration.keyStoreType();
	}

	protected void updateConfigurations(Map<String, Object> properties)
		throws Exception {

		samlConfiguration = ConfigurableUtil.createConfigurable(
			SamlConfiguration.class, properties);
	}

	protected SamlConfiguration samlConfiguration;

}