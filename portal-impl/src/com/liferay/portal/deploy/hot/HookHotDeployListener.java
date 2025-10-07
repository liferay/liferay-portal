/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.deploy.hot;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanLocatorException;
import com.liferay.portal.kernel.bean.ClassLoaderBeanHandler;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.deploy.auto.AutoDeployListener;
import com.liferay.portal.kernel.deploy.hot.BaseHotDeployListener;
import com.liferay.portal.kernel.deploy.hot.HotDeployEvent;
import com.liferay.portal.kernel.deploy.hot.HotDeployException;
import com.liferay.portal.kernel.deploy.hot.HotDeployListener;
import com.liferay.portal.kernel.deploy.hot.HotDeployUtil;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.InvokerAction;
import com.liferay.portal.kernel.events.InvokerSessionAction;
import com.liferay.portal.kernel.events.InvokerSimpleAction;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.SessionAction;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.format.PhoneNumberFormat;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.lock.LockListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.security.auth.AuthFailure;
import com.liferay.portal.kernel.security.auth.AuthToken;
import com.liferay.portal.kernel.security.auth.Authenticator;
import com.liferay.portal.kernel.security.auth.EmailAddressGenerator;
import com.liferay.portal.kernel.security.auth.EmailAddressValidator;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameValidator;
import com.liferay.portal.kernel.security.auth.ScreenNameGenerator;
import com.liferay.portal.kernel.security.auth.ScreenNameValidator;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.ldap.AttributesTransformer;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.RoleMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.SiteMembershipPolicy;
import com.liferay.portal.kernel.security.membershippolicy.UserGroupMembershipPolicy;
import com.liferay.portal.kernel.security.pwd.Toolkit;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.servlet.DirectServletRegistryUtil;
import com.liferay.portal.kernel.servlet.FileAvailabilityUtil;
import com.liferay.portal.kernel.servlet.LiferayFilter;
import com.liferay.portal.kernel.servlet.LiferayFilterTracker;
import com.liferay.portal.kernel.servlet.TryFilter;
import com.liferay.portal.kernel.servlet.TryFinallyFilter;
import com.liferay.portal.kernel.servlet.WrapHttpServletRequestFilter;
import com.liferay.portal.kernel.servlet.WrapHttpServletResponseFilter;
import com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorConstants;
import com.liferay.portal.kernel.servlet.taglib.ui.FormNavigatorEntry;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.url.ServletContextURLContainer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.language.LiferayResourceBundle;
import com.liferay.portal.security.auth.AuthVerifierPipeline;
import com.liferay.portal.security.auth.InterruptedPortletRequestWhitelistUtil;
import com.liferay.portal.servlet.taglib.ui.DeprecatedFormNavigatorEntry;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.util.JavaScriptBundleUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletContext;

import java.io.InputStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Brian Wing Shun Chan
 * @author Bruno Farache
 * @author Wesley Gong
 * @author Ryan Park
 * @author Mika Koivisto
 * @author Peter Fellwock
 * @author Raymond Augé
 * @author Kamesh Sampath
 */
public class HookHotDeployListener
	extends BaseHotDeployListener implements PropsKeys {

	public static final String[] SUPPORTED_PROPERTIES = {
		"admin.default.group.names", "admin.default.role.names",
		"admin.default.user.group.names", "auth.forward.by.last.path",
		"auth.public.paths", "auth.verifier.pipeline", "auto.deploy.listeners",
		"application.startup.events", "auth.failure", "auth.max.failures",
		"auth.token.ignore.actions", "auth.token.ignore.origins",
		"auth.token.ignore.portlets", "auth.token.impl", "auth.pipeline.post",
		"auth.pipeline.pre", "auto.login.hooks", "company.default.locale",
		"company.default.time.zone", "company.settings.form.authentication",
		"company.settings.form.configuration",
		"company.settings.form.identification",
		"company.settings.form.miscellaneous", "company.settings.form.social",
		"default.landing.page.path", "default.regular.color.scheme.id",
		"default.regular.theme.id", "dl.file.entry.drafts.enabled",
		"dl.store.antivirus.enabled", "dl.store.impl",
		"field.enable.com.liferay.portal.kernel.model.Contact.birthday",
		"field.enable.com.liferay.portal.kernel.model.Contact.male",
		"field.enable.com.liferay.portal.kernel.model.Organization.status",
		"hot.deploy.listeners", "javascript.fast.load", "layout.form.add",
		"layout.form.update", "layout.set.form.update",
		"layout.static.portlets.all", "layout.template.cache.enabled",
		"layout.user.private.layouts.auto.create",
		"layout.user.private.layouts.enabled",
		"layout.user.private.layouts.power.user.required",
		"layout.user.public.layouts.auto.create",
		"layout.user.public.layouts.enabled",
		"layout.user.public.layouts.power.user.required",
		"ldap.attrs.transformer.impl", "locales", "locales.beta",
		"locales.enabled", "lock.listeners",
		"login.create.account.allow.custom.password", "login.dialog.disabled",
		"login.events.post", "login.events.pre", "login.form.navigation.post",
		"login.form.navigation.pre", "logout.events.post", "logout.events.pre",
		"my.sites.show.private.sites.with.no.layouts",
		"my.sites.show.public.sites.with.no.layouts",
		"my.sites.show.user.private.sites.with.no.layouts",
		"my.sites.show.user.public.sites.with.no.layouts",
		"organizations.form.add.identification", "organizations.form.add.main",
		"organizations.form.add.miscellaneous",
		"passwords.passwordpolicytoolkit.generator",
		"passwords.passwordpolicytoolkit.static", "phone.number.format.impl",
		"phone.number.format.international.regexp",
		"phone.number.format.usa.regexp",
		"portlet.add.default.resource.check.enabled",
		"portlet.add.default.resource.check.whitelist",
		"portlet.add.default.resource.check.whitelist.actions",
		"rss.feeds.enabled", "sanitizer.impl", "servlet.session.create.events",
		"servlet.session.destroy.events", "servlet.service.events.post",
		"servlet.service.events.pre", "session.max.allowed",
		"session.phishing.protected.attributes", "session.store.password",
		"sites.form.add.advanced", "sites.form.add.main",
		"sites.form.add.miscellaneous", "sites.form.add.seo",
		"sites.form.update.advanced", "sites.form.update.main",
		"sites.form.update.miscellaneous", "sites.form.update.seo",
		"social.activity.sets.bundling.enabled", "social.activity.sets.enabled",
		"social.activity.sets.selector", "terms.of.use.required",
		"theme.css.fast.load", "theme.images.fast.load",
		"theme.jsp.override.enabled", "theme.loader.new.theme.id.on.import",
		"theme.portlet.decorate.default", "theme.portlet.sharing.default",
		"theme.shortcut.icon", "time.zones",
		"user.notification.event.confirmation.enabled",
		"users.email.address.generator", "users.email.address.validator",
		"users.email.address.required", "users.form.add.identification",
		"users.form.add.main", "users.form.add.miscellaneous",
		"users.form.my.account.identification", "users.form.my.account.main",
		"users.form.my.account.miscellaneous",
		"users.form.update.identification", "users.form.update.main",
		"users.form.update.miscellaneous", "users.full.name.generator",
		"users.full.name.validator", "users.screen.name.always.autogenerate",
		"users.screen.name.generator", "users.screen.name.validator",
		"value.object.listener.*"
	};

	public HookHotDeployListener() {
		for (String key : _PROPS_VALUES_MERGE_STRING_ARRAY) {
			_mergeStringArraysContainerMap.put(
				key, new MergeStringArraysContainer(key));
		}

		for (String key : _PROPS_VALUES_OVERRIDE_STRING_ARRAY) {
			_overrideStringArraysContainerMap.put(
				key, new OverrideStringArraysContainer(key));
		}
	}

	@Override
	public void invokeDeploy(HotDeployEvent hotDeployEvent)
		throws HotDeployException {

		try {
			doInvokeDeploy(hotDeployEvent);
		}
		catch (Throwable throwable) {
			throwHotDeployException(
				hotDeployEvent, "Error registering hook for ", throwable);
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
				hotDeployEvent, "Error unregistering hook for ", throwable);
		}
	}

	public <T> void registerService(
		String servletContextName, Object serviceRegistrationKey,
		Class<T> clazz, T service, Map<String, Object> properties) {

		Map<Object, ServiceRegistration<?>> serviceRegistrations =
			getServiceRegistrations(servletContextName);

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceRegistration<T> serviceRegistration =
			bundleContext.registerService(
				clazz, service,
				HashMapDictionaryBuilder.putAll(
					properties
				).build());

		serviceRegistrations.put(serviceRegistrationKey, serviceRegistration);
	}

	public <T> void registerService(
		String servletContextName, Object serviceRegistrationKey,
		Class<T> clazz, T service, Object... propertyKVPs) {

		if ((propertyKVPs.length % 2) != 0) {
			throw new IllegalArgumentException(
				"Properties length is not an even number");
		}

		Map<String, Object> properties = new HashMap<>();

		for (int i = 0; i < propertyKVPs.length; i += 2) {
			String propertyName = String.valueOf(propertyKVPs[i]);
			Object propertyValue = propertyKVPs[i + 1];

			properties.put(propertyName, propertyValue);
		}

		registerService(
			servletContextName, serviceRegistrationKey, clazz, service,
			properties);
	}

	public <T> void registerService(
		String servletContextName, Object serviceRegistrationKey,
		Class<T> clazz, T service, Properties properties) {

		registerService(
			servletContextName, serviceRegistrationKey, clazz, service,
			PropertiesUtil.toMap(properties));
	}

	protected boolean containsKey(Properties portalProperties, String key) {
		if (_log.isDebugEnabled()) {
			return true;
		}

		return portalProperties.containsKey(key);
	}

	protected void destroyCustomJspBag(
			String servletContextName, CustomJspBag customJspBag)
		throws Exception {
	}

	protected void destroyPortalProperties(
			String servletContextName, Properties portalProperties)
		throws Exception {

		for (String name : portalProperties.stringPropertyNames()) {
			PropsUtil.set(name, null);
		}

		if (_log.isDebugEnabled() && portalProperties.containsKey(LOCALES)) {
			_log.debug(
				"Portlet locales " + portalProperties.getProperty(LOCALES));
			_log.debug("Original locales " + PropsUtil.get(LOCALES));

			String[] locales = PropsUtil.getArray(LOCALES);

			_log.debug("Original locales array length " + locales.length);
		}

		resetPortalProperties(servletContextName, portalProperties, false);

		if (portalProperties.containsKey(PropsKeys.DL_STORE_IMPL)) {
			PropsValues.DL_STORE_IMPL = PropsUtil.get(PropsKeys.DL_STORE_IMPL);
		}

		Set<String> liferayFilterClassNames =
			LiferayFilterTracker.getClassNames();

		for (String liferayFilterClassName : liferayFilterClassNames) {
			if (!portalProperties.containsKey(liferayFilterClassName)) {
				continue;
			}

			boolean filterEnabled = GetterUtil.getBoolean(
				PropsUtil.get(liferayFilterClassName));

			Set<LiferayFilter> liferayFilters =
				LiferayFilterTracker.getLiferayFilters(liferayFilterClassName);

			for (LiferayFilter liferayFilter : liferayFilters) {
				liferayFilter.setFilterEnabled(filterEnabled);
			}
		}
	}

	protected void doInvokeDeploy(HotDeployEvent hotDeployEvent)
		throws Exception {

		ServletContext servletContext = hotDeployEvent.getServletContext();

		String servletContextName = servletContext.getServletContextName();

		if (_log.isDebugEnabled()) {
			_log.debug("Invoking deploy for " + servletContextName);
		}

		String xml = StreamUtil.toString(
			servletContext.getResourceAsStream("/WEB-INF/liferay-hook.xml"));

		if (xml == null) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Registering hook for " + servletContextName);
		}

		_servletContextNames.add(servletContextName);

		Document document = UnsecureSAXReaderUtil.read(xml, true);

		Element rootElement = document.getRootElement();

		ClassLoader portletClassLoader = hotDeployEvent.getContextClassLoader();

		initPortalProperties(
			servletContextName, portletClassLoader, rootElement);

		initLanguageProperties(
			servletContextName, portletClassLoader, rootElement);

		try {
			initCustomJspDir(
				servletContext, servletContextName, portletClassLoader,
				hotDeployEvent.getPluginPackage(), rootElement);
		}
		catch (DuplicateCustomJspException duplicateCustomJspException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					servletContextName + " will be undeployed",
					duplicateCustomJspException);
			}

			HotDeployUtil.fireUndeployEvent(
				new HotDeployEvent(servletContext, portletClassLoader));

			return;
		}

		initIndexerPostProcessors(
			servletContextName, portletClassLoader, rootElement);

		initServices(servletContextName, portletClassLoader, rootElement);

		initServletFilters(
			servletContext, servletContextName, portletClassLoader,
			rootElement);

		initStrutsActions(servletContextName, portletClassLoader, rootElement);

		List<Element> modelListenerElements = rootElement.elements(
			"model-listener");

		for (Element modelListenerElement : modelListenerElements) {
			String modelName = modelListenerElement.elementText("model-name");
			String modelListenerClassName = modelListenerElement.elementText(
				"model-listener-class");

			initModelListener(
				servletContextName, portletClassLoader, modelName,
				modelListenerClassName);
		}

		List<Element> eventElements = rootElement.elements("event");

		for (Element eventElement : eventElements) {
			String eventName = eventElement.elementText("event-type");
			String eventClassName = eventElement.elementText("event-class");

			initEvent(
				servletContextName, portletClassLoader, eventName,
				eventClassName);
		}

		// End backwards compatibility for 5.1.0

		DirectServletRegistryUtil.clearServlets();
		FileAvailabilityUtil.clearAvailabilities();

		if (_log.isInfoEnabled()) {
			_log.info(
				"Hook for " + servletContextName + " is available for use");
		}
	}

	protected void doInvokeUndeploy(HotDeployEvent hotDeployEvent)
		throws Exception {

		ServletContext servletContext = hotDeployEvent.getServletContext();

		String servletContextName = servletContext.getServletContextName();

		if (_log.isDebugEnabled()) {
			_log.debug("Invoking undeploy for " + servletContextName);
		}

		if (!_servletContextNames.remove(servletContextName)) {
			return;
		}

		HotDeployListenersContainer hotDeployListenersContainer =
			_hotDeployListenersContainerMap.remove(servletContextName);

		if (hotDeployListenersContainer != null) {
			hotDeployListenersContainer.unregisterHotDeployListeners();
		}

		Properties portalProperties = _portalPropertiesMap.remove(
			servletContextName);

		if (portalProperties != null) {
			destroyPortalProperties(servletContextName, portalProperties);
		}

		Map<Object, ServiceRegistration<?>> serviceRegistrations =
			_serviceRegistrations.remove(servletContextName);

		if (serviceRegistrations != null) {
			for (ServiceRegistration<?> serviceRegistration :
					serviceRegistrations.values()) {

				serviceRegistration.unregister();
			}

			serviceRegistrations.clear();
		}

		if (_log.isInfoEnabled()) {
			_log.info("Hook for " + servletContextName + " was unregistered");
		}
	}

	protected Locale getLocale(String languagePropertiesLocation) {
		int x = languagePropertiesLocation.indexOf(CharPool.UNDERLINE);
		int y = languagePropertiesLocation.indexOf(".properties");

		if ((x == -1) && (y != -1)) {
			return new Locale(StringPool.BLANK);
		}

		Locale locale = null;

		if ((x != -1) && (y != -1)) {
			String localeKey = languagePropertiesLocation.substring(x + 1, y);

			locale = LocaleUtil.fromLanguageId(localeKey, true, false);
		}

		return locale;
	}

	protected BasePersistence<?> getPersistence(
		String servletContextName, String modelName) {

		int pos = modelName.lastIndexOf(CharPool.PERIOD);

		String entityName = modelName.substring(pos + 1);

		pos = modelName.lastIndexOf(".model.");

		String packagePath = modelName.substring(0, pos);

		String beanName = StringBundler.concat(
			packagePath, ".service.persistence.", entityName, "Persistence");

		try {
			return (BasePersistence<?>)PortalBeanLocatorUtil.locate(beanName);
		}
		catch (BeanLocatorException beanLocatorException) {
			if (_log.isDebugEnabled()) {
				_log.debug(beanLocatorException);
			}

			return (BasePersistence<?>)PortletBeanLocatorUtil.locate(
				servletContextName, beanName);
		}
	}

	protected Map<Object, ServiceRegistration<?>> getServiceRegistrations(
		String servletContextName) {

		Map<Object, ServiceRegistration<?>> serviceRegistrations =
			_serviceRegistrations.get(servletContextName);

		if (serviceRegistrations == null) {
			serviceRegistrations = newMap();

			_serviceRegistrations.put(servletContextName, serviceRegistrations);
		}

		return serviceRegistrations;
	}

	protected void initAuthenticators(
			String servletContextName, ClassLoader portletClassLoader,
			Properties portalProperties)
		throws Exception {

		initAuthenticators(
			servletContextName, portletClassLoader, portalProperties,
			AUTH_PIPELINE_PRE);
		initAuthenticators(
			servletContextName, portletClassLoader, portalProperties,
			AUTH_PIPELINE_POST);
	}

	protected void initAuthenticators(
			String servletContextName, ClassLoader portletClassLoader,
			Properties portalProperties, String key)
		throws Exception {

		String[] authenticatorClassNames = StringUtil.split(
			portalProperties.getProperty(key));

		for (String authenticatorClassName : authenticatorClassNames) {
			Authenticator authenticator = (Authenticator)newInstance(
				portletClassLoader, Authenticator.class,
				authenticatorClassName);

			registerService(
				servletContextName, authenticatorClassName, Authenticator.class,
				authenticator, "key", key);
		}
	}

	protected void initAuthFailures(
			String servletContextName, ClassLoader portletClassLoader,
			Properties portalProperties)
		throws Exception {

		initAuthFailures(
			servletContextName, portletClassLoader, portalProperties,
			AUTH_FAILURE);
		initAuthFailures(
			servletContextName, portletClassLoader, portalProperties,
			AUTH_MAX_FAILURES);
	}

	protected void initAuthFailures(
			String servletContextName, ClassLoader portletClassLoader,
			Properties portalProperties, String key)
		throws Exception {

		String[] authFailureClassNames = StringUtil.split(
			portalProperties.getProperty(key));

		for (String authFailureClassName : authFailureClassNames) {
			AuthFailure authFailure = (AuthFailure)newInstance(
				portletClassLoader, AuthFailure.class, authFailureClassName);

			registerService(
				servletContextName, authFailureClassName, AuthFailure.class,
				authFailure, "key", key);
		}
	}

	protected void initAuthPublicPaths(
			String servletContextName, Properties portalProperties)
		throws Exception {

		String[] authPublicPaths = StringUtil.split(
			portalProperties.getProperty(AUTH_PUBLIC_PATHS));

		for (String authPublicPath : authPublicPaths) {
			registerService(
				servletContextName, AUTH_PUBLIC_PATHS + authPublicPath,
				Object.class, new Object(), "auth.public.path", authPublicPath);
		}
	}

	protected void initAuthVerifiers(
			String servletContextName, ClassLoader portletClassLoader,
			Properties portalProperties)
		throws Exception {

		String[] authVerifierClassNames = StringUtil.split(
			portalProperties.getProperty(AUTH_VERIFIER_PIPELINE));

		for (String authVerifierClassName : authVerifierClassNames) {
			AuthVerifier authVerifier = (AuthVerifier)newInstance(
				portletClassLoader, AuthVerifier.class, authVerifierClassName);

			Properties properties = PropertiesUtil.getProperties(
				portalProperties,
				AuthVerifierPipeline.getAuthVerifierPropertyName(
					authVerifierClassName),
				true);

			registerService(
				servletContextName, authVerifierClassName, AuthVerifier.class,
				authVerifier, properties);
		}
	}

	protected void initAutoDeployListeners(
			String servletContextName, ClassLoader portletClassLoader,
			Properties portalProperties)
		throws Exception {

		String[] autoDeployListenerClassNames = StringUtil.split(
			portalProperties.getProperty(PropsKeys.AUTO_DEPLOY_LISTENERS));

		if (autoDeployListenerClassNames.length == 0) {
			return;
		}

		for (String autoDeployListenerClassName :
				autoDeployListenerClassNames) {

			AutoDeployListener autoDeployListener =
				(AutoDeployListener)newInstance(
					portletClassLoader, AutoDeployListener.class,
					autoDeployListenerClassName);

			registerService(
				servletContextName, autoDeployListenerClassName,
				AutoDeployListener.class, autoDeployListener);
		}
	}

	protected void initAutoLogins(
			String servletContextName, ClassLoader portletClassLoader,
			Properties portalProperties)
		throws Exception {

		String[] autoLoginClassNames = StringUtil.split(
			portalProperties.getProperty(AUTO_LOGIN_HOOKS));

		for (String autoLoginClassName : autoLoginClassNames) {
			AutoLogin autoLogin = (AutoLogin)newInstance(
				portletClassLoader, AutoLogin.class, autoLoginClassName);

			registerService(
				servletContextName, autoLoginClassName, AutoLogin.class,
				autoLogin);
		}
	}

	protected void initCustomJspDir(
			ServletContext servletContext, String servletContextName,
			ClassLoader portletClassLoader, PluginPackage pluginPackage,
			Element rootElement)
		throws Exception {

		String customJspDir = rootElement.elementText("custom-jsp-dir");

		if (Validator.isNull(customJspDir)) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Custom JSP directory: " + customJspDir);
		}

		boolean customJspGlobal = GetterUtil.getBoolean(
			rootElement.elementText("custom-jsp-global"), true);

		CustomJspBag customJspBag = new CustomJspBagImpl(
			new ServletContextURLContainer(servletContext), customJspDir,
			customJspGlobal);

		registerService(
			servletContextName, "customJsp", CustomJspBag.class, customJspBag,
			"context.id", servletContextName, "context.name",
			pluginPackage.getName());
	}

	protected void initEvent(
			String servletContextName, ClassLoader portletClassLoader,
			String eventName, String eventClassName)
		throws Exception {

		if (eventName.equals(APPLICATION_STARTUP_EVENTS)) {
			Class<?> clazz = portletClassLoader.loadClass(eventClassName);

			SimpleAction simpleAction = new InvokerSimpleAction(
				(SimpleAction)clazz.newInstance(), portletClassLoader);

			CompanyLocalServiceUtil.forEachCompanyId(
				companyId -> simpleAction.run(
					new String[] {String.valueOf(companyId)}));
		}

		if (_propsKeysEvents.contains(eventName)) {
			Class<?> clazz = portletClassLoader.loadClass(eventClassName);

			Action action = (Action)clazz.newInstance();

			action = new InvokerAction(action, portletClassLoader);

			registerService(
				servletContextName, eventClassName, LifecycleAction.class,
				action, "key", eventName);
		}

		if (_propsKeysSessionEvents.contains(eventName)) {
			Class<?> clazz = portletClassLoader.loadClass(eventClassName);

			SessionAction sessionAction = (SessionAction)clazz.newInstance();

			sessionAction = new InvokerSessionAction(
				sessionAction, portletClassLoader);

			registerService(
				servletContextName, eventClassName, LifecycleAction.class,
				sessionAction, "key", eventName);
		}
	}

	protected void initEvents(
			String servletContextName, ClassLoader portletClassLoader,
			Properties portalProperties)
		throws Exception {

		for (Map.Entry<Object, Object> entry : portalProperties.entrySet()) {
			String key = (String)entry.getKey();

			if (!key.equals(APPLICATION_STARTUP_EVENTS) &&
				!_propsKeysEvents.contains(key) &&
				!_propsKeysSessionEvents.contains(key)) {

				continue;
			}

			String eventName = key;
			String[] eventClassNames = StringUtil.split(
				(String)entry.getValue());

			for (String eventClassName : eventClassNames) {
				initEvent(
					servletContextName, portletClassLoader, eventName,
					eventClassName);
			}
		}
	}

	protected void initFormNavigatorEntries(
			String servletContextName, Properties portalProperties)
		throws Exception {

		initFormNavigatorEntry(
			servletContextName, portalProperties,
			COMPANY_SETTINGS_FORM_CONFIGURATION,
			FormNavigatorConstants.CATEGORY_KEY_COMPANY_SETTINGS_CONFIGURATION,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_COMPANY_SETTINGS,
			"portal_settings");
		initFormNavigatorEntry(
			servletContextName, portalProperties,
			COMPANY_SETTINGS_FORM_IDENTIFICATION,
			FormNavigatorConstants.CATEGORY_KEY_COMPANY_SETTINGS_IDENTIFICATION,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_COMPANY_SETTINGS,
			"portal_settings");
		initFormNavigatorEntry(
			servletContextName, portalProperties,
			COMPANY_SETTINGS_FORM_MISCELLANEOUS,
			FormNavigatorConstants.CATEGORY_KEY_COMPANY_SETTINGS_MISCELLANEOUS,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_COMPANY_SETTINGS,
			"portal_settings");
		initFormNavigatorEntry(
			servletContextName, portalProperties, COMPANY_SETTINGS_FORM_SOCIAL,
			FormNavigatorConstants.CATEGORY_KEY_COMPANY_SETTINGS_SOCIAL,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_COMPANY_SETTINGS,
			"portal_settings");

		initFormNavigatorEntry(
			servletContextName, portalProperties, LAYOUT_FORM_ADD,
			StringPool.BLANK, FormNavigatorConstants.FORM_NAVIGATOR_ID_LAYOUT,
			"layouts_admin/layout");
		initFormNavigatorEntry(
			servletContextName, portalProperties, LAYOUT_FORM_UPDATE,
			StringPool.BLANK, FormNavigatorConstants.FORM_NAVIGATOR_ID_LAYOUT,
			"layouts_admin/layout");

		initFormNavigatorEntry(
			servletContextName, portalProperties, LAYOUT_SET_FORM_UPDATE,
			StringPool.BLANK,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_LAYOUT_SET,
			"layouts_admin/layout_set");

		initFormNavigatorEntry(
			servletContextName, portalProperties,
			ORGANIZATIONS_FORM_ADD_IDENTIFICATION,
			FormNavigatorConstants.CATEGORY_KEY_ORGANIZATION_IDENTIFICATION,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_ORGANIZATIONS,
			"users_admin/organization");
		initFormNavigatorEntry(
			servletContextName, portalProperties, ORGANIZATIONS_FORM_ADD_MAIN,
			FormNavigatorConstants.
				CATEGORY_KEY_ORGANIZATION_ORGANIZATION_INFORMATION,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_ORGANIZATIONS,
			"users_admin/organization");
		initFormNavigatorEntry(
			servletContextName, portalProperties,
			ORGANIZATIONS_FORM_ADD_MISCELLANEOUS,
			FormNavigatorConstants.CATEGORY_KEY_ORGANIZATION_MISCELLANEOUS,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_ORGANIZATIONS,
			"users_admin/organization");
		initFormNavigatorEntry(
			servletContextName, portalProperties,
			ORGANIZATIONS_FORM_UPDATE_IDENTIFICATION,
			FormNavigatorConstants.CATEGORY_KEY_ORGANIZATION_IDENTIFICATION,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_ORGANIZATIONS,
			"users_admin/organization");
		initFormNavigatorEntry(
			servletContextName, portalProperties,
			ORGANIZATIONS_FORM_UPDATE_MAIN,
			FormNavigatorConstants.
				CATEGORY_KEY_ORGANIZATION_ORGANIZATION_INFORMATION,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_ORGANIZATIONS,
			"users_admin/organization");
		initFormNavigatorEntry(
			servletContextName, portalProperties,
			ORGANIZATIONS_FORM_UPDATE_MISCELLANEOUS,
			FormNavigatorConstants.CATEGORY_KEY_ORGANIZATION_MISCELLANEOUS,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_ORGANIZATIONS,
			"users_admin/organization");

		initFormNavigatorEntry(
			servletContextName, portalProperties, SITES_FORM_ADD_ADVANCED,
			FormNavigatorConstants.CATEGORY_KEY_SITES_ADVANCED,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_SITES, "sites_admin/site");
		initFormNavigatorEntry(
			servletContextName, portalProperties, SITES_FORM_ADD_MAIN,
			FormNavigatorConstants.CATEGORY_KEY_SITES_GENERAL,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_SITES, "sites_admin/site");
		initFormNavigatorEntry(
			servletContextName, portalProperties, SITES_FORM_ADD_MISCELLANEOUS,
			FormNavigatorConstants.CATEGORY_KEY_SITES_LANGUAGES,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_SITES, "sites_admin/site");
		initFormNavigatorEntry(
			servletContextName, portalProperties, SITES_FORM_ADD_SEO,
			FormNavigatorConstants.CATEGORY_KEY_SITES_SEO,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_SITES, "sites_admin/site");
		initFormNavigatorEntry(
			servletContextName, portalProperties, SITES_FORM_UPDATE_ADVANCED,
			FormNavigatorConstants.CATEGORY_KEY_SITES_ADVANCED,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_SITES, "sites_admin/site");
		initFormNavigatorEntry(
			servletContextName, portalProperties, SITES_FORM_UPDATE_MAIN,
			FormNavigatorConstants.CATEGORY_KEY_SITES_GENERAL,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_SITES, "sites_admin/site");
		initFormNavigatorEntry(
			servletContextName, portalProperties,
			SITES_FORM_UPDATE_MISCELLANEOUS,
			FormNavigatorConstants.CATEGORY_KEY_SITES_LANGUAGES,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_SITES, "sites_admin/site");
		initFormNavigatorEntry(
			servletContextName, portalProperties, SITES_FORM_UPDATE_SEO,
			FormNavigatorConstants.CATEGORY_KEY_SITES_SEO,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_SITES, "sites_admin/site");

		initFormNavigatorEntry(
			servletContextName, portalProperties, USERS_FORM_ADD_IDENTIFICATION,
			FormNavigatorConstants.CATEGORY_KEY_USER_IDENTIFICATION,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_USERS, "users_admin/user");
		initFormNavigatorEntry(
			servletContextName, portalProperties, USERS_FORM_ADD_MAIN,
			FormNavigatorConstants.CATEGORY_KEY_USER_USER_INFORMATION,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_USERS, "users_admin/user");
		initFormNavigatorEntry(
			servletContextName, portalProperties, USERS_FORM_ADD_MISCELLANEOUS,
			FormNavigatorConstants.CATEGORY_KEY_USER_MISCELLANEOUS,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_USERS, "users_admin/user");
		initFormNavigatorEntry(
			servletContextName, portalProperties,
			USERS_FORM_MY_ACCOUNT_IDENTIFICATION,
			FormNavigatorConstants.CATEGORY_KEY_USER_IDENTIFICATION,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_USERS, "users_admin/user");
		initFormNavigatorEntry(
			servletContextName, portalProperties, USERS_FORM_MY_ACCOUNT_MAIN,
			FormNavigatorConstants.CATEGORY_KEY_USER_USER_INFORMATION,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_USERS, "users_admin/user");
		initFormNavigatorEntry(
			servletContextName, portalProperties,
			USERS_FORM_MY_ACCOUNT_MISCELLANEOUS,
			FormNavigatorConstants.CATEGORY_KEY_USER_MISCELLANEOUS,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_USERS, "users_admin/user");
		initFormNavigatorEntry(
			servletContextName, portalProperties,
			USERS_FORM_UPDATE_IDENTIFICATION,
			FormNavigatorConstants.CATEGORY_KEY_USER_IDENTIFICATION,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_USERS, "users_admin/user");
		initFormNavigatorEntry(
			servletContextName, portalProperties, USERS_FORM_UPDATE_MAIN,
			FormNavigatorConstants.CATEGORY_KEY_USER_USER_INFORMATION,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_USERS, "users_admin/user");
		initFormNavigatorEntry(
			servletContextName, portalProperties,
			USERS_FORM_UPDATE_MISCELLANEOUS,
			FormNavigatorConstants.CATEGORY_KEY_USER_MISCELLANEOUS,
			FormNavigatorConstants.FORM_NAVIGATOR_ID_USERS, "users_admin/user");
	}

	protected void initFormNavigatorEntry(
		String servletContextName, Properties portalProperties,
		String portalPropertiesKey, String categoryKey, String formNavigatorId,
		String jspPath) {

		String[] formNavigatorSections = StringUtil.split(
			portalProperties.getProperty(portalPropertiesKey));

		for (int i = 0; i < formNavigatorSections.length; i++) {
			String formNavigatorSection = formNavigatorSections[i];

			FormNavigatorEntry<Object> deprecatedFormNavigatorEntry =
				new DeprecatedFormNavigatorEntry(
					formNavigatorSection, formNavigatorSection, categoryKey,
					formNavigatorId,
					StringBundler.concat(
						"/html/portlet/", jspPath, "/", formNavigatorSection,
						".jsp"));

			registerService(
				servletContextName,
				formNavigatorId + categoryKey + formNavigatorSection,
				FormNavigatorEntry.class, deprecatedFormNavigatorEntry,
				"service.ranking", -i);
		}
	}

	protected void initHotDeployListeners(
			String servletContextName, ClassLoader portletClassLoader,
			Properties portalProperties)
		throws Exception {

		String[] hotDeployListenerClassNames = StringUtil.split(
			portalProperties.getProperty(PropsKeys.HOT_DEPLOY_LISTENERS));

		if (hotDeployListenerClassNames.length == 0) {
			return;
		}

		HotDeployListenersContainer hotDeployListenersContainer =
			new HotDeployListenersContainer();

		_hotDeployListenersContainerMap.put(
			servletContextName, hotDeployListenersContainer);

		for (String hotDeployListenerClassName : hotDeployListenerClassNames) {
			HotDeployListener hotDeployListener =
				(HotDeployListener)newInstance(
					portletClassLoader, HotDeployListener.class,
					hotDeployListenerClassName);

			hotDeployListenersContainer.registerHotDeployListener(
				hotDeployListener);
		}
	}

	protected void initIndexerPostProcessors(
			String servletContextName, ClassLoader portletClassLoader,
			Element parentElement)
		throws Exception {

		List<Element> indexerPostProcessorElements = parentElement.elements(
			"indexer-post-processor");

		for (Element indexerPostProcessorElement :
				indexerPostProcessorElements) {

			String indexerClassName = indexerPostProcessorElement.elementText(
				"indexer-class-name");

			String indexerPostProcessorImpl =
				indexerPostProcessorElement.elementText(
					"indexer-post-processor-impl");

			IndexerPostProcessor indexerPostProcessor =
				(IndexerPostProcessor)InstanceFactory.newInstance(
					portletClassLoader, indexerPostProcessorImpl);

			registerService(
				servletContextName, indexerPostProcessorImpl,
				IndexerPostProcessor.class, indexerPostProcessor,
				"indexer.class.name", indexerClassName);
		}
	}

	protected void initLanguageProperties(
			String servletContextName, ClassLoader portletClassLoader,
			Element parentElement)
		throws Exception {

		List<Element> languagePropertiesElements = parentElement.elements(
			"language-properties");

		for (Element languagePropertiesElement : languagePropertiesElements) {
			String languagePropertiesLocation =
				languagePropertiesElement.getText();

			Locale locale = getLocale(languagePropertiesLocation);

			if (locale == null) {
				if (_log.isDebugEnabled()) {
					_log.debug("Ignoring " + languagePropertiesLocation);
				}

				continue;
			}

			URL url = portletClassLoader.getResource(
				languagePropertiesLocation);

			if (url == null) {
				continue;
			}

			try (InputStream inputStream = url.openStream()) {
				ResourceBundle resourceBundle = new LiferayResourceBundle(
					inputStream, StringPool.UTF8);

				registerService(
					servletContextName, languagePropertiesLocation,
					ResourceBundle.class, resourceBundle,
					HashMapBuilder.<String, Object>put(
						"language.id", LocaleUtil.toLanguageId(locale)
					).build());
			}
		}
	}

	protected void initModelListener(
			String servletContextName, ClassLoader portletClassLoader,
			String modelName, String modelListenerClassName)
		throws Exception {

		ModelListener<?> modelListener = (ModelListener<?>)newInstance(
			portletClassLoader, ModelListener.class, modelListenerClassName);

		registerService(
			servletContextName, modelListenerClassName, ModelListener.class,
			modelListener);
	}

	protected void initModelListeners(
			String servletContextName, ClassLoader portletClassLoader,
			Properties portalProperties)
		throws Exception {

		for (Map.Entry<Object, Object> entry : portalProperties.entrySet()) {
			String key = (String)entry.getKey();

			if (!key.startsWith(VALUE_OBJECT_LISTENER)) {
				continue;
			}

			String modelName = key.substring(VALUE_OBJECT_LISTENER.length());

			String[] modelListenerClassNames = StringUtil.split(
				(String)entry.getValue());

			for (String modelListenerClassName : modelListenerClassNames) {
				initModelListener(
					servletContextName, portletClassLoader, modelName,
					modelListenerClassName);
			}
		}
	}

	protected void initPortalProperties(
			String servletContextName, ClassLoader portletClassLoader,
			Element parentElement)
		throws Exception {

		String portalPropertiesLocation = parentElement.elementText(
			"portal-properties");

		if (Validator.isNull(portalPropertiesLocation)) {
			return;
		}

		String name = portalPropertiesLocation;

		int pos = name.lastIndexOf(".properties");

		if (pos != -1) {
			name = name.substring(0, pos);
		}

		Configuration portalPropertiesConfiguration =
			ConfigurationFactoryUtil.getConfiguration(portletClassLoader, name);

		if (portalPropertiesConfiguration == null) {
			_log.error("Unable to read " + portalPropertiesLocation);

			return;
		}

		Properties portalProperties =
			portalPropertiesConfiguration.getProperties();

		if (portalProperties.isEmpty()) {
			return;
		}

		Properties unfilteredPortalProperties =
			(Properties)portalProperties.clone();

		portalProperties.remove(PropsKeys.RELEASE_INFO_BUILD_NUMBER);
		portalProperties.remove(PropsKeys.RELEASE_INFO_PREVIOUS_BUILD_NUMBER);

		_portalPropertiesMap.put(servletContextName, portalProperties);

		// Initialize properties, auto logins, model listeners, and events in
		// that specific order. Events have to be loaded last because they may
		// require model listeners to have been registered.

		initPortalProperties(
			servletContextName, portletClassLoader, portalProperties,
			unfilteredPortalProperties);
		initAuthFailures(
			servletContextName, portletClassLoader, portalProperties);
		initAutoDeployListeners(
			servletContextName, portletClassLoader, portalProperties);
		initAutoLogins(
			servletContextName, portletClassLoader, portalProperties);
		initAuthenticators(
			servletContextName, portletClassLoader, portalProperties);
		initAuthVerifiers(
			servletContextName, portletClassLoader, portalProperties);
		initFormNavigatorEntries(servletContextName, portalProperties);
		initHotDeployListeners(
			servletContextName, portletClassLoader, portalProperties);
		initModelListeners(
			servletContextName, portletClassLoader, portalProperties);
		initEvents(servletContextName, portletClassLoader, portalProperties);
	}

	protected void initPortalProperties(
			String servletContextName, ClassLoader portletClassLoader,
			Properties portalProperties, Properties unfilteredPortalProperties)
		throws Exception {

		for (String name : portalProperties.stringPropertyNames()) {
			PropsUtil.set(name, portalProperties.getProperty(name));
		}

		if (_log.isDebugEnabled() && portalProperties.containsKey(LOCALES)) {
			_log.debug(
				"Portlet locales " + portalProperties.getProperty(LOCALES));
			_log.debug("Merged locales " + PropsUtil.get(LOCALES));

			String[] locales = PropsUtil.getArray(LOCALES);

			_log.debug("Merged locales array length " + locales.length);
		}

		for (String key : _PROPS_VALUES_OBSOLETE) {
			if (_log.isInfoEnabled() && portalProperties.contains(key)) {
				_log.info("Portal property \"" + key + "\" is obsolete");
			}
		}

		resetPortalProperties(servletContextName, portalProperties, true);

		if (portalProperties.containsKey(PropsKeys.AUTH_PUBLIC_PATHS)) {
			initAuthPublicPaths(servletContextName, portalProperties);
		}

		if (portalProperties.containsKey(PropsKeys.AUTH_TOKEN_IMPL)) {
			String authTokenClassName = portalProperties.getProperty(
				PropsKeys.AUTH_TOKEN_IMPL);

			AuthToken authToken = (AuthToken)newInstance(
				portletClassLoader, AuthToken.class, authTokenClassName);

			registerService(
				servletContextName, authTokenClassName, AuthToken.class,
				authToken, "service.ranking", 1000);
		}

		if (portalProperties.containsKey(
				PropsKeys.CONTROL_PANEL_DEFAULT_ENTRY_CLASS)) {

			String controlPanelEntryClassName = portalProperties.getProperty(
				PropsKeys.CONTROL_PANEL_DEFAULT_ENTRY_CLASS);

			ControlPanelEntry controlPanelEntry =
				(ControlPanelEntry)newInstance(
					portletClassLoader, ControlPanelEntry.class,
					controlPanelEntryClassName);

			registerService(
				servletContextName, controlPanelEntryClassName,
				ControlPanelEntry.class, controlPanelEntry, "service.ranking",
				1000);
		}

		if (portalProperties.containsKey(PropsKeys.DL_STORE_IMPL)) {
			PropsValues.DL_STORE_IMPL = portalProperties.getProperty(
				PropsKeys.DL_STORE_IMPL);
		}

		if (portalProperties.containsKey(
				PropsKeys.LDAP_ATTRS_TRANSFORMER_IMPL)) {

			String attributesTransformerClassName =
				portalProperties.getProperty(
					PropsKeys.LDAP_ATTRS_TRANSFORMER_IMPL);

			AttributesTransformer attributesTransformer =
				(AttributesTransformer)newInstance(
					portletClassLoader, AttributesTransformer.class,
					attributesTransformerClassName);

			registerService(
				servletContextName, attributesTransformerClassName,
				AttributesTransformer.class, attributesTransformer,
				"service.ranking", 1000);
		}

		if (portalProperties.containsKey(LOCK_LISTENERS)) {
			String[] lockListenerClassNames = StringUtil.split(
				portalProperties.getProperty(LOCK_LISTENERS));

			for (String lockListenerClassName : lockListenerClassNames) {
				LockListener lockListener = (LockListener)newInstance(
					portletClassLoader, LockListener.class,
					lockListenerClassName);

				registerService(
					servletContextName, lockListenerClassName,
					LockListener.class, lockListener, "service.ranking", 1000);
			}
		}

		if (portalProperties.containsKey(
				PropsKeys.MEMBERSHIP_POLICY_ORGANIZATIONS)) {

			String organizationMembershipPolicyClassName =
				portalProperties.getProperty(
					PropsKeys.MEMBERSHIP_POLICY_ORGANIZATIONS);

			OrganizationMembershipPolicy organizationMembershipPolicy =
				(OrganizationMembershipPolicy)newInstance(
					portletClassLoader, OrganizationMembershipPolicy.class,
					organizationMembershipPolicyClassName);

			registerService(
				servletContextName, organizationMembershipPolicyClassName,
				OrganizationMembershipPolicy.class,
				organizationMembershipPolicy, "service.ranking", 1000);
		}

		if (portalProperties.containsKey(PropsKeys.MEMBERSHIP_POLICY_ROLES)) {
			String roleMembershipPolicyClassName = portalProperties.getProperty(
				PropsKeys.MEMBERSHIP_POLICY_ROLES);

			RoleMembershipPolicy roleMembershipPolicy =
				(RoleMembershipPolicy)newInstance(
					portletClassLoader, RoleMembershipPolicy.class,
					roleMembershipPolicyClassName);

			registerService(
				servletContextName, roleMembershipPolicyClassName,
				RoleMembershipPolicy.class, roleMembershipPolicy,
				"service.ranking", 1000);
		}

		if (portalProperties.containsKey(PropsKeys.MEMBERSHIP_POLICY_SITES)) {
			String siteMembershipPolicyClassName = portalProperties.getProperty(
				PropsKeys.MEMBERSHIP_POLICY_SITES);

			SiteMembershipPolicy siteMembershipPolicy =
				(SiteMembershipPolicy)newInstance(
					portletClassLoader, SiteMembershipPolicy.class,
					siteMembershipPolicyClassName);

			registerService(
				servletContextName, siteMembershipPolicyClassName,
				SiteMembershipPolicy.class, siteMembershipPolicy,
				"service.ranking", 1000);
		}

		if (portalProperties.containsKey(
				PropsKeys.MEMBERSHIP_POLICY_USER_GROUPS)) {

			String userGroupMembershipPolicyClassName =
				portalProperties.getProperty(
					PropsKeys.MEMBERSHIP_POLICY_USER_GROUPS);

			UserGroupMembershipPolicy userGroupMembershipPolicy =
				(UserGroupMembershipPolicy)newInstance(
					portletClassLoader, UserGroupMembershipPolicy.class,
					userGroupMembershipPolicyClassName);

			registerService(
				servletContextName, userGroupMembershipPolicyClassName,
				UserGroupMembershipPolicy.class, userGroupMembershipPolicy,
				"service.ranking", 1000);
		}

		if (portalProperties.containsKey(PropsKeys.PASSWORDS_TOOLKIT)) {
			String toolkitClassName = portalProperties.getProperty(
				PropsKeys.PASSWORDS_TOOLKIT);

			Toolkit toolkit = (Toolkit)newInstance(
				portletClassLoader, Toolkit.class, toolkitClassName);

			registerService(
				servletContextName, toolkitClassName, Toolkit.class, toolkit,
				"service.ranking", 1000);
		}

		if (portalProperties.containsKey(PropsKeys.PHONE_NUMBER_FORMAT_IMPL)) {
			String phoneNumberFormatClassName = portalProperties.getProperty(
				PropsKeys.PHONE_NUMBER_FORMAT_IMPL);

			PhoneNumberFormat phoneNumberFormat =
				(PhoneNumberFormat)newInstance(
					portletClassLoader, PhoneNumberFormat.class,
					phoneNumberFormatClassName);

			registerService(
				servletContextName, phoneNumberFormatClassName,
				PhoneNumberFormat.class, phoneNumberFormat, "service.ranking",
				1000);
		}

		if (portalProperties.containsKey(PropsKeys.SANITIZER_IMPL)) {
			String[] sanitizerClassNames = StringUtil.split(
				portalProperties.getProperty(PropsKeys.SANITIZER_IMPL));

			for (String sanitizerClassName : sanitizerClassNames) {
				Sanitizer sanitizer = (Sanitizer)newInstance(
					portletClassLoader, Sanitizer.class, sanitizerClassName);

				registerService(
					servletContextName, sanitizerClassName, Sanitizer.class,
					sanitizer, "service.ranking", 1000);
			}
		}

		if (portalProperties.containsKey(
				PropsKeys.USERS_EMAIL_ADDRESS_GENERATOR)) {

			String emailAddressGeneratorClassName =
				portalProperties.getProperty(
					PropsKeys.USERS_EMAIL_ADDRESS_GENERATOR);

			EmailAddressGenerator emailAddressGenerator =
				(EmailAddressGenerator)newInstance(
					portletClassLoader, EmailAddressGenerator.class,
					emailAddressGeneratorClassName);

			registerService(
				servletContextName, emailAddressGeneratorClassName,
				EmailAddressGenerator.class, emailAddressGenerator,
				"service.ranking", 1000);
		}

		if (portalProperties.containsKey(
				PropsKeys.USERS_EMAIL_ADDRESS_VALIDATOR)) {

			String emailAddressValidatorClassName =
				portalProperties.getProperty(
					PropsKeys.USERS_EMAIL_ADDRESS_VALIDATOR);

			EmailAddressValidator emailAddressValidator =
				(EmailAddressValidator)newInstance(
					portletClassLoader, EmailAddressValidator.class,
					emailAddressValidatorClassName);

			registerService(
				servletContextName, emailAddressValidatorClassName,
				EmailAddressValidator.class, emailAddressValidator,
				"service.ranking", 1000);
		}

		if (portalProperties.containsKey(PropsKeys.USERS_FULL_NAME_GENERATOR)) {
			String fullNameGeneratorClassName = portalProperties.getProperty(
				PropsKeys.USERS_FULL_NAME_GENERATOR);

			FullNameGenerator fullNameGenerator =
				(FullNameGenerator)newInstance(
					portletClassLoader, FullNameGenerator.class,
					fullNameGeneratorClassName);

			registerService(
				servletContextName, fullNameGeneratorClassName,
				FullNameGenerator.class, fullNameGenerator, "service.ranking",
				1000);
		}

		if (portalProperties.containsKey(PropsKeys.USERS_FULL_NAME_VALIDATOR)) {
			String fullNameValidatorClassName = portalProperties.getProperty(
				PropsKeys.USERS_FULL_NAME_VALIDATOR);

			FullNameValidator fullNameValidator =
				(FullNameValidator)newInstance(
					portletClassLoader, FullNameValidator.class,
					fullNameValidatorClassName);

			registerService(
				servletContextName, fullNameValidatorClassName,
				FullNameValidator.class, fullNameValidator, "service.ranking",
				1000);
		}

		if (portalProperties.containsKey(
				PropsKeys.USERS_SCREEN_NAME_GENERATOR)) {

			String screenNameGeneratorClassName = portalProperties.getProperty(
				PropsKeys.USERS_SCREEN_NAME_GENERATOR);

			ScreenNameGenerator screenNameGenerator =
				(ScreenNameGenerator)newInstance(
					portletClassLoader, ScreenNameGenerator.class,
					screenNameGeneratorClassName);

			registerService(
				servletContextName, screenNameGeneratorClassName,
				ScreenNameGenerator.class, screenNameGenerator,
				"service.ranking", 1000);
		}

		if (portalProperties.containsKey(
				PropsKeys.USERS_SCREEN_NAME_VALIDATOR)) {

			String screenNameValidatorClassName = portalProperties.getProperty(
				PropsKeys.USERS_SCREEN_NAME_VALIDATOR);

			ScreenNameValidator screenNameValidator =
				(ScreenNameValidator)newInstance(
					portletClassLoader, ScreenNameValidator.class,
					screenNameValidatorClassName);

			registerService(
				servletContextName, screenNameValidatorClassName,
				ScreenNameValidator.class, screenNameValidator,
				"service.ranking", 1000);
		}

		for (String tokenWhitelistName : _TOKEN_WHITELIST_NAMES) {
			if (containsKey(portalProperties, tokenWhitelistName)) {
				initTokensWhitelists(servletContextName, portalProperties);

				break;
			}
		}

		Set<String> liferayFilterClassNames =
			LiferayFilterTracker.getClassNames();

		for (String liferayFilterClassName : liferayFilterClassNames) {
			if (!portalProperties.containsKey(liferayFilterClassName)) {
				continue;
			}

			boolean filterEnabled = GetterUtil.getBoolean(
				portalProperties.getProperty(liferayFilterClassName));

			Set<LiferayFilter> liferayFilters =
				LiferayFilterTracker.getLiferayFilters(liferayFilterClassName);

			for (LiferayFilter liferayFilter : liferayFilters) {
				liferayFilter.setFilterEnabled(filterEnabled);
			}
		}
	}

	protected void initServices(
			String servletContextName, ClassLoader portletClassLoader,
			Element parentElement)
		throws Exception {

		List<Element> serviceElements = parentElement.elements("service");

		for (Element serviceElement : serviceElements) {
			String serviceType = serviceElement.elementText("service-type");
			String serviceImpl = serviceElement.elementText("service-impl");

			Class<?> serviceTypeClass = portletClassLoader.loadClass(
				serviceType);

			Class<?> serviceImplClass = portletClassLoader.loadClass(
				serviceImpl);

			Constructor<?> serviceImplConstructor =
				serviceImplClass.getConstructor(
					new Class<?>[] {serviceTypeClass});

			Object serviceProxy = null;

			try {
				serviceProxy = PortalBeanLocatorUtil.locate(serviceType);

				_initServices(
					servletContextName, serviceImplConstructor, serviceProxy);
			}
			catch (BeanLocatorException beanLocatorException) {
				if (_log.isDebugEnabled()) {
					_log.debug(beanLocatorException);
				}

				SystemBundleUtil.callService(
					serviceTypeClass,
					registryServiceProxy -> {
						try {
							_initServices(
								servletContextName, serviceImplConstructor,
								registryServiceProxy);
						}
						catch (Exception exception) {
							ReflectionUtil.throwException(exception);
						}

						return null;
					});
			}
		}
	}

	protected Filter initServletFilter(
			String filterClassName, ClassLoader portletClassLoader)
		throws Exception {

		Filter filter = (Filter)InstanceFactory.newInstance(
			portletClassLoader, filterClassName);

		List<Class<?>> interfaces = new ArrayList<>();

		if (filter instanceof TryFilter) {
			interfaces.add(TryFilter.class);
		}

		if (filter instanceof TryFinallyFilter) {
			interfaces.add(TryFinallyFilter.class);
		}

		if (filter instanceof WrapHttpServletRequestFilter) {
			interfaces.add(WrapHttpServletRequestFilter.class);
		}

		if (filter instanceof WrapHttpServletResponseFilter) {
			interfaces.add(WrapHttpServletResponseFilter.class);
		}

		if (filter instanceof LiferayFilter) {
			interfaces.add(LiferayFilter.class);
		}
		else {
			interfaces.add(Filter.class);
		}

		return (Filter)ProxyUtil.newProxyInstance(
			portletClassLoader, interfaces.toArray(new Class<?>[0]),
			new ClassLoaderBeanHandler(filter, portletClassLoader));
	}

	protected void initServletFilters(
			ServletContext servletContext, String servletContextName,
			ClassLoader portletClassLoader, Element parentElement)
		throws Exception {

		Map<String, Tuple> filterTuples = new HashMap<>();

		List<Element> servletFilterMappingElements = parentElement.elements(
			"servlet-filter-mapping");

		for (Element servletFilterMappingElement :
				servletFilterMappingElements) {

			String servletFilterName = servletFilterMappingElement.elementText(
				"servlet-filter-name");
			String afterFilter = servletFilterMappingElement.elementText(
				"after-filter");
			String beforeFilter = servletFilterMappingElement.elementText(
				"before-filter");

			List<Element> urlPatternElements =
				servletFilterMappingElement.elements("url-pattern");

			List<String> urlPatterns = new ArrayList<>();

			for (Element urlPatternElement : urlPatternElements) {
				String urlPattern = urlPatternElement.getTextTrim();

				urlPatterns.add(urlPattern);
			}

			List<Element> dispatcherElements =
				servletFilterMappingElement.elements("dispatcher");

			List<String> dispatchers = new ArrayList<>();

			for (Element dispatcherElement : dispatcherElements) {
				String dispatcher = dispatcherElement.getTextTrim();

				dispatcher = StringUtil.toUpperCase(dispatcher);

				dispatchers.add(dispatcher);
			}

			filterTuples.put(
				servletFilterName,
				new Tuple(afterFilter, beforeFilter, dispatchers, urlPatterns));
		}

		List<Element> servletFilterElements = parentElement.elements(
			"servlet-filter");

		for (Element servletFilterElement : servletFilterElements) {
			String servletFilterName = servletFilterElement.elementText(
				"servlet-filter-name");
			String servletFilterImpl = servletFilterElement.elementText(
				"servlet-filter-impl");

			List<Element> initParamElements = servletFilterElement.elements(
				"init-param");

			Map<String, Object> properties = new HashMap<>();

			for (Element initParamElement : initParamElements) {
				String paramName = initParamElement.elementText("param-name");
				String paramValue = initParamElement.elementText("param-value");

				properties.put("init-param." + paramName, paramValue);
			}

			Tuple filterTuple = filterTuples.get(servletFilterName);

			properties.put("after-filter", filterTuple.getObject(0));
			properties.put("before-filter", filterTuple.getObject(1));
			properties.put("dispatcher", filterTuple.getObject(2));

			properties.put("servlet-context-name", StringPool.BLANK);
			properties.put("servlet-filter-name", servletFilterName);
			properties.put("url-pattern", filterTuple.getObject(3));

			Filter filter = initServletFilter(
				servletFilterImpl, portletClassLoader);

			registerService(
				servletContextName, servletFilterName, Filter.class, filter,
				properties);
		}
	}

	protected void initStrutsAction(
			String servletContextName, ClassLoader portletClassLoader,
			String strutsActionPath, String strutsActionClassName)
		throws Exception {

		Object strutsActionObject = InstanceFactory.newInstance(
			portletClassLoader, strutsActionClassName);

		if (strutsActionObject instanceof StrutsAction) {
			StrutsAction strutsAction =
				_strutsActionProxyProviderFunction.apply(
					new ClassLoaderBeanHandler(
						strutsActionObject, portletClassLoader));

			registerService(
				servletContextName, strutsActionClassName, StrutsAction.class,
				strutsAction, "path", strutsActionPath);
		}
	}

	protected void initStrutsActions(
			String servletContextName, ClassLoader portletClassLoader,
			Element parentElement)
		throws Exception {

		List<Element> strutsActionElements = parentElement.elements(
			"struts-action");

		for (Element strutsActionElement : strutsActionElements) {
			String strutsActionPath = strutsActionElement.elementText(
				"struts-action-path");

			String strutsActionImpl = strutsActionElement.elementText(
				"struts-action-impl");

			initStrutsAction(
				servletContextName, portletClassLoader, strutsActionPath,
				strutsActionImpl);
		}
	}

	protected void initTokensWhitelists(
			String servletContextName, Properties portalProperties)
		throws Exception {

		for (String tokenWhitelistName : _TOKEN_WHITELIST_NAMES) {
			String propertyValue = portalProperties.getProperty(
				tokenWhitelistName);

			if (Validator.isBlank(propertyValue)) {
				continue;
			}

			registerService(
				servletContextName, tokenWhitelistName + propertyValue,
				Object.class, new Object(), tokenWhitelistName,
				StringUtil.split(propertyValue));
		}
	}

	protected <S, T> Map<S, T> newMap() {
		return new ConcurrentHashMap<>();
	}

	protected void resetPortalProperties(
			String servletContextName, Properties portalProperties,
			boolean initPhase)
		throws Exception {

		for (String key : _PROPS_VALUES_BOOLEAN) {
			String fieldName = StringUtil.replace(
				StringUtil.toUpperCase(key), CharPool.PERIOD,
				CharPool.UNDERLINE);

			if (!containsKey(portalProperties, key)) {
				continue;
			}

			try {
				Field field = PropsValues.class.getField(fieldName);

				Boolean value = Boolean.valueOf(
					GetterUtil.getBoolean(PropsUtil.get(key)));

				field.setBoolean(null, value);
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Error setting field ", fieldName, ": ",
						exception.getMessage()));
			}
		}

		for (String key : _PROPS_VALUES_INTEGER) {
			String fieldName = StringUtil.replace(
				StringUtil.toUpperCase(key), CharPool.PERIOD,
				CharPool.UNDERLINE);

			if (!containsKey(portalProperties, key)) {
				continue;
			}

			try {
				Field field = PropsValues.class.getField(fieldName);

				Integer value = Integer.valueOf(
					GetterUtil.getInteger(PropsUtil.get(key)));

				field.setInt(null, value);
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Error setting field ", fieldName, ": ",
						exception.getMessage()));
			}
		}

		for (String key : _PROPS_VALUES_LONG) {
			String fieldName = StringUtil.replace(
				StringUtil.toUpperCase(key), CharPool.PERIOD,
				CharPool.UNDERLINE);

			if (!containsKey(portalProperties, key)) {
				continue;
			}

			try {
				Field field = PropsValues.class.getField(fieldName);

				Long value = Long.valueOf(
					GetterUtil.getLong(PropsUtil.get(key)));

				field.setLong(null, value);
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Error setting field ", fieldName, ": ",
						exception.getMessage()));
			}
		}

		for (String key : _PROPS_VALUES_STRING) {
			String fieldName = StringUtil.replace(
				StringUtil.toUpperCase(key), CharPool.PERIOD,
				CharPool.UNDERLINE);

			if (!containsKey(portalProperties, key)) {
				continue;
			}

			try {
				Field field = PropsValues.class.getField(fieldName);

				String value = GetterUtil.getString(PropsUtil.get(key));

				field.set(null, value);
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Error setting field ", fieldName, ": ",
						exception.getMessage()));
			}
		}

		resetPortalPropertiesStringArray(
			servletContextName, portalProperties, initPhase,
			_PROPS_VALUES_MERGE_STRING_ARRAY, _mergeStringArraysContainerMap);

		resetPortalPropertiesStringArray(
			servletContextName, portalProperties, initPhase,
			_PROPS_VALUES_OVERRIDE_STRING_ARRAY,
			_overrideStringArraysContainerMap);

		if (containsKey(portalProperties, LOCALES) ||
			containsKey(portalProperties, LOCALES_BETA)) {

			PropsValues.LOCALES = PropsUtil.getArray(LOCALES);

			LanguageUtil.init();
		}

		if (containsKey(portalProperties, LOCALES_ENABLED)) {
			PropsValues.LOCALES_ENABLED = PropsUtil.getArray(LOCALES_ENABLED);

			LanguageUtil.init();
		}

		if (containsKey(
				portalProperties, PORTLET_INTERRUPTED_REQUEST_WHITELIST)) {

			InterruptedPortletRequestWhitelistUtil.
				resetPortletInvocationWhitelist();
		}

		if (containsKey(
				portalProperties,
				PORTLET_INTERRUPTED_REQUEST_WHITELIST_ACTIONS)) {

			InterruptedPortletRequestWhitelistUtil.
				resetPortletInvocationWhitelistActions();
		}

		JavaScriptBundleUtil.clearCache();
	}

	protected void resetPortalPropertiesStringArray(
		String servletContextName, Properties portalProperties,
		boolean initPhase, String[] propsValuesStringArray,
		Map<String, StringArraysContainer> stringArraysContainerMap) {

		for (String key : propsValuesStringArray) {
			String fieldName = StringUtil.replace(
				StringUtil.toUpperCase(key), CharPool.PERIOD,
				CharPool.UNDERLINE);

			if (!containsKey(portalProperties, key)) {
				continue;
			}

			try {
				resetPortalPropertiesStringArray(
					servletContextName, portalProperties, initPhase,
					propsValuesStringArray, stringArraysContainerMap, key,
					fieldName);
			}
			catch (Exception exception) {
				_log.error(
					StringBundler.concat(
						"Error setting field ", fieldName, ": ",
						exception.getMessage()));
			}
		}
	}

	protected void resetPortalPropertiesStringArray(
			String servletContextName, Properties portalProperties,
			boolean initPhase, String[] propsValuesStringArray,
			Map<String, StringArraysContainer> stringArraysContainerMap,
			String key, String fieldName)
		throws Exception {

		Field field = PropsValues.class.getField(fieldName);

		StringArraysContainer stringArraysContainer =
			stringArraysContainerMap.get(key);

		String[] value = null;

		if (initPhase) {
			if (stringArraysContainer instanceof
					OverrideStringArraysContainer) {

				OverrideStringArraysContainer overrideStringArraysContainer =
					(OverrideStringArraysContainer)stringArraysContainer;

				if (overrideStringArraysContainer.isOverridden()) {
					_log.error("Error setting overridden field " + fieldName);

					return;
				}

				value = StringUtil.split(portalProperties.getProperty(key));
			}
			else {
				value = PropsUtil.getArray(key);
			}
		}

		stringArraysContainer.setPluginStringArray(servletContextName, value);

		value = stringArraysContainer.getStringArray();

		if (stringArraysContainer instanceof MergeStringArraysContainer) {
			Properties properties = new Properties();

			String valueString = StringUtil.merge(value, StringPool.COMMA);

			properties.setProperty(key, valueString);

			for (String name : properties.stringPropertyNames()) {
				PropsUtil.set(name, properties.getProperty(name));
			}
		}

		if (!_propsKeysEvents.contains(key)) {
			field.set(null, value);
		}
	}

	private void _initServices(
			String servletContextName, Constructor<?> serviceImplConstructor,
			Object serviceProxy)
		throws ReflectiveOperationException {

		Class<?> proxyClass = serviceProxy.getClass();

		if (ProxyUtil.isProxyClass(proxyClass)) {
			AopInvocationHandler aopInvocationHandler =
				ProxyUtil.fetchInvocationHandler(
					serviceProxy, AopInvocationHandler.class);

			Object previousService = aopInvocationHandler.getTarget();

			ServiceWrapper<?> serviceWrapper =
				(ServiceWrapper<?>)serviceImplConstructor.newInstance(
					previousService);

			registerService(
				servletContextName, serviceImplConstructor,
				ServiceWrapper.class, serviceWrapper);
		}
		else {
			_log.error(
				"Service hooks require Spring to be configured to use " +
					"JdkDynamicProxy and will not work with CGLIB");
		}
	}

	private static final String[] _PROPS_KEYS_EVENTS = {
		LOGIN_EVENTS_POST, LOGIN_EVENTS_PRE, LOGOUT_EVENTS_POST,
		LOGOUT_EVENTS_PRE, SERVLET_SERVICE_EVENTS_POST,
		SERVLET_SERVICE_EVENTS_PRE
	};

	private static final String[] _PROPS_KEYS_SESSION_EVENTS = {
		SERVLET_SESSION_CREATE_EVENTS, SERVLET_SESSION_DESTROY_EVENTS
	};

	private static final String[] _PROPS_VALUES_BOOLEAN = {
		"auth.forward.by.last.path", "dl.file.entry.drafts.enabled",
		"dl.store.antivirus.enabled",
		"field.enable.com.liferay.portal.kernel.model.Contact.birthday",
		"field.enable.com.liferay.portal.kernel.model.Contact.male",
		"field.enable.com.liferay.portal.kernel.model.Organization.status",
		"javascript.fast.load", "layout.template.cache.enabled",
		"layout.user.private.layouts.auto.create",
		"layout.user.private.layouts.enabled",
		"layout.user.private.layouts.power.user.required",
		"layout.user.public.layouts.auto.create",
		"layout.user.public.layouts.enabled",
		"layout.user.public.layouts.power.user.required",
		"login.create.account.allow.custom.password", "login.dialog.disabled",
		"my.sites.show.private.sites.with.no.layouts",
		"my.sites.show.public.sites.with.no.layouts",
		"my.sites.show.user.private.sites.with.no.layouts",
		"my.sites.show.user.public.sites.with.no.layouts",
		"portlet.add.default.resource.check.enabled", "rss.feeds.enabled",
		"session.store.password", "social.activity.sets.bundling.enabled",
		"social.activity.sets.enabled", "terms.of.use.required",
		"theme.css.fast.load", "theme.images.fast.load",
		"theme.jsp.override.enabled", "theme.loader.new.theme.id.on.import",
		"theme.portlet.decorate.default", "theme.portlet.sharing.default",
		"user.notification.event.confirmation.enabled",
		"users.email.address.required", "users.screen.name.always.autogenerate"
	};

	private static final String[] _PROPS_VALUES_INTEGER = {
		"session.max.allowed"
	};

	private static final String[] _PROPS_VALUES_LONG = {};

	private static final String[] _PROPS_VALUES_MERGE_STRING_ARRAY = {
		"auth.token.ignore.actions", "auth.token.ignore.origins",
		"auth.token.ignore.portlets", "admin.default.group.names",
		"admin.default.role.names", "admin.default.user.group.names",
		"asset.publisher.display.styles",
		"company.settings.form.authentication",
		"company.settings.form.configuration",
		"company.settings.form.identification",
		"company.settings.form.miscellaneous", "company.settings.form.social",
		"layout.form.add", "layout.form.update", "layout.set.form.update",
		"layout.static.portlets.all", "login.events.post", "login.events.pre",
		"login.form.navigation.post", "login.form.navigation.pre",
		"logout.events.pre", "logout.events.post",
		"organizations.form.add.identification", "organizations.form.add.main",
		"organizations.form.add.miscellaneous",
		"portlet.add.default.resource.check.whitelist",
		"portlet.add.default.resource.check.whitelist.actions",
		"portlet.interrupted.request.whitelist",
		"portlet.interrupted.request.whitelist.actions",
		"servlet.service.events.post", "servlet.service.events.pre",
		"session.phishing.protected.attributes", "sites.form.add.advanced",
		"sites.form.add.main", "sites.form.add.miscellaneous",
		"sites.form.add.seo", "sites.form.update.advanced",
		"sites.form.update.main", "sites.form.update.miscellaneous",
		"sites.form.update.seo", "users.form.add.identification",
		"users.form.add.main", "users.form.add.miscellaneous",
		"users.form.my.account.identification", "users.form.my.account.main",
		"users.form.my.account.miscellaneous",
		"users.form.update.identification", "users.form.update.main",
		"users.form.update.miscellaneous"
	};

	private static final String[] _PROPS_VALUES_OBSOLETE = {
		"asset.publisher.asset.entry.query.processors",
		"asset.publisher.display.styles", "captcha.check.portal.create_account",
		"control.panel.entry.class.default", "dl.store.antivirus.impl",
		"journal.article.form.add", "journal.article.form.translate",
		"journal.article.form.update", "layout.types",
		"layout.user.private.layouts.modifiable",
		"layout.user.public.layouts.modifiable",
		"social.bookmark.display.styles", "social.bookmark.types",
		"users.image.check.token", "users.image.max.height",
		"users.image.max.width"
	};

	private static final String[] _PROPS_VALUES_OVERRIDE_STRING_ARRAY = {
		"locales.beta"
	};

	private static final String[] _PROPS_VALUES_STRING = {
		"company.default.locale", "company.default.time.zone",
		"default.landing.page.path", "default.regular.color.scheme.id",
		"default.regular.theme.id", "passwords.passwordpolicytoolkit.generator",
		"passwords.passwordpolicytoolkit.static",
		"phone.number.format.international.regexp",
		"phone.number.format.usa.regexp", "social.activity.sets.selector",
		"theme.shortcut.icon"
	};

	private static final String[] _TOKEN_WHITELIST_NAMES = {
		AUTH_TOKEN_IGNORE_ACTIONS, AUTH_TOKEN_IGNORE_ORIGINS,
		AUTH_TOKEN_IGNORE_PORTLETS,
		PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST,
		PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_ACTIONS
	};

	private static final Log _log = LogFactoryUtil.getLog(
		HookHotDeployListener.class);

	private static final Function<InvocationHandler, StrutsAction>
		_strutsActionProxyProviderFunction = ProxyUtil.getProxyProviderFunction(
			StrutsAction.class);

	private final Map<String, HotDeployListenersContainer>
		_hotDeployListenersContainerMap = new HashMap<>();
	private final Map<String, StringArraysContainer>
		_mergeStringArraysContainerMap = new HashMap<>();
	private final Map<String, StringArraysContainer>
		_overrideStringArraysContainerMap = new HashMap<>();
	private final Map<String, Properties> _portalPropertiesMap =
		new HashMap<>();
	private final Set<String> _propsKeysEvents = SetUtil.fromArray(
		_PROPS_KEYS_EVENTS);
	private final Set<String> _propsKeysSessionEvents = SetUtil.fromArray(
		_PROPS_KEYS_SESSION_EVENTS);
	private final Map<String, Map<Object, ServiceRegistration<?>>>
		_serviceRegistrations = newMap();
	private final Set<String> _servletContextNames = new HashSet<>();

	private static class HotDeployListenersContainer {

		public void registerHotDeployListener(
			HotDeployListener hotDeployListener) {

			HotDeployUtil.registerListener(hotDeployListener);

			_hotDeployListeners.add(hotDeployListener);
		}

		public void unregisterHotDeployListeners() {
			for (HotDeployListener hotDeployListener : _hotDeployListeners) {
				HotDeployUtil.unregisterListener(hotDeployListener);
			}
		}

		private final List<HotDeployListener> _hotDeployListeners =
			new ArrayList<>();

	}

	private static class MergeStringArraysContainer
		implements StringArraysContainer {

		@Override
		public String[] getStringArray() {
			Set<String> mergedStringSet = new LinkedHashSet<>();

			Collections.addAll(mergedStringSet, _portalStringArray);

			for (Map.Entry<String, String[]> entry :
					_pluginStringArrayMap.entrySet()) {

				Collections.addAll(mergedStringSet, entry.getValue());
			}

			return mergedStringSet.toArray(new String[0]);
		}

		@Override
		public void setPluginStringArray(
			String servletContextName, String[] pluginStringArray) {

			if (pluginStringArray != null) {
				_pluginStringArrayMap.put(
					servletContextName, pluginStringArray);
			}
			else {
				_pluginStringArrayMap.remove(servletContextName);
			}
		}

		private MergeStringArraysContainer(String key) {
			_portalStringArray = PropsUtil.getArray(key);
		}

		private final Map<String, String[]> _pluginStringArrayMap =
			new HashMap<>();
		private String[] _portalStringArray;

	}

	private static class OverrideStringArraysContainer
		implements StringArraysContainer {

		@Override
		public String[] getStringArray() {
			if (_pluginStringArray != null) {
				return _pluginStringArray;
			}

			return _portalStringArray;
		}

		public boolean isOverridden() {
			return Validator.isNotNull(_servletContextName);
		}

		@Override
		public void setPluginStringArray(
			String servletContextName, String[] pluginStringArray) {

			if (pluginStringArray != null) {
				if (!isOverridden()) {
					_servletContextName = servletContextName;
					_pluginStringArray = pluginStringArray;
				}
			}
			else {
				if (_servletContextName.equals(servletContextName)) {
					_servletContextName = null;
					_pluginStringArray = null;
				}
			}
		}

		private OverrideStringArraysContainer(String key) {
			_portalStringArray = PropsUtil.getArray(key);
		}

		private String[] _pluginStringArray;
		private String[] _portalStringArray;
		private String _servletContextName;

	}

	private interface StringArraysContainer {

		public String[] getStringArray();

		public void setPluginStringArray(
			String servletContextName, String[] pluginStringArray);

	}

}