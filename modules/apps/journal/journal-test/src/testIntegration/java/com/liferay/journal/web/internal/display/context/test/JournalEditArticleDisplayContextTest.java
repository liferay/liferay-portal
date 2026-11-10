/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;

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
 * @author Gergely Szalay
 */
@RunWith(Arquillian.class)
public class JournalEditArticleDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();
		_user = TestPropsValues.getUser();
	}

	@Test
	public void testGetDDMTemplate() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group1.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate1 = DDMTemplateTestUtil.addTemplate(
			_group1.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		DDMTemplate ddmTemplate2 = DDMTemplateTestUtil.addTemplate(
			_group1.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		Assert.assertEquals(
			ddmTemplate2,
			_getDDMTemplate(
				HashMapBuilder.put(
					"ddmStructureId",
					String.valueOf(ddmStructure.getStructureId())
				).build()));

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group1.getGroupId(),
				DDMStructureTestUtil.getSampleStructuredContent(),
				ddmStructure.getStructureKey(), ddmTemplate2.getTemplateKey());

		Assert.assertEquals(
			ddmTemplate2,
			_getDDMTemplate(
				HashMapBuilder.put(
					"articleId", journalArticle.getArticleId()
				).put(
					"resourcePrimKey",
					String.valueOf(journalArticle.getResourcePrimKey())
				).build()));

		journalArticle.setDDMTemplateKey(ddmTemplate1.getTemplateKey());

		journalArticle = JournalTestUtil.updateArticle(journalArticle);

		Assert.assertEquals(
			ddmTemplate1,
			_getDDMTemplate(
				HashMapBuilder.put(
					"articleId", journalArticle.getArticleId()
				).put(
					"resourcePrimKey",
					String.valueOf(journalArticle.getResourcePrimKey())
				).build()));
	}

	@Test
	@TestInfo("LPS-172561")
	public void testGetFriendlyURLDuplicatedWarningMessage() throws Exception {
		_assertGetFriendlyURLDuplicatedWarningMessage(
			StringPool.BLANK, _group2.getGroupId());
		_assertGetFriendlyURLDuplicatedWarningMessage(
			"The URL used in English (United States) already exists in other " +
				"sites or asset libraries.",
			_group2.getGroupId(), LocaleUtil.US);
		_assertGetFriendlyURLDuplicatedWarningMessage(
			"The URL used in English (United States), Chinese (China) and " +
				"Japanese (Japan) already exists in other sites or asset " +
					"libraries.",
			_group2.getGroupId(), LocaleUtil.CHINA, LocaleUtil.JAPAN,
			LocaleUtil.US);
		_assertGetFriendlyURLDuplicatedWarningMessage(
			"The URL used in English (United States), Chinese (China), " +
				"Japanese (Japan) and 1 more translations already exist in " +
					"other sites or asset libraries.",
			_group2.getGroupId(), LocaleUtil.CHINA, LocaleUtil.GERMANY,
			LocaleUtil.JAPAN, LocaleUtil.US);
		_assertGetFriendlyURLDuplicatedWarningMessage(
			"The URL used in English (United States), Chinese (China), " +
				"Spanish (Spain) and 2 more translations already exist in " +
					"other sites or asset libraries.",
			_group2.getGroupId(), LocaleUtil.CHINA, LocaleUtil.GERMANY,
			LocaleUtil.JAPAN, LocaleUtil.SPAIN, LocaleUtil.US);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_assertGetFriendlyURLDuplicatedWarningMessage(
			"The URL used in Spanish (Spain) already exists in other sites " +
				"or asset libraries.",
			_depotEntry.getGroupId(), LocaleUtil.SPAIN);
	}

	@FeatureFlag("LPD-11228")
	@Test
	public void testIsShowPublishModal() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group1.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");
		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group1.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);

		Object journalEditArticleDisplayContext =
			_getJournalEditArticleDisplayContext(
				HashMapBuilder.put(
					"articleId", journalArticle.getArticleId()
				).put(
					"resourcePrimKey",
					String.valueOf(journalArticle.getResourcePrimKey())
				).build());

		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				journalEditArticleDisplayContext, "_isShowPublishModal",
				new Class<?>[0]));

		journalArticle.setStatus(WorkflowConstants.STATUS_APPROVED);

		JournalTestUtil.updateArticle(
			journalArticle, RandomTestUtil.randomString());

		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				journalEditArticleDisplayContext, "_isShowPublishModal",
				new Class<?>[0]));
	}

	private JournalArticle _addArticle(
			long groupId, Map<Locale, String> friendlyURLMap)
		throws Exception {

		Map<Locale, String> titleMap = HashMapBuilder.put(
			LocaleUtil.CHINA, RandomTestUtil.randomString()
		).put(
			LocaleUtil.GERMANY, RandomTestUtil.randomString()
		).put(
			LocaleUtil.JAPAN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.SPAIN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		Map<Locale, String> contentMap = HashMapBuilder.put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		return JournalTestUtil.addArticle(
			null, groupId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, null, true, titleMap,
			contentMap, friendlyURLMap, contentMap, null, LocaleUtil.US, null,
			null, false, true,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private void _assertGetFriendlyURLDuplicatedWarningMessage(
			String expectedFriendlyURLDuplicatedWarningMessage, long groupId,
			Locale... duplicatedLocales)
		throws Exception {

		String friendlyURL = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		_addArticle(
			groupId,
			HashMapBuilder.put(
				LocaleUtil.CHINA, friendlyURL + "-zh"
			).put(
				LocaleUtil.GERMANY, friendlyURL + "-de"
			).put(
				LocaleUtil.JAPAN, friendlyURL + "-ja"
			).put(
				LocaleUtil.SPAIN, friendlyURL + "-es"
			).put(
				LocaleUtil.US, friendlyURL + "-en"
			).build());

		JournalArticle journalArticle = _addArticle(
			_group1.getGroupId(),
			HashMapBuilder.put(
				LocaleUtil.CHINA,
				_getFriendlyURL(
					duplicatedLocales, friendlyURL + "-zh", LocaleUtil.CHINA)
			).put(
				LocaleUtil.GERMANY,
				_getFriendlyURL(
					duplicatedLocales, friendlyURL + "-de", LocaleUtil.GERMANY)
			).put(
				LocaleUtil.JAPAN,
				_getFriendlyURL(
					duplicatedLocales, friendlyURL + "-ja", LocaleUtil.JAPAN)
			).put(
				LocaleUtil.SPAIN,
				_getFriendlyURL(
					duplicatedLocales, friendlyURL + "-es", LocaleUtil.SPAIN)
			).put(
				LocaleUtil.US,
				_getFriendlyURL(
					duplicatedLocales, friendlyURL + "-en", LocaleUtil.US)
			).build());

		Assert.assertEquals(
			expectedFriendlyURLDuplicatedWarningMessage,
			ReflectionTestUtil.invoke(
				_getJournalEditArticleDisplayContext(
					HashMapBuilder.put(
						"articleId", journalArticle.getArticleId()
					).put(
						"resourcePrimKey",
						String.valueOf(journalArticle.getResourcePrimKey())
					).build()),
				"getFriendlyURLDuplicatedWarningMessage", new Class<?>[0]));
	}

	private Object _getDDMTemplate(Map<String, String> parameters)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_getJournalEditArticleDisplayContext(parameters), "getDDMTemplate",
			new Class<?>[0]);
	}

	private String _getFriendlyURL(
		Locale[] duplicatedLocales, String duplicatedFriendlyURL,
		Locale locale) {

		if (ArrayUtil.contains(duplicatedLocales, locale)) {
			return duplicatedFriendlyURL;
		}

		return StringUtil.toLowerCase(RandomTestUtil.randomString());
	}

	private Object _getJournalEditArticleDisplayContext(
			Map<String, String> parameters)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"mvcRenderCommandName", "/journal/edit_article");
		mockHttpServletRequest.setParameters(parameters);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);

		mockHttpServletRequest.removeAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG);

		String path = "/edit_article.jsp";

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group1.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
		themeDisplay.setScopeGroupId(_group1.getGroupId());
		themeDisplay.setUser(_user);

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletRenderRequest.setParameter("mvcPath", path);

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest.getAttribute(
			"com.liferay.journal.web.internal.display.context." +
				"JournalEditArticleDisplayContext");
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.portlet.JournalPortlet"
	)
	private Portlet _portlet;

	private User _user;

}