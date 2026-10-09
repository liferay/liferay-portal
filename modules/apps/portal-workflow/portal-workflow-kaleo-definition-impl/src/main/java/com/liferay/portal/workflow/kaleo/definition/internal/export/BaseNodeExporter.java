/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.export;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.workflow.kaleo.definition.Action;
import com.liferay.portal.workflow.kaleo.definition.AddressRecipient;
import com.liferay.portal.workflow.kaleo.definition.Assignment;
import com.liferay.portal.workflow.kaleo.definition.AssignmentType;
import com.liferay.portal.workflow.kaleo.definition.DelayDuration;
import com.liferay.portal.workflow.kaleo.definition.DurationScale;
import com.liferay.portal.workflow.kaleo.definition.ExecutionType;
import com.liferay.portal.workflow.kaleo.definition.Node;
import com.liferay.portal.workflow.kaleo.definition.Notification;
import com.liferay.portal.workflow.kaleo.definition.NotificationReceptionType;
import com.liferay.portal.workflow.kaleo.definition.NotificationType;
import com.liferay.portal.workflow.kaleo.definition.Recipient;
import com.liferay.portal.workflow.kaleo.definition.RecipientType;
import com.liferay.portal.workflow.kaleo.definition.ResourceActionAssignment;
import com.liferay.portal.workflow.kaleo.definition.RoleAssignment;
import com.liferay.portal.workflow.kaleo.definition.RoleRecipient;
import com.liferay.portal.workflow.kaleo.definition.ScriptAction;
import com.liferay.portal.workflow.kaleo.definition.ScriptAssignment;
import com.liferay.portal.workflow.kaleo.definition.ScriptLanguage;
import com.liferay.portal.workflow.kaleo.definition.ScriptRecipient;
import com.liferay.portal.workflow.kaleo.definition.TemplateLanguage;
import com.liferay.portal.workflow.kaleo.definition.Timer;
import com.liferay.portal.workflow.kaleo.definition.Transition;
import com.liferay.portal.workflow.kaleo.definition.UpdateStatusAction;
import com.liferay.portal.workflow.kaleo.definition.UserAssignment;
import com.liferay.portal.workflow.kaleo.definition.UserRecipient;
import com.liferay.portal.workflow.kaleo.definition.export.NodeExporter;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael C. Han
 */
public abstract class BaseNodeExporter implements NodeExporter {

	@Override
	public void exportNode(Node node, Element element, String namespace) {
		Element nodeElement = createNodeElement(element, namespace);

		addTextElement(nodeElement, "name", node.getName());

		if (Validator.isNotNull(node.getDescription())) {
			addTextElement(nodeElement, "description", node.getDescription());
		}

		if (Validator.isNotNull(node.getMetadata())) {
			addCDataElement(nodeElement, "metadata", node.getMetadata());
		}

		Set<Action> actions = node.getActions();
		Set<Notification> notifications = node.getNotifications();

		if (!actions.isEmpty() || !notifications.isEmpty()) {
			Element actionsElement = nodeElement.addElement("actions");

			_exportActionsElement(
				actions, notifications, actionsElement, "action",
				"notification");
		}

		exportAdditionalNodeElements(node, nodeElement);
		_exportLabelsElement(nodeElement, node.getLabelMap());
		_exportTransitionsElement(node, nodeElement);
	}

	protected void addCDataElement(
		Element element, String elementName, String text) {

		Element childElement = element.addElement(elementName);

		childElement.addCDATA(text);
	}

	protected void addTextElement(
		Element element, String elementName, String text) {

		Element childElement = element.addElement(elementName);

		if (Validator.isNotNull(text)) {
			childElement.addText(text);
		}
	}

	protected abstract Element createNodeElement(
		Element element, String namespace);

	protected abstract void exportAdditionalNodeElements(
		Node node, Element nodeElement);

	protected void exportAssignmentsElement(
		Set<Assignment> assignments, Element parentElement,
		String assignmentsElementName) {

		if (assignments.isEmpty()) {
			return;
		}

		Element assignmentsElement = parentElement.addElement(
			assignmentsElementName);

		Element resourceActionsElement = null;

		Element rolesElement = null;

		for (Assignment assignment : assignments) {
			AssignmentType assignmentType = assignment.getAssignmentType();

			if (assignmentType.equals(AssignmentType.RESOURCE_ACTION)) {
				if (resourceActionsElement == null) {
					resourceActionsElement = assignmentsElement.addElement(
						"resource-actions");
				}

				ResourceActionAssignment resourceActionAssignment =
					(ResourceActionAssignment)assignment;

				addTextElement(
					resourceActionsElement, "resource-action",
					resourceActionAssignment.getActionId());
			}
			else if (assignmentType.equals(AssignmentType.ROLE)) {
				if (rolesElement == null) {
					rolesElement = assignmentsElement.addElement("roles");
				}

				Element roleElement = rolesElement.addElement("role");

				RoleAssignment roleAssignment = (RoleAssignment)assignment;

				_populateRoleElement(
					roleElement, roleAssignment.getRoleId(),
					roleAssignment.getRoleType(), roleAssignment.getRoleName(),
					roleAssignment.isAutoCreate());
			}
			else if (assignmentType.equals(AssignmentType.SCRIPT)) {
				Element scriptedAssignmentElement =
					assignmentsElement.addElement("scripted-assignment");

				ScriptAssignment scriptAssignment =
					(ScriptAssignment)assignment;

				ScriptLanguage scriptLanguage =
					scriptAssignment.getScriptLanguage();

				populateScriptingElement(
					scriptedAssignmentElement, scriptAssignment.getScript(),
					scriptLanguage.getValue(),
					scriptAssignment.getScriptRequiredContexts());
			}
			else if (assignmentType.equals(AssignmentType.USER)) {
				Element userElement = assignmentsElement.addElement("user");

				UserAssignment userAssignment = (UserAssignment)assignment;

				_populateUserElement(
					userElement, userAssignment.getUserId(),
					userAssignment.getEmailAddress(),
					userAssignment.getScreenName());
			}
		}
	}

	protected void exportTimersElement(
		Node node, Element nodeElement, String timersElementName,
		String timerElementName) {

		Set<Timer> timers = node.getTimers();

		if (timers.isEmpty()) {
			return;
		}

		Element timersElement = nodeElement.addElement(timersElementName);

		for (Timer timer : timers) {
			Element timerElement = timersElement.addElement(timerElementName);

			addTextElement(timerElement, "name", timer.getName());

			if (Validator.isNotNull(timer.getDescription())) {
				addTextElement(
					timerElement, "description", timer.getDescription());
			}

			_addDelayDuration(timerElement, "delay", timer.getDelayDuration());

			DelayDuration recurrenceDelayDuration = timer.getRecurrence();

			if (recurrenceDelayDuration != null) {
				_addDelayDuration(
					timerElement, "recurrence", recurrenceDelayDuration);
			}

			if (timer.isBlocking()) {
				addTextElement(
					timerElement, "blocking",
					String.valueOf(timer.isBlocking()));
			}

			Set<Action> actions = timer.getActions();

			Set<Notification> notifications = timer.getNotifications();

			Set<Assignment> assignments = timer.getReassignments();

			if (actions.isEmpty() && notifications.isEmpty() &&
				assignments.isEmpty()) {

				continue;
			}

			Element timerActionsElement = timerElement.addElement(
				"timer-actions");

			_exportActionsElement(
				actions, notifications, timerActionsElement, "timer-action",
				"timer-notification");
			exportAssignmentsElement(
				assignments, timerActionsElement, "reassignments");
		}
	}

	protected void populateScriptingElement(
		Element scriptingElement, String script, String scriptLanguage,
		String scriptRequiredContexts) {

		addCDataElement(scriptingElement, "script", script);
		addTextElement(scriptingElement, "script-language", scriptLanguage);

		if (Validator.isNotNull(scriptRequiredContexts)) {
			addTextElement(
				scriptingElement, "script-required-contexts",
				scriptRequiredContexts);
		}
	}

	private void _addDelayDuration(
		Element timerElement, String elementName, DelayDuration delayDuration) {

		Element delayElement = timerElement.addElement(elementName);

		addTextElement(
			delayElement, "duration",
			String.valueOf(delayDuration.getDuration()));

		DurationScale durationScale = delayDuration.getDurationScale();

		addTextElement(delayElement, "scale", durationScale.getValue());
	}

	private void _exportActionElement(Element actionElement, Action action) {
		addTextElement(actionElement, "name", action.getName());

		if (Validator.isNotNull(action.getDescription())) {
			addTextElement(
				actionElement, "description", action.getDescription());
		}

		if (action instanceof ScriptAction) {
			ScriptAction scriptAction = (ScriptAction)action;

			ScriptLanguage scriptLanguage = scriptAction.getScriptLanguage();

			populateScriptingElement(
				actionElement, scriptAction.getScript(),
				scriptLanguage.getValue(),
				scriptAction.getScriptRequiredContexts());
		}
		else if (action instanceof UpdateStatusAction) {
			UpdateStatusAction updateStatusAction = (UpdateStatusAction)action;

			addTextElement(
				actionElement, "status",
				String.valueOf(updateStatusAction.getStatus()));
		}

		if (action.getPriority() > 0) {
			addTextElement(
				actionElement, "priority",
				String.valueOf(action.getPriority()));
		}

		ExecutionType executionType = action.getExecutionType();

		addTextElement(
			actionElement, "execution-type", executionType.getValue());
	}

	private void _exportActionsElement(
		Set<Action> actions, Set<Notification> notifications,
		Element actionsElement, String actionElementName,
		String notificationElementName) {

		for (Action action : actions) {
			Element actionElement = actionsElement.addElement(
				actionElementName);

			_exportActionElement(actionElement, action);
		}

		for (Notification notification : notifications) {
			Element notificationElement = actionsElement.addElement(
				notificationElementName);

			_exportNotificationElement(notificationElement, notification);
		}
	}

	private void _exportLabelsElement(
		Element element, Map<Locale, String> labelMap) {

		if (MapUtil.isEmpty(labelMap)) {
			return;
		}

		Element labelsElement = element.addElement("labels");

		for (Map.Entry<Locale, String> entry : labelMap.entrySet()) {
			Element labelElement = labelsElement.addElement("label");

			labelElement.addAttribute(
				"language-id", LocaleUtil.toLanguageId(entry.getKey()));
			labelElement.addText(entry.getValue());
		}
	}

	private void _exportNotificationElement(
		Element notificationElement, Notification notification) {

		addTextElement(notificationElement, "name", notification.getName());

		if (Validator.isNotNull(notification.getDescription())) {
			addTextElement(
				notificationElement, "description",
				notification.getDescription());
		}

		addCDataElement(
			notificationElement, "template", notification.getTemplate());

		TemplateLanguage templateLanguage = notification.getTemplateLanguage();

		addTextElement(
			notificationElement, "template-language",
			templateLanguage.getValue());

		Set<NotificationType> notificationTypes =
			notification.getNotificationTypes();

		for (NotificationType notificationType : notificationTypes) {
			addTextElement(
				notificationElement, "notification-type",
				notificationType.getValue());
		}

		Map<NotificationReceptionType, Set<Recipient>> recipientsMap =
			notification.getRecipientsMap();

		for (Map.Entry<NotificationReceptionType, Set<Recipient>> entry :
				recipientsMap.entrySet()) {

			Set<Recipient> recipients = entry.getValue();
			NotificationReceptionType notificationReceptionType =
				entry.getKey();

			_exportRecipientsElement(
				notificationElement, recipients, notificationReceptionType);
		}

		ExecutionType executionType = notification.getExecutionType();

		addTextElement(
			notificationElement, "execution-type", executionType.getValue());
	}

	private void _exportRecipientsElement(
		Element notificationElement, Set<Recipient> recipients,
		NotificationReceptionType notificationReceptionType) {

		if (recipients.isEmpty()) {
			return;
		}

		Element recipientsElement = notificationElement.addElement(
			"recipients");

		recipientsElement.addAttribute(
			"receptionType", notificationReceptionType.getValue());

		Element rolesElement = null;

		for (Recipient recipient : recipients) {
			RecipientType recipientType = recipient.getRecipientType();

			if (recipientType.equals(RecipientType.ADDRESS)) {
				AddressRecipient addressRecipient = (AddressRecipient)recipient;

				addTextElement(
					recipientsElement, "address",
					addressRecipient.getAddress());
			}
			else if (recipientType.equals(RecipientType.ASSIGNEES)) {
				addTextElement(recipientsElement, "assignees", null);
			}
			else if (recipientType.equals(RecipientType.ROLE)) {
				if (rolesElement == null) {
					rolesElement = recipientsElement.addElement("roles");
				}

				Element roleElement = rolesElement.addElement("role");

				RoleRecipient roleRecipient = (RoleRecipient)recipient;

				_populateRoleElement(
					roleElement, roleRecipient.getRoleId(),
					roleRecipient.getRoleType(), roleRecipient.getRoleName(),
					roleRecipient.isAutoCreate());
			}
			else if (recipientType.equals(RecipientType.SCRIPT)) {
				Element scriptedRecipientElement = recipientsElement.addElement(
					"scripted-recipient");

				ScriptRecipient scriptRecipient = (ScriptRecipient)recipient;

				ScriptLanguage scriptLanguage =
					scriptRecipient.getScriptLanguage();

				populateScriptingElement(
					scriptedRecipientElement, scriptRecipient.getScript(),
					scriptLanguage.getValue(),
					scriptRecipient.getScriptRequiredContexts());
			}
			else if (recipientType.equals(RecipientType.USER)) {
				Element userElement = recipientsElement.addElement("user");

				UserRecipient userRecipient = (UserRecipient)recipient;

				_populateUserElement(
					userElement, userRecipient.getUserId(),
					userRecipient.getEmailAddress(),
					userRecipient.getScreenName());
			}
		}
	}

	private void _exportTransitionsElement(Node node, Element nodeElement) {
		List<Transition> outgoingTransitions =
			node.getOutgoingTransitionsList();

		if (outgoingTransitions.isEmpty()) {
			return;
		}

		Element transitionsElement = nodeElement.addElement("transitions");

		for (Transition outgoingTransition : outgoingTransitions) {
			Element transition = transitionsElement.addElement("transition");

			addTextElement(
				transition, "default",
				String.valueOf(outgoingTransition.isDefault()));
			addTextElement(transition, "name", outgoingTransition.getName());

			Node targetNode = outgoingTransition.getTargetNode();

			addTextElement(transition, "target", targetNode.getName());

			_exportLabelsElement(transition, outgoingTransition.getLabelMap());
		}
	}

	private void _populateRoleElement(
		Element roleElement, long roleId, String roleType, String roleName,
		boolean autoCreate) {

		if (roleId > 0) {
			addTextElement(roleElement, "role-id", String.valueOf(roleId));
		}
		else {
			addTextElement(roleElement, "role-type", roleType);
			addTextElement(roleElement, "name", roleName);

			if (!autoCreate) {
				addTextElement(
					roleElement, "auto-create", String.valueOf(autoCreate));
			}
		}
	}

	private void _populateUserElement(
		Element userElement, long userId, String emailAddress,
		String screenName) {

		if (userId > 0) {
			addTextElement(userElement, "user-id", String.valueOf(userId));
		}

		if (Validator.isNotNull(emailAddress)) {
			addTextElement(userElement, "email-address", emailAddress);
		}

		if (Validator.isNotNull(screenName)) {
			addTextElement(userElement, "screen-name", screenName);
		}
	}

}