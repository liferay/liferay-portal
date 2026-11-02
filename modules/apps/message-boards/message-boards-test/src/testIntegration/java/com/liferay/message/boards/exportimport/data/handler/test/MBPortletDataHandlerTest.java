/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.test.util.lar.BasePortletDataHandlerTestCase;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBBanLocalServiceUtil;
import com.liferay.message.boards.service.MBCategoryLocalServiceUtil;
import com.liferay.message.boards.service.MBCategoryServiceUtil;
import com.liferay.message.boards.service.MBThreadFlagLocalServiceUtil;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LongWrapper;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zsolt Berentey
 */
@RunWith(Arquillian.class)
public class MBPortletDataHandlerTest extends BasePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testDeleteAllFolders() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		MBCategory parentCategory = MBCategoryServiceUtil.addCategory(
			null, TestPropsValues.getUserId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, serviceContext);

		MBCategory childCategory = MBCategoryServiceUtil.addCategory(
			null, TestPropsValues.getUserId(), parentCategory.getCategoryId(),
			RandomTestUtil.randomString(), StringPool.BLANK, serviceContext);

		MBCategoryLocalServiceUtil.moveCategoryToTrash(
			TestPropsValues.getUserId(), childCategory.getCategoryId());

		MBCategoryLocalServiceUtil.moveCategoryToTrash(
			TestPropsValues.getUserId(), parentCategory.getCategoryId());

		MBCategoryLocalServiceUtil.deleteCategory(parentCategory, false);

		GroupLocalServiceUtil.deleteGroup(group);

		List<MBCategory> categories = MBCategoryLocalServiceUtil.getCategories(
			group.getGroupId());

		Assert.assertEquals(categories.toString(), 0, categories.size());
	}

	@Override
	protected void addParameters(Map<String, String[]> parameterMap) {
		addBooleanParameter(parameterMap, "message_boards", "messages", true);
		addBooleanParameter(
			parameterMap, "message_boards", "thread-flags", true);
		addBooleanParameter(parameterMap, "message_boards", "user-bans", true);
	}

	@Override
	protected void addStagedModels() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), TestPropsValues.getUserId());

		MBCategory category = MBCategoryServiceUtil.addCategory(
			null, TestPropsValues.getUserId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		MBMessage message = MBTestUtil.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			stagingGroup.getGroupId(), category.getCategoryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		MBThreadFlagLocalServiceUtil.addThreadFlag(
			TestPropsValues.getUserId(), message.getThread(), serviceContext);

		User user = UserTestUtil.addUser(TestPropsValues.getGroupId());

		MBBanLocalServiceUtil.addBan(
			TestPropsValues.getUserId(), user.getUserId(), serviceContext);
	}

	@Override
	protected void checkManifestSummary(
		ManifestSummary expectedManifestSummary) {

		String manifestSummaryKey = ManifestSummary.getManifestSummaryKey(
			new StagedModelType(MBThread.class.getName()));

		Collection<String> manifestSummaryKeys =
			expectedManifestSummary.getManifestSummaryKeys();

		manifestSummaryKeys.remove(manifestSummaryKey);

		Map<String, LongWrapper> modelAdditionCounters =
			expectedManifestSummary.getModelAdditionCounters();

		modelAdditionCounters.remove(manifestSummaryKey);

		Map<String, LongWrapper> modelDeletionCounters =
			expectedManifestSummary.getModelDeletionCounters();

		modelDeletionCounters.remove(manifestSummaryKey);

		super.checkManifestSummary(expectedManifestSummary);
	}

	@Override
	protected DataLevel getDataLevel() {
		return DataLevel.PORTLET_INSTANCE;
	}

	@Override
	protected String getPortletId() {
		return MBPortletKeys.MESSAGE_BOARDS;
	}

	@Override
	protected boolean isDataPortalLevel() {
		return false;
	}

	@Override
	protected boolean isDataPortletInstanceLevel() {
		return true;
	}

	@Override
	protected boolean isDataSiteLevel() {
		return false;
	}

}