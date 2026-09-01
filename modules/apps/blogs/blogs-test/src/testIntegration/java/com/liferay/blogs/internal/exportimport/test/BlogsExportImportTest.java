/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.exportimport.test.util.lar.BasePortletExportImportTestCase;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Juan Fernández
 */
@RunWith(Arquillian.class)
public class BlogsExportImportTest extends BasePortletExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	public String getNamespace() {
		return "blogs";
	}

	@Override
	public String getPortletId() {
		return BlogsPortletKeys.BLOGS_ADMIN;
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	@TestInfo({"LPS-127212", "LPS-128781"})
	public void testExportImportBlogsEntryWithExistingTitle() throws Exception {
		String title = RandomTestUtil.randomString();

		BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), title, RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));

		BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), title, RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				importedGroup.getGroupId(), TestPropsValues.getUserId()));

		exportImportPortlet(BlogsPortletKeys.BLOGS_ADMIN);

		List<BlogsEntry> importedBlogsEntries = ListUtil.filter(
			BlogsEntryLocalServiceUtil.getGroupEntries(
				importedGroup.getGroupId(),
				new QueryDefinition<>(WorkflowConstants.STATUS_ANY)),
			blogsEntry -> Objects.equals(title, blogsEntry.getTitle()));

		Assert.assertEquals(
			importedBlogsEntries.toString(), 2, importedBlogsEntries.size());
	}

	@Override
	protected StagedModel addStagedModel(long groupId) throws Exception {
		return BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected StagedModel addStagedModel(long groupId, Date createdDate)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		serviceContext.setCreateDate(createdDate);
		serviceContext.setModifiedDate(createdDate);

		return BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);
	}

	@Override
	protected void deleteStagedModel(StagedModel stagedModel) throws Exception {
		BlogsEntryLocalServiceUtil.deleteEntry((BlogsEntry)stagedModel);
	}

	@Override
	protected Map<String, String[]> getExportParameterMap() throws Exception {
		Map<String, String[]> parameterMap = super.getExportParameterMap();

		addParameter(parameterMap, "entries", true);
		addParameter(parameterMap, "referenced-content", true);

		return parameterMap;
	}

	@Override
	protected Map<String, String[]> getImportParameterMap() throws Exception {
		Map<String, String[]> parameterMap = super.getImportParameterMap();

		addParameter(parameterMap, "entries", true);
		addParameter(parameterMap, "referenced-content", true);

		return parameterMap;
	}

	@Override
	protected StagedModel getStagedModel(String uuid, long groupId)
		throws PortalException {

		return BlogsEntryLocalServiceUtil.getBlogsEntryByUuidAndGroupId(
			uuid, groupId);
	}

}