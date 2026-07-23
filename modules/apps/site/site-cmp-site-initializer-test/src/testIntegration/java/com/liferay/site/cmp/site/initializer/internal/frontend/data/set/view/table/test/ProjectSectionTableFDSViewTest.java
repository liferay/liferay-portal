/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.frontend.data.set.view.table.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
public class ProjectSectionTableFDSViewTest
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
			null, "progressBarTableCellRenderer", "completion-rate",
			"embedded.completionRate");
		assertFDSTableSchemaField(
			null, "dueDateTableCellRenderer", "due-date", "embedded.dueDate");
		assertFDSTableSchemaField(
			null, "userRelationshipTableCellRenderer", "manager",
			"embedded.r_userToCMPProjectManager_user");
		assertFDSTableSchemaField(
			null, "userRelationshipTableCellRenderer", "sponsor",
			"embedded.r_userToCMPProjectSponsor_user");
		assertFDSTableSchemaField(
			null, "stateTableCellRenderer", "state", "embedded.state");
		assertFDSTableSchemaField(
			"actionLink", "simpleActionLinkTableCellRenderer", "title",
			"embedded.title");
	}

	@Override
	protected String getFDSName() {
		return "com.liferay.site.cmp.site.initializer-project";
	}

}