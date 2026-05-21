/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.portal.kernel.model.RoleConstants;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Nilton Vieira
 */
@Component(service = DataSourceMetricsFaroController.class)
@Path("/{groupId}/data-source-metrics")
@Produces(MediaType.APPLICATION_JSON)
public class DataSourceMetricsFaroController extends BaseFaroController {

	@GET
	@Path("/{dataSourceId}/accounts_count")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public Long getAccountsCount(
			@PathParam("groupId") long groupId,
			@PathParam("dataSourceId") String dataSourceId)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		return contactsEngineClient.getDataSourceMetricsAccountsCount(
			faroProject, dataSourceId);
	}

	@GET
	@Path("/{dataSourceId}/events_count")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public Long getEventsCount(
			@PathParam("groupId") long groupId,
			@PathParam("dataSourceId") String dataSourceId)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		return contactsEngineClient.getDataSourceMetricsEventsCount(
			faroProject, dataSourceId);
	}

	@GET
	@Path("/{dataSourceId}/users_count")
	@RolesAllowed(RoleConstants.SITE_ADMINISTRATOR)
	public Long getUsersCount(
			@PathParam("groupId") long groupId,
			@PathParam("dataSourceId") String dataSourceId)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		return contactsEngineClient.getDataSourceMetricsUsersCount(
			faroProject, dataSourceId);
	}

}