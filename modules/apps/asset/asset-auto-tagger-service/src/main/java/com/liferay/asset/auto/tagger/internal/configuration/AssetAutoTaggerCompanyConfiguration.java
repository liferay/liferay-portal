/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alejandro Tardín
 */
@ExtendedObjectClassDefinition(
	category = "assets", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.asset.auto.tagger.internal.configuration.AssetAutoTaggerCompanyConfiguration",
	localization = "content/Language",
	name = "asset-auto-tagger-company-configuration-name"
)
public interface AssetAutoTaggerCompanyConfiguration {

	/**
	 * Enables asset auto tagging.
	 */
	@Meta.AD(
		deflt = "true", name = "enabled[asset-auto-tagger-service]",
		required = false
	)
	public boolean enabled();

	/**
	 * Specifies the maximum number of tags that can be added for a given asset.
	 */
	@Meta.AD(
		description = "company-maximum-number-of-tags-per-asset-description",
		name = "maximum-number-of-tags", required = false
	)
	public int maximumNumberOfTagsPerAsset();

	@Meta.AD(
		deflt = "false", description = "update-auto-tags-description",
		name = "update-auto-tags", required = false
	)
	public boolean updateAutoTags();

}