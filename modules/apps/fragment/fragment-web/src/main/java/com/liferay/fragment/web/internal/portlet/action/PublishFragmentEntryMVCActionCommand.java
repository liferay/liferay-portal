/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.fragment.web.internal.handler.FragmentEntryExceptionRequestHandlerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/publish_fragment_entry"
	},
	service = MVCActionCommand.class
)
public class PublishFragmentEntryMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long fragmentEntryId = ParamUtil.getLong(
			actionRequest, "fragmentEntryId");

		try {
			FragmentEntry draftFragmentEntry = null;

			FragmentEntry fragmentEntry =
				_fragmentEntryService.fetchFragmentEntry(fragmentEntryId);

			if (fragmentEntry.isDraft()) {
				draftFragmentEntry = fragmentEntry;
			}
			else {
				draftFragmentEntry = _fragmentEntryService.fetchDraft(
					fragmentEntryId);
			}

			FragmentEntry publishedFragmentEntry;

			if (draftFragmentEntry == null) {
				publishedFragmentEntry = fragmentEntry;
			}
			else {
				draftFragmentEntry.setStatus(WorkflowConstants.STATUS_APPROVED);

				publishedFragmentEntry = _fragmentEntryService.publishDraft(
					draftFragmentEntry);
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"redirectURL",
					getRedirectURL(actionResponse, publishedFragmentEntry)));

			SessionMessages.remove(
				_portal.getHttpServletRequest(actionRequest),
				FragmentPortletKeys.FRAGMENT +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);
		}
		catch (PortalException portalException) {
			FragmentEntryExceptionRequestHandlerUtil.handlePortalException(
				actionRequest, actionResponse, portalException);
		}
	}

	protected String getRedirectURL(
		ActionResponse actionResponse, FragmentEntry fragmentEntry) {

		return PortletURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(actionResponse)
		).setMVCRenderCommandName(
			"/fragment/view_fragment_entries"
		).setParameter(
			"fragmentCollectionId", fragmentEntry.getFragmentCollectionId()
		).buildString();
	}

	@Reference
	private FragmentEntryService _fragmentEntryService;

	@Reference
	private Portal _portal;

}