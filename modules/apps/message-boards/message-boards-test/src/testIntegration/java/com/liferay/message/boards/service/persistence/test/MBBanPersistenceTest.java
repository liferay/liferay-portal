/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.exception.NoSuchBanException;
import com.liferay.message.boards.model.MBBan;
import com.liferay.message.boards.service.MBBanLocalServiceUtil;
import com.liferay.message.boards.service.persistence.MBBanPersistence;
import com.liferay.message.boards.service.persistence.MBBanUtil;
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
public class MBBanPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.message.boards.service"));

	@Before
	public void setUp() {
		_persistence = MBBanUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<MBBan> iterator = _mbBans.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBBan mbBan = _persistence.create(pk);

		Assert.assertNotNull(mbBan);

		Assert.assertEquals(mbBan.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		MBBan newMBBan = addMBBan();

		_persistence.remove(newMBBan);

		MBBan existingMBBan = _persistence.fetchByPrimaryKey(
			newMBBan.getPrimaryKey());

		Assert.assertNull(existingMBBan);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addMBBan();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		MBBan newMBBan = addMBBan();

		newMBBan.setCtCollectionId(RandomTestUtil.nextLong());

		newMBBan.setUuid(RandomTestUtil.randomString());

		newMBBan.setGroupId(RandomTestUtil.nextLong());

		newMBBan.setCompanyId(RandomTestUtil.nextLong());

		newMBBan.setUserId(RandomTestUtil.nextLong());

		newMBBan.setUserName(RandomTestUtil.randomString());

		newMBBan.setCreateDate(RandomTestUtil.nextDate());

		newMBBan.setModifiedDate(RandomTestUtil.nextDate());

		newMBBan.setBanUserId(RandomTestUtil.nextLong());

		newMBBan.setLastPublishDate(RandomTestUtil.nextDate());

		newMBBan = _persistence.update(newMBBan);

		_mbBans.add(newMBBan);

		MBBan existingMBBan = _persistence.findByPrimaryKey(
			newMBBan.getPrimaryKey());

		Assert.assertEquals(
			existingMBBan.getMvccVersion(), newMBBan.getMvccVersion());
		Assert.assertEquals(
			existingMBBan.getCtCollectionId(), newMBBan.getCtCollectionId());
		Assert.assertEquals(existingMBBan.getUuid(), newMBBan.getUuid());
		Assert.assertEquals(existingMBBan.getBanId(), newMBBan.getBanId());
		Assert.assertEquals(existingMBBan.getGroupId(), newMBBan.getGroupId());
		Assert.assertEquals(
			existingMBBan.getCompanyId(), newMBBan.getCompanyId());
		Assert.assertEquals(existingMBBan.getUserId(), newMBBan.getUserId());
		Assert.assertEquals(
			existingMBBan.getUserName(), newMBBan.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBBan.getCreateDate()),
			Time.getShortTimestamp(newMBBan.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBBan.getModifiedDate()),
			Time.getShortTimestamp(newMBBan.getModifiedDate()));
		Assert.assertEquals(
			existingMBBan.getBanUserId(), newMBBan.getBanUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBBan.getLastPublishDate()),
			Time.getShortTimestamp(newMBBan.getLastPublishDate()));
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
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByBanUserId() throws Exception {
		_persistence.countByBanUserId(RandomTestUtil.nextLong());

		_persistence.countByBanUserId(0L);
	}

	@Test
	public void testCountByG_B() throws Exception {
		_persistence.countByG_B(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_B(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		MBBan newMBBan = addMBBan();

		MBBan existingMBBan = _persistence.findByPrimaryKey(
			newMBBan.getPrimaryKey());

		Assert.assertEquals(existingMBBan, newMBBan);
	}

	@Test(expected = NoSuchBanException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<MBBan> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"MBBan", "mvccVersion", true, "ctCollectionId", true, "uuid", true,
			"banId", true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"banUserId", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		MBBan newMBBan = addMBBan();

		MBBan existingMBBan = _persistence.fetchByPrimaryKey(
			newMBBan.getPrimaryKey());

		Assert.assertEquals(existingMBBan, newMBBan);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBBan missingMBBan = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingMBBan);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		MBBan newMBBan1 = addMBBan();
		MBBan newMBBan2 = addMBBan();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBBan1.getPrimaryKey());
		primaryKeys.add(newMBBan2.getPrimaryKey());

		Map<Serializable, MBBan> mbBans = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, mbBans.size());
		Assert.assertEquals(newMBBan1, mbBans.get(newMBBan1.getPrimaryKey()));
		Assert.assertEquals(newMBBan2, mbBans.get(newMBBan2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, MBBan> mbBans = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(mbBans.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		MBBan newMBBan = addMBBan();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBBan.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, MBBan> mbBans = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, mbBans.size());
		Assert.assertEquals(newMBBan, mbBans.get(newMBBan.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, MBBan> mbBans = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(mbBans.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		MBBan newMBBan = addMBBan();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBBan.getPrimaryKey());

		Map<Serializable, MBBan> mbBans = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, mbBans.size());
		Assert.assertEquals(newMBBan, mbBans.get(newMBBan.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			MBBanLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<MBBan>() {

				@Override
				public void performAction(MBBan mbBan) {
					Assert.assertNotNull(mbBan);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		MBBan newMBBan = addMBBan();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			MBBan.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("banId", newMBBan.getBanId()));

		List<MBBan> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		MBBan existingMBBan = result.get(0);

		Assert.assertEquals(existingMBBan, newMBBan);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			MBBan.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("banId", RandomTestUtil.nextLong()));

		List<MBBan> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		MBBan newMBBan = addMBBan();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			MBBan.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("banId"));

		Object newBanId = newMBBan.getBanId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in("banId", new Object[] {newBanId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingBanId = result.get(0);

		Assert.assertEquals(existingBanId, newBanId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			MBBan.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("banId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"banId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		MBBan newMBBan = addMBBan();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newMBBan.getPrimaryKey()));
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

		MBBan newMBBan = addMBBan();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			MBBan.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("banId", newMBBan.getBanId()));

		List<MBBan> result = _persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(MBBan mbBan) {
		Assert.assertEquals(
			mbBan.getUuid(),
			ReflectionTestUtil.invoke(
				mbBan, "getColumnOriginalValue", new Class<?>[] {String.class},
				"uuid_"));
		Assert.assertEquals(
			Long.valueOf(mbBan.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				mbBan, "getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));

		Assert.assertEquals(
			Long.valueOf(mbBan.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				mbBan, "getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));
		Assert.assertEquals(
			Long.valueOf(mbBan.getBanUserId()),
			ReflectionTestUtil.<Long>invoke(
				mbBan, "getColumnOriginalValue", new Class<?>[] {String.class},
				"banUserId"));
	}

	protected MBBan addMBBan() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBBan mbBan = _persistence.create(pk);

		mbBan.setCtCollectionId(RandomTestUtil.nextLong());

		mbBan.setUuid(RandomTestUtil.randomString());

		mbBan.setGroupId(RandomTestUtil.nextLong());

		mbBan.setCompanyId(RandomTestUtil.nextLong());

		mbBan.setUserId(RandomTestUtil.nextLong());

		mbBan.setUserName(RandomTestUtil.randomString());

		mbBan.setCreateDate(RandomTestUtil.nextDate());

		mbBan.setModifiedDate(RandomTestUtil.nextDate());

		mbBan.setBanUserId(RandomTestUtil.nextLong());

		mbBan.setLastPublishDate(RandomTestUtil.nextDate());

		_mbBans.add(_persistence.update(mbBan));

		return mbBan;
	}

	private List<MBBan> _mbBans = new ArrayList<MBBan>();
	private MBBanPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1815716769