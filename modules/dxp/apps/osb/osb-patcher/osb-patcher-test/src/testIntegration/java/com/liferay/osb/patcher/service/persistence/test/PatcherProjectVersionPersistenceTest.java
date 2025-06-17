/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.patcher.exception.NoSuchPatcherProjectVersionException;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.persistence.PatcherProjectVersionPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherProjectVersionUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
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
public class PatcherProjectVersionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.osb.patcher.service"));

	@Before
	public void setUp() {
		_persistence = PatcherProjectVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PatcherProjectVersion> iterator =
			_patcherProjectVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherProjectVersion patcherProjectVersion = _persistence.create(pk);

		Assert.assertNotNull(patcherProjectVersion);

		Assert.assertEquals(patcherProjectVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PatcherProjectVersion newPatcherProjectVersion =
			addPatcherProjectVersion();

		_persistence.remove(newPatcherProjectVersion);

		PatcherProjectVersion existingPatcherProjectVersion =
			_persistence.fetchByPrimaryKey(
				newPatcherProjectVersion.getPrimaryKey());

		Assert.assertNull(existingPatcherProjectVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPatcherProjectVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherProjectVersion newPatcherProjectVersion = _persistence.create(
			pk);

		newPatcherProjectVersion.setMvccVersion(RandomTestUtil.nextLong());

		newPatcherProjectVersion.setCompanyId(RandomTestUtil.nextLong());

		newPatcherProjectVersion.setUserId(RandomTestUtil.nextLong());

		newPatcherProjectVersion.setUserName(RandomTestUtil.randomString());

		newPatcherProjectVersion.setCreateDate(RandomTestUtil.nextDate());

		newPatcherProjectVersion.setModifiedDate(RandomTestUtil.nextDate());

		newPatcherProjectVersion.setPatcherProductVersionId(
			RandomTestUtil.nextLong());

		newPatcherProjectVersion.setRootPatcherProjectVersionId(
			RandomTestUtil.nextLong());

		newPatcherProjectVersion.setCombinedBranch(
			RandomTestUtil.randomBoolean());

		newPatcherProjectVersion.setCommittish(RandomTestUtil.randomString());

		newPatcherProjectVersion.setFixedIssues(RandomTestUtil.randomString());

		newPatcherProjectVersion.setHide(RandomTestUtil.randomBoolean());

		newPatcherProjectVersion.setName(RandomTestUtil.randomString());

		newPatcherProjectVersion.setProductVersion(RandomTestUtil.nextInt());

		newPatcherProjectVersion.setRepositoryName(
			RandomTestUtil.randomString());

		_patcherProjectVersions.add(
			_persistence.update(newPatcherProjectVersion));

		PatcherProjectVersion existingPatcherProjectVersion =
			_persistence.findByPrimaryKey(
				newPatcherProjectVersion.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherProjectVersion.getMvccVersion(),
			newPatcherProjectVersion.getMvccVersion());
		Assert.assertEquals(
			existingPatcherProjectVersion.getPatcherProjectVersionId(),
			newPatcherProjectVersion.getPatcherProjectVersionId());
		Assert.assertEquals(
			existingPatcherProjectVersion.getCompanyId(),
			newPatcherProjectVersion.getCompanyId());
		Assert.assertEquals(
			existingPatcherProjectVersion.getUserId(),
			newPatcherProjectVersion.getUserId());
		Assert.assertEquals(
			existingPatcherProjectVersion.getUserName(),
			newPatcherProjectVersion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingPatcherProjectVersion.getCreateDate()),
			Time.getShortTimestamp(newPatcherProjectVersion.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingPatcherProjectVersion.getModifiedDate()),
			Time.getShortTimestamp(newPatcherProjectVersion.getModifiedDate()));
		Assert.assertEquals(
			existingPatcherProjectVersion.getPatcherProductVersionId(),
			newPatcherProjectVersion.getPatcherProductVersionId());
		Assert.assertEquals(
			existingPatcherProjectVersion.getRootPatcherProjectVersionId(),
			newPatcherProjectVersion.getRootPatcherProjectVersionId());
		Assert.assertEquals(
			existingPatcherProjectVersion.isCombinedBranch(),
			newPatcherProjectVersion.isCombinedBranch());
		Assert.assertEquals(
			existingPatcherProjectVersion.getCommittish(),
			newPatcherProjectVersion.getCommittish());
		Assert.assertEquals(
			existingPatcherProjectVersion.getFixedIssues(),
			newPatcherProjectVersion.getFixedIssues());
		Assert.assertEquals(
			existingPatcherProjectVersion.isHide(),
			newPatcherProjectVersion.isHide());
		Assert.assertEquals(
			existingPatcherProjectVersion.getName(),
			newPatcherProjectVersion.getName());
		Assert.assertEquals(
			existingPatcherProjectVersion.getProductVersion(),
			newPatcherProjectVersion.getProductVersion());
		Assert.assertEquals(
			existingPatcherProjectVersion.getRepositoryName(),
			newPatcherProjectVersion.getRepositoryName());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PatcherProjectVersion newPatcherProjectVersion =
			addPatcherProjectVersion();

		PatcherProjectVersion existingPatcherProjectVersion =
			_persistence.findByPrimaryKey(
				newPatcherProjectVersion.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherProjectVersion, newPatcherProjectVersion);
	}

	@Test(expected = NoSuchPatcherProjectVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PatcherProjectVersion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"PProjectVersion", "mvccVersion", true, "patcherProjectVersionId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "patcherProductVersionId",
			true, "rootPatcherProjectVersionId", true, "combinedBranch", true,
			"committish", true, "hide", true, "name", true, "productVersion",
			true, "repositoryName", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PatcherProjectVersion newPatcherProjectVersion =
			addPatcherProjectVersion();

		PatcherProjectVersion existingPatcherProjectVersion =
			_persistence.fetchByPrimaryKey(
				newPatcherProjectVersion.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherProjectVersion, newPatcherProjectVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherProjectVersion missingPatcherProjectVersion =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPatcherProjectVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PatcherProjectVersion newPatcherProjectVersion1 =
			addPatcherProjectVersion();
		PatcherProjectVersion newPatcherProjectVersion2 =
			addPatcherProjectVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherProjectVersion1.getPrimaryKey());
		primaryKeys.add(newPatcherProjectVersion2.getPrimaryKey());

		Map<Serializable, PatcherProjectVersion> patcherProjectVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, patcherProjectVersions.size());
		Assert.assertEquals(
			newPatcherProjectVersion1,
			patcherProjectVersions.get(
				newPatcherProjectVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newPatcherProjectVersion2,
			patcherProjectVersions.get(
				newPatcherProjectVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PatcherProjectVersion> patcherProjectVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherProjectVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PatcherProjectVersion newPatcherProjectVersion =
			addPatcherProjectVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherProjectVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PatcherProjectVersion> patcherProjectVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherProjectVersions.size());
		Assert.assertEquals(
			newPatcherProjectVersion,
			patcherProjectVersions.get(
				newPatcherProjectVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PatcherProjectVersion> patcherProjectVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherProjectVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PatcherProjectVersion newPatcherProjectVersion =
			addPatcherProjectVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherProjectVersion.getPrimaryKey());

		Map<Serializable, PatcherProjectVersion> patcherProjectVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherProjectVersions.size());
		Assert.assertEquals(
			newPatcherProjectVersion,
			patcherProjectVersions.get(
				newPatcherProjectVersion.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			PatcherProjectVersionLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<PatcherProjectVersion>() {

				@Override
				public void performAction(
					PatcherProjectVersion patcherProjectVersion) {

					Assert.assertNotNull(patcherProjectVersion);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		PatcherProjectVersion newPatcherProjectVersion =
			addPatcherProjectVersion();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherProjectVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherProjectVersionId",
				newPatcherProjectVersion.getPatcherProjectVersionId()));

		List<PatcherProjectVersion> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		PatcherProjectVersion existingPatcherProjectVersion = result.get(0);

		Assert.assertEquals(
			existingPatcherProjectVersion, newPatcherProjectVersion);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherProjectVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherProjectVersionId", RandomTestUtil.nextLong()));

		List<PatcherProjectVersion> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		PatcherProjectVersion newPatcherProjectVersion =
			addPatcherProjectVersion();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherProjectVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherProjectVersionId"));

		Object newPatcherProjectVersionId =
			newPatcherProjectVersion.getPatcherProjectVersionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherProjectVersionId",
				new Object[] {newPatcherProjectVersionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPatcherProjectVersionId = result.get(0);

		Assert.assertEquals(
			existingPatcherProjectVersionId, newPatcherProjectVersionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherProjectVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherProjectVersionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherProjectVersionId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected PatcherProjectVersion addPatcherProjectVersion()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		PatcherProjectVersion patcherProjectVersion = _persistence.create(pk);

		patcherProjectVersion.setMvccVersion(RandomTestUtil.nextLong());

		patcherProjectVersion.setCompanyId(RandomTestUtil.nextLong());

		patcherProjectVersion.setUserId(RandomTestUtil.nextLong());

		patcherProjectVersion.setUserName(RandomTestUtil.randomString());

		patcherProjectVersion.setCreateDate(RandomTestUtil.nextDate());

		patcherProjectVersion.setModifiedDate(RandomTestUtil.nextDate());

		patcherProjectVersion.setPatcherProductVersionId(
			RandomTestUtil.nextLong());

		patcherProjectVersion.setRootPatcherProjectVersionId(
			RandomTestUtil.nextLong());

		patcherProjectVersion.setCombinedBranch(RandomTestUtil.randomBoolean());

		patcherProjectVersion.setCommittish(RandomTestUtil.randomString());

		patcherProjectVersion.setFixedIssues(RandomTestUtil.randomString());

		patcherProjectVersion.setHide(RandomTestUtil.randomBoolean());

		patcherProjectVersion.setName(RandomTestUtil.randomString());

		patcherProjectVersion.setProductVersion(RandomTestUtil.nextInt());

		patcherProjectVersion.setRepositoryName(RandomTestUtil.randomString());

		_patcherProjectVersions.add(_persistence.update(patcherProjectVersion));

		return patcherProjectVersion;
	}

	private List<PatcherProjectVersion> _patcherProjectVersions =
		new ArrayList<PatcherProjectVersion>();
	private PatcherProjectVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}