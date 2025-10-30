/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchTeamException;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.persistence.TeamPersistence;
import com.liferay.portal.kernel.service.persistence.TeamUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.SimplePermissionChecker;
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
public class TeamPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = TeamUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Team> iterator = _teams.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Team team = _persistence.create(pk);

		Assert.assertNotNull(team);

		Assert.assertEquals(team.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Team newTeam = addTeam();

		_persistence.remove(newTeam);

		Team existingTeam = _persistence.fetchByPrimaryKey(
			newTeam.getPrimaryKey());

		Assert.assertNull(existingTeam);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addTeam();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Team newTeam = _persistence.create(pk);

		newTeam.setMvccVersion(RandomTestUtil.nextLong());

		newTeam.setCtCollectionId(RandomTestUtil.nextLong());

		newTeam.setUuid(RandomTestUtil.randomString());

		newTeam.setCompanyId(RandomTestUtil.nextLong());

		newTeam.setUserId(RandomTestUtil.nextLong());

		newTeam.setUserName(RandomTestUtil.randomString());

		newTeam.setCreateDate(RandomTestUtil.nextDate());

		newTeam.setModifiedDate(RandomTestUtil.nextDate());

		newTeam.setGroupId(RandomTestUtil.nextLong());

		newTeam.setName(RandomTestUtil.randomString());

		newTeam.setDescription(RandomTestUtil.randomString());

		newTeam.setLastPublishDate(RandomTestUtil.nextDate());

		_teams.add(_persistence.update(newTeam));

		Team existingTeam = _persistence.findByPrimaryKey(
			newTeam.getPrimaryKey());

		Assert.assertEquals(
			existingTeam.getMvccVersion(), newTeam.getMvccVersion());
		Assert.assertEquals(
			existingTeam.getCtCollectionId(), newTeam.getCtCollectionId());
		Assert.assertEquals(existingTeam.getUuid(), newTeam.getUuid());
		Assert.assertEquals(existingTeam.getTeamId(), newTeam.getTeamId());
		Assert.assertEquals(
			existingTeam.getCompanyId(), newTeam.getCompanyId());
		Assert.assertEquals(existingTeam.getUserId(), newTeam.getUserId());
		Assert.assertEquals(existingTeam.getUserName(), newTeam.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingTeam.getCreateDate()),
			Time.getShortTimestamp(newTeam.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingTeam.getModifiedDate()),
			Time.getShortTimestamp(newTeam.getModifiedDate()));
		Assert.assertEquals(existingTeam.getGroupId(), newTeam.getGroupId());
		Assert.assertEquals(existingTeam.getName(), newTeam.getName());
		Assert.assertEquals(
			existingTeam.getDescription(), newTeam.getDescription());
		Assert.assertEquals(
			Time.getShortTimestamp(existingTeam.getLastPublishDate()),
			Time.getShortTimestamp(newTeam.getLastPublishDate()));
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
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByG_N() throws Exception {
		_persistence.countByG_N(RandomTestUtil.nextLong(), "");

		_persistence.countByG_N(0L, "null");

		_persistence.countByG_N(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Team newTeam = addTeam();

		Team existingTeam = _persistence.findByPrimaryKey(
			newTeam.getPrimaryKey());

		Assert.assertEquals(existingTeam, newTeam);
	}

	@Test(expected = NoSuchTeamException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Team> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Team", "mvccVersion", true, "ctCollectionId", true, "uuid", true,
			"teamId", true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "groupId", true, "name",
			true, "description", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Team newTeam = addTeam();

		Team existingTeam = _persistence.fetchByPrimaryKey(
			newTeam.getPrimaryKey());

		Assert.assertEquals(existingTeam, newTeam);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Team missingTeam = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingTeam);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Team newTeam1 = addTeam();
		Team newTeam2 = addTeam();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTeam1.getPrimaryKey());
		primaryKeys.add(newTeam2.getPrimaryKey());

		Map<Serializable, Team> teams = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, teams.size());
		Assert.assertEquals(newTeam1, teams.get(newTeam1.getPrimaryKey()));
		Assert.assertEquals(newTeam2, teams.get(newTeam2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Team> teams = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(teams.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Team newTeam = addTeam();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTeam.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Team> teams = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, teams.size());
		Assert.assertEquals(newTeam, teams.get(newTeam.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Team> teams = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(teams.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Team newTeam = addTeam();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTeam.getPrimaryKey());

		Map<Serializable, Team> teams = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, teams.size());
		Assert.assertEquals(newTeam, teams.get(newTeam.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Team newTeam = addTeam();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newTeam.getPrimaryKey()));
	}

	private void _assertOriginalValues(Team team) {
		Assert.assertEquals(
			team.getUuid(),
			ReflectionTestUtil.invoke(
				team, "getColumnOriginalValue", new Class<?>[] {String.class},
				"uuid_"));
		Assert.assertEquals(
			Long.valueOf(team.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				team, "getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));

		Assert.assertEquals(
			Long.valueOf(team.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				team, "getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));
		Assert.assertEquals(
			team.getName(),
			ReflectionTestUtil.invoke(
				team, "getColumnOriginalValue", new Class<?>[] {String.class},
				"name"));
	}

	protected Team addTeam() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Team team = _persistence.create(pk);

		team.setMvccVersion(RandomTestUtil.nextLong());

		team.setCtCollectionId(RandomTestUtil.nextLong());

		team.setUuid(RandomTestUtil.randomString());

		team.setCompanyId(RandomTestUtil.nextLong());

		team.setUserId(RandomTestUtil.nextLong());

		team.setUserName(RandomTestUtil.randomString());

		team.setCreateDate(RandomTestUtil.nextDate());

		team.setModifiedDate(RandomTestUtil.nextDate());

		team.setGroupId(RandomTestUtil.nextLong());

		team.setName(RandomTestUtil.randomString());

		team.setDescription(RandomTestUtil.randomString());

		team.setLastPublishDate(RandomTestUtil.nextDate());

		_teams.add(_persistence.update(team));

		return team;
	}

	private List<Team> _teams = new ArrayList<Team>();
	private TeamPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}