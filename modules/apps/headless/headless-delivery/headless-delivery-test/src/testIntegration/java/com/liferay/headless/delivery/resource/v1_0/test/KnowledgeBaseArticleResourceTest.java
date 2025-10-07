/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.delivery.client.dto.v1_0.KnowledgeBaseArticle;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalServiceUtil;
import com.liferay.knowledge.base.service.KBFolderLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.DateTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class KnowledgeBaseArticleResourceTest
	extends BaseKnowledgeBaseArticleResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(testGroup.getGroupId());

		_kbFolder = KBFolderLocalServiceUtil.addKBFolder(
			null, UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			testGroup.getGroupId(),
			PortalUtil.getClassNameId(KBFolder.class.getName()), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);
	}

	@Override
	@Test
	public void testDeleteKnowledgeBaseArticleMyRating() throws Exception {
		super.testDeleteKnowledgeBaseArticleMyRating();

		KnowledgeBaseArticle knowledgeBaseArticle =
			testDeleteKnowledgeBaseArticleMyRating_addKnowledgeBaseArticle();

		assertHttpResponseStatusCode(
			204,
			knowledgeBaseArticleResource.
				deleteKnowledgeBaseArticleMyRatingHttpResponse(
					knowledgeBaseArticle.getId()));
		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.
				deleteKnowledgeBaseArticleMyRatingHttpResponse(
					knowledgeBaseArticle.getId()));

		KnowledgeBaseArticle irrelevantKnowledgeBaseArticle =
			randomIrrelevantKnowledgeBaseArticle();

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.
				deleteKnowledgeBaseArticleMyRatingHttpResponse(
					irrelevantKnowledgeBaseArticle.getId()));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteKnowledgeBaseArticleMyRating()
		throws Exception {

		super.testGraphQLDeleteKnowledgeBaseArticleMyRating();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetKnowledgeBaseArticleKnowledgeBaseArticlesPage()
		throws Exception {

		super.testGraphQLGetKnowledgeBaseArticleKnowledgeBaseArticlesPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostKnowledgeBaseFolderKnowledgeBaseArticle()
		throws Exception {

		super.testGraphQLPostKnowledgeBaseFolderKnowledgeBaseArticle();
	}

	@Override
	@Test
	public void testPatchKnowledgeBaseArticle() throws Exception {
		super.testPatchKnowledgeBaseArticle();

		KnowledgeBaseArticle randomKnowledgeBaseArticle =
			randomKnowledgeBaseArticle();

		KnowledgeBaseArticle postknowledgeBaseArticle =
			knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
				testGroup.getGroupId(), randomKnowledgeBaseArticle);

		String randomTitle = RandomTestUtil.randomString();

		KnowledgeBaseArticle patchKnowledgeBaseArticle =
			knowledgeBaseArticleResource.patchKnowledgeBaseArticle(
				postknowledgeBaseArticle.getId(),
				new KnowledgeBaseArticle() {
					{
						title = randomTitle;
					}
				});

		assertValid(patchKnowledgeBaseArticle);

		Assert.assertEquals(randomTitle, patchKnowledgeBaseArticle.getTitle());
		Assert.assertNotEquals(
			postknowledgeBaseArticle.getTitle(),
			patchKnowledgeBaseArticle.getTitle());
	}

	@Override
	@Test
	public void testPostKnowledgeBaseArticleKnowledgeBaseArticle()
		throws Exception {

		super.testPostKnowledgeBaseArticleKnowledgeBaseArticle();

		KnowledgeBaseArticle knowledgeBaseArticle =
			randomKnowledgeBaseArticle();

		KnowledgeBaseArticle postKnowledgeBaseArticle =
			knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
				testGroup.getGroupId(), knowledgeBaseArticle);

		assertValid(postKnowledgeBaseArticle);

		DateTestUtil.assertEquals(
			_truncateMilliseconds(knowledgeBaseArticle.getDatePublished()),
			postKnowledgeBaseArticle.getDatePublished());
	}

	@Override
	@Test
	public void testPutSiteKnowledgeBaseArticleSubscribe() throws Exception {
		KnowledgeBaseArticle knowledgeBaseArticle =
			testPutSiteKnowledgeBaseArticleSubscribe_addKnowledgeBaseArticle();

		assertHttpResponseStatusCode(
			204,
			knowledgeBaseArticleResource.
				putSiteKnowledgeBaseArticleSubscribeHttpResponse(
					knowledgeBaseArticle.getSiteId()));
	}

	@Override
	@Test
	public void testPutSiteKnowledgeBaseArticleUnsubscribe() throws Exception {
		KnowledgeBaseArticle knowledgeBaseArticle =
			testPutSiteKnowledgeBaseArticleUnsubscribe_addKnowledgeBaseArticle();

		assertHttpResponseStatusCode(
			204,
			knowledgeBaseArticleResource.
				putSiteKnowledgeBaseArticleUnsubscribeHttpResponse(
					knowledgeBaseArticle.getSiteId()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"articleBody", "description", "title"};
	}

	@Override
	protected KnowledgeBaseArticle randomKnowledgeBaseArticle()
		throws Exception {

		KnowledgeBaseArticle knowledgeBaseArticle =
			super.randomKnowledgeBaseArticle();

		knowledgeBaseArticle.setParentKnowledgeBaseArticleId(0L);
		knowledgeBaseArticle.setParentKnowledgeBaseFolderId(0L);

		return knowledgeBaseArticle;
	}

	@Override
	protected KnowledgeBaseArticle
			testDeleteKnowledgeBaseArticleMyRating_addKnowledgeBaseArticle()
		throws Exception {

		KnowledgeBaseArticle knowledgeBaseArticle =
			super.
				testDeleteKnowledgeBaseArticleMyRating_addKnowledgeBaseArticle();

		knowledgeBaseArticleResource.putKnowledgeBaseArticleMyRating(
			knowledgeBaseArticle.getId(), randomRating());

		return knowledgeBaseArticle;
	}

	@Override
	protected Long
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getParentKnowledgeBaseArticleId()
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(testGroup.getGroupId());

		KBArticle kbArticle = KBArticleLocalServiceUtil.addKBArticle(
			null, UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			PortalUtil.getClassNameId(KBFolder.class.getName()), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, RandomTestUtil.nextDate(), null, null, null, serviceContext);

		return kbArticle.getResourcePrimKey();
	}

	@Override
	protected Long
		testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getKnowledgeBaseFolderId() {

		return _kbFolder.getKbFolderId();
	}

	private Date _truncateMilliseconds(Date date) {
		Instant instant = date.toInstant();

		return Date.from(instant.truncatedTo(ChronoUnit.SECONDS));
	}

	private KBFolder _kbFolder;

}