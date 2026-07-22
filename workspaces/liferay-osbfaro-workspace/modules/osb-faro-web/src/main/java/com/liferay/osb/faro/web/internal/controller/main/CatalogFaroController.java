/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.engine.client.model.CatalogField;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.FaroFDSResultsDisplay;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Riccardo Ferrari
 */
@Component(service = {CatalogFaroController.class, FaroController.class})
@Path("/{groupId}/catalog")
@Produces(MediaType.APPLICATION_JSON)
public class CatalogFaroController extends BaseFaroController {

	@GET
	@Path("/fields")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroFDSResultsDisplay getCatalogFieldsFaroFDSResultsDisplay(
			@PathParam("groupId") long groupId,
			@QueryParam("query") String query,
			@QueryParam("tableName") String tableName,
			@QueryParam("page") int page, @QueryParam("pageSize") int pageSize,
			@DefaultValue(StringPool.BLANK) @QueryParam("sort") String
				sortString)
		throws Exception {

		Results<CatalogField> results = contactsEngineClient.getCatalogFields(
			faroProjectLocalService.getFaroProjectByGroupId(groupId), query,
			tableName, page, pageSize, sortString);

		return new FaroFDSResultsDisplay(results, page, pageSize);
	}

}