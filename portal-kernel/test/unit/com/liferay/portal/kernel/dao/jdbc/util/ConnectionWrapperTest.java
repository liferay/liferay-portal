/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.jdbc.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ConnectionWrapperTest {

	@Test
	public void testIsWrapperFor() throws SQLException {
		ConnectionWrapper connectionWrapper = new ConnectionWrapper(null);

		Assert.assertTrue(connectionWrapper.isWrapperFor(Connection.class));
		Assert.assertFalse(connectionWrapper.isWrapperFor(DataSource.class));

		Assert.assertSame(
			connectionWrapper, connectionWrapper.unwrap(Connection.class));
	}

}