/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Sergio González
 */
@ExtendedObjectClassDefinition(category = "community-tools")
@Meta.OCD(
	id = "com.liferay.mentions.configuration.MentionsGroupServiceConfiguration",
	localization = "content/Language",
	name = "mentions-group-service-configuration-name"
)
@ProviderType
public interface MentionsGroupServiceConfiguration {

	@Meta.AD(
		deflt = "${resource:com/liferay/mentions/configuration/dependencies/asset_entry_mention_email_body.tmpl}",
		name = "asset-entry-mention-email-body", required = false
	)
	public LocalizedValuesMap assetEntryMentionEmailBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/mentions/configuration/dependencies/asset_entry_mention_email_subject.tmpl}",
		name = "asset-entry-mention-email-subject", required = false
	)
	public LocalizedValuesMap assetEntryMentionEmailSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/mentions/configuration/dependencies/comment_mention_email_body.tmpl}",
		name = "comment-mention-email-body", required = false
	)
	public LocalizedValuesMap commentMentionEmailBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/mentions/configuration/dependencies/comment_mention_email_subject.tmpl}",
		name = "comment-mention-email-subject", required = false
	)
	public LocalizedValuesMap commentMentionEmailSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/mentions/configuration/dependencies/comment_user_group_mention_email_body.tmpl}",
		name = "comment-user-group-mention-email-body", required = false
	)
	public LocalizedValuesMap commentUserGroupMentionEmailBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/mentions/configuration/dependencies/comment_user_group_mention_email_subject.tmpl}",
		name = "comment-user-group-mention-email-subject", required = false
	)
	public LocalizedValuesMap commentUserGroupMentionEmailSubject();

}