/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.templateparser.TemplateNode;
import com.liferay.portal.kernel.test.ReflectionTestUtil;

import freemarker.ext.beans.MapModel;
import freemarker.ext.beans.StringModel;
import freemarker.ext.util.ModelFactory;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;

/**
 * @author Tina Tian
 */
public abstract class BaseObjectWrapperTestCase {

	protected <T, R> void assertTemplateModel(
			String expectResult, UnsafeFunction<T, R, Exception> unsafeFunction,
			T templateModel)
		throws Exception {

		R result = unsafeFunction.apply(templateModel);

		Assert.assertEquals(expectResult, result.toString());
	}

	protected void testWrap(ObjectWrapper objectWrapper) throws Exception {

		// 1. Wrap null

		Assert.assertNull(objectWrapper.wrap(null));

		// 2. Wrap TemplateModel

		TemplateModel dummyTemplateModel = new TemplateModel() {
		};

		Assert.assertSame(
			dummyTemplateModel, objectWrapper.wrap(dummyTemplateModel));

		// 3. Wrap TemplateNode

		assertTemplateModel(
			"testName",
			liferayTemplateModel -> liferayTemplateModel.get("name"),
			LiferayTemplateModel.class.cast(
				objectWrapper.wrap(
					new TemplateNode(null, "testName", "", "", null))));

		// 4. Wrap Liferay collection

		assertTemplateModel(
			"testElement", simpleSequence -> simpleSequence.get(0),
			SimpleSequence.class.cast(
				objectWrapper.wrap(new TestLiferayCollection("testElement"))));

		// 5. Wrap Liferay map

		assertTemplateModel(
			"testValue", mapModel -> mapModel.get("testKey"),
			MapModel.class.cast(
				objectWrapper.wrap(
					new TestLiferayMap("testKey", "testValue"))));

		// 6. Wrap Liferay object

		assertTemplateModel(
			"TestLiferayObject", stringModel -> stringModel.getAsString(),
			StringModel.class.cast(
				objectWrapper.wrap(
					new TestLiferayObject("TestLiferayObject"))));

		// 7. Wrap non-Liferay object when module factory is available

		ClassValue<AtomicInteger> modelFactoryIndexClassValue =
			ReflectionTestUtil.getFieldValue(
				LiferayObjectWrapper.class, "_modelFactoryIndexClassValue");

		AtomicInteger modelFactoryIndex = modelFactoryIndexClassValue.get(
			String.class);

		List<ModelFactory> modelFactories = ReflectionTestUtil.getFieldValue(
			LiferayObjectWrapper.class, "_modelFactories");

		ModelFactory modelFactory = modelFactories.set(
			0, (object, wrapper) -> dummyTemplateModel);

		modelFactoryIndex.set(0);

		Assert.assertSame(
			dummyTemplateModel, objectWrapper.wrap(StringPool.BLANK));

		modelFactories.set(0, modelFactory);

		modelFactoryIndexClassValue.remove(String.class);

		// 8. Test

		assertTemplateModel(
			"TestLiferayObject", stringModel -> stringModel.getAsString(),
			StringModel.class.cast(
				objectWrapper.wrap(
					new TestLiferayObject("TestLiferayObject"))));
	}

	protected class TestLiferayObject {

		@Override
		public String toString() {
			return _name;
		}

		private TestLiferayObject(String name) {
			_name = name;
		}

		private final String _name;

	}

	private class TestLiferayCollection extends ArrayList<String> {

		private TestLiferayCollection(String element) {
			add(element);
		}

		private static final long serialVersionUID = 1L;

	}

	private class TestLiferayMap extends HashMap<String, String> {

		private TestLiferayMap(String key, String value) {
			put(key, value);
		}

		private static final long serialVersionUID = 1L;

	}

}