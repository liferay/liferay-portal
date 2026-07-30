/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.frontend.data.set.view.table.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaField;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jhosseph Gonzalez
 */
@RunWith(Arquillian.class)
public class WorkflowTaskSectionTableFDSViewTest
	extends BaseSectionTableFDSViewTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetFDSTableSchema() throws Exception {
		assertFDSTableSchemaField(
			"actionLinkWorkflowTask", "assetTitleTableCellRenderer",
			"asset-title", "assetTitle");
		assertFDSTableSchemaField(
			null, "assetTypeTableCellRenderer", "asset-type", "assetType");
		assertFDSTableSchemaField(
			null, null, "author", "embedded.creator.name");
		assertFDSTableSchemaField(
			null, "taskTableCellRenderer", "task", "task");
		assertFDSTableSchemaField(
			null, "dueDateTableCellRenderer", "due-date", "dueDate");
		assertFDSTableSchemaField(
			null, "workflowStateTableCellRenderer", "status", "state");
		assertFDSTableSchemaField(
			null, "dateTime", "last-activity-date", "dateModified");

		FDSTableSchemaField fdsTableSchemaField = fdsTableSchemaFieldsMap.get(
			"dateModified");

		Assert.assertTrue(fdsTableSchemaField.isSortable());
	}

	@Override
	protected String getFDSName() {
		return "com.liferay.site.cmp.site.initializer-workflow-tasks";
	}

}