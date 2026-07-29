/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.change.tracking.spi.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkCTDisplayRenderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-99262")
	public void testRenderPreviewDoesNotCreateOrphanPortletPreferences()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		FragmentEntryLink fragmentEntryLink = _addPortletFragmentEntryLink(
			draftLayout);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		String portletId = _getPortletId(fragmentEntryLink);

		List<PortletPreferences> initialPortletPreferences =
			_portletPreferencesLocalService.getPortletPreferencesByPortletId(
				portletId);

		CTDisplayRenderer<FragmentEntryLink> ctDisplayRenderer =
			_ctDisplayRendererRegistry.getCTDisplayRenderer(
				_classNameLocalService.getClassNameId(FragmentEntryLink.class));

		ctDisplayRenderer.renderPreview(
			_createDisplayContext(fragmentEntryLink));

		List<PortletPreferences> portletPreferences =
			_portletPreferencesLocalService.getPortletPreferencesByPortletId(
				portletId);

		Assert.assertEquals(
			portletPreferences.toString(), initialPortletPreferences.size(),
			portletPreferences.size());
	}

	private FragmentEntryLink _addPortletFragmentEntryLink(Layout layout)
		throws Exception {

		JSONObject jsonObject = ContentLayoutTestUtil.addPortletToLayout(
			layout, JournalContentPortletKeys.JOURNAL_CONTENT);

		JSONObject fragmentEntryLinkJSONObject = jsonObject.getJSONObject(
			"fragmentEntryLink");

		return _fragmentEntryLinkLocalService.getFragmentEntryLink(
			fragmentEntryLinkJSONObject.getLong("fragmentEntryLinkId"));
	}

	private DisplayContext<FragmentEntryLink> _createDisplayContext(
			FragmentEntryLink fragmentEntryLink)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_company, _group, LayoutTestUtil.addTypeContentLayout(_group));
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		return new DisplayContext<FragmentEntryLink>() {

			@Override
			public String getDownloadURL(String key, long size, String title) {
				return null;
			}

			@Override
			public HttpServletRequest getHttpServletRequest() {
				return mockHttpServletRequest;
			}

			@Override
			public HttpServletResponse getHttpServletResponse() {
				return mockHttpServletResponse;
			}

			@Override
			public Locale getLocale() {
				return LocaleUtil.US;
			}

			@Override
			public FragmentEntryLink getModel() {
				return fragmentEntryLink;
			}

			@Override
			public void render(BaseModel<?> baseModel, Locale locale) {
			}

			@Override
			public String renderPreview(BaseModel<?> baseModel, Locale locale) {
				return null;
			}

		};
	}

	private String _getPortletId(FragmentEntryLink fragmentEntryLink) {
		JSONObject editableValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		return PortletIdCodec.encode(
			JournalContentPortletKeys.JOURNAL_CONTENT,
			editableValuesJSONObject.getString("instanceId"));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CTDisplayRendererRegistry _ctDisplayRendererRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}