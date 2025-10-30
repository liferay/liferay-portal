/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchClassNameException;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.service.persistence.ClassNamePersistence;
import com.liferay.portal.kernel.service.persistence.ClassNameUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
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
		long pk = RandomTestUtil.nextLong();

		ClassName newClassName = _persistence.create(pk);

		newClassName.setMvccVersion(RandomTestUtil.nextLong());

		newClassName.setValue(RandomTestUtil.randomString());

		_classNames.add(_persistence.update(newClassName));

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
	public void testResetOriginalValues() throws Exception {
		ClassName newClassName = addClassName();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newClassName.getPrimaryKey()));
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

		className.setMvccVersion(RandomTestUtil.nextLong());

		className.setValue(RandomTestUtil.randomString());

		_classNames.add(_persistence.update(className));

		return className;
	}

	private List<ClassName> _classNames = new ArrayList<ClassName>();
	private ClassNamePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}