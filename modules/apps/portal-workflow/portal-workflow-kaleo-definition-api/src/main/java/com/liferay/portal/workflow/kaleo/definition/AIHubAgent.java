/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition;

/**
 * @author Carolina Barbosa
 */
public class AIHubAgent extends Node {

	public AIHubAgent(String description, String name) {
		super(NodeType.AI_HUB_AGENT, name, description);
	}

}