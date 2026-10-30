/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Tamas Molnar
 */
@ExtendedObjectClassDefinition(
	category = "infrastructure",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	description = "staging-configuration-description",
	id = "com.liferay.staging.configuration.StagingConfiguration",
	localization = "content/Language", name = "staging-configuration-name"
)
@ProviderType
public interface StagingConfiguration {

	@Meta.AD(
		deflt = "true", description = "staging-delete-temp-lar-on-failure-help",
		name = "staging-delete-temp-lar-on-failure", required = false
	)
	public boolean stagingDeleteTempLAROnFailure();

	@Meta.AD(
		deflt = "true", description = "staging-delete-temp-lar-on-success-help",
		name = "staging-delete-temp-lar-on-success", required = false
	)
	public boolean stagingDeleteTempLAROnSuccess();

	@Meta.AD(
		description = "staging-remote-hosts-allowed-help",
		name = "staging-remote-hosts-allowed", required = false
	)
	public String[] stagingRemoteHostsAllowed();

	@Meta.AD(
		deflt = "false",
		description = "staging-use-virtual-host-of-the-remote-site-help",
		name = "staging-use-virtual-host-of-the-remote-site", required = false
	)
	public boolean stagingUseVirtualHostForRemoteSite();

}