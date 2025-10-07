/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.model.listener;

import com.liferay.change.tracking.closure.CTClosureFactory;
import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = ModelListener.class)
public class CTEntryModelListener extends BaseModelListener<CTEntry> {

	@Override
	public void onAfterCreate(CTEntry ctEntry) {
		_ctClosureFactory.clearCache(ctEntry.getCtCollectionId());

		_reindexCTEntry(ctEntry, "reindex");

		_updateCTScore(ctEntry, true);
	}

	@Override
	public void onAfterRemove(CTEntry ctEntry) {
		_ctClosureFactory.clearCache(ctEntry.getCtCollectionId());

		_reindexCTEntry(ctEntry, "delete");

		_updateCTScore(ctEntry, false);
	}

	@Override
	public void onAfterUpdate(CTEntry originalCTEntry, CTEntry ctEntry)
		throws ModelListenerException {

		if (originalCTEntry.getCtCollectionId() !=
				ctEntry.getCtCollectionId()) {

			_ctClosureFactory.clearCache(originalCTEntry.getCtCollectionId());

			_updateCTScore(originalCTEntry, false);

			_updateCTScore(ctEntry, true);
		}

		if ((originalCTEntry.getCtCollectionId() !=
				ctEntry.getCtCollectionId()) ||
			(originalCTEntry.getUserId() != ctEntry.getUserId())) {

			_reindexCTEntry(ctEntry, "reindex");
		}

		_ctClosureFactory.clearCache(ctEntry.getCtCollectionId());
	}

	private void _reindexCTEntry(CTEntry ctEntry, String type) {
		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				Message message = new Message();

				message.setValues(
					HashMapBuilder.<String, Object>put(
						"companyId", ctEntry.getCompanyId()
					).put(
						"ctEntryId", ctEntry.getCtEntryId()
					).put(
						"type", type
					).build());

				_messageBus.sendMessage(
					CTDestinationNames.CT_ENTRY_REINDEX, message);

				return null;
			});
	}

	private void _updateCTScore(CTEntry ctEntry, boolean increment) {
		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				Message message = new Message();

				message.setValues(
					HashMapBuilder.<String, Object>put(
						"ctCollectionId", ctEntry.getCtCollectionId()
					).put(
						"increment", increment
					).put(
						"modelClassNameId", ctEntry.getModelClassNameId()
					).build());

				_messageBus.sendMessage(CTDestinationNames.CT_SCORE, message);

				return null;
			});
	}

	@Reference
	private CTClosureFactory _ctClosureFactory;

	@Reference
	private MessageBus _messageBus;

}