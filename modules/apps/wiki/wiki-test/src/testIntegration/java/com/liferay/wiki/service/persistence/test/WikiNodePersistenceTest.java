/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
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
import com.liferay.wiki.exception.DuplicateWikiNodeExternalReferenceCodeException;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.persistence.WikiNodePersistence;
import com.liferay.wiki.service.persistence.WikiNodeUtil;

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
public class WikiNodePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.wiki.service"));

	@Before
	public void setUp() {
		_persistence = WikiNodeUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<WikiNode> iterator = _wikiNodes.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WikiNode wikiNode = _persistence.create(pk);

		Assert.assertNotNull(wikiNode);

		Assert.assertEquals(wikiNode.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		WikiNode newWikiNode = addWikiNode();

		_persistence.remove(newWikiNode);

		WikiNode existingWikiNode = _persistence.fetchByPrimaryKey(
			newWikiNode.getPrimaryKey());

		Assert.assertNull(existingWikiNode);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addWikiNode();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WikiNode newWikiNode = _persistence.create(pk);

		newWikiNode.setMvccVersion(RandomTestUtil.nextLong());

		newWikiNode.setCtCollectionId(RandomTestUtil.nextLong());

		newWikiNode.setUuid(RandomTestUtil.randomString());

		newWikiNode.setExternalReferenceCode(RandomTestUtil.randomString());

		newWikiNode.setGroupId(RandomTestUtil.nextLong());

		newWikiNode.setCompanyId(RandomTestUtil.nextLong());

		newWikiNode.setUserId(RandomTestUtil.nextLong());

		newWikiNode.setUserName(RandomTestUtil.randomString());

		newWikiNode.setCreateDate(RandomTestUtil.nextDate());

		newWikiNode.setModifiedDate(RandomTestUtil.nextDate());

		newWikiNode.setName(RandomTestUtil.randomString());

		newWikiNode.setDescription(RandomTestUtil.randomString());

		newWikiNode.setLastPostDate(RandomTestUtil.nextDate());

		newWikiNode.setLastPublishDate(RandomTestUtil.nextDate());

		newWikiNode.setStatus(RandomTestUtil.nextInt());

		newWikiNode.setStatusByUserId(RandomTestUtil.nextLong());

		newWikiNode.setStatusByUserName(RandomTestUtil.randomString());

		newWikiNode.setStatusDate(RandomTestUtil.nextDate());

		_wikiNodes.add(_persistence.update(newWikiNode));

		WikiNode existingWikiNode = _persistence.findByPrimaryKey(
			newWikiNode.getPrimaryKey());

		Assert.assertEquals(
			existingWikiNode.getMvccVersion(), newWikiNode.getMvccVersion());
		Assert.assertEquals(
			existingWikiNode.getCtCollectionId(),
			newWikiNode.getCtCollectionId());
		Assert.assertEquals(existingWikiNode.getUuid(), newWikiNode.getUuid());
		Assert.assertEquals(
			existingWikiNode.getExternalReferenceCode(),
			newWikiNode.getExternalReferenceCode());
		Assert.assertEquals(
			existingWikiNode.getNodeId(), newWikiNode.getNodeId());
		Assert.assertEquals(
			existingWikiNode.getGroupId(), newWikiNode.getGroupId());
		Assert.assertEquals(
			existingWikiNode.getCompanyId(), newWikiNode.getCompanyId());
		Assert.assertEquals(
			existingWikiNode.getUserId(), newWikiNode.getUserId());
		Assert.assertEquals(
			existingWikiNode.getUserName(), newWikiNode.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingWikiNode.getCreateDate()),
			Time.getShortTimestamp(newWikiNode.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingWikiNode.getModifiedDate()),
			Time.getShortTimestamp(newWikiNode.getModifiedDate()));
		Assert.assertEquals(existingWikiNode.getName(), newWikiNode.getName());
		Assert.assertEquals(
			existingWikiNode.getDescription(), newWikiNode.getDescription());
		Assert.assertEquals(
			Time.getShortTimestamp(existingWikiNode.getLastPostDate()),
			Time.getShortTimestamp(newWikiNode.getLastPostDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingWikiNode.getLastPublishDate()),
			Time.getShortTimestamp(newWikiNode.getLastPublishDate()));
		Assert.assertEquals(
			existingWikiNode.getStatus(), newWikiNode.getStatus());
		Assert.assertEquals(
			existingWikiNode.getStatusByUserId(),
			newWikiNode.getStatusByUserId());
		Assert.assertEquals(
			existingWikiNode.getStatusByUserName(),
			newWikiNode.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingWikiNode.getStatusDate()),
			Time.getShortTimestamp(newWikiNode.getStatusDate()));
	}

	@Test(expected = DuplicateWikiNodeExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		WikiNode wikiNode = addWikiNode();

		WikiNode newWikiNode = addWikiNode();

		newWikiNode.setGroupId(wikiNode.getGroupId());

		newWikiNode = _persistence.update(newWikiNode);

		Session session = _persistence.getCurrentSession();

		session.evict(newWikiNode);

		newWikiNode.setExternalReferenceCode(
			wikiNode.getExternalReferenceCode());

		_persistence.update(newWikiNode);
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByG_N() throws Exception {
		_persistence.countByG_N(RandomTestUtil.nextLong(), "");

		_persistence.countByG_N(0L, "null");

		_persistence.countByG_N(0L, (String)null);
	}

	@Test
	public void testCountByG_S() throws Exception {
		_persistence.countByG_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_S(0L, 0);
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_S(0L, 0);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		WikiNode newWikiNode = addWikiNode();

		WikiNode existingWikiNode = _persistence.findByPrimaryKey(
			newWikiNode.getPrimaryKey());

		Assert.assertEquals(existingWikiNode, newWikiNode);
	}

	@Test(expected = NoSuchNodeException.class)
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

	protected OrderByComparator<WikiNode> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"WikiNode", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "nodeId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "name", true,
			"description", true, "lastPostDate", true, "lastPublishDate", true,
			"status", true, "statusByUserId", true, "statusByUserName", true,
			"statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		WikiNode newWikiNode = addWikiNode();

		WikiNode existingWikiNode = _persistence.fetchByPrimaryKey(
			newWikiNode.getPrimaryKey());

		Assert.assertEquals(existingWikiNode, newWikiNode);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WikiNode missingWikiNode = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingWikiNode);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		WikiNode newWikiNode1 = addWikiNode();
		WikiNode newWikiNode2 = addWikiNode();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWikiNode1.getPrimaryKey());
		primaryKeys.add(newWikiNode2.getPrimaryKey());

		Map<Serializable, WikiNode> wikiNodes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, wikiNodes.size());
		Assert.assertEquals(
			newWikiNode1, wikiNodes.get(newWikiNode1.getPrimaryKey()));
		Assert.assertEquals(
			newWikiNode2, wikiNodes.get(newWikiNode2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, WikiNode> wikiNodes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(wikiNodes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		WikiNode newWikiNode = addWikiNode();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWikiNode.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, WikiNode> wikiNodes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, wikiNodes.size());
		Assert.assertEquals(
			newWikiNode, wikiNodes.get(newWikiNode.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, WikiNode> wikiNodes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(wikiNodes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		WikiNode newWikiNode = addWikiNode();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWikiNode.getPrimaryKey());

		Map<Serializable, WikiNode> wikiNodes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, wikiNodes.size());
		Assert.assertEquals(
			newWikiNode, wikiNodes.get(newWikiNode.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		WikiNode newWikiNode = addWikiNode();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newWikiNode.getPrimaryKey()));
	}

	private void _assertOriginalValues(WikiNode wikiNode) {
		Assert.assertEquals(
			wikiNode.getUuid(),
			ReflectionTestUtil.invoke(
				wikiNode, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(wikiNode.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				wikiNode, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(wikiNode.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				wikiNode, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			wikiNode.getName(),
			ReflectionTestUtil.invoke(
				wikiNode, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			wikiNode.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				wikiNode, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(wikiNode.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				wikiNode, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected WikiNode addWikiNode() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WikiNode wikiNode = _persistence.create(pk);

		wikiNode.setMvccVersion(RandomTestUtil.nextLong());

		wikiNode.setCtCollectionId(RandomTestUtil.nextLong());

		wikiNode.setUuid(RandomTestUtil.randomString());

		wikiNode.setExternalReferenceCode(RandomTestUtil.randomString());

		wikiNode.setGroupId(RandomTestUtil.nextLong());

		wikiNode.setCompanyId(RandomTestUtil.nextLong());

		wikiNode.setUserId(RandomTestUtil.nextLong());

		wikiNode.setUserName(RandomTestUtil.randomString());

		wikiNode.setCreateDate(RandomTestUtil.nextDate());

		wikiNode.setModifiedDate(RandomTestUtil.nextDate());

		wikiNode.setName(RandomTestUtil.randomString());

		wikiNode.setDescription(RandomTestUtil.randomString());

		wikiNode.setLastPostDate(RandomTestUtil.nextDate());

		wikiNode.setLastPublishDate(RandomTestUtil.nextDate());

		wikiNode.setStatus(RandomTestUtil.nextInt());

		wikiNode.setStatusByUserId(RandomTestUtil.nextLong());

		wikiNode.setStatusByUserName(RandomTestUtil.randomString());

		wikiNode.setStatusDate(RandomTestUtil.nextDate());

		_wikiNodes.add(_persistence.update(wikiNode));

		return wikiNode;
	}

	private List<WikiNode> _wikiNodes = new ArrayList<WikiNode>();
	private WikiNodePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}