/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.buffer;

import com.liferay.object.search.StrictObjectReindexThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.search.Bufferable;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.search.configuration.IndexerRegistryConfiguration;
import com.liferay.portal.search.index.IndexStatusManager;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.portal.util.PortalInstances;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Michael C. Han
 */
public class BufferedIndexerInvocationHandler implements InvocationHandler {

	public BufferedIndexerInvocationHandler(
		Indexer<?> indexer, IndexStatusManager indexStatusManager,
		IndexerRegistryConfiguration indexerRegistryConfiguration) {

		_indexer = indexer;
		_indexStatusManager = indexStatusManager;
		_indexerRegistryConfiguration = indexerRegistryConfiguration;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		Annotation annotation = method.getAnnotation(Bufferable.class);

		IndexerRequestBuffer indexerRequestBuffer = IndexerRequestBuffer.get();

		if ((annotation == null) || (args.length == 0) || (args.length > 2) ||
			(indexerRequestBuffer == null)) {

			return method.invoke(_indexer, args);
		}

		if (_indexStatusManager.isIndexReadOnly()) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping indexer request buffer because index is read " +
						"only");
			}

			return null;
		}

		if (PortalInstances.isCurrentCompanyInDeletionProcess()) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping indexer request buffer because a company " +
						"delete is in process");
			}

			return null;
		}

		Class<?> args0Class = args[0].getClass();

		if (!(args[0] instanceof BaseModel) &&
			!(args[0] instanceof ClassedModel) &&
			!(args0Class.isArray() ||
			  Collection.class.isAssignableFrom(args0Class)) &&
			!((args.length == 2) && (args[0] instanceof String) &&
			  Objects.equals(args[1].getClass(), Long.class))) {

			return method.invoke(_indexer, args);
		}

		if (args[0] instanceof ClassedModel) {
			if (StrictObjectReindexThreadLocal.isStrictObjectReindex()) {
				MethodKey methodKey = new MethodKey(
					Indexer.class, method.getName(), Object.class);

				IndexerRequest indexerRequest = new IndexerRequest(
					methodKey.getMethod(), (ClassedModel)args[0], _indexer);

				_bufferRequest(indexerRequest, indexerRequestBuffer);
			}
			else if (Objects.equals(method.getName(), "reindex")) {
				MethodKey methodKey = new MethodKey(
					Indexer.class, method.getName(), String.class, Long.TYPE);

				ClassedModel classedModel = (ClassedModel)args[0];

				Long classPK = (Long)classedModel.getPrimaryKeyObj();

				bufferRequest(
					methodKey, classedModel.getModelClassName(), classPK,
					indexerRequestBuffer);
			}
			else {
				MethodKey methodKey = new MethodKey(
					Indexer.class, method.getName(), Object.class);

				bufferRequest(methodKey, args[0], indexerRequestBuffer);
			}
		}
		else if (args.length == 2) {
			String className = (String)args[0];

			PersistedModelLocalService persistedModelLocalService =
				PersistedModelLocalServiceRegistryUtil.
					getPersistedModelLocalService(className);

			Long classPK = (Long)args[1];

			if ((persistedModelLocalService != null) &&
				(persistedModelLocalService.fetchPersistedModel(classPK) !=
					null)) {

				bufferRequest(
					new MethodKey(
						Indexer.class, method.getName(), String.class,
						Long.TYPE),
					className, classPK, indexerRequestBuffer);
			}
		}
		else {
			MethodKey methodKey = new MethodKey(
				Indexer.class, method.getName(), Object.class);

			Collection<Object> objects = null;

			if (args0Class.isArray()) {
				objects = Arrays.asList((Object[])args[0]);
			}
			else {
				objects = (Collection<Object>)args[0];
			}

			for (Object object : objects) {
				if (!(object instanceof ClassedModel)) {
					return method.invoke(_indexer, args);
				}

				bufferRequest(methodKey, object, indexerRequestBuffer);
			}
		}

		return null;
	}

	public void setIndexerRegistryConfiguration(
		IndexerRegistryConfiguration indexerRegistryConfiguration) {

		_indexerRegistryConfiguration = indexerRegistryConfiguration;
	}

	public void setIndexerRequestBufferOverflowHandler(
		IndexerRequestBufferOverflowHandler
			indexerRequestBufferOverflowHandler) {

		_indexerRequestBufferOverflowHandler =
			indexerRequestBufferOverflowHandler;
	}

	protected void bufferRequest(
			MethodKey methodKey, Object object,
			IndexerRequestBuffer indexerRequestBuffer)
		throws Exception {

		BaseModel<?> baseModel = (BaseModel<?>)object;

		ClassedModel classedModel = (ClassedModel)baseModel.clone();

		IndexerRequest indexerRequest = new IndexerRequest(
			methodKey.getMethod(), classedModel, _indexer);

		_bufferRequest(indexerRequest, indexerRequestBuffer);
	}

	protected void bufferRequest(
			MethodKey methodKey, String className, Long classPK,
			IndexerRequestBuffer indexerRequestBuffer)
		throws Exception {

		if (className.equals("com.liferay.object.model.ObjectDefinition") && classPK.equals(Long.getLong("ObjectDefinition.APIApplication.id"))) {
			System.out.println("====== bufferRequest for className: " + className + ", classPK: " + classPK);
		}
		if (_indexStatusManager.isIndexReadOnly(className)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping indexer request buffer because index for " +
						className + " is read only");
			}

			if (className.equals("com.liferay.object.model.ObjectDefinition") && classPK.equals(Long.getLong("ObjectDefinition.APIApplication.id"))) {
				System.out.println("====== skipped for read only setting: " + className + ", classPK: " + classPK);
			}

			return;
		}

		IndexerRequest indexerRequest = new IndexerRequest(
			methodKey.getMethod(), _indexer, className, classPK);

		_bufferRequest(indexerRequest, indexerRequestBuffer);
	}

	private void _bufferRequest(
			IndexerRequest indexerRequest,
			IndexerRequestBuffer indexerRequestBuffer)
		throws Exception {

		IndexerRequestBufferHandler indexerRequestBufferHandler =
			new IndexerRequestBufferHandler(
				_indexerRequestBufferOverflowHandler,
				_indexerRegistryConfiguration);

		indexerRequestBufferHandler.bufferRequest(
			indexerRequest, indexerRequestBuffer);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BufferedIndexerInvocationHandler.class);

	private final Indexer<?> _indexer;
	private volatile IndexerRegistryConfiguration _indexerRegistryConfiguration;
	private volatile IndexerRequestBufferOverflowHandler
		_indexerRequestBufferOverflowHandler;
	private final IndexStatusManager _indexStatusManager;

}