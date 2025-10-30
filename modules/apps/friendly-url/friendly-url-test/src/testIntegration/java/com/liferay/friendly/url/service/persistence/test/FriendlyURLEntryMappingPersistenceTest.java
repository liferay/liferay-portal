/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.friendly.url.exception.NoSuchFriendlyURLEntryMappingException;
import com.liferay.friendly.url.model.FriendlyURLEntryMapping;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryMappingPersistence;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryMappingUtil;
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
public class FriendlyURLEntryMappingPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.friendly.url.service"));

	@Before
	public void setUp() {
		_persistence = FriendlyURLEntryMappingUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<FriendlyURLEntryMapping> iterator =
			_friendlyURLEntryMappings.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FriendlyURLEntryMapping friendlyURLEntryMapping = _persistence.create(
			pk);

		Assert.assertNotNull(friendlyURLEntryMapping);

		Assert.assertEquals(friendlyURLEntryMapping.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		FriendlyURLEntryMapping newFriendlyURLEntryMapping =
			addFriendlyURLEntryMapping();

		_persistence.remove(newFriendlyURLEntryMapping);

		FriendlyURLEntryMapping existingFriendlyURLEntryMapping =
			_persistence.fetchByPrimaryKey(
				newFriendlyURLEntryMapping.getPrimaryKey());

		Assert.assertNull(existingFriendlyURLEntryMapping);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addFriendlyURLEntryMapping();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FriendlyURLEntryMapping newFriendlyURLEntryMapping =
			_persistence.create(pk);

		newFriendlyURLEntryMapping.setMvccVersion(RandomTestUtil.nextLong());

		newFriendlyURLEntryMapping.setCtCollectionId(RandomTestUtil.nextLong());

		newFriendlyURLEntryMapping.setCompanyId(RandomTestUtil.nextLong());

		newFriendlyURLEntryMapping.setClassNameId(RandomTestUtil.nextLong());

		newFriendlyURLEntryMapping.setClassPK(RandomTestUtil.nextLong());

		newFriendlyURLEntryMapping.setFriendlyURLEntryId(
			RandomTestUtil.nextLong());

		_friendlyURLEntryMappings.add(
			_persistence.update(newFriendlyURLEntryMapping));

		FriendlyURLEntryMapping existingFriendlyURLEntryMapping =
			_persistence.findByPrimaryKey(
				newFriendlyURLEntryMapping.getPrimaryKey());

		Assert.assertEquals(
			existingFriendlyURLEntryMapping.getMvccVersion(),
			newFriendlyURLEntryMapping.getMvccVersion());
		Assert.assertEquals(
			existingFriendlyURLEntryMapping.getCtCollectionId(),
			newFriendlyURLEntryMapping.getCtCollectionId());
		Assert.assertEquals(
			existingFriendlyURLEntryMapping.getFriendlyURLEntryMappingId(),
			newFriendlyURLEntryMapping.getFriendlyURLEntryMappingId());
		Assert.assertEquals(
			existingFriendlyURLEntryMapping.getCompanyId(),
			newFriendlyURLEntryMapping.getCompanyId());
		Assert.assertEquals(
			existingFriendlyURLEntryMapping.getClassNameId(),
			newFriendlyURLEntryMapping.getClassNameId());
		Assert.assertEquals(
			existingFriendlyURLEntryMapping.getClassPK(),
			newFriendlyURLEntryMapping.getClassPK());
		Assert.assertEquals(
			existingFriendlyURLEntryMapping.getFriendlyURLEntryId(),
			newFriendlyURLEntryMapping.getFriendlyURLEntryId());
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		FriendlyURLEntryMapping newFriendlyURLEntryMapping =
			addFriendlyURLEntryMapping();

		FriendlyURLEntryMapping existingFriendlyURLEntryMapping =
			_persistence.findByPrimaryKey(
				newFriendlyURLEntryMapping.getPrimaryKey());

		Assert.assertEquals(
			existingFriendlyURLEntryMapping, newFriendlyURLEntryMapping);
	}

	@Test(expected = NoSuchFriendlyURLEntryMappingException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<FriendlyURLEntryMapping>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"FriendlyURLEntryMapping", "mvccVersion", true, "ctCollectionId",
			true, "friendlyURLEntryMappingId", true, "companyId", true,
			"classNameId", true, "classPK", true, "friendlyURLEntryId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		FriendlyURLEntryMapping newFriendlyURLEntryMapping =
			addFriendlyURLEntryMapping();

		FriendlyURLEntryMapping existingFriendlyURLEntryMapping =
			_persistence.fetchByPrimaryKey(
				newFriendlyURLEntryMapping.getPrimaryKey());

		Assert.assertEquals(
			existingFriendlyURLEntryMapping, newFriendlyURLEntryMapping);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FriendlyURLEntryMapping missingFriendlyURLEntryMapping =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingFriendlyURLEntryMapping);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		FriendlyURLEntryMapping newFriendlyURLEntryMapping1 =
			addFriendlyURLEntryMapping();
		FriendlyURLEntryMapping newFriendlyURLEntryMapping2 =
			addFriendlyURLEntryMapping();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFriendlyURLEntryMapping1.getPrimaryKey());
		primaryKeys.add(newFriendlyURLEntryMapping2.getPrimaryKey());

		Map<Serializable, FriendlyURLEntryMapping> friendlyURLEntryMappings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, friendlyURLEntryMappings.size());
		Assert.assertEquals(
			newFriendlyURLEntryMapping1,
			friendlyURLEntryMappings.get(
				newFriendlyURLEntryMapping1.getPrimaryKey()));
		Assert.assertEquals(
			newFriendlyURLEntryMapping2,
			friendlyURLEntryMappings.get(
				newFriendlyURLEntryMapping2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, FriendlyURLEntryMapping> friendlyURLEntryMappings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(friendlyURLEntryMappings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		FriendlyURLEntryMapping newFriendlyURLEntryMapping =
			addFriendlyURLEntryMapping();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFriendlyURLEntryMapping.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, FriendlyURLEntryMapping> friendlyURLEntryMappings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, friendlyURLEntryMappings.size());
		Assert.assertEquals(
			newFriendlyURLEntryMapping,
			friendlyURLEntryMappings.get(
				newFriendlyURLEntryMapping.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, FriendlyURLEntryMapping> friendlyURLEntryMappings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(friendlyURLEntryMappings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		FriendlyURLEntryMapping newFriendlyURLEntryMapping =
			addFriendlyURLEntryMapping();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFriendlyURLEntryMapping.getPrimaryKey());

		Map<Serializable, FriendlyURLEntryMapping> friendlyURLEntryMappings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, friendlyURLEntryMappings.size());
		Assert.assertEquals(
			newFriendlyURLEntryMapping,
			friendlyURLEntryMappings.get(
				newFriendlyURLEntryMapping.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		FriendlyURLEntryMapping newFriendlyURLEntryMapping =
			addFriendlyURLEntryMapping();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newFriendlyURLEntryMapping.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		FriendlyURLEntryMapping friendlyURLEntryMapping) {

		Assert.assertEquals(
			Long.valueOf(friendlyURLEntryMapping.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				friendlyURLEntryMapping, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(friendlyURLEntryMapping.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				friendlyURLEntryMapping, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected FriendlyURLEntryMapping addFriendlyURLEntryMapping()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		FriendlyURLEntryMapping friendlyURLEntryMapping = _persistence.create(
			pk);

		friendlyURLEntryMapping.setMvccVersion(RandomTestUtil.nextLong());

		friendlyURLEntryMapping.setCtCollectionId(RandomTestUtil.nextLong());

		friendlyURLEntryMapping.setCompanyId(RandomTestUtil.nextLong());

		friendlyURLEntryMapping.setClassNameId(RandomTestUtil.nextLong());

		friendlyURLEntryMapping.setClassPK(RandomTestUtil.nextLong());

		friendlyURLEntryMapping.setFriendlyURLEntryId(
			RandomTestUtil.nextLong());

		_friendlyURLEntryMappings.add(
			_persistence.update(friendlyURLEntryMapping));

		return friendlyURLEntryMapping;
	}

	private List<FriendlyURLEntryMapping> _friendlyURLEntryMappings =
		new ArrayList<FriendlyURLEntryMapping>();
	private FriendlyURLEntryMappingPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}