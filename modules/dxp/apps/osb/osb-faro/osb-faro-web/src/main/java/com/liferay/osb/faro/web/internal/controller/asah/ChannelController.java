/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.asah;

import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.web.internal.annotations.TokenAuthentication;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.asah.FaroChannelDisplay;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Geyson Silva
 */
@Component(service = {ChannelController.class, FaroController.class})
@Path("/{lcpProjectId}/channel")
@Produces(MediaType.APPLICATION_JSON)
public class ChannelController extends BaseFaroController {

	@POST
	@TokenAuthentication
	public FaroChannelDisplay create(
			String body, @PathParam("lcpProjectId") String lcpProjectId)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject(body);

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByWeDeployKey(
				lcpProjectId + ".lfr.cloud");

		FaroChannel faroChannel = _faroChannelLocalService.addFaroChannel(
			getUserId(), jsonObject.getString("name"),
			jsonObject.getString("id"), faroProject.getGroupId());

		return new FaroChannelDisplay(faroChannel);
	}

	@Reference
	private FaroChannelLocalService _faroChannelLocalService;

	@Reference
	private JSONFactory _jsonFactory;

}