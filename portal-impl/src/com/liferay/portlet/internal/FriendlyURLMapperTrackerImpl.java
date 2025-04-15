/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapperTracker;
import com.liferay.portal.kernel.portlet.Route;
import com.liferay.portal.kernel.portlet.Router;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portlet.RouterImpl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Raymond Augé
 */
public class FriendlyURLMapperTrackerImpl implements FriendlyURLMapperTracker {

	public FriendlyURLMapperTrackerImpl(Portlet portlet) throws Exception {
		_portlet = portlet;
	}

	@Override
	public void close() {
		for (Map.Entry<FriendlyURLMapper, ServiceRegistration<?>> entry :
				_serviceRegistrations.entrySet()) {

			ServiceRegistration<?> serviceRegistration = entry.getValue();

			serviceRegistration.unregister();
		}

		_serviceTrackerDCLSingleton.destroy(ServiceTracker::close);
	}

	@Override
	public FriendlyURLMapper getFriendlyURLMapper() {
		ServiceTracker<FriendlyURLMapper, FriendlyURLMapper> serviceTracker =
			_serviceTrackerDCLSingleton.getSingleton(this::_openServiceTracker);

		return serviceTracker.getService();
	}

	@Override
	public void register(FriendlyURLMapper friendlyURLMapper) {
		ServiceRegistration<?> serviceRegistration =
			_bundleContext.registerService(
				FriendlyURLMapper.class, friendlyURLMapper,
				MapUtil.singletonDictionary(
					"jakarta.portlet.name", _portlet.getPortletId()));

		_serviceRegistrations.put(friendlyURLMapper, serviceRegistration);
	}

	@Override
	public void unregister(FriendlyURLMapper friendlyURLMapper) {
		ServiceRegistration<?> serviceRegistration =
			_serviceRegistrations.remove(friendlyURLMapper);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	/**
	 * @see PortletBagFactory#getContent(String)
	 */
	protected String getContent(ClassLoader classLoader, String fileName)
		throws Exception {

		String queryString = HttpComponentsUtil.getQueryString(fileName);

		if (Validator.isNull(queryString)) {
			return StringUtil.read(classLoader, fileName);
		}

		int pos = fileName.indexOf(StringPool.QUESTION);

		String xml = StringUtil.read(classLoader, fileName.substring(0, pos));

		Map<String, String[]> parameterMap = HttpComponentsUtil.getParameterMap(
			queryString);

		if (parameterMap == null) {
			return xml;
		}

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String[] values = entry.getValue();

			if (values.length == 0) {
				continue;
			}

			String value = values[0];

			xml = StringUtil.replace(xml, "@" + entry.getKey() + "@", value);
		}

		return xml;
	}

	private ServiceTracker<FriendlyURLMapper, FriendlyURLMapper>
		_openServiceTracker() {

		String filterString = null;

		String portletId = _portlet.getPortletId();

		if (portletId.equals(_portlet.getPortletName())) {
			filterString = StringBundler.concat(
				"(&(jakarta.portlet.name=", portletId, ")(objectClass=",
				FriendlyURLMapper.class.getName(), "))");
		}
		else {
			filterString = StringBundler.concat(
				"(&(|(jakarta.portlet.name=", portletId,
				")(jakarta.portlet.name=", _portlet.getPortletName(),
				"))(objectClass=", FriendlyURLMapper.class.getName(), "))");
		}

		ServiceTracker<FriendlyURLMapper, FriendlyURLMapper> serviceTracker =
			new ServiceTracker<>(
				_bundleContext, SystemBundleUtil.createFilter(filterString),
				new FriendlyURLMapperServiceTrackerCustomizer());

		serviceTracker.open();

		return serviceTracker;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FriendlyURLMapperTrackerImpl.class);

	private static final BiFunction<String, String, String>
		_textReplacerBiFunction;

	static {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

		Object instance = null;

		try {
			Class<?> clazz = classLoader.loadClass(
				"com.liferay.portal.tools.jakarta.ee.transformer.function." +
					"TextReplacerBiFunction");

			instance = clazz.newInstance();
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			if (!(reflectiveOperationException instanceof
					ClassNotFoundException)) {

				throw new ExceptionInInitializerError(
					reflectiveOperationException);
			}
		}

		_textReplacerBiFunction = (BiFunction<String, String, String>)instance;
	}

	private final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private final Portlet _portlet;
	private final Map<FriendlyURLMapper, ServiceRegistration<?>>
		_serviceRegistrations = new ConcurrentHashMap<>();
	private final DCLSingleton
		<ServiceTracker<FriendlyURLMapper, FriendlyURLMapper>>
			_serviceTrackerDCLSingleton = new DCLSingleton<>();

	private class FriendlyURLMapperServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<FriendlyURLMapper, FriendlyURLMapper> {

		@Override
		public FriendlyURLMapper addingService(
			ServiceReference<FriendlyURLMapper> serviceReference) {

			FriendlyURLMapper friendlyURLMapper = _bundleContext.getService(
				serviceReference);

			try {
				friendlyURLMapper.setMapping(
					_portlet.getFriendlyURLMapping(false));
				friendlyURLMapper.setPortletId(_portlet.getPortletId());
				friendlyURLMapper.setPortletInstanceable(
					_portlet.isInstanceable());

				String friendlyURLRoutes = (String)serviceReference.getProperty(
					"com.liferay.portlet.friendly-url-routes");

				if (Validator.isNotNull(_portlet.getFriendlyURLRoutes())) {
					friendlyURLRoutes = _portlet.getFriendlyURLRoutes();
				}

				String xml = null;

				if (Validator.isNotNull(friendlyURLRoutes)) {
					Class<?> clazz = friendlyURLMapper.getClass();

					xml = getContent(clazz.getClassLoader(), friendlyURLRoutes);

					if (_textReplacerBiFunction != null) {
						xml = _textReplacerBiFunction.apply(
							clazz.getName() + "#" + friendlyURLRoutes, xml);
					}
				}

				friendlyURLMapper.setRouter(newFriendlyURLRouter(xml));
			}
			catch (Exception exception) {
				_log.error(exception);

				return null;
			}

			return friendlyURLMapper;
		}

		@Override
		public void modifiedService(
			ServiceReference<FriendlyURLMapper> serviceReference,
			FriendlyURLMapper friendlyURLMapper) {
		}

		@Override
		public void removedService(
			ServiceReference<FriendlyURLMapper> serviceReference,
			FriendlyURLMapper friendlyURLMapper) {

			_bundleContext.ungetService(serviceReference);
		}

		protected Router newFriendlyURLRouter(String xml) throws Exception {
			if (Validator.isNull(xml)) {
				return null;
			}

			Document document = UnsecureSAXReaderUtil.read(xml, true);

			Element rootElement = document.getRootElement();

			List<Element> routeElements = rootElement.elements("route");

			Router router = new RouterImpl(routeElements.size());

			for (Element routeElement : routeElements) {
				String pattern = routeElement.elementText("pattern");

				Route route = router.addRoute(pattern);

				for (Element generatedParameterElement :
						routeElement.elements("generated-parameter")) {

					String name = generatedParameterElement.attributeValue(
						"name");
					String value = generatedParameterElement.getText();

					route.addGeneratedParameter(name, value);
				}

				for (Element ignoredParameterElement :
						routeElement.elements("ignored-parameter")) {

					String name = ignoredParameterElement.attributeValue(
						"name");

					route.addIgnoredParameter(name);
				}

				for (Element implicitParameterElement :
						routeElement.elements("implicit-parameter")) {

					String name = implicitParameterElement.attributeValue(
						"name");
					String value = implicitParameterElement.getText();

					route.addImplicitParameter(name, value);
				}

				for (Element overriddenParameterElement :
						routeElement.elements("overridden-parameter")) {

					String name = overriddenParameterElement.attributeValue(
						"name");
					String value = overriddenParameterElement.getText();

					route.addOverriddenParameter(name, value);
				}
			}

			return router;
		}

	}

}