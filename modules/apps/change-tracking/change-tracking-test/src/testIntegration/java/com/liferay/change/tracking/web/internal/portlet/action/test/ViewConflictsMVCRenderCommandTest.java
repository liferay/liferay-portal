/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.RenderResponse;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Noor Najjar
 */
@RunWith(Arquillian.class)
public class ViewConflictsMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-115353")
	public void testRender() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(
				ctCollection.getCtCollectionId(),
				"https://" + RandomTestUtil.randomString() + ".com");

		Assert.assertEquals(
			"/publications/view_conflicts.jsp",
			_mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse()));

		String viewChangesURL = PortletURLBuilder.createRenderURL(
			(RenderResponse)new MockLiferayPortletRenderResponse()
		).setMVCRenderCommandName(
			"/change_tracking/view_changes"
		).setParameter(
			"ctCollectionId", ctCollection.getCtCollectionId()
		).buildString();

		Assert.assertEquals(
			viewChangesURL,
			ReflectionTestUtil.invoke(
				mockLiferayPortletRenderRequest.getAttribute(
					"VIEW_CONFLICTS_DISPLAY_CONTEXT"),
				"getRedirect", new Class<?>[0]));

		ctCollection.setStatus(WorkflowConstants.STATUS_APPROVED);

		ctCollection = _ctCollectionLocalService.updateCTCollection(
			ctCollection);

		_testRender(
			ctCollection.getCtCollectionId(), viewChangesURL,
			"https://" + RandomTestUtil.randomString() + ".com");
		_testRender(ctCollection.getCtCollectionId(), viewChangesURL, null);

		String redirect = "/" + RandomTestUtil.randomString();

		_testRender(ctCollection.getCtCollectionId(), redirect, redirect);

		ServiceContextThreadLocal.popServiceContext();
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			long ctCollectionId, String redirect)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);

		mockLiferayPortletRenderRequest.addParameter(
			"ctCollectionId", String.valueOf(ctCollectionId));

		if (redirect != null) {
			mockLiferayPortletRenderRequest.addParameter("redirect", redirect);
		}

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletRenderRequest;
	}

	private void _testRender(
			long ctCollectionId, String expectedRedirectedURL, String redirect)
		throws Exception {

		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		String mvcPath = _mvcRenderCommand.render(
			_getMockLiferayPortletRenderRequest(ctCollectionId, redirect),
			mockLiferayPortletRenderResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletRenderResponse.getHttpServletResponse();

		Assert.assertEquals(
			expectedRedirectedURL, mockHttpServletResponse.getRedirectedUrl());

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH, mvcPath);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject(
		filter = "mvc.command.name=/change_tracking/view_conflicts",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

}