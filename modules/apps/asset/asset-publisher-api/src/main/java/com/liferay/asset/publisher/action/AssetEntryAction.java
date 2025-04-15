/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.action;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Jürgen Kappler
 */
@ProviderType
public interface AssetEntryAction<T> {

	public String getDialogTitle(Locale locale);

	public String getDialogURL(
			HttpServletRequest httpServletRequest,
			AssetRenderer<T> assetRenderer)
		throws PortalException;

	public String getIcon();

	public String getMessage(Locale locale);

	public boolean hasPermission(
			PermissionChecker permissionChecker, AssetRenderer<T> assetRenderer)
		throws PortalException;

}