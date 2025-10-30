/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.persistence.WikiPagePersistence;
import com.liferay.wiki.service.persistence.WikiPageUtil;

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
public class WikiPagePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.wiki.service"));

	@Before
	public void setUp() {
		_persistence = WikiPageUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<WikiPage> iterator = _wikiPages.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WikiPage wikiPage = _persistence.create(pk);

		Assert.assertNotNull(wikiPage);

		Assert.assertEquals(wikiPage.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		WikiPage newWikiPage = addWikiPage();

		_persistence.remove(newWikiPage);

		WikiPage existingWikiPage = _persistence.fetchByPrimaryKey(
			newWikiPage.getPrimaryKey());

		Assert.assertNull(existingWikiPage);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addWikiPage();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WikiPage newWikiPage = _persistence.create(pk);

		newWikiPage.setMvccVersion(RandomTestUtil.nextLong());

		newWikiPage.setCtCollectionId(RandomTestUtil.nextLong());

		newWikiPage.setUuid(RandomTestUtil.randomString());

		newWikiPage.setResourcePrimKey(RandomTestUtil.nextLong());

		newWikiPage.setGroupId(RandomTestUtil.nextLong());

		newWikiPage.setCompanyId(RandomTestUtil.nextLong());

		newWikiPage.setUserId(RandomTestUtil.nextLong());

		newWikiPage.setUserName(RandomTestUtil.randomString());

		newWikiPage.setCreateDate(RandomTestUtil.nextDate());

		newWikiPage.setModifiedDate(RandomTestUtil.nextDate());

		newWikiPage.setExternalReferenceCode(RandomTestUtil.randomString());

		newWikiPage.setNodeId(RandomTestUtil.nextLong());

		newWikiPage.setTitle(RandomTestUtil.randomString());

		newWikiPage.setVersion(RandomTestUtil.nextDouble());

		newWikiPage.setMinorEdit(RandomTestUtil.randomBoolean());

		newWikiPage.setContent(RandomTestUtil.randomString());

		newWikiPage.setSummary(RandomTestUtil.randomString());

		newWikiPage.setFormat(RandomTestUtil.randomString());

		newWikiPage.setHead(RandomTestUtil.randomBoolean());

		newWikiPage.setParentTitle(RandomTestUtil.randomString());

		newWikiPage.setRedirectTitle(RandomTestUtil.randomString());

		newWikiPage.setLastPublishDate(RandomTestUtil.nextDate());

		newWikiPage.setStatus(RandomTestUtil.nextInt());

		newWikiPage.setStatusByUserId(RandomTestUtil.nextLong());

		newWikiPage.setStatusByUserName(RandomTestUtil.randomString());

		newWikiPage.setStatusDate(RandomTestUtil.nextDate());

		_wikiPages.add(_persistence.update(newWikiPage));

		WikiPage existingWikiPage = _persistence.findByPrimaryKey(
			newWikiPage.getPrimaryKey());

		Assert.assertEquals(
			existingWikiPage.getMvccVersion(), newWikiPage.getMvccVersion());
		Assert.assertEquals(
			existingWikiPage.getCtCollectionId(),
			newWikiPage.getCtCollectionId());
		Assert.assertEquals(existingWikiPage.getUuid(), newWikiPage.getUuid());
		Assert.assertEquals(
			existingWikiPage.getPageId(), newWikiPage.getPageId());
		Assert.assertEquals(
			existingWikiPage.getResourcePrimKey(),
			newWikiPage.getResourcePrimKey());
		Assert.assertEquals(
			existingWikiPage.getGroupId(), newWikiPage.getGroupId());
		Assert.assertEquals(
			existingWikiPage.getCompanyId(), newWikiPage.getCompanyId());
		Assert.assertEquals(
			existingWikiPage.getUserId(), newWikiPage.getUserId());
		Assert.assertEquals(
			existingWikiPage.getUserName(), newWikiPage.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingWikiPage.getCreateDate()),
			Time.getShortTimestamp(newWikiPage.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingWikiPage.getModifiedDate()),
			Time.getShortTimestamp(newWikiPage.getModifiedDate()));
		Assert.assertEquals(
			existingWikiPage.getExternalReferenceCode(),
			newWikiPage.getExternalReferenceCode());
		Assert.assertEquals(
			existingWikiPage.getNodeId(), newWikiPage.getNodeId());
		Assert.assertEquals(
			existingWikiPage.getTitle(), newWikiPage.getTitle());
		AssertUtils.assertEquals(
			existingWikiPage.getVersion(), newWikiPage.getVersion());
		Assert.assertEquals(
			existingWikiPage.isMinorEdit(), newWikiPage.isMinorEdit());
		Assert.assertEquals(
			existingWikiPage.getContent(), newWikiPage.getContent());
		Assert.assertEquals(
			existingWikiPage.getSummary(), newWikiPage.getSummary());
		Assert.assertEquals(
			existingWikiPage.getFormat(), newWikiPage.getFormat());
		Assert.assertEquals(existingWikiPage.isHead(), newWikiPage.isHead());
		Assert.assertEquals(
			existingWikiPage.getParentTitle(), newWikiPage.getParentTitle());
		Assert.assertEquals(
			existingWikiPage.getRedirectTitle(),
			newWikiPage.getRedirectTitle());
		Assert.assertEquals(
			Time.getShortTimestamp(existingWikiPage.getLastPublishDate()),
			Time.getShortTimestamp(newWikiPage.getLastPublishDate()));
		Assert.assertEquals(
			existingWikiPage.getStatus(), newWikiPage.getStatus());
		Assert.assertEquals(
			existingWikiPage.getStatusByUserId(),
			newWikiPage.getStatusByUserId());
		Assert.assertEquals(
			existingWikiPage.getStatusByUserName(),
			newWikiPage.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingWikiPage.getStatusDate()),
			Time.getShortTimestamp(newWikiPage.getStatusDate()));
	}

	@Test
	public void testCountByResourcePrimKey() throws Exception {
		_persistence.countByResourcePrimKey(RandomTestUtil.nextLong());

		_persistence.countByResourcePrimKey(0L);
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
	public void testCountByNodeId() throws Exception {
		_persistence.countByNodeId(RandomTestUtil.nextLong());

		_persistence.countByNodeId(0L);
	}

	@Test
	public void testCountByFormat() throws Exception {
		_persistence.countByFormat("");

		_persistence.countByFormat("null");

		_persistence.countByFormat((String)null);
	}

	@Test
	public void testCountByR_N() throws Exception {
		_persistence.countByR_N(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByR_N(0L, 0L);
	}

	@Test
	public void testCountByR_S() throws Exception {
		_persistence.countByR_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByR_S(0L, 0);
	}

	@Test
	public void testCountByG_ERC() throws Exception {
		_persistence.countByG_ERC(RandomTestUtil.nextLong(), "");

		_persistence.countByG_ERC(0L, "null");

		_persistence.countByG_ERC(0L, (String)null);
	}

	@Test
	public void testCountByN_T() throws Exception {
		_persistence.countByN_T(RandomTestUtil.nextLong(), "");

		_persistence.countByN_T(0L, "null");

		_persistence.countByN_T(0L, (String)null);
	}

	@Test
	public void testCountByN_H() throws Exception {
		_persistence.countByN_H(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByN_H(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByN_P() throws Exception {
		_persistence.countByN_P(RandomTestUtil.nextLong(), "");

		_persistence.countByN_P(0L, "null");

		_persistence.countByN_P(0L, (String)null);
	}

	@Test
	public void testCountByN_R() throws Exception {
		_persistence.countByN_R(RandomTestUtil.nextLong(), "");

		_persistence.countByN_R(0L, "null");

		_persistence.countByN_R(0L, (String)null);
	}

	@Test
	public void testCountByN_S() throws Exception {
		_persistence.countByN_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByN_S(0L, 0);
	}

	@Test
	public void testCountByR_N_V() throws Exception {
		_persistence.countByR_N_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextDouble());

		_persistence.countByR_N_V(0L, 0L, 0D);
	}

	@Test
	public void testCountByR_N_H() throws Exception {
		_persistence.countByR_N_H(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByR_N_H(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByR_N_S() throws Exception {
		_persistence.countByR_N_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByR_N_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_ERC_V() throws Exception {
		_persistence.countByG_ERC_V(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextDouble());

		_persistence.countByG_ERC_V(0L, "null", 0D);

		_persistence.countByG_ERC_V(0L, (String)null, 0D);
	}

	@Test
	public void testCountByG_N_H() throws Exception {
		_persistence.countByG_N_H(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByG_N_H(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_N_S() throws Exception {
		_persistence.countByG_N_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_N_S(0L, 0L, 0);
	}

	@Test
	public void testCountByU_N_S() throws Exception {
		_persistence.countByU_N_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByU_N_S(0L, 0L, 0);
	}

	@Test
	public void testCountByN_T_V() throws Exception {
		_persistence.countByN_T_V(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextDouble());

		_persistence.countByN_T_V(0L, "null", 0D);

		_persistence.countByN_T_V(0L, (String)null, 0D);
	}

	@Test
	public void testCountByN_T_H() throws Exception {
		_persistence.countByN_T_H(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByN_T_H(0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByN_T_H(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByN_T_S() throws Exception {
		_persistence.countByN_T_S(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByN_T_S(0L, "null", 0);

		_persistence.countByN_T_S(0L, (String)null, 0);
	}

	@Test
	public void testCountByN_H_P() throws Exception {
		_persistence.countByN_H_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByN_H_P(0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByN_H_P(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByN_H_R() throws Exception {
		_persistence.countByN_H_R(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByN_H_R(0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByN_H_R(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByN_H_S() throws Exception {
		_persistence.countByN_H_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByN_H_S(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByN_H_NotS() throws Exception {
		_persistence.countByN_H_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByN_H_NotS(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByG_U_N_S() throws Exception {
		_persistence.countByG_U_N_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_U_N_S(0L, 0L, 0L, 0);
	}

	@Test
	public void testCountByG_N_T_H() throws Exception {
		_persistence.countByG_N_T_H(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.randomBoolean());

		_persistence.countByG_N_T_H(
			0L, 0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByG_N_T_H(
			0L, 0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_N_H_S() throws Exception {
		_persistence.countByG_N_H_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());

		_persistence.countByG_N_H_S(0L, 0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByN_H_P_S() throws Exception {
		_persistence.countByN_H_P_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "",
			RandomTestUtil.nextInt());

		_persistence.countByN_H_P_S(
			0L, RandomTestUtil.randomBoolean(), "null", 0);

		_persistence.countByN_H_P_S(
			0L, RandomTestUtil.randomBoolean(), (String)null, 0);
	}

	@Test
	public void testCountByN_H_P_NotS() throws Exception {
		_persistence.countByN_H_P_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "",
			RandomTestUtil.nextInt());

		_persistence.countByN_H_P_NotS(
			0L, RandomTestUtil.randomBoolean(), "null", 0);

		_persistence.countByN_H_P_NotS(
			0L, RandomTestUtil.randomBoolean(), (String)null, 0);
	}

	@Test
	public void testCountByN_H_R_S() throws Exception {
		_persistence.countByN_H_R_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "",
			RandomTestUtil.nextInt());

		_persistence.countByN_H_R_S(
			0L, RandomTestUtil.randomBoolean(), "null", 0);

		_persistence.countByN_H_R_S(
			0L, RandomTestUtil.randomBoolean(), (String)null, 0);
	}

	@Test
	public void testCountByN_H_R_NotS() throws Exception {
		_persistence.countByN_H_R_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "",
			RandomTestUtil.nextInt());

		_persistence.countByN_H_R_NotS(
			0L, RandomTestUtil.randomBoolean(), "null", 0);

		_persistence.countByN_H_R_NotS(
			0L, RandomTestUtil.randomBoolean(), (String)null, 0);
	}

	@Test
	public void testCountByG_N_H_P_S() throws Exception {
		_persistence.countByG_N_H_P_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), "", RandomTestUtil.nextInt());

		_persistence.countByG_N_H_P_S(
			0L, 0L, RandomTestUtil.randomBoolean(), "null", 0);

		_persistence.countByG_N_H_P_S(
			0L, 0L, RandomTestUtil.randomBoolean(), (String)null, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		WikiPage newWikiPage = addWikiPage();

		WikiPage existingWikiPage = _persistence.findByPrimaryKey(
			newWikiPage.getPrimaryKey());

		Assert.assertEquals(existingWikiPage, newWikiPage);
	}

	@Test(expected = NoSuchPageException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<WikiPage> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"WikiPage", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "pageId", true, "resourcePrimKey", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "externalReferenceCode", true, "nodeId",
			true, "title", true, "version", true, "minorEdit", true, "summary",
			true, "format", true, "head", true, "parentTitle", true,
			"redirectTitle", true, "lastPublishDate", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		WikiPage newWikiPage = addWikiPage();

		WikiPage existingWikiPage = _persistence.fetchByPrimaryKey(
			newWikiPage.getPrimaryKey());

		Assert.assertEquals(existingWikiPage, newWikiPage);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WikiPage missingWikiPage = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingWikiPage);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		WikiPage newWikiPage1 = addWikiPage();
		WikiPage newWikiPage2 = addWikiPage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWikiPage1.getPrimaryKey());
		primaryKeys.add(newWikiPage2.getPrimaryKey());

		Map<Serializable, WikiPage> wikiPages = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, wikiPages.size());
		Assert.assertEquals(
			newWikiPage1, wikiPages.get(newWikiPage1.getPrimaryKey()));
		Assert.assertEquals(
			newWikiPage2, wikiPages.get(newWikiPage2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, WikiPage> wikiPages = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(wikiPages.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		WikiPage newWikiPage = addWikiPage();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWikiPage.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, WikiPage> wikiPages = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, wikiPages.size());
		Assert.assertEquals(
			newWikiPage, wikiPages.get(newWikiPage.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, WikiPage> wikiPages = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(wikiPages.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		WikiPage newWikiPage = addWikiPage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWikiPage.getPrimaryKey());

		Map<Serializable, WikiPage> wikiPages = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, wikiPages.size());
		Assert.assertEquals(
			newWikiPage, wikiPages.get(newWikiPage.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		WikiPage newWikiPage = addWikiPage();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newWikiPage.getPrimaryKey()));
	}

	private void _assertOriginalValues(WikiPage wikiPage) {
		Assert.assertEquals(
			wikiPage.getUuid(),
			ReflectionTestUtil.invoke(
				wikiPage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(wikiPage.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				wikiPage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(wikiPage.getResourcePrimKey()),
			ReflectionTestUtil.<Long>invoke(
				wikiPage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "resourcePrimKey"));
		Assert.assertEquals(
			Long.valueOf(wikiPage.getNodeId()),
			ReflectionTestUtil.<Long>invoke(
				wikiPage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "nodeId"));
		AssertUtils.assertEquals(
			wikiPage.getVersion(),
			ReflectionTestUtil.<Double>invoke(
				wikiPage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));

		Assert.assertEquals(
			Long.valueOf(wikiPage.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				wikiPage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			wikiPage.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				wikiPage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		AssertUtils.assertEquals(
			wikiPage.getVersion(),
			ReflectionTestUtil.<Double>invoke(
				wikiPage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));

		Assert.assertEquals(
			Long.valueOf(wikiPage.getNodeId()),
			ReflectionTestUtil.<Long>invoke(
				wikiPage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "nodeId"));
		Assert.assertEquals(
			wikiPage.getTitle(),
			ReflectionTestUtil.invoke(
				wikiPage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "title"));
		AssertUtils.assertEquals(
			wikiPage.getVersion(),
			ReflectionTestUtil.<Double>invoke(
				wikiPage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected WikiPage addWikiPage() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WikiPage wikiPage = _persistence.create(pk);

		wikiPage.setMvccVersion(RandomTestUtil.nextLong());

		wikiPage.setCtCollectionId(RandomTestUtil.nextLong());

		wikiPage.setUuid(RandomTestUtil.randomString());

		wikiPage.setResourcePrimKey(RandomTestUtil.nextLong());

		wikiPage.setGroupId(RandomTestUtil.nextLong());

		wikiPage.setCompanyId(RandomTestUtil.nextLong());

		wikiPage.setUserId(RandomTestUtil.nextLong());

		wikiPage.setUserName(RandomTestUtil.randomString());

		wikiPage.setCreateDate(RandomTestUtil.nextDate());

		wikiPage.setModifiedDate(RandomTestUtil.nextDate());

		wikiPage.setExternalReferenceCode(RandomTestUtil.randomString());

		wikiPage.setNodeId(RandomTestUtil.nextLong());

		wikiPage.setTitle(RandomTestUtil.randomString());

		wikiPage.setVersion(RandomTestUtil.nextDouble());

		wikiPage.setMinorEdit(RandomTestUtil.randomBoolean());

		wikiPage.setContent(RandomTestUtil.randomString());

		wikiPage.setSummary(RandomTestUtil.randomString());

		wikiPage.setFormat(RandomTestUtil.randomString());

		wikiPage.setHead(RandomTestUtil.randomBoolean());

		wikiPage.setParentTitle(RandomTestUtil.randomString());

		wikiPage.setRedirectTitle(RandomTestUtil.randomString());

		wikiPage.setLastPublishDate(RandomTestUtil.nextDate());

		wikiPage.setStatus(RandomTestUtil.nextInt());

		wikiPage.setStatusByUserId(RandomTestUtil.nextLong());

		wikiPage.setStatusByUserName(RandomTestUtil.randomString());

		wikiPage.setStatusDate(RandomTestUtil.nextDate());

		_wikiPages.add(_persistence.update(wikiPage));

		return wikiPage;
	}

	private List<WikiPage> _wikiPages = new ArrayList<WikiPage>();
	private WikiPagePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}