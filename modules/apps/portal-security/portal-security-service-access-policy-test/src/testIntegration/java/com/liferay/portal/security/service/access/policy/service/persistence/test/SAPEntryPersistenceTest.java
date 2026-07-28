/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.service.access.policy.exception.NoSuchEntryException;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalServiceUtil;
import com.liferay.portal.security.service.access.policy.service.persistence.SAPEntryPersistence;
import com.liferay.portal.security.service.access.policy.service.persistence.SAPEntryUtil;
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
public class SAPEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.security.service.access.policy.service"));

	@Before
	public void setUp() {
		_persistence = SAPEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SAPEntry> iterator = _sapEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SAPEntry sapEntry = _persistence.create(pk);

		Assert.assertNotNull(sapEntry);

		Assert.assertEquals(sapEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SAPEntry newSAPEntry = addSAPEntry();

		_persistence.remove(newSAPEntry);

		SAPEntry existingSAPEntry = _persistence.fetchByPrimaryKey(
			newSAPEntry.getPrimaryKey());

		Assert.assertNull(existingSAPEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSAPEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		SAPEntry newSAPEntry = addSAPEntry();

		newSAPEntry.setUuid(RandomTestUtil.randomString());

		newSAPEntry.setCompanyId(RandomTestUtil.nextLong());

		newSAPEntry.setUserId(RandomTestUtil.nextLong());

		newSAPEntry.setUserName(RandomTestUtil.randomString());

		newSAPEntry.setCreateDate(RandomTestUtil.nextDate());

		newSAPEntry.setModifiedDate(RandomTestUtil.nextDate());

		newSAPEntry.setAllowedServiceSignatures(RandomTestUtil.randomString());

		newSAPEntry.setDefaultSAPEntry(RandomTestUtil.randomBoolean());

		newSAPEntry.setEnabled(RandomTestUtil.randomBoolean());

		newSAPEntry.setName(RandomTestUtil.randomString());

		newSAPEntry.setTitle(RandomTestUtil.randomString());

		newSAPEntry = _persistence.update(newSAPEntry);

		_sapEntries.add(newSAPEntry);

		SAPEntry existingSAPEntry = _persistence.findByPrimaryKey(
			newSAPEntry.getPrimaryKey());

		Assert.assertEquals(existingSAPEntry.getUuid(), newSAPEntry.getUuid());
		Assert.assertEquals(
			existingSAPEntry.getSapEntryId(), newSAPEntry.getSapEntryId());
		Assert.assertEquals(
			existingSAPEntry.getCompanyId(), newSAPEntry.getCompanyId());
		Assert.assertEquals(
			existingSAPEntry.getUserId(), newSAPEntry.getUserId());
		Assert.assertEquals(
			existingSAPEntry.getUserName(), newSAPEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSAPEntry.getCreateDate()),
			Time.getShortTimestamp(newSAPEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingSAPEntry.getModifiedDate()),
			Time.getShortTimestamp(newSAPEntry.getModifiedDate()));
		Assert.assertEquals(
			existingSAPEntry.getAllowedServiceSignatures(),
			newSAPEntry.getAllowedServiceSignatures());
		Assert.assertEquals(
			existingSAPEntry.isDefaultSAPEntry(),
			newSAPEntry.isDefaultSAPEntry());
		Assert.assertEquals(
			existingSAPEntry.isEnabled(), newSAPEntry.isEnabled());
		Assert.assertEquals(existingSAPEntry.getName(), newSAPEntry.getName());
		Assert.assertEquals(
			existingSAPEntry.getTitle(), newSAPEntry.getTitle());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_D() throws Exception {
		_persistence.countByC_D(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_D(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_N() throws Exception {
		_persistence.countByC_N(RandomTestUtil.nextLong(), "");

		_persistence.countByC_N(0L, "null");

		_persistence.countByC_N(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SAPEntry newSAPEntry = addSAPEntry();

		SAPEntry existingSAPEntry = _persistence.findByPrimaryKey(
			newSAPEntry.getPrimaryKey());

		Assert.assertEquals(existingSAPEntry, newSAPEntry);
	}

	@Test(expected = NoSuchEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SAPEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SAPEntry", "uuid", true, "sapEntryId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "allowedServiceSignatures", true,
			"defaultSAPEntry", true, "enabled", true, "name", true, "title",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SAPEntry newSAPEntry = addSAPEntry();

		SAPEntry existingSAPEntry = _persistence.fetchByPrimaryKey(
			newSAPEntry.getPrimaryKey());

		Assert.assertEquals(existingSAPEntry, newSAPEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SAPEntry missingSAPEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSAPEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SAPEntry newSAPEntry1 = addSAPEntry();
		SAPEntry newSAPEntry2 = addSAPEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSAPEntry1.getPrimaryKey());
		primaryKeys.add(newSAPEntry2.getPrimaryKey());

		Map<Serializable, SAPEntry> sapEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, sapEntries.size());
		Assert.assertEquals(
			newSAPEntry1, sapEntries.get(newSAPEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newSAPEntry2, sapEntries.get(newSAPEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SAPEntry> sapEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(sapEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SAPEntry newSAPEntry = addSAPEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSAPEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SAPEntry> sapEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, sapEntries.size());
		Assert.assertEquals(
			newSAPEntry, sapEntries.get(newSAPEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SAPEntry> sapEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(sapEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SAPEntry newSAPEntry = addSAPEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSAPEntry.getPrimaryKey());

		Map<Serializable, SAPEntry> sapEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, sapEntries.size());
		Assert.assertEquals(
			newSAPEntry, sapEntries.get(newSAPEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SAPEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<SAPEntry>() {

				@Override
				public void performAction(SAPEntry sapEntry) {
					Assert.assertNotNull(sapEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SAPEntry newSAPEntry = addSAPEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SAPEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"sapEntryId", newSAPEntry.getSapEntryId()));

		List<SAPEntry> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		SAPEntry existingSAPEntry = result.get(0);

		Assert.assertEquals(existingSAPEntry, newSAPEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SAPEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"sapEntryId", RandomTestUtil.nextLong()));

		List<SAPEntry> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SAPEntry newSAPEntry = addSAPEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SAPEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("sapEntryId"));

		Object newSapEntryId = newSAPEntry.getSapEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"sapEntryId", new Object[] {newSapEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSapEntryId = result.get(0);

		Assert.assertEquals(existingSapEntryId, newSapEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SAPEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("sapEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"sapEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SAPEntry newSAPEntry = addSAPEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newSAPEntry.getPrimaryKey()));
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

		SAPEntry newSAPEntry = addSAPEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SAPEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"sapEntryId", newSAPEntry.getSapEntryId()));

		List<SAPEntry> result = _persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(SAPEntry sapEntry) {
		Assert.assertEquals(
			Long.valueOf(sapEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				sapEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			sapEntry.getName(),
			ReflectionTestUtil.invoke(
				sapEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected SAPEntry addSAPEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SAPEntry sapEntry = _persistence.create(pk);

		sapEntry.setUuid(RandomTestUtil.randomString());

		sapEntry.setCompanyId(RandomTestUtil.nextLong());

		sapEntry.setUserId(RandomTestUtil.nextLong());

		sapEntry.setUserName(RandomTestUtil.randomString());

		sapEntry.setCreateDate(RandomTestUtil.nextDate());

		sapEntry.setModifiedDate(RandomTestUtil.nextDate());

		sapEntry.setAllowedServiceSignatures(RandomTestUtil.randomString());

		sapEntry.setDefaultSAPEntry(RandomTestUtil.randomBoolean());

		sapEntry.setEnabled(RandomTestUtil.randomBoolean());

		sapEntry.setName(RandomTestUtil.randomString());

		sapEntry.setTitle(RandomTestUtil.randomString());

		_sapEntries.add(_persistence.update(sapEntry));

		return sapEntry;
	}

	private List<SAPEntry> _sapEntries = new ArrayList<SAPEntry>();
	private SAPEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-979646221