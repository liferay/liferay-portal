/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.util;

import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.dto.v2_0.DataLayoutColumn;
import com.liferay.data.engine.rest.dto.v2_0.DataLayoutPage;
import com.liferay.data.engine.rest.dto.v2_0.DataLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Gergely Szalay
 */
public class DataDefinitionUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_fieldName = RandomTestUtil.randomString(8);
		_fieldReference = RandomTestUtil.randomString();
	}

	@Test
	public void testUpdateDataDefinitionFields() {
		_testUpdateDataDefinitionFields();
		_testUpdateDataDefinitionFieldsWithExistingFieldName();
		_testUpdateDataDefinitionFieldsWithExistingFieldNameAndExistingReference();
		_testUpdateDataDefinitionFieldsWithExistingLegacyFieldNameAndExistingReference();
	}

	private DataDefinition _getDataDefinition() {
		DataDefinition dataDefinition = new DataDefinition();

		DataDefinitionField dataDefinitionField = new DataDefinitionField();

		dataDefinitionField.setCustomProperties(
			HashMapBuilder.<String, Object>put(
				"fieldReference", _fieldReference
			).build());
		dataDefinitionField.setName(_fieldName);

		dataDefinition.setDataDefinitionFields(
			new DataDefinitionField[] {dataDefinitionField});

		dataDefinition.setDefaultDataLayout(_getDataLayout(_fieldName));

		return dataDefinition;
	}

	private String _getDataDefinitionFieldName(DataDefinition dataDefinition) {
		DataDefinitionField dataDefinitionField =
			dataDefinition.getDataDefinitionFields()[0];

		return dataDefinitionField.getName();
	}

	private DataLayout _getDataLayout(String fieldName) {
		DataLayout dataLayout = new DataLayout();

		dataLayout.setDataLayoutPages(
			new DataLayoutPage[] {
				new DataLayoutPage() {
					{
						dataLayoutRows = new DataLayoutRow[] {
							new DataLayoutRow() {
								{
									dataLayoutColumns = new DataLayoutColumn[] {
										new DataLayoutColumn() {
											{
												fieldNames = new String[] {
													fieldName
												};
											}
										}
									};
								}
							}
						};
						description = HashMapBuilder.<String, Object>put(
							"en_US", "Description"
						).build();
						title = HashMapBuilder.<String, Object>put(
							"en_US", "Title"
						).build();
					}
				}
			});

		return dataLayout;
	}

	private DDMStructure _getDDMStructure(String existingFieldName) {
		DDMStructure ddmStructure = Mockito.mock(DDMStructure.class);

		DDMForm ddmForm = Mockito.mock(DDMForm.class);

		DDMFormField ddmFormField = new DDMFormField(existingFieldName, "text");

		ddmFormField.setFieldReference(_fieldReference);

		Mockito.when(
			ddmForm.getDDMFormFieldsReferencesMap(true)
		).thenReturn(
			Map.of(_fieldReference, ddmFormField)
		);

		Mockito.when(
			ddmStructure.getDDMForm()
		).thenReturn(
			ddmForm
		);

		return ddmStructure;
	}

	private void _testUpdateDataDefinitionFields() {
		DataDefinition dataDefinition = _getDataDefinition();

		DataDefinitionUtil.updateDataDefinitionFields(dataDefinition, null);

		Assert.assertTrue(
			DataDefinitionUtil.isValidFieldName(
				_getDataDefinitionFieldName(dataDefinition)));
	}

	private void _testUpdateDataDefinitionFieldsWithExistingFieldName() {
		DataDefinition dataDefinition = _getDataDefinition();

		String existingFieldName = DDMFormFieldUtil.getDDMFormFieldName(
			_fieldName);

		DataDefinitionUtil.updateDataDefinitionFields(
			dataDefinition, _getDDMStructure(existingFieldName));

		String newFieldName = _getDataDefinitionFieldName(dataDefinition);

		Assert.assertTrue(DataDefinitionUtil.isValidFieldName(newFieldName));
		Assert.assertEquals(existingFieldName, newFieldName);
	}

	private void _testUpdateDataDefinitionFieldsWithExistingFieldNameAndExistingReference() {
		String originalFieldName = _fieldName;

		String existingFieldName = DDMFormFieldUtil.getDDMFormFieldName(
			_fieldName);

		_fieldName = DDMFormFieldUtil.getDDMFormFieldName(_fieldName);

		DataDefinition dataDefinition = _getDataDefinition();

		DataDefinitionUtil.updateDataDefinitionFields(
			dataDefinition, _getDDMStructure(existingFieldName));

		String newFieldName = _getDataDefinitionFieldName(dataDefinition);

		Assert.assertTrue(DataDefinitionUtil.isValidFieldName(newFieldName));
		Assert.assertEquals(existingFieldName, newFieldName);

		_fieldName = originalFieldName;
	}

	private void _testUpdateDataDefinitionFieldsWithExistingLegacyFieldNameAndExistingReference() {
		DataDefinition dataDefinition = _getDataDefinition();

		DataDefinitionUtil.updateDataDefinitionFields(
			dataDefinition, _getDDMStructure(_fieldName));

		String newFieldName = _getDataDefinitionFieldName(dataDefinition);

		Assert.assertEquals(_fieldName, newFieldName);
	}

	private String _fieldName;
	private String _fieldReference;

}