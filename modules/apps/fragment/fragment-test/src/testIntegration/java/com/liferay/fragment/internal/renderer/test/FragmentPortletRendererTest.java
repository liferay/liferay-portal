/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentPortletRenderer;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Anupam Shrivastava
 */
@RunWith(Arquillian.class)
public class FragmentPortletRendererTest {

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
	@TestInfo("LPD-115998")
	public void testRenderPortlet() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentEntryLink fragmentEntryLink = _addPortletFragmentEntryLink(
			LayoutTestUtil.addTypeContentLayout(_group));

		_testRenderPortlet(
			fragmentEntryLink, layout, false,
			StringBundler.concat(
				"Skipping shared preferences for fragment entry link ",
				fragmentEntryLink.getFragmentEntryLinkId(), " with PLID ",
				fragmentEntryLink.getPlid(),
				" because it does not belong to the master of layout ",
				layout.getPlid()));

		fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setGroupId(_group.getGroupId());
		fragmentEntryLink.setEditableValues(
			JSONUtil.put(
				"instanceId", PortletIdCodec.generateInstanceId()
			).put(
				"portletId", JournalContentPortletKeys.JOURNAL_CONTENT
			).toString());

		_testRenderPortlet(fragmentEntryLink, layout, false);

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		layout = LayoutTestUtil.updateMasterLayoutPageTemplateEntryERC(
			layout, masterLayoutPageTemplateEntry.getExternalReferenceCode(),
			null);

		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getPlid(),
			layout.getMasterLayoutPlid());

		_testRenderPortlet(
			_addPortletFragmentEntryLink(
				_layoutLocalService.getLayout(
					masterLayoutPageTemplateEntry.getPlid())),
			layout, true);
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

	private void _testRenderPortlet(
			FragmentEntryLink fragmentEntryLink, Layout layout,
			boolean sharedPreferences, String... expectedWarnMessages)
		throws Exception {

		JSONObject editableValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(_group.getCompanyId()), _group,
				layout);

		mockHttpServletRequest.setAttribute(
			"ORIGINAL_HTTP_SERVLET_REQUEST", mockHttpServletRequest);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.fragment.internal.renderer." +
					"FragmentPortletRendererImpl",
				LoggerTestUtil.WARN)) {

			_fragmentPortletRenderer.renderPortlet(
				fragmentEntryLink, mockHttpServletRequest,
				new MockHttpServletResponse(),
				editableValuesJSONObject.getString("portletId"),
				editableValuesJSONObject.getString("instanceId"),
				StringPool.BLANK);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			for (int i = 0; i < expectedWarnMessages.length; i++) {
				LogEntry logEntry = logEntries.get(i);

				Assert.assertEquals(
					expectedWarnMessages[i], logEntry.getMessage());
			}

			Assert.assertEquals(
				logEntries.toString(), expectedWarnMessages.length,
				logEntries.size());
		}

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPortletPreferences(
				_group.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				PortletKeys.PREFS_PLID_SHARED,
				PortletIdCodec.encode(
					editableValuesJSONObject.getString("portletId"),
					editableValuesJSONObject.getString("instanceId")));

		if (sharedPreferences) {
			Assert.assertNotNull(portletPreferences);
		}
		else {
			Assert.assertNull(portletPreferences);
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentPortletRenderer _fragmentPortletRenderer;

	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}