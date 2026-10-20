/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Beni Herrero
 */
@ExtendedObjectClassDefinition(
	category = "mcp-server", featureFlagKey = "LPD-63311",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	description = "mcp-server-configuration-description",
	id = "com.liferay.mcp.server.rest.internal.configuration.MCPServerConfiguration",
	localization = "content/Language", name = "mcp-server-configuration-name"
)
public interface MCPServerConfiguration {

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "10", description = "search-tool-max-results-count-description",
		name = "search-tool-max-results-count", required = false
	)
	public int searchToolMaxResultsCount();

}