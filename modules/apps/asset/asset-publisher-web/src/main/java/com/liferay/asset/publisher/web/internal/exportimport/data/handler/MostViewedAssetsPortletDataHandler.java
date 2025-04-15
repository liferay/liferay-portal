/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.exportimport.data.handler;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;

import org.osgi.service.component.annotations.Component;

/**
 * Provides the implementation of <code>PortletDataHandler</code> (in
 * <code>com.liferay.portal.kernel</code>) for the Most Viewed Assets portlet.
 * This class defines specific behavior when exporting and importing Liferay
 * data to LAR files when layouts with the Most Viewed Assets portlet are
 * exported or imported.
 *
 * @author Eudaldo Alonso
 */
@Component(
	property = "jakarta.portlet.name=" + AssetPublisherPortletKeys.MOST_VIEWED_ASSETS,
	service = PortletDataHandler.class
)
public class MostViewedAssetsPortletDataHandler
	extends AssetPublisherPortletDataHandler {
}