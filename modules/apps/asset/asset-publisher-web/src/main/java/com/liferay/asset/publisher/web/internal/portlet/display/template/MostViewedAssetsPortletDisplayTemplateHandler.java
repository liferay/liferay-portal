/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.portlet.display.template;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.portal.kernel.template.TemplateHandler;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "jakarta.portlet.name=" + AssetPublisherPortletKeys.MOST_VIEWED_ASSETS,
	service = TemplateHandler.class
)
public class MostViewedAssetsPortletDisplayTemplateHandler
	extends AssetPublisherPortletDisplayTemplateHandler {
}