/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.messaging;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.store.StoreAreaAwareStoreWrapper;
import com.liferay.document.library.kernel.store.StoreAreaProcessor;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "destination.name=" + DestinationNames.DOCUMENT_LIBRARY_DELETION,
	service = MessageListener.class
)
public class DLDeletionMessageListener extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			StringBundler.concat(
				"(&(objectClass=", StoreAreaProcessor.class.getName(),
				")(store.type=", PropsValues.DL_STORE_IMPL, "))"));

		_wrappedStore = new StoreAreaAwareStoreWrapper(
			() -> _store, _serviceTracker::getService);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();

		_wrappedStore = null;
	}

	@Override
	protected void doReceive(Message message) {
		Map<String, Object> values = message.getValues();

		_wrappedStore.deleteDirectory(
			MapUtil.getLong(values, "companyId"),
			MapUtil.getLong(values, "repositoryId"),
			MapUtil.getString(values, "dirName"));
	}

	private ServiceTracker<StoreAreaProcessor, StoreAreaProcessor>
		_serviceTracker;

	@Reference(target = "(default=true)")
	private Store _store;

	private Store _wrappedStore;

}