/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegateRegistry;
import com.liferay.batch.engine.BatchEngineTaskItemDelegateRegistry.BatchEngineTaskItemDelegateSafeCloseable;
import com.liferay.batch.engine.internal.writer.BatchEngineTaskItemDelegateProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Igor Beslic
 */
@Component(service = BatchEngineTaskItemDelegateRegistry.class)
public class BatchEngineTaskItemDelegateRegistryImpl
	implements BatchEngineTaskItemDelegateRegistry {

	@Override
	public BatchEngineTaskItemDelegateSafeCloseable
		getBatchEngineTaskItemDelegateWithSafeCloseable(
			long companyId, String itemClassName, String taskItemDelegateName) {

		ServiceObjects<Object> serviceObjects = _getServiceObjects(
			companyId, itemClassName, taskItemDelegateName);

		if (serviceObjects == null) {
			throw new IllegalStateException(
				StringBundler.concat(
					"No batch engine task item delegate is available for ",
					"class name ", itemClassName,
					" and task item delegate name ", taskItemDelegateName));
		}

		Object service = serviceObjects.getService();

		BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate =
			_batchEngineTaskItemDelegateProvider.toBatchEngineTaskItemDelegate(
				service);

		return new BatchEngineTaskItemDelegateSafeCloseable() {

			@Override
			public void close() {
				serviceObjects.ungetService(service);
			}

			@Override
			public BatchEngineTaskItemDelegate<?>
				getBatchEngineTaskItemDelegate() {

				return batchEngineTaskItemDelegate;
			}

		};
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, null,
			"(|(batch.engine.task.item.delegate=true)(objectClass=" +
				BatchEngineTaskItemDelegate.class.getName() + "))",
			(serviceReference, emitter) -> {
				try {
					Class<?> itemClass = _getItemClass(
						bundleContext, serviceReference);

					emitter.emit(
						_encodeKey(
							GetterUtil.getLong(
								serviceReference.getProperty("companyId")),
							itemClass.getName(),
							(String)serviceReference.getProperty(
								"batch.engine.task.item.delegate.name")));
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}
			},
			new ServiceTrackerCustomizer<Object, ServiceObjects<Object>>() {

				@Override
				public ServiceObjects<Object> addingService(
					ServiceReference<Object> serviceReference) {

					return bundleContext.getServiceObjects(serviceReference);
				}

				@Override
				public void modifiedService(
					ServiceReference<Object> serviceReference,
					ServiceObjects<Object> serviceObjects) {
				}

				@Override
				public void removedService(
					ServiceReference<Object> serviceReference,
					ServiceObjects<Object> serviceObjects) {
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private String _encodeKey(
		Long companyId, String itemClassName, String taskItemDelegateName) {

		if (Validator.isNull(taskItemDelegateName)) {
			taskItemDelegateName = "DEFAULT";
		}

		if (Validator.isNull(companyId)) {
			return StringBundler.concat(
				itemClassName, StringPool.POUND, taskItemDelegateName);
		}

		return StringBundler.concat(
			itemClassName, StringPool.POUND, taskItemDelegateName,
			StringPool.POUND, companyId);
	}

	private Class<?> _getItemClass(
		BundleContext bundleContext, ServiceReference<?> serviceReference) {

		BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate =
			_batchEngineTaskItemDelegateProvider.toBatchEngineTaskItemDelegate(
				bundleContext.getService(serviceReference));

		Class<?> itemClass = batchEngineTaskItemDelegate.getItemClass();

		if (itemClass != null) {
			return itemClass;
		}

		Class<?> batchEngineTaskItemDelegateClass =
			batchEngineTaskItemDelegate.getClass();

		itemClass = _getItemClassFromGenericInterfaces(
			batchEngineTaskItemDelegateClass.getGenericInterfaces());

		if (itemClass == null) {
			itemClass = _getItemClassFromGenericSuperclass(
				batchEngineTaskItemDelegateClass.getGenericSuperclass());
		}

		if (itemClass == null) {
			throw new IllegalStateException(
				BatchEngineTaskItemDelegate.class.getName() +
					" is not implemented");
		}

		return itemClass;
	}

	private Class<?> _getItemClass(ParameterizedType parameterizedType) {
		Type[] genericTypes = parameterizedType.getActualTypeArguments();

		return (Class<BatchEngineTaskItemDelegate<?>>)genericTypes[0];
	}

	private Class<?> _getItemClassFromGenericInterfaces(
		Type[] genericInterfaceTypes) {

		for (Type genericInterfaceType : genericInterfaceTypes) {
			if (genericInterfaceType instanceof ParameterizedType) {
				ParameterizedType parameterizedType =
					(ParameterizedType)genericInterfaceType;

				if (parameterizedType.getRawType() !=
						BatchEngineTaskItemDelegate.class) {

					continue;
				}

				return _getItemClass(parameterizedType);
			}
		}

		return null;
	}

	private Class<?> _getItemClassFromGenericSuperclass(
		Type genericSuperclassType) {

		if (genericSuperclassType == null) {
			return null;
		}

		return _getItemClass((ParameterizedType)genericSuperclassType);
	}

	private ServiceObjects<Object> _getServiceObjects(
		long companyId, String itemClassName, String taskItemDelegateName) {

		String companyIdKey = _encodeKey(
			companyId, itemClassName, taskItemDelegateName);

		if (_serviceTrackerMap.containsKey(companyIdKey)) {
			return _serviceTrackerMap.getService(companyIdKey);
		}

		String key = _encodeKey(null, itemClassName, taskItemDelegateName);

		if (_serviceTrackerMap.containsKey(key)) {
			return _serviceTrackerMap.getService(key);
		}

		return null;
	}

	@Reference
	private BatchEngineTaskItemDelegateProvider
		_batchEngineTaskItemDelegateProvider;

	private ServiceTrackerMap<String, ServiceObjects<Object>>
		_serviceTrackerMap;

}