/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectValidationRuleException;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.service.persistence.ObjectValidationRulePersistence;
import com.liferay.object.service.persistence.ObjectValidationRuleUtil;
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
public class ObjectValidationRulePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectValidationRuleUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectValidationRule> iterator =
			_objectValidationRules.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectValidationRule objectValidationRule = _persistence.create(pk);

		Assert.assertNotNull(objectValidationRule);

		Assert.assertEquals(objectValidationRule.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectValidationRule newObjectValidationRule =
			addObjectValidationRule();

		_persistence.remove(newObjectValidationRule);

		ObjectValidationRule existingObjectValidationRule =
			_persistence.fetchByPrimaryKey(
				newObjectValidationRule.getPrimaryKey());

		Assert.assertNull(existingObjectValidationRule);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectValidationRule();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectValidationRule newObjectValidationRule = _persistence.create(pk);

		newObjectValidationRule.setMvccVersion(RandomTestUtil.nextLong());

		newObjectValidationRule.setUuid(RandomTestUtil.randomString());

		newObjectValidationRule.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newObjectValidationRule.setCompanyId(RandomTestUtil.nextLong());

		newObjectValidationRule.setUserId(RandomTestUtil.nextLong());

		newObjectValidationRule.setUserName(RandomTestUtil.randomString());

		newObjectValidationRule.setCreateDate(RandomTestUtil.nextDate());

		newObjectValidationRule.setModifiedDate(RandomTestUtil.nextDate());

		newObjectValidationRule.setObjectDefinitionId(
			RandomTestUtil.nextLong());

		newObjectValidationRule.setActive(RandomTestUtil.randomBoolean());

		newObjectValidationRule.setEngine(RandomTestUtil.randomString());

		newObjectValidationRule.setErrorLabel(RandomTestUtil.randomString());

		newObjectValidationRule.setName(RandomTestUtil.randomString());

		newObjectValidationRule.setOutputType(RandomTestUtil.randomString());

		newObjectValidationRule.setScript(RandomTestUtil.randomString());

		newObjectValidationRule.setSystem(RandomTestUtil.randomBoolean());

		_objectValidationRules.add(
			_persistence.update(newObjectValidationRule));

		ObjectValidationRule existingObjectValidationRule =
			_persistence.findByPrimaryKey(
				newObjectValidationRule.getPrimaryKey());

		Assert.assertEquals(
			existingObjectValidationRule.getMvccVersion(),
			newObjectValidationRule.getMvccVersion());
		Assert.assertEquals(
			existingObjectValidationRule.getUuid(),
			newObjectValidationRule.getUuid());
		Assert.assertEquals(
			existingObjectValidationRule.getExternalReferenceCode(),
			newObjectValidationRule.getExternalReferenceCode());
		Assert.assertEquals(
			existingObjectValidationRule.getObjectValidationRuleId(),
			newObjectValidationRule.getObjectValidationRuleId());
		Assert.assertEquals(
			existingObjectValidationRule.getCompanyId(),
			newObjectValidationRule.getCompanyId());
		Assert.assertEquals(
			existingObjectValidationRule.getUserId(),
			newObjectValidationRule.getUserId());
		Assert.assertEquals(
			existingObjectValidationRule.getUserName(),
			newObjectValidationRule.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectValidationRule.getCreateDate()),
			Time.getShortTimestamp(newObjectValidationRule.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectValidationRule.getModifiedDate()),
			Time.getShortTimestamp(newObjectValidationRule.getModifiedDate()));
		Assert.assertEquals(
			existingObjectValidationRule.getObjectDefinitionId(),
			newObjectValidationRule.getObjectDefinitionId());
		Assert.assertEquals(
			existingObjectValidationRule.isActive(),
			newObjectValidationRule.isActive());
		Assert.assertEquals(
			existingObjectValidationRule.getEngine(),
			newObjectValidationRule.getEngine());
		Assert.assertEquals(
			existingObjectValidationRule.getErrorLabel(),
			newObjectValidationRule.getErrorLabel());
		Assert.assertEquals(
			existingObjectValidationRule.getName(),
			newObjectValidationRule.getName());
		Assert.assertEquals(
			existingObjectValidationRule.getOutputType(),
			newObjectValidationRule.getOutputType());
		Assert.assertEquals(
			existingObjectValidationRule.getScript(),
			newObjectValidationRule.getScript());
		Assert.assertEquals(
			existingObjectValidationRule.isSystem(),
			newObjectValidationRule.isSystem());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByObjectDefinitionId() throws Exception {
		_persistence.countByObjectDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByObjectDefinitionId(0L);
	}

	@Test
	public void testCountByODI_A() throws Exception {
		_persistence.countByODI_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByODI_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByODI_E() throws Exception {
		_persistence.countByODI_E(RandomTestUtil.nextLong(), "");

		_persistence.countByODI_E(0L, "null");

		_persistence.countByODI_E(0L, (String)null);
	}

	@Test
	public void testCountByODI_O() throws Exception {
		_persistence.countByODI_O(RandomTestUtil.nextLong(), "");

		_persistence.countByODI_O(0L, "null");

		_persistence.countByODI_O(0L, (String)null);
	}

	@Test
	public void testCountByA_E() throws Exception {
		_persistence.countByA_E(RandomTestUtil.randomBoolean(), "");

		_persistence.countByA_E(RandomTestUtil.randomBoolean(), "null");

		_persistence.countByA_E(RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByERC_C_ODI() throws Exception {
		_persistence.countByERC_C_ODI(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByERC_C_ODI("null", 0L, 0L);

		_persistence.countByERC_C_ODI((String)null, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectValidationRule newObjectValidationRule =
			addObjectValidationRule();

		ObjectValidationRule existingObjectValidationRule =
			_persistence.findByPrimaryKey(
				newObjectValidationRule.getPrimaryKey());

		Assert.assertEquals(
			existingObjectValidationRule, newObjectValidationRule);
	}

	@Test(expected = NoSuchObjectValidationRuleException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectValidationRule> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectValidationRule", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "objectValidationRuleId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "objectDefinitionId", true, "active",
			true, "engine", true, "errorLabel", true, "name", true,
			"outputType", true, "system", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectValidationRule newObjectValidationRule =
			addObjectValidationRule();

		ObjectValidationRule existingObjectValidationRule =
			_persistence.fetchByPrimaryKey(
				newObjectValidationRule.getPrimaryKey());

		Assert.assertEquals(
			existingObjectValidationRule, newObjectValidationRule);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectValidationRule missingObjectValidationRule =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectValidationRule);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectValidationRule newObjectValidationRule1 =
			addObjectValidationRule();
		ObjectValidationRule newObjectValidationRule2 =
			addObjectValidationRule();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectValidationRule1.getPrimaryKey());
		primaryKeys.add(newObjectValidationRule2.getPrimaryKey());

		Map<Serializable, ObjectValidationRule> objectValidationRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectValidationRules.size());
		Assert.assertEquals(
			newObjectValidationRule1,
			objectValidationRules.get(
				newObjectValidationRule1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectValidationRule2,
			objectValidationRules.get(
				newObjectValidationRule2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectValidationRule> objectValidationRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectValidationRules.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectValidationRule newObjectValidationRule =
			addObjectValidationRule();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectValidationRule.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectValidationRule> objectValidationRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectValidationRules.size());
		Assert.assertEquals(
			newObjectValidationRule,
			objectValidationRules.get(newObjectValidationRule.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectValidationRule> objectValidationRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectValidationRules.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectValidationRule newObjectValidationRule =
			addObjectValidationRule();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectValidationRule.getPrimaryKey());

		Map<Serializable, ObjectValidationRule> objectValidationRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectValidationRules.size());
		Assert.assertEquals(
			newObjectValidationRule,
			objectValidationRules.get(newObjectValidationRule.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectValidationRule newObjectValidationRule =
			addObjectValidationRule();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newObjectValidationRule.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		ObjectValidationRule objectValidationRule) {

		Assert.assertEquals(
			objectValidationRule.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				objectValidationRule, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(objectValidationRule.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				objectValidationRule, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(objectValidationRule.getObjectDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				objectValidationRule, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectDefinitionId"));
	}

	protected ObjectValidationRule addObjectValidationRule() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectValidationRule objectValidationRule = _persistence.create(pk);

		objectValidationRule.setMvccVersion(RandomTestUtil.nextLong());

		objectValidationRule.setUuid(RandomTestUtil.randomString());

		objectValidationRule.setExternalReferenceCode(
			RandomTestUtil.randomString());

		objectValidationRule.setCompanyId(RandomTestUtil.nextLong());

		objectValidationRule.setUserId(RandomTestUtil.nextLong());

		objectValidationRule.setUserName(RandomTestUtil.randomString());

		objectValidationRule.setCreateDate(RandomTestUtil.nextDate());

		objectValidationRule.setModifiedDate(RandomTestUtil.nextDate());

		objectValidationRule.setObjectDefinitionId(RandomTestUtil.nextLong());

		objectValidationRule.setActive(RandomTestUtil.randomBoolean());

		objectValidationRule.setEngine(RandomTestUtil.randomString());

		objectValidationRule.setErrorLabel(RandomTestUtil.randomString());

		objectValidationRule.setName(RandomTestUtil.randomString());

		objectValidationRule.setOutputType(RandomTestUtil.randomString());

		objectValidationRule.setScript(RandomTestUtil.randomString());

		objectValidationRule.setSystem(RandomTestUtil.randomBoolean());

		_objectValidationRules.add(_persistence.update(objectValidationRule));

		return objectValidationRule;
	}

	private List<ObjectValidationRule> _objectValidationRules =
		new ArrayList<ObjectValidationRule>();
	private ObjectValidationRulePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}