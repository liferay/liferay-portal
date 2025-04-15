/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.annotations.PortletSerializable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
@Sync
public class DLAdminDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@Test
	public void testGetSearchContainer() throws Exception {
		for (int i = 0; i < 25; i++) {
			_addDLFileEntry("alpha_" + i + ".txt", "alpha");
		}

		SearchContainer<Object> searchContainer = _getSearchContainer(
			_getMockLiferayPortletActionRequest());

		Assert.assertEquals(25, searchContainer.getTotal());
	}

	@Test
	public void testGetSearchContainerWithSearch() throws Exception {
		for (int i = 0; i < 25; i++) {
			_addDLFileEntry("alpha_" + i + ".txt", "alpha");
		}

		SearchContainer<Object> searchContainer = _getSearchContainer(
			_getMockLiferayPortletActionRequestWithSearch("alpha"));

		Assert.assertEquals(25, searchContainer.getTotal());
	}

	private FileEntry _addDLFileEntry(String fileName, String content)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			content.getBytes(), null, null, null, serviceContext);
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_CONFIG, null);
		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST,
			mockLiferayPortletActionRequest);
		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE, new MockActionResponse());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletActionRequest;
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequestWithSearch(String keywords)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(
			"mvcRenderCommandName", "/document_library/search");
		mockLiferayPortletActionRequest.setParameter("keywords", keywords);

		return mockLiferayPortletActionRequest;
	}

	private SearchContainer<Object> _getSearchContainer(
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest) {

		Object dlAdminDisplayContext = ReflectionTestUtil.invoke(
			_dlAdminDisplayContextProvider, "getDLAdminDisplayContext",
			new Class<?>[] {
				HttpServletRequest.class, HttpServletResponse.class
			},
			mockLiferayPortletActionRequest.getHttpServletRequest(), null);

		return ReflectionTestUtil.invoke(
			dlAdminDisplayContext, "getSearchContainer", new Class<?>[0], null);
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_layout.getGroupId());
		themeDisplay.setSiteGroupId(_layout.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.display.context.DLAdminDisplayContextProvider",
		type = Inject.NoType.class
	)
	private Object _dlAdminDisplayContextProvider;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	private static class MockActionResponse
		extends MockLiferayPortletActionResponse {

		@Override
		public MockPortletURL createRenderURL() {
			return new MockPortletURL();
		}

	}

	private static class MockPortletURL implements PortletURL, RenderURL {

		@Override
		public void addProperty(String key, String value) {
		}

		@Override
		public Appendable append(Appendable appendable) {
			return null;
		}

		@Override
		public Appendable append(Appendable appendable, boolean escapeXML) {
			return null;
		}

		@Override
		public String getFragmentIdentifier() {
			return null;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return null;
		}

		@Override
		public PortletMode getPortletMode() {
			return null;
		}

		@Override
		public MutableRenderParameters getRenderParameters() {
			return null;
		}

		@Override
		public WindowState getWindowState() {
			return null;
		}

		@Override
		public void removePublicRenderParameter(String name) {
		}

		@Override
		public void setBeanParameter(PortletSerializable portletSerializable) {
		}

		@Override
		public void setFragmentIdentifier(String fragment) {
		}

		@Override
		public void setParameter(String name, String value) {
		}

		@Override
		public void setParameter(String name, String... values) {
		}

		@Override
		public void setParameters(Map<String, String[]> map) {
		}

		@Override
		public void setPortletMode(PortletMode portletMode) {
		}

		@Override
		public void setProperty(String key, String value) {
		}

		@Override
		public void setSecure(boolean secure) {
		}

		@Override
		public void setWindowState(WindowState windowState) {
		}

		@Override
		public void write(Writer writer) {
		}

		@Override
		public void write(Writer writer, boolean escapeXML) {
		}

	}

}