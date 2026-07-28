/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.shop.by.diagram.exception.NoSuchCSDiagramPinException;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramPin;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramPinLocalServiceUtil;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramPinPersistence;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramPinUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class CSDiagramPinPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.shop.by.diagram.service"));

	@Before
	public void setUp() {
		_persistence = CSDiagramPinUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CSDiagramPin> iterator = _csDiagramPins.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CSDiagramPin csDiagramPin = _persistence.create(pk);

		Assert.assertNotNull(csDiagramPin);

		Assert.assertEquals(csDiagramPin.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CSDiagramPin newCSDiagramPin = addCSDiagramPin();

		_persistence.remove(newCSDiagramPin);

		CSDiagramPin existingCSDiagramPin = _persistence.fetchByPrimaryKey(
			newCSDiagramPin.getPrimaryKey());

		Assert.assertNull(existingCSDiagramPin);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCSDiagramPin();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CSDiagramPin newCSDiagramPin = addCSDiagramPin();

		newCSDiagramPin.setCtCollectionId(RandomTestUtil.nextLong());

		newCSDiagramPin.setCompanyId(RandomTestUtil.nextLong());

		newCSDiagramPin.setUserId(RandomTestUtil.nextLong());

		newCSDiagramPin.setUserName(RandomTestUtil.randomString());

		newCSDiagramPin.setCreateDate(RandomTestUtil.nextDate());

		newCSDiagramPin.setModifiedDate(RandomTestUtil.nextDate());

		newCSDiagramPin.setCPDefinitionId(RandomTestUtil.nextLong());

		newCSDiagramPin.setPositionX(RandomTestUtil.nextDouble());

		newCSDiagramPin.setPositionY(RandomTestUtil.nextDouble());

		newCSDiagramPin.setSequence(RandomTestUtil.randomString());

		newCSDiagramPin = _persistence.update(newCSDiagramPin);

		_csDiagramPins.add(newCSDiagramPin);

		CSDiagramPin existingCSDiagramPin = _persistence.findByPrimaryKey(
			newCSDiagramPin.getPrimaryKey());

		Assert.assertEquals(
			existingCSDiagramPin.getMvccVersion(),
			newCSDiagramPin.getMvccVersion());
		Assert.assertEquals(
			existingCSDiagramPin.getCtCollectionId(),
			newCSDiagramPin.getCtCollectionId());
		Assert.assertEquals(
			existingCSDiagramPin.getCSDiagramPinId(),
			newCSDiagramPin.getCSDiagramPinId());
		Assert.assertEquals(
			existingCSDiagramPin.getCompanyId(),
			newCSDiagramPin.getCompanyId());
		Assert.assertEquals(
			existingCSDiagramPin.getUserId(), newCSDiagramPin.getUserId());
		Assert.assertEquals(
			existingCSDiagramPin.getUserName(), newCSDiagramPin.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCSDiagramPin.getCreateDate()),
			Time.getShortTimestamp(newCSDiagramPin.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCSDiagramPin.getModifiedDate()),
			Time.getShortTimestamp(newCSDiagramPin.getModifiedDate()));
		Assert.assertEquals(
			existingCSDiagramPin.getCPDefinitionId(),
			newCSDiagramPin.getCPDefinitionId());
		AssertUtils.assertEquals(
			existingCSDiagramPin.getPositionX(),
			newCSDiagramPin.getPositionX());
		AssertUtils.assertEquals(
			existingCSDiagramPin.getPositionY(),
			newCSDiagramPin.getPositionY());
		Assert.assertEquals(
			existingCSDiagramPin.getSequence(), newCSDiagramPin.getSequence());
	}

	@Test
	public void testCountByCPDefinitionId() throws Exception {
		_persistence.countByCPDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByCPDefinitionId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CSDiagramPin newCSDiagramPin = addCSDiagramPin();

		CSDiagramPin existingCSDiagramPin = _persistence.findByPrimaryKey(
			newCSDiagramPin.getPrimaryKey());

		Assert.assertEquals(existingCSDiagramPin, newCSDiagramPin);
	}

	@Test(expected = NoSuchCSDiagramPinException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CSDiagramPin> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CSDiagramPin", "mvccVersion", true, "ctCollectionId", true,
			"CSDiagramPinId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"CPDefinitionId", true, "positionX", true, "positionY", true,
			"sequence", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CSDiagramPin newCSDiagramPin = addCSDiagramPin();

		CSDiagramPin existingCSDiagramPin = _persistence.fetchByPrimaryKey(
			newCSDiagramPin.getPrimaryKey());

		Assert.assertEquals(existingCSDiagramPin, newCSDiagramPin);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CSDiagramPin missingCSDiagramPin = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCSDiagramPin);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CSDiagramPin newCSDiagramPin1 = addCSDiagramPin();
		CSDiagramPin newCSDiagramPin2 = addCSDiagramPin();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCSDiagramPin1.getPrimaryKey());
		primaryKeys.add(newCSDiagramPin2.getPrimaryKey());

		Map<Serializable, CSDiagramPin> csDiagramPins =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, csDiagramPins.size());
		Assert.assertEquals(
			newCSDiagramPin1,
			csDiagramPins.get(newCSDiagramPin1.getPrimaryKey()));
		Assert.assertEquals(
			newCSDiagramPin2,
			csDiagramPins.get(newCSDiagramPin2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CSDiagramPin> csDiagramPins =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(csDiagramPins.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CSDiagramPin newCSDiagramPin = addCSDiagramPin();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCSDiagramPin.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CSDiagramPin> csDiagramPins =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, csDiagramPins.size());
		Assert.assertEquals(
			newCSDiagramPin,
			csDiagramPins.get(newCSDiagramPin.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CSDiagramPin> csDiagramPins =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(csDiagramPins.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CSDiagramPin newCSDiagramPin = addCSDiagramPin();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCSDiagramPin.getPrimaryKey());

		Map<Serializable, CSDiagramPin> csDiagramPins =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, csDiagramPins.size());
		Assert.assertEquals(
			newCSDiagramPin,
			csDiagramPins.get(newCSDiagramPin.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CSDiagramPinLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<CSDiagramPin>() {

				@Override
				public void performAction(CSDiagramPin csDiagramPin) {
					Assert.assertNotNull(csDiagramPin);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CSDiagramPin newCSDiagramPin = addCSDiagramPin();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CSDiagramPin.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CSDiagramPinId", newCSDiagramPin.getCSDiagramPinId()));

		List<CSDiagramPin> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CSDiagramPin existingCSDiagramPin = result.get(0);

		Assert.assertEquals(existingCSDiagramPin, newCSDiagramPin);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CSDiagramPin.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CSDiagramPinId", RandomTestUtil.nextLong()));

		List<CSDiagramPin> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CSDiagramPin newCSDiagramPin = addCSDiagramPin();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CSDiagramPin.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("CSDiagramPinId"));

		Object newCSDiagramPinId = newCSDiagramPin.getCSDiagramPinId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CSDiagramPinId", new Object[] {newCSDiagramPinId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCSDiagramPinId = result.get(0);

		Assert.assertEquals(existingCSDiagramPinId, newCSDiagramPinId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CSDiagramPin.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("CSDiagramPinId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CSDiagramPinId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected CSDiagramPin addCSDiagramPin() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CSDiagramPin csDiagramPin = _persistence.create(pk);

		csDiagramPin.setCtCollectionId(RandomTestUtil.nextLong());

		csDiagramPin.setCompanyId(RandomTestUtil.nextLong());

		csDiagramPin.setUserId(RandomTestUtil.nextLong());

		csDiagramPin.setUserName(RandomTestUtil.randomString());

		csDiagramPin.setCreateDate(RandomTestUtil.nextDate());

		csDiagramPin.setModifiedDate(RandomTestUtil.nextDate());

		csDiagramPin.setCPDefinitionId(RandomTestUtil.nextLong());

		csDiagramPin.setPositionX(RandomTestUtil.nextDouble());

		csDiagramPin.setPositionY(RandomTestUtil.nextDouble());

		csDiagramPin.setSequence(RandomTestUtil.randomString());

		_csDiagramPins.add(_persistence.update(csDiagramPin));

		return csDiagramPin;
	}

	private List<CSDiagramPin> _csDiagramPins = new ArrayList<CSDiagramPin>();
	private CSDiagramPinPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-145339814