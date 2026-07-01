/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.workflow;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Feliphe Marinho
 */
public interface WorkflowNode {

	public String getLabel(Locale locale);

	public Map<Locale, String> getLabelMap();

	public String getName();

	public Type getType();

	public List<WorkflowNodeSetting> getWorkflowNodeSettings();

	public enum Type {

		AI_DECISION, AI_HUB_AGENT, CONDITION, FORK, HTTP_REQUEST, INITIAL_STATE,
		JOIN, JOIN_XOR, LLM, SERVICE, STATE, TASK, TERMINAL_STATE

	}

}