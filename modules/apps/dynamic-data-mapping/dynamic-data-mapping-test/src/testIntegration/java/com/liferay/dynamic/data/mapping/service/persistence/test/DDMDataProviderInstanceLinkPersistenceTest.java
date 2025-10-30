/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchDataProviderInstanceLinkException;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstanceLink;
import com.liferay.dynamic.data.mapping.service.persistence.DDMDataProviderInstanceLinkPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMDataProviderInstanceLinkUtil;
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
public class DDMDataProviderInstanceLinkPersistenceTest {

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
		_persistence = DDMDataProviderInstanceLinkUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMDataProviderInstanceLink> iterator =
			_ddmDataProviderInstanceLinks.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMDataProviderInstanceLink ddmDataProviderInstanceLink =
			_persistence.create(pk);

		Assert.assertNotNull(ddmDataProviderInstanceLink);

		Assert.assertEquals(ddmDataProviderInstanceLink.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMDataProviderInstanceLink newDDMDataProviderInstanceLink =
			addDDMDataProviderInstanceLink();

		_persistence.remove(newDDMDataProviderInstanceLink);

		DDMDataProviderInstanceLink existingDDMDataProviderInstanceLink =
			_persistence.fetchByPrimaryKey(
				newDDMDataProviderInstanceLink.getPrimaryKey());

		Assert.assertNull(existingDDMDataProviderInstanceLink);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMDataProviderInstanceLink();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMDataProviderInstanceLink newDDMDataProviderInstanceLink =
			_persistence.create(pk);

		newDDMDataProviderInstanceLink.setMvccVersion(
			RandomTestUtil.nextLong());

		newDDMDataProviderInstanceLink.setCtCollectionId(
			RandomTestUtil.nextLong());

		newDDMDataProviderInstanceLink.setCompanyId(RandomTestUtil.nextLong());

		newDDMDataProviderInstanceLink.setDataProviderInstanceId(
			RandomTestUtil.nextLong());

		newDDMDataProviderInstanceLink.setStructureId(
			RandomTestUtil.nextLong());

		_ddmDataProviderInstanceLinks.add(
			_persistence.update(newDDMDataProviderInstanceLink));

		DDMDataProviderInstanceLink existingDDMDataProviderInstanceLink =
			_persistence.findByPrimaryKey(
				newDDMDataProviderInstanceLink.getPrimaryKey());

		Assert.assertEquals(
			existingDDMDataProviderInstanceLink.getMvccVersion(),
			newDDMDataProviderInstanceLink.getMvccVersion());
		Assert.assertEquals(
			existingDDMDataProviderInstanceLink.getCtCollectionId(),
			newDDMDataProviderInstanceLink.getCtCollectionId());
		Assert.assertEquals(
			existingDDMDataProviderInstanceLink.getDataProviderInstanceLinkId(),
			newDDMDataProviderInstanceLink.getDataProviderInstanceLinkId());
		Assert.assertEquals(
			existingDDMDataProviderInstanceLink.getCompanyId(),
			newDDMDataProviderInstanceLink.getCompanyId());
		Assert.assertEquals(
			existingDDMDataProviderInstanceLink.getDataProviderInstanceId(),
			newDDMDataProviderInstanceLink.getDataProviderInstanceId());
		Assert.assertEquals(
			existingDDMDataProviderInstanceLink.getStructureId(),
			newDDMDataProviderInstanceLink.getStructureId());
	}

	@Test
	public void testCountByDataProviderInstanceId() throws Exception {
		_persistence.countByDataProviderInstanceId(RandomTestUtil.nextLong());

		_persistence.countByDataProviderInstanceId(0L);
	}

	@Test
	public void testCountByStructureId() throws Exception {
		_persistence.countByStructureId(RandomTestUtil.nextLong());

		_persistence.countByStructureId(0L);
	}

	@Test
	public void testCountByD_S() throws Exception {
		_persistence.countByD_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByD_S(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMDataProviderInstanceLink newDDMDataProviderInstanceLink =
			addDDMDataProviderInstanceLink();

		DDMDataProviderInstanceLink existingDDMDataProviderInstanceLink =
			_persistence.findByPrimaryKey(
				newDDMDataProviderInstanceLink.getPrimaryKey());

		Assert.assertEquals(
			existingDDMDataProviderInstanceLink,
			newDDMDataProviderInstanceLink);
	}

	@Test(expected = NoSuchDataProviderInstanceLinkException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMDataProviderInstanceLink>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"DDMDataProviderInstanceLink", "mvccVersion", true,
			"ctCollectionId", true, "dataProviderInstanceLinkId", true,
			"companyId", true, "dataProviderInstanceId", true, "structureId",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMDataProviderInstanceLink newDDMDataProviderInstanceLink =
			addDDMDataProviderInstanceLink();

		DDMDataProviderInstanceLink existingDDMDataProviderInstanceLink =
			_persistence.fetchByPrimaryKey(
				newDDMDataProviderInstanceLink.getPrimaryKey());

		Assert.assertEquals(
			existingDDMDataProviderInstanceLink,
			newDDMDataProviderInstanceLink);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMDataProviderInstanceLink missingDDMDataProviderInstanceLink =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMDataProviderInstanceLink);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMDataProviderInstanceLink newDDMDataProviderInstanceLink1 =
			addDDMDataProviderInstanceLink();
		DDMDataProviderInstanceLink newDDMDataProviderInstanceLink2 =
			addDDMDataProviderInstanceLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMDataProviderInstanceLink1.getPrimaryKey());
		primaryKeys.add(newDDMDataProviderInstanceLink2.getPrimaryKey());

		Map<Serializable, DDMDataProviderInstanceLink>
			ddmDataProviderInstanceLinks = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, ddmDataProviderInstanceLinks.size());
		Assert.assertEquals(
			newDDMDataProviderInstanceLink1,
			ddmDataProviderInstanceLinks.get(
				newDDMDataProviderInstanceLink1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMDataProviderInstanceLink2,
			ddmDataProviderInstanceLinks.get(
				newDDMDataProviderInstanceLink2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMDataProviderInstanceLink>
			ddmDataProviderInstanceLinks = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(ddmDataProviderInstanceLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMDataProviderInstanceLink newDDMDataProviderInstanceLink =
			addDDMDataProviderInstanceLink();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMDataProviderInstanceLink.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMDataProviderInstanceLink>
			ddmDataProviderInstanceLinks = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, ddmDataProviderInstanceLinks.size());
		Assert.assertEquals(
			newDDMDataProviderInstanceLink,
			ddmDataProviderInstanceLinks.get(
				newDDMDataProviderInstanceLink.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMDataProviderInstanceLink>
			ddmDataProviderInstanceLinks = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(ddmDataProviderInstanceLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMDataProviderInstanceLink newDDMDataProviderInstanceLink =
			addDDMDataProviderInstanceLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMDataProviderInstanceLink.getPrimaryKey());

		Map<Serializable, DDMDataProviderInstanceLink>
			ddmDataProviderInstanceLinks = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, ddmDataProviderInstanceLinks.size());
		Assert.assertEquals(
			newDDMDataProviderInstanceLink,
			ddmDataProviderInstanceLinks.get(
				newDDMDataProviderInstanceLink.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMDataProviderInstanceLink newDDMDataProviderInstanceLink =
			addDDMDataProviderInstanceLink();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newDDMDataProviderInstanceLink.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		DDMDataProviderInstanceLink ddmDataProviderInstanceLink) {

		Assert.assertEquals(
			Long.valueOf(
				ddmDataProviderInstanceLink.getDataProviderInstanceId()),
			ReflectionTestUtil.<Long>invoke(
				ddmDataProviderInstanceLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "dataProviderInstanceId"));
		Assert.assertEquals(
			Long.valueOf(ddmDataProviderInstanceLink.getStructureId()),
			ReflectionTestUtil.<Long>invoke(
				ddmDataProviderInstanceLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "structureId"));
	}

	protected DDMDataProviderInstanceLink addDDMDataProviderInstanceLink()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		DDMDataProviderInstanceLink ddmDataProviderInstanceLink =
			_persistence.create(pk);

		ddmDataProviderInstanceLink.setMvccVersion(RandomTestUtil.nextLong());

		ddmDataProviderInstanceLink.setCtCollectionId(
			RandomTestUtil.nextLong());

		ddmDataProviderInstanceLink.setCompanyId(RandomTestUtil.nextLong());

		ddmDataProviderInstanceLink.setDataProviderInstanceId(
			RandomTestUtil.nextLong());

		ddmDataProviderInstanceLink.setStructureId(RandomTestUtil.nextLong());

		_ddmDataProviderInstanceLinks.add(
			_persistence.update(ddmDataProviderInstanceLink));

		return ddmDataProviderInstanceLink;
	}

	private List<DDMDataProviderInstanceLink> _ddmDataProviderInstanceLinks =
		new ArrayList<DDMDataProviderInstanceLink>();
	private DDMDataProviderInstanceLinkPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}