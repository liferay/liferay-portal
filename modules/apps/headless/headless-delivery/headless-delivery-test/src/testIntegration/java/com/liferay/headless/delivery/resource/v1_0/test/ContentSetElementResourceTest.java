/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryAudienceEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryLocalServiceUtil;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.headless.delivery.client.dto.v1_0.ContentSetElement;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.resource.v1_0.ContentSetElementResource;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.segments.constants.SegmentsEntryConstants;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class ContentSetElementResourceTest
	extends BaseContentSetElementResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_serviceContext = _getServiceContext();

		_assetListEntry = AssetListEntryLocalServiceUtil.addAssetListEntry(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(),
			RandomTestUtil.randomString(),
			AssetListEntryTypeConstants.TYPE_DYNAMIC, _serviceContext);
		_depotAssetListEntry = AssetListEntryLocalServiceUtil.addAssetListEntry(
			null, TestPropsValues.getUserId(), testDepotEntry.getGroupId(),
			RandomTestUtil.randomString(),
			AssetListEntryTypeConstants.TYPE_DYNAMIC, _serviceContext);

		_manualAssetListEntry =
			AssetListEntryLocalServiceUtil.addAssetListEntry(
				null, TestPropsValues.getUserId(), testGroup.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		_blogsEntry1 = _addBlogsEntry();

		AssetListEntryLocalServiceUtil.addAssetEntrySelections(
			_manualAssetListEntry.getAssetListEntryId(),
			new long[] {_getAssetEntryId(_blogsEntry1)},
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		_audienceEntryERC1 = RandomTestUtil.randomString();

		_assetListEntryAudienceEntryRelLocalService.
			addAssetListEntryAudienceEntryRel(
				TestPropsValues.getUserId(), testGroup.getGroupId(),
				_manualAssetListEntry.getAssetListEntryId(), _audienceEntryERC1,
				0, null, _serviceContext);

		_blogsEntry2 = _addBlogsEntry();

		AssetListEntryLocalServiceUtil.addAssetEntrySelections(
			_manualAssetListEntry.getAssetListEntryId(),
			new long[] {_getAssetEntryId(_blogsEntry2)}, _audienceEntryERC1,
			_serviceContext);

		_audienceEntryERC2 = RandomTestUtil.randomString();

		_assetListEntryAudienceEntryRelLocalService.
			addAssetListEntryAudienceEntryRel(
				TestPropsValues.getUserId(), testGroup.getGroupId(),
				_manualAssetListEntry.getAssetListEntryId(), _audienceEntryERC2,
				1, null, _serviceContext);

		_blogsEntry3 = _addBlogsEntry();

		AssetListEntryLocalServiceUtil.addAssetEntrySelections(
			_manualAssetListEntry.getAssetListEntryId(),
			new long[] {_getAssetEntryId(_blogsEntry3)}, _audienceEntryERC2,
			_serviceContext);
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag("LPD-109242"),
			@FeatureFlag(enable = false, value = "LPD-78863")
		}
	)
	@Override
	@Test
	public void testGetAssetLibraryContentSetByKeyContentSetElementsPage()
		throws Exception {

		super.testGetAssetLibraryContentSetByKeyContentSetElementsPage();

		_testGetAssetLibraryContentSetByKeyContentSetElementsPageWithAudienceExternalReferenceCodes();
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag("LPD-109242"),
			@FeatureFlag(enable = false, value = "LPD-78863")
		}
	)
	@Override
	@Test
	public void testGetAssetLibraryContentSetByUuidContentSetElementsPage()
		throws Exception {

		super.testGetAssetLibraryContentSetByUuidContentSetElementsPage();

		_testGetAssetLibraryContentSetByUuidContentSetElementsPageWithAudienceExternalReferenceCodes();
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag("LPD-109242"),
			@FeatureFlag(enable = false, value = "LPD-78863")
		}
	)
	@Override
	@Test
	public void testGetContentSetContentSetElementsPage() throws Exception {
		super.testGetContentSetContentSetElementsPage();

		_testGetContentSetContentSetElementsPageWhenFeatureFlagIsDisabled();
		_testGetContentSetContentSetElementsPageWhenSegmentsFeatureFlagIsEnabled();
		_testGetContentSetContentSetElementsPageWithAudienceExternalReferenceCodes();
		_testGetContentSetContentSetElementsPageWithAudiencesCookie();
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag("LPD-109242"),
			@FeatureFlag(enable = false, value = "LPD-78863")
		}
	)
	@Override
	@Test
	public void testGetSiteContentSetByKeyContentSetElementsPage()
		throws Exception {

		super.testGetSiteContentSetByKeyContentSetElementsPage();

		_testGetSiteContentSetByKeyContentSetElementsPageWithAudienceExternalReferenceCodes();
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag("LPD-109242"),
			@FeatureFlag(enable = false, value = "LPD-78863")
		}
	)
	@Override
	@Test
	public void testGetSiteContentSetByUuidContentSetElementsPage()
		throws Exception {

		super.testGetSiteContentSetByUuidContentSetElementsPage();

		_testGetSiteContentSetByUuidContentSetElementsPageWithAudienceExternalReferenceCodes();
	}

	@FeatureFlag("LPD-32867")
	@Override
	@Test
	public void testGetSiteContentSetProviderByKeyContentSetElementsPage()
		throws Exception {

		ServiceRegistration<InfoCollectionProvider<?>> serviceRegistration =
			_registerInfoCollectionProviderService();

		try {
			super.testGetSiteContentSetProviderByKeyContentSetElementsPage();
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@FeatureFlag("LPD-32867")
	@Override
	@Test
	public void testGetSiteContentSetProviderByKeyContentSetElementsPageWithPagination()
		throws Exception {

		ServiceRegistration<InfoCollectionProvider<?>> serviceRegistration =
			_registerInfoCollectionProviderService();

		try {
			super.
				testGetSiteContentSetProviderByKeyContentSetElementsPageWithPagination();
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Override
	protected ContentSetElement
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_addContentSetElement(
				Long assetLibraryId, String key,
				ContentSetElement contentSetElement)
		throws Exception {

		return _toContentSetElement(
			JournalTestUtil.addArticle(_depotAssetListEntry.getGroupId(), 0));
	}

	@Override
	protected String
		testGetAssetLibraryContentSetByKeyContentSetElementsPage_getKey() {

		return _depotAssetListEntry.getAssetListEntryKey();
	}

	@Override
	protected ContentSetElement
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_addContentSetElement(
				Long assetLibraryId, String uuid,
				ContentSetElement contentSetElement)
		throws Exception {

		return _toContentSetElement(
			JournalTestUtil.addArticle(_depotAssetListEntry.getGroupId(), 0));
	}

	@Override
	protected String
		testGetAssetLibraryContentSetByUuidContentSetElementsPage_getUuid() {

		return _depotAssetListEntry.getUuid();
	}

	@Override
	protected ContentSetElement
			testGetContentSetContentSetElementsPage_addContentSetElement(
				Long contentSetId, ContentSetElement contentSetElement)
		throws Exception {

		return _toContentSetElement(_addBlogsEntry());
	}

	@Override
	protected Long testGetContentSetContentSetElementsPage_getContentSetId() {
		return _assetListEntry.getAssetListEntryId();
	}

	@Override
	protected ContentSetElement
			testGetSiteContentSetByKeyContentSetElementsPage_addContentSetElement(
				Long siteId, String key, ContentSetElement contentSetElement)
		throws Exception {

		return _toContentSetElement(_addBlogsEntry());
	}

	@Override
	protected String testGetSiteContentSetByKeyContentSetElementsPage_getKey() {
		return _assetListEntry.getAssetListEntryKey();
	}

	@Override
	protected ContentSetElement
			testGetSiteContentSetByUuidContentSetElementsPage_addContentSetElement(
				Long siteId, String uuid, ContentSetElement contentSetElement)
		throws Exception {

		return _toContentSetElement(_addBlogsEntry());
	}

	@Override
	protected String
		testGetSiteContentSetByUuidContentSetElementsPage_getUuid() {

		return _assetListEntry.getUuid();
	}

	@Override
	protected ContentSetElement
			testGetSiteContentSetProviderByKeyContentSetElementsPage_addContentSetElement(
				Long siteId, String key, ContentSetElement contentSetElement)
		throws Exception {

		return _toContentSetElement(_addBlogsEntry());
	}

	@Override
	protected String
		testGetSiteContentSetProviderByKeyContentSetElementsPage_getKey() {

		return TestAssetEntryInfoCollectionProvider.class.getName();
	}

	private BlogsEntry _addBlogsEntry() throws Exception {
		return BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), _serviceContext);
	}

	private AssetListEntry _addDepotAssetListEntry(String audienceEntryERC)
		throws Exception {

		AssetListEntry assetListEntry =
			AssetListEntryLocalServiceUtil.addAssetListEntry(
				null, TestPropsValues.getUserId(), testDepotEntry.getGroupId(),
				RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testDepotEntry.getGroupId(), 0);

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		AssetListEntryLocalServiceUtil.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry.getEntryId()},
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		_assetListEntryAudienceEntryRelLocalService.
			addAssetListEntryAudienceEntryRel(
				TestPropsValues.getUserId(), testDepotEntry.getGroupId(),
				assetListEntry.getAssetListEntryId(), audienceEntryERC, 0, null,
				_serviceContext);

		return assetListEntry;
	}

	private void _assertContentSetElementsPage(
		BlogsEntry blogsEntry, Page<ContentSetElement> contentSetElementsPage) {

		List<ContentSetElement> contentSetElements =
			(List<ContentSetElement>)contentSetElementsPage.getItems();

		Assert.assertEquals(
			contentSetElements.toString(), 1, contentSetElements.size());

		ContentSetElement contentSetElement = contentSetElements.get(0);

		Assert.assertEquals(
			Long.valueOf(blogsEntry.getEntryId()), contentSetElement.getId());
	}

	private void _assertDefaultVariation() throws Exception {
		ContentSetElementResource cookieContentSetElementResource =
			_getContentSetElementResource(_audienceEntryERC2);

		_assertContentSetElementsPage(
			_blogsEntry1,
			cookieContentSetElementResource.getContentSetContentSetElementsPage(
				_manualAssetListEntry.getAssetListEntryId(),
				new String[] {_audienceEntryERC1}, null));
		_assertContentSetElementsPage(
			_blogsEntry1,
			cookieContentSetElementResource.getContentSetContentSetElementsPage(
				_manualAssetListEntry.getAssetListEntryId(), null, null));
	}

	private long _getAssetEntryId(BlogsEntry blogsEntry) throws Exception {
		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			BlogsEntry.class.getName(), blogsEntry.getEntryId());

		return assetEntry.getEntryId();
	}

	private ContentSetElementResource _getContentSetElementResource(
			String audienceEntryERC)
		throws Exception {

		String cookieValue = URLCodec.encodeURL(
			JSONUtil.put(
				audienceEntryERC
			).toString());

		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return ContentSetElementResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).header(
			"Cookie", "com.liferay.frontend.js.audiences=" + cookieValue
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private ServiceContext _getServiceContext() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAttribute(
			WorkflowConstants.CONTEXT_USER_ID, TestPropsValues.getUserId());
		serviceContext.setCompanyId(testGroup.getCompanyId());
		serviceContext.setScopeGroupId(testGroup.getGroupId());
		serviceContext.setUserId(TestPropsValues.getUserId());

		return serviceContext;
	}

	private ServiceRegistration<InfoCollectionProvider<?>>
		_registerInfoCollectionProviderService() {

		Bundle bundle = FrameworkUtil.getBundle(
			ContentSetElementResourceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			(Class<InfoCollectionProvider<?>>)
				(Class<?>)InfoCollectionProvider.class,
			new TestAssetEntryInfoCollectionProvider(), null);
	}

	private void _testGetAssetLibraryContentSetByKeyContentSetElementsPageWithAudienceExternalReferenceCodes()
		throws Exception {

		String audienceEntryERC = RandomTestUtil.randomString();

		AssetListEntry assetListEntry = _addDepotAssetListEntry(
			audienceEntryERC);

		Page<ContentSetElement> contentSetElementsPage =
			contentSetElementResource.
				getAssetLibraryContentSetByKeyContentSetElementsPage(
					testDepotEntry.getGroupId(),
					assetListEntry.getAssetListEntryKey(),
					new String[] {audienceEntryERC}, null);

		Assert.assertEquals(0, contentSetElementsPage.getTotalCount());
	}

	private void _testGetAssetLibraryContentSetByUuidContentSetElementsPageWithAudienceExternalReferenceCodes()
		throws Exception {

		String audienceEntryERC = RandomTestUtil.randomString();

		AssetListEntry assetListEntry = _addDepotAssetListEntry(
			audienceEntryERC);

		Page<ContentSetElement> contentSetElementsPage =
			contentSetElementResource.
				getAssetLibraryContentSetByUuidContentSetElementsPage(
					testDepotEntry.getGroupId(), assetListEntry.getUuid(),
					new String[] {audienceEntryERC}, null);

		Assert.assertEquals(0, contentSetElementsPage.getTotalCount());
	}

	private void _testGetContentSetContentSetElementsPageWhenFeatureFlagIsDisabled()
		throws Exception {

		PropsUtil.set("feature.flag.LPD-109242", "false");

		try {
			_assertDefaultVariation();
		}
		finally {
			PropsUtil.set("feature.flag.LPD-109242", "true");
		}
	}

	private void _testGetContentSetContentSetElementsPageWhenSegmentsFeatureFlagIsEnabled()
		throws Exception {

		PropsUtil.set("feature.flag.LPD-78863", "true");

		try {
			_assertDefaultVariation();
		}
		finally {
			PropsUtil.set("feature.flag.LPD-78863", "false");
		}
	}

	private void _testGetContentSetContentSetElementsPageWithAudienceExternalReferenceCodes()
		throws Exception {

		_assertContentSetElementsPage(
			_blogsEntry1,
			contentSetElementResource.getContentSetContentSetElementsPage(
				_manualAssetListEntry.getAssetListEntryId(),
				new String[] {RandomTestUtil.randomString()}, null));
		_assertContentSetElementsPage(
			_blogsEntry1,
			contentSetElementResource.getContentSetContentSetElementsPage(
				_manualAssetListEntry.getAssetListEntryId(), null, null));
		_assertContentSetElementsPage(
			_blogsEntry2,
			contentSetElementResource.getContentSetContentSetElementsPage(
				_manualAssetListEntry.getAssetListEntryId(),
				new String[] {_audienceEntryERC2, _audienceEntryERC1}, null));
	}

	private void _testGetContentSetContentSetElementsPageWithAudiencesCookie()
		throws Exception {

		ContentSetElementResource cookieContentSetElementResource =
			_getContentSetElementResource(_audienceEntryERC2);

		_assertContentSetElementsPage(
			_blogsEntry1,
			cookieContentSetElementResource.getContentSetContentSetElementsPage(
				_manualAssetListEntry.getAssetListEntryId(),
				new String[] {StringPool.BLANK}, null));
		_assertContentSetElementsPage(
			_blogsEntry2,
			cookieContentSetElementResource.getContentSetContentSetElementsPage(
				_manualAssetListEntry.getAssetListEntryId(),
				new String[] {_audienceEntryERC1}, null));
		_assertContentSetElementsPage(
			_blogsEntry3,
			cookieContentSetElementResource.getContentSetContentSetElementsPage(
				_manualAssetListEntry.getAssetListEntryId(), null, null));
	}

	private void _testGetSiteContentSetByKeyContentSetElementsPageWithAudienceExternalReferenceCodes()
		throws Exception {

		_assertContentSetElementsPage(
			_blogsEntry2,
			contentSetElementResource.
				getSiteContentSetByKeyContentSetElementsPage(
					testGroup.getGroupId(),
					_manualAssetListEntry.getAssetListEntryKey(),
					new String[] {_audienceEntryERC2, _audienceEntryERC1},
					null));
	}

	private void _testGetSiteContentSetByUuidContentSetElementsPageWithAudienceExternalReferenceCodes()
		throws Exception {

		_assertContentSetElementsPage(
			_blogsEntry2,
			contentSetElementResource.
				getSiteContentSetByUuidContentSetElementsPage(
					testGroup.getGroupId(), _manualAssetListEntry.getUuid(),
					new String[] {_audienceEntryERC2, _audienceEntryERC1},
					null));
	}

	private ContentSetElement _toContentSetElement(BlogsEntry blogsEntry) {
		return new ContentSetElement() {
			{
				id = blogsEntry.getEntryId();
				title = blogsEntry.getTitle();
			}
		};
	}

	private ContentSetElement _toContentSetElement(
		JournalArticle journalArticle) {

		return new ContentSetElement() {
			{
				id = journalArticle.getId();
				title = journalArticle.getTitle();
			}
		};
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	private AssetListEntry _assetListEntry;

	@Inject
	private AssetListEntryAudienceEntryRelLocalService
		_assetListEntryAudienceEntryRelLocalService;

	private String _audienceEntryERC1;
	private String _audienceEntryERC2;
	private BlogsEntry _blogsEntry1;
	private BlogsEntry _blogsEntry2;
	private BlogsEntry _blogsEntry3;
	private AssetListEntry _depotAssetListEntry;
	private AssetListEntry _manualAssetListEntry;
	private ServiceContext _serviceContext;

	private class TestAssetEntryInfoCollectionProvider
		implements InfoCollectionProvider<AssetEntry> {

		@Override
		public InfoPage<AssetEntry> getCollectionInfoPage(
			CollectionQuery collectionQuery) {

			AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

			assetEntryQuery.setClassName(BlogsEntry.class.getName());

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			assetEntryQuery.setGroupIds(
				new long[] {serviceContext.getScopeGroupId()});

			List<AssetEntry> assetEntries = _assetEntryLocalService.getEntries(
				assetEntryQuery);

			if (assetEntries == null) {
				assetEntries = Collections.emptyList();
			}

			Pagination pagination = collectionQuery.getPagination();

			return InfoPage.of(
				assetEntries.subList(
					pagination.getStart(),
					Math.min(assetEntries.size(), pagination.getEnd())),
				Pagination.of(pagination.getEnd(), pagination.getStart()),
				assetEntries.size());
		}

		@Override
		public String getLabel(Locale locale) {
			return StringPool.BLANK;
		}

	}

}