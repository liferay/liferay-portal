/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Michael C. Han
 */
@ExtendedObjectClassDefinition(
	category = "audit", generateUI = false,
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.portal.security.audit.configuration.AuditConfiguration",
	localization = "content/Language", name = "audit-configuration-name"
)
@ProviderType
public interface AuditConfiguration {

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Meta.AD(
		deflt = "200", name = "audit-message-max-queue-size", required = false
	)
	public int auditMessageMaxQueueSize();

	@Meta.AD(deflt = "true", name = "enabled", required = false)
	public boolean enabled();

}