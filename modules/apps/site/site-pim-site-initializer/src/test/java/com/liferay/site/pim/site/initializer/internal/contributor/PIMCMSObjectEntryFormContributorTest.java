/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.contributor;

import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

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