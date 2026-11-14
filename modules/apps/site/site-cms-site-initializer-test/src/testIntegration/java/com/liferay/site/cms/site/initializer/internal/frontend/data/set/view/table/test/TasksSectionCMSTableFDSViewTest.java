/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.view.table.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaField;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jan Brychta
 */
@RunWith(Arquillian.class)
public class TasksSectionCMSTableFDSViewTest
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
			"actionLinkWorkflowTask", "simpleActionLinkTableCellRenderer",
			"asset-title", "embedded.objectReviewed.assetTitle");
		assertFDSTableSchemaField(
			null, null, "asset-type", "embedded.objectReviewed.assetType");
		assertFDSTableSchemaField(
			null, "authorTableCellRenderer", "author", "embedded.creator.name");
		assertFDSTableSchemaField(null, null, "task", "embedded.label");
		assertFDSTableSchemaField(
			null, "dueDateTableCellRenderer", "due-date", "dueDate");
		assertFDSTableSchemaField(
			null, "taskStatusTableCellRenderer", "status", "completed");
		assertFDSTableSchemaField(
			null, "dateTime", "last-activity-date", "dateModified");

		Assert.assertFalse(_isSortable("completed"));
		Assert.assertTrue(_isSortable("dateModified"));
		Assert.assertFalse(_isSortable("dueDate"));
	}

	@Override
	protected String getFDSName() {
		return CMSSiteInitializerFDSNames.TASKS_SECTION;
	}

	private boolean _isSortable(String fieldName) {
		FDSTableSchemaField fdsTableSchemaField = fdsTableSchemaFieldsMap.get(
			fieldName);

		return fdsTableSchemaField.isSortable();
	}

}