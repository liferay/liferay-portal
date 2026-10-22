/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.servlet.taglib;

import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.digital.signature.url.DSRequestURLProvider;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.frontend.js.loader.modules.extender.esm.ESImportUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.aui.JSFragment;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(service = DynamicInclude.class)
public class SignDigitalSignatureBottomJSDynamicInclude
	implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		long dsRequestId = ParamUtil.getLong(httpServletRequest, "dsRequestId");

		if (dsRequestId <= 0) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn() || themeDisplay.isStatePopUp()) {
			return;
		}

		DSRequest dsRequest = _dsRequestManager.fetchDSRequest(
			themeDisplay.getCompanyId(), dsRequestId);

		if ((dsRequest == null) ||
			!dsRequest.isSignatureRequired(themeDisplay.getUserId())) {

			return;
		}

		String signModalURL = null;

		try {
			signModalURL = _dsRequestURLProvider.getSignModalURL(
				HttpComponentsUtil.removeParameter(
					themeDisplay.getURLCurrent(), "dsRequestId"),
				themeDisplay.getCompanyId(), dsRequestId,
				dsRequest.getSiteGroupId());
		}
		catch (Exception exception) {
			_log.error(
				"Unable to get the sign modal URL for digital signature " +
					"request " + dsRequestId,
				exception);

			return;
		}

		ScriptData scriptData = new ScriptData();

		scriptData.append(
			_portal.getPortletId(httpServletRequest),
			new JSFragment(
				StringBundler.concat(
					"openSigningModal(",
					JSONUtil.put(
						"title", _getTitle(dsRequest, httpServletRequest)
					).put(
						"url", signModalURL
					),
					");"),
				Arrays.asList(
					ESImportUtil.getESImport(
						_absolutePortalURLBuilderFactory.
							getAbsolutePortalURLBuilder(httpServletRequest),
						"{openSigningModal} from digital-signature-web"))));

		scriptData.writeTo(httpServletResponse.getWriter());
	}

	@Override
	public void register(
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry) {

		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

	private String _getTitle(
		DSRequest dsRequest, HttpServletRequest httpServletRequest) {

		for (long fileEntryId : dsRequest.getFileEntryIds()) {
			DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
				fileEntryId);

			if (dlFileEntry != null) {
				return dlFileEntry.getTitle();
			}
		}

		return _language.get(httpServletRequest, "sign");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SignDigitalSignatureBottomJSDynamicInclude.class);

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DSRequestManager _dsRequestManager;

	@Reference
	private DSRequestURLProvider _dsRequestURLProvider;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}