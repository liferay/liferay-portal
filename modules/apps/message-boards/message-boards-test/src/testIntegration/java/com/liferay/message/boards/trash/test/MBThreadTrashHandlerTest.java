/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.trash.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBCategoryLocalServiceUtil;
import com.liferay.message.boards.service.MBCategoryServiceUtil;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.service.MBThreadLocalServiceUtil;
import com.liferay.message.boards.service.MBThreadServiceUtil;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.test.util.BaseTrashHandlerTestCase;
import com.liferay.trash.test.util.DefaultWhenIsAssetable;
import com.liferay.trash.test.util.DefaultWhenIsIndexableBaseModel;
import com.liferay.trash.test.util.WhenHasGrandParent;
import com.liferay.trash.test.util.WhenHasMyBaseModel;
import com.liferay.trash.test.util.WhenHasRecentBaseModelCount;
import com.liferay.trash.test.util.WhenIsAssetable;
import com.liferay.trash.test.util.WhenIsAssetableBaseModel;
import com.liferay.trash.test.util.WhenIsIndexableBaseModel;
import com.liferay.trash.test.util.WhenIsMoveableFromTrashBaseModel;
import com.liferay.trash.test.util.WhenIsRestorableBaseModel;
import com.liferay.trash.test.util.WhenIsUpdatableBaseModel;

import java.io.InputStream;

import java.util.Calendar;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zsolt Berentey
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
public class MBThreadTrashHandlerTest
	extends BaseTrashHandlerTestCase
	implements WhenHasGrandParent, WhenHasMyBaseModel,
			   WhenHasRecentBaseModelCount, WhenIsAssetableBaseModel,
			   WhenIsIndexableBaseModel, WhenIsMoveableFromTrashBaseModel,
			   WhenIsRestorableBaseModel, WhenIsUpdatableBaseModel {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	public AssetEntry fetchAssetEntry(ClassedModel classedModel)
		throws Exception {

		return _whenIsAssetable.fetchAssetEntry(classedModel);
	}

	@Override
	public int getMineBaseModelsCount(long groupId, long userId)
		throws Exception {

		return MBThreadServiceUtil.getGroupThreadsCount(
			groupId, userId, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public String getParentBaseModelClassName() {
		Class<MBCategory> mbCategoryClass = MBCategory.class;

		return mbCategoryClass.getName();
	}

	@Override
	public int getRecentBaseModelsCount(long groupId) throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.HOUR, -1);

		return MBThreadServiceUtil.getGroupThreadsCount(
			groupId, 0, calendar.getTime(), WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public String getSearchKeywords() {
		return _SUBJECT;
	}

	@Override
	public boolean isAssetEntryVisible(ClassedModel classedModel, long classPK)
		throws Exception {

		MBThread mbThread = (MBThread)classedModel;

		MBMessage rootMessage = MBMessageLocalServiceUtil.getMBMessage(
			mbThread.getRootMessageId());

		return _whenIsAssetable.isAssetEntryVisible(
			rootMessage, getAssetClassPK(rootMessage));
	}

	@Override
	public BaseModel<?> moveBaseModelFromTrash(
			ClassedModel classedModel, Group group,
			ServiceContext serviceContext)
		throws Exception {

		BaseModel<?> parentBaseModel = getParentBaseModel(
			group, serviceContext);

		MBThreadServiceUtil.moveThreadFromTrash(
			(Long)parentBaseModel.getPrimaryKeyObj(),
			(Long)classedModel.getPrimaryKeyObj());

		return parentBaseModel;
	}

	@Override
	public void moveParentBaseModelToTrash(long primaryKey) throws Exception {
		MBCategoryServiceUtil.moveCategoryToTrash(primaryKey);
	}

	@Override
	public int searchBaseModelsCount(Class<?> clazz, long groupId)
		throws Exception {

		return _whenIsIndexableBaseModel.searchBaseModelsCount(
			MBMessage.class, groupId);
	}

	@Override
	public int searchTrashEntriesCount(
			String keywords, ServiceContext serviceContext)
		throws Exception {

		return _whenIsIndexableBaseModel.searchTrashEntriesCount(
			keywords, serviceContext);
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testCategoryMessageCount() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		BaseModel<?> parentBaseModel = getParentBaseModel(
			group, serviceContext);

		int initialBaseModelsCount = getMessageCount(
			(Long)parentBaseModel.getPrimaryKeyObj());

		baseModel = addBaseModel(parentBaseModel, serviceContext);

		Assert.assertEquals(
			initialBaseModelsCount + 1,
			getMessageCount((Long)parentBaseModel.getPrimaryKeyObj()));

		moveBaseModelToTrash((Long)baseModel.getPrimaryKeyObj());

		Assert.assertEquals(
			initialBaseModelsCount,
			getMessageCount((Long)parentBaseModel.getPrimaryKeyObj()));

		baseModel = addBaseModel(parentBaseModel, serviceContext);

		Assert.assertEquals(
			initialBaseModelsCount + 1,
			getMessageCount((Long)parentBaseModel.getPrimaryKeyObj()));

		replyMessage(baseModel);

		Assert.assertEquals(
			initialBaseModelsCount + 2,
			getMessageCount((Long)parentBaseModel.getPrimaryKeyObj()));
	}

	@Override
	@Test(expected = TrashEntryException.class)
	public void testTrashParentAndBaseModel() throws Exception {
		super.testTrashParentAndBaseModel();
	}

	@Override
	@Test(expected = RestoreEntryException.class)
	public void testTrashParentAndRestoreParentAndBaseModel() throws Exception {
		super.testTrashParentAndRestoreParentAndBaseModel();
	}

	@Override
	public BaseModel<?> updateBaseModel(
			long primaryKey, ServiceContext serviceContext)
		throws Exception {

		MBThread thread = MBThreadLocalServiceUtil.getThread(primaryKey);

		if (serviceContext.getWorkflowAction() ==
				WorkflowConstants.ACTION_SAVE_DRAFT) {

			thread = MBThreadLocalServiceUtil.updateStatus(
				TestPropsValues.getUserId(), primaryKey,
				WorkflowConstants.STATUS_DRAFT);
		}

		return thread;
	}

	@Override
	protected BaseModel<?> addBaseModelWithWorkflow(
			BaseModel<?> parentBaseModel, ServiceContext serviceContext)
		throws Exception {

		MBCategory category = (MBCategory)parentBaseModel;

		MBMessage message = MBTestUtil.addMessageWithWorkflow(
			category.getGroupId(), category.getCategoryId(),
			getSearchKeywords(), getSearchKeywords(), true, serviceContext);

		return message.getThread();
	}

	@Override
	protected BaseModel<?> addBaseModelWithWorkflow(
			ServiceContext serviceContext)
		throws Exception {

		MBMessage message = MBTestUtil.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			serviceContext.getScopeGroupId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		return message.getThread();
	}

	@Override
	protected void deleteParentBaseModel(
			BaseModel<?> parentBaseModel, boolean includeTrashedEntries)
		throws Exception {

		MBCategory parentCategory = (MBCategory)parentBaseModel;

		MBCategoryLocalServiceUtil.deleteCategory(parentCategory, false);
	}

	@Override
	protected BaseModel<?> getBaseModel(long primaryKey) throws Exception {
		return MBThreadLocalServiceUtil.getThread(primaryKey);
	}

	@Override
	protected Class<?> getBaseModelClass() {
		return MBThread.class;
	}

	protected int getMessageCount(long categoryId) throws Exception {
		MBCategory category = MBCategoryLocalServiceUtil.getCategory(
			categoryId);

		return category.getMessageCount();
	}

	@Override
	protected int getNotInTrashBaseModelsCount(BaseModel<?> parentBaseModel)
		throws Exception {

		MBCategory category = (MBCategory)parentBaseModel;

		return MBThreadLocalServiceUtil.getThreadsCount(
			category.getGroupId(), category.getCategoryId(),
			WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	protected BaseModel<?> getParentBaseModel(
			Group group, long parentBaseModelId, ServiceContext serviceContext)
		throws Exception {

		return MBCategoryLocalServiceUtil.addCategory(
			null, TestPropsValues.getUserId(), parentBaseModelId,
			RandomTestUtil.randomString(), StringPool.BLANK, serviceContext);
	}

	@Override
	protected BaseModel<?> getParentBaseModel(
			Group group, ServiceContext serviceContext)
		throws Exception {

		return MBCategoryLocalServiceUtil.addCategory(
			null, TestPropsValues.getUserId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, serviceContext);
	}

	@Override
	protected TrashHandler getTrashHandler(String className) {
		if (className.equals(MBCategory.class.getName())) {
			return _mbCategoryTrashHandler;
		}

		if (className.equals(MBThread.class.getName())) {
			return _mbThreadTrashHandler;
		}

		return null;
	}

	@Override
	protected String getUniqueTitle(BaseModel<?> baseModel) {
		return null;
	}

	@Override
	protected boolean isBaseModelContainerModel() {
		return false;
	}

	@Override
	protected boolean isInTrashContainer(TrashedModel trashedModel) {
		return _trashHelper.isInTrashContainer(trashedModel);
	}

	@Override
	protected void moveBaseModelToTrash(long primaryKey) throws Exception {
		MBThreadServiceUtil.moveThreadToTrash(primaryKey);
	}

	protected void replyMessage(BaseModel<?> baseModel) throws Exception {
		MBThread thread = (MBThread)baseModel;

		MBMessageLocalServiceUtil.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			thread.getGroupId(), thread.getCategoryId(), thread.getThreadId(),
			thread.getRootMessageId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), MBMessageConstants.DEFAULT_FORMAT,
			Collections.<ObjectValuePair<String, InputStream>>emptyList(),
			false, 0.0, false,
			ServiceContextTestUtil.getServiceContext(
				thread.getGroupId(), TestPropsValues.getUserId()));
	}

	private static final String _SUBJECT = "Subject";

	@Inject(
		filter = "model.class.name=com.liferay.message.boards.model.MBCategory"
	)
	private TrashHandler _mbCategoryTrashHandler;

	@Inject(
		filter = "model.class.name=com.liferay.message.boards.model.MBThread"
	)
	private TrashHandler _mbThreadTrashHandler;

	@Inject
	private TrashHelper _trashHelper;

	private final WhenIsAssetable _whenIsAssetable =
		new DefaultWhenIsAssetable();
	private final WhenIsIndexableBaseModel _whenIsIndexableBaseModel =
		new DefaultWhenIsIndexableBaseModel();

}