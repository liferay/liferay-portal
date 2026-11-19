/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.internal.upgrade.v25_2_0;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class FaroUserUpgradeProcessTest {

	@Test
	public void testDoUpgrade() throws Exception {
		Mockito.when(
			_connection.getMetaData()
		).thenReturn(
			Mockito.mock(DatabaseMetaData.class)
		);

		Mockito.when(
			_connection.prepareStatement(Mockito.startsWith("select"))
		).thenReturn(
			_preparedStatement1
		);

		Mockito.when(
			_connection.prepareStatement(
				Mockito.startsWith("delete from OSBFaro_FaroPreferences"))
		).thenReturn(
			_preparedStatement2
		);

		Mockito.when(
			_connection.prepareStatement(
				Mockito.startsWith("delete from OSBFaro_FaroUser"))
		).thenReturn(
			_preparedStatement3
		);

		Mockito.when(
			_preparedStatement1.executeQuery()
		).thenReturn(
			_resultSet
		);

		long faroUserId = RandomTestUtil.randomLong();

		Mockito.when(
			_resultSet.getLong("faroUserId")
		).thenReturn(
			faroUserId
		);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			_resultSet.getLong("groupId")
		).thenReturn(
			groupId
		);

		long liveUserId = RandomTestUtil.randomLong();

		Mockito.when(
			_resultSet.getLong("liveUserId")
		).thenReturn(
			liveUserId
		);

		Mockito.when(
			_resultSet.next()
		).thenReturn(
			true, false
		);

		FaroUserUpgradeProcess faroUserUpgradeProcess =
			new FaroUserUpgradeProcess();

		ReflectionTestUtil.setFieldValue(
			faroUserUpgradeProcess, "connection", _connection);

		faroUserUpgradeProcess.doUpgrade();

		Mockito.verify(
			_preparedStatement2
		).setLong(
			1, groupId
		);

		Mockito.verify(
			_preparedStatement2
		).setLong(
			2, liveUserId
		);

		Mockito.verify(
			_preparedStatement2
		).executeUpdate();

		Mockito.verify(
			_preparedStatement3
		).setLong(
			1, faroUserId
		);

		Mockito.verify(
			_preparedStatement3
		).executeUpdate();
	}

	private final Connection _connection = Mockito.mock(Connection.class);
	private final PreparedStatement _preparedStatement1 = Mockito.mock(
		PreparedStatement.class);
	private final PreparedStatement _preparedStatement2 = Mockito.mock(
		PreparedStatement.class);
	private final PreparedStatement _preparedStatement3 = Mockito.mock(
		PreparedStatement.class);
	private final ResultSet _resultSet = Mockito.mock(ResultSet.class);

}