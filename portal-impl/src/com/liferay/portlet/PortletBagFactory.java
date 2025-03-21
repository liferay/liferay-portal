/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.expando.kernel.model.CustomAttributesDisplay;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.pop.MessageListener;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapperTracker;
import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.portlet.PortletConfigurationListener;
import com.liferay.portal.kernel.portlet.PortletInstanceFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.scheduler.SchedulerEntry;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.OpenSearch;
import com.liferay.portal.kernel.security.permission.propagator.PermissionPropagator;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.servlet.URLEncoder;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webdav.WebDAVStorage;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.kernel.xmlrpc.Method;
import com.liferay.portal.notifications.UserNotificationHandlerImpl;
import com.liferay.portal.util.JavaFieldsParser;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.internal.FriendlyURLMapperTrackerImpl;
import com.liferay.portlet.internal.PortletBagImpl;
import com.liferay.social.kernel.model.SocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialRequestInterpreter;
import com.liferay.social.kernel.model.impl.SocialActivityInterpreterImpl;
import com.liferay.social.kernel.model.impl.SocialRequestInterpreterImpl;

import jakarta.portlet.PreferencesValidator;

import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 * @author Ivica Cardic
 * @author Raymond Augé
 */
public class PortletBagFactory {

	public PortletBag create(Portlet portlet) throws Exception {
		return create(portlet, false);
	}

	public PortletBag create(Portlet portlet, boolean destroyPrevious)
		throws Exception {

		_validate();

		jakarta.portlet.Portlet portletInstance = _getPortletInstance(portlet);

		return create(portlet, portletInstance, destroyPrevious);
	}

	public PortletBag create(
			Portlet portlet, jakarta.portlet.Portlet portletInstance,
			boolean destroyPrevious)
		throws Exception {

		_validate();

		Dictionary<String, Object> properties = MapUtil.singletonDictionary(
			"jakarta.portlet.name", portlet.getPortletName());

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();

		_registerConfigurationActions(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerIndexers(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerOpenSearches(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerSchedulerJobConfigurations(
			bundleContext, portlet, properties, serviceRegistrations);

		FriendlyURLMapperTracker friendlyURLMapperTracker =
			_registerFriendlyURLMappers(portlet);

		_registerURLEncoders(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerPortletDataHandlers(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerStagedModelDataHandler(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerTemplateHandlers(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerPortletConfigurationListeners(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerPortletLayoutListeners(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerPOPMessageListeners(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerSocialActivityInterpreterInstances(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerSocialRequestInterpreterInstances(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerUserNotificationDefinitionInstances(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerUserNotificationHandlerInstances(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerWebDAVStorageInstances(bundleContext, portlet);

		_registerXmlRpcMethodInstances(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerControlPanelEntryInstances(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerAssetRendererFactoryInstances(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerCustomAttributesDisplayInstances(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerPermissionPropagators(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerTrashHandlerInstances(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerWorkflowHandlerInstances(
			bundleContext, portlet, properties, serviceRegistrations);

		_registerPreferencesValidatorInstances(
			bundleContext, portlet, properties, serviceRegistrations);

		PortletBag portletBag = new PortletBagImpl(
			portlet.getPortletName(), _servletContext, portletInstance,
			portlet.getResourceBundle(), friendlyURLMapperTracker,
			serviceRegistrations);

		PortletBagPool.put(portlet.getRootPortletId(), portletBag);

		try {
			PortletInstanceFactoryUtil.create(
				portlet, _servletContext, destroyPrevious);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return portletBag;
	}

	public void setClassLoader(ClassLoader classLoader) {
		_classLoader = classLoader;
	}

	public void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	public void setWARFile(boolean warFile) {
		_warFile = warFile;
	}

	/**
	 * @see FriendlyURLMapperTrackerImpl#getContent(ClassLoader, String)
	 */
	private String _getContent(String fileName) throws Exception {
		String queryString = HttpComponentsUtil.getQueryString(fileName);

		if (Validator.isNull(queryString)) {
			return StringUtil.read(_classLoader, fileName);
		}

		int pos = fileName.indexOf(StringPool.QUESTION);

		String xml = StringUtil.read(_classLoader, fileName.substring(0, pos));

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

	private String _getPluginPropertyValue(String propertyKey)
		throws Exception {

		if (_configuration == null) {
			_configuration = ConfigurationFactoryUtil.getConfiguration(
				_classLoader, "portlet");
		}

		return _configuration.get(propertyKey);
	}

	private jakarta.portlet.Portlet _getPortletInstance(Portlet portlet)
		throws Exception {

		Class<?> portletClass = null;

		try {
			portletClass = _classLoader.loadClass(portlet.getPortletClass());
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);

			PortletLocalServiceUtil.destroyPortlet(portlet);

			return null;
		}

		return (jakarta.portlet.Portlet)portletClass.newInstance();
	}

	private <T> T _newInstance(
			Class<? extends T> interfaceClass, String implClassName)
		throws Exception {

		if (_warFile) {
			return (T)ProxyFactory.newInstance(
				_classLoader, new Class<?>[] {interfaceClass}, implClassName);
		}

		Class<?> clazz = _classLoader.loadClass(implClassName);

		return (T)clazz.newInstance();
	}

	private void _registerAssetRendererFactoryInstances(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		for (String assetRendererFactoryClass :
				portlet.getAssetRendererFactoryClasses()) {

			String assetRendererEnabledPropertyKey =
				PropsKeys.ASSET_RENDERER_ENABLED + assetRendererFactoryClass;

			String assetRendererEnabledPropertyValue = null;

			if (_warFile) {
				assetRendererEnabledPropertyValue = _getPluginPropertyValue(
					assetRendererEnabledPropertyKey);
			}
			else {
				assetRendererEnabledPropertyValue = PropsUtil.get(
					assetRendererEnabledPropertyKey);
			}

			boolean assetRendererEnabledValue = GetterUtil.getBoolean(
				assetRendererEnabledPropertyValue, true);

			if (assetRendererEnabledValue) {
				AssetRendererFactory<?> assetRendererFactory = _newInstance(
					AssetRendererFactory.class, assetRendererFactoryClass);

				assetRendererFactory.setClassName(
					assetRendererFactory.getClassName());
				assetRendererFactory.setPortletId(portlet.getPortletId());

				ServiceRegistration<?> serviceRegistration =
					bundleContext.registerService(
						AssetRendererFactory.class, assetRendererFactory,
						properties);

				serviceRegistrations.add(serviceRegistration);
			}
		}
	}

	private void _registerConfigurationActions(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNotNull(portlet.getConfigurationActionClass())) {
			ConfigurationAction configurationAction = _newInstance(
				ConfigurationAction.class,
				portlet.getConfigurationActionClass());

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					ConfigurationAction.class, configurationAction, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerControlPanelEntryInstances(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNotNull(portlet.getControlPanelEntryClass())) {
			ControlPanelEntry controlPanelEntryInstance = _newInstance(
				ControlPanelEntry.class, portlet.getControlPanelEntryClass());

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					ControlPanelEntry.class, controlPanelEntryInstance,
					properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerCustomAttributesDisplayInstances(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		for (String customAttributesDisplayClass :
				portlet.getCustomAttributesDisplayClasses()) {

			CustomAttributesDisplay customAttributesDisplayInstance =
				_newInstance(
					CustomAttributesDisplay.class,
					customAttributesDisplayClass);

			customAttributesDisplayInstance.setClassNameId(
				PortalUtil.getClassNameId(
					customAttributesDisplayInstance.getClassName()));
			customAttributesDisplayInstance.setPortletId(
				portlet.getPortletId());

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					CustomAttributesDisplay.class,
					customAttributesDisplayInstance, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private FriendlyURLMapperTracker _registerFriendlyURLMappers(
			Portlet portlet)
		throws Exception {

		FriendlyURLMapperTracker friendlyURLMapperTracker =
			new FriendlyURLMapperTrackerImpl(portlet);

		if (Validator.isNotNull(portlet.getFriendlyURLMapperClass())) {
			FriendlyURLMapper friendlyURLMapper = _newInstance(
				FriendlyURLMapper.class, portlet.getFriendlyURLMapperClass());

			friendlyURLMapperTracker.register(friendlyURLMapper);
		}

		return friendlyURLMapperTracker;
	}

	private void _registerIndexers(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		for (String indexerClass : portlet.getIndexerClasses()) {
			Indexer<?> indexerInstance = _newInstance(
				Indexer.class, indexerClass);

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					Indexer.class, indexerInstance, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerOpenSearches(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNotNull(portlet.getOpenSearchClass())) {
			OpenSearch openSearch = _newInstance(
				OpenSearch.class, portlet.getOpenSearchClass());

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					OpenSearch.class, openSearch, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerPermissionPropagators(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNotNull(portlet.getPermissionPropagatorClass())) {
			PermissionPropagator permissionPropagatorInstance = _newInstance(
				PermissionPropagator.class,
				portlet.getPermissionPropagatorClass());

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					PermissionPropagator.class, permissionPropagatorInstance,
					properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerPOPMessageListeners(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNotNull(portlet.getPopMessageListenerClass())) {
			MessageListener popMessageListenerInstance = _newInstance(
				MessageListener.class, portlet.getPopMessageListenerClass());

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					MessageListener.class, popMessageListenerInstance,
					properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerPortletConfigurationListeners(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNotNull(
				portlet.getPortletConfigurationListenerClass())) {

			PortletConfigurationListener portletConfigurationListener =
				_newInstance(
					PortletConfigurationListener.class,
					portlet.getPortletConfigurationListenerClass());

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					PortletConfigurationListener.class,
					portletConfigurationListener, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerPortletDataHandlers(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNotNull(portlet.getPortletDataHandlerClass())) {
			PortletDataHandler portletDataHandlerInstance = _newInstance(
				PortletDataHandler.class, portlet.getPortletDataHandlerClass());

			portletDataHandlerInstance.setPortletId(portlet.getPortletId());

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					PortletDataHandler.class, portletDataHandlerInstance,
					properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerPortletLayoutListeners(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNotNull(portlet.getPortletLayoutListenerClass())) {
			PortletLayoutListener portletLayoutListener = _newInstance(
				PortletLayoutListener.class,
				portlet.getPortletLayoutListenerClass());

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					PortletLayoutListener.class, portletLayoutListener,
					properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerPreferencesValidatorInstances(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNull(portlet.getPreferencesValidator())) {
			return;
		}

		PreferencesValidator preferencesValidatorInstance = _newInstance(
			PreferencesValidator.class, portlet.getPreferencesValidator());

		try {
			if (PropsValues.PREFERENCE_VALIDATE_ON_STARTUP) {
				preferencesValidatorInstance.validate(
					PortletPreferencesFactoryUtil.fromDefaultXML(
						portlet.getDefaultPreferences()));
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Portlet with the name " + portlet.getPortletId() +
						" does not have valid default preferences",
					exception);
			}
		}

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				PreferencesValidator.class, preferencesValidatorInstance,
				properties);

		serviceRegistrations.add(serviceRegistration);
	}

	private void _registerSchedulerJobConfigurations(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		for (SchedulerEntry schedulerEntry : portlet.getSchedulerEntries()) {
			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					SchedulerJobConfiguration.class,
					new SchedulerEntrySchedulerJobConfiguration(
						schedulerEntry, _classLoader),
					properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerSocialActivityInterpreterInstances(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		for (String socialActivityInterpreterClass :
				portlet.getSocialActivityInterpreterClasses()) {

			SocialActivityInterpreter socialActivityInterpreterInstance =
				_newInstance(
					SocialActivityInterpreter.class,
					socialActivityInterpreterClass);

			socialActivityInterpreterInstance =
				new SocialActivityInterpreterImpl(
					portlet.getPortletId(), socialActivityInterpreterInstance);

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					SocialActivityInterpreter.class,
					socialActivityInterpreterInstance, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerSocialRequestInterpreterInstances(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNotNull(portlet.getSocialRequestInterpreterClass())) {
			SocialRequestInterpreter socialRequestInterpreterInstance =
				_newInstance(
					SocialRequestInterpreter.class,
					portlet.getSocialRequestInterpreterClass());

			socialRequestInterpreterInstance = new SocialRequestInterpreterImpl(
				portlet.getPortletId(), socialRequestInterpreterInstance);

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					SocialRequestInterpreter.class,
					socialRequestInterpreterInstance, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerStagedModelDataHandler(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		for (String stagedModelDataHandlerClass :
				portlet.getStagedModelDataHandlerClasses()) {

			StagedModelDataHandler<?> stagedModelDataHandler = _newInstance(
				StagedModelDataHandler.class, stagedModelDataHandlerClass);

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					StagedModelDataHandler.class, stagedModelDataHandler,
					properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerTemplateHandlers(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNotNull(portlet.getTemplateHandlerClass())) {
			TemplateHandler templateHandler = _newInstance(
				TemplateHandler.class, portlet.getTemplateHandlerClass());

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					TemplateHandler.class, templateHandler, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerTrashHandlerInstances(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		for (String trashHandlerClass : portlet.getTrashHandlerClasses()) {
			TrashHandler trashHandlerInstance = _newInstance(
				TrashHandler.class, trashHandlerClass);

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					TrashHandler.class, trashHandlerInstance, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerURLEncoders(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNotNull(portlet.getURLEncoderClass())) {
			URLEncoder urlEncoder = _newInstance(
				URLEncoder.class, portlet.getURLEncoderClass());

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					URLEncoder.class, urlEncoder, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerUserNotificationDefinitionInstances(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNull(portlet.getUserNotificationDefinitions())) {
			return;
		}

		String xml = _getContent(portlet.getUserNotificationDefinitions());

		xml = JavaFieldsParser.parse(_classLoader, xml);

		Document document = UnsecureSAXReaderUtil.read(xml);

		Element rootElement = document.getRootElement();

		for (Element definitionElement : rootElement.elements("definition")) {
			String modelName = definitionElement.elementText("model-name");

			long classNameId = 0;

			if (Validator.isNotNull(modelName)) {
				classNameId = PortalUtil.getClassNameId(modelName);
			}

			int notificationType = GetterUtil.getInteger(
				definitionElement.elementText("notification-type"));

			String description = GetterUtil.getString(
				definitionElement.elementText("description"));

			UserNotificationDefinition userNotificationDefinition =
				new UserNotificationDefinition(
					portlet.getPortletId(), classNameId, notificationType,
					description);

			for (Element deliveryTypeElement :
					definitionElement.elements("delivery-type")) {

				String name = deliveryTypeElement.elementText("name");
				int type = GetterUtil.getInteger(
					deliveryTypeElement.elementText("type"));
				boolean defaultValue = GetterUtil.getBoolean(
					deliveryTypeElement.elementText("default"));
				boolean modifiable = GetterUtil.getBoolean(
					deliveryTypeElement.elementText("modifiable"));

				userNotificationDefinition.addUserNotificationDeliveryType(
					new UserNotificationDeliveryType(
						name, type, defaultValue, modifiable));
			}

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					UserNotificationDefinition.class,
					userNotificationDefinition, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerUserNotificationHandlerInstances(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		for (String userNotificationHandlerClass :
				portlet.getUserNotificationHandlerClasses()) {

			UserNotificationHandler userNotificationHandlerInstance =
				_newInstance(
					UserNotificationHandler.class,
					userNotificationHandlerClass);

			userNotificationHandlerInstance = new UserNotificationHandlerImpl(
				userNotificationHandlerInstance);

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					UserNotificationHandler.class,
					userNotificationHandlerInstance, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerWebDAVStorageInstances(
			BundleContext bundleContext, Portlet portlet)
		throws Exception {

		if (Validator.isNotNull(portlet.getWebDAVStorageClass())) {
			WebDAVStorage webDAVStorageInstance = _newInstance(
				WebDAVStorage.class, portlet.getWebDAVStorageClass());

			bundleContext.registerService(
				WebDAVStorage.class, webDAVStorageInstance,
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", portlet.getPortletId()
				).put(
					"webdav.storage.token", portlet.getWebDAVStorageToken()
				).build());
		}
	}

	private void _registerWorkflowHandlerInstances(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		for (String workflowHandlerClass :
				portlet.getWorkflowHandlerClasses()) {

			WorkflowHandler<?> workflowHandlerInstance = _newInstance(
				WorkflowHandler.class, workflowHandlerClass);

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					WorkflowHandler.class, workflowHandlerInstance, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _registerXmlRpcMethodInstances(
			BundleContext bundleContext, Portlet portlet,
			Dictionary<String, Object> properties,
			List<ServiceRegistration<?>> serviceRegistrations)
		throws Exception {

		if (Validator.isNotNull(portlet.getXmlRpcMethodClass())) {
			Method xmlRpcMethodInstance = _newInstance(
				Method.class, portlet.getXmlRpcMethodClass());

			ServiceRegistration<?> serviceRegistration =
				bundleContext.registerService(
					Method.class, xmlRpcMethodInstance, properties);

			serviceRegistrations.add(serviceRegistration);
		}
	}

	private void _validate() {
		if (_classLoader == null) {
			throw new IllegalStateException("Class loader is null");
		}

		if (_servletContext == null) {
			throw new IllegalStateException("Servlet context is null");
		}

		if (_warFile == null) {
			throw new IllegalStateException("WAR file is null");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletBagFactory.class);

	private ClassLoader _classLoader;
	private Configuration _configuration;
	private ServletContext _servletContext;
	private Boolean _warFile;

	private static class SchedulerEntrySchedulerJobConfiguration
		implements SchedulerJobConfiguration {

		@Override
		public UnsafeConsumer<Message, Exception>
			getJobExecutorUnsafeConsumer() {

			return _messageListener::receive;
		}

		@Override
		public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
			throw new UnsupportedOperationException();
		}

		public String getName() {
			return _schedulerEntry.getEventListenerClass();
		}

		@Override
		public TriggerConfiguration getTriggerConfiguration() {
			return _schedulerEntry.getTriggerConfiguration();
		}

		private SchedulerEntrySchedulerJobConfiguration(
				SchedulerEntry schedulerEntry, ClassLoader classLoader)
			throws Exception {

			_schedulerEntry = schedulerEntry;

			_messageListener =
				(com.liferay.portal.kernel.messaging.MessageListener)
					InstanceFactory.newInstance(
						classLoader, schedulerEntry.getEventListenerClass());
		}

		private final com.liferay.portal.kernel.messaging.MessageListener
			_messageListener;
		private final SchedulerEntry _schedulerEntry;

	}

}