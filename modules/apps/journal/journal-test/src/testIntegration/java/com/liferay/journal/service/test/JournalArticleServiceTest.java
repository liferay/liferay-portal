/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.dynamic.data.mapping.exception.RequiredTemplateException;
import com.liferay.dynamic.data.mapping.exception.StorageFieldRequiredException;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLink;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.test.util.JournalFolderFixture;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.comparator.ArticleIDComparator;
import com.liferay.journal.util.comparator.ArticleTitleComparator;
import com.liferay.journal.util.comparator.ArticleVersionComparator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Juan Fernández
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class JournalArticleServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_article = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Version 1",
			"This is a test article.");

		PortalPreferences portalPreferences =
			_portletPreferencesFactory.getPortalPreferences(
				TestPropsValues.getUserId(), true);

		_originalPortalPreferencesXML = _portletPreferencesFactory.toXML(
			portalPreferences);

		portalPreferences.setValue(
			"", "expireAllArticleVersionsEnabled", "true");

		_portalPreferencesLocalService.updatePreferences(
			TestPropsValues.getCompanyId(),
			PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			_portletPreferencesFactory.toXML(portalPreferences));
	}

	@After
	public void tearDown() throws Exception {
		if (_article != null) {
			_journalArticleLocalService.deleteArticle(
				_group.getGroupId(), _article.getArticleId(),
				new ServiceContext());
		}

		_portalPreferencesLocalService.updatePreferences(
			TestPropsValues.getCompanyId(),
			PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			_originalPortalPreferencesXML);
	}

	@Test
	public void testAddArticle() {
		Assert.assertEquals(
			"Version 1", _article.getTitle(LocaleUtil.getDefault()));
		Assert.assertTrue(_article.isApproved());
		Assert.assertEquals(1.0, _article.getVersion(), 0);
	}

	@Test(expected = StorageFieldRequiredException.class)
	public void testAddArticleWithEmptyRequiredHTMLField() throws Exception {
		testAddArticleRequiredFields(
			"test-ddm-structure-html-required-field.xml",
			"test-journal-content-html-empty-required-field.xml",
			HashMapBuilder.put(
				"HTML2030", ""
			).build());
	}

	@Test
	public void testAddArticleWithExternalReferenceCode() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		_article = JournalTestUtil.addArticle(
			externalReferenceCode, _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, StringPool.BLANK,
			true);

		Assert.assertNotNull(_article);
		Assert.assertEquals(
			externalReferenceCode, _article.getExternalReferenceCode());

		_latestArticle =
			_journalArticleService.fetchLatestArticleByExternalReferenceCode(
				_group.getGroupId(), externalReferenceCode);

		Assert.assertNotNull(_latestArticle);
		Assert.assertEquals(
			externalReferenceCode, _latestArticle.getExternalReferenceCode());
	}

	@Test
	public void testAddArticleWithNotEmptyRequiredHTMLField() throws Exception {
		testAddArticleRequiredFields(
			"test-ddm-structure-html-required-field.xml",
			"test-journal-content-html-required-field.xml",
			HashMapBuilder.put(
				"HTML2030", "<p>Hello.</p>"
			).build());
	}

	@Test
	public void testAddArticleWithoutExternalReferenceCode() throws Exception {
		String articleId = StringUtil.toUpperCase(
			RandomTestUtil.randomString());

		_article = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, articleId, false);

		String externalReferenceCode = _article.getExternalReferenceCode();

		Assert.assertNotNull(_article);
		Assert.assertEquals(articleId, _article.getArticleId());
		Assert.assertEquals(externalReferenceCode, _article.getUuid());

		_latestArticle =
			_journalArticleService.fetchLatestArticleByExternalReferenceCode(
				_group.getGroupId(), externalReferenceCode);

		Assert.assertNotNull(_latestArticle);
		Assert.assertEquals(_article, _latestArticle);
	}

	@Test
	public void testCopyArticle() throws Exception {
		JournalArticle journalArticle = _journalArticleService.copyArticle(
			_article.getGroupId(), _article.getArticleId(),
			RandomTestUtil.randomString(), true, _article.getVersion());

		Assert.assertEquals(
			_article.getCompanyId(), journalArticle.getCompanyId());
		Assert.assertEquals(
			_article.getDDMStructureId(), journalArticle.getDDMStructureId());
		Assert.assertEquals(
			_article.getDDMTemplateKey(), journalArticle.getDDMTemplateKey());
		Assert.assertEquals(
			_article.getDefaultLanguageId(),
			journalArticle.getDefaultLanguageId());
		Assert.assertEquals(
			_article.getDisplayDate(), journalArticle.getDisplayDate());
		Assert.assertEquals(
			_article.getExpirationDate(), journalArticle.getExpirationDate());
		Assert.assertEquals(
			_article.getFolderId(), journalArticle.getFolderId());
		Assert.assertEquals(
			_article.getLayoutUuid(), journalArticle.getLayoutUuid());
		Assert.assertEquals(
			_article.getReviewDate(), journalArticle.getReviewDate());
		Assert.assertEquals(
			_article.getSmallImageSource(),
			journalArticle.getSmallImageSource());
		Assert.assertEquals(
			_article.getSmallImageURL(), journalArticle.getSmallImageURL());
		Assert.assertEquals(
			StringBundler.concat(
				_article.getTitle(), " (",
				_language.get(LocaleUtil.getSiteDefault(), "copy"), ")"),
			journalArticle.getTitle());
		Assert.assertEquals(
			_article.getTreePath(), journalArticle.getTreePath());
		Assert.assertEquals(_article.getGroupId(), journalArticle.getGroupId());
		Assert.assertEquals(
			_article.isIndexable(), journalArticle.isIndexable());
		Assert.assertEquals(
			_article.isSmallImage(), journalArticle.isSmallImage());
	}

	@Test
	public void testDeleteDDMTemplateSpecifiedByDeletedArticle()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(), "<title>Test Article</title>",
				ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey());

		DDMTemplateLink ddmTemplateLink =
			_ddmTemplateLinkLocalService.addTemplateLink(
				_classNameLocalService.getClassNameId(
					ResourceActionsUtil.getCompositeModelName(
						JournalArticle.class.getName(),
						DDMTemplate.class.getName())),
				journalArticle.getId(), ddmTemplate.getTemplateId());

		_journalArticleLocalService.deleteArticle(
			_group.getGroupId(), journalArticle.getArticleId(),
			new ServiceContext());

		_ddmTemplateLocalService.deleteTemplate(ddmTemplate.getTemplateId());

		Assert.assertNull(
			_ddmTemplateLocalService.fetchDDMTemplate(
				ddmTemplate.getTemplateId()));
		Assert.assertNull(
			_ddmTemplateLinkLocalService.fetchDDMTemplateLink(
				ddmTemplateLink.getTemplateLinkId()));
	}

	@Test
	public void testDeleteTemplateReferencedByJournalArticles()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), "<title>Test Article</title>",
			ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey());

		try {
			_ddmTemplateLocalService.deleteTemplate(
				ddmTemplate.getTemplateId());

			Assert.fail();
		}
		catch (RequiredTemplateException requiredTemplateException) {
		}
	}

	@Test
	public void testExpireArticle() throws Exception {
		updateAndExpireLatestArticle("Version 2");

		Assert.assertEquals(
			"Version 2", _article.getTitle(LocaleUtil.getDefault()));
		Assert.assertTrue(_article.isExpired());
		Assert.assertEquals(1.1, _article.getVersion(), 0);
	}

	@Test
	public void testFetchLatestArticleByApprovedStatusWhenArticleApproved()
		throws Exception {

		_article = JournalTestUtil.updateArticle(_article, "Version 2");

		_latestArticle = fetchLatestArticle(WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			"Version 2", _latestArticle.getTitle(LocaleUtil.getDefault()));
		Assert.assertTrue(_latestArticle.isApproved());
		Assert.assertTrue(_latestArticle.getVersion() == 1.1);
	}

	@Test
	public void testFetchLatestArticleByApprovedStatusWhenArticleExpired()
		throws Exception {

		updateAndExpireArticle();

		_latestArticle = fetchLatestArticle(WorkflowConstants.STATUS_APPROVED);

		Assert.assertNull(_latestArticle);
	}

	@Test
	public void testFetchLatestArticleByApprovedStatusWhenFirstArticleExpired()
		throws Exception {

		JournalTestUtil.updateArticle(_article, "Version 2");

		_article = JournalTestUtil.expireArticle(
			_group.getGroupId(), _article, 1.0);

		_latestArticle = fetchLatestArticle(WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			"Version 2", _latestArticle.getTitle(LocaleUtil.getDefault()));
		Assert.assertTrue(_latestArticle.isApproved());
		Assert.assertTrue(_latestArticle.getVersion() == 1.1);
	}

	@Test
	public void testFetchLatestArticleByDraftStatusWhenNoDraftArticle()
		throws Exception {

		_article = JournalTestUtil.updateArticle(_article, "Version 2");

		_latestArticle = fetchLatestArticle(WorkflowConstants.STATUS_DRAFT);

		Assert.assertNull(_latestArticle);
	}

	@Test
	public void testFetchLatestArticleByExternalReferenceCode()
		throws Exception {

		long groupId = _article.getGroupId();
		String externalReferenceCode = _article.getExternalReferenceCode();

		_article = JournalTestUtil.updateArticle(_article, "Version 2");

		_latestArticle =
			_journalArticleService.fetchLatestArticleByExternalReferenceCode(
				groupId, externalReferenceCode);

		Assert.assertEquals(
			"Version 2", _latestArticle.getTitle(LocaleUtil.getDefault()));
		Assert.assertEquals(1.1, _latestArticle.getVersion(), 0);
		Assert.assertEquals(
			externalReferenceCode, _latestArticle.getExternalReferenceCode());
	}

	@Test
	public void testFetchLatestArticleByNonexistentExternalReferenceCode()
		throws Exception {

		_latestArticle =
			_journalArticleService.fetchLatestArticleByExternalReferenceCode(
				_article.getGroupId(), RandomTestUtil.randomString());

		Assert.assertNull(_latestArticle);
	}

	@Test
	public void testFetchLatestArticleExpiredWithStatusAny() throws Exception {
		updateAndExpireLatestArticle("Version 2");

		_latestArticle = fetchLatestArticle(
			WorkflowConstants.STATUS_ANY, false);

		Assert.assertTrue(_latestArticle.isExpired());
		Assert.assertEquals(
			"Version 2", _latestArticle.getTitle(LocaleUtil.getDefault()));
		Assert.assertEquals(1.1, _latestArticle.getVersion(), 0);
	}

	@Test
	public void testFetchLatestArticleExpiredWithStatusApproved()
		throws Exception {

		updateAndExpireLatestArticle("Version 2");

		_latestArticle = fetchLatestArticle(
			WorkflowConstants.STATUS_APPROVED, false);

		Assert.assertTrue(_latestArticle.isApproved());
		Assert.assertEquals(
			"Version 1", _latestArticle.getTitle(LocaleUtil.getDefault()));
		Assert.assertEquals(1.0, _latestArticle.getVersion(), 0);
	}

	@Test
	public void testFetchLatestArticleExpiredWithStatusExpired()
		throws Exception {

		updateAndExpireLatestArticle("Version 2");

		_latestArticle = fetchLatestArticle(
			WorkflowConstants.STATUS_EXPIRED, false);

		Assert.assertTrue(_latestArticle.isExpired());
		Assert.assertEquals(
			"Version 2", _latestArticle.getTitle(LocaleUtil.getDefault()));
		Assert.assertEquals(1.1, _latestArticle.getVersion(), 0);
	}

	@Test
	public void testFetchLatestArticleNotExpiredWithStatusAny()
		throws Exception {

		_article = JournalTestUtil.updateArticle(_article, "Version 2");

		_latestArticle = fetchLatestArticle(
			WorkflowConstants.STATUS_ANY, false);

		Assert.assertTrue(_latestArticle.isApproved());
		Assert.assertEquals(
			"Version 2", _latestArticle.getTitle(LocaleUtil.getDefault()));
		Assert.assertEquals(1.1, _latestArticle.getVersion(), 0);
	}

	@Test
	public void testFetchLatestArticleNotExpiredWithStatusApproved()
		throws Exception {

		_article = JournalTestUtil.updateArticle(_article, "Version 2");

		_latestArticle = fetchLatestArticle(
			WorkflowConstants.STATUS_APPROVED, false);

		Assert.assertTrue(_latestArticle.isApproved());
		Assert.assertEquals(
			"Version 2", _latestArticle.getTitle(LocaleUtil.getDefault()));
		Assert.assertEquals(1.1, _latestArticle.getVersion(), 0);
	}

	@Test
	public void testFetchLatestArticleNotExpiredWithStatusExpired()
		throws Exception {

		_article = JournalTestUtil.updateArticle(_article, "Version 2");

		_latestArticle = fetchLatestArticle(
			WorkflowConstants.STATUS_EXPIRED, false);

		Assert.assertNull(_latestArticle);
	}

	@Test
	public void testGetArticles() throws Exception {
		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);

		JournalFolder journalFolder = journalFolderFixture.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalArticle approvedJournalArticle = _addApprovedJournalArticle(
			journalFolder, "ZZ Title");
		JournalArticle expiredJournalArticle = _addExpiredJournalArticle(
			journalFolder, "AA Title");

		_addDeletedJournalArticle(journalFolder, RandomTestUtil.randomString());

		List<JournalArticle> journalArticles =
			_journalArticleService.getArticles(
				_group.getGroupId(), journalFolder.getFolderId(),
				LocaleUtil.getDefault());

		Assert.assertEquals(
			journalArticles.toString(), 2, journalArticles.size());
		Assert.assertTrue(journalArticles.contains(approvedJournalArticle));
		Assert.assertTrue(journalArticles.contains(expiredJournalArticle));

		journalArticles = _journalArticleService.getArticles(
			_group.getGroupId(), journalFolder.getFolderId(),
			LocaleUtil.getDefault(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			ArticleTitleComparator.getInstance(true));

		Assert.assertEquals(
			Arrays.asList(expiredJournalArticle, approvedJournalArticle),
			journalArticles);

		journalArticles = _journalArticleService.getArticles(
			_group.getGroupId(), journalFolder.getFolderId(),
			LocaleUtil.getDefault(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			ArticleTitleComparator.getInstance(false));

		Assert.assertEquals(
			Arrays.asList(approvedJournalArticle, expiredJournalArticle),
			journalArticles);
	}

	@Test
	public void testGetArticlesById() throws Exception {
		List<JournalArticle> expectedArticles = new ArrayList<>();

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true);

		expectedArticles.add(article);

		article = updateArticleStatus(
			article, WorkflowConstants.STATUS_APPROVED);

		expectedArticles.add(article);

		article = updateArticleStatus(article, WorkflowConstants.STATUS_DRAFT);

		expectedArticles.add(article);

		int actualCount = _journalArticleService.getArticlesCountByArticleId(
			_group.getGroupId(), article.getArticleId());

		Assert.assertEquals(expectedArticles.size(), actualCount);

		List<JournalArticle> articles =
			_journalArticleService.getArticlesByArticleId(
				_group.getGroupId(), article.getArticleId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, ArticleVersionComparator.getInstance(true));

		Assert.assertEquals(
			articles.toString(), expectedArticles.size(), articles.size());

		Assert.assertEquals(expectedArticles, articles);
	}

	@Test
	public void testGetArticlesByIdAndStatus() throws Exception {
		List<JournalArticle> expectedArticles = new ArrayList<>();

		JournalArticle article = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true);

		expectedArticles.add(article);

		article = updateArticleStatus(
			article, WorkflowConstants.STATUS_APPROVED);

		expectedArticles.add(article);

		updateArticleStatus(article, WorkflowConstants.STATUS_DRAFT);

		int actualCount = _journalArticleService.getArticlesCountByArticleId(
			_group.getGroupId(), article.getArticleId(),
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(expectedArticles.size(), actualCount);

		List<JournalArticle> articles =
			_journalArticleService.getArticlesByArticleId(
				_group.getGroupId(), article.getArticleId(),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, ArticleVersionComparator.getInstance(true));

		Assert.assertEquals(
			articles.toString(), expectedArticles.size(), articles.size());

		Assert.assertEquals(expectedArticles, articles);
	}

	@Test
	public void testGetArticlesByStructureId() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		String content = DDMStructureTestUtil.getSampleStructuredContent();

		JournalArticle approvedJournalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, content,
				ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey(),
				LocaleUtil.getSiteDefault());

		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);

		JournalFolder journalFolder = journalFolderFixture.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalArticle expiredJournalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(), journalFolder.getFolderId(),
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, content,
				ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey(),
				LocaleUtil.getSiteDefault());

		expiredJournalArticle = _journalArticleLocalService.expireArticle(
			TestPropsValues.getUserId(), _group.getGroupId(),
			expiredJournalArticle.getArticleId(),
			expiredJournalArticle.getVersion(), null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		List<JournalArticle> journalArticles =
			_journalArticleService.getArticlesByStructureId(
				_group.getGroupId(),
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				ddmStructure.getStructureId(), WorkflowConstants.STATUS_ANY,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			journalArticles.toString(), 2, journalArticles.size());
		Assert.assertTrue(journalArticles.contains(approvedJournalArticle));
		Assert.assertTrue(journalArticles.contains(expiredJournalArticle));

		journalArticles = _journalArticleService.getArticlesByStructureId(
			_group.getGroupId(), JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			ddmStructure.getStructureId(), WorkflowConstants.STATUS_EXPIRED,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			Arrays.asList(expiredJournalArticle), journalArticles);

		journalArticles = _journalArticleService.getArticlesByStructureId(
			_group.getGroupId(), journalFolder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			ddmStructure.getStructureId(), WorkflowConstants.STATUS_EXPIRED,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			Arrays.asList(expiredJournalArticle), journalArticles);

		journalArticles = _journalArticleService.getArticlesByStructureId(
			_group.getGroupId(), JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			ddmStructure.getStructureId(), WorkflowConstants.STATUS_ANY,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			ArticleIDComparator.getInstance(true));

		Assert.assertEquals(
			Arrays.asList(approvedJournalArticle, expiredJournalArticle),
			journalArticles);

		journalArticles = _journalArticleService.getArticlesByStructureId(
			_group.getGroupId(), JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			ddmStructure.getStructureId(), WorkflowConstants.STATUS_ANY,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			ArticleIDComparator.getInstance(false));

		Assert.assertEquals(
			Arrays.asList(expiredJournalArticle, approvedJournalArticle),
			journalArticles);
	}

	@Test
	public void testGetArticlesByStructureIdWithNoPermissions()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		String content = DDMStructureTestUtil.getSampleStructuredContent();

		JournalArticle approvedJournalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, content,
				ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey(),
				LocaleUtil.getSiteDefault());

		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);

		JournalFolder journalFolder = journalFolderFixture.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalArticle expiredJournalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(), journalFolder.getFolderId(),
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, content,
				ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey(),
				LocaleUtil.getSiteDefault());

		expiredJournalArticle = _journalArticleLocalService.expireArticle(
			TestPropsValues.getUserId(), _group.getGroupId(),
			expiredJournalArticle.getArticleId(),
			expiredJournalArticle.getVersion(), null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_removeJournalArticleRoleResourcePermission(
			RoleConstants.GUEST,
			Arrays.asList(approvedJournalArticle, expiredJournalArticle),
			ActionKeys.VIEW);

		User user = UserTestUtil.addUser();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				_permissionCheckerFactory.create(user));

			List<JournalArticle> journalArticles =
				_journalArticleService.getArticlesByStructureId(
					_group.getGroupId(),
					JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
					ddmStructure.getStructureId(), WorkflowConstants.STATUS_ANY,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Assert.assertEquals(
				journalArticles.toString(), 0, journalArticles.size());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	public void testGetArticlesCount() throws Exception {
		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);

		JournalFolder journalFolder = journalFolderFixture.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		_addApprovedJournalArticle(
			journalFolder, RandomTestUtil.randomString());
		_addDeletedJournalArticle(journalFolder, RandomTestUtil.randomString());
		_addExpiredJournalArticle(journalFolder, RandomTestUtil.randomString());

		_assertGetArticlesCount(
			journalFolder, 1, WorkflowConstants.STATUS_APPROVED);
		_assertGetArticlesCount(
			journalFolder, 1, WorkflowConstants.STATUS_EXPIRED);
		_assertGetArticlesCount(
			journalFolder, 1, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Test
	public void testGetArticlesCountByStructureId() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		String content = DDMStructureTestUtil.getSampleStructuredContent();

		JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, content,
			ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey(),
			LocaleUtil.getSiteDefault());

		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);

		JournalFolder journalFolder = journalFolderFixture.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(), journalFolder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, content,
			ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey(),
			LocaleUtil.getSiteDefault());

		JournalArticle expiredJournalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(), journalFolder.getFolderId(),
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, content,
				ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey(),
				LocaleUtil.getSiteDefault());

		JournalTestUtil.expireArticle(
			_group.getGroupId(), expiredJournalArticle);

		Assert.assertEquals(
			2,
			_journalArticleService.getArticlesCountByStructureId(
				_group.getGroupId(),
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				ddmStructure.getStructureId(),
				WorkflowConstants.STATUS_APPROVED));

		Assert.assertEquals(
			1,
			_journalArticleService.getArticlesCountByStructureId(
				_group.getGroupId(), journalFolder.getFolderId(),
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				ddmStructure.getStructureId(),
				WorkflowConstants.STATUS_APPROVED));
	}

	@Test
	public void testGetArticlesCountWithNoPermissions() throws Exception {
		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);

		JournalFolder journalFolder = journalFolderFixture.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalArticle approvedJournalArticle = _addApprovedJournalArticle(
			journalFolder, RandomTestUtil.randomString());
		JournalArticle expiredJournalArticle = _addExpiredJournalArticle(
			journalFolder, RandomTestUtil.randomString());
		JournalArticle deletedJournalArticle = _addDeletedJournalArticle(
			journalFolder, RandomTestUtil.randomString());

		_removeJournalArticleRoleResourcePermission(
			RoleConstants.GUEST,
			Arrays.asList(
				approvedJournalArticle, expiredJournalArticle,
				deletedJournalArticle),
			ActionKeys.VIEW);

		User user = UserTestUtil.addUser();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				_permissionCheckerFactory.create(user));

			_assertGetArticlesCount(
				journalFolder, 0, WorkflowConstants.STATUS_APPROVED);
			_assertGetArticlesCount(
				journalFolder, 0, WorkflowConstants.STATUS_EXPIRED);
			_assertGetArticlesCount(
				journalFolder, 0, WorkflowConstants.STATUS_IN_TRASH);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	public void testGetArticlesWithNoPermissions() throws Exception {
		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);

		JournalFolder journalFolder = journalFolderFixture.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalArticle approvedJournalArticle = _addApprovedJournalArticle(
			journalFolder, RandomTestUtil.randomString());
		JournalArticle expiredJournalArticle = _addExpiredJournalArticle(
			journalFolder, RandomTestUtil.randomString());

		_removeJournalArticleRoleResourcePermission(
			RoleConstants.GUEST,
			Arrays.asList(approvedJournalArticle, expiredJournalArticle),
			ActionKeys.VIEW);

		User user = UserTestUtil.addUser();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				_permissionCheckerFactory.create(user));

			List<JournalArticle> journalArticles =
				_journalArticleService.getArticles(
					_group.getGroupId(), journalFolder.getFolderId(),
					LocaleUtil.getDefault());

			Assert.assertEquals(
				journalArticles.toString(), 0, journalArticles.size());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	public void testGetFoldersAndArticlesCountWithEmptyFolderIds() {
		Assert.assertEquals(
			0,
			_journalArticleService.getFoldersAndArticlesCount(
				_group.getGroupId(), Collections.emptyList()));
	}

	@Test
	public void testGetGroupArticlesWhenUserNotNullAndStatusAny()
		throws Exception {

		List<JournalArticle> expectedArticles = addArticles(
			2, RandomTestUtil.randomString());

		_article = updateArticleStatus(
			_article, WorkflowConstants.STATUS_DRAFT);

		expectedArticles.add(_article);

		int count = _journalArticleService.getGroupArticlesCount(
			_group.getGroupId(), _article.getUserId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertEquals(3, count);

		List<JournalArticle> articles = _journalArticleService.getGroupArticles(
			_group.getGroupId(), _article.getUserId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(expectedArticles, articles);
	}

	@Test
	public void testGetGroupArticlesWhenUserNotNullAndStatusApproved()
		throws Exception {

		List<JournalArticle> expectedArticles = addArticles(
			2, RandomTestUtil.randomString());

		expectedArticles.add(0, _article);

		_article = updateArticleStatus(
			_article, WorkflowConstants.STATUS_DRAFT);

		int count = _journalArticleService.getGroupArticlesCount(
			_group.getGroupId(), _article.getUserId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(3, count);

		List<JournalArticle> articles = _journalArticleService.getGroupArticles(
			_group.getGroupId(), _article.getUserId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(expectedArticles, articles);
	}

	@Test
	public void testGetGroupArticlesWhenUserNullAndStatusAny()
		throws Exception {

		List<JournalArticle> expectedArticles = addArticles(
			2, RandomTestUtil.randomString());

		_article = updateArticleStatus(
			_article, WorkflowConstants.STATUS_DRAFT);

		expectedArticles.add(_article);

		int count = _journalArticleService.getGroupArticlesCount(
			_group.getGroupId(), 0,
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertEquals(3, count);

		List<JournalArticle> articles = _journalArticleService.getGroupArticles(
			_group.getGroupId(), 0,
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(expectedArticles, articles);
	}

	@Test
	public void testGetGroupArticlesWhenUserNullAndStatusApproved()
		throws Exception {

		List<JournalArticle> expectedArticles = addArticles(
			2, RandomTestUtil.randomString());

		expectedArticles.add(0, _article);

		_article = updateArticleStatus(
			_article, WorkflowConstants.STATUS_DRAFT);

		int count = _journalArticleService.getGroupArticlesCount(
			_group.getGroupId(), 0,
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(3, count);

		List<JournalArticle> articles = _journalArticleService.getGroupArticles(
			_group.getGroupId(), 0,
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(expectedArticles, articles);
	}

	@Test
	public void testGetLatestArticleByExternalReferenceCode() throws Exception {
		long groupId = _article.getGroupId();
		String externalReferenceCode = _article.getExternalReferenceCode();

		_article = JournalTestUtil.updateArticle(_article, "Version 2");

		_latestArticle =
			_journalArticleService.getLatestArticleByExternalReferenceCode(
				groupId, externalReferenceCode);

		Assert.assertEquals(
			"Version 2", _latestArticle.getTitle(LocaleUtil.getDefault()));
		Assert.assertEquals(1.1, _latestArticle.getVersion(), 0);
		Assert.assertEquals(
			externalReferenceCode, _latestArticle.getExternalReferenceCode());
	}

	@Test(expected = NoSuchArticleException.class)
	public void testGetLatestArticleByNonexistentExternalReferenceCode()
		throws Exception {

		_journalArticleService.getLatestArticleByExternalReferenceCode(
			_article.getGroupId(), RandomTestUtil.randomString());
	}

	@Test
	public void testGetLatestArticlesByStatus() throws Exception {
		List<JournalArticle> articles = addArticles(
			1, RandomTestUtil.randomString());

		articles.add(0, _article);

		int count = _journalArticleService.getLatestArticlesCount(
			_group.getGroupId(), WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(2, count);

		Assert.assertEquals(
			_journalArticleService.getLatestArticles(
				_group.getGroupId(), WorkflowConstants.STATUS_APPROVED,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
			articles);

		_article = updateArticleStatus(
			_article, WorkflowConstants.STATUS_DRAFT);

		int draftCount = _journalArticleService.getLatestArticlesCount(
			_group.getGroupId(), WorkflowConstants.STATUS_DRAFT);

		Assert.assertEquals(1, draftCount);

		List<JournalArticle> draftArticles =
			_journalArticleService.getLatestArticles(
				_group.getGroupId(), WorkflowConstants.STATUS_DRAFT,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(_article, draftArticles.get(0));
	}

	@Test
	public void testUpdateArticle() throws Exception {
		_article.setDisplayDate(new Date());

		_article = JournalTestUtil.updateArticle(_article, "Version 2");

		Assert.assertEquals(
			"Version 2", _article.getTitle(LocaleUtil.getDefault()));
		Assert.assertTrue(_article.isApproved());
		Assert.assertEquals(1.1, _article.getVersion(), 0);

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			_article.getModelClassName(), _article.getResourcePrimKey());

		Assert.assertEquals(
			_article.getDisplayDate(), assetEntry.getPublishDate());
	}

	@Test
	public void testUpdateExpiredArticle() throws Exception {
		_article = JournalTestUtil.expireArticle(
			_group.getGroupId(), _article, _article.getVersion());

		Assert.assertTrue(_article.isExpired());

		_article.setDisplayDate(new Date());

		_article = JournalTestUtil.updateArticle(_article, "Version 2");

		Assert.assertEquals(
			"Version 2", _article.getTitle(LocaleUtil.getDefault()));
		Assert.assertTrue(_article.isApproved());
		Assert.assertEquals(1.1, _article.getVersion(), 0);

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			_article.getModelClassName(), _article.getResourcePrimKey());

		Assert.assertEquals(
			_article.getDisplayDate(), assetEntry.getPublishDate());
	}

	protected List<JournalArticle> addArticles(int count, String content)
		throws Exception {

		List<JournalArticle> articles = new ArrayList<>(count);

		for (int i = 0; i < count; i++) {
			JournalArticle article = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomString(), content);

			articles.add(article);
		}

		return articles;
	}

	protected JournalArticle fetchLatestArticle(int status) {
		return _journalArticleLocalService.fetchLatestArticle(
			_group.getGroupId(), _article.getArticleId(), status);
	}

	protected JournalArticle fetchLatestArticle(
		int status, boolean preferApproved) {

		return _journalArticleLocalService.fetchLatestArticle(
			_article.getResourcePrimKey(), status, preferApproved);
	}

	protected String readText(String fileName) throws Exception {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"com/liferay/journal/dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	protected void testAddArticleRequiredFields(
			String ddmStructureDefinition, String journalArticleContent,
			Map<String, String> requiredFields)
		throws Exception {

		String definition = readText(ddmStructureDefinition);

		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(
				definition);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_ddmFormDeserializer.deserialize(builder.build());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName(),
			ddmFormDeserializerDeserializeResponse.getDDMForm());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		String xmlContent = readText(journalArticleContent);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		for (Map.Entry<String, String> entry : requiredFields.entrySet()) {
			Assert.assertTrue(ddmStructure.getFieldRequired(entry.getKey()));

			serviceContext.setAttribute(entry.getKey(), entry.getValue());
		}

		JournalTestUtil.addArticleWithXMLContent(
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xmlContent,
			ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey(),
			LocaleUtil.fromLanguageId(ddmStructure.getDefaultLanguageId()),
			serviceContext);
	}

	protected void updateAndExpireArticle() throws Exception {
		JournalTestUtil.updateArticle(_article, "Version 2");

		JournalTestUtil.expireArticle(_group.getGroupId(), _article);
	}

	protected void updateAndExpireLatestArticle(String title) throws Exception {
		JournalTestUtil.updateArticle(_article, title);

		_article = JournalTestUtil.expireArticle(
			_group.getGroupId(), _article, 1.1);
	}

	protected JournalArticle updateArticleStatus(
			JournalArticle article, int status)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		if (status == WorkflowConstants.STATUS_DRAFT) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}
		else {
			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		}

		return JournalTestUtil.updateArticle(
			article, "Version 2", article.getContent(), false, true,
			serviceContext);
	}

	private JournalArticle _addApprovedJournalArticle(
			JournalFolder journalFolder, String title)
		throws Exception {

		return JournalTestUtil.addArticle(
			_group.getGroupId(), journalFolder.getFolderId(), title,
			RandomTestUtil.randomString());
	}

	private JournalArticle _addDeletedJournalArticle(
			JournalFolder journalFolder, String title)
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), journalFolder.getFolderId(), title,
			RandomTestUtil.randomString());

		return _journalArticleLocalService.moveArticleToTrash(
			TestPropsValues.getUserId(), journalArticle);
	}

	private JournalArticle _addExpiredJournalArticle(
			JournalFolder journalFolder, String title)
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), journalFolder.getFolderId(), title,
			RandomTestUtil.randomString());

		return _journalArticleLocalService.expireArticle(
			TestPropsValues.getUserId(), _group.getGroupId(),
			journalArticle.getArticleId(), journalArticle.getVersion(), null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _assertGetArticlesCount(
		JournalFolder journalFolder, int expectedCount, int status) {

		Assert.assertEquals(
			expectedCount,
			_journalArticleService.getArticlesCount(
				_group.getGroupId(), journalFolder.getFolderId(), status));
	}

	private void _removeJournalArticleRoleResourcePermission(
			String roleName, List<JournalArticle> journalArticles,
			String actionId)
		throws Exception {

		for (JournalArticle journalArticle : journalArticles) {
			RoleTestUtil.removeResourcePermission(
				roleName, JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(journalArticle.getResourcePrimKey()), actionId);
		}
	}

	private JournalArticle _article;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject(filter = "ddm.form.deserializer.type=xsd")
	private DDMFormDeserializer _ddmFormDeserializer;

	@Inject
	private DDMTemplateLinkLocalService _ddmTemplateLinkLocalService;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalArticleService _journalArticleService;

	@Inject
	private JournalFolderLocalService _journalFolderLocalService;

	@Inject
	private Language _language;

	private JournalArticle _latestArticle;
	private String _originalPortalPreferencesXML;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	@Inject
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Inject
	private UserLocalService _userLocalService;

}