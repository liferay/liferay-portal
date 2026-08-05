/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.system;

import java.util.Collection;

/**
 * @author Rodrigo Paulino
 */
public interface SystemObjectDefinitionManagerRegistry {

	public SystemObjectDefinitionManager getSystemObjectDefinitionManager(
		String name);

	public Collection<SystemObjectDefinitionManager>
		getSystemObjectDefinitionManagers();

}