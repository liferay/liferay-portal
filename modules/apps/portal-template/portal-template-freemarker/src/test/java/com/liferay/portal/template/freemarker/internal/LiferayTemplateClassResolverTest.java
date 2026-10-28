/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import freemarker.core.TemplateClassResolver;

import freemarker.template.TemplateException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tomas Polesovsky
 * @author Manuel de la Peña
 */
public class LiferayTemplateClassResolverTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_liferayTemplateClassResolver = new LiferayTemplateClassResolver();

		_updateProperties(null);
	}

	@Test
	public void testResolveAllowedClassByClassName() throws Exception {
		_updateProperties("freemarker.template.utility.ClassUtil");

		_liferayTemplateClassResolver.resolve(
			"freemarker.template.utility.ClassUtil", null, null);
	}

	@Test
	public void testResolveAllowedClassByStar() throws Exception {
		_updateProperties("freemarker.template.utility.*");

		_liferayTemplateClassResolver.resolve(
			"freemarker.template.utility.ClassUtil", null, null);
	}

	@Test
	public void testResolveAllowedExecuteClass() throws Exception {
		_updateProperties("freemarker.template.utility.*");

		_testResolveNotAllowedClass("freemarker.template.utility.Execute");
	}

	@Test
	public void testResolveAllowedInvalidClass() throws Exception {
		_updateProperties("invalidClass");

		try {
			_liferayTemplateClassResolver.resolve("invalidClass", null, null);

			Assert.fail();
		}
		catch (TemplateException templateException) {
			ClassNotFoundException classNotFoundException =
				(ClassNotFoundException)templateException.getCause();

			Assert.assertEquals(
				"invalidClass", classNotFoundException.getMessage());
		}
	}

	@Test
	public void testResolveAllowedObjectConstructorClass() throws Exception {
		_updateProperties("freemarker.template.utility.*");

		_testResolveNotAllowedClass(
			"freemarker.template.utility.ObjectConstructor");
	}

	@Test
	public void testResolveAllowedPortalClass() throws Exception {
		_updateProperties("com.liferay.portal.kernel.model.User");

		_liferayTemplateClassResolver.resolve(
			"com.liferay.portal.kernel.model.User", null, null);
	}

	@Test
	public void testResolveClassClass() {
		_testResolveNotAllowedClass("java.lang.Class");
	}

	@Test
	public void testResolveClassLoaderClass() {
		_testResolveNotAllowedClass("java.lang.ClassLoader");
	}

	@Test
	public void testResolveExecuteClass() {
		_testResolveNotAllowedClass("freemarker.template.utility.Execute");
	}

	@Test
	public void testResolveNotAllowedPortalClass() {
		_testResolveNotAllowedClass("com.liferay.portal.kernel.model.User");
	}

	@Test
	public void testResolveObjectConstructorClass() {
		_testResolveNotAllowedClass(
			"freemarker.template.utility.ObjectConstructor");
	}

	@Test
	public void testResolveThreadClass() {
		_testResolveNotAllowedClass("java.lang.Thread");
	}

	private void _testResolveNotAllowedClass(String className) {
		try {
			_liferayTemplateClassResolver.resolve(className, null, null);

			Assert.fail();
		}
		catch (TemplateException templateException) {
			Assert.assertEquals(
				StringBundler.concat(
					"Instantiating ", className, " is not allowed in the ",
					"template for security reasons"),
				templateException.getMessage());
		}
	}

	private void _updateProperties(String allowedInstantiableClasses)
		throws Exception {

		Object freeMarkerEngineConfiguration = ProxyUtil.newProxyInstance(
			LiferayTemplateClassResolverTest.class.getClassLoader(),
			new Class<?>[] {FreeMarkerEngineConfiguration.class},
			new InvocationHandler() {

				@Override
				public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {

					String methodName = method.getName();

					if (methodName.equals("allowedInstantiableClasses")) {
						if (allowedInstantiableClasses != null) {
							return new String[] {allowedInstantiableClasses};
						}

						return null;
					}

					return null;
				}

			});

		ReflectionTestUtil.setFieldValue(
			_liferayTemplateClassResolver, "_freeMarkerEngineConfiguration",
			freeMarkerEngineConfiguration);
	}

	private TemplateClassResolver _liferayTemplateClassResolver;

}