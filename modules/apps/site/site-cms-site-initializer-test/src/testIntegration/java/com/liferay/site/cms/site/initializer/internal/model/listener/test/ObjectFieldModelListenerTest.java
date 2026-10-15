/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-82226"))
@RunWith(Arquillian.class)
public class ObjectFieldModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectDefinition = _publishObjectDefinition();

		IndexWriterHelper indexWriterHelper = ReflectionTestUtil.getFieldValue(
			_modelListener, "_indexWriterHelper");

		_autoCloseable = ReflectionTestUtil.setFieldValueWithAutoCloseable(
			_modelListener, "_indexWriterHelper",
			ProxyUtil.newProxyInstance(
				IndexWriterHelper.class.getClassLoader(),
				new Class<?>[] {IndexWriterHelper.class},
				(proxy, method, arguments) -> {
					if (Objects.equals(method.getName(), "reindex")) {
						_classNames.add((String)arguments[3]);

						return null;
					}

					return method.invoke(indexWriterHelper, arguments);
				}));
	}

	@After
	public void tearDown() throws Exception {
		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		_autoCloseable.close();
	}

	@Test
	public void testOnAfterCreate() throws Exception {
		_addObjectField(_objectDefinition, false);

		_assertClassNames();

		_addObjectField(_objectDefinition, true);

		_assertClassNames(_objectDefinition.getClassName());
	}

	@Test
	public void testOnAfterRemove() throws Exception {
		ObjectField objectField = _addObjectField(_objectDefinition, true);

		_classNames.clear();

		_objectFieldLocalService.deleteObjectField(
			_objectFieldLocalService.getObjectField(
				_objectDefinition.getObjectDefinitionId(), "text"));

		_assertClassNames();

		_addObjectField(_objectDefinition, true);

		_classNames.clear();

		_objectFieldLocalService.deleteObjectField(objectField);

		_assertClassNames(_objectDefinition.getClassName());

		ObjectDefinition objectDefinition = _publishObjectDefinition();

		_addObjectField(objectDefinition, true);

		_classNames.clear();

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		_assertClassNames();
	}

	@Test
	public void testOnAfterUpdate() throws Exception {
		ObjectField objectField = _addObjectField(_objectDefinition, true);

		_classNames.clear();

		objectField.setLabelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()));

		objectField = _objectFieldLocalService.updateObjectField(objectField);

		_assertClassNames();

		objectField.setRequired(false);

		_objectFieldLocalService.updateObjectField(objectField);

		_assertClassNames(_objectDefinition.getClassName());
	}

	private ObjectField _addObjectField(
			ObjectDefinition objectDefinition, boolean required)
		throws Exception {

		return _objectFieldLocalService.addCustomObjectField(
			null, TestPropsValues.getUserId(), 0,
			objectDefinition.getObjectDefinitionId(),
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, null, false, false, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			false, "a" + RandomTestUtil.randomString(), null, null, required,
			false, Collections.emptyList());
	}

	private void _assertClassNames(String... classNames) {
		Assert.assertEquals(Arrays.asList(classNames), _classNames);

		_classNames.clear();
	}

	private ObjectDefinition _publishObjectDefinition() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId());

		return ObjectDefinitionTestUtil.publishObjectDefinition(
			true, false, true, ObjectDefinitionTestUtil.getRandomName(),
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"text"
				).build()),
			objectFolder.getObjectFolderId(),
			ObjectDefinitionConstants.SCOPE_DEPOT, TestPropsValues.getUserId());
	}

	private AutoCloseable _autoCloseable;
	private final List<String> _classNames = new ArrayList<>();

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.model.listener.ObjectFieldModelListener"
	)
	private ModelListener<ObjectField> _modelListener;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

}