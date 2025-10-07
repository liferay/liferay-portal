/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.messaging;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.model.uid.UIDFactory;

import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = "destination.name=" + CTDestinationNames.CT_ENTRY_REINDEX,
	service = MessageListener.class
)
public class CTEntryReindexMessageListener extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_SERIAL,
				CTDestinationNames.CT_ENTRY_REINDEX);

		RejectedExecutionHandler rejectedExecutionHandler =
			new ThreadPoolExecutor.CallerRunsPolicy() {

				@Override
				public void rejectedExecution(
					Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

					if (_log.isWarnEnabled()) {
						_log.warn(
							"The current thread will handle the request " +
								"because the CTEntry indexer task queue is " +
									"at its maximum capacity");
					}

					super.rejectedExecution(runnable, threadPoolExecutor);
				}

			};

		destinationConfiguration.setRejectedExecutionHandler(
			rejectedExecutionHandler);

		Destination destination = _destinationFactory.createDestination(
			destinationConfiguration);

		_serviceRegistration = bundleContext.registerService(
			Destination.class, destination,
			MapUtil.singletonDictionary(
				"destination.name", destination.getName()));
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		if (_indexer == null) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			if (Objects.equals(message.getString("type"), "reindex")) {
				CTEntry ctEntry = _ctEntryLocalService.fetchCTEntry(
					message.getLong("ctEntryId"));

				if (ctEntry == null) {
					return;
				}

				_indexer.reindex(ctEntry);
			}
			else {
				_indexWriterHelper.deleteDocument(
					message.getLong("companyId"),
					_uidFactory.getUID(
						_indexer.getClassName(), message.getLong("ctEntryId"),
						CTConstants.CT_COLLECTION_ID_PRODUCTION),
					_indexer.isCommitImmediately());
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTEntryReindexMessageListener.class);

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private DestinationFactory _destinationFactory;

	@Reference(
		target = "(indexer.class.name=com.liferay.change.tracking.model.CTEntry)"
	)
	private Indexer<CTEntry> _indexer;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	private ServiceRegistration<Destination> _serviceRegistration;

	@Reference
	private UIDFactory _uidFactory;

}