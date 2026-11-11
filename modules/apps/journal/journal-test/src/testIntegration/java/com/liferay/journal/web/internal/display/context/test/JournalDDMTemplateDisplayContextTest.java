/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.lang.reflect.Constructor;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class JournalDDMTemplateDisplayContextTest {

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
	public void testGetDDMTemplateSearchContainer() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		Group childGroup = GroupTestUtil.addGroup(_group.getGroupId());

		List<DDMTemplate> ddmTemplates = _getDDMTemplates(childGroup, true);

		Assert.assertTrue(ddmTemplates.contains(ddmTemplate));

		ddmTemplates = _getDDMTemplates(childGroup, false);

		Assert.assertFalse(ddmTemplates.contains(ddmTemplate));

		_groupLocalService.deleteGroup(childGroup);
	}

	private List<DDMTemplate> _getDDMTemplates(
			Group group, boolean showAncestorScopesByDefault)
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.journal.web.internal.configuration." +
						"JournalWebConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"showAncestorScopesByDefault",
						showAncestorScopesByDefault
					).build())) {

			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
				new MockLiferayPortletRenderRequest();

			String path = "/view_ddm_templates.jsp";

			mockLiferayPortletRenderRequest.setAttribute(
				MVCRenderConstants.
					PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX +
						path,
				new MockLiferayPortletContext(path));

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setCompany(
				_companyLocalService.getCompany(group.getCompanyId()));
			themeDisplay.setLocale(LocaleUtil.getDefault());
			themeDisplay.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
			themeDisplay.setScopeGroupId(group.getGroupId());
			themeDisplay.setUser(TestPropsValues.getUser());

			mockLiferayPortletRenderRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			mockLiferayPortletRenderRequest.setParameter("mvcPath", path);

			MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
				new MockLiferayPortletRenderResponse();

			MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

			mvcPortlet.render(
				mockLiferayPortletRenderRequest,
				mockLiferayPortletRenderResponse);

			Class<?> clazz = _portlet.getClass();

			ClassLoader classLoader = clazz.getClassLoader();

			Class<?> displayContextClass = classLoader.loadClass(
				"com.liferay.journal.web.internal.display.context." +
					"JournalDDMTemplateDisplayContext");

			Constructor<?> constructor = displayContextClass.getConstructor(
				RenderRequest.class, RenderResponse.class);

			SearchContainer<DDMTemplate> searchContainer =
				ReflectionTestUtil.invoke(
					constructor.newInstance(
						mockLiferayPortletRenderRequest,
						mockLiferayPortletRenderResponse),
					"getDDMTemplateSearchContainer", new Class<?>[0]);

			return searchContainer.getResults();
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.portlet.JournalPortlet"
	)
	private Portlet _portlet;

}