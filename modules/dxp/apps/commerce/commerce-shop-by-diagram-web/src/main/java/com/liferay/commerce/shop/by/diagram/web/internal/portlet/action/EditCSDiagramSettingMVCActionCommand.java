/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.web.internal.portlet.action;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.exception.NoSuchCPAttachmentFileEntryException;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.commerce.shop.by.diagram.configuration.CSDiagramSettingImageConfiguration;
import com.liferay.commerce.shop.by.diagram.constants.CSDiagramSettingsConstants;
import com.liferay.commerce.shop.by.diagram.exception.NoSuchCSDiagramEntryException;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramSettingService;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	configurationPid = "com.liferay.commerce.shop.by.diagram.configuration.CSDiagramSettingImageConfiguration",
	property = {
		"jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
		"mvc.command.name=/cp_definitions/edit_cs_diagram_setting"
	},
	service = MVCActionCommand.class
)
public class EditCSDiagramSettingMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_csDiagramSettingImageConfiguration =
			ConfigurableUtil.createConfigurable(
				CSDiagramSettingImageConfiguration.class, properties);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		boolean publish = ParamUtil.getBoolean(actionRequest, "publish", true);

		try {
			if (cmd.equals("updateCSDiagramSetting")) {
				Callable<Object> csDiagramSettingCallable =
					new CSDiagramSettingCallable(actionRequest);

				TransactionInvokerUtil.invoke(
					_transactionConfig, csDiagramSettingCallable);

				if (!publish) {
					_writeJSON(
						_portal.getHttpServletResponse(actionResponse),
						JSONUtil.put("success", true));
				}
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof NoSuchCPAttachmentFileEntryException ||
				throwable instanceof NoSuchCSDiagramEntryException ||
				throwable instanceof NoSuchFileEntryException ||
				throwable instanceof PrincipalException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, throwable.getClass());

				if (publish) {
					String redirect = ParamUtil.getString(
						actionRequest, "redirect");

					sendRedirect(actionRequest, actionResponse, redirect);
				}
				else {
					_writeJSON(
						_portal.getHttpServletResponse(actionResponse),
						JSONUtil.put(
							"error",
							_language.get(
								_portal.getHttpServletRequest(actionRequest),
								"please-select-an-existing-file")
						).put(
							"success", false
						));
				}
			}
			else {
				_log.error(throwable, throwable);

				if (publish) {
					throw new Exception(throwable);
				}

				_writeJSON(
					_portal.getHttpServletResponse(actionResponse),
					JSONUtil.put(
						"error", throwable.getMessage()
					).put(
						"success", false
					));
			}
		}
	}

	private CPAttachmentFileEntry _addOrUpdateCPAttachmentFileEntry(
			ActionRequest actionRequest, CSDiagramSetting csDiagramSetting,
			long cpDefinitionId)
		throws Exception {

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CPAttachmentFileEntry.class.getName(), actionRequest);

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
			serviceContext.getTimeZone());
		Calendar expirationCalendar = CalendarFactoryUtil.getCalendar(
			serviceContext.getTimeZone());

		if (csDiagramSetting == null) {
			return _cpAttachmentFileEntryService.addCPAttachmentFileEntry(
				serviceContext.getScopeGroupId(),
				_portal.getClassNameId(CPDefinition.class), cpDefinitionId,
				fileEntryId, false, null, displayCalendar.get(Calendar.MONTH),
				displayCalendar.get(Calendar.DAY_OF_MONTH),
				displayCalendar.get(Calendar.YEAR),
				displayCalendar.get(Calendar.HOUR),
				displayCalendar.get(Calendar.MINUTE),
				expirationCalendar.get(Calendar.MONTH),
				expirationCalendar.get(Calendar.DAY_OF_MONTH),
				expirationCalendar.get(Calendar.YEAR),
				expirationCalendar.get(Calendar.HOUR),
				expirationCalendar.get(Calendar.MINUTE), true, true,
				Collections.emptyMap(), null, 0D,
				CSDiagramSettingsConstants.TYPE_DIAGRAM, serviceContext);
		}

		CPAttachmentFileEntry cpAttachmentFileEntry =
			csDiagramSetting.getCPAttachmentFileEntry();

		if (cpAttachmentFileEntry.getFileEntryId() == fileEntryId) {
			return cpAttachmentFileEntry;
		}

		return _cpAttachmentFileEntryService.updateCPAttachmentFileEntry(
			cpAttachmentFileEntry.getCPAttachmentFileEntryId(), fileEntryId,
			false, null, displayCalendar.get(Calendar.MONTH),
			displayCalendar.get(Calendar.DAY_OF_MONTH),
			displayCalendar.get(Calendar.YEAR),
			displayCalendar.get(Calendar.HOUR),
			displayCalendar.get(Calendar.MINUTE),
			expirationCalendar.get(Calendar.MONTH),
			expirationCalendar.get(Calendar.DAY_OF_MONTH),
			expirationCalendar.get(Calendar.YEAR),
			expirationCalendar.get(Calendar.HOUR),
			expirationCalendar.get(Calendar.MINUTE), true, true,
			Collections.emptyMap(), null, 0D,
			CSDiagramSettingsConstants.TYPE_DIAGRAM, serviceContext);
	}

	private CSDiagramSetting _updateCSDiagramSetting(
			ActionRequest actionRequest)
		throws Exception {

		long cpDefinitionId = ParamUtil.getLong(
			actionRequest, "cpDefinitionId");

		CSDiagramSetting csDiagramSetting =
			_csDiagramSettingService.fetchCSDiagramSettingByCPDefinitionId(
				cpDefinitionId);

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_addOrUpdateCPAttachmentFileEntry(
				actionRequest, csDiagramSetting, cpDefinitionId);

		String type = ParamUtil.getString(actionRequest, "type");

		if (csDiagramSetting == null) {
			return _csDiagramSettingService.addCSDiagramSetting(
				cpDefinitionId,
				cpAttachmentFileEntry.getCPAttachmentFileEntryId(), null,
				ParamUtil.getDouble(
					actionRequest, "radius",
					_csDiagramSettingImageConfiguration.radius()),
				type);
		}

		return _csDiagramSettingService.updateCSDiagramSetting(
			csDiagramSetting.getCSDiagramSettingId(),
			cpAttachmentFileEntry.getCPAttachmentFileEntryId(), null,
			csDiagramSetting.getRadius(), type);
	}

	private void _writeJSON(
			HttpServletResponse httpServletResponse, JSONObject jsonObject)
		throws IOException {

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		ServletResponseUtil.write(httpServletResponse, jsonObject.toString());

		httpServletResponse.flushBuffer();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCSDiagramSettingMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private CPAttachmentFileEntryService _cpAttachmentFileEntryService;

	private volatile CSDiagramSettingImageConfiguration
		_csDiagramSettingImageConfiguration;

	@Reference
	private CSDiagramSettingService _csDiagramSettingService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private class CSDiagramSettingCallable implements Callable<Object> {

		@Override
		public CSDiagramSetting call() throws Exception {
			return _updateCSDiagramSetting(_actionRequest);
		}

		private CSDiagramSettingCallable(ActionRequest actionRequest) {
			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}