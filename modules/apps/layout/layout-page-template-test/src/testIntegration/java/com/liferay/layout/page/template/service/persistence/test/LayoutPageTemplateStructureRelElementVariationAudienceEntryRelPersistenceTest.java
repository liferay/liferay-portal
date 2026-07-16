/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateStructureRelElementVariationAudienceEntryRelExternalReferenceCodeException;
import com.liferay.layout.page.template.exception.NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceUtil;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelUtil;
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
public class
	LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistenceTest {

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
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRelUtil.
				getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			iterator =
				_layoutPageTemplateStructureRelElementVariationAudienceEntryRels.
					iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				_persistence.create(pk);

		Assert.assertNotNull(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel);

		Assert.assertEquals(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getPrimaryKey(),
			pk);
	}

	@Test
	public void testRemove() throws Exception {
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();

		_persistence.remove(
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel);

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				_persistence.fetchByPrimaryKey(
					newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
						getPrimaryKey());

		Assert.assertNull(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setCtCollectionId(RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setUuid(RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setExternalReferenceCode(RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setGroupId(RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setCompanyId(RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setUserId(RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setUserName(RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setCreateDate(RandomTestUtil.nextDate());

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setModifiedDate(RandomTestUtil.nextDate());

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setAudienceEntryERC(RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setLayoutPageTemplateStructureRelElementVariationERC(
				RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
			_persistence.update(
				newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel);

		_layoutPageTemplateStructureRelElementVariationAudienceEntryRels.add(
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel);

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				_persistence.findByPrimaryKey(
					newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
						getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getMvccVersion(),
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getMvccVersion());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getCtCollectionId(),
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getCtCollectionId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getUuid(),
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getUuid());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getExternalReferenceCode(),
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getExternalReferenceCode());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelId(),
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getGroupId(),
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getGroupId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getCompanyId(),
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getCompanyId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getUserId(),
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getUserId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getUserName(),
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getCreateDate()),
			Time.getShortTimestamp(
				newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getModifiedDate()),
			Time.getShortTimestamp(
				newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getModifiedDate()));
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getAudienceEntryERC(),
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getAudienceEntryERC());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getLayoutPageTemplateStructureRelElementVariationERC(),
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getLayoutPageTemplateStructureRelElementVariationERC());
	}

	@Test(
		expected = DuplicateLayoutPageTemplateStructureRelElementVariationAudienceEntryRelExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setGroupId(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getGroupId());

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
			_persistence.update(
				newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel);

		Session session = _persistence.getCurrentSession();

		session.evict(
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel);

		newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setExternalReferenceCode(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getExternalReferenceCode());

		_persistence.update(
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel);
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
	public void testCountByLayoutPageTemplateStructureRelElementVariationERC()
		throws Exception {

		_persistence.countByLayoutPageTemplateStructureRelElementVariationERC(
			"");

		_persistence.countByLayoutPageTemplateStructureRelElementVariationERC(
			"null");

		_persistence.countByLayoutPageTemplateStructureRelElementVariationERC(
			(String)null);
	}

	@Test
	public void testCountByC_AEERC() throws Exception {
		_persistence.countByC_AEERC(RandomTestUtil.nextLong(), "");

		_persistence.countByC_AEERC(0L, "null");

		_persistence.countByC_AEERC(0L, (String)null);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				_persistence.findByPrimaryKey(
					newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
						getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel,
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	@Test(
		expected = NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException.class
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

	protected OrderByComparator
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"LPTSREVAudienceEntryRel", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "externalReferenceCode", true,
			"layoutPageTemplateStructureRelElementVariationAudienceEntryRelId",
			true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"audienceEntryERC", true,
			"layoutPageTemplateStructureRelElementVariationERC", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				_persistence.fetchByPrimaryKey(
					newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
						getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel,
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			missingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(
			missingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel1 =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel2 =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel1.
				getPrimaryKey());
		primaryKeys.add(
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel2.
				getPrimaryKey());

		Map
			<Serializable,
			 LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
				layoutPageTemplateStructureRelElementVariationAudienceEntryRels =
					_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(
			2,
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.
				size());
		Assert.assertEquals(
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel1,
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.get(
				newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel1.
					getPrimaryKey()));
		Assert.assertEquals(
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel2,
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.get(
				newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel2.
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

		Map
			<Serializable,
			 LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
				layoutPageTemplateStructureRelElementVariationAudienceEntryRels =
					_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.
				isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getPrimaryKey());
		primaryKeys.add(pk);

		Map
			<Serializable,
			 LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
				layoutPageTemplateStructureRelElementVariationAudienceEntryRels =
					_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(
			1,
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.
				size());
		Assert.assertEquals(
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel,
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.get(
				newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map
			<Serializable,
			 LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
				layoutPageTemplateStructureRelElementVariationAudienceEntryRels =
					_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.
				isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getPrimaryKey());

		Map
			<Serializable,
			 LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
				layoutPageTemplateStructureRelElementVariationAudienceEntryRels =
					_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(
			1,
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.
				size());
		Assert.assertEquals(
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel,
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.get(
				newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>() {

				@Override
				public void performAction(
					LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
						layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

					Assert.assertNotNull(
						layoutPageTemplateStructureRelElementVariationAudienceEntryRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"layoutPageTemplateStructureRelElementVariationAudienceEntryRelId",
				newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelId()));

		List<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				result.get(0);

		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRel,
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"layoutPageTemplateStructureRelElementVariationAudienceEntryRelId",
				RandomTestUtil.nextLong()));

		List<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"layoutPageTemplateStructureRelElementVariationAudienceEntryRelId"));

		Object
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRelId =
				newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"layoutPageTemplateStructureRelElementVariationAudienceEntryRelId",
				new Object[] {
					newLayoutPageTemplateStructureRelElementVariationAudienceEntryRelId
				}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRelId =
				result.get(0);

		Assert.assertEquals(
			existingLayoutPageTemplateStructureRelElementVariationAudienceEntryRelId,
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"layoutPageTemplateStructureRelElementVariationAudienceEntryRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"layoutPageTemplateStructureRelElementVariationAudienceEntryRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
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

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"layoutPageTemplateStructureRelElementVariationAudienceEntryRelId",
				newLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelId()));

		List<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			result = _persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		Assert.assertEquals(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getUuid(),
			ReflectionTestUtil.invoke(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"uuid_"));
		Assert.assertEquals(
			Long.valueOf(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));

		Assert.assertEquals(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));
	}

	protected LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			addLayoutPageTemplateStructureRelElementVariationAudienceEntryRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				_persistence.create(pk);

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setCtCollectionId(RandomTestUtil.nextLong());

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.setUuid(
			RandomTestUtil.randomString());

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setExternalReferenceCode(RandomTestUtil.randomString());

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setGroupId(RandomTestUtil.nextLong());

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setCompanyId(RandomTestUtil.nextLong());

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setUserId(RandomTestUtil.nextLong());

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setUserName(RandomTestUtil.randomString());

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setCreateDate(RandomTestUtil.nextDate());

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setModifiedDate(RandomTestUtil.nextDate());

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setAudienceEntryERC(RandomTestUtil.randomString());

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setLayoutPageTemplateStructureRelElementVariationERC(
				RandomTestUtil.randomString());

		_layoutPageTemplateStructureRelElementVariationAudienceEntryRels.add(
			_persistence.update(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel));

		return layoutPageTemplateStructureRelElementVariationAudienceEntryRel;
	}

	private List<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
		_layoutPageTemplateStructureRelElementVariationAudienceEntryRels =
			new ArrayList
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>();
	private
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence
			_persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1928368371