/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.util;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Joshua Cords
 * @author Olivia Yu
 */
public class AssetListOrderByColumnUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@AfterClass
	public static void tearDownClass() {
		_objectDefinitionLocalServiceUtilMockedStatic.close();
		_objectFieldLocalServiceUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_objectDefinitionLocalServiceUtilMockedStatic.reset();
		_objectFieldLocalServiceUtilMockedStatic.reset();
		_portalUtilMockedStatic.reset();
	}

	@Test
	public void testToOrderByColumnIgnoresUnresolvableObjectField() {
		String orderByColumn = _buildOrderByColumn("dueDate");

		Assert.assertEquals(
			orderByColumn,
			AssetListOrderByColumnUtil.toOrderByColumn(
				_COMPANY_ID, orderByColumn));
	}

	@Test
	public void testToOrderByColumnWithKeywordTextField() {
		ObjectField objectField = _setUpObjectField(
			ObjectFieldConstants.DB_TYPE_STRING, "name");

		Mockito.when(
			objectField.isIndexedAsKeyword()
		).thenReturn(
			true
		);

		Assert.assertEquals(
			"nestedFieldArray.name.value_keyword",
			AssetListOrderByColumnUtil.toOrderByColumn(
				_COMPANY_ID, _buildOrderByColumn("name")));
	}

	@Test
	public void testToOrderByColumnWithLegacyColumn() {
		Assert.assertEquals(
			"priority",
			AssetListOrderByColumnUtil.toOrderByColumn(
				_COMPANY_ID, "priority"));
	}

	@Test
	public void testToOrderByColumnWithNumericField() {
		_setUpObjectField(ObjectFieldConstants.DB_TYPE_DOUBLE, "amount");

		Assert.assertEquals(
			"nestedFieldArray.amount.value_double",
			AssetListOrderByColumnUtil.toOrderByColumn(
				_COMPANY_ID, _buildOrderByColumn("amount")));
	}

	@Test
	public void testToOrderByColumnWithTextField() {
		_setUpObjectField(ObjectFieldConstants.DB_TYPE_STRING, "description");

		Assert.assertEquals(
			"nestedFieldArray.description.value_keyword_lowercase",
			AssetListOrderByColumnUtil.toOrderByColumn(
				_COMPANY_ID, _buildOrderByColumn("description")));
	}

	private String _buildOrderByColumn(String propertyName) {
		return JSONUtil.put(
			"classNameId", _CLASS_NAME_ID
		).put(
			"classTypeId", _CLASS_TYPE_ID
		).put(
			"propertyName", propertyName
		).toString();
	}

	private ObjectField _setUpObjectField(String dbType, String name) {
		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getDBType()
		).thenReturn(
			dbType
		);

		Mockito.when(
			objectField.getName()
		).thenReturn(
			name
		);

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			_CLASS_TYPE_ID
		);

		_objectDefinitionLocalServiceUtilMockedStatic.when(
			() ->
				ObjectDefinitionLocalServiceUtil.
					fetchObjectDefinitionByClassName(
						_COMPANY_ID, "com.liferay.test.Class" + _CLASS_NAME_ID)
		).thenReturn(
			objectDefinition
		);

		_objectFieldLocalServiceUtilMockedStatic.when(
			() -> ObjectFieldLocalServiceUtil.fetchObjectField(
				_CLASS_TYPE_ID, name)
		).thenReturn(
			objectField
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassName(_CLASS_NAME_ID)
		).thenReturn(
			"com.liferay.test.Class" + _CLASS_NAME_ID
		);

		return objectField;
	}

	private static final long _CLASS_NAME_ID = RandomTestUtil.randomLong();

	private static final long _CLASS_TYPE_ID = RandomTestUtil.randomLong();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final MockedStatic<ObjectDefinitionLocalServiceUtil>
		_objectDefinitionLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectDefinitionLocalServiceUtil.class);
	private static final MockedStatic<ObjectFieldLocalServiceUtil>
		_objectFieldLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectFieldLocalServiceUtil.class);
	private static final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);

}