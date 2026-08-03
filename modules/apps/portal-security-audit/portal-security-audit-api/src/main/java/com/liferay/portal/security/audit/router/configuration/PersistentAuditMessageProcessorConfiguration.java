/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Shanon Mathai
 */
@ExtendedObjectClassDefinition(category = "audit")
@Meta.OCD(
	id = "com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration",
	localization = "content/Language",
	name = "persistent-audit-message-processor-configuration-name"
)
public interface PersistentAuditMessageProcessorConfiguration {

	@Meta.AD(deflt = "2000", name = "buffer-size", required = false)
	public int bufferSize();

	@Meta.AD(deflt = "true", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "60000", name = "flush-interval-in-milliseconds",
		required = false
	)
	public long flushInterval();

}