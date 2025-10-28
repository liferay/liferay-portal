/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Pei-Jung Lan
 */
public class UpgradeListType extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_addListType("phone-number", ListTypeConstants.ADDRESS_PHONE);
	}

	private void _addListType(String name, String type) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from ListType where name = ? and type_ = ?")) {

			preparedStatement.setString(1, StringUtil.quote(name));
			preparedStatement.setString(2, StringUtil.quote(type));

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return;
			}
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into ListType (listTypeId, name, type_) values (?, " +
					"?, ?)")) {

			preparedStatement.setLong(1, increment(ListType.class.getName()));
			preparedStatement.setString(2, name);
			preparedStatement.setString(3, type);

			preparedStatement.executeUpdate();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to add list type", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeListType.class);

}