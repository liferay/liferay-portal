/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.model.FaroPreferences;
import com.liferay.osb.faro.service.FaroPreferencesLocalService;
import com.liferay.osb.faro.web.internal.constants.FaroPreferencesConstants;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.model.display.contacts.FaroGlobalPreferencesDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.portal.kernel.model.RoleConstants;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Martins
 */
@Component(service = GlobalPreferencesController.class)
@Path("/preferences")
@Produces(MediaType.APPLICATION_JSON)
public class GlobalPreferencesController extends BaseFaroController {

	@GET
	@RolesAllowed(RoleConstants.ADMINISTRATOR)
	public FaroGlobalPreferencesDisplay getFaroPreference() throws Exception {
		FaroPreferences faroPreferences =
			_faroPreferencesLocalService.fetchFaroPreferences(0L, 0L);

		if (faroPreferences != null) {
			return new FaroGlobalPreferencesDisplay(faroPreferences);
		}

		return new FaroGlobalPreferencesDisplay();
	}

	@DELETE
	@RolesAllowed(RoleConstants.ADMINISTRATOR)
	public void removeFaroPreference() {
		_faroPreferencesLocalService.deleteFaroPreferences(0, 0);
	}

	@POST
	@RolesAllowed(RoleConstants.ADMINISTRATOR)
	public FaroGlobalPreferencesDisplay saveFaroPreferences(
			@FormParam("preferences") FaroParam<Map<String, Object>>
				preferencesFaroParam,
			@DefaultValue(FaroPreferencesConstants.SCOPE_GROUP)
			@FormParam("scope")
			String scope)
		throws Exception {

		return new FaroGlobalPreferencesDisplay(
			_faroPreferencesLocalService.saveGlobalPreferences(
				JSONUtil.writeValueAsString(preferencesFaroParam.getValue())));
	}

	@Reference
	private FaroPreferencesLocalService _faroPreferencesLocalService;

}