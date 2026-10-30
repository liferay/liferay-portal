/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.language.override.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.headless.admin.language.override.dto.v1_0.LanguageOverride;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alberto Javier Moreno Lage
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-49852"))
@RunWith(Arquillian.class)
public class LanguageOverrideCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		_ploEntryLocalService.addOrUpdatePLOEntry(
			externalReferenceCode, group.getCompanyId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			RandomTestUtil.randomString());
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_ploEntryLocalService.deletePLOEntry(
			_getPLOEntry(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		PLOEntry ploEntry = _getPLOEntry(externalReferenceCode, groupId);

		return ploEntry.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return LanguageOverride.class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		PLOEntry ploEntry = _getPLOEntry(externalReferenceCode, groupId);

		return ploEntry.getPloEntryId();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		PLOEntry ploEntry =
			_ploEntryLocalService.fetchPLOEntryByExternalReferenceCode(
				externalReferenceCode, group.getCompanyId());

		if (ploEntry == null) {
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
		return false;
	}

	@Override
	protected void updateCreatorUserId(
			String externalReferenceCode, long groupId, long userId)
		throws Exception {

		PLOEntry ploEntry = _getPLOEntry(externalReferenceCode, groupId);

		ploEntry.setUserId(userId);

		_ploEntryLocalService.updatePLOEntry(ploEntry);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		PLOEntry ploEntry = _getPLOEntry(externalReferenceCode, groupId);

		ploEntry.setValue(RandomTestUtil.randomString());

		_ploEntryLocalService.updatePLOEntry(ploEntry);
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		PLOEntry ploEntry = _getPLOEntry(externalReferenceCode, groupId);

		ploEntry.setModifiedDate(modifiedDate);

		_ploEntryLocalService.updatePLOEntry(ploEntry);
	}

	private PLOEntry _getPLOEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _ploEntryLocalService.getPLOEntryByExternalReferenceCode(
			externalReferenceCode, group.getCompanyId());
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private PLOEntryLocalService _ploEntryLocalService;

}