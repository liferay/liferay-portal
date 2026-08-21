/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.fields;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;
import com.liferay.portal.vulcan.jaxrs.context.ContextDataInjector;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Alejandro Tardín
 * @author Ivica Cardic
 */
public class NestedFieldsSetterUtil {

	public static void setNestedFields(
			Object item, ContextDataInjector contextDataInjector)
		throws Exception {

		setNestedFields(
			item,
			(fieldName, nestedFieldsContext, resource) ->
				() -> contextDataInjector);
	}

	public static void setNestedFields(
			Object item,
			NestedFieldsSetterCustomizer nestedFieldsSetterCustomizer)
		throws Exception {

		NestedFieldsContext nestedFieldsContext =
			NestedFieldsContextThreadLocal.getNestedFieldsContext();

		if ((nestedFieldsContext == null) ||
			(nestedFieldsContext.getCurrentDepth() >=
				nestedFieldsContext.getDepth()) ||
			ListUtil.isEmpty(nestedFieldsContext.getNestedFields())) {

			return;
		}

		_setFieldValues(
			item, nestedFieldsContext.getNestedFields(), nestedFieldsContext,
			nestedFieldsSetterCustomizer);
	}

	public interface NestedFieldsSetterCustomizer {

		public NestedFieldsSetterSafeCloseable
				getNestedFieldsSetterSafeCloseable(
					String fieldName, NestedFieldsContext nestedFieldsContext,
					Object resource)
			throws Exception;

	}

	public interface NestedFieldsSetterSafeCloseable extends SafeCloseable {

		@Override
		public default void close() {
		}

		public ContextDataInjector getContextDataInjector() throws Exception;

	}

	private static Object _adaptToFieldType(Class<?> fieldType, Object value) {
		if (value instanceof Page) {
			Page<?> page = (Page)value;

			value = page.getItems();
		}

		if (fieldType.isArray() && (value instanceof Collection)) {
			Collection<Object> collection = (Collection)value;

			value = Array.newInstance(
				fieldType.getComponentType(), collection.size());

			int i = 0;

			for (Object object : collection) {
				Array.set(value, i++, object);
			}
		}

		return value;
	}

	private static <A extends Annotation> A _getAnnotation(
		Class<A> annotationClass, Parameter parameter,
		Parameter[] parentParameters, int i) {

		A annotation = parameter.getAnnotation(annotationClass);

		if ((annotation == null) && (parentParameters != null)) {
			return parentParameters[i].getAnnotation(annotationClass);
		}

		return annotation;
	}

	private static Field _getField(Class<?> entityClass, String fieldName) {
		Class<?> clazz = entityClass;

		while ((clazz != null) && (clazz != Object.class)) {
			for (Field field : clazz.getDeclaredFields()) {
				if (Objects.equals(field.getName(), fieldName) ||
					Objects.equals(field.getName(), "_" + fieldName)) {

					return field;
				}
			}

			clazz = clazz.getSuperclass();
		}

		return null;
	}

	private static List<Object> _getItems(Object entity) {
		List<Object> items = new ArrayList<>();

		if (entity instanceof Collection) {
			items.addAll((Collection)entity);
		}
		else if (entity instanceof Page) {
			Page<?> page = (Page)entity;

			items.addAll(page.getItems());
		}
		else if (_isArray(entity)) {
			Collections.addAll(items, (Object[])entity);
		}
		else {
			items.add(entity);
		}

		return items;
	}

	private static NestedFieldGetter _getNestedFieldGetter(
		String fieldName, Class<?> itemClass,
		NestedFieldsContext nestedFieldsContext) {

		List<Class<?>> parentClasses = ListUtil.fromArray(Void.class);

		Class<?> clazz = itemClass;

		while ((clazz != null) && (clazz != Object.class)) {
			parentClasses.add(clazz);

			clazz = clazz.getSuperclass();
		}

		for (Class<?> parentClass : parentClasses) {
			FactoryKey factoryKey = new FactoryKey(
				fieldName, parentClass,
				nestedFieldsContext.getResourceVersion());

			for (Map<FactoryKey, NestedFieldGetter> nestedFieldGetters :
					_serviceTrackerList) {

				NestedFieldGetter nestedFieldGetter = nestedFieldGetters.get(
					factoryKey);

				if (nestedFieldGetter != null) {
					return nestedFieldGetter;
				}
			}
		}

		return null;
	}

	private static boolean _isArray(Object object) {
		Class<?> objectClass = object.getClass();

		return objectClass.isArray();
	}

	private static void _setFieldValues(
			Object entity, List<String> nestedFields,
			NestedFieldsContext nestedFieldsContext,
			NestedFieldsSetterCustomizer nestedFieldsSetterCustomizer)
		throws Exception {

		List<Object> items = _getItems(entity);

		for (String nestedField : nestedFields) {
			String childNestedField = null;

			int index = nestedField.indexOf(".");

			if (index != -1) {
				childNestedField = nestedField.substring(index + 1);

				nestedField = nestedField.substring(0, index);
			}

			for (Object item : items) {
				Class<?> itemClass = item.getClass();

				Field field = _getField(itemClass, nestedField);

				if (field == null) {
					continue;
				}

				field.setAccessible(true);

				NestedFieldGetter nestedFieldGetter = _getNestedFieldGetter(
					nestedField, itemClass, nestedFieldsContext);

				if (nestedFieldGetter == null) {
					continue;
				}

				Object value = _adaptToFieldType(
					field.getType(),
					nestedFieldGetter.getValue(
						nestedField, item, nestedFieldsContext,
						nestedFieldsSetterCustomizer));

				field.set(item, value);

				if (childNestedField != null) {
					_setFieldValues(
						value, Collections.singletonList(childNestedField),
						nestedFieldsContext, nestedFieldsSetterCustomizer);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NestedFieldsSetterUtil.class);

	private static final ServiceTrackerList<Map<FactoryKey, NestedFieldGetter>>
		_serviceTrackerList;

	private static class FactoryKey {

		@Override
		public boolean equals(Object object) {
			FactoryKey factoryKey = (FactoryKey)object;

			if (Objects.equals(factoryKey._nestedFieldName, _nestedFieldName) &&
				Objects.equals(factoryKey._parentClass, _parentClass) &&
				Objects.equals(factoryKey._resourceVersion, _resourceVersion)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hashCode = HashUtil.hash(0, _nestedFieldName);

			hashCode = HashUtil.hash(hashCode, _parentClass);
			hashCode = HashUtil.hash(hashCode, _resourceVersion);

			return hashCode;
		}

		private FactoryKey(
			String nestedFieldName, Class<?> parentClass,
			String resourceVersion) {

			_nestedFieldName = nestedFieldName;
			_parentClass = parentClass;
			_resourceVersion = resourceVersion;
		}

		private final String _nestedFieldName;
		private final Class<?> _parentClass;
		private final String _resourceVersion;

	}

	private static class NestedFieldServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<Object, Map<FactoryKey, NestedFieldGetter>> {

		@Override
		public Map<FactoryKey, NestedFieldGetter> addingService(
			ServiceReference<Object> serviceReference) {

			Map<FactoryKey, NestedFieldGetter> nestedFieldGetters =
				new HashMap<>();

			Object resource = _bundleContext.getService(serviceReference);

			Class<?> resourceClass = resource.getClass();

			for (Method resourceMethod : resourceClass.getDeclaredMethods()) {
				NestedField nestedField = resourceMethod.getAnnotation(
					NestedField.class);

				if (nestedField == null) {
					continue;
				}

				Class<?> parentClass = nestedField.parentClass();

				FactoryKey factoryKey = new FactoryKey(
					nestedField.value(), parentClass,
					_getAPIVersion(resourceClass.getSuperclass()));

				ServiceObjects<Object> serviceObjects =
					_bundleContext.getServiceObjects(serviceReference);

				nestedFieldGetters.put(
					factoryKey,
					(fieldName, item, nestedFieldsContext,
					 nestedFieldsSetterCustomizer) -> _getNestedFieldValue(
						fieldName, item, nestedFieldsContext,
						nestedFieldsSetterCustomizer, resourceMethod,
						_getResourceMethodArgNameTypeEntries(
							resourceClass, resourceMethod),
						serviceObjects));
			}

			if (nestedFieldGetters.isEmpty()) {
				_bundleContext.ungetService(serviceReference);

				return null;
			}

			return nestedFieldGetters;
		}

		@Override
		public void modifiedService(
			ServiceReference<Object> serviceReference,
			Map<FactoryKey, NestedFieldGetter> nestedFieldGetters) {
		}

		@Override
		public void removedService(
			ServiceReference<Object> serviceReference,
			Map<FactoryKey, NestedFieldGetter> nestedFieldGetters) {

			_bundleContext.ungetService(serviceReference);
		}

		private NestedFieldServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		private Object _convert(String value, Class<?> type) {
			if (value == null) {
				return null;
			}

			return _objectMapper.convertValue(value, type);
		}

		private String _getAPIVersion(Class<?> resourceBaseClass) {
			Annotation[] annotations = resourceBaseClass.getAnnotations();

			for (Annotation annotation : annotations) {
				if (annotation instanceof Path) {
					Path path = (Path)annotation;

					String resourceVersion = path.value();

					return resourceVersion.substring(1);
				}
			}

			return null;
		}

		private Object[] _getMethodArgs(
				ContextDataInjector contextDataInjector, String fieldName,
				Object item, NestedFieldsContext nestedFieldsContext,
				Method resourceMethod,
				Map.Entry<String, Class<?>>[] resourceMethodArgNameTypeEntries)
			throws Exception {

			Object[] args = new Object[resourceMethod.getParameterCount()];

			for (int i = 0; i < resourceMethod.getParameterCount(); i++) {
				if (resourceMethodArgNameTypeEntries[i] == null) {
					continue;
				}

				args[i] = _getMethodArgValueFromItem(
					item, resourceMethodArgNameTypeEntries[i]);

				if (args[i] == null) {
					args[i] = _getMethodArgValueFromRequest(
						contextDataInjector, fieldName,
						resourceMethodArgNameTypeEntries[i],
						nestedFieldsContext);
				}
			}

			return args;
		}

		private Object _getMethodArgValueFromItem(
				Object item,
				Map.Entry<String, Class<?>> resourceMethodArgNameTypeEntry)
			throws Exception {

			String argName = resourceMethodArgNameTypeEntry.getKey();

			String methodName =
				"get" + StringUtil.upperCaseFirstLetter(argName);

			List<Class<?>> itemClasses = new ArrayList<>();

			Class<?> itemClass = item.getClass();

			itemClasses.add(itemClass);

			itemClasses.add(itemClass.getSuperclass());

			for (Class<?> curItemClass : itemClasses) {
				for (Method method : curItemClass.getMethods()) {
					if (StringUtil.equals(method.getName(), methodName) &&
						Objects.equals(
							method.getReturnType(),
							resourceMethodArgNameTypeEntry.getValue()) &&
						(method.getParameterCount() == 0)) {

						return method.invoke(item);
					}
				}
			}

			return null;
		}

		private Object _getMethodArgValueFromRequest(
			ContextDataInjector contextDataInjector, String fieldName,
			Map.Entry<String, Class<?>> resourceMethodArgNameTypeEntry,
			NestedFieldsContext nestedFieldsContext) {

			Object argValue = null;

			Class<?> resourceMethodArgType =
				resourceMethodArgNameTypeEntry.getValue();

			Object context = contextDataInjector.getValue(
				resourceMethodArgType);

			if (context != null) {
				argValue = context;
			}
			else {
				MultivaluedMap<String, String> pathParameters =
					nestedFieldsContext.getPathParameters();

				argValue = _convert(
					pathParameters.getFirst(
						resourceMethodArgNameTypeEntry.getKey()),
					resourceMethodArgType);

				if (argValue == null) {
					MultivaluedMap<String, String> queryParameters =
						nestedFieldsContext.getQueryParameters();

					argValue = _convert(
						queryParameters.getFirst(
							fieldName + StringPool.PERIOD +
								resourceMethodArgNameTypeEntry.getKey()),
						resourceMethodArgType);
				}
			}

			return argValue;
		}

		private Object _getNestedFieldValue(
				String fieldName, Object item,
				NestedFieldsContext nestedFieldsContext,
				NestedFieldsSetterCustomizer nestedFieldsSetterCustomizer,
				Method resourceMethod,
				Map.Entry<String, Class<?>>[] resourceMethodArgNameTypeEntries,
				ServiceObjects<Object> serviceObjects)
			throws Exception {

			Object resource = serviceObjects.getService();

			try (NestedFieldsSetterSafeCloseable
					nestedFieldsSetterSafeCloseable =
						nestedFieldsSetterCustomizer.
							getNestedFieldsSetterSafeCloseable(
								fieldName, nestedFieldsContext, resource)) {

				ContextDataInjector contextDataInjector =
					nestedFieldsSetterSafeCloseable.getContextDataInjector();

				contextDataInjector.inject(resource);

				Object[] args = _getMethodArgs(
					contextDataInjector, fieldName, item, nestedFieldsContext,
					resourceMethod, resourceMethodArgNameTypeEntries);

				return resourceMethod.invoke(resource, args);
			}
			finally {
				serviceObjects.ungetService(resource);
			}
		}

		private Map.Entry<String, Class<?>>[]
			_getResourceMethodArgNameTypeEntries(
				Class<?> resourceClass, Method resourceMethod) {

			Parameter[] resourceMethodParameters =
				resourceMethod.getParameters();

			Map.Entry<String, Class<?>>[] resourceMethodArgNameTypeEntries =
				new Map.Entry[resourceMethodParameters.length];

			Parameter[] parentParameters = null;

			try {
				Class<?> parentResourceClass = resourceClass.getSuperclass();

				Method parentResourceMethod = parentResourceClass.getMethod(
					resourceMethod.getName(),
					resourceMethod.getParameterTypes());

				parentParameters = parentResourceMethod.getParameters();
			}
			catch (NoSuchMethodException noSuchMethodException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchMethodException);
				}
			}

			for (int i = 0; i < resourceMethodParameters.length; i++) {
				Parameter parameter = resourceMethodParameters[i];

				NestedFieldId nestedFieldId = _getAnnotation(
					NestedFieldId.class, parameter, parentParameters, i);

				Class<?> parameterType = parameter.getType();

				if (nestedFieldId == null) {
					Context context = _getAnnotation(
						Context.class, parameter, parentParameters, i);

					if (context != null) {
						resourceMethodArgNameTypeEntries[i] =
							new AbstractMap.SimpleImmutableEntry<>(
								parameter.getName(), parameterType);
					}

					PathParam pathParam = _getAnnotation(
						PathParam.class, parameter, parentParameters, i);

					if (pathParam != null) {
						resourceMethodArgNameTypeEntries[i] =
							new AbstractMap.SimpleImmutableEntry<>(
								pathParam.value(), parameterType);
					}

					QueryParam queryParam = _getAnnotation(
						QueryParam.class, parameter, parentParameters, i);

					if (queryParam != null) {
						resourceMethodArgNameTypeEntries[i] =
							new AbstractMap.SimpleImmutableEntry<>(
								queryParam.value(), parameterType);
					}
				}
				else {
					resourceMethodArgNameTypeEntries[i] =
						new AbstractMap.SimpleImmutableEntry<>(
							nestedFieldId.value(), parameterType);
				}
			}

			return resourceMethodArgNameTypeEntries;
		}

		private static final ObjectMapper _objectMapper = new ObjectMapper();

		private final BundleContext _bundleContext;

	}

	static {
		Bundle bundle = FrameworkUtil.getBundle(NestedFieldsSetterUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, null, "(nested.field.support=true)",
			new NestedFieldServiceTrackerCustomizer(bundleContext));
	}

	private interface NestedFieldGetter {

		public Object getValue(
				String fieldName, Object item,
				NestedFieldsContext nestedFieldsContext,
				NestedFieldsSetterCustomizer nestedFieldsSetterCustomizer)
			throws Exception;

	}

}