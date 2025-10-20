/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringEncoder;
import com.liferay.portal.kernel.util.StringParser;
import com.liferay.portal.kernel.util.URLStringEncoder;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a single friendly URL pattern and provides the ability to either
 * parse a friendly URL path or generate a friendly URL from a parameter map.
 *
 * @author Connor McKay
 * @author Brian Wing Shun Chan
 * @see    Router
 * @see    StringParser
 */
public class Route {

	public Route(String pattern) {
		_pattern = pattern;

		_stringParser = StringParser.create(pattern);

		_stringParser.setStringEncoder(_urlEncoder);
	}

	/**
	 * Adds a generated parameter to this route.
	 *
	 * <p>
	 * A generated parameter is never part of the URL, but is constructed from
	 * one or more "virtual parameters" using a pattern string. When a URL is
	 * parsed, the virtual parameters are populated from the URL, and the
	 * generated parameter is then built from its constituent virtual
	 * parameters.
	 * </p>
	 *
	 * <p>
	 * When a URL is generated, the opposite process occurs. The generated
	 * parameter must have its value passed in through the parameter map. This
	 * value is parsed using the pattern string, and the virtual parameters are
	 * populated and made available for use in the route pattern string. The
	 * value of the generated parameter will not be available in the route
	 * pattern; only the values of the virtual parameters may be used.
	 * </p>
	 *
	 * <p>
	 * The following is an example route definition that uses a generated
	 * parameter to remove the .jsp extension of the current page from the URL.
	 * </p>
	 *
	 * <p>
	 * <pre>
	 * <code>
	 * &lt;route&gt;
	 * 	&lt;pattern&gt;/{jspPageName}/{id:\d+}&lt;/pattern&gt;
	 * 	&lt;generated-parameter name=&quot;jspPage&quot;&gt;{jspPageName}.jsp&lt;/generated-parameter&gt;
	 * &lt;/route&gt;
	 * </code>
	 * </pre></p>
	 *
	 * @param name the name of the generated parameter
	 * @param pattern the pattern string of the generated parameter
	 */
	public void addGeneratedParameter(String name, String pattern) {
		if (_generatedParameters == null) {
			_generatedParameters = new HashMap<>();
		}

		StringParser stringParser = StringParser.create(pattern);

		_generatedParameters.put(name, stringParser);
	}

	/**
	 * Adds an ignored parameter to this route.
	 *
	 * <p>
	 * An ignored parameter never appears in the query string regardless of its
	 * value. Ignored parameters have no effect on the parsing of URLs.
	 * </p>
	 *
	 * @param name the name of the ignored parameter
	 */
	public void addIgnoredParameter(String name) {
		if (_ignoredParameters == null) {
			_ignoredParameters = new HashSet<>();
		}

		_ignoredParameters.add(name);
	}

	/**
	 * Adds an implicit parameter to this route.
	 *
	 * <p>
	 * An implicit parameter applies in both stages of a route's lifecycle, URL
	 * parsing and URL generation. When a URL is parsed, the implicit parameters
	 * will be copied onto the parameter map. When a URL is generated from a
	 * parameter map, all the implicit parameters will be checked against the
	 * parameters in the map. If any do not match or are missing from the
	 * parameter map, this route will not be used for generating the URL.
	 * </p>
	 *
	 * <p>
	 * Implicit parameters are matched after all virtual parameters have been
	 * populated, and therefore should not match against the value of a
	 * generated parameter, as it will not be available.
	 * </p>
	 *
	 * <p>
	 * The most common use of implicit parameters is to specify a parameter that
	 * is included statically in the route pattern. For instance, if a custom
	 * route is specified for the JSP page "view_profile.jsp", the routes
	 * definition could look like the following:
	 * </p>
	 *
	 * <p>
	 * <pre>
	 * <code>
	 * &lt;route&gt;
	 * 	&lt;pattern&gt;/profile/view/{id:\d+}&lt;/pattern&gt;
	 * 	&lt;implicit-parameter name=&quot;jspPage&quot;&gt;view_profile.jsp&lt;/implicit-parameter&gt;
	 * &lt;/route&gt;
	 * </code>
	 * </pre></p>
	 *
	 * <p>
	 * Since the jspPage is specified with a implicit-parameter, this route will
	 * only be used to generate URLs if the jspPage is set to
	 * "view_profile.jsp". Likewise, when a URL in this format is recognized,
	 * the jspPage will automatically be set to "view_profile.jsp".
	 * </p>
	 *
	 * @param name the name of the implicit parameter
	 * @param value the value of the implicit parameter
	 */
	public void addImplicitParameter(String name, String value) {
		if (_implicitParameters == null) {
			_implicitParameters = new HashMap<>();
		}

		_implicitParameters.put(name, value);
	}

	/**
	 * Adds an overridden parameter to this route.
	 *
	 * <p>
	 * An overridden parameter is one that should always be set to a certain
	 * value when a URL is parsed. Overridden parameters have no effect on the
	 * generating of URLs.
	 * </p>
	 *
	 * <p>
	 * If a implicit parameter and an overridden parameter share the same name,
	 * the overridden parameter value will take precedence when a URL is parsed.
	 * When a URL is generated the implicit parameter value will be checked
	 * against the parameter map as usual.
	 * </p>
	 *
	 * @param name the name of the overridden parameter
	 * @param value the value of the overridden parameter
	 */
	public void addOverriddenParameter(String name, String value) {
		if (_overriddenParameters == null) {
			_overriddenParameters = new HashMap<>();
		}

		_overriddenParameters.put(name, value);
	}

	/**
	 * Returns the generated parameters for this route.
	 *
	 * @return the generated parameter names and string parsers
	 * @see    #addGeneratedParameter(String, String)
	 */
	public Map<String, StringParser> getGeneratedParameters() {
		if (_generatedParameters == null) {
			return Collections.emptyMap();
		}

		return _generatedParameters;
	}

	/**
	 * Returns the ignored parameters for this route.
	 *
	 * @return the ignored parameter names
	 * @see    #addIgnoredParameter(String)
	 */
	public Set<String> getIgnoredParameters() {
		if (_ignoredParameters == null) {
			return Collections.emptySet();
		}

		return _ignoredParameters;
	}

	/**
	 * Returns the implicit parameters for this route.
	 *
	 * @return the implicit parameter names and values
	 * @see    #addImplicitParameter(String, String)
	 */
	public Map<String, String> getImplicitParameters() {
		if (_implicitParameters == null) {
			return Collections.emptyMap();
		}

		return _implicitParameters;
	}

	/**
	 * Returns the overridden parameters for this route.
	 *
	 * @return the overridden parameter names and values
	 * @see    #addOverriddenParameter(String, String)
	 */
	public Map<String, String> getOverriddenParameters() {
		if (_overriddenParameters == null) {
			return Collections.emptyMap();
		}

		return _overriddenParameters;
	}

	public String getPattern() {
		return _pattern;
	}

	/**
	 * Generates a URL from the parameter map if this route is appropriate.
	 *
	 * <p>
	 * A route is appropriate if:
	 * </p>
	 *
	 * <ol>
	 * <li>
	 * The values given in <code>parameters</code> for the route's generated
	 * parameters match their patterns
	 * </li>
	 * <li>
	 * The route's implicit parameters all match <code>parameters</code>
	 * </li>
	 * <li>
	 * Every fragment in the route pattern has a matching value in
	 * <code>parameters</code>
	 * </li>
	 * </ol>
	 *
	 * @param  parameters the parameter map
	 * @return the URL path, or <code>null</code> if this route is not
	 *         appropriate
	 */
	public String parametersToUrl(Map<String, String> parameters) {
		Map<String, String> allParameters = new HashMap<>(parameters);

		// The order is important because virtual parameters may sometimes be
		// checked by implicit parameters

		Map<String, StringParser> generatedParameters =
			getGeneratedParameters();

		for (Map.Entry<String, StringParser> entry :
				generatedParameters.entrySet()) {

			String name = entry.getKey();
			StringParser stringParser = entry.getValue();

			String value = MapUtil.getString(allParameters, name);

			if (!stringParser.parse(value, allParameters)) {
				return null;
			}
		}

		Map<String, String> implicitParameters = getImplicitParameters();

		for (Map.Entry<String, String> entry : implicitParameters.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();

			if (!value.equals(MapUtil.getString(allParameters, name))) {
				return null;
			}
		}

		String url = _stringParser.build(allParameters);

		if (Validator.isNull(url)) {
			return null;
		}

		for (String name : generatedParameters.keySet()) {

			// Virtual parameters will never be placed in the query string, so
			// parameters is modified directly instead of allParameters

			parameters.remove(name);
		}

		for (String name : implicitParameters.keySet()) {
			parameters.remove(name);
		}

		for (String name : getIgnoredParameters()) {
			parameters.remove(name);
		}

		return url;
	}

	/**
	 * Populates the parameter map with values parsed from the URL if this route
	 * matches.
	 *
	 * <p>
	 * Generated parameters will be built and added to <code>parameters</code>
	 * if possible. If the pattern for a virtual parameter in the generated
	 * parameter does not match its pattern in the route, that generated
	 * parameter may not be build.
	 * </p>
	 *
	 * <p>
	 * Virtual parameters will not be present in <code>parameters</code>.
	 * </p>
	 *
	 * @param  url the URL path
	 * @param  parameters the parameter map. If this route does not match, the
	 *         parameter map will not be modified.
	 * @return <code>true</code> if the route matches; <code>false</code>
	 *         otherwise
	 */
	public boolean urlToParameters(String url, Map<String, String> parameters) {
		if (!_stringParser.parse(url, parameters)) {
			return false;
		}

		parameters.putAll(getImplicitParameters());
		parameters.putAll(getOverriddenParameters());

		// The order is important because generated parameters may be dependent
		// on implicit parameters or overridden parameters

		Map<String, StringParser> generatedParameters =
			getGeneratedParameters();

		for (Map.Entry<String, StringParser> entry :
				generatedParameters.entrySet()) {

			StringParser stringParser = entry.getValue();

			String value = stringParser.build(parameters);

			// Generated parameters are not guaranteed to be created. The format
			// of the virtual parameters in the route pattern must match their
			// format in the generated parameter.

			if (value != null) {
				parameters.put(entry.getKey(), value);
			}
		}

		return true;
	}

	private static final StringEncoder _urlEncoder = new URLStringEncoder();

	private Map<String, StringParser> _generatedParameters;
	private Set<String> _ignoredParameters;
	private Map<String, String> _implicitParameters;
	private Map<String, String> _overriddenParameters;
	private final String _pattern;
	private final StringParser _stringParser;

}