/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureLinkException;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLinkPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLinkUtil;
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
public class DDMStructureLinkPersistenceTest {

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
		_persistence = DDMStructureLinkUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMStructureLink> iterator = _ddmStructureLinks.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureLink ddmStructureLink = _persistence.create(pk);

		Assert.assertNotNull(ddmStructureLink);

		Assert.assertEquals(ddmStructureLink.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		_persistence.remove(newDDMStructureLink);

		DDMStructureLink existingDDMStructureLink =
			_persistence.fetchByPrimaryKey(newDDMStructureLink.getPrimaryKey());

		Assert.assertNull(existingDDMStructureLink);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMStructureLink();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureLink newDDMStructureLink = _persistence.create(pk);

		newDDMStructureLink.setMvccVersion(RandomTestUtil.nextLong());

		newDDMStructureLink.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMStructureLink.setCompanyId(RandomTestUtil.nextLong());

		newDDMStructureLink.setClassNameId(RandomTestUtil.nextLong());

		newDDMStructureLink.setClassPK(RandomTestUtil.nextLong());

		newDDMStructureLink.setStructureId(RandomTestUtil.nextLong());

		_ddmStructureLinks.add(_persistence.update(newDDMStructureLink));

		DDMStructureLink existingDDMStructureLink =
			_persistence.findByPrimaryKey(newDDMStructureLink.getPrimaryKey());

		Assert.assertEquals(
			existingDDMStructureLink.getMvccVersion(),
			newDDMStructureLink.getMvccVersion());
		Assert.assertEquals(
			existingDDMStructureLink.getCtCollectionId(),
			newDDMStructureLink.getCtCollectionId());
		Assert.assertEquals(
			existingDDMStructureLink.getStructureLinkId(),
			newDDMStructureLink.getStructureLinkId());
		Assert.assertEquals(
			existingDDMStructureLink.getCompanyId(),
			newDDMStructureLink.getCompanyId());
		Assert.assertEquals(
			existingDDMStructureLink.getClassNameId(),
			newDDMStructureLink.getClassNameId());
		Assert.assertEquals(
			existingDDMStructureLink.getClassPK(),
			newDDMStructureLink.getClassPK());
		Assert.assertEquals(
			existingDDMStructureLink.getStructureId(),
			newDDMStructureLink.getStructureId());
	}

	@Test
	public void testCountByStructureId() throws Exception {
		_persistence.countByStructureId(RandomTestUtil.nextLong());

		_persistence.countByStructureId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_C_S() throws Exception {
		_persistence.countByC_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_S(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		DDMStructureLink existingDDMStructureLink =
			_persistence.findByPrimaryKey(newDDMStructureLink.getPrimaryKey());

		Assert.assertEquals(existingDDMStructureLink, newDDMStructureLink);
	}

	@Test(expected = NoSuchStructureLinkException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMStructureLink> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDMStructureLink", "mvccVersion", true, "ctCollectionId", true,
			"structureLinkId", true, "companyId", true, "classNameId", true,
			"classPK", true, "structureId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		DDMStructureLink existingDDMStructureLink =
			_persistence.fetchByPrimaryKey(newDDMStructureLink.getPrimaryKey());

		Assert.assertEquals(existingDDMStructureLink, newDDMStructureLink);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureLink missingDDMStructureLink =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMStructureLink);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMStructureLink newDDMStructureLink1 = addDDMStructureLink();
		DDMStructureLink newDDMStructureLink2 = addDDMStructureLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructureLink1.getPrimaryKey());
		primaryKeys.add(newDDMStructureLink2.getPrimaryKey());

		Map<Serializable, DDMStructureLink> ddmStructureLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmStructureLinks.size());
		Assert.assertEquals(
			newDDMStructureLink1,
			ddmStructureLinks.get(newDDMStructureLink1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMStructureLink2,
			ddmStructureLinks.get(newDDMStructureLink2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMStructureLink> ddmStructureLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmStructureLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructureLink.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMStructureLink> ddmStructureLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmStructureLinks.size());
		Assert.assertEquals(
			newDDMStructureLink,
			ddmStructureLinks.get(newDDMStructureLink.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMStructureLink> ddmStructureLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmStructureLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructureLink.getPrimaryKey());

		Map<Serializable, DDMStructureLink> ddmStructureLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmStructureLinks.size());
		Assert.assertEquals(
			newDDMStructureLink,
			ddmStructureLinks.get(newDDMStructureLink.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDDMStructureLink.getPrimaryKey()));
	}

	private void _assertOriginalValues(DDMStructureLink ddmStructureLink) {
		Assert.assertEquals(
			Long.valueOf(ddmStructureLink.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				ddmStructureLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(ddmStructureLink.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				ddmStructureLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Long.valueOf(ddmStructureLink.getStructureId()),
			ReflectionTestUtil.<Long>invoke(
				ddmStructureLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "structureId"));
	}

	protected DDMStructureLink addDDMStructureLink() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureLink ddmStructureLink = _persistence.create(pk);

		ddmStructureLink.setMvccVersion(RandomTestUtil.nextLong());

		ddmStructureLink.setCtCollectionId(RandomTestUtil.nextLong());

		ddmStructureLink.setCompanyId(RandomTestUtil.nextLong());

		ddmStructureLink.setClassNameId(RandomTestUtil.nextLong());

		ddmStructureLink.setClassPK(RandomTestUtil.nextLong());

		ddmStructureLink.setStructureId(RandomTestUtil.nextLong());

		_ddmStructureLinks.add(_persistence.update(ddmStructureLink));

		return ddmStructureLink;
	}

	private List<DDMStructureLink> _ddmStructureLinks =
		new ArrayList<DDMStructureLink>();
	private DDMStructureLinkPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}