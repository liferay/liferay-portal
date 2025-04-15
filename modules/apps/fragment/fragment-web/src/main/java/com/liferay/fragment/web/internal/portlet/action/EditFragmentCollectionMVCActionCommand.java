/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/edit_fragment_collection"
	},
	service = MVCActionCommand.class
)
public class EditFragmentCollectionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long fragmentCollectionId = ParamUtil.getLong(
			actionRequest, "fragmentCollectionId");

		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");

		FragmentCollection fragmentCollection = null;

		if (fragmentCollectionId <= 0) {

			// Add fragment collection

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				actionRequest);

			fragmentCollection =
				_fragmentCollectionService.addFragmentCollection(
					null, serviceContext.getScopeGroupId(), name, description,
					serviceContext);
		}
		else {

			// Update fragment collection

			fragmentCollection =
				_fragmentCollectionService.updateFragmentCollection(
					fragmentCollectionId, name, description);
		}

		String redirect = getRedirectURL(actionResponse, fragmentCollection);

		sendRedirect(actionRequest, actionResponse, redirect);
	}

	protected String getRedirectURL(
		ActionResponse actionResponse, FragmentCollection fragmentCollection) {

		return PortletURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(actionResponse)
		).setParameter(
			"fragmentCollectionId", fragmentCollection.getFragmentCollectionId()
		).buildString();
	}

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

	@Reference
	private Portal _portal;

}