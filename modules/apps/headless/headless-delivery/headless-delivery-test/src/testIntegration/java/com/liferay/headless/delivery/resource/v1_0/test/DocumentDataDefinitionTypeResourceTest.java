/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.headless.delivery.client.dto.v1_0.DataDefinitionField;
import com.liferay.headless.delivery.client.dto.v1_0.DataLayout;
import com.liferay.headless.delivery.client.dto.v1_0.DataLayoutColumn;
import com.liferay.headless.delivery.client.dto.v1_0.DataLayoutPage;
import com.liferay.headless.delivery.client.dto.v1_0.DataLayoutRow;
import com.liferay.headless.delivery.client.dto.v1_0.DocumentDataDefinitionType;
import com.liferay.headless.delivery.client.serdes.v1_0.DataDefinitionFieldSerDes;
import com.liferay.headless.delivery.client.serdes.v1_0.DataLayoutSerDes;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.GroupUtil;

import java.io.InputStream;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class DocumentDataDefinitionTypeResourceTest
	extends BaseDocumentDataDefinitionTypeResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Ignore
	@Override
	@Test
	public void testGraphQLPostSiteDocumentDataDefinitionType() {
	}

	@Override
	@Test
	public void testPostAssetLibraryDocumentDataDefinitionType()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			testDepotEntry.getGroupId(), DLFileEntryMetadata.class.getName());

		Long[] ddmStructureIds = {ddmStructure.getStructureId()};

		_assertDocumentDataDefinitionType(
			GroupUtil.getAssetLibraryKey(testDepotEntry.getGroup()),
			ddmStructureIds,
			documentDataDefinitionTypeResource.
				postAssetLibraryDocumentDataDefinitionType(
					testDepotEntry.getDepotEntryId(),
					_createDocumentDataDefinitionType(ddmStructureIds)),
			null);
	}

	@Override
	@Test
	public void testPostSiteDocumentDataDefinitionType() throws Exception {
		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			testGroup.getGroupId(), DLFileEntryMetadata.class.getName());

		DDMStructure ddmStructure2 = DDMStructureTestUtil.addStructure(
			testGroup.getGroupId(), DLFileEntryMetadata.class.getName());

		Long[] ddmStructureIds = {
			ddmStructure1.getStructureId(), ddmStructure2.getStructureId()
		};

		_assertDocumentDataDefinitionType(
			null, ddmStructureIds,
			documentDataDefinitionTypeResource.
				postSiteDocumentDataDefinitionType(
					testGroup.getGroupId(),
					_createDocumentDataDefinitionType(ddmStructureIds)),
			testGroup.getGroupId());
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"availableLanguages", "description", "name"};
	}

	@Override
	protected DocumentDataDefinitionType randomDocumentDataDefinitionType()
		throws Exception {

		DocumentDataDefinitionType documentDataDefinitionType =
			super.randomDocumentDataDefinitionType();

		documentDataDefinitionType.setAvailableLanguages(
			() -> LocaleUtil.toW3cLanguageIds(
				new Locale[] {LocaleUtil.getDefault()}));
		documentDataDefinitionType.setDataDefinitionFields(
			new DataDefinitionField[0]);
		documentDataDefinitionType.setDataLayout(new DataLayout());

		return documentDataDefinitionType;
	}

	@Override
	protected DocumentDataDefinitionType
			testGraphQLDocumentDataDefinitionType_addDocumentDataDefinitionType()
		throws Exception {

		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			testGroup.getGroupId(), DLFileEntryMetadata.class.getName());

		return documentDataDefinitionTypeResource.
			postSiteDocumentDataDefinitionType(
				testGroup.getGroupId(),
				_createDocumentDataDefinitionType(
					new Long[] {ddmStructure1.getStructureId()}));
	}

	@Override
	protected DocumentDataDefinitionType
			testGraphQLSiteDocumentDataDefinitionType_addDocumentDataDefinitionType(
				Long siteId,
				DocumentDataDefinitionType documentDataDefinitionType)
		throws Exception {

		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			siteId, DLFileEntryMetadata.class.getName());

		return documentDataDefinitionTypeResource.
			postSiteDocumentDataDefinitionType(
				siteId,
				_createDocumentDataDefinitionType(
					new Long[] {ddmStructure1.getStructureId()}));
	}

	private void _assertDataDefinitionField(
		DataDefinitionField actualDataDefinitionField,
		DataDefinitionField expectedDataDefinitionField) {

		_assertDataDefinitionFieldCustomProperties(
			actualDataDefinitionField.getCustomProperties(),
			expectedDataDefinitionField.getCustomProperties());

		Assert.assertEquals(
			expectedDataDefinitionField.getFieldType(),
			actualDataDefinitionField.getFieldType());
		Assert.assertEquals(
			expectedDataDefinitionField.getIndexType(),
			actualDataDefinitionField.getIndexType());
		Assert.assertEquals(
			expectedDataDefinitionField.getIndexable(),
			actualDataDefinitionField.getIndexable());
		Assert.assertEquals(
			expectedDataDefinitionField.getLabel(),
			actualDataDefinitionField.getLabel());
		Assert.assertEquals(
			expectedDataDefinitionField.getLocalizable(),
			actualDataDefinitionField.getLocalizable());
		Assert.assertEquals(
			expectedDataDefinitionField.getName(),
			actualDataDefinitionField.getName());
		Assert.assertEquals(
			expectedDataDefinitionField.getReadOnly(),
			actualDataDefinitionField.getReadOnly());
		Assert.assertEquals(
			expectedDataDefinitionField.getRepeatable(),
			actualDataDefinitionField.getRepeatable());
		Assert.assertEquals(
			expectedDataDefinitionField.getRequired(),
			actualDataDefinitionField.getRequired());
		Assert.assertEquals(
			expectedDataDefinitionField.getShowLabel(),
			actualDataDefinitionField.getShowLabel());
	}

	private void _assertDataDefinitionFieldCustomProperties(
		Map<String, Object> actualDataDefinitionFieldCustomProperties,
		Map<String, Object> expectedDataDefinitionFieldCustomProperties) {

		Assert.assertEquals(
			expectedDataDefinitionFieldCustomProperties.get("dataType"),
			actualDataDefinitionFieldCustomProperties.get("dataType"));
		Assert.assertEquals(
			expectedDataDefinitionFieldCustomProperties.get("fieldReference"),
			actualDataDefinitionFieldCustomProperties.get("fieldReference"));
	}

	private void _assertDataDefinitionFields(
		DataDefinitionField[] actualDataDefinitionFields,
		DataDefinitionField[] expectedDataDefinitionFields) {

		Assert.assertNotNull(actualDataDefinitionFields);
		Assert.assertEquals(
			Arrays.toString(actualDataDefinitionFields),
			expectedDataDefinitionFields.length,
			actualDataDefinitionFields.length);

		for (DataDefinitionField expectedDataDefinitionField :
				expectedDataDefinitionFields) {

			DataDefinitionField actualDataDefinitionField =
				_getDataDefinitionField(
					actualDataDefinitionFields,
					expectedDataDefinitionField.getName());

			Assert.assertNotNull(actualDataDefinitionField);
			_assertDataDefinitionField(
				actualDataDefinitionField, expectedDataDefinitionField);
		}
	}

	private void _assertDataLayout(
		DataLayout actualDataLayout, DataLayout expectedDataLayout) {

		Assert.assertNotNull(actualDataLayout);
		Assert.assertNotNull(actualDataLayout.getDataDefinitionId());
		Assert.assertNotNull(actualDataLayout.getDataLayoutKey());

		DataLayoutPage[] actualDataLayoutPages =
			actualDataLayout.getDataLayoutPages();

		Assert.assertNotNull(actualDataLayoutPages);

		DataLayoutPage[] expectedDataLayoutDataLayoutPages =
			expectedDataLayout.getDataLayoutPages();

		Assert.assertEquals(
			Arrays.toString(actualDataLayoutPages),
			expectedDataLayoutDataLayoutPages.length,
			actualDataLayoutPages.length);

		for (int i = 0; i < expectedDataLayoutDataLayoutPages.length; i++) {
			_assertDataLayoutPage(
				actualDataLayoutPages[i], expectedDataLayoutDataLayoutPages[i]);
		}
	}

	private void _assertDataLayoutPage(
		DataLayoutPage actualDataLayoutPage,
		DataLayoutPage expectedDataLayoutPage) {

		DataLayoutRow[] actualDataLayoutRows =
			actualDataLayoutPage.getDataLayoutRows();

		Assert.assertNotNull(actualDataLayoutRows);

		DataLayoutRow[] expectedDataLayoutRows =
			expectedDataLayoutPage.getDataLayoutRows();

		Assert.assertEquals(
			Arrays.toString(actualDataLayoutRows),
			expectedDataLayoutRows.length, actualDataLayoutRows.length);

		for (int i = 0; i < expectedDataLayoutRows.length; i++) {
			_assertDataLayoutRow(
				actualDataLayoutRows[i], expectedDataLayoutRows[i]);
		}
	}

	private void _assertDataLayoutRow(
		DataLayoutRow actualDataLayoutRow,
		DataLayoutRow expectedDataLayoutRow) {

		DataLayoutColumn[] actualDataLayoutColumns =
			actualDataLayoutRow.getDataLayoutColumns();

		Assert.assertNotNull(actualDataLayoutColumns);

		DataLayoutColumn[] expectedDataLayoutColumns =
			expectedDataLayoutRow.getDataLayoutColumns();

		Assert.assertEquals(
			Arrays.toString(actualDataLayoutColumns),
			expectedDataLayoutColumns.length, actualDataLayoutColumns.length);

		for (int i = 0; i < expectedDataLayoutColumns.length; i++) {
			DataLayoutColumn actualDataLayoutColumn =
				actualDataLayoutColumns[i];

			Assert.assertNotNull(actualDataLayoutColumn);

			DataLayoutColumn expectedDataLayoutColumn =
				expectedDataLayoutColumns[i];

			Assert.assertEquals(
				expectedDataLayoutColumn.getColumnSize(),
				actualDataLayoutColumn.getColumnSize());
			Assert.assertEquals(
				expectedDataLayoutColumn.getFieldNames(),
				actualDataLayoutColumn.getFieldNames());
		}
	}

	private void _assertDocumentDataDefinitionType(
			String assetLibraryKey, Long[] ddmStructureIds,
			DocumentDataDefinitionType documentDataDefinitionType, Long groupId)
		throws Exception {

		Assert.assertNotNull(documentDataDefinitionType);

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.getDLFileEntryType(
				documentDataDefinitionType.getId());

		Assert.assertNotNull(dlFileEntryType);

		Assert.assertEquals(
			assetLibraryKey, documentDataDefinitionType.getAssetLibraryKey());
		Assert.assertEquals(
			LocaleUtil.toW3cLanguageIds(
				new Locale[] {LocaleUtil.US, LocaleUtil.SPAIN}),
			documentDataDefinitionType.getAvailableLanguages());
		Assert.assertNotNull(documentDataDefinitionType.getCreator());
		_assertDataDefinitionFields(
			documentDataDefinitionType.getDataDefinitionFields(),
			DataDefinitionFieldSerDes.toDTOs(_read("test-ddm-fields.json")));
		_assertDataLayout(
			documentDataDefinitionType.getDataLayout(),
			DataLayoutSerDes.toDTO(_read("test-data-layout.json")));
		Assert.assertNotNull(documentDataDefinitionType.getDateCreated());
		Assert.assertNotNull(documentDataDefinitionType.getDateModified());
		Assert.assertEquals(
			HashMapBuilder.put(
				LocaleUtil.SPAIN,
				"Descripción de definición de datos del documento"
			).put(
				LocaleUtil.US, "Document data definition description"
			).build(),
			dlFileEntryType.getDescriptionMap());

		Long[] documentMetadataSetIds =
			documentDataDefinitionType.getDocumentMetadataSetIds();

		Assert.assertEquals(
			documentMetadataSetIds.toString(), ddmStructureIds.length,
			documentMetadataSetIds.length);

		for (Long documentMetadataSetId : documentMetadataSetIds) {
			Assert.assertTrue(
				ArrayUtil.contains(ddmStructureIds, documentMetadataSetId));
		}

		Assert.assertNotNull(
			documentDataDefinitionType.getExternalReferenceCode());
		Assert.assertNotNull(documentDataDefinitionType.getId());
		Assert.assertEquals(
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "Definición de datos del documento"
			).put(
				LocaleUtil.US, "Document data definition"
			).build(),
			dlFileEntryType.getNameMap());
		Assert.assertEquals(groupId, documentDataDefinitionType.getSiteId());
	}

	private DocumentDataDefinitionType _createDocumentDataDefinitionType(
			Long[] ddmStructureIds)
		throws Exception {

		return new DocumentDataDefinitionType() {
			{
				setAvailableLanguages(new String[] {"en_US", "es_ES"});
				setDataDefinitionFields(
					DataDefinitionFieldSerDes.toDTOs(
						_read("test-ddm-fields.json")));
				setDataLayout(
					DataLayoutSerDes.toDTO(_read("test-data-layout.json")));
				setDescription("Document data definition description");
				setDescription_i18n(
					HashMapBuilder.put(
						"en-US", "Document data definition description"
					).put(
						"es-ES",
						"Descripción de definición de datos del documento"
					).build());
				setDocumentMetadataSetIds(ddmStructureIds);
				setName("Document data definition");
				setName_i18n(
					HashMapBuilder.put(
						"en-US", "Document data definition"
					).put(
						"es-ES", "Definición de datos del documento"
					).build());
			}
		};
	}

	private DataDefinitionField _getDataDefinitionField(
		DataDefinitionField[] dataDefinitionFields, String name) {

		for (DataDefinitionField dataDefinitionField : dataDefinitionFields) {
			if (Objects.equals(dataDefinitionField.getName(), name)) {
				return dataDefinitionField;
			}
		}

		return null;
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

}