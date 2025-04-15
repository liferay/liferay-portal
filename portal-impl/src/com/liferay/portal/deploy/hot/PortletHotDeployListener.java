/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.deploy.hot;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.deploy.hot.BaseHotDeployListener;
import com.liferay.portal.kernel.deploy.hot.HotDeployEvent;
import com.liferay.portal.kernel.deploy.hot.HotDeployException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.model.PortletFilter;
import com.liferay.portal.kernel.model.PortletURLListener;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.CustomUserAttributes;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.PortletInstanceFactoryUtil;
import com.liferay.portal.kernel.resource.bundle.ClassResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.servlet.DirectServletRegistryUtil;
import com.liferay.portal.kernel.servlet.FileTimestampUtil;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.WebAppPool;
import com.liferay.portlet.PortletContextBag;
import com.liferay.portlet.PortletContextBagPool;
import com.liferay.portlet.PortletFilterFactory;
import com.liferay.portlet.PortletURLListenerFactory;

import jakarta.portlet.PortletURLGenerationListener;
import jakarta.portlet.filter.ActionFilter;
import jakarta.portlet.filter.EventFilter;
import jakarta.portlet.filter.RenderFilter;
import jakarta.portlet.filter.ResourceFilter;

import jakarta.servlet.ServletContext;

import java.io.InputStream;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 * @author Ivica Cardic
 * @author Raymond Augé
 */
public class PortletHotDeployListener extends BaseHotDeployListener {

	@Override
	public void invokeDeploy(HotDeployEvent hotDeployEvent)
		throws HotDeployException {

		try {
			doInvokeDeploy(hotDeployEvent);
		}
		catch (Throwable throwable) {
			throwHotDeployException(
				hotDeployEvent, "Error registering portlets for ", throwable);
		}
	}

	@Override
	public void invokeUndeploy(HotDeployEvent hotDeployEvent)
		throws HotDeployException {

		try {
			doInvokeUndeploy(hotDeployEvent);
		}
		catch (Throwable throwable) {
			throwHotDeployException(
				hotDeployEvent, "Error unregistering portlets for ", throwable);
		}
	}

	protected void checkResourceBundles(
		ClassLoader classLoader, Portlet portlet) {

		if (Validator.isNull(portlet.getResourceBundle())) {
			return;
		}

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ResourceBundleLoader resourceBundleLoader =
			new ClassResourceBundleLoader(
				portlet.getResourceBundle(), classLoader);

		_resourceBundleLoaderServiceRegistrations.put(
			portlet.getPortletId(),
			bundleContext.registerService(
				ResourceBundleLoader.class, resourceBundleLoader,
				HashMapDictionaryBuilder.<String, Object>put(
					"resource.bundle.base.name", portlet.getResourceBundle()
				).put(
					"service.ranking", Integer.MIN_VALUE
				).put(
					"servlet.context.name", portlet.getContextName()
				).build()));
	}

	protected void doInvokeDeploy(HotDeployEvent hotDeployEvent)
		throws Exception {

		ServletContext servletContext = hotDeployEvent.getServletContext();

		List<String> beanFilterNames =
			(List<String>)servletContext.getAttribute(
				WebKeys.BEAN_FILTER_NAMES);
		List<String> beanPortletIds = (List<String>)servletContext.getAttribute(
			WebKeys.BEAN_PORTLET_IDS);

		if ((beanFilterNames != null) || (beanPortletIds != null)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Detected bean portlet from servlet context attributes");
			}

			return;
		}

		try (InputStream inputStream = servletContext.getResourceAsStream(
				"/META-INF/MANIFEST.MF")) {

			if (inputStream != null) {
				Manifest manifest = new Manifest(inputStream);

				Attributes mainAttributes = manifest.getMainAttributes();

				String value = mainAttributes.getValue("Require-Capability");

				if ((value != null) && value.contains("osgi.cdi.extension")) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Detected bean portlet from /META-INF/MANIFEST.MF");
					}

					return;
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		String servletContextName = servletContext.getServletContextName();

		if (_log.isDebugEnabled()) {
			_log.debug("Invoking deploy for " + servletContextName);
		}

		String[] xmls = {
			StreamUtil.toString(
				servletContext.getResourceAsStream(
					"/WEB-INF/" + Portal.PORTLET_XML_FILE_NAME_STANDARD)),
			StreamUtil.toString(
				servletContext.getResourceAsStream(
					"/WEB-INF/" + Portal.PORTLET_XML_FILE_NAME_CUSTOM)),
			StreamUtil.toString(
				servletContext.getResourceAsStream(
					"/WEB-INF/liferay-portlet.xml")),
			StreamUtil.toString(
				servletContext.getResourceAsStream("/WEB-INF/web.xml"))
		};

		if ((xmls[0] == null) && (xmls[1] == null)) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Registering portlets for " + servletContextName);
		}

		PortletContextBag portletContextBag = new PortletContextBag(
			servletContextName);

		PortletContextBagPool.put(servletContextName, portletContextBag);

		List<Portlet> portlets = PortletLocalServiceUtil.initWAR(
			servletContextName, servletContext, xmls,
			hotDeployEvent.getPluginPackage());

		boolean portletAppInitialized = false;

		ClassLoader classLoader = hotDeployEvent.getContextClassLoader();

		Iterator<Portlet> iterator = portlets.iterator();

		while (iterator.hasNext()) {
			Portlet portlet = iterator.next();

			if (!portletAppInitialized) {
				initPortletApp(
					servletContextName, servletContext, classLoader, portlet);

				portletAppInitialized = true;
			}
		}

		String xml = StreamUtil.toString(
			servletContext.getResourceAsStream("/WEB-INF/liferay-display.xml"));

		PortletCategory newPortletCategory =
			PortletLocalServiceUtil.getWARDisplay(servletContextName, xml);

		CompanyLocalServiceUtil.forEachCompanyId(
			companyId -> {
				PortletCategory portletCategory =
					(PortletCategory)WebAppPool.get(
						companyId, WebKeys.PORTLET_CATEGORY);

				if (portletCategory != null) {
					portletCategory.merge(newPortletCategory);
				}
				else {
					_log.error(
						"Unable to register portlet for company " + companyId +
							" because it does not exist");
				}
			});

		String[] sources = _processPortletProperties(classLoader);

		ResourceActionsUtil.populateModelResources(classLoader, sources);

		for (Portlet portlet : portlets) {
			ResourceActionsUtil.populatePortletResource(
				portlet, classLoader, sources);

			checkResourceBundles(classLoader, portlet);

			CompanyLocalServiceUtil.forEachCompanyId(
				companyId -> {
					Portlet companyPortlet = (Portlet)portlet.clone();

					companyPortlet.setCompanyId(companyId);

					PortletLocalServiceUtil.checkPortlet(companyPortlet);
				});
		}

		for (Portlet portlet : portlets) {
			portlet.setReady(
				GetterUtil.getBoolean(
					servletContext.getInitParameter(
						"portlets-ready-by-default"),
					true));
		}

		DirectServletRegistryUtil.clearServlets();
		FileTimestampUtil.reset(servletContext);

		_portlets.put(servletContextName, portlets);

		servletContext.setAttribute(WebKeys.PLUGIN_PORTLETS, portlets);

		if (_log.isInfoEnabled()) {
			if (portlets.size() == 1) {
				_log.info(
					"1 portlet for " + servletContextName +
						" is available for use");
			}
			else {
				_log.info(
					StringBundler.concat(
						portlets.size(), " portlets for ", servletContextName,
						" are available for use"));
			}
		}
	}

	protected void doInvokeUndeploy(HotDeployEvent hotDeployEvent)
		throws Exception {

		ServletContext servletContext = hotDeployEvent.getServletContext();

		String servletContextName = servletContext.getServletContextName();

		if (_log.isDebugEnabled()) {
			_log.debug("Invoking undeploy for " + servletContextName);
		}

		List<Portlet> portlets = _portlets.remove(servletContextName);

		if (portlets == null) {
			return;
		}

		Set<String> portletIds = new HashSet<>();

		if (_log.isDebugEnabled()) {
			_log.debug("Unregistering portlets for " + servletContextName);
		}

		for (Portlet portlet : portlets) {
			_destroyPortlet(portlet, portletIds);
		}

		ServletContextPool.remove(servletContextName);

		if (!portletIds.isEmpty()) {
			CompanyLocalServiceUtil.forEachCompanyId(
				companyId -> {
					PortletCategory portletCategory =
						(PortletCategory)WebAppPool.get(
							companyId, WebKeys.PORTLET_CATEGORY);

					portletCategory.separate(portletIds);
				});
		}

		PortletContextBagPool.remove(servletContextName);

		DirectServletRegistryUtil.clearServlets();

		if (_log.isInfoEnabled()) {
			if (portlets.size() == 1) {
				_log.info(
					"1 portlet for " + servletContextName +
						" was unregistered");
			}
			else {
				_log.info(
					StringBundler.concat(
						portlets.size(), " portlets for ", servletContextName,
						" were unregistered"));
			}
		}
	}

	protected void initPortletApp(
			String servletContextName, ServletContext servletContext,
			ClassLoader classLoader, Portlet portlet)
		throws Exception {

		PortletContextBag portletContextBag = PortletContextBagPool.get(
			servletContextName);

		PortletApp portletApp = portlet.getPortletApp();

		servletContext.setAttribute(PortletServlet.PORTLET_APP, portletApp);

		Map<String, String> customUserAttributes1 =
			portletApp.getCustomUserAttributes();

		for (Map.Entry<String, String> entry :
				customUserAttributes1.entrySet()) {

			String attrCustomClass = entry.getValue();

			Class<?> clazz = classLoader.loadClass(attrCustomClass);

			CustomUserAttributes customUserAttributes2 =
				(CustomUserAttributes)clazz.newInstance();

			Map<String, CustomUserAttributes> customUserAttributesMap =
				portletContextBag.getCustomUserAttributes();

			customUserAttributesMap.put(attrCustomClass, customUserAttributes2);
		}

		Set<PortletFilter> portletFilters = portletApp.getPortletFilters();

		for (PortletFilter portletFilter : portletFilters) {
			jakarta.portlet.filter.PortletFilter portletFilterInstance =
				(jakarta.portlet.filter.PortletFilter)newInstance(
					classLoader,
					new Class<?>[] {
						ActionFilter.class, EventFilter.class,
						jakarta.portlet.filter.PortletFilter.class,
						RenderFilter.class, ResourceFilter.class
					},
					portletFilter.getFilterClass());

			Map<String, jakarta.portlet.filter.PortletFilter>
				portletFiltersMap = portletContextBag.getPortletFilters();

			portletFiltersMap.put(
				portletFilter.getFilterName(), portletFilterInstance);
		}

		InvokerPortlet invokerPortlet = PortletInstanceFactoryUtil.create(
			portlet, servletContext);

		invokerPortlet.setPortletFilters();

		Set<PortletURLListener> portletURLListeners =
			portletApp.getPortletURLListeners();

		for (PortletURLListener portletURLListener : portletURLListeners) {
			PortletURLGenerationListener portletURLListenerInstance =
				(PortletURLGenerationListener)newInstance(
					classLoader, PortletURLGenerationListener.class,
					portletURLListener.getListenerClass());

			Map<String, PortletURLGenerationListener> portletURLListenersMap =
				portletContextBag.getPortletURLListeners();

			portletURLListenersMap.put(
				portletURLListener.getListenerClass(),
				portletURLListenerInstance);

			PortletURLListenerFactory.create(portletURLListener);
		}
	}

	protected void unbindDataSource(String servletContextName) {
		Boolean dataSourceBindState = _dataSourceBindStates.remove(
			servletContextName);

		if (dataSourceBindState == null) {
			return;
		}

		try {
			if (_log.isDebugEnabled()) {
				_log.debug("Dynamically unbinding the Liferay data source");
			}

			Context context = new InitialContext();

			try {
				context.lookup(_JNDI_JDBC_LIFERAY_POOL);

				context.unbind(_JNDI_JDBC_LIFERAY_POOL);
			}
			catch (NamingException namingException) {
				if (_log.isDebugEnabled()) {
					_log.debug(namingException);
				}
			}

			try {
				context.lookup(_JNDI_JDBC);

				context.destroySubcontext(_JNDI_JDBC);
			}
			catch (NamingException namingException) {
				if (_log.isDebugEnabled()) {
					_log.debug(namingException);
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to dynamically unbind the Liferay data source: " +
						exception.getMessage());
			}
		}
	}

	private void _destroyPortlet(Portlet portlet, Set<String> portletIds)
		throws Exception {

		portlet.unsetReady();

		PortletApp portletApp = portlet.getPortletApp();

		Set<PortletFilter> portletFilters = portletApp.getPortletFilters();

		for (PortletFilter portletFilter : portletFilters) {
			PortletFilterFactory.destroy(portletFilter);
		}

		Set<PortletURLListener> portletURLListeners =
			portletApp.getPortletURLListeners();

		for (PortletURLListener portletURLListener : portletURLListeners) {
			PortletURLListenerFactory.destroy(portletURLListener);
		}

		PortletInstanceFactoryUtil.destroy(portlet);

		portletIds.add(portlet.getPortletId());

		ServiceRegistration<ResourceBundleLoader>
			resourceBundleLoaderServiceRegistration =
				_resourceBundleLoaderServiceRegistrations.remove(
					portlet.getPortletId());

		if (resourceBundleLoaderServiceRegistration != null) {
			resourceBundleLoaderServiceRegistration.unregister();
		}
	}

	private String[] _processPortletProperties(ClassLoader classLoader) {
		Configuration portletPropertiesConfiguration =
			ConfigurationFactoryUtil.getConfiguration(classLoader, "portlet");

		if (portletPropertiesConfiguration == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to read portlet.properties");
			}

			return new String[0];
		}

		Properties portletProperties =
			portletPropertiesConfiguration.getProperties();

		return StringUtil.split(
			portletProperties.getProperty(PropsKeys.RESOURCE_ACTIONS_CONFIGS));
	}

	private static final String _JNDI_JDBC = "java_liferay:jdbc";

	private static final String _JNDI_JDBC_LIFERAY_POOL =
		_JNDI_JDBC + "/LiferayPool";

	private static final Log _log = LogFactoryUtil.getLog(
		PortletHotDeployListener.class);

	private static final Map<String, Boolean> _dataSourceBindStates =
		new HashMap<>();
	private static final Map<String, List<Portlet>> _portlets = new HashMap<>();

	private final Map<String, ServiceRegistration<ResourceBundleLoader>>
		_resourceBundleLoaderServiceRegistrations = new HashMap<>();

}