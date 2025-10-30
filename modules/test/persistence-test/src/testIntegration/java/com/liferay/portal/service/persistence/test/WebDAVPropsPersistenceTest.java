/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchWebDAVPropsException;
import com.liferay.portal.kernel.model.WebDAVProps;
import com.liferay.portal.kernel.service.persistence.WebDAVPropsPersistence;
import com.liferay.portal.kernel.service.persistence.WebDAVPropsUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
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
public class WebDAVPropsPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = WebDAVPropsUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<WebDAVProps> iterator = _webDAVPropses.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WebDAVProps webDAVProps = _persistence.create(pk);

		Assert.assertNotNull(webDAVProps);

		Assert.assertEquals(webDAVProps.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		WebDAVProps newWebDAVProps = addWebDAVProps();

		_persistence.remove(newWebDAVProps);

		WebDAVProps existingWebDAVProps = _persistence.fetchByPrimaryKey(
			newWebDAVProps.getPrimaryKey());

		Assert.assertNull(existingWebDAVProps);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addWebDAVProps();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WebDAVProps newWebDAVProps = _persistence.create(pk);

		newWebDAVProps.setMvccVersion(RandomTestUtil.nextLong());

		newWebDAVProps.setCompanyId(RandomTestUtil.nextLong());

		newWebDAVProps.setCreateDate(RandomTestUtil.nextDate());

		newWebDAVProps.setModifiedDate(RandomTestUtil.nextDate());

		newWebDAVProps.setClassNameId(RandomTestUtil.nextLong());

		newWebDAVProps.setClassPK(RandomTestUtil.nextLong());

		newWebDAVProps.setProps(RandomTestUtil.randomString());

		_webDAVPropses.add(_persistence.update(newWebDAVProps));

		WebDAVProps existingWebDAVProps = _persistence.findByPrimaryKey(
			newWebDAVProps.getPrimaryKey());

		Assert.assertEquals(
			existingWebDAVProps.getMvccVersion(),
			newWebDAVProps.getMvccVersion());
		Assert.assertEquals(
			existingWebDAVProps.getWebDavPropsId(),
			newWebDAVProps.getWebDavPropsId());
		Assert.assertEquals(
			existingWebDAVProps.getCompanyId(), newWebDAVProps.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingWebDAVProps.getCreateDate()),
			Time.getShortTimestamp(newWebDAVProps.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingWebDAVProps.getModifiedDate()),
			Time.getShortTimestamp(newWebDAVProps.getModifiedDate()));
		Assert.assertEquals(
			existingWebDAVProps.getClassNameId(),
			newWebDAVProps.getClassNameId());
		Assert.assertEquals(
			existingWebDAVProps.getClassPK(), newWebDAVProps.getClassPK());
		Assert.assertEquals(
			existingWebDAVProps.getProps(), newWebDAVProps.getProps());
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		WebDAVProps newWebDAVProps = addWebDAVProps();

		WebDAVProps existingWebDAVProps = _persistence.findByPrimaryKey(
			newWebDAVProps.getPrimaryKey());

		Assert.assertEquals(existingWebDAVProps, newWebDAVProps);
	}

	@Test(expected = NoSuchWebDAVPropsException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<WebDAVProps> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"WebDAVProps", "mvccVersion", true, "webDavPropsId", true,
			"companyId", true, "createDate", true, "modifiedDate", true,
			"classNameId", true, "classPK", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		WebDAVProps newWebDAVProps = addWebDAVProps();

		WebDAVProps existingWebDAVProps = _persistence.fetchByPrimaryKey(
			newWebDAVProps.getPrimaryKey());

		Assert.assertEquals(existingWebDAVProps, newWebDAVProps);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WebDAVProps missingWebDAVProps = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingWebDAVProps);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		WebDAVProps newWebDAVProps1 = addWebDAVProps();
		WebDAVProps newWebDAVProps2 = addWebDAVProps();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWebDAVProps1.getPrimaryKey());
		primaryKeys.add(newWebDAVProps2.getPrimaryKey());

		Map<Serializable, WebDAVProps> webDAVPropses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, webDAVPropses.size());
		Assert.assertEquals(
			newWebDAVProps1,
			webDAVPropses.get(newWebDAVProps1.getPrimaryKey()));
		Assert.assertEquals(
			newWebDAVProps2,
			webDAVPropses.get(newWebDAVProps2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, WebDAVProps> webDAVPropses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(webDAVPropses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		WebDAVProps newWebDAVProps = addWebDAVProps();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWebDAVProps.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, WebDAVProps> webDAVPropses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, webDAVPropses.size());
		Assert.assertEquals(
			newWebDAVProps, webDAVPropses.get(newWebDAVProps.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, WebDAVProps> webDAVPropses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(webDAVPropses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		WebDAVProps newWebDAVProps = addWebDAVProps();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWebDAVProps.getPrimaryKey());

		Map<Serializable, WebDAVProps> webDAVPropses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, webDAVPropses.size());
		Assert.assertEquals(
			newWebDAVProps, webDAVPropses.get(newWebDAVProps.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		WebDAVProps newWebDAVProps = addWebDAVProps();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newWebDAVProps.getPrimaryKey()));
	}

	private void _assertOriginalValues(WebDAVProps webDAVProps) {
		Assert.assertEquals(
			Long.valueOf(webDAVProps.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				webDAVProps, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(webDAVProps.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				webDAVProps, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected WebDAVProps addWebDAVProps() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WebDAVProps webDAVProps = _persistence.create(pk);

		webDAVProps.setMvccVersion(RandomTestUtil.nextLong());

		webDAVProps.setCompanyId(RandomTestUtil.nextLong());

		webDAVProps.setCreateDate(RandomTestUtil.nextDate());

		webDAVProps.setModifiedDate(RandomTestUtil.nextDate());

		webDAVProps.setClassNameId(RandomTestUtil.nextLong());

		webDAVProps.setClassPK(RandomTestUtil.nextLong());

		webDAVProps.setProps(RandomTestUtil.randomString());

		_webDAVPropses.add(_persistence.update(webDAVProps));

		return webDAVProps;
	}

	private List<WebDAVProps> _webDAVPropses = new ArrayList<WebDAVProps>();
	private WebDAVPropsPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}