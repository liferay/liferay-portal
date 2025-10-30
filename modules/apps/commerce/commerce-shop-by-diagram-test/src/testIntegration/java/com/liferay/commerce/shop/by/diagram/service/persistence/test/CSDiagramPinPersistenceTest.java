/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.shop.by.diagram.exception.NoSuchCSDiagramPinException;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramPin;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramPinPersistence;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramPinUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
		long pk = RandomTestUtil.nextLong();

		CSDiagramPin newCSDiagramPin = _persistence.create(pk);

		newCSDiagramPin.setMvccVersion(RandomTestUtil.nextLong());

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

		_csDiagramPins.add(_persistence.update(newCSDiagramPin));

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

	protected CSDiagramPin addCSDiagramPin() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CSDiagramPin csDiagramPin = _persistence.create(pk);

		csDiagramPin.setMvccVersion(RandomTestUtil.nextLong());

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