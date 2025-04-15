/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.notifications;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManagerUtil;
import com.liferay.portal.workflow.security.permission.WorkflowTaskPermission;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jonathan Lee
 * @author Leonardo Barros
 */
@Component(
	property = "jakarta.portlet.name=" + PortletKeys.MY_WORKFLOW_TASK,
	service = UserNotificationHandler.class
)
public class WorkflowTaskUserNotificationHandler
	extends BaseUserNotificationHandler {

	public WorkflowTaskUserNotificationHandler() {
		setOpenDialog(true);
		setPortletId(PortletKeys.MY_WORKFLOW_TASK);
	}

	@Override
	public UserNotificationFeedEntry interpret(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		if (Objects.nonNull(
				WorkflowHandlerRegistryUtil.getWorkflowHandler(
					jsonObject.getString("entryClassName")))) {

			return super.interpret(userNotificationEvent, serviceContext);
		}

		Locale locale = serviceContext.getLocale();

		String title = _language.get(locale, "notification-no-longer-applies");

		return new UserNotificationFeedEntry(
			false,
			StringUtil.replace(
				_BODY_TEMPLATE_DEFAULT, new String[] {"[$BODY$]", "[$TITLE$]"},
				new String[] {
					_language.format(
						locale, "notification-for-x-was-deactivated",
						jsonObject.getString("entryType"), false),
					title
				}),
			StringPool.BLANK, false, title);
	}

	@Override
	public boolean isApplicable(
		UserNotificationEvent userNotificationEvent,
		ServiceContext serviceContext) {

		JSONObject jsonObject = null;

		try {
			jsonObject = _jsonFactory.createJSONObject(
				userNotificationEvent.getPayload());
		}
		catch (JSONException jsonException) {
			_log.error(jsonException);
		}

		if (jsonObject == null) {
			return false;
		}

		long ctCollectionId = jsonObject.getLong(
			WorkflowConstants.CONTEXT_CT_COLLECTION_ID);

		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		if ((ctCollection != null) &&
			(ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED)) {

			ctCollectionId = CTConstants.CT_COLLECTION_ID_PRODUCTION;
		}

		if (ctCollectionId == CTCollectionThreadLocal.getCTCollectionId()) {
			return true;
		}

		return false;
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		return _getMessage(userNotificationEvent, serviceContext);
	}

	@Override
	protected String getLink(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		if (!_isNotifiable(userNotificationEvent, serviceContext)) {
			return StringPool.BLANK;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		if (!isApplicable(userNotificationEvent, serviceContext)) {
			return StringPool.BLANK;
		}

		WorkflowHandler<?> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				jsonObject.getString("entryClassName"));

		long workflowTaskId = jsonObject.getLong("workflowTaskId");

		if ((workflowHandler == null) ||
			!_hasPermission(
				jsonObject.getLong(WorkflowConstants.CONTEXT_CT_COLLECTION_ID),
				workflowTaskId, serviceContext)) {

			return StringPool.BLANK;
		}

		serviceContext.setAttribute(
			WorkflowConstants.CONTEXT_ENTRY_CLASS_PK,
			jsonObject.getString(WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));
		serviceContext.setAttribute(
			WorkflowConstants.CONTEXT_URL,
			jsonObject.getString(WorkflowConstants.CONTEXT_URL));
		serviceContext.setAttribute("plid", jsonObject.getLong("plid"));
		serviceContext.setAttribute(
			"portletId", jsonObject.getString("portletId"));

		return workflowHandler.getNotificationLink(
			workflowTaskId, serviceContext);
	}

	@Override
	protected String getTitle(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		return _getMessage(userNotificationEvent, serviceContext);
	}

	private WorkflowTask _fetchWorkflowTask(
			long ctCollectionId, long workflowTaskId)
		throws Exception {

		if (workflowTaskId <= 0) {
			return null;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			return WorkflowTaskManagerUtil.fetchWorkflowTask(workflowTaskId);
		}
	}

	private String _getCTCollectionBody(
		long ctCollectionId, String languageId) {

		if (ctCollectionId == CTConstants.CT_COLLECTION_ID_PRODUCTION) {
			return _language.get(
				LocaleUtil.fromLanguageId(languageId),
				"select-production-to-review-the-change");
		}

		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		if (ctCollection != null) {
			return _language.format(
				LocaleUtil.fromLanguageId(languageId),
				"select-the-publication-x-to-review-the-change",
				new String[] {ctCollection.getName()});
		}

		return StringPool.BLANK;
	}

	private String _getMessage(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			userNotificationEvent.getPayload());

		String notificationMessage = jsonObject.getString(
			"notificationMessage");

		long workflowTaskId = jsonObject.getLong("workflowTaskId");

		if (workflowTaskId > 0) {
			long ctCollectionId = jsonObject.getLong(
				WorkflowConstants.CONTEXT_CT_COLLECTION_ID);

			CTCollection ctCollection =
				_ctCollectionLocalService.fetchCTCollection(ctCollectionId);

			if ((ctCollection != null) &&
				(ctCollection.getStatus() ==
					WorkflowConstants.STATUS_APPROVED)) {

				ctCollectionId = CTConstants.CT_COLLECTION_ID_PRODUCTION;
			}

			WorkflowTask workflowTask = _fetchWorkflowTask(
				ctCollectionId, workflowTaskId);

			if (workflowTask == null) {
				_userNotificationEventLocalService.deleteUserNotificationEvent(
					userNotificationEvent.getUserNotificationEventId());

				return StringPool.BLANK;
			}

			if (ctCollectionId != CTCollectionThreadLocal.getCTCollectionId()) {
				String ctCollectionBody = _getCTCollectionBody(
					ctCollectionId, serviceContext.getLanguageId());

				return HtmlUtil.escape(
					notificationMessage + " " + ctCollectionBody);
			}
		}

		return HtmlUtil.escape(notificationMessage);
	}

	private boolean _hasPermission(
			long ctCollectionId, long workflowTaskId,
			ServiceContext serviceContext)
		throws Exception {

		WorkflowTask workflowTask = _fetchWorkflowTask(
			ctCollectionId, workflowTaskId);

		if ((workflowTask == null) ||
			(serviceContext.getThemeDisplay() == null)) {

			return false;
		}

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		long groupId = MapUtil.getLong(
			workflowTask.getOptionalAttributes(), "groupId",
			themeDisplay.getSiteGroupId());

		return _workflowTaskPermission.contains(
			themeDisplay.getPermissionChecker(), workflowTask, groupId);
	}

	private boolean _isNotifiable(
		UserNotificationEvent userNotificationEvent,
		ServiceContext serviceContext) {

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				userNotificationEvent.getPayload());

			if (!jsonObject.has("workflowTaskId")) {
				return false;
			}

			long ctCollectionId = jsonObject.getLong(
				WorkflowConstants.CONTEXT_CT_COLLECTION_ID);

			CTCollection ctCollection =
				_ctCollectionLocalService.fetchCTCollection(ctCollectionId);

			if ((ctCollection != null) &&
				(ctCollection.getStatus() ==
					WorkflowConstants.STATUS_APPROVED)) {

				ctCollectionId = CTConstants.CT_COLLECTION_ID_PRODUCTION;
			}

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollectionId)) {

				return WorkflowTaskManagerUtil.isNotifiableUser(
					serviceContext.getUserId(),
					jsonObject.getLong("workflowTaskId"));
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	private static final String _BODY_TEMPLATE_DEFAULT =
		"<div class=\"title\">[$TITLE$]</div><div class=\"body\">[$BODY$]" +
			"</div>";

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowTaskUserNotificationHandler.class);

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	@Reference
	private WorkflowTaskPermission _workflowTaskPermission;

}