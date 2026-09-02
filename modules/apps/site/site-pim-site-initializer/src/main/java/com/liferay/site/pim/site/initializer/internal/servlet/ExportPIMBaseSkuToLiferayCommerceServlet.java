/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.servlet;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.internal.link.VariantPIMLinkType;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.site.pim.site.initializer.internal.servlet.ExportPIMBaseSkuToLiferayCommerceServlet",
		"osgi.http.whiteboard.servlet.pattern=/pim/export-to-liferay-commerce/*",
		"servlet.init.httpMethods=GET"
	},
	service = Servlet.class
)
public class ExportPIMBaseSkuToLiferayCommerceServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		long companyId = _portal.getCompanyId(httpServletRequest);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_BASE_SKU,
					companyId);

		if (objectDefinition == null) {
			_sendError(httpServletResponse);

			return;
		}

		try {
			JSONArray jsonArray = _jsonFactory.createJSONArray();

			for (DepotEntry depotEntry :
					_depotEntryLocalService.getDepotEntries(
						companyId, DepotConstants.TYPE_SPACE)) {

				Map<String, String> clusterKeys = _getVariantPIMLinkClusterKeys(
					companyId, depotEntry.getGroupId());
				Map<String, List<ObjectEntry>> objectEntriesMap =
					new LinkedHashMap<>();

				for (ObjectEntry objectEntry :
						_objectEntryLocalService.getObjectEntries(
							depotEntry.getGroupId(),
							objectDefinition.getObjectDefinitionId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

					String externalReferenceCode =
						objectEntry.getExternalReferenceCode();

					List<ObjectEntry> objectEntries =
						objectEntriesMap.computeIfAbsent(
							GetterUtil.getString(
								clusterKeys.get(externalReferenceCode),
								externalReferenceCode),
							key -> new ArrayList<>());

					objectEntries.add(objectEntry);
				}

				for (List<ObjectEntry> objectEntries :
						objectEntriesMap.values()) {

					jsonArray.put(_toJSONObject(objectEntries));
				}
			}

			String json = jsonArray.toString();

			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse, "pim-products.json",
				json.getBytes(StringPool.UTF8), ContentTypes.APPLICATION_JSON,
				HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
		}
		catch (Exception exception) {
			_log.error(exception);

			_sendError(httpServletResponse);
		}
	}

	private Map<String, String> _getVariantPIMLinkClusterKeys(
			long companyId, long groupId)
		throws PortalException {

		Map<String, String> clusterKeys = new HashMap<>();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_LINK,
					companyId);

		if (objectDefinition == null) {
			return clusterKeys;
		}

		for (Map<String, Serializable> values :
				_objectEntryLocalService.getValuesList(
					groupId, companyId, 0,
					objectDefinition.getObjectDefinitionId(),
					_filterFactory.create(
						StringBundler.concat(
							"type eq '", VariantPIMLinkType.TYPE, "'"),
						objectDefinition),
					null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			String clusterKey = MapUtil.getString(values, "clusterKey");

			if (Validator.isNull(clusterKey)) {
				continue;
			}

			clusterKeys.put(
				MapUtil.getString(values, "sourceClassExternalReferenceCode"),
				clusterKey);
		}

		return clusterKeys;
	}

	private void _sendError(HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		ServletResponseUtil.write(
			httpServletResponse,
			JSONUtil.put(
				"error", "Unable to export the products"
			).toString());
	}

	private JSONObject _toJSONObject(List<ObjectEntry> objectEntries)
		throws Exception {

		ObjectEntry objectEntry = objectEntries.get(0);

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			objectEntry);

		return JSONUtil.put(
			"active", true
		).put(
			"catalogId", "[$MASTER_CATALOG_ID$]"
		).put(
			"description",
			JSONUtil.put("en_US", MapUtil.getString(values, "description"))
		).put(
			"externalReferenceCode", objectEntry.getExternalReferenceCode()
		).put(
			"name", JSONUtil.put("en_US", MapUtil.getString(values, "name"))
		).put(
			"productType",
			() -> {
				if (MapUtil.getBoolean(values, "virtual")) {
					return "virtual";
				}

				return "simple";
			}
		).put(
			"skus", JSONUtil.toJSONArray(objectEntries, this::_toSkuJSONObject)
		);
	}

	private JSONObject _toSkuJSONObject(ObjectEntry objectEntry)
		throws Exception {

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			objectEntry);

		JSONObject jsonObject = JSONUtil.put(
			"depth", MapUtil.getDouble(values, "depth")
		).put(
			"height", MapUtil.getDouble(values, "height")
		).put(
			"published", true
		).put(
			"purchasable", true
		).put(
			"sku", MapUtil.getString(values, "code")
		).put(
			"weight", MapUtil.getDouble(values, "weight")
		).put(
			"width", MapUtil.getDouble(values, "width")
		);

		String unitOfMeasureKey = MapUtil.getString(values, "unitOfMeasureKey");

		if (Validator.isNull(unitOfMeasureKey)) {
			return jsonObject;
		}

		int precision = 0;

		if (MapUtil.getBoolean(values, "unitOfMeasureAllowDecimalQuantities")) {
			precision = 2;
		}

		return jsonObject.put(
			"skuUnitOfMeasures",
			JSONUtil.putAll(
				JSONUtil.put(
					"incrementalOrderQuantity", 1
				).put(
					"key", unitOfMeasureKey
				).put(
					"name",
					JSONUtil.put(
						"en_US", MapUtil.getString(values, "unitOfMeasureName"))
				).put(
					"precision", precision
				).put(
					"primary", true
				).put(
					"rate", 1
				)));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportPIMBaseSkuToLiferayCommerceServlet.class);

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

}