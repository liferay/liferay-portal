/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.service.persistence;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.PortalCacheManagerProvider;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.MappingSqlQuery;
import com.liferay.portal.kernel.dao.jdbc.MappingSqlQueryFactoryUtil;
import com.liferay.portal.kernel.dao.jdbc.ParamSetter;
import com.liferay.portal.kernel.dao.jdbc.RowMapper;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.internal.service.persistence.change.tracking.CTTableMapper;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListenerRegistrationUtil;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.tools.ToolDependencies;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.invocation.InterceptedInvocation;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
public class TableMapperTest {

	@ClassRule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new CodeCoverageAssertor() {

				@Override
				public void appendAssertClasses(List<Class<?>> assertClasses) {
					assertClasses.clear();

					assertClasses.add(ReverseTableMapper.class);
					assertClasses.add(TableMapperFactory.class);
					assertClasses.add(TableMapperImpl.class);

					Collections.addAll(
						assertClasses,
						TableMapperFactory.class.getDeclaredClasses());
				}

			},
			LiferayUnitTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		ToolDependencies.wireBasic();

		DBManagerUtil.setDB(DBType.HYPERSONIC, null);
	}

	@Before
	public void setUp() {
		PortalCacheHelperUtil.clearPortalCaches(
			PortalCacheManagerNames.MULTI_VM);

		_mockMappingSqlQueryFactoryUtil();
		_mockSqlUpdateFactoryUtil();

		_dataSource = (DataSource)ProxyUtil.newProxyInstance(
			TableMapperTest.class.getClassLoader(),
			new Class<?>[] {DataSource.class},
			(proxy, method, args) -> {
				throw new UnsupportedOperationException();
			});

		_leftBasePersistence = new MockBasePersistence<>(Left.class);

		_leftBasePersistence.setDataSource(_dataSource);

		_rightBasePersistence = new MockBasePersistence<>(Right.class);

		_rightBasePersistence.setDataSource(_dataSource);

		_serviceRegistration = _bundleContext.registerService(
			FinderCache.class, Mockito.mock(FinderCache.class), null);
		_tableMapperImpl = new TableMapperImpl<>(
			_TABLE_NAME, _COMPANY_COLUMN_NAME, _LEFT_COLUMN_NAME,
			_RIGHT_COLUMN_NAME, Left.class, Right.class, _leftBasePersistence,
			_rightBasePersistence, false);
	}

	@After
	public void tearDown() {
		_mappingSqlQueryFactoryUtilMockedStatic.close();

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		_sqlUpdateFactoryUtilMockedStatic.close();
	}

	@Test
	public void testAddTableMapping() {

		// Success, no model listener

		long companyId = 0;
		long leftPrimaryKey = 1;
		long rightPrimaryKey = 2;

		Assert.assertTrue(
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey, rightPrimaryKey));

		// Fail, no model listener

		Assert.assertFalse(
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey, rightPrimaryKey));

		// Error, no model listener

		PortalCache<Long, long[]> leftToRightPortalCache =
			_tableMapperImpl.leftToRightPortalCache;

		leftToRightPortalCache.put(leftPrimaryKey, new long[0]);

		try {
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey, rightPrimaryKey);

			Assert.fail();
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertSame(RuntimeException.class, throwable.getClass());
			Assert.assertEquals(
				StringBundler.concat(
					"Unique key violation for left primary key ",
					leftPrimaryKey, " and right primary key ", rightPrimaryKey),
				throwable.getMessage());
		}

		// Auto recover after error

		Assert.assertFalse(
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey, rightPrimaryKey));

		// Success, with model listener

		leftToRightPortalCache.remove(leftPrimaryKey);

		_mappingStore.remove(leftPrimaryKey);

		RecorderModelListener<Left> leftModelListener =
			new LeftRecorderModelListener();

		ModelListenerRegistrationUtil.register(leftModelListener);

		RecorderModelListener<Right> rightModelListener =
			new RightRecorderModelListener();

		ModelListenerRegistrationUtil.register(rightModelListener);

		Assert.assertTrue(
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey, rightPrimaryKey));

		leftModelListener.assertOnBeforeAddAssociation(
			true, leftPrimaryKey, Right.class.getName(), rightPrimaryKey);

		rightModelListener.assertOnBeforeAddAssociation(
			true, rightPrimaryKey, Left.class.getName(), leftPrimaryKey);

		leftModelListener.assertOnAfterAddAssociation(
			true, leftPrimaryKey, Right.class.getName(), rightPrimaryKey);

		rightModelListener.assertOnAfterAddAssociation(
			true, rightPrimaryKey, Left.class.getName(), leftPrimaryKey);

		ModelListenerRegistrationUtil.unregister(leftModelListener);
		ModelListenerRegistrationUtil.unregister(rightModelListener);

		// Error, no model listener

		leftToRightPortalCache.put(leftPrimaryKey, new long[0]);

		leftModelListener = new LeftRecorderModelListener();

		ModelListenerRegistrationUtil.register(leftModelListener);

		rightModelListener = new RightRecorderModelListener();

		ModelListenerRegistrationUtil.register(rightModelListener);

		try {
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey, rightPrimaryKey);

			Assert.fail();
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertSame(RuntimeException.class, throwable.getClass());
			Assert.assertEquals(
				StringBundler.concat(
					"Unique key violation for left primary key ",
					leftPrimaryKey, " and right primary key ", rightPrimaryKey),
				throwable.getMessage());
		}

		ModelListenerRegistrationUtil.unregister(leftModelListener);
		ModelListenerRegistrationUtil.unregister(rightModelListener);

		leftModelListener.assertOnBeforeAddAssociation(
			true, leftPrimaryKey, Right.class.getName(), rightPrimaryKey);

		rightModelListener.assertOnBeforeAddAssociation(
			true, rightPrimaryKey, Left.class.getName(), leftPrimaryKey);

		leftModelListener.assertOnAfterAddAssociation(false, null, null, null);

		rightModelListener.assertOnAfterAddAssociation(false, null, null, null);
	}

	@Test
	public void testAddTableMappings() {
		long companyId = 0;
		long leftPrimaryKey1 = 1;
		long leftPrimaryKey2 = 2;
		long rightPrimaryKey1 = 3;
		long rightPrimaryKey2 = 4;

		Assert.assertArrayEquals(
			new long[] {rightPrimaryKey1},
			_tableMapperImpl.addTableMappings(
				companyId, leftPrimaryKey1, new long[] {rightPrimaryKey1}));
		Assert.assertArrayEquals(
			new long[0],
			_tableMapperImpl.addTableMappings(
				companyId, leftPrimaryKey1, new long[] {rightPrimaryKey1}));
		Assert.assertArrayEquals(
			new long[] {rightPrimaryKey2},
			_tableMapperImpl.addTableMappings(
				companyId, leftPrimaryKey1,
				new long[] {rightPrimaryKey1, rightPrimaryKey2}));
		Assert.assertEquals(
			2,
			_tableMapperImpl.deleteLeftPrimaryKeyTableMappings(
				leftPrimaryKey1));
		Assert.assertArrayEquals(
			new long[] {rightPrimaryKey1, rightPrimaryKey2},
			_tableMapperImpl.addTableMappings(
				companyId, leftPrimaryKey1,
				new long[] {rightPrimaryKey1, rightPrimaryKey2}));
		Assert.assertEquals(
			2,
			_tableMapperImpl.deleteLeftPrimaryKeyTableMappings(
				leftPrimaryKey1));
		Assert.assertArrayEquals(
			new long[] {leftPrimaryKey1},
			_tableMapperImpl.addTableMappings(
				companyId, new long[] {leftPrimaryKey1}, rightPrimaryKey1));
		Assert.assertArrayEquals(
			new long[0],
			_tableMapperImpl.addTableMappings(
				companyId, new long[] {leftPrimaryKey1}, rightPrimaryKey1));
		Assert.assertArrayEquals(
			new long[] {leftPrimaryKey2},
			_tableMapperImpl.addTableMappings(
				companyId, new long[] {leftPrimaryKey1, leftPrimaryKey2},
				rightPrimaryKey1));
		Assert.assertEquals(
			2,
			_tableMapperImpl.deleteRightPrimaryKeyTableMappings(
				rightPrimaryKey1));
		Assert.assertArrayEquals(
			new long[] {leftPrimaryKey1, leftPrimaryKey2},
			_tableMapperImpl.addTableMappings(
				companyId, new long[] {leftPrimaryKey1, leftPrimaryKey2},
				rightPrimaryKey1));
		Assert.assertEquals(
			2,
			_tableMapperImpl.deleteRightPrimaryKeyTableMappings(
				rightPrimaryKey1));
	}

	@Test
	public void testCachelessTableMapper() {
		_tableMapperImpl = new TableMapperImpl<>(
			_TABLE_NAME, _COMPANY_COLUMN_NAME, _LEFT_COLUMN_NAME,
			_RIGHT_COLUMN_NAME, Left.class, Right.class, _leftBasePersistence,
			_rightBasePersistence, true);

		long leftPrimaryKey = 1;
		long rightPrimaryKey = 2;

		Assert.assertFalse(
			_tableMapperImpl.containsTableMapping(
				leftPrimaryKey, rightPrimaryKey));

		// Contains table mapping

		_mappingStore.put(leftPrimaryKey, new long[] {rightPrimaryKey});

		Assert.assertTrue(
			_tableMapperImpl.containsTableMapping(
				leftPrimaryKey, rightPrimaryKey));

		MockContainsTableMappingSQLQuery mockContainsTableMappingSQLQuery =
			(MockContainsTableMappingSQLQuery)
				_tableMapperImpl.containsTableMappingSQL;

		mockContainsTableMappingSQLQuery.setDatabaseError(true);

		try {
			_tableMapperImpl.containsTableMapping(
				leftPrimaryKey, rightPrimaryKey);

			Assert.fail();
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertSame(RuntimeException.class, throwable.getClass());

			Assert.assertEquals("Database error", throwable.getMessage());
		}
		finally {
			mockContainsTableMappingSQLQuery.setDatabaseError(false);
		}

		mockContainsTableMappingSQLQuery.setEmptyResultSet(true);

		Assert.assertFalse(
			_tableMapperImpl.containsTableMapping(
				leftPrimaryKey, rightPrimaryKey));
	}

	@Test
	public void testConstructor() {
		new TableMapperFactory();

		Assert.assertTrue(
			_tableMapperImpl.addTableMappingSqlUpdate instanceof
				MockAddMappingSqlUpdate);
		Assert.assertTrue(
			_tableMapperImpl.
				deleteLeftPrimaryKeyTableMappingsSqlUpdate instanceof
					MockDeleteLeftPrimaryKeyTableMappingsSqlUpdate);
		Assert.assertTrue(
			_tableMapperImpl.
				deleteRightPrimaryKeyTableMappingsSqlUpdate instanceof
					MockDeleteRightPrimaryKeyTableMappingsSqlUpdate);
		Assert.assertTrue(
			_tableMapperImpl.deleteTableMappingSqlUpdate instanceof
				MockDeleteMappingSqlUpdate);
		Assert.assertTrue(
			_tableMapperImpl.getLeftPrimaryKeysSqlQuery instanceof
				MockGetLeftPrimaryKeysSqlQuery);
		Assert.assertTrue(
			_tableMapperImpl.getRightPrimaryKeysSqlQuery instanceof
				MockGetRightPrimaryKeysSqlQuery);
		Assert.assertEquals(_LEFT_COLUMN_NAME, _tableMapperImpl.leftColumnName);

		PortalCache<Long, long[]> leftToRightPortalCache =
			_tableMapperImpl.leftToRightPortalCache;

		leftToRightPortalCache = ReflectionTestUtil.getFieldValue(
			leftToRightPortalCache, "_portalCache");

		Class<?> clazz = leftToRightPortalCache.getClass();

		Assert.assertEquals(
			"com.liferay.portal.tools.ToolDependencies$TestPortalCache",
			clazz.getName());

		Assert.assertEquals(
			StringBundler.concat(
				TableMapper.class.getName(), "-", _TABLE_NAME, "-",
				_LEFT_COLUMN_NAME, "-To-", _RIGHT_COLUMN_NAME),
			leftToRightPortalCache.getPortalCacheName());

		Assert.assertEquals(
			_RIGHT_COLUMN_NAME, _tableMapperImpl.rightColumnName);

		PortalCache<Long, long[]> rightToLeftPortalCache =
			_tableMapperImpl.rightToLeftPortalCache;

		rightToLeftPortalCache = ReflectionTestUtil.getFieldValue(
			rightToLeftPortalCache, "_portalCache");

		clazz = rightToLeftPortalCache.getClass();

		Assert.assertEquals(
			"com.liferay.portal.tools.ToolDependencies$TestPortalCache",
			clazz.getName());

		Assert.assertEquals(
			StringBundler.concat(
				TableMapper.class.getName(), "-", _TABLE_NAME, "-",
				_RIGHT_COLUMN_NAME, "-To-", _LEFT_COLUMN_NAME),
			rightToLeftPortalCache.getPortalCacheName());
	}

	@Test
	public void testContainsTableMapping() {

		// Does not contain table mapping

		long leftPrimaryKey = 1;
		long rightPrimaryKey = 2;

		Assert.assertFalse(
			_tableMapperImpl.containsTableMapping(
				leftPrimaryKey, rightPrimaryKey));

		// Contains table mapping

		PortalCache<Long, long[]> leftToRightPortalCache =
			_tableMapperImpl.leftToRightPortalCache;

		leftToRightPortalCache.remove(leftPrimaryKey);

		_mappingStore.put(leftPrimaryKey, new long[] {rightPrimaryKey});

		Assert.assertTrue(
			_tableMapperImpl.containsTableMapping(
				leftPrimaryKey, rightPrimaryKey));
	}

	@Test
	public void testDeleteLeftPrimaryKeyTableMappings() {

		// Delete 0 entry

		long leftPrimaryKey = 1;

		Assert.assertEquals(
			0,
			_tableMapperImpl.deleteLeftPrimaryKeyTableMappings(leftPrimaryKey));

		// Delete 1 entry

		long rightPrimaryKey1 = 2;

		_mappingStore.put(leftPrimaryKey, new long[] {rightPrimaryKey1});

		Assert.assertEquals(
			1,
			_tableMapperImpl.deleteLeftPrimaryKeyTableMappings(leftPrimaryKey));

		// Delete 2 entries

		long rightPrimaryKey2 = 3;

		_mappingStore.put(
			leftPrimaryKey, new long[] {rightPrimaryKey1, rightPrimaryKey2});

		Assert.assertEquals(
			2,
			_tableMapperImpl.deleteLeftPrimaryKeyTableMappings(leftPrimaryKey));

		// Delete 0 entry, with left model listener

		RecorderModelListener<Left> leftModelListener =
			new LeftRecorderModelListener();

		ModelListenerRegistrationUtil.register(leftModelListener);

		Assert.assertEquals(
			0,
			_tableMapperImpl.deleteLeftPrimaryKeyTableMappings(leftPrimaryKey));

		leftModelListener.assertOnBeforeRemoveAssociation(
			false, null, null, null);

		leftModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		ModelListenerRegistrationUtil.unregister(leftModelListener);

		// Delete 0 entry, with right model listener

		RecorderModelListener<Right> rightModelListener =
			new RightRecorderModelListener();

		ModelListenerRegistrationUtil.register(rightModelListener);

		Assert.assertEquals(
			0,
			_tableMapperImpl.deleteLeftPrimaryKeyTableMappings(leftPrimaryKey));

		rightModelListener.assertOnBeforeRemoveAssociation(
			false, null, null, null);

		rightModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		ModelListenerRegistrationUtil.unregister(rightModelListener);

		// Delete 1 entry, with left model listener

		leftModelListener = new LeftRecorderModelListener();

		ModelListenerRegistrationUtil.register(leftModelListener);

		_mappingStore.put(leftPrimaryKey, new long[] {rightPrimaryKey1});

		Assert.assertEquals(
			1,
			_tableMapperImpl.deleteLeftPrimaryKeyTableMappings(leftPrimaryKey));

		leftModelListener.assertOnBeforeRemoveAssociation(
			true, leftPrimaryKey, Right.class.getName(), rightPrimaryKey1);

		leftModelListener.assertOnAfterRemoveAssociation(
			true, leftPrimaryKey, Right.class.getName(), rightPrimaryKey1);

		ModelListenerRegistrationUtil.unregister(leftModelListener);

		// Delete 1 entry, with right model listener

		rightModelListener = new RightRecorderModelListener();

		ModelListenerRegistrationUtil.register(rightModelListener);

		_mappingStore.put(leftPrimaryKey, new long[] {rightPrimaryKey1});

		Assert.assertEquals(
			1,
			_tableMapperImpl.deleteLeftPrimaryKeyTableMappings(leftPrimaryKey));

		rightModelListener.assertOnBeforeRemoveAssociation(
			true, rightPrimaryKey1, Left.class.getName(), leftPrimaryKey);

		rightModelListener.assertOnAfterRemoveAssociation(
			true, rightPrimaryKey1, Left.class.getName(), leftPrimaryKey);

		ModelListenerRegistrationUtil.unregister(rightModelListener);

		// Database error, with both left and right model listeners

		leftModelListener = new LeftRecorderModelListener();

		ModelListenerRegistrationUtil.register(leftModelListener);

		rightModelListener = new RightRecorderModelListener();

		ModelListenerRegistrationUtil.register(rightModelListener);

		_mappingStore.put(leftPrimaryKey, new long[] {rightPrimaryKey1});

		MockDeleteLeftPrimaryKeyTableMappingsSqlUpdate
			mockDeleteLeftPrimaryKeyTableMappingsSqlUpdate =
				(MockDeleteLeftPrimaryKeyTableMappingsSqlUpdate)
					_tableMapperImpl.deleteLeftPrimaryKeyTableMappingsSqlUpdate;

		mockDeleteLeftPrimaryKeyTableMappingsSqlUpdate.setDatabaseError(true);

		try {
			_tableMapperImpl.deleteLeftPrimaryKeyTableMappings(leftPrimaryKey);

			Assert.fail();
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertSame(RuntimeException.class, throwable.getClass());

			Assert.assertEquals("Database error", throwable.getMessage());
		}
		finally {
			mockDeleteLeftPrimaryKeyTableMappingsSqlUpdate.setDatabaseError(
				false);

			_mappingStore.remove(leftPrimaryKey);
		}

		ModelListenerRegistrationUtil.unregister(leftModelListener);
		ModelListenerRegistrationUtil.unregister(rightModelListener);

		leftModelListener.assertOnBeforeRemoveAssociation(
			true, leftPrimaryKey, Right.class.getName(), rightPrimaryKey1);

		rightModelListener.assertOnBeforeRemoveAssociation(
			true, rightPrimaryKey1, Left.class.getName(), leftPrimaryKey);

		leftModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		rightModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);
	}

	@Test
	public void testDeleteRightPrimaryKeyTableMappings() {

		// Delete 0 entry

		long rightPrimaryKey = 1;

		Assert.assertEquals(
			0,
			_tableMapperImpl.deleteRightPrimaryKeyTableMappings(
				rightPrimaryKey));

		// Delete 1 entry

		long leftPrimaryKey1 = 2;

		_mappingStore.put(leftPrimaryKey1, new long[] {rightPrimaryKey});

		Assert.assertEquals(
			1,
			_tableMapperImpl.deleteRightPrimaryKeyTableMappings(
				rightPrimaryKey));

		// Delete 2 entries

		long leftPrimaryKey2 = 3;

		_mappingStore.put(leftPrimaryKey1, new long[] {rightPrimaryKey});
		_mappingStore.put(leftPrimaryKey2, new long[] {rightPrimaryKey});

		Assert.assertEquals(
			2,
			_tableMapperImpl.deleteRightPrimaryKeyTableMappings(
				rightPrimaryKey));

		// Delete 0 entry, with left model listener

		RecorderModelListener<Left> leftModelListener =
			new LeftRecorderModelListener();

		ModelListenerRegistrationUtil.register(leftModelListener);

		Assert.assertEquals(
			0,
			_tableMapperImpl.deleteRightPrimaryKeyTableMappings(
				rightPrimaryKey));

		leftModelListener.assertOnBeforeRemoveAssociation(
			false, null, null, null);

		leftModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		ModelListenerRegistrationUtil.unregister(leftModelListener);

		// Delete 0 entry, with right model listener

		RecorderModelListener<Right> rightModelListener =
			new RightRecorderModelListener();

		ModelListenerRegistrationUtil.register(rightModelListener);

		Assert.assertEquals(
			0,
			_tableMapperImpl.deleteRightPrimaryKeyTableMappings(
				rightPrimaryKey));

		rightModelListener.assertOnBeforeRemoveAssociation(
			false, null, null, null);

		rightModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		ModelListenerRegistrationUtil.unregister(rightModelListener);

		// Delete 1 entry, with left model listener

		leftModelListener = new LeftRecorderModelListener();

		ModelListenerRegistrationUtil.register(leftModelListener);

		_mappingStore.put(leftPrimaryKey1, new long[] {rightPrimaryKey});

		Assert.assertEquals(
			1,
			_tableMapperImpl.deleteRightPrimaryKeyTableMappings(
				rightPrimaryKey));

		leftModelListener.assertOnBeforeRemoveAssociation(
			true, leftPrimaryKey1, Right.class.getName(), rightPrimaryKey);

		leftModelListener.assertOnAfterRemoveAssociation(
			true, leftPrimaryKey1, Right.class.getName(), rightPrimaryKey);

		ModelListenerRegistrationUtil.unregister(leftModelListener);

		// Delete 1 entry, with right model listener

		rightModelListener = new RightRecorderModelListener();

		ModelListenerRegistrationUtil.register(rightModelListener);

		_mappingStore.put(leftPrimaryKey1, new long[] {rightPrimaryKey});

		Assert.assertEquals(
			1,
			_tableMapperImpl.deleteRightPrimaryKeyTableMappings(
				rightPrimaryKey));

		rightModelListener.assertOnBeforeRemoveAssociation(
			true, rightPrimaryKey, Left.class.getName(), leftPrimaryKey1);

		rightModelListener.assertOnAfterRemoveAssociation(
			true, rightPrimaryKey, Left.class.getName(), leftPrimaryKey1);

		ModelListenerRegistrationUtil.unregister(rightModelListener);

		// Database error, with both left and right model listeners

		leftModelListener = new LeftRecorderModelListener();

		ModelListenerRegistrationUtil.register(leftModelListener);

		rightModelListener = new RightRecorderModelListener();

		ModelListenerRegistrationUtil.register(rightModelListener);

		_mappingStore.put(leftPrimaryKey1, new long[] {rightPrimaryKey});

		MockDeleteRightPrimaryKeyTableMappingsSqlUpdate
			mockDeleteRightPrimaryKeyTableMappingsSqlUpdate =
				(MockDeleteRightPrimaryKeyTableMappingsSqlUpdate)
					_tableMapperImpl.
						deleteRightPrimaryKeyTableMappingsSqlUpdate;

		mockDeleteRightPrimaryKeyTableMappingsSqlUpdate.setDatabaseError(true);

		try {
			_tableMapperImpl.deleteRightPrimaryKeyTableMappings(
				rightPrimaryKey);

			Assert.fail();
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertSame(RuntimeException.class, throwable.getClass());

			Assert.assertEquals("Database error", throwable.getMessage());
		}
		finally {
			mockDeleteRightPrimaryKeyTableMappingsSqlUpdate.setDatabaseError(
				false);

			_mappingStore.remove(rightPrimaryKey);
		}

		ModelListenerRegistrationUtil.unregister(leftModelListener);
		ModelListenerRegistrationUtil.unregister(rightModelListener);

		leftModelListener.assertOnBeforeRemoveAssociation(
			true, leftPrimaryKey1, Right.class.getName(), rightPrimaryKey);

		rightModelListener.assertOnBeforeRemoveAssociation(
			true, rightPrimaryKey, Left.class.getName(), leftPrimaryKey1);

		leftModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		rightModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);
	}

	@Test
	public void testDeleteTableMapping() {

		// No such table mapping

		long leftPrimaryKey = 1;
		long rightPrimaryKey = 2;

		Assert.assertFalse(
			_tableMapperImpl.deleteTableMapping(
				leftPrimaryKey, rightPrimaryKey));

		// Success, without model listener

		_mappingStore.put(leftPrimaryKey, new long[] {rightPrimaryKey});

		Assert.assertTrue(
			_tableMapperImpl.deleteTableMapping(
				leftPrimaryKey, rightPrimaryKey));

		// Success, with model listener

		RecorderModelListener<Left> leftModelListener =
			new LeftRecorderModelListener();

		_leftBasePersistence.registerListener(leftModelListener);

		RecorderModelListener<Right> rightModelListener =
			new RightRecorderModelListener();

		_rightBasePersistence.registerListener(rightModelListener);

		_mappingStore.put(leftPrimaryKey, new long[] {rightPrimaryKey});

		Assert.assertTrue(
			_tableMapperImpl.deleteTableMapping(
				leftPrimaryKey, rightPrimaryKey));

		leftModelListener.assertOnBeforeRemoveAssociation(
			true, leftPrimaryKey, Right.class.getName(), rightPrimaryKey);

		rightModelListener.assertOnBeforeRemoveAssociation(
			true, rightPrimaryKey, Left.class.getName(), leftPrimaryKey);

		leftModelListener.assertOnAfterRemoveAssociation(
			true, leftPrimaryKey, Right.class.getName(), rightPrimaryKey);

		rightModelListener.assertOnAfterRemoveAssociation(
			true, rightPrimaryKey, Left.class.getName(), leftPrimaryKey);

		_leftBasePersistence.unregisterListener(leftModelListener);

		_rightBasePersistence.unregisterListener(rightModelListener);

		// Database error, with model listener

		leftModelListener = new LeftRecorderModelListener();

		_leftBasePersistence.registerListener(leftModelListener);

		rightModelListener = new RightRecorderModelListener();

		_rightBasePersistence.registerListener(rightModelListener);

		_mappingStore.put(leftPrimaryKey, new long[] {rightPrimaryKey});

		MockDeleteMappingSqlUpdate mockDeleteSqlUpdate =
			(MockDeleteMappingSqlUpdate)
				_tableMapperImpl.deleteTableMappingSqlUpdate;

		mockDeleteSqlUpdate.setDatabaseError(true);

		try {
			_tableMapperImpl.deleteTableMapping(
				leftPrimaryKey, rightPrimaryKey);

			Assert.fail();
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertSame(RuntimeException.class, throwable.getClass());

			Assert.assertEquals("Database error", throwable.getMessage());
		}
		finally {
			mockDeleteSqlUpdate.setDatabaseError(false);
			_mappingStore.remove(leftPrimaryKey);
		}

		leftModelListener.assertOnBeforeRemoveAssociation(
			true, leftPrimaryKey, Right.class.getName(), rightPrimaryKey);

		rightModelListener.assertOnBeforeRemoveAssociation(
			true, rightPrimaryKey, Left.class.getName(), leftPrimaryKey);

		leftModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		rightModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		_leftBasePersistence.unregisterListener(leftModelListener);

		_rightBasePersistence.unregisterListener(rightModelListener);

		// Phantom delete, with model listener

		leftModelListener = new LeftRecorderModelListener();

		_leftBasePersistence.registerListener(leftModelListener);

		rightModelListener = new RightRecorderModelListener();

		_rightBasePersistence.registerListener(rightModelListener);

		PortalCache<Long, long[]> leftToRightPortalCache =
			_tableMapperImpl.leftToRightPortalCache;

		leftToRightPortalCache.put(
			leftPrimaryKey, new long[] {rightPrimaryKey});

		Assert.assertFalse(
			_tableMapperImpl.deleteTableMapping(
				leftPrimaryKey, rightPrimaryKey));

		leftModelListener.assertOnBeforeRemoveAssociation(
			true, leftPrimaryKey, Right.class.getName(), rightPrimaryKey);

		rightModelListener.assertOnBeforeRemoveAssociation(
			true, rightPrimaryKey, Left.class.getName(), leftPrimaryKey);

		leftModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		rightModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		_leftBasePersistence.unregisterListener(leftModelListener);

		_rightBasePersistence.unregisterListener(rightModelListener);
	}

	@Test
	public void testDeleteTableMappings() {
		long companyId = 0;
		long leftPrimaryKey1 = 1;
		long leftPrimaryKey2 = 2;
		long rightPrimaryKey1 = 3;
		long rightPrimaryKey2 = 4;

		Assert.assertArrayEquals(
			new long[0],
			_tableMapperImpl.deleteTableMappings(
				leftPrimaryKey1, new long[] {rightPrimaryKey1}));
		Assert.assertTrue(
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey1, rightPrimaryKey1));
		Assert.assertArrayEquals(
			new long[] {rightPrimaryKey1},
			_tableMapperImpl.deleteTableMappings(
				leftPrimaryKey1,
				new long[] {rightPrimaryKey1, rightPrimaryKey2}));
		Assert.assertTrue(
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey1, rightPrimaryKey1));
		Assert.assertTrue(
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey1, rightPrimaryKey2));
		Assert.assertArrayEquals(
			new long[] {rightPrimaryKey1, rightPrimaryKey2},
			_tableMapperImpl.deleteTableMappings(
				leftPrimaryKey1,
				new long[] {rightPrimaryKey1, rightPrimaryKey2}));
		Assert.assertArrayEquals(
			new long[0],
			_tableMapperImpl.deleteTableMappings(
				new long[] {leftPrimaryKey1}, rightPrimaryKey1));
		Assert.assertTrue(
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey1, rightPrimaryKey1));
		Assert.assertArrayEquals(
			new long[] {leftPrimaryKey1},
			_tableMapperImpl.deleteTableMappings(
				new long[] {leftPrimaryKey1, leftPrimaryKey2},
				rightPrimaryKey1));
		Assert.assertTrue(
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey1, rightPrimaryKey1));
		Assert.assertTrue(
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey2, rightPrimaryKey1));
		Assert.assertArrayEquals(
			new long[] {leftPrimaryKey1, leftPrimaryKey2},
			_tableMapperImpl.deleteTableMappings(
				new long[] {leftPrimaryKey1, leftPrimaryKey2},
				rightPrimaryKey1));

		PortalCache<Long, long[]> leftToRightPortalCache =
			_tableMapperImpl.leftToRightPortalCache;

		leftToRightPortalCache.put(
			leftPrimaryKey1, new long[] {rightPrimaryKey1});

		Assert.assertArrayEquals(
			new long[0],
			_tableMapperImpl.deleteTableMappings(
				leftPrimaryKey1, new long[] {rightPrimaryKey1}));
		Assert.assertTrue(
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey1, rightPrimaryKey1));
		Assert.assertArrayEquals(
			new long[] {rightPrimaryKey1},
			_tableMapperImpl.deleteTableMappings(
				leftPrimaryKey1,
				new long[] {rightPrimaryKey1, rightPrimaryKey2}));

		PortalCache<Long, long[]> rightToLeftPortalCache =
			_tableMapperImpl.rightToLeftPortalCache;

		rightToLeftPortalCache.put(
			rightPrimaryKey1, new long[] {leftPrimaryKey1});

		Assert.assertArrayEquals(
			new long[0],
			_tableMapperImpl.deleteTableMappings(
				new long[] {leftPrimaryKey1}, rightPrimaryKey1));
		Assert.assertTrue(
			_tableMapperImpl.addTableMapping(
				companyId, leftPrimaryKey1, rightPrimaryKey1));
		Assert.assertArrayEquals(
			new long[] {leftPrimaryKey1},
			_tableMapperImpl.deleteTableMappings(
				new long[] {leftPrimaryKey1, leftPrimaryKey2},
				rightPrimaryKey1));
	}

	@Test
	public void testDestroy() {
		testDestroy(_tableMapperImpl);
	}

	@Test
	public void testDestroyReverse() {
		testDestroy(new ReverseTableMapper<Right, Left>(_tableMapperImpl));
	}

	@Test
	public void testGetLeftBaseModels() {

		// Get 0 result

		long rightPrimaryKey = 1;

		List<Left> lefts = _tableMapperImpl.getLeftBaseModels(
			rightPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertSame(Collections.emptyList(), lefts);

		PortalCache<Long, long[]> rightToLeftPortalCache =
			_tableMapperImpl.rightToLeftPortalCache;

		rightToLeftPortalCache.remove(rightPrimaryKey);

		// Get 1 result

		long leftPrimaryKey1 = 2;

		_mappingStore.put(leftPrimaryKey1, new long[] {rightPrimaryKey});

		lefts = _tableMapperImpl.getLeftBaseModels(
			rightPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(lefts.toString(), 1, lefts.size());

		Left left1 = lefts.get(0);

		Assert.assertEquals(leftPrimaryKey1, left1.getPrimaryKeyObj());

		rightToLeftPortalCache.remove(rightPrimaryKey);

		// Get 2 results, unsorted

		long leftPrimaryKey2 = 3;

		_mappingStore.put(leftPrimaryKey1, new long[] {rightPrimaryKey});
		_mappingStore.put(leftPrimaryKey2, new long[] {rightPrimaryKey});

		lefts = _tableMapperImpl.getLeftBaseModels(
			rightPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(lefts.toString(), 2, lefts.size());

		left1 = lefts.get(0);
		Left left2 = lefts.get(1);

		Assert.assertEquals(leftPrimaryKey1, left1.getPrimaryKeyObj());
		Assert.assertEquals(leftPrimaryKey2, left2.getPrimaryKeyObj());

		rightToLeftPortalCache.remove(rightPrimaryKey);

		// Get 2 results, sorted

		_mappingStore.put(leftPrimaryKey1, new long[] {rightPrimaryKey});
		_mappingStore.put(leftPrimaryKey2, new long[] {rightPrimaryKey});

		lefts = _tableMapperImpl.getLeftBaseModels(
			rightPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new OrderByComparator<Left>() {

				@Override
				public int compare(Left left1, Left left2) {
					Long leftPrimaryKey1 = (Long)left1.getPrimaryKeyObj();
					Long leftPrimaryKey2 = (Long)left2.getPrimaryKeyObj();

					return leftPrimaryKey2.compareTo(leftPrimaryKey1);
				}

			});

		Assert.assertEquals(lefts.toString(), 2, lefts.size());

		left1 = lefts.get(0);
		left2 = lefts.get(1);

		Assert.assertEquals(leftPrimaryKey2, left1.getPrimaryKeyObj());
		Assert.assertEquals(leftPrimaryKey1, left2.getPrimaryKeyObj());

		rightToLeftPortalCache.remove(rightPrimaryKey);

		// Get 3 results, paginated

		long leftPrimaryKey3 = 4;

		_mappingStore.put(leftPrimaryKey1, new long[] {rightPrimaryKey});
		_mappingStore.put(leftPrimaryKey2, new long[] {rightPrimaryKey});
		_mappingStore.put(leftPrimaryKey3, new long[] {rightPrimaryKey});

		lefts = _tableMapperImpl.getLeftBaseModels(rightPrimaryKey, 1, 2, null);

		Assert.assertEquals(lefts.toString(), 1, lefts.size());

		Left left3 = lefts.get(0);

		Assert.assertEquals(leftPrimaryKey2, left3.getPrimaryKeyObj());

		rightToLeftPortalCache.remove(rightPrimaryKey);

		// No such model exception

		_leftBasePersistence.setNoSuchModelException(true);

		try {
			_tableMapperImpl.getLeftBaseModels(
				rightPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertSame(NoSuchModelException.class, throwable.getClass());

			Assert.assertEquals(
				String.valueOf(leftPrimaryKey1), throwable.getMessage());
		}
		finally {
			_leftBasePersistence.setNoSuchModelException(false);
		}
	}

	@Test
	public void testGetLeftPrimaryKeys() {

		// Get 0 result

		long rightPrimaryKey = 1;

		long[] leftPrimaryKeys = _tableMapperImpl.getLeftPrimaryKeys(
			rightPrimaryKey);

		Assert.assertEquals(
			Arrays.toString(leftPrimaryKeys), 0, leftPrimaryKeys.length);

		// Hit cache

		Assert.assertSame(
			leftPrimaryKeys,
			_tableMapperImpl.getLeftPrimaryKeys(rightPrimaryKey));

		// Get 2 results, ensure ordered

		long leftPrimaryKey1 = 3;
		long leftPrimaryKey2 = 2;

		PortalCache<Long, long[]> rightToLeftPortalCache =
			_tableMapperImpl.rightToLeftPortalCache;

		rightToLeftPortalCache.remove(rightPrimaryKey);

		_mappingStore.put(leftPrimaryKey1, new long[] {rightPrimaryKey});
		_mappingStore.put(leftPrimaryKey2, new long[] {rightPrimaryKey});

		Assert.assertArrayEquals(
			new long[] {leftPrimaryKey2, leftPrimaryKey1},
			_tableMapperImpl.getLeftPrimaryKeys(rightPrimaryKey));

		// Database error

		rightToLeftPortalCache.remove(rightPrimaryKey);

		MockGetLeftPrimaryKeysSqlQuery
			mockGetLeftPrimaryKeysByRightPrimaryKeyMappingSqlQuery =
				(MockGetLeftPrimaryKeysSqlQuery)
					_tableMapperImpl.getLeftPrimaryKeysSqlQuery;

		mockGetLeftPrimaryKeysByRightPrimaryKeyMappingSqlQuery.setDatabaseError(
			true);

		try {
			_tableMapperImpl.getLeftPrimaryKeys(rightPrimaryKey);
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertSame(RuntimeException.class, throwable.getClass());

			Assert.assertEquals("Database error", throwable.getMessage());
		}
		finally {
			mockGetLeftPrimaryKeysByRightPrimaryKeyMappingSqlQuery.
				setDatabaseError(false);
		}
	}

	@Test
	public void testGetRightBaseModels() {

		// Get 0 result

		long leftPrimaryKey = 1;

		List<Right> rights = _tableMapperImpl.getRightBaseModels(
			leftPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertSame(Collections.emptyList(), rights);

		PortalCache<Long, long[]> leftToRightPortalCache =
			_tableMapperImpl.leftToRightPortalCache;

		leftToRightPortalCache.remove(leftPrimaryKey);

		// Get 1 result

		long rightPrimaryKey1 = 2;

		_mappingStore.put(leftPrimaryKey, new long[] {rightPrimaryKey1});

		rights = _tableMapperImpl.getRightBaseModels(
			leftPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(rights.toString(), 1, rights.size());

		Right right1 = rights.get(0);

		Assert.assertEquals(rightPrimaryKey1, right1.getPrimaryKeyObj());

		leftToRightPortalCache.remove(leftPrimaryKey);

		// Get 2 results, unsorted

		long rightPrimaryKey2 = 3;

		_mappingStore.put(
			leftPrimaryKey, new long[] {rightPrimaryKey2, rightPrimaryKey1});

		rights = _tableMapperImpl.getRightBaseModels(
			leftPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(rights.toString(), 2, rights.size());

		right1 = rights.get(0);
		Right right2 = rights.get(1);

		Assert.assertEquals(rightPrimaryKey1, right1.getPrimaryKeyObj());
		Assert.assertEquals(rightPrimaryKey2, right2.getPrimaryKeyObj());

		leftToRightPortalCache.remove(leftPrimaryKey);

		// Get 2 results, sorted

		_mappingStore.put(
			leftPrimaryKey, new long[] {rightPrimaryKey2, rightPrimaryKey1});

		rights = _tableMapperImpl.getRightBaseModels(
			leftPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new OrderByComparator<Right>() {

				@Override
				public int compare(Right right1, Right right2) {
					Long rightPrimaryKey1 = (Long)right1.getPrimaryKeyObj();
					Long rightPrimaryKey2 = (Long)right2.getPrimaryKeyObj();

					return rightPrimaryKey2.compareTo(rightPrimaryKey1);
				}

			});

		Assert.assertEquals(rights.toString(), 2, rights.size());

		right1 = rights.get(0);
		right2 = rights.get(1);

		Assert.assertEquals(rightPrimaryKey2, right1.getPrimaryKeyObj());
		Assert.assertEquals(rightPrimaryKey1, right2.getPrimaryKeyObj());

		leftToRightPortalCache.remove(leftPrimaryKey);

		// Get 3 results, paginated

		long rightPrimaryKey3 = 4;

		_mappingStore.put(
			leftPrimaryKey,
			new long[] {rightPrimaryKey3, rightPrimaryKey2, rightPrimaryKey1});

		rights = _tableMapperImpl.getRightBaseModels(
			leftPrimaryKey, 1, 2, null);

		Assert.assertEquals(rights.toString(), 1, rights.size());

		Right right3 = rights.get(0);

		Assert.assertEquals(rightPrimaryKey2, right3.getPrimaryKeyObj());

		leftToRightPortalCache.remove(leftPrimaryKey);

		// No such model exception

		_rightBasePersistence.setNoSuchModelException(true);

		try {
			_tableMapperImpl.getRightBaseModels(
				leftPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertSame(NoSuchModelException.class, throwable.getClass());

			Assert.assertEquals(
				String.valueOf(rightPrimaryKey1), throwable.getMessage());
		}
		finally {
			_rightBasePersistence.setNoSuchModelException(false);
		}
	}

	@Test
	public void testGetRightPrimaryKeys() {

		// Get 0 result

		long leftPrimaryKey = 1;

		long[] rightPrimaryKeys = _tableMapperImpl.getRightPrimaryKeys(
			leftPrimaryKey);

		Assert.assertEquals(
			Arrays.toString(rightPrimaryKeys), 0, rightPrimaryKeys.length);

		// Hit cache

		Assert.assertSame(
			rightPrimaryKeys,
			_tableMapperImpl.getRightPrimaryKeys(leftPrimaryKey));

		// Get 2 results, ensure ordered

		long rightPrimaryKey1 = 3;
		long rightPrimaryKey2 = 2;

		PortalCache<Long, long[]> leftToRightPortalCache =
			_tableMapperImpl.leftToRightPortalCache;

		leftToRightPortalCache.remove(leftPrimaryKey);

		_mappingStore.put(
			leftPrimaryKey, new long[] {rightPrimaryKey1, rightPrimaryKey2});

		Assert.assertArrayEquals(
			new long[] {rightPrimaryKey2, rightPrimaryKey1},
			_tableMapperImpl.getRightPrimaryKeys(leftPrimaryKey));

		// Database error

		leftToRightPortalCache.remove(leftPrimaryKey);

		MockGetRightPrimaryKeysSqlQuery
			mockGetRightPrimaryKeysByLeftPrimaryKeyMappingSqlQuery =
				(MockGetRightPrimaryKeysSqlQuery)
					_tableMapperImpl.getRightPrimaryKeysSqlQuery;

		mockGetRightPrimaryKeysByLeftPrimaryKeyMappingSqlQuery.setDatabaseError(
			true);

		try {
			_tableMapperImpl.getRightPrimaryKeys(leftPrimaryKey);
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertSame(RuntimeException.class, throwable.getClass());

			Assert.assertEquals("Database error", throwable.getMessage());
		}
		finally {
			mockGetRightPrimaryKeysByLeftPrimaryKeyMappingSqlQuery.
				setDatabaseError(false);
		}
	}

	@Test
	public void testMatches() {
		Assert.assertTrue(
			_tableMapperImpl.matches(_LEFT_COLUMN_NAME, _RIGHT_COLUMN_NAME));
		Assert.assertFalse(
			_tableMapperImpl.matches(_LEFT_COLUMN_NAME, _LEFT_COLUMN_NAME));
		Assert.assertFalse(
			_tableMapperImpl.matches(_RIGHT_COLUMN_NAME, _LEFT_COLUMN_NAME));
	}

	@Test
	public void testReverseTableMapper() {
		Class<?> clazz = TableMapper.class;

		RecordInvocationHandler recordInvocationHandler =
			new RecordInvocationHandler();

		TableMapper<Left, Right> tableMapper =
			(TableMapper<Left, Right>)ProxyUtil.newProxyInstance(
				clazz.getClassLoader(), new Class<?>[] {TableMapper.class},
				recordInvocationHandler);

		ReverseTableMapper<Right, Left> reverseTableMapper =
			new ReverseTableMapper<>(tableMapper);

		recordInvocationHandler.setTableMapper(reverseTableMapper);

		reverseTableMapper.addTableMapping(0, 1, 2);

		recordInvocationHandler.assertCall("addTableMapping", 0L, 2L, 1L);

		reverseTableMapper.addTableMappings(0, 1, new long[] {2});

		recordInvocationHandler.assertCall(
			"addTableMappings", 0L, new long[] {2}, 1L);

		reverseTableMapper.addTableMappings(0, new long[] {1}, 2L);

		recordInvocationHandler.assertCall(
			"addTableMappings", 0L, 2L, new long[] {1});

		reverseTableMapper.containsTableMapping(1, 2);

		recordInvocationHandler.assertCall("containsTableMapping", 2L, 1L);

		reverseTableMapper.deleteRightPrimaryKeyTableMappings(2);

		recordInvocationHandler.assertCall(
			"deleteLeftPrimaryKeyTableMappings", 2L);

		reverseTableMapper.deleteLeftPrimaryKeyTableMappings(1);

		recordInvocationHandler.assertCall(
			"deleteRightPrimaryKeyTableMappings", 1L);

		reverseTableMapper.deleteTableMapping(1, 2);

		recordInvocationHandler.assertCall("deleteTableMapping", 2L, 1L);

		reverseTableMapper.deleteTableMappings(1, new long[] {2});

		recordInvocationHandler.assertCall(
			"deleteTableMappings", new long[] {2}, 1L);

		reverseTableMapper.deleteTableMappings(new long[] {1}, 2);

		recordInvocationHandler.assertCall(
			"deleteTableMappings", 2L, new long[] {1});

		reverseTableMapper.getRightBaseModels(1, 2, 3, null);

		recordInvocationHandler.assertCall("getLeftBaseModels", 1L, 2, 3, null);

		reverseTableMapper.getRightPrimaryKeys(1);

		recordInvocationHandler.assertCall("getLeftPrimaryKeys", 1L);

		reverseTableMapper.getLeftBaseModels(2, 2, 3, null);

		recordInvocationHandler.assertCall(
			"getRightBaseModels", 2L, 2, 3, null);

		reverseTableMapper.getLeftPrimaryKeys(2);

		recordInvocationHandler.assertCall("getRightPrimaryKeys", 2L);

		Assert.assertSame(
			tableMapper, reverseTableMapper.getReverseTableMapper());

		reverseTableMapper.matches("left", "right");

		recordInvocationHandler.assertCall("matches", "right", "left");
	}

	@Test
	public void testTableMapperFactory1() {

		// Initial empty

		Map<String, TableMapper<?, ?>> tableMappers =
			ReflectionTestUtil.getFieldValue(
				TableMapperFactory.class, "_tableMappers");

		Assert.assertTrue(tableMappers.toString(), tableMappers.isEmpty());

		// Create

		TableMapper<Left, Right> tableMapper =
			TableMapperFactory.getTableMapper(
				_TABLE_NAME, _COMPANY_COLUMN_NAME, _LEFT_COLUMN_NAME,
				_RIGHT_COLUMN_NAME, _leftBasePersistence,
				_rightBasePersistence);

		Assert.assertEquals(tableMappers.toString(), 1, tableMappers.size());
		Assert.assertSame(tableMapper, tableMappers.get(_TABLE_NAME));

		TableMapper<Right, Left> reverseTableMapper =
			tableMapper.getReverseTableMapper();

		Assert.assertNotNull(reverseTableMapper);

		// Hit cache

		Assert.assertSame(
			tableMapper,
			TableMapperFactory.getTableMapper(
				_TABLE_NAME, _COMPANY_COLUMN_NAME, _LEFT_COLUMN_NAME,
				_RIGHT_COLUMN_NAME, _leftBasePersistence,
				_rightBasePersistence));

		// Reverse mapping table

		Assert.assertSame(
			reverseTableMapper,
			TableMapperFactory.getTableMapper(
				_TABLE_NAME, _COMPANY_COLUMN_NAME, _RIGHT_COLUMN_NAME,
				_LEFT_COLUMN_NAME, _rightBasePersistence,
				_leftBasePersistence));

		// Remove

		TableMapperFactory.removeTableMapper(_TABLE_NAME);

		Assert.assertTrue(tableMappers.toString(), tableMappers.isEmpty());

		TableMapperFactory.removeTableMapper(_TABLE_NAME);

		Assert.assertTrue(tableMappers.toString(), tableMappers.isEmpty());
	}

	@Test
	public void testTableMapperFactory2() {
		String leftKey = "tableKey#" + _LEFT_COLUMN_NAME;
		String rightKey = "tableKey#" + _RIGHT_COLUMN_NAME;

		// Initial empty

		Map<String, TableMapper<?, ?>> tableMappers =
			ReflectionTestUtil.getFieldValue(
				TableMapperFactory.class, "_tableMappers");

		Assert.assertTrue(tableMappers.toString(), tableMappers.isEmpty());

		// Create

		TableMapperImpl<Left, Right> tableMapperImpl =
			(TableMapperImpl<Left, Right>)TableMapperFactory.getTableMapper(
				leftKey, _TABLE_NAME, _COMPANY_COLUMN_NAME, _LEFT_COLUMN_NAME,
				_RIGHT_COLUMN_NAME, _leftBasePersistence, Right.class);

		Assert.assertEquals(tableMappers.toString(), 1, tableMappers.size());
		Assert.assertSame(tableMapperImpl, tableMappers.get(leftKey));

		long leftPrimaryKey = 1;

		try {
			tableMapperImpl.getRightPrimaryKeysSqlQuery =
				_tableMapperImpl.getRightPrimaryKeysSqlQuery;

			_mappingStore.put(leftPrimaryKey, new long[2]);

			tableMapperImpl.getRightBaseModels(
				leftPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			Assert.assertEquals(
				"The TableMapper only supports BaseModel queries on one side",
				unsupportedOperationException.getMessage());
		}
		finally {
			_mappingStore.remove(leftPrimaryKey);
		}

		// Hit cache

		Assert.assertSame(
			tableMapperImpl,
			TableMapperFactory.getTableMapper(
				leftKey, _TABLE_NAME, _COMPANY_COLUMN_NAME, _LEFT_COLUMN_NAME,
				_RIGHT_COLUMN_NAME, _leftBasePersistence, Right.class));

		// Miss cache

		TableMapperImpl<Right, Left> rightTableMapperImpl =
			(TableMapperImpl<Right, Left>)TableMapperFactory.getTableMapper(
				rightKey, _TABLE_NAME, _COMPANY_COLUMN_NAME, _RIGHT_COLUMN_NAME,
				_LEFT_COLUMN_NAME, _rightBasePersistence, Left.class);

		Assert.assertNotSame(tableMapperImpl, rightTableMapperImpl);
		Assert.assertSame(rightTableMapperImpl, tableMappers.get(rightKey));

		Assert.assertEquals(tableMappers.toString(), 2, tableMappers.size());
		Assert.assertNotSame(tableMapperImpl, tableMappers.get(rightKey));

		// Reverse mapping table

		Assert.assertNotSame(
			tableMapperImpl.getReverseTableMapper(),
			TableMapperFactory.getTableMapper(
				rightKey, _TABLE_NAME, _COMPANY_COLUMN_NAME, _RIGHT_COLUMN_NAME,
				_LEFT_COLUMN_NAME, _rightBasePersistence, Left.class));

		// Portal caches

		Assert.assertSame(
			tableMapperImpl.leftToRightPortalCache,
			rightTableMapperImpl.rightToLeftPortalCache);
		Assert.assertSame(
			tableMapperImpl.rightToLeftPortalCache,
			rightTableMapperImpl.leftToRightPortalCache);

		// Remove

		TableMapperFactory.removeTableMapper(leftKey);

		Assert.assertEquals(tableMappers.toString(), 1, tableMappers.size());

		TableMapperFactory.removeTableMapper(rightKey);

		Assert.assertTrue(tableMappers.toString(), tableMappers.isEmpty());
	}

	@Test
	public void testTableMapperFactoryCTModel() {
		MockBasePersistence<CTLeft> ctLeftBasePersistence =
			new MockBasePersistence<>(CTLeft.class);

		ctLeftBasePersistence.setDataSource(_dataSource);

		MockBasePersistence<CTRight> ctRightBasePersistence =
			new MockBasePersistence<>(CTRight.class);

		ctRightBasePersistence.setDataSource(_dataSource);

		TableMapper<CTLeft, Right> tableMapper1 =
			TableMapperFactory.getTableMapper(
				_TABLE_NAME, _COMPANY_COLUMN_NAME, _LEFT_COLUMN_NAME,
				_RIGHT_COLUMN_NAME, ctLeftBasePersistence,
				_rightBasePersistence);

		Assert.assertFalse(tableMapper1 instanceof CTTableMapper);

		TableMapperFactory.removeTableMapper(_TABLE_NAME);

		TableMapper<Left, CTRight> tableMapper2 =
			TableMapperFactory.getTableMapper(
				_TABLE_NAME, _COMPANY_COLUMN_NAME, _LEFT_COLUMN_NAME,
				_RIGHT_COLUMN_NAME, _leftBasePersistence,
				ctRightBasePersistence);

		Assert.assertFalse(tableMapper2 instanceof CTTableMapper);

		TableMapperFactory.removeTableMapper(_TABLE_NAME);

		TableMapper<CTLeft, CTRight> ctTableMapper =
			TableMapperFactory.getTableMapper(
				_TABLE_NAME, _COMPANY_COLUMN_NAME, _LEFT_COLUMN_NAME,
				_RIGHT_COLUMN_NAME, ctLeftBasePersistence,
				ctRightBasePersistence);

		Assert.assertTrue(ctTableMapper instanceof CTTableMapper);
	}

	@Test
	public void testTableMapperFactoryCache() {
		Set<String> cachelessMappingTableNames =
			ReflectionTestUtil.getAndSetFieldValue(
				TableMapperFactory.class, "_cachelessMappingTableNames",
				new HashSet<String>() {

					@Override
					public boolean contains(Object object) {
						return true;
					}

				});

		try {
			testTableMapperFactory1();
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				TableMapperFactory.class, "_cachelessMappingTableNames",
				cachelessMappingTableNames);
		}
	}

	protected void testDestroy(TableMapper<?, ?> tableMapper) {
		Map<String, PortalCache<?, ?>> portalCaches =
			ReflectionTestUtil.getFieldValue(
				PortalCacheManagerProvider.getPortalCacheManager(
					PortalCacheManagerNames.MULTI_VM),
				"_dynamicPortalCaches");

		Assert.assertEquals(portalCaches.toString(), 2, portalCaches.size());

		if (tableMapper instanceof ReverseTableMapper) {
			Assert.assertSame(
				ReflectionTestUtil.getFieldValue(
					tableMapper.getReverseTableMapper(),
					"leftToRightPortalCache"),
				portalCaches.get(
					StringBundler.concat(
						TableMapper.class.getName(), "-", _TABLE_NAME, "-",
						_LEFT_COLUMN_NAME, "-To-", _RIGHT_COLUMN_NAME)));
			Assert.assertSame(
				ReflectionTestUtil.getFieldValue(
					tableMapper.getReverseTableMapper(),
					"rightToLeftPortalCache"),
				portalCaches.get(
					StringBundler.concat(
						TableMapper.class.getName(), "-", _TABLE_NAME, "-",
						_RIGHT_COLUMN_NAME, "-To-", _LEFT_COLUMN_NAME)));
		}
		else {
			Assert.assertSame(
				ReflectionTestUtil.getFieldValue(
					tableMapper, "leftToRightPortalCache"),
				portalCaches.get(
					StringBundler.concat(
						TableMapper.class.getName(), "-", _TABLE_NAME, "-",
						_LEFT_COLUMN_NAME, "-To-", _RIGHT_COLUMN_NAME)));
			Assert.assertSame(
				ReflectionTestUtil.getFieldValue(
					tableMapper, "rightToLeftPortalCache"),
				portalCaches.get(
					StringBundler.concat(
						TableMapper.class.getName(), "-", _TABLE_NAME, "-",
						_RIGHT_COLUMN_NAME, "-To-", _LEFT_COLUMN_NAME)));
		}

		tableMapper.destroy();

		Assert.assertTrue(portalCaches.toString(), portalCaches.isEmpty());
	}

	private void _mockMappingSqlQueryFactoryUtil() {
		_mappingSqlQueryFactoryUtilMockedStatic = Mockito.mockStatic(
			MappingSqlQueryFactoryUtil.class);

		_mappingSqlQueryFactoryUtilMockedStatic.when(
			() -> MappingSqlQueryFactoryUtil.getMappingSqlQuery(
				Mockito.any(), Mockito.anyString(), Mockito.any(),
				Mockito.any())
		).thenAnswer(
			invocation -> {
				InterceptedInvocation interceptedInvocation =
					(InterceptedInvocation)invocation;

				Object[] arguments = interceptedInvocation.getRawArguments();

				DataSource dataSource = (DataSource)arguments[0];
				String sql = (String)arguments[1];
				ParamSetter[] paramSetters = (ParamSetter[])arguments[3];

				if (sql.equals(
						StringBundler.concat(
							"SELECT ", _LEFT_COLUMN_NAME, " FROM ", _TABLE_NAME,
							" WHERE ", _RIGHT_COLUMN_NAME, " = ?"))) {

					return new MockGetLeftPrimaryKeysSqlQuery(
						dataSource, RowMapper.PRIMARY_KEY, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"SELECT ", _RIGHT_COLUMN_NAME, " FROM ",
							_TABLE_NAME, " WHERE ", _LEFT_COLUMN_NAME,
							" = ?"))) {

					return new MockGetRightPrimaryKeysSqlQuery(
						dataSource, RowMapper.PRIMARY_KEY, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"SELECT * FROM ", _TABLE_NAME, " WHERE ",
							_LEFT_COLUMN_NAME, " = ? AND ", _RIGHT_COLUMN_NAME,
							" = ?"))) {

					return new MockContainsTableMappingSQLQuery(
						dataSource, paramSetters);
				}

				if (sql.contains("ctCollectionId")) {
					return null;
				}

				throw new UnsupportedOperationException(sql);
			}
		);
	}

	private void _mockSqlUpdateFactoryUtil() {
		_sqlUpdateFactoryUtilMockedStatic = Mockito.mockStatic(
			SqlUpdateFactoryUtil.class);

		_sqlUpdateFactoryUtilMockedStatic.when(
			() -> SqlUpdateFactoryUtil.getSqlUpdate(
				Mockito.any(), Mockito.anyString(), Mockito.any())
		).thenAnswer(
			invocation -> {
				InterceptedInvocation interceptedInvocation =
					(InterceptedInvocation)invocation;

				Object[] arguments = interceptedInvocation.getRawArguments();

				DataSource dataSource = (DataSource)arguments[0];
				String sql = (String)arguments[1];
				ParamSetter[] paramSetters = (ParamSetter[])arguments[2];

				if (sql.equals(
						StringBundler.concat(
							"INSERT INTO ", _TABLE_NAME, " (",
							_COMPANY_COLUMN_NAME, ", ", _LEFT_COLUMN_NAME, ", ",
							_RIGHT_COLUMN_NAME, ") VALUES (?, ?, ?)"))) {

					return new MockAddMappingSqlUpdate(
						dataSource, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"INSERT INTO ", _TABLE_NAME, " (",
							_COMPANY_COLUMN_NAME, ", ", _RIGHT_COLUMN_NAME,
							", ", _LEFT_COLUMN_NAME, ") VALUES (?, ?, ?)"))) {

					return null;
				}

				if (sql.equals(
						StringBundler.concat(
							"DELETE FROM ", _TABLE_NAME, " WHERE ",
							_LEFT_COLUMN_NAME, " = ?"))) {

					return new MockDeleteLeftPrimaryKeyTableMappingsSqlUpdate(
						dataSource, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"DELETE FROM ", _TABLE_NAME, " WHERE ",
							_RIGHT_COLUMN_NAME, " = ?"))) {

					return new MockDeleteRightPrimaryKeyTableMappingsSqlUpdate(
						dataSource, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"DELETE FROM ", _TABLE_NAME, " WHERE ",
							_LEFT_COLUMN_NAME, " = ? AND ", _RIGHT_COLUMN_NAME,
							" = ?"))) {

					return new MockDeleteMappingSqlUpdate(
						dataSource, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"DELETE FROM ", _TABLE_NAME, " WHERE ",
							_RIGHT_COLUMN_NAME, " = ? AND ", _LEFT_COLUMN_NAME,
							" = ?")) ||
					sql.contains("ctCollectionId")) {

					return null;
				}

				throw new UnsupportedOperationException(sql);
			}
		);
	}

	private static final String _COMPANY_COLUMN_NAME = "companyId";

	private static final String _LEFT_COLUMN_NAME = "leftId";

	private static final String _RIGHT_COLUMN_NAME = "rightId";

	private static final String _TABLE_NAME = "Lefts_Rights";

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private DataSource _dataSource;
	private MockBasePersistence<Left> _leftBasePersistence;
	private MockedStatic<MappingSqlQueryFactoryUtil>
		_mappingSqlQueryFactoryUtilMockedStatic;
	private final Map<Long, long[]> _mappingStore = new HashMap<>();
	private MockBasePersistence<Right> _rightBasePersistence;
	private ServiceRegistration<FinderCache> _serviceRegistration;
	private MockedStatic<SqlUpdateFactoryUtil>
		_sqlUpdateFactoryUtilMockedStatic;
	private TableMapperImpl<Left, Right> _tableMapperImpl;

	private static class LeftRecorderModelListener
		extends RecorderModelListener<Left> {
	}

	private static class RecordInvocationHandler implements InvocationHandler {

		public void assertCall(String methodName, Object... args) {
			Object[] record = _records.get(methodName);

			Assert.assertArrayEquals(record, args);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) {
			_records.put(method.getName(), args);

			Class<?> returnType = method.getReturnType();

			if (returnType == boolean.class) {
				return false;
			}
			else if (returnType == int.class) {
				return 0;
			}
			else if (returnType == List.class) {
				return Collections.emptyList();
			}
			else if (returnType == long[].class) {
				return new long[0];
			}
			else if (returnType == TableMapper.class) {
				return _tableMapper;
			}

			return null;
		}

		public void setTableMapper(TableMapper<?, ?> tableMapper) {
			_tableMapper = tableMapper;
		}

		private final Map<String, Object[]> _records = new HashMap<>();
		private TableMapper<?, ?> _tableMapper;

	}

	private static class RecorderModelListener<T extends BaseModel<T>>
		extends BaseModelListener<T> {

		public void assertOnAfterAddAssociation(
			boolean called, Object classPK, String associationClassName,
			Object associationClassPK) {

			_assertCall(
				0, called, classPK, associationClassName, associationClassPK);
		}

		public void assertOnAfterRemoveAssociation(
			boolean called, Object classPK, String associationClassName,
			Object associationClassPK) {

			_assertCall(
				1, called, classPK, associationClassName, associationClassPK);
		}

		public void assertOnBeforeAddAssociation(
			boolean called, Object classPK, String associationClassName,
			Object associationClassPK) {

			_assertCall(
				2, called, classPK, associationClassName, associationClassPK);
		}

		public void assertOnBeforeRemoveAssociation(
			boolean called, Object classPK, String associationClassName,
			Object associationClassPK) {

			_assertCall(
				3, called, classPK, associationClassName, associationClassPK);
		}

		@Override
		public void onAfterAddAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK) {

			_record(0, classPK, associationClassName, associationClassPK);
		}

		@Override
		public void onAfterRemoveAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK) {

			_record(1, classPK, associationClassName, associationClassPK);
		}

		@Override
		public void onBeforeAddAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK) {

			_record(2, classPK, associationClassName, associationClassPK);
		}

		@Override
		public void onBeforeRemoveAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK) {

			_record(3, classPK, associationClassName, associationClassPK);
		}

		private void _assertCall(
			int index, boolean called, Object classPK,
			String associationClassName, Object associationClassPK) {

			if (called) {
				Assert.assertSame(_classPKs[index], classPK);
				Assert.assertEquals(
					_associationClassNames[index], associationClassName);
				Assert.assertSame(
					_associationClassPKs[index], associationClassPK);
			}
			else {
				Assert.assertFalse(
					"Called onAfterAddAssociation", _markers[index]);
			}
		}

		private void _record(
			int index, Object classPK, String associationClassName,
			Object associationClassPK) {

			_markers[index] = true;
			_classPKs[index] = classPK;
			_associationClassNames[index] = associationClassName;
			_associationClassPKs[index] = associationClassPK;
		}

		private final String[] _associationClassNames = new String[4];
		private final Object[] _associationClassPKs = new Object[4];
		private final Object[] _classPKs = new Object[4];
		private final boolean[] _markers = new boolean[4];

	}

	private static class RightRecorderModelListener
		extends RecorderModelListener<Right> {
	}

	private interface CTLeft extends CTLeftModel {
	}

	private interface CTLeftModel extends BaseModel<CTLeft>, CTModel<CTLeft> {
	}

	private interface CTRight extends CTRightModel {
	}

	private interface CTRightModel
		extends BaseModel<CTRight>, CTModel<CTRight> {
	}

	private class GetPrimaryKeyObjInvocationHandler
		implements InvocationHandler {

		public GetPrimaryKeyObjInvocationHandler(Serializable primaryKey) {
			_primaryKey = primaryKey;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) {
			String methodName = method.getName();

			if (methodName.equals("getPrimaryKeyObj")) {
				return _primaryKey;
			}

			if (methodName.equals("toString")) {
				return String.valueOf(_primaryKey);
			}

			throw new UnsupportedOperationException();
		}

		private final Serializable _primaryKey;

	}

	private interface Left extends LeftModel {
	}

	private interface LeftModel extends BaseModel<Left> {
	}

	private class MockAddMappingSqlUpdate implements SqlUpdate {

		public MockAddMappingSqlUpdate(
			DataSource dataSource, ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {
					ParamSetter.BIGINT, ParamSetter.BIGINT, ParamSetter.BIGINT
				},
				paramSetters);
		}

		@Override
		public int update(Object... params) {
			Assert.assertEquals(3, params.length);
			Assert.assertSame(Long.class, params[0].getClass());
			Assert.assertSame(Long.class, params[1].getClass());
			Assert.assertSame(Long.class, params[2].getClass());

			Long leftPrimaryKey = (Long)params[1];
			Long rightPrimaryKey = (Long)params[2];

			long[] rightPrimaryKeys = _mappingStore.get(leftPrimaryKey);

			if (rightPrimaryKeys == null) {
				rightPrimaryKeys = new long[1];

				rightPrimaryKeys[0] = rightPrimaryKey;

				_mappingStore.put(leftPrimaryKey, rightPrimaryKeys);
			}
			else if (ArrayUtil.contains(rightPrimaryKeys, rightPrimaryKey)) {
				throw new RuntimeException(
					StringBundler.concat(
						"Unique key violation for left primary key ",
						leftPrimaryKey, " and right primary key ",
						rightPrimaryKey));
			}
			else {
				rightPrimaryKeys = ArrayUtil.append(
					rightPrimaryKeys, rightPrimaryKey);

				_mappingStore.put(leftPrimaryKey, rightPrimaryKeys);
			}

			return 1;
		}

	}

	private class MockBasePersistence<T extends BaseModel<T>>
		extends BasePersistenceImpl<T, NoSuchModelException> {

		public MockBasePersistence(Class<T> clazz) {
			setModelClass(clazz);
		}

		@Override
		public T findByPrimaryKey(Serializable primaryKey)
			throws NoSuchModelException {

			if (_noSuchModelException) {
				throw new NoSuchModelException(primaryKey.toString());
			}

			Class<T> modelClass = getModelClass();

			return (T)ProxyUtil.newProxyInstance(
				modelClass.getClassLoader(), new Class<?>[] {modelClass},
				new GetPrimaryKeyObjInvocationHandler(primaryKey));
		}

		public void setNoSuchModelException(boolean noSuchModelException) {
			_noSuchModelException = noSuchModelException;
		}

		private boolean _noSuchModelException;

	}

	private class MockContainsTableMappingSQLQuery
		implements MappingSqlQuery<Integer> {

		public MockContainsTableMappingSQLQuery(
			DataSource dataSource, ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {ParamSetter.BIGINT, ParamSetter.BIGINT},
				paramSetters);
		}

		@Override
		public List<Integer> execute(Object... params) {
			Assert.assertEquals(2, params.length);
			Assert.assertSame(Long.class, params[0].getClass());
			Assert.assertSame(Long.class, params[1].getClass());

			if (_databaseError) {
				throw new RuntimeException("Database error");
			}

			if (_emptyResultSet) {
				return Collections.emptyList();
			}

			Long leftPrimaryKey = (Long)params[0];
			Long rightPrimaryKey = (Long)params[1];

			long[] rightPrimaryKeys = _mappingStore.get(leftPrimaryKey);

			if (ArrayUtil.contains(rightPrimaryKeys, rightPrimaryKey)) {
				return Collections.singletonList(1);
			}

			return Collections.singletonList(0);
		}

		public void setDatabaseError(boolean databaseError) {
			_databaseError = databaseError;
		}

		public void setEmptyResultSet(boolean emptyResultSet) {
			_emptyResultSet = emptyResultSet;
		}

		private boolean _databaseError;
		private boolean _emptyResultSet;

	}

	private class MockDeleteLeftPrimaryKeyTableMappingsSqlUpdate
		implements SqlUpdate {

		public MockDeleteLeftPrimaryKeyTableMappingsSqlUpdate(
			DataSource dataSource, ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {ParamSetter.BIGINT}, paramSetters);
		}

		public void setDatabaseError(boolean databaseError) {
			_databaseError = databaseError;
		}

		@Override
		public int update(Object... params) {
			Assert.assertEquals(1, params.length);
			Assert.assertSame(Long.class, params[0].getClass());

			if (_databaseError) {
				throw new RuntimeException("Database error");
			}

			Long leftPrimaryKey = (Long)params[0];

			long[] rightPrimaryKeys = _mappingStore.remove(leftPrimaryKey);

			if (rightPrimaryKeys == null) {
				return 0;
			}

			return rightPrimaryKeys.length;
		}

		private boolean _databaseError;

	}

	private class MockDeleteMappingSqlUpdate implements SqlUpdate {

		public MockDeleteMappingSqlUpdate(
			DataSource dataSource, ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {ParamSetter.BIGINT, ParamSetter.BIGINT},
				paramSetters);
		}

		public void setDatabaseError(boolean databaseError) {
			_databaseError = databaseError;
		}

		@Override
		public int update(Object... params) {
			Assert.assertEquals(2, params.length);
			Assert.assertSame(Long.class, params[0].getClass());
			Assert.assertSame(Long.class, params[1].getClass());

			if (_databaseError) {
				throw new RuntimeException("Database error");
			}

			Long leftPrimaryKey = (Long)params[0];

			long[] rightPrimaryKeys = _mappingStore.get(leftPrimaryKey);

			if (rightPrimaryKeys == null) {
				return 0;
			}

			Long rightPrimaryKey = (Long)params[1];

			if (ArrayUtil.contains(rightPrimaryKeys, rightPrimaryKey)) {
				rightPrimaryKeys = ArrayUtil.remove(
					rightPrimaryKeys, rightPrimaryKey);

				_mappingStore.put(leftPrimaryKey, rightPrimaryKeys);

				return 1;
			}

			return 0;
		}

		private boolean _databaseError;

	}

	private class MockDeleteRightPrimaryKeyTableMappingsSqlUpdate
		implements SqlUpdate {

		public MockDeleteRightPrimaryKeyTableMappingsSqlUpdate(
			DataSource dataSource, ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {ParamSetter.BIGINT}, paramSetters);
		}

		public void setDatabaseError(boolean databaseError) {
			_databaseError = databaseError;
		}

		@Override
		public int update(Object... params) {
			Assert.assertEquals(1, params.length);
			Assert.assertSame(Long.class, params[0].getClass());

			if (_databaseError) {
				throw new RuntimeException("Database error");
			}

			int count = 0;

			Long rightPrimaryKey = (Long)params[0];

			for (Map.Entry<Long, long[]> entry : _mappingStore.entrySet()) {
				long[] rightPrimaryKeys = entry.getValue();

				if (ArrayUtil.contains(rightPrimaryKeys, rightPrimaryKey)) {
					count++;

					rightPrimaryKeys = ArrayUtil.remove(
						rightPrimaryKeys, rightPrimaryKey);

					entry.setValue(rightPrimaryKeys);
				}
			}

			return count;
		}

		private boolean _databaseError;

	}

	private class MockGetLeftPrimaryKeysSqlQuery
		implements MappingSqlQuery<Long> {

		public MockGetLeftPrimaryKeysSqlQuery(
			DataSource dataSource, RowMapper<Long> rowMapper,
			ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {ParamSetter.BIGINT}, paramSetters);
			Assert.assertSame(RowMapper.PRIMARY_KEY, rowMapper);
		}

		@Override
		public List<Long> execute(Object... params) {
			Assert.assertEquals(1, params.length);
			Assert.assertSame(Long.class, params[0].getClass());

			if (_databaseError) {
				throw new RuntimeException("Database error");
			}

			Long rightPrimaryKey = (Long)params[0];

			List<Long> leftPrimaryKeysList = new ArrayList<>();

			for (Map.Entry<Long, long[]> entry : _mappingStore.entrySet()) {
				long[] rightPrimaryKeys = entry.getValue();

				if (ArrayUtil.contains(rightPrimaryKeys, rightPrimaryKey)) {
					leftPrimaryKeysList.add(entry.getKey());
				}
			}

			return leftPrimaryKeysList;
		}

		public void setDatabaseError(boolean databaseError) {
			_databaseError = databaseError;
		}

		private boolean _databaseError;

	}

	private class MockGetRightPrimaryKeysSqlQuery
		implements MappingSqlQuery<Long> {

		public MockGetRightPrimaryKeysSqlQuery(
			DataSource dataSource, RowMapper<Long> rowMapper,
			ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {ParamSetter.BIGINT}, paramSetters);
			Assert.assertSame(RowMapper.PRIMARY_KEY, rowMapper);
		}

		@Override
		public List<Long> execute(Object... params) {
			Assert.assertEquals(1, params.length);
			Assert.assertSame(Long.class, params[0].getClass());

			if (_databaseError) {
				throw new RuntimeException("Database error");
			}

			Long leftPrimaryKey = (Long)params[0];

			long[] rightPrimaryKeys = _mappingStore.get(leftPrimaryKey);

			if (rightPrimaryKeys == null) {
				return Collections.emptyList();
			}

			List<Long> rightPrimaryKeysList = new ArrayList<>(
				rightPrimaryKeys.length);

			for (long rightPrimaryKey : rightPrimaryKeys) {
				rightPrimaryKeysList.add(rightPrimaryKey);
			}

			return rightPrimaryKeysList;
		}

		public void setDatabaseError(boolean databaseError) {
			_databaseError = databaseError;
		}

		private boolean _databaseError;

	}

	private interface Right extends RightModel {
	}

	private interface RightModel extends BaseModel<Right> {
	}

}