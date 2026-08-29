/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.frontend.data.set.view.table.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.FDSViewRegistry;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
public class TaskSectionTableFDSViewTest
	extends BaseSectionTableFDSViewTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetFDSTableSchema() throws Exception {
		_testGetFDSTableSchema(
			"com.liferay.site.cmp.site.initializer-all-tasks");
		_testGetFDSTableSchema(
			"com.liferay.site.cmp.site.initializer-project-tasks");
	}

	@Override
	protected String getFDSName() {
		return "com.liferay.site.cmp.site.initializer-project-tasks";
	}

	private void _testGetFDSTableSchema(String fdsName) {
		List<FDSView> fdsViews = _fdsViewRegistry.getFDSViews(fdsName);

		FDSView fdsView = fdsViews.get(0);

		FDSTableSchema fdsTableSchema = fdsView.getFDSTableSchema(
			LocaleUtil.US);

		fdsTableSchemaFieldsMap = fdsTableSchema.getFDSTableSchemaFieldsMap();

		assertFDSTableSchemaField(
			null, "assigneeTableCellRenderer", "assign-to", "assignee");
		assertFDSTableSchemaField(
			null, "dueDateTableCellRenderer", "due-date", "dueDate");
		assertFDSTableSchemaField(
			null, "projectTitleTableCellRenderer", "project", "projectTitle");
		assertFDSTableSchemaField(
			null, "stateTableCellRenderer", "state", "state");
		assertFDSTableSchemaField(
			"actionLink", "simpleActionLinkTableCellRenderer", "title",
			"title");
	}

	@Inject
	private FDSViewRegistry _fdsViewRegistry;

}