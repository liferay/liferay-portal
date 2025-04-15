/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/style_book/update_style_book_entry_preview"
	},
	service = MVCActionCommand.class
)
public class UpdateStyleBookEntryPreviewMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

		FileEntry tempFileEntry = fileEntry;

		String extension = fileEntry.getExtension();

		Matcher matcher = _pattern.matcher(extension);

		String mimeType = fileEntry.getMimeType();

		if (!matcher.find() || !mimeType.startsWith("image/")) {
			LiferayPortletRequest liferayPortletRequest =
				_portal.getLiferayPortletRequest(actionRequest);

			hideDefaultErrorMessage(liferayPortletRequest);

			SessionErrors.add(
				liferayPortletRequest,
				"styleBookEntryPreviewFileExtensionInvalid");
		}
		else {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			Repository repository =
				PortletFileRepositoryUtil.fetchPortletRepository(
					themeDisplay.getScopeGroupId(),
					StyleBookPortletKeys.STYLE_BOOK);

			if (repository == null) {
				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setAddGroupPermissions(true);
				serviceContext.setAddGuestPermissions(true);

				repository = PortletFileRepositoryUtil.addPortletRepository(
					themeDisplay.getScopeGroupId(),
					StyleBookPortletKeys.STYLE_BOOK, serviceContext);
			}

			long styleBookEntryId = ParamUtil.getLong(
				actionRequest, "styleBookEntryId");

			String fileName = StringBundler.concat(
				styleBookEntryId, "_preview.", extension);

			FileEntry oldFileEntry =
				PortletFileRepositoryUtil.fetchPortletFileEntry(
					themeDisplay.getScopeGroupId(), repository.getDlFolderId(),
					fileName);

			if (oldFileEntry != null) {
				PortletFileRepositoryUtil.deletePortletFileEntry(
					oldFileEntry.getFileEntryId());
			}

			fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
				null, themeDisplay.getScopeGroupId(), themeDisplay.getUserId(),
				StyleBookEntry.class.getName(), styleBookEntryId,
				StyleBookPortletKeys.STYLE_BOOK, repository.getDlFolderId(),
				fileEntry.getContentStream(), fileName, fileEntry.getMimeType(),
				false);

			_styleBookEntryService.updatePreviewFileEntryId(
				styleBookEntryId, fileEntry.getFileEntryId());
		}

		TempFileEntryUtil.deleteTempFileEntry(tempFileEntry.getFileEntryId());

		sendRedirect(actionRequest, actionResponse);
	}

	private static final Pattern _pattern = Pattern.compile(
		"(bmp|jpeg|jpg|png|tiff)$");

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private StyleBookEntryService _styleBookEntryService;

}