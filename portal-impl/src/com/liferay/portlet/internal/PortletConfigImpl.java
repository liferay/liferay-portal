/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PublicRenderParameter;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.portlet.PortletModeFactory;
import com.liferay.portal.kernel.portlet.WindowStateFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portlet.StrutsResourceBundle;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.WindowState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 * @author Shuyang Zhou
 * @author Neil Griffin
 */
public class PortletConfigImpl implements LiferayPortletConfig {

	public PortletConfigImpl(Portlet portlet, PortletContext portletContext) {
		_portlet = portlet;
		_portletContext = portletContext;

		_portletInfos = PortletResourceBundle.getPortletInfos(
			portlet.getPortletInfo());

		_copyRequestParameters = GetterUtil.getBoolean(
			getInitParameter("copy-request-parameters"));
		_portletApp = portlet.getPortletApp();

		String portletName = portlet.getPortletName();

		if (portletName == null) {
			portletName = portlet.getRootPortletId();
		}

		int pos = portletName.indexOf(PortletConstants.WAR_SEPARATOR);

		if (pos != -1) {
			portletName = portletName.substring(0, pos);
		}

		_portletName = portletName;

		String className = LiferayPortletConfig.class.getName();

		_containerRuntimeOptionPrefix = className.concat(_portletName);
	}

	@Override
	public Map<String, String[]> getContainerRuntimeOptions() {
		Map<String, String[]> portletAppContainerRuntimeOptions =
			_portletApp.getContainerRuntimeOptions();

		Map<String, String[]> containerRuntimeOptions = new HashMap<>();

		String className = LiferayPortletConfig.class.getName();

		for (Map.Entry<String, String[]> portletAppContainerRuntimeOption :
				portletAppContainerRuntimeOptions.entrySet()) {

			String name = portletAppContainerRuntimeOption.getKey();

			if (!name.startsWith(className)) {
				containerRuntimeOptions.put(
					name, portletAppContainerRuntimeOption.getValue());
			}
		}

		// PLT 8.4: If the deployment descriptor contains
		// <container-runtime-option> elements of the same name defined at both
		// the portlet and the portlet application levels, the returned Map
		// contains a the value defined at the portlet level.

		for (Map.Entry<String, String[]> portletAppContainerRuntimeOption :
				portletAppContainerRuntimeOptions.entrySet()) {

			String name = portletAppContainerRuntimeOption.getKey();

			if (name.startsWith(_containerRuntimeOptionPrefix)) {
				containerRuntimeOptions.put(
					name.substring(_containerRuntimeOptionPrefix.length()),
					portletAppContainerRuntimeOption.getValue());
			}
		}

		Set<String> keySet = containerRuntimeOptions.keySet();

		keySet.retainAll(
			SetUtil.fromEnumeration(
				_portletContext.getContainerRuntimeOptions()));

		return Collections.unmodifiableMap(containerRuntimeOptions);
	}

	@Override
	public String getDefaultNamespace() {
		return _portletApp.getDefaultNamespace();
	}

	@Override
	public String getInitParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		Map<String, String> initParams = _portlet.getInitParams();

		return initParams.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		Map<String, String> initParams = _portlet.getInitParams();

		return Collections.enumeration(initParams.keySet());
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public PortletContext getPortletContext() {
		return _portletContext;
	}

	@Override
	public String getPortletId() {
		return _portlet.getPortletId();
	}

	@Override
	public Enumeration<PortletMode> getPortletModes(String mimeType) {
		Map<String, Set<String>> portletModeMap = _portlet.getPortletModes();

		Set<String> portletModeNames = portletModeMap.get(mimeType);

		if (portletModeNames == null) {
			return Collections.emptyEnumeration();
		}

		List<PortletMode> portletModes = new ArrayList<>(
			portletModeNames.size());

		for (String portletModeName : portletModeNames) {
			portletModes.add(
				PortletModeFactory.getPortletMode(
					portletModeName, _portletApp.getSpecMajorVersion()));
		}

		return Collections.enumeration(portletModes);
	}

	@Override
	public String getPortletName() {
		return _portletName;
	}

	@Override
	public Enumeration<QName> getProcessingEventQNames() {
		return Collections.enumeration(
			toJavaxQNames(_portlet.getProcessingEvents()));
	}

	@Override
	public Map<String, QName> getPublicRenderParameterDefinitions() {
		Map<String, QName> publicRenderParameterDefinitions = new HashMap<>();

		for (PublicRenderParameter publicRenderParameter :
				_portlet.getPublicRenderParameters()) {

			com.liferay.portal.kernel.xml.QName qName =
				publicRenderParameter.getQName();

			publicRenderParameterDefinitions.put(
				publicRenderParameter.getIdentifier(),
				new QName(
					qName.getNamespaceURI(), qName.getLocalPart(),
					qName.getNamespacePrefix()));
		}

		return publicRenderParameterDefinitions;
	}

	@Override
	public Enumeration<String> getPublicRenderParameterNames() {
		List<String> publicRenderParameterNames = new ArrayList<>();

		for (PublicRenderParameter publicRenderParameter :
				_portlet.getPublicRenderParameters()) {

			publicRenderParameterNames.add(
				publicRenderParameter.getIdentifier());
		}

		return Collections.enumeration(publicRenderParameterNames);
	}

	@Override
	public Enumeration<QName> getPublishingEventQNames() {
		return Collections.enumeration(
			toJavaxQNames(_portlet.getPublishingEvents()));
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundle resourceBundle = _resourceBundles.get(locale);

		if (resourceBundle != null) {
			return resourceBundle;
		}

		String portletResourceBundle = _portlet.getResourceBundle();

		if (Validator.isNull(portletResourceBundle)) {
			resourceBundle = LanguageResources.getResourceBundle(locale);
		}
		else if (!_portletApp.isWARFile() &&
				 portletResourceBundle.equals(
					 StrutsResourceBundle.class.getName())) {

			resourceBundle = new StrutsResourceBundle(_portletName, locale);
		}
		else {
			PortletBag portletBag = PortletBagPool.get(
				_portlet.getRootPortletId());

			if (portletBag != null) {
				resourceBundle = portletBag.getResourceBundle(locale);
			}
		}

		resourceBundle = new PortletResourceBundle(
			resourceBundle, _portletInfos);

		_resourceBundles.put(locale, resourceBundle);

		return resourceBundle;
	}

	@Override
	public Enumeration<Locale> getSupportedLocales() {
		List<Locale> supportedLocales = new ArrayList<>();

		for (String languageId : _portlet.getSupportedLocales()) {
			supportedLocales.add(LocaleUtil.fromLanguageId(languageId));
		}

		return Collections.enumeration(supportedLocales);
	}

	@Override
	public Enumeration<WindowState> getWindowStates(String mimeType) {
		Map<String, Set<String>> windowStateMap = _portlet.getWindowStates();

		Set<String> windowStateNames = windowStateMap.get(mimeType);

		if (windowStateNames == null) {
			return Collections.emptyEnumeration();
		}

		List<WindowState> windowStates = new ArrayList<>(
			windowStateNames.size());

		for (String windowStateName : windowStateNames) {
			windowStates.add(
				WindowStateFactory.getWindowState(
					windowStateName, _portletApp.getSpecMajorVersion()));
		}

		return Collections.enumeration(windowStates);
	}

	@Override
	public boolean isCopyRequestParameters() {
		return _copyRequestParameters;
	}

	@Override
	public boolean isWARFile() {
		return _portletApp.isWARFile();
	}

	protected Set<QName> toJavaxQNames(
		Set<com.liferay.portal.kernel.xml.QName> liferayQNames) {

		Set<QName> javaxQNames = new HashSet<>();

		for (com.liferay.portal.kernel.xml.QName liferayQName : liferayQNames) {
			QName javaxQName = new QName(
				liferayQName.getNamespaceURI(), liferayQName.getLocalPart(),
				liferayQName.getNamespacePrefix());

			javaxQNames.add(javaxQName);
		}

		return javaxQNames;
	}

	private final String _containerRuntimeOptionPrefix;
	private final boolean _copyRequestParameters;
	private final Portlet _portlet;
	private final PortletApp _portletApp;
	private final PortletContext _portletContext;
	private final Map<String, String> _portletInfos;
	private final String _portletName;
	private final Map<Locale, ResourceBundle> _resourceBundles =
		new ConcurrentHashMap<>();

}