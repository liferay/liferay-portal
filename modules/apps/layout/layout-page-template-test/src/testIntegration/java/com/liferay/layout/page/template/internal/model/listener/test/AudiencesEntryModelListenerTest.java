/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class AudiencesEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@Test
	@TestInfo("LPD-98435")
	public void testOnBeforeRemove() throws Exception {
		AudiencesEntry audiencesEntry =
			_audiencesEntryLocalService.addAudiencesEntry(
				null, _serviceContext.getUserId(), StringPool.BLANK,
				RandomTestUtil.randomString());
		String audienceEntryERC = RandomTestUtil.randomString();
		String externalReferenceCode = RandomTestUtil.randomString();

		_layoutPageTemplateStructureRelElementVariationLocalService.
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, TestPropsValues.getUserId(),
				_group.getGroupId(), true, RandomTestUtil.randomString(),
				Collections.emptyMap(), Collections.emptyMap(),
				RandomTestUtil.randomString(), _layout.getPlid(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				new String[] {
					audiencesEntry.getExternalReferenceCode(), audienceEntryERC
				},
				_serviceContext);

		List<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels =
				_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
					getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
						externalReferenceCode);

		Assert.assertEquals(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.
				toString(),
			2,
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.
				size());

		_audiencesEntryLocalService.deleteAudiencesEntry(
			audiencesEntry.getAudiencesEntryId());

		layoutPageTemplateStructureRelElementVariationAudienceEntryRels =
			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
				getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
					externalReferenceCode);

		Assert.assertEquals(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.
				toString(),
			1,
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.
				size());

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				layoutPageTemplateStructureRelElementVariationAudienceEntryRels.
					get(0);

		Assert.assertEquals(
			audienceEntryERC,
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getAudienceEntryERC());
	}

	@Inject
	private AudiencesEntryLocalService _audiencesEntryLocalService;

	private Group _group;
	private Layout _layout;

	@Inject
	private
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;

	@Inject
	private LayoutPageTemplateStructureRelElementVariationLocalService
		_layoutPageTemplateStructureRelElementVariationLocalService;

	private ServiceContext _serviceContext;

}