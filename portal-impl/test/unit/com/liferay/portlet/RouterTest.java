/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.portlet.Route;
import com.liferay.portal.kernel.portlet.Router;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Connor McKay
 * @author Brian Wing Shun Chan
 */
public class RouterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_router = new Router();

		Route route = _router.addRoute(
			"instance/{userIdAndInstanceId}/{topLink}");

		route.addGeneratedParameter(
			"p_p_id", "15_INSTANCE_{userIdAndInstanceId}");

		route = _router.addRoute("GET/{controller}");

		route.addImplicitParameter("action", "index");
		route.addImplicitParameter("format", "html");
		route.addImplicitParameter("method", "GET");

		route = _router.addRoute("GET/{controller}.{format}");

		route.addImplicitParameter("action", "index");
		route.addImplicitParameter("method", "GET");

		route = _router.addRoute("POST/{controller}");

		route.addImplicitParameter("action", "create");
		route.addImplicitParameter("format", "html");
		route.addImplicitParameter("method", "POST");

		route = _router.addRoute("POST/{controller}.{format}");

		route.addImplicitParameter("action", "create");
		route.addImplicitParameter("method", "POST");

		route = _router.addRoute("GET/{controller}/{id:\\d+}");

		route.addImplicitParameter("action", "view");
		route.addImplicitParameter("format", "html");
		route.addImplicitParameter("method", "GET");

		route = _router.addRoute("GET/{controller}/{id:\\d+}.{format}");

		route.addImplicitParameter("action", "view");
		route.addImplicitParameter("method", "GET");

		route = _router.addRoute("POST/{controller}/{id:\\d+}");

		route.addImplicitParameter("action", "update");
		route.addImplicitParameter("format", "html");
		route.addImplicitParameter("method", "POST");

		route = _router.addRoute("POST/{controller}/{id:\\d+}.{format}");

		route.addImplicitParameter("action", "update");
		route.addImplicitParameter("method", "POST");

		route = _router.addRoute("{method}/{controller}/{id:\\d+}/{action}");

		route.addImplicitParameter("format", "html");

		_router.addRoute("{method}/{controller}/{id:\\d+}/{action}.{format}");

		route = _router.addRoute("{method}/{controller}/{action}");

		route.addImplicitParameter("format", "html");

		_router.addRoute("{method}/{controller}/{action}.{format}");
	}

	@Test
	public void testGeneratedParameters() {
		assertUrlGeneratesParameters(
			"instance/1b7c/recent", "p_p_id=15_INSTANCE_1b7c&topLink=recent");
		assertUrlRegenerates("instance/1b7c/recent");
	}

	@Test
	public void testPriority() {
		assertUrlRegeneratesUrl("GET/boxes/index", "GET/boxes");
	}

	@Test
	public void testReproduction() {
		assertUrlRegenerates("GET/boxes/16");
		assertUrlRegenerates("GET/boxes/25.xml");
		assertUrlRegenerates("POST/boxes/8");
		assertUrlRegenerates("POST/boxes/34.xml");
		assertUrlRegenerates("GET/boxes/new");
		assertUrlRegenerates("GET/boxes/8/export");
		assertUrlRegenerates("GET/boxes");
		assertUrlRegenerates("GET/boxes.xml");
		assertUrlRegenerates("POST/boxes");
		assertUrlRegenerates("POST/boxes.xml");
	}

	@Test
	public void testUrlDecoding() {
		assertParameterInUrlEquals(
			"controller", "open boxes", "POST/open%20boxes");
	}

	@Test
	public void testUrlToParameters() {
		assertUrlGeneratesParameters(
			"GET/boxes/16",
			"id=16&action=view&method=GET&format=html&controller=boxes");
		assertUrlGeneratesParameters(
			"GET/boxes/25.xml",
			"id=25&action=view&method=GET&controller=boxes&format=xml");
		assertUrlGeneratesParameters(
			"POST/boxes/8",
			"id=8&action=update&method=POST&format=html&controller=boxes");
		assertUrlGeneratesParameters(
			"POST/boxes/34.xml",
			"id=34&action=update&method=POST&controller=boxes&format=xml");
		assertUrlGeneratesParameters(
			"GET/boxes/new",
			"action=new&method=GET&format=html&controller=boxes");
		assertUrlGeneratesParameters(
			"GET/boxes/8/export",
			"id=8&action=export&method=GET&format=html&controller=boxes");
		assertUrlGeneratesParameters(
			"GET/boxes",
			"action=index&method=GET&format=html&controller=boxes");
		assertUrlGeneratesParameters(
			"GET/boxes.xml",
			"action=index&method=GET&controller=boxes&format=xml");
		assertUrlGeneratesParameters(
			"POST/boxes",
			"action=create&method=POST&format=html&controller=boxes");
		assertUrlGeneratesParameters(
			"POST/boxes.xml",
			"action=create&method=POST&controller=boxes&format=xml");
	}

	protected void assertParameterInUrlEquals(
		String name, String value, String url) {

		Map<String, String> parameters = new HashMap<>();

		_router.urlToParameters(url, parameters);

		Assert.assertEquals(value, MapUtil.getString(parameters, name));
	}

	protected void assertUrlGeneratesParameters(
		String url, String queryString) {

		Map<String, String[]> parameters =
			HttpComponentsUtil.parameterMapFromString(queryString);

		Map<String, String> generatedParameters = new HashMap<>();

		_router.urlToParameters(url, generatedParameters);

		AssertUtils.assertEquals(parameters, generatedParameters);
	}

	protected void assertUrlRegenerates(String url) {
		assertUrlRegeneratesUrl(url, url);
	}

	protected void assertUrlRegeneratesUrl(String url, String expectedUrl) {
		Map<String, String> parameters = new HashMap<>();

		_router.urlToParameters(url, parameters);

		String generatedUrl = _router.parametersToUrl(parameters);

		Assert.assertEquals(expectedUrl, generatedUrl);
	}

	private Router _router;

}