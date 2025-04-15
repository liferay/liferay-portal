/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.exception.AccountEntryDomainsException;
import com.liferay.account.exception.DuplicateAccountEntryExternalReferenceCodeException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.account.service.AccountEntryUserRelService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Albert Lee
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_ENTRIES_MANAGEMENT,
		"mvc.command.name=/account_admin/edit_account_entry"
	},
	service = MVCActionCommand.class
)
public class EditAccountEntryMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			AccountEntry accountEntry = null;

			if (cmd.equals(Constants.ADD)) {
				accountEntry = _addAccountEntry(actionRequest);

				actionRequest.setAttribute(
					WebKeys.REDIRECT,
					HttpComponentsUtil.setParameter(
						ParamUtil.getString(actionRequest, "redirect"),
						actionResponse.getNamespace() + "accountEntryId",
						accountEntry.getAccountEntryId()));
			}
			else if (cmd.equals(Constants.UPDATE)) {
				accountEntry = updateAccountEntry(actionRequest);
			}

			if (accountEntry != null) {
				accountEntry.setRestrictMembership(
					ParamUtil.getBoolean(
						actionRequest, "restrictMembership",
						accountEntry.isRestrictMembership()));

				_accountEntryService.updateAccountEntry(accountEntry);
			}
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException) {
				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcPath", "/account_entries_admin/error.jsp");
			}
			else if (exception instanceof AccountEntryDomainsException ||
					 exception instanceof
						 DuplicateAccountEntryExternalReferenceCodeException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				sendRedirect(actionRequest, actionResponse);
			}
			else if ((exception instanceof ModelListenerException) &&
					 (exception.getCause() instanceof PortalException)) {

				throw (PortalException)exception.getCause();
			}

			throw new PortletException(exception);
		}
	}

	protected AccountEntry updateAccountEntry(ActionRequest actionRequest)
		throws Exception {

		long accountEntryId = ParamUtil.getLong(
			actionRequest, "accountEntryId");

		AccountEntry accountEntry = _accountEntryService.getAccountEntry(
			accountEntryId);

		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");
		boolean deleteLogo = ParamUtil.getBoolean(actionRequest, "deleteLogo");

		String[] domains = accountEntry.getDomainsArray();

		if (_isAllowUpdateDomains(accountEntry.getType())) {
			domains = ParamUtil.getStringValues(actionRequest, "domains");
		}

		String emailAddress = ParamUtil.getString(
			actionRequest, "emailAddress");
		String taxIdNumber = ParamUtil.getString(actionRequest, "taxIdNumber");

		accountEntry = _accountEntryService.updateAccountEntry(
			accountEntryId, accountEntry.getParentAccountEntryId(), name,
			description, deleteLogo, domains, emailAddress,
			_getLogoBytes(actionRequest), taxIdNumber, accountEntry.getStatus(),
			ServiceContextFactory.getInstance(
				AccountEntry.class.getName(), actionRequest));

		accountEntry = _accountEntryService.updateExternalReferenceCode(
			accountEntry.getAccountEntryId(),
			ParamUtil.getString(actionRequest, "externalReferenceCode"));

		if (Objects.equals(
				AccountConstants.ACCOUNT_ENTRY_TYPE_PERSON,
				accountEntry.getType())) {

			long personAccountEntryUserId = ParamUtil.getLong(
				actionRequest, "personAccountEntryUserId");

			_accountEntryUserRelService.setPersonTypeAccountEntryUser(
				accountEntryId, personAccountEntryUserId);
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping user updates for business account entry: " +
						accountEntryId);
			}
		}

		return accountEntry;
	}

	private AccountEntry _addAccountEntry(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");
		String[] domains = new String[0];
		String emailAddress = ParamUtil.getString(
			actionRequest, "emailAddress");
		String taxIdNumber = ParamUtil.getString(actionRequest, "taxIdNumber");

		String type = ParamUtil.getString(
			actionRequest, "type",
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);

		if (_isAllowUpdateDomains(type)) {
			domains = ParamUtil.getStringValues(actionRequest, "domains");
		}

		AccountEntry accountEntry = _accountEntryService.addAccountEntry(
			themeDisplay.getUserId(), AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			name, description, domains, emailAddress,
			_getLogoBytes(actionRequest), taxIdNumber, type,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextFactory.getInstance(
				AccountEntry.class.getName(), actionRequest));

		return _accountEntryService.updateExternalReferenceCode(
			accountEntry.getAccountEntryId(),
			ParamUtil.getString(actionRequest, "externalReferenceCode"));
	}

	private byte[] _getLogoBytes(ActionRequest actionRequest) throws Exception {
		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		if (fileEntryId == 0) {
			return null;
		}

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

		return _file.getBytes(fileEntry.getContentStream());
	}

	private boolean _isAllowUpdateDomains(String type) {
		return Objects.equals(
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS, type);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditAccountEntryMVCActionCommand.class);

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private AccountEntryUserRelService _accountEntryUserRelService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private File _file;

}