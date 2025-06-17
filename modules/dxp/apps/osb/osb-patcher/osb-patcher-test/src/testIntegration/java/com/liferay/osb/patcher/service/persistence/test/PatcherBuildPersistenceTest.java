/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.patcher.exception.NoSuchPatcherBuildException;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.persistence.PatcherBuildPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherBuildUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class PatcherBuildPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.osb.patcher.service"));

	@Before
	public void setUp() {
		_persistence = PatcherBuildUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PatcherBuild> iterator = _patcherBuilds.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherBuild patcherBuild = _persistence.create(pk);

		Assert.assertNotNull(patcherBuild);

		Assert.assertEquals(patcherBuild.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PatcherBuild newPatcherBuild = addPatcherBuild();

		_persistence.remove(newPatcherBuild);

		PatcherBuild existingPatcherBuild = _persistence.fetchByPrimaryKey(
			newPatcherBuild.getPrimaryKey());

		Assert.assertNull(existingPatcherBuild);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPatcherBuild();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherBuild newPatcherBuild = _persistence.create(pk);

		newPatcherBuild.setMvccVersion(RandomTestUtil.nextLong());

		newPatcherBuild.setCompanyId(RandomTestUtil.nextLong());

		newPatcherBuild.setUserId(RandomTestUtil.nextLong());

		newPatcherBuild.setUserName(RandomTestUtil.randomString());

		newPatcherBuild.setCreateDate(RandomTestUtil.nextDate());

		newPatcherBuild.setModifiedDate(RandomTestUtil.nextDate());

		newPatcherBuild.setHotfixId(RandomTestUtil.nextLong());

		newPatcherBuild.setPatcherAccountId(RandomTestUtil.nextLong());

		newPatcherBuild.setPatcherFixId(RandomTestUtil.nextLong());

		newPatcherBuild.setPatcherProductVersionId(RandomTestUtil.nextLong());

		newPatcherBuild.setPatcherProjectVersionId(RandomTestUtil.nextLong());

		newPatcherBuild.setTicketEntryId(RandomTestUtil.nextLong());

		newPatcherBuild.setAccountEntryCode(RandomTestUtil.randomString());

		newPatcherBuild.setChildBuild(RandomTestUtil.randomBoolean());

		newPatcherBuild.setComments(RandomTestUtil.randomString());

		newPatcherBuild.setFileName(RandomTestUtil.randomString());

		newPatcherBuild.setInitialName(RandomTestUtil.randomString());

		newPatcherBuild.setKey(RandomTestUtil.randomString());

		newPatcherBuild.setKeyVersion(RandomTestUtil.nextDouble());

		newPatcherBuild.setLatestBuild(RandomTestUtil.randomBoolean());

		newPatcherBuild.setLatestKeyBuild(RandomTestUtil.randomBoolean());

		newPatcherBuild.setLatestLESATicketBuild(
			RandomTestUtil.randomBoolean());

		newPatcherBuild.setLatestSupportTicketBuild(
			RandomTestUtil.randomBoolean());

		newPatcherBuild.setLesaTicket(RandomTestUtil.randomString());

		newPatcherBuild.setLesaTicketVersion(RandomTestUtil.nextDouble());

		newPatcherBuild.setName(RandomTestUtil.randomString());

		newPatcherBuild.setNotified(RandomTestUtil.randomBoolean());

		newPatcherBuild.setProductVersion(RandomTestUtil.nextInt());

		newPatcherBuild.setQaComments(RandomTestUtil.randomString());

		newPatcherBuild.setQaStatus(RandomTestUtil.nextInt());

		newPatcherBuild.setRequestKey(RandomTestUtil.randomString());

		newPatcherBuild.setSourceName(RandomTestUtil.randomString());

		newPatcherBuild.setSupportTicket(RandomTestUtil.randomString());

		newPatcherBuild.setSupportTicketVersion(RandomTestUtil.nextDouble());

		newPatcherBuild.setType(RandomTestUtil.nextInt());

		newPatcherBuild.setStatus(RandomTestUtil.nextInt());

		newPatcherBuild.setStatusByUserId(RandomTestUtil.nextLong());

		newPatcherBuild.setStatusByUserName(RandomTestUtil.randomString());

		newPatcherBuild.setStatusDate(RandomTestUtil.nextDate());

		_patcherBuilds.add(_persistence.update(newPatcherBuild));

		PatcherBuild existingPatcherBuild = _persistence.findByPrimaryKey(
			newPatcherBuild.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherBuild.getMvccVersion(),
			newPatcherBuild.getMvccVersion());
		Assert.assertEquals(
			existingPatcherBuild.getPatcherBuildId(),
			newPatcherBuild.getPatcherBuildId());
		Assert.assertEquals(
			existingPatcherBuild.getCompanyId(),
			newPatcherBuild.getCompanyId());
		Assert.assertEquals(
			existingPatcherBuild.getUserId(), newPatcherBuild.getUserId());
		Assert.assertEquals(
			existingPatcherBuild.getUserName(), newPatcherBuild.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherBuild.getCreateDate()),
			Time.getShortTimestamp(newPatcherBuild.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherBuild.getModifiedDate()),
			Time.getShortTimestamp(newPatcherBuild.getModifiedDate()));
		Assert.assertEquals(
			existingPatcherBuild.getHotfixId(), newPatcherBuild.getHotfixId());
		Assert.assertEquals(
			existingPatcherBuild.getPatcherAccountId(),
			newPatcherBuild.getPatcherAccountId());
		Assert.assertEquals(
			existingPatcherBuild.getPatcherFixId(),
			newPatcherBuild.getPatcherFixId());
		Assert.assertEquals(
			existingPatcherBuild.getPatcherProductVersionId(),
			newPatcherBuild.getPatcherProductVersionId());
		Assert.assertEquals(
			existingPatcherBuild.getPatcherProjectVersionId(),
			newPatcherBuild.getPatcherProjectVersionId());
		Assert.assertEquals(
			existingPatcherBuild.getTicketEntryId(),
			newPatcherBuild.getTicketEntryId());
		Assert.assertEquals(
			existingPatcherBuild.getAccountEntryCode(),
			newPatcherBuild.getAccountEntryCode());
		Assert.assertEquals(
			existingPatcherBuild.isChildBuild(),
			newPatcherBuild.isChildBuild());
		Assert.assertEquals(
			existingPatcherBuild.getComments(), newPatcherBuild.getComments());
		Assert.assertEquals(
			existingPatcherBuild.getFileName(), newPatcherBuild.getFileName());
		Assert.assertEquals(
			existingPatcherBuild.getInitialName(),
			newPatcherBuild.getInitialName());
		Assert.assertEquals(
			existingPatcherBuild.getKey(), newPatcherBuild.getKey());
		AssertUtils.assertEquals(
			existingPatcherBuild.getKeyVersion(),
			newPatcherBuild.getKeyVersion());
		Assert.assertEquals(
			existingPatcherBuild.isLatestBuild(),
			newPatcherBuild.isLatestBuild());
		Assert.assertEquals(
			existingPatcherBuild.isLatestKeyBuild(),
			newPatcherBuild.isLatestKeyBuild());
		Assert.assertEquals(
			existingPatcherBuild.isLatestLESATicketBuild(),
			newPatcherBuild.isLatestLESATicketBuild());
		Assert.assertEquals(
			existingPatcherBuild.isLatestSupportTicketBuild(),
			newPatcherBuild.isLatestSupportTicketBuild());
		Assert.assertEquals(
			existingPatcherBuild.getLesaTicket(),
			newPatcherBuild.getLesaTicket());
		AssertUtils.assertEquals(
			existingPatcherBuild.getLesaTicketVersion(),
			newPatcherBuild.getLesaTicketVersion());
		Assert.assertEquals(
			existingPatcherBuild.getName(), newPatcherBuild.getName());
		Assert.assertEquals(
			existingPatcherBuild.isNotified(), newPatcherBuild.isNotified());
		Assert.assertEquals(
			existingPatcherBuild.getProductVersion(),
			newPatcherBuild.getProductVersion());
		Assert.assertEquals(
			existingPatcherBuild.getQaComments(),
			newPatcherBuild.getQaComments());
		Assert.assertEquals(
			existingPatcherBuild.getQaStatus(), newPatcherBuild.getQaStatus());
		Assert.assertEquals(
			existingPatcherBuild.getRequestKey(),
			newPatcherBuild.getRequestKey());
		Assert.assertEquals(
			existingPatcherBuild.getSourceName(),
			newPatcherBuild.getSourceName());
		Assert.assertEquals(
			existingPatcherBuild.getSupportTicket(),
			newPatcherBuild.getSupportTicket());
		AssertUtils.assertEquals(
			existingPatcherBuild.getSupportTicketVersion(),
			newPatcherBuild.getSupportTicketVersion());
		Assert.assertEquals(
			existingPatcherBuild.getType(), newPatcherBuild.getType());
		Assert.assertEquals(
			existingPatcherBuild.getStatus(), newPatcherBuild.getStatus());
		Assert.assertEquals(
			existingPatcherBuild.getStatusByUserId(),
			newPatcherBuild.getStatusByUserId());
		Assert.assertEquals(
			existingPatcherBuild.getStatusByUserName(),
			newPatcherBuild.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherBuild.getStatusDate()),
			Time.getShortTimestamp(newPatcherBuild.getStatusDate()));
	}

	@Test
	public void testCountByK_KV() throws Exception {
		_persistence.countByK_KV("", RandomTestUtil.nextDouble());

		_persistence.countByK_KV("null", 0D);

		_persistence.countByK_KV((String)null, 0D);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PatcherBuild newPatcherBuild = addPatcherBuild();

		PatcherBuild existingPatcherBuild = _persistence.findByPrimaryKey(
			newPatcherBuild.getPrimaryKey());

		Assert.assertEquals(existingPatcherBuild, newPatcherBuild);
	}

	@Test(expected = NoSuchPatcherBuildException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PatcherBuild> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"OSBPatcher_PatcherBuild", "mvccVersion", true, "patcherBuildId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "hotfixId", true,
			"patcherAccountId", true, "patcherFixId", true,
			"patcherProductVersionId", true, "patcherProjectVersionId", true,
			"ticketEntryId", true, "accountEntryCode", true, "childBuild", true,
			"fileName", true, "initialName", true, "key", true, "keyVersion",
			true, "latestBuild", true, "latestKeyBuild", true,
			"latestLESATicketBuild", true, "latestSupportTicketBuild", true,
			"lesaTicket", true, "lesaTicketVersion", true, "notified", true,
			"productVersion", true, "qaStatus", true, "requestKey", true,
			"sourceName", true, "supportTicket", true, "supportTicketVersion",
			true, "type", true, "status", true, "statusByUserId", true,
			"statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PatcherBuild newPatcherBuild = addPatcherBuild();

		PatcherBuild existingPatcherBuild = _persistence.fetchByPrimaryKey(
			newPatcherBuild.getPrimaryKey());

		Assert.assertEquals(existingPatcherBuild, newPatcherBuild);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherBuild missingPatcherBuild = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPatcherBuild);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PatcherBuild newPatcherBuild1 = addPatcherBuild();
		PatcherBuild newPatcherBuild2 = addPatcherBuild();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherBuild1.getPrimaryKey());
		primaryKeys.add(newPatcherBuild2.getPrimaryKey());

		Map<Serializable, PatcherBuild> patcherBuilds =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, patcherBuilds.size());
		Assert.assertEquals(
			newPatcherBuild1,
			patcherBuilds.get(newPatcherBuild1.getPrimaryKey()));
		Assert.assertEquals(
			newPatcherBuild2,
			patcherBuilds.get(newPatcherBuild2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PatcherBuild> patcherBuilds =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherBuilds.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PatcherBuild newPatcherBuild = addPatcherBuild();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherBuild.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PatcherBuild> patcherBuilds =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherBuilds.size());
		Assert.assertEquals(
			newPatcherBuild,
			patcherBuilds.get(newPatcherBuild.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PatcherBuild> patcherBuilds =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherBuilds.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PatcherBuild newPatcherBuild = addPatcherBuild();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherBuild.getPrimaryKey());

		Map<Serializable, PatcherBuild> patcherBuilds =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherBuilds.size());
		Assert.assertEquals(
			newPatcherBuild,
			patcherBuilds.get(newPatcherBuild.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			PatcherBuildLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<PatcherBuild>() {

				@Override
				public void performAction(PatcherBuild patcherBuild) {
					Assert.assertNotNull(patcherBuild);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		PatcherBuild newPatcherBuild = addPatcherBuild();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherBuild.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherBuildId", newPatcherBuild.getPatcherBuildId()));

		List<PatcherBuild> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		PatcherBuild existingPatcherBuild = result.get(0);

		Assert.assertEquals(existingPatcherBuild, newPatcherBuild);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherBuild.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherBuildId", RandomTestUtil.nextLong()));

		List<PatcherBuild> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		PatcherBuild newPatcherBuild = addPatcherBuild();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherBuild.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherBuildId"));

		Object newPatcherBuildId = newPatcherBuild.getPatcherBuildId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherBuildId", new Object[] {newPatcherBuildId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPatcherBuildId = result.get(0);

		Assert.assertEquals(existingPatcherBuildId, newPatcherBuildId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherBuild.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherBuildId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherBuildId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		PatcherBuild newPatcherBuild = addPatcherBuild();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newPatcherBuild.getPrimaryKey()));
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

		PatcherBuild newPatcherBuild = addPatcherBuild();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherBuild.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherBuildId", newPatcherBuild.getPatcherBuildId()));

		List<PatcherBuild> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(PatcherBuild patcherBuild) {
		Assert.assertEquals(
			patcherBuild.getKey(),
			ReflectionTestUtil.invoke(
				patcherBuild, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));
		AssertUtils.assertEquals(
			patcherBuild.getKeyVersion(),
			ReflectionTestUtil.<Double>invoke(
				patcherBuild, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "keyVersion"));
	}

	protected PatcherBuild addPatcherBuild() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherBuild patcherBuild = _persistence.create(pk);

		patcherBuild.setMvccVersion(RandomTestUtil.nextLong());

		patcherBuild.setCompanyId(RandomTestUtil.nextLong());

		patcherBuild.setUserId(RandomTestUtil.nextLong());

		patcherBuild.setUserName(RandomTestUtil.randomString());

		patcherBuild.setCreateDate(RandomTestUtil.nextDate());

		patcherBuild.setModifiedDate(RandomTestUtil.nextDate());

		patcherBuild.setHotfixId(RandomTestUtil.nextLong());

		patcherBuild.setPatcherAccountId(RandomTestUtil.nextLong());

		patcherBuild.setPatcherFixId(RandomTestUtil.nextLong());

		patcherBuild.setPatcherProductVersionId(RandomTestUtil.nextLong());

		patcherBuild.setPatcherProjectVersionId(RandomTestUtil.nextLong());

		patcherBuild.setTicketEntryId(RandomTestUtil.nextLong());

		patcherBuild.setAccountEntryCode(RandomTestUtil.randomString());

		patcherBuild.setChildBuild(RandomTestUtil.randomBoolean());

		patcherBuild.setComments(RandomTestUtil.randomString());

		patcherBuild.setFileName(RandomTestUtil.randomString());

		patcherBuild.setInitialName(RandomTestUtil.randomString());

		patcherBuild.setKey(RandomTestUtil.randomString());

		patcherBuild.setKeyVersion(RandomTestUtil.nextDouble());

		patcherBuild.setLatestBuild(RandomTestUtil.randomBoolean());

		patcherBuild.setLatestKeyBuild(RandomTestUtil.randomBoolean());

		patcherBuild.setLatestLESATicketBuild(RandomTestUtil.randomBoolean());

		patcherBuild.setLatestSupportTicketBuild(
			RandomTestUtil.randomBoolean());

		patcherBuild.setLesaTicket(RandomTestUtil.randomString());

		patcherBuild.setLesaTicketVersion(RandomTestUtil.nextDouble());

		patcherBuild.setName(RandomTestUtil.randomString());

		patcherBuild.setNotified(RandomTestUtil.randomBoolean());

		patcherBuild.setProductVersion(RandomTestUtil.nextInt());

		patcherBuild.setQaComments(RandomTestUtil.randomString());

		patcherBuild.setQaStatus(RandomTestUtil.nextInt());

		patcherBuild.setRequestKey(RandomTestUtil.randomString());

		patcherBuild.setSourceName(RandomTestUtil.randomString());

		patcherBuild.setSupportTicket(RandomTestUtil.randomString());

		patcherBuild.setSupportTicketVersion(RandomTestUtil.nextDouble());

		patcherBuild.setType(RandomTestUtil.nextInt());

		patcherBuild.setStatus(RandomTestUtil.nextInt());

		patcherBuild.setStatusByUserId(RandomTestUtil.nextLong());

		patcherBuild.setStatusByUserName(RandomTestUtil.randomString());

		patcherBuild.setStatusDate(RandomTestUtil.nextDate());

		_patcherBuilds.add(_persistence.update(patcherBuild));

		return patcherBuild;
	}

	private List<PatcherBuild> _patcherBuilds = new ArrayList<PatcherBuild>();
	private PatcherBuildPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}