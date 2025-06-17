/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.patcher.exception.NoSuchPatcherFixException;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.persistence.PatcherFixPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class PatcherFixPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.osb.patcher.service"));

	@Before
	public void setUp() {
		_persistence = PatcherFixUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PatcherFix> iterator = _patcherFixes.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFix patcherFix = _persistence.create(pk);

		Assert.assertNotNull(patcherFix);

		Assert.assertEquals(patcherFix.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PatcherFix newPatcherFix = addPatcherFix();

		_persistence.remove(newPatcherFix);

		PatcherFix existingPatcherFix = _persistence.fetchByPrimaryKey(
			newPatcherFix.getPrimaryKey());

		Assert.assertNull(existingPatcherFix);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPatcherFix();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFix newPatcherFix = _persistence.create(pk);

		newPatcherFix.setMvccVersion(RandomTestUtil.nextLong());

		newPatcherFix.setCompanyId(RandomTestUtil.nextLong());

		newPatcherFix.setUserId(RandomTestUtil.nextLong());

		newPatcherFix.setUserName(RandomTestUtil.randomString());

		newPatcherFix.setCreateDate(RandomTestUtil.nextDate());

		newPatcherFix.setModifiedDate(RandomTestUtil.nextDate());

		newPatcherFix.setPatcherProductVersionId(RandomTestUtil.nextLong());

		newPatcherFix.setPatcherProjectVersionId(RandomTestUtil.nextLong());

		newPatcherFix.setComments(RandomTestUtil.randomString());

		newPatcherFix.setCommittish(RandomTestUtil.randomString());

		newPatcherFix.setDependencies(RandomTestUtil.randomString());

		newPatcherFix.setFixPackStatus(RandomTestUtil.nextInt());

		newPatcherFix.setGitHash(RandomTestUtil.randomString());

		newPatcherFix.setGitRemoteURL(RandomTestUtil.randomString());

		newPatcherFix.setJenkinsResults(RandomTestUtil.randomString());

		newPatcherFix.setKey(RandomTestUtil.randomString());

		newPatcherFix.setKeyVersion(RandomTestUtil.nextDouble());

		newPatcherFix.setLatestFix(RandomTestUtil.randomBoolean());

		newPatcherFix.setName(RandomTestUtil.randomString());

		newPatcherFix.setNotified(RandomTestUtil.randomBoolean());

		newPatcherFix.setObsolete(RandomTestUtil.randomBoolean());

		newPatcherFix.setProductVersion(RandomTestUtil.nextInt());

		newPatcherFix.setRequestKey(RandomTestUtil.randomString());

		newPatcherFix.setRequirements(RandomTestUtil.randomString());

		newPatcherFix.setType(RandomTestUtil.nextInt());

		newPatcherFix.setStatus(RandomTestUtil.nextInt());

		newPatcherFix.setStatusByUserId(RandomTestUtil.nextLong());

		newPatcherFix.setStatusByUserName(RandomTestUtil.randomString());

		newPatcherFix.setStatusDate(RandomTestUtil.nextDate());

		_patcherFixes.add(_persistence.update(newPatcherFix));

		PatcherFix existingPatcherFix = _persistence.findByPrimaryKey(
			newPatcherFix.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherFix.getMvccVersion(),
			newPatcherFix.getMvccVersion());
		Assert.assertEquals(
			existingPatcherFix.getPatcherFixId(),
			newPatcherFix.getPatcherFixId());
		Assert.assertEquals(
			existingPatcherFix.getCompanyId(), newPatcherFix.getCompanyId());
		Assert.assertEquals(
			existingPatcherFix.getUserId(), newPatcherFix.getUserId());
		Assert.assertEquals(
			existingPatcherFix.getUserName(), newPatcherFix.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherFix.getCreateDate()),
			Time.getShortTimestamp(newPatcherFix.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherFix.getModifiedDate()),
			Time.getShortTimestamp(newPatcherFix.getModifiedDate()));
		Assert.assertEquals(
			existingPatcherFix.getPatcherProductVersionId(),
			newPatcherFix.getPatcherProductVersionId());
		Assert.assertEquals(
			existingPatcherFix.getPatcherProjectVersionId(),
			newPatcherFix.getPatcherProjectVersionId());
		Assert.assertEquals(
			existingPatcherFix.getComments(), newPatcherFix.getComments());
		Assert.assertEquals(
			existingPatcherFix.getCommittish(), newPatcherFix.getCommittish());
		Assert.assertEquals(
			existingPatcherFix.getDependencies(),
			newPatcherFix.getDependencies());
		Assert.assertEquals(
			existingPatcherFix.getFixPackStatus(),
			newPatcherFix.getFixPackStatus());
		Assert.assertEquals(
			existingPatcherFix.getGitHash(), newPatcherFix.getGitHash());
		Assert.assertEquals(
			existingPatcherFix.getGitRemoteURL(),
			newPatcherFix.getGitRemoteURL());
		Assert.assertEquals(
			existingPatcherFix.getJenkinsResults(),
			newPatcherFix.getJenkinsResults());
		Assert.assertEquals(
			existingPatcherFix.getKey(), newPatcherFix.getKey());
		AssertUtils.assertEquals(
			existingPatcherFix.getKeyVersion(), newPatcherFix.getKeyVersion());
		Assert.assertEquals(
			existingPatcherFix.isLatestFix(), newPatcherFix.isLatestFix());
		Assert.assertEquals(
			existingPatcherFix.getName(), newPatcherFix.getName());
		Assert.assertEquals(
			existingPatcherFix.isNotified(), newPatcherFix.isNotified());
		Assert.assertEquals(
			existingPatcherFix.isObsolete(), newPatcherFix.isObsolete());
		Assert.assertEquals(
			existingPatcherFix.getProductVersion(),
			newPatcherFix.getProductVersion());
		Assert.assertEquals(
			existingPatcherFix.getRequestKey(), newPatcherFix.getRequestKey());
		Assert.assertEquals(
			existingPatcherFix.getRequirements(),
			newPatcherFix.getRequirements());
		Assert.assertEquals(
			existingPatcherFix.getType(), newPatcherFix.getType());
		Assert.assertEquals(
			existingPatcherFix.getStatus(), newPatcherFix.getStatus());
		Assert.assertEquals(
			existingPatcherFix.getStatusByUserId(),
			newPatcherFix.getStatusByUserId());
		Assert.assertEquals(
			existingPatcherFix.getStatusByUserName(),
			newPatcherFix.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherFix.getStatusDate()),
			Time.getShortTimestamp(newPatcherFix.getStatusDate()));
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PatcherFix newPatcherFix = addPatcherFix();

		PatcherFix existingPatcherFix = _persistence.findByPrimaryKey(
			newPatcherFix.getPrimaryKey());

		Assert.assertEquals(existingPatcherFix, newPatcherFix);
	}

	@Test(expected = NoSuchPatcherFixException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PatcherFix> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"OSBPatcher_PatcherFix", "mvccVersion", true, "patcherFixId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "patcherProductVersionId", true,
			"patcherProjectVersionId", true, "committish", true, "dependencies",
			true, "fixPackStatus", true, "gitHash", true, "gitRemoteURL", true,
			"key", true, "keyVersion", true, "latestFix", true, "notified",
			true, "obsolete", true, "productVersion", true, "requestKey", true,
			"requirements", true, "type", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PatcherFix newPatcherFix = addPatcherFix();

		PatcherFix existingPatcherFix = _persistence.fetchByPrimaryKey(
			newPatcherFix.getPrimaryKey());

		Assert.assertEquals(existingPatcherFix, newPatcherFix);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFix missingPatcherFix = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPatcherFix);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PatcherFix newPatcherFix1 = addPatcherFix();
		PatcherFix newPatcherFix2 = addPatcherFix();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherFix1.getPrimaryKey());
		primaryKeys.add(newPatcherFix2.getPrimaryKey());

		Map<Serializable, PatcherFix> patcherFixes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, patcherFixes.size());
		Assert.assertEquals(
			newPatcherFix1, patcherFixes.get(newPatcherFix1.getPrimaryKey()));
		Assert.assertEquals(
			newPatcherFix2, patcherFixes.get(newPatcherFix2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PatcherFix> patcherFixes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherFixes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PatcherFix newPatcherFix = addPatcherFix();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherFix.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PatcherFix> patcherFixes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherFixes.size());
		Assert.assertEquals(
			newPatcherFix, patcherFixes.get(newPatcherFix.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PatcherFix> patcherFixes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherFixes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PatcherFix newPatcherFix = addPatcherFix();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherFix.getPrimaryKey());

		Map<Serializable, PatcherFix> patcherFixes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherFixes.size());
		Assert.assertEquals(
			newPatcherFix, patcherFixes.get(newPatcherFix.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			PatcherFixLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<PatcherFix>() {

				@Override
				public void performAction(PatcherFix patcherFix) {
					Assert.assertNotNull(patcherFix);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		PatcherFix newPatcherFix = addPatcherFix();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFix.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherFixId", newPatcherFix.getPatcherFixId()));

		List<PatcherFix> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		PatcherFix existingPatcherFix = result.get(0);

		Assert.assertEquals(existingPatcherFix, newPatcherFix);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFix.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherFixId", RandomTestUtil.nextLong()));

		List<PatcherFix> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		PatcherFix newPatcherFix = addPatcherFix();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFix.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherFixId"));

		Object newPatcherFixId = newPatcherFix.getPatcherFixId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherFixId", new Object[] {newPatcherFixId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPatcherFixId = result.get(0);

		Assert.assertEquals(existingPatcherFixId, newPatcherFixId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFix.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherFixId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherFixId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected PatcherFix addPatcherFix() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFix patcherFix = _persistence.create(pk);

		patcherFix.setMvccVersion(RandomTestUtil.nextLong());

		patcherFix.setCompanyId(RandomTestUtil.nextLong());

		patcherFix.setUserId(RandomTestUtil.nextLong());

		patcherFix.setUserName(RandomTestUtil.randomString());

		patcherFix.setCreateDate(RandomTestUtil.nextDate());

		patcherFix.setModifiedDate(RandomTestUtil.nextDate());

		patcherFix.setPatcherProductVersionId(RandomTestUtil.nextLong());

		patcherFix.setPatcherProjectVersionId(RandomTestUtil.nextLong());

		patcherFix.setComments(RandomTestUtil.randomString());

		patcherFix.setCommittish(RandomTestUtil.randomString());

		patcherFix.setDependencies(RandomTestUtil.randomString());

		patcherFix.setFixPackStatus(RandomTestUtil.nextInt());

		patcherFix.setGitHash(RandomTestUtil.randomString());

		patcherFix.setGitRemoteURL(RandomTestUtil.randomString());

		patcherFix.setJenkinsResults(RandomTestUtil.randomString());

		patcherFix.setKey(RandomTestUtil.randomString());

		patcherFix.setKeyVersion(RandomTestUtil.nextDouble());

		patcherFix.setLatestFix(RandomTestUtil.randomBoolean());

		patcherFix.setName(RandomTestUtil.randomString());

		patcherFix.setNotified(RandomTestUtil.randomBoolean());

		patcherFix.setObsolete(RandomTestUtil.randomBoolean());

		patcherFix.setProductVersion(RandomTestUtil.nextInt());

		patcherFix.setRequestKey(RandomTestUtil.randomString());

		patcherFix.setRequirements(RandomTestUtil.randomString());

		patcherFix.setType(RandomTestUtil.nextInt());

		patcherFix.setStatus(RandomTestUtil.nextInt());

		patcherFix.setStatusByUserId(RandomTestUtil.nextLong());

		patcherFix.setStatusByUserName(RandomTestUtil.randomString());

		patcherFix.setStatusDate(RandomTestUtil.nextDate());

		_patcherFixes.add(_persistence.update(patcherFix));

		return patcherFix;
	}

	private List<PatcherFix> _patcherFixes = new ArrayList<PatcherFix>();
	private PatcherFixPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}