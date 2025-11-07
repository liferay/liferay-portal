/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.model.listener;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.layout.friendly.url.LayoutFriendlyURLEntryHelper;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.staging.StagingGroupHelper;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = ModelListener.class)
public class LayoutFriendlyURLModelListener
	extends BaseModelListener<LayoutFriendlyURL> {

	@Override
	public void onAfterCreate(LayoutFriendlyURL layoutFriendlyURL)
		throws ModelListenerException {

		if (!ExportImportThreadLocal.isImportInProcess()) {
			_addFriendlyURLEntry(layoutFriendlyURL);
		}
	}

	@Override
	public void onAfterUpdate(
			LayoutFriendlyURL originalLayoutFriendlyURL,
			LayoutFriendlyURL layoutFriendlyURL)
		throws ModelListenerException {

		if (!ExportImportThreadLocal.isImportInProcess()) {
			_addFriendlyURLEntry(layoutFriendlyURL);
		}
	}

	private void _addFriendlyURLEntry(LayoutFriendlyURL layoutFriendlyURL) {
		try {
			if (!_stagingGroupHelper.isLiveGroup(
					layoutFriendlyURL.getGroupId())) {

				ServiceContext serviceContext =
					ServiceContextThreadLocal.getServiceContext();

				if (serviceContext == null) {
					serviceContext = new ServiceContext();
				}

				serviceContext.setUuid(null);

				_friendlyURLEntryLocalService.addFriendlyURLEntry(
					layoutFriendlyURL.getGroupId(),
					_layoutFriendlyURLEntryHelper.getClassNameId(
						layoutFriendlyURL.isPrivateLayout()),
					layoutFriendlyURL.getPlid(),
					Collections.singletonMap(
						layoutFriendlyURL.getLanguageId(),
						layoutFriendlyURL.getFriendlyURL()), serviceContext);
			}
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private LayoutFriendlyURLEntryHelper _layoutFriendlyURLEntryHelper;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}