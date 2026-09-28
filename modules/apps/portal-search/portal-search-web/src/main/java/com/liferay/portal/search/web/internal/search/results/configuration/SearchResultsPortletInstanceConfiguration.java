/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.results.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Kevin Tan
 */
@ExtendedObjectClassDefinition(
	category = "search",
	scope = ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE
)
@Meta.OCD(
	id = "com.liferay.portal.search.web.internal.search.results.configuration.SearchResultsPortletInstanceConfiguration",
	localization = "content/Language",
	name = "search-results-portlet-instance-configuration-name"
)
public interface SearchResultsPortletInstanceConfiguration {

	@Meta.AD(name = "display-style", required = false)
	public String displayStyle();

	@Meta.AD(
		deflt = "", name = "display-style-group-external-reference-code",
		required = false
	)
	public String displayStyleGroupExternalReferenceCode();

	@Meta.AD(
		deflt = "0", description = "display-style-group-id-description",
		name = "display-style-group-id", required = false
	)
	public long displayStyleGroupId();

	@Meta.AD(
		deflt = "true", description = "limit-result-count-accuracy-help",
		name = "limit-result-count-accuracy", required = false
	)
	public boolean limitResultCountAccuracy();

}