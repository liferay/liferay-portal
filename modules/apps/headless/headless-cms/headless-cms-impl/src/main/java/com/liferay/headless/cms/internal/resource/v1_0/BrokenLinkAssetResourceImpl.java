/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.resource.v1_0;

import com.liferay.headless.cms.resource.v1_0.BrokenLinkAssetResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Crescenzo Rega
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/broken-link-asset.properties",
	scope = ServiceScope.PROTOTYPE, service = BrokenLinkAssetResource.class
)
public class BrokenLinkAssetResourceImpl
	extends BaseBrokenLinkAssetResourceImpl {
}