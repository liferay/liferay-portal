/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brooke Dalton
 */
@RunWith(Arquillian.class)
public class ConvertEmptyLayoutMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(),
			TestPropsValues.getUserId());

		_serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testCannotConvertEmptyLayoutToEmbeddedLayout()
		throws Exception {

		Layout emptyLayout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			LayoutConstants.TYPE_EMPTY, true, StringPool.BLANK,
			_serviceContext);

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				emptyLayout, LayoutConstants.TYPE_EMBEDDED),
			new MockLiferayPortletActionResponse());

		Layout layout = _layoutLocalService.getLayout(emptyLayout.getPlid());

		Assert.assertTrue(layout.isTypeEmpty());
	}

	@Test
	public void testCannotConvertEmptyLayoutToTypeLinkToLayoutLayout()
		throws Exception {

		Layout emptyLayout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			LayoutConstants.TYPE_EMPTY, true, StringPool.BLANK,
			_serviceContext);

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				emptyLayout, LayoutConstants.TYPE_LINK_TO_LAYOUT),
			new MockLiferayPortletActionResponse());

		Layout layout = _layoutLocalService.getLayout(emptyLayout.getPlid());

		Assert.assertTrue(layout.isTypeEmpty());
	}

	@Test
	public void testConvertEmptyLayoutToContentLayout() throws Exception {
		Layout emptyLayout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			LayoutConstants.TYPE_EMPTY, true, StringPool.BLANK,
			_serviceContext);

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				emptyLayout, LayoutConstants.TYPE_CONTENT),
			new MockLiferayPortletActionResponse());

		Layout layout = _layoutLocalService.getLayout(emptyLayout.getPlid());

		Assert.assertFalse(layout.isPublished());
		Assert.assertTrue(layout.isTypeContent());
	}

	@Test
	public void testConvertEmptyLayoutToPortletLayout() throws Exception {
		Layout emptyLayout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			LayoutConstants.TYPE_EMPTY, true, StringPool.BLANK,
			_serviceContext);

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				emptyLayout, LayoutConstants.TYPE_PORTLET),
			new MockLiferayPortletActionResponse());

		Layout layout = _layoutLocalService.getLayout(emptyLayout.getPlid());

		Assert.assertTrue(layout.isTypePortlet());
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			Layout layout, String type)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(_group.getCompanyId()));
		themeDisplay.setLanguageId(_group.getDefaultLanguageId());
		themeDisplay.setLayout(layout);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRequest(
			mockLiferayPortletActionRequest.getHttpServletRequest());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.addParameter(
			"selPlid", String.valueOf(layout.getPlid()));
		mockLiferayPortletActionRequest.addParameter("type", type);
		mockLiferayPortletActionRequest.addParameter(
			"name", layout.getExternalReferenceCode());

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(filter = "mvc.command.name=/layout_admin/convert_empty_layout")
	private MVCActionCommand _mvcActionCommand;

	private ServiceContext _serviceContext;

}