/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.impl;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.social.kernel.model.SocialActivityInterpreter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Leon Chi
 */
public class SocialActivityInterpreterLocalServiceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_socialActivityInterpreterLocalServiceImpl =
			new SocialActivityInterpreterLocalServiceImpl();

		_socialActivityInterpreterLocalServiceImpl.afterPropertiesSet();

		_serviceRegistration = bundleContext.registerService(
			SocialActivityInterpreter.class,
			(SocialActivityInterpreter)ProxyUtil.newProxyInstance(
				SocialActivityInterpreter.class.getClassLoader(),
				new Class<?>[] {SocialActivityInterpreter.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "getClassNames")) {
						return _CLASS_NAMES;
					}

					if (Objects.equals(method.getName(), "getSelector")) {
						return _SELECTOR;
					}

					return null;
				}),
			MapUtil.singletonDictionary(
				"jakarta.portlet.name",
				"SocialActivityInterpreterLocalServiceImplTest"));
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetActivityInterpreters() {
		Map<String, List<SocialActivityInterpreter>> activityInterpreters =
			_socialActivityInterpreterLocalServiceImpl.
				getActivityInterpreters();

		List<SocialActivityInterpreter> socialActivityInterpreters =
			activityInterpreters.get(_SELECTOR);

		Assert.assertEquals(
			socialActivityInterpreters.toString(), 1,
			socialActivityInterpreters.size());

		SocialActivityInterpreter socialActivityInterpreter =
			socialActivityInterpreters.get(0);

		Assert.assertSame(_SELECTOR, socialActivityInterpreter.getSelector());
		Assert.assertSame(
			_CLASS_NAMES, socialActivityInterpreter.getClassNames());
	}

	@Test
	public void testGetActivityInterpretersBySelector() {
		List<SocialActivityInterpreter> activityInterpreters =
			_socialActivityInterpreterLocalServiceImpl.getActivityInterpreters(
				_SELECTOR);

		Assert.assertEquals(
			activityInterpreters.toString(), 1, activityInterpreters.size());

		SocialActivityInterpreter socialActivityInterpreter =
			activityInterpreters.get(0);

		Assert.assertSame(_SELECTOR, socialActivityInterpreter.getSelector());
		Assert.assertSame(
			_CLASS_NAMES, socialActivityInterpreter.getClassNames());
	}

	private static final String[] _CLASS_NAMES = {
		"TestSocialActivityInterpreter"
	};

	private static final String _SELECTOR = "SELECTOR";

	private static ServiceRegistration<SocialActivityInterpreter>
		_serviceRegistration;
	private static SocialActivityInterpreterLocalServiceImpl
		_socialActivityInterpreterLocalServiceImpl;

}