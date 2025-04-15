/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.exception.NoSuchEntryException;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.File;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/edit_fragment_entry"
	},
	service = MVCActionCommand.class
)
public class EditFragmentEntryMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long fragmentEntryId = ParamUtil.getLong(
			actionRequest, "fragmentEntryId");

		FragmentEntry fragmentEntry = _fragmentEntryService.fetchFragmentEntry(
			fragmentEntryId);

		if (fragmentEntry == null) {
			throw new NoSuchEntryException();
		}

		FragmentEntry draftFragmentEntry = null;

		if (fragmentEntry.isDraft()) {
			draftFragmentEntry = fragmentEntry;
		}
		else {
			draftFragmentEntry = _fragmentEntryService.fetchDraft(
				fragmentEntryId);

			if (draftFragmentEntry == null) {
				draftFragmentEntry = _fragmentEntryService.getDraft(
					fragmentEntryId);

				draftFragmentEntry = _fragmentEntryService.updateDraft(
					draftFragmentEntry);
			}
		}

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		String name = ParamUtil.getString(actionRequest, "name");
		String css = _read("cssContent", uploadPortletRequest);
		String html = _read("htmlContent", uploadPortletRequest);
		String js = _read("jsContent", uploadPortletRequest);
		String configuration = ParamUtil.getString(
			actionRequest, "configurationContent");
		int status = ParamUtil.getInteger(actionRequest, "status");

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		draftFragmentEntry.setName(name);
		draftFragmentEntry.setCss(css);
		draftFragmentEntry.setHtml(html);
		draftFragmentEntry.setJs(js);
		draftFragmentEntry.setConfiguration(configuration);

		if (draftFragmentEntry.isTypeInput()) {
			String[] fieldTypes = ParamUtil.getStringValues(
				actionRequest, "fieldTypes");

			JSONArray fieldTypesJSONArray = _jsonFactory.createJSONArray(
				fieldTypes);

			JSONObject typeOptionsJSONObject = _jsonFactory.createJSONObject(
				draftFragmentEntry.getTypeOptions());

			typeOptionsJSONObject.put("fieldTypes", fieldTypesJSONArray);

			draftFragmentEntry.setTypeOptions(typeOptionsJSONObject.toString());
		}

		draftFragmentEntry.setStatus(status);

		try {
			_fragmentEntryService.updateDraft(draftFragmentEntry);
		}
		catch (PortalException portalException) {
			hideDefaultErrorMessage(actionRequest);

			jsonObject.put("error", portalException.getLocalizedMessage());
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private String _read(String fileName, UploadRequest uploadRequest)
		throws Exception {

		File file = uploadRequest.getFile(fileName);

		if (file != null) {
			return FileUtil.read(file);
		}

		return StringPool.BLANK;
	}

	@Reference
	private FragmentEntryService _fragmentEntryService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}