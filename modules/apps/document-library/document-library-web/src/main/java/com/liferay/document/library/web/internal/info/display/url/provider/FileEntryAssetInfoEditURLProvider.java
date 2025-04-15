/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.info.display.url.provider;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.info.display.url.provider.InfoEditURLProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "model.class.name=com.liferay.portal.kernel.repository.model.FileEntry",
	service = InfoEditURLProvider.class
)
public class FileEntryAssetInfoEditURLProvider
	implements InfoEditURLProvider<FileEntry> {

	@Override
	public String getURL(
		FileEntry fileEntry, HttpServletRequest httpServletRequest) {

		Group group = _groupLocalService.fetchGroup(fileEntry.getGroupId());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (group.isCompany()) {
			group = themeDisplay.getScopeGroup();
		}

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
				0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/document_library/edit_file_entry"
		).buildPortletURL();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNull(redirect)) {
			Layout layout = themeDisplay.getLayout();

			if (layout.isTypeAssetDisplay()) {
				redirect = themeDisplay.getURLCurrent();
			}
			else {
				String mode = ParamUtil.getString(
					_portal.getOriginalServletRequest(httpServletRequest),
					"p_l_mode", Constants.VIEW);

				try {
					redirect = HttpComponentsUtil.setParameter(
						_portal.getLayoutRelativeURL(layout, themeDisplay),
						"p_l_mode", mode);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}
		}

		portletURL.setParameter("redirect", redirect);
		portletURL.setParameter(
			"fileEntryId", String.valueOf(fileEntry.getFileEntryId()));

		return portletURL.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileEntryAssetInfoEditURLProvider.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}