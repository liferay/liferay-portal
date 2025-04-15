/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.talend.web.internal.portlet.action;

import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.repository.DispatchFileRepository;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.dispatch.talend.archive.TalendArchiveParserUtil;
import com.liferay.dispatch.talend.archive.exception.TalendArchiveException;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.zip.ZipException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Igor Beslic
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DispatchPortletKeys.DISPATCH,
		"mvc.command.name=/dispatch_talend/edit_dispatch_talend_job_archive"
	},
	service = MVCActionCommand.class
)
public class EditDispatchTalendJobArchiveMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_checkPermission(actionRequest);

			UploadPortletRequest uploadPortletRequest =
				_portal.getUploadPortletRequest(actionRequest);

			long dispatchTriggerId = ParamUtil.getLong(
				uploadPortletRequest, "dispatchTriggerId");

			File jobArchiveFile = FileUtil.createTempFile(
				uploadPortletRequest.getFileAsStream("jobArchive"));

			try (FileInputStream fileInputStream = new FileInputStream(
					jobArchiveFile)) {

				_updateDispatchTaskSettings(dispatchTriggerId, fileInputStream);

				_dispatchFileRepository.addFileEntry(
					_portal.getUserId(actionRequest), dispatchTriggerId,
					uploadPortletRequest.getFileName("jobArchive"),
					uploadPortletRequest.getSize("jobArchive"),
					uploadPortletRequest.getContentType("jobArchive"),
					new FileInputStream(jobArchiveFile));

				_expandoValueLocalService.addValue(
					_portal.getCompanyId(actionRequest),
					DispatchTrigger.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME, "fileName",
					dispatchTriggerId,
					uploadPortletRequest.getFileName("jobArchive"));
			}
			finally {
				FileUtil.delete(jobArchiveFile);
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			if (!_isArchiveException(exception)) {
				return;
			}

			SessionErrors.add(actionRequest, exception.getClass());

			sendRedirect(
				actionRequest, actionResponse,
				ParamUtil.getString(actionRequest, "redirect"));
		}
	}

	private void _checkPermission(ActionRequest actionRequest)
		throws PrincipalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!permissionChecker.isOmniadmin()) {
			throw new PrincipalException();
		}
	}

	private boolean _isArchiveException(Exception exception) {
		if (exception instanceof TalendArchiveException ||
			exception instanceof ZipException ||
			(exception.getCause() instanceof ZipException)) {

			return true;
		}

		return false;
	}

	private void _updateDispatchTaskSettings(
			long dispatchTriggerId, InputStream jobArchiveInputStream)
		throws PortalException {

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.getDispatchTrigger(dispatchTriggerId);

		UnicodeProperties dispatchTaskSettingsUnicodeProperties =
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties();

		TalendArchiveParserUtil.updateUnicodeProperties(
			jobArchiveInputStream, dispatchTaskSettingsUnicodeProperties);

		_dispatchTriggerLocalService.updateDispatchTrigger(
			dispatchTriggerId, dispatchTaskSettingsUnicodeProperties,
			dispatchTrigger.getName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditDispatchTalendJobArchiveMVCActionCommand.class);

	@Reference
	private DispatchFileRepository _dispatchFileRepository;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private Portal _portal;

}