/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Michael C. Han
 */
@ExtendedObjectClassDefinition(category = "search")
@Meta.OCD(
	id = "com.liferay.portal.search.solr8.configuration.SolrConfiguration",
	localization = "content/Language", name = "solr8-configuration-name"
)
public interface SolrConfiguration {

	@Meta.AD(
		deflt = "BASIC", optionLabels = {"basic[authentication]", "cert"},
		optionValues = {"BASIC", "CERT"}, required = false
	)
	public String authenticationMode();

	@Meta.AD(
		deflt = "REPLICATED", optionLabels = {"cloud", "replicated"},
		optionValues = {"CLOUD", "REPLICATED"}, required = false
	)
	public String clientType();

	@Meta.AD(deflt = "liferay", required = false)
	public String defaultCollection();

	@Meta.AD(
		deflt = "true", description = "log-exceptions-only-help[solr]",
		required = false
	)
	public boolean logExceptionsOnly();

	@Meta.AD(deflt = "http://localhost:8983/solr/liferay", required = false)
	public String[] readURL();

	@Meta.AD(deflt = "http://localhost:8983/solr/liferay", required = false)
	public String[] writeURL();

	@Meta.AD(deflt = "localhost:9983", required = false)
	public String zkHost();

}