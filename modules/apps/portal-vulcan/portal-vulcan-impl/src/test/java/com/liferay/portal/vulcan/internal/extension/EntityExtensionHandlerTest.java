/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.extension;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.extension.EntityExtensionHandler;
import com.liferay.portal.vulcan.extension.ExtensionProvider;
import com.liferay.portal.vulcan.extension.PropertyDefinition;

import jakarta.validation.ValidationException;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Javier de Arcos
 */
public class EntityExtensionHandlerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_entityExtensionHandler = new EntityExtensionHandler(
			_CLASS_NAME,
			Arrays.asList(
				_mockedExtensionProvider1, _mockedExtensionProvider2));
	}

	@Test
	public void testGetExtendedProperties() throws Exception {
		String propertyName1 = RandomTestUtil.randomString();
		String propertyName2 = RandomTestUtil.randomString();
		String propertyValue1 = RandomTestUtil.randomString();
		long propertyValue2 = RandomTestUtil.randomLong();

		Map<String, Serializable> testMap1 = Collections.singletonMap(
			propertyName1, propertyValue1);

		Mockito.when(
			_mockedExtensionProvider1.getExtendedProperties(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
				Mockito.any())
		).thenReturn(
			testMap1
		);

		Map<String, Serializable> testMap2 = Collections.singletonMap(
			propertyName2, propertyValue2);

		Mockito.when(
			_mockedExtensionProvider2.getExtendedProperties(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
				Mockito.any())
		).thenReturn(
			testMap2
		);

		Map<String, Serializable> extendedProperties =
			_entityExtensionHandler.getExtendedProperties(
				_COMPANY_ID, _USER_ID, _OBJECT);

		Mockito.verify(
			_mockedExtensionProvider1
		).getExtendedProperties(
			Mockito.eq(_COMPANY_ID), Mockito.eq(_USER_ID),
			Mockito.eq(_CLASS_NAME), Mockito.eq(_OBJECT)
		);

		Mockito.verify(
			_mockedExtensionProvider2
		).getExtendedProperties(
			Mockito.eq(_COMPANY_ID), Mockito.eq(_USER_ID),
			Mockito.eq(_CLASS_NAME), Mockito.eq(_OBJECT)
		);

		Assert.assertEquals(
			extendedProperties.toString(), 2, extendedProperties.size());
		Assert.assertEquals(
			propertyValue1, extendedProperties.get(propertyName1));
		Assert.assertEquals(
			propertyValue2, extendedProperties.get(propertyName2));
	}

	@Test
	public void testGetExtendedPropertyDefinitions() throws Exception {
		String propertyName1 = RandomTestUtil.randomString();
		String propertyName2 = RandomTestUtil.randomString();
		PropertyDefinition propertyDefinition1 = Mockito.mock(
			PropertyDefinition.class);
		PropertyDefinition propertyDefinition2 = Mockito.mock(
			PropertyDefinition.class);

		Map<String, PropertyDefinition> testMap1 = Collections.singletonMap(
			propertyName1, propertyDefinition1);

		Mockito.when(
			_mockedExtensionProvider1.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			testMap1
		);

		Map<String, PropertyDefinition> testMap2 = Collections.singletonMap(
			propertyName2, propertyDefinition2);

		Mockito.when(
			_mockedExtensionProvider2.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			testMap2
		);

		Map<String, PropertyDefinition> extendedPropertyDefinitions =
			_entityExtensionHandler.getExtendedPropertyDefinitions(
				_COMPANY_ID, _CLASS_NAME);

		Mockito.verify(
			_mockedExtensionProvider1
		).getExtendedPropertyDefinitions(
			_COMPANY_ID, _CLASS_NAME
		);

		Mockito.verify(
			_mockedExtensionProvider2
		).getExtendedPropertyDefinitions(
			_COMPANY_ID, _CLASS_NAME
		);

		Assert.assertEquals(
			MapUtil.toString(extendedPropertyDefinitions), 2,
			extendedPropertyDefinitions.size());
		Assert.assertSame(
			propertyDefinition1,
			extendedPropertyDefinitions.get(propertyName1));
		Assert.assertSame(
			propertyDefinition2,
			extendedPropertyDefinitions.get(propertyName2));
	}

	@Test
	public void testGetFilteredPropertyNames() {
		String propertyName1 = RandomTestUtil.randomString();
		String propertyName2 = RandomTestUtil.randomString();

		Set<String> testSet1 = Collections.singleton(propertyName1);

		Mockito.doReturn(
			testSet1
		).when(
			_mockedExtensionProvider1
		).getFilteredPropertyNames(
			Mockito.anyLong(), Mockito.any()
		);

		Set<String> testSet2 = Collections.singleton(propertyName2);

		Mockito.doReturn(
			testSet2
		).when(
			_mockedExtensionProvider2
		).getFilteredPropertyNames(
			Mockito.anyLong(), Mockito.any()
		);

		Set<String> filteredProperties =
			_entityExtensionHandler.getFilteredPropertyNames(
				_COMPANY_ID, _OBJECT);

		Mockito.verify(
			_mockedExtensionProvider1
		).getFilteredPropertyNames(
			Mockito.eq(_COMPANY_ID), Mockito.eq(_OBJECT)
		);

		Mockito.verify(
			_mockedExtensionProvider2
		).getFilteredPropertyNames(
			Mockito.eq(_COMPANY_ID), Mockito.eq(_OBJECT)
		);

		Assert.assertEquals(
			filteredProperties.toString(), 2, filteredProperties.size());
		Assert.assertTrue(filteredProperties.contains(propertyName1));
		Assert.assertTrue(filteredProperties.contains(propertyName2));
	}

	@Test
	public void testSetExtendedProperties() throws Exception {
		String propertyName1 = RandomTestUtil.randomString();
		String propertyName2 = RandomTestUtil.randomString();
		String propertyValue1 = RandomTestUtil.randomString();
		long propertyValue2 = RandomTestUtil.randomLong();

		Map<String, Serializable> testExtendedProperties =
			HashMapBuilder.<String, Serializable>put(
				propertyName1, propertyValue1
			).put(
				propertyName2, propertyValue2
			).build();

		Mockito.when(
			_mockedExtensionProvider1.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			Collections.singletonMap(propertyName1, null)
		);

		Mockito.when(
			_mockedExtensionProvider2.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			Collections.singletonMap(propertyName2, null)
		);

		_entityExtensionHandler.setExtendedProperties(
			_COMPANY_ID, _USER_ID, _OBJECT, testExtendedProperties);

		Mockito.verify(
			_mockedExtensionProvider1
		).getExtendedPropertyDefinitions(
			Mockito.eq(_COMPANY_ID), Mockito.eq(_CLASS_NAME)
		);

		Mockito.verify(
			_mockedExtensionProvider2
		).getExtendedPropertyDefinitions(
			Mockito.eq(_COMPANY_ID), Mockito.eq(_CLASS_NAME)
		);

		Mockito.verify(
			_mockedExtensionProvider1
		).setExtendedProperties(
			Mockito.eq(_COMPANY_ID), Mockito.eq(_USER_ID),
			Mockito.eq(_CLASS_NAME), Mockito.eq(_OBJECT),
			Mockito.eq(Collections.singletonMap(propertyName1, propertyValue1))
		);

		Mockito.verify(
			_mockedExtensionProvider2
		).setExtendedProperties(
			Mockito.eq(_COMPANY_ID), Mockito.eq(_USER_ID),
			Mockito.eq(_CLASS_NAME), Mockito.eq(_OBJECT),
			Mockito.eq(Collections.singletonMap(propertyName2, propertyValue2))
		);
	}

	@Test
	public void testValidate() throws Exception {
		ExtensionProvider extensionProviderMock1 = Mockito.mock(
			ExtensionProvider.class);
		ExtensionProvider extensionProviderMock2 = Mockito.mock(
			ExtensionProvider.class);

		String propertyName1 = RandomTestUtil.randomString();
		String propertyName2 = RandomTestUtil.randomString();
		String propertyName3 = RandomTestUtil.randomString();
		String propertyName4 = RandomTestUtil.randomString();

		PropertyDefinition propertyDefinition1 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName1,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition2 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName2,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition3 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName3,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition4 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName4,
			PropertyDefinition.PropertyType.TEXT, false);

		Mockito.when(
			extensionProviderMock1.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			HashMapBuilder.put(
				propertyDefinition1.getPropertyName(), propertyDefinition1
			).put(
				propertyDefinition2.getPropertyName(), propertyDefinition2
			).build()
		);

		Mockito.when(
			extensionProviderMock2.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			HashMapBuilder.put(
				propertyDefinition3.getPropertyName(), propertyDefinition3
			).put(
				propertyDefinition4.getPropertyName(), propertyDefinition4
			).build()
		);

		EntityExtensionHandler entityExtensionHandler =
			new EntityExtensionHandler(
				_CLASS_NAME,
				Arrays.asList(extensionProviderMock1, extensionProviderMock2));

		entityExtensionHandler.validate(
			_COMPANY_ID,
			HashMapBuilder.<String, Serializable>put(
				propertyName1, RandomTestUtil.randomString()
			).<String, Serializable>put(
				propertyName2, RandomTestUtil.randomString()
			).<String, Serializable>put(
				propertyName3, RandomTestUtil.randomString()
			).<String, Serializable>put(
				propertyName4, RandomTestUtil.randomString()
			).build(),
			false);

		Mockito.verify(
			extensionProviderMock1
		).getExtendedPropertyDefinitions(
			_COMPANY_ID, _CLASS_NAME
		);

		Mockito.verify(
			extensionProviderMock2
		).getExtendedPropertyDefinitions(
			_COMPANY_ID, _CLASS_NAME
		);
	}

	@Test(expected = ValidationException.class)
	public void testValidateInvalidProperty() throws Exception {
		ExtensionProvider extensionProviderMock1 = Mockito.mock(
			ExtensionProvider.class);
		ExtensionProvider extensionProviderMock2 = Mockito.mock(
			ExtensionProvider.class);

		String propertyName1 = RandomTestUtil.randomString();
		String propertyName2 = RandomTestUtil.randomString();
		String propertyName3 = RandomTestUtil.randomString();
		String propertyName4 = RandomTestUtil.randomString();

		PropertyDefinition propertyDefinition1 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName1,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition2 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName2,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition3 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName3,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition4 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName4,
			PropertyDefinition.PropertyType.TEXT, true);

		Mockito.when(
			extensionProviderMock1.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			HashMapBuilder.put(
				propertyDefinition1.getPropertyName(), propertyDefinition1
			).put(
				propertyDefinition2.getPropertyName(), propertyDefinition2
			).build()
		);

		Mockito.when(
			extensionProviderMock2.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			HashMapBuilder.put(
				propertyDefinition3.getPropertyName(), propertyDefinition3
			).put(
				propertyDefinition4.getPropertyName(), propertyDefinition4
			).build()
		);

		EntityExtensionHandler entityExtensionHandler =
			new EntityExtensionHandler(
				_CLASS_NAME,
				Arrays.asList(extensionProviderMock1, extensionProviderMock2));

		entityExtensionHandler.validate(
			_COMPANY_ID,
			HashMapBuilder.<String, Serializable>put(
				propertyName1, RandomTestUtil.randomLong()
			).<String, Serializable>put(
				propertyName2, RandomTestUtil.randomString()
			).<String, Serializable>put(
				propertyName3, RandomTestUtil.randomString()
			).<String, Serializable>put(
				propertyName4, RandomTestUtil.randomString()
			).build(),
			false);

		Mockito.verify(
			extensionProviderMock1
		).getExtendedPropertyDefinitions(
			_COMPANY_ID, _CLASS_NAME
		);

		Mockito.verify(
			extensionProviderMock2
		).getExtendedPropertyDefinitions(
			_COMPANY_ID, _CLASS_NAME
		);
	}

	@Test(expected = ValidationException.class)
	public void testValidateMissingRequiredProperty() throws Exception {
		ExtensionProvider extensionProviderMock1 = Mockito.mock(
			ExtensionProvider.class);
		ExtensionProvider extensionProviderMock2 = Mockito.mock(
			ExtensionProvider.class);

		String propertyName1 = RandomTestUtil.randomString();
		String propertyName2 = RandomTestUtil.randomString();
		String propertyName3 = RandomTestUtil.randomString();
		String propertyName4 = RandomTestUtil.randomString();

		PropertyDefinition propertyDefinition1 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName1,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition2 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName2,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition3 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName3,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition4 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName4,
			PropertyDefinition.PropertyType.TEXT, true);

		Mockito.when(
			extensionProviderMock1.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			HashMapBuilder.put(
				propertyDefinition1.getPropertyName(), propertyDefinition1
			).put(
				propertyDefinition2.getPropertyName(), propertyDefinition2
			).build()
		);

		Mockito.when(
			extensionProviderMock2.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			HashMapBuilder.put(
				propertyDefinition3.getPropertyName(), propertyDefinition3
			).put(
				propertyDefinition4.getPropertyName(), propertyDefinition4
			).build()
		);

		EntityExtensionHandler entityExtensionHandler =
			new EntityExtensionHandler(
				_CLASS_NAME,
				Arrays.asList(extensionProviderMock1, extensionProviderMock2));

		entityExtensionHandler.validate(
			_COMPANY_ID,
			HashMapBuilder.<String, Serializable>put(
				propertyName1, RandomTestUtil.randomString()
			).<String, Serializable>put(
				propertyName2, RandomTestUtil.randomString()
			).<String, Serializable>put(
				propertyName3, RandomTestUtil.randomString()
			).build(),
			false);

		Mockito.verify(
			extensionProviderMock1
		).getExtendedPropertyDefinitions(
			_COMPANY_ID, _CLASS_NAME
		);

		Mockito.verify(
			extensionProviderMock2
		).getExtendedPropertyDefinitions(
			_COMPANY_ID, _CLASS_NAME
		);
	}

	@Test
	public void testValidateMissingRequiredPropertyInPartialUpdate()
		throws Exception {

		ExtensionProvider extensionProviderMock1 = Mockito.mock(
			ExtensionProvider.class);
		ExtensionProvider extensionProviderMock2 = Mockito.mock(
			ExtensionProvider.class);

		String propertyName1 = RandomTestUtil.randomString();
		String propertyName2 = RandomTestUtil.randomString();
		String propertyName3 = RandomTestUtil.randomString();
		String propertyName4 = RandomTestUtil.randomString();

		PropertyDefinition propertyDefinition1 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName1,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition2 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName2,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition3 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName3,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition4 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName4,
			PropertyDefinition.PropertyType.TEXT, true);

		Mockito.when(
			extensionProviderMock1.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			HashMapBuilder.put(
				propertyDefinition1.getPropertyName(), propertyDefinition1
			).put(
				propertyDefinition2.getPropertyName(), propertyDefinition2
			).build()
		);

		Mockito.when(
			extensionProviderMock2.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			HashMapBuilder.put(
				propertyDefinition3.getPropertyName(), propertyDefinition3
			).put(
				propertyDefinition4.getPropertyName(), propertyDefinition4
			).build()
		);

		EntityExtensionHandler entityExtensionHandler =
			new EntityExtensionHandler(
				_CLASS_NAME,
				Arrays.asList(extensionProviderMock1, extensionProviderMock2));

		entityExtensionHandler.validate(
			_COMPANY_ID,
			HashMapBuilder.<String, Serializable>put(
				propertyName1, RandomTestUtil.randomString()
			).<String, Serializable>put(
				propertyName2, RandomTestUtil.randomString()
			).<String, Serializable>put(
				propertyName3, RandomTestUtil.randomString()
			).build(),
			true);

		Mockito.verify(
			extensionProviderMock1
		).getExtendedPropertyDefinitions(
			_COMPANY_ID, _CLASS_NAME
		);

		Mockito.verify(
			extensionProviderMock2
		).getExtendedPropertyDefinitions(
			_COMPANY_ID, _CLASS_NAME
		);
	}

	@Test(expected = ValidationException.class)
	public void testValidateUnknownProperty() throws Exception {
		ExtensionProvider extensionProviderMock1 = Mockito.mock(
			ExtensionProvider.class);
		ExtensionProvider extensionProviderMock2 = Mockito.mock(
			ExtensionProvider.class);

		String propertyName1 = RandomTestUtil.randomString();
		String propertyName2 = RandomTestUtil.randomString();
		String propertyName3 = RandomTestUtil.randomString();
		String propertyName4 = RandomTestUtil.randomString();

		PropertyDefinition propertyDefinition1 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName1,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition2 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName2,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition3 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName3,
			PropertyDefinition.PropertyType.TEXT, false);
		PropertyDefinition propertyDefinition4 = new PropertyDefinition(
			RandomTestUtil.randomString(), propertyName4,
			PropertyDefinition.PropertyType.TEXT, true);

		Mockito.when(
			extensionProviderMock1.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			HashMapBuilder.put(
				propertyDefinition1.getPropertyName(), propertyDefinition1
			).put(
				propertyDefinition2.getPropertyName(), propertyDefinition2
			).build()
		);

		Mockito.when(
			extensionProviderMock2.getExtendedPropertyDefinitions(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			HashMapBuilder.put(
				propertyDefinition3.getPropertyName(), propertyDefinition3
			).put(
				propertyDefinition4.getPropertyName(), propertyDefinition4
			).build()
		);

		EntityExtensionHandler entityExtensionHandler =
			new EntityExtensionHandler(
				_CLASS_NAME,
				Arrays.asList(extensionProviderMock1, extensionProviderMock2));

		entityExtensionHandler.validate(
			_COMPANY_ID,
			HashMapBuilder.<String, Serializable>put(
				propertyName1, RandomTestUtil.randomString()
			).<String, Serializable>put(
				propertyName2, RandomTestUtil.randomString()
			).<String, Serializable>put(
				propertyName3, RandomTestUtil.randomString()
			).<String, Serializable>put(
				propertyName4, RandomTestUtil.randomString()
			).<String, Serializable>put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).build(),
			false);

		Mockito.verify(
			extensionProviderMock1
		).getExtendedPropertyDefinitions(
			_COMPANY_ID, _CLASS_NAME
		);

		Mockito.verify(
			extensionProviderMock2
		).getExtendedPropertyDefinitions(
			_COMPANY_ID, _CLASS_NAME
		);
	}

	private static final String _CLASS_NAME =
		"com.liferay.test.model.TestModel";

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final Object _OBJECT = new Object();

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private EntityExtensionHandler _entityExtensionHandler;
	private final ExtensionProvider _mockedExtensionProvider1 = Mockito.mock(
		ExtensionProvider.class);
	private final ExtensionProvider _mockedExtensionProvider2 = Mockito.mock(
		ExtensionProvider.class);

}