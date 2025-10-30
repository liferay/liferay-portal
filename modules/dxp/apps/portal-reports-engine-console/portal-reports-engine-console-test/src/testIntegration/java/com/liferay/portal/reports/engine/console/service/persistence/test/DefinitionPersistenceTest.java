/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.reports.engine.console.exception.NoSuchDefinitionException;
import com.liferay.portal.reports.engine.console.model.Definition;
import com.liferay.portal.reports.engine.console.service.persistence.DefinitionPersistence;
import com.liferay.portal.reports.engine.console.service.persistence.DefinitionUtil;
import com.liferay.portal.security.permission.SimplePermissionChecker;
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
public class DefinitionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.reports.engine.console.service"));

	@Before
	public void setUp() {
		_persistence = DefinitionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Definition> iterator = _definitions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Definition definition = _persistence.create(pk);

		Assert.assertNotNull(definition);

		Assert.assertEquals(definition.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Definition newDefinition = addDefinition();

		_persistence.remove(newDefinition);

		Definition existingDefinition = _persistence.fetchByPrimaryKey(
			newDefinition.getPrimaryKey());

		Assert.assertNull(existingDefinition);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDefinition();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Definition newDefinition = _persistence.create(pk);

		newDefinition.setUuid(RandomTestUtil.randomString());

		newDefinition.setGroupId(RandomTestUtil.nextLong());

		newDefinition.setCompanyId(RandomTestUtil.nextLong());

		newDefinition.setUserId(RandomTestUtil.nextLong());

		newDefinition.setUserName(RandomTestUtil.randomString());

		newDefinition.setCreateDate(RandomTestUtil.nextDate());

		newDefinition.setModifiedDate(RandomTestUtil.nextDate());

		newDefinition.setName(RandomTestUtil.randomString());

		newDefinition.setDescription(RandomTestUtil.randomString());

		newDefinition.setSourceId(RandomTestUtil.nextLong());

		newDefinition.setReportName(RandomTestUtil.randomString());

		newDefinition.setReportParameters(RandomTestUtil.randomString());

		newDefinition.setLastPublishDate(RandomTestUtil.nextDate());

		_definitions.add(_persistence.update(newDefinition));

		Definition existingDefinition = _persistence.findByPrimaryKey(
			newDefinition.getPrimaryKey());

		Assert.assertEquals(
			existingDefinition.getUuid(), newDefinition.getUuid());
		Assert.assertEquals(
			existingDefinition.getDefinitionId(),
			newDefinition.getDefinitionId());
		Assert.assertEquals(
			existingDefinition.getGroupId(), newDefinition.getGroupId());
		Assert.assertEquals(
			existingDefinition.getCompanyId(), newDefinition.getCompanyId());
		Assert.assertEquals(
			existingDefinition.getUserId(), newDefinition.getUserId());
		Assert.assertEquals(
			existingDefinition.getUserName(), newDefinition.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDefinition.getCreateDate()),
			Time.getShortTimestamp(newDefinition.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDefinition.getModifiedDate()),
			Time.getShortTimestamp(newDefinition.getModifiedDate()));
		Assert.assertEquals(
			existingDefinition.getName(), newDefinition.getName());
		Assert.assertEquals(
			existingDefinition.getDescription(),
			newDefinition.getDescription());
		Assert.assertEquals(
			existingDefinition.getSourceId(), newDefinition.getSourceId());
		Assert.assertEquals(
			existingDefinition.getReportName(), newDefinition.getReportName());
		Assert.assertEquals(
			existingDefinition.getReportParameters(),
			newDefinition.getReportParameters());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDefinition.getLastPublishDate()),
			Time.getShortTimestamp(newDefinition.getLastPublishDate()));
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
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
	public void testFindByPrimaryKeyExisting() throws Exception {
		Definition newDefinition = addDefinition();

		Definition existingDefinition = _persistence.findByPrimaryKey(
			newDefinition.getPrimaryKey());

		Assert.assertEquals(existingDefinition, newDefinition);
	}

	@Test(expected = NoSuchDefinitionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Definition> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Reports_Definition", "uuid", true, "definitionId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "name", true,
			"description", true, "sourceId", true, "reportName", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Definition newDefinition = addDefinition();

		Definition existingDefinition = _persistence.fetchByPrimaryKey(
			newDefinition.getPrimaryKey());

		Assert.assertEquals(existingDefinition, newDefinition);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Definition missingDefinition = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDefinition);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Definition newDefinition1 = addDefinition();
		Definition newDefinition2 = addDefinition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDefinition1.getPrimaryKey());
		primaryKeys.add(newDefinition2.getPrimaryKey());

		Map<Serializable, Definition> definitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, definitions.size());
		Assert.assertEquals(
			newDefinition1, definitions.get(newDefinition1.getPrimaryKey()));
		Assert.assertEquals(
			newDefinition2, definitions.get(newDefinition2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Definition> definitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(definitions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Definition newDefinition = addDefinition();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDefinition.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Definition> definitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, definitions.size());
		Assert.assertEquals(
			newDefinition, definitions.get(newDefinition.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Definition> definitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(definitions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Definition newDefinition = addDefinition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDefinition.getPrimaryKey());

		Map<Serializable, Definition> definitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, definitions.size());
		Assert.assertEquals(
			newDefinition, definitions.get(newDefinition.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Definition newDefinition = addDefinition();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDefinition.getPrimaryKey()));
	}

	private void _assertOriginalValues(Definition definition) {
		Assert.assertEquals(
			definition.getUuid(),
			ReflectionTestUtil.invoke(
				definition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(definition.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				definition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected Definition addDefinition() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Definition definition = _persistence.create(pk);

		definition.setUuid(RandomTestUtil.randomString());

		definition.setGroupId(RandomTestUtil.nextLong());

		definition.setCompanyId(RandomTestUtil.nextLong());

		definition.setUserId(RandomTestUtil.nextLong());

		definition.setUserName(RandomTestUtil.randomString());

		definition.setCreateDate(RandomTestUtil.nextDate());

		definition.setModifiedDate(RandomTestUtil.nextDate());

		definition.setName(RandomTestUtil.randomString());

		definition.setDescription(RandomTestUtil.randomString());

		definition.setSourceId(RandomTestUtil.nextLong());

		definition.setReportName(RandomTestUtil.randomString());

		definition.setReportParameters(RandomTestUtil.randomString());

		definition.setLastPublishDate(RandomTestUtil.nextDate());

		_definitions.add(_persistence.update(definition));

		return definition;
	}

	private List<Definition> _definitions = new ArrayList<Definition>();
	private DefinitionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}