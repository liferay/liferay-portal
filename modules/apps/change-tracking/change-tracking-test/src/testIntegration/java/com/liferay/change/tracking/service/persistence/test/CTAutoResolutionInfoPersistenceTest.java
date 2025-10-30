/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.exception.NoSuchAutoResolutionInfoException;
import com.liferay.change.tracking.model.CTAutoResolutionInfo;
import com.liferay.change.tracking.service.persistence.CTAutoResolutionInfoPersistence;
import com.liferay.change.tracking.service.persistence.CTAutoResolutionInfoUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class CTAutoResolutionInfoPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.change.tracking.service"));

	@Before
	public void setUp() {
		_persistence = CTAutoResolutionInfoUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTAutoResolutionInfo> iterator =
			_ctAutoResolutionInfos.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTAutoResolutionInfo ctAutoResolutionInfo = _persistence.create(pk);

		Assert.assertNotNull(ctAutoResolutionInfo);

		Assert.assertEquals(ctAutoResolutionInfo.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTAutoResolutionInfo newCTAutoResolutionInfo =
			addCTAutoResolutionInfo();

		_persistence.remove(newCTAutoResolutionInfo);

		CTAutoResolutionInfo existingCTAutoResolutionInfo =
			_persistence.fetchByPrimaryKey(
				newCTAutoResolutionInfo.getPrimaryKey());

		Assert.assertNull(existingCTAutoResolutionInfo);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTAutoResolutionInfo();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTAutoResolutionInfo newCTAutoResolutionInfo = _persistence.create(pk);

		newCTAutoResolutionInfo.setMvccVersion(RandomTestUtil.nextLong());

		newCTAutoResolutionInfo.setCompanyId(RandomTestUtil.nextLong());

		newCTAutoResolutionInfo.setCreateDate(RandomTestUtil.nextDate());

		newCTAutoResolutionInfo.setCtCollectionId(RandomTestUtil.nextLong());

		newCTAutoResolutionInfo.setModelClassNameId(RandomTestUtil.nextLong());

		newCTAutoResolutionInfo.setSourceModelClassPK(
			RandomTestUtil.nextLong());

		newCTAutoResolutionInfo.setTargetModelClassPK(
			RandomTestUtil.nextLong());

		newCTAutoResolutionInfo.setConflictIdentifier(
			RandomTestUtil.randomString());

		_ctAutoResolutionInfos.add(
			_persistence.update(newCTAutoResolutionInfo));

		CTAutoResolutionInfo existingCTAutoResolutionInfo =
			_persistence.findByPrimaryKey(
				newCTAutoResolutionInfo.getPrimaryKey());

		Assert.assertEquals(
			existingCTAutoResolutionInfo.getMvccVersion(),
			newCTAutoResolutionInfo.getMvccVersion());
		Assert.assertEquals(
			existingCTAutoResolutionInfo.getCtAutoResolutionInfoId(),
			newCTAutoResolutionInfo.getCtAutoResolutionInfoId());
		Assert.assertEquals(
			existingCTAutoResolutionInfo.getCompanyId(),
			newCTAutoResolutionInfo.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCTAutoResolutionInfo.getCreateDate()),
			Time.getShortTimestamp(newCTAutoResolutionInfo.getCreateDate()));
		Assert.assertEquals(
			existingCTAutoResolutionInfo.getCtCollectionId(),
			newCTAutoResolutionInfo.getCtCollectionId());
		Assert.assertEquals(
			existingCTAutoResolutionInfo.getModelClassNameId(),
			newCTAutoResolutionInfo.getModelClassNameId());
		Assert.assertEquals(
			existingCTAutoResolutionInfo.getSourceModelClassPK(),
			newCTAutoResolutionInfo.getSourceModelClassPK());
		Assert.assertEquals(
			existingCTAutoResolutionInfo.getTargetModelClassPK(),
			newCTAutoResolutionInfo.getTargetModelClassPK());
		Assert.assertEquals(
			existingCTAutoResolutionInfo.getConflictIdentifier(),
			newCTAutoResolutionInfo.getConflictIdentifier());
	}

	@Test
	public void testCountByCtCollectionId() throws Exception {
		_persistence.countByCtCollectionId(RandomTestUtil.nextLong());

		_persistence.countByCtCollectionId(0L);
	}

	@Test
	public void testCountByC_MCNI_SMCPK() throws Exception {
		_persistence.countByC_MCNI_SMCPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_MCNI_SMCPK(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_MCNI_SMCPKArrayable() throws Exception {
		_persistence.countByC_MCNI_SMCPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTAutoResolutionInfo newCTAutoResolutionInfo =
			addCTAutoResolutionInfo();

		CTAutoResolutionInfo existingCTAutoResolutionInfo =
			_persistence.findByPrimaryKey(
				newCTAutoResolutionInfo.getPrimaryKey());

		Assert.assertEquals(
			existingCTAutoResolutionInfo, newCTAutoResolutionInfo);
	}

	@Test(expected = NoSuchAutoResolutionInfoException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTAutoResolutionInfo> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTAutoResolutionInfo", "mvccVersion", true,
			"ctAutoResolutionInfoId", true, "companyId", true, "createDate",
			true, "ctCollectionId", true, "modelClassNameId", true,
			"sourceModelClassPK", true, "targetModelClassPK", true,
			"conflictIdentifier", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTAutoResolutionInfo newCTAutoResolutionInfo =
			addCTAutoResolutionInfo();

		CTAutoResolutionInfo existingCTAutoResolutionInfo =
			_persistence.fetchByPrimaryKey(
				newCTAutoResolutionInfo.getPrimaryKey());

		Assert.assertEquals(
			existingCTAutoResolutionInfo, newCTAutoResolutionInfo);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTAutoResolutionInfo missingCTAutoResolutionInfo =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTAutoResolutionInfo);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTAutoResolutionInfo newCTAutoResolutionInfo1 =
			addCTAutoResolutionInfo();
		CTAutoResolutionInfo newCTAutoResolutionInfo2 =
			addCTAutoResolutionInfo();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTAutoResolutionInfo1.getPrimaryKey());
		primaryKeys.add(newCTAutoResolutionInfo2.getPrimaryKey());

		Map<Serializable, CTAutoResolutionInfo> ctAutoResolutionInfos =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ctAutoResolutionInfos.size());
		Assert.assertEquals(
			newCTAutoResolutionInfo1,
			ctAutoResolutionInfos.get(
				newCTAutoResolutionInfo1.getPrimaryKey()));
		Assert.assertEquals(
			newCTAutoResolutionInfo2,
			ctAutoResolutionInfos.get(
				newCTAutoResolutionInfo2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTAutoResolutionInfo> ctAutoResolutionInfos =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctAutoResolutionInfos.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTAutoResolutionInfo newCTAutoResolutionInfo =
			addCTAutoResolutionInfo();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTAutoResolutionInfo.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTAutoResolutionInfo> ctAutoResolutionInfos =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctAutoResolutionInfos.size());
		Assert.assertEquals(
			newCTAutoResolutionInfo,
			ctAutoResolutionInfos.get(newCTAutoResolutionInfo.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTAutoResolutionInfo> ctAutoResolutionInfos =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctAutoResolutionInfos.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTAutoResolutionInfo newCTAutoResolutionInfo =
			addCTAutoResolutionInfo();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTAutoResolutionInfo.getPrimaryKey());

		Map<Serializable, CTAutoResolutionInfo> ctAutoResolutionInfos =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctAutoResolutionInfos.size());
		Assert.assertEquals(
			newCTAutoResolutionInfo,
			ctAutoResolutionInfos.get(newCTAutoResolutionInfo.getPrimaryKey()));
	}

	protected CTAutoResolutionInfo addCTAutoResolutionInfo() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTAutoResolutionInfo ctAutoResolutionInfo = _persistence.create(pk);

		ctAutoResolutionInfo.setMvccVersion(RandomTestUtil.nextLong());

		ctAutoResolutionInfo.setCompanyId(RandomTestUtil.nextLong());

		ctAutoResolutionInfo.setCreateDate(RandomTestUtil.nextDate());

		ctAutoResolutionInfo.setCtCollectionId(RandomTestUtil.nextLong());

		ctAutoResolutionInfo.setModelClassNameId(RandomTestUtil.nextLong());

		ctAutoResolutionInfo.setSourceModelClassPK(RandomTestUtil.nextLong());

		ctAutoResolutionInfo.setTargetModelClassPK(RandomTestUtil.nextLong());

		ctAutoResolutionInfo.setConflictIdentifier(
			RandomTestUtil.randomString());

		_ctAutoResolutionInfos.add(_persistence.update(ctAutoResolutionInfo));

		return ctAutoResolutionInfo;
	}

	private List<CTAutoResolutionInfo> _ctAutoResolutionInfos =
		new ArrayList<CTAutoResolutionInfo>();
	private CTAutoResolutionInfoPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}