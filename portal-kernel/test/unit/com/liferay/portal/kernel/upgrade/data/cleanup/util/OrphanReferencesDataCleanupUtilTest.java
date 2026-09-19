/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup.util;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jorge Avalos
 */
public class OrphanReferencesDataCleanupUtilTest {

	@Test
	public void testExecuteDeleteWithRetry() throws Exception {
		PreparedStatement preparedStatement = Mockito.mock(
			PreparedStatement.class);

		Mockito.doThrow(
			new SQLException(RandomTestUtil.randomString(), _SQL_STATE_DEADLOCK)
		).doReturn(
			RandomTestUtil.randomInt()
		).when(
			preparedStatement
		).executeUpdate();

		_invokeExecuteDelete(preparedStatement);

		Mockito.verify(
			preparedStatement, Mockito.times(2)
		).executeUpdate();
	}

	@Test
	public void testExecuteDeleteWithRetryExhausted() throws Exception {
		PreparedStatement preparedStatement = Mockito.mock(
			PreparedStatement.class);

		Mockito.doThrow(
			new SQLException(RandomTestUtil.randomString(), _SQL_STATE_DEADLOCK)
		).when(
			preparedStatement
		).executeUpdate();

		try {
			_invokeExecuteDelete(preparedStatement);

			Assert.fail();
		}
		catch (Exception exception) {
			SQLException sqlException = (SQLException)exception;

			Assert.assertEquals(
				_SQL_STATE_DEADLOCK, sqlException.getSQLState());
		}

		int expectedAttempts =
			(int)ReflectionTestUtil.getFieldValue(
				OrphanReferencesDataCleanupUtil.class,
				"_DELETE_DEADLOCK_MAX_RETRIES") + 1;

		Mockito.verify(
			preparedStatement, Mockito.times(expectedAttempts)
		).executeUpdate();
	}

	@Test
	public void testExecuteDeleteWithoutRetry() throws Exception {
		PreparedStatement preparedStatement = Mockito.mock(
			PreparedStatement.class);

		Mockito.doThrow(
			new SQLException(
				RandomTestUtil.randomString(), _SQL_STATE_CONSTRAINT_VIOLATION)
		).when(
			preparedStatement
		).executeUpdate();

		try {
			_invokeExecuteDelete(preparedStatement);

			Assert.fail();
		}
		catch (Exception exception) {
			SQLException sqlException = (SQLException)exception;

			Assert.assertEquals(
				_SQL_STATE_CONSTRAINT_VIOLATION, sqlException.getSQLState());
		}

		Mockito.verify(
			preparedStatement, Mockito.times(1)
		).executeUpdate();
	}

	private void _invokeExecuteDelete(PreparedStatement preparedStatement)
		throws Exception {

		Connection connection = Mockito.mock(Connection.class);

		Mockito.when(
			connection.prepareStatement(Mockito.anyString())
		).thenReturn(
			preparedStatement
		);

		ReflectionTestUtil.invoke(
			OrphanReferencesDataCleanupUtil.class, "_executeDelete",
			new Class<?>[] {Connection.class, String.class}, connection,
			RandomTestUtil.randomString());
	}

	private static final String _SQL_STATE_CONSTRAINT_VIOLATION = "23000";

	private static final String _SQL_STATE_DEADLOCK = "40001";

}