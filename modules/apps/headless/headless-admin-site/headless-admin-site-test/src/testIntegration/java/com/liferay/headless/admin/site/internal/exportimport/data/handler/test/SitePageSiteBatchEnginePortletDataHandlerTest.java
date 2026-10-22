/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseSiteBatchEnginePortletDataHandlerTestCase;
import com.liferay.headless.admin.site.dto.v1_0.SitePage;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Javier Moreno Lage
 */
@RunWith(Arquillian.class)
public class SitePageSiteBatchEnginePortletDataHandlerTest
	extends BaseSiteBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Ignore
	@Override
	@Test
	public void testExportImportFromLastPublishDate() throws Exception {
	}

	@Override
	protected void addEmptyEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_layoutLocalService.getOrAddEmptyLayout(
			externalReferenceCode, TestPropsValues.getUserId(), groupId, false,
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		String name = RandomTestUtil.randomString();

		_layoutLocalService.addLayout(
			externalReferenceCode, TestPropsValues.getUserId(), groupId, false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, name, name, null,
			LayoutConstants.TYPE_PORTLET, false,
			"/" + StringUtil.toLowerCase(name),
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_layoutLocalService.deleteLayout(
			_getLayout(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		Layout layout = _getLayout(externalReferenceCode, groupId);

		return layout.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return SitePage.class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		Layout layout = _getLayout(externalReferenceCode, groupId);

		return layout.getPlid();
	}

	@Override
	protected int getStatus(String externalReferenceCode, long groupId)
		throws Exception {

		Layout layout = _getLayout(externalReferenceCode, groupId);

		return layout.getStatus();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Layout layout = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			externalReferenceCode, groupId);

		if (layout == null) {
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
		return true;
	}

	@Override
	protected boolean supportsPermissions() {
		return true;
	}

	@Override
	protected void updateCreatorUserId(
			String externalReferenceCode, long groupId, long userId)
		throws Exception {

		Layout layout = _getLayout(externalReferenceCode, groupId);

		layout.setUserId(userId);

		_layoutLocalService.updateLayout(layout);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Layout layout = _getLayout(externalReferenceCode, groupId);

		_layoutLocalService.updateLayout(
			groupId, layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getParentLayoutId(),
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
			).build(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(),
			LayoutConstants.TYPE_PORTLET, layout.isHidden(),
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(),
				"/" + StringUtil.toLowerCase(RandomTestUtil.randomString())
			).build(),
			false, null, null, null, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				groupId, layout.getUserId()));
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		Layout layout = _getLayout(externalReferenceCode, groupId);

		layout.setModifiedDate(modifiedDate);

		_layoutLocalService.updateLayout(layout);
	}

	private Layout _getLayout(String externalReferenceCode, long groupId)
		throws Exception {

		return _layoutLocalService.getLayoutByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Inject
	private LayoutLocalService _layoutLocalService;

}