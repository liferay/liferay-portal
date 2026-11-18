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
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
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
	public void testRenderPortletFromAnotherLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentEntryLink fragmentEntryLink = _addPortletFragmentEntryLink(
			layout);

		_renderPortlet(
			fragmentEntryLink, LayoutTestUtil.addTypeContentLayout(_group));

		Assert.assertNull(_fetchSharedPortletPreferences(fragmentEntryLink));
	}

	@Test
	@TestInfo("LPD-115998")
	public void testRenderPortletFromMasterLayout() throws Exception {
		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		layout.setMasterLayoutPageTemplateEntryERC(
			masterLayoutPageTemplateEntry.getExternalReferenceCode());

		layout = _layoutLocalService.updateLayout(layout);

		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getPlid(),
			layout.getMasterLayoutPlid());

		FragmentEntryLink fragmentEntryLink = _addPortletFragmentEntryLink(
			_layoutLocalService.getLayout(
				masterLayoutPageTemplateEntry.getPlid()));

		_renderPortlet(fragmentEntryLink, layout);

		Assert.assertNotNull(_fetchSharedPortletPreferences(fragmentEntryLink));
	}

	@Test
	@TestInfo("LPD-115998")
	public void testRenderPortletFromSameLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentEntryLink fragmentEntryLink = _addPortletFragmentEntryLink(
			layout);

		_renderPortlet(fragmentEntryLink, layout);

		Assert.assertNull(_fetchSharedPortletPreferences(fragmentEntryLink));
	}

	@Test
	@TestInfo("LPD-115998")
	public void testRenderPortletFromUnsavedFragmentEntryLink()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setGroupId(_group.getGroupId());
		fragmentEntryLink.setEditableValues(
			JSONUtil.put(
				"instanceId", PortletIdCodec.generateInstanceId()
			).put(
				"portletId", JournalContentPortletKeys.JOURNAL_CONTENT
			).toString());

		_renderPortlet(
			fragmentEntryLink, LayoutTestUtil.addTypeContentLayout(_group));

		Assert.assertNull(_fetchSharedPortletPreferences(fragmentEntryLink));
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

	private PortletPreferences _fetchSharedPortletPreferences(
		FragmentEntryLink fragmentEntryLink) {

		JSONObject editableValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		return _portletPreferencesLocalService.fetchPortletPreferences(
			_group.getGroupId(), PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
			PortletKeys.PREFS_PLID_SHARED,
			PortletIdCodec.encode(
				editableValuesJSONObject.getString("portletId"),
				editableValuesJSONObject.getString("instanceId")));
	}

	private void _renderPortlet(
			FragmentEntryLink fragmentEntryLink, Layout layout)
		throws Exception {

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout)) {

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			HttpServletRequest httpServletRequest = serviceContext.getRequest();

			httpServletRequest.setAttribute(
				"ORIGINAL_HTTP_SERVLET_REQUEST", httpServletRequest);

			JSONObject editableValuesJSONObject =
				fragmentEntryLink.getEditableValuesJSONObject();

			_fragmentPortletRenderer.renderPortlet(
				fragmentEntryLink, httpServletRequest,
				new MockHttpServletResponse(),
				editableValuesJSONObject.getString("portletId"),
				editableValuesJSONObject.getString("instanceId"),
				StringPool.BLANK);
		}
	}

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentPortletRenderer _fragmentPortletRenderer;

	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}