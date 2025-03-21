/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.opener.constants.DLOpenerMimeTypes;
import com.liferay.document.library.opener.onedrive.web.internal.DLOpenerOneDriveFileReference;
import com.liferay.document.library.opener.onedrive.web.internal.DLOpenerOneDriveManager;
import com.liferay.document.library.opener.onedrive.web.internal.constants.DLOpenerOneDriveMimeTypes;
import com.liferay.document.library.opener.onedrive.web.internal.oauth.OAuth2Controller;
import com.liferay.document.library.opener.onedrive.web.internal.oauth.OAuth2ControllerFactory;
import com.liferay.document.library.opener.onedrive.web.internal.portlet.action.util.OneDriveURLUtil;
import com.liferay.document.library.opener.upload.UniqueFileEntryTitleProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	property = {
		"auth.token.ignore.mvc.action=true",
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"mvc.command.name=/document_library/create_in_one_drive"
	},
	service = MVCActionCommand.class
)
public class CreateInOneDriveMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		OAuth2Controller oAuth2Controller = getOAuth2Controller();

		oAuth2Controller.execute(
			actionRequest, actionResponse, this::_executeCommand);
	}

	protected OAuth2Controller getOAuth2Controller() {
		return oAuth2ControllerFactory.getJSONOAuth2Controller(
			this::_getSuccessURL);
	}

	@Reference
	protected DLAppService dlAppService;

	@Reference
	protected DLOpenerOneDriveManager dlOpenerOneDriveManager;

	@Reference
	protected Language language;

	@Reference
	protected OAuth2ControllerFactory oAuth2ControllerFactory;

	@Reference
	protected Portal portal;

	@Reference
	protected PortletURLFactory portletURLFactory;

	@Reference
	protected UniqueFileEntryTitleProvider uniqueFileEntryTitleProvider;

	private DLOpenerOneDriveFileReference _addDLOpenerOneDriveFileReference(
			long userId, long repositoryId, long folderId, String mimeType,
			String title, ServiceContext serviceContext)
		throws PortalException {

		String mimeTypeExtension =
			DLOpenerOneDriveMimeTypes.getMimeTypeExtension(mimeType);

		String uniqueTitle = uniqueFileEntryTitleProvider.provide(
			serviceContext.getScopeGroupId(), folderId, mimeTypeExtension,
			title);

		String sourceFileName = uniqueTitle + mimeTypeExtension;

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		FileEntry fileEntry = dlAppService.addFileEntry(
			null, repositoryId, folderId, sourceFileName, mimeType, uniqueTitle,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, new byte[0],
			null, null, null, serviceContext);

		dlAppService.checkOutFileEntry(
			fileEntry.getFileEntryId(), serviceContext);

		return dlOpenerOneDriveManager.createFile(
			userId, fileEntry, serviceContext.getLocale());
	}

	private JSONObject _executeCommand(PortletRequest portletRequest)
		throws PortalException {

		try {
			long repositoryId = ParamUtil.getLong(
				portletRequest, "repositoryId");
			long folderId = ParamUtil.getLong(portletRequest, "folderId");
			String contentType = ParamUtil.getString(
				portletRequest, "contentType",
				DLOpenerMimeTypes.APPLICATION_VND_DOCX);
			String title = ParamUtil.getString(
				portletRequest, "title",
				_translate(portal.getLocale(portletRequest), "untitled"));

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				portletRequest);

			DLOpenerOneDriveFileReference dlOpenerOneDriveFileReference =
				TransactionInvokerUtil.invoke(
					_transactionConfig,
					() -> _addDLOpenerOneDriveFileReference(
						portal.getUserId(portletRequest), repositoryId,
						folderId, contentType, title, serviceContext));

			String oneDriveBackgroundTaskStatusURL =
				OneDriveURLUtil.getBackgroundTaskStatusURL(
					dlOpenerOneDriveFileReference, portal, portletRequest,
					portletURLFactory);

			return JSONUtil.put(
				"dialogMessage",
				_translate(
					portal.getLocale(portletRequest),
					"you-are-being-redirected-to-an-external-editor-to-" +
						"create-this-document")
			).put(
				"oneDriveBackgroundTaskStatusURL",
				oneDriveBackgroundTaskStatusURL
			);
		}
		catch (PortalException | RuntimeException exception) {
			throw exception;
		}
		catch (Throwable throwable) {
			throw new PortalException(throwable);
		}
	}

	private String _getSuccessURL(PortletRequest portletRequest) {
		LiferayPortletURL liferayPortletURL = portletURLFactory.create(
			portletRequest, portal.getPortletId(portletRequest),
			PortletRequest.ACTION_PHASE);

		liferayPortletURL.setParameters(portletRequest.getParameterMap());
		liferayPortletURL.setParameter(
			ActionRequest.ACTION_NAME,
			"/document_library/create_in_one_drive_and_redirect");

		return liferayPortletURL.toString();
	}

	private String _translate(Locale locale, String key) {
		return language.get(locale, key);
	}

	private final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

}