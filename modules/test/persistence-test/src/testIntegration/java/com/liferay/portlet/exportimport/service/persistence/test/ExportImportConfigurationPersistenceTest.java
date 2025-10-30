/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.exportimport.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.exception.NoSuchConfigurationException;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.persistence.ExportImportConfigurationPersistence;
import com.liferay.exportimport.kernel.service.persistence.ExportImportConfigurationUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class ExportImportConfigurationPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = ExportImportConfigurationUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ExportImportConfiguration> iterator =
			_exportImportConfigurations.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExportImportConfiguration exportImportConfiguration =
			_persistence.create(pk);

		Assert.assertNotNull(exportImportConfiguration);

		Assert.assertEquals(exportImportConfiguration.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ExportImportConfiguration newExportImportConfiguration =
			addExportImportConfiguration();

		_persistence.remove(newExportImportConfiguration);

		ExportImportConfiguration existingExportImportConfiguration =
			_persistence.fetchByPrimaryKey(
				newExportImportConfiguration.getPrimaryKey());

		Assert.assertNull(existingExportImportConfiguration);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addExportImportConfiguration();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExportImportConfiguration newExportImportConfiguration =
			_persistence.create(pk);

		newExportImportConfiguration.setMvccVersion(RandomTestUtil.nextLong());

		newExportImportConfiguration.setGroupId(RandomTestUtil.nextLong());

		newExportImportConfiguration.setCompanyId(RandomTestUtil.nextLong());

		newExportImportConfiguration.setUserId(RandomTestUtil.nextLong());

		newExportImportConfiguration.setUserName(RandomTestUtil.randomString());

		newExportImportConfiguration.setCreateDate(RandomTestUtil.nextDate());

		newExportImportConfiguration.setModifiedDate(RandomTestUtil.nextDate());

		newExportImportConfiguration.setName(RandomTestUtil.randomString());

		newExportImportConfiguration.setDescription(
			RandomTestUtil.randomString());

		newExportImportConfiguration.setType(RandomTestUtil.nextInt());

		newExportImportConfiguration.setSettings(RandomTestUtil.randomString());

		newExportImportConfiguration.setStatus(RandomTestUtil.nextInt());

		newExportImportConfiguration.setStatusByUserId(
			RandomTestUtil.nextLong());

		newExportImportConfiguration.setStatusByUserName(
			RandomTestUtil.randomString());

		newExportImportConfiguration.setStatusDate(RandomTestUtil.nextDate());

		_exportImportConfigurations.add(
			_persistence.update(newExportImportConfiguration));

		ExportImportConfiguration existingExportImportConfiguration =
			_persistence.findByPrimaryKey(
				newExportImportConfiguration.getPrimaryKey());

		Assert.assertEquals(
			existingExportImportConfiguration.getMvccVersion(),
			newExportImportConfiguration.getMvccVersion());
		Assert.assertEquals(
			existingExportImportConfiguration.getExportImportConfigurationId(),
			newExportImportConfiguration.getExportImportConfigurationId());
		Assert.assertEquals(
			existingExportImportConfiguration.getGroupId(),
			newExportImportConfiguration.getGroupId());
		Assert.assertEquals(
			existingExportImportConfiguration.getCompanyId(),
			newExportImportConfiguration.getCompanyId());
		Assert.assertEquals(
			existingExportImportConfiguration.getUserId(),
			newExportImportConfiguration.getUserId());
		Assert.assertEquals(
			existingExportImportConfiguration.getUserName(),
			newExportImportConfiguration.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingExportImportConfiguration.getCreateDate()),
			Time.getShortTimestamp(
				newExportImportConfiguration.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingExportImportConfiguration.getModifiedDate()),
			Time.getShortTimestamp(
				newExportImportConfiguration.getModifiedDate()));
		Assert.assertEquals(
			existingExportImportConfiguration.getName(),
			newExportImportConfiguration.getName());
		Assert.assertEquals(
			existingExportImportConfiguration.getDescription(),
			newExportImportConfiguration.getDescription());
		Assert.assertEquals(
			existingExportImportConfiguration.getType(),
			newExportImportConfiguration.getType());
		Assert.assertEquals(
			existingExportImportConfiguration.getSettings(),
			newExportImportConfiguration.getSettings());
		Assert.assertEquals(
			existingExportImportConfiguration.getStatus(),
			newExportImportConfiguration.getStatus());
		Assert.assertEquals(
			existingExportImportConfiguration.getStatusByUserId(),
			newExportImportConfiguration.getStatusByUserId());
		Assert.assertEquals(
			existingExportImportConfiguration.getStatusByUserName(),
			newExportImportConfiguration.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingExportImportConfiguration.getStatusDate()),
			Time.getShortTimestamp(
				newExportImportConfiguration.getStatusDate()));
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByG_T() throws Exception {
		_persistence.countByG_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_T(0L, 0);
	}

	@Test
	public void testCountByG_S() throws Exception {
		_persistence.countByG_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_S(0L, 0);
	}

	@Test
	public void testCountByG_T_S() throws Exception {
		_persistence.countByG_T_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt());

		_persistence.countByG_T_S(0L, 0, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ExportImportConfiguration newExportImportConfiguration =
			addExportImportConfiguration();

		ExportImportConfiguration existingExportImportConfiguration =
			_persistence.findByPrimaryKey(
				newExportImportConfiguration.getPrimaryKey());

		Assert.assertEquals(
			existingExportImportConfiguration, newExportImportConfiguration);
	}

	@Test(expected = NoSuchConfigurationException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ExportImportConfiguration>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"ExportImportConfiguration", "mvccVersion", true,
			"exportImportConfigurationId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "name", true, "description", true, "type",
			true, "status", true, "statusByUserId", true, "statusByUserName",
			true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ExportImportConfiguration newExportImportConfiguration =
			addExportImportConfiguration();

		ExportImportConfiguration existingExportImportConfiguration =
			_persistence.fetchByPrimaryKey(
				newExportImportConfiguration.getPrimaryKey());

		Assert.assertEquals(
			existingExportImportConfiguration, newExportImportConfiguration);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExportImportConfiguration missingExportImportConfiguration =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingExportImportConfiguration);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ExportImportConfiguration newExportImportConfiguration1 =
			addExportImportConfiguration();
		ExportImportConfiguration newExportImportConfiguration2 =
			addExportImportConfiguration();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExportImportConfiguration1.getPrimaryKey());
		primaryKeys.add(newExportImportConfiguration2.getPrimaryKey());

		Map<Serializable, ExportImportConfiguration>
			exportImportConfigurations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, exportImportConfigurations.size());
		Assert.assertEquals(
			newExportImportConfiguration1,
			exportImportConfigurations.get(
				newExportImportConfiguration1.getPrimaryKey()));
		Assert.assertEquals(
			newExportImportConfiguration2,
			exportImportConfigurations.get(
				newExportImportConfiguration2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ExportImportConfiguration>
			exportImportConfigurations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(exportImportConfigurations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ExportImportConfiguration newExportImportConfiguration =
			addExportImportConfiguration();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExportImportConfiguration.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ExportImportConfiguration>
			exportImportConfigurations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, exportImportConfigurations.size());
		Assert.assertEquals(
			newExportImportConfiguration,
			exportImportConfigurations.get(
				newExportImportConfiguration.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ExportImportConfiguration>
			exportImportConfigurations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(exportImportConfigurations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ExportImportConfiguration newExportImportConfiguration =
			addExportImportConfiguration();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExportImportConfiguration.getPrimaryKey());

		Map<Serializable, ExportImportConfiguration>
			exportImportConfigurations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, exportImportConfigurations.size());
		Assert.assertEquals(
			newExportImportConfiguration,
			exportImportConfigurations.get(
				newExportImportConfiguration.getPrimaryKey()));
	}

	protected ExportImportConfiguration addExportImportConfiguration()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		ExportImportConfiguration exportImportConfiguration =
			_persistence.create(pk);

		exportImportConfiguration.setMvccVersion(RandomTestUtil.nextLong());

		exportImportConfiguration.setGroupId(RandomTestUtil.nextLong());

		exportImportConfiguration.setCompanyId(RandomTestUtil.nextLong());

		exportImportConfiguration.setUserId(RandomTestUtil.nextLong());

		exportImportConfiguration.setUserName(RandomTestUtil.randomString());

		exportImportConfiguration.setCreateDate(RandomTestUtil.nextDate());

		exportImportConfiguration.setModifiedDate(RandomTestUtil.nextDate());

		exportImportConfiguration.setName(RandomTestUtil.randomString());

		exportImportConfiguration.setDescription(RandomTestUtil.randomString());

		exportImportConfiguration.setType(RandomTestUtil.nextInt());

		exportImportConfiguration.setSettings(RandomTestUtil.randomString());

		exportImportConfiguration.setStatus(RandomTestUtil.nextInt());

		exportImportConfiguration.setStatusByUserId(RandomTestUtil.nextLong());

		exportImportConfiguration.setStatusByUserName(
			RandomTestUtil.randomString());

		exportImportConfiguration.setStatusDate(RandomTestUtil.nextDate());

		_exportImportConfigurations.add(
			_persistence.update(exportImportConfiguration));

		return exportImportConfiguration;
	}

	private List<ExportImportConfiguration> _exportImportConfigurations =
		new ArrayList<ExportImportConfiguration>();
	private ExportImportConfigurationPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}