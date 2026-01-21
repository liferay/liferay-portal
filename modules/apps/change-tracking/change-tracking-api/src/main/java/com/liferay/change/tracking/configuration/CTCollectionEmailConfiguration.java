/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;

/**
 * @author Brooke Dalton
 */
@ExtendedObjectClassDefinition(
	category = "publications",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY, strictScope = true
)
@Meta.OCD(
	id = "com.liferay.change.tracking.configuration.CTCollectionEmailConfiguration",
	localization = "content/Language",
	name = "publications-email-notification-configuration-name"
)
public interface CTCollectionEmailConfiguration {

	@Meta.AD(deflt = "", name = "email-from-address", required = false)
	public String emailFromAddress();

	@Meta.AD(deflt = "", name = "email-from-name", required = false)
	public String emailFromName();

	@Meta.AD(
		deflt = "${resource:com/liferay/change/tracking/dependencies/email_change_tracking_collection_invitation_body.tmpl}",
		description = "invitation-email-body-description",
		name = "invitation-email-body", required = false
	)
	public LocalizedValuesMap invitationEmailBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/change/tracking/dependencies/email_change_tracking_collection_invitation_subject.tmpl}",
		description = "invitation-email-subject-description",
		name = "invitation-email-subject", required = false
	)
	public LocalizedValuesMap invitationEmailSubject();

}