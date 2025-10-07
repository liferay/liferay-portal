/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBCategoryServiceUtil;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.service.MBThreadLocalServiceUtil;
import com.liferay.message.boards.service.MBThreadServiceUtil;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.util.BaseSearchTestCase;
import com.liferay.portal.search.test.util.SearchContextTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
@Sync
public class MBMessageSearchTest extends BaseSearchTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Override
	@Test
	public void testLocalizedSearch() throws Exception {
	}

	@Override
	@Test
	public void testSearchByDDMStructureField() throws Exception {
	}

	@Override
	@Test
	public void testSearchByKeywordsInsideParentBaseModel() throws Exception {
	}

	@Override
	@Test
	public void testSearchComments() throws Exception {
	}

	@Override
	@Test
	public void testSearchCommentsByKeywords() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			serviceContext);

		String body = RandomTestUtil.randomString();

		CommentManagerUtil.addComment(
			TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			BlogsEntry.class.getName(), blogsEntry.getEntryId(), body,
			serviceContext1 -> serviceContext);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			TestPropsValues.getGroupId(), 0);

		long messageId = CommentManagerUtil.addComment(
			TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			JournalArticle.class.getName(), journalArticle.getClassPK(), body,
			serviceContext1 -> serviceContext);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			TestPropsValues.getUserId(), body, LocaleUtil.US);

		searchContext.setAttribute(
			Field.CLASS_NAME_ID,
			PortalUtil.getClassNameId(JournalArticle.class));
		searchContext.setAttribute("discussion", Boolean.TRUE);

		Indexer<MBMessage> indexer = IndexerRegistryUtil.getIndexer(
			MBMessage.class);

		Hits hits = indexer.search(searchContext);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());

		Document document = hits.doc(0);

		Assert.assertEquals(
			messageId, GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));
	}

	@Override
	@Test
	public void testSearchExpireAllVersions() throws Exception {
	}

	@Override
	@Test
	public void testSearchExpireLatestVersion() throws Exception {
	}

	@Override
	@Test
	public void testSearchStatus() throws Exception {
	}

	@Override
	@Test
	public void testSearchVersions() throws Exception {
	}

	@Override
	@Test
	public void testSearchWithinDDMStructure() throws Exception {
	}

	@Override
	protected void addAttachment(ClassedModel classedModel) throws Exception {
		MBMessage message = (MBMessage)classedModel;

		MBMessageLocalServiceUtil.updateMessage(
			TestPropsValues.getUserId(), message.getMessageId(),
			getSearchKeywords(), getSearchKeywords(),
			MBTestUtil.getInputStreamOVPs(
				"OSX_Test.docx", getClass(), getSearchKeywords()),
			0, false,
			ServiceContextTestUtil.getServiceContext(
				message.getGroupId(), TestPropsValues.getUserId()));
	}

	@Override
	protected BaseModel<?> addBaseModelWithWorkflow(
			BaseModel<?> parentBaseModel, boolean approved, String keywords,
			ServiceContext serviceContext)
		throws Exception {

		MBCategory category = (MBCategory)parentBaseModel;

		return MBTestUtil.addMessageWithWorkflow(
			category.getGroupId(), category.getCategoryId(),
			RandomTestUtil.randomString(), keywords, approved, serviceContext);
	}

	@Override
	protected void deleteBaseModel(long primaryKey) throws Exception {
		MBMessage message = MBMessageLocalServiceUtil.getMessage(primaryKey);

		if (!message.isRoot()) {
			MBMessageLocalServiceUtil.deleteMessage(primaryKey);
		}
		else {
			MBThreadLocalServiceUtil.deleteThread(message.getThreadId());
		}
	}

	@Override
	protected Class<?> getBaseModelClass() {
		return MBMessage.class;
	}

	@Override
	protected BaseModel<?> getParentBaseModel(
			Group group, ServiceContext serviceContext)
		throws Exception {

		return MBCategoryServiceUtil.addCategory(
			null, TestPropsValues.getUserId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, serviceContext);
	}

	@Override
	protected String getParentBaseModelClassName() {
		return MBCategory.class.getName();
	}

	@Override
	protected String getSearchKeywords() {
		return "Title";
	}

	@Override
	protected void moveBaseModelToTrash(long primaryKey) throws Exception {
		MBMessage message = MBMessageLocalServiceUtil.getMessage(primaryKey);

		MBThreadServiceUtil.moveThreadToTrash(message.getThreadId());
	}

	@Override
	protected void moveParentBaseModelToTrash(long primaryKey)
		throws Exception {

		MBCategoryServiceUtil.moveCategoryToTrash(primaryKey);
	}

	@Override
	protected Hits searchGroupEntries(long groupId, long creatorUserId)
		throws Exception {

		return MBThreadServiceUtil.search(
			groupId, creatorUserId, WorkflowConstants.STATUS_APPROVED,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	protected BaseModel<?> updateBaseModel(
			BaseModel<?> baseModel, String keywords,
			ServiceContext serviceContext)
		throws Exception {

		MBMessage message = (MBMessage)baseModel;

		ServiceContext updateServiceContext =
			ServiceContextTestUtil.getServiceContext(
				message.getGroupId(), TestPropsValues.getUserId());

		updateServiceContext.setWorkflowAction(
			WorkflowConstants.ACTION_PUBLISH);

		return MBMessageLocalServiceUtil.updateMessage(
			TestPropsValues.getUserId(), message.getMessageId(), keywords,
			updateServiceContext);
	}

}