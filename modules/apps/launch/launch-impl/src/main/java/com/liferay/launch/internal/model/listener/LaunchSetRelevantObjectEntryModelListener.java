/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.model.listener;

import com.liferay.launch.internal.constants.LaunchDestinationNames;
import com.liferay.launch.internal.set.publisher.util.LaunchSetPublisherUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.listener.RelevantObjectEntryModelListener;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose Luis Navarro
 */
@Component(service = RelevantObjectEntryModelListener.class)
public class LaunchSetRelevantObjectEntryModelListener
	extends BaseModelListener<ObjectEntry>
	implements RelevantObjectEntryModelListener {

	@Override
	public String getObjectDefinitionExternalReferenceCode() {
		return "L_LAUNCH_SET";
	}

	@Override
	public void onBeforeRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_schedulerEngineHelper.delete(
				LaunchSetPublisherUtil.getJobName(objectEntry),
				LaunchDestinationNames.LAUNCH_SET_SCHEDULED_PUBLISH,
				StorageType.PERSISTED);
		}
		catch (SchedulerException schedulerException) {
			throw new ModelListenerException(schedulerException);
		}
	}

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

}