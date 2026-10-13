/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.model.listener;

import com.liferay.launch.entry.type.LaunchEntryType;
import com.liferay.launch.entry.type.registry.LaunchEntryTypeRegistry;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.listener.RelevantObjectEntryModelListener;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = RelevantObjectEntryModelListener.class)
public class LaunchEntryRelevantObjectEntryModelListener
	extends BaseModelListener<ObjectEntry>
	implements RelevantObjectEntryModelListener {

	@Override
	public String getObjectDefinitionExternalReferenceCode() {
		return "L_LAUNCH_ENTRY";
	}

	@Override
	public void onAfterUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		Map<String, Serializable> originalValues =
			originalObjectEntry.getValues();
		Map<String, Serializable> values = objectEntry.getValues();

		if (GetterUtil.getLong(originalValues.get("draftId")) ==
				GetterUtil.getLong(values.get("draftId"))) {

			return;
		}

		_deleteDraft(originalValues);
	}

	@Override
	public void onBeforeRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		_deleteDraft(objectEntry.getValues());
	}

	private void _deleteDraft(Map<String, Serializable> values)
		throws ModelListenerException {

		LaunchEntryType launchEntryType =
			_launchEntryTypeRegistry.getLaunchEntryType(
				MapUtil.getString(values, "className"));

		long draftId = GetterUtil.getLong(values.get("draftId"));

		if ((launchEntryType == null) || (draftId == 0)) {
			return;
		}

		try {
			launchEntryType.deleteDraft(
				GetterUtil.getLong(values.get("classPK")), draftId);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Reference
	private LaunchEntryTypeRegistry _launchEntryTypeRegistry;

}