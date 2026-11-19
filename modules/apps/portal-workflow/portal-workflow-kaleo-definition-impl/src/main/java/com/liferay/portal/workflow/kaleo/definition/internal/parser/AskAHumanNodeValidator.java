/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.parser;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.definition.AskAHuman;
import com.liferay.portal.workflow.kaleo.definition.Definition;
import com.liferay.portal.workflow.kaleo.definition.NodeType;
import com.liferay.portal.workflow.kaleo.definition.Setting;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;
import com.liferay.portal.workflow.kaleo.definition.parser.NodeValidator;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Guilherme Camacho
 */
@Component(service = NodeValidator.class)
public class AskAHumanNodeValidator extends BaseNodeValidator<AskAHuman> {

	@Override
	public NodeType getNodeType() {
		return NodeType.ASK_A_HUMAN;
	}

	@Override
	protected void doValidate(Definition definition, AskAHuman askAHuman)
		throws KaleoDefinitionValidationException {

		String question = null;

		for (Setting setting : askAHuman.getSettings()) {
			if (!Objects.equals(setting.getName(), "question")) {
				continue;
			}

			question = setting.getValue();
		}

		if (Validator.isNull(question) || Objects.equals(question, "{}")) {
			throw new KaleoDefinitionValidationException(
				StringBundler.concat(
					"The ", askAHuman.getDefaultLabel(),
					" node must have a question"));
		}

		if (askAHuman.getOutgoingTransitionsCount() == 0) {
			throw new KaleoDefinitionValidationException.
				MustSetOutgoingTransition(askAHuman.getDefaultLabel());
		}
	}

}