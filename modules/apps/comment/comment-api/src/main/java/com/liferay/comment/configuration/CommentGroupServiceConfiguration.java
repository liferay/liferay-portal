/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.configuration;

import aQute.bnd.annotation.ProviderType;
import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;

/**
 * @author Roberto Díaz
 * @author István András Dézsi
 */
@ExtendedObjectClassDefinition(
	category = "comments", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.comment.configuration.CommentGroupServiceConfiguration",
	localization = "content/Language", name = "discussion-configuration-name"
)
@ProviderType
public interface CommentGroupServiceConfiguration {

	@Meta.AD(
		deflt = "false", description = "always-editable-by-owner-description",
		name = "always-editable-by-owner", required = false
	)
	public boolean alwaysEditableByOwner();

	@Meta.AD(
		deflt = "${resource:com/liferay/comment/configuration/dependencies/discussion_email_added_body.tmpl}",
		name = "email-discussion-comment-added-body", required = false
	)
	public LocalizedValuesMap discussionEmailBody();

	@Meta.AD(
		deflt = "true", name = "email-discussion-comment-added-enabled",
		required = false
	)
	public boolean discussionEmailCommentsAddedEnabled();

	@Meta.AD(
		deflt = "${resource:com/liferay/comment/configuration/dependencies/discussion_email_added_subject.tmpl}",
		name = "email-discussion-comment-added-subject", required = false
	)
	public LocalizedValuesMap discussionEmailSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/comment/configuration/dependencies/discussion_email_updated_body.tmpl}",
		name = "email-discussion-comment-updated-body", required = false
	)
	public LocalizedValuesMap discussionEmailUpdatedBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/comment/configuration/dependencies/discussion_email_updated_subject.tmpl}",
		name = "email-discussion-comment-updated-subject", required = false
	)
	public LocalizedValuesMap discussionEmailUpdatedSubject();

	@Meta.AD(
		deflt = "${server-property://com.liferay.portal/admin.email.from.address}",
		name = "email-from-address", required = false
	)
	public String emailFromAddress();

	@Meta.AD(
		deflt = "${server-property://com.liferay.portal/admin.email.from.name}",
		name = "email-from-name", required = false
	)
	public String emailFromName();

	@Meta.AD(
		deflt = "true", description = "subscribe-help", name = "subscribe",
		required = false
	)
	public boolean subscribe();

}