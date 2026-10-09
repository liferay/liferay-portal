/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.model.listener;

import com.liferay.change.tracking.internal.score.CTScoreSizeClassificationCache;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = ModelListener.class)
public class CTCollectionModelListener extends BaseModelListener<CTCollection> {

	@Override
	public void onAfterRemove(CTCollection ctCollection) {
		_ctScoreSizeClassificationCache.remove(
			ctCollection.getCtCollectionId());

		_ctPreferencesLocalService.resetCTPreferences(
			ctCollection.getCtCollectionId());

		List<CTProcess> ctProcesses = _ctProcessLocalService.getCTProcesses(
			ctCollection.getCtCollectionId());

		for (CTProcess ctProcess : ctProcesses) {
			try {
				_ctProcessLocalService.deleteCTProcess(ctProcess);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}
			}
		}

		_deleteTickets(ctCollection);
	}

	@Override
	public void onAfterUpdate(
			CTCollection originalCTCollection, CTCollection ctCollection)
		throws ModelListenerException {

		if (!Objects.equals(
				originalCTCollection.getName(), ctCollection.getName())) {

			try {
				_indexer.reindex(
					_ctEntryLocalService.getCTCollectionCTEntries(
						ctCollection.getCtCollectionId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS, null));
			}
			catch (SearchException searchException) {
				throw new ModelListenerException(searchException);
			}
		}

		if ((originalCTCollection.getStatus() != ctCollection.getStatus()) &&
			(ctCollection.getStatus() == WorkflowConstants.STATUS_EXPIRED)) {

			_ctPreferencesLocalService.resetCTPreferences(
				ctCollection.getCtCollectionId());
		}

		if (ctCollection.isShareable() ||
			(ctCollection.isShareable() ==
				originalCTCollection.isShareable())) {

			return;
		}

		_deleteTickets(ctCollection);
	}

	private void _deleteTickets(CTCollection ctCollection) {
		for (Ticket ticket :
				_ticketLocalService.getTickets(
					ctCollection.getCompanyId(), CTCollection.class.getName(),
					ctCollection.getCtCollectionId(),
					TicketConstants.TYPE_ON_DEMAND_USER_LOGIN)) {

			_ticketLocalService.deleteTicket(ticket);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTCollectionModelListener.class.getName());

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private CTProcessLocalService _ctProcessLocalService;

	@Reference
	private CTScoreSizeClassificationCache _ctScoreSizeClassificationCache;

	@Reference(
		target = "(indexer.class.name=com.liferay.change.tracking.model.CTEntry)"
	)
	private Indexer<CTEntry> _indexer;

	@Reference
	private TicketLocalService _ticketLocalService;

}