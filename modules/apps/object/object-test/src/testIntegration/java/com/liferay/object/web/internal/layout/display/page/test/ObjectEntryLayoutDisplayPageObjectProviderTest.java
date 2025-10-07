/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.layout.display.page.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectEntryLayoutDisplayPageObjectProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetTitle() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, Collections.emptyList());

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				StringUtil.randomId()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		objectDefinition =
			_objectDefinitionLocalService.updateTitleObjectFieldId(
				objectDefinition.getObjectDefinitionId(),
				objectField.getObjectFieldId());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(),
			ServiceContextTestUtil.getServiceContext(), Collections.emptyMap());

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByURLSeparator(
					FriendlyURLResolverConstants.URL_SEPARATOR_OBJECT_ENTRY);

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				0, String.valueOf(objectEntry.getObjectEntryId()));

		Assert.assertEquals(
			objectDefinition.getLabel(LocaleUtil.getDefault()),
			layoutDisplayPageObjectProvider.getTitle(LocaleUtil.getDefault()));

		Map<String, String> localizedValues = HashMapBuilder.put(
			"en_US", RandomTestUtil.randomString()
		).put(
			"pt_BR", RandomTestUtil.randomString()
		).build();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		String[] assetTagNames = {"tag1", "tag2"};

		serviceContext.setAssetTagNames(assetTagNames);

		objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(), serviceContext,
			HashMapBuilder.<String, Serializable>put(
				objectField.getI18nObjectFieldName(),
				(Serializable)localizedValues
			).build());

		layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				0, String.valueOf(objectEntry.getObjectEntryId()));

		Assert.assertEquals(
			localizedValues.get("en_US"),
			layoutDisplayPageObjectProvider.getTitle(LocaleUtil.SPAIN));
		Assert.assertEquals(
			localizedValues.get("en_US"),
			layoutDisplayPageObjectProvider.getTitle(LocaleUtil.US));
		Assert.assertEquals(
			localizedValues.get("pt_BR"),
			layoutDisplayPageObjectProvider.getTitle(LocaleUtil.BRAZIL));

		Assert.assertEquals(
			StringUtil.merge(assetTagNames),
			layoutDisplayPageObjectProvider.getKeywords(LocaleUtil.US));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Inject
	protected LayoutDisplayPageProviderRegistry
		layoutDisplayPageProviderRegistry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}