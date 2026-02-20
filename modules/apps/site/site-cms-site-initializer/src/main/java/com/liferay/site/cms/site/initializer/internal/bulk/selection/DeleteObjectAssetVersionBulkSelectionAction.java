/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.bulk.selection;

import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManagerProvider;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.site.cms.site.initializer.bulk.selection.BaseObjectBulkSelectionAction;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michele Vigilante
 */
@Component(
	property = "bulk.selection.action.key=delete.object.asset.version",
	service = BulkSelectionAction.class
)
public class DeleteObjectAssetVersionBulkSelectionAction
	extends BaseObjectBulkSelectionAction {

	@Override
	protected void doExecute(
			User user, Map<String, Serializable> inputMap, Object object)
		throws Exception {

		if (!(object instanceof ObjectEntry)) {
			return;
		}

		ObjectEntry objectObjectEntry = (ObjectEntry)object;

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.getObjectDefinition(
				objectObjectEntry.getObjectDefinitionId());

		DefaultObjectEntryManager defaultObjectEntryManager =
			DefaultObjectEntryManagerProvider.provide(
				_objectEntryManagerRegistry.getObjectEntryManager(
					objectDefinition.getCompanyId(),
					objectDefinition.getStorageType()));

		Set<Integer> toRemoveVersions = _toIntegerSet(
			inputMap, "toRemoveVersions");

		for (Integer version : toRemoveVersions) {
			defaultObjectEntryManager.deleteObjectEntryByVersion(
				objectObjectEntry.getExternalReferenceCode(), objectDefinition,
				_getScopeKey(objectObjectEntry.getGroupId(), objectDefinition),
				version);
		}
	}

	private String _getScopeKey(
		long groupId, ObjectDefinition objectDefinition) {

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		if (!objectScopeProvider.isGroupAware()) {
			return null;
		}

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		return group.getGroupKey();
	}

	private Set<Integer> _toIntegerSet(
		Map<String, Serializable> map, String key) {

		try {
			Serializable values = map.get(key);

			if (values instanceof Integer[]) {
				return SetUtil.fromArray((Integer[])values);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return SetUtil.fromArray(new Integer[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteObjectAssetVersionBulkSelectionAction.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

}