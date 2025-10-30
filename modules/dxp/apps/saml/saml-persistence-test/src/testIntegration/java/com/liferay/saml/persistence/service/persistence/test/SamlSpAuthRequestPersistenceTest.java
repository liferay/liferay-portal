/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.saml.persistence.exception.NoSuchSpAuthRequestException;
import com.liferay.saml.persistence.model.SamlSpAuthRequest;
import com.liferay.saml.persistence.service.persistence.SamlSpAuthRequestPersistence;
import com.liferay.saml.persistence.service.persistence.SamlSpAuthRequestUtil;

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
public class SamlSpAuthRequestPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.saml.persistence.service"));

	@Before
	public void setUp() {
		_persistence = SamlSpAuthRequestUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SamlSpAuthRequest> iterator = _samlSpAuthRequests.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpAuthRequest samlSpAuthRequest = _persistence.create(pk);

		Assert.assertNotNull(samlSpAuthRequest);

		Assert.assertEquals(samlSpAuthRequest.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SamlSpAuthRequest newSamlSpAuthRequest = addSamlSpAuthRequest();

		_persistence.remove(newSamlSpAuthRequest);

		SamlSpAuthRequest existingSamlSpAuthRequest =
			_persistence.fetchByPrimaryKey(
				newSamlSpAuthRequest.getPrimaryKey());

		Assert.assertNull(existingSamlSpAuthRequest);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSamlSpAuthRequest();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpAuthRequest newSamlSpAuthRequest = _persistence.create(pk);

		newSamlSpAuthRequest.setCompanyId(RandomTestUtil.nextLong());

		newSamlSpAuthRequest.setCreateDate(RandomTestUtil.nextDate());

		newSamlSpAuthRequest.setSamlIdpEntityId(RandomTestUtil.randomString());

		newSamlSpAuthRequest.setSamlRelayState(RandomTestUtil.randomString());

		newSamlSpAuthRequest.setSamlSpAuthRequestKey(
			RandomTestUtil.randomString());

		_samlSpAuthRequests.add(_persistence.update(newSamlSpAuthRequest));

		SamlSpAuthRequest existingSamlSpAuthRequest =
			_persistence.findByPrimaryKey(newSamlSpAuthRequest.getPrimaryKey());

		Assert.assertEquals(
			existingSamlSpAuthRequest.getSamlSpAuthnRequestId(),
			newSamlSpAuthRequest.getSamlSpAuthnRequestId());
		Assert.assertEquals(
			existingSamlSpAuthRequest.getCompanyId(),
			newSamlSpAuthRequest.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSamlSpAuthRequest.getCreateDate()),
			Time.getShortTimestamp(newSamlSpAuthRequest.getCreateDate()));
		Assert.assertEquals(
			existingSamlSpAuthRequest.getSamlIdpEntityId(),
			newSamlSpAuthRequest.getSamlIdpEntityId());
		Assert.assertEquals(
			existingSamlSpAuthRequest.getSamlRelayState(),
			newSamlSpAuthRequest.getSamlRelayState());
		Assert.assertEquals(
			existingSamlSpAuthRequest.getSamlSpAuthRequestKey(),
			newSamlSpAuthRequest.getSamlSpAuthRequestKey());
	}

	@Test
	public void testCountByLtCreateDate() throws Exception {
		_persistence.countByLtCreateDate(RandomTestUtil.nextDate());

		_persistence.countByLtCreateDate(RandomTestUtil.nextDate());
	}

	@Test
	public void testCountBySIEI_SSARK() throws Exception {
		_persistence.countBySIEI_SSARK("", "");

		_persistence.countBySIEI_SSARK("null", "null");

		_persistence.countBySIEI_SSARK((String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SamlSpAuthRequest newSamlSpAuthRequest = addSamlSpAuthRequest();

		SamlSpAuthRequest existingSamlSpAuthRequest =
			_persistence.findByPrimaryKey(newSamlSpAuthRequest.getPrimaryKey());

		Assert.assertEquals(existingSamlSpAuthRequest, newSamlSpAuthRequest);
	}

	@Test(expected = NoSuchSpAuthRequestException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SamlSpAuthRequest> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SamlSpAuthRequest", "samlSpAuthnRequestId", true, "companyId",
			true, "createDate", true, "samlIdpEntityId", true, "samlRelayState",
			true, "samlSpAuthRequestKey", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SamlSpAuthRequest newSamlSpAuthRequest = addSamlSpAuthRequest();

		SamlSpAuthRequest existingSamlSpAuthRequest =
			_persistence.fetchByPrimaryKey(
				newSamlSpAuthRequest.getPrimaryKey());

		Assert.assertEquals(existingSamlSpAuthRequest, newSamlSpAuthRequest);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpAuthRequest missingSamlSpAuthRequest =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSamlSpAuthRequest);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SamlSpAuthRequest newSamlSpAuthRequest1 = addSamlSpAuthRequest();
		SamlSpAuthRequest newSamlSpAuthRequest2 = addSamlSpAuthRequest();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlSpAuthRequest1.getPrimaryKey());
		primaryKeys.add(newSamlSpAuthRequest2.getPrimaryKey());

		Map<Serializable, SamlSpAuthRequest> samlSpAuthRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, samlSpAuthRequests.size());
		Assert.assertEquals(
			newSamlSpAuthRequest1,
			samlSpAuthRequests.get(newSamlSpAuthRequest1.getPrimaryKey()));
		Assert.assertEquals(
			newSamlSpAuthRequest2,
			samlSpAuthRequests.get(newSamlSpAuthRequest2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SamlSpAuthRequest> samlSpAuthRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlSpAuthRequests.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SamlSpAuthRequest newSamlSpAuthRequest = addSamlSpAuthRequest();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlSpAuthRequest.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SamlSpAuthRequest> samlSpAuthRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlSpAuthRequests.size());
		Assert.assertEquals(
			newSamlSpAuthRequest,
			samlSpAuthRequests.get(newSamlSpAuthRequest.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SamlSpAuthRequest> samlSpAuthRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlSpAuthRequests.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SamlSpAuthRequest newSamlSpAuthRequest = addSamlSpAuthRequest();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlSpAuthRequest.getPrimaryKey());

		Map<Serializable, SamlSpAuthRequest> samlSpAuthRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlSpAuthRequests.size());
		Assert.assertEquals(
			newSamlSpAuthRequest,
			samlSpAuthRequests.get(newSamlSpAuthRequest.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SamlSpAuthRequest newSamlSpAuthRequest = addSamlSpAuthRequest();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newSamlSpAuthRequest.getPrimaryKey()));
	}

	private void _assertOriginalValues(SamlSpAuthRequest samlSpAuthRequest) {
		Assert.assertEquals(
			samlSpAuthRequest.getSamlIdpEntityId(),
			ReflectionTestUtil.invoke(
				samlSpAuthRequest, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "samlIdpEntityId"));
		Assert.assertEquals(
			samlSpAuthRequest.getSamlSpAuthRequestKey(),
			ReflectionTestUtil.invoke(
				samlSpAuthRequest, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "samlSpAuthRequestKey"));
	}

	protected SamlSpAuthRequest addSamlSpAuthRequest() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpAuthRequest samlSpAuthRequest = _persistence.create(pk);

		samlSpAuthRequest.setCompanyId(RandomTestUtil.nextLong());

		samlSpAuthRequest.setCreateDate(RandomTestUtil.nextDate());

		samlSpAuthRequest.setSamlIdpEntityId(RandomTestUtil.randomString());

		samlSpAuthRequest.setSamlRelayState(RandomTestUtil.randomString());

		samlSpAuthRequest.setSamlSpAuthRequestKey(
			RandomTestUtil.randomString());

		_samlSpAuthRequests.add(_persistence.update(samlSpAuthRequest));

		return samlSpAuthRequest;
	}

	private List<SamlSpAuthRequest> _samlSpAuthRequests =
		new ArrayList<SamlSpAuthRequest>();
	private SamlSpAuthRequestPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}