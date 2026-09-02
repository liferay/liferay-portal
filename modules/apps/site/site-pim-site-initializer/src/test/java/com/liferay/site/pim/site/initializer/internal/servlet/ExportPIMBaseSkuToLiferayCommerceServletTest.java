/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.servlet;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Andrea Sbarra
 */
public class ExportPIMBaseSkuToLiferayCommerceServletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_exportPIMBaseSkuToLiferayCommerceServlet,
			"_depotEntryLocalService", _depotEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_exportPIMBaseSkuToLiferayCommerceServlet, "_filterFactory",
			_filterFactory);
		ReflectionTestUtil.setFieldValue(
			_exportPIMBaseSkuToLiferayCommerceServlet, "_jsonFactory",
			_jsonFactory);
		ReflectionTestUtil.setFieldValue(
			_exportPIMBaseSkuToLiferayCommerceServlet,
			"_objectDefinitionLocalService", _objectDefinitionLocalService);
		ReflectionTestUtil.setFieldValue(
			_exportPIMBaseSkuToLiferayCommerceServlet,
			"_objectEntryLocalService", _objectEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_exportPIMBaseSkuToLiferayCommerceServlet, "_portal", _portal);

		Mockito.when(
			_portal.getCompanyId(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			_COMPANY_ID
		);
	}

	@Test
	public void testDoGet() throws Exception {
		_testDoGet();
		_testDoGetWithMissingObjectDefinition();
		_testDoGetWithMultipleDepotEntries();
		_testDoGetWithPortalException();
		_testDoGetWithVariantPIMLink();
		_testDoGetWithVirtualSku();
		_testDoGetWithoutUnitOfMeasure();
	}

	private JSONObject _getJSONObject() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			_getMockHttpServletResponse();

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(1, jsonArray.length());

		return jsonArray.getJSONObject(0);
	}

	private MockHttpServletResponse _getMockHttpServletResponse()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_exportPIMBaseSkuToLiferayCommerceServlet.doGet(
			new MockHttpServletRequest(), mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private JSONObject _getSkuJSONObject(JSONObject jsonObject) {
		JSONArray jsonArray = jsonObject.getJSONArray("skus");

		Assert.assertEquals(1, jsonArray.length());

		return jsonArray.getJSONObject(0);
	}

	private JSONObject _getSkuUnitOfMeasureJSONObject(JSONObject jsonObject) {
		JSONArray jsonArray = jsonObject.getJSONArray("skuUnitOfMeasures");

		Assert.assertEquals(1, jsonArray.length());

		return jsonArray.getJSONObject(0);
	}

	private void _mockGetObjectEntries(
		long groupId, ObjectDefinition objectDefinition,
		ObjectEntry... objectEntries) {

		Mockito.when(
			_objectEntryLocalService.getObjectEntries(
				groupId, objectDefinition.getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS)
		).thenReturn(
			Arrays.asList(objectEntries)
		);
	}

	private ObjectDefinition _mockObjectDefinition() {
		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_BASE_SKU,
					_COMPANY_ID)
		).thenReturn(
			objectDefinition
		);

		return objectDefinition;
	}

	private ObjectEntry _mockObjectEntry(
			String code, String externalReferenceCode)
		throws Exception {

		return _mockObjectEntry(
			code, externalReferenceCode,
			Collections.<String, Serializable>emptyMap());
	}

	private ObjectEntry _mockObjectEntry(
			String code, String externalReferenceCode,
			Map<String, Serializable> values)
		throws Exception {

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		Mockito.when(
			_objectEntryLocalService.getValues(objectEntry)
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"code", code
			).put(
				"depth", 10.5D
			).put(
				"description", code + " description"
			).put(
				"height", 20.5D
			).put(
				"name", code + " name"
			).put(
				"unitOfMeasureAllowDecimalQuantities", true
			).put(
				"unitOfMeasureKey", "box"
			).put(
				"unitOfMeasureName", "Box"
			).put(
				"virtual", false
			).put(
				"weight", 30.5D
			).put(
				"width", 40.5D
			).putAll(
				values
			).build()
		);

		return objectEntry;
	}

	private void _mockSpaceDepotEntries(long... groupIds) {
		DepotEntry[] depotEntries = new DepotEntry[groupIds.length];

		for (int i = 0; i < groupIds.length; i++) {
			depotEntries[i] = Mockito.mock(DepotEntry.class);

			Mockito.when(
				depotEntries[i].getGroupId()
			).thenReturn(
				groupIds[i]
			);
		}

		Mockito.when(
			_depotEntryLocalService.getDepotEntries(
				_COMPANY_ID, DepotConstants.TYPE_SPACE)
		).thenReturn(
			Arrays.asList(depotEntries)
		);
	}

	private void _mockVariantPIMLinks(
			String clusterKey, String... externalReferenceCodes)
		throws Exception {

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_LINK,
					_COMPANY_ID)
		).thenReturn(
			objectDefinition
		);

		Predicate predicate = Mockito.mock(Predicate.class);

		Mockito.when(
			_filterFactory.create(
				Mockito.anyString(), Mockito.eq(objectDefinition))
		).thenReturn(
			predicate
		);

		Mockito.when(
			_objectEntryLocalService.getValuesList(
				_GROUP_ID, _COMPANY_ID, 0,
				objectDefinition.getObjectDefinitionId(), predicate, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)
		).thenReturn(
			TransformUtil.transform(
				Arrays.asList(externalReferenceCodes),
				externalReferenceCode ->
					HashMapBuilder.<String, Serializable>put(
						"clusterKey", clusterKey
					).put(
						"sourceClassExternalReferenceCode",
						externalReferenceCode
					).build())
		);
	}

	private void _testDoGet() throws Exception {
		_mockGetObjectEntries(
			_GROUP_ID, _mockObjectDefinition(),
			_mockObjectEntry("SKU-1", "SKU-1"));
		_mockSpaceDepotEntries(_GROUP_ID);

		MockHttpServletResponse mockHttpServletResponse =
			_getMockHttpServletResponse();

		Assert.assertEquals(
			ContentTypes.APPLICATION_JSON,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(
			HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT +
				"; filename=\"pim-products.json\"",
			mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION));
		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertTrue(jsonObject.getBoolean("active"));
		Assert.assertEquals(
			"[$MASTER_CATALOG_ID$]", jsonObject.getString("catalogId"));
		Assert.assertEquals(
			"SKU-1", jsonObject.getString("externalReferenceCode"));
		Assert.assertEquals("simple", jsonObject.getString("productType"));

		JSONObject descriptionJSONObject = jsonObject.getJSONObject(
			"description");

		Assert.assertEquals(
			"SKU-1 description", descriptionJSONObject.getString("en_US"));

		JSONObject nameJSONObject = jsonObject.getJSONObject("name");

		Assert.assertEquals("SKU-1 name", nameJSONObject.getString("en_US"));

		JSONArray skusJSONArray = jsonObject.getJSONArray("skus");

		Assert.assertEquals(1, skusJSONArray.length());

		JSONObject skuJSONObject = skusJSONArray.getJSONObject(0);

		Assert.assertEquals(10.5, skuJSONObject.getDouble("depth"), 0);
		Assert.assertEquals(20.5, skuJSONObject.getDouble("height"), 0);
		Assert.assertTrue(skuJSONObject.getBoolean("published"));
		Assert.assertTrue(skuJSONObject.getBoolean("purchasable"));
		Assert.assertEquals("SKU-1", skuJSONObject.getString("sku"));
		Assert.assertEquals(30.5, skuJSONObject.getDouble("weight"), 0);
		Assert.assertEquals(40.5, skuJSONObject.getDouble("width"), 0);

		JSONObject skuUnitOfMeasureJSONObject = _getSkuUnitOfMeasureJSONObject(
			skuJSONObject);

		Assert.assertEquals(
			1, skuUnitOfMeasureJSONObject.getInt("incrementalOrderQuantity"));
		Assert.assertEquals("box", skuUnitOfMeasureJSONObject.getString("key"));
		Assert.assertEquals(2, skuUnitOfMeasureJSONObject.getInt("precision"));
		Assert.assertTrue(skuUnitOfMeasureJSONObject.getBoolean("primary"));
		Assert.assertEquals(1, skuUnitOfMeasureJSONObject.getInt("rate"));

		JSONObject skuUnitOfMeasureNameJSONObject =
			skuUnitOfMeasureJSONObject.getJSONObject("name");

		Assert.assertEquals(
			"Box", skuUnitOfMeasureNameJSONObject.getString("en_US"));
	}

	private void _testDoGetWithMissingObjectDefinition() throws Exception {
		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_BASE_SKU,
					_COMPANY_ID)
		).thenReturn(
			null
		);

		MockHttpServletResponse mockHttpServletResponse =
			_getMockHttpServletResponse();

		Assert.assertEquals(
			JSONUtil.put(
				"error", "Unable to export the products"
			).toString(),
			mockHttpServletResponse.getContentAsString());
		Assert.assertEquals(
			ContentTypes.APPLICATION_JSON,
			mockHttpServletResponse.getContentType());
		Assert.assertNull(
			mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION));
		Assert.assertEquals(
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			mockHttpServletResponse.getStatus());
	}

	private void _testDoGetWithMultipleDepotEntries() throws Exception {
		ObjectDefinition objectDefinition = _mockObjectDefinition();

		_mockGetObjectEntries(
			_GROUP_ID, objectDefinition, _mockObjectEntry("SKU-1", "SKU-1"));
		_mockGetObjectEntries(
			_OTHER_GROUP_ID, objectDefinition,
			_mockObjectEntry("SKU-2", "SKU-2"));

		_mockSpaceDepotEntries(_GROUP_ID, _OTHER_GROUP_ID);

		MockHttpServletResponse mockHttpServletResponse =
			_getMockHttpServletResponse();

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(2, jsonArray.length());

		JSONObject jsonObject1 = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			"SKU-1", jsonObject1.getString("externalReferenceCode"));

		JSONObject jsonObject2 = jsonArray.getJSONObject(1);

		Assert.assertEquals(
			"SKU-2", jsonObject2.getString("externalReferenceCode"));
	}

	private void _testDoGetWithoutUnitOfMeasure() throws Exception {
		_mockGetObjectEntries(
			_GROUP_ID, _mockObjectDefinition(),
			_mockObjectEntry(
				"SKU-1", "SKU-1",
				HashMapBuilder.<String, Serializable>put(
					"unitOfMeasureKey", ""
				).build()));
		_mockSpaceDepotEntries(_GROUP_ID);

		JSONObject skuJSONObject = _getSkuJSONObject(_getJSONObject());

		Assert.assertEquals("SKU-1", skuJSONObject.getString("sku"));
		Assert.assertFalse(skuJSONObject.has("skuUnitOfMeasures"));
	}

	private void _testDoGetWithPortalException() throws Exception {
		ObjectEntry objectEntry = _mockObjectEntry("SKU-1", "SKU-1");

		_mockGetObjectEntries(_GROUP_ID, _mockObjectDefinition(), objectEntry);

		_mockSpaceDepotEntries(_GROUP_ID);

		Mockito.when(
			_objectEntryLocalService.getValues(objectEntry)
		).thenThrow(
			new PortalException()
		);

		MockHttpServletResponse mockHttpServletResponse =
			_getMockHttpServletResponse();

		Assert.assertEquals(
			JSONUtil.put(
				"error", "Unable to export the products"
			).toString(),
			mockHttpServletResponse.getContentAsString());
		Assert.assertEquals(
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			mockHttpServletResponse.getStatus());
	}

	private void _testDoGetWithVariantPIMLink() throws Exception {
		ObjectDefinition objectDefinition = _mockObjectDefinition();

		_mockGetObjectEntries(
			_GROUP_ID, objectDefinition, _mockObjectEntry("SKU-1", "ERC-1"),
			_mockObjectEntry("SKU-2", "ERC-2"),
			_mockObjectEntry("SKU-3", "ERC-3"));

		_mockSpaceDepotEntries(_GROUP_ID);

		_mockVariantPIMLinks("cluster-1", "ERC-1", "ERC-2");

		MockHttpServletResponse mockHttpServletResponse =
			_getMockHttpServletResponse();

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(2, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			"ERC-1", jsonObject.getString("externalReferenceCode"));
		Assert.assertEquals("simple", jsonObject.getString("productType"));

		JSONObject nameJSONObject = jsonObject.getJSONObject("name");

		Assert.assertEquals("SKU-1 name", nameJSONObject.getString("en_US"));

		JSONArray skusJSONArray = jsonObject.getJSONArray("skus");

		Assert.assertEquals(2, skusJSONArray.length());

		JSONObject skuJSONObject = skusJSONArray.getJSONObject(0);

		Assert.assertEquals("SKU-1", skuJSONObject.getString("sku"));

		skuJSONObject = skusJSONArray.getJSONObject(1);

		Assert.assertEquals("SKU-2", skuJSONObject.getString("sku"));

		jsonObject = jsonArray.getJSONObject(1);

		Assert.assertEquals(
			"ERC-3", jsonObject.getString("externalReferenceCode"));
		Assert.assertEquals("simple", jsonObject.getString("productType"));

		skusJSONArray = jsonObject.getJSONArray("skus");

		Assert.assertEquals(1, skusJSONArray.length());

		skuJSONObject = skusJSONArray.getJSONObject(0);

		Assert.assertEquals("SKU-3", skuJSONObject.getString("sku"));
	}

	private void _testDoGetWithVirtualSku() throws Exception {
		_mockGetObjectEntries(
			_GROUP_ID, _mockObjectDefinition(),
			_mockObjectEntry(
				"SKU-1", "SKU-1",
				HashMapBuilder.<String, Serializable>put(
					"virtual", true
				).build()));
		_mockSpaceDepotEntries(_GROUP_ID);

		JSONObject jsonObject = _getJSONObject();

		Assert.assertEquals("virtual", jsonObject.getString("productType"));
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final long _OTHER_GROUP_ID = RandomTestUtil.randomLong();

	private final DepotEntryLocalService _depotEntryLocalService = Mockito.mock(
		DepotEntryLocalService.class);
	private final ExportPIMBaseSkuToLiferayCommerceServlet
		_exportPIMBaseSkuToLiferayCommerceServlet =
			new ExportPIMBaseSkuToLiferayCommerceServlet();
	private final FilterFactory<Predicate> _filterFactory = Mockito.mock(
		FilterFactory.class);
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final Portal _portal = Mockito.mock(Portal.class);

}