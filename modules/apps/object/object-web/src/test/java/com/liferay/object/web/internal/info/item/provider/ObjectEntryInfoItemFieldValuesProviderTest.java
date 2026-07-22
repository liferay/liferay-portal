/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider;

import com.liferay.asset.info.item.provider.AssetEntryInfoItemFieldSetProvider;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.field.reader.InfoItemFieldReaderFieldSetProvider;
import com.liferay.layout.page.template.info.item.provider.DisplayPageInfoItemFieldSetProvider;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.info.field.converter.ObjectFieldInfoFieldConverter;
import com.liferay.object.info.item.ObjectEntryInfoItemFields;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.web.internal.model.ProxyObjectEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.template.info.item.provider.TemplateInfoItemFieldSetProvider;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Yuri Monteiro
 */
public class ObjectEntryInfoItemFieldValuesProviderTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		DisplayPageInfoItemFieldSetProvider
			displayPageInfoItemFieldSetProvider = Mockito.mock(
				DisplayPageInfoItemFieldSetProvider.class);

		Mockito.when(
			displayPageInfoItemFieldSetProvider.getInfoFieldValues(
				Mockito.any(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(), Mockito.any())
		).thenReturn(
			Collections.emptyList()
		);

		InfoItemFieldReaderFieldSetProvider
			infoItemFieldReaderFieldSetProvider = Mockito.mock(
				InfoItemFieldReaderFieldSetProvider.class);

		Mockito.when(
			infoItemFieldReaderFieldSetProvider.getInfoFieldValues(
				Mockito.anyString(), Mockito.any())
		).thenReturn(
			Collections.emptyList()
		);

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.isDefaultStorageType()
		).thenReturn(
			false
		);

		ObjectFieldLocalService objectFieldLocalService = Mockito.mock(
			ObjectFieldLocalService.class);

		Mockito.when(
			objectFieldLocalService.getObjectFields(Mockito.anyLong())
		).thenReturn(
			Collections.emptyList()
		);

		TemplateInfoItemFieldSetProvider templateInfoItemFieldSetProvider =
			Mockito.mock(TemplateInfoItemFieldSetProvider.class);

		Mockito.when(
			templateInfoItemFieldSetProvider.getInfoFieldValues(
				Mockito.anyString(), Mockito.any())
		).thenReturn(
			Collections.emptyList()
		);

		_objectEntryInfoItemFieldValuesProvider =
			new ObjectEntryInfoItemFieldValuesProvider(
				Mockito.mock(AssetEntryInfoItemFieldSetProvider.class),
				displayPageInfoItemFieldSetProvider,
				Mockito.mock(DLAppLocalService.class),
				Mockito.mock(DLURLHelper.class),
				Mockito.mock(FriendlyURLEntryLocalService.class),
				infoItemFieldReaderFieldSetProvider,
				Mockito.mock(ListTypeEntryLocalService.class),
				Mockito.mock(ObjectActionLocalService.class), objectDefinition,
				Mockito.mock(ObjectDefinitionLocalService.class),
				Mockito.mock(ObjectFieldInfoFieldConverter.class),
				Mockito.mock(ObjectEntryLocalService.class),
				Mockito.mock(ObjectEntryManagerRegistry.class),
				Mockito.mock(ObjectEntryService.class), objectFieldLocalService,
				Mockito.mock(ObjectRelatedModelsProviderRegistry.class),
				Mockito.mock(ObjectRelationshipLocalService.class),
				Mockito.mock(ObjectScopeProviderRegistry.class),
				Mockito.mock(Portal.class), templateInfoItemFieldSetProvider,
				Mockito.mock(UserLocalService.class));
	}

	@Test
	public void testGetInfoItemFieldValues() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
			new com.liferay.object.rest.dto.v1_0.ObjectEntry();

		objectEntry.setExternalReferenceCode(externalReferenceCode);
		objectEntry.setProperties(Collections.emptyMap());

		ObjectEntry serviceBuilderObjectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			serviceBuilderObjectEntry.getObjectEntryId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		ServiceContext serviceContext = Mockito.mock(ServiceContext.class);

		Mockito.when(
			serviceContext.getThemeDisplay()
		).thenReturn(
			new ThemeDisplay()
		);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			InfoItemFieldValues infoItemFieldValues =
				_objectEntryInfoItemFieldValuesProvider.getInfoItemFieldValues(
					new ProxyObjectEntry(
						serviceBuilderObjectEntry, objectEntry));

			InfoFieldValue<Object> infoFieldValue =
				infoItemFieldValues.getInfoFieldValue(
					ObjectEntryInfoItemFields.externalReferenceCodeInfoField.
						getName());

			Assert.assertEquals(
				externalReferenceCode, infoFieldValue.getValue());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private ObjectEntryInfoItemFieldValuesProvider
		_objectEntryInfoItemFieldValuesProvider;

}