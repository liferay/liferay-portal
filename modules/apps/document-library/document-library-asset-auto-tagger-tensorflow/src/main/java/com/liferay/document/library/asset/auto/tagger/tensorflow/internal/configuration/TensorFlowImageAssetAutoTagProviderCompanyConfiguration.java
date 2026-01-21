/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alejandro Tardín
 */
@ExtendedObjectClassDefinition(
	category = "assets", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	description = "tensorflow-auto-tag-provider-configuration-description",
	id = "com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderCompanyConfiguration",
	localization = "content/Language",
	name = "tensorflow-auto-tag-provider-configuration-name"
)
public interface TensorFlowImageAssetAutoTagProviderCompanyConfiguration {

	/**
	 * Sets the confidence threshold for the returned tags.
	 */
	@Meta.AD(
		deflt = "0.1", description = "confidence-threshold-description",
		name = "confidence-threshold", required = false
	)
	public float confidenceThreshold();

	/**
	 * Enables auto tagging of images using a pre-trained tensorflow model.
	 */
	@Meta.AD(
		deflt = "false", description = "enabled-description[tensorflow]",
		name = "enabled[tensorflow]", required = false
	)
	public boolean enabled();

}