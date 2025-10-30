/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.social.kernel.exception.NoSuchRequestException;
import com.liferay.social.kernel.model.SocialRequest;
import com.liferay.social.kernel.service.persistence.SocialRequestPersistence;
import com.liferay.social.kernel.service.persistence.SocialRequestUtil;

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
public class SocialRequestPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = SocialRequestUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SocialRequest> iterator = _socialRequests.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialRequest socialRequest = _persistence.create(pk);

		Assert.assertNotNull(socialRequest);

		Assert.assertEquals(socialRequest.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SocialRequest newSocialRequest = addSocialRequest();

		_persistence.remove(newSocialRequest);

		SocialRequest existingSocialRequest = _persistence.fetchByPrimaryKey(
			newSocialRequest.getPrimaryKey());

		Assert.assertNull(existingSocialRequest);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSocialRequest();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialRequest newSocialRequest = _persistence.create(pk);

		newSocialRequest.setMvccVersion(RandomTestUtil.nextLong());

		newSocialRequest.setCtCollectionId(RandomTestUtil.nextLong());

		newSocialRequest.setUuid(RandomTestUtil.randomString());

		newSocialRequest.setGroupId(RandomTestUtil.nextLong());

		newSocialRequest.setCompanyId(RandomTestUtil.nextLong());

		newSocialRequest.setUserId(RandomTestUtil.nextLong());

		newSocialRequest.setCreateDate(RandomTestUtil.nextLong());

		newSocialRequest.setModifiedDate(RandomTestUtil.nextLong());

		newSocialRequest.setClassNameId(RandomTestUtil.nextLong());

		newSocialRequest.setClassPK(RandomTestUtil.nextLong());

		newSocialRequest.setType(RandomTestUtil.nextInt());

		newSocialRequest.setExtraData(RandomTestUtil.randomString());

		newSocialRequest.setReceiverUserId(RandomTestUtil.nextLong());

		newSocialRequest.setStatus(RandomTestUtil.nextInt());

		_socialRequests.add(_persistence.update(newSocialRequest));

		SocialRequest existingSocialRequest = _persistence.findByPrimaryKey(
			newSocialRequest.getPrimaryKey());

		Assert.assertEquals(
			existingSocialRequest.getMvccVersion(),
			newSocialRequest.getMvccVersion());
		Assert.assertEquals(
			existingSocialRequest.getCtCollectionId(),
			newSocialRequest.getCtCollectionId());
		Assert.assertEquals(
			existingSocialRequest.getUuid(), newSocialRequest.getUuid());
		Assert.assertEquals(
			existingSocialRequest.getRequestId(),
			newSocialRequest.getRequestId());
		Assert.assertEquals(
			existingSocialRequest.getGroupId(), newSocialRequest.getGroupId());
		Assert.assertEquals(
			existingSocialRequest.getCompanyId(),
			newSocialRequest.getCompanyId());
		Assert.assertEquals(
			existingSocialRequest.getUserId(), newSocialRequest.getUserId());
		Assert.assertEquals(
			existingSocialRequest.getCreateDate(),
			newSocialRequest.getCreateDate());
		Assert.assertEquals(
			existingSocialRequest.getModifiedDate(),
			newSocialRequest.getModifiedDate());
		Assert.assertEquals(
			existingSocialRequest.getClassNameId(),
			newSocialRequest.getClassNameId());
		Assert.assertEquals(
			existingSocialRequest.getClassPK(), newSocialRequest.getClassPK());
		Assert.assertEquals(
			existingSocialRequest.getType(), newSocialRequest.getType());
		Assert.assertEquals(
			existingSocialRequest.getExtraData(),
			newSocialRequest.getExtraData());
		Assert.assertEquals(
			existingSocialRequest.getReceiverUserId(),
			newSocialRequest.getReceiverUserId());
		Assert.assertEquals(
			existingSocialRequest.getStatus(), newSocialRequest.getStatus());
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByReceiverUserId() throws Exception {
		_persistence.countByReceiverUserId(RandomTestUtil.nextLong());

		_persistence.countByReceiverUserId(0L);
	}

	@Test
	public void testCountByU_S() throws Exception {
		_persistence.countByU_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByU_S(0L, 0);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByR_S() throws Exception {
		_persistence.countByR_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByR_S(0L, 0);
	}

	@Test
	public void testCountByU_C_C_T_R() throws Exception {
		_persistence.countByU_C_C_T_R(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextLong());

		_persistence.countByU_C_C_T_R(0L, 0L, 0L, 0, 0L);
	}

	@Test
	public void testCountByU_C_C_T_S() throws Exception {
		_persistence.countByU_C_C_T_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt());

		_persistence.countByU_C_C_T_S(0L, 0L, 0L, 0, 0);
	}

	@Test
	public void testCountByC_C_T_R_S() throws Exception {
		_persistence.countByC_C_T_R_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByC_C_T_R_S(0L, 0L, 0, 0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SocialRequest newSocialRequest = addSocialRequest();

		SocialRequest existingSocialRequest = _persistence.findByPrimaryKey(
			newSocialRequest.getPrimaryKey());

		Assert.assertEquals(existingSocialRequest, newSocialRequest);
	}

	@Test(expected = NoSuchRequestException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SocialRequest> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SocialRequest", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "requestId", true, "groupId", true, "companyId", true,
			"userId", true, "createDate", true, "modifiedDate", true,
			"classNameId", true, "classPK", true, "type", true, "extraData",
			true, "receiverUserId", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SocialRequest newSocialRequest = addSocialRequest();

		SocialRequest existingSocialRequest = _persistence.fetchByPrimaryKey(
			newSocialRequest.getPrimaryKey());

		Assert.assertEquals(existingSocialRequest, newSocialRequest);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialRequest missingSocialRequest = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSocialRequest);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SocialRequest newSocialRequest1 = addSocialRequest();
		SocialRequest newSocialRequest2 = addSocialRequest();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSocialRequest1.getPrimaryKey());
		primaryKeys.add(newSocialRequest2.getPrimaryKey());

		Map<Serializable, SocialRequest> socialRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, socialRequests.size());
		Assert.assertEquals(
			newSocialRequest1,
			socialRequests.get(newSocialRequest1.getPrimaryKey()));
		Assert.assertEquals(
			newSocialRequest2,
			socialRequests.get(newSocialRequest2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SocialRequest> socialRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(socialRequests.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SocialRequest newSocialRequest = addSocialRequest();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSocialRequest.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SocialRequest> socialRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, socialRequests.size());
		Assert.assertEquals(
			newSocialRequest,
			socialRequests.get(newSocialRequest.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SocialRequest> socialRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(socialRequests.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SocialRequest newSocialRequest = addSocialRequest();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSocialRequest.getPrimaryKey());

		Map<Serializable, SocialRequest> socialRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, socialRequests.size());
		Assert.assertEquals(
			newSocialRequest,
			socialRequests.get(newSocialRequest.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SocialRequest newSocialRequest = addSocialRequest();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newSocialRequest.getPrimaryKey()));
	}

	private void _assertOriginalValues(SocialRequest socialRequest) {
		Assert.assertEquals(
			socialRequest.getUuid(),
			ReflectionTestUtil.invoke(
				socialRequest, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(socialRequest.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				socialRequest, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(socialRequest.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				socialRequest, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			Long.valueOf(socialRequest.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				socialRequest, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(socialRequest.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				socialRequest, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Integer.valueOf(socialRequest.getType()),
			ReflectionTestUtil.<Integer>invoke(
				socialRequest, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "type_"));
		Assert.assertEquals(
			Long.valueOf(socialRequest.getReceiverUserId()),
			ReflectionTestUtil.<Long>invoke(
				socialRequest, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "receiverUserId"));
	}

	protected SocialRequest addSocialRequest() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialRequest socialRequest = _persistence.create(pk);

		socialRequest.setMvccVersion(RandomTestUtil.nextLong());

		socialRequest.setCtCollectionId(RandomTestUtil.nextLong());

		socialRequest.setUuid(RandomTestUtil.randomString());

		socialRequest.setGroupId(RandomTestUtil.nextLong());

		socialRequest.setCompanyId(RandomTestUtil.nextLong());

		socialRequest.setUserId(RandomTestUtil.nextLong());

		socialRequest.setCreateDate(RandomTestUtil.nextLong());

		socialRequest.setModifiedDate(RandomTestUtil.nextLong());

		socialRequest.setClassNameId(RandomTestUtil.nextLong());

		socialRequest.setClassPK(RandomTestUtil.nextLong());

		socialRequest.setType(RandomTestUtil.nextInt());

		socialRequest.setExtraData(RandomTestUtil.randomString());

		socialRequest.setReceiverUserId(RandomTestUtil.nextLong());

		socialRequest.setStatus(RandomTestUtil.nextInt());

		_socialRequests.add(_persistence.update(socialRequest));

		return socialRequest;
	}

	private List<SocialRequest> _socialRequests =
		new ArrayList<SocialRequest>();
	private SocialRequestPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}