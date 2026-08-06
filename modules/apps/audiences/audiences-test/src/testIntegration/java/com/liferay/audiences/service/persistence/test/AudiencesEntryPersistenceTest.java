/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.audiences.exception.DuplicateAudiencesEntryExternalReferenceCodeException;
import com.liferay.audiences.exception.NoSuchAudiencesEntryException;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryLocalServiceUtil;
import com.liferay.audiences.service.persistence.AudiencesEntryPersistence;
import com.liferay.audiences.service.persistence.AudiencesEntryUtil;
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
public class AudiencesEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.audiences.service"));

	@Before
	public void setUp() {
		_persistence = AudiencesEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AudiencesEntry> iterator = _audiencesEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AudiencesEntry audiencesEntry = _persistence.create(pk);

		Assert.assertNotNull(audiencesEntry);

		Assert.assertEquals(audiencesEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AudiencesEntry newAudiencesEntry = addAudiencesEntry();

		_persistence.remove(newAudiencesEntry);

		AudiencesEntry existingAudiencesEntry = _persistence.fetchByPrimaryKey(
			newAudiencesEntry.getPrimaryKey());

		Assert.assertNull(existingAudiencesEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAudiencesEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		AudiencesEntry newAudiencesEntry = addAudiencesEntry();

		newAudiencesEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newAudiencesEntry.setCompanyId(RandomTestUtil.nextLong());

		newAudiencesEntry.setUserId(RandomTestUtil.nextLong());

		newAudiencesEntry.setUserName(RandomTestUtil.randomString());

		newAudiencesEntry.setCreateDate(RandomTestUtil.nextDate());

		newAudiencesEntry.setModifiedDate(RandomTestUtil.nextDate());

		newAudiencesEntry.setJSON(RandomTestUtil.randomString());

		newAudiencesEntry.setName(RandomTestUtil.randomString());

		newAudiencesEntry = _persistence.update(newAudiencesEntry);

		_audiencesEntries.add(newAudiencesEntry);

		AudiencesEntry existingAudiencesEntry = _persistence.findByPrimaryKey(
			newAudiencesEntry.getPrimaryKey());

		Assert.assertEquals(
			existingAudiencesEntry.getMvccVersion(),
			newAudiencesEntry.getMvccVersion());
		Assert.assertEquals(
			existingAudiencesEntry.getExternalReferenceCode(),
			newAudiencesEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingAudiencesEntry.getAudiencesEntryId(),
			newAudiencesEntry.getAudiencesEntryId());
		Assert.assertEquals(
			existingAudiencesEntry.getCompanyId(),
			newAudiencesEntry.getCompanyId());
		Assert.assertEquals(
			existingAudiencesEntry.getUserId(), newAudiencesEntry.getUserId());
		Assert.assertEquals(
			existingAudiencesEntry.getUserName(),
			newAudiencesEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAudiencesEntry.getCreateDate()),
			Time.getShortTimestamp(newAudiencesEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingAudiencesEntry.getModifiedDate()),
			Time.getShortTimestamp(newAudiencesEntry.getModifiedDate()));
		Assert.assertEquals(
			existingAudiencesEntry.getJSON(), newAudiencesEntry.getJSON());
		Assert.assertEquals(
			existingAudiencesEntry.getName(), newAudiencesEntry.getName());
	}

	@Test(
		expected = DuplicateAudiencesEntryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		AudiencesEntry audiencesEntry = addAudiencesEntry();

		AudiencesEntry newAudiencesEntry = addAudiencesEntry();

		newAudiencesEntry.setCompanyId(audiencesEntry.getCompanyId());

		newAudiencesEntry = _persistence.update(newAudiencesEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newAudiencesEntry);

		newAudiencesEntry.setExternalReferenceCode(
			audiencesEntry.getExternalReferenceCode());

		_persistence.update(newAudiencesEntry);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_LikeN() throws Exception {
		_persistence.countByC_LikeN(RandomTestUtil.nextLong(), "");

		_persistence.countByC_LikeN(0L, "null");

		_persistence.countByC_LikeN(0L, (String)null);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AudiencesEntry newAudiencesEntry = addAudiencesEntry();

		AudiencesEntry existingAudiencesEntry = _persistence.findByPrimaryKey(
			newAudiencesEntry.getPrimaryKey());

		Assert.assertEquals(existingAudiencesEntry, newAudiencesEntry);
	}

	@Test(expected = NoSuchAudiencesEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AudiencesEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AudiencesEntry", "mvccVersion", true, "externalReferenceCode",
			true, "audiencesEntryId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true, "name",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AudiencesEntry newAudiencesEntry = addAudiencesEntry();

		AudiencesEntry existingAudiencesEntry = _persistence.fetchByPrimaryKey(
			newAudiencesEntry.getPrimaryKey());

		Assert.assertEquals(existingAudiencesEntry, newAudiencesEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AudiencesEntry missingAudiencesEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingAudiencesEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AudiencesEntry newAudiencesEntry1 = addAudiencesEntry();
		AudiencesEntry newAudiencesEntry2 = addAudiencesEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAudiencesEntry1.getPrimaryKey());
		primaryKeys.add(newAudiencesEntry2.getPrimaryKey());

		Map<Serializable, AudiencesEntry> audiencesEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, audiencesEntries.size());
		Assert.assertEquals(
			newAudiencesEntry1,
			audiencesEntries.get(newAudiencesEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newAudiencesEntry2,
			audiencesEntries.get(newAudiencesEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AudiencesEntry> audiencesEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(audiencesEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AudiencesEntry newAudiencesEntry = addAudiencesEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAudiencesEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AudiencesEntry> audiencesEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, audiencesEntries.size());
		Assert.assertEquals(
			newAudiencesEntry,
			audiencesEntries.get(newAudiencesEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AudiencesEntry> audiencesEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(audiencesEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AudiencesEntry newAudiencesEntry = addAudiencesEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAudiencesEntry.getPrimaryKey());

		Map<Serializable, AudiencesEntry> audiencesEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, audiencesEntries.size());
		Assert.assertEquals(
			newAudiencesEntry,
			audiencesEntries.get(newAudiencesEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			AudiencesEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<AudiencesEntry>() {

				@Override
				public void performAction(AudiencesEntry audiencesEntry) {
					Assert.assertNotNull(audiencesEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		AudiencesEntry newAudiencesEntry = addAudiencesEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AudiencesEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"audiencesEntryId", newAudiencesEntry.getAudiencesEntryId()));

		List<AudiencesEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		AudiencesEntry existingAudiencesEntry = result.get(0);

		Assert.assertEquals(existingAudiencesEntry, newAudiencesEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AudiencesEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"audiencesEntryId", RandomTestUtil.nextLong()));

		List<AudiencesEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		AudiencesEntry newAudiencesEntry = addAudiencesEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AudiencesEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("audiencesEntryId"));

		Object newAudiencesEntryId = newAudiencesEntry.getAudiencesEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"audiencesEntryId", new Object[] {newAudiencesEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingAudiencesEntryId = result.get(0);

		Assert.assertEquals(existingAudiencesEntryId, newAudiencesEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AudiencesEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("audiencesEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"audiencesEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AudiencesEntry newAudiencesEntry = addAudiencesEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newAudiencesEntry.getPrimaryKey()));
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

		AudiencesEntry newAudiencesEntry = addAudiencesEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AudiencesEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"audiencesEntryId", newAudiencesEntry.getAudiencesEntryId()));

		List<AudiencesEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(AudiencesEntry audiencesEntry) {
		Assert.assertEquals(
			audiencesEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				audiencesEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(audiencesEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				audiencesEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected AudiencesEntry addAudiencesEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AudiencesEntry audiencesEntry = _persistence.create(pk);

		audiencesEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		audiencesEntry.setCompanyId(RandomTestUtil.nextLong());

		audiencesEntry.setUserId(RandomTestUtil.nextLong());

		audiencesEntry.setUserName(RandomTestUtil.randomString());

		audiencesEntry.setCreateDate(RandomTestUtil.nextDate());

		audiencesEntry.setModifiedDate(RandomTestUtil.nextDate());

		audiencesEntry.setJSON(RandomTestUtil.randomString());

		audiencesEntry.setName(RandomTestUtil.randomString());

		_audiencesEntries.add(_persistence.update(audiencesEntry));

		return audiencesEntry;
	}

	private List<AudiencesEntry> _audiencesEntries =
		new ArrayList<AudiencesEntry>();
	private AudiencesEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1331802761