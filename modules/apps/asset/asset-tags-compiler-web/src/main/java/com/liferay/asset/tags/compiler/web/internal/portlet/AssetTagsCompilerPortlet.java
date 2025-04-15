/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.compiler.web.internal.portlet;

import com.liferay.asset.tags.compiler.web.internal.constants.AssetTagsCompilerPortletKeys;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.ajaxable=false",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=1",
		"com.liferay.portlet.show-portlet-access-denied=false",
		"com.liferay.portlet.show-portlet-inactive=false",
		"com.liferay.portlet.system=true",
		"com.liferay.portlet.use-default-template=false",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.info.keywords=Tags Compiler",
		"jakarta.portlet.info.short-title=Tags Compiler",
		"jakarta.portlet.info.title=Tags Compiler",
		"jakarta.portlet.name=" + AssetTagsCompilerPortletKeys.ASSET_TAGS_COMPILER,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.supported-public-render-parameter=tags",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class AssetTagsCompilerPortlet extends MVCPortlet {

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.asset.tags.compiler.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

}