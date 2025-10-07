/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.info.field.converter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.info.field.InfoField;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.builder.IntegerObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.info.field.converter.ObjectFieldInfoFieldConverter;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectFieldInfoFieldConverterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			ListUtil.fromArray(
				new IntegerObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"integerObjectField"
				).build()));

		_objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"textObjectField"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).readOnly(
				ObjectFieldConstants.READ_ONLY_CONDITIONAL
			).readOnlyConditionExpression(
				"not(isEmpty(integerObjectField))"
			).userId(
				TestPropsValues.getUserId()
			).build());
	}

	@Test
	public void testGetInfoField() throws Exception {
		ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter =
			new ObjectFieldInfoFieldConverter(
				_ddmExpressionFactory, null, null, null, null,
				_objectFieldSettingLocalService, null, null, null, null, null,
				null, null);

		InfoField<?> infoField = objectFieldInfoFieldConverter.getInfoField(
			true, ObjectField.class.getSimpleName(), _objectField);

		Assert.assertFalse(infoField.isReadOnly());

		try {
			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			HttpServletRequest httpServletRequest =
				new MockHttpServletRequest();

			LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
				_layoutDisplayPageProviderRegistry.
					getLayoutDisplayPageProviderByClassName(
						_objectDefinition.getClassName());

			ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
				0, _objectDefinition.getObjectDefinitionId(),
				Collections.singletonMap(
					"integerObjectField", RandomTestUtil.randomInt()));

			httpServletRequest.setAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
				layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					new InfoItemReference(
						_objectDefinition.getClassName(),
						objectEntry.getObjectEntryId())));

			serviceContext.setRequest(httpServletRequest);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			infoField = objectFieldInfoFieldConverter.getInfoField(
				true, ObjectField.class.getSimpleName(), _objectField);

			Assert.assertTrue(infoField.isReadOnly());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Inject
	private DDMExpressionFactory _ddmExpressionFactory;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	private ObjectField _objectField;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

}