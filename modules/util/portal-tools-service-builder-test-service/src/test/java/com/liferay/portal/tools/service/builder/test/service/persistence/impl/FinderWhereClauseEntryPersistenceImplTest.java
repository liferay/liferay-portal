/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.test.ReflectionTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class FinderWhereClauseEntryPersistenceImplTest {

	@Test
	public void testFinderWhereClause() {
		Assert.assertEquals(
			"finderWhereClauseEntry.name = ? AND finderWhereClauseEntry." +
				"nickname IS NOT NULL",
			ReflectionTestUtil.getFieldValue(
				FinderWhereClauseEntryPersistenceImpl.class,
				"_FINDER_COLUMN_NAME_NICKNAME_NAME_2"));
	}

}