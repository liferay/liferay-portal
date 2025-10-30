/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.workflow.kaleo.exception.NoSuchDefinitionVersionException;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoDefinitionVersionPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoDefinitionVersionUtil;

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
public class KaleoDefinitionVersionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.workflow.kaleo.service"));

	@Before
	public void setUp() {
		_persistence = KaleoDefinitionVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoDefinitionVersion> iterator =
			_kaleoDefinitionVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoDefinitionVersion kaleoDefinitionVersion = _persistence.create(pk);

		Assert.assertNotNull(kaleoDefinitionVersion);

		Assert.assertEquals(kaleoDefinitionVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoDefinitionVersion newKaleoDefinitionVersion =
			addKaleoDefinitionVersion();

		_persistence.remove(newKaleoDefinitionVersion);

		KaleoDefinitionVersion existingKaleoDefinitionVersion =
			_persistence.fetchByPrimaryKey(
				newKaleoDefinitionVersion.getPrimaryKey());

		Assert.assertNull(existingKaleoDefinitionVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoDefinitionVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoDefinitionVersion newKaleoDefinitionVersion = _persistence.create(
			pk);

		newKaleoDefinitionVersion.setMvccVersion(RandomTestUtil.nextLong());

		newKaleoDefinitionVersion.setCtCollectionId(RandomTestUtil.nextLong());

		newKaleoDefinitionVersion.setGroupId(RandomTestUtil.nextLong());

		newKaleoDefinitionVersion.setCompanyId(RandomTestUtil.nextLong());

		newKaleoDefinitionVersion.setUserId(RandomTestUtil.nextLong());

		newKaleoDefinitionVersion.setUserName(RandomTestUtil.randomString());

		newKaleoDefinitionVersion.setCreateDate(RandomTestUtil.nextDate());

		newKaleoDefinitionVersion.setModifiedDate(RandomTestUtil.nextDate());

		newKaleoDefinitionVersion.setKaleoDefinitionId(
			RandomTestUtil.nextLong());

		newKaleoDefinitionVersion.setName(RandomTestUtil.randomString());

		newKaleoDefinitionVersion.setTitle(RandomTestUtil.randomString());

		newKaleoDefinitionVersion.setDescription(RandomTestUtil.randomString());

		newKaleoDefinitionVersion.setContent(RandomTestUtil.randomString());

		newKaleoDefinitionVersion.setVersion(RandomTestUtil.randomString());

		newKaleoDefinitionVersion.setStartKaleoNodeId(
			RandomTestUtil.nextLong());

		newKaleoDefinitionVersion.setStatus(RandomTestUtil.nextInt());

		newKaleoDefinitionVersion.setStatusByUserId(RandomTestUtil.nextLong());

		newKaleoDefinitionVersion.setStatusByUserName(
			RandomTestUtil.randomString());

		newKaleoDefinitionVersion.setStatusDate(RandomTestUtil.nextDate());

		_kaleoDefinitionVersions.add(
			_persistence.update(newKaleoDefinitionVersion));

		KaleoDefinitionVersion existingKaleoDefinitionVersion =
			_persistence.findByPrimaryKey(
				newKaleoDefinitionVersion.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoDefinitionVersion.getMvccVersion(),
			newKaleoDefinitionVersion.getMvccVersion());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getCtCollectionId(),
			newKaleoDefinitionVersion.getCtCollectionId());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getKaleoDefinitionVersionId(),
			newKaleoDefinitionVersion.getKaleoDefinitionVersionId());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getGroupId(),
			newKaleoDefinitionVersion.getGroupId());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getCompanyId(),
			newKaleoDefinitionVersion.getCompanyId());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getUserId(),
			newKaleoDefinitionVersion.getUserId());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getUserName(),
			newKaleoDefinitionVersion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingKaleoDefinitionVersion.getCreateDate()),
			Time.getShortTimestamp(newKaleoDefinitionVersion.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingKaleoDefinitionVersion.getModifiedDate()),
			Time.getShortTimestamp(
				newKaleoDefinitionVersion.getModifiedDate()));
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getKaleoDefinitionId(),
			newKaleoDefinitionVersion.getKaleoDefinitionId());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getName(),
			newKaleoDefinitionVersion.getName());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getTitle(),
			newKaleoDefinitionVersion.getTitle());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getDescription(),
			newKaleoDefinitionVersion.getDescription());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getContent(),
			newKaleoDefinitionVersion.getContent());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getVersion(),
			newKaleoDefinitionVersion.getVersion());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getStartKaleoNodeId(),
			newKaleoDefinitionVersion.getStartKaleoNodeId());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getStatus(),
			newKaleoDefinitionVersion.getStatus());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getStatusByUserId(),
			newKaleoDefinitionVersion.getStatusByUserId());
		Assert.assertEquals(
			existingKaleoDefinitionVersion.getStatusByUserName(),
			newKaleoDefinitionVersion.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingKaleoDefinitionVersion.getStatusDate()),
			Time.getShortTimestamp(newKaleoDefinitionVersion.getStatusDate()));
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_N() throws Exception {
		_persistence.countByC_N(RandomTestUtil.nextLong(), "");

		_persistence.countByC_N(0L, "null");

		_persistence.countByC_N(0L, (String)null);
	}

	@Test
	public void testCountByC_N_V() throws Exception {
		_persistence.countByC_N_V(RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_N_V(0L, "null", "null");

		_persistence.countByC_N_V(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoDefinitionVersion newKaleoDefinitionVersion =
			addKaleoDefinitionVersion();

		KaleoDefinitionVersion existingKaleoDefinitionVersion =
			_persistence.findByPrimaryKey(
				newKaleoDefinitionVersion.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoDefinitionVersion, newKaleoDefinitionVersion);
	}

	@Test(expected = NoSuchDefinitionVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoDefinitionVersion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KaleoDefinitionVersion", "mvccVersion", true, "ctCollectionId",
			true, "kaleoDefinitionVersionId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "kaleoDefinitionId", true, "name", true,
			"title", true, "description", true, "version", true,
			"startKaleoNodeId", true, "status", true, "statusByUserId", true,
			"statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoDefinitionVersion newKaleoDefinitionVersion =
			addKaleoDefinitionVersion();

		KaleoDefinitionVersion existingKaleoDefinitionVersion =
			_persistence.fetchByPrimaryKey(
				newKaleoDefinitionVersion.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoDefinitionVersion, newKaleoDefinitionVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoDefinitionVersion missingKaleoDefinitionVersion =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKaleoDefinitionVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoDefinitionVersion newKaleoDefinitionVersion1 =
			addKaleoDefinitionVersion();
		KaleoDefinitionVersion newKaleoDefinitionVersion2 =
			addKaleoDefinitionVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoDefinitionVersion1.getPrimaryKey());
		primaryKeys.add(newKaleoDefinitionVersion2.getPrimaryKey());

		Map<Serializable, KaleoDefinitionVersion> kaleoDefinitionVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kaleoDefinitionVersions.size());
		Assert.assertEquals(
			newKaleoDefinitionVersion1,
			kaleoDefinitionVersions.get(
				newKaleoDefinitionVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoDefinitionVersion2,
			kaleoDefinitionVersions.get(
				newKaleoDefinitionVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoDefinitionVersion> kaleoDefinitionVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoDefinitionVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoDefinitionVersion newKaleoDefinitionVersion =
			addKaleoDefinitionVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoDefinitionVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoDefinitionVersion> kaleoDefinitionVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoDefinitionVersions.size());
		Assert.assertEquals(
			newKaleoDefinitionVersion,
			kaleoDefinitionVersions.get(
				newKaleoDefinitionVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoDefinitionVersion> kaleoDefinitionVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoDefinitionVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoDefinitionVersion newKaleoDefinitionVersion =
			addKaleoDefinitionVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoDefinitionVersion.getPrimaryKey());

		Map<Serializable, KaleoDefinitionVersion> kaleoDefinitionVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoDefinitionVersions.size());
		Assert.assertEquals(
			newKaleoDefinitionVersion,
			kaleoDefinitionVersions.get(
				newKaleoDefinitionVersion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		KaleoDefinitionVersion newKaleoDefinitionVersion =
			addKaleoDefinitionVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newKaleoDefinitionVersion.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		Assert.assertEquals(
			Long.valueOf(kaleoDefinitionVersion.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				kaleoDefinitionVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			kaleoDefinitionVersion.getName(),
			ReflectionTestUtil.invoke(
				kaleoDefinitionVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
		Assert.assertEquals(
			kaleoDefinitionVersion.getVersion(),
			ReflectionTestUtil.invoke(
				kaleoDefinitionVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected KaleoDefinitionVersion addKaleoDefinitionVersion()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		KaleoDefinitionVersion kaleoDefinitionVersion = _persistence.create(pk);

		kaleoDefinitionVersion.setMvccVersion(RandomTestUtil.nextLong());

		kaleoDefinitionVersion.setCtCollectionId(RandomTestUtil.nextLong());

		kaleoDefinitionVersion.setGroupId(RandomTestUtil.nextLong());

		kaleoDefinitionVersion.setCompanyId(RandomTestUtil.nextLong());

		kaleoDefinitionVersion.setUserId(RandomTestUtil.nextLong());

		kaleoDefinitionVersion.setUserName(RandomTestUtil.randomString());

		kaleoDefinitionVersion.setCreateDate(RandomTestUtil.nextDate());

		kaleoDefinitionVersion.setModifiedDate(RandomTestUtil.nextDate());

		kaleoDefinitionVersion.setKaleoDefinitionId(RandomTestUtil.nextLong());

		kaleoDefinitionVersion.setName(RandomTestUtil.randomString());

		kaleoDefinitionVersion.setTitle(RandomTestUtil.randomString());

		kaleoDefinitionVersion.setDescription(RandomTestUtil.randomString());

		kaleoDefinitionVersion.setContent(RandomTestUtil.randomString());

		kaleoDefinitionVersion.setVersion(RandomTestUtil.randomString());

		kaleoDefinitionVersion.setStartKaleoNodeId(RandomTestUtil.nextLong());

		kaleoDefinitionVersion.setStatus(RandomTestUtil.nextInt());

		kaleoDefinitionVersion.setStatusByUserId(RandomTestUtil.nextLong());

		kaleoDefinitionVersion.setStatusByUserName(
			RandomTestUtil.randomString());

		kaleoDefinitionVersion.setStatusDate(RandomTestUtil.nextDate());

		_kaleoDefinitionVersions.add(
			_persistence.update(kaleoDefinitionVersion));

		return kaleoDefinitionVersion;
	}

	private List<KaleoDefinitionVersion> _kaleoDefinitionVersions =
		new ArrayList<KaleoDefinitionVersion>();
	private KaleoDefinitionVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}