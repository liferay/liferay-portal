/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.trash.helper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.TrashedModel;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Eudaldo Alonso
 */
@ProviderType
public interface TrashHelper {

	public String getOriginalTitle(String title);

	public String getOriginalTitle(String title, String paramName);

	public String getTrashTitle(long entryId);

	public PortletURL getViewContentURL(
			HttpServletRequest httpServletRequest, String className,
			long classPK)
		throws PortalException;

	public boolean isInTrashContainer(TrashedModel trashedModel);

	public boolean isInTrashExplicitly(TrashedModel trashedModel);

	public boolean isInTrashImplicitly(TrashedModel trashedModel);

}