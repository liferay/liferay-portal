/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alicia García
 */
@ExtendedObjectClassDefinition(
	category = "knowledge-base",
	scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.liferay.knowledge.base.internal.configuration.KBServiceConfiguration",
	localization = "content/Language",
	name = "knowledge-base-service-configuration-name"
)
public interface KBServiceConfiguration {

	@Meta.AD(
		deflt = "15", description = "check-interval-in-minutes-description",
		min = "1", name = "check-interval", required = false
	)
	public int checkInterval();

	@Meta.AD(
		deflt = "1",
		description = "expiration-date-notification-date-weeks-help",
		name = "expiration-date-notification-date-weeks", required = false
	)
	public int expirationDateNotificationDateWeeks();

	@Meta.AD(
		deflt = "100",
		description = "knowledge-base-article-check-limit-description",
		min = "1", name = "knowledge-base-article-check-limit", required = false
	)
	public int kbArticleCheckLimit();

}