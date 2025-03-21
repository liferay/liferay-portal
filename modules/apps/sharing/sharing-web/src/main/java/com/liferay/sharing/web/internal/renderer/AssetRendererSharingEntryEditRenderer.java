/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.renderer;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.renderer.SharingEntryEditRenderer;
import com.liferay.sharing.web.internal.util.AssetRendererSharingUtil;

import jakarta.portlet.PortletURL;

/**
 * @author Alejandro Tardín
 */
public class AssetRendererSharingEntryEditRenderer
	implements SharingEntryEditRenderer {

	@Override
	public PortletURL getURLEdit(
			SharingEntry sharingEntry,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		try {
			AssetRenderer<?> assetRenderer =
				AssetRendererSharingUtil.getAssetRenderer(sharingEntry);

			if (assetRenderer == null) {
				return null;
			}

			return assetRenderer.getURLEdit(
				liferayPortletRequest, liferayPortletResponse);
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

}