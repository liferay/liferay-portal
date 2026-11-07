/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.upgrade.v6_1_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;
import com.liferay.sites.kernel.util.Sites;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Chaparro
 * @author Adolfo Pérez
 */
@FeatureFlag("LPD-82107")
@RunWith(Arquillian.class)
@Sync
public class LayoutUpgradeProcessTest extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_connection = DataAccess.getConnection();

		_layoutSetPrototype = LayoutTestUtil.addLayoutSetPrototype(
			RandomTestUtil.randomString());

		_layoutSetPrototypeGroup = _layoutSetPrototype.getGroup();

		_grandparentLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true);

		_parentLayout = _addChildLayout(_grandparentLayout);

		_childLayout = _addChildLayout(_parentLayout);

		_contentLayout = LayoutTestUtil.addTypeContentLayout(
			_layoutSetPrototypeGroup, true, false);

		Layout draftLayout = _contentLayout.fetchDraftLayout();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					draftLayout.getGroupId(), draftLayout.getPlid());

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				draftLayout.getGroupId(), draftLayout.getPlid());

		_layoutPageTemplateStructureRelLocalService.
			addLayoutPageTemplateStructureRel(
				TestPropsValues.getUserId(), draftLayout.getGroupId(),
				layoutPageTemplateStructure.getLayoutPageTemplateStructureId(),
				segmentsExperience.getSegmentsExperienceId(),
				layoutPageTemplateStructure.getDefaultSegmentsExperienceData(),
				ServiceContextTestUtil.getServiceContext(
					draftLayout.getGroupId(), TestPropsValues.getUserId()));

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			"{}", draftLayout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, _contentLayout);

		_group = GroupTestUtil.addGroup();

		_sites.updateLayoutSetPrototypesLinks(
			_group, _layoutSetPrototype.getLayoutSetPrototypeId(),
			_layoutSetPrototype.getLayoutSetPrototypeId(), true, true);

		_sites.mergeLayoutSetPrototypeLayouts(
			_layoutSetPrototype, TestPropsValues.getUserId());
	}

	@After
	public void tearDown() throws Exception {
		DataAccess.cleanUp(_connection);

		GroupTestUtil.deleteGroup(_group);

		_layoutSetPrototypeLocalService.deleteLayoutSetPrototype(
			_layoutSetPrototype);
	}

	@Test
	@TestInfo("LPD-108617")
	public void testUpgrade() throws Exception {
		_resetExternalReferenceCodes(_group);

		runUpgrade();

		for (boolean privateLayout : new boolean[] {false, true}) {
			_assertExternalReferenceCodes(_group, _childLayout, privateLayout);
			_assertExternalReferenceCodes(
				_group, _contentLayout, privateLayout);
			_assertExternalReferenceCodes(
				_group, _grandparentLayout, privateLayout);
			_assertExternalReferenceCodes(_group, _parentLayout, privateLayout);
		}

		int privateLayoutsCount = _layoutLocalService.getLayoutsCount(
			_group, true);
		int publicLayoutsCount = _layoutLocalService.getLayoutsCount(
			_group, false);

		String languageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());
		String name = RandomTestUtil.randomString();

		_layoutLocalService.updateName(
			_childLayout.getPlid(), name, languageId);

		_sites.mergeLayoutSetPrototypeLayouts(
			_layoutSetPrototype, TestPropsValues.getUserId());

		Assert.assertEquals(
			privateLayoutsCount,
			_layoutLocalService.getLayoutsCount(_group, true));
		Assert.assertEquals(
			publicLayoutsCount,
			_layoutLocalService.getLayoutsCount(_group, false));

		Layout privateLinkedChildLayout = _getLinkedLayout(
			_group, _childLayout, true);

		Assert.assertEquals(name, privateLinkedChildLayout.getName(languageId));

		Layout publicLinkedChildLayout = _getLinkedLayout(
			_group, _childLayout, false);

		Assert.assertEquals(name, publicLinkedChildLayout.getName(languageId));
	}

	@Test
	@TestInfo("LPD-108617")
	public void testUpgradeWithGrandparentLayout() throws Exception {
		Layout linkedChildLayout = _getLinkedLayout(
			_group, _childLayout, false);

		Layout linkedGrandparentLayout = _getLinkedLayout(
			_group, _grandparentLayout, false);
		Layout linkedParentLayout = _getLinkedLayout(
			_group, _parentLayout, false);

		_updateLayoutSetPrototypeLayoutERC(
			linkedGrandparentLayout, _parentLayout.getExternalReferenceCode());
		_updateLayoutSetPrototypeLayoutERC(
			linkedParentLayout, _childLayout.getExternalReferenceCode());

		runUpgrade();

		_assertLayoutSetPrototypeLayoutERC(_childLayout, linkedChildLayout);
		_assertLayoutSetPrototypeLayoutERC(
			_grandparentLayout, linkedGrandparentLayout);
		_assertLayoutSetPrototypeLayoutERC(_parentLayout, linkedParentLayout);
	}

	@Test
	@TestInfo("LPD-108617")
	public void testUpgradeWithParentLayout() throws Exception {
		Layout linkedChildLayout = _getLinkedLayout(
			_group, _childLayout, false);

		Layout linkedParentLayout = _getLinkedLayout(
			_group, _parentLayout, false);

		_updateLayoutSetPrototypeLayoutERC(
			linkedParentLayout, _childLayout.getExternalReferenceCode());

		runUpgrade();

		_assertLayoutSetPrototypeLayoutERC(_childLayout, linkedChildLayout);
		_assertLayoutSetPrototypeLayoutERC(_parentLayout, linkedParentLayout);
	}

	@Test
	@TestInfo("LPD-108617")
	public void testUpgradeWithPrivateLayoutSetPrototypeLink()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		try {
			_sites.updateLayoutSetPrototypesLinks(
				group, 0, _layoutSetPrototype.getLayoutSetPrototypeId(), false,
				true);

			_sites.mergeLayoutSetPrototypeLayouts(
				_layoutSetPrototype, TestPropsValues.getUserId());

			_resetExternalReferenceCodes(group);

			Layout[] layoutSetPrototypeLayouts = {
				_childLayout, _contentLayout, _grandparentLayout, _parentLayout
			};

			for (Layout layout : layoutSetPrototypeLayouts) {
				Layout linkedLayout = _getLinkedLayout(group, layout, true);

				_copyLayoutSetPrototypeLayout(linkedLayout, layout);

				Layout draftLayout = layout.fetchDraftLayout();

				if (draftLayout == null) {
					continue;
				}

				_copyLayoutSetPrototypeLayout(
					linkedLayout.fetchDraftLayout(), draftLayout);
			}

			runUpgrade();

			for (Layout layout : layoutSetPrototypeLayouts) {
				_assertExternalReferenceCodes(group, layout, true);

				layout = _layoutLocalService.getLayout(layout.getPlid());

				Assert.assertTrue(
					Validator.isNull(layout.getLayoutSetPrototypeLayoutERC()));
			}
		}
		finally {
			GroupTestUtil.deleteGroup(group);
		}
	}

	@Test
	@TestInfo("LPD-108617")
	public void testUpgradeWithPrivateSegmentsExperienceUUIDs()
		throws Exception {

		_resetExternalReferenceCodes(_group);

		runUpgrade();

		_assertSegmentsExperienceExternalReferenceCodes(
			_contentLayout, _getLinkedLayout(_group, _contentLayout, true),
			true);
	}

	@Test
	@TestInfo("LPD-108617")
	public void testUpgradeWithoutLayoutSetPrototypeLinks() throws Exception {
		Layout linkedParentLayout = _getLinkedLayout(
			_group, _parentLayout, false);

		_updateLayoutSetPrototypeLayoutERC(
			linkedParentLayout, _childLayout.getExternalReferenceCode());

		_executeUpdate(
			"update LayoutSet set layoutSetPrototypeUuid = null where " +
				"groupId = ?",
			_group.getGroupId());

		runUpgrade();

		linkedParentLayout = _layoutLocalService.getLayout(
			linkedParentLayout.getPlid());

		Assert.assertEquals(
			_childLayout.getExternalReferenceCode(),
			linkedParentLayout.getLayoutSetPrototypeLayoutERC());
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		_resetExternalReferenceCodes(_group);

		return _getLinkedLayout(_group, _contentLayout, false);
	}

	@Override
	protected void deleteCTModel(long primaryKey) throws Exception {
		Layout layout = _layoutLocalService.fetchLayout(primaryKey);

		if (layout != null) {
			_layoutLocalService.deleteLayout(layout);
		}
	}

	@Override
	protected CTService<?> getCTService() {
		return _layoutLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(6, 1, 1));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) {
		return _layoutLocalService.updateLayout((Layout)ctModel);
	}

	private Layout _addChildLayout(Layout parentLayout) throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true);

		return _layoutLocalService.updateParentLayoutId(
			layout.getPlid(), parentLayout.getPlid());
	}

	private void _assertExternalReferenceCodes(
			Group group, Layout layout, boolean privateLayout)
		throws Exception {

		Layout linkedLayout = _getLinkedLayout(group, layout, privateLayout);

		_assertExternalReferenceCodes(layout, linkedLayout, privateLayout);

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout != null) {
			_assertExternalReferenceCodes(
				draftLayout, linkedLayout.fetchDraftLayout(), privateLayout);
		}
	}

	private void _assertExternalReferenceCodes(
		Layout layout, Layout linkedLayout, boolean privateLayout) {

		_assertLayoutExternalReferenceCode(layout, linkedLayout, privateLayout);

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				layout.getGroupId(), layout.getPlid());
		List<FragmentEntryLink> linkedFragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				linkedLayout.getGroupId(), linkedLayout.getPlid());

		String suffix = _getSuffix(privateLayout);

		Assert.assertEquals(
			_getExternalReferenceCodes(fragmentEntryLinks, suffix),
			_getExternalReferenceCodes(linkedFragmentEntryLinks, null));
		Assert.assertEquals(
			_getOriginalFragmentEntryLinkERCs(fragmentEntryLinks, suffix),
			_getOriginalFragmentEntryLinkERCs(linkedFragmentEntryLinks, null));

		if (privateLayout) {
			Set<String> uuids = _getUUIDs(fragmentEntryLinks);

			uuids.retainAll(_getUUIDs(linkedFragmentEntryLinks));

			Assert.assertEquals(uuids.toString(), 0, uuids.size());
		}

		_assertSegmentsExperienceExternalReferenceCodes(
			layout, linkedLayout, privateLayout);
	}

	private void _assertLayoutExternalReferenceCode(
		Layout layout, Layout linkedLayout, boolean privateLayout) {

		Assert.assertEquals(
			layout.getExternalReferenceCode() + _getSuffix(privateLayout),
			linkedLayout.getExternalReferenceCode());
		Assert.assertEquals(
			layout.getExternalReferenceCode(),
			linkedLayout.getLayoutSetPrototypeLayoutERC());
	}

	private void _assertLayoutSetPrototypeLayoutERC(
			Layout layoutSetPrototypeLayout, Layout linkedLayout)
		throws Exception {

		linkedLayout = _layoutLocalService.getLayout(linkedLayout.getPlid());

		Assert.assertEquals(
			layoutSetPrototypeLayout.getExternalReferenceCode(),
			linkedLayout.getLayoutSetPrototypeLayoutERC());
	}

	private void _assertSegmentsExperienceExternalReferenceCodes(
		Layout layout, Layout linkedLayout, boolean privateLayout) {

		String suffix = _getSuffix(privateLayout);

		for (SegmentsExperience segmentsExperience :
				_segmentsExperienceLocalService.getSegmentsExperiences(
					layout.getGroupId(), layout.getPlid())) {

			SegmentsExperience linkedSegmentsExperience =
				_segmentsExperienceLocalService.fetchSegmentsExperience(
					linkedLayout.getGroupId(),
					segmentsExperience.getSegmentsExperienceKey(),
					linkedLayout.getPlid());

			Assert.assertEquals(
				segmentsExperience.getExternalReferenceCode() + suffix,
				linkedSegmentsExperience.getExternalReferenceCode());

			Assert.assertFalse(
				privateLayout &&
				Objects.equals(
					segmentsExperience.getUuid(),
					linkedSegmentsExperience.getUuid()));
		}
	}

	private void _copyLayoutSetPrototypeLayout(
			Layout layout, Layout layoutSetPrototypeLayout)
		throws Exception {

		_executeUpdate(
			"update Layout set externalReferenceCode = ?, " +
				"layoutSetPrototypeLayoutERC = null where plid = ?",
			layoutSetPrototypeLayout.getExternalReferenceCode(),
			layout.getPlid());

		for (SegmentsExperience segmentsExperience :
				_segmentsExperienceLocalService.getSegmentsExperiences(
					layoutSetPrototypeLayout.getGroupId(),
					layoutSetPrototypeLayout.getPlid())) {

			_executeUpdate(
				"update SegmentsExperience set uuid_ = ? where plid = ? and " +
					"segmentsExperienceKey = ?",
				segmentsExperience.getUuid(), layout.getPlid(),
				segmentsExperience.getSegmentsExperienceKey());
		}

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				layoutSetPrototypeLayout.getGroupId(),
				layoutSetPrototypeLayout.getPlid());

		if (ListUtil.isEmpty(fragmentEntryLinks)) {
			return;
		}

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		FragmentEntryLink fragmentEntryLink = fragmentEntryLinks.get(0);

		_executeUpdate(
			"update FragmentEntryLink set uuid_ = ? where plid = ?",
			fragmentEntryLink.getUuid(), layout.getPlid());
	}

	private void _executeUpdate(String sql, Object... parameters)
		throws Exception {

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				sql)) {

			for (int i = 0; i < parameters.length; i++) {
				preparedStatement.setObject(i + 1, parameters[i]);
			}

			preparedStatement.executeUpdate();
		}

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	private Set<String> _getExternalReferenceCodes(
		List<FragmentEntryLink> fragmentEntryLinks, String suffix) {

		Set<String> externalReferenceCodes = new HashSet<>();

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			String externalReferenceCode =
				fragmentEntryLink.getExternalReferenceCode();

			if (suffix != null) {
				externalReferenceCode += suffix;
			}

			externalReferenceCodes.add(externalReferenceCode);
		}

		return externalReferenceCodes;
	}

	private Layout _getLinkedLayout(
			Group group, Layout layout, boolean privateLayout)
		throws Exception {

		for (Layout linkedLayout :
				_layoutLocalService.getLayouts(
					group.getGroupId(), privateLayout)) {

			if (Objects.equals(
					layout.getExternalReferenceCode(),
					linkedLayout.getLayoutSetPrototypeLayoutERC())) {

				return linkedLayout;
			}
		}

		throw new AssertionError(
			"No layout is linked to " + layout.getExternalReferenceCode());
	}

	private Set<String> _getOriginalFragmentEntryLinkERCs(
		List<FragmentEntryLink> fragmentEntryLinks, String suffix) {

		Set<String> originalFragmentEntryLinkERCs = new HashSet<>();

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			String originalFragmentEntryLinkERC =
				fragmentEntryLink.getOriginalFragmentEntryLinkERC();

			if (Validator.isNotNull(originalFragmentEntryLinkERC) &&
				(suffix != null)) {

				originalFragmentEntryLinkERC += suffix;
			}

			originalFragmentEntryLinkERCs.add(originalFragmentEntryLinkERC);
		}

		return originalFragmentEntryLinkERCs;
	}

	private String _getSuffix(boolean privateLayout) {
		if (privateLayout) {
			return LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_PRIVATE;
		}

		return StringPool.BLANK;
	}

	private Set<String> _getUUIDs(List<FragmentEntryLink> fragmentEntryLinks) {
		Set<String> uuids = new HashSet<>();

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			uuids.add(fragmentEntryLink.getUuid());
		}

		return uuids;
	}

	private void _resetExternalReferenceCodes(Group group) throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL(
			StringBundler.concat(
				"update FragmentEntryLink set externalReferenceCode = ",
				"CAST_TEXT(fragmentEntryLinkId) where groupId = ",
				group.getGroupId()));
		db.runSQL(
			StringBundler.concat(
				"update FragmentEntryLink set originalFragmentEntryLinkERC = ",
				"CAST_TEXT(fragmentEntryLinkId) where groupId = ",
				group.getGroupId(),
				" and originalFragmentEntryLinkERC is not null"));
		db.runSQL(
			StringBundler.concat(
				"update Layout set externalReferenceCode = CAST_TEXT(plid) ",
				"where groupId = ", group.getGroupId()));
		db.runSQL(
			StringBundler.concat(
				"update SegmentsExperience set externalReferenceCode = ",
				"CAST_TEXT(segmentsExperienceId) where groupId = ",
				group.getGroupId()));

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	private void _updateLayoutSetPrototypeLayoutERC(
		Layout layout, String layoutSetPrototypeLayoutERC) {

		layout.setLayoutSetPrototypeLayoutERC(layoutSetPrototypeLayoutERC);

		_layoutLocalService.updateLayout(layout);
	}

	private Layout _childLayout;
	private Connection _connection;
	private Layout _contentLayout;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	private Layout _grandparentLayout;
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	private LayoutSetPrototype _layoutSetPrototype;
	private Group _layoutSetPrototypeGroup;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	private Layout _parentLayout;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private Sites _sites;

	@Inject(
		filter = "component.name=com.liferay.layout.internal.upgrade.registry.LayoutServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}