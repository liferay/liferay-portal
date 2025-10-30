/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchFormInstanceReportException;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceReport;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceReportPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceReportUtil;
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
public class DDMFormInstanceReportPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.dynamic.data.mapping.service"));

	@Before
	public void setUp() {
		_persistence = DDMFormInstanceReportUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMFormInstanceReport> iterator =
			_ddmFormInstanceReports.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceReport ddmFormInstanceReport = _persistence.create(pk);

		Assert.assertNotNull(ddmFormInstanceReport);

		Assert.assertEquals(ddmFormInstanceReport.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMFormInstanceReport newDDMFormInstanceReport =
			addDDMFormInstanceReport();

		_persistence.remove(newDDMFormInstanceReport);

		DDMFormInstanceReport existingDDMFormInstanceReport =
			_persistence.fetchByPrimaryKey(
				newDDMFormInstanceReport.getPrimaryKey());

		Assert.assertNull(existingDDMFormInstanceReport);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMFormInstanceReport();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceReport newDDMFormInstanceReport = _persistence.create(
			pk);

		newDDMFormInstanceReport.setMvccVersion(RandomTestUtil.nextLong());

		newDDMFormInstanceReport.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMFormInstanceReport.setGroupId(RandomTestUtil.nextLong());

		newDDMFormInstanceReport.setCompanyId(RandomTestUtil.nextLong());

		newDDMFormInstanceReport.setCreateDate(RandomTestUtil.nextDate());

		newDDMFormInstanceReport.setModifiedDate(RandomTestUtil.nextDate());

		newDDMFormInstanceReport.setFormInstanceId(RandomTestUtil.nextLong());

		newDDMFormInstanceReport.setData(RandomTestUtil.randomString());

		_ddmFormInstanceReports.add(
			_persistence.update(newDDMFormInstanceReport));

		DDMFormInstanceReport existingDDMFormInstanceReport =
			_persistence.findByPrimaryKey(
				newDDMFormInstanceReport.getPrimaryKey());

		Assert.assertEquals(
			existingDDMFormInstanceReport.getMvccVersion(),
			newDDMFormInstanceReport.getMvccVersion());
		Assert.assertEquals(
			existingDDMFormInstanceReport.getCtCollectionId(),
			newDDMFormInstanceReport.getCtCollectionId());
		Assert.assertEquals(
			existingDDMFormInstanceReport.getFormInstanceReportId(),
			newDDMFormInstanceReport.getFormInstanceReportId());
		Assert.assertEquals(
			existingDDMFormInstanceReport.getGroupId(),
			newDDMFormInstanceReport.getGroupId());
		Assert.assertEquals(
			existingDDMFormInstanceReport.getCompanyId(),
			newDDMFormInstanceReport.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDDMFormInstanceReport.getCreateDate()),
			Time.getShortTimestamp(newDDMFormInstanceReport.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDDMFormInstanceReport.getModifiedDate()),
			Time.getShortTimestamp(newDDMFormInstanceReport.getModifiedDate()));
		Assert.assertEquals(
			existingDDMFormInstanceReport.getFormInstanceId(),
			newDDMFormInstanceReport.getFormInstanceId());
		Assert.assertEquals(
			existingDDMFormInstanceReport.getData(),
			newDDMFormInstanceReport.getData());
	}

	@Test
	public void testCountByFormInstanceId() throws Exception {
		_persistence.countByFormInstanceId(RandomTestUtil.nextLong());

		_persistence.countByFormInstanceId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMFormInstanceReport newDDMFormInstanceReport =
			addDDMFormInstanceReport();

		DDMFormInstanceReport existingDDMFormInstanceReport =
			_persistence.findByPrimaryKey(
				newDDMFormInstanceReport.getPrimaryKey());

		Assert.assertEquals(
			existingDDMFormInstanceReport, newDDMFormInstanceReport);
	}

	@Test(expected = NoSuchFormInstanceReportException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMFormInstanceReport> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDMFormInstanceReport", "mvccVersion", true, "ctCollectionId",
			true, "formInstanceReportId", true, "groupId", true, "companyId",
			true, "createDate", true, "modifiedDate", true, "formInstanceId",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMFormInstanceReport newDDMFormInstanceReport =
			addDDMFormInstanceReport();

		DDMFormInstanceReport existingDDMFormInstanceReport =
			_persistence.fetchByPrimaryKey(
				newDDMFormInstanceReport.getPrimaryKey());

		Assert.assertEquals(
			existingDDMFormInstanceReport, newDDMFormInstanceReport);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceReport missingDDMFormInstanceReport =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMFormInstanceReport);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMFormInstanceReport newDDMFormInstanceReport1 =
			addDDMFormInstanceReport();
		DDMFormInstanceReport newDDMFormInstanceReport2 =
			addDDMFormInstanceReport();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMFormInstanceReport1.getPrimaryKey());
		primaryKeys.add(newDDMFormInstanceReport2.getPrimaryKey());

		Map<Serializable, DDMFormInstanceReport> ddmFormInstanceReports =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmFormInstanceReports.size());
		Assert.assertEquals(
			newDDMFormInstanceReport1,
			ddmFormInstanceReports.get(
				newDDMFormInstanceReport1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMFormInstanceReport2,
			ddmFormInstanceReports.get(
				newDDMFormInstanceReport2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMFormInstanceReport> ddmFormInstanceReports =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmFormInstanceReports.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMFormInstanceReport newDDMFormInstanceReport =
			addDDMFormInstanceReport();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMFormInstanceReport.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMFormInstanceReport> ddmFormInstanceReports =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmFormInstanceReports.size());
		Assert.assertEquals(
			newDDMFormInstanceReport,
			ddmFormInstanceReports.get(
				newDDMFormInstanceReport.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMFormInstanceReport> ddmFormInstanceReports =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmFormInstanceReports.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMFormInstanceReport newDDMFormInstanceReport =
			addDDMFormInstanceReport();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMFormInstanceReport.getPrimaryKey());

		Map<Serializable, DDMFormInstanceReport> ddmFormInstanceReports =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmFormInstanceReports.size());
		Assert.assertEquals(
			newDDMFormInstanceReport,
			ddmFormInstanceReports.get(
				newDDMFormInstanceReport.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMFormInstanceReport newDDMFormInstanceReport =
			addDDMFormInstanceReport();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newDDMFormInstanceReport.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		DDMFormInstanceReport ddmFormInstanceReport) {

		Assert.assertEquals(
			Long.valueOf(ddmFormInstanceReport.getFormInstanceId()),
			ReflectionTestUtil.<Long>invoke(
				ddmFormInstanceReport, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "formInstanceId"));
	}

	protected DDMFormInstanceReport addDDMFormInstanceReport()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceReport ddmFormInstanceReport = _persistence.create(pk);

		ddmFormInstanceReport.setMvccVersion(RandomTestUtil.nextLong());

		ddmFormInstanceReport.setCtCollectionId(RandomTestUtil.nextLong());

		ddmFormInstanceReport.setGroupId(RandomTestUtil.nextLong());

		ddmFormInstanceReport.setCompanyId(RandomTestUtil.nextLong());

		ddmFormInstanceReport.setCreateDate(RandomTestUtil.nextDate());

		ddmFormInstanceReport.setModifiedDate(RandomTestUtil.nextDate());

		ddmFormInstanceReport.setFormInstanceId(RandomTestUtil.nextLong());

		ddmFormInstanceReport.setData(RandomTestUtil.randomString());

		_ddmFormInstanceReports.add(_persistence.update(ddmFormInstanceReport));

		return ddmFormInstanceReport;
	}

	private List<DDMFormInstanceReport> _ddmFormInstanceReports =
		new ArrayList<DDMFormInstanceReport>();
	private DDMFormInstanceReportPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}