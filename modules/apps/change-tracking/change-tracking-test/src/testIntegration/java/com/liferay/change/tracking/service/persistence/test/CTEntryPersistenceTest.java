/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.exception.DuplicateCTEntryExternalReferenceCodeException;
import com.liferay.change.tracking.exception.NoSuchEntryException;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.persistence.CTEntryPersistence;
import com.liferay.change.tracking.service.persistence.CTEntryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
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
public class CTEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.change.tracking.service"));

	@Before
	public void setUp() {
		_persistence = CTEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTEntry> iterator = _ctEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTEntry ctEntry = _persistence.create(pk);

		Assert.assertNotNull(ctEntry);

		Assert.assertEquals(ctEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTEntry newCTEntry = addCTEntry();

		_persistence.remove(newCTEntry);

		CTEntry existingCTEntry = _persistence.fetchByPrimaryKey(
			newCTEntry.getPrimaryKey());

		Assert.assertNull(existingCTEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTEntry newCTEntry = _persistence.create(pk);

		newCTEntry.setMvccVersion(RandomTestUtil.nextLong());

		newCTEntry.setUuid(RandomTestUtil.randomString());

		newCTEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		newCTEntry.setCompanyId(RandomTestUtil.nextLong());

		newCTEntry.setUserId(RandomTestUtil.nextLong());

		newCTEntry.setCreateDate(RandomTestUtil.nextDate());

		newCTEntry.setModifiedDate(RandomTestUtil.nextDate());

		newCTEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newCTEntry.setModelClassNameId(RandomTestUtil.nextLong());

		newCTEntry.setModelClassPK(RandomTestUtil.nextLong());

		newCTEntry.setModelMvccVersion(RandomTestUtil.nextLong());

		newCTEntry.setChangeType(RandomTestUtil.nextInt());

		_ctEntries.add(_persistence.update(newCTEntry));

		CTEntry existingCTEntry = _persistence.findByPrimaryKey(
			newCTEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCTEntry.getMvccVersion(), newCTEntry.getMvccVersion());
		Assert.assertEquals(existingCTEntry.getUuid(), newCTEntry.getUuid());
		Assert.assertEquals(
			existingCTEntry.getExternalReferenceCode(),
			newCTEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingCTEntry.getCtEntryId(), newCTEntry.getCtEntryId());
		Assert.assertEquals(
			existingCTEntry.getCompanyId(), newCTEntry.getCompanyId());
		Assert.assertEquals(
			existingCTEntry.getUserId(), newCTEntry.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCTEntry.getCreateDate()),
			Time.getShortTimestamp(newCTEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCTEntry.getModifiedDate()),
			Time.getShortTimestamp(newCTEntry.getModifiedDate()));
		Assert.assertEquals(
			existingCTEntry.getCtCollectionId(),
			newCTEntry.getCtCollectionId());
		Assert.assertEquals(
			existingCTEntry.getModelClassNameId(),
			newCTEntry.getModelClassNameId());
		Assert.assertEquals(
			existingCTEntry.getModelClassPK(), newCTEntry.getModelClassPK());
		Assert.assertEquals(
			existingCTEntry.getModelMvccVersion(),
			newCTEntry.getModelMvccVersion());
		Assert.assertEquals(
			existingCTEntry.getChangeType(), newCTEntry.getChangeType());
	}

	@Test(expected = DuplicateCTEntryExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CTEntry ctEntry = addCTEntry();

		CTEntry newCTEntry = addCTEntry();

		newCTEntry.setCompanyId(ctEntry.getCompanyId());

		newCTEntry = _persistence.update(newCTEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newCTEntry);

		newCTEntry.setExternalReferenceCode(ctEntry.getExternalReferenceCode());

		_persistence.update(newCTEntry);
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
	public void testCountByCtCollectionId() throws Exception {
		_persistence.countByCtCollectionId(RandomTestUtil.nextLong());

		_persistence.countByCtCollectionId(0L);
	}

	@Test
	public void testCountByC_MCNI() throws Exception {
		_persistence.countByC_MCNI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_MCNI(0L, 0L);
	}

	@Test
	public void testCountByC_MCNI_MCPK() throws Exception {
		_persistence.countByC_MCNI_MCPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_MCNI_MCPK(0L, 0L, 0L);
	}

	@Test
	public void testCountByNotC_MCNI_MCPK() throws Exception {
		_persistence.countByNotC_MCNI_MCPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByNotC_MCNI_MCPK(0L, 0L, 0L);
	}

	@Test
	public void testCountByNotC_MCNI_MCPKArrayable() throws Exception {
		_persistence.countByNotC_MCNI_MCPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTEntry newCTEntry = addCTEntry();

		CTEntry existingCTEntry = _persistence.findByPrimaryKey(
			newCTEntry.getPrimaryKey());

		Assert.assertEquals(existingCTEntry, newCTEntry);
	}

	@Test(expected = NoSuchEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTEntry", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "ctEntryId", true, "companyId", true,
			"userId", true, "createDate", true, "modifiedDate", true,
			"ctCollectionId", true, "modelClassNameId", true, "modelClassPK",
			true, "modelMvccVersion", true, "changeType", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTEntry newCTEntry = addCTEntry();

		CTEntry existingCTEntry = _persistence.fetchByPrimaryKey(
			newCTEntry.getPrimaryKey());

		Assert.assertEquals(existingCTEntry, newCTEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTEntry missingCTEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTEntry newCTEntry1 = addCTEntry();
		CTEntry newCTEntry2 = addCTEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTEntry1.getPrimaryKey());
		primaryKeys.add(newCTEntry2.getPrimaryKey());

		Map<Serializable, CTEntry> ctEntries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, ctEntries.size());
		Assert.assertEquals(
			newCTEntry1, ctEntries.get(newCTEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCTEntry2, ctEntries.get(newCTEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTEntry> ctEntries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(ctEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTEntry newCTEntry = addCTEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTEntry> ctEntries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, ctEntries.size());
		Assert.assertEquals(
			newCTEntry, ctEntries.get(newCTEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTEntry> ctEntries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(ctEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTEntry newCTEntry = addCTEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTEntry.getPrimaryKey());

		Map<Serializable, CTEntry> ctEntries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, ctEntries.size());
		Assert.assertEquals(
			newCTEntry, ctEntries.get(newCTEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CTEntry newCTEntry = addCTEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCTEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(CTEntry ctEntry) {
		Assert.assertEquals(
			Long.valueOf(ctEntry.getCtCollectionId()),
			ReflectionTestUtil.<Long>invoke(
				ctEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "ctCollectionId"));
		Assert.assertEquals(
			Long.valueOf(ctEntry.getModelClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				ctEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "modelClassNameId"));
		Assert.assertEquals(
			Long.valueOf(ctEntry.getModelClassPK()),
			ReflectionTestUtil.<Long>invoke(
				ctEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "modelClassPK"));

		Assert.assertEquals(
			ctEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				ctEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(ctEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				ctEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CTEntry addCTEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTEntry ctEntry = _persistence.create(pk);

		ctEntry.setMvccVersion(RandomTestUtil.nextLong());

		ctEntry.setUuid(RandomTestUtil.randomString());

		ctEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		ctEntry.setCompanyId(RandomTestUtil.nextLong());

		ctEntry.setUserId(RandomTestUtil.nextLong());

		ctEntry.setCreateDate(RandomTestUtil.nextDate());

		ctEntry.setModifiedDate(RandomTestUtil.nextDate());

		ctEntry.setCtCollectionId(RandomTestUtil.nextLong());

		ctEntry.setModelClassNameId(RandomTestUtil.nextLong());

		ctEntry.setModelClassPK(RandomTestUtil.nextLong());

		ctEntry.setModelMvccVersion(RandomTestUtil.nextLong());

		ctEntry.setChangeType(RandomTestUtil.nextInt());

		_ctEntries.add(_persistence.update(ctEntry));

		return ctEntry;
	}

	private List<CTEntry> _ctEntries = new ArrayList<CTEntry>();
	private CTEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}