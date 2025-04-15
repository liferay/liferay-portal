/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.PortletSecurityException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;
import jakarta.portlet.annotations.PortletSerializable;

import java.io.IOException;
import java.io.Writer;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Yang Cao
 */
@RunWith(Arquillian.class)
@Sync
public class AddAssetListEntryMVCActionCommandTest {

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
	}

	@Test
	public void testAddDynamicAssetListEntry() throws Exception {
		String title = "Dynamic Asset List Title";

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter("title", title);
		mockLiferayPortletActionRequest.addParameter("type", String.valueOf(0));

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest, new MockActionResponse());

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.getAssetListEntry(
				_group.getGroupId(), "dynamic-asset-list-title");

		Assert.assertNotNull(assetListEntry);

		Assert.assertEquals(title, assetListEntry.getTitle());

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			true
		).fastLoad(
			assetListEntry.getTypeSettings(0)
		).build();

		Assert.assertEquals(
			String.valueOf(_group.getGroupId()),
			unicodeProperties.getProperty("groupIds"));
	}

	@Test
	public void testAddManualAssetListEntry() throws Exception {
		String title = "Manual Asset List Title";

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter("title", title);
		mockLiferayPortletActionRequest.addParameter("type", String.valueOf(1));

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest, new MockActionResponse());

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.getAssetListEntry(
				_group.getGroupId(), "manual-asset-list-title");

		Assert.assertEquals(title, assetListEntry.getTitle());
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private static Company _company;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "mvc.command.name=/asset_list/add_asset_list_entry")
	private MVCActionCommand _mvcActionCommand;

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
		public Appendable append(Appendable appendable) throws IOException {
			return null;
		}

		@Override
		public Appendable append(Appendable appendable, boolean escapeXML)
			throws IOException {

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
		public void setPortletMode(PortletMode portletMode)
			throws PortletModeException {
		}

		@Override
		public void setProperty(String key, String value) {
		}

		@Override
		public void setSecure(boolean secure) throws PortletSecurityException {
		}

		@Override
		public void setWindowState(WindowState windowState)
			throws WindowStateException {
		}

		@Override
		public void write(Writer writer) throws IOException {
		}

		@Override
		public void write(Writer writer, boolean escapeXML) throws IOException {
		}

	}

}