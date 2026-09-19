/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.builder;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.journal.model.JournalArticle;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.search.similar.results.web.internal.contributor.BaseSimilarResultsContributorTestCase;
import com.liferay.portal.search.similar.results.web.internal.contributor.SimilarResultsContributor;
import com.liferay.portal.search.similar.results.web.internal.portlet.shared.search.Criteria;
import com.liferay.portal.search.similar.results.web.internal.portlet.shared.search.CriteriaBuilderImpl;
import com.liferay.portal.search.similar.results.web.internal.portlet.shared.search.CriteriaHelperImpl;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.CriteriaHelper;
import com.liferay.portal.search.similar.results.web.spi.contributor.helper.DestinationHelper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class SimilarResultsUidsAndDestinationsTest
	extends BaseSimilarResultsContributorTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());

		_similarResultsContributorsRegistry =
			new SimilarResultsContributorsRegistry(
				assetEntryLocalService, blogsEntryLocalService,
				dlFileEntryLocalService, dlFolderLocalService,
				mbCategoryLocalService, mbMessageLocalService, uidFactory,
				wikiNodeLocalService, wikiPageLocalService);
	}

	@Test
	public void testAssetPublisher() {
		String urlString = StringBundler.concat(
			"http://localhost:8080/web/guest/ap-page/-/asset_publisher",
			"/BNPTUvWUBXIr/content/id?p_r_p_assetEntryId=43152",
			"&_com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_",
			"INSTANCE_BNPTUvWUBXIr_redirect=",
			"http%3A%2F%2Flocalhost%3A8080%2Fweb%2Fguest%2F",
			"ap-page%3Fp_p_id=com_liferay_asset_publisher_web_portlet_",
			"AssetPublisherPortlet_",
			"INSTANCE_BNPTUvWUBXIr&p_p_lifecycle=0&p_p_state=normal",
			"&p_p_mode=view",
			"&_com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_",
			"INSTANCE_BNPTUvWUBXIr_cur=0&p_r_p_resetCur=false",
			"&p_r_p_assetEntryId=43152");

		String className1 = JournalArticle.class.getName();
		String className2 = BlogsEntry.class.getName();
		long classPK1 = 12345678;
		long classPK2 = 87654321;
		long entryId1 = 43152;
		long entryId2 = 25134;

		String expectedUID = className1 + "_PORTLET_12345678";

		String expectedDestination = StringBundler.concat(
			"http://localhost:8080/web/guest/ap-page/-/asset_publisher",
			"/BNPTUvWUBXIr/blog/id?p_r_p_assetEntryId=25134",
			"&_com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_",
			"INSTANCE_BNPTUvWUBXIr_redirect=http://localhost:8080/web/guest",
			"/ap-page?p_p_id=com_liferay_asset_publisher_web_portlet_",
			"AssetPublisherPortlet_",
			"INSTANCE_BNPTUvWUBXIr&p_p_lifecycle=0&p_p_state=normal",
			"&p_p_mode=view",
			"&_com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_",
			"INSTANCE_BNPTUvWUBXIr_cur=0&p_r_p_resetCur=false",
			"&p_r_p_assetEntryId=25134");

		AssetEntry assetEntry1 = getAssetEntry(className1, classPK1);

		Mockito.doReturn(
			entryId1
		).when(
			assetEntry1
		).getEntryId();

		Mockito.doReturn(
			assetEntry1
		).when(
			assetEntryLocalService
		).fetchAssetEntry(
			entryId1
		);

		JournalArticle journalArticle = Mockito.mock(JournalArticle.class);

		Mockito.doReturn(
			classPK1
		).when(
			journalArticle
		).getId();

		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.doReturn(
			journalArticle
		).when(
			assetRenderer
		).getAssetObject();

		Mockito.doReturn(
			assetRenderer
		).when(
			assetEntry1
		).getAssetRenderer();

		AssetEntry assetEntry2 = getAssetEntry(className2, classPK2);

		Mockito.doReturn(
			entryId2
		).when(
			assetEntry2
		).getEntryId();

		_setUpDestinationAssetEntry(assetEntry2);

		_setUpDestinationClassName(className2);

		_setUpUIDFactory(expectedUID);

		_assertSimilarResultsContributor(
			urlString, expectedUID, expectedDestination);
	}

	@Test
	public void testBlogs() {
		String urlString = StringBundler.concat(
			"http://localhost:8080/web/guest/blogs/-/blogs/blog-2",
			"?_com_liferay_blogs_web_portlet_BlogsPortlet",
			"_redirect=%2Fweb%2Fguest%2Fassetpublisher");

		String className = "com_liferay_blogs_web_portlet_BlogsPortlet";
		int classPK = 12345;
		long entryId = 98766;
		long groupId = RandomTestUtil.randomLong();

		String expectedUID = className + "_PORTLET_" + entryId;

		String urlTitle = "2-golb";
		String uuid = "abcde-efgh";

		String expectedDestination = StringBundler.concat(
			"http://localhost:8080/web/guest/blogs/-/blogs/", urlTitle,
			"?_com_liferay_blogs_web_portlet_BlogsPortlet",
			"_redirect=/web/guest/assetpublisher");

		AssetEntry assetEntry = getAssetEntry(className, classPK);

		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.doReturn(
			urlTitle
		).when(
			assetRenderer
		).getUrlTitle();

		BlogsEntry blogsEntry = Mockito.mock(BlogsEntry.class);

		Mockito.doReturn(
			entryId
		).when(
			blogsEntry
		).getEntryId();

		Mockito.doReturn(
			uuid
		).when(
			blogsEntry
		).getUuid();

		_setUpAssetEntryLocalServiceFetchGroupIdUUID(assetEntry, groupId, uuid);
		_setUpBlogsEntryLocalService(blogsEntry);
		_setUpInputGroupId(groupId);
		_setUpDestinationAssetRenderer(assetRenderer);
		_setUpUIDFactory(expectedUID);

		_assertSimilarResultsContributor(
			urlString, expectedUID, expectedDestination);
	}

	@Test
	public void testDocumentLibraryDLFileEntry() {
		String urlString = StringBundler.concat(
			"http://localhost:8080/web/guest/document-and-media/-",
			"/document_library/oagkfEivnD1J/view_file/39730?",
			"_com_liferay_document_library_web_portlet_DLPortlet_INSTANCE");

		String className = DLFileEntry.class.getName();
		long fileEntryId = 33333;
		long groupId = RandomTestUtil.randomLong();

		String expectedUID = className + "_PORTLET_" + fileEntryId;

		String expectedDestination = StringBundler.concat(
			"http://localhost:8080/web/guest/document-and-media/-",
			"/document_library/oagkfEivnD1J/view_file/", fileEntryId,
			"?_com_liferay_document_library_web_portlet_DLPortlet_INSTANCE");

		DLFileEntry dlFileEntry = Mockito.mock(DLFileEntry.class);

		Mockito.doReturn(
			fileEntryId
		).when(
			dlFileEntry
		).getFileEntryId();

		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.doReturn(
			dlFileEntry
		).when(
			assetRenderer
		).getAssetObject();

		_setUpAssetEntryLocalServiceFetchUUID(getAssetEntry(className, 12345));

		_setUpDestinationAssetRenderer(assetRenderer);
		_setUpDestinationClassName(className);
		_setUpDLFileEntryLocalService(dlFileEntry);
		_setUpInputGroupId(groupId);

		_assertSimilarResultsContributor(
			urlString, expectedUID, expectedDestination);
	}

	@Test
	public void testDocumentLibraryDLFolder() {
		String urlString = StringBundler.concat(
			"http://localhost:8080/web/guest/document-and-media/-",
			"/document_library/oagkfEivnD1J/view_file/39730?",
			"_com_liferay_document_library_web_portlet_DLPortlet_INSTANCE");

		String className = DLFolder.class.getName();
		long folderId = 33333;
		long groupId = RandomTestUtil.randomLong();

		String expectedUID = className + "_PORTLET_" + folderId;

		String expectedDestination = StringBundler.concat(
			"http://localhost:8080/web/guest/document-and-media/-",
			"/document_library/oagkfEivnD1J/view_file/", folderId,
			"?_com_liferay_document_library_web_portlet_DLPortlet_INSTANCE");

		DLFolder dlFolder = Mockito.mock(DLFolder.class);

		Mockito.doReturn(
			folderId
		).when(
			dlFolder
		).getFolderId();

		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.doReturn(
			dlFolder
		).when(
			assetRenderer
		).getAssetObject();

		_setUpAssetEntryLocalServiceFetchUUID(getAssetEntry(className, 12345));

		_setUpDestinationAssetRenderer(assetRenderer);
		_setUpDestinationClassName(className);
		_setUpDLFolderLocalService(dlFolder);
		_setUpInputGroupId(groupId);

		_assertSimilarResultsContributor(
			urlString, expectedUID, expectedDestination);
	}

	@Test
	public void testMessageBoardsCategory() {
		String urlString = StringBundler.concat(
			"http://localhost:8080/web/guest/message-boards/-/message_boards",
			"/category/39748?",
			"_com_liferay_message_boards_web_portlet_MBPortlet_redirect=",
			"%2Fweb%2Fguest%2Fassetpublisher");

		String className = "com.liferay.message.boards.model.MBCategory";
		long categoryId = 22222;
		long groupId = RandomTestUtil.randomLong();

		String expectedUID = className + "_PORTLET_" + categoryId;

		String expectedDestination = StringBundler.concat(
			"http://localhost:8080/web/guest/message-boards/-/message_boards",
			"/category/", categoryId,
			"?_com_liferay_message_boards_web_portlet_MBPortlet_redirect=/web",
			"/guest/assetpublisher");

		Mockito.doReturn(
			MBCategory.class.getName()
		).when(
			destinationHelper
		).getClassName();

		Mockito.doReturn(
			categoryId
		).when(
			destinationHelper
		).getClassPK();

		_setUpInputGroupId(groupId);
		_setUpMBCategoryLocalService(categoryId);

		_assertSimilarResultsContributor(
			urlString, expectedUID, expectedDestination);
	}

	@Test
	public void testMessageBoardsMessage() {
		String urlString = StringBundler.concat(
			"http://localhost:8080/web/guest/message-boards/-/message_boards",
			"/message/39753?",
			"_com_liferay_message_boards_web_portlet_MBPortlet_redirect=",
			"%2Fweb%2Fguest%2Fassetpublisher");

		String className = "com_liferay_message_web_portlet_MessagePortlet";
		long groupId = RandomTestUtil.randomLong();

		long messageId = 123456;

		String expectedDestination = StringBundler.concat(
			"http://localhost:8080/web/guest/message-boards/-/message_boards",
			"/message/", messageId,
			"?_com_liferay_message_boards_web_portlet_MBPortlet_redirect=/web",
			"/guest/assetpublisher");

		String expectedUID = className + "_PORTLET_" + 123456;

		Mockito.doReturn(
			MBMessage.class.getName()
		).when(
			destinationHelper
		).getClassName();

		Mockito.doReturn(
			messageId
		).when(
			destinationHelper
		).getClassPK();

		_setUpAssetEntryLocalServiceFetchUUID(getAssetEntry(className, 12345));

		_setUpInputGroupId(groupId);
		_setUpMBMessageLocalService(messageId);

		_assertSimilarResultsContributor(
			urlString, expectedUID, expectedDestination);
	}

	@Test
	public void testURLBlank() {
		Assert.assertNull(
			_similarResultsContributorsRegistry.detectRoute(StringPool.BLANK));
	}

	@Test
	public void testURLClassNameClassPK() {
		String urlString =
			"http://localhost:8080/web/guest/blabal?" +
				"className=ABCDE&classPK=34567";

		String className = "EDCBA";
		int classPK = 76543;

		String expectedUID = "ABCDE_PORTLET_34567";

		String expectedDestination = StringBundler.concat(
			"http://localhost:8080/web/guest/blabal?className=", className,
			"&classPK=", classPK);

		_setUpDestinationAssetEntry(getAssetEntry(className, classPK));

		_assertSimilarResultsContributor(
			urlString, expectedUID, expectedDestination);
	}

	@Test
	public void testURLClassNameIDClassPK() {
		String urlString =
			"http://localhost:8080/web/guest/blabal?classNameId" +
				"=142857&classPK=34567";

		String className = "ClassNamePortlet";
		long classNameId1 = 142857;
		long classNameId2 = 758241;
		long classPK1 = 34567;
		long classPK2 = 76543;

		String expectedUID = "ClassNamePortlet_PORTLET_34567";

		String expectedDestination = StringBundler.concat(
			"http://localhost:8080/web/guest/blabal?classNameId=", classNameId2,
			"&classPK=", classPK2);

		AssetEntry assetEntry1 = getAssetEntry(className, classPK1);

		Mockito.doReturn(
			classNameId1
		).when(
			assetEntry1
		).getClassNameId();

		Mockito.when(
			assetEntryLocalService.fetchEntry(classNameId1, classPK1)
		).thenReturn(
			assetEntry1
		);

		AssetEntry assetEntry2 = getAssetEntry(className, classPK2);

		Mockito.doReturn(
			classNameId2
		).when(
			assetEntry2
		).getClassNameId();

		_setUpDestinationAssetEntry(assetEntry2);

		_assertSimilarResultsContributor(
			urlString, expectedUID, expectedDestination);
	}

	@Test
	public void testURLClassUUID() {
		String uuid1 = "abcde-efgh";

		String urlString =
			"http://localhost:8080/web/guest/blabal?classUuid=" + uuid1;

		String className = "ABCDE";
		long classPK = 8885;
		long groupId = RandomTestUtil.randomLong();

		String uuid2 = "edcba-hgfe";

		String expectedUID = "ABCDE_PORTLET_8885";
		String expectedDestination =
			"http://localhost:8080/web/guest/blabal?classUuid=" + uuid2;

		AssetEntry assetEntry1 = getAssetEntry(className, classPK);

		AssetEntry assetEntry2 = Mockito.mock(AssetEntry.class);

		Mockito.doReturn(
			uuid2
		).when(
			assetEntry2
		).getClassUuid();

		_setUpAssetEntryLocalServiceFetchGroupIdUUID(
			assetEntry1, groupId, uuid1);
		_setUpDestinationAssetEntry(assetEntry2);
		_setUpInputGroupId(groupId);

		_assertSimilarResultsContributor(
			urlString, expectedUID, expectedDestination);
	}

	@Test
	public void testURLEntryId() {
		String urlString =
			"http://localhost:8080/web/guest/blabal?entryId=12345";

		String className = "ClassNamePortlet";
		long classPK = 9995;
		long entryId = 54321;

		String expectedUID = className + "_PORTLET_" + classPK;

		String expectedDestination =
			"http://localhost:8080/web/guest/blabal?entryId=" + entryId;

		AssetEntry assetEntry = getAssetEntry(className, classPK);

		Mockito.doReturn(
			entryId
		).when(
			assetEntry
		).getEntryId();

		Mockito.when(
			assetEntryLocalService.fetchAssetEntry(Mockito.anyLong())
		).thenReturn(
			assetEntry
		);

		_setUpDestinationAssetEntry(assetEntry);

		_assertSimilarResultsContributor(
			urlString, expectedUID, expectedDestination);
	}

	@Test
	public void testURLUID() {
		String urlString = "http://localhost:8080/web/guest/blabal?uid=old";

		String uid = "new";

		Mockito.doReturn(
			uid
		).when(
			destinationHelper
		).getUID();

		String expectedUID = "old";

		String expectedDestination =
			"http://localhost:8080/web/guest/blabal?uid=new";

		_assertSimilarResultsContributor(
			urlString, expectedUID, expectedDestination);
	}

	@Test
	public void testWikiDisplay() {
		String urlString = StringBundler.concat(
			"http://localhost:8080/web/guest/wiki",
			"?p_p_id=com_liferay_wiki_web_portlet_WikiDisplayPortlet_INSTANCE",
			"&_com_liferay_wiki_web_portlet_WikiDisplayPortlet_INSTANCE",
			"_nodeName=OLDNODE",
			"&_com_liferay_wiki_web_portlet_WikiDisplayPortlet_INSTANCE",
			"_title=OLDTITLE");

		String className = WikiPage.class.getName();
		long classPK = 12345;

		long groupId = RandomTestUtil.randomLong();

		String nodeName = "NEWNODE";
		String title = "NEWTITLE";

		String expectedUID = className + "_PORTLET_" + classPK;

		String expectedDestination = StringBundler.concat(
			"http://localhost:8080/web/guest/wiki",
			"?p_p_id=com_liferay_wiki_web_portlet_WikiDisplayPortlet_INSTANCE",
			"&_com_liferay_wiki_web_portlet_WikiDisplayPortlet_INSTANCE",
			"_nodeName=", nodeName,
			"&_com_liferay_wiki_web_portlet_WikiDisplayPortlet_INSTANCE",
			"_title=", title);

		_setUpDestinationClassName(className);

		WikiNode wikiNode = Mockito.mock(WikiNode.class);

		Mockito.doReturn(
			nodeName
		).when(
			wikiNode
		).getName();

		WikiPage wikiPage = Mockito.mock(WikiPage.class);

		Mockito.doReturn(
			wikiNode
		).when(
			wikiPage
		).getNode();

		Mockito.doReturn(
			title
		).when(
			wikiPage
		).getTitle();

		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.doReturn(
			wikiPage
		).when(
			assetRenderer
		).getAssetObject();

		_setUpAssetEntryLocalServiceFetchUUID(
			getAssetEntry(className, classPK));

		_setUpDestinationAssetRenderer(assetRenderer);
		_setUpInputGroupId(groupId);
		_setUpUIDFactory(expectedUID);
		_setUpWikiNodeLocalService(wikiNode);
		_setUpWikiPageLocalService(wikiPage);

		_assertSimilarResultsContributor(
			urlString, expectedUID, expectedDestination);
	}

	protected SimilarResultsRoute detectRoute(String urlString) {
		return _similarResultsContributorsRegistry.detectRoute(urlString);
	}

	protected AssetEntry getAssetEntry(String className, long classPK) {
		AssetEntry assetEntry = Mockito.mock(AssetEntry.class);

		Mockito.doReturn(
			className
		).when(
			assetEntry
		).getClassName();

		Mockito.doReturn(
			classPK
		).when(
			assetEntry
		).getClassPK();

		return assetEntry;
	}

	protected String writeDestination(
		String urlString, SimilarResultsRoute similarResultsRoute) {

		DestinationBuilderImpl destinationBuilderImpl =
			new DestinationBuilderImpl(urlString);

		SimilarResultsContributor similarResultsContributor =
			similarResultsRoute.getContributor();

		Mockito.doAnswer(
			invocationOnMock -> similarResultsRoute.getRouteParameter(
				invocationOnMock.getArgument(0, String.class))
		).when(
			destinationHelper
		).getRouteParameter(
			Mockito.nullable(String.class)
		);

		similarResultsContributor.writeDestination(
			destinationBuilderImpl, destinationHelper);

		return destinationBuilderImpl.build();
	}

	protected DestinationHelper destinationHelper = Mockito.mock(
		DestinationHelper.class);

	private void _assertSimilarResultsContributor(
		String urlString, String expectedUID, String expectedDestination) {

		SimilarResultsRoute similarResultsRoute = detectRoute(urlString);

		Assert.assertEquals(expectedUID, _resolveUID(similarResultsRoute));

		Assert.assertEquals(
			expectedDestination,
			writeDestination(urlString, similarResultsRoute));
	}

	private Criteria _resolveCriteria(SimilarResultsRoute similarResultsRoute) {
		CriteriaBuilderImpl criteriaBuilderImpl = new CriteriaBuilderImpl();

		CriteriaHelper inputHelper = new CriteriaHelperImpl(
			_groupId, similarResultsRoute);

		SimilarResultsContributor similarResultsContributor =
			similarResultsRoute.getContributor();

		similarResultsContributor.resolveCriteria(
			criteriaBuilderImpl, inputHelper);

		return criteriaBuilderImpl.build();
	}

	private String _resolveUID(SimilarResultsRoute similarResultsContributor) {
		Criteria similarResultsInputCriteria = _resolveCriteria(
			similarResultsContributor);

		return similarResultsInputCriteria.getUID();
	}

	private void _setUpAssetEntryLocalServiceFetchGroupIdUUID(
		AssetEntry assetEntry, long groupId, String uuid) {

		Mockito.doReturn(
			assetEntry
		).when(
			assetEntryLocalService
		).fetchEntry(
			Mockito.eq(groupId), Mockito.eq(uuid)
		);
	}

	private void _setUpAssetEntryLocalServiceFetchUUID(AssetEntry assetEntry) {
		Mockito.when(
			assetEntryLocalService.fetchEntry(
				Mockito.anyLong(), Mockito.nullable(String.class))
		).thenReturn(
			assetEntry
		);
	}

	private void _setUpBlogsEntryLocalService(BlogsEntry blogsEntry) {
		Mockito.when(
			blogsEntryLocalService.fetchEntry(
				Mockito.anyLong(), Mockito.nullable(String.class))
		).thenReturn(
			blogsEntry
		);
	}

	private void _setUpDLFileEntryLocalService(DLFileEntry dlFileEntry) {
		Mockito.when(
			dlFileEntryLocalService.fetchDLFileEntry(Mockito.anyLong())
		).thenReturn(
			dlFileEntry
		);
	}

	private void _setUpDLFolderLocalService(DLFolder dlFolder) {
		Mockito.doReturn(
			dlFolder
		).when(
			dlFolderLocalService
		).fetchDLFolder(
			Mockito.anyLong()
		);
	}

	private void _setUpDestinationAssetEntry(AssetEntry assetEntry) {
		Mockito.doReturn(
			assetEntry
		).when(
			destinationHelper
		).getAssetEntry();
	}

	private void _setUpDestinationAssetRenderer(
		AssetRenderer<?> assetRenderer) {

		Mockito.doReturn(
			assetRenderer
		).when(
			destinationHelper
		).getAssetRenderer();
	}

	private void _setUpDestinationClassName(String className) {
		Mockito.doReturn(
			className
		).when(
			destinationHelper
		).getClassName();
	}

	private void _setUpInputGroupId(long groupId) {
		_groupId = groupId;
	}

	private void _setUpMBCategoryLocalService(long categoryId) {
		MBCategory mbCategory = Mockito.mock(MBCategory.class);

		Mockito.doReturn(
			categoryId
		).when(
			mbCategory
		).getCategoryId();

		Mockito.when(
			mbCategoryLocalService.fetchMBCategory(Mockito.anyLong())
		).thenReturn(
			mbCategory
		);
	}

	private void _setUpMBMessageLocalService(long messageId) {
		MBMessage mbMessage = Mockito.mock(MBMessage.class);

		Mockito.doReturn(
			messageId
		).when(
			mbMessage
		).getRootMessageId();

		Mockito.when(
			mbMessageLocalService.fetchMBMessage(Mockito.anyLong())
		).thenReturn(
			mbMessage
		);
	}

	private void _setUpUIDFactory(String uid) {
		Mockito.when(
			uidFactory.getUID(Mockito.any(ClassedModel.class))
		).thenReturn(
			uid
		);
	}

	private void _setUpWikiNodeLocalService(WikiNode wikiNode) {
		Mockito.when(
			wikiNodeLocalService.fetchNode(
				Mockito.anyLong(), Mockito.nullable(String.class))
		).thenReturn(
			wikiNode
		);
	}

	private void _setUpWikiPageLocalService(WikiPage wikiPage) {
		Mockito.when(
			wikiPageLocalService.fetchPage(
				Mockito.anyLong(), Mockito.nullable(String.class),
				Mockito.anyDouble())
		).thenReturn(
			wikiPage
		);
	}

	private long _groupId;
	private SimilarResultsContributorsRegistry
		_similarResultsContributorsRegistry;

}