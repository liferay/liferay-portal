/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.audiences.exception.NoSuchAudiencesEntryGroupRelException;
import com.liferay.audiences.model.AudiencesEntryGroupRel;
import com.liferay.audiences.service.AudiencesEntryGroupRelLocalServiceUtil;
import com.liferay.audiences.service.persistence.AudiencesEntryGroupRelPersistence;
import com.liferay.audiences.service.persistence.AudiencesEntryGroupRelUtil;
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
public class AudiencesEntryGroupRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.audiences.service"));

	@Before
	public void setUp() {
		_persistence = AudiencesEntryGroupRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AudiencesEntryGroupRel> iterator =
			_audiencesEntryGroupRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AudiencesEntryGroupRel audiencesEntryGroupRel = _persistence.create(pk);

		Assert.assertNotNull(audiencesEntryGroupRel);

		Assert.assertEquals(audiencesEntryGroupRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AudiencesEntryGroupRel newAudiencesEntryGroupRel =
			addAudiencesEntryGroupRel();

		_persistence.remove(newAudiencesEntryGroupRel);

		AudiencesEntryGroupRel existingAudiencesEntryGroupRel =
			_persistence.fetchByPrimaryKey(
				newAudiencesEntryGroupRel.getPrimaryKey());

		Assert.assertNull(existingAudiencesEntryGroupRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAudiencesEntryGroupRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		AudiencesEntryGroupRel newAudiencesEntryGroupRel =
			addAudiencesEntryGroupRel();

		newAudiencesEntryGroupRel.setCompanyId(RandomTestUtil.nextLong());

		newAudiencesEntryGroupRel.setUserId(RandomTestUtil.nextLong());

		newAudiencesEntryGroupRel.setUserName(RandomTestUtil.randomString());

		newAudiencesEntryGroupRel.setCreateDate(RandomTestUtil.nextDate());

		newAudiencesEntryGroupRel.setModifiedDate(RandomTestUtil.nextDate());

		newAudiencesEntryGroupRel.setAudienceEntryERC(
			RandomTestUtil.randomString());

		newAudiencesEntryGroupRel.setGroupERC(RandomTestUtil.randomString());

		newAudiencesEntryGroupRel = _persistence.update(
			newAudiencesEntryGroupRel);

		_audiencesEntryGroupRels.add(newAudiencesEntryGroupRel);

		AudiencesEntryGroupRel existingAudiencesEntryGroupRel =
			_persistence.findByPrimaryKey(
				newAudiencesEntryGroupRel.getPrimaryKey());

		Assert.assertEquals(
			existingAudiencesEntryGroupRel.getMvccVersion(),
			newAudiencesEntryGroupRel.getMvccVersion());
		Assert.assertEquals(
			existingAudiencesEntryGroupRel.getAudiencesEntryGroupRelId(),
			newAudiencesEntryGroupRel.getAudiencesEntryGroupRelId());
		Assert.assertEquals(
			existingAudiencesEntryGroupRel.getCompanyId(),
			newAudiencesEntryGroupRel.getCompanyId());
		Assert.assertEquals(
			existingAudiencesEntryGroupRel.getUserId(),
			newAudiencesEntryGroupRel.getUserId());
		Assert.assertEquals(
			existingAudiencesEntryGroupRel.getUserName(),
			newAudiencesEntryGroupRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAudiencesEntryGroupRel.getCreateDate()),
			Time.getShortTimestamp(newAudiencesEntryGroupRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAudiencesEntryGroupRel.getModifiedDate()),
			Time.getShortTimestamp(
				newAudiencesEntryGroupRel.getModifiedDate()));
		Assert.assertEquals(
			existingAudiencesEntryGroupRel.getAudienceEntryERC(),
			newAudiencesEntryGroupRel.getAudienceEntryERC());
		Assert.assertEquals(
			existingAudiencesEntryGroupRel.getGroupERC(),
			newAudiencesEntryGroupRel.getGroupERC());
	}

	@Test
	public void testCountByC_AEERC() throws Exception {
		_persistence.countByC_AEERC(RandomTestUtil.nextLong(), "");

		_persistence.countByC_AEERC(0L, "null");

		_persistence.countByC_AEERC(0L, (String)null);
	}

	@Test
	public void testCountByC_GERC() throws Exception {
		_persistence.countByC_GERC(RandomTestUtil.nextLong(), "");

		_persistence.countByC_GERC(0L, "null");

		_persistence.countByC_GERC(0L, (String)null);
	}

	@Test
	public void testCountByC_AEERC_GERC() throws Exception {
		_persistence.countByC_AEERC_GERC(RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_AEERC_GERC(0L, "null", "null");

		_persistence.countByC_AEERC_GERC(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AudiencesEntryGroupRel newAudiencesEntryGroupRel =
			addAudiencesEntryGroupRel();

		AudiencesEntryGroupRel existingAudiencesEntryGroupRel =
			_persistence.findByPrimaryKey(
				newAudiencesEntryGroupRel.getPrimaryKey());

		Assert.assertEquals(
			existingAudiencesEntryGroupRel, newAudiencesEntryGroupRel);
	}

	@Test(expected = NoSuchAudiencesEntryGroupRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AudiencesEntryGroupRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AudiencesEntryGroupRel", "mvccVersion", true,
			"audiencesEntryGroupRelId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"audienceEntryERC", true, "groupERC", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AudiencesEntryGroupRel newAudiencesEntryGroupRel =
			addAudiencesEntryGroupRel();

		AudiencesEntryGroupRel existingAudiencesEntryGroupRel =
			_persistence.fetchByPrimaryKey(
				newAudiencesEntryGroupRel.getPrimaryKey());

		Assert.assertEquals(
			existingAudiencesEntryGroupRel, newAudiencesEntryGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AudiencesEntryGroupRel missingAudiencesEntryGroupRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAudiencesEntryGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AudiencesEntryGroupRel newAudiencesEntryGroupRel1 =
			addAudiencesEntryGroupRel();
		AudiencesEntryGroupRel newAudiencesEntryGroupRel2 =
			addAudiencesEntryGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAudiencesEntryGroupRel1.getPrimaryKey());
		primaryKeys.add(newAudiencesEntryGroupRel2.getPrimaryKey());

		Map<Serializable, AudiencesEntryGroupRel> audiencesEntryGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, audiencesEntryGroupRels.size());
		Assert.assertEquals(
			newAudiencesEntryGroupRel1,
			audiencesEntryGroupRels.get(
				newAudiencesEntryGroupRel1.getPrimaryKey()));
		Assert.assertEquals(
			newAudiencesEntryGroupRel2,
			audiencesEntryGroupRels.get(
				newAudiencesEntryGroupRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AudiencesEntryGroupRel> audiencesEntryGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(audiencesEntryGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AudiencesEntryGroupRel newAudiencesEntryGroupRel =
			addAudiencesEntryGroupRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAudiencesEntryGroupRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AudiencesEntryGroupRel> audiencesEntryGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, audiencesEntryGroupRels.size());
		Assert.assertEquals(
			newAudiencesEntryGroupRel,
			audiencesEntryGroupRels.get(
				newAudiencesEntryGroupRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AudiencesEntryGroupRel> audiencesEntryGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(audiencesEntryGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AudiencesEntryGroupRel newAudiencesEntryGroupRel =
			addAudiencesEntryGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAudiencesEntryGroupRel.getPrimaryKey());

		Map<Serializable, AudiencesEntryGroupRel> audiencesEntryGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, audiencesEntryGroupRels.size());
		Assert.assertEquals(
			newAudiencesEntryGroupRel,
			audiencesEntryGroupRels.get(
				newAudiencesEntryGroupRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			AudiencesEntryGroupRelLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<AudiencesEntryGroupRel>() {

				@Override
				public void performAction(
					AudiencesEntryGroupRel audiencesEntryGroupRel) {

					Assert.assertNotNull(audiencesEntryGroupRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		AudiencesEntryGroupRel newAudiencesEntryGroupRel =
			addAudiencesEntryGroupRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AudiencesEntryGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"audiencesEntryGroupRelId",
				newAudiencesEntryGroupRel.getAudiencesEntryGroupRelId()));

		List<AudiencesEntryGroupRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		AudiencesEntryGroupRel existingAudiencesEntryGroupRel = result.get(0);

		Assert.assertEquals(
			existingAudiencesEntryGroupRel, newAudiencesEntryGroupRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AudiencesEntryGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"audiencesEntryGroupRelId", RandomTestUtil.nextLong()));

		List<AudiencesEntryGroupRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		AudiencesEntryGroupRel newAudiencesEntryGroupRel =
			addAudiencesEntryGroupRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AudiencesEntryGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("audiencesEntryGroupRelId"));

		Object newAudiencesEntryGroupRelId =
			newAudiencesEntryGroupRel.getAudiencesEntryGroupRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"audiencesEntryGroupRelId",
				new Object[] {newAudiencesEntryGroupRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingAudiencesEntryGroupRelId = result.get(0);

		Assert.assertEquals(
			existingAudiencesEntryGroupRelId, newAudiencesEntryGroupRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AudiencesEntryGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("audiencesEntryGroupRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"audiencesEntryGroupRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AudiencesEntryGroupRel newAudiencesEntryGroupRel =
			addAudiencesEntryGroupRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newAudiencesEntryGroupRel.getPrimaryKey()));
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

		AudiencesEntryGroupRel newAudiencesEntryGroupRel =
			addAudiencesEntryGroupRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AudiencesEntryGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"audiencesEntryGroupRelId",
				newAudiencesEntryGroupRel.getAudiencesEntryGroupRelId()));

		List<AudiencesEntryGroupRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		AudiencesEntryGroupRel audiencesEntryGroupRel) {

		Assert.assertEquals(
			Long.valueOf(audiencesEntryGroupRel.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				audiencesEntryGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			audiencesEntryGroupRel.getAudienceEntryERC(),
			ReflectionTestUtil.invoke(
				audiencesEntryGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "audienceEntryERC"));
		Assert.assertEquals(
			audiencesEntryGroupRel.getGroupERC(),
			ReflectionTestUtil.invoke(
				audiencesEntryGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupERC"));
	}

	protected AudiencesEntryGroupRel addAudiencesEntryGroupRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		AudiencesEntryGroupRel audiencesEntryGroupRel = _persistence.create(pk);

		audiencesEntryGroupRel.setCompanyId(RandomTestUtil.nextLong());

		audiencesEntryGroupRel.setUserId(RandomTestUtil.nextLong());

		audiencesEntryGroupRel.setUserName(RandomTestUtil.randomString());

		audiencesEntryGroupRel.setCreateDate(RandomTestUtil.nextDate());

		audiencesEntryGroupRel.setModifiedDate(RandomTestUtil.nextDate());

		audiencesEntryGroupRel.setAudienceEntryERC(
			RandomTestUtil.randomString());

		audiencesEntryGroupRel.setGroupERC(RandomTestUtil.randomString());

		_audiencesEntryGroupRels.add(
			_persistence.update(audiencesEntryGroupRel));

		return audiencesEntryGroupRel;
	}

	private List<AudiencesEntryGroupRel> _audiencesEntryGroupRels =
		new ArrayList<AudiencesEntryGroupRel>();
	private AudiencesEntryGroupRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:665566700