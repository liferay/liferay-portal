/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateOrganizationExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.OrganizationPersistence;
import com.liferay.portal.kernel.service.persistence.OrganizationUtil;
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
public class OrganizationPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = OrganizationUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Organization> iterator = _organizations.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Organization organization = _persistence.create(pk);

		Assert.assertNotNull(organization);

		Assert.assertEquals(organization.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Organization newOrganization = addOrganization();

		_persistence.remove(newOrganization);

		Organization existingOrganization = _persistence.fetchByPrimaryKey(
			newOrganization.getPrimaryKey());

		Assert.assertNull(existingOrganization);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addOrganization();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Organization newOrganization = _persistence.create(pk);

		newOrganization.setMvccVersion(RandomTestUtil.nextLong());

		newOrganization.setCtCollectionId(RandomTestUtil.nextLong());

		newOrganization.setUuid(RandomTestUtil.randomString());

		newOrganization.setExternalReferenceCode(RandomTestUtil.randomString());

		newOrganization.setCompanyId(RandomTestUtil.nextLong());

		newOrganization.setUserId(RandomTestUtil.nextLong());

		newOrganization.setUserName(RandomTestUtil.randomString());

		newOrganization.setCreateDate(RandomTestUtil.nextDate());

		newOrganization.setModifiedDate(RandomTestUtil.nextDate());

		newOrganization.setParentOrganizationId(RandomTestUtil.nextLong());

		newOrganization.setTreePath(RandomTestUtil.randomString());

		newOrganization.setName(RandomTestUtil.randomString());

		newOrganization.setType(RandomTestUtil.randomString());

		newOrganization.setRecursable(RandomTestUtil.randomBoolean());

		newOrganization.setRegionId(RandomTestUtil.nextLong());

		newOrganization.setCountryId(RandomTestUtil.nextLong());

		newOrganization.setStatusListTypeId(RandomTestUtil.nextLong());

		newOrganization.setComments(RandomTestUtil.randomString());

		newOrganization.setLogoId(RandomTestUtil.nextLong());

		newOrganization.setStatus(RandomTestUtil.nextInt());

		_organizations.add(_persistence.update(newOrganization));

		Organization existingOrganization = _persistence.findByPrimaryKey(
			newOrganization.getPrimaryKey());

		Assert.assertEquals(
			existingOrganization.getMvccVersion(),
			newOrganization.getMvccVersion());
		Assert.assertEquals(
			existingOrganization.getCtCollectionId(),
			newOrganization.getCtCollectionId());
		Assert.assertEquals(
			existingOrganization.getUuid(), newOrganization.getUuid());
		Assert.assertEquals(
			existingOrganization.getExternalReferenceCode(),
			newOrganization.getExternalReferenceCode());
		Assert.assertEquals(
			existingOrganization.getOrganizationId(),
			newOrganization.getOrganizationId());
		Assert.assertEquals(
			existingOrganization.getCompanyId(),
			newOrganization.getCompanyId());
		Assert.assertEquals(
			existingOrganization.getUserId(), newOrganization.getUserId());
		Assert.assertEquals(
			existingOrganization.getUserName(), newOrganization.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingOrganization.getCreateDate()),
			Time.getShortTimestamp(newOrganization.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingOrganization.getModifiedDate()),
			Time.getShortTimestamp(newOrganization.getModifiedDate()));
		Assert.assertEquals(
			existingOrganization.getParentOrganizationId(),
			newOrganization.getParentOrganizationId());
		Assert.assertEquals(
			existingOrganization.getTreePath(), newOrganization.getTreePath());
		Assert.assertEquals(
			existingOrganization.getName(), newOrganization.getName());
		Assert.assertEquals(
			existingOrganization.getType(), newOrganization.getType());
		Assert.assertEquals(
			existingOrganization.isRecursable(),
			newOrganization.isRecursable());
		Assert.assertEquals(
			existingOrganization.getRegionId(), newOrganization.getRegionId());
		Assert.assertEquals(
			existingOrganization.getCountryId(),
			newOrganization.getCountryId());
		Assert.assertEquals(
			existingOrganization.getStatusListTypeId(),
			newOrganization.getStatusListTypeId());
		Assert.assertEquals(
			existingOrganization.getComments(), newOrganization.getComments());
		Assert.assertEquals(
			existingOrganization.getLogoId(), newOrganization.getLogoId());
		Assert.assertEquals(
			existingOrganization.getStatus(), newOrganization.getStatus());
	}

	@Test(expected = DuplicateOrganizationExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		Organization organization = addOrganization();

		Organization newOrganization = addOrganization();

		newOrganization.setCompanyId(organization.getCompanyId());

		newOrganization = _persistence.update(newOrganization);

		Session session = _persistence.getCurrentSession();

		session.evict(newOrganization);

		newOrganization.setExternalReferenceCode(
			organization.getExternalReferenceCode());

		_persistence.update(newOrganization);
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
	public void testCountByCompanyIdLocations() throws Exception {
		_persistence.countByCompanyIdLocations(RandomTestUtil.nextLong());

		_persistence.countByCompanyIdLocations(0L);
	}

	@Test
	public void testCountByLogoId() throws Exception {
		_persistence.countByLogoId(RandomTestUtil.nextLong());

		_persistence.countByLogoId(0L);
	}

	@Test
	public void testCountByC_P() throws Exception {
		_persistence.countByC_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_P(0L, 0L);
	}

	@Test
	public void testCountByC_LikeT() throws Exception {
		_persistence.countByC_LikeT(RandomTestUtil.nextLong(), "");

		_persistence.countByC_LikeT(0L, "null");

		_persistence.countByC_LikeT(0L, (String)null);
	}

	@Test
	public void testCountByC_N() throws Exception {
		_persistence.countByC_N(RandomTestUtil.nextLong(), "");

		_persistence.countByC_N(0L, "null");

		_persistence.countByC_N(0L, (String)null);
	}

	@Test
	public void testCountByC_LikeN() throws Exception {
		_persistence.countByC_LikeN(RandomTestUtil.nextLong(), "");

		_persistence.countByC_LikeN(0L, "null");

		_persistence.countByC_LikeN(0L, (String)null);
	}

	@Test
	public void testCountByGtO_C_P() throws Exception {
		_persistence.countByGtO_C_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByGtO_C_P(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_P_LikeN() throws Exception {
		_persistence.countByC_P_LikeN(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByC_P_LikeN(0L, 0L, "null");

		_persistence.countByC_P_LikeN(0L, 0L, (String)null);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Organization newOrganization = addOrganization();

		Organization existingOrganization = _persistence.findByPrimaryKey(
			newOrganization.getPrimaryKey());

		Assert.assertEquals(existingOrganization, newOrganization);
	}

	@Test(expected = NoSuchOrganizationException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Organization> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Organization_", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "organizationId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "parentOrganizationId", true,
			"treePath", true, "name", true, "type", true, "recursable", true,
			"regionId", true, "countryId", true, "statusListTypeId", true,
			"comments", true, "logoId", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Organization newOrganization = addOrganization();

		Organization existingOrganization = _persistence.fetchByPrimaryKey(
			newOrganization.getPrimaryKey());

		Assert.assertEquals(existingOrganization, newOrganization);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Organization missingOrganization = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingOrganization);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Organization newOrganization1 = addOrganization();
		Organization newOrganization2 = addOrganization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOrganization1.getPrimaryKey());
		primaryKeys.add(newOrganization2.getPrimaryKey());

		Map<Serializable, Organization> organizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, organizations.size());
		Assert.assertEquals(
			newOrganization1,
			organizations.get(newOrganization1.getPrimaryKey()));
		Assert.assertEquals(
			newOrganization2,
			organizations.get(newOrganization2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Organization> organizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(organizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Organization newOrganization = addOrganization();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOrganization.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Organization> organizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, organizations.size());
		Assert.assertEquals(
			newOrganization,
			organizations.get(newOrganization.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Organization> organizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(organizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Organization newOrganization = addOrganization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOrganization.getPrimaryKey());

		Map<Serializable, Organization> organizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, organizations.size());
		Assert.assertEquals(
			newOrganization,
			organizations.get(newOrganization.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			OrganizationLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<Organization>() {

				@Override
				public void performAction(Organization organization) {
					Assert.assertNotNull(organization);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		Organization newOrganization = addOrganization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Organization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"organizationId", newOrganization.getOrganizationId()));

		List<Organization> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		Organization existingOrganization = result.get(0);

		Assert.assertEquals(existingOrganization, newOrganization);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Organization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"organizationId", RandomTestUtil.nextLong()));

		List<Organization> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		Organization newOrganization = addOrganization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Organization.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("organizationId"));

		Object newOrganizationId = newOrganization.getOrganizationId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"organizationId", new Object[] {newOrganizationId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingOrganizationId = result.get(0);

		Assert.assertEquals(existingOrganizationId, newOrganizationId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Organization.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("organizationId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"organizationId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Organization newOrganization = addOrganization();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newOrganization.getPrimaryKey()));
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

		Organization newOrganization = addOrganization();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Organization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"organizationId", newOrganization.getOrganizationId()));

		List<Organization> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(Organization organization) {
		Assert.assertEquals(
			Long.valueOf(organization.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				organization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			organization.getName(),
			ReflectionTestUtil.invoke(
				organization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			organization.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				organization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(organization.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				organization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected Organization addOrganization() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Organization organization = _persistence.create(pk);

		organization.setMvccVersion(RandomTestUtil.nextLong());

		organization.setCtCollectionId(RandomTestUtil.nextLong());

		organization.setUuid(RandomTestUtil.randomString());

		organization.setExternalReferenceCode(RandomTestUtil.randomString());

		organization.setCompanyId(RandomTestUtil.nextLong());

		organization.setUserId(RandomTestUtil.nextLong());

		organization.setUserName(RandomTestUtil.randomString());

		organization.setCreateDate(RandomTestUtil.nextDate());

		organization.setModifiedDate(RandomTestUtil.nextDate());

		organization.setParentOrganizationId(RandomTestUtil.nextLong());

		organization.setTreePath(RandomTestUtil.randomString());

		organization.setName(RandomTestUtil.randomString());

		organization.setType(RandomTestUtil.randomString());

		organization.setRecursable(RandomTestUtil.randomBoolean());

		organization.setRegionId(RandomTestUtil.nextLong());

		organization.setCountryId(RandomTestUtil.nextLong());

		organization.setStatusListTypeId(RandomTestUtil.nextLong());

		organization.setComments(RandomTestUtil.randomString());

		organization.setLogoId(RandomTestUtil.nextLong());

		organization.setStatus(RandomTestUtil.nextInt());

		_organizations.add(_persistence.update(organization));

		return organization;
	}

	private List<Organization> _organizations = new ArrayList<Organization>();
	private OrganizationPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}