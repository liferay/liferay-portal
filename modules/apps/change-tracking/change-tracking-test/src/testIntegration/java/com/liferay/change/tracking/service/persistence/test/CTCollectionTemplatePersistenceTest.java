/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.exception.NoSuchCollectionTemplateException;
import com.liferay.change.tracking.model.CTCollectionTemplate;
import com.liferay.change.tracking.service.CTCollectionTemplateLocalServiceUtil;
import com.liferay.change.tracking.service.persistence.CTCollectionTemplatePersistence;
import com.liferay.change.tracking.service.persistence.CTCollectionTemplateUtil;
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
public class CTCollectionTemplatePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.change.tracking.service"));

	@Before
	public void setUp() {
		_persistence = CTCollectionTemplateUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTCollectionTemplate> iterator =
			_ctCollectionTemplates.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTCollectionTemplate ctCollectionTemplate = _persistence.create(pk);

		Assert.assertNotNull(ctCollectionTemplate);

		Assert.assertEquals(ctCollectionTemplate.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTCollectionTemplate newCTCollectionTemplate =
			addCTCollectionTemplate();

		_persistence.remove(newCTCollectionTemplate);

		CTCollectionTemplate existingCTCollectionTemplate =
			_persistence.fetchByPrimaryKey(
				newCTCollectionTemplate.getPrimaryKey());

		Assert.assertNull(existingCTCollectionTemplate);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTCollectionTemplate();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CTCollectionTemplate newCTCollectionTemplate =
			addCTCollectionTemplate();

		newCTCollectionTemplate.setCompanyId(RandomTestUtil.nextLong());

		newCTCollectionTemplate.setUserId(RandomTestUtil.nextLong());

		newCTCollectionTemplate.setCreateDate(RandomTestUtil.nextDate());

		newCTCollectionTemplate.setModifiedDate(RandomTestUtil.nextDate());

		newCTCollectionTemplate.setName(RandomTestUtil.randomString());

		newCTCollectionTemplate.setDescription(RandomTestUtil.randomString());

		newCTCollectionTemplate = _persistence.update(newCTCollectionTemplate);

		_ctCollectionTemplates.add(newCTCollectionTemplate);

		CTCollectionTemplate existingCTCollectionTemplate =
			_persistence.findByPrimaryKey(
				newCTCollectionTemplate.getPrimaryKey());

		Assert.assertEquals(
			existingCTCollectionTemplate.getMvccVersion(),
			newCTCollectionTemplate.getMvccVersion());
		Assert.assertEquals(
			existingCTCollectionTemplate.getCtCollectionTemplateId(),
			newCTCollectionTemplate.getCtCollectionTemplateId());
		Assert.assertEquals(
			existingCTCollectionTemplate.getCompanyId(),
			newCTCollectionTemplate.getCompanyId());
		Assert.assertEquals(
			existingCTCollectionTemplate.getUserId(),
			newCTCollectionTemplate.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCTCollectionTemplate.getCreateDate()),
			Time.getShortTimestamp(newCTCollectionTemplate.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCTCollectionTemplate.getModifiedDate()),
			Time.getShortTimestamp(newCTCollectionTemplate.getModifiedDate()));
		Assert.assertEquals(
			existingCTCollectionTemplate.getName(),
			newCTCollectionTemplate.getName());
		Assert.assertEquals(
			existingCTCollectionTemplate.getDescription(),
			newCTCollectionTemplate.getDescription());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTCollectionTemplate newCTCollectionTemplate =
			addCTCollectionTemplate();

		CTCollectionTemplate existingCTCollectionTemplate =
			_persistence.findByPrimaryKey(
				newCTCollectionTemplate.getPrimaryKey());

		Assert.assertEquals(
			existingCTCollectionTemplate, newCTCollectionTemplate);
	}

	@Test(expected = NoSuchCollectionTemplateException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTCollectionTemplate> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTCollectionTemplate", "mvccVersion", true,
			"ctCollectionTemplateId", true, "companyId", true, "userId", true,
			"createDate", true, "modifiedDate", true, "name", true,
			"description", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTCollectionTemplate newCTCollectionTemplate =
			addCTCollectionTemplate();

		CTCollectionTemplate existingCTCollectionTemplate =
			_persistence.fetchByPrimaryKey(
				newCTCollectionTemplate.getPrimaryKey());

		Assert.assertEquals(
			existingCTCollectionTemplate, newCTCollectionTemplate);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTCollectionTemplate missingCTCollectionTemplate =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTCollectionTemplate);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTCollectionTemplate newCTCollectionTemplate1 =
			addCTCollectionTemplate();
		CTCollectionTemplate newCTCollectionTemplate2 =
			addCTCollectionTemplate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTCollectionTemplate1.getPrimaryKey());
		primaryKeys.add(newCTCollectionTemplate2.getPrimaryKey());

		Map<Serializable, CTCollectionTemplate> ctCollectionTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ctCollectionTemplates.size());
		Assert.assertEquals(
			newCTCollectionTemplate1,
			ctCollectionTemplates.get(
				newCTCollectionTemplate1.getPrimaryKey()));
		Assert.assertEquals(
			newCTCollectionTemplate2,
			ctCollectionTemplates.get(
				newCTCollectionTemplate2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTCollectionTemplate> ctCollectionTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctCollectionTemplates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTCollectionTemplate newCTCollectionTemplate =
			addCTCollectionTemplate();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTCollectionTemplate.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTCollectionTemplate> ctCollectionTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctCollectionTemplates.size());
		Assert.assertEquals(
			newCTCollectionTemplate,
			ctCollectionTemplates.get(newCTCollectionTemplate.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTCollectionTemplate> ctCollectionTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctCollectionTemplates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTCollectionTemplate newCTCollectionTemplate =
			addCTCollectionTemplate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTCollectionTemplate.getPrimaryKey());

		Map<Serializable, CTCollectionTemplate> ctCollectionTemplates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctCollectionTemplates.size());
		Assert.assertEquals(
			newCTCollectionTemplate,
			ctCollectionTemplates.get(newCTCollectionTemplate.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CTCollectionTemplateLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CTCollectionTemplate>() {

				@Override
				public void performAction(
					CTCollectionTemplate ctCollectionTemplate) {

					Assert.assertNotNull(ctCollectionTemplate);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CTCollectionTemplate newCTCollectionTemplate =
			addCTCollectionTemplate();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTCollectionTemplate.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctCollectionTemplateId",
				newCTCollectionTemplate.getCtCollectionTemplateId()));

		List<CTCollectionTemplate> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CTCollectionTemplate existingCTCollectionTemplate = result.get(0);

		Assert.assertEquals(
			existingCTCollectionTemplate, newCTCollectionTemplate);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTCollectionTemplate.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctCollectionTemplateId", RandomTestUtil.nextLong()));

		List<CTCollectionTemplate> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CTCollectionTemplate newCTCollectionTemplate =
			addCTCollectionTemplate();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTCollectionTemplate.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ctCollectionTemplateId"));

		Object newCtCollectionTemplateId =
			newCTCollectionTemplate.getCtCollectionTemplateId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ctCollectionTemplateId",
				new Object[] {newCtCollectionTemplateId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCtCollectionTemplateId = result.get(0);

		Assert.assertEquals(
			existingCtCollectionTemplateId, newCtCollectionTemplateId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTCollectionTemplate.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ctCollectionTemplateId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ctCollectionTemplateId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected CTCollectionTemplate addCTCollectionTemplate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTCollectionTemplate ctCollectionTemplate = _persistence.create(pk);

		ctCollectionTemplate.setCompanyId(RandomTestUtil.nextLong());

		ctCollectionTemplate.setUserId(RandomTestUtil.nextLong());

		ctCollectionTemplate.setCreateDate(RandomTestUtil.nextDate());

		ctCollectionTemplate.setModifiedDate(RandomTestUtil.nextDate());

		ctCollectionTemplate.setName(RandomTestUtil.randomString());

		ctCollectionTemplate.setDescription(RandomTestUtil.randomString());

		_ctCollectionTemplates.add(_persistence.update(ctCollectionTemplate));

		return ctCollectionTemplate;
	}

	private List<CTCollectionTemplate> _ctCollectionTemplates =
		new ArrayList<CTCollectionTemplate>();
	private CTCollectionTemplatePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1195204366