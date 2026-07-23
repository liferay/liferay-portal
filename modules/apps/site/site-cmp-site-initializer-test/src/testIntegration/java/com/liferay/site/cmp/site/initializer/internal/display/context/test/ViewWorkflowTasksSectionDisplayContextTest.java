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
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
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

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Fábio Alves
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
@Sync
public class ViewWorkflowTasksSectionDisplayContextTest
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
				CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN,
				"&filter=cmpTaskObjectEntryIds/any(x:x gt 0)",
				"&nestedFields=embedded"),
			getAPIURL(null));
	}

	@Override
	@Test
	public void testGetBulkActionDropdownItems() throws Exception {
		List<DropdownItem> bulkActionDropdownItems = getBulkActionDropdownItems(
			null);

		Assert.assertEquals(
			bulkActionDropdownItems.toString(), 2,
			bulkActionDropdownItems.size());

		FDSActionDropdownItem updateDueDateFDSActionDropdownItem =
			(FDSActionDropdownItem)bulkActionDropdownItems.get(0);

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"date-time", "update-due-date", "Update Due Date", null,
			updateDueDateFDSActionDropdownItem);

		Assert.assertEquals(
			"updateDueDate",
			getValue(updateDueDateFDSActionDropdownItem, "permissionKey"));

		FDSActionDropdownItem assignToFDSActionDropdownItem =
			(FDSActionDropdownItem)bulkActionDropdownItems.get(1);

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"user", "assign-to", "Assign to...", null,
			assignToFDSActionDropdownItem);

		Assert.assertEquals(
			"assignToUser",
			getValue(assignToFDSActionDropdownItem, "permissionKey"));
	}

	@Override
	@Test
	public void testGetCreationMenu() throws Exception {
		Assert.assertNull(getCreationMenu(null));
		Assert.assertNull(getCreationMenu(assetEntry));
	}

	@Test
	public void testGetEmptyState() throws Exception {
		Map<String, Object> emptyState = getEmptyState(null);

		Assert.assertEquals(
			"There are no workflow tasks related to your projects.",
			emptyState.get("description"));
		Assert.assertEquals(
			"/states/cmp_empty_state_tasks.svg", emptyState.get("image"));
		Assert.assertEquals("No Tasks", emptyState.get("title"));
	}

	@Test
	public void testGetFDSActionDropdownItems() throws Exception {
		List<FDSActionDropdownItem> groupFDSActionDropdownItems =
			getFDSActionDropdownItems(null);

		Assert.assertEquals(
			groupFDSActionDropdownItems.toString(), 2,
			groupFDSActionDropdownItems.size());

		FDSActionDropdownItem groupFDSActionDropdownItem =
			groupFDSActionDropdownItems.get(0);

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			(List<FDSActionDropdownItem>)groupFDSActionDropdownItem.get(
				"items");

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 1,
			fdsActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			null, "workflow-transition", null, null,
			fdsActionDropdownItems.get(0));

		groupFDSActionDropdownItem = groupFDSActionDropdownItems.get(1);

		fdsActionDropdownItems =
			(List<FDSActionDropdownItem>)groupFDSActionDropdownItem.get(
				"items");

		Assert.assertEquals(
			fdsActionDropdownItems.toString(), 4,
			fdsActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"view", "actionLinkWorkflowTask", "View", null,
			Collections.singletonMap(
				"entryClassName", CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN),
			fdsActionDropdownItems.get(0));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			null, "assignToMeWorkflowTask", "Assign to Me", null,
			HashMapBuilder.<String, Object>put(
				"embedded.assignedToMe", false
			).put(
				"embedded.completed", false
			).put(
				"entryClassName", CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
			).build(),
			fdsActionDropdownItems.get(1));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			null, "assignToWorkflowTask", "Assign to...", null,
			HashMapBuilder.<String, Object>put(
				"embedded.completed", false
			).put(
				"entryClassName", CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
			).build(),
			fdsActionDropdownItems.get(2));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"date-time", "updateDueDateWorkflowTask", "Update Due Date", null,
			HashMapBuilder.<String, Object>put(
				"embedded.completed", false
			).put(
				"entryClassName", CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN
			).build(),
			fdsActionDropdownItems.get(3));
	}

	@Override
	@Test
	public void testGetFDSFilters() throws Exception {
		List<FDSFilter> fdsFilters = getFDSFilters(null);

		Assert.assertEquals(fdsFilters.toString(), 3, fdsFilters.size());

		assertFDSFilter(
			FDSEntityFieldTypes.STRING, "cmpAssignTo", "assignee",
			fdsFilters.get(0));
		assertFDSFilter(
			FDSEntityFieldTypes.DATE_TIME, "dueDate", "due-date",
			fdsFilters.get(1));
		assertFDSFilter(
			FDSEntityFieldTypes.BOOLEAN, "completed", "status",
			fdsFilters.get(2));
	}

	@Override
	protected Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		return httpServletRequest.getAttribute(
			"com.liferay.site.cmp.site.initializer.internal.display.context." +
				"ViewWorkflowTasksSectionDisplayContext");
	}

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.ViewWorkflowTasksJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}