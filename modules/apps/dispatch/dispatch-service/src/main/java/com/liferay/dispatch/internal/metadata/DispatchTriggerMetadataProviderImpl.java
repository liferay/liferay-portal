/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.internal.metadata;

import com.liferay.dispatch.metadata.DispatchTriggerMetadata;
import com.liferay.dispatch.metadata.DispatchTriggerMetadataFactory;
import com.liferay.dispatch.metadata.DispatchTriggerMetadataProvider;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import java.util.Collections;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Beslic
 */
@Component(service = DispatchTriggerMetadataProvider.class)
public class DispatchTriggerMetadataProviderImpl
	implements DispatchTriggerMetadataProvider {

	@Override
	public DispatchTriggerMetadata getDispatchTriggerMetadata(
		long dispatchTriggerId) {

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.fetchDispatchTrigger(
				dispatchTriggerId);

		if ((dispatchTrigger == null) ||
			!_serviceTrackerMap.containsKey(
				dispatchTrigger.getDispatchTaskExecutorType())) {

			return _defaultDispatchTriggerMetadata;
		}

		DispatchTriggerMetadataFactory dispatchTriggerMetadataFactory =
			_serviceTrackerMap.getService(
				dispatchTrigger.getDispatchTaskExecutorType());

		return dispatchTriggerMetadataFactory.instance(dispatchTrigger);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DispatchTriggerMetadataFactory.class,
			_KEY_DISPATCH_TASK_EXECUTOR_TYPE);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final String _KEY_DISPATCH_TASK_EXECUTOR_TYPE =
		"dispatch.task.executor.type";

	private static final DispatchTriggerMetadata
		_defaultDispatchTriggerMetadata = new DispatchTriggerMetadata() {

			@Override
			public Map<String, String> getAttributes() {
				return Collections.emptyMap();
			}

			@Override
			public Map<String, String> getErrors() {
				return Collections.emptyMap();
			}

			@Override
			public boolean isDispatchTaskExecutorReady() {
				return true;
			}

		};

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	private ServiceTrackerMap<String, DispatchTriggerMetadataFactory>
		_serviceTrackerMap;

}