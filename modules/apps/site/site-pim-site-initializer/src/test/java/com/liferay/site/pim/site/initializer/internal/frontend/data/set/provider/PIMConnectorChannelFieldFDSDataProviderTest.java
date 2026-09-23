/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.provider;

import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;
import com.liferay.site.pim.site.initializer.internal.frontend.data.set.model.PIMConnectorChannelFieldDisplay;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
public class PIMConnectorChannelFieldFDSDataProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_pimConnectorChannelFieldFDSDataProvider,
			"_objectDefinitionLocalService", _objectDefinitionLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorChannelFieldFDSDataProvider,
			"_objectEntryLocalService", _objectEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorChannelFieldFDSDataProvider,
			"_objectFieldLocalService", _objectFieldLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorChannelFieldFDSDataProvider,
			"_objectFolderLocalService", _objectFolderLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorChannelFieldFDSDataProvider,
			"_objectRelationshipLocalService", _objectRelationshipLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorChannelFieldFDSDataProvider, "_pimConnectorRegistry",
			_pimConnectorRegistry);

		_httpServletRequest = Mockito.mock(HttpServletRequest.class);

		Mockito.when(
			_httpServletRequest.getParameter("editFieldMappingURL")
		).thenReturn(
			"/web/pim/edit-field-mapping?objectEntryId=" + _OBJECT_ENTRY_ID
		);

		Mockito.when(
			_httpServletRequest.getParameter("objectEntryId")
		).thenReturn(
			String.valueOf(_OBJECT_ENTRY_ID)
		);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(Mockito.eq(LocaleUtil.US), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		languageUtil.setLanguage(language);

		_mockPIMObjectDefinition();
		_mockPIMObjectRelationship();
	}

	@Test
	public void testGetItems() throws Exception {
		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(_OBJECT_ENTRY_ID)
		).thenReturn(
			null
		);

		List<PIMConnectorChannelFieldDisplay> pimConnectorChannelFieldDisplays =
			_pimConnectorChannelFieldFDSDataProvider.getItems(
				_mockFDSKeywords(StringPool.BLANK), _mockFDSPagination(0, 20),
				_httpServletRequest, null);

		Assert.assertTrue(
			pimConnectorChannelFieldDisplays.toString(),
			pimConnectorChannelFieldDisplays.isEmpty());

		_mockPIMConnectorObjectEntry();

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			null
		);

		pimConnectorChannelFieldDisplays =
			_pimConnectorChannelFieldFDSDataProvider.getItems(
				_mockFDSKeywords(StringPool.BLANK), _mockFDSPagination(0, 20),
				_httpServletRequest, null);

		Assert.assertTrue(
			pimConnectorChannelFieldDisplays.toString(),
			pimConnectorChannelFieldDisplays.isEmpty());

		_mockPIMConnector();
		_mockPIMConnectorFieldMappingObjectEntries();

		pimConnectorChannelFieldDisplays =
			_pimConnectorChannelFieldFDSDataProvider.getItems(
				_mockFDSKeywords(StringPool.BLANK), _mockFDSPagination(0, 20),
				_httpServletRequest, null);

		Assert.assertEquals(
			pimConnectorChannelFieldDisplays.toString(), 2,
			pimConnectorChannelFieldDisplays.size());

		PIMConnectorChannelFieldDisplay pimConnectorChannelFieldDisplay =
			pimConnectorChannelFieldDisplays.get(0);

		Assert.assertEquals(
			"Name", pimConnectorChannelFieldDisplay.getChannelField());
		Assert.assertFalse(pimConnectorChannelFieldDisplay.isMapped());
		Assert.assertTrue(pimConnectorChannelFieldDisplay.isRequired());

		_mockPIMConnectorFieldMappingObjectEntries(
			_mockPIMConnectorFieldMappingObjectEntry(
				"skus[].sku", 0, "code", "dynamicValue", StringPool.BLANK));

		pimConnectorChannelFieldDisplays =
			_pimConnectorChannelFieldFDSDataProvider.getItems(
				_mockFDSKeywords(StringPool.BLANK), _mockFDSPagination(0, 20),
				_httpServletRequest, null);

		pimConnectorChannelFieldDisplay = pimConnectorChannelFieldDisplays.get(
			1);

		Assert.assertEquals(
			"SKU", pimConnectorChannelFieldDisplay.getChannelField());
		Assert.assertEquals(
			"/web/pim/edit-field-mapping?objectEntryId=" + _OBJECT_ENTRY_ID +
				"&channelField=skus%5B%5D.sku",
			pimConnectorChannelFieldDisplay.getHref());
		Assert.assertTrue(pimConnectorChannelFieldDisplay.isMapped());
		Assert.assertEquals(
			Collections.singletonList("Code"),
			pimConnectorChannelFieldDisplay.getSourceAttributes());

		_mockPIMConnectorFieldMappingObjectEntries(
			_mockPIMConnectorFieldMappingObjectEntry(
				"skus[].sku", 0, StringPool.BLANK, "fixedValue", "ABC-1"));

		pimConnectorChannelFieldDisplays =
			_pimConnectorChannelFieldFDSDataProvider.getItems(
				_mockFDSKeywords(StringPool.BLANK), _mockFDSPagination(0, 20),
				_httpServletRequest, null);

		pimConnectorChannelFieldDisplay = pimConnectorChannelFieldDisplays.get(
			1);

		Assert.assertEquals(
			"SKU", pimConnectorChannelFieldDisplay.getChannelField());
		Assert.assertTrue(pimConnectorChannelFieldDisplay.isMapped());
		Assert.assertEquals(
			Collections.singletonList("ABC-1"),
			pimConnectorChannelFieldDisplay.getSourceAttributes());

		_mockPIMConnectorFieldMappingObjectEntries(
			_mockPIMConnectorFieldMappingObjectEntry(
				"skus[].sku", 2, StringPool.BLANK, "fixedValue", "ABC-1"),
			_mockPIMConnectorFieldMappingObjectEntry(
				"skus[].sku", 1, "code", "dynamicValue", StringPool.BLANK));

		pimConnectorChannelFieldDisplays =
			_pimConnectorChannelFieldFDSDataProvider.getItems(
				_mockFDSKeywords(StringPool.BLANK), _mockFDSPagination(0, 20),
				_httpServletRequest, null);

		pimConnectorChannelFieldDisplay = pimConnectorChannelFieldDisplays.get(
			1);

		Assert.assertEquals(
			Arrays.asList("Code", "ABC-1"),
			pimConnectorChannelFieldDisplay.getSourceAttributes());

		pimConnectorChannelFieldDisplays =
			_pimConnectorChannelFieldFDSDataProvider.getItems(
				_mockFDSKeywords("sku"), _mockFDSPagination(0, 20),
				_httpServletRequest, null);

		Assert.assertEquals(
			pimConnectorChannelFieldDisplays.toString(), 1,
			pimConnectorChannelFieldDisplays.size());

		pimConnectorChannelFieldDisplay = pimConnectorChannelFieldDisplays.get(
			0);

		Assert.assertEquals(
			"SKU", pimConnectorChannelFieldDisplay.getChannelField());

		Sort sort = Mockito.mock(Sort.class);

		Mockito.when(
			sort.getFieldName()
		).thenReturn(
			"channelField"
		);

		Mockito.when(
			sort.isReverse()
		).thenReturn(
			true
		);

		pimConnectorChannelFieldDisplays =
			_pimConnectorChannelFieldFDSDataProvider.getItems(
				_mockFDSKeywords(StringPool.BLANK), _mockFDSPagination(0, 20),
				_httpServletRequest, sort);

		Assert.assertEquals(
			Arrays.asList("SKU", "Name"),
			TransformUtil.transform(
				pimConnectorChannelFieldDisplays,
				PIMConnectorChannelFieldDisplay::getChannelField));

		sort = Mockito.mock(Sort.class);

		Mockito.when(
			sort.getFieldName()
		).thenReturn(
			"required"
		);

		pimConnectorChannelFieldDisplays =
			_pimConnectorChannelFieldFDSDataProvider.getItems(
				_mockFDSKeywords(StringPool.BLANK), _mockFDSPagination(0, 20),
				_httpServletRequest, sort);

		Assert.assertEquals(
			Arrays.asList("SKU", "Name"),
			TransformUtil.transform(
				pimConnectorChannelFieldDisplays,
				PIMConnectorChannelFieldDisplay::getChannelField));

		PIMConnector pimConnector = Mockito.mock(PIMConnector.class);

		Mockito.when(
			pimConnector.getPIMConnectorChannelFields(LocaleUtil.US)
		).thenReturn(
			Collections.singletonList(
				new PIMConnectorChannelField("Tags", true, "tags", false))
		);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			pimConnector
		);

		pimConnectorChannelFieldDisplays =
			_pimConnectorChannelFieldFDSDataProvider.getItems(
				_mockFDSKeywords(StringPool.BLANK), _mockFDSPagination(0, 20),
				_httpServletRequest, null);

		Assert.assertEquals(
			pimConnectorChannelFieldDisplays.toString(), 1,
			pimConnectorChannelFieldDisplays.size());

		pimConnectorChannelFieldDisplay = pimConnectorChannelFieldDisplays.get(
			0);

		Assert.assertEquals(
			"Tags[]", pimConnectorChannelFieldDisplay.getChannelField());
	}

	@Test
	public void testGetItemsCount() throws Exception {
		_mockPIMConnector();
		_mockPIMConnectorObjectEntry();

		Assert.assertEquals(
			2,
			_pimConnectorChannelFieldFDSDataProvider.getItemsCount(
				_mockFDSKeywords(StringPool.BLANK), _httpServletRequest));

		Assert.assertEquals(
			1,
			_pimConnectorChannelFieldFDSDataProvider.getItemsCount(
				_mockFDSKeywords("sku"), _httpServletRequest));

		Mockito.verify(
			_objectEntryLocalService, Mockito.never()
		).getOneToManyObjectEntries(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.any(),
			Mockito.anyBoolean(), Mockito.anyLong(), Mockito.anyBoolean(),
			Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any()
		);

		Mockito.verify(
			_objectFolderLocalService, Mockito.never()
		).fetchObjectFolderByExternalReferenceCode(
			Mockito.anyString(), Mockito.anyLong()
		);
	}

	private FDSKeywords _mockFDSKeywords(String keywords) {
		FDSKeywords fdsKeywords = Mockito.mock(FDSKeywords.class);

		Mockito.when(
			fdsKeywords.getKeywords()
		).thenReturn(
			keywords
		);

		return fdsKeywords;
	}

	private FDSPagination _mockFDSPagination(
		int startPosition, int endPosition) {

		FDSPagination fdsPagination = Mockito.mock(FDSPagination.class);

		Mockito.when(
			fdsPagination.getEndPosition()
		).thenReturn(
			endPosition
		);

		Mockito.when(
			fdsPagination.getStartPosition()
		).thenReturn(
			startPosition
		);

		return fdsPagination;
	}

	private ObjectField _mockObjectField(String label, String name) {
		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getLabel(LocaleUtil.US)
		).thenReturn(
			label
		);

		Mockito.when(
			objectField.getName()
		).thenReturn(
			name
		);

		return objectField;
	}

	private void _mockPIMConnector() {
		PIMConnector pimConnector = Mockito.mock(PIMConnector.class);

		Mockito.when(
			pimConnector.getPIMConnectorChannelFields(LocaleUtil.US)
		).thenReturn(
			Arrays.asList(
				new PIMConnectorChannelField("Name", false, "name", true),
				new PIMConnectorChannelField("SKU", false, "skus[].sku", false))
		);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			pimConnector
		);
	}

	private void _mockPIMConnectorFieldMappingObjectEntries(
			ObjectEntry... objectEntries)
		throws Exception {

		Mockito.when(
			_objectEntryLocalService.getOneToManyObjectEntries(
				_GROUP_ID, _OBJECT_RELATIONSHIP_ID, null, false,
				_OBJECT_ENTRY_ID, true, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null)
		).thenReturn(
			Arrays.asList(objectEntries)
		);
	}

	private ObjectEntry _mockPIMConnectorFieldMappingObjectEntry(
		String channelFieldName, int priority, String sourceFieldName,
		String type, String value) {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"channelFieldName", channelFieldName
			).put(
				"priority", priority
			).put(
				"sourceClassName", StringPool.BLANK
			).put(
				"sourceFieldName", sourceFieldName
			).put(
				"type", type
			).put(
				"value", value
			).build()
		);

		return objectEntry;
	}

	private void _mockPIMConnectorObjectEntry() {
		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			objectEntry.getObjectDefinitionId()
		).thenReturn(
			_CONNECTOR_OBJECT_DEFINITION_ID
		);

		Mockito.when(
			objectEntry.getObjectEntryId()
		).thenReturn(
			_OBJECT_ENTRY_ID
		);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"key", _KEY
			).build()
		);

		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(_OBJECT_ENTRY_ID)
		).thenReturn(
			objectEntry
		);
	}

	private void _mockPIMObjectDefinition() {
		ObjectFolder objectFolder = Mockito.mock(ObjectFolder.class);

		Mockito.when(
			objectFolder.getObjectFolderId()
		).thenReturn(
			_OBJECT_FOLDER_ID
		);

		Mockito.when(
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES,
				_COMPANY_ID)
		).thenReturn(
			objectFolder
		);

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			_OBJECT_DEFINITION_ID
		);

		Mockito.when(
			_objectDefinitionLocalService.getObjectFolderObjectDefinitions(
				_OBJECT_FOLDER_ID)
		).thenReturn(
			Collections.singletonList(objectDefinition)
		);

		ObjectField objectField = _mockObjectField("Code", "code");

		Mockito.when(
			_objectFieldLocalService.getObjectFields(_OBJECT_DEFINITION_ID)
		).thenReturn(
			Collections.singletonList(objectField)
		);
	}

	private void _mockPIMObjectRelationship() {
		ObjectRelationship objectRelationship = Mockito.mock(
			ObjectRelationship.class);

		Mockito.when(
			objectRelationship.getObjectRelationshipId()
		).thenReturn(
			_OBJECT_RELATIONSHIP_ID
		);

		Mockito.when(
			_objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					"L_PIM_CONNECTOR_TO_PIM_CONNECTOR_FIELD_MAPPINGS",
					_CONNECTOR_OBJECT_DEFINITION_ID)
		).thenReturn(
			objectRelationship
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _CONNECTOR_OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _KEY = "liferay-commerce";

	private static final long _OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final long _OBJECT_ENTRY_ID = RandomTestUtil.randomLong();

	private static final long _OBJECT_FOLDER_ID = RandomTestUtil.randomLong();

	private static final long _OBJECT_RELATIONSHIP_ID =
		RandomTestUtil.randomLong();

	private HttpServletRequest _httpServletRequest;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final ObjectFieldLocalService _objectFieldLocalService =
		Mockito.mock(ObjectFieldLocalService.class);
	private final ObjectFolderLocalService _objectFolderLocalService =
		Mockito.mock(ObjectFolderLocalService.class);
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService = Mockito.mock(
			ObjectRelationshipLocalService.class);
	private final PIMConnectorChannelFieldFDSDataProvider
		_pimConnectorChannelFieldFDSDataProvider =
			new PIMConnectorChannelFieldFDSDataProvider();
	private final PIMConnectorRegistry _pimConnectorRegistry = Mockito.mock(
		PIMConnectorRegistry.class);

}