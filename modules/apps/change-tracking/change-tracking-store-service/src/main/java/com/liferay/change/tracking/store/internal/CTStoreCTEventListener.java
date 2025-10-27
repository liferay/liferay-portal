/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.store.internal;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.exception.CTEventException;
import com.liferay.change.tracking.spi.listener.CTEventListener;
import com.liferay.change.tracking.store.model.CTSContent;
import com.liferay.change.tracking.store.service.CTSContentLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.sql.CTSQLModeThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = CTEventListener.class)
public class CTStoreCTEventListener implements CTEventListener {

	@Override
	public void onAfterPublish(long ctCollectionId) throws CTEventException {
		List<CTEntry> ctEntries = _ctEntryLocalService.getCTEntries(
			ctCollectionId, _portal.getClassNameId(CTSContent.class.getName()));

		if (ctEntries.isEmpty()) {
			return;
		}

		List<CTEntry> deletedCTEnties = new ArrayList<>();

		List<CTEntry> addOrModifiedCTEntries = new ArrayList<>();

		for (CTEntry ctEntry : ctEntries) {
			if (ctEntry.getChangeType() ==
					CTConstants.CT_CHANGE_TYPE_DELETION) {

				deletedCTEnties.add(ctEntry);
			}
			else {
				addOrModifiedCTEntries.add(ctEntry);
			}
		}

		// Deleted CTEntries need to read CTSContent from CTCollection

		if (!deletedCTEnties.isEmpty()) {
			try (SafeCloseable safeCloseable1 =
					CTSQLModeThreadLocal.setCTSQLModeWithSafeCloseable(
						CTSQLModeThreadLocal.CTSQLMode.CT_ONLY);
				SafeCloseable safeCloseable2 =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollectionId)) {

				for (CTEntry ctEntry : deletedCTEnties) {
					CTSContent ctsContent =
						_ctsContentLocalService.fetchCTSContent(
							ctEntry.getModelClassPK());

					if (ctsContent != null) {
						Store store = _serviceTrackerMap.getService(
							ctsContent.getStoreType());

						store.deleteFile(
							ctsContent.getCompanyId(),
							ctsContent.getRepositoryId(), ctsContent.getPath(),
							ctsContent.getVersion());
					}
					else if (_log.isWarnEnabled()) {
						_log.warn(
							"No change tracking store content found for " +
								"model class PK " + ctEntry.getModelClassPK());
					}
				}
			}
		}

		// Add or modifed CTEntries need to read CTSContent from production

		if (!addOrModifiedCTEntries.isEmpty()) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				for (CTEntry ctEntry : addOrModifiedCTEntries) {
					CTSContent ctsContent =
						_ctsContentLocalService.getCTSContent(
							ctEntry.getModelClassPK());

					Store store = _serviceTrackerMap.getService(
						ctsContent.getStoreType());

					store.addFile(
						ctsContent.getCompanyId(), ctsContent.getRepositoryId(),
						ctsContent.getPath(), ctsContent.getVersion(),
						ctsContent.getData(
						).getBinaryStream());
				}
			}
			catch (Exception portalException) {
				throw new CTEventException(portalException);
			}
		}
	}

	@Override
	public void onBeforeRemove(long ctCollectionId) throws CTEventException {
		List<Long> ctsContentIds =
			_ctEntryLocalService.getExclusiveModelClassPKs(
				ctCollectionId,
				_portal.getClassNameId(CTSContent.class.getName()));

		if (ctsContentIds.isEmpty()) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			for (long ctsContentId : ctsContentIds) {
				CTSContent ctsContent = _ctsContentLocalService.fetchCTSContent(
					ctsContentId);

				if (ctsContent != null) {
					_ctsContentLocalService.deleteCTSContent(ctsContent);
				}
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, Store.class, "store.type");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTStoreCTEventListener.class);

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTSContentLocalService _ctsContentLocalService;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, Store> _serviceTrackerMap;

}