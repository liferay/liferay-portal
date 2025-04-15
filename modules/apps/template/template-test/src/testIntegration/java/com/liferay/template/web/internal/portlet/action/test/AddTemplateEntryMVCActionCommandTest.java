/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.TemplateNameException;
import com.liferay.dynamic.data.mapping.exception.TemplateScriptException;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.service.TemplateEntryLocalService;
import com.liferay.template.test.util.TemplateTestUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
@Sync
public class AddTemplateEntryMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		_serviceContext.setCompanyId(TestPropsValues.getCompanyId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@Test
	public void testAddTemplateEntry() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		InfoItemClassDetails infoItemClassDetails =
			TemplateTestUtil.getFirstTemplateInfoItemClassDetails(
				_infoItemServiceRegistry, _group.getGroupId());

		String infoItemFormVariationKey = StringPool.BLANK;

		InfoItemFormVariation infoItemFormVariation =
			TemplateTestUtil.getFirstInfoItemFormVariation(
				infoItemClassDetails, _infoItemServiceRegistry,
				_group.getGroupId());

		if (infoItemFormVariation != null) {
			infoItemFormVariationKey = infoItemFormVariation.getKey();
		}

		mockLiferayPortletActionRequest.addParameter(
			"infoItemClassName", infoItemClassDetails.getClassName());
		mockLiferayPortletActionRequest.addParameter(
			"infoItemFormVariationKey", infoItemFormVariationKey);
		mockLiferayPortletActionRequest.addParameter(
			"name", RandomTestUtil.randomString());

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		List<TemplateEntry> templateEntries =
			_templateEntryLocalService.getTemplateEntries(
				_group.getGroupId(), infoItemClassDetails.getClassName(),
				infoItemFormVariationKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			templateEntries.toString(), 1, templateEntries.size());

		TemplateEntry templateEntry = templateEntries.get(0);

		DDMTemplate ddmTemplate = _ddmTemplateLocalService.getTemplate(
			templateEntry.getDDMTemplateId());

		Assert.assertNotNull(ddmTemplate);
	}

	@Test
	public void testHandleTemplateNameException() throws Exception {
		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "_getErrorJSONObject",
			new Class<?>[] {PortalException.class, ThemeDisplay.class},
			new TemplateNameException(), _getThemeDisplay());

		Assert.assertTrue(jsonObject.has("name"));
	}

	@Test
	public void testHandleTemplateScriptException() throws Exception {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "_getErrorJSONObject",
			new Class<?>[] {PortalException.class, ThemeDisplay.class},
			new TemplateScriptException(), themeDisplay);

		Assert.assertTrue(jsonObject.has("other"));

		Assert.assertEquals(
			LanguageUtil.get(
				themeDisplay.getLocale(), "please-enter-a-valid-script"),
			jsonObject.get("other"));
	}

	@Test
	public void testHandleUnexpectedException() throws Exception {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "_getErrorJSONObject",
			new Class<?>[] {PortalException.class, ThemeDisplay.class},
			new PortalException(), themeDisplay);

		Assert.assertTrue(jsonObject.has("other"));

		Assert.assertEquals(
			LanguageUtil.get(
				themeDisplay.getLocale(), "an-unexpected-error-occurred"),
			jsonObject.get("other"));
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject(filter = "mvc.command.name=/template/add_template_entry")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	@Inject
	private TemplateEntryLocalService _templateEntryLocalService;

}