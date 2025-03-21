/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.trash.model.TrashEntry;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Eudaldo Alonso
 */
@ProviderType
public interface TrashHelper {

	public int getMaxAge(Group group);

	public String getNewName(
			ThemeDisplay themeDisplay, String className, long classPK,
			String oldName)
		throws PortalException;

	public String getOriginalTitle(String title);

	public String getOriginalTitle(String title, String paramName);

	public TrashEntry getTrashEntry(TrashedModel trashedModel)
		throws PortalException;

	public String getTrashTitle(long entryId);

	public PortletURL getViewContentURL(
			HttpServletRequest httpServletRequest, String className,
			long classPK)
		throws PortalException;

	public boolean isInTrashContainer(TrashedModel trashedModel);

	public boolean isInTrashExplicitly(TrashedModel trashedModel);

	public boolean isInTrashImplicitly(TrashedModel trashedModel);

	public boolean isTrashEnabled(Group group);

	public boolean isTrashEnabled(long groupId) throws PortalException;

}