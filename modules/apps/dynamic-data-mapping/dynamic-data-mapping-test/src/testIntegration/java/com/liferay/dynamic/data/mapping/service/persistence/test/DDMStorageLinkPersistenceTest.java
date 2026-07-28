/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchStorageLinkException;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStorageLinkPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStorageLinkUtil;
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
public class DDMStorageLinkPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.dynamic.data.mapping.service"));

	@Before
	public void setUp() {
		_persistence = DDMStorageLinkUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMStorageLink> iterator = _ddmStorageLinks.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStorageLink ddmStorageLink = _persistence.create(pk);

		Assert.assertNotNull(ddmStorageLink);

		Assert.assertEquals(ddmStorageLink.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMStorageLink newDDMStorageLink = addDDMStorageLink();

		_persistence.remove(newDDMStorageLink);

		DDMStorageLink existingDDMStorageLink = _persistence.fetchByPrimaryKey(
			newDDMStorageLink.getPrimaryKey());

		Assert.assertNull(existingDDMStorageLink);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMStorageLink();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		DDMStorageLink newDDMStorageLink = addDDMStorageLink();

		newDDMStorageLink.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMStorageLink.setUuid(RandomTestUtil.randomString());

		newDDMStorageLink.setCompanyId(RandomTestUtil.nextLong());

		newDDMStorageLink.setClassNameId(RandomTestUtil.nextLong());

		newDDMStorageLink.setClassPK(RandomTestUtil.nextLong());

		newDDMStorageLink.setStructureId(RandomTestUtil.nextLong());

		newDDMStorageLink.setStructureVersionId(RandomTestUtil.nextLong());

		newDDMStorageLink = _persistence.update(newDDMStorageLink);

		_ddmStorageLinks.add(newDDMStorageLink);

		DDMStorageLink existingDDMStorageLink = _persistence.findByPrimaryKey(
			newDDMStorageLink.getPrimaryKey());

		Assert.assertEquals(
			existingDDMStorageLink.getMvccVersion(),
			newDDMStorageLink.getMvccVersion());
		Assert.assertEquals(
			existingDDMStorageLink.getCtCollectionId(),
			newDDMStorageLink.getCtCollectionId());
		Assert.assertEquals(
			existingDDMStorageLink.getUuid(), newDDMStorageLink.getUuid());
		Assert.assertEquals(
			existingDDMStorageLink.getStorageLinkId(),
			newDDMStorageLink.getStorageLinkId());
		Assert.assertEquals(
			existingDDMStorageLink.getCompanyId(),
			newDDMStorageLink.getCompanyId());
		Assert.assertEquals(
			existingDDMStorageLink.getClassNameId(),
			newDDMStorageLink.getClassNameId());
		Assert.assertEquals(
			existingDDMStorageLink.getClassPK(),
			newDDMStorageLink.getClassPK());
		Assert.assertEquals(
			existingDDMStorageLink.getStructureId(),
			newDDMStorageLink.getStructureId());
		Assert.assertEquals(
			existingDDMStorageLink.getStructureVersionId(),
			newDDMStorageLink.getStructureVersionId());
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
	public void testCountByClassPK() throws Exception {
		_persistence.countByClassPK(RandomTestUtil.nextLong());

		_persistence.countByClassPK(0L);
	}

	@Test
	public void testCountByStructureId() throws Exception {
		_persistence.countByStructureId(RandomTestUtil.nextLong());

		_persistence.countByStructureId(0L);
	}

	@Test
	public void testCountByStructureVersionId() throws Exception {
		_persistence.countByStructureVersionId(RandomTestUtil.nextLong());

		_persistence.countByStructureVersionId(0L);
	}

	@Test
	public void testCountByStructureVersionIdArrayable() throws Exception {
		_persistence.countByStructureVersionId(
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMStorageLink newDDMStorageLink = addDDMStorageLink();

		DDMStorageLink existingDDMStorageLink = _persistence.findByPrimaryKey(
			newDDMStorageLink.getPrimaryKey());

		Assert.assertEquals(existingDDMStorageLink, newDDMStorageLink);
	}

	@Test(expected = NoSuchStorageLinkException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMStorageLink> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDMStorageLink", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "storageLinkId", true, "companyId", true,
			"classNameId", true, "classPK", true, "structureId", true,
			"structureVersionId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMStorageLink newDDMStorageLink = addDDMStorageLink();

		DDMStorageLink existingDDMStorageLink = _persistence.fetchByPrimaryKey(
			newDDMStorageLink.getPrimaryKey());

		Assert.assertEquals(existingDDMStorageLink, newDDMStorageLink);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStorageLink missingDDMStorageLink = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingDDMStorageLink);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMStorageLink newDDMStorageLink1 = addDDMStorageLink();
		DDMStorageLink newDDMStorageLink2 = addDDMStorageLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStorageLink1.getPrimaryKey());
		primaryKeys.add(newDDMStorageLink2.getPrimaryKey());

		Map<Serializable, DDMStorageLink> ddmStorageLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmStorageLinks.size());
		Assert.assertEquals(
			newDDMStorageLink1,
			ddmStorageLinks.get(newDDMStorageLink1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMStorageLink2,
			ddmStorageLinks.get(newDDMStorageLink2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMStorageLink> ddmStorageLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmStorageLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMStorageLink newDDMStorageLink = addDDMStorageLink();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStorageLink.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMStorageLink> ddmStorageLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmStorageLinks.size());
		Assert.assertEquals(
			newDDMStorageLink,
			ddmStorageLinks.get(newDDMStorageLink.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMStorageLink> ddmStorageLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmStorageLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMStorageLink newDDMStorageLink = addDDMStorageLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStorageLink.getPrimaryKey());

		Map<Serializable, DDMStorageLink> ddmStorageLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmStorageLinks.size());
		Assert.assertEquals(
			newDDMStorageLink,
			ddmStorageLinks.get(newDDMStorageLink.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			DDMStorageLinkLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<DDMStorageLink>() {

				@Override
				public void performAction(DDMStorageLink ddmStorageLink) {
					Assert.assertNotNull(ddmStorageLink);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		DDMStorageLink newDDMStorageLink = addDDMStorageLink();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStorageLink.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"storageLinkId", newDDMStorageLink.getStorageLinkId()));

		List<DDMStorageLink> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		DDMStorageLink existingDDMStorageLink = result.get(0);

		Assert.assertEquals(existingDDMStorageLink, newDDMStorageLink);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStorageLink.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"storageLinkId", RandomTestUtil.nextLong()));

		List<DDMStorageLink> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		DDMStorageLink newDDMStorageLink = addDDMStorageLink();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStorageLink.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("storageLinkId"));

		Object newStorageLinkId = newDDMStorageLink.getStorageLinkId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"storageLinkId", new Object[] {newStorageLinkId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingStorageLinkId = result.get(0);

		Assert.assertEquals(existingStorageLinkId, newStorageLinkId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStorageLink.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("storageLinkId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"storageLinkId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMStorageLink newDDMStorageLink = addDDMStorageLink();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDDMStorageLink.getPrimaryKey()));
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

		DDMStorageLink newDDMStorageLink = addDDMStorageLink();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStorageLink.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"storageLinkId", newDDMStorageLink.getStorageLinkId()));

		List<DDMStorageLink> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(DDMStorageLink ddmStorageLink) {
		Assert.assertEquals(
			Long.valueOf(ddmStorageLink.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				ddmStorageLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected DDMStorageLink addDDMStorageLink() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStorageLink ddmStorageLink = _persistence.create(pk);

		ddmStorageLink.setCtCollectionId(RandomTestUtil.nextLong());

		ddmStorageLink.setUuid(RandomTestUtil.randomString());

		ddmStorageLink.setCompanyId(RandomTestUtil.nextLong());

		ddmStorageLink.setClassNameId(RandomTestUtil.nextLong());

		ddmStorageLink.setClassPK(RandomTestUtil.nextLong());

		ddmStorageLink.setStructureId(RandomTestUtil.nextLong());

		ddmStorageLink.setStructureVersionId(RandomTestUtil.nextLong());

		_ddmStorageLinks.add(_persistence.update(ddmStorageLink));

		return ddmStorageLink;
	}

	private List<DDMStorageLink> _ddmStorageLinks =
		new ArrayList<DDMStorageLink>();
	private DDMStorageLinkPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1602423268