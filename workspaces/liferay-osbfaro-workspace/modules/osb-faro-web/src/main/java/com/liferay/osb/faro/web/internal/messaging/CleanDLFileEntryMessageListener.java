/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.messaging;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.osb.faro.engine.client.model.DataSource;
import com.liferay.osb.faro.web.internal.constants.FaroMessageDestinationNames;
import com.liferay.osb.faro.web.internal.messaging.destination.creator.DestinationCreator;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(
	property = "destination.name=" + FaroMessageDestinationNames.FARO_CLEAN_DL_FILE_ENTRY_MESSAGE_PROCESSOR,
	service = MessageListener.class
)
public class CleanDLFileEntryMessageListener extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		try {
			_destinationCreator.createDestination(
				bundleContext, _destinationFactory,
				FaroMessageDestinationNames.
					FARO_CLEAN_DL_FILE_ENTRY_MESSAGE_PROCESSOR);

			Class<?> clazz = getClass();

			_trigger = _triggerFactory.createTrigger(
				clazz.getName(), clazz.getName(), new Date(), null,
				"0 0 0 * * ?");

			_schedulerEngineHelper.schedule(
				_trigger, StorageType.PERSISTED, null,
				FaroMessageDestinationNames.
					FARO_CLEAN_DL_FILE_ENTRY_MESSAGE_PROCESSOR,
				null);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Deactivate
	protected void deactivate() {
		try {
			if (_destinationCreator != null) {
				_destinationCreator.removeDestination();

				_destinationCreator = null;
			}

			if (_trigger == null) {
				return;
			}

			_schedulerEngineHelper.delete(
				_trigger.getJobName(), _trigger.getGroupName(),
				StorageType.PERSISTED);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		Date date = new Date(System.currentTimeMillis() - Time.DAY);

		DynamicQuery dynamicQuery = _dlFileVersionLocalService.dynamicQuery();

		Property statusProperty = PropertyFactoryUtil.forName("status");

		dynamicQuery.add(statusProperty.eq(WorkflowConstants.STATUS_DRAFT));

		Property versionProperty = PropertyFactoryUtil.forName("version");

		dynamicQuery.add(
			versionProperty.eq(DLFileEntryConstants.VERSION_DEFAULT));

		List<DLFileVersion> dlFileVersions =
			_dlFileVersionLocalService.dynamicQuery(dynamicQuery);

		for (DLFileVersion dlFileVersion : dlFileVersions) {
			if (DateUtil.compareTo(date, dlFileVersion.getCreateDate()) <= 0) {
				continue;
			}

			DLFileEntry dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(
				dlFileVersion.getFileEntryId());

			String className = dlFileEntry.getClassName();

			if (!className.equals(DataSource.class.getName())) {
				continue;
			}

			_dlFileEntryLocalService.deleteFileEntry(
				dlFileVersion.getFileEntryId());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CleanDLFileEntryMessageListener.class);

	private DestinationCreator _destinationCreator = new DestinationCreator();

	@Reference
	private DestinationFactory _destinationFactory;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	private Trigger _trigger;

	@Reference
	private TriggerFactory _triggerFactory;

}