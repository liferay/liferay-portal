/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.taglib.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.taglib.servlet.taglib.JournalArticleTag;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.portlet.MockPortletResponse;
import com.liferay.portal.kernel.test.portlet.MockRenderRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;

/**
 * @author Akhash Ramprakash
 */
@RunWith(Arquillian.class)
public class JournalArticleTagTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@Test
	public void testDoStartTag() throws Exception {
		JournalArticle journalArticle = _addJournalArticle(true);

		JournalArticleDisplay journalArticleDisplay1 =
			_getJournalArticleDisplay(
				journalArticle, PermissionThreadLocal.getPermissionChecker());
		JournalArticleDisplay journalArticleDisplay2 =
			_getJournalArticleDisplay(
				journalArticle, PermissionThreadLocal.getPermissionChecker());

		Assert.assertEquals(
			journalArticleDisplay1.getContent(),
			journalArticleDisplay2.getContent());

		journalArticle.setStatus(WorkflowConstants.STATUS_DRAFT);

		journalArticleDisplay1 = _getJournalArticleDisplay(
			journalArticle, PermissionThreadLocal.getPermissionChecker());
		journalArticleDisplay2 = _getJournalArticleDisplay(
			journalArticle, PermissionThreadLocal.getPermissionChecker());

		Assert.assertNotEquals(
			journalArticleDisplay1.getContent(),
			journalArticleDisplay2.getContent());

		journalArticle = _addJournalArticle(false);

		journalArticleDisplay1 = _getJournalArticleDisplay(
			journalArticle, PermissionThreadLocal.getPermissionChecker());
		journalArticleDisplay2 = _getJournalArticleDisplay(
			journalArticle, PermissionThreadLocal.getPermissionChecker());

		Assert.assertNotEquals(
			journalArticleDisplay1.getContent(),
			journalArticleDisplay2.getContent());
	}

	@Test
	public void testDoStartTagWithoutViewPermission() throws Exception {
		JournalArticle journalArticle = _addJournalArticle(true);

		JournalArticleDisplay journalArticleDisplay1 =
			_getJournalArticleDisplay(
				journalArticle, PermissionThreadLocal.getPermissionChecker());

		for (String roleName :
				new String[] {RoleConstants.GUEST, RoleConstants.SITE_MEMBER}) {

			RoleTestUtil.removeResourcePermission(
				roleName, JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(journalArticle.getResourcePrimKey()),
				ActionKeys.VIEW);
		}

		JournalArticleDisplay journalArticleDisplay2 =
			_getJournalArticleDisplay(
				journalArticle,
				PermissionCheckerFactoryUtil.create(
					_userLocalService.getGuestUser(_group.getCompanyId())));

		Assert.assertNotEquals(
			journalArticleDisplay1.getContent(),
			journalArticleDisplay2.getContent());
	}

	private JournalArticle _addJournalArticle(boolean cacheable)
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL, "${randomNamespace}",
			LocaleUtil.getSiteDefault());

		ddmTemplate.setCacheable(cacheable);

		ddmTemplate = _ddmTemplateLocalService.updateDDMTemplate(ddmTemplate);

		return JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			DDMStructureTestUtil.getSampleStructuredContent(
				"content",
				Collections.singletonList(
					HashMapBuilder.put(
						LocaleUtil.US, RandomTestUtil.randomString()
					).build()),
				LocaleUtil.toLanguageId(LocaleUtil.US)),
			ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey(),
			LocaleUtil.getSiteDefault());
	}

	private JournalArticleDisplay _getJournalArticleDisplay(
			JournalArticle journalArticle, PermissionChecker permissionChecker)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST, new MockRenderRequest());
		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE, new MockPortletResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(mockHttpServletRequest, permissionChecker));

		JournalArticleTag journalArticleTag = new JournalArticleTag();

		journalArticleTag.setArticle(journalArticle);
		journalArticleTag.setLanguageId(journalArticle.getDefaultLanguageId());
		journalArticleTag.setPageContext(
			new MockPageContext(
				null, mockHttpServletRequest, new MockHttpServletResponse()));

		journalArticleTag.doStartTag();

		return (JournalArticleDisplay)mockHttpServletRequest.getAttribute(
			"liferay-journal:journal-article:articleDisplay");
	}

	private ThemeDisplay _getThemeDisplay(
			MockHttpServletRequest mockHttpServletRequest,
			PermissionChecker permissionChecker)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(
			_layoutSetLocalService.getLayoutSet(_group.getGroupId(), false));
		themeDisplay.setPermissionChecker(permissionChecker);
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setTimeZone(TimeZoneUtil.getDefault());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private UserLocalService _userLocalService;

}