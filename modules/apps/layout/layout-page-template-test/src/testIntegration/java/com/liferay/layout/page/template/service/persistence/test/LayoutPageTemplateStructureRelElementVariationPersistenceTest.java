/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateStructureRelElementVariationExternalReferenceCodeException;
import com.liferay.layout.page.template.exception.NoSuchPageTemplateStructureRelElementVariationException;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationLocalServiceUtil;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelElementVariationPersistence;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelElementVariationUtil;
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
public class LayoutPageTemplateStructureRelElementVariationPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.layout.page.template.service"));

	@Before
	public void setUp() {
		_persistence =
			LayoutPageTemplateStructureRelElementVariationUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LayoutPageTemplateStructureRelElementVariation> iterator =
			_layoutPageTemplateStructureRelElementVariations.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation =
				_persistence.create(pk);

		Assert.assertNotNull(layoutPageTemplateStructureRelElementVariation);

		Assert.assertEquals(
			layoutPageTemplateStructureRelElementVariation.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LayoutPageTemplateStructureRelElementVariation
			newLayoutPageTemplateStructureRelElementVariation =
				addLayoutPageTemplateStructureRelElementVariation();

		_persistence.remove(newLayoutPageTemplateStructureRelElementVariation);

		LayoutPageTemplateStructureRelElementVariation
			existingLayoutPageTemplateStructureRelElementVariation =
				_persistence.fetchByPrimaryKey(
					newLayoutPageTemplateStructureRelElementVariation.
						getPrimaryKey());

		Assert.assertNull(
			existingLayoutPageTemplateStructureRelElementVariation);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayoutPageTemplateStructureRelElementVariation();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		LayoutPageTemplateStructureRelElementVariation
			newLayoutPageTemplateStructureRelElementVariation =
				addLayoutPageTemplateStructureRelElementVariation();

		newLayoutPageTemplateStructureRelElementVariation.setCtCollectionId(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRelElementVariation.setUuid(
			RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariation.
			setExternalReferenceCode(RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariation.setGroupId(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRelElementVariation.setCompanyId(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRelElementVariation.setUserId(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRelElementVariation.setUserName(
			RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariation.setCreateDate(
			RandomTestUtil.nextDate());

		newLayoutPageTemplateStructureRelElementVariation.setModifiedDate(
			RandomTestUtil.nextDate());

		newLayoutPageTemplateStructureRelElementVariation.setActive(
			RandomTestUtil.randomBoolean());

		newLayoutPageTemplateStructureRelElementVariation.setHide(
			RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariation.setHtml(
			RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariation.setJs(
			RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariation.setName(
			RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariation.setPlid(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRelElementVariation.
			setSegmentsExperienceERC(RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariation.setTargetElement(
			RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariation = _persistence.update(
			newLayoutPageTemplateStructureRelElementVariation);

		_layoutPageTemplateStructureRelElementVariations.add(
			newLayoutPageTemplateStructureRelElementVariation);

		LayoutPageTemplateStructureRelElementVariation
			existingLayoutPageTemplateStructureRelElementVariation =
				_persistence.findByPrimaryKey(
					newLayoutPageTemplateStructureRelElementVariation.
						getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.
				getMvccVersion(),
			newLayoutPageTemplateStructureRelElementVariation.getMvccVersion());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.
				getCtCollectionId(),
			newLayoutPageTemplateStructureRelElementVariation.
				getCtCollectionId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.getUuid(),
			newLayoutPageTemplateStructureRelElementVariation.getUuid());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.
				getExternalReferenceCode(),
			newLayoutPageTemplateStructureRelElementVariation.
				getExternalReferenceCode());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.
				getLayoutPageTemplateStructureRelElementVariationId(),
			newLayoutPageTemplateStructureRelElementVariation.
				getLayoutPageTemplateStructureRelElementVariationId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.getGroupId(),
			newLayoutPageTemplateStructureRelElementVariation.getGroupId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.
				getCompanyId(),
			newLayoutPageTemplateStructureRelElementVariation.getCompanyId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.getUserId(),
			newLayoutPageTemplateStructureRelElementVariation.getUserId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.
				getUserName(),
			newLayoutPageTemplateStructureRelElementVariation.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutPageTemplateStructureRelElementVariation.
					getCreateDate()),
			Time.getShortTimestamp(
				newLayoutPageTemplateStructureRelElementVariation.
					getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutPageTemplateStructureRelElementVariation.
					getModifiedDate()),
			Time.getShortTimestamp(
				newLayoutPageTemplateStructureRelElementVariation.
					getModifiedDate()));
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.isActive(),
			newLayoutPageTemplateStructureRelElementVariation.isActive());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.getHide(),
			newLayoutPageTemplateStructureRelElementVariation.getHide());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.getHtml(),
			newLayoutPageTemplateStructureRelElementVariation.getHtml());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.getJs(),
			newLayoutPageTemplateStructureRelElementVariation.getJs());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.getName(),
			newLayoutPageTemplateStructureRelElementVariation.getName());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.getPlid(),
			newLayoutPageTemplateStructureRelElementVariation.getPlid());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.
				getSegmentsExperienceERC(),
			newLayoutPageTemplateStructureRelElementVariation.
				getSegmentsExperienceERC());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation.
				getTargetElement(),
			newLayoutPageTemplateStructureRelElementVariation.
				getTargetElement());
	}

	@Test(
		expected = DuplicateLayoutPageTemplateStructureRelElementVariationExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation =
				addLayoutPageTemplateStructureRelElementVariation();

		LayoutPageTemplateStructureRelElementVariation
			newLayoutPageTemplateStructureRelElementVariation =
				addLayoutPageTemplateStructureRelElementVariation();

		newLayoutPageTemplateStructureRelElementVariation.setGroupId(
			layoutPageTemplateStructureRelElementVariation.getGroupId());

		newLayoutPageTemplateStructureRelElementVariation = _persistence.update(
			newLayoutPageTemplateStructureRelElementVariation);

		Session session = _persistence.getCurrentSession();

		session.evict(newLayoutPageTemplateStructureRelElementVariation);

		newLayoutPageTemplateStructureRelElementVariation.
			setExternalReferenceCode(
				layoutPageTemplateStructureRelElementVariation.
					getExternalReferenceCode());

		_persistence.update(newLayoutPageTemplateStructureRelElementVariation);
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
	public void testCountByPlid() throws Exception {
		_persistence.countByPlid(RandomTestUtil.nextLong());

		_persistence.countByPlid(0L);
	}

	@Test
	public void testCountBySegmentsExperienceERC() throws Exception {
		_persistence.countBySegmentsExperienceERC("");

		_persistence.countBySegmentsExperienceERC("null");

		_persistence.countBySegmentsExperienceERC((String)null);
	}

	@Test
	public void testCountByP_SEERC() throws Exception {
		_persistence.countByP_SEERC(RandomTestUtil.nextLong(), "");

		_persistence.countByP_SEERC(0L, "null");

		_persistence.countByP_SEERC(0L, (String)null);
	}

	@Test
	public void testCountByA_P_SEERC() throws Exception {
		_persistence.countByA_P_SEERC(
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextLong(), "");

		_persistence.countByA_P_SEERC(
			RandomTestUtil.randomBoolean(), 0L, "null");

		_persistence.countByA_P_SEERC(
			RandomTestUtil.randomBoolean(), 0L, (String)null);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LayoutPageTemplateStructureRelElementVariation
			newLayoutPageTemplateStructureRelElementVariation =
				addLayoutPageTemplateStructureRelElementVariation();

		LayoutPageTemplateStructureRelElementVariation
			existingLayoutPageTemplateStructureRelElementVariation =
				_persistence.findByPrimaryKey(
					newLayoutPageTemplateStructureRelElementVariation.
						getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation,
			newLayoutPageTemplateStructureRelElementVariation);
	}

	@Test(
		expected = NoSuchPageTemplateStructureRelElementVariationException.class
	)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"LPTSRelElementVariation", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "externalReferenceCode", true,
			"layoutPageTemplateStructureRelElementVariationId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "active", true, "html",
			true, "js", true, "name", true, "plid", true,
			"segmentsExperienceERC", true, "targetElement", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LayoutPageTemplateStructureRelElementVariation
			newLayoutPageTemplateStructureRelElementVariation =
				addLayoutPageTemplateStructureRelElementVariation();

		LayoutPageTemplateStructureRelElementVariation
			existingLayoutPageTemplateStructureRelElementVariation =
				_persistence.fetchByPrimaryKey(
					newLayoutPageTemplateStructureRelElementVariation.
						getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation,
			newLayoutPageTemplateStructureRelElementVariation);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructureRelElementVariation
			missingLayoutPageTemplateStructureRelElementVariation =
				_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(
			missingLayoutPageTemplateStructureRelElementVariation);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LayoutPageTemplateStructureRelElementVariation
			newLayoutPageTemplateStructureRelElementVariation1 =
				addLayoutPageTemplateStructureRelElementVariation();
		LayoutPageTemplateStructureRelElementVariation
			newLayoutPageTemplateStructureRelElementVariation2 =
				addLayoutPageTemplateStructureRelElementVariation();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newLayoutPageTemplateStructureRelElementVariation1.getPrimaryKey());
		primaryKeys.add(
			newLayoutPageTemplateStructureRelElementVariation2.getPrimaryKey());

		Map<Serializable, LayoutPageTemplateStructureRelElementVariation>
			layoutPageTemplateStructureRelElementVariations =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(
			2, layoutPageTemplateStructureRelElementVariations.size());
		Assert.assertEquals(
			newLayoutPageTemplateStructureRelElementVariation1,
			layoutPageTemplateStructureRelElementVariations.get(
				newLayoutPageTemplateStructureRelElementVariation1.
					getPrimaryKey()));
		Assert.assertEquals(
			newLayoutPageTemplateStructureRelElementVariation2,
			layoutPageTemplateStructureRelElementVariations.get(
				newLayoutPageTemplateStructureRelElementVariation2.
					getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LayoutPageTemplateStructureRelElementVariation>
			layoutPageTemplateStructureRelElementVariations =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(
			layoutPageTemplateStructureRelElementVariations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LayoutPageTemplateStructureRelElementVariation
			newLayoutPageTemplateStructureRelElementVariation =
				addLayoutPageTemplateStructureRelElementVariation();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newLayoutPageTemplateStructureRelElementVariation.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LayoutPageTemplateStructureRelElementVariation>
			layoutPageTemplateStructureRelElementVariations =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(
			1, layoutPageTemplateStructureRelElementVariations.size());
		Assert.assertEquals(
			newLayoutPageTemplateStructureRelElementVariation,
			layoutPageTemplateStructureRelElementVariations.get(
				newLayoutPageTemplateStructureRelElementVariation.
					getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LayoutPageTemplateStructureRelElementVariation>
			layoutPageTemplateStructureRelElementVariations =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(
			layoutPageTemplateStructureRelElementVariations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LayoutPageTemplateStructureRelElementVariation
			newLayoutPageTemplateStructureRelElementVariation =
				addLayoutPageTemplateStructureRelElementVariation();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newLayoutPageTemplateStructureRelElementVariation.getPrimaryKey());

		Map<Serializable, LayoutPageTemplateStructureRelElementVariation>
			layoutPageTemplateStructureRelElementVariations =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(
			1, layoutPageTemplateStructureRelElementVariations.size());
		Assert.assertEquals(
			newLayoutPageTemplateStructureRelElementVariation,
			layoutPageTemplateStructureRelElementVariations.get(
				newLayoutPageTemplateStructureRelElementVariation.
					getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			LayoutPageTemplateStructureRelElementVariationLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<LayoutPageTemplateStructureRelElementVariation>() {

				@Override
				public void performAction(
					LayoutPageTemplateStructureRelElementVariation
						layoutPageTemplateStructureRelElementVariation) {

					Assert.assertNotNull(
						layoutPageTemplateStructureRelElementVariation);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		LayoutPageTemplateStructureRelElementVariation
			newLayoutPageTemplateStructureRelElementVariation =
				addLayoutPageTemplateStructureRelElementVariation();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutPageTemplateStructureRelElementVariation.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"layoutPageTemplateStructureRelElementVariationId",
				newLayoutPageTemplateStructureRelElementVariation.
					getLayoutPageTemplateStructureRelElementVariationId()));

		List<LayoutPageTemplateStructureRelElementVariation> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		LayoutPageTemplateStructureRelElementVariation
			existingLayoutPageTemplateStructureRelElementVariation = result.get(
				0);

		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariation,
			newLayoutPageTemplateStructureRelElementVariation);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutPageTemplateStructureRelElementVariation.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"layoutPageTemplateStructureRelElementVariationId",
				RandomTestUtil.nextLong()));

		List<LayoutPageTemplateStructureRelElementVariation> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		LayoutPageTemplateStructureRelElementVariation
			newLayoutPageTemplateStructureRelElementVariation =
				addLayoutPageTemplateStructureRelElementVariation();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutPageTemplateStructureRelElementVariation.class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"layoutPageTemplateStructureRelElementVariationId"));

		Object newLayoutPageTemplateStructureRelElementVariationId =
			newLayoutPageTemplateStructureRelElementVariation.
				getLayoutPageTemplateStructureRelElementVariationId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"layoutPageTemplateStructureRelElementVariationId",
				new Object[] {
					newLayoutPageTemplateStructureRelElementVariationId
				}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingLayoutPageTemplateStructureRelElementVariationId =
			result.get(0);

		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationId,
			newLayoutPageTemplateStructureRelElementVariationId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutPageTemplateStructureRelElementVariation.class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"layoutPageTemplateStructureRelElementVariationId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"layoutPageTemplateStructureRelElementVariationId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LayoutPageTemplateStructureRelElementVariation
			newLayoutPageTemplateStructureRelElementVariation =
				addLayoutPageTemplateStructureRelElementVariation();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newLayoutPageTemplateStructureRelElementVariation.
					getPrimaryKey()));
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

		LayoutPageTemplateStructureRelElementVariation
			newLayoutPageTemplateStructureRelElementVariation =
				addLayoutPageTemplateStructureRelElementVariation();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutPageTemplateStructureRelElementVariation.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"layoutPageTemplateStructureRelElementVariationId",
				newLayoutPageTemplateStructureRelElementVariation.
					getLayoutPageTemplateStructureRelElementVariationId()));

		List<LayoutPageTemplateStructureRelElementVariation> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation) {

		Assert.assertEquals(
			layoutPageTemplateStructureRelElementVariation.getUuid(),
			ReflectionTestUtil.invoke(
				layoutPageTemplateStructureRelElementVariation,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"uuid_"));
		Assert.assertEquals(
			Long.valueOf(
				layoutPageTemplateStructureRelElementVariation.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateStructureRelElementVariation,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));

		Assert.assertEquals(
			layoutPageTemplateStructureRelElementVariation.
				getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				layoutPageTemplateStructureRelElementVariation,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(
				layoutPageTemplateStructureRelElementVariation.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateStructureRelElementVariation,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));
	}

	protected LayoutPageTemplateStructureRelElementVariation
			addLayoutPageTemplateStructureRelElementVariation()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation =
				_persistence.create(pk);

		layoutPageTemplateStructureRelElementVariation.setCtCollectionId(
			RandomTestUtil.nextLong());

		layoutPageTemplateStructureRelElementVariation.setUuid(
			RandomTestUtil.randomString());

		layoutPageTemplateStructureRelElementVariation.setExternalReferenceCode(
			RandomTestUtil.randomString());

		layoutPageTemplateStructureRelElementVariation.setGroupId(
			RandomTestUtil.nextLong());

		layoutPageTemplateStructureRelElementVariation.setCompanyId(
			RandomTestUtil.nextLong());

		layoutPageTemplateStructureRelElementVariation.setUserId(
			RandomTestUtil.nextLong());

		layoutPageTemplateStructureRelElementVariation.setUserName(
			RandomTestUtil.randomString());

		layoutPageTemplateStructureRelElementVariation.setCreateDate(
			RandomTestUtil.nextDate());

		layoutPageTemplateStructureRelElementVariation.setModifiedDate(
			RandomTestUtil.nextDate());

		layoutPageTemplateStructureRelElementVariation.setActive(
			RandomTestUtil.randomBoolean());

		layoutPageTemplateStructureRelElementVariation.setHide(
			RandomTestUtil.randomString());

		layoutPageTemplateStructureRelElementVariation.setHtml(
			RandomTestUtil.randomString());

		layoutPageTemplateStructureRelElementVariation.setJs(
			RandomTestUtil.randomString());

		layoutPageTemplateStructureRelElementVariation.setName(
			RandomTestUtil.randomString());

		layoutPageTemplateStructureRelElementVariation.setPlid(
			RandomTestUtil.nextLong());

		layoutPageTemplateStructureRelElementVariation.setSegmentsExperienceERC(
			RandomTestUtil.randomString());

		layoutPageTemplateStructureRelElementVariation.setTargetElement(
			RandomTestUtil.randomString());

		_layoutPageTemplateStructureRelElementVariations.add(
			_persistence.update(
				layoutPageTemplateStructureRelElementVariation));

		return layoutPageTemplateStructureRelElementVariation;
	}

	private List<LayoutPageTemplateStructureRelElementVariation>
		_layoutPageTemplateStructureRelElementVariations =
			new ArrayList<LayoutPageTemplateStructureRelElementVariation>();
	private LayoutPageTemplateStructureRelElementVariationPersistence
		_persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:569507337