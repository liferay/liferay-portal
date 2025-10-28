/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;

import java.util.List;
import java.util.Map;

/**
 * The base implementation of {@link FriendlyURLMapper}.
 *
 * <p>
 * Typically not subclassed directly. {@link DefaultFriendlyURLMapper} and a
 * <code>friendly-url-routes.xml</code> file will handle the needs of most
 * portlets.
 * </p>
 *
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 * @author Connor McKay
 * @see    DefaultFriendlyURLMapper
 */
public abstract class BaseFriendlyURLMapper implements FriendlyURLMapper {

	@Override
	public String getMapping() {
		return _mapping;
	}

	@Override
	public String getPortletId() {
		return _portletId;
	}

	@Override
	public Router getRouter() {
		return router;
	}

	@Override
	public void init(Portlet portlet) {
		if (_initialized) {
			return;
		}

		synchronized (this) {
			if (_initialized) {
				return;
			}

			try {
				_mapping = portlet.getFriendlyURLMapping(false);
				_portletId = portlet.getPortletId();
				_portletInstanceable = portlet.isInstanceable();

				String friendlyURLRoutes = _friendlyURLRoutes;

				if (Validator.isNotNull(portlet.getFriendlyURLRoutes())) {
					friendlyURLRoutes = portlet.getFriendlyURLRoutes();
				}

				String xml = null;

				if (Validator.isNotNull(friendlyURLRoutes)) {
					Class<?> clazz = getClass();

					xml = _getContent(
						clazz.getClassLoader(), friendlyURLRoutes);
				}

				router = _newFriendlyURLRouter(xml);
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			_initialized = true;
		}
	}

	@Override
	public boolean isCheckMappingWithPrefix() {
		return _CHECK_MAPPING_WITH_PREFIX;
	}

	@Override
	public boolean isPortletInstanceable() {
		return _portletInstanceable;
	}

	@Override
	public void setFriendlyURLRoutes(String friendlyURLRoutes) {
		_friendlyURLRoutes = friendlyURLRoutes;
	}

	/**
	 * Adds a default namespaced parameter of any type to the parameter map.
	 *
	 * <p>
	 * <b>Do not use this method with an instanceable portlet, it will not
	 * properly namespace parameter names.</b>
	 * </p>
	 *
	 * @param parameterMap the parameter map
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @see   #addParameter(Map, String, String)
	 */
	protected void addParameter(
		Map<String, String[]> parameterMap, String name, Object value) {

		addParameter(getNamespace(), parameterMap, name, String.valueOf(value));
	}

	/**
	 * Adds a default namespaced string parameter to the parameter map.
	 *
	 * <p>
	 * <b>Do not use this method with an instanceable portlet, it will not
	 * properly namespace parameter names.</b>
	 * </p>
	 *
	 * @param parameterMap the parameter map
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @see   #getNamespace()
	 */
	protected void addParameter(
		Map<String, String[]> parameterMap, String name, String value) {

		addParameter(getNamespace(), parameterMap, name, new String[] {value});
	}

	/**
	 * Adds a default namespaced string parameter to the parameter map.
	 *
	 * <p>
	 * <b>Do not use this method with an instanceable portlet, it will not
	 * properly namespace parameter names.</b>
	 * </p>
	 *
	 * @param parameterMap the parameter map
	 * @param name the name of the parameter
	 * @param values the values of the parameter
	 * @see   #getNamespace()
	 */
	protected void addParameter(
		Map<String, String[]> parameterMap, String name, String[] values) {

		addParameter(getNamespace(), parameterMap, name, values);
	}

	/**
	 * Adds a namespaced parameter of any type to the parameter map.
	 *
	 * @param namespace the namespace for portlet parameters. For instanceable
	 *        portlets this must include the instance ID.
	 * @param parameterMap the parameter map
	 * @param name space the namespace for portlet parameters. For instanceable
	 *        portlets this must include the instance ID.
	 * @param value the value of the parameter
	 * @see   #addParameter(String, Map, String, String)
	 */
	protected void addParameter(
		String namespace, Map<String, String[]> parameterMap, String name,
		Object value) {

		addParameter(
			namespace, parameterMap, name,
			new String[] {String.valueOf(value)});
	}

	/**
	 * Adds a namespaced string parameter to the parameter map.
	 *
	 * @param namespace the namespace for portlet parameters. For instanceable
	 *        portlets this must include the instance ID.
	 * @param parameterMap the parameter map
	 * @param name space the namespace for portlet parameters. For instanceable
	 *        portlets this must include the instance ID.
	 * @param value the value of the parameter
	 * @see   PortalUtil#getPortletNamespace(String)
	 * @see   DefaultFriendlyURLMapper#getPortletId(Map)
	 */
	protected void addParameter(
		String namespace, Map<String, String[]> parameterMap, String name,
		String value) {

		addParameter(namespace, parameterMap, name, new String[] {value});
	}

	/**
	 * Adds a namespaced string parameter to the parameter map.
	 *
	 * @param namespace the namespace for portlet parameters. For instanceable
	 *        portlets this must include the instance ID.
	 * @param parameterMap the parameter map
	 * @param name space the namespace for portlet parameters. For instanceable
	 *        portlets this must include the instance ID.
	 * @param values the values of the parameter
	 * @see   PortalUtil#getPortletNamespace(String)
	 * @see   DefaultFriendlyURLMapper#getPortletId(Map)
	 */
	protected void addParameter(
		String namespace, Map<String, String[]> parameterMap, String name,
		String[] values) {

		try {
			if (!PortalUtil.isReservedParameter(name)) {
				Map<String, String> prpIdentifiers =
					FriendlyURLMapperThreadLocal.getPRPIdentifiers();

				String identiferValue = prpIdentifiers.get(name);

				if (identiferValue != null) {
					name = identiferValue;
				}
				else if (Validator.isNotNull(namespace)) {
					name = namespace.concat(name);
				}
			}

			parameterMap.put(name, values);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	/**
	 * Returns the default namespace.
	 *
	 * <p>
	 * <b>Do not use this method with an instanceable portlet, it will not
	 * include the instance ID.</b>
	 * </p>
	 *
	 * @return the default namespace, not including the instance ID
	 * @see    PortalUtil#getPortletNamespace(String)
	 */
	protected String getNamespace() {
		return PortalUtil.getPortletNamespace(getPortletId());
	}

	protected Router router;

	private String _getContent(ClassLoader classLoader, String fileName)
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

	private Router _newFriendlyURLRouter(String xml) throws Exception {
		if (Validator.isNull(xml)) {
			return null;
		}

		Document document = UnsecureSAXReaderUtil.read(xml, true);

		Element rootElement = document.getRootElement();

		List<Element> routeElements = rootElement.elements("route");

		Router router = new Router(routeElements.size());

		for (Element routeElement : routeElements) {
			String pattern = routeElement.elementText("pattern");

			Route route = router.addRoute(pattern);

			for (Element generatedParameterElement :
					routeElement.elements("generated-parameter")) {

				String name = generatedParameterElement.attributeValue("name");
				String value = generatedParameterElement.getText();

				route.addGeneratedParameter(name, value);
			}

			for (Element ignoredParameterElement :
					routeElement.elements("ignored-parameter")) {

				String name = ignoredParameterElement.attributeValue("name");

				route.addIgnoredParameter(name);
			}

			for (Element implicitParameterElement :
					routeElement.elements("implicit-parameter")) {

				String name = implicitParameterElement.attributeValue("name");
				String value = implicitParameterElement.getText();

				route.addImplicitParameter(name, value);
			}

			for (Element overriddenParameterElement :
					routeElement.elements("overridden-parameter")) {

				String name = overriddenParameterElement.attributeValue("name");
				String value = overriddenParameterElement.getText();

				route.addOverriddenParameter(name, value);
			}
		}

		return router;
	}

	private static final boolean _CHECK_MAPPING_WITH_PREFIX = true;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseFriendlyURLMapper.class);

	private String _friendlyURLRoutes;
	private volatile boolean _initialized;
	private String _mapping;
	private String _portletId;
	private boolean _portletInstanceable;

}