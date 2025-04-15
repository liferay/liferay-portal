/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.main;

import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.FaroResultsDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.search.FaroSearchContext;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Matthew Kong
 */
@Component(service = MainController.class)
@Path("/{groupId}")
@Produces(MediaType.APPLICATION_JSON)
public class MainController extends BaseFaroController {

	@GET
	@Path("/entities")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<FaroResultsDisplay> search(
			@PathParam("groupId") long groupId,
			@QueryParam("faroSearchContexts") FaroParam<List<FaroSearchContext>>
				faroSearchContextsFaroParam)
		throws Exception {

		return _search(groupId, faroSearchContextsFaroParam.getValue());
	}

	@Path("/entities/search")
	@POST
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public List<FaroResultsDisplay> searchByForm(
			@PathParam("groupId") long groupId,
			@FormParam("faroSearchContexts") FaroParam<List<FaroSearchContext>>
				faroSearchContextsFaroParam)
		throws Exception {

		return search(groupId, faroSearchContextsFaroParam);
	}

	@Path("/engine")
	@POST
	@RolesAllowed(StringPool.BLANK)
	public void setEngineURL(
		@FormParam("contactsEngineURL") String contactsEngineURL) {

		if (Validator.isNotNull(contactsEngineURL)) {
			contactsEngineClient.setEngineURL(contactsEngineURL);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, FaroController.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private FaroResultsDisplay _getFaroResultsDisplay(
			long groupId, FaroSearchContext faroSearchContext)
		throws Exception {

		for (FaroController faroController : _serviceTrackerList) {
			if (ArrayUtil.contains(
					faroController.getEntityTypes(),
					faroSearchContext.getType())) {

				return faroController.search(groupId, faroSearchContext);
			}
		}

		return new FaroResultsDisplay();
	}

	private List<FaroResultsDisplay> _search(
			long groupId, List<FaroSearchContext> faroSearchContexts)
		throws Exception {

		return TransformUtil.transform(
			faroSearchContexts,
			faroSearchContext -> _getFaroResultsDisplay(
				groupId, faroSearchContext));
	}

	private ServiceTrackerList<FaroController> _serviceTrackerList;

}