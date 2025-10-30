/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.reports.engine.console.exception.NoSuchSourceException;
import com.liferay.portal.reports.engine.console.model.Source;
import com.liferay.portal.reports.engine.console.service.persistence.SourcePersistence;
import com.liferay.portal.reports.engine.console.service.persistence.SourceUtil;
import com.liferay.portal.security.permission.SimplePermissionChecker;
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
public class SourcePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.reports.engine.console.service"));

	@Before
	public void setUp() {
		_persistence = SourceUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Source> iterator = _sources.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Source source = _persistence.create(pk);

		Assert.assertNotNull(source);

		Assert.assertEquals(source.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Source newSource = addSource();

		_persistence.remove(newSource);

		Source existingSource = _persistence.fetchByPrimaryKey(
			newSource.getPrimaryKey());

		Assert.assertNull(existingSource);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSource();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Source newSource = _persistence.create(pk);

		newSource.setUuid(RandomTestUtil.randomString());

		newSource.setGroupId(RandomTestUtil.nextLong());

		newSource.setCompanyId(RandomTestUtil.nextLong());

		newSource.setUserId(RandomTestUtil.nextLong());

		newSource.setUserName(RandomTestUtil.randomString());

		newSource.setCreateDate(RandomTestUtil.nextDate());

		newSource.setModifiedDate(RandomTestUtil.nextDate());

		newSource.setLastPublishDate(RandomTestUtil.nextDate());

		newSource.setName(RandomTestUtil.randomString());

		newSource.setDriverClassName(RandomTestUtil.randomString());

		newSource.setDriverUrl(RandomTestUtil.randomString());

		newSource.setDriverUserName(RandomTestUtil.randomString());

		newSource.setDriverPassword(RandomTestUtil.randomString());

		_sources.add(_persistence.update(newSource));

		Source existingSource = _persistence.findByPrimaryKey(
			newSource.getPrimaryKey());

		Assert.assertEquals(existingSource.getUuid(), newSource.getUuid());
		Assert.assertEquals(
			existingSource.getSourceId(), newSource.getSourceId());
		Assert.assertEquals(
			existingSource.getGroupId(), newSource.getGroupId());
		Assert.assertEquals(
			existingSource.getCompanyId(), newSource.getCompanyId());
		Assert.assertEquals(existingSource.getUserId(), newSource.getUserId());
		Assert.assertEquals(
			existingSource.getUserName(), newSource.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSource.getCreateDate()),
			Time.getShortTimestamp(newSource.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingSource.getModifiedDate()),
			Time.getShortTimestamp(newSource.getModifiedDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingSource.getLastPublishDate()),
			Time.getShortTimestamp(newSource.getLastPublishDate()));
		Assert.assertEquals(existingSource.getName(), newSource.getName());
		Assert.assertEquals(
			existingSource.getDriverClassName(),
			newSource.getDriverClassName());
		Assert.assertEquals(
			existingSource.getDriverUrl(), newSource.getDriverUrl());
		Assert.assertEquals(
			existingSource.getDriverUserName(), newSource.getDriverUserName());
		Assert.assertEquals(
			existingSource.getDriverPassword(), newSource.getDriverPassword());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Source newSource = addSource();

		Source existingSource = _persistence.findByPrimaryKey(
			newSource.getPrimaryKey());

		Assert.assertEquals(existingSource, newSource);
	}

	@Test(expected = NoSuchSourceException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Source> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Reports_Source", "uuid", true, "sourceId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "lastPublishDate", true, "name", true,
			"driverClassName", true, "driverUrl", true, "driverUserName", true,
			"driverPassword", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Source newSource = addSource();

		Source existingSource = _persistence.fetchByPrimaryKey(
			newSource.getPrimaryKey());

		Assert.assertEquals(existingSource, newSource);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Source missingSource = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSource);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Source newSource1 = addSource();
		Source newSource2 = addSource();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSource1.getPrimaryKey());
		primaryKeys.add(newSource2.getPrimaryKey());

		Map<Serializable, Source> sources = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, sources.size());
		Assert.assertEquals(
			newSource1, sources.get(newSource1.getPrimaryKey()));
		Assert.assertEquals(
			newSource2, sources.get(newSource2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Source> sources = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(sources.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Source newSource = addSource();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSource.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Source> sources = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, sources.size());
		Assert.assertEquals(newSource, sources.get(newSource.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Source> sources = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(sources.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Source newSource = addSource();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSource.getPrimaryKey());

		Map<Serializable, Source> sources = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, sources.size());
		Assert.assertEquals(newSource, sources.get(newSource.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Source newSource = addSource();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newSource.getPrimaryKey()));
	}

	private void _assertOriginalValues(Source source) {
		Assert.assertEquals(
			source.getUuid(),
			ReflectionTestUtil.invoke(
				source, "getColumnOriginalValue", new Class<?>[] {String.class},
				"uuid_"));
		Assert.assertEquals(
			Long.valueOf(source.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				source, "getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));
	}

	protected Source addSource() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Source source = _persistence.create(pk);

		source.setUuid(RandomTestUtil.randomString());

		source.setGroupId(RandomTestUtil.nextLong());

		source.setCompanyId(RandomTestUtil.nextLong());

		source.setUserId(RandomTestUtil.nextLong());

		source.setUserName(RandomTestUtil.randomString());

		source.setCreateDate(RandomTestUtil.nextDate());

		source.setModifiedDate(RandomTestUtil.nextDate());

		source.setLastPublishDate(RandomTestUtil.nextDate());

		source.setName(RandomTestUtil.randomString());

		source.setDriverClassName(RandomTestUtil.randomString());

		source.setDriverUrl(RandomTestUtil.randomString());

		source.setDriverUserName(RandomTestUtil.randomString());

		source.setDriverPassword(RandomTestUtil.randomString());

		_sources.add(_persistence.update(source));

		return source;
	}

	private List<Source> _sources = new ArrayList<Source>();
	private SourcePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}