/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchClassNameException;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.ClassNamePersistence;
import com.liferay.portal.kernel.service.persistence.ClassNameUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class ClassNamePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = ClassNameUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ClassName> iterator = _classNames.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ClassName className = _persistence.create(pk);

		Assert.assertNotNull(className);

		Assert.assertEquals(className.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ClassName newClassName = addClassName();

		_persistence.remove(newClassName);

		ClassName existingClassName = _persistence.fetchByPrimaryKey(
			newClassName.getPrimaryKey());

		Assert.assertNull(existingClassName);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addClassName();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		ClassName newClassName = addClassName();

		newClassName.setValue(RandomTestUtil.randomString());

		newClassName = _persistence.update(newClassName);

		_classNames.add(newClassName);

		ClassName existingClassName = _persistence.findByPrimaryKey(
			newClassName.getPrimaryKey());

		Assert.assertEquals(
			existingClassName.getMvccVersion(), newClassName.getMvccVersion());
		Assert.assertEquals(
			existingClassName.getClassNameId(), newClassName.getClassNameId());
		Assert.assertEquals(
			existingClassName.getValue(), newClassName.getValue());
	}

	@Test
	public void testCountByValue() throws Exception {
		_persistence.countByValue("");

		_persistence.countByValue("null");

		_persistence.countByValue((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ClassName newClassName = addClassName();

		ClassName existingClassName = _persistence.findByPrimaryKey(
			newClassName.getPrimaryKey());

		Assert.assertEquals(existingClassName, newClassName);
	}

	@Test(expected = NoSuchClassNameException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ClassName> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ClassName_", "mvccVersion", true, "classNameId", true, "value",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ClassName newClassName = addClassName();

		ClassName existingClassName = _persistence.fetchByPrimaryKey(
			newClassName.getPrimaryKey());

		Assert.assertEquals(existingClassName, newClassName);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ClassName missingClassName = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingClassName);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ClassName newClassName1 = addClassName();
		ClassName newClassName2 = addClassName();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newClassName1.getPrimaryKey());
		primaryKeys.add(newClassName2.getPrimaryKey());

		Map<Serializable, ClassName> classNames =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, classNames.size());
		Assert.assertEquals(
			newClassName1, classNames.get(newClassName1.getPrimaryKey()));
		Assert.assertEquals(
			newClassName2, classNames.get(newClassName2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ClassName> classNames =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(classNames.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ClassName newClassName = addClassName();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newClassName.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ClassName> classNames =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, classNames.size());
		Assert.assertEquals(
			newClassName, classNames.get(newClassName.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ClassName> classNames =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(classNames.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ClassName newClassName = addClassName();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newClassName.getPrimaryKey());

		Map<Serializable, ClassName> classNames =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, classNames.size());
		Assert.assertEquals(
			newClassName, classNames.get(newClassName.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ClassNameLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<ClassName>() {

				@Override
				public void performAction(ClassName className) {
					Assert.assertNotNull(className);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ClassName newClassName = addClassName();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ClassName.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"classNameId", newClassName.getClassNameId()));

		List<ClassName> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ClassName existingClassName = result.get(0);

		Assert.assertEquals(existingClassName, newClassName);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ClassName.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"classNameId", RandomTestUtil.nextLong()));

		List<ClassName> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ClassName newClassName = addClassName();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ClassName.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("classNameId"));

		Object newClassNameId = newClassName.getClassNameId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"classNameId", new Object[] {newClassNameId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingClassNameId = result.get(0);

		Assert.assertEquals(existingClassNameId, newClassNameId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ClassName.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("classNameId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"classNameId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ClassName newClassName = addClassName();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newClassName.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		ClassName newClassName = addClassName();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ClassName.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"classNameId", newClassName.getClassNameId()));

		List<ClassName> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ClassName className) {
		Assert.assertEquals(
			className.getValue(),
			ReflectionTestUtil.invoke(
				className, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "value"));
	}

	protected ClassName addClassName() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ClassName className = _persistence.create(pk);

		className.setValue(RandomTestUtil.randomString());

		_classNames.add(_persistence.update(className));

		return className;
	}

	private List<ClassName> _classNames = new ArrayList<ClassName>();
	private ClassNamePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1912573359