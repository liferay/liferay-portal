/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.search.internal.service;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalServiceWrapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = ServiceWrapper.class)
public class SharingEntryObjectEntryLocalServiceWrapper
	extends ObjectEntryLocalServiceWrapper {

	@Override
	public ObjectEntry updateObjectEntry(
			long userId, long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		return _reindexSharingEntries(
			super.updateObjectEntry(
				userId, objectEntryId, objectEntryFolderId, values,
				serviceContext));
	}

	@Override
	public ObjectEntry updateObjectEntry(ObjectEntry objectEntry) {
		return _reindexSharingEntries(super.updateObjectEntry(objectEntry));
	}

	private ObjectEntry _reindexSharingEntries(ObjectEntry objectEntry) {
		ClassName className = _classNameLocalService.fetchClassName(
			objectEntry.getModelClassName());

		if (className == null) {
			return objectEntry;
		}

		Indexer<Object> indexer = _indexerRegistry.getIndexer(
			className.getValue());

		if (indexer == null) {
			return objectEntry;
		}

		List<SharingEntry> sharingEntryList =
			_sharingEntryLocalService.getSharingEntries(
				className.getClassNameId(), objectEntry.getObjectEntryId());

		sharingEntryList.forEach(
			sharingEntry -> {
				try {
					indexer.reindex(sharingEntry);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			});

		return objectEntry;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SharingEntryObjectEntryLocalServiceWrapper.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

}