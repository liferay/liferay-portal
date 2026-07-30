/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Fábio Alves
 */
@RunWith(Arquillian.class)
@Sync
public class ViewProjectTasksSectionDisplayContextTest
	extends BaseTasksSectionDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetAPIURL() throws Exception {
		Assert.assertEquals(
			StringBundler.concat(
				"/o/search/v1.0/search?emptySearch=true&filter=",
				"(objectDefinitionId eq ",
				objectDefinition.getObjectDefinitionId(),
				" and scopeGroupId eq ", assetEntry.getGroupId(),
				")&nestedFields=embedded,embedded.cmpProjectToCMPTasks",
				"&nestedFieldsDepth=2"),
			getAPIURL(assetEntry));
		Assert.assertEquals(
			StringBundler.concat(
				"/o/search/v1.0/search?emptySearch=true&filter=",
				"(objectDefinitionId eq ",
				objectDefinition.getObjectDefinitionId(),
				")&nestedFields=embedded,embedded.cmpProjectToCMPTasks",
				"&nestedFieldsDepth=2"),
			getAPIURL(null));
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			getFDSActionDropdownItems(assetEntry);

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 7,
			fdsActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"pencil", "edit", "Edit", "get",
			Collections.singletonMap(
				"entryClassName", objectDefinition.getClassName()),
			fdsActionDropdownItems.get(0));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"view", "actionLink", "View", null,
			Collections.singletonMap(
				"entryClassName", objectDefinition.getClassName()),
			fdsActionDropdownItems.get(1));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"bell-on", "subscribe", "Watch Task", "post",
			fdsActionDropdownItems.get(2));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"bell-off", "unsubscribe", "Stop Watching Task", "post",
			fdsActionDropdownItems.get(3));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			null, "assign-to", "Assign to...", "get",
			Collections.singletonMap(
				"entryClassName", objectDefinition.getClassName()),
			fdsActionDropdownItems.get(4));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"date-time", "update-due-date", "Update Due Date", "get",
			Collections.singletonMap(
				"entryClassName", objectDefinition.getClassName()),
			fdsActionDropdownItems.get(5));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"trash", "delete", "Delete", null,
			Collections.singletonMap(
				"entryClassName", objectDefinition.getClassName()),
			fdsActionDropdownItems.get(6));
	}

	@Test
	public void testGetTasksQuickFiltersProperties() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.LAYOUT_ASSET_ENTRY, assetEntry);
		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		Object displayContext = getSectionDisplayContext(httpServletRequest);

		Map<String, Object> tasksQuickFiltersProperties =
			ReflectionTestUtil.invoke(
				displayContext, "getTasksQuickFiltersProperties",
				new Class<?>[0]);

		Assert.assertEquals(
			assetEntry.getClassPK(),
			tasksQuickFiltersProperties.get("cmpProjectObjectEntryId"));
	}

	@Override
	protected Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		return httpServletRequest.getAttribute(
			"com.liferay.site.cmp.site.initializer.internal.display.context." +
				"ViewProjectTasksSectionDisplayContext");
	}

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.ViewProjectTasksJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}