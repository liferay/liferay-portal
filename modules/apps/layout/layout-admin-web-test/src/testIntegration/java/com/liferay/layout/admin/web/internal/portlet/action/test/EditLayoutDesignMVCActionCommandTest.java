/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LayoutNameException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upload.test.util.UploadTestUtil;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockMultipartHttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class EditLayoutDesignMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPS-183218")
	public void testEditDisplayPageLayoutPageTemplateEntryDesignInLayoutSetPrototypeGroup()
		throws Exception {

		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		Group group = layoutSetPrototype.getGroup();

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			group.getGroupId(), _portal.getClassNameId(JournalArticle.class),
			"BASIC-WEB-CONTENT", true);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureId(), false,
				WorkflowConstants.STATUS_DRAFT);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		draftLayout = _layoutLocalService.getLayout(draftLayout.getPlid());

		Assert.assertTrue(Validator.isNull(draftLayout.getThemeId()));

		layout = _layoutLocalService.getLayout(layout.getPlid());

		Assert.assertTrue(Validator.isNull(layout.getThemeId()));

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());

		MockActionRequest mockActionRequest = _getMockActionRequest(
			draftLayout);

		mockActionRequest.setParameter(
			"regularThemeId", "dialect_WAR_dialecttheme");

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_updateLayout",
			new Class<?>[] {
				ActionRequest.class, ActionResponse.class,
				UploadPortletRequest.class
			},
			mockActionRequest, new MockActionResponse(),
			UploadTestUtil.createUploadPortletRequest(
				UploadTestUtil.createUploadServletRequest(
					mockMultipartHttpServletRequest, null, null),
				null, RandomTestUtil.randomString()));

		Assert.assertTrue(SessionErrors.isEmpty(mockActionRequest));

		draftLayout = _layoutLocalService.getLayout(draftLayout.getPlid());

		Assert.assertEquals(
			"dialect_WAR_dialecttheme", draftLayout.getThemeId());

		layout = _layoutLocalService.getLayout(layout.getPlid());

		Assert.assertTrue(Validator.isNull(layout.getThemeId()));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		layout = _layoutLocalService.getLayout(layout.getPlid());

		Assert.assertEquals("dialect_WAR_dialecttheme", layout.getThemeId());
	}

	@Test
	@TestInfo("LPS-121979")
	public void testEditLayoutDesignDoesntChangeWithNotEditedValues()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				StringPool.BLANK, RandomTestUtil.randomString(),
				StringPool.BLANK, RandomTestUtil.randomString(),
				serviceContext);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".jpg", ContentTypes.IMAGE_PNG,
			FileUtil.getBytes(getClass(), "dependencies/dxp_logo.png"), null,
			null, null, serviceContext);

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		draftLayout = _layoutLocalService.updateLayout(
			_group.getGroupId(), draftLayout.isPrivateLayout(),
			draftLayout.getLayoutId(), draftLayout.getParentLayoutId(),
			draftLayout.getNameMap(), draftLayout.getTitleMap(),
			draftLayout.getDescriptionMap(), draftLayout.getKeywordsMap(),
			draftLayout.getRobotsMap(), draftLayout.getType(),
			draftLayout.isHidden(), draftLayout.getFriendlyURLMap(), false,
			null, styleBookEntry.getStyleBookEntryId(),
			fileEntry.getFileEntryId(), masterLayoutPageTemplateEntry.getPlid(),
			serviceContext);

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_updateLayout",
			new Class<?>[] {
				ActionRequest.class, ActionResponse.class,
				UploadPortletRequest.class
			},
			_getMockActionRequest(draftLayout), new MockActionResponse(),
			UploadTestUtil.createUploadPortletRequest(
				UploadTestUtil.createUploadServletRequest(
					mockMultipartHttpServletRequest, null, null),
				null, RandomTestUtil.randomString()));

		Layout updatedDraftLayout = _layoutLocalService.fetchLayout(
			draftLayout.getPlid());

		Assert.assertEquals(
			draftLayout.getFaviconFileEntryId(),
			updatedDraftLayout.getFaviconFileEntryId());
		Assert.assertEquals(
			draftLayout.getMasterLayoutPlid(),
			updatedDraftLayout.getMasterLayoutPlid());
		Assert.assertEquals(
			draftLayout.getStyleBookEntryId(),
			updatedDraftLayout.getStyleBookEntryId());
	}

	@Test
	@TestInfo("LPP-56406")
	public void testEditLayoutDesignWithLayoutWithoutName() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		draftLayout.setName(StringPool.BLANK);

		draftLayout = _layoutLocalService.updateLayout(draftLayout);

		Assert.assertTrue(
			MapUtil.toString(draftLayout.getNameMap()),
			MapUtil.isEmpty(draftLayout.getNameMap()));

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());

		MockActionRequest mockActionRequest = _getMockActionRequest(
			draftLayout);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.layout.admin.web.internal.portlet.action." +
					"EditLayoutDesignMVCActionCommand",
				LoggerTestUtil.DEBUG)) {

			ReflectionTestUtil.invoke(
				_mvcActionCommand, "_updateLayout",
				new Class<?>[] {
					ActionRequest.class, ActionResponse.class,
					UploadPortletRequest.class
				},
				mockActionRequest, new MockActionResponse(),
				UploadTestUtil.createUploadPortletRequest(
					UploadTestUtil.createUploadServletRequest(
						mockMultipartHttpServletRequest, null, null),
					null, RandomTestUtil.randomString()));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());
		}

		Assert.assertFalse(SessionErrors.isEmpty(mockActionRequest));
		Assert.assertEquals(
			LayoutNameException.class.getName(),
			SessionErrors.get(mockActionRequest, LayoutNameException.class));

		draftLayout = _layoutLocalService.fetchLayout(draftLayout.getPlid());

		Assert.assertTrue(
			MapUtil.toString(draftLayout.getNameMap()),
			MapUtil.isEmpty(draftLayout.getNameMap()));
		Assert.assertFalse(
			GetterUtil.getBoolean(
				draftLayout.getTypeSettingsProperty(
					LayoutTypeSettingsConstants.
						KEY_DESIGN_CONFIGURATION_MODIFIED)));
	}

	@Test
	@TestInfo("LPP-56406")
	public void testEditLayoutPageTemplateEntryDesignWithLayoutWithoutName()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		draftLayout.setName(StringPool.BLANK);

		draftLayout = _layoutLocalService.updateLayout(draftLayout);

		Assert.assertTrue(
			MapUtil.toString(draftLayout.getNameMap()),
			MapUtil.isEmpty(draftLayout.getNameMap()));

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());

		MockActionRequest mockActionRequest = _getMockActionRequest(
			draftLayout);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_updateLayout",
			new Class<?>[] {
				ActionRequest.class, ActionResponse.class,
				UploadPortletRequest.class
			},
			mockActionRequest, new MockActionResponse(),
			UploadTestUtil.createUploadPortletRequest(
				UploadTestUtil.createUploadServletRequest(
					mockMultipartHttpServletRequest, null, null),
				null, RandomTestUtil.randomString()));

		Assert.assertTrue(SessionErrors.isEmpty(mockActionRequest));

		draftLayout = _layoutLocalService.getLayout(draftLayout.getPlid());

		Map<Locale, String> nameMap = draftLayout.getNameMap();

		Assert.assertEquals(MapUtil.toString(nameMap), 1, nameMap.size());

		Assert.assertEquals(
			layoutPageTemplateEntry.getName(),
			draftLayout.getName(_portal.getSiteDefaultLocale(_group)));

		Assert.assertTrue(
			GetterUtil.getBoolean(
				draftLayout.getTypeSettingsProperty(
					LayoutTypeSettingsConstants.
						KEY_DESIGN_CONFIGURATION_MODIFIED)));
	}

	private MockActionRequest _getMockActionRequest(Layout layout)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.setParameter(
			"groupId", String.valueOf(layout.getGroupId()));
		mockLiferayPortletActionRequest.setParameter(
			"liveGroupId", String.valueOf(layout.getGroupId()));
		mockLiferayPortletActionRequest.setParameter(
			"selPlid", String.valueOf(layout.getPlid()));

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject(filter = "mvc.command.name=/layout_admin/edit_layout_design")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}