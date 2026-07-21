/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.velocity.internal;

import com.liferay.petra.lang.ClassLoaderPool;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManager;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.TemplateResourceCache;
import com.liferay.portal.kernel.template.TemplateResourceLoader;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.template.BaseTemplateResourceCache;
import com.liferay.portal.template.BaseTemplateResourceLoader;
import com.liferay.portal.template.engine.BaseTemplateManager;
import com.liferay.portal.template.engine.TemplateContextHelper;
import com.liferay.portal.template.velocity.configuration.VelocityEngineConfiguration;

import java.util.Map;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 * @author Peter Fellwock
 */
@Component(
	configurationPid = "com.liferay.portal.template.velocity.configuration.VelocityEngineConfiguration",
	property = "language.type=" + TemplateConstants.LANG_TYPE_VM,
	service = TemplateManager.class
)
public class VelocityManager extends BaseTemplateManager {

	@Override
	public String getName() {
		return TemplateConstants.LANG_TYPE_VM;
	}

	@Override
	public String[] getRestrictedVariables() {
		return _velocityEngineConfiguration.restrictedVariables();
	}

	public class VelocityTemplateResourceCache
		extends BaseTemplateResourceCache {

		public VelocityTemplateResourceCache() {
			init(
				Long.MIN_VALUE, _portalCacheName,
				StringBundler.concat(
					TemplateResource.class.getName(), StringPool.POUND,
					TemplateConstants.LANG_TYPE_VM));
		}

		public void destroy() {
			super.destroy();
		}

		private final String _portalCacheName =
			VelocityManager.VelocityTemplateResourceCache.class.getName();

	}

	public class VelocityTemplateResourceLoader
		extends BaseTemplateResourceLoader {

		public VelocityTemplateResourceLoader(
			BundleContext bundleContext,
			TemplateResourceCache templateResourceCache) {

			init(
				bundleContext, TemplateConstants.LANG_TYPE_VM,
				templateResourceCache);
		}

		public void destroy() {
			super.destroy();
		}

	}

	@Activate
	protected void activate(
			BundleContext bundleContext, Map<String, Object> properties)
		throws TemplateException {

		_velocityEngineConfiguration = ConfigurableUtil.createConfigurable(
			VelocityEngineConfiguration.class, properties);

		_velocityTemplateResourceCache = new VelocityTemplateResourceCache();

		_velocityTemplateResourceLoader = new VelocityTemplateResourceLoader(
			bundleContext, _velocityTemplateResourceCache);

		_templateResourceLoaderServiceRegistration =
			bundleContext.registerService(
				TemplateResourceLoader.class, _velocityTemplateResourceLoader,
				null);

		_init();
	}

	@Deactivate
	protected void deactivate() {
		_destroy();

		_templateResourceLoaderServiceRegistration.unregister();

		_velocityTemplateResourceCache.destroy();

		_velocityTemplateResourceLoader.destroy();
	}

	@Override
	protected Template doGetTemplate(
		TemplateResource templateResource, boolean restricted,
		Map<String, Object> helperUtilities) {

		return new VelocityTemplate(
			templateResource, helperUtilities, _velocityEngine,
			_templateContextHelper, _velocityTemplateResourceCache, restricted);
	}

	@Override
	protected TemplateContextHelper getTemplateContextHelper() {
		return _templateContextHelper;
	}

	@Modified
	protected void modified(Map<String, Object> properties)
		throws TemplateException {

		_velocityEngineConfiguration = ConfigurableUtil.createConfigurable(
			VelocityEngineConfiguration.class, properties);

		_destroy();

		_init();
	}

	private void _destroy() {
		if (_velocityEngine == null) {
			return;
		}

		_velocityEngine = null;

		_templateContextHelper.removeAllHelperUtilities();
	}

	private String _getVelocimacroLibrary(Class<?> clazz) {
		String contextName = ClassLoaderPool.getContextName(
			clazz.getClassLoader());

		contextName = contextName.concat(
			TemplateConstants.CLASS_LOADER_SEPARATOR);

		String[] velocimacroLibrary =
			_velocityEngineConfiguration.velocimacroLibrary();

		StringBundler sb = new StringBundler(3 * velocimacroLibrary.length);

		for (String library : velocimacroLibrary) {
			sb.append(contextName);
			sb.append(library);
			sb.append(StringPool.COMMA);
		}

		if (velocimacroLibrary.length > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private void _init() throws TemplateException {
		if (_velocityEngine != null) {
			return;
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				VelocityManager.class.getClassLoader())) {

			_velocityEngine = new VelocityEngine();

			ExtendedProperties extendedProperties =
				new FastExtendedProperties();

			extendedProperties.setProperty(
				RuntimeConstants.INTROSPECTOR_RESTRICT_CLASSES,
				StringUtil.merge(
					_velocityEngineConfiguration.restrictedClasses()));
			extendedProperties.setProperty(
				RuntimeConstants.INTROSPECTOR_RESTRICT_PACKAGES,
				StringUtil.merge(
					_velocityEngineConfiguration.restrictedPackages()));
			extendedProperties.setProperty(
				RuntimeConstants.PARSER_POOL_CLASS,
				VelocityParserPool.class.getName());
			extendedProperties.setProperty(
				RuntimeConstants.UBERSPECT_CLASSNAME,
				LiferaySecureUberspector.class.getName());
			extendedProperties.setProperty(
				VelocityEngine.DIRECTIVE_IF_TOSTRING_NULLCHECK,
				String.valueOf(
					_velocityEngineConfiguration.
						directiveIfToStringNullCheck()));
			extendedProperties.setProperty(
				VelocityEngine.EVENTHANDLER_METHODEXCEPTION,
				LiferayMethodExceptionEventHandler.class.getName());
			extendedProperties.setProperty(
				VelocityEngine.RESOURCE_LOADER, "liferay");
			extendedProperties.setProperty(
				VelocityEngine.RESOURCE_MANAGER_CLASS,
				LiferayResourceManager.class.getName());
			extendedProperties.setProperty(
				VelocityEngine.RUNTIME_LOG_LOGSYSTEM + ".log4j.category",
				_velocityEngineConfiguration.loggerCategory());
			extendedProperties.setProperty(
				VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS,
				_velocityEngineConfiguration.logger());
			extendedProperties.setProperty(
				VelocityEngine.VM_LIBRARY,
				_getVelocimacroLibrary(VelocityManager.class));
			extendedProperties.setProperty(
				VelocityEngine.VM_LIBRARY_AUTORELOAD,
				String.valueOf(!_velocityTemplateResourceCache.isEnabled()));
			extendedProperties.setProperty(
				VelocityEngine.VM_PERM_ALLOW_INLINE_REPLACE_GLOBAL,
				String.valueOf(!_velocityTemplateResourceCache.isEnabled()));
			extendedProperties.setProperty(
				VelocityManager.VelocityTemplateResourceLoader.class.getName(),
				_velocityTemplateResourceLoader);
			extendedProperties.setProperty(
				"liferay." + RuntimeConstants.INTROSPECTOR_RESTRICT_CLASSES +
					".methods",
				_velocityEngineConfiguration.restrictedMethods());
			extendedProperties.setProperty(
				StringBundler.concat(
					"liferay.", VelocityEngine.RESOURCE_LOADER, ".",
					VelocityManager.VelocityTemplateResourceLoader.class.
						getName()),
				_velocityTemplateResourceLoader);
			extendedProperties.setProperty(
				"liferay." + VelocityEngine.RESOURCE_LOADER + ".cache",
				String.valueOf(_velocityTemplateResourceCache.isEnabled()));
			extendedProperties.setProperty(
				"liferay." + VelocityEngine.RESOURCE_LOADER + ".class",
				LiferayResourceLoader.class.getName());
			extendedProperties.setProperty(
				"liferay." + VelocityEngine.RESOURCE_LOADER + "portal.cache",
				_velocityTemplateResourceCache.getSecondLevelPortalCache());

			_velocityEngine.setExtendedProperties(extendedProperties);

			_velocityEngine.init();
		}
		catch (Exception exception) {
			throw new TemplateException(exception);
		}
	}

	private static volatile VelocityEngineConfiguration
		_velocityEngineConfiguration;

	@Reference(
		target = "(component.name=com.liferay.portal.template.velocity.internal.helper.VelocityTemplateContextHelper)"
	)
	private TemplateContextHelper _templateContextHelper;

	private ServiceRegistration<TemplateResourceLoader>
		_templateResourceLoaderServiceRegistration;
	private VelocityEngine _velocityEngine;
	private VelocityTemplateResourceCache _velocityTemplateResourceCache;
	private volatile VelocityTemplateResourceLoader
		_velocityTemplateResourceLoader;

}