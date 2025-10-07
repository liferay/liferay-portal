/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.bean;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.BeanLocatorException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * @author Brian Wing Shun Chan
 * @author Miguel Pastor
 */
public class BeanLocatorImpl implements BeanLocator {

	public static final String VELOCITY_SUFFIX = ".velocity";

	public BeanLocatorImpl(
		ClassLoader classLoader, ApplicationContext applicationContext) {

		_classLoader = classLoader;
		_applicationContext = applicationContext;
	}

	@Override
	public void destroy() {
		if (_applicationContext instanceof
				AbstractApplicationContext abstractApplicationContext) {

			abstractApplicationContext.close();
		}

		_applicationContext = null;
	}

	public ApplicationContext getApplicationContext() {
		return _applicationContext;
	}

	@Override
	public ClassLoader getClassLoader() {
		return _classLoader;
	}

	@Override
	public String[] getNames() {
		return _applicationContext.getBeanDefinitionNames();
	}

	@Override
	public Class<?> getType(String name) {
		try {
			return _applicationContext.getType(name);
		}
		catch (Exception exception) {
			throw new BeanLocatorException(exception);
		}
	}

	@Override
	public <T> Map<String, T> locate(Class<T> clazz)
		throws BeanLocatorException {

		try {
			return doLocate(clazz);
		}
		catch (SecurityException securityException) {
			throw securityException;
		}
		catch (Exception exception) {
			throw new BeanLocatorException(exception);
		}
	}

	@Override
	public Object locate(String name) throws BeanLocatorException {
		try {
			return doLocate(name);
		}
		catch (SecurityException securityException) {
			throw securityException;
		}
		catch (Exception exception) {
			throw new BeanLocatorException(exception);
		}
	}

	/**
	 * This method ensures the calls stack is the proper length.
	 */
	protected <T> Map<String, T> doLocate(Class<T> clazz) throws Exception {
		return _applicationContext.getBeansOfType(clazz);
	}

	protected Object doLocate(String name) throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("Locating " + name);
		}

		if (name.endsWith(VELOCITY_SUFFIX)) {
			Object velocityBean = _velocityBeans.get(name);

			if (velocityBean == null) {
				String originalName = name.substring(
					0, name.length() - VELOCITY_SUFFIX.length());

				Object curBean = _applicationContext.getBean(originalName);

				velocityBean = ProxyUtil.newProxyInstance(
					_classLoader,
					ReflectionUtil.getInterfaces(curBean, _classLoader),
					new VelocityBeanHandler(curBean, _classLoader));

				_velocityBeans.put(name, velocityBean);
			}

			return velocityBean;
		}

		return _applicationContext.getBean(name);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BeanLocatorImpl.class);

	private ApplicationContext _applicationContext;
	private final ClassLoader _classLoader;
	private final Map<String, Object> _velocityBeans =
		new ConcurrentHashMap<>();

}