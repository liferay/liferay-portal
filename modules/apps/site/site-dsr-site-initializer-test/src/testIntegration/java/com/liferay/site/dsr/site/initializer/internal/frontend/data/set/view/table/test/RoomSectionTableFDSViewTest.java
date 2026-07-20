/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.frontend.data.set.view.table.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class RoomSectionTableFDSViewTest
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
			"view", "roomNameTableCellRenderer", "name", "name");
		assertFDSTableSchemaField(
			null, "dateTime", "creation-date", "dateCreated");
		assertFDSTableSchemaField(
			null, "dateTime", "last-modified", "dateModified");
		assertFDSTableSchemaField(null, null, "owner", "creator.name");
		assertFDSTableSchemaField(
			null, "roomStatusTableCellRenderer", "status", "status");
		assertFDSTableSchemaField(
			null, "roomTrendTableCellRenderer", "trend", "trend");
		assertFDSTableSchemaField(
			null, "roomStatusFieldTableCellRenderer", "room-status",
			"roomStatus");
	}

	@Override
	protected String getFDSName() {
		return "com.liferay.site.dsr.site.initializer-room";
	}

}