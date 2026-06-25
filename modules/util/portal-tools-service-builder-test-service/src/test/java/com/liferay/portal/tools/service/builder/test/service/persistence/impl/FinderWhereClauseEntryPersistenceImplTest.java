/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactory;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.tools.service.builder.test.service.util.ServiceProps;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Tina Tian
 */
public class FinderWhereClauseEntryPersistenceImplTest {

	@BeforeClass
	public static void setUpClass() {
		ConfigurationFactoryUtil.setConfigurationFactory(
			ProxyFactory.newDummyInstance(ConfigurationFactory.class));

		ReflectionTestUtil.setFieldValue(
			ReflectionTestUtil.<Object>getFieldValue(
				ServiceProps.class, "_instance"),
			"_configuration",
			ProxyFactory.newDummyInstance(Configuration.class));
	}

	@Test
	public void testFinderWhereClause() {
		FinderWhereClauseEntryPersistenceImpl
			finderWhereClauseEntryPersistenceImpl =
				new FinderWhereClauseEntryPersistenceImpl();

		finderWhereClauseEntryPersistenceImpl.afterPropertiesSet();

		Object collectionPersistenceFinder = ReflectionTestUtil.getFieldValue(
			finderWhereClauseEntryPersistenceImpl,
			"_collectionPersistenceFinderByName_Nickname");

		Assert.assertEquals(
			"finderWhereClauseEntry.name = ? AND " +
				"finderWhereClauseEntry.nickname IS NOT NULL",
			ReflectionTestUtil.invoke(
				collectionPersistenceFinder, "buildSQLWhere",
				new Class<?>[] {String.class, Object[].class, boolean.class},
				"", new Object[] {"name"}, false));
	}

}