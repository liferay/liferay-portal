/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.NoSuchCPDAvailabilityEstimateException;
import com.liferay.commerce.model.CPDAvailabilityEstimate;
import com.liferay.commerce.service.persistence.CPDAvailabilityEstimatePersistence;
import com.liferay.commerce.service.persistence.CPDAvailabilityEstimateUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class CPDAvailabilityEstimatePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.service"));

	@Before
	public void setUp() {
		_persistence = CPDAvailabilityEstimateUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPDAvailabilityEstimate> iterator =
			_cpdAvailabilityEstimates.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDAvailabilityEstimate cpdAvailabilityEstimate = _persistence.create(
			pk);

		Assert.assertNotNull(cpdAvailabilityEstimate);

		Assert.assertEquals(cpdAvailabilityEstimate.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPDAvailabilityEstimate newCPDAvailabilityEstimate =
			addCPDAvailabilityEstimate();

		_persistence.remove(newCPDAvailabilityEstimate);

		CPDAvailabilityEstimate existingCPDAvailabilityEstimate =
			_persistence.fetchByPrimaryKey(
				newCPDAvailabilityEstimate.getPrimaryKey());

		Assert.assertNull(existingCPDAvailabilityEstimate);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPDAvailabilityEstimate();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDAvailabilityEstimate newCPDAvailabilityEstimate =
			_persistence.create(pk);

		newCPDAvailabilityEstimate.setMvccVersion(RandomTestUtil.nextLong());

		newCPDAvailabilityEstimate.setUuid(RandomTestUtil.randomString());

		newCPDAvailabilityEstimate.setCompanyId(RandomTestUtil.nextLong());

		newCPDAvailabilityEstimate.setUserId(RandomTestUtil.nextLong());

		newCPDAvailabilityEstimate.setUserName(RandomTestUtil.randomString());

		newCPDAvailabilityEstimate.setCreateDate(RandomTestUtil.nextDate());

		newCPDAvailabilityEstimate.setModifiedDate(RandomTestUtil.nextDate());

		newCPDAvailabilityEstimate.setCommerceAvailabilityEstimateId(
			RandomTestUtil.nextLong());

		newCPDAvailabilityEstimate.setCProductId(RandomTestUtil.nextLong());

		newCPDAvailabilityEstimate.setLastPublishDate(
			RandomTestUtil.nextDate());

		_cpdAvailabilityEstimates.add(
			_persistence.update(newCPDAvailabilityEstimate));

		CPDAvailabilityEstimate existingCPDAvailabilityEstimate =
			_persistence.findByPrimaryKey(
				newCPDAvailabilityEstimate.getPrimaryKey());

		Assert.assertEquals(
			existingCPDAvailabilityEstimate.getMvccVersion(),
			newCPDAvailabilityEstimate.getMvccVersion());
		Assert.assertEquals(
			existingCPDAvailabilityEstimate.getUuid(),
			newCPDAvailabilityEstimate.getUuid());
		Assert.assertEquals(
			existingCPDAvailabilityEstimate.getCPDAvailabilityEstimateId(),
			newCPDAvailabilityEstimate.getCPDAvailabilityEstimateId());
		Assert.assertEquals(
			existingCPDAvailabilityEstimate.getCompanyId(),
			newCPDAvailabilityEstimate.getCompanyId());
		Assert.assertEquals(
			existingCPDAvailabilityEstimate.getUserId(),
			newCPDAvailabilityEstimate.getUserId());
		Assert.assertEquals(
			existingCPDAvailabilityEstimate.getUserName(),
			newCPDAvailabilityEstimate.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDAvailabilityEstimate.getCreateDate()),
			Time.getShortTimestamp(newCPDAvailabilityEstimate.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDAvailabilityEstimate.getModifiedDate()),
			Time.getShortTimestamp(
				newCPDAvailabilityEstimate.getModifiedDate()));
		Assert.assertEquals(
			existingCPDAvailabilityEstimate.getCommerceAvailabilityEstimateId(),
			newCPDAvailabilityEstimate.getCommerceAvailabilityEstimateId());
		Assert.assertEquals(
			existingCPDAvailabilityEstimate.getCProductId(),
			newCPDAvailabilityEstimate.getCProductId());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDAvailabilityEstimate.getLastPublishDate()),
			Time.getShortTimestamp(
				newCPDAvailabilityEstimate.getLastPublishDate()));
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
	public void testCountByCommerceAvailabilityEstimateId() throws Exception {
		_persistence.countByCommerceAvailabilityEstimateId(
			RandomTestUtil.nextLong());

		_persistence.countByCommerceAvailabilityEstimateId(0L);
	}

	@Test
	public void testCountByCProductId() throws Exception {
		_persistence.countByCProductId(RandomTestUtil.nextLong());

		_persistence.countByCProductId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPDAvailabilityEstimate newCPDAvailabilityEstimate =
			addCPDAvailabilityEstimate();

		CPDAvailabilityEstimate existingCPDAvailabilityEstimate =
			_persistence.findByPrimaryKey(
				newCPDAvailabilityEstimate.getPrimaryKey());

		Assert.assertEquals(
			existingCPDAvailabilityEstimate, newCPDAvailabilityEstimate);
	}

	@Test(expected = NoSuchCPDAvailabilityEstimateException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPDAvailabilityEstimate>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CPDAvailabilityEstimate", "mvccVersion", true, "uuid", true,
			"CPDAvailabilityEstimateId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"commerceAvailabilityEstimateId", true, "CProductId", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPDAvailabilityEstimate newCPDAvailabilityEstimate =
			addCPDAvailabilityEstimate();

		CPDAvailabilityEstimate existingCPDAvailabilityEstimate =
			_persistence.fetchByPrimaryKey(
				newCPDAvailabilityEstimate.getPrimaryKey());

		Assert.assertEquals(
			existingCPDAvailabilityEstimate, newCPDAvailabilityEstimate);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDAvailabilityEstimate missingCPDAvailabilityEstimate =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPDAvailabilityEstimate);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPDAvailabilityEstimate newCPDAvailabilityEstimate1 =
			addCPDAvailabilityEstimate();
		CPDAvailabilityEstimate newCPDAvailabilityEstimate2 =
			addCPDAvailabilityEstimate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDAvailabilityEstimate1.getPrimaryKey());
		primaryKeys.add(newCPDAvailabilityEstimate2.getPrimaryKey());

		Map<Serializable, CPDAvailabilityEstimate> cpdAvailabilityEstimates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpdAvailabilityEstimates.size());
		Assert.assertEquals(
			newCPDAvailabilityEstimate1,
			cpdAvailabilityEstimates.get(
				newCPDAvailabilityEstimate1.getPrimaryKey()));
		Assert.assertEquals(
			newCPDAvailabilityEstimate2,
			cpdAvailabilityEstimates.get(
				newCPDAvailabilityEstimate2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPDAvailabilityEstimate> cpdAvailabilityEstimates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpdAvailabilityEstimates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPDAvailabilityEstimate newCPDAvailabilityEstimate =
			addCPDAvailabilityEstimate();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDAvailabilityEstimate.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPDAvailabilityEstimate> cpdAvailabilityEstimates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpdAvailabilityEstimates.size());
		Assert.assertEquals(
			newCPDAvailabilityEstimate,
			cpdAvailabilityEstimates.get(
				newCPDAvailabilityEstimate.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPDAvailabilityEstimate> cpdAvailabilityEstimates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpdAvailabilityEstimates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPDAvailabilityEstimate newCPDAvailabilityEstimate =
			addCPDAvailabilityEstimate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDAvailabilityEstimate.getPrimaryKey());

		Map<Serializable, CPDAvailabilityEstimate> cpdAvailabilityEstimates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpdAvailabilityEstimates.size());
		Assert.assertEquals(
			newCPDAvailabilityEstimate,
			cpdAvailabilityEstimates.get(
				newCPDAvailabilityEstimate.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPDAvailabilityEstimate newCPDAvailabilityEstimate =
			addCPDAvailabilityEstimate();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPDAvailabilityEstimate.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CPDAvailabilityEstimate cpdAvailabilityEstimate) {

		Assert.assertEquals(
			Long.valueOf(cpdAvailabilityEstimate.getCProductId()),
			ReflectionTestUtil.<Long>invoke(
				cpdAvailabilityEstimate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CProductId"));
	}

	protected CPDAvailabilityEstimate addCPDAvailabilityEstimate()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPDAvailabilityEstimate cpdAvailabilityEstimate = _persistence.create(
			pk);

		cpdAvailabilityEstimate.setMvccVersion(RandomTestUtil.nextLong());

		cpdAvailabilityEstimate.setUuid(RandomTestUtil.randomString());

		cpdAvailabilityEstimate.setCompanyId(RandomTestUtil.nextLong());

		cpdAvailabilityEstimate.setUserId(RandomTestUtil.nextLong());

		cpdAvailabilityEstimate.setUserName(RandomTestUtil.randomString());

		cpdAvailabilityEstimate.setCreateDate(RandomTestUtil.nextDate());

		cpdAvailabilityEstimate.setModifiedDate(RandomTestUtil.nextDate());

		cpdAvailabilityEstimate.setCommerceAvailabilityEstimateId(
			RandomTestUtil.nextLong());

		cpdAvailabilityEstimate.setCProductId(RandomTestUtil.nextLong());

		cpdAvailabilityEstimate.setLastPublishDate(RandomTestUtil.nextDate());

		_cpdAvailabilityEstimates.add(
			_persistence.update(cpdAvailabilityEstimate));

		return cpdAvailabilityEstimate;
	}

	private List<CPDAvailabilityEstimate> _cpdAvailabilityEstimates =
		new ArrayList<CPDAvailabilityEstimate>();
	private CPDAvailabilityEstimatePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}