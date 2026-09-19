/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class DeleteLayoutPageTemplateEntryPreviewMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.fetchGroup(TestPropsValues.getGroupId());
	}

	@Test
	public void testDeleteLayoutPageTemplateEntryPreviewWithPermissions()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId());

		Repository repository = _portletFileRepository.addPortletRepository(
			_group.getGroupId(),
			LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		FileEntry fileEntry = _portletFileRepository.addPortletFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			LayoutPageTemplateEntry.class.getName(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
			repository.getDlFolderId(), new byte[0], "test.png",
			ContentTypes.IMAGE_PNG, false);

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				fileEntry.getFileEntryId());

		ReflectionTestUtil.invoke(
			_deleteLayoutPageTemplateEntryPreviewMVCActionCommand,
			"doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				layoutPageTemplateEntry, TestPropsValues.getUser()),
			new MockLiferayPortletActionResponse());

		long previewFileEntryId =
			layoutPageTemplateEntry.getPreviewFileEntryId();

		Assert.assertThrows(
			NoSuchFileEntryException.class,
			() -> _portletFileRepository.getPortletFileEntry(
				previewFileEntryId));

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(0, layoutPageTemplateEntry.getPreviewFileEntryId());
	}

	@Test
	public void testDeleteLayoutPageTemplateEntryPreviewWithoutPermissions()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId());

		User user = UserTestUtil.addGroupUser(_group, RoleConstants.POWER_USER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> ReflectionTestUtil.invoke(
					_deleteLayoutPageTemplateEntryPreviewMVCActionCommand,
					"doProcessAction",
					new Class<?>[] {ActionRequest.class, ActionResponse.class},
					_getMockLiferayPortletActionRequest(
						layoutPageTemplateEntry, user),
					new MockLiferayPortletActionResponse()));
		}
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			LayoutPageTemplateEntry layoutPageTemplateEntry, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay();

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setUser(user);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.setParameter(
			"layoutPageTemplateEntryId",
			String.valueOf(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Layout controlPanelLayout = LayoutLocalServiceUtil.getLayout(
			PortalUtil.getControlPanelPlid(_group.getCompanyId()));

		themeDisplay.setLayout(controlPanelLayout);

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(layoutSet.getTheme(), null);

		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());

		return themeDisplay;
	}

	@Inject(
		filter = "mvc.command.name=/layout_page_template_admin/delete_layout_page_template_entry_preview"
	)
	private MVCActionCommand
		_deleteLayoutPageTemplateEntryPreviewMVCActionCommand;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private PortletFileRepository _portletFileRepository;

}