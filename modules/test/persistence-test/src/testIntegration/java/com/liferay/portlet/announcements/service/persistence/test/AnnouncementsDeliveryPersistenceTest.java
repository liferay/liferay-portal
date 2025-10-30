/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.announcements.service.persistence.test;

import com.liferay.announcements.kernel.exception.NoSuchDeliveryException;
import com.liferay.announcements.kernel.model.AnnouncementsDelivery;
import com.liferay.announcements.kernel.service.persistence.AnnouncementsDeliveryPersistence;
import com.liferay.announcements.kernel.service.persistence.AnnouncementsDeliveryUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class AnnouncementsDeliveryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = AnnouncementsDeliveryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AnnouncementsDelivery> iterator =
			_announcementsDeliveries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AnnouncementsDelivery announcementsDelivery = _persistence.create(pk);

		Assert.assertNotNull(announcementsDelivery);

		Assert.assertEquals(announcementsDelivery.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AnnouncementsDelivery newAnnouncementsDelivery =
			addAnnouncementsDelivery();

		_persistence.remove(newAnnouncementsDelivery);

		AnnouncementsDelivery existingAnnouncementsDelivery =
			_persistence.fetchByPrimaryKey(
				newAnnouncementsDelivery.getPrimaryKey());

		Assert.assertNull(existingAnnouncementsDelivery);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAnnouncementsDelivery();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AnnouncementsDelivery newAnnouncementsDelivery = _persistence.create(
			pk);

		newAnnouncementsDelivery.setMvccVersion(RandomTestUtil.nextLong());

		newAnnouncementsDelivery.setCtCollectionId(RandomTestUtil.nextLong());

		newAnnouncementsDelivery.setCompanyId(RandomTestUtil.nextLong());

		newAnnouncementsDelivery.setUserId(RandomTestUtil.nextLong());

		newAnnouncementsDelivery.setType(RandomTestUtil.randomString());

		newAnnouncementsDelivery.setEmail(RandomTestUtil.randomBoolean());

		newAnnouncementsDelivery.setSms(RandomTestUtil.randomBoolean());

		newAnnouncementsDelivery.setWebsite(RandomTestUtil.randomBoolean());

		_announcementsDeliveries.add(
			_persistence.update(newAnnouncementsDelivery));

		AnnouncementsDelivery existingAnnouncementsDelivery =
			_persistence.findByPrimaryKey(
				newAnnouncementsDelivery.getPrimaryKey());

		Assert.assertEquals(
			existingAnnouncementsDelivery.getMvccVersion(),
			newAnnouncementsDelivery.getMvccVersion());
		Assert.assertEquals(
			existingAnnouncementsDelivery.getCtCollectionId(),
			newAnnouncementsDelivery.getCtCollectionId());
		Assert.assertEquals(
			existingAnnouncementsDelivery.getDeliveryId(),
			newAnnouncementsDelivery.getDeliveryId());
		Assert.assertEquals(
			existingAnnouncementsDelivery.getCompanyId(),
			newAnnouncementsDelivery.getCompanyId());
		Assert.assertEquals(
			existingAnnouncementsDelivery.getUserId(),
			newAnnouncementsDelivery.getUserId());
		Assert.assertEquals(
			existingAnnouncementsDelivery.getType(),
			newAnnouncementsDelivery.getType());
		Assert.assertEquals(
			existingAnnouncementsDelivery.isEmail(),
			newAnnouncementsDelivery.isEmail());
		Assert.assertEquals(
			existingAnnouncementsDelivery.isSms(),
			newAnnouncementsDelivery.isSms());
		Assert.assertEquals(
			existingAnnouncementsDelivery.isWebsite(),
			newAnnouncementsDelivery.isWebsite());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByU_T() throws Exception {
		_persistence.countByU_T(RandomTestUtil.nextLong(), "");

		_persistence.countByU_T(0L, "null");

		_persistence.countByU_T(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AnnouncementsDelivery newAnnouncementsDelivery =
			addAnnouncementsDelivery();

		AnnouncementsDelivery existingAnnouncementsDelivery =
			_persistence.findByPrimaryKey(
				newAnnouncementsDelivery.getPrimaryKey());

		Assert.assertEquals(
			existingAnnouncementsDelivery, newAnnouncementsDelivery);
	}

	@Test(expected = NoSuchDeliveryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AnnouncementsDelivery> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AnnouncementsDelivery", "mvccVersion", true, "ctCollectionId",
			true, "deliveryId", true, "companyId", true, "userId", true, "type",
			true, "email", true, "sms", true, "website", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AnnouncementsDelivery newAnnouncementsDelivery =
			addAnnouncementsDelivery();

		AnnouncementsDelivery existingAnnouncementsDelivery =
			_persistence.fetchByPrimaryKey(
				newAnnouncementsDelivery.getPrimaryKey());

		Assert.assertEquals(
			existingAnnouncementsDelivery, newAnnouncementsDelivery);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AnnouncementsDelivery missingAnnouncementsDelivery =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAnnouncementsDelivery);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AnnouncementsDelivery newAnnouncementsDelivery1 =
			addAnnouncementsDelivery();
		AnnouncementsDelivery newAnnouncementsDelivery2 =
			addAnnouncementsDelivery();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAnnouncementsDelivery1.getPrimaryKey());
		primaryKeys.add(newAnnouncementsDelivery2.getPrimaryKey());

		Map<Serializable, AnnouncementsDelivery> announcementsDeliveries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, announcementsDeliveries.size());
		Assert.assertEquals(
			newAnnouncementsDelivery1,
			announcementsDeliveries.get(
				newAnnouncementsDelivery1.getPrimaryKey()));
		Assert.assertEquals(
			newAnnouncementsDelivery2,
			announcementsDeliveries.get(
				newAnnouncementsDelivery2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AnnouncementsDelivery> announcementsDeliveries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(announcementsDeliveries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AnnouncementsDelivery newAnnouncementsDelivery =
			addAnnouncementsDelivery();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAnnouncementsDelivery.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AnnouncementsDelivery> announcementsDeliveries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, announcementsDeliveries.size());
		Assert.assertEquals(
			newAnnouncementsDelivery,
			announcementsDeliveries.get(
				newAnnouncementsDelivery.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AnnouncementsDelivery> announcementsDeliveries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(announcementsDeliveries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AnnouncementsDelivery newAnnouncementsDelivery =
			addAnnouncementsDelivery();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAnnouncementsDelivery.getPrimaryKey());

		Map<Serializable, AnnouncementsDelivery> announcementsDeliveries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, announcementsDeliveries.size());
		Assert.assertEquals(
			newAnnouncementsDelivery,
			announcementsDeliveries.get(
				newAnnouncementsDelivery.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AnnouncementsDelivery newAnnouncementsDelivery =
			addAnnouncementsDelivery();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newAnnouncementsDelivery.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		AnnouncementsDelivery announcementsDelivery) {

		Assert.assertEquals(
			Long.valueOf(announcementsDelivery.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				announcementsDelivery, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			announcementsDelivery.getType(),
			ReflectionTestUtil.invoke(
				announcementsDelivery, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "type_"));
	}

	protected AnnouncementsDelivery addAnnouncementsDelivery()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		AnnouncementsDelivery announcementsDelivery = _persistence.create(pk);

		announcementsDelivery.setMvccVersion(RandomTestUtil.nextLong());

		announcementsDelivery.setCtCollectionId(RandomTestUtil.nextLong());

		announcementsDelivery.setCompanyId(RandomTestUtil.nextLong());

		announcementsDelivery.setUserId(RandomTestUtil.nextLong());

		announcementsDelivery.setType(RandomTestUtil.randomString());

		announcementsDelivery.setEmail(RandomTestUtil.randomBoolean());

		announcementsDelivery.setSms(RandomTestUtil.randomBoolean());

		announcementsDelivery.setWebsite(RandomTestUtil.randomBoolean());

		_announcementsDeliveries.add(
			_persistence.update(announcementsDelivery));

		return announcementsDelivery;
	}

	private List<AnnouncementsDelivery> _announcementsDeliveries =
		new ArrayList<AnnouncementsDelivery>();
	private AnnouncementsDeliveryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}