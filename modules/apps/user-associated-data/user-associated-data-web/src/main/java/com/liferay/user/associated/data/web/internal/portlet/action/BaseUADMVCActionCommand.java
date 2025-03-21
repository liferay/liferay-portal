/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.portlet.action;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.aui.AUIUtil;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.user.associated.data.display.UADDisplay;
import com.liferay.user.associated.data.web.internal.helper.SelectedUserHelper;
import com.liferay.user.associated.data.web.internal.helper.UADApplicationSummaryHelper;
import com.liferay.user.associated.data.web.internal.registry.UADRegistry;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
public abstract class BaseUADMVCActionCommand extends BaseMVCActionCommand {

	protected void doMultipleAction(
			List<Serializable> primaryKeys,
			UnsafeConsumer<Serializable, Exception> unsafeConsumer)
		throws Exception {

		for (Serializable primaryKey : primaryKeys) {
			unsafeConsumer.accept(primaryKey);
		}
	}

	protected void doNonreviewableRedirect(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String mvcRenderCommandName = null;

		long selectedUserId = getSelectedUserId(actionRequest);

		int totalNonreviewableUADEntitiesCount =
			uadApplicationSummaryHelper.getTotalNonreviewableUADEntitiesCount(
				selectedUserId);

		if (totalNonreviewableUADEntitiesCount == 0) {
			int totalReviewableUADEntitiesCount =
				uadApplicationSummaryHelper.getTotalReviewableUADEntitiesCount(
					selectedUserId);

			if (totalReviewableUADEntitiesCount == 0) {
				mvcRenderCommandName =
					"/user_associated_data/completed_data_erasure";
			}
			else {
				mvcRenderCommandName = "/user_associated_data/review_uad_data";
			}
		}

		if (Validator.isNull(mvcRenderCommandName)) {
			return;
		}

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			actionRequest, UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
			PortletRequest.RENDER_PHASE);

		liferayPortletURL.setParameter(
			"p_u_i_d", String.valueOf(selectedUserId));
		liferayPortletURL.setParameter(
			"mvcRenderCommandName", mvcRenderCommandName);

		sendRedirect(
			actionRequest, actionResponse, liferayPortletURL.toString());
	}

	protected void doReviewableRedirect(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String mvcRenderCommandName = null;

		long selectedUserId = getSelectedUserId(actionRequest);

		int totalReviewableUADEntitiesCount =
			uadApplicationSummaryHelper.getTotalReviewableUADEntitiesCount(
				selectedUserId);

		if (totalReviewableUADEntitiesCount == 0) {
			int totalNonreviewableUADEntitiesCount =
				uadApplicationSummaryHelper.
					getTotalNonreviewableUADEntitiesCount(selectedUserId);

			if (totalNonreviewableUADEntitiesCount == 0) {
				mvcRenderCommandName =
					"/user_associated_data/completed_data_erasure";
			}
			else {
				mvcRenderCommandName =
					"/user_associated_data/anonymize_nonreviewable_uad_data";
			}
		}

		if (Validator.isNull(mvcRenderCommandName)) {
			return;
		}

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			actionRequest, UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
			PortletRequest.RENDER_PHASE);

		liferayPortletURL.setParameter(
			"p_u_i_d", String.valueOf(selectedUserId));
		liferayPortletURL.setParameter(
			"mvcRenderCommandName", mvcRenderCommandName);

		sendRedirect(
			actionRequest, actionResponse, liferayPortletURL.toString());
	}

	protected String[] getApplicationKeys(ActionRequest actionRequest) {
		String applicationKey = ParamUtil.getString(
			actionRequest, "applicationKey");

		if (Validator.isNotNull(applicationKey)) {
			return new String[] {applicationKey};
		}

		return ParamUtil.getStringValues(actionRequest, "applicationKeys");
	}

	protected List<String> getEntityTypes(ActionRequest actionRequest) {
		List<String> entityTypes = new ArrayList<>();

		Map<String, String[]> parameterMap = actionRequest.getParameterMap();

		for (String key : parameterMap.keySet()) {
			if (key.startsWith("uadRegistryKey__")) {
				entityTypes.add(
					StringUtil.removeSubstring(key, "uadRegistryKey__"));
			}
		}

		return entityTypes;
	}

	protected String[] getPrimaryKeys(
		ActionRequest actionRequest, String entityType) {

		entityType = AUIUtil.normalizeId(entityType);

		String primaryKey = ParamUtil.getString(
			actionRequest, "primaryKey__" + entityType);

		if (Validator.isNotNull(primaryKey)) {
			return new String[] {primaryKey};
		}

		return ParamUtil.getStringValues(
			actionRequest, "primaryKeys__" + entityType);
	}

	protected User getSelectedUser(ActionRequest actionRequest)
		throws PortalException {

		return selectedUserHelper.getSelectedUser(actionRequest);
	}

	protected long getSelectedUserId(ActionRequest actionRequest)
		throws PortalException {

		return selectedUserHelper.getSelectedUserId(actionRequest);
	}

	protected UADAnonymizer<?> getUADAnonymizer(
		ActionRequest actionRequest, String entityType) {

		return uadRegistry.getUADAnonymizer(
			_getUADRegistryKey(actionRequest, entityType));
	}

	protected UADDisplay<?> getUADDisplay(
		ActionRequest actionRequest, String entityType) {

		return uadRegistry.getUADDisplay(
			_getUADRegistryKey(actionRequest, entityType));
	}

	protected void handleExceptions(
			ActionRequest actionRequest, ActionResponse actionResponse,
			Exception exception, UADAnonymizer<Object> uadAnonymizer)
		throws Exception {

		if (exception instanceof NoSuchModelException) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Map<Class<?>, String> exceptionMessageMap =
			uadAnonymizer.getExceptionMessageMap(themeDisplay.getLocale());

		if (exceptionMessageMap.containsKey(exception.getClass())) {
			SessionErrors.add(
				actionRequest, "deleteUADEntityException",
				exceptionMessageMap.get(exception.getClass()));

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (Validator.isNotNull(redirect)) {
				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
		else {
			throw exception;
		}
	}

	@Reference
	protected SelectedUserHelper selectedUserHelper;

	@Reference
	protected UADApplicationSummaryHelper uadApplicationSummaryHelper;

	@Reference
	protected UADRegistry uadRegistry;

	private String _getUADRegistryKey(
		ActionRequest actionRequest, String entityType) {

		entityType = AUIUtil.normalizeId(entityType);

		return ParamUtil.getString(
			actionRequest, "uadRegistryKey__" + entityType);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseUADMVCActionCommand.class);

}