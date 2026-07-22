/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.akismet.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Jamie Sammons
 */
@ExtendedObjectClassDefinition(category = "community-tools")
@Meta.OCD(
	id = "com.liferay.akismet.internal.configuration.AkismetServiceConfiguration",
	localization = "content/Language", name = "akismet-configuration-name"
)
public interface AkismetServiceConfiguration {

	@Meta.AD(name = "api-key", required = false, type = Meta.Type.Password)
	public String akismetApiKey();

	@Meta.AD(deflt = "50", name = "check-threshold", required = false)
	public int akismetCheckThreshold();

	@Meta.AD(deflt = "30", name = "reportable-time", required = false)
	public int akismetReportableTime();

	@Meta.AD(deflt = "30", name = "retain-spam-time", required = false)
	public int akismetRetainSpamTime();

	@Meta.AD(name = "enable-for-message-boards", required = false)
	public boolean messageBoardsEnabled();

}