/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.function.UnsafeTriFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorFieldMappingsUtil;

import java.io.Serializable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
@Component(service = PIMConnector.class)
public class LiferayCommercePIMConnector extends BasePIMConnector {

	public static final String KEY = "liferay-commerce";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public List<PIMConnectorChannelField> getPIMConnectorChannelFields() {
		return ListUtil.fromArray(
			_PIM_CONNECTOR_CHANNEL_FIELD_CATALOG_ID,
			_PIM_CONNECTOR_CHANNEL_FIELD_DEPTH,
			_PIM_CONNECTOR_CHANNEL_FIELD_DESCRIPTION,
			_PIM_CONNECTOR_CHANNEL_FIELD_HEIGHT,
			_PIM_CONNECTOR_CHANNEL_FIELD_NAME,
			_PIM_CONNECTOR_CHANNEL_FIELD_PRECISION,
			_PIM_CONNECTOR_CHANNEL_FIELD_PRODUCT_OPTIONS,
			_PIM_CONNECTOR_CHANNEL_FIELD_PRODUCT_SPECIFICATIONS,
			_PIM_CONNECTOR_CHANNEL_FIELD_PRODUCT_TYPE,
			_PIM_CONNECTOR_CHANNEL_FIELD_SKU, _PIM_CONNECTOR_CHANNEL_FIELD_TAGS,
			_PIM_CONNECTOR_CHANNEL_FIELD_UNIT_OF_MEASURE_KEY,
			_PIM_CONNECTOR_CHANNEL_FIELD_UNIT_OF_MEASURE_NAME,
			_PIM_CONNECTOR_CHANNEL_FIELD_WEIGHT,
			_PIM_CONNECTOR_CHANNEL_FIELD_WIDTH);
	}

	@Override
	public String getSettingsObjectDefinitionExternalReferenceCode() {
		return "L_PIM_CONNECTOR_SETTINGS_LIFERAY_COMMERCE";
	}

	@Override
	public boolean isActive(long companyId) {
		return true;
	}

	@Override
	protected String doExecute(
			ObjectEntry pimConnectorObjectEntry,
			Map<String, List<ObjectEntry>> pimFieldMappingObjectEntriesMap,
			Map<String, List<ObjectEntry>> pimProductObjectEntriesMap,
			Map<String, Serializable> settings)
		throws Exception {

		return String.valueOf(
			JSONUtil.toJSONArray(
				pimProductObjectEntriesMap.entrySet(),
				entry -> _createProductJSONObject(
					entry.getKey(), pimFieldMappingObjectEntriesMap,
					entry.getValue())));
	}

	private JSONObject _createLocalizedJSONObject(
		ObjectDefinition objectDefinition,
		Map<String, List<ObjectEntry>> objectEntriesMap,
		PIMConnectorChannelField pimConnectorChannelField,
		Map<String, Serializable> values) {

		Object value = PIMConnectorFieldMappingsUtil.getChannelFieldValue(
			objectDefinition, objectEntriesMap, pimConnectorChannelField,
			values);

		if (value == null) {
			return null;
		}

		return JSONUtil.put("en_US", value);
	}

	private JSONObject _createProductJSONObject(
			String externalReferenceCode,
			Map<String, List<ObjectEntry>> pimFieldMappingObjectEntriesMap,
			List<ObjectEntry> pimProductObjectEntries)
		throws PortalException {

		ObjectEntry objectEntry = pimProductObjectEntries.get(0);

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		List<Map<String, Serializable>> valuesList = TransformUtil.transform(
			pimProductObjectEntries, objectEntryLocalService::getValues);

		Map<String, Serializable> values = valuesList.get(0);

		return JSONUtil.put(
			"active", true
		).put(
			"catalogId",
			() -> PIMConnectorFieldMappingsUtil.getChannelFieldValue(
				objectDefinition, pimFieldMappingObjectEntriesMap,
				_PIM_CONNECTOR_CHANNEL_FIELD_CATALOG_ID, values)
		).put(
			"description",
			() -> _createLocalizedJSONObject(
				objectDefinition, pimFieldMappingObjectEntriesMap,
				_PIM_CONNECTOR_CHANNEL_FIELD_DESCRIPTION, values)
		).put(
			"externalReferenceCode", externalReferenceCode
		).put(
			"name",
			() -> _createLocalizedJSONObject(
				objectDefinition, pimFieldMappingObjectEntriesMap,
				_PIM_CONNECTOR_CHANNEL_FIELD_NAME, values)
		).put(
			"productOptions",
			() -> _toJSONArray(
				objectDefinition,
				pimFieldMappingObjectEntriesMap.get(
					_PIM_CONNECTOR_CHANNEL_FIELD_PRODUCT_OPTIONS.getName()),
				(curObjectEntry, sourceFieldName, priority) ->
					_createProductOptionJSONObject(
						objectDefinition, curObjectEntry, priority,
						sourceFieldName, valuesList))
		).put(
			"productSpecifications",
			() -> _toJSONArray(
				objectDefinition,
				pimFieldMappingObjectEntriesMap.get(
					_PIM_CONNECTOR_CHANNEL_FIELD_PRODUCT_SPECIFICATIONS.
						getName()),
				(curObjectEntry, sourceFieldName, priority) ->
					_createProductSpecificationJSONObject(
						objectDefinition, curObjectEntry, priority,
						sourceFieldName, values))
		).put(
			"productType",
			() -> {
				Object productType =
					PIMConnectorFieldMappingsUtil.getChannelFieldValue(
						objectDefinition, pimFieldMappingObjectEntriesMap,
						_PIM_CONNECTOR_CHANNEL_FIELD_PRODUCT_TYPE, values);

				if (Objects.equals(productType, StringPool.FALSE)) {
					return "simple";
				}

				if (Objects.equals(productType, StringPool.TRUE)) {
					return "virtual";
				}

				return productType;
			}
		).put(
			"skus",
			() -> JSONUtil.toJSONArray(
				pimProductObjectEntries,
				pimProductObjectEntry -> _createSkuJSONObject(
					pimProductObjectEntry.getExternalReferenceCode(),
					objectDefinition, pimFieldMappingObjectEntriesMap,
					valuesList.get(
						pimProductObjectEntries.indexOf(
							pimProductObjectEntry))))
		).put(
			"tags",
			() -> PIMConnectorFieldMappingsUtil.getChannelFieldValue(
				objectDefinition, pimFieldMappingObjectEntriesMap,
				_PIM_CONNECTOR_CHANNEL_FIELD_TAGS, values)
		);
	}

	private JSONObject _createProductOptionJSONObject(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			int priority, String sourceFieldName,
			List<Map<String, Serializable>> valuesList)
		throws Exception {

		AtomicInteger atomicInteger = new AtomicInteger();
		Set<String> valueKeys = new HashSet<>();

		JSONArray jsonArray = JSONUtil.toJSONArray(
			valuesList,
			values -> {
				String value = PIMConnectorFieldMappingsUtil.getValue(
					objectDefinition, objectEntry, values);

				if (Validator.isNull(value)) {
					return null;
				}

				String valueKey = _friendlyURLNormalizer.normalize(value);

				if (!valueKeys.add(valueKey)) {
					return null;
				}

				return JSONUtil.put(
					"key", valueKey
				).put(
					"name", JSONUtil.put("en_US", value)
				).put(
					"priority", atomicInteger.getAndIncrement()
				);
			});

		if (jsonArray.length() == 0) {
			return null;
		}

		return JSONUtil.put(
			"fieldType", "select"
		).put(
			"key", sourceFieldName
		).put(
			"name",
			JSONUtil.put(
				"en_US",
				PIMConnectorFieldMappingsUtil.getLabel(
					objectDefinition, objectEntry))
		).put(
			"optionExternalReferenceCode", sourceFieldName
		).put(
			"optionId", 0
		).put(
			"priority", priority
		).put(
			"productOptionValues", jsonArray
		).put(
			"skuContributor", true
		);
	}

	private JSONObject _createProductSpecificationJSONObject(
		ObjectDefinition objectDefinition, ObjectEntry objectEntry,
		int priority, String sourceFieldName,
		Map<String, Serializable> values) {

		String value = PIMConnectorFieldMappingsUtil.getValue(
			objectDefinition, objectEntry, values);

		if (Validator.isNull(value)) {
			return null;
		}

		return JSONUtil.put(
			"label",
			JSONUtil.put(
				"en_US",
				PIMConnectorFieldMappingsUtil.getLabel(
					objectDefinition, objectEntry))
		).put(
			"priority", priority
		).put(
			"specificationKey", sourceFieldName
		).put(
			"value", JSONUtil.put("en_US", value)
		);
	}

	private JSONObject _createSkuJSONObject(
		String externalReferenceCode, ObjectDefinition objectDefinition,
		Map<String, List<ObjectEntry>> objectEntriesMap,
		Map<String, Serializable> values) {

		return JSONUtil.put(
			"depth",
			() -> PIMConnectorFieldMappingsUtil.getChannelFieldValue(
				objectDefinition, objectEntriesMap,
				_PIM_CONNECTOR_CHANNEL_FIELD_DEPTH, values)
		).put(
			"externalReferenceCode", externalReferenceCode
		).put(
			"height",
			() -> PIMConnectorFieldMappingsUtil.getChannelFieldValue(
				objectDefinition, objectEntriesMap,
				_PIM_CONNECTOR_CHANNEL_FIELD_HEIGHT, values)
		).put(
			"published", true
		).put(
			"purchasable", true
		).put(
			"sku",
			() -> PIMConnectorFieldMappingsUtil.getChannelFieldValue(
				objectDefinition, objectEntriesMap,
				_PIM_CONNECTOR_CHANNEL_FIELD_SKU, values)
		).put(
			"skuOptions",
			() -> _toJSONArray(
				objectDefinition,
				objectEntriesMap.get(
					_PIM_CONNECTOR_CHANNEL_FIELD_PRODUCT_OPTIONS.getName()),
				(objectEntry, sourceFieldName, priority) ->
					_createSkuOptionJSONObject(
						objectDefinition, objectEntry, sourceFieldName, values))
		).put(
			"skuUnitOfMeasures",
			() -> {
				Object unitOfMeasureKey =
					PIMConnectorFieldMappingsUtil.getChannelFieldValue(
						objectDefinition, objectEntriesMap,
						_PIM_CONNECTOR_CHANNEL_FIELD_UNIT_OF_MEASURE_KEY,
						values);

				if (unitOfMeasureKey == null) {
					return null;
				}

				return JSONUtil.putAll(
					JSONUtil.put(
						"incrementalOrderQuantity", 1
					).put(
						"key", unitOfMeasureKey
					).put(
						"name",
						JSONUtil.put(
							"en_US",
							() ->
								PIMConnectorFieldMappingsUtil.
									getChannelFieldValue(
										objectDefinition, objectEntriesMap,
										_PIM_CONNECTOR_CHANNEL_FIELD_UNIT_OF_MEASURE_NAME,
										values))
					).put(
						"precision",
						() ->
							PIMConnectorFieldMappingsUtil.getChannelFieldValue(
								objectDefinition, objectEntriesMap,
								_PIM_CONNECTOR_CHANNEL_FIELD_PRECISION, values)
					).put(
						"primary", true
					).put(
						"rate", 1
					));
			}
		).put(
			"weight",
			() -> PIMConnectorFieldMappingsUtil.getChannelFieldValue(
				objectDefinition, objectEntriesMap,
				_PIM_CONNECTOR_CHANNEL_FIELD_WEIGHT, values)
		).put(
			"width",
			() -> PIMConnectorFieldMappingsUtil.getChannelFieldValue(
				objectDefinition, objectEntriesMap,
				_PIM_CONNECTOR_CHANNEL_FIELD_WIDTH, values)
		);
	}

	private JSONObject _createSkuOptionJSONObject(
		ObjectDefinition objectDefinition, ObjectEntry objectEntry,
		String sourceFieldName, Map<String, Serializable> values) {

		String value = _friendlyURLNormalizer.normalize(
			PIMConnectorFieldMappingsUtil.getValue(
				objectDefinition, objectEntry, values));

		if (Validator.isNull(value)) {
			return null;
		}

		return JSONUtil.put(
			"key", sourceFieldName
		).put(
			"value", value
		);
	}

	private JSONArray _toJSONArray(
			ObjectDefinition objectDefinition, List<ObjectEntry> objectEntries,
			UnsafeTriFunction
				<ObjectEntry, String, Integer, JSONObject, Exception>
					unsafeTriFunction)
		throws Exception {

		AtomicInteger atomicInteger = new AtomicInteger();
		Set<String> sourceFieldNames = new HashSet<>();

		JSONArray jsonArray = JSONUtil.toJSONArray(
			PIMConnectorFieldMappingsUtil.filterObjectEntries(
				objectDefinition, objectEntries),
			objectEntry -> {
				if (PIMConnectorFieldMappingsUtil.isFixedValue(objectEntry)) {
					return null;
				}

				String sourceFieldName = _friendlyURLNormalizer.normalize(
					MapUtil.getString(
						objectEntry.getValues(), "sourceFieldName"));

				if (!sourceFieldNames.add(sourceFieldName)) {
					return null;
				}

				JSONObject jsonObject = unsafeTriFunction.apply(
					objectEntry, sourceFieldName, atomicInteger.get());

				if (jsonObject == null) {
					return null;
				}

				atomicInteger.incrementAndGet();

				return jsonObject;
			});

		if (jsonArray.length() == 0) {
			return null;
		}

		return jsonArray;
	}

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_CATALOG_ID = new PIMConnectorChannelField(
			"catalog-id", false, "catalogId", true,
			ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_DEPTH = new PIMConnectorChannelField(
			"depth", false, "skus[].depth", false,
			ObjectFieldConstants.BUSINESS_TYPE_DECIMAL);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_DESCRIPTION = new PIMConnectorChannelField(
			"description", false, "description", false,
			ObjectFieldConstants.BUSINESS_TYPE_TEXT);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_HEIGHT = new PIMConnectorChannelField(
			"height", false, "skus[].height", false,
			ObjectFieldConstants.BUSINESS_TYPE_DECIMAL);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_NAME = new PIMConnectorChannelField(
			"name", false, "name", true,
			ObjectFieldConstants.BUSINESS_TYPE_TEXT);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_PRECISION = new PIMConnectorChannelField(
			"precision", false, "skus[].skuUnitOfMeasures[].precision", false,
			ObjectFieldConstants.BUSINESS_TYPE_INTEGER);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_PRODUCT_OPTIONS =
			new PIMConnectorChannelField(
				"product-options", true, "productOptions", false,
				ObjectFieldConstants.BUSINESS_TYPE_TEXT);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_PRODUCT_SPECIFICATIONS =
			new PIMConnectorChannelField(
				"product-specifications", true, "productSpecifications", false,
				ObjectFieldConstants.BUSINESS_TYPE_TEXT);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_PRODUCT_TYPE =
			new PIMConnectorChannelField(
				"product-type", false, "productType", true,
				ObjectFieldConstants.BUSINESS_TYPE_TEXT);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_SKU = new PIMConnectorChannelField(
			"sku", false, "skus[].sku", true,
			ObjectFieldConstants.BUSINESS_TYPE_TEXT);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_TAGS = new PIMConnectorChannelField(
			"tags", true, "tags", false,
			ObjectFieldConstants.BUSINESS_TYPE_TEXT);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_UNIT_OF_MEASURE_KEY =
			new PIMConnectorChannelField(
				"unit-of-measure-key", false, "skus[].skuUnitOfMeasures[].key",
				false, ObjectFieldConstants.BUSINESS_TYPE_TEXT);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_UNIT_OF_MEASURE_NAME =
			new PIMConnectorChannelField(
				"unit-of-measure-name", false,
				"skus[].skuUnitOfMeasures[].name", false,
				ObjectFieldConstants.BUSINESS_TYPE_TEXT);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_WEIGHT = new PIMConnectorChannelField(
			"weight", false, "skus[].weight", false,
			ObjectFieldConstants.BUSINESS_TYPE_DECIMAL);

	private static final PIMConnectorChannelField
		_PIM_CONNECTOR_CHANNEL_FIELD_WIDTH = new PIMConnectorChannelField(
			"width", false, "skus[].width", false,
			ObjectFieldConstants.BUSINESS_TYPE_DECIMAL);

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

}