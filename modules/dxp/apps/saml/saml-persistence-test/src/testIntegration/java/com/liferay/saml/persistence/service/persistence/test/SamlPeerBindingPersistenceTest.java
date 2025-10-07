/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.saml.persistence.exception.NoSuchPeerBindingException;
import com.liferay.saml.persistence.model.SamlPeerBinding;
import com.liferay.saml.persistence.service.SamlPeerBindingLocalServiceUtil;
import com.liferay.saml.persistence.service.persistence.SamlPeerBindingPersistence;
import com.liferay.saml.persistence.service.persistence.SamlPeerBindingUtil;

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
public class SamlPeerBindingPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.saml.persistence.service"));

	@Before
	public void setUp() {
		_persistence = SamlPeerBindingUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SamlPeerBinding> iterator = _samlPeerBindings.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlPeerBinding samlPeerBinding = _persistence.create(pk);

		Assert.assertNotNull(samlPeerBinding);

		Assert.assertEquals(samlPeerBinding.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SamlPeerBinding newSamlPeerBinding = addSamlPeerBinding();

		_persistence.remove(newSamlPeerBinding);

		SamlPeerBinding existingSamlPeerBinding =
			_persistence.fetchByPrimaryKey(newSamlPeerBinding.getPrimaryKey());

		Assert.assertNull(existingSamlPeerBinding);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSamlPeerBinding();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlPeerBinding newSamlPeerBinding = _persistence.create(pk);

		newSamlPeerBinding.setCompanyId(RandomTestUtil.nextLong());

		newSamlPeerBinding.setUserId(RandomTestUtil.nextLong());

		newSamlPeerBinding.setUserName(RandomTestUtil.randomString());

		newSamlPeerBinding.setCreateDate(RandomTestUtil.nextDate());

		newSamlPeerBinding.setSamlPeerEntityId(RandomTestUtil.randomString());

		newSamlPeerBinding.setDeleted(RandomTestUtil.randomBoolean());

		newSamlPeerBinding.setSamlNameIdFormat(RandomTestUtil.randomString());

		newSamlPeerBinding.setSamlNameIdNameQualifier(
			RandomTestUtil.randomString());

		newSamlPeerBinding.setSamlNameIdSpNameQualifier(
			RandomTestUtil.randomString());

		newSamlPeerBinding.setSamlNameIdSpProvidedId(
			RandomTestUtil.randomString());

		newSamlPeerBinding.setSamlNameIdValue(RandomTestUtil.randomString());

		_samlPeerBindings.add(_persistence.update(newSamlPeerBinding));

		SamlPeerBinding existingSamlPeerBinding = _persistence.findByPrimaryKey(
			newSamlPeerBinding.getPrimaryKey());

		Assert.assertEquals(
			existingSamlPeerBinding.getSamlPeerBindingId(),
			newSamlPeerBinding.getSamlPeerBindingId());
		Assert.assertEquals(
			existingSamlPeerBinding.getCompanyId(),
			newSamlPeerBinding.getCompanyId());
		Assert.assertEquals(
			existingSamlPeerBinding.getUserId(),
			newSamlPeerBinding.getUserId());
		Assert.assertEquals(
			existingSamlPeerBinding.getUserName(),
			newSamlPeerBinding.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSamlPeerBinding.getCreateDate()),
			Time.getShortTimestamp(newSamlPeerBinding.getCreateDate()));
		Assert.assertEquals(
			existingSamlPeerBinding.getSamlPeerEntityId(),
			newSamlPeerBinding.getSamlPeerEntityId());
		Assert.assertEquals(
			existingSamlPeerBinding.isDeleted(),
			newSamlPeerBinding.isDeleted());
		Assert.assertEquals(
			existingSamlPeerBinding.getSamlNameIdFormat(),
			newSamlPeerBinding.getSamlNameIdFormat());
		Assert.assertEquals(
			existingSamlPeerBinding.getSamlNameIdNameQualifier(),
			newSamlPeerBinding.getSamlNameIdNameQualifier());
		Assert.assertEquals(
			existingSamlPeerBinding.getSamlNameIdSpNameQualifier(),
			newSamlPeerBinding.getSamlNameIdSpNameQualifier());
		Assert.assertEquals(
			existingSamlPeerBinding.getSamlNameIdSpProvidedId(),
			newSamlPeerBinding.getSamlNameIdSpProvidedId());
		Assert.assertEquals(
			existingSamlPeerBinding.getSamlNameIdValue(),
			newSamlPeerBinding.getSamlNameIdValue());
	}

	@Test
	public void testCountByC_D_SNIV() throws Exception {
		_persistence.countByC_D_SNIV(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByC_D_SNIV(
			0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByC_D_SNIV(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByC_U_SPEI_D() throws Exception {
		_persistence.countByC_U_SPEI_D(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.randomBoolean());

		_persistence.countByC_U_SPEI_D(
			0L, 0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByC_U_SPEI_D(
			0L, 0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SamlPeerBinding newSamlPeerBinding = addSamlPeerBinding();

		SamlPeerBinding existingSamlPeerBinding = _persistence.findByPrimaryKey(
			newSamlPeerBinding.getPrimaryKey());

		Assert.assertEquals(existingSamlPeerBinding, newSamlPeerBinding);
	}

	@Test(expected = NoSuchPeerBindingException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SamlPeerBinding> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SamlPeerBinding", "samlPeerBindingId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"samlPeerEntityId", true, "deleted", true, "samlNameIdFormat", true,
			"samlNameIdNameQualifier", true, "samlNameIdSpNameQualifier", true,
			"samlNameIdSpProvidedId", true, "samlNameIdValue", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SamlPeerBinding newSamlPeerBinding = addSamlPeerBinding();

		SamlPeerBinding existingSamlPeerBinding =
			_persistence.fetchByPrimaryKey(newSamlPeerBinding.getPrimaryKey());

		Assert.assertEquals(existingSamlPeerBinding, newSamlPeerBinding);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlPeerBinding missingSamlPeerBinding = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingSamlPeerBinding);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SamlPeerBinding newSamlPeerBinding1 = addSamlPeerBinding();
		SamlPeerBinding newSamlPeerBinding2 = addSamlPeerBinding();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlPeerBinding1.getPrimaryKey());
		primaryKeys.add(newSamlPeerBinding2.getPrimaryKey());

		Map<Serializable, SamlPeerBinding> samlPeerBindings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, samlPeerBindings.size());
		Assert.assertEquals(
			newSamlPeerBinding1,
			samlPeerBindings.get(newSamlPeerBinding1.getPrimaryKey()));
		Assert.assertEquals(
			newSamlPeerBinding2,
			samlPeerBindings.get(newSamlPeerBinding2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SamlPeerBinding> samlPeerBindings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlPeerBindings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SamlPeerBinding newSamlPeerBinding = addSamlPeerBinding();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlPeerBinding.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SamlPeerBinding> samlPeerBindings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlPeerBindings.size());
		Assert.assertEquals(
			newSamlPeerBinding,
			samlPeerBindings.get(newSamlPeerBinding.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SamlPeerBinding> samlPeerBindings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlPeerBindings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SamlPeerBinding newSamlPeerBinding = addSamlPeerBinding();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlPeerBinding.getPrimaryKey());

		Map<Serializable, SamlPeerBinding> samlPeerBindings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlPeerBindings.size());
		Assert.assertEquals(
			newSamlPeerBinding,
			samlPeerBindings.get(newSamlPeerBinding.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SamlPeerBindingLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<SamlPeerBinding>() {

				@Override
				public void performAction(SamlPeerBinding samlPeerBinding) {
					Assert.assertNotNull(samlPeerBinding);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SamlPeerBinding newSamlPeerBinding = addSamlPeerBinding();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlPeerBinding.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlPeerBindingId",
				newSamlPeerBinding.getSamlPeerBindingId()));

		List<SamlPeerBinding> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		SamlPeerBinding existingSamlPeerBinding = result.get(0);

		Assert.assertEquals(existingSamlPeerBinding, newSamlPeerBinding);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlPeerBinding.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlPeerBindingId", RandomTestUtil.nextLong()));

		List<SamlPeerBinding> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SamlPeerBinding newSamlPeerBinding = addSamlPeerBinding();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlPeerBinding.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("samlPeerBindingId"));

		Object newSamlPeerBindingId = newSamlPeerBinding.getSamlPeerBindingId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"samlPeerBindingId", new Object[] {newSamlPeerBindingId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSamlPeerBindingId = result.get(0);

		Assert.assertEquals(existingSamlPeerBindingId, newSamlPeerBindingId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlPeerBinding.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("samlPeerBindingId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"samlPeerBindingId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected SamlPeerBinding addSamlPeerBinding() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlPeerBinding samlPeerBinding = _persistence.create(pk);

		samlPeerBinding.setCompanyId(RandomTestUtil.nextLong());

		samlPeerBinding.setUserId(RandomTestUtil.nextLong());

		samlPeerBinding.setUserName(RandomTestUtil.randomString());

		samlPeerBinding.setCreateDate(RandomTestUtil.nextDate());

		samlPeerBinding.setSamlPeerEntityId(RandomTestUtil.randomString());

		samlPeerBinding.setDeleted(RandomTestUtil.randomBoolean());

		samlPeerBinding.setSamlNameIdFormat(RandomTestUtil.randomString());

		samlPeerBinding.setSamlNameIdNameQualifier(
			RandomTestUtil.randomString());

		samlPeerBinding.setSamlNameIdSpNameQualifier(
			RandomTestUtil.randomString());

		samlPeerBinding.setSamlNameIdSpProvidedId(
			RandomTestUtil.randomString());

		samlPeerBinding.setSamlNameIdValue(RandomTestUtil.randomString());

		_samlPeerBindings.add(_persistence.update(samlPeerBinding));

		return samlPeerBinding;
	}

	private List<SamlPeerBinding> _samlPeerBindings =
		new ArrayList<SamlPeerBinding>();
	private SamlPeerBindingPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}