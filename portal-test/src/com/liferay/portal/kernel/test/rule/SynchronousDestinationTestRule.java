/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.rule;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.increment.BufferedIncrementThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseDestination;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.messaging.MessageListenerRegistry;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule.SyncHandler;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Miguel Pastor
 * @author Shuyang Zhou
 */
public class SynchronousDestinationTestRule
	extends AbstractTestRule<SyncHandler, SyncHandler> {

	public static final SynchronousDestinationTestRule INSTANCE =
		new SynchronousDestinationTestRule();

	@Override
	public void afterClass(Description description, SyncHandler syncHandler)
		throws Exception {

		if (syncHandler != null) {
			syncHandler.restorePreviousSync();
		}
	}

	@Override
	public void afterMethod(
		Description description, SyncHandler syncHandler, Object target) {

		if (syncHandler != null) {
			syncHandler.restorePreviousSync();
		}
	}

	@Override
	public SyncHandler beforeClass(Description description) throws Throwable {
		DependencyManagerSyncUtil.sync();

		Class<?> testClass = description.getTestClass();

		return _createSyncHandler(testClass.getAnnotation(Sync.class));
	}

	@Override
	public SyncHandler beforeMethod(Description description, Object target) {
		Class<?> testClass = description.getTestClass();

		Sync sync = testClass.getAnnotation(Sync.class);

		if (sync != null) {
			return null;
		}

		sync = description.getAnnotation(Sync.class);

		if (sync == null) {
			return null;
		}

		return _createSyncHandler(sync);
	}

	public static class SyncHandler {

		public BaseDestination createSynchronousDestination(
			String destinationName) {

			TestSynchronousDestination testSynchronousDestination = null;

			if ((_sync != null) && _sync.cleanTransaction()) {
				testSynchronousDestination =
					new CleanTransactionSynchronousDestination();
			}
			else {
				testSynchronousDestination = new TestSynchronousDestination();
			}

			testSynchronousDestination.setMessageListenerRegistry(
				_serviceTracker.getService());
			testSynchronousDestination.setName(destinationName);

			return testSynchronousDestination;
		}

		public void enableSync() {
			_serviceTracker.open();

			Filter audioProcessorFilter = _registerDestinationFilter(
				DestinationNames.DOCUMENT_LIBRARY_AUDIO_PROCESSOR);
			Filter asyncFilter = _registerDestinationFilter(
				DestinationNames.ASYNC_SERVICE);
			Filter backgroundTaskFilter = _registerDestinationFilter(
				DestinationNames.BACKGROUND_TASK);
			Filter backgroundTaskStatusFilter = _registerDestinationFilter(
				DestinationNames.BACKGROUND_TASK_STATUS);
			Filter commerceBasePriceListFilter = _registerDestinationFilter(
				DestinationNames.COMMERCE_BASE_PRICE_LIST);
			Filter commerceOrderFilter = _registerDestinationFilter(
				DestinationNames.COMMERCE_ORDER_STATUS);
			Filter commercePaymentFilter = _registerDestinationFilter(
				DestinationNames.COMMERCE_PAYMENT_STATUS);
			Filter commerceShipmentFilter = _registerDestinationFilter(
				DestinationNames.COMMERCE_SHIPMENT_STATUS);
			Filter commerceSubscriptionFilter = _registerDestinationFilter(
				DestinationNames.COMMERCE_SUBSCRIPTION_STATUS);
			Filter ddmStructureReindexFilter = _registerDestinationFilter(
				"liferay/ddm_structure_reindex");
			Filter deletionProcessorFilter = _registerDestinationFilter(
				DestinationNames.DOCUMENT_LIBRARY_DELETION);
			Filter mailFilter = _registerDestinationFilter(
				DestinationNames.MAIL);
			Filter pdfProcessorFilter = _registerDestinationFilter(
				DestinationNames.DOCUMENT_LIBRARY_PDF_PROCESSOR);
			Filter rawMetaDataProcessorFilter = _registerDestinationFilter(
				DestinationNames.DOCUMENT_LIBRARY_RAW_METADATA_PROCESSOR);
			Filter segmentsEntryReindexFilter = _registerDestinationFilter(
				"liferay/segments_entry_reindex");
			Filter subscrpitionSenderFilter = _registerDestinationFilter(
				DestinationNames.SUBSCRIPTION_SENDER);
			Filter tensorflowModelDownloadFilter = _registerDestinationFilter(
				"liferay/tensorflow_model_download");
			Filter videoProcessorFilter = _registerDestinationFilter(
				DestinationNames.DOCUMENT_LIBRARY_VIDEO_PROCESSOR);

			_waitForDependencies(
				audioProcessorFilter, asyncFilter, backgroundTaskFilter,
				backgroundTaskStatusFilter, commerceBasePriceListFilter,
				commerceOrderFilter, commercePaymentFilter,
				commerceShipmentFilter, commerceSubscriptionFilter,
				ddmStructureReindexFilter, deletionProcessorFilter, mailFilter,
				pdfProcessorFilter, rawMetaDataProcessorFilter,
				segmentsEntryReindexFilter, subscrpitionSenderFilter,
				tensorflowModelDownloadFilter, videoProcessorFilter);

			_bufferedIncrementForceSyncSafeCloseable =
				BufferedIncrementThreadLocal.setForceSyncWithSafeCloseable(
					true);

			replaceDestination(DestinationNames.ASYNC_SERVICE);
			replaceDestination(DestinationNames.BACKGROUND_TASK);
			replaceDestination(DestinationNames.BACKGROUND_TASK_STATUS);
			replaceDestination(DestinationNames.COMMERCE_BASE_PRICE_LIST);
			replaceDestination(DestinationNames.COMMERCE_ORDER_STATUS);
			replaceDestination(DestinationNames.COMMERCE_PAYMENT_STATUS);
			replaceDestination(DestinationNames.COMMERCE_SHIPMENT_STATUS);
			replaceDestination(DestinationNames.COMMERCE_SUBSCRIPTION_STATUS);
			replaceDestination(
				DestinationNames.DOCUMENT_LIBRARY_AUDIO_PROCESSOR);
			replaceDestination(DestinationNames.DOCUMENT_LIBRARY_DELETION);
			replaceDestination(DestinationNames.DOCUMENT_LIBRARY_PDF_PROCESSOR);
			replaceDestination(
				DestinationNames.DOCUMENT_LIBRARY_RAW_METADATA_PROCESSOR);
			replaceDestination(
				DestinationNames.DOCUMENT_LIBRARY_VIDEO_PROCESSOR);
			replaceDestination(DestinationNames.MAIL);
			replaceDestination(DestinationNames.SUBSCRIPTION_SENDER);
			replaceDestination("liferay/adaptive_media_processor");
			replaceDestination("liferay/asset_auto_tagger");
			replaceDestination("liferay/asset_category_asset_entries_reindex");
			replaceDestination("liferay/ddm_structure_reindex");
			replaceDestination("liferay/report_request");
			replaceDestination("liferay/reports_admin");
			replaceDestination("liferay/segments_entry_reindex");
			replaceDestination("liferay/tensorflow_model_download");

			if (_sync != null) {
				for (String name : _sync.destinationNames()) {
					replaceDestination(name);
				}
			}

			Destination schedulerDestination = MessageBusUtil.getDestination(
				DestinationNames.SCHEDULER_DISPATCH);

			if (schedulerDestination == null) {
				return;
			}

			_registerDestination(
				new TestSynchronousDestination() {

					@Override
					public String getName() {
						return DestinationNames.SCHEDULER_DISPATCH;
					}

					@Override
					public void send(Message message) {
					}

				});
		}

		public void replaceDestination(String destinationName) {
			Destination destination = MessageBusUtil.getDestination(
				destinationName);

			if (destination != null) {
				try {
					ReflectionTestUtil.getField(
						destination.getClass(),
						"_noticeableThreadPoolExecutor");

					_registerDestination(
						createSynchronousDestination(destinationName));
				}
				catch (Exception exception) {
				}
			}
			else {
				_registerDestination(
					createSynchronousDestination(destinationName));
			}
		}

		public void restorePreviousSync() {
			if (_bufferedIncrementForceSyncSafeCloseable != null) {
				_bufferedIncrementForceSyncSafeCloseable.close();
			}

			for (ServiceRegistration<Destination> serviceRegistration :
					_serviceRegistrations) {

				serviceRegistration.unregister();
			}

			_serviceRegistrations.clear();

			_serviceTracker.close();
		}

		/**
		 * @deprecated As of Mueller (7.2.x), with no direct replacement
		 */
		@Deprecated
		public void setForceSync(boolean forceSync) {
		}

		public void setSync(Sync sync) {
			_sync = sync;
		}

		private void _registerDestination(Destination destination) {
			_serviceRegistrations.add(
				_bundleContext.registerService(
					Destination.class, destination,
					HashMapDictionaryBuilder.<String, Object>put(
						"destination.name", destination.getName()
					).put(
						"service.ranking", Integer.MAX_VALUE - 500
					).build()));
		}

		private Filter _registerDestinationFilter(String destinationName) {
			return SystemBundleUtil.createFilter(
				StringBundler.concat(
					"(&(destination.name=", destinationName, ")(objectClass=",
					Destination.class.getName(), "))"));
		}

		private void _waitForDependencies(Filter... filters) {
			for (Filter filter : filters) {
				ServiceTracker<Object, Object> serviceTracker =
					new ServiceTracker<>(
						SystemBundleUtil.getBundleContext(), filter, null);

				serviceTracker.open();

				while (true) {
					try {
						Object service = serviceTracker.waitForService(2000);

						if (service != null) {
							serviceTracker.close();

							break;
						}

						System.out.println(
							"Waiting for destination " + filter.toString());
					}
					catch (InterruptedException interruptedException) {
						System.out.println(
							StringBundler.concat(
								"Stopped waiting for destination ", filter,
								" due to interruption"));

						return;
					}
				}
			}
		}

		private SafeCloseable _bufferedIncrementForceSyncSafeCloseable;
		private final List<ServiceRegistration<Destination>>
			_serviceRegistrations = new ArrayList<>();
		private final ServiceTracker
			<MessageListenerRegistry, MessageListenerRegistry> _serviceTracker =
				new ServiceTracker<>(
					SystemBundleUtil.getBundleContext(),
					MessageListenerRegistry.class, null);
		private Sync _sync;

	}

	public static class TestSynchronousDestination extends BaseDestination {

		@Override
		public void send(Message message) {
			for (MessageListener messageListener :
					messageListenerRegistry.getMessageListeners(name)) {

				try {
					messageListener.receive(message);
				}
				catch (MessageListenerException messageListenerException) {
					_log.error(
						"Unable to process message " + message,
						messageListenerException);
				}
			}
		}

		private static final Log _log = LogFactoryUtil.getLog(
			TestSynchronousDestination.class);

	}

	protected SynchronousDestinationTestRule() {
	}

	private SyncHandler _createSyncHandler(Sync sync) {
		SyncHandler syncHandler = new SyncHandler();

		syncHandler.setSync(sync);

		syncHandler.enableSync();

		return syncHandler;
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final TransactionConfig _transactionConfig;

	static {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setPropagation(Propagation.NOT_SUPPORTED);
		builder.setRollbackForClasses(
			PortalException.class, SystemException.class);

		_transactionConfig = builder.build();
	}

	private static class CleanTransactionSynchronousDestination
		extends TestSynchronousDestination {

		@Override
		public void send(Message message) {
			try {
				TransactionInvokerUtil.invoke(
					_transactionConfig,
					() -> {
						CleanTransactionSynchronousDestination.super.send(
							message);

						return null;
					});
			}
			catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		}

	}

}