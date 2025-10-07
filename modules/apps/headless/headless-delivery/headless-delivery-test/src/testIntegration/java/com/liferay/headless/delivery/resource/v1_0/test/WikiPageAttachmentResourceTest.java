/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.delivery.client.dto.v1_0.WikiPageAttachment;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;

import java.io.File;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class WikiPageAttachmentResourceTest
	extends BaseWikiPageAttachmentResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setCommand("update");
		serviceContext.setScopeGroupId(testGroup.getGroupId());

		WikiNode wikiNode = WikiNodeLocalServiceUtil.addNode(
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		_wikiPage = WikiPageLocalServiceUtil.addPage(
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			wikiNode.getNodeId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			serviceContext);
	}

	@Override
	@Test
	public void testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode()
		throws Exception {

		super.
			testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode();

		WikiPageAttachment wikiPageAttachment =
			testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment();

		// Nonexistent wiki page

		assertHttpResponseStatusCode(
			404,
			wikiPageAttachmentResource.
				deleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId(),
					RandomTestUtil.randomString(),
					wikiPageAttachment.getExternalReferenceCode()));

		// Nonexistent wiki page attachment

		assertHttpResponseStatusCode(
			404,
			wikiPageAttachmentResource.
				deleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId(),
					testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode(),
					RandomTestUtil.randomString()));

		// Wiki page attachment associated to a different wiki page

		WikiPage previousWikiPage = _wikiPage;

		WikiPageAttachment newWikiPageAttachment =
			testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment();

		assertHttpResponseStatusCode(
			204,
			wikiPageAttachmentResource.
				deleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId(),
					previousWikiPage.getExternalReferenceCode(),
					newWikiPageAttachment.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode()
		throws Exception {

		super.
			testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			WikiPageAttachment wikiPageAttachment =
				testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment();

			// Nonexistent wiki page

			assertHttpResponseStatusCode(
				404,
				wikiPageAttachmentResource.
					getSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCodeHttpResponse(
						testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId(),
						RandomTestUtil.randomString(),
						wikiPageAttachment.getExternalReferenceCode()));
		}

		// Nonexistent wiki page attachment

		assertHttpResponseStatusCode(
			404,
			wikiPageAttachmentResource.
				getSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCodeHttpResponse(
					testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId(),
					testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode(),
					RandomTestUtil.randomString()));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetWikiPageWikiPageAttachmentsPage()
		throws Exception {

		super.testGraphQLGetWikiPageWikiPageAttachmentsPage();
	}

	@Override
	protected void assertValid(
			WikiPageAttachment wikiPageAttachment,
			Map<String, File> multipartFiles)
		throws Exception {

		Assert.assertEquals(
			new String(FileUtil.getBytes(multipartFiles.get("file"))),
			_read(
				"http://localhost:8080" + wikiPageAttachment.getContentUrl()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"title"};
	}

	@Override
	protected Map<String, File> getMultipartFiles() throws Exception {
		return HashMapBuilder.<String, File>put(
			"file",
			() -> {
				File file = new File(_tempFileName);

				FileUtil.write(file, TestDataConstants.TEST_BYTE_ARRAY);

				return file;
			}
		).build();
	}

	@Override
	protected WikiPageAttachment randomWikiPageAttachment() throws Exception {
		WikiPageAttachment wikiPageAttachment =
			super.randomWikiPageAttachment();

		_tempFileName = FileUtil.createTempFileName();

		File file = new File(_tempFileName);

		wikiPageAttachment.setTitle(file.getName());

		return wikiPageAttachment;
	}

	@Override
	protected WikiPageAttachment
			testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment()
		throws Exception {

		return testDeleteWikiPageAttachment_addWikiPageAttachment();
	}

	@Override
	protected Long
			testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return _wikiPage.getGroupId();
	}

	@Override
	protected String
			testDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode()
		throws Exception {

		return _wikiPage.getExternalReferenceCode();
	}

	@Override
	protected WikiPageAttachment
			testDeleteWikiPageAttachment_addWikiPageAttachment()
		throws Exception {

		return wikiPageAttachmentResource.postWikiPageWikiPageAttachment(
			_wikiPage.getResourcePrimKey(), randomWikiPageAttachment(),
			getMultipartFiles());
	}

	@Override
	protected WikiPageAttachment
			testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment()
		throws Exception {

		return testDeleteWikiPageAttachment_addWikiPageAttachment();
	}

	@Override
	protected Long
			testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return _wikiPage.getGroupId();
	}

	@Override
	protected String
			testGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode()
		throws Exception {

		return _wikiPage.getExternalReferenceCode();
	}

	@Override
	protected WikiPageAttachment
			testGetWikiPageAttachment_addWikiPageAttachment()
		throws Exception {

		return wikiPageAttachmentResource.postWikiPageWikiPageAttachment(
			_wikiPage.getResourcePrimKey(), randomWikiPageAttachment(),
			getMultipartFiles());
	}

	@Override
	protected Long testGetWikiPageWikiPageAttachmentsPage_getWikiPageId() {
		return _wikiPage.getResourcePrimKey();
	}

	@Override
	protected String
			testGraphQLDeleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode()
		throws Exception {

		return _wikiPage.getExternalReferenceCode();
	}

	@Override
	protected WikiPageAttachment
			testGraphQLGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_addWikiPageAttachment()
		throws Exception {

		return testDeleteWikiPageAttachment_addWikiPageAttachment();
	}

	@Override
	protected Long
			testGraphQLGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return _wikiPage.getGroupId();
	}

	@Override
	protected String
			testGraphQLGetSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode_getWikiPageExternalReferenceCode()
		throws Exception {

		return _wikiPage.getExternalReferenceCode();
	}

	@Override
	protected WikiPageAttachment
			testGraphQLSiteWikiPageAttachment_addWikiPageAttachment()
		throws Exception {

		return wikiPageAttachmentResource.postWikiPageWikiPageAttachment(
			_wikiPage.getResourcePrimKey(), randomWikiPageAttachment(),
			getMultipartFiles());
	}

	@Override
	protected WikiPageAttachment
			testGraphQLWikiPageAttachment_addWikiPageAttachment()
		throws Exception {

		return testDeleteWikiPageAttachment_addWikiPageAttachment();
	}

	private String _read(String url) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.httpMethod(HttpInvoker.HttpMethod.GET);
		httpInvoker.path(url);
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	private String _tempFileName;
	private WikiPage _wikiPage;

}