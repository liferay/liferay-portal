/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.persistence.JournalArticlePersistence;
import com.liferay.journal.service.persistence.JournalArticleUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class JournalArticlePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.journal.service"));

	@Before
	public void setUp() {
		_persistence = JournalArticleUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<JournalArticle> iterator = _journalArticles.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalArticle journalArticle = _persistence.create(pk);

		Assert.assertNotNull(journalArticle);

		Assert.assertEquals(journalArticle.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		JournalArticle newJournalArticle = addJournalArticle();

		_persistence.remove(newJournalArticle);

		JournalArticle existingJournalArticle = _persistence.fetchByPrimaryKey(
			newJournalArticle.getPrimaryKey());

		Assert.assertNull(existingJournalArticle);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addJournalArticle();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalArticle newJournalArticle = _persistence.create(pk);

		newJournalArticle.setMvccVersion(RandomTestUtil.nextLong());

		newJournalArticle.setCtCollectionId(RandomTestUtil.nextLong());

		newJournalArticle.setUuid(RandomTestUtil.randomString());

		newJournalArticle.setResourcePrimKey(RandomTestUtil.nextLong());

		newJournalArticle.setGroupId(RandomTestUtil.nextLong());

		newJournalArticle.setCompanyId(RandomTestUtil.nextLong());

		newJournalArticle.setUserId(RandomTestUtil.nextLong());

		newJournalArticle.setUserName(RandomTestUtil.randomString());

		newJournalArticle.setCreateDate(RandomTestUtil.nextDate());

		newJournalArticle.setModifiedDate(RandomTestUtil.nextDate());

		newJournalArticle.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newJournalArticle.setFolderId(RandomTestUtil.nextLong());

		newJournalArticle.setClassNameId(RandomTestUtil.nextLong());

		newJournalArticle.setClassPK(RandomTestUtil.nextLong());

		newJournalArticle.setTreePath(RandomTestUtil.randomString());

		newJournalArticle.setArticleId(RandomTestUtil.randomString());

		newJournalArticle.setVersion(RandomTestUtil.nextDouble());

		newJournalArticle.setUrlTitle(RandomTestUtil.randomString());

		newJournalArticle.setDDMStructureId(RandomTestUtil.nextLong());

		newJournalArticle.setDDMTemplateKey(RandomTestUtil.randomString());

		newJournalArticle.setDefaultLanguageId(RandomTestUtil.randomString());

		newJournalArticle.setLayoutUuid(RandomTestUtil.randomString());

		newJournalArticle.setDisplayDate(RandomTestUtil.nextDate());

		newJournalArticle.setExpirationDate(RandomTestUtil.nextDate());

		newJournalArticle.setReviewDate(RandomTestUtil.nextDate());

		newJournalArticle.setIndexable(RandomTestUtil.randomBoolean());

		newJournalArticle.setSmallImage(RandomTestUtil.randomBoolean());

		newJournalArticle.setSmallImageId(RandomTestUtil.nextLong());

		newJournalArticle.setSmallImageSource(RandomTestUtil.nextInt());

		newJournalArticle.setSmallImageURL(RandomTestUtil.randomString());

		newJournalArticle.setLastPublishDate(RandomTestUtil.nextDate());

		newJournalArticle.setStatus(RandomTestUtil.nextInt());

		newJournalArticle.setStatusByUserId(RandomTestUtil.nextLong());

		newJournalArticle.setStatusByUserName(RandomTestUtil.randomString());

		newJournalArticle.setStatusDate(RandomTestUtil.nextDate());

		_journalArticles.add(_persistence.update(newJournalArticle));

		JournalArticle existingJournalArticle = _persistence.findByPrimaryKey(
			newJournalArticle.getPrimaryKey());

		Assert.assertEquals(
			existingJournalArticle.getMvccVersion(),
			newJournalArticle.getMvccVersion());
		Assert.assertEquals(
			existingJournalArticle.getCtCollectionId(),
			newJournalArticle.getCtCollectionId());
		Assert.assertEquals(
			existingJournalArticle.getUuid(), newJournalArticle.getUuid());
		Assert.assertEquals(
			existingJournalArticle.getId(), newJournalArticle.getId());
		Assert.assertEquals(
			existingJournalArticle.getResourcePrimKey(),
			newJournalArticle.getResourcePrimKey());
		Assert.assertEquals(
			existingJournalArticle.getGroupId(),
			newJournalArticle.getGroupId());
		Assert.assertEquals(
			existingJournalArticle.getCompanyId(),
			newJournalArticle.getCompanyId());
		Assert.assertEquals(
			existingJournalArticle.getUserId(), newJournalArticle.getUserId());
		Assert.assertEquals(
			existingJournalArticle.getUserName(),
			newJournalArticle.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalArticle.getCreateDate()),
			Time.getShortTimestamp(newJournalArticle.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalArticle.getModifiedDate()),
			Time.getShortTimestamp(newJournalArticle.getModifiedDate()));
		Assert.assertEquals(
			existingJournalArticle.getExternalReferenceCode(),
			newJournalArticle.getExternalReferenceCode());
		Assert.assertEquals(
			existingJournalArticle.getFolderId(),
			newJournalArticle.getFolderId());
		Assert.assertEquals(
			existingJournalArticle.getClassNameId(),
			newJournalArticle.getClassNameId());
		Assert.assertEquals(
			existingJournalArticle.getClassPK(),
			newJournalArticle.getClassPK());
		Assert.assertEquals(
			existingJournalArticle.getTreePath(),
			newJournalArticle.getTreePath());
		Assert.assertEquals(
			existingJournalArticle.getArticleId(),
			newJournalArticle.getArticleId());
		AssertUtils.assertEquals(
			existingJournalArticle.getVersion(),
			newJournalArticle.getVersion());
		Assert.assertEquals(
			existingJournalArticle.getUrlTitle(),
			newJournalArticle.getUrlTitle());
		Assert.assertEquals(
			existingJournalArticle.getDDMStructureId(),
			newJournalArticle.getDDMStructureId());
		Assert.assertEquals(
			existingJournalArticle.getDDMTemplateKey(),
			newJournalArticle.getDDMTemplateKey());
		Assert.assertEquals(
			existingJournalArticle.getDefaultLanguageId(),
			newJournalArticle.getDefaultLanguageId());
		Assert.assertEquals(
			existingJournalArticle.getLayoutUuid(),
			newJournalArticle.getLayoutUuid());
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalArticle.getDisplayDate()),
			Time.getShortTimestamp(newJournalArticle.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalArticle.getExpirationDate()),
			Time.getShortTimestamp(newJournalArticle.getExpirationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalArticle.getReviewDate()),
			Time.getShortTimestamp(newJournalArticle.getReviewDate()));
		Assert.assertEquals(
			existingJournalArticle.isIndexable(),
			newJournalArticle.isIndexable());
		Assert.assertEquals(
			existingJournalArticle.isSmallImage(),
			newJournalArticle.isSmallImage());
		Assert.assertEquals(
			existingJournalArticle.getSmallImageId(),
			newJournalArticle.getSmallImageId());
		Assert.assertEquals(
			existingJournalArticle.getSmallImageSource(),
			newJournalArticle.getSmallImageSource());
		Assert.assertEquals(
			existingJournalArticle.getSmallImageURL(),
			newJournalArticle.getSmallImageURL());
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalArticle.getLastPublishDate()),
			Time.getShortTimestamp(newJournalArticle.getLastPublishDate()));
		Assert.assertEquals(
			existingJournalArticle.getStatus(), newJournalArticle.getStatus());
		Assert.assertEquals(
			existingJournalArticle.getStatusByUserId(),
			newJournalArticle.getStatusByUserId());
		Assert.assertEquals(
			existingJournalArticle.getStatusByUserName(),
			newJournalArticle.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingJournalArticle.getStatusDate()),
			Time.getShortTimestamp(newJournalArticle.getStatusDate()));
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
	public void testCountByDDMStructureId() throws Exception {
		_persistence.countByDDMStructureId(RandomTestUtil.nextLong());

		_persistence.countByDDMStructureId(0L);
	}

	@Test
	public void testCountByDDMTemplateKey() throws Exception {
		_persistence.countByDDMTemplateKey("");

		_persistence.countByDDMTemplateKey("null");

		_persistence.countByDDMTemplateKey((String)null);
	}

	@Test
	public void testCountByLayoutUuid() throws Exception {
		_persistence.countByLayoutUuid("");

		_persistence.countByLayoutUuid("null");

		_persistence.countByLayoutUuid((String)null);
	}

	@Test
	public void testCountBySmallImageId() throws Exception {
		_persistence.countBySmallImageId(RandomTestUtil.nextLong());

		_persistence.countBySmallImageId(0L);
	}

	@Test
	public void testCountByR_I() throws Exception {
		_persistence.countByR_I(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByR_I(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByR_ST() throws Exception {
		_persistence.countByR_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByR_ST(0L, 0);
	}

	@Test
	public void testCountByR_STArrayable() throws Exception {
		_persistence.countByR_ST(
			RandomTestUtil.nextLong(), new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByG_U() throws Exception {
		_persistence.countByG_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_U(0L, 0L);
	}

	@Test
	public void testCountByG_ERC() throws Exception {
		_persistence.countByG_ERC(RandomTestUtil.nextLong(), "");

		_persistence.countByG_ERC(0L, "null");

		_persistence.countByG_ERC(0L, (String)null);
	}

	@Test
	public void testCountByG_F() throws Exception {
		_persistence.countByG_F(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_F(0L, 0L);
	}

	@Test
	public void testCountByG_FArrayable() throws Exception {
		_persistence.countByG_F(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByG_A() throws Exception {
		_persistence.countByG_A(RandomTestUtil.nextLong(), "");

		_persistence.countByG_A(0L, "null");

		_persistence.countByG_A(0L, (String)null);
	}

	@Test
	public void testCountByG_UT() throws Exception {
		_persistence.countByG_UT(RandomTestUtil.nextLong(), "");

		_persistence.countByG_UT(0L, "null");

		_persistence.countByG_UT(0L, (String)null);
	}

	@Test
	public void testCountByG_DDMSI() throws Exception {
		_persistence.countByG_DDMSI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_DDMSI(0L, 0L);
	}

	@Test
	public void testCountByG_DDMTK() throws Exception {
		_persistence.countByG_DDMTK(RandomTestUtil.nextLong(), "");

		_persistence.countByG_DDMTK(0L, "null");

		_persistence.countByG_DDMTK(0L, (String)null);
	}

	@Test
	public void testCountByG_L() throws Exception {
		_persistence.countByG_L(RandomTestUtil.nextLong(), "");

		_persistence.countByG_L(0L, "null");

		_persistence.countByG_L(0L, (String)null);
	}

	@Test
	public void testCountByG_NotL() throws Exception {
		_persistence.countByG_NotL(RandomTestUtil.nextLong(), "");

		_persistence.countByG_NotL(0L, "null");

		_persistence.countByG_NotL(0L, (String)null);
	}

	@Test
	public void testCountByG_NotLArrayable() throws Exception {
		_persistence.countByG_NotL(
			RandomTestUtil.nextLong(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			});
	}

	@Test
	public void testCountByG_ST() throws Exception {
		_persistence.countByG_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_ST(0L, 0);
	}

	@Test
	public void testCountByC_V() throws Exception {
		_persistence.countByC_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDouble());

		_persistence.countByC_V(0L, 0D);
	}

	@Test
	public void testCountByC_ST() throws Exception {
		_persistence.countByC_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_ST(0L, 0);
	}

	@Test
	public void testCountByC_NotST() throws Exception {
		_persistence.countByC_NotST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_NotST(0L, 0);
	}

	@Test
	public void testCountByLtD_S() throws Exception {
		_persistence.countByLtD_S(
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByLtD_S(RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByR_I_S() throws Exception {
		_persistence.countByR_I_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByR_I_S(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByR_I_SArrayable() throws Exception {
		_persistence.countByR_I_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByG_U_C() throws Exception {
		_persistence.countByG_U_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_U_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_ERC_V() throws Exception {
		_persistence.countByG_ERC_V(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextDouble());

		_persistence.countByG_ERC_V(0L, "null", 0D);

		_persistence.countByG_ERC_V(0L, (String)null, 0D);
	}

	@Test
	public void testCountByG_ERC_ST() throws Exception {
		_persistence.countByG_ERC_ST(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_ERC_ST(0L, "null", 0);

		_persistence.countByG_ERC_ST(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_ERC_STArrayable() throws Exception {
		_persistence.countByG_ERC_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
			new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByG_F_ST() throws Exception {
		_persistence.countByG_F_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_F_ST(0L, 0L, 0);
	}

	@Test
	public void testCountByG_F_STArrayable() throws Exception {
		_persistence.countByG_F_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByG_C_C() throws Exception {
		_persistence.countByG_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_C_DDMSI() throws Exception {
		_persistence.countByG_C_DDMSI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_DDMSI(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_C_DDMTK() throws Exception {
		_persistence.countByG_C_DDMTK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_C_DDMTK(0L, 0L, "null");

		_persistence.countByG_C_DDMTK(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_C_L() throws Exception {
		_persistence.countByG_C_L(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_C_L(0L, 0L, "null");

		_persistence.countByG_C_L(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_A_V() throws Exception {
		_persistence.countByG_A_V(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextDouble());

		_persistence.countByG_A_V(0L, "null", 0D);

		_persistence.countByG_A_V(0L, (String)null, 0D);
	}

	@Test
	public void testCountByG_A_ST() throws Exception {
		_persistence.countByG_A_ST(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_A_ST(0L, "null", 0);

		_persistence.countByG_A_ST(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_A_STArrayable() throws Exception {
		_persistence.countByG_A_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
			new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByG_A_NotST() throws Exception {
		_persistence.countByG_A_NotST(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_A_NotST(0L, "null", 0);

		_persistence.countByG_A_NotST(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_UT_ST() throws Exception {
		_persistence.countByG_UT_ST(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_UT_ST(0L, "null", 0);

		_persistence.countByG_UT_ST(0L, (String)null, 0);
	}

	@Test
	public void testCountByC_V_ST() throws Exception {
		_persistence.countByC_V_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDouble(),
			RandomTestUtil.nextInt());

		_persistence.countByC_V_ST(0L, 0D, 0);
	}

	@Test
	public void testCountByG_F_C_NotST() throws Exception {
		_persistence.countByG_F_C_NotST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_F_C_NotST(0L, 0L, 0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		JournalArticle newJournalArticle = addJournalArticle();

		JournalArticle existingJournalArticle = _persistence.findByPrimaryKey(
			newJournalArticle.getPrimaryKey());

		Assert.assertEquals(existingJournalArticle, newJournalArticle);
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

	protected OrderByComparator<JournalArticle> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"JournalArticle", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "id", true, "resourcePrimKey", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "externalReferenceCode", true,
			"folderId", true, "classNameId", true, "classPK", true, "treePath",
			true, "articleId", true, "version", true, "urlTitle", true,
			"DDMStructureId", true, "DDMTemplateKey", true, "defaultLanguageId",
			true, "layoutUuid", true, "displayDate", true, "expirationDate",
			true, "reviewDate", true, "indexable", true, "smallImage", true,
			"smallImageId", true, "smallImageSource", true, "smallImageURL",
			true, "lastPublishDate", true, "status", true, "statusByUserId",
			true, "statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		JournalArticle newJournalArticle = addJournalArticle();

		JournalArticle existingJournalArticle = _persistence.fetchByPrimaryKey(
			newJournalArticle.getPrimaryKey());

		Assert.assertEquals(existingJournalArticle, newJournalArticle);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalArticle missingJournalArticle = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingJournalArticle);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		JournalArticle newJournalArticle1 = addJournalArticle();
		JournalArticle newJournalArticle2 = addJournalArticle();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalArticle1.getPrimaryKey());
		primaryKeys.add(newJournalArticle2.getPrimaryKey());

		Map<Serializable, JournalArticle> journalArticles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, journalArticles.size());
		Assert.assertEquals(
			newJournalArticle1,
			journalArticles.get(newJournalArticle1.getPrimaryKey()));
		Assert.assertEquals(
			newJournalArticle2,
			journalArticles.get(newJournalArticle2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, JournalArticle> journalArticles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(journalArticles.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		JournalArticle newJournalArticle = addJournalArticle();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalArticle.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, JournalArticle> journalArticles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, journalArticles.size());
		Assert.assertEquals(
			newJournalArticle,
			journalArticles.get(newJournalArticle.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, JournalArticle> journalArticles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(journalArticles.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		JournalArticle newJournalArticle = addJournalArticle();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalArticle.getPrimaryKey());

		Map<Serializable, JournalArticle> journalArticles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, journalArticles.size());
		Assert.assertEquals(
			newJournalArticle,
			journalArticles.get(newJournalArticle.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		JournalArticle newJournalArticle = addJournalArticle();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newJournalArticle.getPrimaryKey()));
	}

	private void _assertOriginalValues(JournalArticle journalArticle) {
		Assert.assertEquals(
			journalArticle.getUuid(),
			ReflectionTestUtil.invoke(
				journalArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(journalArticle.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				journalArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(journalArticle.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				journalArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			journalArticle.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				journalArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		AssertUtils.assertEquals(
			journalArticle.getVersion(),
			ReflectionTestUtil.<Double>invoke(
				journalArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));

		Assert.assertEquals(
			Long.valueOf(journalArticle.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				journalArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(journalArticle.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				journalArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(journalArticle.getDDMStructureId()),
			ReflectionTestUtil.<Long>invoke(
				journalArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "DDMStructureId"));

		Assert.assertEquals(
			Long.valueOf(journalArticle.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				journalArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			journalArticle.getArticleId(),
			ReflectionTestUtil.invoke(
				journalArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "articleId"));
		AssertUtils.assertEquals(
			journalArticle.getVersion(),
			ReflectionTestUtil.<Double>invoke(
				journalArticle, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected JournalArticle addJournalArticle() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalArticle journalArticle = _persistence.create(pk);

		journalArticle.setMvccVersion(RandomTestUtil.nextLong());

		journalArticle.setCtCollectionId(RandomTestUtil.nextLong());

		journalArticle.setUuid(RandomTestUtil.randomString());

		journalArticle.setResourcePrimKey(RandomTestUtil.nextLong());

		journalArticle.setGroupId(RandomTestUtil.nextLong());

		journalArticle.setCompanyId(RandomTestUtil.nextLong());

		journalArticle.setUserId(RandomTestUtil.nextLong());

		journalArticle.setUserName(RandomTestUtil.randomString());

		journalArticle.setCreateDate(RandomTestUtil.nextDate());

		journalArticle.setModifiedDate(RandomTestUtil.nextDate());

		journalArticle.setExternalReferenceCode(RandomTestUtil.randomString());

		journalArticle.setFolderId(RandomTestUtil.nextLong());

		journalArticle.setClassNameId(RandomTestUtil.nextLong());

		journalArticle.setClassPK(RandomTestUtil.nextLong());

		journalArticle.setTreePath(RandomTestUtil.randomString());

		journalArticle.setArticleId(RandomTestUtil.randomString());

		journalArticle.setVersion(RandomTestUtil.nextDouble());

		journalArticle.setUrlTitle(RandomTestUtil.randomString());

		journalArticle.setDDMStructureId(RandomTestUtil.nextLong());

		journalArticle.setDDMTemplateKey(RandomTestUtil.randomString());

		journalArticle.setDefaultLanguageId(RandomTestUtil.randomString());

		journalArticle.setLayoutUuid(RandomTestUtil.randomString());

		journalArticle.setDisplayDate(RandomTestUtil.nextDate());

		journalArticle.setExpirationDate(RandomTestUtil.nextDate());

		journalArticle.setReviewDate(RandomTestUtil.nextDate());

		journalArticle.setIndexable(RandomTestUtil.randomBoolean());

		journalArticle.setSmallImage(RandomTestUtil.randomBoolean());

		journalArticle.setSmallImageId(RandomTestUtil.nextLong());

		journalArticle.setSmallImageSource(RandomTestUtil.nextInt());

		journalArticle.setSmallImageURL(RandomTestUtil.randomString());

		journalArticle.setLastPublishDate(RandomTestUtil.nextDate());

		journalArticle.setStatus(RandomTestUtil.nextInt());

		journalArticle.setStatusByUserId(RandomTestUtil.nextLong());

		journalArticle.setStatusByUserName(RandomTestUtil.randomString());

		journalArticle.setStatusDate(RandomTestUtil.nextDate());

		_journalArticles.add(_persistence.update(journalArticle));

		return journalArticle;
	}

	private List<JournalArticle> _journalArticles =
		new ArrayList<JournalArticle>();
	private JournalArticlePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}