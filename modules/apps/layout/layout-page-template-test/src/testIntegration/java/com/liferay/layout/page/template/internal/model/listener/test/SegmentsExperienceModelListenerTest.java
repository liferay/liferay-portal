/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.internal.portlet.constants.LayoutPageTemplatePortletKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Arques
 */
@RunWith(Arquillian.class)
public class SegmentsExperienceModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo({"LPD-75847", "LPD-98435"})
	public void testOnBeforeRemove() throws Exception {
		_testOnBeforeRemoveWithFragmentEntryLinks();
		_testOnBeforeRemoveWithLayoutPageTemplateStructureRelElementVariation();
		_testOnBeforeRemoveWithLayoutPageTemplateStructureRels();
		_testOnBeforeRemoveWithPortletPreferences();
	}

	private SegmentsExperience _getSegmentsExperience(String portletId)
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				"portletId", portletId
			).toString(),
			StringPool.BLANK, StringPool.BLANK, null, null, StringPool.BLANK,
			StringPool.BLANK, _layout, StringPool.BLANK,
			segmentsExperience.getSegmentsExperienceId(),
			FragmentConstants.TYPE_PORTLET);

		return segmentsExperience;
	}

	private void _testOnBeforeRemoveWithFragmentEntryLinks() throws Exception {
		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			"{}", _layout, segmentsExperience.getSegmentsExperienceId());

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					_group.getGroupId(),
					segmentsExperience.getSegmentsExperienceId(),
					_layout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			segmentsExperience);

		fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					_group.getGroupId(),
					segmentsExperience.getSegmentsExperienceId(),
					_layout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 0, fragmentEntryLinks.size());
	}

	private void _testOnBeforeRemoveWithLayoutPageTemplateStructureRelElementVariation()
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		String externalReferenceCode = RandomTestUtil.randomString();

		_layoutPageTemplateStructureRelElementVariationLocalService.
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, TestPropsValues.getUserId(),
				_group.getGroupId(), true, RandomTestUtil.randomString(),
				Collections.emptyMap(), Collections.emptyMap(),
				RandomTestUtil.randomString(), _layout.getPlid(),
				segmentsExperience.getExternalReferenceCode(),
				RandomTestUtil.randomString(),
				new String[] {RandomTestUtil.randomString()}, _serviceContext);

		List<LayoutPageTemplateStructureRelElementVariation>
			layoutPageTemplateStructureRelElementVariations =
				_layoutPageTemplateStructureRelElementVariationLocalService.
					getLayoutPageTemplateStructureRelElementVariations(
						_layout.getPlid(),
						segmentsExperience.getExternalReferenceCode());

		Assert.assertEquals(
			layoutPageTemplateStructureRelElementVariations.toString(), 1,
			layoutPageTemplateStructureRelElementVariations.size());

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			segmentsExperience);

		layoutPageTemplateStructureRelElementVariations =
			_layoutPageTemplateStructureRelElementVariationLocalService.
				getLayoutPageTemplateStructureRelElementVariations(
					_layout.getPlid(),
					segmentsExperience.getExternalReferenceCode());

		Assert.assertEquals(
			layoutPageTemplateStructureRelElementVariations.toString(), 0,
			layoutPageTemplateStructureRelElementVariations.size());

		List<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels =
				_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
					getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
						externalReferenceCode);

		Assert.assertEquals(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.
				toString(),
			0,
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels.
				size());
	}

	private void _testOnBeforeRemoveWithLayoutPageTemplateStructureRels()
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		int count = 5;

		for (int i = 0; i < count; i++) {
			_layoutPageTemplateStructureRelLocalService.
				addLayoutPageTemplateStructureRel(
					_serviceContext.getUserId(),
					_serviceContext.getScopeGroupId(),
					RandomTestUtil.nextLong(),
					segmentsExperience.getSegmentsExperienceId(),
					StringPool.BLANK, _serviceContext);
		}

		List<LayoutPageTemplateStructureRel> layoutPageTemplateStructureRels =
			_layoutPageTemplateStructureRelLocalService.
				getLayoutPageTemplateStructureRelsBySegmentsExperienceId(
					segmentsExperience.getSegmentsExperienceId());

		Assert.assertEquals(
			layoutPageTemplateStructureRels.toString(), count,
			layoutPageTemplateStructureRels.size());

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			segmentsExperience);

		layoutPageTemplateStructureRels =
			_layoutPageTemplateStructureRelLocalService.
				getLayoutPageTemplateStructureRelsBySegmentsExperienceId(
					segmentsExperience.getSegmentsExperienceId());

		Assert.assertEquals(
			layoutPageTemplateStructureRels.toString(), 0,
			layoutPageTemplateStructureRels.size());
	}

	private void _testOnBeforeRemoveWithPortletPreferences() throws Exception {
		String portletId = PortletIdCodec.encode(
			LayoutPageTemplatePortletKeys.
				LAYOUT_PAGE_TEMPLATE_NONINSTANCEABLE_TEST_PORTLET,
			StringPool.BLANK);

		SegmentsExperience segmentsExperience1 = _getSegmentsExperience(
			portletId);
		SegmentsExperience segmentsExperience2 = _getSegmentsExperience(
			portletId);

		_portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(), portletId,
			_portletLocalService.getPortletById(
				TestPropsValues.getCompanyId(), portletId),
			null);

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			segmentsExperience1);

		Assert.assertNotNull(
			_portletPreferencesLocalService.fetchPreferences(
				TestPropsValues.getCompanyId(),
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				portletId));

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			segmentsExperience2);

		Assert.assertNull(
			_portletPreferencesLocalService.fetchPreferences(
				TestPropsValues.getCompanyId(),
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				portletId));
	}

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;

	@Inject
	private LayoutPageTemplateStructureRelElementVariationLocalService
		_layoutPageTemplateStructureRelElementVariationLocalService;

	@Inject
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}