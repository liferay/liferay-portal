/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.notification;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.KaleoWorkflowModelConverter;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTimerInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationMessageGenerationException;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationMessageGenerator;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;

import java.io.Serializable;
import java.io.StringWriter;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 * @author Michael C. Han
 */
@Component(service = NotificationMessageGenerator.class)
public class TemplateNotificationMessageGenerator
	implements NotificationMessageGenerator {

	@Override
	public String generateMessage(
			String kaleoClassName, long kaleoClassPK, String notificationName,
			String notificationTemplateLanguage, String notificationTemplate,
			ExecutionContext executionContext)
		throws NotificationMessageGenerationException {

		try {
			Template template = _getTemplate(
				kaleoClassName, kaleoClassPK, notificationName,
				notificationTemplate, notificationTemplateLanguage);

			_populateContextVariables(template, executionContext);

			if (_log.isDebugEnabled()) {
				template.forEach(
					(key, value) -> _log.debug(
						StringBundler.concat(
							key, CharPool.SPACE, CharPool.OPEN_PARENTHESIS,
							value.getClass(), CharPool.CLOSE_PARENTHESIS)));
			}

			StringWriter stringWriter = new StringWriter();

			template.processTemplate(stringWriter);

			return stringWriter.toString();
		}
		catch (Exception exception) {
			throw new NotificationMessageGenerationException(
				"Unable to generate notification message", exception);
		}
	}

	@Override
	public String[] getTemplateLanguages() {
		return new String[] {"freemarker", "soy", "velocity"};
	}

	@Activate
	protected void activate() {
		_templateManagerNames.put(
			"freemarker", TemplateConstants.LANG_TYPE_FTL);
		_templateManagerNames.put("soy", TemplateConstants.LANG_TYPE_SOY);
		_templateManagerNames.put("velocity", TemplateConstants.LANG_TYPE_VM);
	}

	private Template _getTemplate(
			String kaleoClassName, long kaleoClassPK, String notificationName,
			String notificationTemplate, String notificationTemplateLanguage)
		throws Exception {

		String templateManagerName = _templateManagerNames.get(
			notificationTemplateLanguage);

		if (Validator.isNull(templateManagerName)) {
			throw new NotificationMessageGenerationException(
				"Unsupported notification template language " +
					notificationTemplateLanguage);
		}

		String templateId = notificationName + kaleoClassName + kaleoClassPK;

		return TemplateManagerUtil.getTemplate(
			templateManagerName,
			new StringTemplateResource(templateId, notificationTemplate), true);
	}

	private void _populateContextVariables(
			Template template, ExecutionContext executionContext)
		throws Exception {

		Map<String, Serializable> workflowContext =
			executionContext.getWorkflowContext();

		if (workflowContext == null) {
			KaleoInstanceToken kaleoInstanceToken =
				executionContext.getKaleoInstanceToken();

			KaleoInstance kaleoInstance = kaleoInstanceToken.getKaleoInstance();

			workflowContext = WorkflowContextUtil.convert(
				kaleoInstance.getWorkflowContext());
		}

		for (Map.Entry<String, Serializable> entry :
				workflowContext.entrySet()) {

			template.put(entry.getKey(), entry.getValue());
		}

		template.put(
			"kaleoInstanceToken", executionContext.getKaleoInstanceToken());

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			executionContext.getKaleoTaskInstanceToken();

		if (kaleoTaskInstanceToken != null) {
			KaleoTask kaleoTask = kaleoTaskInstanceToken.getKaleoTask();

			template.put("kaleoTaskInstanceToken", kaleoTaskInstanceToken);
			template.put("taskName", kaleoTask.getName());

			ServiceContext serviceContext =
				executionContext.getServiceContext();

			User user = _userLocalService.getUser(
				serviceContext.getGuestOrUserId());

			template.put("userId", user.getUserId());
			template.put("userName", user.getFullName());

			template.put(
				"workflowTaskAssignees",
				_kaleoWorkflowModelConverter.getWorkflowTaskAssignees(
					kaleoTaskInstanceToken));
		}
		else {
			KaleoInstanceToken kaleoInstanceToken =
				executionContext.getKaleoInstanceToken();

			template.put("userId", kaleoInstanceToken.getUserId());
			template.put("userName", kaleoInstanceToken.getUserName());
		}

		KaleoTimerInstanceToken kaleoTimerInstanceToken =
			executionContext.getKaleoTimerInstanceToken();

		if (kaleoTimerInstanceToken != null) {
			template.put("kaleoTimerInstanceToken", kaleoTimerInstanceToken);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TemplateNotificationMessageGenerator.class);

	@Reference
	private KaleoWorkflowModelConverter _kaleoWorkflowModelConverter;

	private final Map<String, String> _templateManagerNames = new HashMap<>();

	@Reference
	private UserLocalService _userLocalService;

}