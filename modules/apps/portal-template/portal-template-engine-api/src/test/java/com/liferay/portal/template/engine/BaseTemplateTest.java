/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.engine;

import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Shuyang Zhou
 */
public class BaseTemplateTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testClear() {
		Map<String, Object> sharedContext = HashMapBuilder.<String, Object>put(
			"key2", "shared2"
		).build();

		BaseTemplate baseTemplate = _createBaseTemplate(sharedContext);

		baseTemplate.put("key1", "own1");

		Map<String, String> taglibContext = HashMapBuilder.put(
			"key3", "taglib3"
		).build();

		baseTemplate.setTaglibContext(taglibContext);

		baseTemplate.clear();

		Assert.assertNull(baseTemplate.get("key2"));
		Assert.assertNull(baseTemplate.get("key3"));

		Assert.assertFalse(baseTemplate.containsKey("key2"));
		Assert.assertFalse(baseTemplate.containsKey("key3"));
		Assert.assertTrue(baseTemplate.isEmpty());

		Assert.assertEquals(0, baseTemplate.size());
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"key2", "shared2"
			).build(),
			sharedContext);
		Assert.assertEquals(
			HashMapBuilder.put(
				"key3", "taglib3"
			).build(),
			taglibContext);
	}

	@Test
	public void testContainsKey() {
		BaseTemplate baseTemplate = _createBaseTemplate(
			HashMapBuilder.<String, Object>put(
				"key2", "shared2"
			).put(
				"key3", "shared3"
			).build());

		baseTemplate.put("key1", "own1");

		baseTemplate.setTaglibContext(
			HashMapBuilder.put(
				"key4", "taglib4"
			).build());

		baseTemplate.remove("key3");

		Assert.assertTrue(baseTemplate.containsKey("key1"));
		Assert.assertTrue(baseTemplate.containsKey("key2"));
		Assert.assertFalse(baseTemplate.containsKey("key3"));
		Assert.assertTrue(baseTemplate.containsKey("key4"));
		Assert.assertFalse(baseTemplate.containsKey("key5"));
	}

	@Test
	public void testContainsValue() {
		BaseTemplate baseTemplate = _createBaseTemplate(
			HashMapBuilder.<String, Object>put(
				"key2", "shared2"
			).put(
				"key3", "shared3"
			).put(
				"key4", "shared4"
			).build());

		baseTemplate.put("key1", "own1");
		baseTemplate.put("key3", "own3");
		baseTemplate.remove("key4");

		Assert.assertTrue(baseTemplate.containsValue("own1"));
		Assert.assertTrue(baseTemplate.containsValue("own3"));
		Assert.assertTrue(baseTemplate.containsValue("shared2"));
		Assert.assertFalse(baseTemplate.containsValue("shared3"));
		Assert.assertFalse(baseTemplate.containsValue("shared4"));
	}

	@Test
	public void testEntrySet() {
		BaseTemplate baseTemplate = _createBaseTemplate(
			HashMapBuilder.<String, Object>put(
				"key2", "shared2"
			).put(
				"key3", "shared3"
			).put(
				"key5", "shared5"
			).build());

		baseTemplate.put("key1", "own1");
		baseTemplate.put("key2", "own2");

		baseTemplate.setTaglibContext(
			HashMapBuilder.put(
				"key3", "taglib3"
			).put(
				"key4", "taglib4"
			).build());

		baseTemplate.put("key4", "own4");
		baseTemplate.remove("key5");

		List<String> keys = new ArrayList<>();

		for (Map.Entry<String, Object> entry : baseTemplate.entrySet()) {
			keys.add(entry.getKey());
		}

		Assert.assertEquals(keys.toString(), 4, keys.size());

		Map<String, Object> expectedMap = HashMapBuilder.<String, Object>put(
			"key1", "own1"
		).put(
			"key2", "own2"
		).put(
			"key3", "taglib3"
		).put(
			"key4", "own4"
		).build();

		Assert.assertEquals(expectedMap, new HashMap<>(baseTemplate));
		Assert.assertEquals(expectedMap.keySet(), baseTemplate.keySet());

		Assert.assertEquals(4, baseTemplate.size());

		Collection<Object> values = baseTemplate.values();

		Assert.assertEquals(values.toString(), 4, values.size());
	}

	@Test
	public void testGet() {
		_testGet(false);
		_testGet(true);
	}

	@Test
	public void testPrepare() {
		BaseTemplate baseTemplate = _createBaseTemplate(
			null,
			new TemplateContextHelper() {

				@Override
				public void prepare(
					Map<String, Object> contextObjects,
					HttpServletRequest httpServletRequest) {

					contextObjects.put("key2", "eager2");
				}

				@Override
				protected void populateLazyContextFunctions(
					Map<String, Function<HttpServletRequest, Object>>
						lazyContextFunctions) {

					lazyContextFunctions.put(
						"key1", httpServletRequest -> "lazy1");
					lazyContextFunctions.put(
						"key2", httpServletRequest -> "lazy2");
					lazyContextFunctions.put(
						"key3", httpServletRequest -> "lazy3");
					lazyContextFunctions.put(
						"key5", httpServletRequest -> null);
				}

			});

		baseTemplate.put("key1", "own1");
		baseTemplate.put("key4", "own4");
		baseTemplate.put("key5", "own5");

		baseTemplate.prepare(new MockHttpServletRequest());

		Assert.assertEquals("lazy1", baseTemplate.get("key1"));
		Assert.assertEquals("eager2", baseTemplate.get("key2"));
		Assert.assertEquals("lazy3", baseTemplate.get("key3"));
		Assert.assertEquals("own4", baseTemplate.get("key4"));
		Assert.assertEquals("own5", baseTemplate.get("key5"));

		baseTemplate.put("key3", "own3");

		Assert.assertEquals("own3", baseTemplate.get("key3"));
	}

	@Test
	public void testPut() {
		BaseTemplate baseTemplate = _createBaseTemplate(
			HashMapBuilder.<String, Object>put(
				"key1", "shared1"
			).put(
				"key2", "shared2"
			).build());

		Assert.assertNull(baseTemplate.put("key1", "own1"));
		Assert.assertEquals("own1", baseTemplate.get("key1"));
		Assert.assertEquals("own1", baseTemplate.put("key1", "own1new"));
		Assert.assertEquals("own1new", baseTemplate.get("key1"));

		baseTemplate.remove("key2");

		Assert.assertNull(baseTemplate.put("key2", "own2"));
		Assert.assertEquals("own2", baseTemplate.get("key2"));
		Assert.assertNull(baseTemplate.put("key3", "own3"));
	}

	@Test
	public void testRemove() {
		Map<String, Object> sharedContext = HashMapBuilder.<String, Object>put(
			"key1", "shared1"
		).put(
			"key2", "shared2"
		).build();

		BaseTemplate baseTemplate = _createBaseTemplate(sharedContext);

		Assert.assertEquals("shared1", baseTemplate.remove("key1"));
		Assert.assertNull(baseTemplate.get("key1"));
		Assert.assertNull(baseTemplate.remove("key1"));

		Set<String> keys = baseTemplate.keySet();

		Assert.assertEquals(keys.toString(), 1, keys.size());
		Assert.assertTrue(keys.contains("key2"));

		baseTemplate.put("key1", "own1");

		Assert.assertEquals("own1", baseTemplate.get("key1"));
		Assert.assertEquals("own1", baseTemplate.remove("key1"));
		Assert.assertNull(baseTemplate.get("key1"));
		Assert.assertNull(baseTemplate.remove("key3"));
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"key1", "shared1"
			).put(
				"key2", "shared2"
			).build(),
			sharedContext);
	}

	@Test
	public void testSetTaglibContext() {
		BaseTemplate baseTemplate = _createBaseTemplate(
			HashMapBuilder.<String, Object>put(
				"key5", "shared5"
			).build());

		baseTemplate.put("key1", "own1");
		baseTemplate.put("key2", "own2");

		Map<String, String> taglibContext1 = HashMapBuilder.put(
			"key1", "taglib1"
		).put(
			"key3", "taglib1"
		).put(
			"key6", "taglib1"
		).build();

		baseTemplate.setTaglibContext(taglibContext1);

		Assert.assertEquals("taglib1", baseTemplate.get("key1"));
		Assert.assertEquals("own2", baseTemplate.get("key2"));
		Assert.assertEquals("taglib1", baseTemplate.get("key3"));
		Assert.assertEquals("shared5", baseTemplate.get("key5"));
		Assert.assertEquals("taglib1", baseTemplate.get("key6"));

		baseTemplate.put("key3", "own3");

		Assert.assertEquals("own3", baseTemplate.get("key3"));

		baseTemplate.remove("key1");

		Assert.assertNull(baseTemplate.get("key1"));

		baseTemplate.setTaglibContext(
			HashMapBuilder.put(
				"key1", "taglib2"
			).put(
				"key3", "taglib2"
			).put(
				"key4", "taglib2"
			).build());

		Assert.assertEquals("taglib2", baseTemplate.get("key1"));
		Assert.assertEquals("own2", baseTemplate.get("key2"));
		Assert.assertEquals("taglib2", baseTemplate.get("key3"));
		Assert.assertEquals("taglib2", baseTemplate.get("key4"));
		Assert.assertEquals("shared5", baseTemplate.get("key5"));
		Assert.assertNull(baseTemplate.get("key6"));
		Assert.assertEquals(
			HashMapBuilder.put(
				"key1", "taglib1"
			).put(
				"key3", "taglib1"
			).put(
				"key6", "taglib1"
			).build(),
			taglibContext1);
	}

	@Test
	public void testSetTaglibContextWithNullValue() {
		BaseTemplate baseTemplate = _createBaseTemplate(
			HashMapBuilder.<String, Object>put(
				"key4", "shared4"
			).build());

		baseTemplate.put("key1", "own1");

		Map<String, Object> taglibContext = new HashMap<>();

		taglibContext.put("key1", null);
		taglibContext.put("key2", null);
		taglibContext.put("key3", "taglib3");
		taglibContext.put("key4", null);

		baseTemplate.setTaglibContext(taglibContext);

		Assert.assertEquals("own1", baseTemplate.get("key1"));
		Assert.assertNull(baseTemplate.get("key2"));
		Assert.assertEquals("taglib3", baseTemplate.get("key3"));
		Assert.assertEquals("shared4", baseTemplate.get("key4"));

		Set<String> keys = baseTemplate.keySet();

		Assert.assertFalse(baseTemplate.containsKey("key2"));
		Assert.assertEquals(SetUtil.fromArray("key1", "key3", "key4"), keys);

		Assert.assertEquals(3, baseTemplate.size());
	}

	private BaseTemplate _createBaseTemplate(
		Map<String, Object> sharedContext) {

		return _createBaseTemplate(sharedContext, new TemplateContextHelper());
	}

	private BaseTemplate _createBaseTemplate(
		Map<String, Object> sharedContext,
		TemplateContextHelper templateContextHelper) {

		TemplateResource templateResource = new StringTemplateResource(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		return new BaseTemplate(
			templateResource, sharedContext, templateContextHelper, false) {

			@Override
			protected void handleException(
				TemplateResource templateResource,
				TemplateResource errorTemplateResource, Exception exception,
				Writer writer) {
			}

			@Override
			protected void processTemplate(
				TemplateResource templateResource, Writer writer) {
			}

		};
	}

	private void _testGet(boolean prepareFirst) {
		BaseTemplate baseTemplate = _createBaseTemplate(
			HashMapBuilder.<String, Object>put(
				"key1", "shared1"
			).put(
				"key2", "shared2"
			).put(
				"key3", "shared3"
			).build(),
			new TemplateContextHelper() {

				@Override
				public void prepare(
					Map<String, Object> contextObjects,
					HttpServletRequest httpServletRequest) {
				}

				@Override
				protected void populateLazyContextFunctions(
					Map<String, Function<HttpServletRequest, Object>>
						lazyContextFunctions) {

					lazyContextFunctions.put(
						"key3", httpServletRequest -> "lazy3");
				}

			});

		Map<String, String> taglibContext = HashMapBuilder.put(
			"key2", "taglib2"
		).put(
			"key3", "taglib3"
		).build();

		if (prepareFirst) {
			baseTemplate.prepare(new MockHttpServletRequest());
			baseTemplate.setTaglibContext(taglibContext);
		}
		else {
			baseTemplate.setTaglibContext(taglibContext);
			baseTemplate.prepare(new MockHttpServletRequest());
		}

		Assert.assertEquals("shared1", baseTemplate.get("key1"));
		Assert.assertEquals("taglib2", baseTemplate.get("key2"));
		Assert.assertEquals("lazy3", baseTemplate.get("key3"));
	}

}