/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.exception.KBArticleImportException;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.web.internal.portlet.AdminPortlet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"mvc.command.name=/knowledge_base/import_file"
	},
	service = MVCActionCommand.class
)
public class ImportFileMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			UploadPortletRequest uploadPortletRequest =
				_portal.getUploadPortletRequest(actionRequest);

			_checkExceededSizeLimit(actionRequest);

			String fileName = uploadPortletRequest.getFileName("file");

			if (Validator.isNull(fileName)) {
				throw new KBArticleImportException("File name is null");
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			long parentKBFolderId = ParamUtil.getLong(
				uploadPortletRequest, "parentKBFolderId",
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			boolean prioritizeByNumericalPrefix = ParamUtil.getBoolean(
				uploadPortletRequest, "prioritizeByNumericalPrefix");

			try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
					"file")) {

				ServiceContext serviceContext =
					ServiceContextFactory.getInstance(
						AdminPortlet.class.getName(), actionRequest);

				ModelPermissions modelPermissions =
					serviceContext.getModelPermissions();

				modelPermissions.addRolePermissions(
					RoleConstants.GUEST, ActionKeys.VIEW);

				int importedKBArticlesCount =
					_kbArticleService.addKBArticlesMarkdown(
						themeDisplay.getScopeGroupId(), parentKBFolderId,
						fileName, prioritizeByNumericalPrefix, inputStream,
						serviceContext);

				SessionMessages.add(
					actionRequest, "importedKBArticlesCount",
					importedKBArticlesCount);
			}
		}
		catch (KBArticleImportException kbArticleImportException) {
			hideDefaultErrorMessage(actionRequest);

			SessionErrors.add(
				actionRequest, kbArticleImportException.getClass(),
				kbArticleImportException);
		}
	}

	private void _checkExceededSizeLimit(PortletRequest portletRequest)
		throws PortalException {

		UploadException uploadException =
			(UploadException)portletRequest.getAttribute(
				WebKeys.UPLOAD_EXCEPTION);

		if (uploadException != null) {
			Throwable throwable = uploadException.getCause();

			if (uploadException.isExceededFileSizeLimit()) {
				throw new FileSizeException(throwable);
			}

			if (uploadException.isExceededLiferayFileItemSizeLimit()) {
				throw new LiferayFileItemException(throwable);
			}

			if (uploadException.isExceededUploadRequestSizeLimit()) {
				throw new UploadRequestSizeException(throwable);
			}

			throw new PortalException(throwable);
		}
	}

	@Reference
	private KBArticleService _kbArticleService;

	@Reference
	private Portal _portal;

}