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
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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

		_testRenderPortlet(
			_addPortletFragmentEntryLink(
				LayoutTestUtil.addTypeContentLayout(_group)),
			layout, false);

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				true, RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND, null,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_testRenderPortlet(
			_addPortletFragmentEntryLink(
				_layoutLocalService.getLayout(
					layoutUtilityPageEntry.getPlid())),
			layout, false);

		FragmentEntryLink fragmentEntryLink =
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
			FragmentEntryLink fragmentEntryLink, Layout layout, boolean shared)
		throws Exception {

		JSONObject editableValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout)) {

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			HttpServletRequest httpServletRequest = serviceContext.getRequest();

			httpServletRequest.setAttribute(
				"ORIGINAL_HTTP_SERVLET_REQUEST", httpServletRequest);

			_fragmentPortletRenderer.renderPortlet(
				fragmentEntryLink, httpServletRequest,
				new MockHttpServletResponse(),
				editableValuesJSONObject.getString("portletId"),
				editableValuesJSONObject.getString("instanceId"),
				StringPool.BLANK);
		}

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPortletPreferences(
				_group.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				PortletKeys.PREFS_PLID_SHARED,
				PortletIdCodec.encode(
					editableValuesJSONObject.getString("portletId"),
					editableValuesJSONObject.getString("instanceId")));

		Assert.assertEquals(shared, portletPreferences != null);
	}

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentPortletRenderer _fragmentPortletRenderer;

	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}