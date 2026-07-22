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
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
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
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.pim.site.initializer.internal.constants.PIMObjectDefinitionConstants;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serializable;

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

				for (ObjectEntry objectEntry :
						_objectEntryLocalService.getObjectEntries(
							depotEntry.getGroupId(),
							objectDefinition.getObjectDefinitionId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

					jsonArray.put(
						_toJSONObject(
							_objectEntryLocalService.getValues(objectEntry)));
				}
			}

			String json = jsonArray.toString();

			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse, "pim-products.json",
				json.getBytes(StringPool.UTF8), ContentTypes.APPLICATION_JSON,
				HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			_sendError(httpServletResponse);
		}
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

	private JSONObject _toJSONObject(Map<String, Serializable> values) {
		String code = MapUtil.getString(values, "code");

		return JSONUtil.put(
			"catalogId", "[$MASTER_CATALOG_ID$]"
		).put(
			"description",
			JSONUtil.put("en_US", MapUtil.getString(values, "description"))
		).put(
			"externalReferenceCode", code
		).put(
			"name", JSONUtil.put("en_US", MapUtil.getString(values, "name"))
		).put(
			"productType", "simple"
		).put(
			"skus",
			JSONUtil.put(
				JSONUtil.put(
					"published", true
				).put(
					"purchasable", true
				).put(
					"sku", code
				))
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportPIMBaseSkuToLiferayCommerceServlet.class);

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

}