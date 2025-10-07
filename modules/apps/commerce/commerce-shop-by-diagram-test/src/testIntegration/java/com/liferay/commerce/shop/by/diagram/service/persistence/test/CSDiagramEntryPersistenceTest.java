/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.shop.by.diagram.exception.DuplicateCSDiagramEntryExternalReferenceCodeException;
import com.liferay.commerce.shop.by.diagram.exception.NoSuchCSDiagramEntryException;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryLocalServiceUtil;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramEntryPersistence;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramEntryUtil;
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
public class CSDiagramEntryPersistenceTest {

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
		_persistence = CSDiagramEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CSDiagramEntry> iterator = _csDiagramEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CSDiagramEntry csDiagramEntry = _persistence.create(pk);

		Assert.assertNotNull(csDiagramEntry);

		Assert.assertEquals(csDiagramEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CSDiagramEntry newCSDiagramEntry = addCSDiagramEntry();

		_persistence.remove(newCSDiagramEntry);

		CSDiagramEntry existingCSDiagramEntry = _persistence.fetchByPrimaryKey(
			newCSDiagramEntry.getPrimaryKey());

		Assert.assertNull(existingCSDiagramEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCSDiagramEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CSDiagramEntry newCSDiagramEntry = _persistence.create(pk);

		newCSDiagramEntry.setMvccVersion(RandomTestUtil.nextLong());

		newCSDiagramEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newCSDiagramEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCSDiagramEntry.setCompanyId(RandomTestUtil.nextLong());

		newCSDiagramEntry.setUserId(RandomTestUtil.nextLong());

		newCSDiagramEntry.setUserName(RandomTestUtil.randomString());

		newCSDiagramEntry.setCreateDate(RandomTestUtil.nextDate());

		newCSDiagramEntry.setModifiedDate(RandomTestUtil.nextDate());

		newCSDiagramEntry.setCPDefinitionId(RandomTestUtil.nextLong());

		newCSDiagramEntry.setCPInstanceId(RandomTestUtil.nextLong());

		newCSDiagramEntry.setCProductId(RandomTestUtil.nextLong());

		newCSDiagramEntry.setDiagram(RandomTestUtil.randomBoolean());

		newCSDiagramEntry.setQuantity(RandomTestUtil.nextInt());

		newCSDiagramEntry.setSequence(RandomTestUtil.randomString());

		newCSDiagramEntry.setSku(RandomTestUtil.randomString());

		_csDiagramEntries.add(_persistence.update(newCSDiagramEntry));

		CSDiagramEntry existingCSDiagramEntry = _persistence.findByPrimaryKey(
			newCSDiagramEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCSDiagramEntry.getMvccVersion(),
			newCSDiagramEntry.getMvccVersion());
		Assert.assertEquals(
			existingCSDiagramEntry.getCtCollectionId(),
			newCSDiagramEntry.getCtCollectionId());
		Assert.assertEquals(
			existingCSDiagramEntry.getExternalReferenceCode(),
			newCSDiagramEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingCSDiagramEntry.getCSDiagramEntryId(),
			newCSDiagramEntry.getCSDiagramEntryId());
		Assert.assertEquals(
			existingCSDiagramEntry.getCompanyId(),
			newCSDiagramEntry.getCompanyId());
		Assert.assertEquals(
			existingCSDiagramEntry.getUserId(), newCSDiagramEntry.getUserId());
		Assert.assertEquals(
			existingCSDiagramEntry.getUserName(),
			newCSDiagramEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCSDiagramEntry.getCreateDate()),
			Time.getShortTimestamp(newCSDiagramEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCSDiagramEntry.getModifiedDate()),
			Time.getShortTimestamp(newCSDiagramEntry.getModifiedDate()));
		Assert.assertEquals(
			existingCSDiagramEntry.getCPDefinitionId(),
			newCSDiagramEntry.getCPDefinitionId());
		Assert.assertEquals(
			existingCSDiagramEntry.getCPInstanceId(),
			newCSDiagramEntry.getCPInstanceId());
		Assert.assertEquals(
			existingCSDiagramEntry.getCProductId(),
			newCSDiagramEntry.getCProductId());
		Assert.assertEquals(
			existingCSDiagramEntry.isDiagram(), newCSDiagramEntry.isDiagram());
		Assert.assertEquals(
			existingCSDiagramEntry.getQuantity(),
			newCSDiagramEntry.getQuantity());
		Assert.assertEquals(
			existingCSDiagramEntry.getSequence(),
			newCSDiagramEntry.getSequence());
		Assert.assertEquals(
			existingCSDiagramEntry.getSku(), newCSDiagramEntry.getSku());
	}

	@Test(
		expected = DuplicateCSDiagramEntryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CSDiagramEntry csDiagramEntry = addCSDiagramEntry();

		CSDiagramEntry newCSDiagramEntry = addCSDiagramEntry();

		newCSDiagramEntry.setCompanyId(csDiagramEntry.getCompanyId());

		newCSDiagramEntry = _persistence.update(newCSDiagramEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newCSDiagramEntry);

		newCSDiagramEntry.setExternalReferenceCode(
			csDiagramEntry.getExternalReferenceCode());

		_persistence.update(newCSDiagramEntry);
	}

	@Test
	public void testCountByCPDefinitionId() throws Exception {
		_persistence.countByCPDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByCPDefinitionId(0L);
	}

	@Test
	public void testCountByCPInstanceId() throws Exception {
		_persistence.countByCPInstanceId(RandomTestUtil.nextLong());

		_persistence.countByCPInstanceId(0L);
	}

	@Test
	public void testCountByCProductId() throws Exception {
		_persistence.countByCProductId(RandomTestUtil.nextLong());

		_persistence.countByCProductId(0L);
	}

	@Test
	public void testCountByCPDI_S() throws Exception {
		_persistence.countByCPDI_S(RandomTestUtil.nextLong(), "");

		_persistence.countByCPDI_S(0L, "null");

		_persistence.countByCPDI_S(0L, (String)null);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CSDiagramEntry newCSDiagramEntry = addCSDiagramEntry();

		CSDiagramEntry existingCSDiagramEntry = _persistence.findByPrimaryKey(
			newCSDiagramEntry.getPrimaryKey());

		Assert.assertEquals(existingCSDiagramEntry, newCSDiagramEntry);
	}

	@Test(expected = NoSuchCSDiagramEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CSDiagramEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CSDiagramEntry", "mvccVersion", true, "ctCollectionId", true,
			"externalReferenceCode", true, "CSDiagramEntryId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "CPDefinitionId", true, "CPInstanceId",
			true, "CProductId", true, "diagram", true, "quantity", true,
			"sequence", true, "sku", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CSDiagramEntry newCSDiagramEntry = addCSDiagramEntry();

		CSDiagramEntry existingCSDiagramEntry = _persistence.fetchByPrimaryKey(
			newCSDiagramEntry.getPrimaryKey());

		Assert.assertEquals(existingCSDiagramEntry, newCSDiagramEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CSDiagramEntry missingCSDiagramEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingCSDiagramEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CSDiagramEntry newCSDiagramEntry1 = addCSDiagramEntry();
		CSDiagramEntry newCSDiagramEntry2 = addCSDiagramEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCSDiagramEntry1.getPrimaryKey());
		primaryKeys.add(newCSDiagramEntry2.getPrimaryKey());

		Map<Serializable, CSDiagramEntry> csDiagramEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, csDiagramEntries.size());
		Assert.assertEquals(
			newCSDiagramEntry1,
			csDiagramEntries.get(newCSDiagramEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCSDiagramEntry2,
			csDiagramEntries.get(newCSDiagramEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CSDiagramEntry> csDiagramEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(csDiagramEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CSDiagramEntry newCSDiagramEntry = addCSDiagramEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCSDiagramEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CSDiagramEntry> csDiagramEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, csDiagramEntries.size());
		Assert.assertEquals(
			newCSDiagramEntry,
			csDiagramEntries.get(newCSDiagramEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CSDiagramEntry> csDiagramEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(csDiagramEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CSDiagramEntry newCSDiagramEntry = addCSDiagramEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCSDiagramEntry.getPrimaryKey());

		Map<Serializable, CSDiagramEntry> csDiagramEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, csDiagramEntries.size());
		Assert.assertEquals(
			newCSDiagramEntry,
			csDiagramEntries.get(newCSDiagramEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CSDiagramEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<CSDiagramEntry>() {

				@Override
				public void performAction(CSDiagramEntry csDiagramEntry) {
					Assert.assertNotNull(csDiagramEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CSDiagramEntry newCSDiagramEntry = addCSDiagramEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CSDiagramEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CSDiagramEntryId", newCSDiagramEntry.getCSDiagramEntryId()));

		List<CSDiagramEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CSDiagramEntry existingCSDiagramEntry = result.get(0);

		Assert.assertEquals(existingCSDiagramEntry, newCSDiagramEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CSDiagramEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CSDiagramEntryId", RandomTestUtil.nextLong()));

		List<CSDiagramEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CSDiagramEntry newCSDiagramEntry = addCSDiagramEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CSDiagramEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("CSDiagramEntryId"));

		Object newCSDiagramEntryId = newCSDiagramEntry.getCSDiagramEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CSDiagramEntryId", new Object[] {newCSDiagramEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCSDiagramEntryId = result.get(0);

		Assert.assertEquals(existingCSDiagramEntryId, newCSDiagramEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CSDiagramEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("CSDiagramEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CSDiagramEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CSDiagramEntry newCSDiagramEntry = addCSDiagramEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCSDiagramEntry.getPrimaryKey()));
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

		CSDiagramEntry newCSDiagramEntry = addCSDiagramEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CSDiagramEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CSDiagramEntryId", newCSDiagramEntry.getCSDiagramEntryId()));

		List<CSDiagramEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(CSDiagramEntry csDiagramEntry) {
		Assert.assertEquals(
			Long.valueOf(csDiagramEntry.getCPDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				csDiagramEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionId"));
		Assert.assertEquals(
			csDiagramEntry.getSequence(),
			ReflectionTestUtil.invoke(
				csDiagramEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "sequence"));

		Assert.assertEquals(
			csDiagramEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				csDiagramEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(csDiagramEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				csDiagramEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CSDiagramEntry addCSDiagramEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CSDiagramEntry csDiagramEntry = _persistence.create(pk);

		csDiagramEntry.setMvccVersion(RandomTestUtil.nextLong());

		csDiagramEntry.setCtCollectionId(RandomTestUtil.nextLong());

		csDiagramEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		csDiagramEntry.setCompanyId(RandomTestUtil.nextLong());

		csDiagramEntry.setUserId(RandomTestUtil.nextLong());

		csDiagramEntry.setUserName(RandomTestUtil.randomString());

		csDiagramEntry.setCreateDate(RandomTestUtil.nextDate());

		csDiagramEntry.setModifiedDate(RandomTestUtil.nextDate());

		csDiagramEntry.setCPDefinitionId(RandomTestUtil.nextLong());

		csDiagramEntry.setCPInstanceId(RandomTestUtil.nextLong());

		csDiagramEntry.setCProductId(RandomTestUtil.nextLong());

		csDiagramEntry.setDiagram(RandomTestUtil.randomBoolean());

		csDiagramEntry.setQuantity(RandomTestUtil.nextInt());

		csDiagramEntry.setSequence(RandomTestUtil.randomString());

		csDiagramEntry.setSku(RandomTestUtil.randomString());

		_csDiagramEntries.add(_persistence.update(csDiagramEntry));

		return csDiagramEntry;
	}

	private List<CSDiagramEntry> _csDiagramEntries =
		new ArrayList<CSDiagramEntry>();
	private CSDiagramEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}