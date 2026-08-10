/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_2_0;

import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;

/**
 * @author Carolina Barbosa
 */
public class ObjectDefinitionExternalReferenceCodeUpgradeProcess
	extends com.liferay.object.internal.upgrade.v13_1_0.
				ObjectDefinitionExternalReferenceCodeUpgradeProcess {

	public ObjectDefinitionExternalReferenceCodeUpgradeProcess(
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		super(systemObjectDefinitionManagerRegistry);
	}

}