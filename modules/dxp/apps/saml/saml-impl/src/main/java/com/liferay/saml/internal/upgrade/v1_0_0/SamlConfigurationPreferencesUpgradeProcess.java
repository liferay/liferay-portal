/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.internal.upgrade.v1_0_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.internal.constants.LegacySamlPropsKeys;

import java.util.Dictionary;
import java.util.Objects;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Stian Sigvartsen
 * @author Tomas Polesovsky
 */
public class SamlConfigurationPreferencesUpgradeProcess
	extends BaseUpgradeSaml {

	public SamlConfigurationPreferencesUpgradeProcess(
		ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			Configuration configuration = _configurationAdmin.getConfiguration(
				"com.liferay.saml.runtime.configuration.SamlConfiguration",
				StringPool.QUESTION);

			Dictionary<String, Object> dictionary =
				configuration.getProperties();

			if (dictionary == null) {
				dictionary = new HashMapDictionary<>();
			}

			Filter filter = null;

			String entityId = PropsUtil.get(LegacySamlPropsKeys.SAML_ENTITY_ID);

			if (Validator.isNotNull(entityId)) {
				filter = new Filter(entityId);
			}

			for (String key : LegacySamlPropsKeys.SAML_KEYS_PROPS) {
				if (ArrayUtil.contains(
						LegacySamlPropsKeys.SAML_KEYS_DEPRECATED, key)) {

					continue;
				}

				String value = getPropsValue(key, filter);

				if (value == null) {
					continue;
				}

				if (!Objects.equals(value, getDefaultValue(key))) {
					dictionary.put(key, value);
				}
			}

			if (!dictionary.isEmpty()) {
				configuration.update(dictionary);
			}
		}
	}

	private final ConfigurationAdmin _configurationAdmin;

}