/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.headless.admin.site.client.dto.v1_0.ClassSubtypeReference;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateFolder;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateOpenGraphSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateSEOSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.FavIcon;
import com.liferay.headless.admin.site.client.dto.v1_0.FriendlyUrlHistory;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.SitemapSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.ThumbnailURLReference;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.permission.Permission;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.DisplayPageTemplateResource;
import com.liferay.headless.admin.site.resource.v1_0.test.util.FileEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.FragmentEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageElementsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageExperiencesTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.SettingsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ThumbnailHttpServer;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ThumbnailURLReferenceUtil;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 * @author Lourdes Fernández Besada
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-35443"), @FeatureFlag("LPD-57283")}
)
@RunWith(Arquillian.class)
public class DisplayPageTemplateResourceTest
	extends BaseDisplayPageTemplateResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDisplayPageTemplateResourceTestCase.setUpClass();

		_thumbnailHttpServer = ThumbnailHttpServer.start(
			DisplayPageTemplateResourceTest.class);

		_thumbnail1Base64 = _thumbnailHttpServer.getThumbnail1Base64();
		_thumbnail1Bytes = _thumbnailHttpServer.getThumbnail1Bytes();
		_thumbnail1URL = _thumbnailHttpServer.getThumbnail1URL();
		_thumbnail2Base64 = _thumbnailHttpServer.getThumbnail2Base64();
		_thumbnail2Bytes = _thumbnailHttpServer.getThumbnail2Bytes();
		_thumbnail2URL = _thumbnailHttpServer.getThumbnail2URL();
	}

	@AfterClass
	public static void tearDownClass() {
		if (_thumbnailHttpServer != null) {
			_thumbnailHttpServer.stop();
		}
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	public void testDeleteSiteDisplayPageTemplate() throws Exception {
		DisplayPageTemplate postDisplayPageTemplate =
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					postDisplayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId()));

		displayPageTemplateResource.deleteSiteDisplayPageTemplate(
			testGroup.getExternalReferenceCode(),
			postDisplayPageTemplate.getExternalReferenceCode());

		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					postDisplayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId()));

		_assertProblemException(
			"NOT_FOUND", null,
			() -> displayPageTemplateResource.deleteSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				postDisplayPageTemplate.getExternalReferenceCode()));

		DisplayPageTemplate liveGroupDisplayPageTemplate =
			testGetSiteDisplayPageTemplatesPage_addDisplayPageTemplate(
				irrelevantGroup.getExternalReferenceCode(),
				randomDisplayPageTemplate());

		_enableLocalStaging(irrelevantGroup);

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> displayPageTemplateResource.deleteSiteDisplayPageTemplate(
				irrelevantGroup.getExternalReferenceCode(),
				liveGroupDisplayPageTemplate.getExternalReferenceCode()));
	}

	@Override
	@Test
	@TestInfo("LPD-106070")
	public void testGetDesignLibraryDisplayPageTemplate() throws Exception {
		super.testGetDesignLibraryDisplayPageTemplate();

		DisplayPageTemplate displayPageTemplate =
			_addDesignLibraryDisplayPageTemplate(
				_getDesignLibraryExternalReferenceCode());

		Map<String, Map<String, String>> actions =
			displayPageTemplate.getActions();

		Assert.assertTrue(actions.toString(), actions.containsKey("delete"));
		Assert.assertTrue(actions.toString(), actions.containsKey("get"));
		Assert.assertTrue(
			actions.toString(), actions.containsKey("permissions"));
	}

	@Override
	@Test
	@TestInfo("LPD-106070")
	public void testGetDesignLibraryDisplayPageTemplatePermissionsPage()
		throws Exception {

		DisplayPageTemplate displayPageTemplate =
			testGetDesignLibraryDisplayPageTemplatePermissionsPage_addDisplayPageTemplate();

		Page<Permission> page =
			displayPageTemplateResource.
				getDesignLibraryDisplayPageTemplatePermissionsPage(
					_getDesignLibraryExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					RoleConstants.GUEST);

		_assertDesignLibraryPermissionActionHrefs(page.getActions());
	}

	@Override
	@Test
	public void testGetSiteDisplayPageTemplate() throws Exception {
		DisplayPageTemplate displayPageTemplate =
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		_testGetSiteDisplayPageTemplate(displayPageTemplate);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertFalse(_isPublished(layout));

		_testGetSiteDisplayPageTemplateWithNestedFields(displayPageTemplate);
		_testGetSiteDisplayPageTemplateWithPageElementsWithTemplateEntries();

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_publishLayoutPageTemplateEntry",
			new Class<?>[] {Layout.class, Layout.class, long.class},
			layout.fetchDraftLayout(), layout, TestPropsValues.getUserId());

		Assert.assertTrue(_isPublished(layout));

		_testGetSiteDisplayPageTemplateWithNestedFields(displayPageTemplate);

		_assertProblemException(
			"NOT_FOUND", null,
			() -> displayPageTemplateResource.getSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString()));

		_enableLocalStaging();

		_testGetSiteDisplayPageTemplate(displayPageTemplate);
		_testGetSiteDisplayPageTemplateWithNestedFields(displayPageTemplate);
	}

	@Override
	@Test
	public void testGetSiteDisplayPageTemplatesPage() throws Exception {
		super.testGetSiteDisplayPageTemplatesPage();

		_testGetSiteDisplayPageTemplatesPageWithPageSpecificationsAsNestedFields();
		_testGetSiteDisplayPageTemplatesPageWithThumbnailAsNestedField();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteDisplayPageTemplatesPageWithSortDateTime()
		throws Exception {

		super.testGetSiteDisplayPageTemplatesPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteDisplayPageTemplatesPageWithSortDouble()
		throws Exception {

		super.testGetSiteDisplayPageTemplatesPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteDisplayPageTemplatesPageWithSortInteger()
		throws Exception {

		super.testGetSiteDisplayPageTemplatesPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteDisplayPageTemplatesPageWithSortString()
		throws Exception {

		super.testGetSiteDisplayPageTemplatesPageWithSortString();
	}

	@Override
	@Test
	@TestInfo("LPD-92443")
	public void testPatchSiteDisplayPageTemplate() throws Exception {
		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(),
				PageSpecification.Status.APPROVED);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), PageSpecification.Status.APPROVED);

		displayPageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		DisplayPageTemplate expectedDisplayPageTemplate =
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				displayPageTemplate);

		_testPatchSiteDisplayPageTemplate(
			expectedDisplayPageTemplate, new DisplayPageTemplate());

		Assert.assertNull(expectedDisplayPageTemplate.getParentFolder());

		ClassSubtypeReference contentTypeReference =
			expectedDisplayPageTemplate.getContentTypeReference();

		String className = contentTypeReference.getClassName();

		if (className.equals(AssetCategory.class.getName())) {
			className = "com.liferay.journal.model.JournalArticle";
		}

		expectedDisplayPageTemplate.setContentTypeReference(
			_getClassSubtypeReference(className));
		expectedDisplayPageTemplate.setMarkedAsDefault(Boolean.FALSE);
		expectedDisplayPageTemplate.setParentFolder(
			new DisplayPageTemplateFolder() {
				{
					setExternalReferenceCode(
						_getLayoutPageTemplateCollectionExternalReferenceCode(
							testGroup.getGroupId()));
				}
			});

		_testPatchSiteDisplayPageTemplate(
			expectedDisplayPageTemplate,
			new DisplayPageTemplate() {
				{
					setContentTypeReference(
						expectedDisplayPageTemplate.getContentTypeReference());
					setMarkedAsDefault(
						expectedDisplayPageTemplate.getMarkedAsDefault());
					setParentFolder(
						expectedDisplayPageTemplate.getParentFolder());
				}
			});

		expectedDisplayPageTemplate.setMarkedAsDefault(Boolean.TRUE);

		_testPatchSiteDisplayPageTemplate(
			expectedDisplayPageTemplate,
			new DisplayPageTemplate() {
				{
					setMarkedAsDefault(
						expectedDisplayPageTemplate.getMarkedAsDefault());
				}
			});

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		expectedDisplayPageTemplate.setThumbnailURLReference(
			ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, null));

		_testPatchSiteDisplayPageTemplate(
			expectedDisplayPageTemplate,
			new DisplayPageTemplate() {
				{
					setMarkedAsDefault(
						expectedDisplayPageTemplate.getMarkedAsDefault());
					setThumbnailURLReference(
						expectedDisplayPageTemplate.getThumbnailURLReference());
				}
			});

		expectedDisplayPageTemplate.setDisplayPageTemplateSettings(
			_randomDisplayPageTemplateSettings());

		_testPatchSiteDisplayPageTemplate(
			expectedDisplayPageTemplate,
			new DisplayPageTemplate() {
				{
					setDisplayPageTemplateSettings(
						expectedDisplayPageTemplate.
							getDisplayPageTemplateSettings());
					setMarkedAsDefault(
						expectedDisplayPageTemplate.getMarkedAsDefault());
				}
			});

		_testPatchSiteDisplayPageTemplateWithPageSpecifications();
		_testPatchSiteDisplayPageTemplateWithThumbnail();

		_assertProblemException(
			"NOT_FOUND", null,
			() -> displayPageTemplateResource.patchSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), randomDisplayPageTemplate()));

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> displayPageTemplateResource.patchSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				expectedDisplayPageTemplate.getExternalReferenceCode(),
				expectedDisplayPageTemplate));
	}

	@Override
	@Test
	@TestInfo("LPD-92443")
	public void testPostSiteDisplayPageTemplate() throws Exception {
		super.testPostSiteDisplayPageTemplate();

		_testPostSiteDisplayPageTemplateWithKey();
		_testPostSiteDisplayPageTemplateWithMarkedAsDefault();
		_testPostSiteDisplayPageTemplateWithPageElementsWithTemplateEntries();
		_testPostSiteDisplayPageTemplateWithPageSpecifications();
		_testPostSiteDisplayPageTemplateWithParentFolder();
		_testPostSiteDisplayPageTemplateWithThumbnail();
		_testPostSiteDisplayPageTemplateWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64();
		_testPostSiteDisplayPageTemplateWithThumbnailURLReferenceExternalReferenceCodeEmptyAndFileBase64();
		_testPostSiteDisplayPageTemplateWithThumbnailURLReferenceExternalReferenceCodeNullAndFileBase64();
		_testPostSiteDisplayPageTemplateWithThumbnailURLReferenceExternalReferenceCodeNullAndURL();
		_testPostSiteDisplayPageTemplateWithThumbnailURLReferenceFileBase64();
		_testPostSiteDisplayPageTemplateWithThumbnailURLReferenceFileBase64AndURL();
		_testPostSiteDisplayPageTemplateWithThumbnailURLReferenceNonexistingProblemException();
		_testPostSiteDisplayPageTemplateWithThumbnailURLReferenceURL();
		_testPostSiteDisplayPageTemplateWithThumbnailURLReferenceURLUnsupportedProtocolProblemException();
	}

	@Ignore
	@Override
	@Test
	public void testPostSiteDisplayPageTemplateFolderDisplayPageTemplate()
		throws Exception {

		super.testPostSiteDisplayPageTemplateFolderDisplayPageTemplate();
	}

	@Override
	@Test
	public void testPostSiteDisplayPageTemplatePageSpecification()
		throws Exception {

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource(
				"friendlyUrlHistory,pageSpecifications");

		DisplayPageTemplate displayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplate());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		PageSpecificationsTestUtil.testPostSitePageSpecification(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			displayPageTemplate.getPageSpecifications(), serviceContext,
			contentPageSpecification ->
				displayPageTemplateResource.
					postSiteDisplayPageTemplatePageSpecification(
						testGroup.getExternalReferenceCode(),
						displayPageTemplate.getExternalReferenceCode(),
						contentPageSpecification));

		_assertPostSiteDisplayPageTemplatePageSpecificationProblemException(
			"The display page template type does not match the display page " +
				"type",
			LayoutPageTemplateEntryTestUtil.getBasicLayoutPageTemplateEntry(
				serviceContext));

		_assertPostSiteDisplayPageTemplatePageSpecificationProblemException(
			"The display page template type does not match the display page " +
				"type",
			LayoutPageTemplateEntryTestUtil.getMasterLayoutPageTemplateEntry(
				serviceContext, WorkflowConstants.STATUS_DRAFT));
	}

	@Override
	@Test
	@TestInfo("LPD-106070")
	public void testPutDesignLibraryDisplayPageTemplatePermissionsPage()
		throws Exception {

		DisplayPageTemplate displayPageTemplate =
			testPutDesignLibraryDisplayPageTemplatePermissionsPage_addDisplayPageTemplate();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			displayPageTemplateResource.
				putDesignLibraryDisplayPageTemplatePermissionsPageHttpResponse(
					_getDesignLibraryExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"VIEW"});
								setRoleName(role.getName());
							}
						}
					}));

		assertHttpResponseStatusCode(
			404,
			displayPageTemplateResource.
				putDesignLibraryDisplayPageTemplatePermissionsPageHttpResponse(
					_getDesignLibraryExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"-"});
								setRoleName("-");
							}
						}
					}));

		Page<Permission> page =
			displayPageTemplateResource.
				putDesignLibraryDisplayPageTemplatePermissionsPage(
					_getDesignLibraryExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"VIEW"});
								setRoleName(role.getName());
							}
						}
					});

		_assertDesignLibraryPermissionActionHrefs(page.getActions());
	}

	@Override
	@Test
	@TestInfo("LPD-92443")
	public void testPutSiteDisplayPageTemplate() throws Exception {
		_testPutSiteDisplayPageTemplateContentTypeReference();
		_testPutSiteDisplayPageTemplateMarkedAsDefault();
		_testPutSiteDisplayPageTemplateSettings();
		_testPutSiteDisplayPageTemplateWithSubtype();
		_testPutSiteDisplayPageTemplateWithThumbnail();
		_testPutSiteDisplayPageTemplateWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64();
		_testPutSiteDisplayPageTemplateWithThumbnailURLReferenceFileBase64();
		_testPutSiteDisplayPageTemplateWithThumbnailURLReferenceURL();

		_testPutSiteDisplayPageTemplate(randomDisplayPageTemplate());

		DisplayPageTemplate displayPageTemplate =
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		_testPutSiteDisplayPageTemplate(displayPageTemplate);

		_testPutSiteDisplayPageTemplateWithPageSpecifications();

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		displayPageTemplate.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, null));

		displayPageTemplateResource.putSiteDisplayPageTemplate(
			testGroup.getExternalReferenceCode(),
			displayPageTemplate.getExternalReferenceCode(),
			displayPageTemplate);

		_enableLocalStaging();

		_assertStagingGroupDisplayPageTemplateThumbnail(
			displayPageTemplate, fileEntry);

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"displayPageTemplateSettings", "externalReferenceCode",
			"friendlyUrlPath_i18n", "name"
		};
	}

	@Override
	protected DisplayPageTemplate randomDisplayPageTemplate() throws Exception {
		return _randomDisplayPageTemplate(Boolean.FALSE);
	}

	@Override
	protected DisplayPageTemplate
			testDeleteDesignLibraryDisplayPageTemplate_addDisplayPageTemplate()
		throws Exception {

		return _addDesignLibraryDisplayPageTemplate(
			_getDesignLibraryExternalReferenceCode());
	}

	@Override
	protected String
			testDeleteDesignLibraryDisplayPageTemplate_getDesignLibraryExternalReferenceCode()
		throws Exception {

		return _getDesignLibraryExternalReferenceCode();
	}

	@Override
	protected DisplayPageTemplate
			testGetDesignLibraryDisplayPageTemplate_addDisplayPageTemplate()
		throws Exception {

		return _addDesignLibraryDisplayPageTemplate(
			_getDesignLibraryExternalReferenceCode());
	}

	@Override
	protected String
			testGetDesignLibraryDisplayPageTemplate_getDesignLibraryExternalReferenceCode()
		throws Exception {

		return _getDesignLibraryExternalReferenceCode();
	}

	@Override
	protected DisplayPageTemplate
			testGetDesignLibraryDisplayPageTemplatePermissionsPage_addDisplayPageTemplate()
		throws Exception {

		return _addDesignLibraryDisplayPageTemplate(
			_getDesignLibraryExternalReferenceCode());
	}

	@Override
	protected DisplayPageTemplate
			testGetDesignLibraryDisplayPageTemplatesPage_addDisplayPageTemplate(
				String designLibraryExternalReferenceCode,
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		return _addDesignLibraryDisplayPageTemplate(
			designLibraryExternalReferenceCode);
	}

	@Override
	protected String
			testGetDesignLibraryDisplayPageTemplatesPage_getDesignLibraryExternalReferenceCode()
		throws Exception {

		return _getDesignLibraryExternalReferenceCode();
	}

	@Override
	protected String
			testGetDesignLibraryDisplayPageTemplatesPage_getIrrelevantDesignLibraryExternalReferenceCode()
		throws Exception {

		return _getIrrelevantDesignLibraryExternalReferenceCode();
	}

	@Override
	protected DisplayPageTemplate
			testGetSiteDisplayPageTemplateFolderDisplayPageTemplatesPage_addDisplayPageTemplate(
				String siteExternalReferenceCode,
				String displayPageTemplateFolderExternalReferenceCode,
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		return displayPageTemplateResource.
			postSiteDisplayPageTemplateFolderDisplayPageTemplate(
				siteExternalReferenceCode,
				displayPageTemplateFolderExternalReferenceCode,
				displayPageTemplate);
	}

	@Override
	protected String
			testGetSiteDisplayPageTemplateFolderDisplayPageTemplatesPage_getDisplayPageTemplateFolderExternalReferenceCode()
		throws Exception {

		return _getLayoutPageTemplateCollectionExternalReferenceCode(
			testGroup.getGroupId());
	}

	@Override
	protected String
			testGetSiteDisplayPageTemplateFolderDisplayPageTemplatesPage_getIrrelevantDisplayPageTemplateFolderExternalReferenceCode()
		throws Exception {

		return _getLayoutPageTemplateCollectionExternalReferenceCode(
			irrelevantGroup.getGroupId());
	}

	@Override
	protected DisplayPageTemplate
			testGetSiteDisplayPageTemplatesPage_addDisplayPageTemplate(
				String siteExternalReferenceCode,
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		return displayPageTemplateResource.postSiteDisplayPageTemplate(
			siteExternalReferenceCode, displayPageTemplate);
	}

	@Override
	protected Map<String, Map<String, String>>
		testGetSiteDisplayPageTemplatesPage_getExpectedActions(
			String siteExternalReferenceCode) {

		return new HashMap<>();
	}

	@Override
	protected DisplayPageTemplate
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		return testGetSiteDisplayPageTemplatesPage_addDisplayPageTemplate(
			testGroup.getExternalReferenceCode(), displayPageTemplate);
	}

	@Override
	protected DisplayPageTemplate
			testPutDesignLibraryDisplayPageTemplatePermissionsPage_addDisplayPageTemplate()
		throws Exception {

		return _addDesignLibraryDisplayPageTemplate(
			_getDesignLibraryExternalReferenceCode());
	}

	private DisplayPageTemplate _addDesignLibraryDisplayPageTemplate(
			String designLibraryExternalReferenceCode)
		throws Exception {

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			designLibraryExternalReferenceCode, TestPropsValues.getCompanyId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				group.getGroupId());

		return displayPageTemplateResource.getDesignLibraryDisplayPageTemplate(
			designLibraryExternalReferenceCode,
			layoutPageTemplateEntry.getExternalReferenceCode());
	}

	private Group _addDesignLibraryGroup() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));

		return depotEntry.getGroup();
	}

	private void _assertDesignLibraryPermissionActionHrefs(
		Map<String, Map<String, String>> actions) {

		Map<String, String> getAction = actions.get("get");

		String getHref = getAction.get("href");

		Assert.assertTrue(getHref, getHref.contains("/design-libraries/"));

		Map<String, String> replaceAction = actions.get("replace");

		String replaceHref = replaceAction.get("href");

		Assert.assertTrue(
			replaceHref, replaceHref.contains("/design-libraries/"));
	}

	private void _assertNestedFields(DisplayPageTemplate displayPageTemplate)
		throws Exception {

		FriendlyUrlHistory friendlyUrlHistory =
			displayPageTemplate.getFriendlyUrlHistory();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			GetterUtil.getString(friendlyUrlHistory.getFriendlyUrlPath_i18n()));

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		Map<Locale, String> friendlyURLMap = new HashMap<>();

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		if (_isPublished(layout)) {
			friendlyURLMap = layout.getFriendlyURLMap();
		}

		Assert.assertEquals(
			jsonObject.toString(), friendlyURLMap.size(), jsonObject.length());

		for (Map.Entry<Locale, String> entry : friendlyURLMap.entrySet()) {
			String key = LocaleUtil.toBCP47LanguageId(entry.getKey());

			JSONArray jsonArray = jsonObject.getJSONArray(key);

			Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());
			Assert.assertEquals(
				jsonArray.toString(), entry.getValue(), jsonArray.getString(0));
		}

		PageSpecificationsTestUtil.assertPageSpecifications(
			layout, displayPageTemplate.getPageSpecifications());
	}

	private void _assertPageSpecifications(
			DisplayPageTemplate displayPageTemplate,
			ContentPageSpecification draftContentPageSpecification,
			ContentPageSpecification publishedContentPageSpecification)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		PageSpecificationsTestUtil.assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			displayPageTemplate.getPageSpecifications(),
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			publishedContentPageSpecification.getStatus());
	}

	private void
			_assertPostSiteDisplayPageTemplatePageSpecificationProblemException(
				String expectedTitle,
				LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST", expectedTitle,
			() ->
				displayPageTemplateResource.
					postSiteDisplayPageTemplatePageSpecification(
						testGroup.getExternalReferenceCode(),
						layoutPageTemplateEntry.getExternalReferenceCode(),
						new ContentPageSpecification() {
							{
								setType(() -> Type.CONTENT_PAGE_SPECIFICATION);
							}
						}));
	}

	private void _assertProblemException(
			String expectedStatus, String expectedTitle,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(expectedStatus, problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
	}

	private void _assertStagingGroupDisplayPageTemplateThumbnail(
			DisplayPageTemplate displayPageTemplate,
			FileEntry liveGroupFileEntry)
		throws Exception {

		Group stagingGroup = testGroup.getStagingGroup();

		FileEntry stagingGroupFileEntry =
			_portletFileRepository.getPortletFileEntryByExternalReferenceCode(
				liveGroupFileEntry.getExternalReferenceCode(),
				stagingGroup.getGroupId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					stagingGroup.getGroupId());

		Assert.assertEquals(
			stagingGroupFileEntry.getFileEntryId(),
			layoutPageTemplateEntry.getPreviewFileEntryId());
	}

	private void _assertThumbnailFileEntryId(
			Boolean defaultValue,
			String displayPageTemplateExternalReferenceCode,
			String thumbnailExternalReferenceCode)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplate =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplateExternalReferenceCode,
					testGroup.getGroupId());

		long fileEntryId = 0;

		if (!defaultValue) {
			FileEntry fileEntry =
				_portletFileRepository.
					getPortletFileEntryByExternalReferenceCode(
						thumbnailExternalReferenceCode, testGroup.getGroupId());

			fileEntryId = fileEntry.getFileEntryId();
		}

		Assert.assertEquals(
			layoutPageTemplate.getPreviewFileEntryId(), fileEntryId);
	}

	private void _assertThumbnailURLReference(
			byte[] expectedBytes, String expectedExternalReferenceCode,
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		Assert.assertNotNull(expectedExternalReferenceCode);

		ThumbnailURLReference thumbnailURLReference =
			displayPageTemplate.getThumbnailURLReference();

		Assert.assertEquals(
			expectedExternalReferenceCode,
			thumbnailURLReference.getExternalReferenceCode());

		_assertThumbnailFileEntryId(
			false, displayPageTemplate.getExternalReferenceCode(),
			expectedExternalReferenceCode);

		FileEntry fileEntry =
			PortletFileRepositoryUtil.
				fetchPortletFileEntryByExternalReferenceCode(
					expectedExternalReferenceCode, testGroup.getGroupId());

		try (InputStream inputStream = fileEntry.getContentStream()) {
			Assert.assertArrayEquals(
				expectedBytes, StreamUtil.toByteArray(inputStream));
		}

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource("thumbnailURLReference");

		URL url = new URL(
			IdempotentRetryAssert.retryAssert(
				30, TimeUnit.SECONDS, 500, TimeUnit.MILLISECONDS,
				() -> {
					DisplayPageTemplate getDisplayPageTemplate =
						displayPageTemplateResource.getSiteDisplayPageTemplate(
							testGroup.getExternalReferenceCode(),
							displayPageTemplate.getExternalReferenceCode());

					ThumbnailURLReference getThumbnailURLReference =
						getDisplayPageTemplate.getThumbnailURLReference();

					Assert.assertNotNull(getThumbnailURLReference);

					return getThumbnailURLReference.getUrl();
				}));

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		Assert.assertEquals(
			HttpURLConnection.HTTP_OK, httpURLConnection.getResponseCode());

		String contentType = httpURLConnection.getContentType();

		Assert.assertTrue(contentType.startsWith("image/"));
	}

	private void _enableLocalStaging() throws Exception {
		_enableLocalStaging(testGroup);
	}

	private void _enableLocalStaging(Group group) throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.OFF)) {

			_stagingLocalService.enableLocalStaging(
				TestPropsValues.getUserId(), group, true, false,
				ServiceContextTestUtil.getServiceContext(
					group, TestPropsValues.getUserId()));
		}

		Assert.assertTrue(group.hasStagingGroup());
	}

	private ClassSubtypeReference _getClassSubtypeReference(String className) {
		if (className.equals(AssetCategory.class.getName())) {
			ClassSubtypeReference classSubtypeReference =
				new ClassSubtypeReference();

			classSubtypeReference.setClassName(className);

			return classSubtypeReference;
		}

		Assert.assertEquals(
			"com.liferay.journal.model.JournalArticle", className);

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class, className);

		List<InfoItemFormVariation> infoItemFormVariations = new ArrayList<>(
			infoItemFormVariationsProvider.getInfoItemFormVariations(
				testGroup.getGroupId()));

		Assert.assertFalse(infoItemFormVariations.isEmpty());

		infoItemFormVariations.sort(
			Comparator.comparing(InfoItemFormVariation::getKey));

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariations.get(0);

		List<InfoItemFormVariation> filteredInfoItemFormVariations =
			ListUtil.filter(
				infoItemFormVariations,
				curInfoItemFormVariation -> Objects.equals(
					curInfoItemFormVariation.getLabelInfoLocalizedValue(
					).getValue(),
					"Test Structure"));

		if (!filteredInfoItemFormVariations.isEmpty()) {
			infoItemFormVariation = filteredInfoItemFormVariations.get(0);
		}

		return _getClassSubtypeReference(
			className, infoItemFormVariation.getExternalReferenceCode(),
			infoItemFormVariationsProvider.getInfoItemFormVariationClassName());
	}

	private ClassSubtypeReference _getClassSubtypeReference(
		String className, String externalReferenceCode,
		String subtypeClassName) {

		ClassSubtypeReference classSubtypeReference =
			new ClassSubtypeReference();

		classSubtypeReference.setClassName(className);

		ItemExternalReference itemExternalReference =
			new ItemExternalReference();

		itemExternalReference.setClassName(subtypeClassName);
		itemExternalReference.setExternalReferenceCode(externalReferenceCode);

		classSubtypeReference.setSubTypeExternalReference(
			itemExternalReference);

		return classSubtypeReference;
	}

	private String _getDesignLibraryExternalReferenceCode() throws Exception {
		if (_designLibraryGroup == null) {
			_designLibraryGroup = _addDesignLibraryGroup();
		}

		return _designLibraryGroup.getExternalReferenceCode();
	}

	private DisplayPageTemplate _getDisplayPageTemplate(
		List<DisplayPageTemplate> displayPageTemplates,
		String externalReferenceCode) {

		for (DisplayPageTemplate displayPageTemplate : displayPageTemplates) {
			if (Objects.equals(
					displayPageTemplate.getExternalReferenceCode(),
					externalReferenceCode)) {

				return displayPageTemplate;
			}
		}

		return null;
	}

	private DisplayPageTemplateResource _getDisplayPageTemplateResource(
			String nestedFields)
		throws Exception {

		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return DisplayPageTemplateResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", nestedFields
		).build();
	}

	private DisplayPageTemplate _getDisplayPageTemplateWithPageElements(
			PageElement[] draftPageElements,
			PageElement[] publishedPageElements)
		throws Exception {

		DisplayPageTemplate displayPageTemplate = _randomDisplayPageTemplate(
			Boolean.TRUE);

		displayPageTemplate.setContentTypeReference(
			_getClassSubtypeReference(
				"com.liferay.journal.model.JournalArticle"));

		String draftContentPageSpecificationExternalReferenceCode =
			RandomTestUtil.randomString();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), PageSpecification.Status.DRAFT);

		draftContentPageSpecification.setPageExperiences(
			PageExperiencesTestUtil.getDefaultPageExperiences(
				draftPageElements,
				draftContentPageSpecificationExternalReferenceCode));

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), PageSpecification.Status.APPROVED);

		publishedContentPageSpecification.setExternalReferenceCode(
			displayPageTemplate.getExternalReferenceCode());
		publishedContentPageSpecification.setPageExperiences(
			PageExperiencesTestUtil.getDefaultPageExperiences(
				publishedPageElements,
				displayPageTemplate.getExternalReferenceCode()));

		displayPageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		return displayPageTemplate;
	}

	private String _getIrrelevantDesignLibraryExternalReferenceCode()
		throws Exception {

		if (_irrelevantDesignLibraryGroup == null) {
			_irrelevantDesignLibraryGroup = _addDesignLibraryGroup();
		}

		return _irrelevantDesignLibraryGroup.getExternalReferenceCode();
	}

	private LayoutDisplayPageObjectProvider<?>
		_getLayoutDisplayPageObjectProvider(JournalArticle journalArticle) {

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					journalArticle.getCompanyId(),
					JournalArticle.class.getName());

		return layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
			new InfoItemReference(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey()));
	}

	private String _getLayoutPageTemplateCollectionExternalReferenceCode(
			long groupId)
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), groupId,
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					ServiceContextTestUtil.getServiceContext(
						groupId, TestPropsValues.getUserId()));

		return layoutPageTemplateCollection.getExternalReferenceCode();
	}

	private ClassSubtypeReference _getRandomClassSubtypeReference() {
		if (RandomTestUtil.randomBoolean()) {
			return _getClassSubtypeReference(AssetCategory.class.getName());
		}

		return _getClassSubtypeReference(
			"com.liferay.journal.model.JournalArticle");
	}

	private String _getRenderLayoutHTML(
			JournalArticle journalArticle, Layout layout)
		throws Exception {

		return ContentLayoutTestUtil.getRenderLayoutHTML(
			HashMapBuilder.<String, Object>put(
				InfoDisplayWebKeys.INFO_ITEM_DETAILS,
				new InfoItemDetails(
					new InfoItemClassDetails(
						"com.liferay.journal.model.JournalArticle"),
					new InfoItemReference(
						"com.liferay.journal.model.JournalArticle",
						new ERCInfoItemIdentifier(
							journalArticle.getExternalReferenceCode(),
							"L_GLOBAL")))
			).put(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
				_getLayoutDisplayPageObjectProvider(journalArticle)
			).build(),
			layout, _layoutServiceContextHelper, _layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));
	}

	private boolean _isPublished(Layout layout) {
		Layout draftLayout = layout.fetchDraftLayout();

		return GetterUtil.getBoolean(
			draftLayout.getTypeSettingsProperty("published"));
	}

	private DisplayPageTemplate
			_postSiteDisplayPageTemplateAndAssertThumbnailURLReference(
				byte[] expectedBytes, String expectedExternalReferenceCode,
				ThumbnailURLReference thumbnailURLReference)
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		displayPageTemplate.setThumbnailURLReference(thumbnailURLReference);

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource("thumbnailURLReference");

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

		if (expectedExternalReferenceCode == null) {
			ThumbnailURLReference postThumbnailURLReference =
				postDisplayPageTemplate.getThumbnailURLReference();

			expectedExternalReferenceCode =
				postThumbnailURLReference.getExternalReferenceCode();
		}

		_assertThumbnailURLReference(
			expectedBytes, expectedExternalReferenceCode,
			postDisplayPageTemplate);

		return postDisplayPageTemplate;
	}

	private DisplayPageTemplate
			_putSiteDisplayPageTemplateAndAssertThumbnailURLReference(
				byte[] expectedBytes, String expectedExternalReferenceCode,
				DisplayPageTemplate displayPageTemplate,
				ThumbnailURLReference thumbnailURLReference)
		throws Exception {

		displayPageTemplate.setThumbnailURLReference(thumbnailURLReference);

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource("thumbnailURLReference");

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		if (expectedExternalReferenceCode == null) {
			ThumbnailURLReference putThumbnailURLReference =
				putDisplayPageTemplate.getThumbnailURLReference();

			expectedExternalReferenceCode =
				putThumbnailURLReference.getExternalReferenceCode();
		}

		_assertThumbnailURLReference(
			expectedBytes, expectedExternalReferenceCode,
			putDisplayPageTemplate);

		return putDisplayPageTemplate;
	}

	private JournalArticle _randomCompanyGroupJournalArticle()
		throws Exception {

		ServiceContext companyGroupServiceContext =
			ServiceContextTestUtil.getServiceContext(testCompany.getGroupId());

		return JournalTestUtil.addArticle(
			testCompany.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			companyGroupServiceContext);
	}

	private DisplayPageTemplate _randomDisplayPageTemplate(
			Boolean markedAsDefault)
		throws Exception {

		return _randomDisplayPageTemplate(
			_getRandomClassSubtypeReference(), markedAsDefault);
	}

	private DisplayPageTemplate _randomDisplayPageTemplate(
			ClassSubtypeReference classSubtypeReference,
			Boolean markedAsDefault)
		throws Exception {

		DisplayPageTemplate displayPageTemplate =
			super.randomDisplayPageTemplate();

		displayPageTemplate.setContentTypeReference(classSubtypeReference);
		displayPageTemplate.setDisplayPageTemplateSettings(
			this::_randomDisplayPageTemplateSettings);
		displayPageTemplate.setFriendlyUrlPath_i18n(
			() -> HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).build());
		displayPageTemplate.setMarkedAsDefault(() -> markedAsDefault);

		return displayPageTemplate;
	}

	private DisplayPageTemplateSettings _randomDisplayPageTemplateSettings() {
		DisplayPageTemplateSettings displayPageTemplateSettings =
			new DisplayPageTemplateSettings();

		displayPageTemplateSettings.setOpenGraphSettings(
			new DisplayPageTemplateOpenGraphSettings() {
				{
					setDescriptionTemplate(RandomTestUtil.randomString());
					setImageAltTemplate(RandomTestUtil.randomString());
					setImageTemplate(RandomTestUtil.randomString());
					setTitleTemplate(RandomTestUtil.randomString());
				}
			});

		SitemapSettings randomSitemapSettings = new SitemapSettings() {
			{
				setChangeFrequency(
					RandomTestUtil.randomEnum(ChangeFrequency.class));
				setInclude(RandomTestUtil.randomBoolean());
				setPagePriority(RandomTestUtil.randomDouble());
			}
		};

		displayPageTemplateSettings.setSeoSettings(
			new DisplayPageTemplateSEOSettings() {
				{
					setDescriptionTemplate(RandomTestUtil.randomString());
					setHtmlTitleTemplate(RandomTestUtil.randomString());
					setRobots_i18n(
						LocalizedMapUtil.getI18nMap(
							RandomTestUtil.randomLocaleStringMap()));
					setSitemapSettings(randomSitemapSettings);
				}
			});

		return displayPageTemplateSettings;
	}

	private void
			_testCreatingDisplayPageTemplateFolderWithLazyReferencingEnabled(
				UnsafeFunction
					<DisplayPageTemplateFolder, DisplayPageTemplate, Exception>
						unsafeFunction)
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			new DisplayPageTemplateFolder() {
				{
					setDescription(RandomTestUtil::randomString);
					setExternalReferenceCode(RandomTestUtil::randomString);
					setName(RandomTestUtil::randomString);
				}
			};

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			new DisplayPageTemplateFolder() {
				{
					setDescription(RandomTestUtil::randomString);
					setExternalReferenceCode(RandomTestUtil::randomString);
					setName(RandomTestUtil::randomString);
				}
			};

		displayPageTemplateFolder.setParentDisplayPageTemplateFolder(
			parentDisplayPageTemplateFolder);
		displayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				parentDisplayPageTemplateFolder.getExternalReferenceCode());

		try {
			unsafeFunction.apply(displayPageTemplateFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			if (_log.isDebugEnabled()) {
				_log.debug(problemException);
			}

			Problem problem = problemException.getProblem();

			Assert.assertEquals("IllegalArgumentException", problem.getType());
		}

		Assert.assertNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollectionByExternalReferenceCode(
					displayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId()));
		Assert.assertNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollectionByExternalReferenceCode(
					parentDisplayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId()));

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			DisplayPageTemplate displayPageTemplate = unsafeFunction.apply(
				displayPageTemplateFolder);

			DisplayPageTemplateFolder addedDisplayPageTemplateFolder =
				displayPageTemplate.getParentFolder();

			Assert.assertEquals(
				displayPageTemplateFolder.getExternalReferenceCode(),
				addedDisplayPageTemplateFolder.getExternalReferenceCode());
			Assert.assertEquals(
				parentDisplayPageTemplateFolder.getExternalReferenceCode(),
				addedDisplayPageTemplateFolder.
					getParentDisplayPageTemplateFolderExternalReferenceCode());

			Assert.assertNotNull(
				_layoutPageTemplateCollectionLocalService.
					fetchLayoutPageTemplateCollectionByExternalReferenceCode(
						displayPageTemplateFolder.getExternalReferenceCode(),
						testGroup.getGroupId()));
			Assert.assertNotNull(
				_layoutPageTemplateCollectionLocalService.
					fetchLayoutPageTemplateCollectionByExternalReferenceCode(
						parentDisplayPageTemplateFolder.
							getExternalReferenceCode(),
						testGroup.getGroupId()));
		}
	}

	private void _testGetSiteDisplayPageTemplate(
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		DisplayPageTemplate getDisplayPageTemplate =
			displayPageTemplateResource.getSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode());

		assertEquals(displayPageTemplate, getDisplayPageTemplate);
		assertValid(getDisplayPageTemplate);
	}

	private void _testGetSiteDisplayPageTemplateWithNestedFields(
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource(
				"friendlyUrlHistory,pageSpecifications");

		_assertNestedFields(
			displayPageTemplateResource.getSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode()));
	}

	private void _testGetSiteDisplayPageTemplateWithPageElementsWithTemplateEntries()
		throws Exception {

		FragmentEntry fragmentEntry =
			FragmentEntryTestUtil.
				addCompanyGroupFragmentEntryWithTextEditable();
		JournalArticle journalArticle = _randomCompanyGroupJournalArticle();

		DisplayPageTemplate displayPageTemplate =
			_getDisplayPageTemplateWithPageElements(
				PageElementsTestUtil.getDisplayPageTemplatePageElements(
					testCompany, fragmentEntry.getFragmentEntryKey(),
					journalArticle, testGroup.getGroupId()),
				PageElementsTestUtil.getDisplayPageTemplatePageElements(
					testCompany, fragmentEntry.getFragmentEntryKey(),
					journalArticle, testGroup.getGroupId()));

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource("pageSpecifications");

		DisplayPageTemplate getDisplayPageTemplate =
			displayPageTemplateResource.getSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				postDisplayPageTemplate.getExternalReferenceCode());

		assertEquals(displayPageTemplate, getDisplayPageTemplate);
		assertValid(getDisplayPageTemplate);

		PageElementsTestUtil.assertFieldKeysWithTemplateEntries(
			getDisplayPageTemplate.getPageSpecifications(),
			displayPageTemplate.getPageSpecifications());
	}

	private void _testGetSiteDisplayPageTemplatesPageWithPageSpecificationsAsNestedFields()
		throws Exception {

		Page<DisplayPageTemplate> page =
			displayPageTemplateResource.getSiteDisplayPageTemplatesPage(
				testGroup.getExternalReferenceCode(), null, null, null, null,
				null);

		long totalCount = page.getTotalCount();

		DisplayPageTemplate displayPageTemplate =
			testGetSiteDisplayPageTemplatesPage_addDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplate());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertFalse(_isPublished(layout));

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource(
				"friendlyUrlHistory,pageSpecifications");

		page = displayPageTemplateResource.getSiteDisplayPageTemplatesPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		_assertNestedFields(
			_getDisplayPageTemplate(
				(List<DisplayPageTemplate>)page.getItems(),
				displayPageTemplate.getExternalReferenceCode()));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Assert.assertTrue(_isPublished(layout));

		page = displayPageTemplateResource.getSiteDisplayPageTemplatesPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		_assertNestedFields(
			_getDisplayPageTemplate(
				(List<DisplayPageTemplate>)page.getItems(),
				displayPageTemplate.getExternalReferenceCode()));
	}

	private void _testGetSiteDisplayPageTemplatesPageWithThumbnailAsNestedField()
		throws Exception {

		DisplayPageTemplate randomDisplayPageTemplate =
			randomDisplayPageTemplate();

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		randomDisplayPageTemplate.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, null));

		DisplayPageTemplate postDisplayPageTemplate =
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate);

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource("thumbnailURLReference");

		IdempotentRetryAssert.retryAssert(
			30, TimeUnit.SECONDS, 500, TimeUnit.MILLISECONDS,
			() -> {
				DisplayPageTemplate getDisplayPageTemplate =
					displayPageTemplateResource.getSiteDisplayPageTemplate(
						testGroup.getExternalReferenceCode(),
						postDisplayPageTemplate.getExternalReferenceCode());

				ThumbnailURLReference thumbnailURLReference =
					getDisplayPageTemplate.getThumbnailURLReference();

				Assert.assertNotNull(thumbnailURLReference);

				return thumbnailURLReference.getUrl();
			});

		Page<DisplayPageTemplate> page =
			displayPageTemplateResource.getSiteDisplayPageTemplatesPage(
				testGroup.getExternalReferenceCode(), null, null, null, null,
				null);

		for (DisplayPageTemplate displayPageTemplate : page.getItems()) {
			if (StringUtil.equals(
					displayPageTemplate.getExternalReferenceCode(),
					postDisplayPageTemplate.getExternalReferenceCode())) {

				ThumbnailURLReference thumbnail =
					displayPageTemplate.getThumbnailURLReference();

				_assertThumbnailURLReference(
					_thumbnail1Bytes, thumbnail.getExternalReferenceCode(),
					displayPageTemplate);
			}
			else {
				Assert.assertNull(
					displayPageTemplate.getThumbnailURLReference());
			}
		}
	}

	private void _testPatchSiteDisplayPageTemplate(
			DisplayPageTemplate expectedDisplayPageTemplate,
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		DisplayPageTemplate patchDisplayPageTemplate =
			displayPageTemplateResource.patchSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				expectedDisplayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		assertEquals(expectedDisplayPageTemplate, patchDisplayPageTemplate);
		assertValid(patchDisplayPageTemplate);

		Assert.assertEquals(
			expectedDisplayPageTemplate.getContentTypeReference(),
			patchDisplayPageTemplate.getContentTypeReference());
		Assert.assertEquals(
			expectedDisplayPageTemplate.getMarkedAsDefault(),
			patchDisplayPageTemplate.getMarkedAsDefault());

		DisplayPageTemplateFolder displayPageTemplateFolder1 =
			expectedDisplayPageTemplate.getParentFolder();
		DisplayPageTemplateFolder displayPageTemplateFolder2 =
			patchDisplayPageTemplate.getParentFolder();

		if ((displayPageTemplateFolder1 != null) &&
			(displayPageTemplateFolder2 != null)) {

			Assert.assertEquals(
				displayPageTemplateFolder1.getExternalReferenceCode(),
				displayPageTemplateFolder2.getExternalReferenceCode());
		}
		else {
			Assert.assertEquals(
				displayPageTemplateFolder1, displayPageTemplateFolder2);
		}
	}

	private void _testPatchSiteDisplayPageTemplateWithPageSpecifications()
		throws Exception {

		_testPatchSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPatchSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPatchSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPatchSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
	}

	private void _testPatchSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status newDraftLayoutStatus,
			PageSpecification.Status newPublishedLayoutStatus,
			PageSpecification.Status oldDraftLayoutStatus,
			PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), oldPublishedLayoutStatus);

		displayPageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource(
				"friendlyUrlHistory,pageSpecifications");

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

		_assertPageSpecifications(
			postDisplayPageTemplate, draftContentPageSpecification,
			publishedContentPageSpecification);

		_assertPageSpecifications(
			displayPageTemplateResource.patchSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				new DisplayPageTemplate()),
			draftContentPageSpecification, publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			displayPageTemplateResource.patchSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				new DisplayPageTemplate() {
					{
						setPageSpecifications(
							() -> new PageSpecification[] {
								publishedContentPageSpecification,
								draftContentPageSpecification
							});
					}
				}),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

	private void _testPatchSiteDisplayPageTemplateWithThumbnail()
		throws Exception {

		DisplayPageTemplate displayPageTemplate1 = randomDisplayPageTemplate();

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		displayPageTemplate1.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, RandomTestUtil.randomString()));

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource("thumbnailURLReference");

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate1);

		Assert.assertEquals(
			displayPageTemplate1.getKey(), postDisplayPageTemplate.getKey());

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry.getExternalReferenceCode(),
			postDisplayPageTemplate);

		FileEntry newFileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		displayPageTemplate1.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				newFileEntry, RandomTestUtil.randomString()));

		DisplayPageTemplate patchDisplayPageTemplate =
			displayPageTemplateResource.patchSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate1.getExternalReferenceCode(),
				displayPageTemplate1);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, newFileEntry.getExternalReferenceCode(),
			patchDisplayPageTemplate);

		DisplayPageTemplate displayPageTemplate2 = randomDisplayPageTemplate();

		ThumbnailURLReference thumbnailURLReference =
			ThumbnailURLReferenceUtil.getRandomThumbnailURLReference();

		displayPageTemplate2.setThumbnailURLReference(thumbnailURLReference);

		try {
			displayPageTemplateResource.patchSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate1.getExternalReferenceCode(),
				displayPageTemplate2);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Unable to download file from " +
					thumbnailURLReference.getUrl(),
				problem.getTitle());
		}
	}

	private void
			_testPostDisplayPageTemplateThumbnailURLReferenceProblemException(
				String expectedTitle, String externalReferenceCode, String url)
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setUrl(url);

		displayPageTemplate.setThumbnailURLReference(thumbnailURLReference);

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource("thumbnailURLReference");

		try {
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
	}

	private void _testPostSiteDisplayPageTemplateWithKey() throws Exception {
		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		displayPageTemplate.setKey(StringPool.BLANK);

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

		Assert.assertTrue(
			Validator.isNotNull(postDisplayPageTemplate.getKey()));

		displayPageTemplate = randomDisplayPageTemplate();

		postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getKey(), postDisplayPageTemplate.getKey());
	}

	private void _testPostSiteDisplayPageTemplateWithMarkedAsDefault()
		throws Exception {

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplate());

		Assert.assertFalse(postDisplayPageTemplate.getMarkedAsDefault());

		postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				_randomDisplayPageTemplate(null));

		Assert.assertFalse(postDisplayPageTemplate.getMarkedAsDefault());

		DisplayPageTemplate displayPageTemplate = _randomDisplayPageTemplate(
			Boolean.TRUE);

		String draftContentPageSpecificationExternalReferenceCode =
			RandomTestUtil.randomString();

		displayPageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				PageSpecificationsTestUtil.getContentPageSpecification(
					draftContentPageSpecificationExternalReferenceCode,
					testGroup.getGroupId(), PageSpecification.Status.APPROVED),
				PageSpecificationsTestUtil.getContentPageSpecification(
					draftContentPageSpecificationExternalReferenceCode, null,
					null, null, testGroup.getGroupId(),
					PageSpecification.Status.DRAFT)
			});

		postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

		Assert.assertTrue(postDisplayPageTemplate.getMarkedAsDefault());

		_assertProblemException(
			"CONFLICT",
			"The default display page template must be published first.",
			() -> displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				_randomDisplayPageTemplate(Boolean.TRUE)));
	}

	private void _testPostSiteDisplayPageTemplateWithPageElementsWithTemplateEntries()
		throws Exception {

		FragmentEntry fragmentEntry =
			FragmentEntryTestUtil.
				addCompanyGroupFragmentEntryWithTextEditable();

		JournalArticle journalArticle = _randomCompanyGroupJournalArticle();

		DisplayPageTemplate displayPageTemplate =
			_getDisplayPageTemplateWithPageElements(
				PageElementsTestUtil.getDisplayPageTemplatePageElements(
					testCompany, fragmentEntry.getFragmentEntryKey(),
					journalArticle, testGroup.getGroupId()),
				PageElementsTestUtil.getDisplayPageTemplatePageElements(
					testCompany, fragmentEntry.getFragmentEntryKey(),
					journalArticle, testGroup.getGroupId()));

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

		assertEquals(displayPageTemplate, postDisplayPageTemplate);
		assertValid(postDisplayPageTemplate);

		PageElementsTestUtil.assertRenderedLayoutHTMLWithTemplateEntries(
			_getRenderLayoutHTML(
				journalArticle,
				_layoutLocalService.getLayoutByExternalReferenceCode(
					postDisplayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId())));
	}

	private void _testPostSiteDisplayPageTemplateWithPageSpecifications()
		throws Exception {

		_testPostSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPostSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPostSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED);
		_testPostSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT);
		_testPostSiteDisplayPageTemplateWithPageSpecificationsNull();
		_testPostSiteDisplayPageTemplateWithPageSpecificationsWithSettings();
	}

	private void _testPostSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status draftLayoutStatus,
			PageSpecification.Status publishedLayoutStatus)
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), draftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), publishedLayoutStatus);

		displayPageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource(
				"friendlyUrlHistory,pageSpecifications");

		_assertPageSpecifications(
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

	private void _testPostSiteDisplayPageTemplateWithPageSpecificationsNull()
		throws Exception {

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource(
				"friendlyUrlHistory,pageSpecifications");

		DisplayPageTemplate displayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplate());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		PageSpecificationsTestUtil.assertPageSpecifications(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			displayPageTemplate.getPageSpecifications());
	}

	private void _testPostSiteDisplayPageTemplateWithPageSpecificationsWithSettings()
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), PageSpecification.Status.DRAFT);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		draftContentPageSpecification.setSettings(
			SettingsTestUtil.getSettings(
				FavIcon.FavIconType.CLIENT_EXTENSION, serviceContext));

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), PageSpecification.Status.APPROVED);

		publishedContentPageSpecification.setSettings(
			SettingsTestUtil.getSettings(
				FavIcon.FavIconType.ITEM_EXTERNAL_REFERENCE, serviceContext));

		displayPageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource(
				"friendlyUrlHistory,pageSpecifications");

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.client.extension.type.internal.manager." +
					"CETManagerImpl",
				LoggerTestUtil.WARN);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.headless.admin.site.internal.util.LogUtil",
				LoggerTestUtil.WARN)) {

			_assertPageSpecifications(
				displayPageTemplateResource.postSiteDisplayPageTemplate(
					testGroup.getExternalReferenceCode(), displayPageTemplate),
				draftContentPageSpecification,
				publishedContentPageSpecification);

			List<LogEntry> logEntries = logCapture2.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 4, logEntries.size());

			for (LogEntry logEntry : logEntries) {
				String message = logEntry.getMessage();

				Assert.assertTrue(
					message,
					message.startsWith(
						"Optional reference generated for missing"));
			}
		}
	}

	private void _testPostSiteDisplayPageTemplateWithParentFolder()
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), testGroup.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					ServiceContextTestUtil.getServiceContext(
						testGroup, TestPropsValues.getUserId()));

		displayPageTemplate.setParentFolder(
			() -> new DisplayPageTemplateFolder() {
				{
					setExternalReferenceCode(
						layoutPageTemplateCollection.
							getExternalReferenceCode());
				}
			});

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

		assertEquals(displayPageTemplate, postDisplayPageTemplate);
		assertValid(postDisplayPageTemplate);

		DisplayPageTemplateFolder displayPageTemplateFolder =
			displayPageTemplate.getParentFolder();
		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			postDisplayPageTemplate.getParentFolder();

		Assert.assertEquals(
			displayPageTemplateFolder.getExternalReferenceCode(),
			postDisplayPageTemplateFolder.getExternalReferenceCode());

		_testCreatingDisplayPageTemplateFolderWithLazyReferencingEnabled(
			nonexistingDisplayPageTemplateFolder -> {
				DisplayPageTemplate randomDisplayPageTemplate =
					randomDisplayPageTemplate();

				randomDisplayPageTemplate.setParentFolder(
					nonexistingDisplayPageTemplateFolder);

				return displayPageTemplateResource.postSiteDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					randomDisplayPageTemplate);
			});
	}

	private void _testPostSiteDisplayPageTemplateWithThumbnail()
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		displayPageTemplate.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, null));

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource("thumbnailURLReference");

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry.getExternalReferenceCode(),
			postDisplayPageTemplate);

		displayPageTemplate = randomDisplayPageTemplate();

		ThumbnailURLReference thumbnailURLReference =
			ThumbnailURLReferenceUtil.getRandomThumbnailURLReference();

		displayPageTemplate.setThumbnailURLReference(thumbnailURLReference);

		try {
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				displayPageTemplate);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Unable to download file from " +
					thumbnailURLReference.getUrl(),
				problem.getTitle());
		}
	}

	private void _testPostSiteDisplayPageTemplateWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64()
		throws Exception {

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		String externalReferenceCode = fileEntry.getExternalReferenceCode();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail2Base64);

		_postSiteDisplayPageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteDisplayPageTemplateWithThumbnailURLReferenceExternalReferenceCodeEmptyAndFileBase64()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(StringPool.BLANK);
		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSiteDisplayPageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSiteDisplayPageTemplateWithThumbnailURLReferenceExternalReferenceCodeNullAndFileBase64()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSiteDisplayPageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSiteDisplayPageTemplateWithThumbnailURLReferenceExternalReferenceCodeNullAndURL()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setUrl(_thumbnail1URL);

		_postSiteDisplayPageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSiteDisplayPageTemplateWithThumbnailURLReferenceFileBase64()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSiteDisplayPageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteDisplayPageTemplateWithThumbnailURLReferenceFileBase64AndURL()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail1Base64);
		thumbnailURLReference.setUrl(_thumbnail2URL);

		_postSiteDisplayPageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteDisplayPageTemplateWithThumbnailURLReferenceNonexistingProblemException()
		throws Exception {

		_testPostDisplayPageTemplateThumbnailURLReferenceProblemException(
			"Unable to resolve file", RandomTestUtil.randomString(), null);
	}

	private void _testPostSiteDisplayPageTemplateWithThumbnailURLReferenceURL()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setUrl(_thumbnail1URL);

		_postSiteDisplayPageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteDisplayPageTemplateWithThumbnailURLReferenceURLUnsupportedProtocolProblemException()
		throws Exception {

		String url =
			"ftp://invalid.example.test/" + RandomTestUtil.randomString();

		_testPostDisplayPageTemplateThumbnailURLReferenceProblemException(
			"Unable to download file from " + url +
				" because of unsupported protocol ftp",
			RandomTestUtil.randomString(), url);
	}

	private void _testPutSiteDisplayPageTemplate(
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		assertEquals(displayPageTemplate, putDisplayPageTemplate);
		assertValid(putDisplayPageTemplate);

		Assert.assertNull(putDisplayPageTemplate.getParentFolder());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), testGroup.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					ServiceContextTestUtil.getServiceContext(
						testGroup, TestPropsValues.getUserId()));

		displayPageTemplate.setParentFolder(
			new DisplayPageTemplateFolder() {
				{
					setExternalReferenceCode(
						layoutPageTemplateCollection.
							getExternalReferenceCode());
				}
			});

		putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		assertEquals(displayPageTemplate, putDisplayPageTemplate);
		assertValid(putDisplayPageTemplate);

		DisplayPageTemplateFolder displayPageTemplateFolder =
			putDisplayPageTemplate.getParentFolder();

		Assert.assertEquals(
			layoutPageTemplateCollection.getExternalReferenceCode(),
			displayPageTemplateFolder.getExternalReferenceCode());

		_testCreatingDisplayPageTemplateFolderWithLazyReferencingEnabled(
			nonexistingDisplayPageTemplateFolder -> {
				DisplayPageTemplate postDisplayPageTemplate =
					testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
						randomDisplayPageTemplate());

				postDisplayPageTemplate.setParentFolder(
					nonexistingDisplayPageTemplateFolder);

				return displayPageTemplateResource.putSiteDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					postDisplayPageTemplate.getExternalReferenceCode(),
					postDisplayPageTemplate);
			});
	}

	private void _testPutSiteDisplayPageTemplateContentTypeReference()
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		displayPageTemplate.setContentTypeReference(
			_getClassSubtypeReference(AssetCategory.class.getName()));

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getContentTypeReference(),
			putDisplayPageTemplate.getContentTypeReference());

		displayPageTemplate.setContentTypeReference(
			_getClassSubtypeReference(
				"com.liferay.journal.model.JournalArticle"));

		putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getContentTypeReference(),
			putDisplayPageTemplate.getContentTypeReference());
	}

	private void _testPutSiteDisplayPageTemplateMarkedAsDefault()
		throws Exception {

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource("pageSpecifications");

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		Assert.assertFalse(putDisplayPageTemplate.getMarkedAsDefault());

		putDisplayPageTemplate.setMarkedAsDefault(() -> null);

		putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				putDisplayPageTemplate.getExternalReferenceCode(),
				putDisplayPageTemplate);

		Assert.assertFalse(putDisplayPageTemplate.getMarkedAsDefault());

		putDisplayPageTemplate.setMarkedAsDefault(true);

		for (PageSpecification pageSpecification :
				putDisplayPageTemplate.getPageSpecifications()) {

			pageSpecification.setStatus(PageSpecification.Status.APPROVED);
		}

		putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				putDisplayPageTemplate);

		Assert.assertTrue(putDisplayPageTemplate.getMarkedAsDefault());
	}

	private void _testPutSiteDisplayPageTemplateSettings() throws Exception {
		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getDisplayPageTemplateSettings(),
			putDisplayPageTemplate.getDisplayPageTemplateSettings());

		displayPageTemplate.setDisplayPageTemplateSettings(
			_randomDisplayPageTemplateSettings());

		putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getDisplayPageTemplateSettings(),
			putDisplayPageTemplate.getDisplayPageTemplateSettings());

		displayPageTemplate.setDisplayPageTemplateSettings(() -> null);

		putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		Assert.assertEquals(
			new DisplayPageTemplateSettings() {
				{
					setOpenGraphSettings(
						new DisplayPageTemplateOpenGraphSettings() {
							{
								setSeoSettings(
									new DisplayPageTemplateSEOSettings() {
										{
											setSitemapSettings(
												new SitemapSettings());
										}
									});
							}
						});
				}
			},
			putDisplayPageTemplate.getDisplayPageTemplateSettings());
	}

	private void _testPutSiteDisplayPageTemplateWithMissingOptionalReference(
			int count, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.headless.admin.site.internal.util.LogUtil",
				LoggerTestUtil.WARN)) {

			unsafeRunnable.run();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(
				logEntries.toString(), count, logEntries.size());

			for (LogEntry logEntry : logEntries) {
				String message = logEntry.getMessage();

				Assert.assertTrue(
					message,
					message.startsWith(
						"Optional reference generated for missing"));
			}
		}
	}

	private void _testPutSiteDisplayPageTemplateWithPageSpecifications()
		throws Exception {

		_testPutSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPutSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPutSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPutSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
	}

	private void _testPutSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status newDraftLayoutStatus,
			PageSpecification.Status newPublishedLayoutStatus,
			PageSpecification.Status oldDraftLayoutStatus,
			PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), oldPublishedLayoutStatus);

		displayPageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource(
				"friendlyUrlHistory,pageSpecifications");

		_assertPageSpecifications(
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate),
			draftContentPageSpecification, publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

	private void _testPutSiteDisplayPageTemplateWithSubtype() throws Exception {
		_testPutSiteDisplayPageTemplateWithMissingOptionalReference(
			1,
			() -> {
				String externalReferenceCode = RandomTestUtil.randomString();

				_testPutSiteDisplayPageTemplateWithSubtype(
					JournalArticle.class.getName(), -1, externalReferenceCode,
					_getClassSubtypeReference(
						JournalArticle.class.getName(), externalReferenceCode,
						DDMStructure.class.getName()));
			});
		_testPutSiteDisplayPageTemplateWithMissingOptionalReference(
			2,
			() -> {
				String className = RandomTestUtil.randomString();
				String externalReferenceCode = RandomTestUtil.randomString();

				_testPutSiteDisplayPageTemplateWithSubtype(
					className, -1, externalReferenceCode,
					_getClassSubtypeReference(
						className, externalReferenceCode, null));
			});

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			testGroup.getGroupId(), JournalArticle.class.getName());
		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				JournalArticle.class.getName());

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariationsProvider.getInfoItemFormVariation(
				testGroup.getGroupId(), ddmStructure.getStructureKey(),
				String.valueOf(ddmStructure.getStructureId()));

		_testPutSiteDisplayPageTemplateWithSubtype(
			JournalArticle.class.getName(), ddmStructure.getStructureId(),
			infoItemFormVariation.getExternalReferenceCode(),
			_getClassSubtypeReference(
				JournalArticle.class.getName(),
				infoItemFormVariation.getExternalReferenceCode(),
				DDMStructure.class.getName()));
	}

	private void _testPutSiteDisplayPageTemplateWithSubtype(
			String className, long classTypeId, String classTypeKey,
			ClassSubtypeReference classSubtypeReference)
		throws Exception {

		DisplayPageTemplate displayPageTemplate = _randomDisplayPageTemplate(
			classSubtypeReference, Boolean.FALSE);

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		Assert.assertEquals(
			classSubtypeReference,
			putDisplayPageTemplate.getContentTypeReference());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		Assert.assertEquals(className, layoutPageTemplateEntry.getClassName());
		Assert.assertEquals(
			classTypeId, layoutPageTemplateEntry.getClassTypeId());
		Assert.assertEquals(
			classTypeKey, layoutPageTemplateEntry.getClassTypeKey());
	}

	private void _testPutSiteDisplayPageTemplateWithThumbnail()
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		displayPageTemplate.setExternalReferenceCode(
			RandomTestUtil.randomString());

		FileEntry fileEntry1 = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		displayPageTemplate.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry1, null));

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource("thumbnailURLReference");

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry1.getExternalReferenceCode(),
			putDisplayPageTemplate);

		FileEntry fileEntry2 = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		putDisplayPageTemplate.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry2, null));

		putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				putDisplayPageTemplate.getExternalReferenceCode(),
				putDisplayPageTemplate);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry2.getExternalReferenceCode(),
			putDisplayPageTemplate);

		putDisplayPageTemplate.setThumbnailURLReference(() -> null);

		putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				putDisplayPageTemplate.getExternalReferenceCode(),
				putDisplayPageTemplate);

		_assertThumbnailFileEntryId(
			true, putDisplayPageTemplate.getExternalReferenceCode(), null);

		displayPageTemplate = randomDisplayPageTemplate();

		ThumbnailURLReference thumbnailURLReference =
			ThumbnailURLReferenceUtil.getRandomThumbnailURLReference();

		displayPageTemplate.setThumbnailURLReference(thumbnailURLReference);

		try {
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				putDisplayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Unable to download file from " +
					thumbnailURLReference.getUrl(),
				problem.getTitle());
		}
	}

	private void _testPutSiteDisplayPageTemplateWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64()
		throws Exception {

		DisplayPageTemplate postDisplayPageTemplate =
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		Assert.assertNull(postDisplayPageTemplate.getThumbnailURLReference());

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		String externalReferenceCode = fileEntry.getExternalReferenceCode();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail2Base64);

		_putSiteDisplayPageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, postDisplayPageTemplate,
			thumbnailURLReference);
	}

	private void _testPutSiteDisplayPageTemplateWithThumbnailURLReferenceFileBase64()
		throws Exception {

		DisplayPageTemplate postDisplayPageTemplate =
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		Assert.assertNull(postDisplayPageTemplate.getThumbnailURLReference());

		ThumbnailURLReference thumbnailURLReference1 =
			new ThumbnailURLReference();

		thumbnailURLReference1.setFileBase64(_thumbnail1Base64);

		DisplayPageTemplate putDisplayPageTemplate =
			_putSiteDisplayPageTemplateAndAssertThumbnailURLReference(
				_thumbnail1Bytes, null, postDisplayPageTemplate,
				thumbnailURLReference1);

		ThumbnailURLReference thumbnailURLReference2 =
			new ThumbnailURLReference();

		thumbnailURLReference2.setFileBase64(_thumbnail2Base64);

		_putSiteDisplayPageTemplateAndAssertThumbnailURLReference(
			_thumbnail2Bytes, null, putDisplayPageTemplate,
			thumbnailURLReference2);
	}

	private void _testPutSiteDisplayPageTemplateWithThumbnailURLReferenceURL()
		throws Exception {

		DisplayPageTemplate postDisplayPageTemplate =
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		Assert.assertNull(postDisplayPageTemplate.getThumbnailURLReference());

		ThumbnailURLReference thumbnailURLReference1 =
			new ThumbnailURLReference();

		thumbnailURLReference1.setUrl(_thumbnail1URL);

		DisplayPageTemplate putDisplayPageTemplate =
			_putSiteDisplayPageTemplateAndAssertThumbnailURLReference(
				_thumbnail1Bytes, null, postDisplayPageTemplate,
				thumbnailURLReference1);

		ThumbnailURLReference thumbnailURLReference2 =
			new ThumbnailURLReference();

		thumbnailURLReference2.setUrl(_thumbnail2URL);

		_putSiteDisplayPageTemplateAndAssertThumbnailURLReference(
			_thumbnail2Bytes, null, putDisplayPageTemplate,
			thumbnailURLReference2);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DisplayPageTemplateResourceTest.class);

	private static String _thumbnail1Base64;
	private static byte[] _thumbnail1Bytes;
	private static String _thumbnail1URL;
	private static String _thumbnail2Base64;
	private static byte[] _thumbnail2Bytes;
	private static String _thumbnail2URL;
	private static ThumbnailHttpServer _thumbnailHttpServer;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _designLibraryGroup;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private Group _irrelevantDesignLibraryGroup;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/publish_layout_page_template_entry"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private StagingLocalService _stagingLocalService;

	@Inject
	private UserLocalService _userLocalService;

}