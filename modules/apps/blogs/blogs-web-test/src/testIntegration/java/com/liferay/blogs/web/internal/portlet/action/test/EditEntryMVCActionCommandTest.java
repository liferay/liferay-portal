/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.exception.NoSuchEntryException;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.friendly.url.exception.FriendlyURLLocalizationUrlTitleException;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
@Sync
public class EditEntryMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@Test(expected = NoSuchEntryException.class)
	public void testDeleteEntries() throws Exception {
		BlogsEntry blogsEntry = _addBlogEntry(RandomTestUtil.randomString());

		_deleteEntries(
			_getMockLiferayPortletActionRequest(blogsEntry.getEntryId()),
			false);

		_blogsEntryService.getEntry(blogsEntry.getEntryId());
	}

	@Test
	public void testDeleteEntriesToTrash() throws Exception {
		BlogsEntry blogsEntry = _addBlogEntry(RandomTestUtil.randomString());

		_deleteEntries(
			_getMockLiferayPortletActionRequest(blogsEntry.getEntryId()), true);

		Assert.assertNotNull(
			_blogsEntryService.getEntry(blogsEntry.getEntryId()));

		List<TrashEntry> trashEntries = _trashEntryLocalService.getEntries(
			_group.getGroupId());

		Assert.assertFalse(
			"There are not trash elements on the recycle bin",
			trashEntries.isEmpty());
	}

	@Test
	public void testUpdateEntry() throws Exception {
		BlogsEntry blogsEntry = _addBlogEntry(RandomTestUtil.randomString());

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(blogsEntry.getDisplayDate());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				HashMapBuilder.put(
					Constants.CMD, Constants.UPDATE
				).put(
					"content", blogsEntry.getContent()
				).put(
					"displayDateAmPm",
					String.valueOf(calendar.get(Calendar.AM_PM))
				).put(
					"displayDateDay",
					String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))
				).put(
					"displayDateHour",
					String.valueOf(calendar.get(Calendar.HOUR))
				).put(
					"displayDateMinute",
					String.valueOf(calendar.get(Calendar.MINUTE))
				).put(
					"displayDateMonth",
					String.valueOf(calendar.get(Calendar.MONTH))
				).put(
					"displayDateYear",
					String.valueOf(calendar.get(Calendar.YEAR))
				).put(
					"entryId", String.valueOf(blogsEntry.getEntryId())
				).put(
					"urlTitle", StringUtil.randomString() + StringPool.SLASH
				).build());

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertNotNull(
			SessionErrors.get(
				mockLiferayPortletActionRequest,
				FriendlyURLLocalizationUrlTitleException.
					MustNotHaveTrailingSlash.class));
	}

	private BlogsEntry _addBlogEntry(String title) throws Exception {
		return _blogsEntryService.addEntry(
			title, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), 1, 1, 1990, 1, 1, true, false,
			new String[0], RandomTestUtil.randomString(), null, null,
			_serviceContext);
	}

	private void _deleteEntries(
		ActionRequest actionRequest, boolean moveToTrash) {

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_deleteEntries",
			new Class<?>[] {ActionRequest.class, boolean.class}, actionRequest,
			moveToTrash);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			long entryId)
		throws Exception {

		return _getMockLiferayPortletActionRequest(
			HashMapBuilder.put(
				"entryId", String.valueOf(entryId)
			).build());
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			Map<String, String> parameters)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			mockLiferayPortletActionRequest.addParameter(
				entry.getKey(), new String[] {entry.getValue()});
		}

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));

		themeDisplay.setRequest(
			mockLiferayPortletActionRequest.getHttpServletRequest());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private BlogsEntryService _blogsEntryService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.blogs.web.internal.portlet.action.EditEntryMVCActionCommand"
	)
	private MVCActionCommand _mvcActionCommand;

	private ServiceContext _serviceContext;

	@Inject
	private TrashEntryLocalService _trashEntryLocalService;

}