/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.moderation.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Eduardo García
 */
@ExtendedObjectClassDefinition(
	category = "message-boards",
	scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.message.boards.moderation.configuration.MBModerationGroupConfiguration",
	localization = "content/Language",
	name = "message-boards-moderation-workflow-group-configuration-name"
)
public interface MBModerationGroupConfiguration {

	@Meta.AD(
		description = "authorized-domain-names-help",
		name = "authorized-domain-names", required = false
	)
	public String[] authorizedDomainNames();

	@Meta.AD(
		deflt = "false", description = "enable-message-boards-moderation-help",
		name = "enable-message-boards-moderation", required = false
	)
	public boolean enableMessageBoardsModeration();

	@Meta.AD(
		deflt = "1", description = "minimum-contributed-messages-help",
		min = "1", name = "minimum-contributed-messages", required = false
	)
	public int minimumContributedMessages();

}