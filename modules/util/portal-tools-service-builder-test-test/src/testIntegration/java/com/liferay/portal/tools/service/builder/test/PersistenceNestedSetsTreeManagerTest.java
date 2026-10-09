/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.NestedSetsTreeManager;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.model.NestedSetsTreeEntry;
import com.liferay.portal.tools.service.builder.test.service.NestedSetsTreeEntryLocalService;
import com.liferay.portal.tools.service.builder.test.service.persistence.NestedSetsTreeEntryPersistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class PersistenceNestedSetsTreeManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@BeforeClass
	public static void setUpClass() throws Exception {
		_sessionFactoryInvocationHandler = new SessionFactoryInvocationHandler(
			ReflectionTestUtil.getFieldValue(
				_nestedSetsTreeEntryPersistence, "_sessionFactory"));

		ReflectionTestUtil.setFieldValue(
			_nestedSetsTreeEntryPersistence, "_sessionFactory",
			ProxyUtil.newProxyInstance(
				SessionFactory.class.getClassLoader(),
				new Class<?>[] {SessionFactory.class},
				_sessionFactoryInvocationHandler));

		_nestedSetsTreeEntryPersistence.setRebuildTreeEnabled(false);

		_nestedSetsTreeManager = ReflectionTestUtil.getFieldValue(
			_nestedSetsTreeEntryPersistence, "nestedSetsTreeManager");

		_group = GroupTestUtil.addGroup();

		_nestedSetsTreeEntries = new NestedSetsTreeEntry[9];
	}

	@AfterClass
	public static void tearDownClass() throws PortalException {
		_groupLocalService.deleteGroup(_group);

		ReflectionTestUtil.setFieldValue(
			_nestedSetsTreeEntryPersistence, "_sessionFactory",
			_sessionFactoryInvocationHandler.getTarget());

		_nestedSetsTreeEntryPersistence.setRebuildTreeEnabled(true);
	}

	@Before
	public void setUp() {
		for (int i = 0; i < 9; i++) {
			_nestedSetsTreeEntries[i] =
				_nestedSetsTreeEntryLocalService.addNestedSetsTreeEntry(
					_group.getGroupId());
		}
	}

	@After
	public void tearDown() {
		for (NestedSetsTreeEntry nestedSetsTreeEntry : _nestedSetsTreeEntries) {
			if (nestedSetsTreeEntry == null) {
				continue;
			}

			_nestedSetsTreeEntryPersistence.remove(nestedSetsTreeEntry);
		}
	}

	@Test
	public void testCountAncestors() {
		testInsert();

		assertCountAncestors(1, _nestedSetsTreeEntries[0]);
		assertCountAncestors(1, _nestedSetsTreeEntries[1]);
		assertCountAncestors(1, _nestedSetsTreeEntries[2]);
		assertCountAncestors(2, _nestedSetsTreeEntries[3]);
		assertCountAncestors(2, _nestedSetsTreeEntries[4]);
		assertCountAncestors(3, _nestedSetsTreeEntries[5]);
		assertCountAncestors(2, _nestedSetsTreeEntries[6]);
		assertCountAncestors(3, _nestedSetsTreeEntries[7]);
		assertCountAncestors(3, _nestedSetsTreeEntries[8]);
	}

	@Test
	public void testCountChildren() {
		testInsert();

		assertCountChildren(5, _nestedSetsTreeEntries[0]);
		assertCountChildren(3, _nestedSetsTreeEntries[1]);
		assertCountChildren(1, _nestedSetsTreeEntries[2]);
		assertCountChildren(2, _nestedSetsTreeEntries[3]);
		assertCountChildren(2, _nestedSetsTreeEntries[4]);
		assertCountChildren(1, _nestedSetsTreeEntries[5]);
		assertCountChildren(2, _nestedSetsTreeEntries[6]);
		assertCountChildren(1, _nestedSetsTreeEntries[7]);
		assertCountChildren(1, _nestedSetsTreeEntries[8]);
	}

	@Test
	public void testDelete() {
		testInsert();

		_nestedSetsTreeManager.delete(_nestedSetsTreeEntries[7]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[7], true);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 10);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 11, 14);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 15, 16);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 2, 5);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 6, 9);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 3, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 12, 13);
		assertLeftAndRight(_nestedSetsTreeEntries[8], 7, 8);

		_nestedSetsTreeManager.delete(_nestedSetsTreeEntries[4]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[4], true);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 8);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 9, 12);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 13, 14);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 2, 5);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 3, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 10, 11);
		assertLeftAndRight(_nestedSetsTreeEntries[8], 6, 7);

		_nestedSetsTreeManager.delete(_nestedSetsTreeEntries[0]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[0], true);

		assertLeftAndRight(_nestedSetsTreeEntries[1], 7, 10);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 11, 12);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 1, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 2, 3);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 8, 9);
		assertLeftAndRight(_nestedSetsTreeEntries[8], 5, 6);

		_nestedSetsTreeManager.delete(_nestedSetsTreeEntries[8]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[8], true);

		assertLeftAndRight(_nestedSetsTreeEntries[1], 5, 8);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 9, 10);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 1, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 2, 3);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 6, 7);

		_nestedSetsTreeManager.delete(_nestedSetsTreeEntries[2]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[2], true);

		assertLeftAndRight(_nestedSetsTreeEntries[1], 5, 8);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 1, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 2, 3);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 6, 7);

		_nestedSetsTreeManager.delete(_nestedSetsTreeEntries[5]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[5], true);

		assertLeftAndRight(_nestedSetsTreeEntries[1], 3, 6);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 1, 2);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 4, 5);

		_nestedSetsTreeManager.delete(_nestedSetsTreeEntries[1]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[1], true);

		assertLeftAndRight(_nestedSetsTreeEntries[3], 1, 2);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 3, 4);

		_nestedSetsTreeManager.delete(_nestedSetsTreeEntries[6]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[6], true);

		assertLeftAndRight(_nestedSetsTreeEntries[3], 1, 2);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[3], true);

		for (NestedSetsTreeEntry nestedSetsTreeEntry : _nestedSetsTreeEntries) {
			Assert.assertNull(nestedSetsTreeEntry);
		}
	}

	@Test
	public void testError() {
		_sessionFactoryInvocationHandler.setFailOpenSession(true);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BasePersistenceImpl.class.getName(), LoggerTestUtil.OFF)) {

			try {
				ReflectionTestUtil.invoke(
					_nestedSetsTreeManager, "doCountAncestors",
					new Class<?>[] {long.class, long.class, long.class},
					new Object[] {0, 0, 0});

				Assert.fail();
			}
			catch (SystemException systemException) {
				Throwable throwable = systemException.getCause();

				throwable = throwable.getCause();

				Assert.assertEquals(
					"Unable to open session", throwable.getMessage());
			}

			try {
				ReflectionTestUtil.invoke(
					_nestedSetsTreeManager, "doCountDescendants",
					new Class<?>[] {long.class, long.class, long.class},
					new Object[] {0, 0, 0});

				Assert.fail();
			}
			catch (SystemException systemException) {
				Throwable throwable = systemException.getCause();

				throwable = throwable.getCause();

				Assert.assertEquals(
					"Unable to open session", throwable.getMessage());
			}

			try {
				ReflectionTestUtil.invoke(
					_nestedSetsTreeManager, "doGetAncestors",
					new Class<?>[] {long.class, long.class, long.class},
					new Object[] {0, 0, 0});

				Assert.fail();
			}
			catch (SystemException systemException) {
				Throwable throwable = systemException.getCause();

				throwable = throwable.getCause();

				Assert.assertEquals(
					"Unable to open session", throwable.getMessage());
			}

			try {
				ReflectionTestUtil.invoke(
					_nestedSetsTreeManager, "doGetDescendants",
					new Class<?>[] {long.class, long.class, long.class},
					new Object[] {0, 0, 0});

				Assert.fail();
			}
			catch (SystemException systemException) {
				Throwable throwable = systemException.getCause();

				throwable = throwable.getCause();

				Assert.assertEquals(
					"Unable to open session", throwable.getMessage());
			}

			try {
				ReflectionTestUtil.invoke(
					_nestedSetsTreeManager, "doUpdate",
					new Class<?>[] {
						long.class, boolean.class, long.class, long.class,
						boolean.class
					},
					new Object[] {0, true, 0, 0, true});

				Assert.fail();
			}
			catch (SystemException systemException) {
				Throwable throwable = systemException.getCause();

				throwable = throwable.getCause();

				Assert.assertEquals(
					"Unable to open session", throwable.getMessage());
			}

			try {
				ReflectionTestUtil.invoke(
					_nestedSetsTreeManager, "doUpdate",
					new Class<?>[] {
						long.class, long.class, long.class, boolean.class,
						long.class, boolean.class, List.class
					},
					new Object[] {0, 0, 0, true, 0, true, null});

				Assert.fail();
			}
			catch (SystemException systemException) {
				Throwable throwable = systemException.getCause();

				throwable = throwable.getCause();

				Assert.assertEquals(
					"Unable to open session", throwable.getMessage());
			}

			try {
				ReflectionTestUtil.invoke(
					_nestedSetsTreeManager, "getMaxNestedSetsTreeNodeRight",
					new Class<?>[] {long.class}, new Object[] {0});

				Assert.fail();
			}
			catch (SystemException systemException) {
				Throwable throwable = systemException.getCause();

				throwable = throwable.getCause();

				Assert.assertEquals(
					"Unable to open session", throwable.getMessage());
			}
		}
		finally {
			_sessionFactoryInvocationHandler.setFailOpenSession(false);
		}
	}

	@Test
	public void testGetAncestors() {
		testInsert();

		assertGetAncestors(_nestedSetsTreeEntries[0]);
		assertGetAncestors(_nestedSetsTreeEntries[1]);
		assertGetAncestors(_nestedSetsTreeEntries[2]);
		assertGetAncestors(
			_nestedSetsTreeEntries[3], _nestedSetsTreeEntries[0]);
		assertGetAncestors(
			_nestedSetsTreeEntries[4], _nestedSetsTreeEntries[0]);
		assertGetAncestors(
			_nestedSetsTreeEntries[5], _nestedSetsTreeEntries[3],
			_nestedSetsTreeEntries[0]);
		assertGetAncestors(
			_nestedSetsTreeEntries[6], _nestedSetsTreeEntries[1]);
		assertGetAncestors(
			_nestedSetsTreeEntries[7], _nestedSetsTreeEntries[6],
			_nestedSetsTreeEntries[1]);
		assertGetAncestors(
			_nestedSetsTreeEntries[8], _nestedSetsTreeEntries[4],
			_nestedSetsTreeEntries[0]);
	}

	@Test
	public void testGetDescendants() {
		testInsert();

		assertGetDescendants(
			_nestedSetsTreeEntries[0], _nestedSetsTreeEntries[3],
			_nestedSetsTreeEntries[4], _nestedSetsTreeEntries[5],
			_nestedSetsTreeEntries[8]);
		assertGetDescendants(
			_nestedSetsTreeEntries[1], _nestedSetsTreeEntries[6],
			_nestedSetsTreeEntries[7]);
		assertGetDescendants(_nestedSetsTreeEntries[2]);
		assertGetDescendants(
			_nestedSetsTreeEntries[3], _nestedSetsTreeEntries[5]);
		assertGetDescendants(
			_nestedSetsTreeEntries[4], _nestedSetsTreeEntries[8]);
		assertGetDescendants(_nestedSetsTreeEntries[5]);
		assertGetDescendants(
			_nestedSetsTreeEntries[6], _nestedSetsTreeEntries[7]);
		assertGetDescendants(_nestedSetsTreeEntries[7]);
		assertGetDescendants(_nestedSetsTreeEntries[8]);
	}

	@Test
	public void testInsert() {

		// (0)

		_nestedSetsTreeManager.insert(_nestedSetsTreeEntries[0], null);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[0]);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 2);

		// (0, 1)

		_nestedSetsTreeManager.insert(_nestedSetsTreeEntries[1], null);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[1]);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 2);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 3, 4);

		// (0, 1, 2)

		_nestedSetsTreeManager.insert(_nestedSetsTreeEntries[2], null);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[2]);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 2);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 3, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 5, 6);

		// (0(3), 1, 2)

		_nestedSetsTreeManager.insert(
			_nestedSetsTreeEntries[3], _nestedSetsTreeEntries[0]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[3]);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 5, 6);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 7, 8);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 2, 3);

		// (0(3, 4), 1, 2)

		_nestedSetsTreeManager.insert(
			_nestedSetsTreeEntries[4], _nestedSetsTreeEntries[0]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[4]);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 6);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 7, 8);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 9, 10);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 2, 3);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 4, 5);

		// (0(3(5), 4), 1, 2)

		_nestedSetsTreeManager.insert(
			_nestedSetsTreeEntries[5], _nestedSetsTreeEntries[3]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[5]);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 8);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 9, 10);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 11, 12);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 2, 5);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 6, 7);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 3, 4);

		// (0(3(5), 4), 1(6), 2)

		_nestedSetsTreeManager.insert(
			_nestedSetsTreeEntries[6], _nestedSetsTreeEntries[1]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[6]);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 8);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 9, 12);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 13, 14);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 2, 5);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 6, 7);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 3, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 10, 11);

		// (0(3(5), 4), 1(6(7)), 2)

		_nestedSetsTreeManager.insert(
			_nestedSetsTreeEntries[7], _nestedSetsTreeEntries[6]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[7]);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 8);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 9, 14);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 15, 16);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 2, 5);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 6, 7);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 3, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 10, 13);
		assertLeftAndRight(_nestedSetsTreeEntries[7], 11, 12);

		// (0(3(5), 4(8)), 1(6(7)), 2)

		_nestedSetsTreeManager.insert(
			_nestedSetsTreeEntries[8], _nestedSetsTreeEntries[4]);

		synchronizeNestedSetsTreeEntries(_nestedSetsTreeEntries[8]);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 10);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 11, 16);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 17, 18);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 2, 5);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 6, 9);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 3, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 12, 15);
		assertLeftAndRight(_nestedSetsTreeEntries[7], 13, 14);
		assertLeftAndRight(_nestedSetsTreeEntries[8], 7, 8);
	}

	@Test
	public void testMove() {
		testInsert();

		_nestedSetsTreeManager.move(_nestedSetsTreeEntries[4], null, null);

		synchronizeNestedSetsTreeEntries(null);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 10);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 11, 16);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 17, 18);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 2, 5);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 6, 9);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 3, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 12, 15);
		assertLeftAndRight(_nestedSetsTreeEntries[7], 13, 14);
		assertLeftAndRight(_nestedSetsTreeEntries[8], 7, 8);

		_nestedSetsTreeManager.move(
			_nestedSetsTreeEntries[4], _nestedSetsTreeEntries[0],
			_nestedSetsTreeEntries[0]);

		synchronizeNestedSetsTreeEntries(null);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 10);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 11, 16);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 17, 18);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 2, 5);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 6, 9);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 3, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 12, 15);
		assertLeftAndRight(_nestedSetsTreeEntries[7], 13, 14);
		assertLeftAndRight(_nestedSetsTreeEntries[8], 7, 8);

		_nestedSetsTreeManager.move(
			_nestedSetsTreeEntries[4], _nestedSetsTreeEntries[0],
			_nestedSetsTreeEntries[2]);

		synchronizeNestedSetsTreeEntries(null);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 6);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 7, 12);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 13, 18);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 2, 5);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 14, 17);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 3, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 8, 11);
		assertLeftAndRight(_nestedSetsTreeEntries[7], 9, 10);
		assertLeftAndRight(_nestedSetsTreeEntries[8], 15, 16);

		_nestedSetsTreeManager.move(
			_nestedSetsTreeEntries[2], null, _nestedSetsTreeEntries[0]);

		synchronizeNestedSetsTreeEntries(null);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 12);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 13, 18);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 6, 11);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 2, 5);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 7, 10);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 3, 4);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 14, 17);
		assertLeftAndRight(_nestedSetsTreeEntries[7], 15, 16);
		assertLeftAndRight(_nestedSetsTreeEntries[8], 8, 9);

		_nestedSetsTreeManager.move(
			_nestedSetsTreeEntries[3], _nestedSetsTreeEntries[0], null);

		synchronizeNestedSetsTreeEntries(null);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 8);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 9, 14);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 2, 7);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 15, 18);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 3, 6);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 16, 17);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 10, 13);
		assertLeftAndRight(_nestedSetsTreeEntries[7], 11, 12);
		assertLeftAndRight(_nestedSetsTreeEntries[8], 4, 5);

		_nestedSetsTreeManager.move(
			_nestedSetsTreeEntries[1], null, _nestedSetsTreeEntries[0]);

		synchronizeNestedSetsTreeEntries(null);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 14);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 8, 13);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 2, 7);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 15, 18);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 3, 6);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 16, 17);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 9, 12);
		assertLeftAndRight(_nestedSetsTreeEntries[7], 10, 11);
		assertLeftAndRight(_nestedSetsTreeEntries[8], 4, 5);

		_nestedSetsTreeManager.move(
			_nestedSetsTreeEntries[3], null, _nestedSetsTreeEntries[1]);

		synchronizeNestedSetsTreeEntries(null);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 18);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 8, 17);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 2, 7);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 13, 16);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 3, 6);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 14, 15);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 9, 12);
		assertLeftAndRight(_nestedSetsTreeEntries[7], 10, 11);
		assertLeftAndRight(_nestedSetsTreeEntries[8], 4, 5);

		_nestedSetsTreeManager.move(
			_nestedSetsTreeEntries[2], _nestedSetsTreeEntries[0],
			_nestedSetsTreeEntries[3]);

		synchronizeNestedSetsTreeEntries(null);

		assertLeftAndRight(_nestedSetsTreeEntries[0], 1, 18);
		assertLeftAndRight(_nestedSetsTreeEntries[1], 2, 17);
		assertLeftAndRight(_nestedSetsTreeEntries[2], 10, 15);
		assertLeftAndRight(_nestedSetsTreeEntries[3], 7, 16);
		assertLeftAndRight(_nestedSetsTreeEntries[4], 11, 14);
		assertLeftAndRight(_nestedSetsTreeEntries[5], 8, 9);
		assertLeftAndRight(_nestedSetsTreeEntries[6], 3, 6);
		assertLeftAndRight(_nestedSetsTreeEntries[7], 4, 5);
		assertLeftAndRight(_nestedSetsTreeEntries[8], 12, 13);
	}

	protected void assertCountAncestors(
		long ancestorsCount, NestedSetsTreeEntry nestedSetsTreeEntry) {

		Assert.assertEquals(
			ancestorsCount,
			_nestedSetsTreeManager.countAncestors(nestedSetsTreeEntry));
	}

	protected void assertCountChildren(
		long childrenCount, NestedSetsTreeEntry nestedSetsTreeEntry) {

		Assert.assertEquals(
			childrenCount,
			_nestedSetsTreeManager.countDescendants(nestedSetsTreeEntry));
	}

	protected void assertGetAncestors(
		NestedSetsTreeEntry nestedSetsTreeEntry,
		NestedSetsTreeEntry... ancestorNestedSetsTreeEntries) {

		List<NestedSetsTreeEntry> expectedNestedSetsTreeEntries =
			new ArrayList<>(Arrays.asList(ancestorNestedSetsTreeEntries));

		expectedNestedSetsTreeEntries.add(nestedSetsTreeEntry);

		Collections.sort(expectedNestedSetsTreeEntries);

		List<NestedSetsTreeEntry> actualNestedSetsTreeEntries = new ArrayList<>(
			_nestedSetsTreeManager.getAncestors(nestedSetsTreeEntry));

		Collections.sort(actualNestedSetsTreeEntries);

		Assert.assertEquals(
			expectedNestedSetsTreeEntries, actualNestedSetsTreeEntries);
	}

	protected void assertGetDescendants(
		NestedSetsTreeEntry nestedSetsTreeEntry,
		NestedSetsTreeEntry... childNestedSetsTreeEntries) {

		List<NestedSetsTreeEntry> expectedNestedSetsTreeEntries =
			new ArrayList<>(Arrays.asList(childNestedSetsTreeEntries));

		expectedNestedSetsTreeEntries.add(nestedSetsTreeEntry);

		Collections.sort(expectedNestedSetsTreeEntries);

		List<NestedSetsTreeEntry> actualNestedSetsTreeEntries = new ArrayList<>(
			_nestedSetsTreeManager.getDescendants(nestedSetsTreeEntry));

		Collections.sort(actualNestedSetsTreeEntries);

		Assert.assertEquals(
			expectedNestedSetsTreeEntries, actualNestedSetsTreeEntries);
	}

	protected void assertLeftAndRight(
		NestedSetsTreeEntry nestedSetsTreeEntry, long leftNestedSetsTreeEntryId,
		long rightNestedSetsTreeEntryId) {

		Assert.assertEquals(
			leftNestedSetsTreeEntryId,
			nestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			rightNestedSetsTreeEntryId,
			nestedSetsTreeEntry.getRightNestedSetsTreeEntryId());

		_nestedSetsTreeEntryPersistence.update(nestedSetsTreeEntry);
	}

	protected void synchronizeNestedSetsTreeEntries(
		NestedSetsTreeEntry nestedSetsTreeEntry) {

		synchronizeNestedSetsTreeEntries(nestedSetsTreeEntry, false);
	}

	protected void synchronizeNestedSetsTreeEntries(
		NestedSetsTreeEntry nestedSetsTreeEntry, boolean delete) {

		if (nestedSetsTreeEntry != null) {
			if (delete) {
				_nestedSetsTreeEntryPersistence.remove(nestedSetsTreeEntry);
			}
			else {
				nestedSetsTreeEntry = _nestedSetsTreeEntryPersistence.update(
					nestedSetsTreeEntry);
			}
		}

		_nestedSetsTreeEntryPersistence.clearCache();

		for (int i = 0; i < _nestedSetsTreeEntries.length; i++) {
			nestedSetsTreeEntry = _nestedSetsTreeEntries[i];

			if (nestedSetsTreeEntry == null) {
				continue;
			}

			_nestedSetsTreeEntries[i] =
				_nestedSetsTreeEntryPersistence.fetchByPrimaryKey(
					nestedSetsTreeEntry.getNestedSetsTreeEntryId());
		}
	}

	private static Group _group;

	@Inject
	private static GroupLocalService _groupLocalService;

	private static NestedSetsTreeEntry[] _nestedSetsTreeEntries;

	@Inject
	private static NestedSetsTreeEntryPersistence
		_nestedSetsTreeEntryPersistence;

	private static NestedSetsTreeManager<NestedSetsTreeEntry>
		_nestedSetsTreeManager;
	private static SessionFactoryInvocationHandler
		_sessionFactoryInvocationHandler;

	@Inject
	private NestedSetsTreeEntryLocalService _nestedSetsTreeEntryLocalService;

	private static class SessionFactoryInvocationHandler
		implements InvocationHandler {

		public Object getTarget() {
			return _target;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			String methodName = method.getName();

			if (methodName.equals("closeSession")) {
				Session session = (Session)args[0];

				if (session == null) {
					return null;
				}

				session.flush();

				session.clear();
			}
			else if (methodName.equals("openSession") && _failOpenSession) {
				throw new Exception("Unable to open session");
			}

			return method.invoke(_target, args);
		}

		public void setFailOpenSession(boolean failOpenSession) {
			_failOpenSession = failOpenSession;
		}

		private SessionFactoryInvocationHandler(Object target) {
			_target = target;
		}

		private boolean _failOpenSession;
		private final Object _target;

	}

}