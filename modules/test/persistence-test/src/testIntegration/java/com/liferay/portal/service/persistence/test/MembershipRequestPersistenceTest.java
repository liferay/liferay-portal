/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchMembershipRequestException;
import com.liferay.portal.kernel.model.MembershipRequest;
import com.liferay.portal.kernel.service.persistence.MembershipRequestPersistence;
import com.liferay.portal.kernel.service.persistence.MembershipRequestUtil;
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
public class MembershipRequestPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = MembershipRequestUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<MembershipRequest> iterator = _membershipRequests.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MembershipRequest membershipRequest = _persistence.create(pk);

		Assert.assertNotNull(membershipRequest);

		Assert.assertEquals(membershipRequest.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		MembershipRequest newMembershipRequest = addMembershipRequest();

		_persistence.remove(newMembershipRequest);

		MembershipRequest existingMembershipRequest =
			_persistence.fetchByPrimaryKey(
				newMembershipRequest.getPrimaryKey());

		Assert.assertNull(existingMembershipRequest);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addMembershipRequest();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MembershipRequest newMembershipRequest = _persistence.create(pk);

		newMembershipRequest.setMvccVersion(RandomTestUtil.nextLong());

		newMembershipRequest.setGroupId(RandomTestUtil.nextLong());

		newMembershipRequest.setCompanyId(RandomTestUtil.nextLong());

		newMembershipRequest.setUserId(RandomTestUtil.nextLong());

		newMembershipRequest.setCreateDate(RandomTestUtil.nextDate());

		newMembershipRequest.setComments(RandomTestUtil.randomString());

		newMembershipRequest.setReplyComments(RandomTestUtil.randomString());

		newMembershipRequest.setReplyDate(RandomTestUtil.nextDate());

		newMembershipRequest.setReplierUserId(RandomTestUtil.nextLong());

		newMembershipRequest.setStatusId(RandomTestUtil.nextLong());

		_membershipRequests.add(_persistence.update(newMembershipRequest));

		MembershipRequest existingMembershipRequest =
			_persistence.findByPrimaryKey(newMembershipRequest.getPrimaryKey());

		Assert.assertEquals(
			existingMembershipRequest.getMvccVersion(),
			newMembershipRequest.getMvccVersion());
		Assert.assertEquals(
			existingMembershipRequest.getMembershipRequestId(),
			newMembershipRequest.getMembershipRequestId());
		Assert.assertEquals(
			existingMembershipRequest.getGroupId(),
			newMembershipRequest.getGroupId());
		Assert.assertEquals(
			existingMembershipRequest.getCompanyId(),
			newMembershipRequest.getCompanyId());
		Assert.assertEquals(
			existingMembershipRequest.getUserId(),
			newMembershipRequest.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMembershipRequest.getCreateDate()),
			Time.getShortTimestamp(newMembershipRequest.getCreateDate()));
		Assert.assertEquals(
			existingMembershipRequest.getComments(),
			newMembershipRequest.getComments());
		Assert.assertEquals(
			existingMembershipRequest.getReplyComments(),
			newMembershipRequest.getReplyComments());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMembershipRequest.getReplyDate()),
			Time.getShortTimestamp(newMembershipRequest.getReplyDate()));
		Assert.assertEquals(
			existingMembershipRequest.getReplierUserId(),
			newMembershipRequest.getReplierUserId());
		Assert.assertEquals(
			existingMembershipRequest.getStatusId(),
			newMembershipRequest.getStatusId());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByG_S() throws Exception {
		_persistence.countByG_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_S(0L, 0L);
	}

	@Test
	public void testCountByG_U_S() throws Exception {
		_persistence.countByG_U_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_U_S(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		MembershipRequest newMembershipRequest = addMembershipRequest();

		MembershipRequest existingMembershipRequest =
			_persistence.findByPrimaryKey(newMembershipRequest.getPrimaryKey());

		Assert.assertEquals(existingMembershipRequest, newMembershipRequest);
	}

	@Test(expected = NoSuchMembershipRequestException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<MembershipRequest> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"MembershipRequest", "mvccVersion", true, "membershipRequestId",
			true, "groupId", true, "companyId", true, "userId", true,
			"createDate", true, "comments", true, "replyComments", true,
			"replyDate", true, "replierUserId", true, "statusId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		MembershipRequest newMembershipRequest = addMembershipRequest();

		MembershipRequest existingMembershipRequest =
			_persistence.fetchByPrimaryKey(
				newMembershipRequest.getPrimaryKey());

		Assert.assertEquals(existingMembershipRequest, newMembershipRequest);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MembershipRequest missingMembershipRequest =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingMembershipRequest);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		MembershipRequest newMembershipRequest1 = addMembershipRequest();
		MembershipRequest newMembershipRequest2 = addMembershipRequest();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMembershipRequest1.getPrimaryKey());
		primaryKeys.add(newMembershipRequest2.getPrimaryKey());

		Map<Serializable, MembershipRequest> membershipRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, membershipRequests.size());
		Assert.assertEquals(
			newMembershipRequest1,
			membershipRequests.get(newMembershipRequest1.getPrimaryKey()));
		Assert.assertEquals(
			newMembershipRequest2,
			membershipRequests.get(newMembershipRequest2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, MembershipRequest> membershipRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(membershipRequests.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		MembershipRequest newMembershipRequest = addMembershipRequest();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMembershipRequest.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, MembershipRequest> membershipRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, membershipRequests.size());
		Assert.assertEquals(
			newMembershipRequest,
			membershipRequests.get(newMembershipRequest.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, MembershipRequest> membershipRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(membershipRequests.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		MembershipRequest newMembershipRequest = addMembershipRequest();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMembershipRequest.getPrimaryKey());

		Map<Serializable, MembershipRequest> membershipRequests =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, membershipRequests.size());
		Assert.assertEquals(
			newMembershipRequest,
			membershipRequests.get(newMembershipRequest.getPrimaryKey()));
	}

	protected MembershipRequest addMembershipRequest() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MembershipRequest membershipRequest = _persistence.create(pk);

		membershipRequest.setMvccVersion(RandomTestUtil.nextLong());

		membershipRequest.setGroupId(RandomTestUtil.nextLong());

		membershipRequest.setCompanyId(RandomTestUtil.nextLong());

		membershipRequest.setUserId(RandomTestUtil.nextLong());

		membershipRequest.setCreateDate(RandomTestUtil.nextDate());

		membershipRequest.setComments(RandomTestUtil.randomString());

		membershipRequest.setReplyComments(RandomTestUtil.randomString());

		membershipRequest.setReplyDate(RandomTestUtil.nextDate());

		membershipRequest.setReplierUserId(RandomTestUtil.nextLong());

		membershipRequest.setStatusId(RandomTestUtil.nextLong());

		_membershipRequests.add(_persistence.update(membershipRequest));

		return membershipRequest;
	}

	private List<MembershipRequest> _membershipRequests =
		new ArrayList<MembershipRequest>();
	private MembershipRequestPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}