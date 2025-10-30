/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateGroupExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.GroupUtil;
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
public class GroupPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = GroupUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Group> iterator = _groups.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Group group = _persistence.create(pk);

		Assert.assertNotNull(group);

		Assert.assertEquals(group.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Group newGroup = addGroup();

		_persistence.remove(newGroup);

		Group existingGroup = _persistence.fetchByPrimaryKey(
			newGroup.getPrimaryKey());

		Assert.assertNull(existingGroup);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addGroup();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Group newGroup = _persistence.create(pk);

		newGroup.setMvccVersion(RandomTestUtil.nextLong());

		newGroup.setCtCollectionId(RandomTestUtil.nextLong());

		newGroup.setUuid(RandomTestUtil.randomString());

		newGroup.setExternalReferenceCode(RandomTestUtil.randomString());

		newGroup.setCompanyId(RandomTestUtil.nextLong());

		newGroup.setCreatorUserId(RandomTestUtil.nextLong());

		newGroup.setModifiedDate(RandomTestUtil.nextDate());

		newGroup.setClassNameId(RandomTestUtil.nextLong());

		newGroup.setClassPK(RandomTestUtil.nextLong());

		newGroup.setParentGroupId(RandomTestUtil.nextLong());

		newGroup.setLiveGroupId(RandomTestUtil.nextLong());

		newGroup.setTreePath(RandomTestUtil.randomString());

		newGroup.setGroupKey(RandomTestUtil.randomString());

		newGroup.setName(RandomTestUtil.randomString());

		newGroup.setDescription(RandomTestUtil.randomString());

		newGroup.setType(RandomTestUtil.nextInt());

		newGroup.setTypeSettings(RandomTestUtil.randomString());

		newGroup.setManualMembership(RandomTestUtil.randomBoolean());

		newGroup.setMembershipRestriction(RandomTestUtil.nextInt());

		newGroup.setFriendlyURL(RandomTestUtil.randomString());

		newGroup.setSite(RandomTestUtil.randomBoolean());

		newGroup.setRemoteStagingGroupCount(RandomTestUtil.nextInt());

		newGroup.setInheritContent(RandomTestUtil.randomBoolean());

		newGroup.setActive(RandomTestUtil.randomBoolean());

		_groups.add(_persistence.update(newGroup));

		Group existingGroup = _persistence.findByPrimaryKey(
			newGroup.getPrimaryKey());

		Assert.assertEquals(
			existingGroup.getMvccVersion(), newGroup.getMvccVersion());
		Assert.assertEquals(
			existingGroup.getCtCollectionId(), newGroup.getCtCollectionId());
		Assert.assertEquals(existingGroup.getUuid(), newGroup.getUuid());
		Assert.assertEquals(
			existingGroup.getExternalReferenceCode(),
			newGroup.getExternalReferenceCode());
		Assert.assertEquals(existingGroup.getGroupId(), newGroup.getGroupId());
		Assert.assertEquals(
			existingGroup.getCompanyId(), newGroup.getCompanyId());
		Assert.assertEquals(
			existingGroup.getCreatorUserId(), newGroup.getCreatorUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingGroup.getModifiedDate()),
			Time.getShortTimestamp(newGroup.getModifiedDate()));
		Assert.assertEquals(
			existingGroup.getClassNameId(), newGroup.getClassNameId());
		Assert.assertEquals(existingGroup.getClassPK(), newGroup.getClassPK());
		Assert.assertEquals(
			existingGroup.getParentGroupId(), newGroup.getParentGroupId());
		Assert.assertEquals(
			existingGroup.getLiveGroupId(), newGroup.getLiveGroupId());
		Assert.assertEquals(
			existingGroup.getTreePath(), newGroup.getTreePath());
		Assert.assertEquals(
			existingGroup.getGroupKey(), newGroup.getGroupKey());
		Assert.assertEquals(existingGroup.getName(), newGroup.getName());
		Assert.assertEquals(
			existingGroup.getDescription(), newGroup.getDescription());
		Assert.assertEquals(existingGroup.getType(), newGroup.getType());
		Assert.assertEquals(
			existingGroup.getTypeSettings(), newGroup.getTypeSettings());
		Assert.assertEquals(
			existingGroup.isManualMembership(), newGroup.isManualMembership());
		Assert.assertEquals(
			existingGroup.getMembershipRestriction(),
			newGroup.getMembershipRestriction());
		Assert.assertEquals(
			existingGroup.getFriendlyURL(), newGroup.getFriendlyURL());
		Assert.assertEquals(existingGroup.isSite(), newGroup.isSite());
		Assert.assertEquals(
			existingGroup.getRemoteStagingGroupCount(),
			newGroup.getRemoteStagingGroupCount());
		Assert.assertEquals(
			existingGroup.isInheritContent(), newGroup.isInheritContent());
		Assert.assertEquals(existingGroup.isActive(), newGroup.isActive());
	}

	@Test(expected = DuplicateGroupExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		Group group = addGroup();

		Group newGroup = addGroup();

		newGroup.setCompanyId(group.getCompanyId());

		newGroup = _persistence.update(newGroup);

		Session session = _persistence.getCurrentSession();

		session.evict(newGroup);

		newGroup.setExternalReferenceCode(group.getExternalReferenceCode());

		_persistence.update(newGroup);
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
	public void testCountByLiveGroupId() throws Exception {
		_persistence.countByLiveGroupId(RandomTestUtil.nextLong());

		_persistence.countByLiveGroupId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_P() throws Exception {
		_persistence.countByC_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_P(0L, 0L);
	}

	@Test
	public void testCountByC_GK() throws Exception {
		_persistence.countByC_GK(RandomTestUtil.nextLong(), "");

		_persistence.countByC_GK(0L, "null");

		_persistence.countByC_GK(0L, (String)null);
	}

	@Test
	public void testCountByC_GKArrayable() throws Exception {
		_persistence.countByC_GK(
			RandomTestUtil.nextLong(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			});
	}

	@Test
	public void testCountByC_F() throws Exception {
		_persistence.countByC_F(RandomTestUtil.nextLong(), "");

		_persistence.countByC_F(0L, "null");

		_persistence.countByC_F(0L, (String)null);
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_S(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_A() throws Exception {
		_persistence.countByC_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_CPK() throws Exception {
		_persistence.countByC_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_CPK(0L, 0L);
	}

	@Test
	public void testCountByT_A() throws Exception {
		_persistence.countByT_A(
			RandomTestUtil.nextInt(), RandomTestUtil.randomBoolean());

		_persistence.countByT_A(0, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByGtG_C_P() throws Exception {
		_persistence.countByGtG_C_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByGtG_C_P(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_C_P() throws Exception {
		_persistence.countByC_C_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_P(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_C_S() throws Exception {
		_persistence.countByC_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByC_C_S(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_P_S() throws Exception {
		_persistence.countByC_P_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByC_P_S(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_L_GK() throws Exception {
		_persistence.countByC_L_GK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByC_L_GK(0L, 0L, "null");

		_persistence.countByC_L_GK(0L, 0L, (String)null);
	}

	@Test
	public void testCountByC_LikeT_S() throws Exception {
		_persistence.countByC_LikeT_S(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByC_LikeT_S(
			0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByC_LikeT_S(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_LikeN_S() throws Exception {
		_persistence.countByC_LikeN_S(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByC_LikeN_S(
			0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByC_LikeN_S(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_S_A() throws Exception {
		_persistence.countByC_S_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean());

		_persistence.countByC_S_A(
			0L, RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByGtG_C_C_P() throws Exception {
		_persistence.countByGtG_C_C_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByGtG_C_C_P(0L, 0L, 0L, 0L);
	}

	@Test
	public void testCountByGtG_C_P_S() throws Exception {
		_persistence.countByGtG_C_P_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByGtG_C_P_S(
			0L, 0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_C_L_GK() throws Exception {
		_persistence.countByC_C_L_GK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), "");

		_persistence.countByC_C_L_GK(0L, 0L, 0L, "null");

		_persistence.countByC_C_L_GK(0L, 0L, 0L, (String)null);
	}

	@Test
	public void testCountByC_P_LikeN_S() throws Exception {
		_persistence.countByC_P_LikeN_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.randomBoolean());

		_persistence.countByC_P_LikeN_S(
			0L, 0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByC_P_LikeN_S(
			0L, 0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_P_S_I() throws Exception {
		_persistence.countByC_P_S_I(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());

		_persistence.countByC_P_S_I(
			0L, 0L, RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Group newGroup = addGroup();

		Group existingGroup = _persistence.findByPrimaryKey(
			newGroup.getPrimaryKey());

		Assert.assertEquals(existingGroup, newGroup);
	}

	@Test(expected = NoSuchGroupException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Group> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Group_", "mvccVersion", true, "ctCollectionId", true, "uuid", true,
			"externalReferenceCode", true, "groupId", true, "companyId", true,
			"creatorUserId", true, "modifiedDate", true, "classNameId", true,
			"classPK", true, "parentGroupId", true, "liveGroupId", true,
			"treePath", true, "groupKey", true, "name", true, "description",
			true, "type", true, "manualMembership", true,
			"membershipRestriction", true, "friendlyURL", true, "site", true,
			"remoteStagingGroupCount", true, "inheritContent", true, "active",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Group newGroup = addGroup();

		Group existingGroup = _persistence.fetchByPrimaryKey(
			newGroup.getPrimaryKey());

		Assert.assertEquals(existingGroup, newGroup);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Group missingGroup = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingGroup);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Group newGroup1 = addGroup();
		Group newGroup2 = addGroup();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newGroup1.getPrimaryKey());
		primaryKeys.add(newGroup2.getPrimaryKey());

		Map<Serializable, Group> groups = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, groups.size());
		Assert.assertEquals(newGroup1, groups.get(newGroup1.getPrimaryKey()));
		Assert.assertEquals(newGroup2, groups.get(newGroup2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Group> groups = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(groups.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Group newGroup = addGroup();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newGroup.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Group> groups = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, groups.size());
		Assert.assertEquals(newGroup, groups.get(newGroup.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Group> groups = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(groups.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Group newGroup = addGroup();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newGroup.getPrimaryKey());

		Map<Serializable, Group> groups = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, groups.size());
		Assert.assertEquals(newGroup, groups.get(newGroup.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Group newGroup = addGroup();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newGroup.getPrimaryKey()));
	}

	private void _assertOriginalValues(Group group) {
		Assert.assertEquals(
			group.getUuid(),
			ReflectionTestUtil.invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"uuid_"));
		Assert.assertEquals(
			Long.valueOf(group.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));

		Assert.assertEquals(
			Long.valueOf(group.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"companyId"));
		Assert.assertEquals(
			group.getGroupKey(),
			ReflectionTestUtil.invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"groupKey"));

		Assert.assertEquals(
			Long.valueOf(group.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"companyId"));
		Assert.assertEquals(
			group.getFriendlyURL(),
			ReflectionTestUtil.invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"friendlyURL"));

		Assert.assertEquals(
			Long.valueOf(group.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"companyId"));
		Assert.assertEquals(
			Long.valueOf(group.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"classNameId"));
		Assert.assertEquals(
			Long.valueOf(group.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"classPK"));

		Assert.assertEquals(
			Long.valueOf(group.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"companyId"));
		Assert.assertEquals(
			Long.valueOf(group.getLiveGroupId()),
			ReflectionTestUtil.<Long>invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"liveGroupId"));
		Assert.assertEquals(
			group.getGroupKey(),
			ReflectionTestUtil.invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"groupKey"));

		Assert.assertEquals(
			Long.valueOf(group.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"companyId"));
		Assert.assertEquals(
			Long.valueOf(group.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"classNameId"));
		Assert.assertEquals(
			Long.valueOf(group.getLiveGroupId()),
			ReflectionTestUtil.<Long>invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"liveGroupId"));
		Assert.assertEquals(
			group.getGroupKey(),
			ReflectionTestUtil.invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"groupKey"));

		Assert.assertEquals(
			group.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(group.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				group, "getColumnOriginalValue", new Class<?>[] {String.class},
				"companyId"));
	}

	protected Group addGroup() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Group group = _persistence.create(pk);

		group.setMvccVersion(RandomTestUtil.nextLong());

		group.setCtCollectionId(RandomTestUtil.nextLong());

		group.setUuid(RandomTestUtil.randomString());

		group.setExternalReferenceCode(RandomTestUtil.randomString());

		group.setCompanyId(RandomTestUtil.nextLong());

		group.setCreatorUserId(RandomTestUtil.nextLong());

		group.setModifiedDate(RandomTestUtil.nextDate());

		group.setClassNameId(RandomTestUtil.nextLong());

		group.setClassPK(RandomTestUtil.nextLong());

		group.setParentGroupId(RandomTestUtil.nextLong());

		group.setLiveGroupId(RandomTestUtil.nextLong());

		group.setTreePath(RandomTestUtil.randomString());

		group.setGroupKey(RandomTestUtil.randomString());

		group.setName(RandomTestUtil.randomString());

		group.setDescription(RandomTestUtil.randomString());

		group.setType(RandomTestUtil.nextInt());

		group.setTypeSettings(RandomTestUtil.randomString());

		group.setManualMembership(RandomTestUtil.randomBoolean());

		group.setMembershipRestriction(RandomTestUtil.nextInt());

		group.setFriendlyURL(RandomTestUtil.randomString());

		group.setSite(RandomTestUtil.randomBoolean());

		group.setRemoteStagingGroupCount(RandomTestUtil.nextInt());

		group.setInheritContent(RandomTestUtil.randomBoolean());

		group.setActive(RandomTestUtil.randomBoolean());

		_groups.add(_persistence.update(group));

		return group;
	}

	private List<Group> _groups = new ArrayList<Group>();
	private GroupPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}