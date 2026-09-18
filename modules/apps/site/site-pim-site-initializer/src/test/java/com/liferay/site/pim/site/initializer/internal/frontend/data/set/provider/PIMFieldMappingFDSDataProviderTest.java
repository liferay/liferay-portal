/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.provider;

import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorField;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.constants.PIMConnectorFieldConstants;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;
import com.liferay.site.pim.site.initializer.internal.frontend.data.set.model.PIMFieldMappingFDSEntry;

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
 */
public class PIMFieldMappingFDSDataProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_pimFieldMappingFDSDataProvider, "_jsonFactory", _jsonFactory);
		ReflectionTestUtil.setFieldValue(
			_pimFieldMappingFDSDataProvider, "_objectDefinitionLocalService",
			_objectDefinitionLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimFieldMappingFDSDataProvider, "_objectEntryLocalService",
			_objectEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimFieldMappingFDSDataProvider, "_objectFieldLocalService",
			_objectFieldLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimFieldMappingFDSDataProvider, "_objectFolderLocalService",
			_objectFolderLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimFieldMappingFDSDataProvider, "_pimConnectorRegistry",
			_pimConnectorRegistry);

		_httpServletRequest = Mockito.mock(HttpServletRequest.class);

		Mockito.when(
			_httpServletRequest.getParameter("mapChannelFieldURL")
		).thenReturn(
			"/web/pim/map-channel-field?objectEntryId=" + _OBJECT_ENTRY_ID
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

		_mockPIMObjectDefinition();
	}

	@Test
	public void testGetItems() throws Exception {
		_mockPIMConnector();
		_mockPIMConnectorObjectEntry(
			"{\"skus[].sku\": [{\"attribute\": \"code\", \"source\": \"\"}]}");

		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries =
			_pimFieldMappingFDSDataProvider.getItems(
				_getFDSKeywords(""), _getFDSPagination(0, 20),
				_httpServletRequest, null);

		Assert.assertEquals(
			pimFieldMappingFDSEntries.toString(), 2,
			pimFieldMappingFDSEntries.size());

		PIMFieldMappingFDSEntry pimFieldMappingFDSEntry =
			pimFieldMappingFDSEntries.get(0);

		Assert.assertEquals("Name", pimFieldMappingFDSEntry.getChannelField());
		Assert.assertTrue(pimFieldMappingFDSEntry.isRequired());
		Assert.assertFalse(pimFieldMappingFDSEntry.isMapped());

		pimFieldMappingFDSEntry = pimFieldMappingFDSEntries.get(1);

		Assert.assertEquals("SKU", pimFieldMappingFDSEntry.getChannelField());
		Assert.assertTrue(pimFieldMappingFDSEntry.isMapped());
		Assert.assertEquals(
			Collections.singletonList("Code"),
			pimFieldMappingFDSEntry.getSourceAttributes());
		Assert.assertEquals(
			"/web/pim/map-channel-field?objectEntryId=" + _OBJECT_ENTRY_ID +
				"&channelField=skus%5B%5D.sku",
			pimFieldMappingFDSEntry.getHref());
	}

	@Test
	public void testGetItemsCount() throws Exception {
		_mockPIMConnector();
		_mockPIMConnectorObjectEntry("{}");

		Assert.assertEquals(
			2,
			_pimFieldMappingFDSDataProvider.getItemsCount(
				_getFDSKeywords(""), _httpServletRequest));
	}

	@Test
	public void testGetItemsWithFixedValue() throws Exception {
		_mockPIMConnector();
		_mockPIMConnectorObjectEntry(
			"{\"skus[].sku\": [{\"type\": \"fixedValue\", \"value\": " +
				"\"ABC-1\"}]}");

		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries =
			_pimFieldMappingFDSDataProvider.getItems(
				_getFDSKeywords(""), _getFDSPagination(0, 20),
				_httpServletRequest, null);

		PIMFieldMappingFDSEntry pimFieldMappingFDSEntry =
			pimFieldMappingFDSEntries.get(1);

		Assert.assertEquals("SKU", pimFieldMappingFDSEntry.getChannelField());
		Assert.assertTrue(pimFieldMappingFDSEntry.isMapped());
		Assert.assertEquals(
			Collections.singletonList("ABC-1"),
			pimFieldMappingFDSEntry.getSourceAttributes());
	}

	@Test
	public void testGetItemsWithKeywords() throws Exception {
		_mockPIMConnector();
		_mockPIMConnectorObjectEntry("{}");

		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries =
			_pimFieldMappingFDSDataProvider.getItems(
				_getFDSKeywords("sku"), _getFDSPagination(0, 20),
				_httpServletRequest, null);

		Assert.assertEquals(
			pimFieldMappingFDSEntries.toString(), 1,
			pimFieldMappingFDSEntries.size());

		PIMFieldMappingFDSEntry pimFieldMappingFDSEntry =
			pimFieldMappingFDSEntries.get(0);

		Assert.assertEquals("SKU", pimFieldMappingFDSEntry.getChannelField());
	}

	@Test
	public void testGetItemsWithMappedFilter() throws Exception {
		_mockPIMConnector();
		_mockPIMConnectorObjectEntry(
			"{\"skus[].sku\": [{\"attribute\": \"code\", \"source\": \"\"}]}");

		Mockito.when(
			_httpServletRequest.getParameter("filter")
		).thenReturn(
			"mapped eq true"
		);

		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries =
			_pimFieldMappingFDSDataProvider.getItems(
				_getFDSKeywords(""), _getFDSPagination(0, 20),
				_httpServletRequest, null);

		Assert.assertEquals(
			pimFieldMappingFDSEntries.toString(), 1,
			pimFieldMappingFDSEntries.size());

		PIMFieldMappingFDSEntry pimFieldMappingFDSEntry =
			pimFieldMappingFDSEntries.get(0);

		Assert.assertEquals("SKU", pimFieldMappingFDSEntry.getChannelField());
	}

	@Test
	public void testGetItemsWithMissingObjectEntry() throws Exception {
		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(_OBJECT_ENTRY_ID)
		).thenReturn(
			null
		);

		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries =
			_pimFieldMappingFDSDataProvider.getItems(
				_getFDSKeywords(""), _getFDSPagination(0, 20),
				_httpServletRequest, null);

		Assert.assertTrue(
			pimFieldMappingFDSEntries.toString(),
			pimFieldMappingFDSEntries.isEmpty());
	}

	@Test
	public void testGetItemsWithMissingPIMConnector() throws Exception {
		_mockPIMConnectorObjectEntry("{}");

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			null
		);

		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries =
			_pimFieldMappingFDSDataProvider.getItems(
				_getFDSKeywords(""), _getFDSPagination(0, 20),
				_httpServletRequest, null);

		Assert.assertTrue(
			pimFieldMappingFDSEntries.toString(),
			pimFieldMappingFDSEntries.isEmpty());
	}

	@Test
	public void testGetItemsWithMultipleChannelField() throws Exception {
		PIMConnector pimConnector = Mockito.mock(PIMConnector.class);

		Mockito.when(
			pimConnector.getPIMConnectorFields(LocaleUtil.US)
		).thenReturn(
			Collections.singletonList(
				new PIMConnectorField(
					"Tags", true, "tags", false,
					PIMConnectorFieldConstants.TYPE_TEXT))
		);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			pimConnector
		);

		_mockPIMConnectorObjectEntry("{}");

		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries =
			_pimFieldMappingFDSDataProvider.getItems(
				_getFDSKeywords(""), _getFDSPagination(0, 20),
				_httpServletRequest, null);

		Assert.assertEquals(
			pimFieldMappingFDSEntries.toString(), 1,
			pimFieldMappingFDSEntries.size());

		PIMFieldMappingFDSEntry pimFieldMappingFDSEntry =
			pimFieldMappingFDSEntries.get(0);

		Assert.assertEquals(
			"Tags[]", pimFieldMappingFDSEntry.getChannelField());
	}

	@Test
	public void testGetItemsWithRequiredSort() throws Exception {
		_mockPIMConnector();
		_mockPIMConnectorObjectEntry("{}");

		Sort sort = Mockito.mock(Sort.class);

		Mockito.when(
			sort.getFieldName()
		).thenReturn(
			"required"
		);

		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries =
			_pimFieldMappingFDSDataProvider.getItems(
				_getFDSKeywords(""), _getFDSPagination(0, 20),
				_httpServletRequest, sort);

		Assert.assertEquals(
			Arrays.asList("SKU", "Name"),
			TransformUtil.transform(
				pimFieldMappingFDSEntries,
				PIMFieldMappingFDSEntry::getChannelField));
	}

	@Test
	public void testGetItemsWithSort() throws Exception {
		_mockPIMConnector();
		_mockPIMConnectorObjectEntry("{}");

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

		List<PIMFieldMappingFDSEntry> pimFieldMappingFDSEntries =
			_pimFieldMappingFDSDataProvider.getItems(
				_getFDSKeywords(""), _getFDSPagination(0, 20),
				_httpServletRequest, sort);

		Assert.assertEquals(
			Arrays.asList("SKU", "Name"),
			TransformUtil.transform(
				pimFieldMappingFDSEntries,
				PIMFieldMappingFDSEntry::getChannelField));
	}

	private FDSKeywords _getFDSKeywords(String keywords) {
		FDSKeywords fdsKeywords = Mockito.mock(FDSKeywords.class);

		Mockito.when(
			fdsKeywords.getKeywords()
		).thenReturn(
			keywords
		);

		return fdsKeywords;
	}

	private FDSPagination _getFDSPagination(
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
			objectField.getBusinessType()
		).thenReturn(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT
		);

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
			pimConnector.getPIMConnectorFields(LocaleUtil.US)
		).thenReturn(
			Arrays.asList(
				new PIMConnectorField(
					"Name", false, "name", true,
					PIMConnectorFieldConstants.TYPE_LOCALIZED_TEXT),
				new PIMConnectorField(
					"SKU", false, "skus[].sku", false,
					PIMConnectorFieldConstants.TYPE_TEXT))
		);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			pimConnector
		);
	}

	private void _mockPIMConnectorObjectEntry(String fieldMapping) {
		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"fieldMapping", fieldMapping
			).put(
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

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _KEY = "liferay-commerce";

	private static final long _OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final long _OBJECT_ENTRY_ID = RandomTestUtil.randomLong();

	private static final long _OBJECT_FOLDER_ID = RandomTestUtil.randomLong();

	private HttpServletRequest _httpServletRequest;
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final ObjectFieldLocalService _objectFieldLocalService =
		Mockito.mock(ObjectFieldLocalService.class);
	private final ObjectFolderLocalService _objectFolderLocalService =
		Mockito.mock(ObjectFolderLocalService.class);
	private final PIMConnectorRegistry _pimConnectorRegistry = Mockito.mock(
		PIMConnectorRegistry.class);
	private final PIMFieldMappingFDSDataProvider
		_pimFieldMappingFDSDataProvider = new PIMFieldMappingFDSDataProvider();

}