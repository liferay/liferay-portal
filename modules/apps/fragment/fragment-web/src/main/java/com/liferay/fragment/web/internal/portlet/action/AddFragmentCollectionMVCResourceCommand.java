/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.exception.FragmentCollectionNameException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/add_fragment_collection"
	},
	service = MVCResourceCommand.class
)
public class AddFragmentCollectionMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			String name = ParamUtil.getString(resourceRequest, "name");
			String description = ParamUtil.getString(
				resourceRequest, "description");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				resourceRequest);

			FragmentCollection fragmentCollection =
				_fragmentCollectionService.addFragmentCollection(
					null, serviceContext.getScopeGroupId(), name, description,
					serviceContext);

			jsonObject.put(
				"fragmentCollectionId",
				fragmentCollection.getFragmentCollectionId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			String errorMessage = "an-unexpected-error-occurred";

			if (exception instanceof FragmentCollectionNameException) {
				errorMessage = "name-is-invalid";
			}

			jsonObject.put(
				"error",
				_language.get(
					_portal.getHttpServletRequest(resourceRequest),
					errorMessage));
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonObject);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddFragmentCollectionMVCResourceCommand.class);

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}