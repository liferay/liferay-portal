/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.BufferableThreadLocal;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableAware;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.util.PortalInstances;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Map;

/**
 * @author Shuyang Zhou
 */
public class IndexableAdvice extends ChainableMethodAdvice {

	@Override
	public Object createMethodContext(
		Object target, Method method,
		Map<Class<? extends Annotation>, Annotation> annotations) {

		Indexable indexable = (Indexable)annotations.get(Indexable.class);

		if (indexable == null) {
			return null;
		}

		Class<?> returnType = method.getReturnType();

		if (!BaseModel.class.isAssignableFrom(returnType)) {
			if (_log.isWarnEnabled()) {
				_log.warn(method + " does not have a valid return type");
			}

			return null;
		}

		boolean skipBuffer = false;

		Transactional transactional = (Transactional)annotations.get(
			Transactional.class);

		if ((transactional != null) &&
			(transactional.propagation() == Propagation.REQUIRES_NEW)) {

			skipBuffer = true;
		}

		return new IndexableContext(
			indexable.callbackKey(), returnType.getName(), indexable.type(),
			_getServiceContextParameterIndex(method), skipBuffer);
	}

	@Override
	protected Object afterReturning(
			AopMethodInvocation aopMethodInvocation, Object[] arguments,
			Object result)
		throws Throwable {

		if (result == null) {
			return result;
		}

		if (PortalInstances.isCurrentCompanyInDeletionProcess() ||
			IndexWriterHelperUtil.isIndexReadOnly() ||
			((result instanceof IndexableAware indexableAware) &&
			 !indexableAware.shouldIndex())) {

			if (_log.isDebugEnabled()) {
				if (PortalInstances.isCurrentCompanyInDeletionProcess()) {
					_log.debug(
						"Skip indexing because company delete is in process");
				}
				else if (IndexWriterHelperUtil.isIndexReadOnly()) {
					_log.debug("Skip indexing because the index is read only");
				}
				else {
					_log.debug("Skip indexing because the result opts out");
				}
			}

			return result;
		}

		IndexableContext indexableContext =
			aopMethodInvocation.getAdviceMethodContext();

		String name = indexableContext._name;

		Indexer<Object> indexer = IndexerRegistryUtil.getIndexer(name);

		if (indexer != null) {
			_reindex(indexableContext, indexer, arguments, result);

			return result;
		}

		DependencyManagerSyncUtil.registerSyncCallable(
			new CompanyInheritableThreadLocalCallable<>(
				() -> {
					Indexer<Object> curIndexer = IndexerRegistryUtil.getIndexer(
						name);

					if (curIndexer == null) {
						return null;
					}

					_reindex(indexableContext, curIndexer, arguments, result);

					return null;
				}));

		return result;
	}

	private int _getServiceContextParameterIndex(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();

		for (int i = parameterTypes.length - 1; i >= 0; i--) {
			if (ServiceContext.class.isAssignableFrom(parameterTypes[i])) {
				return i;
			}
		}

		return -1;
	}

	private void _reindex(
			IndexableContext indexableContext, Indexer<Object> indexer,
			Object result)
		throws SearchException {

		if (indexableContext._indexableType == IndexableType.DELETE) {
			indexer.delete(result);
		}
		else {
			Indexable.Callback callback = _callbacks.getService(
				indexableContext._callbackKey);

			if (callback == null) {
				indexer.reindex(result);
			}
			else {
				callback.reindex((BaseModel<?>)result);
			}
		}
	}

	private void _reindex(
			IndexableContext indexableContext, Indexer<Object> indexer,
			Object[] arguments, Object result)
		throws SearchException {

		if (IndexWriterHelperUtil.isIndexReadOnly(indexer.getClassName())) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping indexing read only index for " +
						indexer.getClassName());
			}

			return;
		}

		int serviceContextIndex = indexableContext._serviceContextIndex;

		if (serviceContextIndex >= 0) {
			ServiceContext serviceContext =
				(ServiceContext)arguments[serviceContextIndex];

			if ((serviceContext != null) &&
				!serviceContext.isIndexingEnabled()) {

				return;
			}
		}

		if (indexableContext._skipBuffer) {
			try (SafeCloseable safeCloseable =
					BufferableThreadLocal.setEnabledWithSafeCloseable(false)) {

				_reindex(indexableContext, indexer, result);
			}
		}
		else {
			_reindex(indexableContext, indexer, result);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexableAdvice.class);

	private static final ServiceTrackerMap<String, Indexable.Callback>
		_callbacks = ServiceTrackerMapFactory.openSingleValueMap(
			SystemBundleUtil.getBundleContext(), Indexable.Callback.class,
			"key");

	private static class IndexableContext {

		private IndexableContext(
			String callbackKey, String name, IndexableType indexableType,
			int serviceContextIndex, boolean skipBuffer) {

			_callbackKey = callbackKey;
			_name = name;
			_indexableType = indexableType;
			_serviceContextIndex = serviceContextIndex;
			_skipBuffer = skipBuffer;
		}

		private final String _callbackKey;
		private final IndexableType _indexableType;
		private final String _name;
		private final int _serviceContextIndex;
		private final boolean _skipBuffer;

	}

}