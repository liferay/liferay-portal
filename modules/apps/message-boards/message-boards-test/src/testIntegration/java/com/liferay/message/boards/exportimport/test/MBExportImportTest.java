/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.test.util.lar.BasePortletExportImportTestCase;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.service.MBThreadLocalServiceUtil;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Kocsis
 */
@RunWith(Arquillian.class)
public class MBExportImportTest extends BasePortletExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	public String getNamespace() {
		return "message_boards";
	}

	@Override
	public String getPortletId() {
		return MBPortletKeys.MESSAGE_BOARDS_ADMIN;
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testExportImportThreadsDeletions() throws Exception {
		MBMessage message = MBTestUtil.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			group.getGroupId(), MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));

		MBThread thread = message.getThread();

		String messageUuid = message.getUuid();

		exportImportPortlet(getPortletId());

		// Delete the thread and not the message this time

		MBThreadLocalServiceUtil.deleteThread(thread);

		exportImportPortlet(getPortletId());

		MBMessage importedMessage =
			MBMessageLocalServiceUtil.fetchMBMessageByUuidAndGroupId(
				messageUuid, importedGroup.getGroupId());

		Assert.assertNotNull(importedMessage);

		exportImportPortlet(
			getPortletId(),
			LinkedHashMapBuilder.put(
				PortletDataHandlerKeys.DELETIONS,
				new String[] {Boolean.TRUE.toString()}
			).build(),
			LinkedHashMapBuilder.put(
				PortletDataHandlerKeys.DELETIONS,
				new String[] {Boolean.TRUE.toString()}
			).build());

		importedMessage =
			MBMessageLocalServiceUtil.fetchMBMessageByUuidAndGroupId(
				messageUuid, importedGroup.getGroupId());

		Assert.assertNull(importedMessage);
	}

	@Override
	protected StagedModel addStagedModel(long groupId) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		return MBTestUtil.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), groupId,
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);
	}

	@Override
	protected StagedModel addStagedModel(long groupId, Date createdDate)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setCreateDate(createdDate);
		serviceContext.setModifiedDate(createdDate);

		return MBTestUtil.addMessage(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), groupId,
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);
	}

	@Override
	protected void deleteStagedModel(StagedModel stagedModel) throws Exception {
		MBMessageLocalServiceUtil.deleteMessage((MBMessage)stagedModel);
	}

	@Override
	protected Map<String, String[]> getExportParameterMap() throws Exception {
		Map<String, String[]> parameterMap = super.getExportParameterMap();

		addParameter(parameterMap, "messages", true);

		return parameterMap;
	}

	@Override
	protected Map<String, String[]> getImportParameterMap() throws Exception {
		Map<String, String[]> parameterMap = super.getImportParameterMap();

		addParameter(parameterMap, "messages", true);

		return parameterMap;
	}

	@Override
	protected StagedModel getStagedModel(String uuid, long groupId) {
		return MBMessageLocalServiceUtil.fetchMBMessageByUuidAndGroupId(
			uuid, groupId);
	}

}