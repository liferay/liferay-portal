/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.fragment.web.internal.handler.FragmentEntryExceptionRequestHandlerUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

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
		"mvc.command.name=/fragment/add_fragment_entry"
	},
	service = MVCActionCommand.class
)
public class AddFragmentEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long fragmentCollectionId = ParamUtil.getLong(
			actionRequest, "fragmentCollectionId");

		String name = ParamUtil.getString(actionRequest, "name");
		int type = ParamUtil.getInteger(
			actionRequest, "type", FragmentConstants.TYPE_SECTION);

		String typeOptions = null;

		if (type == FragmentConstants.TYPE_INPUT) {
			String[] fieldTypes = ParamUtil.getStringValues(
				actionRequest, "fieldTypes");

			JSONArray fieldTypesJSONArray = _jsonFactory.createJSONArray(
				fieldTypes);

			typeOptions = JSONUtil.put(
				"fieldTypes", fieldTypesJSONArray
			).toString();
		}

		try {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				actionRequest);

			FragmentEntry fragmentEntry =
				_fragmentEntryService.addFragmentEntry(
					null, serviceContext.getScopeGroupId(),
					fragmentCollectionId, null, name, StringPool.BLANK,
					StringPool.BLANK, StringPool.BLANK, false, StringPool.BLANK,
					null, 0, false, false, type, typeOptions,
					WorkflowConstants.STATUS_DRAFT, serviceContext);

			fragmentEntry.setCss(
				StringBundler.concat(
					".fragment_", fragmentEntry.getFragmentEntryId(), " {\n}"));
			fragmentEntry.setHtml(
				StringBundler.concat(
					"<div class=\"fragment_",
					fragmentEntry.getFragmentEntryId(), "\">\n</div>"));

			fragmentEntry = _fragmentEntryService.updateDraft(fragmentEntry);

			JSONObject jsonObject = JSONUtil.put(
				"redirectURL", getRedirectURL(actionResponse, fragmentEntry));

			if (SessionErrors.contains(actionRequest, "fragmentNameInvalid")) {
				addSuccessMessage(actionRequest, actionResponse);
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
		catch (PortalException portalException) {
			SessionErrors.add(actionRequest, "fragmentNameInvalid");

			hideDefaultErrorMessage(actionRequest);

			FragmentEntryExceptionRequestHandlerUtil.handlePortalException(
				actionRequest, actionResponse, portalException);
		}
	}

	protected String getRedirectURL(
		ActionResponse actionResponse, FragmentEntry fragmentEntry) {

		return PortletURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(actionResponse)
		).setMVCRenderCommandName(
			"/fragment/edit_fragment_entry"
		).setParameter(
			"fragmentCollectionId", fragmentEntry.getFragmentCollectionId()
		).setParameter(
			"fragmentEntryId", fragmentEntry.getFragmentEntryId()
		).buildString();
	}

	@Reference
	private FragmentEntryService _fragmentEntryService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}