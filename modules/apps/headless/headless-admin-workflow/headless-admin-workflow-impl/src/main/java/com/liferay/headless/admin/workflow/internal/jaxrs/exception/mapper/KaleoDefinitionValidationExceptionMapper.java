/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iliyan Peychev
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Admin.Workflow)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Admin.Workflow.KaleoDefinitionValidationExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class KaleoDefinitionValidationExceptionMapper
	extends BaseExceptionMapper<KaleoDefinitionValidationException> {

	@Override
	protected Problem getProblem(
		KaleoDefinitionValidationException kaleoDefinitionValidationException) {

		Class<?> clazz = kaleoDefinitionValidationException.getClass();

		return new Problem(
			null, Response.Status.BAD_REQUEST,
			_getTitle(kaleoDefinitionValidationException), clazz.getName());
	}

	private String _getLocalizedMessage(String key, Object[] arguments) {
		ResourceBundle resourceBundle =
			ResourceBundleUtil.getModuleAndPortalResourceBundle(
				_acceptLanguage.getPreferredLocale(),
				KaleoDefinitionValidationExceptionMapper.class);

		if (arguments == null) {
			return _language.get(resourceBundle, key);
		}

		return _language.format(resourceBundle, key, arguments, false);
	}

	private String _getTitle(
		KaleoDefinitionValidationException kaleoDefinitionValidationException) {

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.EmptyNotificationTemplate) {

			KaleoDefinitionValidationException.EmptyNotificationTemplate
				emptyNotificationTemplate =
					(KaleoDefinitionValidationException.
						EmptyNotificationTemplate)
							kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"the-x-node-has-an-empty-notification-template",
				new Object[] {emptyNotificationTemplate.getNode()});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.MultipleInitialStateNodes) {

			KaleoDefinitionValidationException.MultipleInitialStateNodes
				multipleInitialStateNodes =
					(KaleoDefinitionValidationException.
						MultipleInitialStateNodes)
							kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"the-workflow-has-too-many-start-nodes-state-nodes-x-and-x",
				new Object[] {
					multipleInitialStateNodes.getState1(),
					multipleInitialStateNodes.getState2()
				});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.
					MustNotSetIncomingTransition) {

			KaleoDefinitionValidationException.MustNotSetIncomingTransition
				mustNotSetIncomingTransition =
					(KaleoDefinitionValidationException.
						MustNotSetIncomingTransition)
							kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"the-x-node-cannot-have-an-incoming-transition",
				new Object[] {mustNotSetIncomingTransition.getNode()});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.MustPairedForkAndJoinNodes) {

			KaleoDefinitionValidationException.MustPairedForkAndJoinNodes
				mustPairedForkAndJoinNodes =
					(KaleoDefinitionValidationException.
						MustPairedForkAndJoinNodes)
							kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"fork-x-and-join-x-nodes-must-be-paired",
				new Object[] {
					mustPairedForkAndJoinNodes.getFork(),
					mustPairedForkAndJoinNodes.getNode()
				});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.MustSetAssignments) {

			KaleoDefinitionValidationException.MustSetAssignments
				mustSetAssignments =
					(KaleoDefinitionValidationException.MustSetAssignments)
						kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"specify-at-least-one-assignment-for-the-x-task-node",
				new Object[] {mustSetAssignments.getTask()});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.MustSetIncomingTransition) {

			KaleoDefinitionValidationException.MustSetIncomingTransition
				mustSetIncomingTransition =
					(KaleoDefinitionValidationException.
						MustSetIncomingTransition)
							kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"the-x-node-must-have-an-incoming-transition",
				new Object[] {mustSetIncomingTransition.getNode()});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.MustSetInitialStateNode) {

			return _getLocalizedMessage("you-must-define-a-start-node", null);
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.MustSetJoinNode) {

			KaleoDefinitionValidationException.MustSetJoinNode mustSetJoinNode =
				(KaleoDefinitionValidationException.MustSetJoinNode)
					kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"the-x-fork-node-must-have-a-matching-join-node",
				new Object[] {mustSetJoinNode.getFork()});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.
					MustSetMultipleOutgoingTransition) {

			KaleoDefinitionValidationException.MustSetMultipleOutgoingTransition
				mustSetMultipleOutgoingTransition =
					(KaleoDefinitionValidationException.
						MustSetMultipleOutgoingTransition)
							kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"the-x-node-must-have-at-least-two-outgoing-transitions",
				new Object[] {mustSetMultipleOutgoingTransition.getNode()});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.MustSetOutgoingTransition) {

			KaleoDefinitionValidationException.MustSetOutgoingTransition
				mustSetOutgoingTransition =
					(KaleoDefinitionValidationException.
						MustSetOutgoingTransition)
							kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"the-x-node-must-have-an-outgoing-transition",
				new Object[] {mustSetOutgoingTransition.getNode()});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.MustSetSourceNode) {

			KaleoDefinitionValidationException.MustSetSourceNode
				mustSetSourceNode =
					(KaleoDefinitionValidationException.MustSetSourceNode)
						kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"the-x-transition-must-have-a-source-node",
				new Object[] {mustSetSourceNode.getNode()});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.MustSetTargetNode) {

			KaleoDefinitionValidationException.MustSetTargetNode
				mustSetTargetNode =
					(KaleoDefinitionValidationException.MustSetTargetNode)
						kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"the-x-transition-must-end-at-a-node",
				new Object[] {mustSetTargetNode.getNode()});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.
					MustSetTaskFormDefinitionOrReference) {

			KaleoDefinitionValidationException.
				MustSetTaskFormDefinitionOrReference
					mustSetTaskFormDefinitionOrReference =
						(KaleoDefinitionValidationException.
							MustSetTaskFormDefinitionOrReference)
								kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"the-task-form-x-for-task-x-must-specify-a-form-reference-or-" +
					"form-definition",
				new Object[] {
					mustSetTaskFormDefinitionOrReference.getTaskForm(),
					mustSetTaskFormDefinitionOrReference.getTask()
				});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.MustSetTerminalStateNode) {

			return _getLocalizedMessage("you-must-define-an-end-node", null);
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.MustSetValidNodeNameLength) {

			KaleoDefinitionValidationException.MustSetValidNodeNameLength
				mustSetValidNodeNameLength =
					(KaleoDefinitionValidationException.
						MustSetValidNodeNameLength)
							kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"the-x-node-name-exceeds-the-length-limit-of-x-characters",
				new Object[] {
					mustSetValidNodeNameLength.getNode(),
					mustSetValidNodeNameLength.getLength()
				});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.UnbalancedForkAndJoinNode) {

			KaleoDefinitionValidationException.UnbalancedForkAndJoinNode
				unbalancedForkAndJoinNode =
					(KaleoDefinitionValidationException.
						UnbalancedForkAndJoinNode)
							kaleoDefinitionValidationException;

			return _getLocalizedMessage(
				"fix-the-errors-between-the-fork-node-x-and-join-node-x",
				new Object[] {
					unbalancedForkAndJoinNode.getFork(),
					unbalancedForkAndJoinNode.getJoin()
				});
		}

		if (kaleoDefinitionValidationException instanceof
				KaleoDefinitionValidationException.UnbalancedForkAndJoinNodes) {

			return _getLocalizedMessage(
				"each-fork-node-requires-a-join-node-make-sure-all-forks-and-" +
					"joins-are-properly-paired",
				null);
		}

		String message = kaleoDefinitionValidationException.getMessage();

		if (Validator.isNotNull(message)) {
			return message;
		}

		return _getLocalizedMessage("please-enter-valid-content", null);
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	@Reference
	private Language _language;

}