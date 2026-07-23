/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Pedro Leite
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
@Sync
public class ViewAllTasksSectionDisplayContextTest
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
				"/o/search/v1.0/search?emptySearch=true&entryClassNames=",
				HtmlUtil.escapeURL(objectDefinition.getClassName()), ",",
				CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN,
				"&filter=(objectDefinitionId eq ",
				objectDefinition.getObjectDefinitionId(),
				" or cmpTaskObjectEntryIds/any(x:x gt 0))",
				"&nestedFields=cmpProjectToCMPTasks,embedded"),
			getAPIURL(null));
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> groupFDSActionDropdownItems =
			getFDSActionDropdownItems(assetEntry);

		Assert.assertEquals(
			groupFDSActionDropdownItems.toString(), 2,
			groupFDSActionDropdownItems.size());

		FDSActionDropdownItem fdsActionDropdownItem =
			groupFDSActionDropdownItems.get(0);

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			(List<FDSActionDropdownItem>)fdsActionDropdownItem.get("items");

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 1,
			fdsActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			null, "workflow-transition", null, null,
			fdsActionDropdownItems.get(0));

		fdsActionDropdownItem = groupFDSActionDropdownItems.get(1);

		fdsActionDropdownItems =
			(List<FDSActionDropdownItem>)fdsActionDropdownItem.get("items");

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
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"view", "actionLinkWorkflowTask", "View", null,
			Collections.singletonMap(
				"entryClassName", CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN),
			fdsActionDropdownItems.get(7));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			null, "assignToMeWorkflowTask", "Assign to Me", null,
			HashMapBuilder.<String, Object>put(
				"embedded.assignedToMe", false
			).put(
				"embedded.completed", false
			).put(
				"entryClassName", CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
			).build(),
			fdsActionDropdownItems.get(8));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			null, "assignToWorkflowTask", "Assign to...", null,
			HashMapBuilder.<String, Object>put(
				"embedded.completed", false
			).put(
				"entryClassName", CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
			).build(),
			fdsActionDropdownItems.get(9));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"date-time", "updateDueDateWorkflowTask", "Update Due Date", null,
			HashMapBuilder.<String, Object>put(
				"embedded.completed", false
			).put(
				"entryClassName", CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
			).build(),
			fdsActionDropdownItems.get(10));
		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 11,
			fdsActionDropdownItems.size());
	}

	@Override
	@Test
	public void testGetFDSFilters() throws Exception {
		List<FDSFilter> fdsFilters = getFDSFilters(null);

		assertFDSFilter(
			FDSEntityFieldTypes.STRING, "cmpAssignTo", "assignee",
			fdsFilters.get(0));
		assertFDSFilter(
			FDSEntityFieldTypes.DATE_TIME, "dateCreated", "create-date",
			fdsFilters.get(1));
		assertFDSFilter(
			FDSEntityFieldTypes.DATE_TIME, "cmpDueDate", "due-date",
			fdsFilters.get(2));
		assertFDSFilter(
			FDSEntityFieldTypes.INTEGER, "cmpTaskCMPProjectId", "project",
			fdsFilters.get(3));
		assertFDSFilter(
			FDSEntityFieldTypes.STRING, "cmpState", "state", fdsFilters.get(4));
		assertFDSFilter(
			FDSEntityFieldTypes.STRING, "keywords", "tag", fdsFilters.get(5));
		assertFDSFilter(
			FDSEntityFieldTypes.INTEGER, "classNameId", "task-type",
			fdsFilters.get(6));
		Assert.assertEquals(fdsFilters.toString(), 7, fdsFilters.size());
	}

	@Override
	protected Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		return httpServletRequest.getAttribute(
			"com.liferay.site.cmp.site.initializer.internal.display.context." +
				"ViewAllTasksSectionDisplayContext");
	}

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.ViewAllTasksJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}