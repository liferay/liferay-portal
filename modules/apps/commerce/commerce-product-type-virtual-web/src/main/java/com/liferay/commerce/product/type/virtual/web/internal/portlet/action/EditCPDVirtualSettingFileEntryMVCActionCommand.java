/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.web.internal.portlet.action;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingFileEntryIdException;
import com.liferay.commerce.product.type.virtual.exception.NoSuchCPDefinitionVirtualSettingException;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryService;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cpd_virtual_setting_file_entry"
	},
	service = MVCActionCommand.class
)
public class EditCPDVirtualSettingFileEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				Callable<CPDVirtualSettingFileEntry>
					cpdVirtualSettingFileEntryCallable =
						new CPDVirtualSettingFileEntryCallable(actionRequest);

				CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
					TransactionInvokerUtil.invoke(
						_transactionConfig, cpdVirtualSettingFileEntryCallable);

				sendRedirect(
					actionRequest, actionResponse,
					_getSaveAndContinueRedirect(
						actionRequest,
						cpdVirtualSettingFileEntry.
							getCPDefinitionVirtualSettingFileEntryId()));

				return;
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCPDVirtualSettingFileEntry(actionRequest);
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof CPDefinitionVirtualSettingException ||
				throwable instanceof
					CPDefinitionVirtualSettingFileEntryIdException ||
				throwable instanceof
					NoSuchCPDefinitionVirtualSettingException ||
				throwable instanceof PrincipalException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, throwable.getClass());
			}
			else {
				_log.error(throwable, throwable);

				throw new Exception(throwable);
			}
		}

		String className = ParamUtil.getString(actionRequest, "className");

		if (className.equals(CPInstance.class.getName())) {
			sendRedirect(
				actionRequest, actionResponse,
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						actionRequest, CPDefinition.class.getName(),
						PortletProvider.Action.EDIT)
				).setMVCRenderCommandName(
					"/cp_definitions/edit_cp_instance"
				).setParameter(
					"cpDefinitionId",
					ParamUtil.getLong(actionRequest, "cpDefinitionId")
				).setParameter(
					"cpInstanceId", ParamUtil.getLong(actionRequest, "classPK")
				).setParameter(
					"override", ParamUtil.getBoolean(actionRequest, "override")
				).setParameter(
					"screenNavigationCategoryKey", "virtual-settings"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
		}
		else {
			String redirect = ParamUtil.getString(actionRequest, "redirect");

			sendRedirect(actionRequest, actionResponse, redirect);
		}
	}

	private void _deleteCPDVirtualSettingFileEntry(ActionRequest actionRequest)
		throws Exception {

		long cpdVirtualSettingFileEntryId = ParamUtil.getLong(
			actionRequest, "cpdVirtualSettingFileEntryId");

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			_cpdVirtualSettingFileEntryService.getCPDVirtualSettingFileEntry(
				cpdVirtualSettingFileEntryId);

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			cpdVirtualSettingFileEntry.getCPDefinitionVirtualSetting();

		_cpdVirtualSettingFileEntryService.deleteCPDVirtualSettingFileEntry(
			cpDefinitionVirtualSetting.getClassName(),
			cpDefinitionVirtualSetting.getClassPK(),
			cpdVirtualSettingFileEntryId);
	}

	private String _getSaveAndContinueRedirect(
			ActionRequest actionRequest, long cpdVirtualSettingFileEntryId)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletURL portletURL = PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				actionRequest, themeDisplay.getScopeGroup(),
				CPDefinition.class.getName(), PortletProvider.Action.EDIT)
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cpd_virtual_setting_file_entry"
		).setParameter(
			"cpDefinitionId",
			() -> {
				long cpDefinitionId = ParamUtil.getLong(
					actionRequest, "cpDefinitionId");

				if (cpDefinitionId > 0) {
					return cpDefinitionId;
				}

				return null;
			}
		).setParameter(
			"cpdVirtualSettingFileEntryId", cpdVirtualSettingFileEntryId
		).setParameter(
			"cpInstanceId",
			() -> {
				long cpInstanceId = ParamUtil.getLong(
					actionRequest, "cpInstanceId");

				if (cpInstanceId > 0) {
					return cpInstanceId;
				}

				return null;
			}
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			_log.error(windowStateException);
		}

		return portletURL.toString();
	}

	private CPDVirtualSettingFileEntry _updateCPDVirtualSettingFileEntry(
			ActionRequest actionRequest)
		throws Exception {

		long cpdVirtualSettingFileEntryId = ParamUtil.getLong(
			actionRequest, "cpdVirtualSettingFileEntryId");
		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");
		String url = ParamUtil.getString(actionRequest, "url");
		String version = ParamUtil.getString(actionRequest, "version");

		if (cpdVirtualSettingFileEntryId > 0) {
			return _cpdVirtualSettingFileEntryService.
				updateCPDefinitionVirtualSetting(
					cpdVirtualSettingFileEntryId, fileEntryId, url, version);
		}

		String className = ParamUtil.getString(actionRequest, "className");
		long classPK = ParamUtil.getLong(actionRequest, "classPK");

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingService.fetchCPDefinitionVirtualSetting(
				className, classPK);

		if (cpDefinitionVirtualSetting == null) {
			cpDefinitionVirtualSetting =
				_cpDefinitionVirtualSettingService.
					addCPDefinitionVirtualSetting(
						className, classPK, 0, StringPool.BLANK,
						CommerceOrderConstants.ORDER_STATUS_COMPLETED, 0, 0,
						false, 0, StringPool.BLANK, false, new HashMap<>(), 0,
						true,
						ServiceContextFactory.getInstance(
							CPDefinitionVirtualSetting.class.getName(),
							actionRequest));
		}

		return _cpdVirtualSettingFileEntryService.addCPDefinitionVirtualSetting(
			cpDefinitionVirtualSetting.getGroupId(), className, classPK,
			cpDefinitionVirtualSetting.getCPDefinitionVirtualSettingId(),
			fileEntryId, url, version);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCPDVirtualSettingFileEntryMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private CPDefinitionVirtualSettingService
		_cpDefinitionVirtualSettingService;

	@Reference
	private CPDVirtualSettingFileEntryService
		_cpdVirtualSettingFileEntryService;

	private class CPDVirtualSettingFileEntryCallable
		implements Callable<CPDVirtualSettingFileEntry> {

		@Override
		public CPDVirtualSettingFileEntry call() throws Exception {
			return _updateCPDVirtualSettingFileEntry(_actionRequest);
		}

		private CPDVirtualSettingFileEntryCallable(
			ActionRequest actionRequest) {

			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}