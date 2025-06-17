/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.rule;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.io.unsync.UnsyncPrintWriter;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.ClassLoaderBeanHandler;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionCustomizer;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.dao.orm.SessionWrapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.ResourcePermissionTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;

import java.io.Closeable;
import java.io.Serializable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Matthew Tambara
 */
public class DataGuardTestRuleUtil {

	public static void afterClass(DataBag dataBag, String testClassName)
		throws Throwable {

		afterClass(dataBag, testClassName, true);
	}

	public static void afterClass(
			DataBag dataBag, String testClassName, boolean autoDelete)
		throws Throwable {

		ServiceRegistration<SessionCustomizer> serviceRegistration =
			dataBag._serviceRegistration;

		serviceRegistration.unregister();

		_records.remove();

		_autoDeleteAndAssert(
			testClassName, dataBag._dataMap, dataBag._portlets,
			dataBag._records, autoDelete);
	}

	public static void afterMethod(DataBag dataBag, String testClassName)
		throws Throwable {

		afterMethod(dataBag, testClassName, true);
	}

	public static void afterMethod(
			DataBag dataBag, String testClassName, boolean autoDelete)
		throws Throwable {

		_autoDeleteAndAssert(
			testClassName, dataBag._dataMap, dataBag._portlets,
			dataBag._records, autoDelete);
	}

	public static DataBag beforeClass() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Map<String, Map<Serializable, String>> records =
			new ConcurrentHashMap<>();

		_records.set(records);

		ServiceRegistration<SessionCustomizer> serviceRegistration =
			bundleContext.registerService(
				SessionCustomizer.class,
				new RecordingSessionCustomizer(records), null);

		return new DataBag(
			_captureDataMap(), PortletLocalServiceUtil.getPortlets(), records,
			serviceRegistration);
	}

	public static DataBag beforeMethod() {
		return new DataBag(
			_captureDataMap(), PortletLocalServiceUtil.getPortlets(),
			_records.get(), null);
	}

	public static void smartDelete(
			PersistedModelLocalService persistedModelLocalService,
			Class<?> modelClass, PersistedModel persistedModel)
		throws Exception {

		Method deleteMethod = null;

		Class<?> clazz = persistedModelLocalService.getClass();

		Class<?>[] parameterTypes = new Class<?>[] {modelClass};

		for (Method method : clazz.getMethods()) {
			String methodName = method.getName();

			if (methodName.startsWith("delete") &&
				Arrays.equals(method.getParameterTypes(), parameterTypes)) {

				if (deleteMethod == null) {
					deleteMethod = method;
				}
				else {
					String deleteMethodName = deleteMethod.getName();

					if (deleteMethodName.length() > methodName.length()) {
						deleteMethod = method;
					}
				}
			}
		}

		try {
			if (deleteMethod == null) {
				persistedModelLocalService.deletePersistedModel(persistedModel);
			}
			else {
				BaseModel<?> baseModel = (BaseModel<?>)persistedModel;

				deleteMethod.invoke(
					persistedModelLocalService,
					persistedModelLocalService.getPersistedModel(
						baseModel.getPrimaryKeyObj()));
			}
		}
		catch (Throwable throwable1) {
			ResourcePermissionTestUtil.deleteResourcePermissions(
				persistedModel);

			BasePersistence<?> basePersistence = _getBasePersistence(
				persistedModelLocalService);

			Class<?> persistenceClass = basePersistence.getClass();

			try (Closeable closeable1 = _installTransactionExecutor(
					_getSymbolicName(persistenceClass.getClassLoader()))) {

				TransactionInvokerUtil.invoke(
					_transactionConfig,
					() -> {
						try (Closeable closeable2 =
								_removeSessionFactoryVerifier(
									basePersistence)) {

							Session session =
								basePersistence.getCurrentSession();

							if (session.contains(persistedModel)) {
								session.delete(persistedModel);
							}
							else {
								BaseModel<?> baseModel =
									(BaseModel<?>)persistedModel;

								Object refetchedBaseModel = session.get(
									persistedModel.getClass(),
									baseModel.getPrimaryKeyObj());

								if (refetchedBaseModel != null) {
									session.delete(refetchedBaseModel);
								}
							}

							return null;
						}
					});

				Indexer<PersistedModel> indexer =
					(Indexer<PersistedModel>)IndexerRegistryUtil.getIndexer(
						modelClass);

				if (indexer != null) {
					indexer.delete(persistedModel);
				}
			}
			catch (Throwable throwable2) {
				throwable2.addSuppressed(throwable1);

				ReflectionUtil.throwException(throwable2);
			}
		}
	}

	public static class DataBag {

		private DataBag(
			Map<String, List<BaseModel<?>>> dataMap, List<Portlet> portlets,
			Map<String, Map<Serializable, String>> records,
			ServiceRegistration<SessionCustomizer> serviceRegistration) {

			_dataMap = dataMap;
			_portlets = portlets;
			_records = records;
			_serviceRegistration = serviceRegistration;
		}

		private final Map<String, List<BaseModel<?>>> _dataMap;
		private final List<Portlet> _portlets;
		private final Map<String, Map<Serializable, String>> _records;
		private final ServiceRegistration<SessionCustomizer>
			_serviceRegistration;

	}

	private static void _autoDeleteAndAssert(
			String testClassName,
			Map<String, List<BaseModel<?>>> previousDataMap,
			List<Portlet> previousPortlets,
			Map<String, Map<Serializable, String>> records, boolean autoDelete)
		throws Throwable {

		for (Portlet portlet : PortletLocalServiceUtil.getPortlets()) {
			if (!previousPortlets.remove(portlet)) {
				PortletLocalServiceUtil.destroyPortlet(portlet);
			}
		}

		if (autoDelete) {
			_autoDeleteLeftovers(previousDataMap);
		}

		StringBundler sb = new StringBundler();

		Map<String, List<BaseModel<?>>> dataMap = _captureDataMap();

		for (Map.Entry<String, List<BaseModel<?>>> entry : dataMap.entrySet()) {
			String className = entry.getKey();

			List<BaseModel<?>> currentBaseModels = entry.getValue();

			List<BaseModel<?>> previsoutBaseModels = previousDataMap.remove(
				className);

			List<BaseModel<?>> leftoverBaseModels = new ArrayList<>(
				currentBaseModels);

			if (previsoutBaseModels != null) {
				leftoverBaseModels.removeAll(previsoutBaseModels);
			}

			if (!leftoverBaseModels.isEmpty()) {
				sb.append(testClassName);
				sb.append(" caused leftover data for class :");
				sb.append(className);
				sb.append(" with data : [\n");

				for (BaseModel<?> baseModel : leftoverBaseModels) {
					sb.append(StringPool.TAB);
					sb.append(baseModel);

					String backtraceInfo = null;

					Map<Serializable, String> map = records.get(
						baseModel.getModelClassName());

					if (map != null) {
						backtraceInfo = map.get(baseModel.getPrimaryKeyObj());
					}

					if (backtraceInfo == null) {
						sb.append(" with no backtrace info,\n");
					}
					else {
						sb.append(" with backtrace info,\n");
						sb.append(StringPool.TAB);
						sb.append(StringPool.TAB);
						sb.append(backtraceInfo);
						sb.append(",\n");
					}
				}

				sb.setStringAt("\n]\n", sb.index() - 1);
			}
		}

		Assert.assertTrue(sb.toString(), sb.index() == 0);
	}

	private static void _autoDeleteLeftovers(
			Map<String, List<BaseModel<?>>> previousDataMap)
		throws Throwable {

		Map<String, PersistedModelLocalService> persistedModelLocalServices =
			_getPersistedModelLocalServices();

		while (true) {
			boolean deleted = false;

			Map<String, List<BaseModel<?>>> dataMap = _captureDataMap();

			for (Map.Entry<String, List<BaseModel<?>>> entry :
					dataMap.entrySet()) {

				String className = entry.getKey();

				PersistedModelLocalService persistedModelLocalService =
					persistedModelLocalServices.get(className);

				Class<?> persistedModelLocalServiceClass =
					persistedModelLocalService.getClass();

				ClassLoader classLoader =
					persistedModelLocalServiceClass.getClassLoader();

				Class<?> modelClass = classLoader.loadClass(className);

				List<BaseModel<?>> currentBaseModels = entry.getValue();

				List<BaseModel<?>> previsoutBaseModels = previousDataMap.get(
					className);

				List<BaseModel<?>> leftoverBaseModels = new ArrayList<>(
					currentBaseModels);

				if (previsoutBaseModels != null) {
					leftoverBaseModels.removeAll(previsoutBaseModels);
				}

				for (BaseModel<?> leftoverBaseModel : leftoverBaseModels) {
					if (className.equals(ResourcePermission.class.getName())) {
						ResourcePermission resourcePermission =
							(ResourcePermission)leftoverBaseModel;

						if ((resourcePermission.getScope() ==
								ResourceConstants.SCOPE_INDIVIDUAL) &&
							(resourcePermission.getPrimKeyId() != 0) &&
							persistedModelLocalServices.containsKey(
								resourcePermission.getName())) {

							continue;
						}
					}

					smartDelete(
						persistedModelLocalService, modelClass,
						(PersistedModel)leftoverBaseModel);

					deleted = true;
				}
			}

			if (!deleted) {
				break;
			}
		}
	}

	private static Map<String, List<BaseModel<?>>> _captureDataMap() {
		Map<String, PersistedModelLocalService> persistedModelLocalServices =
			_getPersistedModelLocalServices();

		Map<String, List<BaseModel<?>>> dataMap = new HashMap<>();

		for (Map.Entry<String, PersistedModelLocalService> entry :
				persistedModelLocalServices.entrySet()) {

			PersistedModelLocalService persistedModelLocalService =
				entry.getValue();

			BasePersistence<?> basePersistence = _getBasePersistence(
				persistedModelLocalService);

			if (basePersistence == null) {
				continue;
			}

			Class<?> clazz = basePersistence.getClass();

			try (Closeable closeable1 = _installTransactionExecutor(
					_getSymbolicName(clazz.getClassLoader()))) {

				TransactionInvokerUtil.invoke(
					_transactionConfig,
					() -> {
						basePersistence.clearCache();

						try (Closeable closeable2 =
								_removeSessionFactoryVerifier(
									basePersistence)) {

							List<BaseModel<?>> baseModels =
								ReflectionTestUtil.invoke(
									basePersistence, "findAll",
									new Class<?>[0]);

							if (!baseModels.isEmpty()) {
								dataMap.put(entry.getKey(), baseModels);
							}
						}

						return null;
					});
			}
			catch (Throwable throwable) {
				return ReflectionUtil.throwException(throwable);
			}
		}

		return dataMap;
	}

	private static BasePersistence<?> _getBasePersistence(
		PersistedModelLocalService persistedModelLocalService) {

		while (true) {
			if (ProxyUtil.isProxyClass(persistedModelLocalService.getClass())) {
				InvocationHandler invocationHandler =
					ProxyUtil.getInvocationHandler(persistedModelLocalService);

				Class<?> clazz = invocationHandler.getClass();

				String className = clazz.getName();

				if (className.equals(
						"com.liferay.portal.spring.aop.AopInvocationHandler")) {

					persistedModelLocalService =
						ReflectionTestUtil.getFieldValue(
							invocationHandler, "_target");

					continue;
				}
				else if (invocationHandler instanceof ClassLoaderBeanHandler) {
					ClassLoaderBeanHandler classLoaderBeanHandler =
						(ClassLoaderBeanHandler)invocationHandler;

					persistedModelLocalService =
						(PersistedModelLocalService)
							classLoaderBeanHandler.getBean();

					continue;
				}
			}

			if (persistedModelLocalService instanceof ServiceWrapper) {
				ServiceWrapper<?> serviceWrapper =
					(ServiceWrapper<?>)persistedModelLocalService;

				Class<?> clazz = serviceWrapper.getClass();

				String simpleName = clazz.getSimpleName();

				if (simpleName.startsWith("Modular")) {
					return null;
				}

				persistedModelLocalService =
					(PersistedModelLocalService)
						serviceWrapper.getWrappedService();

				continue;
			}

			break;
		}

		Class<?> clazz = persistedModelLocalService.getClass();

		Deprecated deprecated = clazz.getAnnotation(Deprecated.class);

		if (deprecated != null) {
			return null;
		}

		return persistedModelLocalService.getBasePersistence();
	}

	private static Map<String, PersistedModelLocalService>
		_getPersistedModelLocalServices() {

		Map<String, PersistedModelLocalService>
			scrubbedPersistedModelLocalServices = new LinkedHashMap<>();

		ServiceTrackerMap<String, PersistedModelLocalService>
			serviceTrackerMap = ReflectionTestUtil.getFieldValue(
				PersistedModelLocalServiceRegistryUtil.class,
				"_serviceTrackerMap");

		for (String modelClassName : _PRIORITIZED_MODEL_CLASS_NAMES) {
			if (serviceTrackerMap.containsKey(modelClassName) &&
				(modelClassName.indexOf(CharPool.POUND) == -1)) {

				scrubbedPersistedModelLocalServices.put(
					modelClassName,
					serviceTrackerMap.getService(modelClassName));
			}
		}

		for (String modelClassName : serviceTrackerMap.keySet()) {
			if (!_blacklistedModelClassNames.contains(modelClassName) &&
				(modelClassName.indexOf(CharPool.POUND) == -1)) {

				scrubbedPersistedModelLocalServices.put(
					modelClassName,
					serviceTrackerMap.getService(modelClassName));
			}
		}

		return scrubbedPersistedModelLocalServices;
	}

	private static String _getSymbolicName(ClassLoader classLoader) {
		if (classLoader instanceof BundleReference) {
			BundleReference bundleReference = (BundleReference)classLoader;

			Bundle bundle = bundleReference.getBundle();

			return bundle.getSymbolicName();
		}

		return null;
	}

	private static Closeable _installTransactionExecutor(
			String originBundleSymbolicName)
		throws Exception {

		if (originBundleSymbolicName == null) {
			return () -> {
			};
		}

		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		Class<?> clazz = classLoader.loadClass(
			"com.liferay.portal.spring.transaction." +
				"TransactionExecutorThreadLocal");

		Field field = clazz.getDeclaredField("_transactionExecutorDeque");

		field.setAccessible(true);

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceReference<?>[] serviceReferences =
			bundleContext.getAllServiceReferences(
				"com.liferay.portal.spring.transaction.TransactionExecutor",
				"(origin.bundle.symbolic.name=" + originBundleSymbolicName +
					")");

		if (serviceReferences == null) {
			return () -> {
			};
		}

		Assert.assertEquals(
			StringBundler.concat(
				"Expected 1 TransactionExecutor for ", originBundleSymbolicName,
				", actually have ", Arrays.toString(serviceReferences)),
			1, serviceReferences.length);

		ServiceReference<?> serviceReference = serviceReferences[0];

		Object portletTransactionExecutor = bundleContext.getService(
			serviceReference);

		ThreadLocal<Deque<Object>> deque =
			(ThreadLocal<Deque<Object>>)field.get(null);

		Deque<Object> transactionExecutorDeque = deque.get();

		if (portletTransactionExecutor == transactionExecutorDeque.peek()) {
			return () -> {
			};
		}

		transactionExecutorDeque.push(portletTransactionExecutor);

		return () -> {
			transactionExecutorDeque.pop();

			bundleContext.ungetService(serviceReference);
		};
	}

	private static Closeable _removeSessionFactoryVerifier(
		BasePersistence<?> basePersistence) {

		SessionFactory originalSessionFactory =
			ReflectionTestUtil.getFieldValue(
				basePersistence, "_sessionFactory");

		Class<?> clazz = originalSessionFactory.getClass();

		if (!Objects.equals(
				clazz.getName(),
				"com.liferay.portal.dao.orm.hibernate." +
					"VerifySessionFactoryWrapper")) {

			return () -> {
			};
		}

		SessionFactory sessionFactory = ReflectionTestUtil.getFieldValue(
			originalSessionFactory, "_sessionFactoryImpl");

		ReflectionTestUtil.setFieldValue(
			basePersistence, "_sessionFactory", sessionFactory);

		return () -> ReflectionTestUtil.setFieldValue(
			basePersistence, "_sessionFactory", originalSessionFactory);
	}

	private static final String[] _PRIORITIZED_MODEL_CLASS_NAMES = {
		Company.class.getName()
	};

	private static final Set<String> _blacklistedModelClassNames =
		SetUtil.fromArray(
			"com.liferay.portal.security.audit.storage.model.AuditEvent");
	private static final ThreadLocal<Map<String, Map<Serializable, String>>>
		_records = new ThreadLocal<>();
	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.SUPPORTS,
			new Class<?>[] {PortalException.class, SystemException.class});

	private static class RecordingSessionCustomizer
		implements SessionCustomizer {

		@Override
		public Session customize(Session session) {
			return new RecordingSessionWrapper(session, _records);
		}

		private RecordingSessionCustomizer(
			Map<String, Map<Serializable, String>> records) {

			_records = records;
		}

		private final Map<String, Map<Serializable, String>> _records;

	}

	private static class RecordingSessionWrapper extends SessionWrapper {

		@Override
		public void delete(Object object) throws ORMException {
			super.delete(object);

			BaseModel<?> baseModel = (BaseModel<?>)object;

			Map<Serializable, String> map = _records.get(
				baseModel.getModelClassName());

			if (map != null) {
				map.remove(baseModel.getPrimaryKeyObj());
			}
		}

		@Override
		public Serializable save(Object object) throws ORMException {
			_record(object);

			return super.save(object);
		}

		@Override
		public void saveOrUpdate(Object object) throws ORMException {
			_record(object);

			super.saveOrUpdate(object);
		}

		private RecordingSessionWrapper(
			Session session, Map<String, Map<Serializable, String>> records) {

			super(session);

			_records = records;
		}

		private void _record(Object object) {
			BaseModel<?> baseModel = (BaseModel<?>)object;

			if (baseModel.isNew() &&
				!_blacklistedModelClassNames.contains(
					baseModel.getModelClassName())) {

				Map<Serializable, String> map = _records.computeIfAbsent(
					baseModel.getModelClassName(),
					className -> new ConcurrentHashMap<>());

				Thread currentThread = Thread.currentThread();

				UnsyncStringWriter unsyncStringWriter =
					new UnsyncStringWriter();

				unsyncStringWriter.write("Thread name : ");
				unsyncStringWriter.write(currentThread.getName());
				unsyncStringWriter.write(", id : ");
				unsyncStringWriter.write(String.valueOf(currentThread.getId()));
				unsyncStringWriter.write(", created : ");
				unsyncStringWriter.write(baseModel.toString());
				unsyncStringWriter.write(" at \n");

				Exception exception = new Exception();

				exception.printStackTrace(
					new UnsyncPrintWriter(unsyncStringWriter));

				map.put(
					baseModel.getPrimaryKeyObj(),
					unsyncStringWriter.toString());
			}
		}

		private final Map<String, Map<Serializable, String>> _records;

	}

}