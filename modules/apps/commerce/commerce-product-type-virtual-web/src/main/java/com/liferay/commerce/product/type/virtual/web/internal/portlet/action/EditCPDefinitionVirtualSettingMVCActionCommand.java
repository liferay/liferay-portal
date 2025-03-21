/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingFileEntryIdException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingSampleException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingSampleFileEntryIdException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingSampleURLException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingTermsOfUseArticleResourcePKException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingTermsOfUseContentException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingTermsOfUseException;
import com.liferay.commerce.product.type.virtual.exception.CPDefinitionVirtualSettingURLException;
import com.liferay.commerce.product.type.virtual.exception.NoSuchCPDefinitionVirtualSettingException;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingService;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cp_definition_virtual_setting"
	},
	service = MVCActionCommand.class
)
public class EditCPDefinitionVirtualSettingMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCPDefinitionVirtualSetting(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof CPDefinitionVirtualSettingException ||
				exception instanceof
					CPDefinitionVirtualSettingFileEntryIdException ||
				exception instanceof
					CPDefinitionVirtualSettingSampleException ||
				exception instanceof
					CPDefinitionVirtualSettingSampleFileEntryIdException ||
				exception instanceof
					CPDefinitionVirtualSettingSampleURLException ||
				exception instanceof
					CPDefinitionVirtualSettingTermsOfUseArticleResourcePKException ||
				exception instanceof
					CPDefinitionVirtualSettingTermsOfUseContentException ||
				exception instanceof
					CPDefinitionVirtualSettingTermsOfUseException ||
				exception instanceof CPDefinitionVirtualSettingURLException ||
				exception instanceof
					NoSuchCPDefinitionVirtualSettingException ||
				exception instanceof PrincipalException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
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

	private CPDefinitionVirtualSetting _updateCPDefinitionVirtualSetting(
			ActionRequest actionRequest)
		throws Exception {

		String className = ParamUtil.getString(actionRequest, "className");
		long classPK = ParamUtil.getLong(actionRequest, "classPK");
		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");
		String url = ParamUtil.getString(actionRequest, "url");
		int activationStatus = ParamUtil.getInteger(
			actionRequest, "activationStatus");
		long durationDays = ParamUtil.getLong(actionRequest, "durationDays");
		int maxUsages = ParamUtil.getInteger(actionRequest, "maxUsages");
		boolean useSample = ParamUtil.getBoolean(actionRequest, "useSample");
		long sampleFileEntryId = ParamUtil.getLong(
			actionRequest, "sampleFileEntryId");
		String sampleURL = ParamUtil.getString(actionRequest, "sampleURL");
		boolean termsOfUseRequired = ParamUtil.getBoolean(
			actionRequest, "termsOfUseRequired");
		Map<Locale, String> termsOfUseContentMap =
			_localization.getLocalizationMap(
				actionRequest, "termsOfUseContent");
		long termsOfUseJournalArticleResourcePrimKey = ParamUtil.getLong(
			actionRequest, "termsOfUseJournalArticleResourcePrimKey");
		boolean override = ParamUtil.getBoolean(
			actionRequest, "override", true);

		long duration = TimeUnit.DAYS.toMillis(durationDays);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPDefinitionVirtualSetting.class.getName(), actionRequest);

		CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
			_cpDefinitionVirtualSettingService.fetchCPDefinitionVirtualSetting(
				className, classPK);

		if (cpDefinitionVirtualSetting == null) {
			cpDefinitionVirtualSetting =
				_cpDefinitionVirtualSettingService.
					addCPDefinitionVirtualSetting(
						className, classPK, fileEntryId, url, activationStatus,
						duration, maxUsages, useSample, sampleFileEntryId,
						sampleURL, termsOfUseRequired, termsOfUseContentMap,
						termsOfUseJournalArticleResourcePrimKey, override,
						serviceContext);
		}
		else {
			if (!override) {
				cpDefinitionVirtualSetting =
					_cpDefinitionVirtualSettingService.
						deleteCPDefinitionVirtualSetting(className, classPK);
			}
			else {
				cpDefinitionVirtualSetting =
					_cpDefinitionVirtualSettingService.
						updateCPDefinitionVirtualSetting(
							cpDefinitionVirtualSetting.
								getCPDefinitionVirtualSettingId(),
							fileEntryId, url, activationStatus, duration,
							maxUsages, useSample, sampleFileEntryId, sampleURL,
							termsOfUseRequired, termsOfUseContentMap,
							termsOfUseJournalArticleResourcePrimKey, override,
							serviceContext);
			}
		}

		return cpDefinitionVirtualSetting;
	}

	@Reference
	private CPDefinitionVirtualSettingService
		_cpDefinitionVirtualSettingService;

	@Reference
	private Localization _localization;

}