/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence.internal.upgrade.v2_0_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Dictionary;

import org.apache.felix.cm.file.ConfigurationHandler;

/**
 * @author Thiago Buarque
 */
public class ConfigurationUpgradeProcess extends UpgradeProcess {

	public ConfigurationUpgradeProcess(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		if (!hasTable("Configuration_")) {
			return;
		}

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select configurationId, dictionary from Configuration_ " +
					"where dictionary like '%groupId=%'");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update Configuration_ set dictionary = ? where " +
						"configurationId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				Dictionary<String, Object> dictionary = _toDictionary(
					resultSet.getString(2));

				String configurationId = resultSet.getString(1);

				Long companyId = _getCompanyId(configurationId, dictionary);

				if (companyId == null) {
					continue;
				}

				dictionary.put(
					ExtendedObjectClassDefinition.Scope.COMPANY.
						getPropertyKey(),
					companyId);

				preparedStatement2.setString(1, _toString(dictionary));

				preparedStatement2.setString(2, configurationId);

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private Long _getCompanyId(
		String configurationId, Dictionary<String, Object> dictionary) {

		long groupId = GetterUtil.getLong(
			dictionary.get(
				ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey()));

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Skipping configuration \"", configurationId,
						"\" because group \"", groupId,
						"\" does not exist for company \"",
						CompanyThreadLocal.getCompanyId(), "\""));
			}

			return null;
		}

		return group.getCompanyId();
	}

	private Dictionary<String, Object> _toDictionary(String dictionaryString)
		throws IOException {

		UnsyncByteArrayInputStream unsyncByteArrayInputStream =
			new UnsyncByteArrayInputStream(
				dictionaryString.getBytes(StandardCharsets.UTF_8));

		return ConfigurationHandler.read(unsyncByteArrayInputStream);
	}

	private String _toString(Dictionary<String, Object> dictionary)
		throws IOException {

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		ConfigurationHandler.write(unsyncByteArrayOutputStream, dictionary);

		return new String(
			unsyncByteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationUpgradeProcess.class);

	private final GroupLocalService _groupLocalService;

}