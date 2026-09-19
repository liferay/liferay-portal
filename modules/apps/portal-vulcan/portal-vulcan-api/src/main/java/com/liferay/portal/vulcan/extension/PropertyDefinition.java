/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.extension;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.vulcan.extension.validation.DefaultPropertyValidator;
import com.liferay.portal.vulcan.extension.validation.PropertyValidator;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Carlos Correa
 */
public class PropertyDefinition {

	public PropertyDefinition(
		Set<Class<?>> propertyClasses, String propertyClassDescription,
		String propertyClassName, String propertyDescription,
		String propertyName, PropertyType propertyType,
		PropertyValidator propertyValidator, boolean required) {

		_propertyClasses = propertyClasses;
		_propertyClassDescription = propertyClassDescription;
		_propertyClassName = propertyClassName;
		_propertyDescription = propertyDescription;
		_propertyName = propertyName;
		_propertyType = propertyType;
		_propertyValidator = propertyValidator;
		_required = required;
	}

	public PropertyDefinition(
		String propertyDescription, String propertyName,
		PropertyType propertyType, boolean required) {

		_propertyDescription = propertyDescription;
		_propertyName = propertyName;
		_propertyType = propertyType;
		_required = required;

		_propertyClasses = _propertyTypeClasses.get(propertyType);
		_propertyValidator = new DefaultPropertyValidator();
	}

	public PropertyDefinition(
		String propertyDescription, String propertyName,
		PropertyType propertyType, PropertyValidator propertyValidator,
		boolean required) {

		_propertyDescription = propertyDescription;
		_propertyName = propertyName;
		_propertyType = propertyType;
		_propertyValidator = propertyValidator;
		_required = required;

		_propertyClasses = _propertyTypeClasses.get(propertyType);
	}

	public String getPropertyClassDescription() {
		return _propertyClassDescription;
	}

	public String getPropertyClassName() {
		return _propertyClassName;
	}

	public Set<Class<?>> getPropertyClasses() {
		return _propertyClasses;
	}

	public List<PropertyDefinition> getPropertyDefinitions() {
		return _propertyDefinitions;
	}

	public String getPropertyDescription() {
		return _propertyDescription;
	}

	public String getPropertyName() {
		return _propertyName;
	}

	public PropertyType getPropertyType() {
		return _propertyType;
	}

	public PropertyValidator getPropertyValidator() {
		return _propertyValidator;
	}

	public boolean isRequired() {
		return _required;
	}

	public void setPropertyDefinitions(
		List<PropertyDefinition> propertyDefinitions) {

		_propertyDefinitions = propertyDefinitions;
	}

	public enum PropertyType {

		BIG_DECIMAL, BOOLEAN, DATE_TIME, DECIMAL, DOUBLE, INTEGER, LONG,
		MULTIPLE_ELEMENT, SINGLE_ELEMENT, TEXT

	}

	private static final Map<PropertyType, Set<Class<?>>> _propertyTypeClasses =
		HashMapBuilder.<PropertyType, Set<Class<?>>>put(
			PropertyType.BIG_DECIMAL,
			SetUtil.fromArray(
				BigDecimal.class, Double.class, Float.class, Integer.class,
				Long.class)
		).<PropertyType, Set<Class<?>>>put(
			PropertyType.BOOLEAN, SetUtil.fromArray(Boolean.class)
		).<PropertyType, Set<Class<?>>>put(
			PropertyType.DECIMAL,
			SetUtil.fromArray(Float.class, Integer.class, Long.class)
		).<PropertyType, Set<Class<?>>>put(
			PropertyType.DOUBLE, SetUtil.fromArray(Double.class, Float.class)
		).<PropertyType, Set<Class<?>>>put(
			PropertyType.INTEGER, SetUtil.fromArray(Integer.class)
		).<PropertyType, Set<Class<?>>>put(
			PropertyType.LONG, SetUtil.fromArray(Integer.class, Long.class)
		).<PropertyType, Set<Class<?>>>put(
			PropertyType.TEXT, SetUtil.fromArray(String.class)
		).build();

	private String _propertyClassDescription;
	private String _propertyClassName;
	private final Set<Class<?>> _propertyClasses;
	private List<PropertyDefinition> _propertyDefinitions;
	private final String _propertyDescription;
	private final String _propertyName;
	private final PropertyType _propertyType;
	private final PropertyValidator _propertyValidator;
	private final boolean _required;

}