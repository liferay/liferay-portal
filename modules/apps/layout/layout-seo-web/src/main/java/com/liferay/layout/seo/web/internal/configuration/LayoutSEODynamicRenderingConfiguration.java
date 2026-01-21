/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Jamie Sammons
 */
@ExtendedObjectClassDefinition(category = "pages")
@Meta.OCD(
	description = "layout-seo-dynamic-rendering-configuration-description",
	id = "com.liferay.layout.seo.web.internal.configuration.LayoutSEODynamicRenderingConfiguration",
	localization = "content/Language",
	name = "layout-seo-dynamic-rendering-configuration-name"
)
public interface LayoutSEODynamicRenderingConfiguration {

	@Meta.AD(
		deflt = "false",
		name = "layout-seo-dynamic-rendering-configuration-enable-service",
		required = false
	)
	public boolean enabled();

	@Meta.AD(
		deflt = ".ai,.avi,.css,.dat,.dmg,.doc,.doc,.eot,.exe,.flv,.gif,.ico,.iso,.jpeg,.jpg,.js,.less,.m4a,.m4v,.mov,.mp3,.mp4,.mpeg,.mpg,.pdf,.png,.ppt,.psd,.rar,.rss,.svg,.swf,.tif,.torrent,.ttf,.txt,.wav,.wmv,.woff,.xls,.xml,.zip",
		name = "layout-seo-dynamic-rendering-configuration-ignored-extensions",
		required = false
	)
	public String[] ignoredExtensions();

	@Meta.AD(
		deflt = "",
		name = "layout-seo-dynamic-rendering-configuration-included-path",
		required = false
	)
	public String[] includedPaths();

	@Meta.AD(deflt = "", name = "service-url", required = false)
	public String serviceURL();

}