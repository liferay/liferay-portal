/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence.internal.upgrade.v1_0_3;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Dictionary;

import org.apache.felix.cm.file.ConfigurationHandler;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Shuyang Zhou
 */
public class ConfigurationUpgradeProcess extends UpgradeProcess {

	public ConfigurationUpgradeProcess(ConfigurationAdmin configurationAdmin) {
		_configurationAdmin = configurationAdmin;
	}

	@Override
	protected void doUpgrade() throws Exception {
		if (!hasTable("Configuration_")) {
			return;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from Configuration_ where configurationId = ?")) {

			preparedStatement.setString(1, _PID);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String dictionaryString = resultSet.getString("dictionary");

					if (Validator.isNull(dictionaryString)) {
						continue;
					}

					Dictionary<String, String> dictionary =
						ConfigurationHandler.read(
							new UnsyncByteArrayInputStream(
								dictionaryString.getBytes(StringPool.UTF8)));

					if (_FILTER_VALUE.equals(dictionary.get(_FILTER_KEY))) {
						continue;
					}

					dictionary.put(_FILTER_KEY, _FILTER_VALUE);

					Configuration configuration =
						_configurationAdmin.getConfiguration(_PID, "?");

					configuration.update(dictionary);
				}
			}
		}
	}

	private static final String _FILTER_KEY =
		"application.ready.service.filter";

	private static final String _FILTER_VALUE =
		"(liferay.jaxrs.whiteboard.ready=true)";

	private static final String _PID =
		"org.apache.aries.jax.rs.whiteboard.default";

	private final ConfigurationAdmin _configurationAdmin;

}