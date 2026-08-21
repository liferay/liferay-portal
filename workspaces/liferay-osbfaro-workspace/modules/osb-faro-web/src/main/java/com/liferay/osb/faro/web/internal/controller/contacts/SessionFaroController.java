/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.model.AcquisitionParameter;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.AcquisitionParameterDisplay;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.RoleConstants;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matthew Kong
 */
@Component(service = {FaroController.class, SessionFaroController.class})
@Path("/{groupId}/session")
@Produces(MediaType.APPLICATION_JSON)
public class SessionFaroController extends BaseFaroController {

	@GET
	@Path("/acquisition_parameters")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay getAcquisitionParametersFaroResultsDisplay(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") String channelId)
		throws Exception {

		List<AcquisitionParameter> acquisitionParameters =
			contactsEngineClient.getAcquisitionParameters(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				channelId);

		return new FaroResultsDisplay(
			TransformUtil.transform(
				acquisitionParameters, AcquisitionParameterDisplay::new),
			acquisitionParameters.size());
	}

	@GET
	@Path("/values")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay searchValues(
			@PathParam("groupId") long groupId,
			@QueryParam("channelId") String channelId,
			@QueryParam("fieldName") String fieldName,
			@QueryParam("filter") String filterString,
			@QueryParam("query") String query, @QueryParam("cur") int cur,
			@QueryParam("delta") int delta)
		throws Exception {

		return new FaroResultsDisplay(
			contactsEngineClient.getSessionValues(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				channelId, fieldName, filterString, query, cur, delta));
	}

}