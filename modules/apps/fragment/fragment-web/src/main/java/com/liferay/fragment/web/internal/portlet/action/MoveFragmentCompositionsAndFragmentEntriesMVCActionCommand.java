/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.service.FragmentCompositionService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/move_fragment_compositions_and_fragment_entries"
	},
	service = MVCActionCommand.class
)
public class MoveFragmentCompositionsAndFragmentEntriesMVCActionCommand
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
					long[] fragmentEntryIds = StringUtil.split(
						ParamUtil.getString(actionRequest, "fragmentEntryIds"),
						0L);

					long[] fragmentCompositionIds = StringUtil.split(
						ParamUtil.getString(
							actionRequest, "fragmentCompositionIds"),
						0L);

					long fragmentCollectionId = ParamUtil.getLong(
						actionRequest, "fragmentCollectionId");

					for (long fragmentCompositionId : fragmentCompositionIds) {
						_fragmentCompositionService.moveFragmentComposition(
							fragmentCompositionId, fragmentCollectionId);
					}

					for (long fragmentEntryId : fragmentEntryIds) {
						_fragmentEntryService.moveFragmentEntry(
							fragmentEntryId, fragmentCollectionId);
					}

					return fragmentCollectionId;
				}
			).buildString());
	}

	@Reference
	private FragmentCompositionService _fragmentCompositionService;

	@Reference
	private FragmentEntryService _fragmentEntryService;

	@Reference
	private Portal _portal;

}