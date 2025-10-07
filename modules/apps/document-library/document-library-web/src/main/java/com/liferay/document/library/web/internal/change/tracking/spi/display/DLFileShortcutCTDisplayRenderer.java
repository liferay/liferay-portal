/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class DLFileShortcutCTDisplayRenderer
	extends BaseCTDisplayRenderer<DLFileShortcut> {

	@Override
	public InputStream getDownloadInputStream(
			DLFileShortcut dlFileShortcut, String version)
		throws PortalException {

		FileVersion fileVersion = dlFileShortcut.getFileVersion();

		return fileVersion.getContentStream(false);
	}

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			DLFileShortcut dlFileShortcut)
		throws PortalException {

		Group group = _groupLocalService.getGroup(dlFileShortcut.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
				0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/document_library/edit_file_shortcut"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"fileShortcutId", dlFileShortcut.getFileShortcutId()
		).setParameter(
			"folderId", dlFileShortcut.getFolderId()
		).setParameter(
			"repositoryId", dlFileShortcut.getRepositoryId()
		).setParameter(
			"toFileEntryId", dlFileShortcut.getToFileEntryId()
		).buildString();
	}

	@Override
	public Class<DLFileShortcut> getModelClass() {
		return DLFileShortcut.class;
	}

	@Override
	public String getTitle(Locale locale, DLFileShortcut dlFileShortcut) {
		return dlFileShortcut.getToTitle();
	}

	@Override
	protected void buildDisplay(DisplayBuilder<DLFileShortcut> displayBuilder)
		throws PortalException {

		DLFileShortcut dlFileShortcut = displayBuilder.getModel();

		displayBuilder.display(
			"title", dlFileShortcut.getToTitle()
		).display(
			"created-by",
			() -> {
				String userName = dlFileShortcut.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", dlFileShortcut.getCreateDate()
		).display(
			"folder",
			() -> {
				Folder folder = dlFileShortcut.getFolder();

				return folder.getName();
			}
		).display(
			"download",
			() -> {
				FileVersion fileVersion = dlFileShortcut.getFileVersion();

				DLFileVersion dlFileVersion =
					(DLFileVersion)fileVersion.getModel();

				return getDownloadLink(
					displayBuilder.getDisplayContext(),
					dlFileVersion.getVersion(), dlFileVersion.getSize(),
					dlFileVersion.getFileName());
			},
			false
		);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}