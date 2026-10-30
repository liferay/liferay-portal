/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.headless.admin.site.dto.v1_0.Site;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alberto Javier Moreno Lage
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag(value = "LPD-57655"), @FeatureFlag(value = "LPD-85946")
	}
)
@RunWith(Arquillian.class)
public class SiteCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		_externalReferenceCodes.clear();

		super.setUp();
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		_groupLocalService.addGroup(
			externalReferenceCode, TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0,
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			null, GroupConstants.TYPE_SITE_OPEN, null, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			"/" + RandomTestUtil.randomString(), true, false, true,
			ServiceContextTestUtil.getServiceContext(
				group.getCompanyId(), groupId, TestPropsValues.getUserId()));

		_externalReferenceCodes.add(externalReferenceCode);
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_groupLocalService.deleteGroup(
			_getGroup(externalReferenceCode, groupId));
	}

	@Override
	protected void exportImport(
			Date endDate, Map<String, String[]> parameterMap, Date startDate)
		throws Exception {

		super.exportImport(
			endDate,
			HashMapBuilder.putAll(
				parameterMap
			).put(
				PortletDataHandlerKeys.GROUP_EXTERNAL_REFERENCE_CODES,
				_externalReferenceCodes.toArray(new String[0])
			).build(),
			startDate);
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _getGroup(externalReferenceCode, groupId);

		return group.getCreatorUserId();
	}

	@Override
	protected String getEntityClassName() {
		return Site.class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _getGroup(externalReferenceCode, groupId);

		return group.getGroupId();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		Group site = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, group.getCompanyId());

		if (site == null) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean supportsComments() {
		return false;
	}

	@Override
	protected boolean supportsEmptyEntries() {
		return false;
	}

	@Override
	protected boolean supportsPermissions() {
		return true;
	}

	@Override
	protected void updateCreatorUserId(
			String externalReferenceCode, long groupId, long userId)
		throws Exception {

		Group group = _getGroup(externalReferenceCode, groupId);

		group.setCreatorUserId(userId);

		_groupLocalService.updateGroup(group);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _getGroup(externalReferenceCode, groupId);

		group.setNameMap(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build());

		_groupLocalService.updateGroup(group);
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		Group group = _getGroup(externalReferenceCode, groupId);

		group.setModifiedDate(modifiedDate);

		_groupLocalService.updateGroup(group);
	}

	private Group _getGroup(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _groupLocalService.getGroupByExternalReferenceCode(
			externalReferenceCode, group.getCompanyId());
	}

	private final List<String> _externalReferenceCodes = new ArrayList<>();

	@Inject
	private GroupLocalService _groupLocalService;

}