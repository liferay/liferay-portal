/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.patcher.exception.NoSuchPatcherProductVersionException;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.persistence.PatcherProductVersionPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherProductVersionUtil;
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
public class PatcherProductVersionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.osb.patcher.service"));

	@Before
	public void setUp() {
		_persistence = PatcherProductVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PatcherProductVersion> iterator =
			_patcherProductVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherProductVersion patcherProductVersion = _persistence.create(pk);

		Assert.assertNotNull(patcherProductVersion);

		Assert.assertEquals(patcherProductVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PatcherProductVersion newPatcherProductVersion =
			addPatcherProductVersion();

		_persistence.remove(newPatcherProductVersion);

		PatcherProductVersion existingPatcherProductVersion =
			_persistence.fetchByPrimaryKey(
				newPatcherProductVersion.getPrimaryKey());

		Assert.assertNull(existingPatcherProductVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPatcherProductVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherProductVersion newPatcherProductVersion = _persistence.create(
			pk);

		newPatcherProductVersion.setMvccVersion(RandomTestUtil.nextLong());

		newPatcherProductVersion.setCompanyId(RandomTestUtil.nextLong());

		newPatcherProductVersion.setUserId(RandomTestUtil.nextLong());

		newPatcherProductVersion.setUserName(RandomTestUtil.randomString());

		newPatcherProductVersion.setCreateDate(RandomTestUtil.nextDate());

		newPatcherProductVersion.setModifiedDate(RandomTestUtil.nextDate());

		newPatcherProductVersion.setFixDeliveryMethod(RandomTestUtil.nextInt());

		newPatcherProductVersion.setModuleFolderName(
			RandomTestUtil.randomString());

		newPatcherProductVersion.setName(RandomTestUtil.randomString());

		_patcherProductVersions.add(
			_persistence.update(newPatcherProductVersion));

		PatcherProductVersion existingPatcherProductVersion =
			_persistence.findByPrimaryKey(
				newPatcherProductVersion.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherProductVersion.getMvccVersion(),
			newPatcherProductVersion.getMvccVersion());
		Assert.assertEquals(
			existingPatcherProductVersion.getPatcherProductVersionId(),
			newPatcherProductVersion.getPatcherProductVersionId());
		Assert.assertEquals(
			existingPatcherProductVersion.getCompanyId(),
			newPatcherProductVersion.getCompanyId());
		Assert.assertEquals(
			existingPatcherProductVersion.getUserId(),
			newPatcherProductVersion.getUserId());
		Assert.assertEquals(
			existingPatcherProductVersion.getUserName(),
			newPatcherProductVersion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingPatcherProductVersion.getCreateDate()),
			Time.getShortTimestamp(newPatcherProductVersion.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingPatcherProductVersion.getModifiedDate()),
			Time.getShortTimestamp(newPatcherProductVersion.getModifiedDate()));
		Assert.assertEquals(
			existingPatcherProductVersion.getFixDeliveryMethod(),
			newPatcherProductVersion.getFixDeliveryMethod());
		Assert.assertEquals(
			existingPatcherProductVersion.getModuleFolderName(),
			newPatcherProductVersion.getModuleFolderName());
		Assert.assertEquals(
			existingPatcherProductVersion.getName(),
			newPatcherProductVersion.getName());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PatcherProductVersion newPatcherProductVersion =
			addPatcherProductVersion();

		PatcherProductVersion existingPatcherProductVersion =
			_persistence.findByPrimaryKey(
				newPatcherProductVersion.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherProductVersion, newPatcherProductVersion);
	}

	@Test(expected = NoSuchPatcherProductVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PatcherProductVersion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"PProductVersion", "mvccVersion", true, "patcherProductVersionId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "fixDeliveryMethod", true,
			"moduleFolderName", true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PatcherProductVersion newPatcherProductVersion =
			addPatcherProductVersion();

		PatcherProductVersion existingPatcherProductVersion =
			_persistence.fetchByPrimaryKey(
				newPatcherProductVersion.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherProductVersion, newPatcherProductVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherProductVersion missingPatcherProductVersion =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPatcherProductVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PatcherProductVersion newPatcherProductVersion1 =
			addPatcherProductVersion();
		PatcherProductVersion newPatcherProductVersion2 =
			addPatcherProductVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherProductVersion1.getPrimaryKey());
		primaryKeys.add(newPatcherProductVersion2.getPrimaryKey());

		Map<Serializable, PatcherProductVersion> patcherProductVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, patcherProductVersions.size());
		Assert.assertEquals(
			newPatcherProductVersion1,
			patcherProductVersions.get(
				newPatcherProductVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newPatcherProductVersion2,
			patcherProductVersions.get(
				newPatcherProductVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PatcherProductVersion> patcherProductVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherProductVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PatcherProductVersion newPatcherProductVersion =
			addPatcherProductVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherProductVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PatcherProductVersion> patcherProductVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherProductVersions.size());
		Assert.assertEquals(
			newPatcherProductVersion,
			patcherProductVersions.get(
				newPatcherProductVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PatcherProductVersion> patcherProductVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherProductVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PatcherProductVersion newPatcherProductVersion =
			addPatcherProductVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherProductVersion.getPrimaryKey());

		Map<Serializable, PatcherProductVersion> patcherProductVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherProductVersions.size());
		Assert.assertEquals(
			newPatcherProductVersion,
			patcherProductVersions.get(
				newPatcherProductVersion.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			PatcherProductVersionLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<PatcherProductVersion>() {

				@Override
				public void performAction(
					PatcherProductVersion patcherProductVersion) {

					Assert.assertNotNull(patcherProductVersion);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		PatcherProductVersion newPatcherProductVersion =
			addPatcherProductVersion();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherProductVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherProductVersionId",
				newPatcherProductVersion.getPatcherProductVersionId()));

		List<PatcherProductVersion> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		PatcherProductVersion existingPatcherProductVersion = result.get(0);

		Assert.assertEquals(
			existingPatcherProductVersion, newPatcherProductVersion);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherProductVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherProductVersionId", RandomTestUtil.nextLong()));

		List<PatcherProductVersion> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		PatcherProductVersion newPatcherProductVersion =
			addPatcherProductVersion();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherProductVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherProductVersionId"));

		Object newPatcherProductVersionId =
			newPatcherProductVersion.getPatcherProductVersionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherProductVersionId",
				new Object[] {newPatcherProductVersionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPatcherProductVersionId = result.get(0);

		Assert.assertEquals(
			existingPatcherProductVersionId, newPatcherProductVersionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherProductVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherProductVersionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherProductVersionId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected PatcherProductVersion addPatcherProductVersion()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		PatcherProductVersion patcherProductVersion = _persistence.create(pk);

		patcherProductVersion.setMvccVersion(RandomTestUtil.nextLong());

		patcherProductVersion.setCompanyId(RandomTestUtil.nextLong());

		patcherProductVersion.setUserId(RandomTestUtil.nextLong());

		patcherProductVersion.setUserName(RandomTestUtil.randomString());

		patcherProductVersion.setCreateDate(RandomTestUtil.nextDate());

		patcherProductVersion.setModifiedDate(RandomTestUtil.nextDate());

		patcherProductVersion.setFixDeliveryMethod(RandomTestUtil.nextInt());

		patcherProductVersion.setModuleFolderName(
			RandomTestUtil.randomString());

		patcherProductVersion.setName(RandomTestUtil.randomString());

		_patcherProductVersions.add(_persistence.update(patcherProductVersion));

		return patcherProductVersion;
	}

	private List<PatcherProductVersion> _patcherProductVersions =
		new ArrayList<PatcherProductVersion>();
	private PatcherProductVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}