/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.aop;

import com.liferay.portal.cache.thread.local.ThreadLocalCacheAdvice;
import com.liferay.portal.increment.BufferedIncrementAdvice;
import com.liferay.portal.internal.cluster.ClusterableAdvice;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.search.IndexableAdvice;
import com.liferay.portal.security.access.control.AccessControlAdvice;
import com.liferay.portal.service.ServiceContextAdvice;
import com.liferay.portal.spring.transaction.TransactionExecutor;
import com.liferay.portal.systemevent.SystemEventAdvice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Preston Crary
 */
public class AopCacheManager {

	public static synchronized AopInvocationHandler create(
		Object target, TransactionExecutor transactionExecutor) {

		AopInvocationHandler aopInvocationHandler = new AopInvocationHandler(
			target,
			_chainableMethodAdvices.toArray(new ChainableMethodAdvice[0]),
			transactionExecutor);

		_aopInvocationHandlers.add(aopInvocationHandler);

		return aopInvocationHandler;
	}

	public static synchronized void destroy(
		AopInvocationHandler aopInvocationHandler) {

		_aopInvocationHandlers.remove(aopInvocationHandler);
	}

	private static List<ChainableMethodAdvice>
		_createStaticChainableMethodAdvices() {

		List<ChainableMethodAdvice> chainableMethodAdvices = new ArrayList<>();

		chainableMethodAdvices.add(new AccessControlAdvice());

		chainableMethodAdvices.add(new BufferedIncrementAdvice());

		if (PropsValues.CLUSTER_LINK_ENABLED) {
			chainableMethodAdvices.add(new ClusterableAdvice());
		}

		chainableMethodAdvices.add(new IndexableAdvice());

		chainableMethodAdvices.add(new RetryAdvice());

		chainableMethodAdvices.add(new ServiceContextAdvice());

		chainableMethodAdvices.add(new SystemEventAdvice());

		chainableMethodAdvices.add(new ThreadLocalCacheAdvice());

		chainableMethodAdvices.sort(_CHAINABLE_METHOD_ADVICE_COMPARATOR);

		return chainableMethodAdvices;
	}

	private AopCacheManager() {
	}

	private static final Comparator<ChainableMethodAdvice>
		_CHAINABLE_METHOD_ADVICE_COMPARATOR = Comparator.comparing(
			chainableMethodAdvice -> {
				Class<? extends ChainableMethodAdvice> clazz =
					chainableMethodAdvice.getClass();

				return clazz.getName();
			});

	private static final Set<AopInvocationHandler> _aopInvocationHandlers =
		new HashSet<>();
	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final List<ChainableMethodAdvice> _chainableMethodAdvices =
		_createStaticChainableMethodAdvices();

	private static class ChainableMethodAdviceServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ChainableMethodAdvice, ChainableMethodAdvice> {

		@Override
		public ChainableMethodAdvice addingService(
			ServiceReference<ChainableMethodAdvice> serviceReference) {

			ChainableMethodAdvice chainableMethodAdvice =
				_bundleContext.getService(serviceReference);

			synchronized (AopCacheManager.class) {
				int index = Collections.binarySearch(
					_chainableMethodAdvices, chainableMethodAdvice,
					_CHAINABLE_METHOD_ADVICE_COMPARATOR);

				if (index < 0) {
					index = -index - 1;
				}

				_chainableMethodAdvices.add(index, chainableMethodAdvice);

				_reset();
			}

			return chainableMethodAdvice;
		}

		@Override
		public void modifiedService(
			ServiceReference<ChainableMethodAdvice> serviceReference,
			ChainableMethodAdvice chainableMethodAdvice) {

			synchronized (AopCacheManager.class) {
				for (AopInvocationHandler aopInvocationHandler :
						_aopInvocationHandlers) {

					aopInvocationHandler.reset();
				}
			}
		}

		@Override
		public void removedService(
			ServiceReference<ChainableMethodAdvice> serviceReference,
			ChainableMethodAdvice chainableMethodAdvice) {

			synchronized (AopCacheManager.class) {
				_chainableMethodAdvices.remove(chainableMethodAdvice);

				_reset();
			}

			_bundleContext.ungetService(serviceReference);
		}

		private void _reset() {
			ChainableMethodAdvice[] chainableMethodAdvices =
				_chainableMethodAdvices.toArray(new ChainableMethodAdvice[0]);

			for (AopInvocationHandler aopInvocationHandler :
					_aopInvocationHandlers) {

				aopInvocationHandler.setChainableMethodAdvices(
					chainableMethodAdvices);
			}
		}

	}

	static {
		ServiceTracker<?, ?> serviceTracker = new ServiceTracker<>(
			_bundleContext, ChainableMethodAdvice.class,
			new ChainableMethodAdviceServiceTrackerCustomizer());

		serviceTracker.open();
	}

}