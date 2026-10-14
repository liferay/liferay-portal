/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.templateparser.TemplateNode;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.EnumerationModel;
import freemarker.ext.beans.MapModel;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.ext.beans.StringModel;
import freemarker.ext.dom.NodeModel;
import freemarker.ext.util.ModelFactory;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.lang.reflect.Field;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import org.w3c.dom.Node;

/**
 * @author Mika Koivisto
 */
public class LiferayObjectWrapper extends DefaultObjectWrapper {

	public LiferayObjectWrapper() {
		super(Configuration.VERSION_2_3_33);

		try {
			_cacheClassNamesField.set(
				_classIntrospectorField.get(this),
				new HashSet<Object>() {

					@Override
					public boolean add(Object object) {
						return false;
					}

					@Override
					public void clear() {
					}

					@Override
					public boolean contains(Object object) {
						return false;
					}

					@Override
					public boolean remove(Object object) {
						return false;
					}

				});
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public TemplateModel wrap(Object object) throws TemplateModelException {
		if (object == null) {
			return null;
		}

		if (object instanceof TemplateModel) {
			return (TemplateModel)object;
		}

		Class<?> clazz = object.getClass();

		String className = clazz.getName();

		if (className.startsWith("com.liferay.")) {
			if (object instanceof TemplateNode) {
				return new LiferayTemplateModel((TemplateNode)object, this);
			}

			if (object instanceof Collection) {
				return new SimpleSequence((Collection)object, this);
			}

			if (object instanceof Map) {
				return new MapModel((Map)object, this);
			}

			return _STRING_MODEL_FACTORY.create(object, this);
		}

		AtomicInteger modelFactoryIndex = _modelFactoryIndexClassValue.get(
			clazz);

		int index = modelFactoryIndex.get();

		if (index < 0) {
			return super.wrap(object);
		}

		ModelFactory modelFactory = _modelFactories.get(index);

		return modelFactory.create(object, this);
	}

	@Override
	protected TemplateModel handleUnknownType(Object object) {
		ModelFactory modelFactory = null;

		if (object instanceof Enumeration) {
			modelFactory = _ENUMERATION_MODEL_FACTORY;
		}
		else if (object instanceof Node) {
			modelFactory = _NODE_MODEL_FACTORY;
		}
		else if (object instanceof ResourceBundle) {
			modelFactory = _RESOURCE_BUNDLE_MODEL_FACTORY;
		}
		else {
			modelFactory = _STRING_MODEL_FACTORY;
		}

		AtomicInteger modelFactoryIndex = _modelFactoryIndexClassValue.get(
			object.getClass());

		modelFactoryIndex.set(_modelFactories.indexOf(modelFactory));

		return modelFactory.create(object, this);
	}

	private static final ModelFactory _ENUMERATION_MODEL_FACTORY =
		new ModelFactory() {

			@Override
			public TemplateModel create(
				Object object, ObjectWrapper objectWrapper) {

				return new EnumerationModel(
					(Enumeration)object, (BeansWrapper)objectWrapper);
			}

		};

	private static final ModelFactory _NODE_MODEL_FACTORY = new ModelFactory() {

		@Override
		public TemplateModel create(
			Object object, ObjectWrapper objectWrapper) {

			return NodeModel.wrap((Node)object);
		}

	};

	private static final ModelFactory _RESOURCE_BUNDLE_MODEL_FACTORY =
		new ModelFactory() {

			@Override
			public TemplateModel create(
				Object object, ObjectWrapper objectWrapper) {

				return new ResourceBundleModel(
					(ResourceBundle)object, (BeansWrapper)objectWrapper);
			}

		};

	private static final ModelFactory _STRING_MODEL_FACTORY =
		new ModelFactory() {

			@Override
			public TemplateModel create(
				Object object, ObjectWrapper objectWrapper) {

				return new StringModel(object, (BeansWrapper)objectWrapper);
			}

		};

	private static final Field _cacheClassNamesField;
	private static final Field _classIntrospectorField;
	private static final List<ModelFactory> _modelFactories = Arrays.asList(
		_ENUMERATION_MODEL_FACTORY, _NODE_MODEL_FACTORY,
		_RESOURCE_BUNDLE_MODEL_FACTORY, _STRING_MODEL_FACTORY);

	private static final ClassValue<AtomicInteger>
		_modelFactoryIndexClassValue = new ClassValue<AtomicInteger>() {

			@Override
			protected AtomicInteger computeValue(Class<?> clazz) {
				return new AtomicInteger(-1);
			}

		};

	static {
		try {
			ClassLoader classLoader =
				DefaultObjectWrapper.class.getClassLoader();

			_cacheClassNamesField = ReflectionUtil.getDeclaredField(
				classLoader.loadClass("freemarker.ext.beans.ClassIntrospector"),
				"cacheClassNames");

			_classIntrospectorField = ReflectionUtil.getDeclaredField(
				BeansWrapper.class, "classIntrospector");
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

}