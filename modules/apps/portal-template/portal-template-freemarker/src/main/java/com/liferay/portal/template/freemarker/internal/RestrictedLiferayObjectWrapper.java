/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.util.PortalImpl;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.ext.util.ModelFactory;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author Mika Koivisto
 */
public class RestrictedLiferayObjectWrapper extends LiferayObjectWrapper {

	public RestrictedLiferayObjectWrapper(
		String[] allowedClassNames, String[] restrictedClassNames,
		String[] restrictedMethodNames) {

		if (allowedClassNames == null) {
			_allowedClassNames = Collections.emptyList();
		}
		else {
			_allowedClassNames = new ArrayList<>(allowedClassNames.length);

			for (String allowedClassName : allowedClassNames) {
				allowedClassName = StringUtil.trim(allowedClassName);

				if (Validator.isBlank(allowedClassName)) {
					continue;
				}

				_allowedClassNames.add(allowedClassName);
			}
		}

		if (restrictedMethodNames == null) {
			_deniedAccessToStringClasses = Collections.emptySet();
			_restrictedMethodNames = Collections.emptyMap();
		}
		else {
			_deniedAccessToStringClasses = new HashSet<>();
			_restrictedMethodNames = new HashMap<>();

			for (String restrictedMethodName : restrictedMethodNames) {
				int index = restrictedMethodName.indexOf(CharPool.POUND);

				if (index < 0) {
					_log.error(
						StringBundler.concat(
							"\"", restrictedMethodName,
							"\" does not match format ",
							"\"className#methodName\""));

					continue;
				}

				String className = StringUtil.trim(
					restrictedMethodName.substring(0, index));
				String methodName = StringUtil.trim(
					restrictedMethodName.substring(index + 1));

				if (methodName.equals("toString")) {
					_deniedAccessToStringClasses.add(className);
				}

				Set<String> methodNames =
					_restrictedMethodNames.computeIfAbsent(
						className, key -> new HashSet<>());

				methodNames.add(StringUtil.toLowerCase(methodName));
			}
		}

		_allowAllClasses = _allowedClassNames.contains(StringPool.STAR);

		if (restrictedClassNames == null) {
			_restrictedClasses = Collections.emptyList();
			_restrictedPackageNames = Collections.emptyList();
		}
		else {
			_restrictedClasses = new ArrayList<>(restrictedClassNames.length);
			_restrictedPackageNames = new ArrayList<>();

			AggregateClassLoader aggregateClassLoader =
				new AggregateClassLoader(
					LiferayObjectWrapper.class.getClassLoader());

			aggregateClassLoader.addClassLoader(
				PortalImpl.class.getClassLoader());

			Thread thread = Thread.currentThread();

			if (thread.getContextClassLoader() != null) {
				aggregateClassLoader.addClassLoader(
					thread.getContextClassLoader());
			}

			for (String restrictedClassName : restrictedClassNames) {
				restrictedClassName = StringUtil.trim(restrictedClassName);

				if (Validator.isBlank(restrictedClassName)) {
					continue;
				}

				try {
					_restrictedClasses.add(
						aggregateClassLoader.loadClass(restrictedClassName));
				}
				catch (ClassNotFoundException classNotFoundException) {
					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Unable to find restricted class ",
								restrictedClassName,
								". Registering as a package."),
							classNotFoundException);
					}

					if (restrictedClassName.endsWith(StringPool.STAR)) {
						restrictedClassName = restrictedClassName.substring(
							0, restrictedClassName.length() - 1);
					}

					_restrictedPackageNames.add(restrictedClassName);
				}
			}
		}
	}

	@Override
	public TemplateModel wrap(Object object) throws TemplateModelException {
		if (object == null) {
			return null;
		}

		if (object instanceof TemplateModel) {
			return (TemplateModel)object;
		}

		Class<?> clazz = object.getClass();

		if (PropsValues.TEMPLATE_ENGINE_FREEMARKER_COMPANY_RESTRICT &&
			(object instanceof BaseModel) &&
			!CompanyThreadLocal.isInitializingPortalInstance()) {

			long currentCompanyId = CompanyThreadLocal.getCompanyId();

			if (currentCompanyId != CompanyConstants.SYSTEM) {
				BaseModel<?> baseModel = (BaseModel<?>)object;

				Map<String, Function<Object, Object>> getterFunctions =
					(Map<String, Function<Object, Object>>)
						(Map<String, ?>)baseModel.getAttributeGetterFunctions();

				Function<Object, Object> function = getterFunctions.get(
					"companyId");

				if ((function != null) &&
					(currentCompanyId != (Long)function.apply(object))) {

					throw new TemplateModelException(
						StringBundler.concat(
							"Denied access to model object as it does not ",
							"belong to current company ", currentCompanyId));
				}
			}
		}

		if (!_allowAllClasses && _isRestricted(clazz)) {
			return _RESTRICTED_STRING_MODEL_FACTORY.create(object, this);
		}

		String className = clazz.getName();

		if (_restrictedMethodNames.containsKey(className)) {
			LiferayFreeMarkerStringModel liferayFreeMarkerStringModel =
				(LiferayFreeMarkerStringModel)
					_RESTRICTED_STRING_MODEL_FACTORY.create(object, this);

			liferayFreeMarkerStringModel.setDeniedAccessToString(
				_deniedAccessToStringClasses.contains(className));
			liferayFreeMarkerStringModel.setRestrictedMethodNames(
				_restrictedMethodNames.get(className));

			return liferayFreeMarkerStringModel;
		}

		if (_serviceProxyClassNames.contains(className)) {
			return _SERVICE_PROXY_STRING_MODEL_FACTORY.create(object, this);
		}

		if (object instanceof BaseLocalService ||
			object instanceof BaseService) {

			AopInvocationHandler aopInvocationHandler =
				ProxyUtil.fetchInvocationHandler(
					object, AopInvocationHandler.class);

			if (aopInvocationHandler != null) {
				_serviceProxyClassNames.add(className);

				return _SERVICE_PROXY_STRING_MODEL_FACTORY.create(object, this);
			}
		}

		return super.wrap(object);
	}

	private boolean _isRestricted(Class<?> clazz) {
		return _restrictedClassMap.computeIfAbsent(
			clazz.getName(),
			className -> {
				if (_allowedClassNames.contains(className)) {
					return false;
				}

				for (Class<?> restrictedClass : _restrictedClasses) {
					if (!restrictedClass.isAssignableFrom(clazz)) {
						continue;
					}

					return true;
				}

				int index = className.lastIndexOf(StringPool.PERIOD);

				if (index == -1) {
					return false;
				}

				String packageName = className.substring(0, index);

				packageName = packageName.concat(StringPool.PERIOD);

				for (String restrictedPackageName : _restrictedPackageNames) {
					if (!packageName.startsWith(restrictedPackageName)) {
						continue;
					}

					return true;
				}

				return false;
			});
	}

	private static final ModelFactory _RESTRICTED_STRING_MODEL_FACTORY =
		new ModelFactory() {

			@Override
			public TemplateModel create(
				Object object, ObjectWrapper objectWrapper) {

				return new LiferayFreeMarkerStringModel(
					object, (BeansWrapper)objectWrapper);
			}

		};

	private static final ModelFactory _SERVICE_PROXY_STRING_MODEL_FACTORY =
		new ModelFactory() {

			@Override
			public TemplateModel create(
				Object object, ObjectWrapper objectWrapper) {

				Class<?> clazz = object.getClass();

				return new StringModel(
					ProxyUtil.newProxyInstance(
						clazz.getClassLoader(), clazz.getInterfaces(),
						(proxy, method, args) -> TransactionInvokerUtil.invoke(
							_transactionConfig,
							() -> method.invoke(object, args))),
					(BeansWrapper)objectWrapper);
			}

		};

	private static final Log _log = LogFactoryUtil.getLog(
		RestrictedLiferayObjectWrapper.class);

	private static final TransactionConfig _transactionConfig;

	static {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setPropagation(Propagation.REQUIRES_NEW);
		builder.setStrictReadOnly(
			PropsValues.TEMPLATE_ENGINE_FREEMARKER_TRANSACTION_READ_ONLY);
		builder.setRollbackForClasses(Exception.class);

		_transactionConfig = builder.build();
	}

	private final boolean _allowAllClasses;
	private final List<String> _allowedClassNames;
	private final Set<String> _deniedAccessToStringClasses;
	private final List<Class<?>> _restrictedClasses;
	private final Map<String, Boolean> _restrictedClassMap =
		new ConcurrentHashMap<>();
	private final Map<String, Set<String>> _restrictedMethodNames;
	private final List<String> _restrictedPackageNames;
	private final Set<String> _serviceProxyClassNames =
		Collections.newSetFromMap(new ConcurrentHashMap<>());

}