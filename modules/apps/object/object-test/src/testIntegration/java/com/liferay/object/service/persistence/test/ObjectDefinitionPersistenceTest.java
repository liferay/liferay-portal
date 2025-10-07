/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.DuplicateObjectDefinitionExternalReferenceCodeException;
import com.liferay.object.exception.NoSuchObjectDefinitionException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.object.service.persistence.ObjectDefinitionUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
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
public class ObjectDefinitionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectDefinitionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectDefinition> iterator = _objectDefinitions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectDefinition objectDefinition = _persistence.create(pk);

		Assert.assertNotNull(objectDefinition);

		Assert.assertEquals(objectDefinition.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectDefinition newObjectDefinition = addObjectDefinition();

		_persistence.remove(newObjectDefinition);

		ObjectDefinition existingObjectDefinition =
			_persistence.fetchByPrimaryKey(newObjectDefinition.getPrimaryKey());

		Assert.assertNull(existingObjectDefinition);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectDefinition();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectDefinition newObjectDefinition = _persistence.create(pk);

		newObjectDefinition.setMvccVersion(RandomTestUtil.nextLong());

		newObjectDefinition.setUuid(RandomTestUtil.randomString());

		newObjectDefinition.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newObjectDefinition.setCompanyId(RandomTestUtil.nextLong());

		newObjectDefinition.setUserId(RandomTestUtil.nextLong());

		newObjectDefinition.setUserName(RandomTestUtil.randomString());

		newObjectDefinition.setCreateDate(RandomTestUtil.nextDate());

		newObjectDefinition.setModifiedDate(RandomTestUtil.nextDate());

		newObjectDefinition.setAccountEntryRestrictedObjectFieldId(
			RandomTestUtil.nextLong());

		newObjectDefinition.setDescriptionObjectFieldId(
			RandomTestUtil.nextLong());

		newObjectDefinition.setObjectFolderId(RandomTestUtil.nextLong());

		newObjectDefinition.setTitleObjectFieldId(RandomTestUtil.nextLong());

		newObjectDefinition.setAccountEntryRestricted(
			RandomTestUtil.randomBoolean());

		newObjectDefinition.setActive(RandomTestUtil.randomBoolean());

		newObjectDefinition.setClassName(RandomTestUtil.randomString());

		newObjectDefinition.setDBTableName(RandomTestUtil.randomString());

		newObjectDefinition.setEnableCategorization(
			RandomTestUtil.randomBoolean());

		newObjectDefinition.setEnableComments(RandomTestUtil.randomBoolean());

		newObjectDefinition.setEnableFormContainer(
			RandomTestUtil.randomBoolean());

		newObjectDefinition.setEnableFriendlyURLCustomization(
			RandomTestUtil.randomBoolean());

		newObjectDefinition.setEnableIndexSearch(
			RandomTestUtil.randomBoolean());

		newObjectDefinition.setEnableLocalization(
			RandomTestUtil.randomBoolean());

		newObjectDefinition.setEnableObjectEntryDraft(
			RandomTestUtil.randomBoolean());

		newObjectDefinition.setEnableObjectEntryHistory(
			RandomTestUtil.randomBoolean());

		newObjectDefinition.setEnableObjectEntrySchedule(
			RandomTestUtil.randomBoolean());

		newObjectDefinition.setEnableObjectEntrySubscription(
			RandomTestUtil.randomBoolean());

		newObjectDefinition.setEnableObjectEntryVersioning(
			RandomTestUtil.randomBoolean());

		newObjectDefinition.setFriendlyURLSeparator(
			RandomTestUtil.randomString());

		newObjectDefinition.setLabel(RandomTestUtil.randomString());

		newObjectDefinition.setModifiable(RandomTestUtil.randomBoolean());

		newObjectDefinition.setName(RandomTestUtil.randomString());

		newObjectDefinition.setPanelAppOrder(RandomTestUtil.randomString());

		newObjectDefinition.setPanelCategoryKey(RandomTestUtil.randomString());

		newObjectDefinition.setPKObjectFieldDBColumnName(
			RandomTestUtil.randomString());

		newObjectDefinition.setPKObjectFieldName(RandomTestUtil.randomString());

		newObjectDefinition.setPluralLabel(RandomTestUtil.randomString());

		newObjectDefinition.setPortlet(RandomTestUtil.randomBoolean());

		newObjectDefinition.setScope(RandomTestUtil.randomString());

		newObjectDefinition.setStorageType(RandomTestUtil.randomString());

		newObjectDefinition.setSystem(RandomTestUtil.randomBoolean());

		newObjectDefinition.setVersion(RandomTestUtil.nextInt());

		newObjectDefinition.setStatus(RandomTestUtil.nextInt());

		_objectDefinitions.add(_persistence.update(newObjectDefinition));

		ObjectDefinition existingObjectDefinition =
			_persistence.findByPrimaryKey(newObjectDefinition.getPrimaryKey());

		Assert.assertEquals(
			existingObjectDefinition.getMvccVersion(),
			newObjectDefinition.getMvccVersion());
		Assert.assertEquals(
			existingObjectDefinition.getUuid(), newObjectDefinition.getUuid());
		Assert.assertEquals(
			existingObjectDefinition.getExternalReferenceCode(),
			newObjectDefinition.getExternalReferenceCode());
		Assert.assertEquals(
			existingObjectDefinition.getObjectDefinitionId(),
			newObjectDefinition.getObjectDefinitionId());
		Assert.assertEquals(
			existingObjectDefinition.getCompanyId(),
			newObjectDefinition.getCompanyId());
		Assert.assertEquals(
			existingObjectDefinition.getUserId(),
			newObjectDefinition.getUserId());
		Assert.assertEquals(
			existingObjectDefinition.getUserName(),
			newObjectDefinition.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectDefinition.getCreateDate()),
			Time.getShortTimestamp(newObjectDefinition.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectDefinition.getModifiedDate()),
			Time.getShortTimestamp(newObjectDefinition.getModifiedDate()));
		Assert.assertEquals(
			existingObjectDefinition.getAccountEntryRestrictedObjectFieldId(),
			newObjectDefinition.getAccountEntryRestrictedObjectFieldId());
		Assert.assertEquals(
			existingObjectDefinition.getDescriptionObjectFieldId(),
			newObjectDefinition.getDescriptionObjectFieldId());
		Assert.assertEquals(
			existingObjectDefinition.getObjectFolderId(),
			newObjectDefinition.getObjectFolderId());
		Assert.assertEquals(
			existingObjectDefinition.getTitleObjectFieldId(),
			newObjectDefinition.getTitleObjectFieldId());
		Assert.assertEquals(
			existingObjectDefinition.isAccountEntryRestricted(),
			newObjectDefinition.isAccountEntryRestricted());
		Assert.assertEquals(
			existingObjectDefinition.isActive(),
			newObjectDefinition.isActive());
		Assert.assertEquals(
			existingObjectDefinition.getClassName(),
			newObjectDefinition.getClassName());
		Assert.assertEquals(
			existingObjectDefinition.getDBTableName(),
			newObjectDefinition.getDBTableName());
		Assert.assertEquals(
			existingObjectDefinition.isEnableCategorization(),
			newObjectDefinition.isEnableCategorization());
		Assert.assertEquals(
			existingObjectDefinition.isEnableComments(),
			newObjectDefinition.isEnableComments());
		Assert.assertEquals(
			existingObjectDefinition.isEnableFormContainer(),
			newObjectDefinition.isEnableFormContainer());
		Assert.assertEquals(
			existingObjectDefinition.isEnableFriendlyURLCustomization(),
			newObjectDefinition.isEnableFriendlyURLCustomization());
		Assert.assertEquals(
			existingObjectDefinition.isEnableIndexSearch(),
			newObjectDefinition.isEnableIndexSearch());
		Assert.assertEquals(
			existingObjectDefinition.isEnableLocalization(),
			newObjectDefinition.isEnableLocalization());
		Assert.assertEquals(
			existingObjectDefinition.isEnableObjectEntryDraft(),
			newObjectDefinition.isEnableObjectEntryDraft());
		Assert.assertEquals(
			existingObjectDefinition.isEnableObjectEntryHistory(),
			newObjectDefinition.isEnableObjectEntryHistory());
		Assert.assertEquals(
			existingObjectDefinition.isEnableObjectEntrySchedule(),
			newObjectDefinition.isEnableObjectEntrySchedule());
		Assert.assertEquals(
			existingObjectDefinition.isEnableObjectEntrySubscription(),
			newObjectDefinition.isEnableObjectEntrySubscription());
		Assert.assertEquals(
			existingObjectDefinition.isEnableObjectEntryVersioning(),
			newObjectDefinition.isEnableObjectEntryVersioning());
		Assert.assertEquals(
			existingObjectDefinition.getFriendlyURLSeparator(),
			newObjectDefinition.getFriendlyURLSeparator());
		Assert.assertEquals(
			existingObjectDefinition.getLabel(),
			newObjectDefinition.getLabel());
		Assert.assertEquals(
			existingObjectDefinition.isModifiable(),
			newObjectDefinition.isModifiable());
		Assert.assertEquals(
			existingObjectDefinition.getName(), newObjectDefinition.getName());
		Assert.assertEquals(
			existingObjectDefinition.getPanelAppOrder(),
			newObjectDefinition.getPanelAppOrder());
		Assert.assertEquals(
			existingObjectDefinition.getPanelCategoryKey(),
			newObjectDefinition.getPanelCategoryKey());
		Assert.assertEquals(
			existingObjectDefinition.getPKObjectFieldDBColumnName(),
			newObjectDefinition.getPKObjectFieldDBColumnName());
		Assert.assertEquals(
			existingObjectDefinition.getPKObjectFieldName(),
			newObjectDefinition.getPKObjectFieldName());
		Assert.assertEquals(
			existingObjectDefinition.getPluralLabel(),
			newObjectDefinition.getPluralLabel());
		Assert.assertEquals(
			existingObjectDefinition.isPortlet(),
			newObjectDefinition.isPortlet());
		Assert.assertEquals(
			existingObjectDefinition.getScope(),
			newObjectDefinition.getScope());
		Assert.assertEquals(
			existingObjectDefinition.getStorageType(),
			newObjectDefinition.getStorageType());
		Assert.assertEquals(
			existingObjectDefinition.isSystem(),
			newObjectDefinition.isSystem());
		Assert.assertEquals(
			existingObjectDefinition.getVersion(),
			newObjectDefinition.getVersion());
		Assert.assertEquals(
			existingObjectDefinition.getStatus(),
			newObjectDefinition.getStatus());
	}

	@Test(
		expected = DuplicateObjectDefinitionExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		ObjectDefinition objectDefinition = addObjectDefinition();

		ObjectDefinition newObjectDefinition = addObjectDefinition();

		newObjectDefinition.setCompanyId(objectDefinition.getCompanyId());

		newObjectDefinition = _persistence.update(newObjectDefinition);

		Session session = _persistence.getCurrentSession();

		session.evict(newObjectDefinition);

		newObjectDefinition.setExternalReferenceCode(
			objectDefinition.getExternalReferenceCode());

		_persistence.update(newObjectDefinition);
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByObjectFolderId() throws Exception {
		_persistence.countByObjectFolderId(RandomTestUtil.nextLong());

		_persistence.countByObjectFolderId(0L);
	}

	@Test
	public void testCountByAccountEntryRestricted() throws Exception {
		_persistence.countByAccountEntryRestricted(
			RandomTestUtil.randomBoolean());

		_persistence.countByAccountEntryRestricted(
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByClassName() throws Exception {
		_persistence.countByClassName("");

		_persistence.countByClassName("null");

		_persistence.countByClassName((String)null);
	}

	@Test
	public void testCountBySystem() throws Exception {
		_persistence.countBySystem(RandomTestUtil.randomBoolean());

		_persistence.countBySystem(RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_U() throws Exception {
		_persistence.countByC_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_U(0L, 0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(RandomTestUtil.nextLong(), "");

		_persistence.countByC_C(0L, "null");

		_persistence.countByC_C(0L, (String)null);
	}

	@Test
	public void testCountByC_N() throws Exception {
		_persistence.countByC_N(RandomTestUtil.nextLong(), "");

		_persistence.countByC_N(0L, "null");

		_persistence.countByC_N(0L, (String)null);
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_S(0L, 0);
	}

	@Test
	public void testCountByS_S() throws Exception {
		_persistence.countByS_S(
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());

		_persistence.countByS_S(RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByC_A_S() throws Exception {
		_persistence.countByC_A_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByC_A_S(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByC_M_S() throws Exception {
		_persistence.countByC_M_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean());

		_persistence.countByC_M_S(
			0L, RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_A_S_S() throws Exception {
		_persistence.countByC_A_S_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());

		_persistence.countByC_A_S_S(
			0L, RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			0);
	}

	@Test
	public void testCountByC_OFI_A_E_S_S() throws Exception {
		_persistence.countByC_OFI_A_E_S_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(), "",
			RandomTestUtil.nextInt());

		_persistence.countByC_OFI_A_E_S_S(
			0L, 0L, RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), "null", 0);

		_persistence.countByC_OFI_A_E_S_S(
			0L, 0L, RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), (String)null, 0);
	}

	@Test
	public void testCountByC_OFI_A_E_S_SArrayable() throws Exception {
		_persistence.countByC_OFI_A_E_S_S(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomString(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectDefinition newObjectDefinition = addObjectDefinition();

		ObjectDefinition existingObjectDefinition =
			_persistence.findByPrimaryKey(newObjectDefinition.getPrimaryKey());

		Assert.assertEquals(existingObjectDefinition, newObjectDefinition);
	}

	@Test(expected = NoSuchObjectDefinitionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectDefinition> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectDefinition", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "objectDefinitionId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "accountEntryRestrictedObjectFieldId",
			true, "descriptionObjectFieldId", true, "objectFolderId", true,
			"titleObjectFieldId", true, "accountEntryRestricted", true,
			"active", true, "className", true, "dbTableName", true,
			"enableCategorization", true, "enableComments", true,
			"enableFormContainer", true, "enableFriendlyURLCustomization", true,
			"enableIndexSearch", true, "enableLocalization", true,
			"enableObjectEntryDraft", true, "enableObjectEntryHistory", true,
			"enableObjectEntrySchedule", true, "enableObjectEntrySubscription",
			true, "enableObjectEntryVersioning", true, "friendlyURLSeparator",
			true, "label", true, "modifiable", true, "name", true,
			"panelAppOrder", true, "panelCategoryKey", true,
			"pkObjectFieldDBColumnName", true, "pkObjectFieldName", true,
			"pluralLabel", true, "portlet", true, "scope", true, "storageType",
			true, "system", true, "version", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectDefinition newObjectDefinition = addObjectDefinition();

		ObjectDefinition existingObjectDefinition =
			_persistence.fetchByPrimaryKey(newObjectDefinition.getPrimaryKey());

		Assert.assertEquals(existingObjectDefinition, newObjectDefinition);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectDefinition missingObjectDefinition =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectDefinition);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectDefinition newObjectDefinition1 = addObjectDefinition();
		ObjectDefinition newObjectDefinition2 = addObjectDefinition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectDefinition1.getPrimaryKey());
		primaryKeys.add(newObjectDefinition2.getPrimaryKey());

		Map<Serializable, ObjectDefinition> objectDefinitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectDefinitions.size());
		Assert.assertEquals(
			newObjectDefinition1,
			objectDefinitions.get(newObjectDefinition1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectDefinition2,
			objectDefinitions.get(newObjectDefinition2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectDefinition> objectDefinitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectDefinitions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectDefinition newObjectDefinition = addObjectDefinition();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectDefinition.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectDefinition> objectDefinitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectDefinitions.size());
		Assert.assertEquals(
			newObjectDefinition,
			objectDefinitions.get(newObjectDefinition.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectDefinition> objectDefinitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectDefinitions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectDefinition newObjectDefinition = addObjectDefinition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectDefinition.getPrimaryKey());

		Map<Serializable, ObjectDefinition> objectDefinitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectDefinitions.size());
		Assert.assertEquals(
			newObjectDefinition,
			objectDefinitions.get(newObjectDefinition.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ObjectDefinitionLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<ObjectDefinition>() {

				@Override
				public void performAction(ObjectDefinition objectDefinition) {
					Assert.assertNotNull(objectDefinition);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ObjectDefinition newObjectDefinition = addObjectDefinition();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectDefinition.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectDefinitionId",
				newObjectDefinition.getObjectDefinitionId()));

		List<ObjectDefinition> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ObjectDefinition existingObjectDefinition = result.get(0);

		Assert.assertEquals(existingObjectDefinition, newObjectDefinition);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectDefinition.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectDefinitionId", RandomTestUtil.nextLong()));

		List<ObjectDefinition> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ObjectDefinition newObjectDefinition = addObjectDefinition();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectDefinition.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectDefinitionId"));

		Object newObjectDefinitionId =
			newObjectDefinition.getObjectDefinitionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectDefinitionId", new Object[] {newObjectDefinitionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingObjectDefinitionId = result.get(0);

		Assert.assertEquals(existingObjectDefinitionId, newObjectDefinitionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectDefinition.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectDefinitionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectDefinitionId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectDefinition newObjectDefinition = addObjectDefinition();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newObjectDefinition.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		ObjectDefinition newObjectDefinition = addObjectDefinition();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectDefinition.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectDefinitionId",
				newObjectDefinition.getObjectDefinitionId()));

		List<ObjectDefinition> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ObjectDefinition objectDefinition) {
		Assert.assertEquals(
			objectDefinition.getClassName(),
			ReflectionTestUtil.invoke(
				objectDefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "className"));

		Assert.assertEquals(
			Long.valueOf(objectDefinition.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				objectDefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			objectDefinition.getClassName(),
			ReflectionTestUtil.invoke(
				objectDefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "className"));

		Assert.assertEquals(
			Long.valueOf(objectDefinition.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				objectDefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			objectDefinition.getName(),
			ReflectionTestUtil.invoke(
				objectDefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			objectDefinition.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				objectDefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(objectDefinition.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				objectDefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected ObjectDefinition addObjectDefinition() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectDefinition objectDefinition = _persistence.create(pk);

		objectDefinition.setMvccVersion(RandomTestUtil.nextLong());

		objectDefinition.setUuid(RandomTestUtil.randomString());

		objectDefinition.setExternalReferenceCode(
			RandomTestUtil.randomString());

		objectDefinition.setCompanyId(RandomTestUtil.nextLong());

		objectDefinition.setUserId(RandomTestUtil.nextLong());

		objectDefinition.setUserName(RandomTestUtil.randomString());

		objectDefinition.setCreateDate(RandomTestUtil.nextDate());

		objectDefinition.setModifiedDate(RandomTestUtil.nextDate());

		objectDefinition.setAccountEntryRestrictedObjectFieldId(
			RandomTestUtil.nextLong());

		objectDefinition.setDescriptionObjectFieldId(RandomTestUtil.nextLong());

		objectDefinition.setObjectFolderId(RandomTestUtil.nextLong());

		objectDefinition.setTitleObjectFieldId(RandomTestUtil.nextLong());

		objectDefinition.setAccountEntryRestricted(
			RandomTestUtil.randomBoolean());

		objectDefinition.setActive(RandomTestUtil.randomBoolean());

		objectDefinition.setClassName(RandomTestUtil.randomString());

		objectDefinition.setDBTableName(RandomTestUtil.randomString());

		objectDefinition.setEnableCategorization(
			RandomTestUtil.randomBoolean());

		objectDefinition.setEnableComments(RandomTestUtil.randomBoolean());

		objectDefinition.setEnableFormContainer(RandomTestUtil.randomBoolean());

		objectDefinition.setEnableFriendlyURLCustomization(
			RandomTestUtil.randomBoolean());

		objectDefinition.setEnableIndexSearch(RandomTestUtil.randomBoolean());

		objectDefinition.setEnableLocalization(RandomTestUtil.randomBoolean());

		objectDefinition.setEnableObjectEntryDraft(
			RandomTestUtil.randomBoolean());

		objectDefinition.setEnableObjectEntryHistory(
			RandomTestUtil.randomBoolean());

		objectDefinition.setEnableObjectEntrySchedule(
			RandomTestUtil.randomBoolean());

		objectDefinition.setEnableObjectEntrySubscription(
			RandomTestUtil.randomBoolean());

		objectDefinition.setEnableObjectEntryVersioning(
			RandomTestUtil.randomBoolean());

		objectDefinition.setFriendlyURLSeparator(RandomTestUtil.randomString());

		objectDefinition.setLabel(RandomTestUtil.randomString());

		objectDefinition.setModifiable(RandomTestUtil.randomBoolean());

		objectDefinition.setName(RandomTestUtil.randomString());

		objectDefinition.setPanelAppOrder(RandomTestUtil.randomString());

		objectDefinition.setPanelCategoryKey(RandomTestUtil.randomString());

		objectDefinition.setPKObjectFieldDBColumnName(
			RandomTestUtil.randomString());

		objectDefinition.setPKObjectFieldName(RandomTestUtil.randomString());

		objectDefinition.setPluralLabel(RandomTestUtil.randomString());

		objectDefinition.setPortlet(RandomTestUtil.randomBoolean());

		objectDefinition.setScope(RandomTestUtil.randomString());

		objectDefinition.setStorageType(RandomTestUtil.randomString());

		objectDefinition.setSystem(RandomTestUtil.randomBoolean());

		objectDefinition.setVersion(RandomTestUtil.nextInt());

		objectDefinition.setStatus(RandomTestUtil.nextInt());

		_objectDefinitions.add(_persistence.update(objectDefinition));

		return objectDefinition;
	}

	private List<ObjectDefinition> _objectDefinitions =
		new ArrayList<ObjectDefinition>();
	private ObjectDefinitionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}