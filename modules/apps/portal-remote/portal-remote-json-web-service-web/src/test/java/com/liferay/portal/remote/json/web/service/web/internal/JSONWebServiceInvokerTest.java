/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.remote.json.web.service.JSONWebServiceAction;
import com.liferay.portal.remote.json.web.service.web.internal.action.JSONWebServiceInvokerAction;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Igor Spasic
 */
public class JSONWebServiceInvokerTest extends BaseJSONWebServiceTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		final Method getDefaultPlidMethod = LayoutLocalService.class.getMethod(
			"getDefaultPlid", long.class, boolean.class);

		ReflectionTestUtil.setFieldValue(
			LayoutLocalServiceUtil.class, "_service",
			ProxyUtil.newProxyInstance(
				LayoutLocalService.class.getClassLoader(),
				new Class<?>[] {LayoutLocalService.class},
				new InvocationHandler() {

					@Override
					public Object invoke(
						Object proxy, Method method, Object[] args) {

						if (getDefaultPlidMethod.equals(method)) {
							return 0L;
						}

						throw new UnsupportedOperationException();
					}

				}));

		PropsTestUtil.setProps(Collections.emptyMap());

		initPortalServices();

		registerActionClass(FooService.class);
	}

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			ServiceContextFactory.getInstance(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			new ServiceContext()
		);
	}

	@After
	public void tearDown() {
		_serviceContextFactoryMockedStatic.close();
	}

	@Test
	public void testBatchCalls() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/hello-world", params
		).build();

		params.put("userId", 173);
		params.put("worldName", "Jupiter");

		String json = toJSON(map);

		json = StringBundler.concat("[", json, ", ", json, "]");

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		result = invokerResult.getResult();

		Assert.assertTrue(result instanceof List);

		Assert.assertEquals(
			"[\"Welcome 173 to Jupiter\",\"Welcome 173 to Jupiter\"]",
			toJSON(invokerResult));
	}

	@Test
	public void testCamelCaseNormalizedParameters() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/camel", params
		).build();

		params.put("goodName", "goodboy");
		params.put("badNAME", "badboy");

		String json = toJSON(map);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals("\"goodboy*badboy\"", toJSON(invokerResult));
	}

	@Test
	public void testCreateArgumentInstances() throws Exception {

		// Style 1

		Map<String, Object> params = new LinkedHashMap<>();

		params.put("+fooData", null);

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/use1", params
		).build();

		String json = toJSON(map);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"using #1: h=177/id=-1/n=John Doe/v=foo!",
			invokerResult.getResult());

		// Style 2

		map.clear();

		map.put("/foo/use2", params);

		json = toJSON(map);

		jsonWebServiceAction = prepareInvokerAction(json);

		try {
			jsonWebServiceAction.invoke();

			Assert.fail();
		}
		catch (Exception exception) {
		}

		map.clear();

		params.clear();

		params.put("+fooData:" + FooDataImpl.class.getName(), null);

		map.put("/foo/use2", params);

		json = toJSON(map);

		jsonWebServiceAction = prepareInvokerAction(json);

		result = jsonWebServiceAction.invoke();

		invokerResult = (JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"using #2: h=177/id=-1/n=John Doe/v=foo!",
			invokerResult.getResult());

		// Style 3

		map.clear();

		params.clear();

		params.put("+fooData", FooDataImpl.class.getName());

		map.put("/foo/use2", params);

		json = toJSON(map);

		jsonWebServiceAction = prepareInvokerAction(json);

		result = jsonWebServiceAction.invoke();

		invokerResult = (JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"using #2: h=177/id=-1/n=John Doe/v=foo!",
			invokerResult.getResult());

		// Style 4

		map.clear();

		params.clear();

		params.put(
			"fooData",
			HashMapBuilder.<String, Object>put(
				"name", "Jane Doe"
			).build());

		map.put("/foo/use1", params);

		json = toJSON(map);

		jsonWebServiceAction = prepareInvokerAction(json);

		result = jsonWebServiceAction.invoke();

		invokerResult = (JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"using #1: h=177/id=-1/n=Jane Doe/v=foo!",
			invokerResult.getResult());
	}

	@Test
	public void testFiltering() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map1 = LinkedHashMapBuilder.<String, Object>put(
			"$data[id] = /foo/get-foo-data", params
		).build();

		params.put("id", 173);

		Map<String, Object> map2 = new LinkedHashMap<>();

		params.put("$world = /foo/hello-world", map2);

		map2.put("@userId", "$data.id");
		map2.put("worldName", "Jupiter");

		String json = toJSON(map1);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertTrue(invokerResult.getResult() instanceof Map);

		Assert.assertEquals(
			toMap("{\"id\":173,\"world\":\"Welcome 173 to Jupiter\"}"),
			toMap(toJSON(result)));
	}

	@Test
	public void testFilteringList() throws Exception {
		String json = toJSON(
			LinkedHashMapBuilder.<String, Object>put(
				"$datas[id] = /foo/get-foo-datas2", new LinkedHashMap<>()
			).build());

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		result = invokerResult.getResult();

		Assert.assertTrue(result instanceof List);
		Assert.assertEquals(
			"[{\"id\":1},{\"id\":2},{\"id\":3}]", toJSON(result));
	}

	@Test
	public void testFilteringPrimitivesList() throws Exception {
		String json = toJSON(
			LinkedHashMapBuilder.<String, Object>put(
				"$datas[id] = /foo/get-foo-datas3", new LinkedHashMap<>()
			).build());

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		result = invokerResult.getResult();

		Assert.assertTrue(result instanceof List);
		Assert.assertEquals(
			"[{\"id\":null},{\"id\":null},{\"id\":null}]", toJSON(result));
	}

	@Test
	public void testInnerCalls() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map1 = LinkedHashMapBuilder.<String, Object>put(
			"$data = /foo/get-foo-data", params
		).build();

		params.put("id", 173);

		Map<String, Object> map2 = new LinkedHashMap<>();

		params.put("$world = /foo/hello-world", map2);

		map2.put("@userId", "$data.id");
		map2.put("worldName", "Jupiter");

		String json = toJSON(map1);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		result = invokerResult.getResult();

		Assert.assertTrue(result instanceof Map);

		Assert.assertEquals(
			"{\"height\":177,\"id\":173,\"name\":\"John Doe\",\"value\":" +
				"\"foo!\",\"world\":\"Welcome 173 to Jupiter\"}",
			toJSON(invokerResult));
	}

	@Test
	public void testInnerCallsNested() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map1 = LinkedHashMapBuilder.<String, Object>put(
			"$data = /foo/get-foo-data", params
		).build();

		params.put("id", 173);

		Map<String, Object> map2 = new LinkedHashMap<>();

		params.put("$spy = /foo/get-foo-data", map2);

		map2.put("id", "007");

		Map<String, Object> map3 = new LinkedHashMap<>();

		map2.put("$thief = /foo/get-foo-data", map3);

		map3.put("id", -13);

		Map<String, Object> map4 = new LinkedHashMap<>();

		map3.put("$world = /foo/hello-world", map4);

		map4.put("@userId", "$thief.id");
		map4.put("worldName", "Jupiter");

		String json = toJSON(map1);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertTrue(invokerResult.getResult() instanceof Map);

		Assert.assertEquals(
			toMap(
				StringBundler.concat(
					"{\"height\":177,\"id\":173,\"name\":\"John Doe\",",
					"\"spy\":{\"height\":173,\"id\":7,\"name\":\"James Bond\",",
					"\"thief\":{\"height\":59,\"id\":-13,",
					"\"name\":\"Dr. Evil\",\"value\":\"fun\",",
					"\"world\":\"Welcome -13 to Jupiter\"},",
					"\"value\":\"licensed\"},\"value\":\"foo!\"}")),
			toMap(toJSON(result)));
	}

	@Test
	public void testListFiltering() throws Exception {
		String json = toJSON(
			LinkedHashMapBuilder.<String, Object>put(
				"$world[id] = /foo/get-foo-datas", new LinkedHashMap<>()
			).build());

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		result = invokerResult.getResult();

		Assert.assertTrue(result instanceof List);

		Assert.assertEquals(
			"[{\"id\":1},{\"id\":2},{\"id\":3}]", toJSON(invokerResult));
	}

	@Test
	public void testListFilteringAndFlags() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map1 = LinkedHashMapBuilder.<String, Object>put(
			"$world[id] = /foo/get-foo-datas", params
		).build();

		Map<String, Object> map2 = new LinkedHashMap<>();

		params.put("$resource[id,value] = /foo/get-foo-data", map2);

		map2.put("@id", "$world.id");

		String json = toJSON(map1);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertTrue(invokerResult.getResult() instanceof List);

		List<Object> expectedList = toList(
			"[{\"id\":1,\"resource\":{\"id\":1,\"value\":\"foo!\"}}," +
				"{\"id\":2,\"resource\":{\"id\":2,\"value\":\"foo!\"}}," +
					"{\"id\":3,\"resource\":{\"id\":3,\"value\":\"foo!\"}}]");

		Assert.assertEquals(expectedList, toList(toJSON(result)));
	}

	@Test
	public void testNoProperty() throws Exception {
		String json = toJSON(
			LinkedHashMapBuilder.<String, Object>put(
				"/foo/bar", new LinkedHashMap<>()
			).build());

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		json = invokerResult.toJSONString();

		Assert.assertEquals("{\"array\":[1,2,3],\"value\":\"value\"}", json);
	}

	@Test
	public void testPropertyInner() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/bar", params
		).build();

		Map<String, Object> innerParam = new LinkedHashMap<>();

		params.put("$new1 = /foo/bar", innerParam);

		innerParam.put("$new2 = /foo/hello", Collections.emptyMap());

		String json = toJSON(map);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		json = invokerResult.toJSONString();

		Assert.assertEquals(2, StringUtil.count(json, "\"array\":[1,2,3]"));
		Assert.assertFalse(json, json.contains("\"secret\""));
		Assert.assertTrue(json, json.contains("\"new1\":{"));
		Assert.assertTrue(json, json.contains("\"new2\":\"world\""));
	}

	@Test
	public void testPropertySimple() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/bar", params
		).build();

		Map<String, Object> innerParam = new LinkedHashMap<>();

		params.put("$new = /foo/hello", innerParam);

		String json = toJSON(map);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		json = invokerResult.toJSONString();

		Assert.assertTrue(json, json.contains("\"array\":[1,2,3]"));
		Assert.assertFalse(json, json.contains("\"secret\""));
		Assert.assertTrue(json, json.contains("\"new\":\"world\""));
	}

	@Test
	public void testSerializationComplexObjects1() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/search", params
		).build();

		params.put("name", "target");
		params.put("params", new String[] {"active:false:boolean"});

		String json = toJSON(map, "*.params");

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"\"search target>active:false:boolean\"", toJSON(invokerResult));

		params.put("params", new String[] {"active", "false", "boolean"});

		json = toJSON(map, "*.params");

		jsonWebServiceAction = prepareInvokerAction(json);

		result = jsonWebServiceAction.invoke();

		invokerResult = (JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"\"search target>active,false,boolean\"", toJSON(invokerResult));
	}

	@Test
	public void testSerializationComplexObjects2() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/complex", params
		).build();

		params.put("longs", "1,2,3");
		params.put("ints", "1,2");
		params.put("map", "{\"key\" : 122}");

		String json = toJSON(map);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals("6", toJSON(invokerResult));
	}

	@Test
	public void testSerializationComplexObjects3() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/complex", params
		).build();

		params.put("longs", new long[] {1, 2, 3});
		params.put("ints", new int[] {1, 2});

		params.put(
			"map",
			HashMapBuilder.put(
				"key", Integer.valueOf(122)
			).build());

		String json = toJSON(map, "*.ints", "*.longs", "*.map");

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals("6", toJSON(invokerResult));
	}

	@Test
	public void testSerializationComplexObjects4() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/complex-with-arrays", params
		).build();

		params.put(
			"longArrays",
			new long[][] {new long[] {1, 2, 3}, new long[] {8, 9}});

		params.put(
			"mapNames",
			HashMapBuilder.put(
				"p1", new String[] {"one", "two"}
			).build());

		String json = toJSON(map, "*.longArrays", "*.mapNames.*");

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"\"[1, 2, 3]|[8, 9]|*p1=[one, two]|\"", toJSON(invokerResult));
	}

	@Test
	public void testSerializationHack() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/bar", params
		).build();

		String json = toJSON(map);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"{\"array\":[1,2,3],\"value\":\"value\"}", toJSON(invokerResult));

		// Hack 1

		map.clear();

		map.put("$* = /foo/bar", params);

		json = toJSON(map);

		jsonWebServiceAction = prepareInvokerAction(json);

		result = jsonWebServiceAction.invoke();

		invokerResult = (JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"{\"array\":[1,2,3],\"value\":\"value\"}", toJSON(invokerResult));

		// Hack 2

		map.clear();

		map.put("$secret = /foo/bar", params);

		json = toJSON(map);

		jsonWebServiceAction = prepareInvokerAction(json);

		result = jsonWebServiceAction.invoke();

		invokerResult = (JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"{\"array\":[1,2,3],\"value\":\"value\"}", toJSON(invokerResult));
	}

	@Test
	public void testServiceContext() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/srvcctx2", params
		).build();

		params.put(
			"serviceContext",
			"{\"failOnPortalException\": false, \"uuid\": \"uuid\"}");

		String json = toJSON(map);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		ServiceContext serviceContext =
			(ServiceContext)invokerResult.getResult();

		Assert.assertEquals("uuid", serviceContext.getUuid());
		Assert.assertFalse(serviceContext.isFailOnPortalException());
	}

	@Test
	public void testSimpleCall() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/hello-world", params
		).build();

		params.put("userId", 173);
		params.put("worldName", "Jupiter");

		String json = toJSON(map);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"Welcome 173 to Jupiter", invokerResult.getResult());
		Assert.assertEquals(
			"\"Welcome 173 to Jupiter\"", toJSON(invokerResult));
	}

	@Test
	public void testSimpleCallUsingCmdParam() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/hello-world", params
		).build();

		params.put("userId", 173);
		params.put("worldName", "Jupiter");

		String command = toJSON(map);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(
			command);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"Welcome 173 to Jupiter", invokerResult.getResult());

		Assert.assertEquals(
			"\"Welcome 173 to Jupiter\"", toJSON(invokerResult));
	}

	@Test
	public void testSimpleCallWithName() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"$world = /foo/hello-world", params
		).build();

		params.put("userId", 173);
		params.put("worldName", "Jupiter");

		String json = toJSON(map);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"Welcome 173 to Jupiter", invokerResult.getResult());

		Assert.assertEquals(
			"\"Welcome 173 to Jupiter\"", toJSON(invokerResult));
	}

	@Test
	public void testSimpleCallWithNull() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/hello-world", params
		).build();

		params.put("userId", 173);
		params.put("worldName", null);

		String json = toJSON(map);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals("Welcome 173 to null", invokerResult.getResult());

		Assert.assertEquals("\"Welcome 173 to null\"", toJSON(invokerResult));
	}

	@Test
	public void testTypeConversion1() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/hey", params
		).build();

		params.put("calendar", "1330419334285");
		params.put("userIds", "1,2,3");
		params.put("locales", "\"en\",\"fr\"");
		params.put("ids", "173,-7,007");

		String json = toJSON(map);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"2012, 1/3, en/2, 173/3", invokerResult.getResult());
	}

	@Test
	public void testTypeConversion2() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/hey", params
		).build();

		params.put("calendar", "1330419334285");
		params.put("userIds", new long[] {1, 2, 3});
		params.put("locales", new String[] {"en", "fr"});
		params.put("ids", new long[] {173, -7, 7});

		String json = toJSON(map, "*.userIds", "*.locales", "*.ids");

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		Assert.assertEquals(
			"2012, 1/3, en/2, 173/3", invokerResult.getResult());
	}

	@Test
	public void testVariableAsList() throws Exception {
		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/bar", params
		).build();

		params.put(
			"$fds[name,value] = /foo/get-foo-datas",
			new HashMap<String, Object>());

		String json = toJSON(map);

		JSONWebServiceAction jsonWebServiceAction = prepareInvokerAction(json);

		Object result = jsonWebServiceAction.invoke();

		JSONWebServiceInvokerAction.InvokerResult invokerResult =
			(JSONWebServiceInvokerAction.InvokerResult)result;

		result = invokerResult.getResult();

		Assert.assertTrue(result instanceof Map);

		map = (Map<String, Object>)result;

		Assert.assertTrue(map.containsKey("array"));
		Assert.assertTrue(map.containsKey("fds"));
		Assert.assertFalse(map.containsKey("secret"));
		Assert.assertTrue(map.containsKey("value"));

		String jsonResult = toJSON(invokerResult);

		Assert.assertFalse(jsonResult, jsonResult.contains("secret"));
	}

	protected JSONWebServiceAction prepareInvokerAction(String content)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/invoker");

		mockHttpServletRequest.setContent(content.getBytes());

		return new JSONWebServiceInvokerAction(
			jsonWebServiceActionsManager, mockHttpServletRequest);
	}

	private final MockedStatic<ServiceContextFactory>
		_serviceContextFactoryMockedStatic = Mockito.mockStatic(
			ServiceContextFactory.class);

}