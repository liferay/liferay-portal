/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchCPDisplayLayoutException;
import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.commerce.product.service.persistence.CPDisplayLayoutPersistence;
import com.liferay.commerce.product.service.persistence.CPDisplayLayoutUtil;
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
public class CPDisplayLayoutPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPDisplayLayoutUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPDisplayLayout> iterator = _cpDisplayLayouts.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDisplayLayout cpDisplayLayout = _persistence.create(pk);

		Assert.assertNotNull(cpDisplayLayout);

		Assert.assertEquals(cpDisplayLayout.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPDisplayLayout newCPDisplayLayout = addCPDisplayLayout();

		_persistence.remove(newCPDisplayLayout);

		CPDisplayLayout existingCPDisplayLayout =
			_persistence.fetchByPrimaryKey(newCPDisplayLayout.getPrimaryKey());

		Assert.assertNull(existingCPDisplayLayout);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPDisplayLayout();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDisplayLayout newCPDisplayLayout = _persistence.create(pk);

		newCPDisplayLayout.setMvccVersion(RandomTestUtil.nextLong());

		newCPDisplayLayout.setCtCollectionId(RandomTestUtil.nextLong());

		newCPDisplayLayout.setUuid(RandomTestUtil.randomString());

		newCPDisplayLayout.setGroupId(RandomTestUtil.nextLong());

		newCPDisplayLayout.setCompanyId(RandomTestUtil.nextLong());

		newCPDisplayLayout.setUserId(RandomTestUtil.nextLong());

		newCPDisplayLayout.setUserName(RandomTestUtil.randomString());

		newCPDisplayLayout.setCreateDate(RandomTestUtil.nextDate());

		newCPDisplayLayout.setModifiedDate(RandomTestUtil.nextDate());

		newCPDisplayLayout.setClassNameId(RandomTestUtil.nextLong());

		newCPDisplayLayout.setClassPK(RandomTestUtil.nextLong());

		newCPDisplayLayout.setLayoutPageTemplateEntryUuid(
			RandomTestUtil.randomString());

		newCPDisplayLayout.setLayoutUuid(RandomTestUtil.randomString());

		_cpDisplayLayouts.add(_persistence.update(newCPDisplayLayout));

		CPDisplayLayout existingCPDisplayLayout = _persistence.findByPrimaryKey(
			newCPDisplayLayout.getPrimaryKey());

		Assert.assertEquals(
			existingCPDisplayLayout.getMvccVersion(),
			newCPDisplayLayout.getMvccVersion());
		Assert.assertEquals(
			existingCPDisplayLayout.getCtCollectionId(),
			newCPDisplayLayout.getCtCollectionId());
		Assert.assertEquals(
			existingCPDisplayLayout.getUuid(), newCPDisplayLayout.getUuid());
		Assert.assertEquals(
			existingCPDisplayLayout.getCPDisplayLayoutId(),
			newCPDisplayLayout.getCPDisplayLayoutId());
		Assert.assertEquals(
			existingCPDisplayLayout.getGroupId(),
			newCPDisplayLayout.getGroupId());
		Assert.assertEquals(
			existingCPDisplayLayout.getCompanyId(),
			newCPDisplayLayout.getCompanyId());
		Assert.assertEquals(
			existingCPDisplayLayout.getUserId(),
			newCPDisplayLayout.getUserId());
		Assert.assertEquals(
			existingCPDisplayLayout.getUserName(),
			newCPDisplayLayout.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPDisplayLayout.getCreateDate()),
			Time.getShortTimestamp(newCPDisplayLayout.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPDisplayLayout.getModifiedDate()),
			Time.getShortTimestamp(newCPDisplayLayout.getModifiedDate()));
		Assert.assertEquals(
			existingCPDisplayLayout.getClassNameId(),
			newCPDisplayLayout.getClassNameId());
		Assert.assertEquals(
			existingCPDisplayLayout.getClassPK(),
			newCPDisplayLayout.getClassPK());
		Assert.assertEquals(
			existingCPDisplayLayout.getLayoutPageTemplateEntryUuid(),
			newCPDisplayLayout.getLayoutPageTemplateEntryUuid());
		Assert.assertEquals(
			existingCPDisplayLayout.getLayoutUuid(),
			newCPDisplayLayout.getLayoutUuid());
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
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testCountByG_LPTEU() throws Exception {
		_persistence.countByG_LPTEU(RandomTestUtil.nextLong(), "");

		_persistence.countByG_LPTEU(0L, "null");

		_persistence.countByG_LPTEU(0L, (String)null);
	}

	@Test
	public void testCountByG_L() throws Exception {
		_persistence.countByG_L(RandomTestUtil.nextLong(), "");

		_persistence.countByG_L(0L, "null");

		_persistence.countByG_L(0L, (String)null);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_C_LPTEU() throws Exception {
		_persistence.countByC_C_LPTEU(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C_LPTEU(0L, 0L);
	}

	@Test
	public void testCountByC_C_L() throws Exception {
		_persistence.countByC_C_L(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C_L(0L, 0L);
	}

	@Test
	public void testCountByG_C_C() throws Exception {
		_persistence.countByG_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_C(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPDisplayLayout newCPDisplayLayout = addCPDisplayLayout();

		CPDisplayLayout existingCPDisplayLayout = _persistence.findByPrimaryKey(
			newCPDisplayLayout.getPrimaryKey());

		Assert.assertEquals(existingCPDisplayLayout, newCPDisplayLayout);
	}

	@Test(expected = NoSuchCPDisplayLayoutException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPDisplayLayout> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPDisplayLayout", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "CPDisplayLayoutId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "classNameId", true, "classPK", true,
			"layoutPageTemplateEntryUuid", true, "layoutUuid", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPDisplayLayout newCPDisplayLayout = addCPDisplayLayout();

		CPDisplayLayout existingCPDisplayLayout =
			_persistence.fetchByPrimaryKey(newCPDisplayLayout.getPrimaryKey());

		Assert.assertEquals(existingCPDisplayLayout, newCPDisplayLayout);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDisplayLayout missingCPDisplayLayout = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingCPDisplayLayout);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPDisplayLayout newCPDisplayLayout1 = addCPDisplayLayout();
		CPDisplayLayout newCPDisplayLayout2 = addCPDisplayLayout();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDisplayLayout1.getPrimaryKey());
		primaryKeys.add(newCPDisplayLayout2.getPrimaryKey());

		Map<Serializable, CPDisplayLayout> cpDisplayLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpDisplayLayouts.size());
		Assert.assertEquals(
			newCPDisplayLayout1,
			cpDisplayLayouts.get(newCPDisplayLayout1.getPrimaryKey()));
		Assert.assertEquals(
			newCPDisplayLayout2,
			cpDisplayLayouts.get(newCPDisplayLayout2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPDisplayLayout> cpDisplayLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpDisplayLayouts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPDisplayLayout newCPDisplayLayout = addCPDisplayLayout();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDisplayLayout.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPDisplayLayout> cpDisplayLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpDisplayLayouts.size());
		Assert.assertEquals(
			newCPDisplayLayout,
			cpDisplayLayouts.get(newCPDisplayLayout.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPDisplayLayout> cpDisplayLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpDisplayLayouts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPDisplayLayout newCPDisplayLayout = addCPDisplayLayout();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDisplayLayout.getPrimaryKey());

		Map<Serializable, CPDisplayLayout> cpDisplayLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpDisplayLayouts.size());
		Assert.assertEquals(
			newCPDisplayLayout,
			cpDisplayLayouts.get(newCPDisplayLayout.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPDisplayLayout newCPDisplayLayout = addCPDisplayLayout();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCPDisplayLayout.getPrimaryKey()));
	}

	private void _assertOriginalValues(CPDisplayLayout cpDisplayLayout) {
		Assert.assertEquals(
			cpDisplayLayout.getUuid(),
			ReflectionTestUtil.invoke(
				cpDisplayLayout, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpDisplayLayout.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpDisplayLayout, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(cpDisplayLayout.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpDisplayLayout, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(cpDisplayLayout.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				cpDisplayLayout, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(cpDisplayLayout.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				cpDisplayLayout, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected CPDisplayLayout addCPDisplayLayout() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDisplayLayout cpDisplayLayout = _persistence.create(pk);

		cpDisplayLayout.setMvccVersion(RandomTestUtil.nextLong());

		cpDisplayLayout.setCtCollectionId(RandomTestUtil.nextLong());

		cpDisplayLayout.setUuid(RandomTestUtil.randomString());

		cpDisplayLayout.setGroupId(RandomTestUtil.nextLong());

		cpDisplayLayout.setCompanyId(RandomTestUtil.nextLong());

		cpDisplayLayout.setUserId(RandomTestUtil.nextLong());

		cpDisplayLayout.setUserName(RandomTestUtil.randomString());

		cpDisplayLayout.setCreateDate(RandomTestUtil.nextDate());

		cpDisplayLayout.setModifiedDate(RandomTestUtil.nextDate());

		cpDisplayLayout.setClassNameId(RandomTestUtil.nextLong());

		cpDisplayLayout.setClassPK(RandomTestUtil.nextLong());

		cpDisplayLayout.setLayoutPageTemplateEntryUuid(
			RandomTestUtil.randomString());

		cpDisplayLayout.setLayoutUuid(RandomTestUtil.randomString());

		_cpDisplayLayouts.add(_persistence.update(cpDisplayLayout));

		return cpDisplayLayout;
	}

	private List<CPDisplayLayout> _cpDisplayLayouts =
		new ArrayList<CPDisplayLayout>();
	private CPDisplayLayoutPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}