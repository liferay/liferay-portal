/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.internal.upgrade.v24_1_0;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class GroupUsersUpgradeProcessTest {

	@Test
	public void testDoUpgrade() throws Exception {
		Mockito.when(
			_connection.prepareStatement(Mockito.anyString())
		).thenReturn(
			_preparedStatement
		);

		Mockito.when(
			_preparedStatement.executeQuery()
		).thenReturn(
			_resultSet
		);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			_resultSet.getLong("groupId")
		).thenReturn(
			groupId
		);

		long liveUserId = RandomTestUtil.randomLong();
		long missingLiveUserId = RandomTestUtil.randomLong();

		Mockito.when(
			_resultSet.getLong("liveUserId")
		).thenReturn(
			missingLiveUserId, liveUserId
		);

		Mockito.when(
			_resultSet.next()
		).thenReturn(
			true, true, false
		);

		Mockito.when(
			_userLocalService.fetchUser(liveUserId)
		).thenReturn(
			Mockito.mock(User.class)
		);

		GroupUsersUpgradeProcess groupUsersUpgradeProcess =
			new GroupUsersUpgradeProcess(_groupLocalService, _userLocalService);

		ReflectionTestUtil.setFieldValue(
			groupUsersUpgradeProcess, "connection", _connection);

		groupUsersUpgradeProcess.doUpgrade();

		Mockito.verify(
			_groupLocalService
		).addUserGroup(
			liveUserId, groupId
		);

		Mockito.verifyNoMoreInteractions(_groupLocalService);
	}

	private final Connection _connection = Mockito.mock(Connection.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final PreparedStatement _preparedStatement = Mockito.mock(
		PreparedStatement.class);
	private final ResultSet _resultSet = Mockito.mock(ResultSet.class);
	private final UserLocalService _userLocalService = Mockito.mock(
		UserLocalService.class);

}