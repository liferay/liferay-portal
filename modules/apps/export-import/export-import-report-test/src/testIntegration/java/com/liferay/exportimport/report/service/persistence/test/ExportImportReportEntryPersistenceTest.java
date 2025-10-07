/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.report.exception.NoSuchExportImportReportEntryException;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalServiceUtil;
import com.liferay.exportimport.report.service.persistence.ExportImportReportEntryPersistence;
import com.liferay.exportimport.report.service.persistence.ExportImportReportEntryUtil;
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
public class ExportImportReportEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.exportimport.report.service"));

	@Before
	public void setUp() {
		_persistence = ExportImportReportEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ExportImportReportEntry> iterator =
			_exportImportReportEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExportImportReportEntry exportImportReportEntry = _persistence.create(
			pk);

		Assert.assertNotNull(exportImportReportEntry);

		Assert.assertEquals(exportImportReportEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ExportImportReportEntry newExportImportReportEntry =
			addExportImportReportEntry();

		_persistence.remove(newExportImportReportEntry);

		ExportImportReportEntry existingExportImportReportEntry =
			_persistence.fetchByPrimaryKey(
				newExportImportReportEntry.getPrimaryKey());

		Assert.assertNull(existingExportImportReportEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addExportImportReportEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExportImportReportEntry newExportImportReportEntry =
			_persistence.create(pk);

		newExportImportReportEntry.setMvccVersion(RandomTestUtil.nextLong());

		newExportImportReportEntry.setGroupId(RandomTestUtil.nextLong());

		newExportImportReportEntry.setCompanyId(RandomTestUtil.nextLong());

		newExportImportReportEntry.setCreateDate(RandomTestUtil.nextDate());

		newExportImportReportEntry.setModifiedDate(RandomTestUtil.nextDate());

		newExportImportReportEntry.setClassExternalReferenceCode(
			RandomTestUtil.randomString());

		newExportImportReportEntry.setClassNameId(RandomTestUtil.nextLong());

		newExportImportReportEntry.setClassPK(RandomTestUtil.nextLong());

		newExportImportReportEntry.setExportImportConfigurationId(
			RandomTestUtil.nextLong());

		newExportImportReportEntry.setErrorMessage(
			RandomTestUtil.randomString());

		newExportImportReportEntry.setErrorStacktrace(
			RandomTestUtil.randomString());

		newExportImportReportEntry.setModelName(RandomTestUtil.randomString());

		newExportImportReportEntry.setOrigin(RandomTestUtil.nextInt());

		newExportImportReportEntry.setScope(RandomTestUtil.randomString());

		newExportImportReportEntry.setScopeKey(RandomTestUtil.randomString());

		newExportImportReportEntry.setType(RandomTestUtil.nextInt());

		newExportImportReportEntry.setStatus(RandomTestUtil.nextInt());

		_exportImportReportEntries.add(
			_persistence.update(newExportImportReportEntry));

		ExportImportReportEntry existingExportImportReportEntry =
			_persistence.findByPrimaryKey(
				newExportImportReportEntry.getPrimaryKey());

		Assert.assertEquals(
			existingExportImportReportEntry.getMvccVersion(),
			newExportImportReportEntry.getMvccVersion());
		Assert.assertEquals(
			existingExportImportReportEntry.getExportImportReportEntryId(),
			newExportImportReportEntry.getExportImportReportEntryId());
		Assert.assertEquals(
			existingExportImportReportEntry.getGroupId(),
			newExportImportReportEntry.getGroupId());
		Assert.assertEquals(
			existingExportImportReportEntry.getCompanyId(),
			newExportImportReportEntry.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingExportImportReportEntry.getCreateDate()),
			Time.getShortTimestamp(newExportImportReportEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingExportImportReportEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newExportImportReportEntry.getModifiedDate()));
		Assert.assertEquals(
			existingExportImportReportEntry.getClassExternalReferenceCode(),
			newExportImportReportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(
			existingExportImportReportEntry.getClassNameId(),
			newExportImportReportEntry.getClassNameId());
		Assert.assertEquals(
			existingExportImportReportEntry.getClassPK(),
			newExportImportReportEntry.getClassPK());
		Assert.assertEquals(
			existingExportImportReportEntry.getExportImportConfigurationId(),
			newExportImportReportEntry.getExportImportConfigurationId());
		Assert.assertEquals(
			existingExportImportReportEntry.getErrorMessage(),
			newExportImportReportEntry.getErrorMessage());
		Assert.assertEquals(
			existingExportImportReportEntry.getErrorStacktrace(),
			newExportImportReportEntry.getErrorStacktrace());
		Assert.assertEquals(
			existingExportImportReportEntry.getModelName(),
			newExportImportReportEntry.getModelName());
		Assert.assertEquals(
			existingExportImportReportEntry.getOrigin(),
			newExportImportReportEntry.getOrigin());
		Assert.assertEquals(
			existingExportImportReportEntry.getScope(),
			newExportImportReportEntry.getScope());
		Assert.assertEquals(
			existingExportImportReportEntry.getScopeKey(),
			newExportImportReportEntry.getScopeKey());
		Assert.assertEquals(
			existingExportImportReportEntry.getType(),
			newExportImportReportEntry.getType());
		Assert.assertEquals(
			existingExportImportReportEntry.getStatus(),
			newExportImportReportEntry.getStatus());
	}

	@Test
	public void testCountByC_E() throws Exception {
		_persistence.countByC_E(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_E(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ExportImportReportEntry newExportImportReportEntry =
			addExportImportReportEntry();

		ExportImportReportEntry existingExportImportReportEntry =
			_persistence.findByPrimaryKey(
				newExportImportReportEntry.getPrimaryKey());

		Assert.assertEquals(
			existingExportImportReportEntry, newExportImportReportEntry);
	}

	@Test(expected = NoSuchExportImportReportEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ExportImportReportEntry>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"ExportImportReportEntry", "mvccVersion", true,
			"exportImportReportEntryId", true, "groupId", true, "companyId",
			true, "createDate", true, "modifiedDate", true,
			"classExternalReferenceCode", true, "classNameId", true, "classPK",
			true, "exportImportConfigurationId", true, "modelName", true,
			"origin", true, "scope", true, "scopeKey", true, "type", true,
			"status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ExportImportReportEntry newExportImportReportEntry =
			addExportImportReportEntry();

		ExportImportReportEntry existingExportImportReportEntry =
			_persistence.fetchByPrimaryKey(
				newExportImportReportEntry.getPrimaryKey());

		Assert.assertEquals(
			existingExportImportReportEntry, newExportImportReportEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExportImportReportEntry missingExportImportReportEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingExportImportReportEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ExportImportReportEntry newExportImportReportEntry1 =
			addExportImportReportEntry();
		ExportImportReportEntry newExportImportReportEntry2 =
			addExportImportReportEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExportImportReportEntry1.getPrimaryKey());
		primaryKeys.add(newExportImportReportEntry2.getPrimaryKey());

		Map<Serializable, ExportImportReportEntry> exportImportReportEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, exportImportReportEntries.size());
		Assert.assertEquals(
			newExportImportReportEntry1,
			exportImportReportEntries.get(
				newExportImportReportEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newExportImportReportEntry2,
			exportImportReportEntries.get(
				newExportImportReportEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ExportImportReportEntry> exportImportReportEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(exportImportReportEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ExportImportReportEntry newExportImportReportEntry =
			addExportImportReportEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExportImportReportEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ExportImportReportEntry> exportImportReportEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, exportImportReportEntries.size());
		Assert.assertEquals(
			newExportImportReportEntry,
			exportImportReportEntries.get(
				newExportImportReportEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ExportImportReportEntry> exportImportReportEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(exportImportReportEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ExportImportReportEntry newExportImportReportEntry =
			addExportImportReportEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExportImportReportEntry.getPrimaryKey());

		Map<Serializable, ExportImportReportEntry> exportImportReportEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, exportImportReportEntries.size());
		Assert.assertEquals(
			newExportImportReportEntry,
			exportImportReportEntries.get(
				newExportImportReportEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ExportImportReportEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<ExportImportReportEntry>() {

				@Override
				public void performAction(
					ExportImportReportEntry exportImportReportEntry) {

					Assert.assertNotNull(exportImportReportEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ExportImportReportEntry newExportImportReportEntry =
			addExportImportReportEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ExportImportReportEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"exportImportReportEntryId",
				newExportImportReportEntry.getExportImportReportEntryId()));

		List<ExportImportReportEntry> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		ExportImportReportEntry existingExportImportReportEntry = result.get(0);

		Assert.assertEquals(
			existingExportImportReportEntry, newExportImportReportEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ExportImportReportEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"exportImportReportEntryId", RandomTestUtil.nextLong()));

		List<ExportImportReportEntry> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ExportImportReportEntry newExportImportReportEntry =
			addExportImportReportEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ExportImportReportEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("exportImportReportEntryId"));

		Object newExportImportReportEntryId =
			newExportImportReportEntry.getExportImportReportEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"exportImportReportEntryId",
				new Object[] {newExportImportReportEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingExportImportReportEntryId = result.get(0);

		Assert.assertEquals(
			existingExportImportReportEntryId, newExportImportReportEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ExportImportReportEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("exportImportReportEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"exportImportReportEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected ExportImportReportEntry addExportImportReportEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		ExportImportReportEntry exportImportReportEntry = _persistence.create(
			pk);

		exportImportReportEntry.setMvccVersion(RandomTestUtil.nextLong());

		exportImportReportEntry.setGroupId(RandomTestUtil.nextLong());

		exportImportReportEntry.setCompanyId(RandomTestUtil.nextLong());

		exportImportReportEntry.setCreateDate(RandomTestUtil.nextDate());

		exportImportReportEntry.setModifiedDate(RandomTestUtil.nextDate());

		exportImportReportEntry.setClassExternalReferenceCode(
			RandomTestUtil.randomString());

		exportImportReportEntry.setClassNameId(RandomTestUtil.nextLong());

		exportImportReportEntry.setClassPK(RandomTestUtil.nextLong());

		exportImportReportEntry.setExportImportConfigurationId(
			RandomTestUtil.nextLong());

		exportImportReportEntry.setErrorMessage(RandomTestUtil.randomString());

		exportImportReportEntry.setErrorStacktrace(
			RandomTestUtil.randomString());

		exportImportReportEntry.setModelName(RandomTestUtil.randomString());

		exportImportReportEntry.setOrigin(RandomTestUtil.nextInt());

		exportImportReportEntry.setScope(RandomTestUtil.randomString());

		exportImportReportEntry.setScopeKey(RandomTestUtil.randomString());

		exportImportReportEntry.setType(RandomTestUtil.nextInt());

		exportImportReportEntry.setStatus(RandomTestUtil.nextInt());

		_exportImportReportEntries.add(
			_persistence.update(exportImportReportEntry));

		return exportImportReportEntry;
	}

	private List<ExportImportReportEntry> _exportImportReportEntries =
		new ArrayList<ExportImportReportEntry>();
	private ExportImportReportEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}