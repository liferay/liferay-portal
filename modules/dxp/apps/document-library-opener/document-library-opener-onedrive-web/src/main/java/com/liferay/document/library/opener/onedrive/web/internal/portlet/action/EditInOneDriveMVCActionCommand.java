/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.opener.onedrive.web.internal.DLOpenerOneDriveFileReference;
import com.liferay.document.library.opener.onedrive.web.internal.DLOpenerOneDriveManager;
import com.liferay.document.library.opener.onedrive.web.internal.oauth.OAuth2Controller;
import com.liferay.document.library.opener.onedrive.web.internal.oauth.OAuth2ControllerFactory;
import com.liferay.document.library.opener.onedrive.web.internal.portlet.action.util.OneDriveURLUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

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
		"mvc.command.name=/document_library/edit_in_one_drive"
	},
	service = MVCActionCommand.class
)
public class EditInOneDriveMVCActionCommand extends BaseMVCActionCommand {

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

	private DLOpenerOneDriveFileReference _checkOutOneDriveFileEntry(
			long fileEntryId, ServiceContext serviceContext)
		throws PortalException {

		dlAppService.checkOutFileEntry(fileEntryId, serviceContext);

		return dlOpenerOneDriveManager.checkOut(
			serviceContext.getUserId(), dlAppService.getFileEntry(fileEntryId),
			serviceContext.getLocale());
	}

	private JSONObject _executeCommand(PortletRequest portletRequest)
		throws PortalException {

		String cmd = ParamUtil.getString(portletRequest, Constants.CMD);
		long fileEntryId = ParamUtil.getLong(portletRequest, "fileEntryId");

		if (cmd.equals(Constants.CHECKOUT)) {
			try {
				ServiceContext serviceContext =
					ServiceContextFactory.getInstance(portletRequest);

				DLOpenerOneDriveFileReference dlOpenerOneDriveFileReference =
					TransactionInvokerUtil.invoke(
						_transactionConfig,
						() -> _checkOutOneDriveFileEntry(
							fileEntryId, serviceContext));

				String oneDriveBackgroundTaskStatusURL =
					OneDriveURLUtil.getBackgroundTaskStatusURL(
						dlOpenerOneDriveFileReference, portal, portletRequest,
						portletURLFactory);

				return JSONUtil.put(
					"dialogMessage",
					_translate(
						portal.getLocale(portletRequest),
						"you-are-being-redirected-to-an-external-editor-to-" +
							"edit-this-document")
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
		else if (cmd.equals(Constants.EDIT)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			DLOpenerOneDriveFileReference dlOpenerOneDriveFileReference =
				dlOpenerOneDriveManager.requestEditAccess(
					themeDisplay.getUserId(),
					dlAppService.getFileEntry(fileEntryId));

			String oneDriveBackgroundTaskStatusURL =
				OneDriveURLUtil.getBackgroundTaskStatusURL(
					dlOpenerOneDriveFileReference, portal, portletRequest,
					portletURLFactory);

			return JSONUtil.put(
				"dialogMessage",
				_translate(
					portal.getLocale(portletRequest),
					"you-are-being-redirected-to-an-external-editor-to-edit-" +
						"this-document")
			).put(
				"oneDriveBackgroundTaskStatusURL",
				oneDriveBackgroundTaskStatusURL
			);
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	private String _getSuccessURL(PortletRequest portletRequest) {
		LiferayPortletURL liferayPortletURL = portletURLFactory.create(
			portletRequest, portal.getPortletId(portletRequest),
			PortletRequest.ACTION_PHASE);

		liferayPortletURL.setParameters(portletRequest.getParameterMap());
		liferayPortletURL.setParameter(
			ActionRequest.ACTION_NAME,
			"/document_library/edit_in_one_drive_and_redirect");

		return liferayPortletURL.toString();
	}

	private String _translate(Locale locale, String key) {
		return language.get(locale, key);
	}

	private final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

}