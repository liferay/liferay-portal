/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author David Arques
 */
@ExtendedObjectClassDefinition(category = "segments")
@Meta.OCD(
	id = "com.liferay.segments.configuration.SegmentsConfiguration",
	localization = "content/Language",
	name = "segments-service-configuration-name"
)
public interface SegmentsConfiguration {

	@Meta.AD(
		deflt = "false", description = "role-segmentation-enabled-description",
		name = "role-segmentation-enabled-name", required = false
	)
	public boolean roleSegmentationEnabled();

	@Meta.AD(
		deflt = "true", description = "segmentation-enabled-description",
		name = "segmentation-enabled-name", required = false
	)
	public boolean segmentationEnabled();

	@ExtendedAttributeDefinition(featureFlagKey = "LPD-78863")
	@Meta.AD(
		deflt = "120",
		description = "segments-preview-check-interval-description", min = "1",
		name = "segments-preview-check-interval-name", required = false
	)
	public int segmentsPreviewCheckInterval();

}