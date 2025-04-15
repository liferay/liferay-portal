/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.navigation.web.internal.portlet.display.template;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.tags.navigation.constants.AssetTagsNavigationPortletKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.template.TemplateHandler;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "jakarta.portlet.name=" + AssetTagsNavigationPortletKeys.ASSET_TAGS_CLOUD,
	service = TemplateHandler.class
)
public class AssetTagsCloudPortletDisplayTemplateHandler
	extends AssetTagsNavigationPortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return AssetTag.class.getName();
	}

	@Override
	public String getName(Locale locale) {
		return _language.format(
			locale, "x-template",
			portal.getPortletTitle(
				AssetTagsNavigationPortletKeys.ASSET_TAGS_CLOUD, locale),
			false);
	}

	@Override
	public String getResourceName() {
		return AssetTagsNavigationPortletKeys.ASSET_TAGS_CLOUD;
	}

	@Reference
	private Language _language;

}