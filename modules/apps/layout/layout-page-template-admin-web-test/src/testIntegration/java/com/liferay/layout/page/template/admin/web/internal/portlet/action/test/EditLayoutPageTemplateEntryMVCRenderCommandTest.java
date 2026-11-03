/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Javier Moral
 */
@FeatureFlag("LPD-57283")
@RunWith(Arquillian.class)
public class EditLayoutPageTemplateEntryMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo({"LPD-106071", "LPD-108813"})
	public void testRender() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		String name = RandomTestUtil.randomString();

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(LocaleUtil.getDefault(), name), null,
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		_testRenderRedirectsToDraftLayoutEditor(
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				depotEntry.getGroupId()),
			name);
		_testRenderRedirectsToDraftLayoutEditor(
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				depotEntry.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_DRAFT),
			name);
		_testRenderWithUnknownExternalReferenceCode(depotEntry.getGroupId());
		_testRenderWithoutExternalReferenceCode(depotEntry.getGroupId());
	}

	private String _getRedirect(
		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse) {

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletRenderResponse.getHttpServletResponse();

		return mockHttpServletResponse.getRedirectedUrl();
	}

	private ThemeDisplay _getThemeDisplay(long groupId) throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			company, _groupLocalService.getGroup(TestPropsValues.getGroupId()),
			_layoutLocalService.getLayout(
				_portal.getControlPanelPlid(company.getCompanyId())));

		themeDisplay.setScopeGroupId(groupId);

		return themeDisplay;
	}

	private String _render(
			String externalReferenceCode, long groupId,
			MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse,
			String redirect)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(groupId));
		mockLiferayPortletRenderRequest.setParameter(
			"layoutPageTemplateEntryExternalReferenceCode",
			externalReferenceCode);
		mockLiferayPortletRenderRequest.setParameter("redirect", redirect);

		return _mvcRenderCommand.render(
			mockLiferayPortletRenderRequest, mockLiferayPortletRenderResponse);
	}

	private void _testRenderRedirectsToDraftLayoutEditor(
			LayoutPageTemplateEntry layoutPageTemplateEntry, String name)
		throws Exception {

		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();
		String backURL = "/" + RandomTestUtil.randomString();

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH,
			_render(
				layoutPageTemplateEntry.getExternalReferenceCode(),
				layoutPageTemplateEntry.getGroupId(),
				mockLiferayPortletRenderResponse, backURL));

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			layoutPageTemplateEntry.getPlid());
		String redirect = _getRedirect(mockLiferayPortletRenderResponse);

		Assert.assertTrue(
			redirect.startsWith(
				_portal.getLayoutFullURL(
					draftLayout,
					_getThemeDisplay(layoutPageTemplateEntry.getGroupId()))));
		Assert.assertEquals(
			backURL,
			HttpComponentsUtil.decodeURL(
				HttpComponentsUtil.getParameter(
					redirect, "p_l_back_url", false)));
		Assert.assertEquals(
			name,
			HttpComponentsUtil.decodeURL(
				HttpComponentsUtil.getParameter(
					redirect, "p_l_back_url_title", false)));
		Assert.assertEquals(
			Constants.EDIT,
			HttpComponentsUtil.getParameter(redirect, "p_l_mode", false));
	}

	private void _testRenderWithUnknownExternalReferenceCode(long groupId)
		throws Exception {

		Assert.assertEquals(
			"/view.jsp",
			_render(
				RandomTestUtil.randomString(), groupId,
				new MockLiferayPortletRenderResponse(), StringPool.BLANK));
	}

	private void _testRenderWithoutExternalReferenceCode(long groupId)
		throws Exception {

		Assert.assertEquals(
			"/view.jsp",
			_render(
				StringPool.BLANK, groupId,
				new MockLiferayPortletRenderResponse(), StringPool.BLANK));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_page_template_admin/edit_layout_page_template_entry"
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private Portal _portal;

}