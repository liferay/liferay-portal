/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.exception.NoSuchFeedException;
import com.liferay.journal.model.JournalFeed;
import com.liferay.journal.service.JournalFeedLocalServiceUtil;
import com.liferay.journal.service.persistence.JournalFeedPersistence;
import com.liferay.journal.service.persistence.JournalFeedUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
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
public class JournalFeedPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.journal.service"));

	@Before
	public void setUp() {
		_persistence = JournalFeedUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<JournalFeed> iterator = _journalFeeds.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalFeed journalFeed = _persistence.create(pk);

		Assert.assertNotNull(journalFeed);

		Assert.assertEquals(journalFeed.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		JournalFeed newJournalFeed = addJournalFeed();

		_persistence.remove(newJournalFeed);

		JournalFeed existingJournalFeed = _persistence.fetchByPrimaryKey(
			newJournalFeed.getPrimaryKey());

		Assert.assertNull(existingJournalFeed);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addJournalFeed();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		JournalFeed newJournalFeed = addJournalFeed();

		newJournalFeed.setCtCollectionId(RandomTestUtil.nextLong());

		newJournalFeed.setUuid(RandomTestUtil.randomString());

		newJournalFeed.setGroupId(RandomTestUtil.nextLong());

		newJournalFeed.setCompanyId(RandomTestUtil.nextLong());

		newJournalFeed.setUserId(RandomTestUtil.nextLong());

		newJournalFeed.setUserName(RandomTestUtil.randomString());

		newJournalFeed.setCreateDate(RandomTestUtil.nextDate());

		newJournalFeed.setModifiedDate(RandomTestUtil.nextDate());

		newJournalFeed.setFeedId(RandomTestUtil.randomString());

		newJournalFeed.setName(RandomTestUtil.randomString());

		newJournalFeed.setDescription(RandomTestUtil.randomString());

		newJournalFeed.setDDMStructureId(RandomTestUtil.nextLong());

		newJournalFeed.setDDMTemplateKey(RandomTestUtil.randomString());

		newJournalFeed.setDDMRendererTemplateKey(RandomTestUtil.randomString());

		newJournalFeed.setDelta(RandomTestUtil.nextInt());

		newJournalFeed.setOrderByCol(RandomTestUtil.randomString());

		newJournalFeed.setOrderByType(RandomTestUtil.randomString());

		newJournalFeed.setTargetLayoutFriendlyUrl(
			RandomTestUtil.randomString());

		newJournalFeed.setTargetPortletId(RandomTestUtil.randomString());

		newJournalFeed.setContentField(RandomTestUtil.randomString());

		newJournalFeed.setFeedFormat(RandomTestUtil.randomString());

		newJournalFeed.setFeedVersion(RandomTestUtil.nextDouble());

		newJournalFeed.setLastPublishDate(RandomTestUtil.nextDate());

		newJournalFeed = _persistence.update(newJournalFeed);

		_journalFeeds.add(newJournalFeed);

		JournalFeed existingJournalFeed = _persistence.findByPrimaryKey(
			newJournalFeed.getPrimaryKey());

		Assert.assertEquals(
			existingJournalFeed.getMvccVersion(),
			newJournalFeed.getMvccVersion());
		Assert.assertEquals(
			existingJournalFeed.getCtCollectionId(),
			newJournalFeed.getCtCollectionId());
		Assert.assertEquals(
			existingJournalFeed.getUuid(), newJournalFeed.getUuid());
		Assert.assertEquals(
			existingJournalFeed.getId(), newJournalFeed.getId());
		Assert.assertEquals(
			existingJournalFeed.getGroupId(), newJournalFeed.getGroupId());
		Assert.assertEquals(
			existingJournalFeed.getCompanyId(), newJournalFeed.getCompanyId());
		Assert.assertEquals(
			existingJournalFeed.getUserId(), newJournalFeed.getUserId());
		Assert.assertEquals(
			existingJournalFeed.getUserName(), newJournalFeed.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalFeed.getCreateDate()),
			Time.getShortTimestamp(newJournalFeed.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalFeed.getModifiedDate()),
			Time.getShortTimestamp(newJournalFeed.getModifiedDate()));
		Assert.assertEquals(
			existingJournalFeed.getFeedId(), newJournalFeed.getFeedId());
		Assert.assertEquals(
			existingJournalFeed.getName(), newJournalFeed.getName());
		Assert.assertEquals(
			existingJournalFeed.getDescription(),
			newJournalFeed.getDescription());
		Assert.assertEquals(
			existingJournalFeed.getDDMStructureId(),
			newJournalFeed.getDDMStructureId());
		Assert.assertEquals(
			existingJournalFeed.getDDMTemplateKey(),
			newJournalFeed.getDDMTemplateKey());
		Assert.assertEquals(
			existingJournalFeed.getDDMRendererTemplateKey(),
			newJournalFeed.getDDMRendererTemplateKey());
		Assert.assertEquals(
			existingJournalFeed.getDelta(), newJournalFeed.getDelta());
		Assert.assertEquals(
			existingJournalFeed.getOrderByCol(),
			newJournalFeed.getOrderByCol());
		Assert.assertEquals(
			existingJournalFeed.getOrderByType(),
			newJournalFeed.getOrderByType());
		Assert.assertEquals(
			existingJournalFeed.getTargetLayoutFriendlyUrl(),
			newJournalFeed.getTargetLayoutFriendlyUrl());
		Assert.assertEquals(
			existingJournalFeed.getTargetPortletId(),
			newJournalFeed.getTargetPortletId());
		Assert.assertEquals(
			existingJournalFeed.getContentField(),
			newJournalFeed.getContentField());
		Assert.assertEquals(
			existingJournalFeed.getFeedFormat(),
			newJournalFeed.getFeedFormat());
		AssertUtils.assertEquals(
			existingJournalFeed.getFeedVersion(),
			newJournalFeed.getFeedVersion());
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalFeed.getLastPublishDate()),
			Time.getShortTimestamp(newJournalFeed.getLastPublishDate()));
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
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByG_F() throws Exception {
		_persistence.countByG_F(RandomTestUtil.nextLong(), "");

		_persistence.countByG_F(0L, "null");

		_persistence.countByG_F(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		JournalFeed newJournalFeed = addJournalFeed();

		JournalFeed existingJournalFeed = _persistence.findByPrimaryKey(
			newJournalFeed.getPrimaryKey());

		Assert.assertEquals(existingJournalFeed, newJournalFeed);
	}

	@Test(expected = NoSuchFeedException.class)
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

	protected OrderByComparator<JournalFeed> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"JournalFeed", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "id", true, "groupId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"feedId", true, "name", true, "description", true, "DDMStructureId",
			true, "DDMTemplateKey", true, "DDMRendererTemplateKey", true,
			"delta", true, "orderByCol", true, "orderByType", true,
			"targetLayoutFriendlyUrl", true, "targetPortletId", true,
			"contentField", true, "feedFormat", true, "feedVersion", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		JournalFeed newJournalFeed = addJournalFeed();

		JournalFeed existingJournalFeed = _persistence.fetchByPrimaryKey(
			newJournalFeed.getPrimaryKey());

		Assert.assertEquals(existingJournalFeed, newJournalFeed);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalFeed missingJournalFeed = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingJournalFeed);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		JournalFeed newJournalFeed1 = addJournalFeed();
		JournalFeed newJournalFeed2 = addJournalFeed();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalFeed1.getPrimaryKey());
		primaryKeys.add(newJournalFeed2.getPrimaryKey());

		Map<Serializable, JournalFeed> journalFeeds =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, journalFeeds.size());
		Assert.assertEquals(
			newJournalFeed1, journalFeeds.get(newJournalFeed1.getPrimaryKey()));
		Assert.assertEquals(
			newJournalFeed2, journalFeeds.get(newJournalFeed2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, JournalFeed> journalFeeds =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(journalFeeds.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		JournalFeed newJournalFeed = addJournalFeed();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalFeed.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, JournalFeed> journalFeeds =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, journalFeeds.size());
		Assert.assertEquals(
			newJournalFeed, journalFeeds.get(newJournalFeed.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, JournalFeed> journalFeeds =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(journalFeeds.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		JournalFeed newJournalFeed = addJournalFeed();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalFeed.getPrimaryKey());

		Map<Serializable, JournalFeed> journalFeeds =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, journalFeeds.size());
		Assert.assertEquals(
			newJournalFeed, journalFeeds.get(newJournalFeed.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			JournalFeedLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<JournalFeed>() {

				@Override
				public void performAction(JournalFeed journalFeed) {
					Assert.assertNotNull(journalFeed);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		JournalFeed newJournalFeed = addJournalFeed();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			JournalFeed.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("id", newJournalFeed.getId()));

		List<JournalFeed> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		JournalFeed existingJournalFeed = result.get(0);

		Assert.assertEquals(existingJournalFeed, newJournalFeed);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			JournalFeed.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("id", RandomTestUtil.nextLong()));

		List<JournalFeed> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		JournalFeed newJournalFeed = addJournalFeed();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			JournalFeed.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("id"));

		Object newId = newJournalFeed.getId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in("id", new Object[] {newId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingId = result.get(0);

		Assert.assertEquals(existingId, newId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			JournalFeed.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("id"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"id", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		JournalFeed newJournalFeed = addJournalFeed();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newJournalFeed.getPrimaryKey()));
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

		JournalFeed newJournalFeed = addJournalFeed();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			JournalFeed.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("id", newJournalFeed.getId()));

		List<JournalFeed> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(JournalFeed journalFeed) {
		Assert.assertEquals(
			journalFeed.getUuid(),
			ReflectionTestUtil.invoke(
				journalFeed, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(journalFeed.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				journalFeed, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(journalFeed.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				journalFeed, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			journalFeed.getFeedId(),
			ReflectionTestUtil.invoke(
				journalFeed, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "feedId"));
	}

	protected JournalFeed addJournalFeed() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalFeed journalFeed = _persistence.create(pk);

		journalFeed.setCtCollectionId(RandomTestUtil.nextLong());

		journalFeed.setUuid(RandomTestUtil.randomString());

		journalFeed.setGroupId(RandomTestUtil.nextLong());

		journalFeed.setCompanyId(RandomTestUtil.nextLong());

		journalFeed.setUserId(RandomTestUtil.nextLong());

		journalFeed.setUserName(RandomTestUtil.randomString());

		journalFeed.setCreateDate(RandomTestUtil.nextDate());

		journalFeed.setModifiedDate(RandomTestUtil.nextDate());

		journalFeed.setFeedId(RandomTestUtil.randomString());

		journalFeed.setName(RandomTestUtil.randomString());

		journalFeed.setDescription(RandomTestUtil.randomString());

		journalFeed.setDDMStructureId(RandomTestUtil.nextLong());

		journalFeed.setDDMTemplateKey(RandomTestUtil.randomString());

		journalFeed.setDDMRendererTemplateKey(RandomTestUtil.randomString());

		journalFeed.setDelta(RandomTestUtil.nextInt());

		journalFeed.setOrderByCol(RandomTestUtil.randomString());

		journalFeed.setOrderByType(RandomTestUtil.randomString());

		journalFeed.setTargetLayoutFriendlyUrl(RandomTestUtil.randomString());

		journalFeed.setTargetPortletId(RandomTestUtil.randomString());

		journalFeed.setContentField(RandomTestUtil.randomString());

		journalFeed.setFeedFormat(RandomTestUtil.randomString());

		journalFeed.setFeedVersion(RandomTestUtil.nextDouble());

		journalFeed.setLastPublishDate(RandomTestUtil.nextDate());

		_journalFeeds.add(_persistence.update(journalFeed));

		return journalFeed;
	}

	private List<JournalFeed> _journalFeeds = new ArrayList<JournalFeed>();
	private JournalFeedPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1717880535