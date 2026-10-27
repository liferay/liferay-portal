/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.engine;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Shuyang Zhou
 */
public class TemplateContextHelperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCreateLazyContext() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute("key", "value");

		Map<String, Object> lazyContext =
			_templateContextHelper.createLazyContext(
				new HashMap<>(), mockHttpServletRequest, false);

		Assert.assertEquals("value", lazyContext.get("key"));
		Assert.assertEquals("value", lazyContext.get("key"));
		Assert.assertNull(lazyContext.get("nullKey"));

		Assert.assertEquals(
			HashMapBuilder.put(
				"key", 1
			).put(
				"nullKey", 1
			).build(),
			_counts);

		Assert.assertEquals(
			SetUtil.fromArray("key", "restrictedKey", "unreadKey"),
			lazyContext.keySet());
		Assert.assertEquals(
			HashMapBuilder.put(
				"key", 1
			).put(
				"nullKey", 1
			).put(
				"restrictedKey", 1
			).put(
				"unreadKey", 1
			).build(),
			_counts);
		Assert.assertFalse(lazyContext.containsKey("nullKey"));

		lazyContext = _templateContextHelper.createLazyContext(
			new HashMap<>(), mockHttpServletRequest, false);

		Assert.assertEquals("value", lazyContext.get("key"));

		Assert.assertEquals(Integer.valueOf(2), _counts.get("key"));

		try {
			lazyContext.put("key", "value");

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
		}

		try {
			lazyContext.remove("key");

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
		}
	}

	@Test
	public void testCreateLazyContextRestricted() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute("key", "value");

		Map<String, Object> lazyContext =
			_templateContextHelper.createLazyContext(
				new HashMap<>(), mockHttpServletRequest, true);

		Assert.assertNull(lazyContext.get("restrictedKey"));
		Assert.assertFalse(lazyContext.containsKey("restrictedKey"));
		Assert.assertEquals(
			SetUtil.fromArray("key", "unreadKey"), lazyContext.keySet());

		Assert.assertNull(_counts.get("restrictedKey"));
	}

	@Test
	public void testCreateLazyContextWithTemplateContextContributors() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		TemplateContextContributor declaredTemplateContextContributor =
			(contextObjects, httpServletRequest) -> {
				_counts.merge("declared", 1, Integer::sum);

				contextObjects.put("contributorKey1", contextObjects);
				contextObjects.put(
					"contributorKey2", httpServletRequest.getAttribute("key"));
				contextObjects.put("restrictedKey", "contributorValue");
			};

		ServiceRegistration<TemplateContextContributor>
			declaredServiceRegistration = bundleContext.registerService(
				TemplateContextContributor.class,
				declaredTemplateContextContributor,
				HashMapDictionaryBuilder.<String, Object>put(
					"template.context.keys",
					new String[] {
						"contributorKey1", "contributorKey2", "restrictedKey"
					}
				).put(
					"type", TemplateContextContributor.TYPE_GLOBAL
				).build());

		TemplateContextContributor undeclaredTemplateContextContributor =
			(contextObjects, httpServletRequest) -> _counts.merge(
				"undeclared", 1, Integer::sum);

		ServiceRegistration<TemplateContextContributor>
			undeclaredServiceRegistration = bundleContext.registerService(
				TemplateContextContributor.class,
				undeclaredTemplateContextContributor,
				HashMapDictionaryBuilder.<String, Object>put(
					"type", TemplateContextContributor.TYPE_GLOBAL
				).build());

		_templateContextHelper.init(bundleContext);

		try {
			List<TemplateContextContributor> templateContextContributors =
				new ArrayList<>();

			for (TemplateContextContributor templateContextContributor :
					_templateContextHelper.getTemplateContextContributors()) {

				templateContextContributors.add(templateContextContributor);
			}

			Assert.assertEquals(
				Collections.singletonList(undeclaredTemplateContextContributor),
				templateContextContributors);

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute("key", "value");

			Map<String, Object> contextObjects =
				HashMapBuilder.<String, Object>put(
					"templateKey", "templateValue"
				).build();

			Map<String, Object> lazyContext =
				_templateContextHelper.createLazyContext(
					contextObjects, mockHttpServletRequest, false);

			Assert.assertEquals("value", lazyContext.get("key"));

			Assert.assertNull(_counts.get("declared"));

			Map<String, Object> contributorContext =
				(Map<String, Object>)lazyContext.get("contributorKey1");

			Assert.assertEquals(
				"templateValue", contributorContext.get("templateKey"));

			Assert.assertEquals("value", lazyContext.get("contributorKey2"));
			Assert.assertEquals(
				"contributorValue", lazyContext.get("restrictedKey"));
			Assert.assertEquals(Integer.valueOf(1), _counts.get("declared"));
			Assert.assertNull(_counts.get("restrictedKey"));

			Set<String> keys = lazyContext.keySet();

			Assert.assertTrue(keys.contains("contributorKey1"));
			Assert.assertFalse(keys.contains(StringPool.STAR));

			Assert.assertNull(lazyContext.get(StringPool.STAR));
			Assert.assertNull(_counts.get("undeclared"));

			lazyContext = _templateContextHelper.createLazyContext(
				contextObjects, mockHttpServletRequest, true);

			Assert.assertEquals("value", lazyContext.get("contributorKey2"));
			Assert.assertNull(lazyContext.get("restrictedKey"));

			Assert.assertEquals(Integer.valueOf(2), _counts.get("declared"));
		}
		finally {
			_templateContextHelper.destory();

			declaredServiceRegistration.unregister();
			undeclaredServiceRegistration.unregister();
		}
	}

	private final Map<String, Integer> _counts = new HashMap<>();

	private final TemplateContextHelper _templateContextHelper =
		new TemplateContextHelper() {

			@Override
			public Set<String> getRestrictedVariables() {
				return Collections.singleton("restrictedKey");
			}

			@Override
			protected void populateLazyContextFunctions(
				Map<String, Function<HttpServletRequest, Object>>
					lazyContextFunctions) {

				lazyContextFunctions.put(
					"key",
					httpServletRequest -> {
						_counts.merge("key", 1, Integer::sum);

						return httpServletRequest.getAttribute("key");
					});
				lazyContextFunctions.put(
					"nullKey",
					httpServletRequest -> {
						_counts.merge("nullKey", 1, Integer::sum);

						return null;
					});
				lazyContextFunctions.put(
					"restrictedKey",
					httpServletRequest -> {
						_counts.merge("restrictedKey", 1, Integer::sum);

						return RandomTestUtil.randomString();
					});
				lazyContextFunctions.put(
					"unreadKey",
					httpServletRequest -> {
						_counts.merge("unreadKey", 1, Integer::sum);

						return RandomTestUtil.randomString();
					});
			}

		};

}