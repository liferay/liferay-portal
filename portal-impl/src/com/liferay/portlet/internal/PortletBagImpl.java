/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.expando.kernel.model.CustomAttributesDisplay;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.pop.MessageListener;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.portlet.FriendlyURLMapperTracker;
import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletConfigurationListener;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.OpenSearch;
import com.liferay.portal.kernel.security.permission.propagator.PermissionPropagator;
import com.liferay.portal.kernel.servlet.URLEncoder;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.webdav.WebDAVStorage;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.xmlrpc.Method;
import com.liferay.portal.language.LanguageResources;
import com.liferay.social.kernel.model.SocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialRequestInterpreter;

import jakarta.portlet.Portlet;
import jakarta.portlet.PreferencesValidator;

import jakarta.servlet.ServletContext;

import java.util.Dictionary;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
public class PortletBagImpl implements PortletBag {

	public PortletBagImpl(
		String portletName, ServletContext servletContext,
		Portlet portletInstance, String resourceBundleBaseName,
		FriendlyURLMapperTracker friendlyURLMapperTracker,
		List<ServiceRegistration<?>> serviceRegistrations) {

		_portletName = portletName;
		_servletContext = servletContext;
		_portletInstance = portletInstance;
		_resourceBundleBaseName = resourceBundleBaseName;
		_friendlyURLMapperTracker = friendlyURLMapperTracker;
		_serviceRegistrations = serviceRegistrations;

		_filterString =
			"(|(jakarta.portlet.name=" + portletName +
				")(jakarta.portlet.name=ALL))";
		_resourceBundleLoaderSnapshot = new Snapshot<>(
			PortletBagImpl.class, ResourceBundleLoader.class,
			StringBundler.concat(
				"(&(resource.bundle.base.name=", getResourceBundleBaseName(),
				")(servlet.context.name=",
				servletContext.getServletContextName(), "))"),
			true);

		_configurationActionSnapshot = new Snapshot<>(
			PortletBagImpl.class, ConfigurationAction.class, _filterString,
			true);
		_controlPanelEntrySnapshot = new Snapshot<>(
			PortletBagImpl.class, ControlPanelEntry.class, _filterString, true);
		_methodSnapshot = new Snapshot<>(
			PortletBagImpl.class, Method.class, _filterString, true);
		_messageListenerSnapshot = new Snapshot<>(
			PortletBagImpl.class, MessageListener.class, _filterString, true);
		_openSearchSnapshot = new Snapshot<>(
			PortletBagImpl.class, OpenSearch.class, _filterString, true);
		_permissionPropagatorSnapshot = new Snapshot<>(
			PortletBagImpl.class, PermissionPropagator.class, _filterString,
			true);
		_portletConfigurationListenerSnapshot = new Snapshot<>(
			PortletBagImpl.class, PortletConfigurationListener.class,
			_filterString, true);
		_portletDataHandlerSnapshot = new Snapshot<>(
			PortletBagImpl.class, PortletDataHandler.class, _filterString,
			true);
		_portletLayoutListenerSnapshot = new Snapshot<>(
			PortletBagImpl.class, PortletLayoutListener.class, _filterString,
			true);
		_preferencesValidatorSnapshot = new Snapshot<>(
			PortletBagImpl.class, PreferencesValidator.class, _filterString,
			true);
		_socialRequestInterpreterSnapshot = new Snapshot<>(
			PortletBagImpl.class, SocialRequestInterpreter.class, _filterString,
			true);
		_templateHandlerSnapshot = new Snapshot<>(
			PortletBagImpl.class, TemplateHandler.class, _filterString, true);
		_urlEncoderSnapshot = new Snapshot<>(
			PortletBagImpl.class, URLEncoder.class, _filterString, true);
		_webDAVStorageSnapshot = new Snapshot<>(
			PortletBagImpl.class, WebDAVStorage.class, _filterString, true);
	}

	@Override
	public Object clone() {
		return new PortletBagImpl(
			getPortletName(), getServletContext(), getPortletInstance(),
			getResourceBundleBaseName(), getFriendlyURLMapperTracker(), null);
	}

	@Override
	public void destroy() {
		if (_serviceRegistrations == null) {
			return;
		}

		_friendlyURLMapperTracker.close();

		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();

		for (ServiceTrackerList<?> serviceTrackerList :
				_serviceTrackerListMap.values()) {

			serviceTrackerList.close();
		}
	}

	@Override
	public ConfigurationAction getConfigurationActionInstance() {
		return _configurationActionSnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getConfigurationActionInstance()}
	 */
	@Deprecated
	@Override
	public List<ConfigurationAction> getConfigurationActionInstances() {
		return _getList(ConfigurationAction.class);
	}

	@Override
	public ControlPanelEntry getControlPanelEntryInstance() {
		return _controlPanelEntrySnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getControlPanelEntryInstance()}
	 */
	@Deprecated
	@Override
	public List<ControlPanelEntry> getControlPanelEntryInstances() {
		return _getList(ControlPanelEntry.class);
	}

	@Override
	public List<CustomAttributesDisplay> getCustomAttributesDisplayInstances() {
		return _getList(CustomAttributesDisplay.class);
	}

	@Override
	public FriendlyURLMapperTracker getFriendlyURLMapperTracker() {
		return _friendlyURLMapperTracker;
	}

	@Override
	public List<Indexer<?>> getIndexerInstances() {
		return _getList(Indexer.class);
	}

	@Override
	public OpenSearch getOpenSearchInstance() {
		return _openSearchSnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getOpenSearchInstance()}
	 */
	@Deprecated
	@Override
	public List<OpenSearch> getOpenSearchInstances() {
		return _getList(OpenSearch.class);
	}

	@Override
	public PermissionPropagator getPermissionPropagatorInstance() {
		return _permissionPropagatorSnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getPermissionPropagatorInstance()}
	 */
	@Deprecated
	@Override
	public List<PermissionPropagator> getPermissionPropagatorInstances() {
		return _getList(PermissionPropagator.class);
	}

	@Override
	public MessageListener getPopMessageListenerInstance() {
		return _messageListenerSnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getPopMessageListenerInstance()}
	 */
	@Deprecated
	@Override
	public List<MessageListener> getPopMessageListenerInstances() {
		return _getList(MessageListener.class);
	}

	@Override
	public PortletConfigurationListener
		getPortletConfigurationListenerInstance() {

		return _portletConfigurationListenerSnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getPortletConfigurationListenerInstance()}
	 */
	@Deprecated
	@Override
	public List<PortletConfigurationListener>
		getPortletConfigurationListenerInstances() {

		return _getList(PortletConfigurationListener.class);
	}

	@Override
	public PortletDataHandler getPortletDataHandlerInstance() {
		return _portletDataHandlerSnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getPortletDataHandlerInstance()}
	 */
	@Deprecated
	@Override
	public List<PortletDataHandler> getPortletDataHandlerInstances() {
		return _getList(PortletDataHandler.class);
	}

	@Override
	public Portlet getPortletInstance() {
		return _portletInstance;
	}

	@Override
	public PortletLayoutListener getPortletLayoutListenerInstance() {
		return _portletLayoutListenerSnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getPortletLayoutListenerInstance()}
	 */
	@Deprecated
	@Override
	public List<PortletLayoutListener> getPortletLayoutListenerInstances() {
		return _getList(PortletLayoutListener.class);
	}

	@Override
	public String getPortletName() {
		return _portletName;
	}

	@Override
	public PreferencesValidator getPreferencesValidatorInstance() {
		return _preferencesValidatorSnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getPreferencesValidatorInstance()}
	 */
	@Deprecated
	@Override
	public List<PreferencesValidator> getPreferencesValidatorInstances() {
		return _getList(PreferencesValidator.class);
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundleLoader resourceBundleLoader =
			_resourceBundleLoaderSnapshot.get();

		if (resourceBundleLoader == null) {
			return LanguageResources.getResourceBundle(locale);
		}

		ResourceBundle resourceBundle = resourceBundleLoader.loadResourceBundle(
			locale);

		if (resourceBundle == null) {
			resourceBundle = LanguageResources.getResourceBundle(locale);
		}

		return resourceBundle;
	}

	@Override
	public String getResourceBundleBaseName() {
		return _resourceBundleBaseName;
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public List<SocialActivityInterpreter>
		getSocialActivityInterpreterInstances() {

		return _getList(SocialActivityInterpreter.class);
	}

	@Override
	public SocialRequestInterpreter getSocialRequestInterpreterInstance() {
		return _socialRequestInterpreterSnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getSocialRequestInterpreterInstance()}
	 */
	@Deprecated
	@Override
	public List<SocialRequestInterpreter>
		getSocialRequestInterpreterInstances() {

		return _getList(SocialRequestInterpreter.class);
	}

	@Override
	public List<StagedModelDataHandler<?>>
		getStagedModelDataHandlerInstances() {

		return _getList(StagedModelDataHandler.class);
	}

	@Override
	public TemplateHandler getTemplateHandlerInstance() {
		return _templateHandlerSnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getTemplateHandlerInstance()}
	 */
	@Deprecated
	@Override
	public List<TemplateHandler> getTemplateHandlerInstances() {
		return _getList(TemplateHandler.class);
	}

	@Override
	public List<TrashHandler> getTrashHandlerInstances() {
		return _getList(TrashHandler.class);
	}

	@Override
	public URLEncoder getURLEncoderInstance() {
		return _urlEncoderSnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getURLEncoderInstance()}
	 */
	@Deprecated
	@Override
	public List<URLEncoder> getURLEncoderInstances() {
		return _getList(URLEncoder.class);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public List<UserNotificationDefinition>
		getUserNotificationDefinitionInstances() {

		return _getList(UserNotificationDefinition.class);
	}

	@Override
	public List<UserNotificationHandler> getUserNotificationHandlerInstances() {
		return _getList(UserNotificationHandler.class);
	}

	@Override
	public WebDAVStorage getWebDAVStorageInstance() {
		return _webDAVStorageSnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getWebDAVStorageInstance()}
	 */
	@Deprecated
	@Override
	public List<WebDAVStorage> getWebDAVStorageInstances() {
		return _getList(WebDAVStorage.class);
	}

	@Override
	public List<WorkflowHandler<?>> getWorkflowHandlerInstances() {
		return _getList(WorkflowHandler.class);
	}

	@Override
	public Method getXmlRpcMethodInstance() {
		return _methodSnapshot.get();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getXmlRpcMethodInstance()}
	 */
	@Deprecated
	@Override
	public List<Method> getXmlRpcMethodInstances() {
		return _getList(Method.class);
	}

	@Override
	public void setPortletInstance(Portlet portletInstance) {
		_portletInstance = portletInstance;
	}

	@Override
	public void setPortletName(String portletName) {
		_portletName = portletName;
	}

	private final <T> List<T> _getList(Class<?> clazz) {
		ServiceTrackerList<Class<?>> serviceTrackerList =
			_serviceTrackerListMap.computeIfAbsent(
				clazz,
				key ->
					(ServiceTrackerList<Class<?>>)
						(ServiceTrackerList)ServiceTrackerListFactory.open(
							_bundleContext, clazz, _filterString));

		return (List<T>)serviceTrackerList.toList();
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private final Snapshot<ConfigurationAction> _configurationActionSnapshot;
	private final Snapshot<ControlPanelEntry> _controlPanelEntrySnapshot;
	private final String _filterString;
	private final FriendlyURLMapperTracker _friendlyURLMapperTracker;
	private final Snapshot<MessageListener> _messageListenerSnapshot;
	private final Snapshot<Method> _methodSnapshot;
	private final Snapshot<OpenSearch> _openSearchSnapshot;
	private final Snapshot<PermissionPropagator> _permissionPropagatorSnapshot;
	private final Snapshot<PortletConfigurationListener>
		_portletConfigurationListenerSnapshot;
	private final Snapshot<PortletDataHandler> _portletDataHandlerSnapshot;
	private Portlet _portletInstance;
	private final Snapshot<PortletLayoutListener>
		_portletLayoutListenerSnapshot;
	private String _portletName;
	private final Snapshot<PreferencesValidator> _preferencesValidatorSnapshot;
	private final String _resourceBundleBaseName;
	private final Snapshot<ResourceBundleLoader> _resourceBundleLoaderSnapshot;
	private final List<ServiceRegistration<?>> _serviceRegistrations;
	private final Map<Class<?>, ServiceTrackerList<Class<?>>>
		_serviceTrackerListMap = new ConcurrentHashMap<>();
	private final ServletContext _servletContext;
	private final Snapshot<SocialRequestInterpreter>
		_socialRequestInterpreterSnapshot;
	private final Snapshot<TemplateHandler> _templateHandlerSnapshot;
	private final Snapshot<URLEncoder> _urlEncoderSnapshot;
	private final Snapshot<WebDAVStorage> _webDAVStorageSnapshot;

	@SuppressWarnings("deprecation")
	private static class PermissionPropagatorServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<com.liferay.portal.kernel.security.permission.PermissionPropagator,
			 ServiceRegistration<PermissionPropagator>> {

		@Override
		public ServiceRegistration<PermissionPropagator> addingService(
			ServiceReference
				<com.liferay.portal.kernel.security.permission.
					PermissionPropagator> serviceReference) {

			return _bundleContext.registerService(
				PermissionPropagator.class,
				_bundleContext.getService(serviceReference),
				_toProperties(serviceReference));
		}

		@Override
		public void modifiedService(
			ServiceReference
				<com.liferay.portal.kernel.security.permission.
					PermissionPropagator> serviceReference,
			ServiceRegistration<PermissionPropagator> serviceRegistration) {

			serviceRegistration.setProperties(_toProperties(serviceReference));
		}

		@Override
		public void removedService(
			ServiceReference
				<com.liferay.portal.kernel.security.permission.
					PermissionPropagator> serviceReference,
			ServiceRegistration<PermissionPropagator> serviceRegistration) {

			serviceRegistration.unregister();

			_bundleContext.ungetService(serviceReference);
		}

		private Dictionary<String, Object> _toProperties(
			ServiceReference<?> serviceReference) {

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			for (String key : serviceReference.getPropertyKeys()) {
				Object value = serviceReference.getProperty(key);

				properties.put(key, value);
			}

			return properties;
		}

	}

	static {
		ServiceTracker<?, ?> serviceTracker = new ServiceTracker<>(
			_bundleContext,
			com.liferay.portal.kernel.security.permission.PermissionPropagator.
				class,
			new PermissionPropagatorServiceTrackerCustomizer());

		serviceTracker.open();
	}

}