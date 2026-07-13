/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.parser;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.definition.Definition;
import com.liferay.portal.workflow.kaleo.definition.NodeType;
import com.liferay.portal.workflow.kaleo.definition.ServiceNode;
import com.liferay.portal.workflow.kaleo.definition.Setting;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;
import com.liferay.portal.workflow.kaleo.definition.parser.NodeValidator;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Iliyan Peychev
 */
@Component(service = NodeValidator.class)
public class ServiceNodeValidator extends BaseNodeValidator<ServiceNode> {

	@Override
	public NodeType getNodeType() {
		return NodeType.SERVICE;
	}

	@Override
	protected void doValidate(Definition definition, ServiceNode serviceNode)
		throws KaleoDefinitionValidationException {

		String javaDelegate = null;

		for (Setting setting : serviceNode.getSettings()) {
			if (Objects.equals(setting.getName(), "javaDelegate")) {
				javaDelegate = setting.getValue();
			}
		}

		if (Validator.isNull(javaDelegate) ||
			!javaDelegate.matches("[\\w.$]+(#\\w+)+")) {

			throw new KaleoDefinitionValidationException(
				StringBundler.concat(
					"The ", serviceNode.getDefaultLabel(),
					" node must set a \"java-delegate\" in the form ",
					"\"com.example.Converter#convert\""));
		}

		if (serviceNode.getOutgoingTransitionsCount() == 0) {
			throw new KaleoDefinitionValidationException.
				MustSetOutgoingTransition(serviceNode.getDefaultLabel());
		}

		if (serviceNode.getOutgoingTransitionsCount() > 1) {
			throw new KaleoDefinitionValidationException.
				MustNotSetMultipleOutgoingTransitions(
					serviceNode.getDefaultLabel());
		}
	}

}