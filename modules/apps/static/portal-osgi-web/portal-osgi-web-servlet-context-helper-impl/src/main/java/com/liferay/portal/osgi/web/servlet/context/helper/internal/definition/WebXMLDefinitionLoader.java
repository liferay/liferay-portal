/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.context.helper.internal.definition;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.osgi.web.servlet.JSPServletFactory;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.FilterDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.ListenerDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.ServletDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.WebResourceCollectionDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.WebXMLDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.internal.JspServletWrapper;
import com.liferay.portal.osgi.web.servlet.context.helper.internal.order.OrderUtil;
import com.liferay.portal.osgi.web.servlet.context.helper.order.Order;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Bundle;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class WebXMLDefinitionLoader extends DefaultHandler {

	public WebXMLDefinitionLoader(
		Bundle bundle, JSPServletFactory jspServletFactory,
		SAXParserFactory saxParserFactory, Set<Class<?>> classes,
		Set<Class<?>> annotatedClasses) {

		_bundle = bundle;
		_jspServletFactory = jspServletFactory;
		_saxParserFactory = saxParserFactory;
		_classes = classes;
		_annotatedClasses = annotatedClasses;
	}

	@Override
	public void characters(char[] c, int start, int length) {
		if (_stack.empty()) {
			return;
		}

		StringBuilder stringBuilder = _stack.peek();

		stringBuilder.append(c, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equals("absolute-ordering")) {
			if (_othersAbsoluteOrderingSet &&
				(_absoluteOrderingNames != null)) {

				_absoluteOrderingNames.add(Order.OTHERS);
			}

			List<String> absoluteOrderingNames =
				_webXMLDefinition.getAbsoluteOrderingNames();

			absoluteOrderingNames.addAll(_absoluteOrderingNames);

			_absoluteOrderingNames = null;
			_othersAbsoluteOrderingSet = false;
		}
		else if (qName.equals("after")) {
			_after = false;
			_afterName = _name;

			_name = null;
		}
		else if (qName.equals("async-supported")) {
			boolean asyncSupported = GetterUtil.getBoolean(_stack.pop());

			if (_filterDefinition != null) {
				_filterDefinition.setAsyncSupported(asyncSupported);
			}
			else if (_servletDefinition != null) {
				_servletDefinition.setAsyncSupported(asyncSupported);
			}
		}
		else if (qName.equals("before")) {
			_before = false;
			_beforeName = _name;

			_name = null;
		}
		else if (qName.equals("context-param")) {
			_webXMLDefinition.setContextParameter(
				_parameterName, _parameterValue);

			_parameterName = null;
			_parameterValue = null;
		}
		else if (qName.equals("dispatcher")) {
			String dispatcher = String.valueOf(_stack.pop());

			dispatcher = StringUtil.toUpperCase(dispatcher);
			dispatcher = dispatcher.trim();

			_filterMapping.dispatchers.add(dispatcher);
		}
		else if (qName.equals("filter")) {
			if (_filterDefinition.getFilter() != null) {
				_webXMLDefinition.setFilterDefinition(
					_filterDefinition.getName(), _filterDefinition);
			}

			_filterDefinition = null;
		}
		else if (qName.equals("filter-class")) {
			String filterClassName = String.valueOf(_stack.pop());

			_setFilter(_filterDefinition, filterClassName.trim());
		}
		else if (qName.equals("filter-mapping")) {
			Map<String, FilterDefinition> filterDefinitions =
				_webXMLDefinition.getFilterDefinitions();

			FilterDefinition filterDefinition = filterDefinitions.get(
				_filterMapping.filterName);

			if (filterDefinition != null) {
				filterDefinition.setDispatchers(_filterMapping.dispatchers);

				if (_filterMapping.servletName != null) {
					List<String> servletNames =
						filterDefinition.getServletNames();

					servletNames.add(_filterMapping.servletName);
				}

				_addURLPatterns(filterDefinition, _filterMapping.urlPatterns);
			}

			_filterMapping = null;
		}
		else if (qName.equals("filter-name")) {
			if (_filterMapping != null) {
				String filterName = String.valueOf(_stack.pop());

				_filterMapping.filterName = filterName.trim();
			}
			else if (_filterDefinition != null) {
				String filterName = String.valueOf(_stack.pop());

				_filterDefinition.setName(filterName.trim());
			}
		}
		else if (qName.equals("http-method")) {
			if (_webResourceCollection != null) {
				String httpMethod = String.valueOf(_stack.pop());

				_webResourceCollection.httpMethods.add(httpMethod.trim());
			}
		}
		else if (qName.equals("http-method-exception")) {
			if (_webResourceCollection != null) {
				String httpMethodException = String.valueOf(_stack.pop());

				_webResourceCollection.httpMethodExceptions.add(
					httpMethodException.trim());
			}
		}
		else if (qName.equals("init-param")) {
			if (_filterDefinition != null) {
				_filterDefinition.setInitParameter(
					_parameterName, _parameterValue);
			}
			else if (_servletDefinition != null) {
				_servletDefinition.setInitParameter(
					_parameterName, _parameterValue);
			}

			_parameterName = null;
			_parameterValue = null;
		}
		else if (qName.equals("jsp-config")) {
			_webXMLDefinition.setJspTaglibMappings(_jspConfig.mappings);

			_jspConfig = null;
		}
		else if (qName.equals("jsp-file")) {
			String jspFile = String.valueOf(_stack.pop());

			_servletDefinition.setJSPFile(jspFile);
			_servletDefinition.setServlet(
				new JspServletWrapper(
					_jspServletFactory.createJSPServlet(), jspFile));
		}
		else if (qName.equals("listener")) {
			if (_listenerDefinition.getEventListener() != null) {
				_webXMLDefinition.addListenerDefinition(_listenerDefinition);
			}

			_listenerDefinition = null;
		}
		else if (qName.equals("listener-class")) {
			String listenerClassName = String.valueOf(_stack.pop());

			_setEventListener(_listenerDefinition, listenerClassName);
		}
		else if (qName.equals("name")) {
			String name = String.valueOf(_stack.pop());

			if (_absoluteOrderingNames != null) {
				_absoluteOrderingNames.add(name);
			}
			else if (!_after && !_before) {
				_webXMLDefinition.setFragmentName(name);
			}
			else {
				_name = name;
			}
		}
		else if (qName.equals("ordering")) {
			if (_order == null) {
				return;
			}

			EnumMap<Order.Path, String[]> routes = _order.getRoutes();

			List<String> beforeNames = new ArrayList<>(2);

			if (_beforeName != null) {
				beforeNames.add(_beforeName);
			}

			if (_othersBeforeSet) {
				beforeNames.add(Order.OTHERS);
			}

			if (ListUtil.isNotEmpty(beforeNames)) {
				routes.put(
					Order.Path.BEFORE, beforeNames.toArray(new String[0]));
			}

			List<String> afterNames = new ArrayList<>(2);

			if (_afterName != null) {
				afterNames.add(_afterName);
			}

			if (_othersAfterSet) {
				afterNames.add(Order.OTHERS);
			}

			if (ListUtil.isNotEmpty(afterNames)) {
				routes.put(Order.Path.AFTER, afterNames.toArray(new String[0]));
			}

			_order.setRoutes(routes);

			_webXMLDefinition.setOrder(_order);

			_afterName = null;
			_beforeName = null;
			_order = null;
			_othersAfterSet = false;
			_othersBeforeSet = false;
		}
		else if (qName.equals("others")) {
			if (_absoluteOrderingNames != null) {
				_othersAbsoluteOrderingSet = true;
			}

			if (_after) {
				_othersAfterSet = true;
			}
			else if (_before) {
				_othersBeforeSet = true;
			}
		}
		else if (qName.equals("param-name")) {
			_parameterName = String.valueOf(_stack.pop());
			_parameterName = _parameterName.trim();
		}
		else if (qName.equals("param-value")) {
			_parameterValue = String.valueOf(_stack.pop());
			_parameterValue = _parameterValue.trim();
		}
		else if (qName.equals("role-name") ||
				 qName.equals("transport-guarantee")) {

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						qName, " from web.xml in bundle ", _bundle,
						" is not supported"));
			}
		}
		else if (qName.equals("servlet")) {
			_webXMLDefinition.setServletDefinition(
				_servletDefinition.getName(), _servletDefinition);

			_servletDefinition = null;
		}
		else if (qName.equals("servlet-class")) {
			String servletClassName = String.valueOf(_stack.pop());

			_setServlet(_servletDefinition, servletClassName.trim());
		}
		else if (qName.equals("servlet-mapping")) {
			Map<String, ServletDefinition> servletDefinitions =
				_webXMLDefinition.getServletDefinitions();

			ServletDefinition servletDefinition = servletDefinitions.get(
				_servletMapping.servletName);

			if (servletDefinition != null) {
				_addURLPatterns(servletDefinition, _servletMapping.urlPatterns);
			}

			_servletMapping = null;
		}
		else if (qName.equals("servlet-name")) {
			if (_filterMapping != null) {
				String servletName = String.valueOf(_stack.pop());

				_filterMapping.servletName = servletName.trim();
			}
			else if (_servletDefinition != null) {
				String servletName = String.valueOf(_stack.pop());

				_servletDefinition.setName(servletName.trim());
			}
			else if (_servletMapping != null) {
				String servletName = String.valueOf(_stack.pop());

				_servletMapping.servletName = servletName.trim();
			}
		}
		else if (qName.equals("taglib")) {
			_jspConfig.mappings.put(_taglibUri, _taglibLocation);

			_taglibUri = null;
			_taglibLocation = null;
		}
		else if (qName.equals("taglib-location")) {
			_taglibLocation = String.valueOf(_stack.pop());
		}
		else if (qName.equals("taglib-uri")) {
			_taglibUri = String.valueOf(_stack.pop());
		}
		else if (qName.equals("url-pattern")) {
			if (_filterMapping != null) {
				String urlPattern = String.valueOf(_stack.pop());

				_filterMapping.urlPatterns.add(urlPattern.trim());
			}
			else if (_servletMapping != null) {
				String urlPattern = String.valueOf(_stack.pop());

				_servletMapping.urlPatterns.add(urlPattern.trim());
			}
			else if (_webResourceCollection != null) {
				String urlPattern = String.valueOf(_stack.pop());

				_webResourceCollection.urlPatterns.add(urlPattern.trim());
			}
		}
		else if (qName.equals("web-resource-collection")) {
			List<WebResourceCollectionDefinition>
				webResourceCollectionDefinitions =
					_webXMLDefinition.getWebResourceCollectionDefinitions();

			WebResourceCollectionDefinition webResourceCollectionDefinition =
				new WebResourceCollectionDefinition(
					_webResourceCollection.webResourceName);

			for (String httpMethod : _webResourceCollection.httpMethods) {
				webResourceCollectionDefinition.addHttpMethod(httpMethod);
			}

			for (String httpMethodException :
					_webResourceCollection.httpMethodExceptions) {

				webResourceCollectionDefinition.addHttpMethodException(
					httpMethodException);
			}

			for (String urlPattern : _webResourceCollection.urlPatterns) {
				webResourceCollectionDefinition.addURLPattern(urlPattern);
			}

			webResourceCollectionDefinitions.add(
				webResourceCollectionDefinition);

			_webResourceCollection = null;
		}
		else if (qName.equals("web-resource-name")) {
			String name = String.valueOf(_stack.pop());

			_webResourceCollection.webResourceName = name.trim();
		}
	}

	@Override
	public void error(SAXParseException saxParseException) {
		_log.error(
			_bundle + ": " + saxParseException.getMessage(), saxParseException);
	}

	public WebXMLDefinition loadWebXML() throws Exception {
		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			_bundle.getEntry("WEB-INF/web.xml"));

		if (webXMLDefinition.isMetadataComplete()) {
			return webXMLDefinition;
		}

		Enumeration<URL> enumeration = _bundle.getResources(
			"META-INF/web-fragment.xml");

		List<WebXMLDefinition> webXMLDefinitions = new ArrayList<>();

		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				WebXMLDefinitionLoader webXMLDefinitionLoader =
					new WebXMLDefinitionLoader(
						_bundle, _jspServletFactory, _saxParserFactory,
						_classes, _annotatedClasses);

				webXMLDefinitions.add(
					webXMLDefinitionLoader.loadWebXMLDefinition(url));
			}
		}

		WebXMLDefinition annotationWebXMLDefinition = new WebXMLDefinition();

		_collectAnnotatedClasses(annotationWebXMLDefinition);

		webXMLDefinitions.add(annotationWebXMLDefinition);

		List<WebXMLDefinition> orderedWebXMLDefinitions = new ArrayList<>();

		if (ListUtil.isNotEmpty(webXMLDefinitions)) {
			orderedWebXMLDefinitions = OrderUtil.getOrderedWebXMLDefinitions(
				webXMLDefinitions, webXMLDefinition.getAbsoluteOrderingNames());
		}

		return _assembleWebXMLDefinition(
			webXMLDefinition, orderedWebXMLDefinitions);
	}

	public WebXMLDefinition loadWebXMLDefinition(URL url) throws Exception {
		if (url == null) {
			return _webXMLDefinition;
		}

		try (InputStream inputStream = _toInputStream(url, url.openStream())) {
			SAXParser saxParser = _saxParserFactory.newSAXParser();

			XMLReader xmlReader = saxParser.getXMLReader();

			xmlReader.setContentHandler(this);

			xmlReader.parse(new InputSource(inputStream));

			return _webXMLDefinition;
		}
		catch (SAXParseException saxParseException) {
			String message = saxParseException.getMessage();

			if (message.contains("DOCTYPE is disallowed")) {
				throw new Exception(
					url + " must be updated to the Servlet 3.0 specification");
			}

			throw saxParseException;
		}
	}

	@Override
	public void startElement(
		String uri, String localName, String qName, Attributes attributes) {

		if (qName.equals("absolute-ordering")) {
			_absoluteOrderingNames = new ArrayList<>();
		}
		else if (qName.equals("after")) {
			_after = true;
		}
		else if (qName.equals("before")) {
			_before = true;
		}
		else if (qName.equals("filter")) {
			_filterDefinition = new FilterDefinition();
		}
		else if (qName.equals("filter-mapping")) {
			_filterMapping = new FilterMapping();
		}
		else if (qName.equals("jsp-config")) {
			_jspConfig = new JSPConfig();
		}
		else if (qName.equals("listener")) {
			_listenerDefinition = new ListenerDefinition();
		}
		else if (qName.equals("ordering")) {
			_order = new Order();
		}
		else if (qName.equals("servlet")) {
			_servletDefinition = new ServletDefinition();
		}
		else if (qName.equals("servlet-mapping")) {
			_servletMapping = new ServletMapping();
		}
		else if (qName.equals("web-app")) {
			_webXMLDefinition.setMetadataComplete(
				GetterUtil.getBoolean(
					attributes.getValue("metadata-complete")));
		}
		else if (qName.equals("web-resource-collection")) {
			_webResourceCollection = new WebResourceCollection();
		}
		else if (Arrays.binarySearch(_LEAVES, qName) > -1) {
			_stack.push(new StringBuilder());
		}
	}

	private void _addURLPatterns(
		FilterDefinition filterDefinition, List<String> value) {

		if (ListUtil.isNotEmpty(value)) {
			_addURLPatterns(
				filterDefinition, value.toArray(new String[0]), null);
		}
	}

	private void _addURLPatterns(
		FilterDefinition filterDefinition, String[] value,
		String[] urlPatterns) {

		if (ArrayUtil.isNotEmpty(value)) {
			for (String urlPattern : value) {
				filterDefinition.addURLPattern(urlPattern);
			}
		}

		if (ArrayUtil.isNotEmpty(urlPatterns)) {
			if (ListUtil.isNotEmpty(filterDefinition.getURLPatterns())) {
				throw new IllegalStateException(
					"Both value and URL patterns are declared");
			}

			for (String urlPattern : urlPatterns) {
				filterDefinition.addURLPattern(urlPattern);
			}
		}
	}

	private void _addURLPatterns(
		ServletDefinition servletDefinition, List<String> value) {

		if (ListUtil.isNotEmpty(value)) {
			_addURLPatterns(
				servletDefinition, value.toArray(new String[0]), null);
		}
	}

	private void _addURLPatterns(
		ServletDefinition servletDefinition, String[] value,
		String[] urlPatterns) {

		if (ArrayUtil.isNotEmpty(value)) {
			for (String urlPattern : value) {
				servletDefinition.addURLPattern(urlPattern);
			}
		}

		if (ArrayUtil.isNotEmpty(urlPatterns)) {
			if (ListUtil.isNotEmpty(servletDefinition.getURLPatterns())) {
				throw new IllegalStateException(
					"Both value and URL patterns are declared");
			}

			for (String urlPattern : urlPatterns) {
				servletDefinition.addURLPattern(urlPattern);
			}
		}
	}

	private void _assembleContextParameters(
		Map<String, String> assembledContextParameters,
		Map<String, String> fragmentContextParameters) {

		for (Map.Entry<String, String> entry :
				fragmentContextParameters.entrySet()) {

			String name = entry.getKey();

			if (!assembledContextParameters.containsKey(name)) {
				assembledContextParameters.put(name, entry.getValue());
			}
		}
	}

	private void _assembleFilterDefinitions(
			Map<String, FilterDefinition> webXMLFilterDefinitions,
			Map<String, FilterDefinition> assembledFilterDefinitions,
			Map<String, FilterDefinition> fragmentFilterDefinitions)
		throws Exception {

		for (Map.Entry<String, FilterDefinition> entry :
				fragmentFilterDefinitions.entrySet()) {

			String filterName = entry.getKey();

			if (!assembledFilterDefinitions.containsKey(filterName)) {
				assembledFilterDefinitions.put(filterName, entry.getValue());

				continue;
			}

			FilterDefinition webXMLFilterDefinition =
				webXMLFilterDefinitions.get(filterName);

			Map<String, String> webXMLInitParameters = null;

			if (webXMLFilterDefinition != null) {
				webXMLInitParameters =
					webXMLFilterDefinition.getInitParameters();
			}

			FilterDefinition assembledFilterDefinition =
				assembledFilterDefinitions.get(filterName);

			Map<String, String> assembledInitParameters =
				assembledFilterDefinition.getInitParameters();

			FilterDefinition fragmentFilterDefinition = entry.getValue();

			Map<String, String> fragmentInitParameters =
				fragmentFilterDefinition.getInitParameters();

			for (Map.Entry<String, String> initParametersEntry :
					fragmentInitParameters.entrySet()) {

				String initParameterName = initParametersEntry.getKey();

				String webXMLInitParameterValue = null;

				if (webXMLInitParameters != null) {
					webXMLInitParameterValue = webXMLInitParameters.get(
						initParameterName);
				}

				String assembledInitParameterValue =
					assembledInitParameters.get(initParameterName);

				if (Validator.isNull(assembledInitParameterValue)) {
					if ((webXMLInitParameterValue == null) &&
						!Objects.equals(
							assembledInitParameterValue,
							initParametersEntry.getValue())) {

						// Servlet 3 spec 8.2.3

						throw new Exception(
							StringBundler.concat(
								"Init paramter name ", initParameterName,
								" conflicts with filter name ", filterName));
					}

					assembledInitParameters.put(
						initParameterName, initParametersEntry.getValue());
				}
			}

			List<String> assembledDispatchers =
				assembledFilterDefinition.getDispatchers();

			List<String> fragmentDispatchers =
				fragmentFilterDefinition.getDispatchers();

			for (String dispatcher : fragmentDispatchers) {
				if (!assembledDispatchers.contains(dispatcher)) {
					assembledDispatchers.add(dispatcher);
				}
			}

			List<String> assembledServletNames =
				assembledFilterDefinition.getServletNames();

			List<String> fragmentServletNames =
				fragmentFilterDefinition.getServletNames();

			for (String servletName : fragmentServletNames) {
				if (!assembledServletNames.contains(servletName)) {
					assembledServletNames.add(servletName);
				}
			}

			List<String> assembledURLPatterns =
				assembledFilterDefinition.getURLPatterns();

			List<String> fragmentURLPatterns =
				fragmentFilterDefinition.getURLPatterns();

			for (String urlPattern : fragmentURLPatterns) {
				if (!assembledURLPatterns.contains(urlPattern)) {
					assembledURLPatterns.add(urlPattern);
				}
			}
		}
	}

	private void _assembleListenerDefinitions(
		List<ListenerDefinition> assembledListenerDefinitions,
		List<ListenerDefinition> fragmentListenerDefinitions) {

		for (ListenerDefinition fragmentListenerDefinition :
				fragmentListenerDefinitions) {

			if (!assembledListenerDefinitions.contains(
					fragmentListenerDefinition)) {

				assembledListenerDefinitions.add(fragmentListenerDefinition);
			}
		}
	}

	private void _assembleServletDefinitions(
			Map<String, ServletDefinition> webXMLServletDefinitions,
			Map<String, ServletDefinition> assembledServletDefinitions,
			Map<String, ServletDefinition> fragmentServletDefinitions)
		throws Exception {

		for (Map.Entry<String, ServletDefinition> entry :
				fragmentServletDefinitions.entrySet()) {

			String servletName = entry.getKey();

			if (!assembledServletDefinitions.containsKey(servletName)) {
				assembledServletDefinitions.put(servletName, entry.getValue());

				continue;
			}

			ServletDefinition webXMLServletDefinition =
				webXMLServletDefinitions.get(servletName);

			Map<String, String> webXMLServletInitParameters = null;

			if (webXMLServletDefinition != null) {
				webXMLServletInitParameters =
					webXMLServletDefinition.getInitParameters();
			}

			ServletDefinition assembledServletDefinition =
				assembledServletDefinitions.get(servletName);

			Map<String, String> assembledInitInitParameters =
				assembledServletDefinition.getInitParameters();

			ServletDefinition fragmentServletDefinition = entry.getValue();

			Map<String, String> fragmentServletInitParameters =
				fragmentServletDefinition.getInitParameters();

			for (Map.Entry<String, String> initParameterEntry :
					fragmentServletInitParameters.entrySet()) {

				String initParameterName = initParameterEntry.getKey();

				String webXMLInitParameterValue = null;

				if (webXMLServletInitParameters != null) {
					webXMLInitParameterValue = webXMLServletInitParameters.get(
						initParameterName);
				}

				String assembledInitParameterValue =
					assembledInitInitParameters.get(initParameterName);

				if (Validator.isNull(assembledInitParameterValue)) {
					if ((webXMLInitParameterValue == null) &&
						!Objects.equals(
							assembledInitParameterValue,
							initParameterEntry.getValue())) {

						// Servlet 3 spec 8.2.3

						throw new Exception(
							StringBundler.concat(
								"Init paramter name ", initParameterName,
								" conflicts with servlet name ", servletName));
					}

					assembledInitInitParameters.put(
						initParameterName, initParameterEntry.getValue());
				}
			}

			List<String> assembledURLPatterns =
				assembledServletDefinition.getURLPatterns();

			List<String> fragmentURLPatterns =
				fragmentServletDefinition.getURLPatterns();

			for (String urlPattern : fragmentURLPatterns) {
				if (!assembledURLPatterns.contains(urlPattern)) {
					assembledURLPatterns.add(urlPattern);
				}
			}

			if (Validator.isNull(assembledServletDefinition.getJspFile())) {
				assembledServletDefinition.setJSPFile(
					fragmentServletDefinition.getJspFile());
			}
		}
	}

	private WebXMLDefinition _assembleWebXMLDefinition(
			WebXMLDefinition webXMLWebXMLDefinition,
			List<WebXMLDefinition> fragmentWebXMLDefinitions)
		throws Exception {

		Map<String, FilterDefinition> webXMLFilterDefinitions =
			webXMLWebXMLDefinition.getFilterDefinitions();
		Map<String, ServletDefinition> webXMLServletDefinitions =
			webXMLWebXMLDefinition.getServletDefinitions();

		WebXMLDefinition assembledWebXMLDefinition =
			(WebXMLDefinition)webXMLWebXMLDefinition.clone();

		Map<String, String> assembledContextParameters =
			assembledWebXMLDefinition.getContextParameters();
		Map<String, FilterDefinition> assembledFilterDefinitions =
			assembledWebXMLDefinition.getFilterDefinitions();
		List<ListenerDefinition> assembledListenerDefinitions =
			assembledWebXMLDefinition.getListenerDefinitions();
		Map<String, ServletDefinition> assembledServletDefinitions =
			assembledWebXMLDefinition.getServletDefinitions();

		for (WebXMLDefinition fragmentWebXMLDefinition :
				fragmentWebXMLDefinitions) {

			Map<String, String> fragmentContextParameters =
				fragmentWebXMLDefinition.getContextParameters();

			_assembleContextParameters(
				assembledContextParameters, fragmentContextParameters);

			Map<String, FilterDefinition> fragmentFilterDefinitions =
				fragmentWebXMLDefinition.getFilterDefinitions();

			_assembleFilterDefinitions(
				webXMLFilterDefinitions, assembledFilterDefinitions,
				fragmentFilterDefinitions);

			List<ListenerDefinition> fragmentListenerDefinitions =
				fragmentWebXMLDefinition.getListenerDefinitions();

			_assembleListenerDefinitions(
				assembledListenerDefinitions, fragmentListenerDefinitions);

			Map<String, ServletDefinition> fragmentServletDefinitions =
				fragmentWebXMLDefinition.getServletDefinitions();

			_assembleServletDefinitions(
				webXMLServletDefinitions, assembledServletDefinitions,
				fragmentServletDefinitions);
		}

		return assembledWebXMLDefinition;
	}

	private void _collectAnnotatedClasses(WebXMLDefinition webXMLDefinition) {
		for (Class<?> clazz : _classes) {
			_collectAnnotatedClasses(webXMLDefinition, clazz);
		}
	}

	private void _collectAnnotatedClasses(
		WebXMLDefinition webXMLDefinition, Class<?> clazz) {

		WebServlet webServlet = null;

		try {
			webServlet = clazz.getAnnotation(WebServlet.class);
		}
		catch (Exception exception) {

			// See http://bugs.java.com/view_bug.do?bug_id=7183985 and LPS-69679

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unexpected error retrieving the annotation ",
						WebServlet.class, "from class ", clazz,
						" because a some dependency may not be present in the ",
						"classpath"),
					exception);
			}

			return;
		}

		boolean annotated = false;

		if (webServlet != null) {
			annotated = true;

			ServletDefinition servletDefinition = new ServletDefinition();

			servletDefinition.setAsyncSupported(webServlet.asyncSupported());

			_setInitParameters(
				webServlet.initParams(), servletDefinition.getInitParameters());

			String name = webServlet.name();

			if (Validator.isNotNull(name)) {
				servletDefinition.setName(name);
			}
			else {
				servletDefinition.setName(clazz.getCanonicalName());
			}

			_addURLPatterns(
				servletDefinition, webServlet.value(),
				webServlet.urlPatterns());

			_setServlet(servletDefinition, clazz.getCanonicalName());

			webXMLDefinition.setServletDefinition(
				servletDefinition.getName(), servletDefinition);
		}

		WebFilter webFilter = clazz.getAnnotation(WebFilter.class);

		if (webFilter != null) {
			annotated = true;

			FilterDefinition filterDefinition = new FilterDefinition();

			filterDefinition.setAsyncSupported(webFilter.asyncSupported());

			DispatcherType[] dispatcherTypes = webFilter.dispatcherTypes();

			if (ArrayUtil.isNotEmpty(dispatcherTypes)) {
				for (DispatcherType dispatcherType : dispatcherTypes) {
					filterDefinition.addDispatcher(dispatcherType.name());
				}
			}

			_setFilter(filterDefinition, clazz.getCanonicalName());

			_setInitParameters(
				webFilter.initParams(), filterDefinition.getInitParameters());

			String filterName = webFilter.filterName();

			if (Validator.isNotNull(filterName)) {
				filterDefinition.setName(filterName);
			}
			else {
				filterDefinition.setName(clazz.getCanonicalName());
			}

			String[] servletNames = webFilter.servletNames();

			if (ArrayUtil.isNotEmpty(servletNames)) {
				for (String servletName : servletNames) {
					filterDefinition.addServletName(servletName);
				}
			}

			_addURLPatterns(
				filterDefinition, webFilter.value(), webFilter.urlPatterns());

			webXMLDefinition.setFilterDefinition(
				filterDefinition.getName(), filterDefinition);
		}

		WebListener webListener = clazz.getAnnotation(WebListener.class);

		if (webListener != null) {
			annotated = true;

			ListenerDefinition listenerDefinition = new ListenerDefinition();

			_setEventListener(listenerDefinition, clazz.getCanonicalName());

			webXMLDefinition.addListenerDefinition(listenerDefinition);
		}

		if (annotated) {
			_annotatedClasses.add(clazz);
		}
	}

	private Filter _getFilterInstance(String filterClassName) {
		try {
			Class<?> clazz = _bundle.loadClass(filterClassName);

			Class<? extends Filter> filterClass = clazz.asSubclass(
				Filter.class);

			return filterClass.newInstance();
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Bundle ", _bundle, " is unable to load filter ",
					filterClassName),
				exception);

			return null;
		}
	}

	private EventListener _getListenerInstance(String listenerClassName) {
		try {
			Class<?> clazz = _bundle.loadClass(listenerClassName);

			Class<? extends EventListener> eventListenerClass =
				clazz.asSubclass(EventListener.class);

			return eventListenerClass.newInstance();
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Bundle ", _bundle, " is unable to load listener ",
					listenerClassName),
				exception);

			return null;
		}
	}

	private Servlet _getServletInstance(String servletClassName) {
		try {
			Class<?> clazz = _bundle.loadClass(servletClassName);

			Class<? extends Servlet> servletClass = clazz.asSubclass(
				Servlet.class);

			return servletClass.newInstance();
		}
		catch (Exception exception) {
			_log.error(
				_bundle + " unable to load servlet " + servletClassName,
				exception);

			return null;
		}
	}

	private void _setEventListener(
		ListenerDefinition listenerDefinition, String listenerClassName) {

		listenerDefinition.setEventListener(
			_getListenerInstance(listenerClassName));
	}

	private void _setFilter(
		FilterDefinition filterDefinition, String filterClassName) {

		filterDefinition.setFilter(_getFilterInstance(filterClassName));
	}

	private void _setInitParameters(
		WebInitParam[] webInitParams, Map<String, String> initParametersMap) {

		if (ArrayUtil.isNotEmpty(webInitParams)) {
			for (WebInitParam webInitParam : webInitParams) {
				initParametersMap.put(
					webInitParam.name(), webInitParam.value());
			}
		}
	}

	private void _setServlet(
		ServletDefinition servletDefinition, String servletClassName) {

		servletDefinition.setServlet(_getServletInstance(servletClassName));
	}

	private InputStream _toInputStream(URL url, InputStream inputStream)
		throws IOException {

		if (_textReplacerBiFunction == null) {
			return inputStream;
		}

		String xmlContent = _textReplacerBiFunction.apply(
			"WebXMLDefinitionLoader#" + url,
			StreamUtil.toString(inputStream, StringPool.UTF8));

		return new UnsyncByteArrayInputStream(
			xmlContent.getBytes(StringPool.UTF8));
	}

	private static final String[] _LEAVES = {
		"async-supported", "dispatcher", "error-code", "exception-type",
		"filter-class", "filter-name", "http-method", "http-method-exception",
		"jsp-file", "listener-class", "location", "name", "param-name",
		"param-value", "servlet-class", "servlet-name", "taglib-location",
		"taglib-uri", "url-pattern", "web-resource-name"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		WebXMLDefinitionLoader.class);

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

	private List<String> _absoluteOrderingNames;
	private boolean _after;
	private String _afterName;
	private final Set<Class<?>> _annotatedClasses;
	private boolean _before;
	private String _beforeName;
	private final Bundle _bundle;
	private final Set<Class<?>> _classes;
	private FilterDefinition _filterDefinition;
	private FilterMapping _filterMapping;
	private JSPConfig _jspConfig;
	private final JSPServletFactory _jspServletFactory;
	private ListenerDefinition _listenerDefinition;
	private String _name;
	private Order _order;
	private boolean _othersAbsoluteOrderingSet;
	private boolean _othersAfterSet;
	private boolean _othersBeforeSet;
	private String _parameterName;
	private String _parameterValue;
	private final SAXParserFactory _saxParserFactory;
	private ServletDefinition _servletDefinition;
	private ServletMapping _servletMapping;
	private final Stack<StringBuilder> _stack = new Stack<>();
	private String _taglibLocation;
	private String _taglibUri;
	private WebResourceCollection _webResourceCollection;
	private final WebXMLDefinition _webXMLDefinition = new WebXMLDefinition();

	private static class FilterMapping {

		protected List<String> dispatchers = new ArrayList<>();
		protected String filterName;
		protected String servletName;
		protected List<String> urlPatterns = new ArrayList<>();

	}

	private static class JSPConfig {

		protected Map<String, String> mappings = new HashMap<>();

	}

	private static class ServletMapping {

		protected String servletName;
		protected List<String> urlPatterns = new ArrayList<>();

	}

	private static class WebResourceCollection {

		protected List<String> httpMethodExceptions = new ArrayList<>();
		protected List<String> httpMethods = new ArrayList<>();
		protected List<String> urlPatterns = new ArrayList<>();
		protected String webResourceName;

	}

}