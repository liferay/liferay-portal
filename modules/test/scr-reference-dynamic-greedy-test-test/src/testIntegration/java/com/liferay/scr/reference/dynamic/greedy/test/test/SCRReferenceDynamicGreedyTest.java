/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scr.reference.dynamic.greedy.test.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.scr.reference.dynamic.greedy.test.ComponentController;
import com.liferay.scr.reference.dynamic.greedy.test.DynamicGreedyComponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class SCRReferenceDynamicGreedyTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDynamicGreedyAtLeastOne() throws Exception {
		_testDynamicGreedyComponent(
			"com.liferay.scr.reference.dynamic.greedy.test.internal." +
				"DynamicGreedyAtLeastOneComponent",
			"at_least_one",
			bindingCalls -> Assert.assertEquals(
				bindingCalls,
				Arrays.asList(
					"bindAtLeastOneDependency-" + _SERVICE_1, "step1",
					"bindAtLeastOneDependency-" + _SERVICE_2, "step2",
					"unbindAtLeastOneDependency-" + _SERVICE_1, "step3",
					"unbindAtLeastOneDependency-" + _SERVICE_2)));
	}

	@Test
	public void testDynamicGreedyFieldReplace() throws Exception {
		_testDynamicGreedyFieldComponent(false);
	}

	@Test
	public void testDynamicGreedyFieldUpdate() throws Exception {
		_testDynamicGreedyFieldComponent(true);
	}

	@Test
	public void testDynamicGreedyMandatory() throws Exception {
		_testDynamicGreedyComponent(
			"com.liferay.scr.reference.dynamic.greedy.test.internal." +
				"DynamicGreedyMandatoryComponent",
			"mandatory",
			bindingCalls -> Assert.assertEquals(
				bindingCalls,
				Arrays.asList(
					"bindMandatoryDependency-" + _SERVICE_1, "step1",
					"bindMandatoryDependency-" + _SERVICE_2,
					"unbindMandatoryDependency-" + _SERVICE_1, "step2", "step3",
					"unbindMandatoryDependency-" + _SERVICE_2)));
	}

	@Test
	public void testDynamicGreedyMultiple() throws Exception {
		_testDynamicGreedyComponent(
			"com.liferay.scr.reference.dynamic.greedy.test.internal." +
				"DynamicGreedyMultipleComponent",
			"multiple",
			bindingCalls -> Assert.assertEquals(
				bindingCalls,
				Arrays.asList(
					"bindMultipleDependency-" + _SERVICE_1, "step1",
					"bindMultipleDependency-" + _SERVICE_2, "step2",
					"unbindMultipleDependency-" + _SERVICE_1, "step3",
					"unbindMultipleDependency-" + _SERVICE_2)));
	}

	@Test
	public void testDynamicGreedyOptional() throws Exception {
		_testDynamicGreedyComponent(
			"com.liferay.scr.reference.dynamic.greedy.test.internal." +
				"DynamicGreedyOptionalComponent",
			"optional",
			bindingCalls -> Assert.assertEquals(
				bindingCalls,
				Arrays.asList(
					"bindOptionalDependency-" + _SERVICE_1, "step1",
					"bindOptionalDependency-" + _SERVICE_2,
					"unbindOptionalDependency-" + _SERVICE_1, "step2", "step3",
					"unbindOptionalDependency-" + _SERVICE_2)));
	}

	private void _testDynamicGreedyComponent(
			String name, String referenceCardinality,
			Consumer<List<String>> bindingCallsConsumer)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			SCRReferenceDynamicGreedyTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"reference.cardinality", referenceCardinality
			).build();

		_componentController.enabledComponent(name);

		ServiceRegistration<?> serviceRegistration1 =
			bundleContext.registerService(Object.class, _SERVICE_1, properties);

		ServiceTracker<DynamicGreedyComponent, DynamicGreedyComponent>
			serviceTracker = new ServiceTracker<>(
				bundleContext,
				bundleContext.createFilter(
					StringBundler.concat(
						"(&(objectClass=",
						DynamicGreedyComponent.class.getName(),
						")(reference.cardinality=", referenceCardinality,
						"))")),
				null);

		serviceTracker.open();

		try {
			DynamicGreedyComponent dynamicGreedyComponent =
				serviceTracker.waitForService(0);

			List<String> bindingCalls =
				dynamicGreedyComponent.getBindingCalls();

			bindingCalls.add("step1");

			properties.put("service.ranking", 1);

			ServiceRegistration<?> serviceRegistration2 =
				bundleContext.registerService(
					Object.class, _SERVICE_2, properties);

			bindingCalls.add("step2");

			serviceRegistration1.unregister();

			bindingCalls.add("step3");

			if (serviceRegistration2 != null) {
				serviceRegistration2.unregister();
			}

			bindingCallsConsumer.accept(bindingCalls);
		}
		finally {
			serviceTracker.close();

			_componentController.disableComponent(name);
		}
	}

	private void _testDynamicGreedyFieldComponent(boolean update)
		throws Exception {

		String name =
			"com.liferay.scr.reference.dynamic.greedy.test.internal." +
				"DynamicGreedyFieldReplaceComponent";

		String fieldOption = "replace";

		if (update) {
			name =
				"com.liferay.scr.reference.dynamic.greedy.test.internal." +
					"DynamicGreedyFieldUpdateComponent";

			fieldOption = "update";
		}

		Bundle bundle = FrameworkUtil.getBundle(
			SCRReferenceDynamicGreedyTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"field.option", fieldOption
			).build();

		_componentController.enabledComponent(name);

		ServiceRegistration<?> serviceRegistration1 =
			bundleContext.registerService(String.class, _SERVICE_1, properties);

		ServiceTracker<DynamicGreedyComponent, DynamicGreedyComponent>
			serviceTracker = new ServiceTracker<>(
				bundleContext,
				bundleContext.createFilter(
					StringBundler.concat(
						"(&(field.option=", fieldOption, ")(objectClass=",
						DynamicGreedyComponent.class.getName(), "))")),
				null);

		serviceTracker.open();

		try {
			DynamicGreedyComponent dynamicGreedyComponent =
				serviceTracker.waitForService(0);

			List<String> bindingCalls =
				dynamicGreedyComponent.getBindingCalls();

			Assert.assertEquals(
				Collections.singletonList(_SERVICE_1), bindingCalls);

			properties.put("service.ranking", 1);

			ServiceRegistration<?> serviceRegistration2 =
				bundleContext.registerService(
					String.class, _SERVICE_2, properties);

			if (update) {
				Assert.assertSame(
					bindingCalls, dynamicGreedyComponent.getBindingCalls());
			}
			else {
				Assert.assertNotSame(
					bindingCalls, dynamicGreedyComponent.getBindingCalls());

				bindingCalls = dynamicGreedyComponent.getBindingCalls();
			}

			Assert.assertEquals(
				Arrays.asList(_SERVICE_1, _SERVICE_2), bindingCalls);

			serviceRegistration1.unregister();

			if (update) {
				Assert.assertSame(
					bindingCalls, dynamicGreedyComponent.getBindingCalls());
			}
			else {
				Assert.assertNotSame(
					bindingCalls, dynamicGreedyComponent.getBindingCalls());

				bindingCalls = dynamicGreedyComponent.getBindingCalls();
			}

			Assert.assertEquals(
				Collections.singletonList(_SERVICE_2), bindingCalls);

			properties.remove("service.ranking");

			serviceRegistration1 = bundleContext.registerService(
				String.class, _SERVICE_1, properties);

			if (update) {
				Assert.assertSame(
					bindingCalls, dynamicGreedyComponent.getBindingCalls());
			}
			else {
				Assert.assertNotSame(
					bindingCalls, dynamicGreedyComponent.getBindingCalls());

				bindingCalls = dynamicGreedyComponent.getBindingCalls();
			}

			if (update) {
				Assert.assertEquals(
					Arrays.asList(_SERVICE_2, _SERVICE_1), bindingCalls);
			}
			else {
				Assert.assertEquals(
					Arrays.asList(_SERVICE_1, _SERVICE_2), bindingCalls);
			}

			serviceRegistration1.unregister();

			serviceRegistration2.unregister();

			if (update) {
				Assert.assertSame(
					bindingCalls, dynamicGreedyComponent.getBindingCalls());
			}
			else {
				Assert.assertNotSame(
					bindingCalls, dynamicGreedyComponent.getBindingCalls());

				bindingCalls = dynamicGreedyComponent.getBindingCalls();
			}

			Assert.assertEquals(bindingCalls, Collections.emptyList());
		}
		finally {
			serviceTracker.close();

			_componentController.disableComponent(name);
		}
	}

	private static final String _SERVICE_1 = "service 1";

	private static final String _SERVICE_2 = "service 2";

	@Inject
	private static ComponentController _componentController;

}