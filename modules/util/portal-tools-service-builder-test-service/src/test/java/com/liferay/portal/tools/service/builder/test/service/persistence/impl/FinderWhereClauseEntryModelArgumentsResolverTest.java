/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactory;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryImpl;
import com.liferay.portal.tools.service.builder.test.service.util.ServiceProps;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class FinderWhereClauseEntryModelArgumentsResolverTest {

	@BeforeClass
	public static void setUpClass() {
		ConfigurationFactoryUtil.setConfigurationFactory(
			ProxyFactory.newDummyInstance(ConfigurationFactory.class));

		ReflectionTestUtil.setFieldValue(
			ReflectionTestUtil.<Object>getFieldValue(
				ServiceProps.class, "_instance"),
			"_configuration",
			ProxyFactory.newDummyInstance(Configuration.class));

		_argumentsResolver = new FinderWhereClauseEntryModelArgumentsResolver();
	}

	@Test
	public void testGetArgumentsWithChangedWhereClauseColumn() {
		FinderWhereClauseEntryImpl finderWhereClauseEntryImpl =
			_createFinderWhereClauseEntryImpl();

		finderWhereClauseEntryImpl.resetOriginalValues();

		finderWhereClauseEntryImpl.setNickname("nickname");

		Assert.assertArrayEquals(
			new Object[] {"name"},
			_argumentsResolver.getArguments(
				_nameNicknameFinderPath, finderWhereClauseEntryImpl, true,
				false));
		Assert.assertNull(
			_argumentsResolver.getArguments(
				_nameNicknameFinderPath, finderWhereClauseEntryImpl, true,
				true));
	}

	@Test
	public void testGetArgumentsWithCompoundWhereClause() {
		FinderWhereClauseEntryImpl finderWhereClauseEntryImpl =
			_createFinderWhereClauseEntryImpl();

		finderWhereClauseEntryImpl.setNickname("nickname");
		finderWhereClauseEntryImpl.setStatus(1);

		Assert.assertArrayEquals(
			new Object[] {"name", 1},
			_argumentsResolver.getArguments(
				_nameStatusFinderPath, finderWhereClauseEntryImpl, false,
				false));

		finderWhereClauseEntryImpl.setStatus(0);

		Assert.assertNull(
			_argumentsResolver.getArguments(
				_nameStatusFinderPath, finderWhereClauseEntryImpl, false,
				false));

		finderWhereClauseEntryImpl.setNickname(null);
		finderWhereClauseEntryImpl.setStatus(1);

		Assert.assertNull(
			_argumentsResolver.getArguments(
				_nameStatusFinderPath, finderWhereClauseEntryImpl, false,
				false));
	}

	@Test
	public void testGetArgumentsWithEqualsWhereClause() {
		FinderWhereClauseEntryImpl finderWhereClauseEntryImpl =
			_createFinderWhereClauseEntryImpl();

		finderWhereClauseEntryImpl.setFinderWhereClauseEntryId(1);
		finderWhereClauseEntryImpl.setHeadId(1);

		Assert.assertArrayEquals(
			new Object[] {1L},
			_argumentsResolver.getArguments(
				_headIdFinderPath, finderWhereClauseEntryImpl, false, false));

		finderWhereClauseEntryImpl.setHeadId(2);

		Assert.assertNull(
			_argumentsResolver.getArguments(
				_headIdFinderPath, finderWhereClauseEntryImpl, false, false));
	}

	@Test
	public void testGetArgumentsWithNotEqualsWhereClause() {
		FinderWhereClauseEntryImpl finderWhereClauseEntryImpl =
			_createFinderWhereClauseEntryImpl();

		finderWhereClauseEntryImpl.setFinderWhereClauseEntryId(1);
		finderWhereClauseEntryImpl.setHeadId(2);
		finderWhereClauseEntryImpl.setStatus(3);

		Assert.assertArrayEquals(
			new Object[] {3},
			_argumentsResolver.getArguments(
				_statusFinderPath, finderWhereClauseEntryImpl, false, false));

		finderWhereClauseEntryImpl.setHeadId(1);

		Assert.assertNull(
			_argumentsResolver.getArguments(
				_statusFinderPath, finderWhereClauseEntryImpl, false, false));
	}

	@Test
	public void testGetArgumentsWithNotNullWhereClause() {
		FinderWhereClauseEntryImpl finderWhereClauseEntryImpl =
			_createFinderWhereClauseEntryImpl();

		finderWhereClauseEntryImpl.setNickname("nickname");

		Assert.assertArrayEquals(
			new Object[] {"name"},
			_argumentsResolver.getArguments(
				_nameNicknameFinderPath, finderWhereClauseEntryImpl, false,
				false));

		finderWhereClauseEntryImpl.setNickname(null);

		Assert.assertNull(
			_argumentsResolver.getArguments(
				_nameNicknameFinderPath, finderWhereClauseEntryImpl, false,
				false));
	}

	private FinderWhereClauseEntryImpl _createFinderWhereClauseEntryImpl() {
		FinderWhereClauseEntryImpl finderWhereClauseEntryImpl =
			new FinderWhereClauseEntryImpl();

		finderWhereClauseEntryImpl.setName("name");

		return finderWhereClauseEntryImpl;
	}

	private static ArgumentsResolver _argumentsResolver;
	private static final FinderPath _headIdFinderPath = new FinderPath(
		FinderWhereClauseEntryPersistenceImpl.
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
		"findByHeadId", new String[] {Long.class.getName()},
		new String[] {"headId"}, true);
	private static final FinderPath _nameNicknameFinderPath = new FinderPath(
		FinderWhereClauseEntryPersistenceImpl.
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
		"findByName_Nickname", new String[] {String.class.getName()},
		new String[] {"name"}, true);
	private static final FinderPath _nameStatusFinderPath = new FinderPath(
		FinderWhereClauseEntryPersistenceImpl.
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
		"findByName_Status",
		new String[] {String.class.getName(), Integer.class.getName()},
		new String[] {"name", "status"}, true);
	private static final FinderPath _statusFinderPath = new FinderPath(
		FinderWhereClauseEntryPersistenceImpl.
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
		"findByStatus", new String[] {Integer.class.getName()},
		new String[] {"status"}, true);

}