/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.portlet;

import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.digital.signature.web.internal.constants.DigitalSignatureWebKeys;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Keven Leone
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Collect Digital Signature",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/collect_digital_signature/view.jsp",
		"jakarta.portlet.name=" + DigitalSignaturePortletKeys.COLLECT_DIGITAL_SIGNATURE,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CollectDigitalSignaturePortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			portletDisplay.setShowBackIcon(true);
			portletDisplay.setURLBack(
				ParamUtil.getString(renderRequest, "backURL"));

			long[] fileEntryIds = ParamUtil.getLongValues(
				renderRequest, "fileEntryId");

			JSONArray jsonArray = JSONUtil.toJSONArray(
				ArrayUtil.toLongArray(fileEntryIds),
				fileEntryId -> {
					FileEntry fileEntry = _dlAppLocalService.getFileEntry(
						fileEntryId);

					return JSONUtil.put(
						"extension", fileEntry.getExtension()
					).put(
						"fileEntryId", fileEntryId
					).put(
						"groupId", fileEntry.getGroupId()
					).put(
						"title", fileEntry.getTitle()
					);
				});

			renderRequest.setAttribute(
				DigitalSignatureWebKeys.DIGITAL_SIGNATURE_FILE_ENTRIES,
				jsonArray);
			renderRequest.setAttribute(
				DigitalSignatureWebKeys.DIGITAL_SIGNATURE_TITLE,
				_getTitle(
					jsonArray.getJSONObject(
						0
					).getString(
						"title"
					),
					fileEntryIds.length - 1));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		super.render(renderRequest, renderResponse);
	}

	private String _getTitle(String fileEntryTitle, int count) {
		if (count == 0) {
			return fileEntryTitle;
		}

		return _language.format(
			ResourceBundleUtil.getBundle("content.Language", getClass()),
			(count == 1) ? "x-and-x-other-file" : "x-and-x-other-files",
			new String[] {fileEntryTitle, String.valueOf(count)}, false);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CollectDigitalSignaturePortlet.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private Language _language;

}