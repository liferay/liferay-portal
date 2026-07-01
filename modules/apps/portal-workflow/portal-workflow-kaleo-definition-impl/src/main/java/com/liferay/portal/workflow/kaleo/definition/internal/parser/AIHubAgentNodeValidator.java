/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.parser;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.definition.AIHubAgent;
import com.liferay.portal.workflow.kaleo.definition.Definition;
import com.liferay.portal.workflow.kaleo.definition.NodeType;
import com.liferay.portal.workflow.kaleo.definition.Setting;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;
import com.liferay.portal.workflow.kaleo.definition.parser.NodeValidator;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carolina Barbosa
 */
@Component(service = NodeValidator.class)
public class AIHubAgentNodeValidator extends BaseNodeValidator<AIHubAgent> {

	@Override
	public NodeType getNodeType() {
		return NodeType.AI_HUB_AGENT;
	}

	@Override
	protected void doValidate(Definition definition, AIHubAgent aiHubAgent)
		throws KaleoDefinitionValidationException {

		String agentDefinitionExternalReferenceCode = null;

		for (Setting setting : aiHubAgent.getSettings()) {
			if (Objects.equals(
					setting.getName(),
					"agentDefinitionExternalReferenceCode")) {

				agentDefinitionExternalReferenceCode = setting.getValue();
			}
		}

		if (Validator.isNull(agentDefinitionExternalReferenceCode)) {
			throw new KaleoDefinitionValidationException(
				StringBundler.concat(
					"The ", aiHubAgent.getDefaultLabel(),
					" node must have an agent definition external reference ",
					"code"));
		}

		if (aiHubAgent.getIncomingTransitionsCount() == 0) {
			throw new KaleoDefinitionValidationException.
				MustSetIncomingTransition(aiHubAgent.getDefaultLabel());
		}

		if (aiHubAgent.getOutgoingTransitionsCount() == 0) {
			throw new KaleoDefinitionValidationException.
				MustSetOutgoingTransition(aiHubAgent.getDefaultLabel());
		}

		if (aiHubAgent.getOutgoingTransitionsCount() > 1) {
			throw new KaleoDefinitionValidationException.
				MustNotSetMultipleOutgoingTransitions(
					aiHubAgent.getDefaultLabel());
		}
	}

}