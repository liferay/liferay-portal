/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.asset.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.portlet.AssetDisplayPageEntryFormProcessor;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderParameters;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.Cookie;

import java.security.Principal;

import java.util.Collections;
import java.util.Enumeration;
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
 * @author Alejandro Tardín
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class AssetDisplayPageFormProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testProcessUpdateAssetDisplayPageFromSpecificToDefaultExisting()
		throws Exception {

		long classNameId = _portal.getClassNameId(FileEntry.class.getName());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, 0, true,
				WorkflowConstants.STATUS_APPROVED);

		_withAndWithoutAssetEntry(
			fileEntry -> {
				_assetDisplayPageEntryFormProcessor.process(
					FileEntry.class.getName(), fileEntry.getFileEntryId(),
					new MockPortletRequest(
						String.valueOf(AssetDisplayPageConstants.TYPE_SPECIFIC),
						String.valueOf(RandomTestUtil.randomLong())));

				_assetDisplayPageEntryFormProcessor.process(
					FileEntry.class.getName(), fileEntry.getFileEntryId(),
					new MockPortletRequest(
						String.valueOf(AssetDisplayPageConstants.TYPE_DEFAULT),
						String.valueOf(
							layoutPageTemplateEntry.
								getLayoutPageTemplateEntryId())));

				Assert.assertNull(
					_assetDisplayPageEntryLocalService.
						fetchAssetDisplayPageEntry(
							_group.getGroupId(), classNameId,
							fileEntry.getFileEntryId()));

				Assert.assertTrue(
					AssetDisplayPageUtil.hasAssetDisplayPage(
						fileEntry.getGroupId(), classNameId,
						fileEntry.getFileEntryId(), 0));
			});
	}

	@Test
	public void testProcessUpdateAssetDisplayPageFromSpecificToDefaultNonexisting()
		throws Exception {

		_withAndWithoutAssetEntry(
			fileEntry -> {
				_assetDisplayPageEntryFormProcessor.process(
					FileEntry.class.getName(), fileEntry.getFileEntryId(),
					new MockPortletRequest(
						String.valueOf(AssetDisplayPageConstants.TYPE_SPECIFIC),
						String.valueOf(RandomTestUtil.randomLong())));

				_assetDisplayPageEntryFormProcessor.process(
					FileEntry.class.getName(), fileEntry.getFileEntryId(),
					new MockPortletRequest(
						String.valueOf(AssetDisplayPageConstants.TYPE_DEFAULT),
						null));

				Assert.assertNull(
					_assetDisplayPageEntryLocalService.
						fetchAssetDisplayPageEntry(
							_group.getGroupId(),
							_portal.getClassNameId(FileEntry.class.getName()),
							fileEntry.getFileEntryId()));
			});
	}

	@Test
	public void testProcessUpdateAssetDisplayPageFromSpecificToNone()
		throws Exception {

		_withAndWithoutAssetEntry(
			fileEntry -> {
				_assetDisplayPageEntryFormProcessor.process(
					FileEntry.class.getName(), fileEntry.getFileEntryId(),
					new MockPortletRequest(
						String.valueOf(AssetDisplayPageConstants.TYPE_SPECIFIC),
						String.valueOf(RandomTestUtil.randomLong())));

				_assetDisplayPageEntryFormProcessor.process(
					FileEntry.class.getName(), fileEntry.getFileEntryId(),
					new MockPortletRequest(
						String.valueOf(AssetDisplayPageConstants.TYPE_NONE),
						null));

				Assert.assertNotNull(
					_assetDisplayPageEntryLocalService.
						fetchAssetDisplayPageEntry(
							_group.getGroupId(),
							_portal.getClassNameId(FileEntry.class.getName()),
							fileEntry.getFileEntryId()));
			});
	}

	@Test
	public void testProcessWithDefaultAssetDisplayPage() throws Exception {
		long classNameId = _portal.getClassNameId(FileEntry.class.getName());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, 0, true,
				WorkflowConstants.STATUS_APPROVED);

		_withAndWithoutAssetEntry(
			fileEntry -> {
				_assetDisplayPageEntryFormProcessor.process(
					FileEntry.class.getName(), fileEntry.getFileEntryId(),
					new MockPortletRequest(
						String.valueOf(AssetDisplayPageConstants.TYPE_DEFAULT),
						String.valueOf(
							layoutPageTemplateEntry.
								getLayoutPageTemplateEntryId())));

				Assert.assertNull(
					_assetDisplayPageEntryLocalService.
						fetchAssetDisplayPageEntry(
							_group.getGroupId(), classNameId,
							fileEntry.getFileEntryId()));
				Assert.assertTrue(
					AssetDisplayPageUtil.hasAssetDisplayPage(
						fileEntry.getGroupId(), classNameId,
						fileEntry.getFileEntryId(), 0));
			});
	}

	@Test
	public void testProcessWithDefaultNonexistingAssetDisplayPage()
		throws Exception {

		long classNameId = _portal.getClassNameId(FileEntry.class.getName());

		long defaultAssetDisplayPageEntryId = 0;

		_withAndWithoutAssetEntry(
			fileEntry -> {
				_assetDisplayPageEntryFormProcessor.process(
					FileEntry.class.getName(), fileEntry.getFileEntryId(),
					new MockPortletRequest(
						String.valueOf(AssetDisplayPageConstants.TYPE_DEFAULT),
						String.valueOf(defaultAssetDisplayPageEntryId)));

				Assert.assertNull(
					_assetDisplayPageEntryLocalService.
						fetchAssetDisplayPageEntry(
							_group.getGroupId(), classNameId,
							fileEntry.getFileEntryId()));
			});
	}

	@Test
	public void testProcessWithDefaultParameters() throws Exception {
		long classNameId = _portal.getClassNameId(FileEntry.class.getName());

		_withAndWithoutAssetEntry(
			fileEntry -> {
				_assetDisplayPageEntryFormProcessor.process(
					FileEntry.class.getName(), fileEntry.getFileEntryId(),
					new MockPortletRequest(null, null));

				Assert.assertNull(
					_assetDisplayPageEntryLocalService.
						fetchAssetDisplayPageEntry(
							_group.getGroupId(), classNameId,
							fileEntry.getFileEntryId()));
			});
	}

	@Test
	public void testProcessWithNoAssetDisplayPage() throws Exception {
		_withAndWithoutAssetEntry(
			fileEntry -> {
				_assetDisplayPageEntryFormProcessor.process(
					FileEntry.class.getName(), fileEntry.getFileEntryId(),
					new MockPortletRequest(
						String.valueOf(AssetDisplayPageConstants.TYPE_NONE),
						null));

				Assert.assertNotNull(
					_assetDisplayPageEntryLocalService.
						fetchAssetDisplayPageEntry(
							_group.getGroupId(),
							_portal.getClassNameId(FileEntry.class.getName()),
							fileEntry.getFileEntryId()));
			});
	}

	@Test
	public void testProcessWithSpecificAssetDisplayPage() throws Exception {
		_withAndWithoutAssetEntry(
			fileEntry -> {
				_assetDisplayPageEntryFormProcessor.process(
					FileEntry.class.getName(), fileEntry.getFileEntryId(),
					new MockPortletRequest(
						String.valueOf(AssetDisplayPageConstants.TYPE_SPECIFIC),
						String.valueOf(RandomTestUtil.randomLong())));

				Assert.assertNotNull(
					_assetDisplayPageEntryLocalService.
						fetchAssetDisplayPageEntry(
							_group.getGroupId(),
							_portal.getClassNameId(FileEntry.class.getName()),
							fileEntry.getFileEntryId()));
			});
	}

	private ThemeDisplay _getThemeDisplay() throws PortalException {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _withAndWithoutAssetEntry(
			UnsafeConsumer<FileEntry, PortalException> testFunction)
		throws Exception {

		DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		testFunction.accept(
			_dlAppLocalService.getFileEntry(dlFileEntry.getFileEntryId()));

		dlFileEntry = DLTestUtil.addDLFileEntry(dlFolder.getFolderId());

		AssetEntryLocalServiceUtil.deleteEntry(
			FileEntry.class.getName(), dlFileEntry.getFileEntryId());

		testFunction.accept(
			_dlAppLocalService.getFileEntry(dlFileEntry.getFileEntryId()));
	}

	@Inject
	private AssetDisplayPageEntryFormProcessor
		_assetDisplayPageEntryFormProcessor;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	private class MockPortletRequest implements PortletRequest {

		public MockPortletRequest(
				String displayPageType, String assetDisplayPageId)
			throws PortalException {

			_attributes = HashMapBuilder.<String, Object>put(
				PortletServlet.PORTLET_SERVLET_REQUEST,
				new MockHttpServletRequest()
			).put(
				WebKeys.CURRENT_URL, RandomTestUtil.randomString()
			).put(
				WebKeys.THEME_DISPLAY, _getThemeDisplay()
			).build();

			_parameters = HashMapBuilder.put(
				Constants.CMD, new String[] {RandomTestUtil.randomString()}
			).put(
				"assetDisplayPageId", new String[] {assetDisplayPageId}
			).put(
				"assetEntryVisible", new String[] {String.valueOf(Boolean.TRUE)}
			).put(
				"displayPageType", new String[] {displayPageType}
			).put(
				"formDate",
				new String[] {String.valueOf(RandomTestUtil.randomLong())}
			).build();
		}

		@Override
		public Object getAttribute(String name) {
			return _attributes.get(name);
		}

		@Override
		public Enumeration<String> getAttributeNames() {
			return Collections.enumeration(_attributes.keySet());
		}

		@Override
		public String getAuthType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getContextPath() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Cookie[] getCookies() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Locale getLocale() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Enumeration<Locale> getLocales() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getParameter(String name) {
			String[] parameters = _parameters.get(name);

			if (ArrayUtil.isEmpty(parameters)) {
				return null;
			}

			if (parameters.length > 1) {
				throw new AssertionError(
					StringBundler.concat(
						"Unexpected value for: ", name, " values: ",
						parameters));
			}

			return parameters[0];
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return _parameters;
		}

		@Override
		public Enumeration<String> getParameterNames() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String[] getParameterValues(String name) {
			return _parameters.get(name);
		}

		@Override
		public PortalContext getPortalContext() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PortletContext getPortletContext() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PortletMode getPortletMode() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PortletSession getPortletSession() {
			throw new UnsupportedOperationException();
		}

		@Override
		public PortletSession getPortletSession(boolean create) {
			throw new UnsupportedOperationException();
		}

		@Override
		public PortletPreferences getPreferences() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Map<String, String[]> getPrivateParameterMap() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Enumeration<String> getProperties(String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getProperty(String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Enumeration<String> getPropertyNames() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Map<String, String[]> getPublicParameterMap() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getRemoteUser() {
			throw new UnsupportedOperationException();
		}

		@Override
		public RenderParameters getRenderParameters() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getRequestedSessionId() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getResponseContentType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Enumeration<String> getResponseContentTypes() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getScheme() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getServerName() {
			return RandomTestUtil.randomString();
		}

		@Override
		public int getServerPort() {
			return RandomTestUtil.randomInt();
		}

		@Override
		public String getUserAgent() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Principal getUserPrincipal() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getWindowID() {
			throw new UnsupportedOperationException();
		}

		@Override
		public WindowState getWindowState() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isPortletModeAllowed(PortletMode portletMode) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isRequestedSessionIdValid() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isSecure() {
			return false;
		}

		@Override
		public boolean isUserInRole(String role) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isWindowStateAllowed(WindowState windowState) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeAttribute(String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setAttribute(String name, Object value) {
			throw new UnsupportedOperationException();
		}

		private final Map<String, Object> _attributes;
		private final Map<String, String[]> _parameters;

	}

}