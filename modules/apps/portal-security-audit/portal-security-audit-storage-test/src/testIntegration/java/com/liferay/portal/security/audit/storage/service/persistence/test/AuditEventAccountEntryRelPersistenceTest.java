/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.audit.storage.exception.NoSuchEventAccountEntryRelException;
import com.liferay.portal.security.audit.storage.model.AuditEventAccountEntryRel;
import com.liferay.portal.security.audit.storage.service.persistence.AuditEventAccountEntryRelPersistence;
import com.liferay.portal.security.audit.storage.service.persistence.AuditEventAccountEntryRelUtil;
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
public class AuditEventAccountEntryRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.security.audit.storage.service"));

	@Before
	public void setUp() {
		_persistence = AuditEventAccountEntryRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AuditEventAccountEntryRel> iterator =
			_auditEventAccountEntryRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AuditEventAccountEntryRel auditEventAccountEntryRel =
			_persistence.create(pk);

		Assert.assertNotNull(auditEventAccountEntryRel);

		Assert.assertEquals(auditEventAccountEntryRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AuditEventAccountEntryRel newAuditEventAccountEntryRel =
			addAuditEventAccountEntryRel();

		_persistence.remove(newAuditEventAccountEntryRel);

		AuditEventAccountEntryRel existingAuditEventAccountEntryRel =
			_persistence.fetchByPrimaryKey(
				newAuditEventAccountEntryRel.getPrimaryKey());

		Assert.assertNull(existingAuditEventAccountEntryRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAuditEventAccountEntryRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		AuditEventAccountEntryRel newAuditEventAccountEntryRel =
			addAuditEventAccountEntryRel();

		newAuditEventAccountEntryRel.setCompanyId(RandomTestUtil.nextLong());

		newAuditEventAccountEntryRel.setCreateDate(RandomTestUtil.nextDate());

		newAuditEventAccountEntryRel.setAccountEntryId(
			RandomTestUtil.nextLong());

		newAuditEventAccountEntryRel.setAuditEventId(RandomTestUtil.nextLong());

		newAuditEventAccountEntryRel = _persistence.update(
			newAuditEventAccountEntryRel);

		_auditEventAccountEntryRels.add(newAuditEventAccountEntryRel);

		AuditEventAccountEntryRel existingAuditEventAccountEntryRel =
			_persistence.findByPrimaryKey(
				newAuditEventAccountEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAuditEventAccountEntryRel.getAuditEventAccountEntryRelId(),
			newAuditEventAccountEntryRel.getAuditEventAccountEntryRelId());
		Assert.assertEquals(
			existingAuditEventAccountEntryRel.getCompanyId(),
			newAuditEventAccountEntryRel.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAuditEventAccountEntryRel.getCreateDate()),
			Time.getShortTimestamp(
				newAuditEventAccountEntryRel.getCreateDate()));
		Assert.assertEquals(
			existingAuditEventAccountEntryRel.getAccountEntryId(),
			newAuditEventAccountEntryRel.getAccountEntryId());
		Assert.assertEquals(
			existingAuditEventAccountEntryRel.getAuditEventId(),
			newAuditEventAccountEntryRel.getAuditEventId());
	}

	@Test
	public void testCountByAuditEventId() throws Exception {
		_persistence.countByAuditEventId(RandomTestUtil.nextLong());

		_persistence.countByAuditEventId(0L);
	}

	@Test
	public void testCountByC_CD_AEI() throws Exception {
		_persistence.countByC_CD_AEI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDate(),
			RandomTestUtil.nextLong());

		_persistence.countByC_CD_AEI(0L, RandomTestUtil.nextDate(), 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AuditEventAccountEntryRel newAuditEventAccountEntryRel =
			addAuditEventAccountEntryRel();

		AuditEventAccountEntryRel existingAuditEventAccountEntryRel =
			_persistence.findByPrimaryKey(
				newAuditEventAccountEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAuditEventAccountEntryRel, newAuditEventAccountEntryRel);
	}

	@Test(expected = NoSuchEventAccountEntryRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AuditEventAccountEntryRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"Audit_EventAccountEntryRel", "auditEventAccountEntryRelId", true,
			"companyId", true, "createDate", true, "accountEntryId", true,
			"auditEventId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AuditEventAccountEntryRel newAuditEventAccountEntryRel =
			addAuditEventAccountEntryRel();

		AuditEventAccountEntryRel existingAuditEventAccountEntryRel =
			_persistence.fetchByPrimaryKey(
				newAuditEventAccountEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAuditEventAccountEntryRel, newAuditEventAccountEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AuditEventAccountEntryRel missingAuditEventAccountEntryRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAuditEventAccountEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AuditEventAccountEntryRel newAuditEventAccountEntryRel1 =
			addAuditEventAccountEntryRel();
		AuditEventAccountEntryRel newAuditEventAccountEntryRel2 =
			addAuditEventAccountEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAuditEventAccountEntryRel1.getPrimaryKey());
		primaryKeys.add(newAuditEventAccountEntryRel2.getPrimaryKey());

		Map<Serializable, AuditEventAccountEntryRel>
			auditEventAccountEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, auditEventAccountEntryRels.size());
		Assert.assertEquals(
			newAuditEventAccountEntryRel1,
			auditEventAccountEntryRels.get(
				newAuditEventAccountEntryRel1.getPrimaryKey()));
		Assert.assertEquals(
			newAuditEventAccountEntryRel2,
			auditEventAccountEntryRels.get(
				newAuditEventAccountEntryRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AuditEventAccountEntryRel>
			auditEventAccountEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(auditEventAccountEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AuditEventAccountEntryRel newAuditEventAccountEntryRel =
			addAuditEventAccountEntryRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAuditEventAccountEntryRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AuditEventAccountEntryRel>
			auditEventAccountEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, auditEventAccountEntryRels.size());
		Assert.assertEquals(
			newAuditEventAccountEntryRel,
			auditEventAccountEntryRels.get(
				newAuditEventAccountEntryRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AuditEventAccountEntryRel>
			auditEventAccountEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(auditEventAccountEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AuditEventAccountEntryRel newAuditEventAccountEntryRel =
			addAuditEventAccountEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAuditEventAccountEntryRel.getPrimaryKey());

		Map<Serializable, AuditEventAccountEntryRel>
			auditEventAccountEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, auditEventAccountEntryRels.size());
		Assert.assertEquals(
			newAuditEventAccountEntryRel,
			auditEventAccountEntryRels.get(
				newAuditEventAccountEntryRel.getPrimaryKey()));
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		AuditEventAccountEntryRel newAuditEventAccountEntryRel =
			addAuditEventAccountEntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AuditEventAccountEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"auditEventAccountEntryRelId",
				newAuditEventAccountEntryRel.getAuditEventAccountEntryRelId()));

		List<AuditEventAccountEntryRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		AuditEventAccountEntryRel existingAuditEventAccountEntryRel =
			result.get(0);

		Assert.assertEquals(
			existingAuditEventAccountEntryRel, newAuditEventAccountEntryRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AuditEventAccountEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"auditEventAccountEntryRelId", RandomTestUtil.nextLong()));

		List<AuditEventAccountEntryRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		AuditEventAccountEntryRel newAuditEventAccountEntryRel =
			addAuditEventAccountEntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AuditEventAccountEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("auditEventAccountEntryRelId"));

		Object newAuditEventAccountEntryRelId =
			newAuditEventAccountEntryRel.getAuditEventAccountEntryRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"auditEventAccountEntryRelId",
				new Object[] {newAuditEventAccountEntryRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingAuditEventAccountEntryRelId = result.get(0);

		Assert.assertEquals(
			existingAuditEventAccountEntryRelId,
			newAuditEventAccountEntryRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AuditEventAccountEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("auditEventAccountEntryRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"auditEventAccountEntryRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected AuditEventAccountEntryRel addAuditEventAccountEntryRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		AuditEventAccountEntryRel auditEventAccountEntryRel =
			_persistence.create(pk);

		auditEventAccountEntryRel.setCompanyId(RandomTestUtil.nextLong());

		auditEventAccountEntryRel.setCreateDate(RandomTestUtil.nextDate());

		auditEventAccountEntryRel.setAccountEntryId(RandomTestUtil.nextLong());

		auditEventAccountEntryRel.setAuditEventId(RandomTestUtil.nextLong());

		_auditEventAccountEntryRels.add(
			_persistence.update(auditEventAccountEntryRel));

		return auditEventAccountEntryRel;
	}

	private List<AuditEventAccountEntryRel> _auditEventAccountEntryRels =
		new ArrayList<AuditEventAccountEntryRel>();
	private AuditEventAccountEntryRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:139812402