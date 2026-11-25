/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.contributor;

import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;
import com.liferay.site.pim.site.initializer.internal.fragment.renderer.ProductStickerSectionFragmentRenderer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class PIMCMSObjectEntryFormContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_objectDefinition.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_objectDefinition.getObjectFolderExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_fragmentEntryLinkListenerRegistry.getFragmentEntryLinkListeners()
		).thenReturn(
			Collections.emptyList()
		);

		ReflectionTestUtil.setFieldValue(
			_pimCMSObjectEntryFormContributor,
			"_fragmentEntryLinkListenerRegistry",
			_fragmentEntryLinkListenerRegistry);
		ReflectionTestUtil.setFieldValue(
			_pimCMSObjectEntryFormContributor, "_fragmentEntryLinkLocalService",
			_fragmentEntryLinkLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimCMSObjectEntryFormContributor, "_fragmentRendererRegistry",
			_fragmentRendererRegistry);
		ReflectionTestUtil.setFieldValue(
			_pimCMSObjectEntryFormContributor, "_jsonFactory",
			new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_pimCMSObjectEntryFormContributor, "_language", _language);
		ReflectionTestUtil.setFieldValue(
			_pimCMSObjectEntryFormContributor,
			"_layoutPageTemplateStructureLocalService",
			_layoutPageTemplateStructureLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimCMSObjectEntryFormContributor,
			"_segmentsExperienceLocalService", _segmentsExperienceLocalService);
	}

	@Test
	public void testContribute() throws Exception {
		_testContribute();
		_testContributeWithTitle();
	}

	private void _testContribute() throws Exception {
		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				false
			);

			_pimCMSObjectEntryFormContributor.contribute(
				_layout, _objectDefinition, _serviceContext);

			Mockito.verify(
				_segmentsExperienceLocalService, Mockito.never()
			).fetchDefaultSegmentsExperienceId(
				Mockito.anyLong()
			);

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				true
			);

			_pimCMSObjectEntryFormContributor.contribute(
				_layout, _objectDefinition, _serviceContext);

			Mockito.verify(
				_segmentsExperienceLocalService, Mockito.never()
			).fetchDefaultSegmentsExperienceId(
				Mockito.anyLong()
			);

			Mockito.when(
				_objectDefinition.getObjectFolderExternalReferenceCode()
			).thenReturn(
				PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES
			);

			_pimCMSObjectEntryFormContributor.contribute(
				_layout, _objectDefinition, _serviceContext);

			Mockito.verify(
				_segmentsExperienceLocalService, Mockito.times(1)
			).fetchDefaultSegmentsExperienceId(
				Mockito.anyLong()
			);
		}
	}

	private void _testContributeWithTitle() throws Exception {
		Mockito.when(
			_objectDefinition.getObjectFolderExternalReferenceCode()
		).thenReturn(
			PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES
		);

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		LanguageUtil languageUtil = new LanguageUtil();

		Mockito.when(
			_language.getLocale(Mockito.anyString())
		).thenReturn(
			LocaleUtil.US
		);

		languageUtil.setLanguage(_language);

		LayoutStructure layoutStructure = new LayoutStructure();

		layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem formLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0);

		long titleFragmentEntryLinkId = RandomTestUtil.randomLong();

		layoutStructure.addFragmentStyledLayoutStructureItem(
			titleFragmentEntryLinkId, formLayoutStructureItem.getItemId(), 0);

		LayoutPageTemplateStructure layoutPageTemplateStructure = Mockito.mock(
			LayoutPageTemplateStructure.class);

		Mockito.when(
			layoutPageTemplateStructure.getData(Mockito.anyLong())
		).thenReturn(
			layoutStructure.toString()
		);

		Mockito.when(
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			layoutPageTemplateStructure
		);

		FragmentEntryLink titleFragmentEntryLink = Mockito.mock(
			FragmentEntryLink.class);

		Mockito.when(
			titleFragmentEntryLink.getEditableValues()
		).thenReturn(
			JSONUtil.toString(
				JSONUtil.put(
					"com.liferay.fragment.entry.processor.freemarker." +
						"FreeMarkerFragmentEntryProcessor",
					JSONUtil.put("inputFieldId", "ObjectField_title")))
		);

		Mockito.when(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				titleFragmentEntryLinkId)
		).thenReturn(
			titleFragmentEntryLink
		);

		Mockito.when(
			_fragmentRendererRegistry.getFragmentRenderer(Mockito.anyString())
		).thenReturn(
			Mockito.mock(FragmentRenderer.class)
		);

		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getUniqueElementId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		portalUtil.setPortal(portal);

		long productStickerFragmentEntryLinkId = RandomTestUtil.randomLong();

		FragmentEntryLink productStickerFragmentEntryLink = Mockito.mock(
			FragmentEntryLink.class);

		Mockito.when(
			productStickerFragmentEntryLink.getFragmentEntryLinkId()
		).thenReturn(
			productStickerFragmentEntryLinkId
		);

		Mockito.when(
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				Mockito.any(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt(),
				Mockito.any(), Mockito.anyInt(), Mockito.any())
		).thenReturn(
			productStickerFragmentEntryLink
		);

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96666"))
			).thenReturn(
				true
			);

			_pimCMSObjectEntryFormContributor.contribute(
				_layout, _objectDefinition, _serviceContext);
		}

		Mockito.verify(
			_fragmentEntryLinkLocalService
		).addFragmentEntryLink(
			Mockito.any(), Mockito.anyLong(), Mockito.anyLong(), Mockito.any(),
			Mockito.any(), Mockito.any(), Mockito.anyLong(), Mockito.anyLong(),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
			Mockito.any(), Mockito.any(), Mockito.anyInt(),
			Mockito.eq(ProductStickerSectionFragmentRenderer.class.getName()),
			Mockito.anyInt(), Mockito.any()
		);

		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			_layoutPageTemplateStructureLocalService, Mockito.atLeastOnce()
		).updateLayoutPageTemplateStructureData(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
			Mockito.anyLong(), argumentCaptor.capture()
		);

		List<String> allValues = argumentCaptor.getAllValues();

		layoutStructure = LayoutStructure.of(allValues.get(0));

		LayoutStructureItem titleLayoutStructureItem =
			layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
				titleFragmentEntryLinkId);

		LayoutStructureItem parentLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				titleLayoutStructureItem.getParentItemId());

		Assert.assertTrue(
			parentLayoutStructureItem instanceof
				ContainerStyledLayoutStructureItem);

		ContainerStyledLayoutStructureItem containerStyledLayoutStructureItem =
			(ContainerStyledLayoutStructureItem)parentLayoutStructureItem;

		Assert.assertEquals(
			"flex-row", containerStyledLayoutStructureItem.getContentDisplay());

		LayoutStructureItem productStickerLayoutStructureItem =
			layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
				productStickerFragmentEntryLinkId);

		LayoutStructureItem productStickerContainerLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				productStickerLayoutStructureItem.getParentItemId());

		Assert.assertTrue(
			productStickerContainerLayoutStructureItem instanceof
				ContainerStyledLayoutStructureItem);

		JSONObject itemConfigJSONObject =
			productStickerContainerLayoutStructureItem.
				getItemConfigJSONObject();

		JSONObject stylesJSONObject = itemConfigJSONObject.getJSONObject(
			"styles");

		Assert.assertEquals("80px", stylesJSONObject.getString("height"));
		Assert.assertEquals("80px", stylesJSONObject.getString("minWidth"));
		Assert.assertEquals("80px", stylesJSONObject.getString("width"));

		Assert.assertEquals(
			Arrays.asList(
				productStickerContainerLayoutStructureItem.getItemId(),
				titleLayoutStructureItem.getItemId()),
			containerStyledLayoutStructureItem.getChildrenItemIds());

		formLayoutStructureItem = layoutStructure.getLayoutStructureItem(
			formLayoutStructureItem.getItemId());

		List<String> childrenItemIds =
			formLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			containerStyledLayoutStructureItem.getItemId(),
			childrenItemIds.get(0));
	}

	private final FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry = Mockito.mock(
			FragmentEntryLinkListenerRegistry.class);
	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService =
		Mockito.mock(FragmentEntryLinkLocalService.class);
	private final FragmentRendererRegistry _fragmentRendererRegistry =
		Mockito.mock(FragmentRendererRegistry.class);
	private final Language _language = Mockito.mock(Language.class);
	private final Layout _layout = Mockito.mock(Layout.class);
	private final LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService = Mockito.mock(
			LayoutPageTemplateStructureLocalService.class);
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final PIMCMSObjectEntryFormContributor
		_pimCMSObjectEntryFormContributor =
			new PIMCMSObjectEntryFormContributor();
	private final SegmentsExperienceLocalService
		_segmentsExperienceLocalService = Mockito.mock(
			SegmentsExperienceLocalService.class);
	private final ServiceContext _serviceContext = Mockito.mock(
		ServiceContext.class);

}