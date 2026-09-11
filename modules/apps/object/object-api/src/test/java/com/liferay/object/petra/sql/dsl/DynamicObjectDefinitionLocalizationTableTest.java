/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.petra.sql.dsl;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jhosseph Gonzalez
 */
public class DynamicObjectDefinitionLocalizationTableTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetCreateTableSQL() {
		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		String localizationDBTableName = RandomTestUtil.randomString();

		Mockito.when(
			objectDefinition.getLocalizationDBTableName()
		).thenReturn(
			localizationDBTableName
		);

		String pkObjectFieldDBColumnName = RandomTestUtil.randomString();

		Mockito.when(
			objectDefinition.getPKObjectFieldDBColumnName()
		).thenReturn(
			pkObjectFieldDBColumnName
		);

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getBusinessType()
		).thenReturn(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT
		);

		String dbColumnName = RandomTestUtil.randomString();

		Mockito.when(
			objectField.getDBColumnName()
		).thenReturn(
			dbColumnName
		);

		Mockito.when(
			objectField.getDBType()
		).thenReturn(
			ObjectFieldConstants.DB_TYPE_STRING
		);

		DynamicObjectDefinitionLocalizationTable
			dynamicObjectDefinitionLocalizationTable =
				new DynamicObjectDefinitionLocalizationTable(
					objectDefinition, Collections.singletonList(objectField));

		Assert.assertEquals(
			StringBundler.concat(
				"create table ", localizationDBTableName, " (",
				pkObjectFieldDBColumnName,
				" LONG not null, languageId VARCHAR(75) not null, ",
				dbColumnName, " VARCHAR(280), primary key (",
				pkObjectFieldDBColumnName, ",languageId));"),
			dynamicObjectDefinitionLocalizationTable.getCreateTableSQL());
	}

}