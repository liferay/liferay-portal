/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchRegionException;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.service.persistence.RegionPersistence;
import com.liferay.portal.kernel.service.persistence.RegionUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class RegionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = RegionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Region> iterator = _regions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Region region = _persistence.create(pk);

		Assert.assertNotNull(region);

		Assert.assertEquals(region.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Region newRegion = addRegion();

		_persistence.remove(newRegion);

		Region existingRegion = _persistence.fetchByPrimaryKey(
			newRegion.getPrimaryKey());

		Assert.assertNull(existingRegion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addRegion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Region newRegion = _persistence.create(pk);

		newRegion.setMvccVersion(RandomTestUtil.nextLong());

		newRegion.setCtCollectionId(RandomTestUtil.nextLong());

		newRegion.setUuid(RandomTestUtil.randomString());

		newRegion.setDefaultLanguageId(RandomTestUtil.randomString());

		newRegion.setCompanyId(RandomTestUtil.nextLong());

		newRegion.setUserId(RandomTestUtil.nextLong());

		newRegion.setUserName(RandomTestUtil.randomString());

		newRegion.setCreateDate(RandomTestUtil.nextDate());

		newRegion.setModifiedDate(RandomTestUtil.nextDate());

		newRegion.setCountryId(RandomTestUtil.nextLong());

		newRegion.setActive(RandomTestUtil.randomBoolean());

		newRegion.setName(RandomTestUtil.randomString());

		newRegion.setPosition(RandomTestUtil.nextDouble());

		newRegion.setRegionCode(RandomTestUtil.randomString());

		newRegion.setLastPublishDate(RandomTestUtil.nextDate());

		_regions.add(_persistence.update(newRegion));

		Region existingRegion = _persistence.findByPrimaryKey(
			newRegion.getPrimaryKey());

		Assert.assertEquals(
			existingRegion.getMvccVersion(), newRegion.getMvccVersion());
		Assert.assertEquals(
			existingRegion.getCtCollectionId(), newRegion.getCtCollectionId());
		Assert.assertEquals(existingRegion.getUuid(), newRegion.getUuid());
		Assert.assertEquals(
			existingRegion.getDefaultLanguageId(),
			newRegion.getDefaultLanguageId());
		Assert.assertEquals(
			existingRegion.getRegionId(), newRegion.getRegionId());
		Assert.assertEquals(
			existingRegion.getCompanyId(), newRegion.getCompanyId());
		Assert.assertEquals(existingRegion.getUserId(), newRegion.getUserId());
		Assert.assertEquals(
			existingRegion.getUserName(), newRegion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingRegion.getCreateDate()),
			Time.getShortTimestamp(newRegion.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingRegion.getModifiedDate()),
			Time.getShortTimestamp(newRegion.getModifiedDate()));
		Assert.assertEquals(
			existingRegion.getCountryId(), newRegion.getCountryId());
		Assert.assertEquals(existingRegion.isActive(), newRegion.isActive());
		Assert.assertEquals(existingRegion.getName(), newRegion.getName());
		AssertUtils.assertEquals(
			existingRegion.getPosition(), newRegion.getPosition());
		Assert.assertEquals(
			existingRegion.getRegionCode(), newRegion.getRegionCode());
		Assert.assertEquals(
			Time.getShortTimestamp(existingRegion.getLastPublishDate()),
			Time.getShortTimestamp(newRegion.getLastPublishDate()));
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
	public void testCountByCountryId() throws Exception {
		_persistence.countByCountryId(RandomTestUtil.nextLong());

		_persistence.countByCountryId(0L);
	}

	@Test
	public void testCountByActive() throws Exception {
		_persistence.countByActive(RandomTestUtil.randomBoolean());

		_persistence.countByActive(RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_A() throws Exception {
		_persistence.countByC_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_R() throws Exception {
		_persistence.countByC_R(RandomTestUtil.nextLong(), "");

		_persistence.countByC_R(0L, "null");

		_persistence.countByC_R(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Region newRegion = addRegion();

		Region existingRegion = _persistence.findByPrimaryKey(
			newRegion.getPrimaryKey());

		Assert.assertEquals(existingRegion, newRegion);
	}

	@Test(expected = NoSuchRegionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Region> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Region", "mvccVersion", true, "ctCollectionId", true, "uuid", true,
			"defaultLanguageId", true, "regionId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "countryId", true, "active", true, "name",
			true, "position", true, "regionCode", true, "lastPublishDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Region newRegion = addRegion();

		Region existingRegion = _persistence.fetchByPrimaryKey(
			newRegion.getPrimaryKey());

		Assert.assertEquals(existingRegion, newRegion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Region missingRegion = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingRegion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Region newRegion1 = addRegion();
		Region newRegion2 = addRegion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRegion1.getPrimaryKey());
		primaryKeys.add(newRegion2.getPrimaryKey());

		Map<Serializable, Region> regions = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, regions.size());
		Assert.assertEquals(
			newRegion1, regions.get(newRegion1.getPrimaryKey()));
		Assert.assertEquals(
			newRegion2, regions.get(newRegion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Region> regions = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(regions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Region newRegion = addRegion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRegion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Region> regions = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, regions.size());
		Assert.assertEquals(newRegion, regions.get(newRegion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Region> regions = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(regions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Region newRegion = addRegion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRegion.getPrimaryKey());

		Map<Serializable, Region> regions = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, regions.size());
		Assert.assertEquals(newRegion, regions.get(newRegion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Region newRegion = addRegion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newRegion.getPrimaryKey()));
	}

	private void _assertOriginalValues(Region region) {
		Assert.assertEquals(
			Long.valueOf(region.getCountryId()),
			ReflectionTestUtil.<Long>invoke(
				region, "getColumnOriginalValue", new Class<?>[] {String.class},
				"countryId"));
		Assert.assertEquals(
			region.getRegionCode(),
			ReflectionTestUtil.invoke(
				region, "getColumnOriginalValue", new Class<?>[] {String.class},
				"regionCode"));
	}

	protected Region addRegion() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Region region = _persistence.create(pk);

		region.setMvccVersion(RandomTestUtil.nextLong());

		region.setCtCollectionId(RandomTestUtil.nextLong());

		region.setUuid(RandomTestUtil.randomString());

		region.setDefaultLanguageId(RandomTestUtil.randomString());

		region.setCompanyId(RandomTestUtil.nextLong());

		region.setUserId(RandomTestUtil.nextLong());

		region.setUserName(RandomTestUtil.randomString());

		region.setCreateDate(RandomTestUtil.nextDate());

		region.setModifiedDate(RandomTestUtil.nextDate());

		region.setCountryId(RandomTestUtil.nextLong());

		region.setActive(RandomTestUtil.randomBoolean());

		region.setName(RandomTestUtil.randomString());

		region.setPosition(RandomTestUtil.nextDouble());

		region.setRegionCode(RandomTestUtil.randomString());

		region.setLastPublishDate(RandomTestUtil.nextDate());

		_regions.add(_persistence.update(region));

		return region;
	}

	private List<Region> _regions = new ArrayList<Region>();
	private RegionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}