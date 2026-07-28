/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchCompoundPKEntryException;
import com.liferay.portal.tools.service.builder.test.model.CompoundPKEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.CompoundPKEntryPK;
import com.liferay.portal.tools.service.builder.test.service.persistence.CompoundPKEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.CompoundPKEntryUtil;

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
public class CompoundPKEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = CompoundPKEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CompoundPKEntry> iterator = _compoundPKEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		CompoundPKEntryPK pk = new CompoundPKEntryPK(
			CompanyThreadLocal.getCompanyId(), RandomTestUtil.nextLong());

		CompoundPKEntry compoundPKEntry = _persistence.create(pk);

		Assert.assertNotNull(compoundPKEntry);

		Assert.assertEquals(compoundPKEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CompoundPKEntry newCompoundPKEntry = addCompoundPKEntry();

		_persistence.remove(newCompoundPKEntry);

		CompoundPKEntry existingCompoundPKEntry =
			_persistence.fetchByPrimaryKey(newCompoundPKEntry.getPrimaryKey());

		Assert.assertNull(existingCompoundPKEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCompoundPKEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CompoundPKEntry newCompoundPKEntry = addCompoundPKEntry();

		newCompoundPKEntry.setName(RandomTestUtil.randomString());

		newCompoundPKEntry = _persistence.update(newCompoundPKEntry);

		_compoundPKEntries.add(newCompoundPKEntry);

		CompoundPKEntry existingCompoundPKEntry = _persistence.findByPrimaryKey(
			newCompoundPKEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCompoundPKEntry.getCompanyId(),
			newCompoundPKEntry.getCompanyId());
		Assert.assertEquals(
			existingCompoundPKEntry.getClassNameId(),
			newCompoundPKEntry.getClassNameId());
		Assert.assertEquals(
			existingCompoundPKEntry.getName(), newCompoundPKEntry.getName());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CompoundPKEntry newCompoundPKEntry = addCompoundPKEntry();

		CompoundPKEntry existingCompoundPKEntry = _persistence.findByPrimaryKey(
			newCompoundPKEntry.getPrimaryKey());

		Assert.assertEquals(existingCompoundPKEntry, newCompoundPKEntry);
	}

	@Test(expected = NoSuchCompoundPKEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		CompoundPKEntryPK pk = new CompoundPKEntryPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CompoundPKEntry newCompoundPKEntry = addCompoundPKEntry();

		CompoundPKEntry existingCompoundPKEntry =
			_persistence.fetchByPrimaryKey(newCompoundPKEntry.getPrimaryKey());

		Assert.assertEquals(existingCompoundPKEntry, newCompoundPKEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		CompoundPKEntryPK pk = new CompoundPKEntryPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		CompoundPKEntry missingCompoundPKEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingCompoundPKEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CompoundPKEntry newCompoundPKEntry1 = addCompoundPKEntry();
		CompoundPKEntry newCompoundPKEntry2 = addCompoundPKEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCompoundPKEntry1.getPrimaryKey());
		primaryKeys.add(newCompoundPKEntry2.getPrimaryKey());

		Map<Serializable, CompoundPKEntry> compoundPKEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, compoundPKEntries.size());
		Assert.assertEquals(
			newCompoundPKEntry1,
			compoundPKEntries.get(newCompoundPKEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCompoundPKEntry2,
			compoundPKEntries.get(newCompoundPKEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		CompoundPKEntryPK pk1 = new CompoundPKEntryPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		CompoundPKEntryPK pk2 = new CompoundPKEntryPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CompoundPKEntry> compoundPKEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(compoundPKEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CompoundPKEntry newCompoundPKEntry = addCompoundPKEntry();

		CompoundPKEntryPK pk = new CompoundPKEntryPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCompoundPKEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CompoundPKEntry> compoundPKEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, compoundPKEntries.size());
		Assert.assertEquals(
			newCompoundPKEntry,
			compoundPKEntries.get(newCompoundPKEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CompoundPKEntry> compoundPKEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(compoundPKEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CompoundPKEntry newCompoundPKEntry = addCompoundPKEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCompoundPKEntry.getPrimaryKey());

		Map<Serializable, CompoundPKEntry> compoundPKEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, compoundPKEntries.size());
		Assert.assertEquals(
			newCompoundPKEntry,
			compoundPKEntries.get(newCompoundPKEntry.getPrimaryKey()));
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CompoundPKEntry newCompoundPKEntry = addCompoundPKEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CompoundPKEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"id.companyId", newCompoundPKEntry.getCompanyId()));
		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"id.classNameId", newCompoundPKEntry.getClassNameId()));

		List<CompoundPKEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CompoundPKEntry existingCompoundPKEntry = result.get(0);

		Assert.assertEquals(existingCompoundPKEntry, newCompoundPKEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CompoundPKEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"id.companyId", RandomTestUtil.nextLong()));
		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"id.classNameId", RandomTestUtil.nextLong()));

		List<CompoundPKEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CompoundPKEntry newCompoundPKEntry = addCompoundPKEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CompoundPKEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("id.companyId"));

		Object newCompanyId = newCompoundPKEntry.getCompanyId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"id.companyId", new Object[] {newCompanyId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCompanyId = result.get(0);

		Assert.assertEquals(existingCompanyId, newCompanyId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CompoundPKEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("id.companyId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"id.companyId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected CompoundPKEntry addCompoundPKEntry() throws Exception {
		CompoundPKEntryPK pk = new CompoundPKEntryPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		CompoundPKEntry compoundPKEntry = _persistence.create(pk);

		compoundPKEntry.setName(RandomTestUtil.randomString());

		_compoundPKEntries.add(_persistence.update(compoundPKEntry));

		return compoundPKEntry;
	}

	private List<CompoundPKEntry> _compoundPKEntries =
		new ArrayList<CompoundPKEntry>();
	private CompoundPKEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1078125304