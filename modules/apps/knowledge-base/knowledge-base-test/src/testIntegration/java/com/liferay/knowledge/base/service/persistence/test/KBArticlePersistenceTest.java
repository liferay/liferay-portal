/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.exception.NoSuchArticleException;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalServiceUtil;
import com.liferay.knowledge.base.service.persistence.KBArticlePersistence;
import com.liferay.knowledge.base.service.persistence.KBArticleUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class KBArticlePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.knowledge.base.service"));

	@Before
	public void setUp() {
		_persistence = KBArticleUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KBArticle> iterator = _kbArticles.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KBArticle kbArticle = _persistence.create(pk);

		Assert.assertNotNull(kbArticle);

		Assert.assertEquals(kbArticle.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KBArticle newKBArticle = addKBArticle();

		_persistence.remove(newKBArticle);

		KBArticle existingKBArticle = _persistence.fetchByPrimaryKey(
			newKBArticle.getPrimaryKey());

		Assert.assertNull(existingKBArticle);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKBArticle();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KBArticle newKBArticle = _persistence.create(pk);

		newKBArticle.setMvccVersion(RandomTestUtil.nextLong());

		newKBArticle.setCtCollectionId(RandomTestUtil.nextLong());

		newKBArticle.setUuid(RandomTestUtil.randomString());

		newKBArticle.setResourcePrimKey(RandomTestUtil.nextLong());

		newKBArticle.setGroupId(RandomTestUtil.nextLong());

		newKBArticle.setCompanyId(RandomTestUtil.nextLong());

		newKBArticle.setUserId(RandomTestUtil.nextLong());

		newKBArticle.setUserName(RandomTestUtil.randomString());

		newKBArticle.setCreateDate(RandomTestUtil.nextDate());

		newKBArticle.setModifiedDate(RandomTestUtil.nextDate());

		newKBArticle.setExternalReferenceCode(RandomTestUtil.randomString());

		newKBArticle.setRootResourcePrimKey(RandomTestUtil.nextLong());

		newKBArticle.setParentResourceClassNameId(RandomTestUtil.nextLong());

		newKBArticle.setParentResourcePrimKey(RandomTestUtil.nextLong());

		newKBArticle.setKbFolderId(RandomTestUtil.nextLong());

		newKBArticle.setVersion(RandomTestUtil.nextInt());

		newKBArticle.setTitle(RandomTestUtil.randomString());

		newKBArticle.setUrlTitle(RandomTestUtil.randomString());

		newKBArticle.setContent(RandomTestUtil.randomString());

		newKBArticle.setDescription(RandomTestUtil.randomString());

		newKBArticle.setPriority(RandomTestUtil.nextDouble());

		newKBArticle.setSections(RandomTestUtil.randomString());

		newKBArticle.setLatest(RandomTestUtil.randomBoolean());

		newKBArticle.setMain(RandomTestUtil.randomBoolean());

		newKBArticle.setSourceURL(RandomTestUtil.randomString());

		newKBArticle.setDisplayDate(RandomTestUtil.nextDate());

		newKBArticle.setExpirationDate(RandomTestUtil.nextDate());

		newKBArticle.setReviewDate(RandomTestUtil.nextDate());

		newKBArticle.setLastPublishDate(RandomTestUtil.nextDate());

		newKBArticle.setStatus(RandomTestUtil.nextInt());

		newKBArticle.setStatusByUserId(RandomTestUtil.nextLong());

		newKBArticle.setStatusByUserName(RandomTestUtil.randomString());

		newKBArticle.setStatusDate(RandomTestUtil.nextDate());

		_kbArticles.add(_persistence.update(newKBArticle));

		KBArticle existingKBArticle = _persistence.findByPrimaryKey(
			newKBArticle.getPrimaryKey());

		Assert.assertEquals(
			existingKBArticle.getMvccVersion(), newKBArticle.getMvccVersion());
		Assert.assertEquals(
			existingKBArticle.getCtCollectionId(),
			newKBArticle.getCtCollectionId());
		Assert.assertEquals(
			existingKBArticle.getUuid(), newKBArticle.getUuid());
		Assert.assertEquals(
			existingKBArticle.getKbArticleId(), newKBArticle.getKbArticleId());
		Assert.assertEquals(
			existingKBArticle.getResourcePrimKey(),
			newKBArticle.getResourcePrimKey());
		Assert.assertEquals(
			existingKBArticle.getGroupId(), newKBArticle.getGroupId());
		Assert.assertEquals(
			existingKBArticle.getCompanyId(), newKBArticle.getCompanyId());
		Assert.assertEquals(
			existingKBArticle.getUserId(), newKBArticle.getUserId());
		Assert.assertEquals(
			existingKBArticle.getUserName(), newKBArticle.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBArticle.getCreateDate()),
			Time.getShortTimestamp(newKBArticle.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBArticle.getModifiedDate()),
			Time.getShortTimestamp(newKBArticle.getModifiedDate()));
		Assert.assertEquals(
			existingKBArticle.getExternalReferenceCode(),
			newKBArticle.getExternalReferenceCode());
		Assert.assertEquals(
			existingKBArticle.getRootResourcePrimKey(),
			newKBArticle.getRootResourcePrimKey());
		Assert.assertEquals(
			existingKBArticle.getParentResourceClassNameId(),
			newKBArticle.getParentResourceClassNameId());
		Assert.assertEquals(
			existingKBArticle.getParentResourcePrimKey(),
			newKBArticle.getParentResourcePrimKey());
		Assert.assertEquals(
			existingKBArticle.getKbFolderId(), newKBArticle.getKbFolderId());
		Assert.assertEquals(
			existingKBArticle.getVersion(), newKBArticle.getVersion());
		Assert.assertEquals(
			existingKBArticle.getTitle(), newKBArticle.getTitle());
		Assert.assertEquals(
			existingKBArticle.getUrlTitle(), newKBArticle.getUrlTitle());
		Assert.assertEquals(
			existingKBArticle.getContent(), newKBArticle.getContent());
		Assert.assertEquals(
			existingKBArticle.getDescription(), newKBArticle.getDescription());
		AssertUtils.assertEquals(
			existingKBArticle.getPriority(), newKBArticle.getPriority());
		Assert.assertEquals(
			existingKBArticle.getSections(), newKBArticle.getSections());
		Assert.assertEquals(
			existingKBArticle.isLatest(), newKBArticle.isLatest());
		Assert.assertEquals(existingKBArticle.isMain(), newKBArticle.isMain());
		Assert.assertEquals(
			existingKBArticle.getSourceURL(), newKBArticle.getSourceURL());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBArticle.getDisplayDate()),
			Time.getShortTimestamp(newKBArticle.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBArticle.getExpirationDate()),
			Time.getShortTimestamp(newKBArticle.getExpirationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBArticle.getReviewDate()),
			Time.getShortTimestamp(newKBArticle.getReviewDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBArticle.getLastPublishDate()),
			Time.getShortTimestamp(newKBArticle.getLastPublishDate()));
		Assert.assertEquals(
			existingKBArticle.getStatus(), newKBArticle.getStatus());
		Assert.assertEquals(
			existingKBArticle.getStatusByUserId(),
			newKBArticle.getStatusByUserId());
		Assert.assertEquals(
			existingKBArticle.getStatusByUserName(),
			newKBArticle.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKBArticle.getStatusDate()),
			Time.getShortTimestamp(newKBArticle.getStatusDate()));
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
	public void testCountByR_G() throws Exception {
		_persistence.countByR_G(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByR_G(0L, 0L);
	}

	@Test
	public void testCountByR_V() throws Exception {
		_persistence.countByR_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByR_V(0L, 0);
	}

	@Test
	public void testCountByR_L() throws Exception {
		_persistence.countByR_L(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByR_L(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByR_LArrayable() throws Exception {
		_persistence.countByR_L(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByR_M() throws Exception {
		_persistence.countByR_M(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByR_M(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByR_MArrayable() throws Exception {
		_persistence.countByR_M(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByR_S() throws Exception {
		_persistence.countByR_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByR_S(0L, 0);
	}

	@Test
	public void testCountByR_SArrayable() throws Exception {
		_persistence.countByR_S(
			new long[] {RandomTestUtil.nextLong(), 0L},
			new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByG_ERC() throws Exception {
		_persistence.countByG_ERC(RandomTestUtil.nextLong(), "");

		_persistence.countByG_ERC(0L, "null");

		_persistence.countByG_ERC(0L, (String)null);
	}

	@Test
	public void testCountByG_L() throws Exception {
		_persistence.countByG_L(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_L(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_M() throws Exception {
		_persistence.countByG_M(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_M(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_S() throws Exception {
		_persistence.countByG_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_S(0L, 0);
	}

	@Test
	public void testCountByC_L() throws Exception {
		_persistence.countByC_L(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_L(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_M() throws Exception {
		_persistence.countByC_M(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_M(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_S(0L, 0);
	}

	@Test
	public void testCountByP_L() throws Exception {
		_persistence.countByP_L(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByP_L(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByP_LArrayable() throws Exception {
		_persistence.countByP_L(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByP_M() throws Exception {
		_persistence.countByP_M(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByP_M(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByP_MArrayable() throws Exception {
		_persistence.countByP_M(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByP_S() throws Exception {
		_persistence.countByP_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByP_S(0L, 0);
	}

	@Test
	public void testCountByP_SArrayable() throws Exception {
		_persistence.countByP_S(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByLtD_S() throws Exception {
		_persistence.countByLtD_S(
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByLtD_S(RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByR_G_V() throws Exception {
		_persistence.countByR_G_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByR_G_V(0L, 0L, 0);
	}

	@Test
	public void testCountByR_G_L() throws Exception {
		_persistence.countByR_G_L(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByR_G_L(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByR_G_LArrayable() throws Exception {
		_persistence.countByR_G_L(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByR_G_M() throws Exception {
		_persistence.countByR_G_M(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByR_G_M(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByR_G_MArrayable() throws Exception {
		_persistence.countByR_G_M(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByR_G_S() throws Exception {
		_persistence.countByR_G_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByR_G_S(0L, 0L, 0);
	}

	@Test
	public void testCountByR_G_SArrayable() throws Exception {
		_persistence.countByR_G_S(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByR_G_NotS() throws Exception {
		_persistence.countByR_G_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByR_G_NotS(0L, 0L, 0);
	}

	@Test
	public void testCountByR_L_NotS() throws Exception {
		_persistence.countByR_L_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByR_L_NotS(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByR_L_NotSArrayable() throws Exception {
		_persistence.countByR_L_NotS(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByR_M_NotS() throws Exception {
		_persistence.countByR_M_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByR_M_NotS(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByR_M_NotSArrayable() throws Exception {
		_persistence.countByR_M_NotS(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_ERC_V() throws Exception {
		_persistence.countByG_ERC_V(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_ERC_V(0L, "null", 0);

		_persistence.countByG_ERC_V(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_ERC_S() throws Exception {
		_persistence.countByG_ERC_S(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_ERC_S(0L, "null", 0);

		_persistence.countByG_ERC_S(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_P_L() throws Exception {
		_persistence.countByG_P_L(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByG_P_L(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_P_LArrayable() throws Exception {
		_persistence.countByG_P_L(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_P_M() throws Exception {
		_persistence.countByG_P_M(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByG_P_M(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_P_MArrayable() throws Exception {
		_persistence.countByG_P_M(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_P_S() throws Exception {
		_persistence.countByG_P_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_P_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_P_SArrayable() throws Exception {
		_persistence.countByG_P_S(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_KBFI_UT() throws Exception {
		_persistence.countByG_KBFI_UT(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_KBFI_UT(0L, 0L, "null");

		_persistence.countByG_KBFI_UT(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_KBFI_L() throws Exception {
		_persistence.countByG_KBFI_L(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByG_KBFI_L(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_KBFI_S() throws Exception {
		_persistence.countByG_KBFI_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_KBFI_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_LikeS_L() throws Exception {
		_persistence.countByG_LikeS_L(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByG_LikeS_L(
			0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByG_LikeS_L(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_LikeS_LArrayable() throws Exception {
		_persistence.countByG_LikeS_L(
			RandomTestUtil.nextLong(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_LikeS_M() throws Exception {
		_persistence.countByG_LikeS_M(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByG_LikeS_M(
			0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByG_LikeS_M(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_LikeS_MArrayable() throws Exception {
		_persistence.countByG_LikeS_M(
			RandomTestUtil.nextLong(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_LikeS_S() throws Exception {
		_persistence.countByG_LikeS_S(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_LikeS_S(0L, "null", 0);

		_persistence.countByG_LikeS_S(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_LikeS_SArrayable() throws Exception {
		_persistence.countByG_LikeS_S(
			RandomTestUtil.nextLong(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_L_NotS() throws Exception {
		_persistence.countByG_L_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByG_L_NotS(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByG_M_NotS() throws Exception {
		_persistence.countByG_M_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByG_M_NotS(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByC_L_NotS() throws Exception {
		_persistence.countByC_L_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByC_L_NotS(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByC_M_NotS() throws Exception {
		_persistence.countByC_M_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByC_M_NotS(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByP_L_NotS() throws Exception {
		_persistence.countByP_L_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByP_L_NotS(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByP_L_NotSArrayable() throws Exception {
		_persistence.countByP_L_NotS(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByP_M_NotS() throws Exception {
		_persistence.countByP_M_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByP_M_NotS(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByP_M_NotSArrayable() throws Exception {
		_persistence.countByP_M_NotS(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByR_G_L_NotS() throws Exception {
		_persistence.countByR_G_L_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());

		_persistence.countByR_G_L_NotS(
			0L, 0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByR_G_L_NotSArrayable() throws Exception {
		_persistence.countByR_G_L_NotS(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByR_G_M_NotS() throws Exception {
		_persistence.countByR_G_M_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());

		_persistence.countByR_G_M_NotS(
			0L, 0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByR_G_M_NotSArrayable() throws Exception {
		_persistence.countByR_G_M_NotS(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_P_L_S() throws Exception {
		_persistence.countByG_P_L_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());

		_persistence.countByG_P_L_S(0L, 0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByG_P_L_SArrayable() throws Exception {
		_persistence.countByG_P_L_S(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_P_L_NotS() throws Exception {
		_persistence.countByG_P_L_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());

		_persistence.countByG_P_L_NotS(
			0L, 0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByG_P_L_NotSArrayable() throws Exception {
		_persistence.countByG_P_L_NotS(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_P_M_S() throws Exception {
		_persistence.countByG_P_M_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());

		_persistence.countByG_P_M_S(0L, 0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByG_P_M_SArrayable() throws Exception {
		_persistence.countByG_P_M_S(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_P_M_NotS() throws Exception {
		_persistence.countByG_P_M_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());

		_persistence.countByG_P_M_NotS(
			0L, 0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByG_P_M_NotSArrayable() throws Exception {
		_persistence.countByG_P_M_NotS(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_KBFI_UT_S() throws Exception {
		_persistence.countByG_KBFI_UT_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt());

		_persistence.countByG_KBFI_UT_S(0L, 0L, "null", 0);

		_persistence.countByG_KBFI_UT_S(0L, 0L, (String)null, 0);
	}

	@Test
	public void testCountByG_KBFI_UT_SArrayable() throws Exception {
		_persistence.countByG_KBFI_UT_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomString(),
			new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByG_KBFI_UT_NotS() throws Exception {
		_persistence.countByG_KBFI_UT_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt());

		_persistence.countByG_KBFI_UT_NotS(0L, 0L, "null", 0);

		_persistence.countByG_KBFI_UT_NotS(0L, 0L, (String)null, 0);
	}

	@Test
	public void testCountByG_KBFI_L_NotS() throws Exception {
		_persistence.countByG_KBFI_L_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());

		_persistence.countByG_KBFI_L_NotS(
			0L, 0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByG_LikeS_L_NotS() throws Exception {
		_persistence.countByG_LikeS_L_NotS(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByG_LikeS_L_NotS(
			0L, "null", RandomTestUtil.randomBoolean(), 0);

		_persistence.countByG_LikeS_L_NotS(
			0L, (String)null, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByG_LikeS_L_NotSArrayable() throws Exception {
		_persistence.countByG_LikeS_L_NotS(
			RandomTestUtil.nextLong(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			},
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_LikeS_M_NotS() throws Exception {
		_persistence.countByG_LikeS_M_NotS(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByG_LikeS_M_NotS(
			0L, "null", RandomTestUtil.randomBoolean(), 0);

		_persistence.countByG_LikeS_M_NotS(
			0L, (String)null, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByG_LikeS_M_NotSArrayable() throws Exception {
		_persistence.countByG_LikeS_M_NotS(
			RandomTestUtil.nextLong(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			},
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextInt());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KBArticle newKBArticle = addKBArticle();

		KBArticle existingKBArticle = _persistence.findByPrimaryKey(
			newKBArticle.getPrimaryKey());

		Assert.assertEquals(existingKBArticle, newKBArticle);
	}

	@Test(expected = NoSuchArticleException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KBArticle> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KBArticle", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "kbArticleId", true, "resourcePrimKey", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "externalReferenceCode", true,
			"rootResourcePrimKey", true, "parentResourceClassNameId", true,
			"parentResourcePrimKey", true, "kbFolderId", true, "version", true,
			"title", true, "urlTitle", true, "description", true, "priority",
			true, "sections", true, "latest", true, "main", true, "sourceURL",
			true, "displayDate", true, "expirationDate", true, "reviewDate",
			true, "lastPublishDate", true, "status", true, "statusByUserId",
			true, "statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KBArticle newKBArticle = addKBArticle();

		KBArticle existingKBArticle = _persistence.fetchByPrimaryKey(
			newKBArticle.getPrimaryKey());

		Assert.assertEquals(existingKBArticle, newKBArticle);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KBArticle missingKBArticle = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKBArticle);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KBArticle newKBArticle1 = addKBArticle();
		KBArticle newKBArticle2 = addKBArticle();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKBArticle1.getPrimaryKey());
		primaryKeys.add(newKBArticle2.getPrimaryKey());

		Map<Serializable, KBArticle> kbArticles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kbArticles.size());
		Assert.assertEquals(
			newKBArticle1, kbArticles.get(newKBArticle1.getPrimaryKey()));
		Assert.assertEquals(
			newKBArticle2, kbArticles.get(newKBArticle2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KBArticle> kbArticles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kbArticles.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KBArticle newKBArticle = addKBArticle();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKBArticle.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KBArticle> kbArticles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kbArticles.size());
		Assert.assertEquals(
			newKBArticle, kbArticles.get(newKBArticle.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KBArticle> kbArticles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kbArticles.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KBArticle newKBArticle = addKBArticle();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKBArticle.getPrimaryKey());

		Map<Serializable, KBArticle> kbArticles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kbArticles.size());
		Assert.assertEquals(
			newKBArticle, kbArticles.get(newKBArticle.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			KBArticleLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<KBArticle>() {

				@Override
				public void performAction(KBArticle kbArticle) {
					Assert.assertNotNull(kbArticle);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		KBArticle newKBArticle = addKBArticle();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KBArticle.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"kbArticleId", newKBArticle.getKbArticleId()));

		List<KBArticle> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		KBArticle existingKBArticle = result.get(0);

		Assert.assertEquals(existingKBArticle, newKBArticle);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KBArticle.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"kbArticleId", RandomTestUtil.nextLong()));

		List<KBArticle> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		KBArticle newKBArticle = addKBArticle();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KBArticle.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("kbArticleId"));

		Object newKbArticleId = newKBArticle.getKbArticleId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"kbArticleId", new Object[] {newKbArticleId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingKbArticleId = result.get(0);

		Assert.assertEquals(existingKbArticleId, newKbArticleId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KBArticle.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("kbArticleId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"kbArticleId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		KBArticle newKBArticle = addKBArticle();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newKBArticle.getPrimaryKey()));
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

		KBArticle newKBArticle = addKBArticle();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KBArticle.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"kbArticleId", newKBArticle.getKbArticleId()));

		List<KBArticle> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(KBArticle kbArticle) {
		Assert.assertEquals(
			kbArticle.getUuid(),
			ReflectionTestUtil.invoke(
				kbArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(kbArticle.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				kbArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(kbArticle.getResourcePrimKey()),
			ReflectionTestUtil.<Long>invoke(
				kbArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "resourcePrimKey"));
		Assert.assertEquals(
			Integer.valueOf(kbArticle.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				kbArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));

		Assert.assertEquals(
			Long.valueOf(kbArticle.getResourcePrimKey()),
			ReflectionTestUtil.<Long>invoke(
				kbArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "resourcePrimKey"));
		Assert.assertEquals(
			Long.valueOf(kbArticle.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				kbArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Integer.valueOf(kbArticle.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				kbArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));

		Assert.assertEquals(
			Long.valueOf(kbArticle.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				kbArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			kbArticle.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				kbArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Integer.valueOf(kbArticle.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				kbArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected KBArticle addKBArticle() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KBArticle kbArticle = _persistence.create(pk);

		kbArticle.setMvccVersion(RandomTestUtil.nextLong());

		kbArticle.setCtCollectionId(RandomTestUtil.nextLong());

		kbArticle.setUuid(RandomTestUtil.randomString());

		kbArticle.setResourcePrimKey(RandomTestUtil.nextLong());

		kbArticle.setGroupId(RandomTestUtil.nextLong());

		kbArticle.setCompanyId(RandomTestUtil.nextLong());

		kbArticle.setUserId(RandomTestUtil.nextLong());

		kbArticle.setUserName(RandomTestUtil.randomString());

		kbArticle.setCreateDate(RandomTestUtil.nextDate());

		kbArticle.setModifiedDate(RandomTestUtil.nextDate());

		kbArticle.setExternalReferenceCode(RandomTestUtil.randomString());

		kbArticle.setRootResourcePrimKey(RandomTestUtil.nextLong());

		kbArticle.setParentResourceClassNameId(RandomTestUtil.nextLong());

		kbArticle.setParentResourcePrimKey(RandomTestUtil.nextLong());

		kbArticle.setKbFolderId(RandomTestUtil.nextLong());

		kbArticle.setVersion(RandomTestUtil.nextInt());

		kbArticle.setTitle(RandomTestUtil.randomString());

		kbArticle.setUrlTitle(RandomTestUtil.randomString());

		kbArticle.setContent(RandomTestUtil.randomString());

		kbArticle.setDescription(RandomTestUtil.randomString());

		kbArticle.setPriority(RandomTestUtil.nextDouble());

		kbArticle.setSections(RandomTestUtil.randomString());

		kbArticle.setLatest(RandomTestUtil.randomBoolean());

		kbArticle.setMain(RandomTestUtil.randomBoolean());

		kbArticle.setSourceURL(RandomTestUtil.randomString());

		kbArticle.setDisplayDate(RandomTestUtil.nextDate());

		kbArticle.setExpirationDate(RandomTestUtil.nextDate());

		kbArticle.setReviewDate(RandomTestUtil.nextDate());

		kbArticle.setLastPublishDate(RandomTestUtil.nextDate());

		kbArticle.setStatus(RandomTestUtil.nextInt());

		kbArticle.setStatusByUserId(RandomTestUtil.nextLong());

		kbArticle.setStatusByUserName(RandomTestUtil.randomString());

		kbArticle.setStatusDate(RandomTestUtil.nextDate());

		_kbArticles.add(_persistence.update(kbArticle));

		return kbArticle;
	}

	private List<KBArticle> _kbArticles = new ArrayList<KBArticle>();
	private KBArticlePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}