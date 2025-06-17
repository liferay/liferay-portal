/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.translation.constants.TranslationPortletKeys;
import com.liferay.translation.service.TranslationEntryLocalService;
import com.liferay.translation.test.util.TranslationTestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class TranslateMVCRenderCommandTest {

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
	public void testGetInfoFieldSetEntriesData() throws Exception {
		JournalArticle journalArticle = _addJournalArticle();

		_translationEntryLocalService.addOrUpdateTranslationEntry(
			_group.getGroupId(), JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey(),
			StringUtil.replace(
				TranslationTestUtil.readFileToString(
					"test-journal-article-with-repeatable-text-field.xlf"),
				"[$JOURNAL_ARTICLE_ID$]",
				String.valueOf(journalArticle.getResourcePrimKey())),
			"application/xliff+xml", LocaleUtil.toLanguageId(LocaleUtil.SPAIN),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequests(journalArticle);

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		Map<String, Object> infoFieldSetEntriesData = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.translation.web.internal.display.context." +
					"TranslateDisplayContext"),
			"getInfoFieldSetEntriesData", new Class<?>[0]);

		List<Object> infoFieldSetEntries =
			(List<Object>)infoFieldSetEntriesData.get("infoFieldSetEntries");

		Assert.assertNotNull(infoFieldSetEntries);
		Assert.assertEquals(
			infoFieldSetEntries.toString(), 2, infoFieldSetEntries.size());

		Map<String, Object> structureFields =
			(Map<String, Object>)infoFieldSetEntries.get(1);

		Assert.assertNotNull(structureFields);
		Assert.assertEquals(
			"Content (Test Structure)", structureFields.get("legend"));

		List<Object> fields = (List<Object>)structureFields.get("fields");

		Assert.assertNotNull(fields);
		Assert.assertEquals(fields.toString(), 1, fields.size());

		Map<String, Object> fieldAttributes = (Map<String, Object>)fields.get(
			0);

		Assert.assertNotNull(fieldAttributes);
		Assert.assertEquals(
			"infoField--DDMStructure_text--", fieldAttributes.get("id"));
		Assert.assertEquals(
			Arrays.asList("content 1 EN", "content 2 EN"),
			fieldAttributes.get("sourceContent"));
		Assert.assertEquals(
			Arrays.asList("content 1 ES", "content 2 ES"),
			fieldAttributes.get("targetContent"));
		Assert.assertEquals(
			LocaleUtil.SPAIN.toString(),
			fieldAttributes.get("targetLanguageId"));
	}

	private JournalArticle _addJournalArticle() throws Exception {
		String content = new String(
			FileUtil.getBytes(
				getClass(),
				"dependencies/test-journal-content-with-repeatable-text-" +
					"field.xml"));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName(), 0,
			DDMStructureTestUtil.getSampleDDMForm(
				"text", "string", "text", true, DDMFormFieldTypeConstants.TEXT,
				new Locale[] {LocaleUtil.getSiteDefault()},
				LocaleUtil.getSiteDefault()),
			LocaleUtil.getSiteDefault(), serviceContext);

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), _portal.getClassNameId(DDMStructure.class),
			ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));

		return _journalArticleLocalService.addArticle(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			Collections.singletonMap(LocaleUtil.US, "title example"), null,
			content, ddmStructure.getStructureId(),
			ddmTemplate.getTemplateKey(), serviceContext);
	}

	private MockLiferayPortletRenderRequest
			_getMockLiferayPortletRenderRequests(JournalArticle journalArticle)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			mockLiferayPortletRenderRequest);

		mockLiferayPortletRenderRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);
		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(mockHttpServletRequest));

		mockHttpServletRequest.setParameter(
			"classNameId",
			String.valueOf(_portal.getClassNameId(JournalArticle.class)));
		mockHttpServletRequest.setParameter(
			"classPK", String.valueOf(journalArticle.getResourcePrimKey()));
		mockHttpServletRequest.setParameter(
			"groupId", String.valueOf(journalArticle.getGroupId()));

		mockLiferayPortletRenderRequest.setParameter(
			"sourceLanguageId", LocaleUtil.US.toString());
		mockLiferayPortletRenderRequest.setParameter(
			"targetLanguageId", LocaleUtil.SPAIN.toString());

		return mockLiferayPortletRenderRequest;
	}

	private ThemeDisplay _getThemeDisplay(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		themeDisplay.setClayCSSURL("http://test.com");
		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		themeDisplay.setLayout(layout);

		LayoutSet layoutSet = layout.getLayoutSet();

		themeDisplay.setLayoutSet(layoutSet);

		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());
		themeDisplay.setMainCSSURL("http://test.com");
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setPpid(TranslationPortletKeys.TRANSLATION);
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject(
		filter = "mvc.command.name=/translation/translate",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private Portal _portal;

	@Inject
	private TranslationEntryLocalService _translationEntryLocalService;

}