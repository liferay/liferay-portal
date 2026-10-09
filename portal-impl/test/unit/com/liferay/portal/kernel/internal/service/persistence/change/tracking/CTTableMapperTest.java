/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.service.persistence.change.tracking;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.MappingSqlQuery;
import com.liferay.portal.kernel.dao.jdbc.MappingSqlQueryFactoryUtil;
import com.liferay.portal.kernel.dao.jdbc.ParamSetter;
import com.liferay.portal.kernel.dao.jdbc.RowMapper;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListenerRegistrationUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.tools.ToolDependencies;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

/**
 * @author Samuel Trong Tran
 */
public class CTTableMapperTest {

	@ClassRule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		ToolDependencies.wireBasic();

		DBManagerUtil.setDB(DBType.HYPERSONIC, null);
	}

	@Before
	public void setUp() {
		_mappingStore.clear();

		PortalCacheHelperUtil.clearPortalCaches(
			PortalCacheManagerNames.MULTI_VM);

		_mockMappingSqlQueryFactoryUtil();
		_mockSqlUpdateFactoryUtil();

		_leftBasePersistence = new MockBasePersistence<>(Left.class);

		_leftBasePersistence.setDataSource(_dataSource);

		_rightBasePersistence = new MockBasePersistence<>(Right.class);

		_rightBasePersistence.setDataSource(_dataSource);

		_ctTableMapper = new CTTableMapper<>(
			_TABLE_NAME, _COMPANY_COLUMN_NAME, _LEFT_COLUMN_NAME,
			_RIGHT_COLUMN_NAME, Left.class, Right.class, _leftBasePersistence,
			_rightBasePersistence, false);
	}

	@After
	public void tearDown() {
		_mappingSqlQueryFactoryUtilMockedStatic.close();
		_sqlUpdateFactoryUtilMockedStatic.close();
	}

	@Test
	public void testAddTableMapping() {

		// Success (production)

		long companyId = 0;
		long leftPrimaryKey = 1;
		long rightPrimaryKey = 2;

		Assert.assertTrue(
			_ctTableMapper.addTableMapping(
				companyId, leftPrimaryKey, rightPrimaryKey));

		// Fail (production)

		Assert.assertFalse(
			_ctTableMapper.addTableMapping(
				companyId, leftPrimaryKey, rightPrimaryKey));

		// Fail, exists in production

		long ctCollectionId = 3;

		_put(leftPrimaryKey, rightPrimaryKey, 0L, null);

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertFalse(
				_ctTableMapper.addTableMapping(
					companyId, leftPrimaryKey, rightPrimaryKey));
		}

		_remove(leftPrimaryKey, rightPrimaryKey, 0L);

		// Success

		try (SafeCloseable safeCloseable2 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertTrue(
				_ctTableMapper.addTableMapping(
					companyId, leftPrimaryKey, rightPrimaryKey));

			// Fail

			Assert.assertFalse(
				_ctTableMapper.addTableMapping(
					companyId, leftPrimaryKey, rightPrimaryKey));
		}

		// Success, previously deleted

		_put(leftPrimaryKey, rightPrimaryKey, 0L, null);
		_put(leftPrimaryKey, rightPrimaryKey, ctCollectionId, false);

		try (SafeCloseable safeCloseable3 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertTrue(
				_ctTableMapper.addTableMapping(
					companyId, leftPrimaryKey, rightPrimaryKey));
		}

		_remove(leftPrimaryKey, rightPrimaryKey, 0L);
		_remove(leftPrimaryKey, rightPrimaryKey, ctCollectionId);

		// Success, with model listener

		RecorderModelListener<Left> leftModelListener =
			new LeftRecorderModelListener();

		ModelListenerRegistrationUtil.register(leftModelListener);

		RecorderModelListener<Right> rightModelListener =
			new RightRecorderModelListener();

		ModelListenerRegistrationUtil.register(rightModelListener);

		try (SafeCloseable safeCloseable4 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertTrue(
				_ctTableMapper.addTableMapping(
					companyId, leftPrimaryKey, rightPrimaryKey));
		}

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

		_remove(leftPrimaryKey, rightPrimaryKey, ctCollectionId);

		// Database error

		MockAddCTTableMappingSqlUpdate mockAddCTTableMappingSqlUpdate =
			ReflectionTestUtil.getFieldValue(
				_ctTableMapper, "_addCTTableMappingSqlUpdate");

		mockAddCTTableMappingSqlUpdate.setDatabaseError(true);

		try (SafeCloseable safeCloseable5 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			_ctTableMapper.addTableMapping(
				companyId, leftPrimaryKey, rightPrimaryKey);
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertSame(RuntimeException.class, throwable.getClass());

			Assert.assertEquals("Database error", throwable.getMessage());
		}
		finally {
			mockAddCTTableMappingSqlUpdate.setDatabaseError(false);
		}
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
			_ctTableMapper.addTableMappings(
				companyId, leftPrimaryKey1, new long[] {rightPrimaryKey1}));
		Assert.assertArrayEquals(
			new long[0],
			_ctTableMapper.addTableMappings(
				companyId, leftPrimaryKey1, new long[] {rightPrimaryKey1}));
		Assert.assertArrayEquals(
			new long[] {rightPrimaryKey2},
			_ctTableMapper.addTableMappings(
				companyId, leftPrimaryKey1,
				new long[] {rightPrimaryKey1, rightPrimaryKey2}));
		Assert.assertEquals(
			2,
			_ctTableMapper.deleteLeftPrimaryKeyTableMappings(leftPrimaryKey1));
		Assert.assertArrayEquals(
			new long[] {leftPrimaryKey1},
			_ctTableMapper.addTableMappings(
				companyId, new long[] {leftPrimaryKey1}, rightPrimaryKey1));
		Assert.assertArrayEquals(
			new long[0],
			_ctTableMapper.addTableMappings(
				companyId, new long[] {leftPrimaryKey1}, rightPrimaryKey1));
		Assert.assertArrayEquals(
			new long[] {leftPrimaryKey2},
			_ctTableMapper.addTableMappings(
				companyId, new long[] {leftPrimaryKey1, leftPrimaryKey2},
				rightPrimaryKey1));
		Assert.assertEquals(
			2,
			_ctTableMapper.deleteRightPrimaryKeyTableMappings(
				rightPrimaryKey1));

		long ctCollectionId = 5;

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertArrayEquals(
				new long[] {rightPrimaryKey1},
				_ctTableMapper.addTableMappings(
					companyId, leftPrimaryKey1, new long[] {rightPrimaryKey1}));
			Assert.assertArrayEquals(
				new long[0],
				_ctTableMapper.addTableMappings(
					companyId, leftPrimaryKey1, new long[] {rightPrimaryKey1}));
			Assert.assertArrayEquals(
				new long[] {rightPrimaryKey2},
				_ctTableMapper.addTableMappings(
					companyId, leftPrimaryKey1,
					new long[] {rightPrimaryKey1, rightPrimaryKey2}));
			Assert.assertEquals(
				2,
				_ctTableMapper.deleteLeftPrimaryKeyTableMappings(
					leftPrimaryKey1));
			Assert.assertArrayEquals(
				new long[] {rightPrimaryKey1, rightPrimaryKey2},
				_ctTableMapper.addTableMappings(
					companyId, leftPrimaryKey1,
					new long[] {rightPrimaryKey1, rightPrimaryKey2}));
			Assert.assertEquals(
				2,
				_ctTableMapper.deleteLeftPrimaryKeyTableMappings(
					leftPrimaryKey1));
			Assert.assertArrayEquals(
				new long[] {leftPrimaryKey1},
				_ctTableMapper.addTableMappings(
					companyId, new long[] {leftPrimaryKey1}, rightPrimaryKey1));
			Assert.assertArrayEquals(
				new long[0],
				_ctTableMapper.addTableMappings(
					companyId, new long[] {leftPrimaryKey1}, rightPrimaryKey1));
			Assert.assertArrayEquals(
				new long[] {leftPrimaryKey2},
				_ctTableMapper.addTableMappings(
					companyId, new long[] {leftPrimaryKey1, leftPrimaryKey2},
					rightPrimaryKey1));
			Assert.assertEquals(
				2,
				_ctTableMapper.deleteRightPrimaryKeyTableMappings(
					rightPrimaryKey1));
			Assert.assertArrayEquals(
				new long[] {leftPrimaryKey1, leftPrimaryKey2},
				_ctTableMapper.addTableMappings(
					companyId, new long[] {leftPrimaryKey1, leftPrimaryKey2},
					rightPrimaryKey1));
			Assert.assertEquals(
				2,
				_ctTableMapper.deleteRightPrimaryKeyTableMappings(
					rightPrimaryKey1));
		}
	}

	@Test
	public void testBooleanParamSetter() throws Exception {
		ParamSetter booleanParamSetter = ReflectionTestUtil.getFieldValue(
			_ctTableMapper, "_booleanParamSetter");

		MappingSqlQuery<Void> mappingSqlQuery =
			new MockBooleanParamSetterSqlQuery(booleanParamSetter);

		mappingSqlQuery.execute(true);
	}

	@Test
	public void testCachelessTableMapper() {
		_ctTableMapper = new CTTableMapper<>(
			_TABLE_NAME, _COMPANY_COLUMN_NAME, _LEFT_COLUMN_NAME,
			_RIGHT_COLUMN_NAME, Left.class, Right.class, _leftBasePersistence,
			_rightBasePersistence, true);

		long leftPrimaryKey = 1;
		long rightPrimaryKey = 2;

		Assert.assertFalse(
			_ctTableMapper.containsTableMapping(
				leftPrimaryKey, rightPrimaryKey));

		// Contains table mapping (production)

		_put(leftPrimaryKey, rightPrimaryKey, 0L, null);

		Assert.assertTrue(
			_ctTableMapper.containsTableMapping(
				leftPrimaryKey, rightPrimaryKey));

		MockContainsTableMappingSQLQuery mockContainsTableMappingSQLQuery =
			ReflectionTestUtil.getFieldValue(
				_ctTableMapper, "containsTableMappingSQL");

		mockContainsTableMappingSQLQuery.setDatabaseError(true);

		try {
			_ctTableMapper.containsTableMapping(
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
			_ctTableMapper.containsTableMapping(
				leftPrimaryKey, rightPrimaryKey));
	}

	@Test
	public void testContainsCTTableMapping() {

		// Database error

		long companyId = 0;
		long leftPrimaryKey = 1;
		long rightPrimaryKey = 2;
		long ctCollectionId = 3;

		MockContainsCTTableMappingSQLQuery mockContainsCTTableMappingSQLQuery =
			ReflectionTestUtil.getFieldValue(
				_ctTableMapper, "_containsCTTableMappingSQL");

		mockContainsCTTableMappingSQLQuery.setDatabaseError(true);

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			_ctTableMapper.addTableMapping(
				companyId, leftPrimaryKey, rightPrimaryKey);
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertSame(RuntimeException.class, throwable.getClass());

			Assert.assertEquals("Database error", throwable.getMessage());
		}
		finally {
			mockContainsCTTableMappingSQLQuery.setDatabaseError(false);
		}

		// Empty results

		_put(leftPrimaryKey, rightPrimaryKey, ctCollectionId, true);

		try (SafeCloseable safeCloseable2 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertFalse(
				_ctTableMapper.addTableMapping(
					companyId, leftPrimaryKey, rightPrimaryKey));
		}

		mockContainsCTTableMappingSQLQuery.setEmptyResultSet(true);

		try (SafeCloseable safeCloseable3 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertFalse(
				_ctTableMapper.addTableMapping(
					companyId, leftPrimaryKey, rightPrimaryKey));
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertSame(RuntimeException.class, throwable.getClass());

			Assert.assertEquals(
				StringBundler.concat(
					"Unique key violation for left primary key ",
					leftPrimaryKey, " and right primary key ", rightPrimaryKey,
					" and ctcollectionId ", ctCollectionId),
				throwable.getMessage());
		}
		finally {
			mockContainsCTTableMappingSQLQuery.setEmptyResultSet(false);
		}
	}

	@Test
	public void testContainsTableMapping() {

		// Does not contain table mapping (production)

		long leftPrimaryKey = 1;
		long rightPrimaryKey = 2;

		Assert.assertFalse(
			_ctTableMapper.containsTableMapping(
				leftPrimaryKey, rightPrimaryKey));

		// Contains table mapping (production)

		PortalCache<Long, long[]> leftToRightPortalCache =
			ReflectionTestUtil.getFieldValue(
				_ctTableMapper, "leftToRightPortalCache");

		leftToRightPortalCache.remove(leftPrimaryKey);

		_put(leftPrimaryKey, rightPrimaryKey, 0L, null);

		Assert.assertTrue(
			_ctTableMapper.containsTableMapping(
				leftPrimaryKey, rightPrimaryKey));
	}

	@Test
	public void testDeleteLeftPrimaryKeyTableMappings() {

		// Delete 0 entry (production)

		long leftPrimaryKey = 1;

		Assert.assertEquals(
			0,
			_ctTableMapper.deleteLeftPrimaryKeyTableMappings(leftPrimaryKey));

		// Delete 1 entry (production)

		long rightPrimaryKey1 = 2;

		_put(leftPrimaryKey, rightPrimaryKey1, 0L, null);

		Assert.assertEquals(
			1,
			_ctTableMapper.deleteLeftPrimaryKeyTableMappings(leftPrimaryKey));

		_remove(leftPrimaryKey, rightPrimaryKey1, 0L);

		// Delete 0 entry

		long ctCollectionId = 4;

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				0,
				_ctTableMapper.deleteLeftPrimaryKeyTableMappings(
					leftPrimaryKey));
		}

		// Delete 1 entry

		_put(leftPrimaryKey, rightPrimaryKey1, ctCollectionId, true);

		try (SafeCloseable safeCloseable2 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				1,
				_ctTableMapper.deleteLeftPrimaryKeyTableMappings(
					leftPrimaryKey));
		}

		// Delete 2 entries

		long rightPrimaryKey2 = 3;

		_put(leftPrimaryKey, rightPrimaryKey1, ctCollectionId, true);
		_put(leftPrimaryKey, rightPrimaryKey2, ctCollectionId, true);

		try (SafeCloseable safeCloseable3 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				2,
				_ctTableMapper.deleteLeftPrimaryKeyTableMappings(
					leftPrimaryKey));
		}

		// Delete 0 entry, with left model listener

		RecorderModelListener<Left> leftModelListener =
			new LeftRecorderModelListener();

		ModelListenerRegistrationUtil.register(leftModelListener);

		try (SafeCloseable safeCloseable4 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				0,
				_ctTableMapper.deleteLeftPrimaryKeyTableMappings(
					leftPrimaryKey));
		}

		leftModelListener.assertOnBeforeRemoveAssociation(
			false, null, null, null);

		leftModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		ModelListenerRegistrationUtil.unregister(leftModelListener);

		// Delete 0 entry, with right model listener

		RecorderModelListener<Right> rightModelListener =
			new RightRecorderModelListener();

		ModelListenerRegistrationUtil.register(rightModelListener);

		try (SafeCloseable safeCloseable5 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				0,
				_ctTableMapper.deleteLeftPrimaryKeyTableMappings(
					leftPrimaryKey));
		}

		rightModelListener.assertOnBeforeRemoveAssociation(
			false, null, null, null);

		rightModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		ModelListenerRegistrationUtil.unregister(rightModelListener);

		// Delete 1 entry, with left model listener

		leftModelListener = new LeftRecorderModelListener();

		ModelListenerRegistrationUtil.register(leftModelListener);

		_put(leftPrimaryKey, rightPrimaryKey1, ctCollectionId, true);

		try (SafeCloseable safeCloseable6 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				1,
				_ctTableMapper.deleteLeftPrimaryKeyTableMappings(
					leftPrimaryKey));
		}

		leftModelListener.assertOnBeforeRemoveAssociation(
			true, leftPrimaryKey, Right.class.getName(), rightPrimaryKey1);

		leftModelListener.assertOnAfterRemoveAssociation(
			true, leftPrimaryKey, Right.class.getName(), rightPrimaryKey1);

		ModelListenerRegistrationUtil.unregister(leftModelListener);

		// Delete 1 entry, with right model listener

		rightModelListener = new RightRecorderModelListener();

		ModelListenerRegistrationUtil.register(rightModelListener);

		_put(leftPrimaryKey, rightPrimaryKey1, ctCollectionId, true);

		try (SafeCloseable safeCloseable7 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				1,
				_ctTableMapper.deleteLeftPrimaryKeyTableMappings(
					leftPrimaryKey));
		}

		rightModelListener.assertOnBeforeRemoveAssociation(
			true, rightPrimaryKey1, Left.class.getName(), leftPrimaryKey);

		rightModelListener.assertOnAfterRemoveAssociation(
			true, rightPrimaryKey1, Left.class.getName(), leftPrimaryKey);

		ModelListenerRegistrationUtil.unregister(rightModelListener);
	}

	@Test
	public void testDeleteRightPrimaryKeyTableMappings() {

		// Delete 0 entry (production)

		long rightPrimaryKey = 1;

		Assert.assertEquals(
			0,
			_ctTableMapper.deleteRightPrimaryKeyTableMappings(rightPrimaryKey));

		// Delete 1 entry (production)

		long leftPrimaryKey1 = 2;

		_put(leftPrimaryKey1, rightPrimaryKey, 0L, null);

		Assert.assertEquals(
			1,
			_ctTableMapper.deleteRightPrimaryKeyTableMappings(rightPrimaryKey));

		_remove(leftPrimaryKey1, rightPrimaryKey, 0L);

		// Delete 0 entry

		long ctCollectionId = 4;

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				0,
				_ctTableMapper.deleteRightPrimaryKeyTableMappings(
					rightPrimaryKey));
		}

		// Delete 1 entry

		_put(leftPrimaryKey1, rightPrimaryKey, ctCollectionId, true);

		try (SafeCloseable safeCloseable2 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				1,
				_ctTableMapper.deleteRightPrimaryKeyTableMappings(
					rightPrimaryKey));
		}

		// Delete 2 entries

		long leftPrimaryKey2 = 3;

		_put(leftPrimaryKey1, rightPrimaryKey, ctCollectionId, true);
		_put(leftPrimaryKey2, rightPrimaryKey, ctCollectionId, true);

		try (SafeCloseable safeCloseable3 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				2,
				_ctTableMapper.deleteRightPrimaryKeyTableMappings(
					rightPrimaryKey));
		}

		// Delete 0 entry, with left model listener

		RecorderModelListener<Left> leftModelListener =
			new LeftRecorderModelListener();

		ModelListenerRegistrationUtil.register(leftModelListener);

		try (SafeCloseable safeCloseable4 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				0,
				_ctTableMapper.deleteRightPrimaryKeyTableMappings(
					rightPrimaryKey));
		}

		leftModelListener.assertOnBeforeRemoveAssociation(
			false, null, null, null);

		leftModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		ModelListenerRegistrationUtil.unregister(leftModelListener);

		// Delete 0 entry, with right model listener

		RecorderModelListener<Right> rightModelListener =
			new RightRecorderModelListener();

		ModelListenerRegistrationUtil.register(rightModelListener);

		try (SafeCloseable safeCloseable5 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				0,
				_ctTableMapper.deleteRightPrimaryKeyTableMappings(
					rightPrimaryKey));
		}

		rightModelListener.assertOnBeforeRemoveAssociation(
			false, null, null, null);

		rightModelListener.assertOnAfterRemoveAssociation(
			false, null, null, null);

		ModelListenerRegistrationUtil.unregister(rightModelListener);

		// Delete 1 entry, with left model listener

		leftModelListener = new LeftRecorderModelListener();

		ModelListenerRegistrationUtil.register(leftModelListener);

		_put(leftPrimaryKey1, rightPrimaryKey, ctCollectionId, true);

		try (SafeCloseable safeCloseable6 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				1,
				_ctTableMapper.deleteRightPrimaryKeyTableMappings(
					rightPrimaryKey));
		}

		leftModelListener.assertOnBeforeRemoveAssociation(
			true, leftPrimaryKey1, Right.class.getName(), rightPrimaryKey);

		leftModelListener.assertOnAfterRemoveAssociation(
			true, leftPrimaryKey1, Right.class.getName(), rightPrimaryKey);

		ModelListenerRegistrationUtil.unregister(leftModelListener);

		// Delete 1 entry, with right model listener

		rightModelListener = new RightRecorderModelListener();

		ModelListenerRegistrationUtil.register(rightModelListener);

		_put(leftPrimaryKey1, rightPrimaryKey, ctCollectionId, true);

		try (SafeCloseable safeCloseable7 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertEquals(
				1,
				_ctTableMapper.deleteRightPrimaryKeyTableMappings(
					rightPrimaryKey));
		}

		rightModelListener.assertOnBeforeRemoveAssociation(
			true, rightPrimaryKey, Left.class.getName(), leftPrimaryKey1);

		rightModelListener.assertOnAfterRemoveAssociation(
			true, rightPrimaryKey, Left.class.getName(), leftPrimaryKey1);

		ModelListenerRegistrationUtil.unregister(rightModelListener);
	}

	@Test
	public void testDeleteTableMapping() {

		// No such table mapping (production)

		long leftPrimaryKey = 1;
		long rightPrimaryKey = 2;

		Assert.assertFalse(
			_ctTableMapper.deleteTableMapping(leftPrimaryKey, rightPrimaryKey));

		// Success (production)

		_put(leftPrimaryKey, rightPrimaryKey, 0L, null);

		Assert.assertTrue(
			_ctTableMapper.deleteTableMapping(leftPrimaryKey, rightPrimaryKey));

		Assert.assertFalse(
			_ctTableMapper.deleteTableMapping(leftPrimaryKey, rightPrimaryKey));

		_remove(leftPrimaryKey, rightPrimaryKey, 0L);

		// No such table mapping

		long ctCollectionId = 3;

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertFalse(
				_ctTableMapper.deleteTableMapping(
					leftPrimaryKey, rightPrimaryKey));
		}

		// Success

		_put(leftPrimaryKey, rightPrimaryKey, ctCollectionId, true);

		try (SafeCloseable safeCloseable2 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertTrue(
				_ctTableMapper.deleteTableMapping(
					leftPrimaryKey, rightPrimaryKey));
		}

		_remove(leftPrimaryKey, rightPrimaryKey, ctCollectionId);

		// Success, delete from production

		_put(leftPrimaryKey, rightPrimaryKey, 0L, null);

		try (SafeCloseable safeCloseable2 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertTrue(
				_ctTableMapper.deleteTableMapping(
					leftPrimaryKey, rightPrimaryKey));
		}

		// Fail, already deleted

		try (SafeCloseable safeCloseable3 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertFalse(
				_ctTableMapper.deleteTableMapping(
					leftPrimaryKey, rightPrimaryKey));
		}

		_remove(leftPrimaryKey, rightPrimaryKey, ctCollectionId);

		// Success, with model listener

		RecorderModelListener<Left> leftModelListener =
			new LeftRecorderModelListener();

		_leftBasePersistence.registerListener(leftModelListener);

		RecorderModelListener<Right> rightModelListener =
			new RightRecorderModelListener();

		_rightBasePersistence.registerListener(rightModelListener);

		_put(leftPrimaryKey, rightPrimaryKey, ctCollectionId, true);

		try (SafeCloseable safeCloseable4 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertTrue(
				_ctTableMapper.deleteTableMapping(
					leftPrimaryKey, rightPrimaryKey));
		}

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

		_remove(leftPrimaryKey, rightPrimaryKey, ctCollectionId);
	}

	@Test
	public void testDeleteTableMappings() {
		long companyId = 0;
		long leftPrimaryKey1 = 1;
		long leftPrimaryKey2 = 2;
		long rightPrimaryKey1 = 3;
		long rightPrimaryKey2 = 4;
		long ctCollectionId = 5;

		Assert.assertArrayEquals(
			new long[0],
			_ctTableMapper.deleteTableMappings(
				leftPrimaryKey1, new long[] {rightPrimaryKey1}));
		Assert.assertTrue(
			_ctTableMapper.addTableMapping(
				companyId, leftPrimaryKey1, rightPrimaryKey1));
		Assert.assertArrayEquals(
			new long[] {rightPrimaryKey1},
			_ctTableMapper.deleteTableMappings(
				leftPrimaryKey1,
				new long[] {rightPrimaryKey1, rightPrimaryKey2}));
		Assert.assertArrayEquals(
			new long[0],
			_ctTableMapper.deleteTableMappings(
				new long[] {leftPrimaryKey1}, rightPrimaryKey1));
		Assert.assertTrue(
			_ctTableMapper.addTableMapping(
				companyId, leftPrimaryKey1, rightPrimaryKey1));
		Assert.assertArrayEquals(
			new long[] {leftPrimaryKey1},
			_ctTableMapper.deleteTableMappings(
				new long[] {leftPrimaryKey1, leftPrimaryKey2},
				rightPrimaryKey1));

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			Assert.assertArrayEquals(
				new long[0],
				_ctTableMapper.deleteTableMappings(
					leftPrimaryKey1, new long[] {rightPrimaryKey1}));
			Assert.assertTrue(
				_ctTableMapper.addTableMapping(
					companyId, leftPrimaryKey1, rightPrimaryKey1));
			Assert.assertArrayEquals(
				new long[] {rightPrimaryKey1},
				_ctTableMapper.deleteTableMappings(
					leftPrimaryKey1,
					new long[] {rightPrimaryKey1, rightPrimaryKey2}));
			Assert.assertTrue(
				_ctTableMapper.addTableMapping(
					companyId, leftPrimaryKey1, rightPrimaryKey1));
			Assert.assertTrue(
				_ctTableMapper.addTableMapping(
					companyId, leftPrimaryKey1, rightPrimaryKey2));
			Assert.assertArrayEquals(
				new long[] {rightPrimaryKey1, rightPrimaryKey2},
				_ctTableMapper.deleteTableMappings(
					leftPrimaryKey1,
					new long[] {rightPrimaryKey1, rightPrimaryKey2}));
			Assert.assertArrayEquals(
				new long[0],
				_ctTableMapper.deleteTableMappings(
					new long[] {leftPrimaryKey1}, rightPrimaryKey1));
			Assert.assertTrue(
				_ctTableMapper.addTableMapping(
					companyId, leftPrimaryKey1, rightPrimaryKey1));
			Assert.assertArrayEquals(
				new long[] {leftPrimaryKey1},
				_ctTableMapper.deleteTableMappings(
					new long[] {leftPrimaryKey1, leftPrimaryKey2},
					rightPrimaryKey1));
			Assert.assertTrue(
				_ctTableMapper.addTableMapping(
					companyId, leftPrimaryKey1, rightPrimaryKey1));
			Assert.assertTrue(
				_ctTableMapper.addTableMapping(
					companyId, leftPrimaryKey2, rightPrimaryKey1));
			Assert.assertArrayEquals(
				new long[] {leftPrimaryKey1, leftPrimaryKey2},
				_ctTableMapper.deleteTableMappings(
					new long[] {leftPrimaryKey1, leftPrimaryKey2},
					rightPrimaryKey1));
		}
	}

	@Test
	public void testGetLeftBaseModels() {

		// Get 0 result (production)

		long rightPrimaryKey = 1;

		List<Left> lefts = _ctTableMapper.getLeftBaseModels(
			rightPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertSame(Collections.emptyList(), lefts);

		PortalCache<Long, long[]> rightToLeftPortalCache =
			ReflectionTestUtil.getFieldValue(
				_ctTableMapper, "rightToLeftPortalCache");

		rightToLeftPortalCache.remove(rightPrimaryKey);

		// Get 1 result (production)

		long leftPrimaryKey1 = 2;

		_put(leftPrimaryKey1, rightPrimaryKey, 0L, null);

		lefts = _ctTableMapper.getLeftBaseModels(
			rightPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(lefts.toString(), 1, lefts.size());

		Left left1 = lefts.get(0);

		Assert.assertEquals(leftPrimaryKey1, left1.getPrimaryKeyObj());

		rightToLeftPortalCache.remove(rightPrimaryKey);

		_remove(leftPrimaryKey1, rightPrimaryKey, 0L);

		// Get 0 result

		long ctCollectionId = 5;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			lefts = _ctTableMapper.getLeftBaseModels(
				rightPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}

		Assert.assertSame(Collections.emptyList(), lefts);

		// Get 1 result

		_put(leftPrimaryKey1, rightPrimaryKey, ctCollectionId, true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			lefts = _ctTableMapper.getLeftBaseModels(
				rightPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}

		Assert.assertEquals(lefts.toString(), 1, lefts.size());

		left1 = lefts.get(0);

		Assert.assertEquals(leftPrimaryKey1, left1.getPrimaryKeyObj());

		// Get 2 results, unsorted

		long leftPrimaryKey2 = 3;

		_put(leftPrimaryKey1, rightPrimaryKey, ctCollectionId, true);
		_put(leftPrimaryKey2, rightPrimaryKey, ctCollectionId, true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			lefts = _ctTableMapper.getLeftBaseModels(
				rightPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}

		Assert.assertEquals(lefts.toString(), 2, lefts.size());

		left1 = lefts.get(0);
		Left left2 = lefts.get(1);

		Assert.assertEquals(leftPrimaryKey1, left1.getPrimaryKeyObj());
		Assert.assertEquals(leftPrimaryKey2, left2.getPrimaryKeyObj());

		// Get 2 results, sorted

		_put(leftPrimaryKey1, rightPrimaryKey, ctCollectionId, true);
		_put(leftPrimaryKey2, rightPrimaryKey, ctCollectionId, true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			lefts = _ctTableMapper.getLeftBaseModels(
				rightPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new OrderByComparator<Left>() {

					@Override
					public int compare(Left left1, Left left2) {
						Long leftPrimaryKey1 = (Long)left1.getPrimaryKeyObj();
						Long leftPrimaryKey2 = (Long)left2.getPrimaryKeyObj();

						return leftPrimaryKey2.compareTo(leftPrimaryKey1);
					}

				});
		}

		Assert.assertEquals(lefts.toString(), 2, lefts.size());

		left1 = lefts.get(0);
		left2 = lefts.get(1);

		Assert.assertEquals(leftPrimaryKey2, left1.getPrimaryKeyObj());
		Assert.assertEquals(leftPrimaryKey1, left2.getPrimaryKeyObj());

		// Get 3 results, paginated

		long leftPrimaryKey3 = 4;

		_put(leftPrimaryKey1, rightPrimaryKey, ctCollectionId, true);
		_put(leftPrimaryKey2, rightPrimaryKey, ctCollectionId, true);
		_put(leftPrimaryKey3, rightPrimaryKey, ctCollectionId, true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			lefts = _ctTableMapper.getLeftBaseModels(
				rightPrimaryKey, 1, 2, null);
		}

		Assert.assertEquals(lefts.toString(), 1, lefts.size());

		Left left3 = lefts.get(0);

		Assert.assertEquals(leftPrimaryKey2, left3.getPrimaryKeyObj());

		// No such model exception

		_leftBasePersistence.setNoSuchModelException(true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			_ctTableMapper.getLeftBaseModels(
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

		// Get 0 result (production)

		long rightPrimaryKey = 1;

		long[] leftPrimaryKeys = _ctTableMapper.getLeftPrimaryKeys(
			rightPrimaryKey);

		Assert.assertEquals(
			Arrays.toString(leftPrimaryKeys), 0, leftPrimaryKeys.length);

		// Hit cache (production)

		Assert.assertSame(
			leftPrimaryKeys,
			_ctTableMapper.getLeftPrimaryKeys(rightPrimaryKey));

		// Get 2 results, ensure ordered (production)

		long leftPrimaryKey1 = 3;
		long leftPrimaryKey2 = 2;

		PortalCache<Long, long[]> rightToLeftPortalCache =
			ReflectionTestUtil.getFieldValue(
				_ctTableMapper, "rightToLeftPortalCache");

		rightToLeftPortalCache.remove(rightPrimaryKey);

		_put(leftPrimaryKey1, rightPrimaryKey, 0L, null);
		_put(leftPrimaryKey2, rightPrimaryKey, 0L, null);

		leftPrimaryKeys = _ctTableMapper.getLeftPrimaryKeys(rightPrimaryKey);

		Assert.assertArrayEquals(
			new long[] {leftPrimaryKey2, leftPrimaryKey1}, leftPrimaryKeys);

		_remove(leftPrimaryKey1, rightPrimaryKey, 0L);
		_remove(leftPrimaryKey2, rightPrimaryKey, 0L);

		// Get 0 result

		long ctCollectionId = 5;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			leftPrimaryKeys = _ctTableMapper.getLeftPrimaryKeys(
				rightPrimaryKey);
		}

		Assert.assertEquals(
			Arrays.toString(leftPrimaryKeys), 0, leftPrimaryKeys.length);

		// Get 2 results, ensure ordered

		_put(leftPrimaryKey1, rightPrimaryKey, ctCollectionId, true);
		_put(leftPrimaryKey2, rightPrimaryKey, ctCollectionId, true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			leftPrimaryKeys = _ctTableMapper.getLeftPrimaryKeys(
				rightPrimaryKey);
		}

		Assert.assertArrayEquals(
			new long[] {leftPrimaryKey2, leftPrimaryKey1}, leftPrimaryKeys);

		// Database error

		MockGetCTLeftPrimaryKeysSqlQuery
			mockGetLeftPrimaryKeysByRightPrimaryKeyMappingSqlQuery =
				ReflectionTestUtil.getFieldValue(
					_ctTableMapper, "_getCTLeftPrimaryKeysSqlQuery");

		mockGetLeftPrimaryKeysByRightPrimaryKeyMappingSqlQuery.setDatabaseError(
			true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			_ctTableMapper.getLeftPrimaryKeys(rightPrimaryKey);
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

		// Get 0 result (production)

		long leftPrimaryKey = 1;

		List<Right> rights = _ctTableMapper.getRightBaseModels(
			leftPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertSame(Collections.emptyList(), rights);

		PortalCache<Long, long[]> leftToRightPortalCache =
			ReflectionTestUtil.getFieldValue(
				_ctTableMapper, "leftToRightPortalCache");

		leftToRightPortalCache.remove(leftPrimaryKey);

		// Get 1 result (production)

		long rightPrimaryKey1 = 2;

		_put(leftPrimaryKey, rightPrimaryKey1, 0L, null);

		rights = _ctTableMapper.getRightBaseModels(
			leftPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(rights.toString(), 1, rights.size());

		Right right1 = rights.get(0);

		Assert.assertEquals(rightPrimaryKey1, right1.getPrimaryKeyObj());

		leftToRightPortalCache.remove(leftPrimaryKey);

		_remove(leftPrimaryKey, rightPrimaryKey1, 0L);

		// Get 0 result

		long ctCollectionId = 5;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			rights = _ctTableMapper.getRightBaseModels(
				leftPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}

		Assert.assertSame(Collections.emptyList(), rights);

		// Get 1 result

		_put(leftPrimaryKey, rightPrimaryKey1, ctCollectionId, true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			rights = _ctTableMapper.getRightBaseModels(
				leftPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}

		Assert.assertEquals(rights.toString(), 1, rights.size());

		right1 = rights.get(0);

		Assert.assertEquals(rightPrimaryKey1, right1.getPrimaryKeyObj());

		// Get 2 results, unsorted

		long rightPrimaryKey2 = 3;

		_put(leftPrimaryKey, rightPrimaryKey2, ctCollectionId, true);

		_put(leftPrimaryKey, rightPrimaryKey1, ctCollectionId, true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			rights = _ctTableMapper.getRightBaseModels(
				leftPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}

		Assert.assertEquals(rights.toString(), 2, rights.size());

		right1 = rights.get(0);
		Right right2 = rights.get(1);

		Assert.assertEquals(rightPrimaryKey1, right1.getPrimaryKeyObj());
		Assert.assertEquals(rightPrimaryKey2, right2.getPrimaryKeyObj());

		// Get 2 results, sorted

		_put(leftPrimaryKey, rightPrimaryKey2, ctCollectionId, true);
		_put(leftPrimaryKey, rightPrimaryKey1, ctCollectionId, true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			rights = _ctTableMapper.getRightBaseModels(
				leftPrimaryKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new OrderByComparator<Right>() {

					@Override
					public int compare(Right right1, Right right2) {
						Long rightPrimaryKey1 = (Long)right1.getPrimaryKeyObj();
						Long rightPrimaryKey2 = (Long)right2.getPrimaryKeyObj();

						return rightPrimaryKey2.compareTo(rightPrimaryKey1);
					}

				});
		}

		Assert.assertEquals(rights.toString(), 2, rights.size());

		right1 = rights.get(0);
		right2 = rights.get(1);

		Assert.assertEquals(rightPrimaryKey2, right1.getPrimaryKeyObj());
		Assert.assertEquals(rightPrimaryKey1, right2.getPrimaryKeyObj());

		// Get 3 results, paginated

		long rightPrimaryKey3 = 4;

		_put(leftPrimaryKey, rightPrimaryKey3, ctCollectionId, true);

		_put(leftPrimaryKey, rightPrimaryKey2, ctCollectionId, true);
		_put(leftPrimaryKey, rightPrimaryKey1, ctCollectionId, true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			rights = _ctTableMapper.getRightBaseModels(
				leftPrimaryKey, 1, 2, null);
		}

		Assert.assertEquals(rights.toString(), 1, rights.size());

		Right right3 = rights.get(0);

		Assert.assertEquals(rightPrimaryKey2, right3.getPrimaryKeyObj());

		// No such model exception

		_rightBasePersistence.setNoSuchModelException(true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			_ctTableMapper.getRightBaseModels(
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

		// Get 0 result (production)

		long leftPrimaryKey = 1;

		long[] rightPrimaryKeys = _ctTableMapper.getRightPrimaryKeys(
			leftPrimaryKey);

		Assert.assertEquals(
			Arrays.toString(rightPrimaryKeys), 0, rightPrimaryKeys.length);

		// Hit cache (production)

		Assert.assertSame(
			rightPrimaryKeys,
			_ctTableMapper.getRightPrimaryKeys(leftPrimaryKey));

		// Get 2 results, ensure ordered (production)

		long rightPrimaryKey1 = 3;
		long rightPrimaryKey2 = 2;

		PortalCache<Long, long[]> leftToRightPortalCache =
			ReflectionTestUtil.getFieldValue(
				_ctTableMapper, "leftToRightPortalCache");

		leftToRightPortalCache.remove(leftPrimaryKey);

		_put(leftPrimaryKey, rightPrimaryKey1, 0L, null);
		_put(leftPrimaryKey, rightPrimaryKey2, 0L, null);

		rightPrimaryKeys = _ctTableMapper.getRightPrimaryKeys(leftPrimaryKey);

		Assert.assertArrayEquals(
			new long[] {rightPrimaryKey2, rightPrimaryKey1}, rightPrimaryKeys);

		_remove(leftPrimaryKey, rightPrimaryKey1, 0L);
		_remove(leftPrimaryKey, rightPrimaryKey2, 0L);

		// Get 0 result

		long ctCollectionId = 5;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			rightPrimaryKeys = _ctTableMapper.getRightPrimaryKeys(
				leftPrimaryKey);
		}

		Assert.assertEquals(
			Arrays.toString(rightPrimaryKeys), 0, rightPrimaryKeys.length);

		// Get 2 results, ensure ordered

		_put(leftPrimaryKey, rightPrimaryKey1, ctCollectionId, true);
		_put(leftPrimaryKey, rightPrimaryKey2, ctCollectionId, true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			rightPrimaryKeys = _ctTableMapper.getRightPrimaryKeys(
				leftPrimaryKey);
		}

		Assert.assertArrayEquals(
			new long[] {rightPrimaryKey2, rightPrimaryKey1}, rightPrimaryKeys);

		// Database error

		MockGetCTRightPrimaryKeysSqlQuery
			mockGetRightPrimaryKeysByLeftPrimaryKeyMappingSqlQuery =
				ReflectionTestUtil.getFieldValue(
					_ctTableMapper, "_getCTRightPrimaryKeysSqlQuery");

		mockGetRightPrimaryKeysByLeftPrimaryKeyMappingSqlQuery.setDatabaseError(
			true);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			_ctTableMapper.getRightPrimaryKeys(leftPrimaryKey);
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

	private static boolean _containsKey(
		long leftPrimaryKey, long rightPrimaryKey, long ctCollectionId) {

		return _mappingStore.containsKey(
			new MappingKey(leftPrimaryKey, rightPrimaryKey, ctCollectionId));
	}

	private static Boolean _get(
		long leftPrimaryKey, long rightPrimaryKey, long ctCollectionId) {

		return _mappingStore.get(
			new MappingKey(leftPrimaryKey, rightPrimaryKey, ctCollectionId));
	}

	private static void _put(
		long leftPrimaryKey, long rightPrimaryKey, long ctCollectionId,
		Boolean value) {

		_mappingStore.put(
			new MappingKey(leftPrimaryKey, rightPrimaryKey, ctCollectionId),
			value);
	}

	private static void _remove(
		long leftPrimaryKey, long rightPrimaryKey, long ctCollectionId) {

		_mappingStore.remove(
			new MappingKey(leftPrimaryKey, rightPrimaryKey, ctCollectionId));
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
							" WHERE ", _RIGHT_COLUMN_NAME, " = ? AND ",
							"ctCollectionId = 0"))) {

					return new MockGetLeftPrimaryKeysSqlQuery(
						dataSource, RowMapper.PRIMARY_KEY, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"SELECT ", _RIGHT_COLUMN_NAME, " FROM ",
							_TABLE_NAME, " WHERE ", _LEFT_COLUMN_NAME,
							" = ? AND ctCollectionId = 0"))) {

					return new MockGetRightPrimaryKeysSqlQuery(
						dataSource, RowMapper.PRIMARY_KEY, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"SELECT * FROM ", _TABLE_NAME, " WHERE ",
							_LEFT_COLUMN_NAME, " = ? AND ", _RIGHT_COLUMN_NAME,
							" = ? AND ctCollectionId = 0"))) {

					return new MockContainsTableMappingSQLQuery(
						dataSource, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"SELECT * FROM ", _TABLE_NAME, " WHERE ",
							_LEFT_COLUMN_NAME, " = ? AND ", _RIGHT_COLUMN_NAME,
							" = ? AND (ctCollectionId = 0 OR ctCollectionId = ",
							"?) AND (ctChangeType is NULL or ctChangeType = ",
							"true) AND NOT EXISTS (SELECT * FROM ", _TABLE_NAME,
							" WHERE ", _LEFT_COLUMN_NAME, " = ? AND ",
							_RIGHT_COLUMN_NAME, " = ? AND ctCollectionId = ? ",
							"AND ctChangeType = false)"))) {

					return new MockContainsCTTableMappingSQLQuery(
						dataSource, RowMapper.COUNT, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"SELECT DISTINCT (", _LEFT_COLUMN_NAME, ") FROM ",
							_TABLE_NAME, " WHERE ", _RIGHT_COLUMN_NAME,
							" = ? AND ((", _LEFT_COLUMN_NAME,
							" NOT IN (SELECT ", _LEFT_COLUMN_NAME, " FROM ",
							_TABLE_NAME, " WHERE ", _RIGHT_COLUMN_NAME,
							" = ? AND ctCollectionId = ? AND ctChangeType = ",
							"false) AND ctCollectionId = 0) OR ",
							"(ctCollectionId = ? AND ctChangeType = true))"))) {

					return new MockGetCTLeftPrimaryKeysSqlQuery(
						dataSource, RowMapper.PRIMARY_KEY, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"SELECT DISTINCT (", _RIGHT_COLUMN_NAME, ") FROM ",
							_TABLE_NAME, " WHERE ", _LEFT_COLUMN_NAME,
							" = ? AND ((", _RIGHT_COLUMN_NAME,
							" NOT IN (SELECT ", _RIGHT_COLUMN_NAME, " FROM ",
							_TABLE_NAME, " WHERE ", _LEFT_COLUMN_NAME,
							" = ? AND ctCollectionId = ? AND ctChangeType = ",
							"false) AND ctCollectionId = 0) OR ",
							"(ctCollectionId = ? AND ctChangeType = true))"))) {

					return new MockGetCTRightPrimaryKeysSqlQuery(
						dataSource, RowMapper.PRIMARY_KEY, paramSetters);
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

				if (sql.equals(
						StringBundler.concat(
							"INSERT INTO ", _TABLE_NAME, " (",
							_COMPANY_COLUMN_NAME, ", ", _LEFT_COLUMN_NAME, ", ",
							_RIGHT_COLUMN_NAME,
							", ctCollectionId, ctChangeType) VALUES (?, ?, ?, ",
							"?, ?)"))) {

					return new MockAddCTTableMappingSqlUpdate(dataSource);
				}

				ParamSetter[] paramSetters = (ParamSetter[])arguments[2];

				if (sql.equals(
						StringBundler.concat(
							"INSERT INTO ", _TABLE_NAME, " (",
							_COMPANY_COLUMN_NAME, ", ", _LEFT_COLUMN_NAME, ", ",
							_RIGHT_COLUMN_NAME,
							", ctCollectionId) VALUES (?, ?, ?, 0)"))) {

					return new MockAddTableMappingSqlUpdate(
						dataSource, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"DELETE FROM ", _TABLE_NAME, " WHERE ",
							_LEFT_COLUMN_NAME,
							" = ? AND ctCollectionId = 0"))) {

					return new MockDeleteLeftPrimaryKeyTableMappingsSqlUpdate(
						dataSource, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"DELETE FROM ", _TABLE_NAME, " WHERE ",
							_RIGHT_COLUMN_NAME,
							" = ? AND ctCollectionId = 0"))) {

					return new MockDeleteRightPrimaryKeyTableMappingsSqlUpdate(
						dataSource, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"DELETE FROM ", _TABLE_NAME, " WHERE ",
							_LEFT_COLUMN_NAME, " = ? AND ", _RIGHT_COLUMN_NAME,
							" = ? AND ctCollectionId = 0"))) {

					return new MockDeleteTableMappingSqlUpdate(
						dataSource, paramSetters);
				}

				if (sql.equals(
						StringBundler.concat(
							"UPDATE ", _TABLE_NAME,
							" SET ctChangeType = ? WHERE ", _LEFT_COLUMN_NAME,
							" = ? AND ", _RIGHT_COLUMN_NAME,
							" = ? AND ctCollectionId = ?"))) {

					return new MockUpdateCTTableMappingSqlUpdate(dataSource);
				}

				throw new UnsupportedOperationException(sql);
			}
		);
	}

	private static final String _COMPANY_COLUMN_NAME = "companyId";

	private static final String _LEFT_COLUMN_NAME = "leftId";

	private static final String _RIGHT_COLUMN_NAME = "rightId";

	private static final String _TABLE_NAME = "Lefts_Rights";

	private static final DataSource _dataSource =
		(DataSource)ProxyUtil.newProxyInstance(
			CTTableMapperTest.class.getClassLoader(),
			new Class<?>[] {DataSource.class},
			(proxy, method, args) -> {
				throw new UnsupportedOperationException();
			});
	private static final Map<MappingKey, Boolean> _mappingStore =
		new HashMap<>();

	private CTTableMapper<Left, Right> _ctTableMapper;
	private MockBasePersistence<Left> _leftBasePersistence;
	private MockedStatic<MappingSqlQueryFactoryUtil>
		_mappingSqlQueryFactoryUtilMockedStatic;
	private MockBasePersistence<Right> _rightBasePersistence;
	private MockedStatic<SqlUpdateFactoryUtil>
		_sqlUpdateFactoryUtilMockedStatic;

	private static class LeftRecorderModelListener
		extends RecorderModelListener<Left> {
	}

	private static class MappingKey {

		@Override
		public boolean equals(Object object) {
			MappingKey mappingKey = (MappingKey)object;

			if ((_leftPrimaryKey == mappingKey._leftPrimaryKey) &&
				(_rightPrimaryKey == mappingKey._rightPrimaryKey) &&
				(_ctCollectionId == mappingKey._ctCollectionId)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hashCode = HashUtil.hash(0, _leftPrimaryKey);

			hashCode = HashUtil.hash(hashCode, _rightPrimaryKey);

			return HashUtil.hash(hashCode, _ctCollectionId);
		}

		private MappingKey(
			long leftPrimaryKey, long rightPrimaryKey, long ctCollectionId) {

			_leftPrimaryKey = leftPrimaryKey;
			_rightPrimaryKey = rightPrimaryKey;
			_ctCollectionId = ctCollectionId;
		}

		private final long _ctCollectionId;
		private final long _leftPrimaryKey;
		private final long _rightPrimaryKey;

	}

	private static class MockAddCTTableMappingSqlUpdate implements SqlUpdate {

		public void setDatabaseError(boolean databaseError) {
			_databaseError = databaseError;
		}

		@Override
		public int update(Object... params) {
			Assert.assertEquals(Arrays.toString(params), 5, params.length);

			Assert.assertSame(Long.class, params[0].getClass());
			Assert.assertSame(Long.class, params[1].getClass());
			Assert.assertSame(Long.class, params[2].getClass());
			Assert.assertSame(Long.class, params[3].getClass());
			Assert.assertSame(Boolean.class, params[4].getClass());

			if (_databaseError) {
				throw new RuntimeException("Database error");
			}

			Long leftPrimaryKey = (Long)params[1];
			Long rightPrimaryKey = (Long)params[2];
			Long ctCollectionId = (Long)params[3];

			if (_containsKey(leftPrimaryKey, rightPrimaryKey, ctCollectionId)) {
				throw new RuntimeException(
					StringBundler.concat(
						"Unique key violation for left primary key ",
						leftPrimaryKey, " and right primary key ",
						rightPrimaryKey, " and ctcollectionId ",
						ctCollectionId));
			}

			Boolean ctChangeType = (Boolean)params[4];

			_put(leftPrimaryKey, rightPrimaryKey, ctCollectionId, ctChangeType);

			return 1;
		}

		private MockAddCTTableMappingSqlUpdate(DataSource dataSource) {
			Assert.assertSame(_dataSource, dataSource);
		}

		private boolean _databaseError;

	}

	private static class MockAddTableMappingSqlUpdate implements SqlUpdate {

		@Override
		public int update(Object... params) {
			Assert.assertEquals(Arrays.toString(params), 3, params.length);

			Assert.assertSame(Long.class, params[0].getClass());
			Assert.assertSame(Long.class, params[1].getClass());
			Assert.assertSame(Long.class, params[2].getClass());

			Long leftPrimaryKey = (Long)params[1];
			Long rightPrimaryKey = (Long)params[2];

			if (_containsKey(leftPrimaryKey, rightPrimaryKey, 0L)) {
				throw new RuntimeException(
					StringBundler.concat(
						"Unique key violation for left primary key ",
						leftPrimaryKey, " and right primary key ",
						rightPrimaryKey, "and ctcollectionId 0"));
			}

			_put(leftPrimaryKey, rightPrimaryKey, 0L, null);

			return 1;
		}

		private MockAddTableMappingSqlUpdate(
			DataSource dataSource, ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {
					ParamSetter.BIGINT, ParamSetter.BIGINT, ParamSetter.BIGINT
				},
				paramSetters);
		}

	}

	private static class MockContainsCTTableMappingSQLQuery
		implements MappingSqlQuery<Integer> {

		@Override
		public List<Integer> execute(Object... params) {
			Assert.assertEquals(Arrays.toString(params), 6, params.length);

			Assert.assertSame(Long.class, params[0].getClass());
			Assert.assertSame(Long.class, params[1].getClass());
			Assert.assertSame(Long.class, params[2].getClass());

			if (_databaseError) {
				throw new RuntimeException("Database error");
			}

			if (_emptyResultSet) {
				return Collections.emptyList();
			}

			Long leftPrimaryKey = (Long)params[0];
			Long rightPrimaryKey = (Long)params[1];

			Long ctCollectionId = (Long)params[2];

			Boolean ctChangeType = _get(
				leftPrimaryKey, rightPrimaryKey, ctCollectionId);

			boolean inProduction = _containsKey(
				leftPrimaryKey, rightPrimaryKey, 0L);

			if (((ctChangeType != null) && ctChangeType) ||
				(inProduction && (ctChangeType == null))) {

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

		private MockContainsCTTableMappingSQLQuery(
			DataSource dataSource, RowMapper<Integer> rowMapper,
			ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {
					ParamSetter.BIGINT, ParamSetter.BIGINT, ParamSetter.BIGINT,
					ParamSetter.BIGINT, ParamSetter.BIGINT, ParamSetter.BIGINT
				},
				paramSetters);
			Assert.assertSame(RowMapper.COUNT, rowMapper);
		}

		private boolean _databaseError;
		private boolean _emptyResultSet;

	}

	private static class MockContainsTableMappingSQLQuery
		implements MappingSqlQuery<Integer> {

		@Override
		public List<Integer> execute(Object... params) {
			Assert.assertEquals(Arrays.toString(params), 2, params.length);

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

			if (_containsKey(leftPrimaryKey, rightPrimaryKey, 0L)) {
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

		private MockContainsTableMappingSQLQuery(
			DataSource dataSource, ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {ParamSetter.BIGINT, ParamSetter.BIGINT},
				paramSetters);
		}

		private boolean _databaseError;
		private boolean _emptyResultSet;

	}

	private static class MockDeleteLeftPrimaryKeyTableMappingsSqlUpdate
		implements SqlUpdate {

		@Override
		public int update(Object... params) {
			Assert.assertEquals(Arrays.toString(params), 1, params.length);

			Assert.assertSame(Long.class, params[0].getClass());

			Long leftPrimaryKey = (Long)params[0];

			Set<MappingKey> mappingKeys = _mappingStore.keySet();

			int size = mappingKeys.size();

			mappingKeys.removeIf(
				mappingKey ->
					(leftPrimaryKey == mappingKey._leftPrimaryKey) &&
					(mappingKey._ctCollectionId == 0));

			return size - mappingKeys.size();
		}

		private MockDeleteLeftPrimaryKeyTableMappingsSqlUpdate(
			DataSource dataSource, ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {ParamSetter.BIGINT}, paramSetters);
		}

	}

	private static class MockDeleteRightPrimaryKeyTableMappingsSqlUpdate
		implements SqlUpdate {

		@Override
		public int update(Object... params) {
			Assert.assertEquals(Arrays.toString(params), 1, params.length);

			Assert.assertSame(Long.class, params[0].getClass());

			Long rightPrimaryKey = (Long)params[0];

			Set<MappingKey> mappingKeys = _mappingStore.keySet();

			int size = mappingKeys.size();

			mappingKeys.removeIf(
				mappingKey ->
					(rightPrimaryKey == mappingKey._rightPrimaryKey) &&
					(mappingKey._ctCollectionId == 0));

			return size - mappingKeys.size();
		}

		private MockDeleteRightPrimaryKeyTableMappingsSqlUpdate(
			DataSource dataSource, ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {ParamSetter.BIGINT}, paramSetters);
		}

	}

	private static class MockDeleteTableMappingSqlUpdate implements SqlUpdate {

		@Override
		public int update(Object... params) {
			Assert.assertEquals(Arrays.toString(params), 2, params.length);

			Assert.assertSame(Long.class, params[0].getClass());
			Assert.assertSame(Long.class, params[1].getClass());

			Long leftPrimaryKey = (Long)params[0];
			Long rightPrimaryKey = (Long)params[1];

			if (_containsKey(leftPrimaryKey, rightPrimaryKey, 0L)) {
				_remove(leftPrimaryKey, rightPrimaryKey, 0L);

				return 1;
			}

			return 0;
		}

		private MockDeleteTableMappingSqlUpdate(
			DataSource dataSource, ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {ParamSetter.BIGINT, ParamSetter.BIGINT},
				paramSetters);
		}

	}

	private static class MockGetCTLeftPrimaryKeysSqlQuery
		implements MappingSqlQuery<Long> {

		@Override
		public List<Long> execute(Object... params) {
			Assert.assertEquals(Arrays.toString(params), 4, params.length);

			Assert.assertSame(Long.class, params[0].getClass());
			Assert.assertSame(Long.class, params[1].getClass());
			Assert.assertSame(Long.class, params[2].getClass());
			Assert.assertSame(Long.class, params[3].getClass());

			if (_databaseError) {
				throw new RuntimeException("Database error");
			}

			Long rightPrimaryKey = (Long)params[0];
			Long ctCollectionId = (Long)params[2];

			List<Long> leftPrimaryKeysList = new ArrayList<>();

			for (MappingKey mappingKey : _mappingStore.keySet()) {
				if (rightPrimaryKey != mappingKey._rightPrimaryKey) {
					continue;
				}

				long leftPrimaryKey = mappingKey._leftPrimaryKey;

				Boolean ctChangeType = _get(
					leftPrimaryKey, rightPrimaryKey, ctCollectionId);
				boolean inProduction = _containsKey(
					leftPrimaryKey, rightPrimaryKey, 0L);

				if ((((ctChangeType == null) || !ctChangeType) &&
					 (!inProduction || (ctChangeType != null))) ||
					leftPrimaryKeysList.contains(leftPrimaryKey)) {

					continue;
				}

				leftPrimaryKeysList.add(leftPrimaryKey);
			}

			return leftPrimaryKeysList;
		}

		public void setDatabaseError(boolean databaseError) {
			_databaseError = databaseError;
		}

		private MockGetCTLeftPrimaryKeysSqlQuery(
			DataSource dataSource, RowMapper<Long> rowMapper,
			ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {
					ParamSetter.BIGINT, ParamSetter.BIGINT, ParamSetter.BIGINT,
					ParamSetter.BIGINT
				},
				paramSetters);
			Assert.assertSame(RowMapper.PRIMARY_KEY, rowMapper);
		}

		private boolean _databaseError;

	}

	private static class MockGetCTRightPrimaryKeysSqlQuery
		implements MappingSqlQuery<Long> {

		@Override
		public List<Long> execute(Object... params) {
			Assert.assertEquals(Arrays.toString(params), 4, params.length);

			Assert.assertSame(Long.class, params[0].getClass());
			Assert.assertSame(Long.class, params[1].getClass());
			Assert.assertSame(Long.class, params[2].getClass());
			Assert.assertSame(Long.class, params[3].getClass());

			if (_databaseError) {
				throw new RuntimeException("Database error");
			}

			Long leftPrimaryKey = (Long)params[0];
			Long ctCollectionId = (Long)params[2];

			List<Long> rightPrimaryKeysList = new ArrayList<>();

			for (MappingKey mappingKey : _mappingStore.keySet()) {
				if (leftPrimaryKey != mappingKey._leftPrimaryKey) {
					continue;
				}

				long rightPrimaryKey = mappingKey._rightPrimaryKey;

				Boolean ctChangeType = _get(
					leftPrimaryKey, rightPrimaryKey, ctCollectionId);
				boolean inProduction = _containsKey(
					leftPrimaryKey, rightPrimaryKey, 0L);

				if ((((ctChangeType == null) || !ctChangeType) &&
					 (!inProduction || (ctChangeType != null))) ||
					rightPrimaryKeysList.contains(rightPrimaryKey)) {

					continue;
				}

				rightPrimaryKeysList.add(rightPrimaryKey);
			}

			return rightPrimaryKeysList;
		}

		public void setDatabaseError(boolean databaseError) {
			_databaseError = databaseError;
		}

		private MockGetCTRightPrimaryKeysSqlQuery(
			DataSource dataSource, RowMapper<Long> rowMapper,
			ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {
					ParamSetter.BIGINT, ParamSetter.BIGINT, ParamSetter.BIGINT,
					ParamSetter.BIGINT
				},
				paramSetters);
			Assert.assertSame(RowMapper.PRIMARY_KEY, rowMapper);
		}

		private boolean _databaseError;

	}

	private static class MockGetLeftPrimaryKeysSqlQuery
		implements MappingSqlQuery<Long> {

		@Override
		public List<Long> execute(Object... params) {
			Assert.assertEquals(Arrays.toString(params), 1, params.length);

			Assert.assertSame(Long.class, params[0].getClass());

			Long rightPrimaryKey = (Long)params[0];

			List<Long> leftPrimaryKeysList = new ArrayList<>();

			for (MappingKey mappingKey : _mappingStore.keySet()) {
				if ((rightPrimaryKey != mappingKey._rightPrimaryKey) ||
					(mappingKey._ctCollectionId != 0)) {

					continue;
				}

				leftPrimaryKeysList.add(mappingKey._leftPrimaryKey);
			}

			return leftPrimaryKeysList;
		}

		private MockGetLeftPrimaryKeysSqlQuery(
			DataSource dataSource, RowMapper<Long> rowMapper,
			ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {ParamSetter.BIGINT}, paramSetters);
			Assert.assertSame(RowMapper.PRIMARY_KEY, rowMapper);
		}

	}

	private static class MockGetRightPrimaryKeysSqlQuery
		implements MappingSqlQuery<Long> {

		@Override
		public List<Long> execute(Object... params) {
			Assert.assertEquals(Arrays.toString(params), 1, params.length);

			Assert.assertSame(Long.class, params[0].getClass());

			Long leftPrimaryKey = (Long)params[0];

			List<Long> rightPrimaryKeysList = new ArrayList<>();

			for (MappingKey mappingKey : _mappingStore.keySet()) {
				if ((leftPrimaryKey != mappingKey._leftPrimaryKey) ||
					(mappingKey._ctCollectionId != 0)) {

					continue;
				}

				rightPrimaryKeysList.add(mappingKey._rightPrimaryKey);
			}

			return rightPrimaryKeysList;
		}

		private MockGetRightPrimaryKeysSqlQuery(
			DataSource dataSource, RowMapper<Long> rowMapper,
			ParamSetter... paramSetters) {

			Assert.assertSame(_dataSource, dataSource);
			Assert.assertArrayEquals(
				new ParamSetter[] {ParamSetter.BIGINT}, paramSetters);
			Assert.assertSame(RowMapper.PRIMARY_KEY, rowMapper);
		}

	}

	private static class MockUpdateCTTableMappingSqlUpdate
		implements SqlUpdate {

		@Override
		public int update(Object... params) {
			Assert.assertEquals(Arrays.toString(params), 4, params.length);

			Assert.assertSame(Boolean.class, params[0].getClass());
			Assert.assertSame(Long.class, params[1].getClass());
			Assert.assertSame(Long.class, params[2].getClass());
			Assert.assertSame(Long.class, params[3].getClass());

			Long leftPrimaryKey = (Long)params[1];
			Long rightPrimaryKey = (Long)params[2];
			Long ctCollectionId = (Long)params[3];

			if (_containsKey(leftPrimaryKey, rightPrimaryKey, ctCollectionId)) {
				Boolean ctChangeType = (Boolean)params[0];

				Boolean currentChangeType = _get(
					leftPrimaryKey, rightPrimaryKey, ctCollectionId);

				if (!ctChangeType.equals(currentChangeType)) {
					_put(
						leftPrimaryKey, rightPrimaryKey, ctCollectionId,
						ctChangeType);

					return 1;
				}
			}

			return 0;
		}

		private MockUpdateCTTableMappingSqlUpdate(DataSource dataSource) {
			Assert.assertSame(_dataSource, dataSource);
		}

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

	private class GetPrimaryKeyObjInvocationHandler
		implements InvocationHandler {

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

		private GetPrimaryKeyObjInvocationHandler(Serializable primaryKey) {
			_primaryKey = primaryKey;
		}

		private final Serializable _primaryKey;

	}

	private interface Left extends LeftModel {
	}

	private interface LeftModel extends BaseModel<Left> {
	}

	private class MockBasePersistence<T extends BaseModel<T>>
		extends BasePersistenceImpl<T, NoSuchModelException> {

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

		private MockBasePersistence(Class<T> clazz) {
			setModelClass(clazz);
		}

		private boolean _noSuchModelException;

	}

	private class MockBooleanParamSetterSqlQuery
		implements MappingSqlQuery<Void> {

		@Override
		public List<Void> execute(Object... params) throws SQLException {
			Assert.assertEquals(Arrays.toString(params), 1, params.length);

			PreparedStatement preparedStatement =
				(PreparedStatement)ProxyUtil.newProxyInstance(
					CTTableMapperTest.class.getClassLoader(),
					new Class<?>[] {PreparedStatement.class},
					(proxy, method, args) -> {
						Assert.assertEquals(
							PreparedStatement.class.getMethod(
								"setBoolean", int.class, boolean.class),
							method);

						Assert.assertEquals(1, args[0]);
						Assert.assertEquals(args[1], true);

						return null;
					});

			ParamSetter paramSetter = _paramSetters[0];

			paramSetter.set(preparedStatement, 1, params[0]);

			return null;
		}

		private MockBooleanParamSetterSqlQuery(ParamSetter... paramSetters) {
			_paramSetters = paramSetters;
		}

		private final ParamSetter[] _paramSetters;

	}

	private interface Right extends RightModel {
	}

	private interface RightModel extends BaseModel<Right> {
	}

}