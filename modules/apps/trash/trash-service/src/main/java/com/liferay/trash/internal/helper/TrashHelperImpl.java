/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.internal.helper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.trash.helper.TrashHelper;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = TrashHelper.class)
public class TrashHelperImpl implements TrashHelper {

	@Override
	public String getOriginalTitle(String title) {
		return _trashHelper.getOriginalTitle(title);
	}

	@Override
	public String getOriginalTitle(String title, String paramName) {
		return _trashHelper.getOriginalTitle(title, paramName);
	}

	@Override
	public String getTrashTitle(long entryId) {
		return _trashHelper.getTrashTitle(entryId);
	}

	@Override
	public PortletURL getViewContentURL(
			HttpServletRequest httpServletRequest, String className,
			long classPK)
		throws PortalException {

		return _trashHelper.getViewContentURL(
			httpServletRequest, className, classPK);
	}

	@Override
	public boolean isInTrashContainer(TrashedModel trashedModel) {
		return _trashHelper.isInTrashContainer(trashedModel);
	}

	@Override
	public boolean isInTrashExplicitly(TrashedModel trashedModel) {
		return _trashHelper.isInTrashExplicitly(trashedModel);
	}

	@Override
	public boolean isInTrashImplicitly(TrashedModel trashedModel) {
		return _trashHelper.isInTrashImplicitly(trashedModel);
	}

	@Reference
	private com.liferay.trash.TrashHelper _trashHelper;

}