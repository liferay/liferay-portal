/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.exception.FragmentCompositionDescriptionException;
import com.liferay.fragment.exception.FragmentCompositionNameException;
import com.liferay.layout.content.page.editor.web.internal.exception.FormContainerParentItemRequiredException;
import com.liferay.layout.content.page.editor.web.internal.exception.NoninstanceablePortletException;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.exception.PortletIdException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseContentPageEditorTransactionalMVCActionCommand
	extends BaseMVCActionCommand implements MVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = null;

		try {
			if (isLayoutLockRequired()) {
				layoutLockManager.getLock(actionRequest);
			}

			Callable<JSONObject> callable = () -> doTransactionalCommand(
				actionRequest, actionResponse);

			jsonObject = TransactionInvokerUtil.invoke(
				_transactionConfig, callable);
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(throwable, throwable);
			}

			Exception exception = null;

			if (throwable instanceof Exception) {
				exception = (Exception)throwable;
			}
			else {
				exception = new Exception(throwable);
			}

			jsonObject = processException(actionRequest, exception);
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);

		hideDefaultSuccessMessage(actionRequest);
	}

	protected abstract JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception;

	protected boolean isLayoutLockRequired() {
		return true;
	}

	protected JSONObject processException(
		ActionRequest actionRequest, Exception exception) {

		if (exception instanceof LockedLayoutException) {
			return processLockedLayoutException(actionRequest);
		}

		String errorMessage = "an-unexpected-error-occurred";

		if (exception instanceof FormContainerParentItemRequiredException) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			errorMessage = LanguageUtil.get(
				themeDisplay.getLocale(),
				"this-form-component-can-only-be-placed-inside-a-mapped-form-" +
					"container");
		}
		else if (exception instanceof FragmentCompositionDescriptionException) {
			errorMessage =
				"please-enter-a-valid-fragment-composition-description";
		}
		else if (exception instanceof FragmentCompositionNameException) {
			errorMessage = "please-enter-a-valid-fragment-composition-name";
		}
		else if ((exception instanceof NoninstanceablePortletException) ||
				 (exception.getCause() instanceof
					 NoninstanceablePortletException) ||
				 (exception instanceof PortletIdException)) {

			errorMessage =
				"noninstanceable-widgets-can-be-embedded-only-once-on-the-" +
					"same-page";
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"error", LanguageUtil.get(themeDisplay.getRequest(), errorMessage));
	}

	protected JSONObject processLockedLayoutException(
		ActionRequest actionRequest) {

		return JSONUtil.put(
			"redirectURL",
			() -> layoutLockManager.getLockedLayoutURL(actionRequest));
	}

	@Reference
	protected LayoutLockManager layoutLockManager;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseContentPageEditorTransactionalMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

}