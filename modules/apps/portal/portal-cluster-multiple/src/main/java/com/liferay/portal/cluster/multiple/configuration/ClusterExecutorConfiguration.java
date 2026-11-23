/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Michael C. Han
 */
@ExtendedObjectClassDefinition(category = "infrastructure")
@Meta.OCD(
	id = "com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration",
	localization = "content/Language",
	name = "cluster-executor-configuration-name"
)
public interface ClusterExecutorConfiguration {

	@Meta.AD(deflt = "false", name = "debug-enabled", required = false)
	public boolean debugEnabled();

	@Meta.AD(
		deflt = "access_key|connection_password|connection_username|secret_access_key",
		description = "excluded-property-keys-help",
		name = "excluded-property-keys", required = false
	)
	public String[] excludedPropertyKeys();

}