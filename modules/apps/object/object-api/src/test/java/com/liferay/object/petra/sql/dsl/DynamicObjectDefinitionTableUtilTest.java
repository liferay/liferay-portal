/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.petra.sql.dsl;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Gabriel Albuquerque
 */
public class DynamicObjectDefinitionTableUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetDataType() {
		Assert.assertEquals(
			"BOOLEAN",
			DynamicObjectDefinitionTableUtil.getDataType(
				ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
				ObjectFieldConstants.DB_TYPE_BOOLEAN));
		Assert.assertEquals(
			"VARCHAR(75)",
			DynamicObjectDefinitionTableUtil.getDataType(
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST,
				ObjectFieldConstants.DB_TYPE_STRING));
		Assert.assertEquals(
			"VARCHAR(280)",
			DynamicObjectDefinitionTableUtil.getDataType(
				ObjectFieldConstants.BUSINESS_TYPE_TEXT,
				ObjectFieldConstants.DB_TYPE_STRING));
	}

	@Test
	public void testGetInsertMissingExtensionTableRowsSQL() {
		Assert.assertEquals(
			StringBundler.concat(
				"insert into L_Ticket_x (c_ticketId) select ",
				"L_Ticket.c_ticketId from L_Ticket left join L_Ticket_x on ",
				"L_Ticket_x.c_ticketId = L_Ticket.c_ticketId where ",
				"L_Ticket_x.c_ticketId is null"),
			DynamicObjectDefinitionTableUtil.
				getInsertMissingExtensionTableRowsSQL(
					"L_Ticket_x", "c_ticketId", "L_Ticket"));
	}

}