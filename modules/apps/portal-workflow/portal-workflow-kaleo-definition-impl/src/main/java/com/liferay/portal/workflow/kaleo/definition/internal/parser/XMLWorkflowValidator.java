/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.parser;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;
import com.liferay.portal.workflow.kaleo.definition.Action;
import com.liferay.portal.workflow.kaleo.definition.Assignment;
import com.liferay.portal.workflow.kaleo.definition.Condition;
import com.liferay.portal.workflow.kaleo.definition.Definition;
import com.liferay.portal.workflow.kaleo.definition.Node;
import com.liferay.portal.workflow.kaleo.definition.NodeType;
import com.liferay.portal.workflow.kaleo.definition.Notification;
import com.liferay.portal.workflow.kaleo.definition.NotificationReceptionType;
import com.liferay.portal.workflow.kaleo.definition.Recipient;
import com.liferay.portal.workflow.kaleo.definition.ScriptAction;
import com.liferay.portal.workflow.kaleo.definition.ScriptAssignment;
import com.liferay.portal.workflow.kaleo.definition.ScriptLanguage;
import com.liferay.portal.workflow.kaleo.definition.ScriptRecipient;
import com.liferay.portal.workflow.kaleo.definition.State;
import com.liferay.portal.workflow.kaleo.definition.Task;
import com.liferay.portal.workflow.kaleo.definition.Timer;
import com.liferay.portal.workflow.kaleo.definition.Transition;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;
import com.liferay.portal.workflow.kaleo.definition.parser.NodeValidator;
import com.liferay.portal.workflow.kaleo.definition.parser.WorkflowValidator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Marcellus Tavares
 */
@Component(service = WorkflowValidator.class)
public class XMLWorkflowValidator implements WorkflowValidator {

	@Override
	public void validate(Definition definition) throws WorkflowException {
		State initialState = definition.getInitialState();

		if (initialState == null) {
			throw new KaleoDefinitionValidationException.
				MustSetInitialStateNode();
		}

		List<State> terminalStates = definition.getTerminalStates();

		if (terminalStates.isEmpty()) {
			throw new KaleoDefinitionValidationException.
				MustSetTerminalStateNode();
		}

		if (!_scriptManagementConfigurationHelper.
				isAllowScriptContentToBeExecutedOrIncluded()) {

			_validateScriptLanguage(definition);
		}

		if (definition.getForksCount() != definition.getJoinsCount()) {
			throw new KaleoDefinitionValidationException.
				UnbalancedForkAndJoinNodes();
		}

		Collection<Node> nodes = definition.getNodes();

		for (Node node : nodes) {
			NodeValidator<Node> nodeValidator = _serviceTrackerMap.getService(
				node.getNodeType());

			nodeValidator.validate(definition, node);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext,
			(Class<NodeValidator<Node>>)(Class<?>)NodeValidator.class, null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(nodeValidator, emitter) -> emitter.emit(
					nodeValidator.getNodeType())));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private void _validateScriptLanguage(Definition definition)
		throws WorkflowException {

		String name = definition.getName();

		for (Node node : definition.getNodes()) {
			if (node instanceof Condition condition) {
				_validateScriptLanguage(name, condition.getScriptLanguage());
			}

			if (node instanceof Task task) {
				_validateScriptLanguageInAssignments(
					task.getAssignments(), name);
			}

			_validateScriptLanguageInActions(node.getActions(), name);
			_validateScriptLanguageInNotifications(
				name, node.getNotifications());

			Map<String, Transition> outgoingTransitions =
				node.getOutgoingTransitions();

			for (Transition transition : outgoingTransitions.values()) {
				_validateScriptLanguageInTimer(name, transition.getTimer());
			}

			for (Timer timer : node.getTimers()) {
				_validateScriptLanguageInTimer(name, timer);
			}
		}
	}

	private void _validateScriptLanguage(
			String name, ScriptLanguage scriptLanguage)
		throws WorkflowException {

		if (scriptLanguage == null) {
			return;
		}

		String value = scriptLanguage.getValue();

		if (Objects.equals(value, ScriptLanguage.DRL.getValue())) {
			throw new KaleoDefinitionValidationException.
				NotAllowedScriptLanguage("DRL is not allowed");
		}

		if (Objects.equals(value, ScriptLanguage.GROOVY.getValue())) {
			throw new KaleoDefinitionValidationException.
				NotAllowedScriptLanguage("Groovy is not allowed");
		}

		if (!Objects.equals(
				name,
				"Message Board Threads and Comments Reputation Approver") &&
			Objects.equals(value, ScriptLanguage.JAVA.getValue())) {

			throw new KaleoDefinitionValidationException.
				NotAllowedScriptLanguage("Java is not allowed");
		}
	}

	private void _validateScriptLanguageInActions(
			Set<Action> actions, String name)
		throws WorkflowException {

		for (Action action : actions) {
			if (action instanceof ScriptAction scriptAction) {
				_validateScriptLanguage(name, scriptAction.getScriptLanguage());
			}
		}
	}

	private void _validateScriptLanguageInAssignments(
			Set<Assignment> assignments, String name)
		throws WorkflowException {

		if (assignments == null) {
			return;
		}

		for (Assignment assignment : assignments) {
			if (assignment instanceof ScriptAssignment scriptAssignment) {
				_validateScriptLanguage(
					name, scriptAssignment.getScriptLanguage());
			}
		}
	}

	private void _validateScriptLanguageInNotifications(
			String name, Set<Notification> notifications)
		throws WorkflowException {

		for (Notification notification : notifications) {
			Map<NotificationReceptionType, Set<Recipient>> recipientsMap =
				notification.getRecipientsMap();

			if (recipientsMap == null) {
				continue;
			}

			for (Set<Recipient> recipients : recipientsMap.values()) {
				_validateScriptLanguageInRecipients(name, recipients);
			}
		}
	}

	private void _validateScriptLanguageInRecipients(
			String name, Set<Recipient> recipients)
		throws WorkflowException {

		for (Recipient recipient : recipients) {
			if (!(recipient instanceof ScriptRecipient scriptRecipient)) {
				continue;
			}

			_validateScriptLanguage(name, scriptRecipient.getScriptLanguage());
		}
	}

	private void _validateScriptLanguageInTimer(String name, Timer timer)
		throws WorkflowException {

		if (timer == null) {
			return;
		}

		_validateScriptLanguageInActions(timer.getActions(), name);
		_validateScriptLanguageInAssignments(timer.getReassignments(), name);
		_validateScriptLanguageInNotifications(name, timer.getNotifications());
	}

	@Reference
	private ScriptManagementConfigurationHelper
		_scriptManagementConfigurationHelper;

	private ServiceTrackerMap<NodeType, NodeValidator<Node>> _serviceTrackerMap;

}