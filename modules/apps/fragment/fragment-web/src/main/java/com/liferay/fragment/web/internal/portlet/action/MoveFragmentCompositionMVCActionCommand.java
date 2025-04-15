/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.service.FragmentCompositionService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/move_fragment_composition"
	},
	service = MVCActionCommand.class
)
public class MoveFragmentCompositionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		sendRedirect(
			actionRequest, actionResponse,
			PortletURLBuilder.createRenderURL(
				_portal.getLiferayPortletResponse(actionResponse)
			).setParameter(
				"fragmentCollectionId",
				() -> {
					long fragmentCompositionId = ParamUtil.getLong(
						actionRequest, "fragmentCompositionId");

					long fragmentCollectionId = ParamUtil.getLong(
						actionRequest, "fragmentCollectionId");

					_fragmentCompositionService.moveFragmentComposition(
						fragmentCompositionId, fragmentCollectionId);

					return fragmentCollectionId;
				}
			).buildString());
	}

	@Reference
	private FragmentCompositionService _fragmentCompositionService;

	@Reference
	private Portal _portal;

}