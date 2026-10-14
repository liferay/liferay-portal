/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.set.publisher;

import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.launch.set.publisher.LaunchSetPublisher;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = LaunchSetPublisher.class)
public class LaunchSetPublisherImpl implements LaunchSetPublisher {

	@Override
	public void publish(long launchSetId) throws PortalException {
		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.getObjectEntry(launchSetId);

		if (launchSetObjectEntry.getStatus() ==
				WorkflowConstants.STATUS_APPROVED) {

			throw new PortalException(
				"Launch set " + launchSetId + " was already published");
		}

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				launchSetObjectEntry.getObjectDefinitionId(),
				"launchSetToLaunchEntries");

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		for (ObjectEntry launchEntryObjectEntry :
				_objectEntryLocalService.getOneToManyObjectEntries(
					launchSetObjectEntry.getGroupId(),
					objectRelationship.getObjectRelationshipId(), null, false,
					launchSetObjectEntry.getObjectEntryId(), true, null,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			Map<String, Serializable> values =
				launchEntryObjectEntry.getValues();

			String className = MapUtil.getString(values, "className");

			LaunchEntryManager launchEntryManager =
				_launchEntryManagerRegistry.getLaunchEntryManager(className);

			if (launchEntryManager == null) {
				throw new PortalException(
					"No launch entry manager exists for " + className);
			}

			LaunchEntryManager.Version version =
				launchEntryManager.publishDraft(
					MapUtil.getLong(values, "classPK"),
					MapUtil.getLong(values, "draftId"));

			values.put("draftId", 0L);
			values.put("versionKey", version.getVersionKey());

			_objectEntryLocalService.updateObjectEntry(
				serviceContext.getUserId(),
				launchEntryObjectEntry.getObjectEntryId(),
				launchEntryObjectEntry.getObjectEntryFolderId(), values,
				serviceContext);
		}

		_objectEntryLocalService.updateStatus(
			serviceContext.getUserId(), launchSetId,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	@Reference
	private LaunchEntryManagerRegistry _launchEntryManagerRegistry;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}