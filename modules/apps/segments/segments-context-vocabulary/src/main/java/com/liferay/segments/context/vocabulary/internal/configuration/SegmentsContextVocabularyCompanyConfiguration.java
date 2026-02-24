/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.context.vocabulary.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Yurena Cabrera
 */
@ExtendedObjectClassDefinition(
	category = "segments", deprecated = true,
	factoryInstanceLabelAttribute = "entityFieldName",
	featureFlagKey = "LPD-78863",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	description = "segments-context-vocabulary-company-configuration-description",
	factory = true,
	id = "com.liferay.segments.context.vocabulary.internal.configuration.SegmentsContextVocabularyCompanyConfiguration",
	localization = "content/Language",
	name = "segments-context-vocabulary-company-configuration-name"
)
public interface SegmentsContextVocabularyCompanyConfiguration {

	@Meta.AD(
		name = "segments-context-vocabulary-company-configuration-asset-vocabulary-name"
	)
	public String assetVocabularyName();

	@Meta.AD(
		name = "segments-context-vocabulary-company-configuration-entity-field-name"
	)
	public String entityFieldName();

}