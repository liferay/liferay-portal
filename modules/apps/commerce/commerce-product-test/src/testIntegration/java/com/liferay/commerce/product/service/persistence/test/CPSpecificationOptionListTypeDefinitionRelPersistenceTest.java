/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchCPSpecificationOptionListTypeDefinitionRelException;
import com.liferay.commerce.product.model.CPSpecificationOptionListTypeDefinitionRel;
import com.liferay.commerce.product.service.CPSpecificationOptionListTypeDefinitionRelLocalServiceUtil;
import com.liferay.commerce.product.service.persistence.CPSpecificationOptionListTypeDefinitionRelPersistence;
import com.liferay.commerce.product.service.persistence.CPSpecificationOptionListTypeDefinitionRelUtil;
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
public class CPSpecificationOptionListTypeDefinitionRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence =
			CPSpecificationOptionListTypeDefinitionRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPSpecificationOptionListTypeDefinitionRel> iterator =
			_cpSpecificationOptionListTypeDefinitionRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPSpecificationOptionListTypeDefinitionRel
			cpSpecificationOptionListTypeDefinitionRel = _persistence.create(
				pk);

		Assert.assertNotNull(cpSpecificationOptionListTypeDefinitionRel);

		Assert.assertEquals(
			cpSpecificationOptionListTypeDefinitionRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPSpecificationOptionListTypeDefinitionRel
			newCPSpecificationOptionListTypeDefinitionRel =
				addCPSpecificationOptionListTypeDefinitionRel();

		_persistence.remove(newCPSpecificationOptionListTypeDefinitionRel);

		CPSpecificationOptionListTypeDefinitionRel
			existingCPSpecificationOptionListTypeDefinitionRel =
				_persistence.fetchByPrimaryKey(
					newCPSpecificationOptionListTypeDefinitionRel.
						getPrimaryKey());

		Assert.assertNull(existingCPSpecificationOptionListTypeDefinitionRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPSpecificationOptionListTypeDefinitionRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CPSpecificationOptionListTypeDefinitionRel
			newCPSpecificationOptionListTypeDefinitionRel =
				addCPSpecificationOptionListTypeDefinitionRel();

		newCPSpecificationOptionListTypeDefinitionRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newCPSpecificationOptionListTypeDefinitionRel.setCompanyId(
			RandomTestUtil.nextLong());

		newCPSpecificationOptionListTypeDefinitionRel.
			setCPSpecificationOptionId(RandomTestUtil.nextLong());

		newCPSpecificationOptionListTypeDefinitionRel.setListTypeDefinitionId(
			RandomTestUtil.nextLong());

		newCPSpecificationOptionListTypeDefinitionRel = _persistence.update(
			newCPSpecificationOptionListTypeDefinitionRel);

		_cpSpecificationOptionListTypeDefinitionRels.add(
			newCPSpecificationOptionListTypeDefinitionRel);

		CPSpecificationOptionListTypeDefinitionRel
			existingCPSpecificationOptionListTypeDefinitionRel =
				_persistence.findByPrimaryKey(
					newCPSpecificationOptionListTypeDefinitionRel.
						getPrimaryKey());

		Assert.assertEquals(
			existingCPSpecificationOptionListTypeDefinitionRel.getMvccVersion(),
			newCPSpecificationOptionListTypeDefinitionRel.getMvccVersion());
		Assert.assertEquals(
			existingCPSpecificationOptionListTypeDefinitionRel.
				getCtCollectionId(),
			newCPSpecificationOptionListTypeDefinitionRel.getCtCollectionId());
		Assert.assertEquals(
			existingCPSpecificationOptionListTypeDefinitionRel.
				getCPSpecificationOptionListTypeDefinitionRelId(),
			newCPSpecificationOptionListTypeDefinitionRel.
				getCPSpecificationOptionListTypeDefinitionRelId());
		Assert.assertEquals(
			existingCPSpecificationOptionListTypeDefinitionRel.getCompanyId(),
			newCPSpecificationOptionListTypeDefinitionRel.getCompanyId());
		Assert.assertEquals(
			existingCPSpecificationOptionListTypeDefinitionRel.
				getCPSpecificationOptionId(),
			newCPSpecificationOptionListTypeDefinitionRel.
				getCPSpecificationOptionId());
		Assert.assertEquals(
			existingCPSpecificationOptionListTypeDefinitionRel.
				getListTypeDefinitionId(),
			newCPSpecificationOptionListTypeDefinitionRel.
				getListTypeDefinitionId());
	}

	@Test
	public void testCountByCPSpecificationOptionId() throws Exception {
		_persistence.countByCPSpecificationOptionId(RandomTestUtil.nextLong());

		_persistence.countByCPSpecificationOptionId(0L);
	}

	@Test
	public void testCountByListTypeDefinitionId() throws Exception {
		_persistence.countByListTypeDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByListTypeDefinitionId(0L);
	}

	@Test
	public void testCountByC_L() throws Exception {
		_persistence.countByC_L(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_L(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPSpecificationOptionListTypeDefinitionRel
			newCPSpecificationOptionListTypeDefinitionRel =
				addCPSpecificationOptionListTypeDefinitionRel();

		CPSpecificationOptionListTypeDefinitionRel
			existingCPSpecificationOptionListTypeDefinitionRel =
				_persistence.findByPrimaryKey(
					newCPSpecificationOptionListTypeDefinitionRel.
						getPrimaryKey());

		Assert.assertEquals(
			existingCPSpecificationOptionListTypeDefinitionRel,
			newCPSpecificationOptionListTypeDefinitionRel);
	}

	@Test(
		expected = NoSuchCPSpecificationOptionListTypeDefinitionRelException.class
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

	protected OrderByComparator<CPSpecificationOptionListTypeDefinitionRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CPSOListTypeDefinitionRel", "mvccVersion", true, "ctCollectionId",
			true, "CPSpecificationOptionListTypeDefinitionRelId", true,
			"companyId", true, "CPSpecificationOptionId", true,
			"listTypeDefinitionId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPSpecificationOptionListTypeDefinitionRel
			newCPSpecificationOptionListTypeDefinitionRel =
				addCPSpecificationOptionListTypeDefinitionRel();

		CPSpecificationOptionListTypeDefinitionRel
			existingCPSpecificationOptionListTypeDefinitionRel =
				_persistence.fetchByPrimaryKey(
					newCPSpecificationOptionListTypeDefinitionRel.
						getPrimaryKey());

		Assert.assertEquals(
			existingCPSpecificationOptionListTypeDefinitionRel,
			newCPSpecificationOptionListTypeDefinitionRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPSpecificationOptionListTypeDefinitionRel
			missingCPSpecificationOptionListTypeDefinitionRel =
				_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPSpecificationOptionListTypeDefinitionRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPSpecificationOptionListTypeDefinitionRel
			newCPSpecificationOptionListTypeDefinitionRel1 =
				addCPSpecificationOptionListTypeDefinitionRel();
		CPSpecificationOptionListTypeDefinitionRel
			newCPSpecificationOptionListTypeDefinitionRel2 =
				addCPSpecificationOptionListTypeDefinitionRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCPSpecificationOptionListTypeDefinitionRel1.getPrimaryKey());
		primaryKeys.add(
			newCPSpecificationOptionListTypeDefinitionRel2.getPrimaryKey());

		Map<Serializable, CPSpecificationOptionListTypeDefinitionRel>
			cpSpecificationOptionListTypeDefinitionRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(
			2, cpSpecificationOptionListTypeDefinitionRels.size());
		Assert.assertEquals(
			newCPSpecificationOptionListTypeDefinitionRel1,
			cpSpecificationOptionListTypeDefinitionRels.get(
				newCPSpecificationOptionListTypeDefinitionRel1.
					getPrimaryKey()));
		Assert.assertEquals(
			newCPSpecificationOptionListTypeDefinitionRel2,
			cpSpecificationOptionListTypeDefinitionRels.get(
				newCPSpecificationOptionListTypeDefinitionRel2.
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

		Map<Serializable, CPSpecificationOptionListTypeDefinitionRel>
			cpSpecificationOptionListTypeDefinitionRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(
			cpSpecificationOptionListTypeDefinitionRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPSpecificationOptionListTypeDefinitionRel
			newCPSpecificationOptionListTypeDefinitionRel =
				addCPSpecificationOptionListTypeDefinitionRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCPSpecificationOptionListTypeDefinitionRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPSpecificationOptionListTypeDefinitionRel>
			cpSpecificationOptionListTypeDefinitionRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(
			1, cpSpecificationOptionListTypeDefinitionRels.size());
		Assert.assertEquals(
			newCPSpecificationOptionListTypeDefinitionRel,
			cpSpecificationOptionListTypeDefinitionRels.get(
				newCPSpecificationOptionListTypeDefinitionRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPSpecificationOptionListTypeDefinitionRel>
			cpSpecificationOptionListTypeDefinitionRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(
			cpSpecificationOptionListTypeDefinitionRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPSpecificationOptionListTypeDefinitionRel
			newCPSpecificationOptionListTypeDefinitionRel =
				addCPSpecificationOptionListTypeDefinitionRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCPSpecificationOptionListTypeDefinitionRel.getPrimaryKey());

		Map<Serializable, CPSpecificationOptionListTypeDefinitionRel>
			cpSpecificationOptionListTypeDefinitionRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(
			1, cpSpecificationOptionListTypeDefinitionRels.size());
		Assert.assertEquals(
			newCPSpecificationOptionListTypeDefinitionRel,
			cpSpecificationOptionListTypeDefinitionRels.get(
				newCPSpecificationOptionListTypeDefinitionRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CPSpecificationOptionListTypeDefinitionRelLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CPSpecificationOptionListTypeDefinitionRel>() {

				@Override
				public void performAction(
					CPSpecificationOptionListTypeDefinitionRel
						cpSpecificationOptionListTypeDefinitionRel) {

					Assert.assertNotNull(
						cpSpecificationOptionListTypeDefinitionRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CPSpecificationOptionListTypeDefinitionRel
			newCPSpecificationOptionListTypeDefinitionRel =
				addCPSpecificationOptionListTypeDefinitionRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPSpecificationOptionListTypeDefinitionRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CPSpecificationOptionListTypeDefinitionRelId",
				newCPSpecificationOptionListTypeDefinitionRel.
					getCPSpecificationOptionListTypeDefinitionRelId()));

		List<CPSpecificationOptionListTypeDefinitionRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		CPSpecificationOptionListTypeDefinitionRel
			existingCPSpecificationOptionListTypeDefinitionRel = result.get(0);

		Assert.assertEquals(
			existingCPSpecificationOptionListTypeDefinitionRel,
			newCPSpecificationOptionListTypeDefinitionRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPSpecificationOptionListTypeDefinitionRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CPSpecificationOptionListTypeDefinitionRelId",
				RandomTestUtil.nextLong()));

		List<CPSpecificationOptionListTypeDefinitionRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CPSpecificationOptionListTypeDefinitionRel
			newCPSpecificationOptionListTypeDefinitionRel =
				addCPSpecificationOptionListTypeDefinitionRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPSpecificationOptionListTypeDefinitionRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"CPSpecificationOptionListTypeDefinitionRelId"));

		Object newCPSpecificationOptionListTypeDefinitionRelId =
			newCPSpecificationOptionListTypeDefinitionRel.
				getCPSpecificationOptionListTypeDefinitionRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CPSpecificationOptionListTypeDefinitionRelId",
				new Object[] {
					newCPSpecificationOptionListTypeDefinitionRelId
				}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCPSpecificationOptionListTypeDefinitionRelId =
			result.get(0);

		Assert.assertEquals(
			existingCPSpecificationOptionListTypeDefinitionRelId,
			newCPSpecificationOptionListTypeDefinitionRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPSpecificationOptionListTypeDefinitionRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"CPSpecificationOptionListTypeDefinitionRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CPSpecificationOptionListTypeDefinitionRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPSpecificationOptionListTypeDefinitionRel
			newCPSpecificationOptionListTypeDefinitionRel =
				addCPSpecificationOptionListTypeDefinitionRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPSpecificationOptionListTypeDefinitionRel.getPrimaryKey()));
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

		CPSpecificationOptionListTypeDefinitionRel
			newCPSpecificationOptionListTypeDefinitionRel =
				addCPSpecificationOptionListTypeDefinitionRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPSpecificationOptionListTypeDefinitionRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CPSpecificationOptionListTypeDefinitionRelId",
				newCPSpecificationOptionListTypeDefinitionRel.
					getCPSpecificationOptionListTypeDefinitionRelId()));

		List<CPSpecificationOptionListTypeDefinitionRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CPSpecificationOptionListTypeDefinitionRel
			cpSpecificationOptionListTypeDefinitionRel) {

		Assert.assertEquals(
			Long.valueOf(
				cpSpecificationOptionListTypeDefinitionRel.
					getCPSpecificationOptionId()),
			ReflectionTestUtil.<Long>invoke(
				cpSpecificationOptionListTypeDefinitionRel,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"CPSpecificationOptionId"));
		Assert.assertEquals(
			Long.valueOf(
				cpSpecificationOptionListTypeDefinitionRel.
					getListTypeDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				cpSpecificationOptionListTypeDefinitionRel,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"listTypeDefinitionId"));
	}

	protected CPSpecificationOptionListTypeDefinitionRel
			addCPSpecificationOptionListTypeDefinitionRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPSpecificationOptionListTypeDefinitionRel
			cpSpecificationOptionListTypeDefinitionRel = _persistence.create(
				pk);

		cpSpecificationOptionListTypeDefinitionRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		cpSpecificationOptionListTypeDefinitionRel.setCompanyId(
			RandomTestUtil.nextLong());

		cpSpecificationOptionListTypeDefinitionRel.setCPSpecificationOptionId(
			RandomTestUtil.nextLong());

		cpSpecificationOptionListTypeDefinitionRel.setListTypeDefinitionId(
			RandomTestUtil.nextLong());

		_cpSpecificationOptionListTypeDefinitionRels.add(
			_persistence.update(cpSpecificationOptionListTypeDefinitionRel));

		return cpSpecificationOptionListTypeDefinitionRel;
	}

	private List<CPSpecificationOptionListTypeDefinitionRel>
		_cpSpecificationOptionListTypeDefinitionRels =
			new ArrayList<CPSpecificationOptionListTypeDefinitionRel>();
	private CPSpecificationOptionListTypeDefinitionRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:776712083