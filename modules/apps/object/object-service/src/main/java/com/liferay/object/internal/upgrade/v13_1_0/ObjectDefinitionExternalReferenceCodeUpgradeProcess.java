/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_1_0;

import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jhosseph Gonzalez
 */
public class ObjectDefinitionExternalReferenceCodeUpgradeProcess
	extends UpgradeProcess {

	public ObjectDefinitionExternalReferenceCodeUpgradeProcess(
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		_systemObjectDefinitionManagerRegistry =
			systemObjectDefinitionManagerRegistry;
	}

	@Override
	protected void doUpgrade() throws Exception {
		Map<Long, Set<String>> externalReferenceCodesByCompanyId =
			_getExternalReferenceCodesByCompanyId();

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select externalReferenceCode, objectDefinitionId, ",
						"companyId, name from ObjectDefinition where ",
						"modifiable = [$FALSE$] and system_ = [$TRUE$]")));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update ObjectDefinition set externalReferenceCode = ? " +
						"where objectDefinitionId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				SystemObjectDefinitionManager systemObjectDefinitionManager =
					_systemObjectDefinitionManagerRegistry.
						getSystemObjectDefinitionManager(
							resultSet.getString("name"));

				if (systemObjectDefinitionManager == null) {
					continue;
				}

				String externalReferenceCode =
					systemObjectDefinitionManager.getExternalReferenceCode();
				String oldExternalReferenceCode = resultSet.getString(
					"externalReferenceCode");

				if (Validator.isNull(externalReferenceCode) ||
					externalReferenceCode.equals(oldExternalReferenceCode)) {

					continue;
				}

				long companyId = resultSet.getLong("companyId");
				long objectDefinitionId = resultSet.getLong(
					"objectDefinitionId");

				Set<String> externalReferenceCodes =
					externalReferenceCodesByCompanyId.get(companyId);

				if ((externalReferenceCodes != null) &&
					externalReferenceCodes.contains(externalReferenceCode)) {

					throw new UpgradeException(
						StringBundler.concat(
							"Unable to update object definition ",
							objectDefinitionId,
							" because external reference code \"",
							externalReferenceCode,
							"\" is already used by another object definition ",
							"in company ", companyId));
				}

				preparedStatement2.setString(1, externalReferenceCode);
				preparedStatement2.setLong(2, objectDefinitionId);

				preparedStatement2.addBatch();

				externalReferenceCodes.remove(oldExternalReferenceCode);
				externalReferenceCodes.add(externalReferenceCode);
			}

			preparedStatement2.executeBatch();
		}
	}

	private Map<Long, Set<String>> _getExternalReferenceCodesByCompanyId()
		throws Exception {

		Map<Long, Set<String>> externalReferenceCodesByCompanyId =
			new HashMap<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select externalReferenceCode, companyId from " +
					"ObjectDefinition");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String externalReferenceCode = resultSet.getString(
					"externalReferenceCode");

				if (Validator.isNull(externalReferenceCode)) {
					continue;
				}

				Set<String> externalReferenceCodes =
					externalReferenceCodesByCompanyId.computeIfAbsent(
						resultSet.getLong("companyId"),
						companyId -> new HashSet<>());

				externalReferenceCodes.add(externalReferenceCode);
			}
		}

		return externalReferenceCodesByCompanyId;
	}

	private final SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}