/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.dto.converter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class DTOConverterRegistryTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(DTOConverterRegistryTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@Test
	public void testGetDTOClassNames() throws Exception {
		String dtoClassName = RandomTestUtil.randomString();

		Set<String> dtoClassNames = _dtoConverterRegistry.getDTOClassNames();

		Assert.assertFalse(dtoClassNames.contains(dtoClassName));

		try (AutoCloseable autoCloseable = _registerDTOConverter(
				null, dtoClassName, new TestDTOConverter(), null)) {

			dtoClassNames = _dtoConverterRegistry.getDTOClassNames();

			Assert.assertTrue(dtoClassNames.contains(dtoClassName));
		}
	}

	@Test
	public void testGetDTOClassNamesWithMultipleConvertersAndNoDefault()
		throws Exception {

		String dtoClassName = RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable1 = _registerDTOConverter(
				null, dtoClassName, new TestDTOConverter(), null);
			AutoCloseable autoCloseable2 = _registerDTOConverter(
				null, dtoClassName, new TestDTOConverter(), null)) {

			Set<String> dtoClassNames =
				_dtoConverterRegistry.getDTOClassNames();

			Assert.assertFalse(dtoClassNames.contains(dtoClassName));

			try (AutoCloseable autoCloseable3 = _registerDefaultDTOConverter(
					dtoClassName, new TestDTOConverter(), null)) {

				dtoClassNames = _dtoConverterRegistry.getDTOClassNames();

				Assert.assertTrue(dtoClassNames.contains(dtoClassName));
			}
		}
	}

	@Test
	public void testGetDTOConverterWithApplicationNameDTOClassNameAndVersionProperties()
		throws Exception {

		String applicationName = RandomTestUtil.randomString();
		String dtoClassName = RandomTestUtil.randomString();
		DTOConverter<?, ?> dtoConverter = new TestDTOConverter();
		String version = RandomTestUtil.randomString();

		Assert.assertNull(_dtoConverterRegistry.getDTOConverter(dtoClassName));
		Assert.assertNull(
			_dtoConverterRegistry.getDTOConverter(
				applicationName, dtoClassName, version));

		try (AutoCloseable autoCloseable = _registerDTOConverter(
				applicationName, dtoClassName, dtoConverter, version)) {

			Assert.assertSame(
				dtoConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName));

			Assert.assertSame(
				dtoConverter,
				_dtoConverterRegistry.getDTOConverter(
					applicationName, dtoClassName, version));
		}
	}

	@Test
	public void testGetDTOConverterWithDTOClassNameAndTypeProperties()
		throws Exception {

		String dtoClassName = RandomTestUtil.randomString();

		DTOConverter<?, ?> dtoConverter1 = new TestDTOConverter();
		DTOConverter<?, ?> dtoConverter2 = new TestDTOConverter();

		String type1 = RandomTestUtil.randomString();
		String type2 = RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable1 = _registerDTOConverterWithTypes(
				dtoClassName, dtoConverter1, type1);
			AutoCloseable autoCloseable2 = _registerDTOConverterWithTypes(
				dtoClassName, dtoConverter2, type2)) {

			Assert.assertSame(
				dtoConverter1,
				_dtoConverterRegistry.getDTOConverter(dtoClassName, type1));
			Assert.assertSame(
				dtoConverter2,
				_dtoConverterRegistry.getDTOConverter(dtoClassName, type2));
			Assert.assertNull(
				_dtoConverterRegistry.getDTOConverter(
					dtoClassName, RandomTestUtil.randomString()));
			Assert.assertNull(
				_dtoConverterRegistry.getDTOConverter(dtoClassName));
		}
	}

	@Test
	public void testGetDTOConverterWithDTOClassNameProperty() throws Exception {
		String dtoClassName = RandomTestUtil.randomString();

		Assert.assertNull(_dtoConverterRegistry.getDTOConverter(dtoClassName));

		DTOConverter<?, ?> dtoConverter = new TestDTOConverter();

		try (AutoCloseable autoCloseable = _registerDTOConverter(
				null, dtoClassName, dtoConverter, null)) {

			Assert.assertSame(
				dtoConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName));
		}
	}

	@Test
	public void testGetDTOConverterWithDefaultProperty() throws Exception {
		String dtoClassName = RandomTestUtil.randomString();
		DTOConverter<?, ?> dtoConverter = new TestDTOConverter();

		try (AutoCloseable autoCloseable1 = _registerDefaultDTOConverter(
				dtoClassName, dtoConverter, null);
			AutoCloseable autoCloseable2 = _registerDTOConverter(
				null, dtoClassName, new TestDTOConverter(), null)) {

			Assert.assertSame(
				dtoConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName));
		}

		try (AutoCloseable autoCloseable1 = _registerDTOConverter(
				null, dtoClassName, new TestDTOConverter(), null);
			AutoCloseable autoCloseable2 = _registerDefaultDTOConverter(
				dtoClassName, dtoConverter, null)) {

			Assert.assertSame(
				dtoConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName));
		}
	}

	@Test
	public void testGetDTOConverterWithDefaultPropertyOverridesServiceRanking()
		throws Exception {

		String dtoClassName = RandomTestUtil.randomString();
		DTOConverter<?, ?> dtoConverter = new TestDTOConverter();

		try (AutoCloseable autoCloseable1 = _registerDefaultDTOConverter(
				dtoClassName, dtoConverter, null);
			AutoCloseable autoCloseable2 =
				_registerDTOConverterWithServiceRanking(
					dtoClassName, new TestDTOConverter(), Integer.MAX_VALUE)) {

			Assert.assertSame(
				dtoConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName));
		}

		try (AutoCloseable autoCloseable1 =
				_registerDTOConverterWithServiceRanking(
					dtoClassName, new TestDTOConverter(), Integer.MAX_VALUE);
			AutoCloseable autoCloseable2 = _registerDefaultDTOConverter(
				dtoClassName, dtoConverter, null)) {

			Assert.assertSame(
				dtoConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName));
		}
	}

	@Test
	public void testGetDTOConverterWithEveryRegisteredDTOClassName()
		throws Exception {

		Set<String> unresolvedDTOClassNames = new TreeSet<>();

		for (ServiceReference<DTOConverter<?, ?>> serviceReference :
				_bundleContext.getServiceReferences(
					(Class<DTOConverter<?, ?>>)(Class<?>)DTOConverter.class,
					"(dto.class.name=*)")) {

			String dtoClassName = GetterUtil.getString(
				serviceReference.getProperty("dto.class.name"));

			for (String type : _getTypes(serviceReference)) {
				if (_dtoConverterRegistry.getDTOConverter(dtoClassName, type) !=
						null) {

					continue;
				}

				unresolvedDTOClassNames.add(
					StringBundler.concat(dtoClassName, StringPool.POUND, type));
			}
		}

		Assert.assertTrue(
			unresolvedDTOClassNames.toString(),
			unresolvedDTOClassNames.isEmpty());
	}

	@Test
	public void testGetDTOConverterWithMultipleConvertersAndMultipleDefaults()
		throws Exception {

		String dtoClassName = RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable1 = _registerDefaultDTOConverter(
				dtoClassName, new TestDTOConverter(), null);
			AutoCloseable autoCloseable2 = _registerDefaultDTOConverter(
				dtoClassName, new TestDTOConverter(), null)) {

			Assert.assertNull(
				_dtoConverterRegistry.getDTOConverter(dtoClassName));
		}
	}

	@Test
	public void testGetDTOConverterWithMultipleConvertersAndNoDefault()
		throws Exception {

		String dtoClassName = RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable1 = _registerDTOConverter(
				null, dtoClassName, new TestDTOConverter(), null);
			AutoCloseable autoCloseable2 = _registerDTOConverter(
				null, dtoClassName, new TestDTOConverter(), null)) {

			Assert.assertNull(
				_dtoConverterRegistry.getDTOConverter(dtoClassName));
		}
	}

	@Test
	public void testGetDTOConverterWithMultipleDefaultsAndHigherServiceRanking()
		throws Exception {

		String dtoClassName = RandomTestUtil.randomString();

		DTOConverter<?, ?> dtoConverter = new TestDTOConverter();

		try (AutoCloseable autoCloseable1 = _registerDefaultDTOConverter(
				dtoClassName, new TestDTOConverter(), null);
			AutoCloseable autoCloseable2 = _registerDefaultDTOConverter(
				dtoClassName, dtoConverter, 100)) {

			Assert.assertSame(
				dtoConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName));
		}
	}

	@Test
	public void testGetDTOConverterWithMultipleTypesInOneConverter()
		throws Exception {

		String dtoClassName = RandomTestUtil.randomString();

		DTOConverter<?, ?> dtoConverter = new TestDTOConverter();

		String type1 = RandomTestUtil.randomString();
		String type2 = RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable = _registerDTOConverterWithTypes(
				dtoClassName, dtoConverter, type1, type2)) {

			Assert.assertSame(
				dtoConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName, type1));
			Assert.assertSame(
				dtoConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName, type2));
			Assert.assertNull(
				_dtoConverterRegistry.getDTOConverter(
					dtoClassName, RandomTestUtil.randomString()));
			Assert.assertNull(
				_dtoConverterRegistry.getDTOConverter(dtoClassName, null));
			Assert.assertNull(
				_dtoConverterRegistry.getDTOConverter(dtoClassName));
		}
	}

	@Test
	public void testGetDTOConverterWithTypeAndDefaultOnlyForThatType()
		throws Exception {

		String dtoClassName = RandomTestUtil.randomString();

		DTOConverter<?, ?> dtoConverter1 = new TestDTOConverter();
		DTOConverter<?, ?> dtoConverter2 = new TestDTOConverter();

		String type1 = RandomTestUtil.randomString();
		String type2 = RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable1 =
				_registerDefaultDTOConverterWithType(
					dtoClassName, dtoConverter1, null, type1);
			AutoCloseable autoCloseable2 = _registerDTOConverterWithTypes(
				dtoClassName, dtoConverter2, type2)) {

			Assert.assertSame(
				dtoConverter1,
				_dtoConverterRegistry.getDTOConverter(dtoClassName, type1));
			Assert.assertSame(
				dtoConverter2,
				_dtoConverterRegistry.getDTOConverter(dtoClassName, type2));
			Assert.assertNull(
				_dtoConverterRegistry.getDTOConverter(
					dtoClassName, RandomTestUtil.randomString()));
			Assert.assertNull(
				_dtoConverterRegistry.getDTOConverter(dtoClassName));
		}
	}

	@Test
	public void testGetDTOConverterWithTypeAndDefaultUntypedConverter()
		throws Exception {

		String dtoClassName = RandomTestUtil.randomString();

		DTOConverter<?, ?> dtoConverter1 = new TestDTOConverter();
		DTOConverter<?, ?> dtoConverter2 = new TestDTOConverter();

		String type = RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable1 = _registerDTOConverterWithTypes(
				dtoClassName, dtoConverter1, type);
			AutoCloseable autoCloseable2 = _registerDefaultDTOConverter(
				dtoClassName, dtoConverter2, 100)) {

			Assert.assertSame(
				dtoConverter1,
				_dtoConverterRegistry.getDTOConverter(dtoClassName, type));
			Assert.assertSame(
				dtoConverter2,
				_dtoConverterRegistry.getDTOConverter(
					dtoClassName, RandomTestUtil.randomString()));
			Assert.assertSame(
				dtoConverter2,
				_dtoConverterRegistry.getDTOConverter(dtoClassName, null));
		}
	}

	@Test
	public void testGetDTOConverterWithTypeAndMultipleConvertersForTheSameType()
		throws Exception {

		String dtoClassName = RandomTestUtil.randomString();

		String type = RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable1 = _registerDTOConverterWithTypes(
				dtoClassName, new TestDTOConverter(), type);
			AutoCloseable autoCloseable2 = _registerDTOConverterWithTypes(
				dtoClassName, new TestDTOConverter(), type)) {

			Assert.assertNull(
				_dtoConverterRegistry.getDTOConverter(dtoClassName, type));

			DTOConverter<?, ?> dtoConverter = new TestDTOConverter();

			try (AutoCloseable autoCloseable3 =
					_registerDefaultDTOConverterWithType(
						dtoClassName, dtoConverter, null, type)) {

				Assert.assertSame(
					dtoConverter,
					_dtoConverterRegistry.getDTOConverter(dtoClassName, type));
			}
		}
	}

	@Test
	public void testGetDTOConverterWithTypeAndMultipleDefaults()
		throws Exception {

		String dtoClassName = RandomTestUtil.randomString();

		DTOConverter<?, ?> dtoConverter = new TestDTOConverter();

		String type = RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable1 =
				_registerDefaultDTOConverterWithType(
					dtoClassName, new TestDTOConverter(), null, type);
			AutoCloseable autoCloseable2 = _registerDefaultDTOConverterWithType(
				dtoClassName, dtoConverter, 100, type)) {

			Assert.assertSame(
				dtoConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName, type));
		}

		try (AutoCloseable autoCloseable1 =
				_registerDefaultDTOConverterWithType(
					dtoClassName, new TestDTOConverter(), null, type);
			AutoCloseable autoCloseable2 = _registerDefaultDTOConverterWithType(
				dtoClassName, new TestDTOConverter(), null, type)) {

			Assert.assertNull(
				_dtoConverterRegistry.getDTOConverter(dtoClassName, type));
		}
	}

	@Test
	public void testGetDTOConverterWithTypeAndNoTypedConverter()
		throws Exception {

		String dtoClassName = RandomTestUtil.randomString();

		DTOConverter<?, ?> dtoConverter = new TestDTOConverter();

		try (AutoCloseable autoCloseable = _registerDTOConverter(
				null, dtoClassName, dtoConverter, null)) {

			Assert.assertSame(
				dtoConverter,
				_dtoConverterRegistry.getDTOConverter(
					dtoClassName, RandomTestUtil.randomString()));
			Assert.assertSame(
				dtoConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName, null));
		}
	}

	@Test
	public void testGetDTOConverterWithTypeAndTypedAndUntypedConverters()
		throws Exception {

		String dtoClassName = RandomTestUtil.randomString();

		DTOConverter<?, ?> typedDTOConverter = new TestDTOConverter();
		DTOConverter<?, ?> untypedDTOConverter = new TestDTOConverter();

		String type = RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable1 = _registerDTOConverterWithTypes(
				dtoClassName, typedDTOConverter, type);
			AutoCloseable autoCloseable2 = _registerDTOConverter(
				null, dtoClassName, untypedDTOConverter, null)) {

			Assert.assertSame(
				typedDTOConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName, type));
			Assert.assertSame(
				untypedDTOConverter,
				_dtoConverterRegistry.getDTOConverter(
					dtoClassName, RandomTestUtil.randomString()));
			Assert.assertSame(
				untypedDTOConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName, null));
			Assert.assertSame(
				untypedDTOConverter,
				_dtoConverterRegistry.getDTOConverter(dtoClassName));
		}
	}

	private String[] _getTypes(
		ServiceReference<DTOConverter<?, ?>> serviceReference) {

		Object types = serviceReference.getProperty("dto.class.type");

		if (types == null) {
			return new String[] {null};
		}

		if (types instanceof String[]) {
			return (String[])types;
		}

		return new String[] {String.valueOf(types)};
	}

	private AutoCloseable _registerDTOConverter(
		String applicationName, String dtoClassName,
		DTOConverter<?, ?> dtoConverter, String version) {

		ServiceRegistration<DTOConverter<?, ?>> serviceRegistration =
			_bundleContext.registerService(
				(Class<DTOConverter<?, ?>>)(Class<?>)DTOConverter.class,
				dtoConverter,
				HashMapDictionaryBuilder.put(
					"application.name", () -> applicationName
				).put(
					"dto.class.name", dtoClassName
				).put(
					"version", () -> version
				).build());

		return serviceRegistration::unregister;
	}

	private AutoCloseable _registerDTOConverterWithServiceRanking(
		String dtoClassName, DTOConverter<?, ?> dtoConverter,
		int serviceRanking) {

		ServiceRegistration<DTOConverter<?, ?>> serviceRegistration =
			_bundleContext.registerService(
				(Class<DTOConverter<?, ?>>)(Class<?>)DTOConverter.class,
				dtoConverter,
				HashMapDictionaryBuilder.<String, Object>put(
					"dto.class.name", dtoClassName
				).put(
					"service.ranking", serviceRanking
				).build());

		return serviceRegistration::unregister;
	}

	private AutoCloseable _registerDTOConverterWithTypes(
		String dtoClassName, DTOConverter<?, ?> dtoConverter, String... types) {

		ServiceRegistration<DTOConverter<?, ?>> serviceRegistration =
			_bundleContext.registerService(
				(Class<DTOConverter<?, ?>>)(Class<?>)DTOConverter.class,
				dtoConverter,
				HashMapDictionaryBuilder.<String, Object>put(
					"dto.class.name", dtoClassName
				).put(
					"dto.class.type", _toProperty(types)
				).build());

		return serviceRegistration::unregister;
	}

	private AutoCloseable _registerDefaultDTOConverter(
		String dtoClassName, DTOConverter<?, ?> dtoConverter,
		Integer serviceRanking) {

		ServiceRegistration<DTOConverter<?, ?>> serviceRegistration =
			_bundleContext.registerService(
				(Class<DTOConverter<?, ?>>)(Class<?>)DTOConverter.class,
				dtoConverter,
				HashMapDictionaryBuilder.<String, Object>put(
					"default", "true"
				).put(
					"dto.class.name", dtoClassName
				).put(
					"service.ranking", () -> serviceRanking
				).build());

		return serviceRegistration::unregister;
	}

	private AutoCloseable _registerDefaultDTOConverterWithType(
		String dtoClassName, DTOConverter<?, ?> dtoConverter,
		Integer serviceRanking, String type) {

		ServiceRegistration<DTOConverter<?, ?>> serviceRegistration =
			_bundleContext.registerService(
				(Class<DTOConverter<?, ?>>)(Class<?>)DTOConverter.class,
				dtoConverter,
				HashMapDictionaryBuilder.<String, Object>put(
					"default", "true"
				).put(
					"dto.class.name", dtoClassName
				).put(
					"dto.class.type", type
				).put(
					"service.ranking", () -> serviceRanking
				).build());

		return serviceRegistration::unregister;
	}

	private Object _toProperty(String[] types) {
		if (types.length == 1) {
			return types[0];
		}

		return types;
	}

	private static BundleContext _bundleContext;

	@Inject
	private DTOConverterRegistry _dtoConverterRegistry;

	private static class TestDTOConverter
		implements DTOConverter<Object, Object> {

		@Override
		public String getContentType() {
			return "";
		}

	}

}