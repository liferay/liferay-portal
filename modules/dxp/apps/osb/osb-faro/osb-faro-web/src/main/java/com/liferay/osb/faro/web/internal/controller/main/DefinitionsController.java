/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.engine.client.model.FieldMapping;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.AttributesDisplay;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
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
 * @author Rachael Koestartyo
 */
@Component(service = {DefinitionsController.class, FaroController.class})
@Path("/{groupId}/definitions")
@Produces(MediaType.APPLICATION_JSON)
public class DefinitionsController extends BaseFaroController {

	@GET
	@Path("/individual_attributes")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public FaroResultsDisplay get(
			@PathParam("groupId") long groupId,
			@QueryParam("displayName") String displayName)
		throws PortalException {

		List<FieldMapping> individualAttributes =
			contactsEngineClient.getIndividualAttributes(
				faroProjectLocalService.getFaroProjectByGroupId(groupId),
				displayName);

		return new FaroResultsDisplay(
			TransformUtil.transform(
				individualAttributes, AttributesDisplay::new),
			individualAttributes.size());
	}

}