/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.client.dto.v1_0.ExpiredAsset;
import com.liferay.analytics.cms.rest.client.pagination.Page;
import com.liferay.analytics.cms.rest.client.pagination.Pagination;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;
import java.io.Serializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Thiago Buarque
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-21926"),
		@FeatureFlag("LPD-31149"), @FeatureFlag("LPD-34594"),
		@FeatureFlag("LPS-179669")
	}
)
@RunWith(Arquillian.class)
public class ExpiredAssetResourceTest extends BaseExpiredAssetResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testGetExpiredAssetsPage() throws Exception {
		_setUpCMSContext();

		Page<ExpiredAsset> page = expiredAssetResource.getExpiredAssetsPage(
			null, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		ExpiredAsset expiredAsset1 = _addExpiredAsset(
			RandomTestUtil.randomString(), randomExpiredAsset(), null);

		String portugueseTitle = RandomTestUtil.randomString();

		ExpiredAsset expiredAsset2 = _addExpiredAsset(
			RandomTestUtil.randomString(), randomExpiredAsset(),
			portugueseTitle);

		page = expiredAssetResource.getExpiredAssetsPage(
			null, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(expiredAsset1, (List<ExpiredAsset>)page.getItems());
		assertContains(expiredAsset2, (List<ExpiredAsset>)page.getItems());

		page = expiredAssetResource.getExpiredAssetsPage(
			_depotEntry.getDepotEntryId(), "pt_BR", Pagination.of(1, 10));

		Collection<ExpiredAsset> items = page.getItems();

		Assert.assertEquals(items.toString(), 1, items.size());

		expiredAsset2.setTitle(portugueseTitle);

		assertContains(expiredAsset2, (List<ExpiredAsset>)page.getItems());
	}

	@Override
	@Test
	public void testGetExpiredAssetsPageWithPagination() throws Exception {
		_setUpCMSContext();

		super.testGetExpiredAssetsPageWithPagination();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"title", "href", "usages"};
	}

	@Override
	protected ExpiredAsset testGetExpiredAssetsPage_addExpiredAsset(
			ExpiredAsset expiredAsset)
		throws Exception {

		return _addExpiredAsset(
			RandomTestUtil.randomString(), expiredAsset,
			RandomTestUtil.randomString());
	}

	private ExpiredAsset _addExpiredAsset(
			String englishTitle, ExpiredAsset expiredAsset,
			String portugueseTitle)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BASIC_WEB_CONTENT", testCompany.getCompanyId());

		_serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), _depotEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, "en_US",
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				() -> {
					HashMapBuilder.HashMapWrapper<String, Serializable>
						titleMap = HashMapBuilder.<String, Serializable>put(
							"en_US", englishTitle);

					if (Validator.isNotNull(portugueseTitle)) {
						titleMap = titleMap.put("pt_BR", portugueseTitle);
					}

					return titleMap.build();
				}
			).build(),
			_serviceContext);

		_objectEntryLocalService.expireObjectEntry(
			_depotEntry.getUserId(), objectEntry.getObjectEntryId(),
			_serviceContext);

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			testGroup.getGroupId(), StringPool.BLANK,
			_portal.getClassNameId(objectDefinition.getClassName()),
			objectEntry.getObjectEntryId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
			_serviceContext);

		expiredAsset.setHref(
			() -> StringBundler.concat(
				_themeDisplay.getPortalURL(), _portal.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/edit_content_item?&p_l_mode=read&p_p_state=",
				LiferayWindowState.POP_UP, "&objectEntryId=",
				objectEntry.getObjectEntryId()));

		expiredAsset.setTitle(englishTitle);
		expiredAsset.setUsages(1);

		return expiredAsset;
	}

	private void _deleteFile(Bundle bundle, String fileName) {
		File file = bundle.getDataFile(
			".com.liferay.site.initializer.cms.internal.batch." + fileName +
				".batch.engine.data.json.0.processed");

		if ((file != null) && file.exists()) {
			file.delete();
		}
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(testCompany);
		themeDisplay.setLanguageId(testGroup.getDefaultLanguageId());
		themeDisplay.setLocale(
			LocaleUtil.fromLanguageId(testGroup.getDefaultLanguageId()));
		themeDisplay.setPortalDomain("localhost");
		themeDisplay.setPortalURL(
			testCompany.getPortalURL(testGroup.getGroupId()));
		themeDisplay.setServerName("localhost");
		themeDisplay.setServerPort(8080);
		themeDisplay.setSiteGroupId(testGroup.getGroupId());
		themeDisplay.setUser(testCompany.getGuestUser());

		return themeDisplay;
	}

	private void _setUpCMSContext() throws Exception {
		Bundle testBundle = FrameworkUtil.getBundle(OverviewResourceTest.class);

		BundleContext bundleContext = testBundle.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			if (Objects.equals(
					bundle.getSymbolicName(),
					"com.liferay.site.initializer.cms")) {

				_deleteFile(bundle, "00.list.type.definition");
				_deleteFile(bundle, "01.object.folder");
				_deleteFile(bundle, "02.object.definition");

				CompletableFuture<Void> completableFuture =
					_batchEngineUnitProcessor.processBatchEngineUnits(
						_batchEngineUnitReader.getBatchEngineUnits(bundle));

				completableFuture.join();

				break;
			}
		}

		testGroup.setType(GroupConstants.TYPE_DEPOT);

		testGroup = _groupLocalService.updateGroup(testGroup);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testGroup.getGroupId(), TestPropsValues.getUserId());

		_serviceContext.setAttribute("staging", Boolean.TRUE);

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			testGroup, _serviceContext);

		_themeDisplay = _getThemeDisplay();
	}

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;
	private ThemeDisplay _themeDisplay;

}