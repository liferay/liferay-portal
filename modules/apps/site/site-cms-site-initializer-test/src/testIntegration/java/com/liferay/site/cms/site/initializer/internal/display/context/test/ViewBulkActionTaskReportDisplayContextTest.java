/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Luca Pellizzon
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
@Sync
public class ViewBulkActionTaskReportDisplayContextTest
	extends BaseDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			_getFDSActionDropdownItems();

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 1,
			fdsActionDropdownItems.size());

		FDSActionDropdownItem fdsActionDropdownItem =
			fdsActionDropdownItems.get(0);

		Assert.assertNotNull(fdsActionDropdownItem);

		Map<String, String> data =
			(Map<String, String>)fdsActionDropdownItem.get("data");

		Assert.assertEquals("view", data.get("id"));

		Assert.assertEquals("view", fdsActionDropdownItem.get("icon"));
		Assert.assertEquals("view", fdsActionDropdownItem.get("label"));
	}

	private List<FDSActionDropdownItem> _getFDSActionDropdownItems()
		throws Exception {

		return ReflectionTestUtil.invoke(
			_getSectionDisplayContext(getMockHttpServletRequest()),
			"getFDSActionDropdownItems", new Class<?>[0]);
	}

	private Object _getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		Object viewBulkActionTaskReportDisplayContext =
			httpServletRequest.getAttribute(
				"com.liferay.site.cms.site.initializer.internal.display." +
					"context.ViewBulkActionTaskReportDisplayContext");

		Assert.assertNotNull(viewBulkActionTaskReportDisplayContext);

		return viewBulkActionTaskReportDisplayContext;
	}

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewBulkActionTaskReportFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}