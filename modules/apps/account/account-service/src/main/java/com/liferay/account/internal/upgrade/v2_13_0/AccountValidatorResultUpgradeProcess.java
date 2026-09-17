/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.upgrade.v2_13_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Tancredi Covioli
 */
public class AccountValidatorResultUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ObjectField.objectFieldId from ObjectDefinition ",
					"inner join ObjectField on ObjectField.objectDefinitionId ",
					"= ObjectDefinition.objectDefinitionId where ",
					"ObjectDefinition.externalReferenceCode = ? and ",
					"ObjectField.name = ?"))) {

			preparedStatement.setString(1, _OBJECT_DEFINITION_ERC);
			preparedStatement.setString(2, _OBJECT_FIELD_NAME);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					runSQL(
						"update ObjectRelationship set deletionType = " +
							"'cascade' where objectFieldId2 = " +
								resultSet.getLong("objectFieldId"));
				}
			}
		}
	}

	private static final String _OBJECT_DEFINITION_ERC =
		"L_ACCOUNT_VALIDATOR_RESULT";

	private static final String _OBJECT_FIELD_NAME =
		"r_accountToAccountValidatorResults_accountEntryId";

}