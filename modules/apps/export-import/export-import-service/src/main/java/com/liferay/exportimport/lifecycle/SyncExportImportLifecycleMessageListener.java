/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.lifecycle;

import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageListener;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Daniel Kocsis
 */
@Component(
	property = "destination.name=" + DestinationNames.EXPORT_IMPORT_LIFECYCLE_EVENT_SYNC,
	service = MessageListener.class
)
public class SyncExportImportLifecycleMessageListener
	extends BaseExportImportLifecycleMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		activate(bundleContext, false);
	}

}