/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.NamedThreadFactory;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.configuration.SearchEngineHelperConfiguration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.search.configuration.SearchEngineHelperConfiguration",
	service = SearchEngineHelper.class
)
public class SearchEngineHelperImpl implements SearchEngineHelper {

	@Override
	public ExecutorService getDocumentsConsumerExecutorService() {
		return _documentsConsumerExecutorService;
	}

	@Override
	public ExecutorService getDocumentsProducerExecutorService() {
		return _documentsProducerExecutorService;
	}

	@Override
	public String[] getEntryClassNames() {
		Set<String> assetEntryClassNames = new HashSet<>();

		for (Indexer<?> indexer : IndexerRegistryUtil.getIndexers()) {
			for (String className : indexer.getSearchClassNames()) {
				if (_excludedEntryClassNames.contains(className)) {
					continue;
				}

				assetEntryClassNames.add(className);
			}
		}

		return assetEntryClassNames.toArray(new String[0]);
	}

	@Override
	public SearchEngine getSearchEngine() {
		return _searchEngine;
	}

	@Override
	public void initialize(long companyId) {
		_searchEngine.initialize(companyId);
	}

	@Override
	public void removeCompany(long companyId) {
		_searchEngine.removeCompany(companyId);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_searchEngineHelperConfiguration = ConfigurableUtil.createConfigurable(
			SearchEngineHelperConfiguration.class, properties);

		_excludedEntryClassNames.clear();

		Collections.addAll(
			_excludedEntryClassNames,
			_searchEngineHelperConfiguration.excludedEntryClassNames());

		_initDocumentsConsumerExecutorService(
			_searchEngineHelperConfiguration.documentsConsumerMaxPoolSize());
		_initDocumentsProducerExecutorService(
			_searchEngineHelperConfiguration.documentsProducerMaxPoolSize());
	}

	@Deactivate
	protected void deactivate() {
		_documentsConsumerExecutorService.shutdown();

		try {
			_documentsConsumerExecutorService.awaitTermination(
				_searchEngineHelperConfiguration.shutdownTimeout(),
				TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException interruptedException) {
			_log.error(interruptedException);
		}

		_documentsProducerExecutorService.shutdown();

		try {
			_documentsProducerExecutorService.awaitTermination(
				_searchEngineHelperConfiguration.shutdownTimeout(),
				TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException interruptedException) {
			_log.error(interruptedException);
		}
	}

	private void _initDocumentsConsumerExecutorService(int maxPoolSize) {
		if (maxPoolSize < 1) {
			maxPoolSize = _DEFAULT_CONSUMER_MAX_POOL_SIZE;
		}

		ExecutorService documentsConsumerExecutorService =
			_documentsConsumerExecutorService;

		if (documentsConsumerExecutorService != null) {
			documentsConsumerExecutorService.shutdown();
		}

		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
			0, maxPoolSize, 60, TimeUnit.SECONDS, new SynchronousQueue<>(),
			new NamedThreadFactory(
				"DocumentsConsumer", Thread.NORM_PRIORITY, null),
			new ThreadPoolExecutor.CallerRunsPolicy());

		threadPoolExecutor.allowCoreThreadTimeOut(true);

		_documentsConsumerExecutorService = threadPoolExecutor;
	}

	private void _initDocumentsProducerExecutorService(int maxPoolSize) {
		if (maxPoolSize < 1) {
			maxPoolSize = _DEFAULT_PRODUCER_MAX_POOL_SIZE;
		}

		ExecutorService documentsProducerExecutorService =
			_documentsProducerExecutorService;

		if (documentsProducerExecutorService != null) {
			documentsProducerExecutorService.shutdown();
		}

		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
			0, maxPoolSize, 60, TimeUnit.SECONDS, new SynchronousQueue<>(),
			new NamedThreadFactory(
				"DocumentsProducer", Thread.NORM_PRIORITY, null),
			new ThreadPoolExecutor.CallerRunsPolicy());

		threadPoolExecutor.allowCoreThreadTimeOut(true);

		_documentsProducerExecutorService = threadPoolExecutor;
	}

	private static final int _DEFAULT_CONSUMER_MAX_POOL_SIZE;

	private static final int _DEFAULT_PRODUCER_MAX_POOL_SIZE;

	private static final Log _log = LogFactoryUtil.getLog(
		SearchEngineHelperImpl.class);

	static {
		Runtime runtime = Runtime.getRuntime();

		int producerMaxPoolSize = GetterUtil.getInteger(
			PropsUtil.get(
				"search.engine.helper.documents.producer.maxpoolsize"),
			runtime.availableProcessors());

		if (producerMaxPoolSize < 1) {
			producerMaxPoolSize = 1;
		}

		int consumerMaxPoolSize = GetterUtil.getInteger(
			PropsUtil.get(
				"search.engine.helper.documents.consumer.maxpoolsize"),
			producerMaxPoolSize / 2);

		if (consumerMaxPoolSize < 1) {
			consumerMaxPoolSize = 1;
		}

		_DEFAULT_CONSUMER_MAX_POOL_SIZE = consumerMaxPoolSize;
		_DEFAULT_PRODUCER_MAX_POOL_SIZE = producerMaxPoolSize;
	}

	private volatile ExecutorService _documentsConsumerExecutorService;
	private volatile ExecutorService _documentsProducerExecutorService;
	private final Set<String> _excludedEntryClassNames =
		Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile SearchEngine _searchEngine;

	private volatile SearchEngineHelperConfiguration
		_searchEngineHelperConfiguration;

}