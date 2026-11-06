/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.model.listener;

import com.liferay.batch.engine.thread.local.BatchEngineThreadLocal;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.friendly.url.constants.FriendlyURLEntryConstants;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.layout.friendly.url.LayoutFriendlyURLEntryHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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

		_addFriendlyURLEntry(layoutFriendlyURL);
	}

	@Override
	public void onAfterUpdate(
			LayoutFriendlyURL originalLayoutFriendlyURL,
			LayoutFriendlyURL layoutFriendlyURL)
		throws ModelListenerException {

		_addFriendlyURLEntry(layoutFriendlyURL);
	}

	private void _addFriendlyURLEntry(LayoutFriendlyURL layoutFriendlyURL) {
		if (!BatchEngineThreadLocal.isBatchImportInProcess() &&
			(ExportImportThreadLocal.isImportInProcess() ||
			 _stagingGroupHelper.isLiveGroup(layoutFriendlyURL.getGroupId()))) {

			return;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();
		String uuid = null;

		if (serviceContext == null) {
			serviceContext = new ServiceContext();
		}
		else {
			uuid = serviceContext.getUuid();

			serviceContext.setUuid(null);
		}

		try {
			long classNameId = _layoutFriendlyURLEntryHelper.getClassNameId(
				layoutFriendlyURL.isPrivateLayout());

			_deleteHistoricalFriendlyURLEntryLocalization(
				classNameId, layoutFriendlyURL);

			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				layoutFriendlyURL.getGroupId(), classNameId,
				layoutFriendlyURL.getPlid(),
				Collections.singletonMap(
					layoutFriendlyURL.getLanguageId(),
					layoutFriendlyURL.getFriendlyURL()),
				serviceContext);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
		finally {
			if (serviceContext != null) {
				serviceContext.setUuid(uuid);
			}
		}
	}

	private void _deleteHistoricalFriendlyURLEntryLocalization(
			long classNameId, LayoutFriendlyURL layoutFriendlyURL)
		throws PortalException {

		if (!ExportImportThreadLocal.isStagingInProcess()) {
			return;
		}

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				layoutFriendlyURL.getGroupId(), classNameId,
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				layoutFriendlyURL.getLanguageId(),
				layoutFriendlyURL.getFriendlyURL());

		if ((friendlyURLEntryLocalization == null) ||
			(friendlyURLEntryLocalization.getClassPK() ==
				layoutFriendlyURL.getPlid())) {

			return;
		}

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				friendlyURLEntryLocalization.getFriendlyURLEntryId());

		if (friendlyURLEntry != null) {
			FriendlyURLEntry mainFriendlyURLEntry =
				_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
					friendlyURLEntry.getClassNameId(),
					friendlyURLEntry.getClassPK());

			if ((mainFriendlyURLEntry != null) &&
				(mainFriendlyURLEntry.getFriendlyURLEntryId() ==
					friendlyURLEntry.getFriendlyURLEntryId())) {

				return;
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Releasing friendly URL ",
					layoutFriendlyURL.getFriendlyURL(),
					" from the URL history of layout ",
					friendlyURLEntryLocalization.getClassPK(), " for layout ",
					layoutFriendlyURL.getPlid()));
		}

		_friendlyURLEntryLocalService.deleteFriendlyURLLocalizationEntry(
			friendlyURLEntryLocalization);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutFriendlyURLModelListener.class);

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private LayoutFriendlyURLEntryHelper _layoutFriendlyURLEntryHelper;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}