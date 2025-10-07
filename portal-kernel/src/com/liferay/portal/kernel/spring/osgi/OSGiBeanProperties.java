/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.spring.osgi;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

/**
 * Provides the OSGi service properties used when publishing Spring beans as
 * services.
 *
 * @author Raymond Augé
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OSGiBeanProperties {

	/**
	 * Returns <code>true</code> if the property prefix should be removed from
	 * <code>portal.properties</code>.
	 *
	 * @return <code>true</code> if the property prefix should be removed from
	 *         <code>portal.properties</code>; <code>false</code> otherwise
	 */
	public boolean portalPropertiesRemovePrefix() default true;

	/**
	 * Returns the value of the property prefix used for retrieving properties
	 * from <code>portal.properties</code>.
	 *
	 * @return the value of the property prefix
	 */
	public String portalPropertyPrefix() default "";

	/**
	 * Returns the service properties.
	 *
	 * <p>
	 * Each property string is specified as <code>"key=value"</code>. The type
	 * of the property value can be specified in the key as
	 * <code>"key:type=value"</code>. The type must be from {@link Type}. To
	 * specify a property with multiple values, use multiple key-value pairs.
	 * For example, <code>"foo=bar", "foo=baz"</code>.
	 * </p>
	 *
	 * @return the service properties
	 */
	public String[] property() default {};

	/**
	 * Returns the types under which the bean is published as a service.
	 *
	 * @return the service types
	 */
	public Class<?>[] service() default {};

	/**
	 * Converts OSGi bean properties from the {@link OSGiBeanProperties}
	 * annotation into a properties map. This is a helper class.
	 */
	public static class Convert {

		/**
		 * Returns a properties map representing the object's OSGi bean
		 * properties.
		 *
		 * @param  object the object that is possibly annotated with {@link
		 *         OSGiBeanProperties}
		 * @return a properties map representing the object's OSGi bean
		 *         properties. The map will be <code>null</code> if the object
		 *         is not annotated with {@link OSGiBeanProperties} or will be
		 *         empty if the object is annotated with {@link
		 *         OSGiBeanProperties} but has no properties.
		 */
		public static Map<String, Object> fromObject(Object object) {
			Class<? extends Object> clazz = object.getClass();

			OSGiBeanProperties osgiBeanProperties = clazz.getAnnotation(
				OSGiBeanProperties.class);

			if (osgiBeanProperties == null) {
				return null;
			}

			return toMap(osgiBeanProperties);
		}

		/**
		 * Returns a properties map representing the {@link OSGiBeanProperties}
		 * instance.
		 *
		 * @param  osgiBeanProperties the instance of {@link OSGiBeanProperties}
		 *         read for properties
		 * @return a properties map representing the {@link OSGiBeanProperties}
		 *         instance. The map will be empty if the {@link
		 *         OSGiBeanProperties} instance has no properties.
		 */
		public static Map<String, Object> toMap(
			OSGiBeanProperties osgiBeanProperties) {

			Map<String, Object> properties = new HashMap<>();

			for (String property : osgiBeanProperties.property()) {
				String[] parts = property.split(StringPool.EQUAL, 2);

				if (parts.length <= 1) {
					continue;
				}

				String key = parts[0];
				String className = String.class.getSimpleName();

				if (key.contains(StringPool.COLON)) {
					String[] keyParts = StringUtil.split(key, StringPool.COLON);

					key = keyParts[0];
					className = keyParts[1];
				}

				String value = parts[1];

				_put(key, value, className, properties);
			}

			String portalPropertyPrefix =
				osgiBeanProperties.portalPropertyPrefix();

			if (Validator.isNotNull(portalPropertyPrefix)) {
				Properties portalProperties = PropsUtil.getProperties(
					portalPropertyPrefix,
					osgiBeanProperties.portalPropertiesRemovePrefix());

				properties.putAll(PropertiesUtil.toMap(portalProperties));
			}

			return properties;
		}

		private static void _put(
			String key, String value, String className,
			Map<String, Object> properties) {

			Type type = Type._getType(className);

			Object previousValue = properties.get(key);

			properties.put(key, type._convert(value, previousValue));
		}

	}

	/**
	 * Obtains types under which the bean is published as a service.
	 */
	public static class Service {

		public static Set<String> interfaceNames(
			Object object, OSGiBeanProperties osgiBeanProperties,
			String[] ignoredInterfaceNames) {

			return _interfaceNames(
				object, osgiBeanProperties, ignoredInterfaceNames,
				Class::getName);
		}

		private static <T> Set<T> _interfaceNames(
			Object object, OSGiBeanProperties osgiBeanProperties,
			String[] ignoredInterfaceNames,
			Function<Class<?>, T> mappingFunction) {

			Set<T> interfaceNames = new LinkedHashSet<>();

			Class<?>[] serviceClasses = null;

			if (osgiBeanProperties != null) {
				serviceClasses = osgiBeanProperties.service();
			}

			if (ArrayUtil.isEmpty(serviceClasses)) {
				Queue<Class<?>> queue = new ArrayDeque<>();

				queue.add(object.getClass());

				while (!queue.isEmpty()) {
					Class<?> clazz = queue.remove();

					for (Class<?> interfaceClass : clazz.getInterfaces()) {
						_optionallyCollectInterface(
							interfaceClass, interfaceNames,
							ignoredInterfaceNames, mappingFunction);

						queue.add(interfaceClass);
					}

					clazz = clazz.getSuperclass();

					if (clazz != null) {
						if (clazz.isInterface()) {
							_optionallyCollectInterface(
								clazz, interfaceNames, ignoredInterfaceNames,
								mappingFunction);
						}

						queue.add(clazz);
					}
				}
			}
			else {
				for (Class<?> serviceClazz : serviceClasses) {
					serviceClazz.cast(object);

					_optionallyCollectInterface(
						serviceClazz, interfaceNames, ignoredInterfaceNames,
						mappingFunction);
				}
			}

			_optionallyCollectInterface(
				object.getClass(), interfaceNames, ignoredInterfaceNames,
				mappingFunction);

			return interfaceNames;
		}

		private static <T> void _optionallyCollectInterface(
			Class<?> clazz, Set<T> interfaceNames,
			String[] ignoredInterfaceNames,
			Function<Class<?>, T> mappingFunction) {

			String interfaceClassName = clazz.getName();

			for (String ignoredInterface : ignoredInterfaceNames) {
				if (!ignoredInterface.startsWith(StringPool.EXCLAMATION) &&
					(ignoredInterface.equals(interfaceClassName) ||
					 (ignoredInterface.endsWith(StringPool.STAR) &&
					  interfaceClassName.regionMatches(
						  0, ignoredInterface, 0,
						  ignoredInterface.length() - 1)))) {

					return;
				}
			}

			interfaceNames.add(mappingFunction.apply(clazz));
		}

	}

	public enum Type {

		BOOLEAN, BYTE, CHARACTER, DOUBLE, FLOAT, INTEGER, LONG, SHORT, STRING;

		private static Type _getType(String name) {
			name = StringUtil.toUpperCase(name);

			for (Type type : values()) {
				if (name.equals(type.name())) {
					return type;
				}
			}

			return Type.STRING;
		}

		private Object _convert(String value, Object previousValue) {
			if (previousValue == null) {
				return _getTypedValue(value);
			}

			Class<?> clazz = previousValue.getClass();

			if (!clazz.isArray()) {
				Object array = Array.newInstance(_getTypeClass(), 2);

				Array.set(array, 0, previousValue);
				Array.set(array, 1, _getTypedValue(value));

				return array;
			}

			Object array = Array.newInstance(
				_getTypeClass(), Array.getLength(previousValue) + 1);

			for (int i = 0; i < Array.getLength(previousValue); i++) {
				Array.set(array, i, Array.get(previousValue, i));
			}

			Array.set(
				array, Array.getLength(previousValue), _getTypedValue(value));

			return array;
		}

		private Class<?> _getTypeClass() {
			if (this == Type.BOOLEAN) {
				return Boolean.class;
			}
			else if (this == Type.BYTE) {
				return Byte.class;
			}
			else if (this == Type.CHARACTER) {
				return Character.class;
			}
			else if (this == Type.DOUBLE) {
				return Double.class;
			}
			else if (this == Type.FLOAT) {
				return Float.class;
			}
			else if (this == Type.INTEGER) {
				return Integer.class;
			}
			else if (this == Type.LONG) {
				return Long.class;
			}
			else if (this == Type.SHORT) {
				return Short.class;
			}
			else if (this == Type.STRING) {
				return String.class;
			}

			return null;
		}

		private Object _getTypedValue(String value) {
			if (this == Type.BOOLEAN) {
				return GetterUtil.getBoolean(value);
			}
			else if (this == Type.BYTE) {
				Byte byteValue = Byte.valueOf(value);

				return byteValue.byteValue();
			}
			else if (this == Type.CHARACTER) {
				return value.charAt(0);
			}
			else if (this == Type.DOUBLE) {
				return GetterUtil.getDouble(value);
			}
			else if (this == Type.FLOAT) {
				return GetterUtil.getFloat(value);
			}
			else if (this == Type.INTEGER) {
				return GetterUtil.getInteger(value);
			}
			else if (this == Type.LONG) {
				return GetterUtil.getLong(value);
			}
			else if (this == Type.SHORT) {
				return GetterUtil.getShort(value);
			}

			return value;
		}

	}

}