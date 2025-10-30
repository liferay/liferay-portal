/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.exception.NoSuchSchemaVersionException;
import com.liferay.change.tracking.model.CTSchemaVersion;
import com.liferay.change.tracking.service.persistence.CTSchemaVersionPersistence;
import com.liferay.change.tracking.service.persistence.CTSchemaVersionUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
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
public class CTSchemaVersionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.change.tracking.service"));

	@Before
	public void setUp() {
		_persistence = CTSchemaVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTSchemaVersion> iterator = _ctSchemaVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSchemaVersion ctSchemaVersion = _persistence.create(pk);

		Assert.assertNotNull(ctSchemaVersion);

		Assert.assertEquals(ctSchemaVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTSchemaVersion newCTSchemaVersion = addCTSchemaVersion();

		_persistence.remove(newCTSchemaVersion);

		CTSchemaVersion existingCTSchemaVersion =
			_persistence.fetchByPrimaryKey(newCTSchemaVersion.getPrimaryKey());

		Assert.assertNull(existingCTSchemaVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTSchemaVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSchemaVersion newCTSchemaVersion = _persistence.create(pk);

		newCTSchemaVersion.setMvccVersion(RandomTestUtil.nextLong());

		newCTSchemaVersion.setCompanyId(RandomTestUtil.nextLong());

		newCTSchemaVersion.setSchemaContext(
			new HashMap<String, Serializable>());

		_ctSchemaVersions.add(_persistence.update(newCTSchemaVersion));

		CTSchemaVersion existingCTSchemaVersion = _persistence.findByPrimaryKey(
			newCTSchemaVersion.getPrimaryKey());

		Assert.assertEquals(
			existingCTSchemaVersion.getMvccVersion(),
			newCTSchemaVersion.getMvccVersion());
		Assert.assertEquals(
			existingCTSchemaVersion.getSchemaVersionId(),
			newCTSchemaVersion.getSchemaVersionId());
		Assert.assertEquals(
			existingCTSchemaVersion.getCompanyId(),
			newCTSchemaVersion.getCompanyId());
		Assert.assertEquals(
			existingCTSchemaVersion.getSchemaContext(),
			newCTSchemaVersion.getSchemaContext());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTSchemaVersion newCTSchemaVersion = addCTSchemaVersion();

		CTSchemaVersion existingCTSchemaVersion = _persistence.findByPrimaryKey(
			newCTSchemaVersion.getPrimaryKey());

		Assert.assertEquals(existingCTSchemaVersion, newCTSchemaVersion);
	}

	@Test(expected = NoSuchSchemaVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTSchemaVersion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTSchemaVersion", "mvccVersion", true, "schemaVersionId", true,
			"companyId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTSchemaVersion newCTSchemaVersion = addCTSchemaVersion();

		CTSchemaVersion existingCTSchemaVersion =
			_persistence.fetchByPrimaryKey(newCTSchemaVersion.getPrimaryKey());

		Assert.assertEquals(existingCTSchemaVersion, newCTSchemaVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSchemaVersion missingCTSchemaVersion = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingCTSchemaVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTSchemaVersion newCTSchemaVersion1 = addCTSchemaVersion();
		CTSchemaVersion newCTSchemaVersion2 = addCTSchemaVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSchemaVersion1.getPrimaryKey());
		primaryKeys.add(newCTSchemaVersion2.getPrimaryKey());

		Map<Serializable, CTSchemaVersion> ctSchemaVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ctSchemaVersions.size());
		Assert.assertEquals(
			newCTSchemaVersion1,
			ctSchemaVersions.get(newCTSchemaVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newCTSchemaVersion2,
			ctSchemaVersions.get(newCTSchemaVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTSchemaVersion> ctSchemaVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctSchemaVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTSchemaVersion newCTSchemaVersion = addCTSchemaVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSchemaVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTSchemaVersion> ctSchemaVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctSchemaVersions.size());
		Assert.assertEquals(
			newCTSchemaVersion,
			ctSchemaVersions.get(newCTSchemaVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTSchemaVersion> ctSchemaVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctSchemaVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTSchemaVersion newCTSchemaVersion = addCTSchemaVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSchemaVersion.getPrimaryKey());

		Map<Serializable, CTSchemaVersion> ctSchemaVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctSchemaVersions.size());
		Assert.assertEquals(
			newCTSchemaVersion,
			ctSchemaVersions.get(newCTSchemaVersion.getPrimaryKey()));
	}

	protected CTSchemaVersion addCTSchemaVersion() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSchemaVersion ctSchemaVersion = _persistence.create(pk);

		ctSchemaVersion.setMvccVersion(RandomTestUtil.nextLong());

		ctSchemaVersion.setCompanyId(RandomTestUtil.nextLong());

		ctSchemaVersion.setSchemaContext(new HashMap<String, Serializable>());

		_ctSchemaVersions.add(_persistence.update(ctSchemaVersion));

		return ctSchemaVersion;
	}

	private List<CTSchemaVersion> _ctSchemaVersions =
		new ArrayList<CTSchemaVersion>();
	private CTSchemaVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}