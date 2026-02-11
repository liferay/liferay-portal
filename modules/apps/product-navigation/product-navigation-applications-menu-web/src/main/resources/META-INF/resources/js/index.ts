/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.agent;

/**
 * @author João Victor Alves
 */
public interface AgentsFactory {

	public Object[] create(AgentContext agentContext);

}

export {default as ApplicationsMenu} from './ApplicationsMenu';
export {default as GlobalMenu} from './GlobalMenu';
