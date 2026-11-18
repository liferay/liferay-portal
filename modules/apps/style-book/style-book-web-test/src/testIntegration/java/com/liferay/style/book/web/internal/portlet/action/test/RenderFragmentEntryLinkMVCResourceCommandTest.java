/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Anupam Shrivastava
 */
@RunWith(Arquillian.class)
public class RenderFragmentEntryLinkMVCResourceCommandTest {

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
	public void testServeResource() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				serviceContext);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				StringPool.BLANK,
				"<div><lfr-widget-web-content></lfr-widget-web-content></div>",
				StringPool.BLANK, false, StringPool.BLANK, null, 0, false,
				false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		int portletPreferencesCount = _getPortletPreferencesCount();

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout)) {

			ServiceContext layoutServiceContext =
				ServiceContextThreadLocal.getServiceContext();

			HttpServletRequest httpServletRequest =
				layoutServiceContext.getRequest();

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			for (String name :
					Collections.list(httpServletRequest.getAttributeNames())) {

				mockHttpServletRequest.setAttribute(
					name, httpServletRequest.getAttribute(name));
			}

			mockHttpServletRequest.setAttribute(
				"ORIGINAL_HTTP_SERVLET_REQUEST", mockHttpServletRequest);

			MockLiferayResourceRequest mockLiferayResourceRequest =
				new MockLiferayResourceRequest(mockHttpServletRequest);

			mockLiferayResourceRequest.addParameter(
				"fragmentEntryKey", fragmentEntry.getFragmentEntryKey());
			mockLiferayResourceRequest.addParameter(
				"groupId", String.valueOf(_group.getGroupId()));

			_mvcResourceCommand.serveResource(
				mockLiferayResourceRequest, new MockLiferayResourceResponse());
		}

		Assert.assertEquals(
			portletPreferencesCount, _getPortletPreferencesCount());
	}

	private int _getPortletPreferencesCount() {
		List<PortletPreferences> portletPreferencesList =
			_portletPreferencesLocalService.getPortletPreferencesByPlid(0);

		return portletPreferencesList.size();
	}

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	private Group _group;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject(filter = "mvc.command.name=/style_book/render_fragment_entry_link")
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}