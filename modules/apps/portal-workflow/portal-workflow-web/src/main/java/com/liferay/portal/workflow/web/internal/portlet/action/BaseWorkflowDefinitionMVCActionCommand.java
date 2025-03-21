/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchRoleException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Jeyvison Nascimento
 */
public abstract class BaseWorkflowDefinitionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		try {
			doProcessAction(actionRequest, actionResponse);

			addSuccessMessage(actionRequest, actionResponse);

			_setCloseRedirect(actionRequest);

			sendRedirect(actionRequest, actionResponse);

			return SessionErrors.isEmpty(actionRequest);
		}
		catch (WorkflowException workflowException) {
			Throwable rootThrowable = _getRootThrowable(workflowException);

			if (_log.isWarnEnabled()) {
				_log.warn(workflowException);
			}

			hideDefaultErrorMessage(actionRequest);

			SessionErrors.add(
				actionRequest, rootThrowable.getClass(), rootThrowable);

			return false;
		}
		catch (PortletException portletException) {
			_log.error(portletException);

			throw portletException;
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new PortletException(exception);
		}
	}

	@Override
	protected void addSuccessMessage(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		SessionMessages.add(
			actionRequest, "requestProcessed",
			getSuccessMessage(actionRequest));
	}

	protected ResourceBundle getResourceBundle(ActionRequest actionRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return ResourceBundleUtil.getModuleAndPortalResourceBundle(
			themeDisplay.getLocale(), getClass());
	}

	protected String getSuccessMessage(ActionRequest actionRequest) {
		return LanguageUtil.get(
			getResourceBundle(actionRequest), "workflow-updated-successfully");
	}

	protected String getTitle(
		ActionRequest actionRequest, Map<Locale, String> titleMap) {

		if (titleMap == null) {
			return StringPool.BLANK;
		}

		String value = StringPool.BLANK;

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);
			String title = titleMap.get(locale);

			if (Validator.isNotNull(title)) {
				value = LocalizationUtil.updateLocalization(
					value, "Title", title, languageId);
			}
			else {
				value = LocalizationUtil.removeLocalization(
					value, "Title", languageId);
			}
		}

		return value;
	}

	protected void setRedirectAttribute(
			ActionRequest actionRequest, WorkflowDefinition workflowDefinition)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
			actionRequest, themeDisplay.getPpid(), PortletRequest.RENDER_PHASE);

		portletURL.setParameter(
			"mvcPath", "/definition/edit_workflow_definition.jsp");

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		portletURL.setParameter("redirect", redirect, false);

		String closeRedirect = ParamUtil.getString(
			actionRequest, "closeRedirect");

		portletURL.setParameter("closeRedirect", closeRedirect, false);

		portletURL.setParameter("name", workflowDefinition.getName(), false);
		portletURL.setParameter(
			"version", String.valueOf(workflowDefinition.getVersion()), false);
		portletURL.setWindowState(actionRequest.getWindowState());

		actionRequest.setAttribute(WebKeys.REDIRECT, portletURL.toString());
	}

	@Reference
	protected Portal portal;

	@Reference
	protected WorkflowDefinitionManager workflowDefinitionManager;

	private Throwable _getRootThrowable(WorkflowException workflowException) {
		if (workflowException.getCause() instanceof IllegalArgumentException ||
			workflowException.getCause() instanceof NoSuchRoleException ||
			workflowException.getCause() instanceof
				PrincipalException.MustBeCompanyAdmin ||
			workflowException.getCause() instanceof
				PrincipalException.MustBeOmniadmin) {

			return workflowException.getCause();
		}

		return workflowException;
	}

	private void _setCloseRedirect(ActionRequest actionRequest) {
		String closeRedirect = ParamUtil.getString(
			actionRequest, "closeRedirect");

		if (Validator.isNull(closeRedirect)) {
			return;
		}

		SessionMessages.add(
			actionRequest,
			portal.getPortletId(actionRequest) +
				SessionMessages.KEY_SUFFIX_CLOSE_REDIRECT,
			closeRedirect);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseWorkflowDefinitionMVCActionCommand.class);

}