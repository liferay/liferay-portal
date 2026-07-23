/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class GroupModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_cmsGroup = CMSTestUtil.getOrAddGroup(GroupModelListenerTest.class);
	}

	@Test
	@TestInfo("LPD-92888")
	public void testOnAfterUpdate() throws Exception {
		_testOnAfterUpdateWithExternalReferenceCode();
		_testOnAfterUpdateWithTrashEnabled();
	}

	private DepotEntry _addDepotEntry() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private void _setTrashEnabled(Group group, String value) throws Exception {
		UnicodeProperties unicodeProperties = group.getTypeSettingsProperties();

		if (value == null) {
			unicodeProperties.remove("trashEnabled");
		}
		else {
			unicodeProperties.setProperty("trashEnabled", value);
		}

		_groupLocalService.updateGroup(
			group.getGroupId(), unicodeProperties.toString());
	}

	private void _testOnAfterUpdateWithExternalReferenceCode()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry();

		Group depotGroup = depotEntry.getGroup();

		String originalExternalReferenceCode =
			depotGroup.getExternalReferenceCode();

		Assert.assertNotNull(
			CMSDefaultPermissionUtil.fetchObjectEntry(
				depotGroup.getCompanyId(), depotGroup.getCreatorUserId(),
				originalExternalReferenceCode, depotEntry.getModelClassName(),
				_filterFactory));

		String externalReferenceCode = RandomTestUtil.randomString();

		depotGroup.setExternalReferenceCode(externalReferenceCode);

		depotGroup = _groupLocalService.updateGroup(depotGroup);

		Assert.assertNull(
			CMSDefaultPermissionUtil.fetchObjectEntry(
				depotGroup.getCompanyId(), depotGroup.getCreatorUserId(),
				originalExternalReferenceCode, depotEntry.getModelClassName(),
				_filterFactory));

		Assert.assertNotNull(
			CMSDefaultPermissionUtil.fetchObjectEntry(
				depotGroup.getCompanyId(), depotGroup.getCreatorUserId(),
				externalReferenceCode, depotEntry.getModelClassName(),
				_filterFactory));
	}

	private void _testOnAfterUpdateWithTrashEnabled() throws Exception {
		Layout layout = _layoutLocalService.getLayoutByFriendlyURL(
			_cmsGroup.getGroupId(), false, "/recycle-bin");

		Assert.assertFalse(layout.isHidden());

		DepotEntry depotEntry = _addDepotEntry();

		Group depotGroup = depotEntry.getGroup();

		_setTrashEnabled(depotGroup, Boolean.FALSE.toString());

		Assert.assertFalse(
			GetterUtil.getBoolean(
				depotGroup.getTypeSettingsProperty("trashEnabled")));

		_setTrashEnabled(depotGroup, Boolean.TRUE.toString());

		Assert.assertTrue(
			GetterUtil.getBoolean(
				depotGroup.getTypeSettingsProperty("trashEnabled")));
	}

	private Group _cmsGroup;

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

}