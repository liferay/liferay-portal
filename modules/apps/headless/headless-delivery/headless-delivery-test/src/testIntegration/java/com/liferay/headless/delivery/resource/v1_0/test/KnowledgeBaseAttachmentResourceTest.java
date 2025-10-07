/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.delivery.client.dto.v1_0.KnowledgeBaseAttachment;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.File;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 * @author Igor Beslic
 */
@RunWith(Arquillian.class)
public class KnowledgeBaseAttachmentResourceTest
	extends BaseKnowledgeBaseAttachmentResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_kbArticle = _addKBArticle();
	}

	@Override
	@Test
	public void testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode()
		throws Exception {

		super.
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode();

		// Nonexistent knowledge base article

		KnowledgeBaseAttachment knowledgeBaseAttachment =
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment();

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					RandomTestUtil.randomString(),
					knowledgeBaseAttachment.getExternalReferenceCode()));

		// Nonexistent knowledge base attachment

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode(),
					RandomTestUtil.randomString()));

		// Knowledge base attachment associated to a different article

		KBArticle prevKBArticle = _kbArticle;

		_kbArticle = _addKBArticle();

		KnowledgeBaseAttachment newKnowledgeBaseAttachment =
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment();

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					prevKBArticle.getExternalReferenceCode(),
					newKnowledgeBaseAttachment.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode()
		throws Exception {

		super.
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode();

		// Nonexistent knowledge base article

		KnowledgeBaseAttachment knowledgeBaseAttachment =
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment();

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					RandomTestUtil.randomString(),
					knowledgeBaseAttachment.getExternalReferenceCode()));

		// Nonexistent knowledge base attachment

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode(),
					RandomTestUtil.randomString()));

		// Knowledge base attachment associated to a different article

		KBArticle prevKBArticle = _kbArticle;

		_kbArticle = _addKBArticle();

		KnowledgeBaseAttachment newKnowledgeBaseAttachment =
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment();

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					prevKBArticle.getExternalReferenceCode(),
					newKnowledgeBaseAttachment.getExternalReferenceCode()));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage()
		throws Exception {

		super.testGraphQLGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage();
	}

	@Override
	protected void assertValid(
			KnowledgeBaseAttachment knowledgeBaseAttachment,
			Map<String, File> multipartFiles)
		throws Exception {

		Assert.assertEquals(
			new String(FileUtil.getBytes(multipartFiles.get("file"))),
			_read(
				"http://localhost:8080" +
					knowledgeBaseAttachment.getContentUrl()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"title"};
	}

	@Override
	protected Map<String, File> getMultipartFiles() {
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
	protected KnowledgeBaseAttachment randomKnowledgeBaseAttachment()
		throws Exception {

		KnowledgeBaseAttachment knowledgeBaseAttachment =
			super.randomKnowledgeBaseAttachment();

		_tempFileName = FileUtil.createTempFileName();

		File file = new File(_tempFileName);

		knowledgeBaseAttachment.setTitle(file.getName());

		return knowledgeBaseAttachment;
	}

	@Override
	protected KnowledgeBaseAttachment
			testDeleteKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		return _addKnowledgeBaseAttachment();
	}

	@Override
	protected KnowledgeBaseAttachment
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment()
		throws Exception {

		return _addKnowledgeBaseAttachment();
	}

	@Override
	protected String
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode()
		throws Exception {

		return _kbArticle.getExternalReferenceCode();
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_getExpectedActions(
				Long knowledgeBaseArticleId)
		throws Exception {

		return Collections.emptyMap();
	}

	@Override
	protected Long
		testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_getKnowledgeBaseArticleId() {

		return _kbArticle.getResourcePrimKey();
	}

	@Override
	protected KnowledgeBaseAttachment
			testGetKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		return _addKnowledgeBaseAttachment();
	}

	@Override
	protected KnowledgeBaseAttachment
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment()
		throws Exception {

		return _addKnowledgeBaseAttachment();
	}

	@Override
	protected String
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode()
		throws Exception {

		return _kbArticle.getExternalReferenceCode();
	}

	@Override
	protected Long
		testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId() {

		return testGroup.getGroupId();
	}

	@Override
	protected String
			testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode()
		throws Exception {

		return _kbArticle.getExternalReferenceCode();
	}

	@Override
	protected String
			testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode()
		throws Exception {

		return _kbArticle.getExternalReferenceCode();
	}

	@Override
	protected KnowledgeBaseAttachment
			testGraphQLKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		return testDeleteKnowledgeBaseAttachment_addKnowledgeBaseAttachment();
	}

	@Override
	protected KnowledgeBaseAttachment
			testGraphQLSiteKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		return _addKnowledgeBaseAttachment();
	}

	private KBArticle _addKBArticle() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(testGroup.getGroupId());

		return KBArticleLocalServiceUtil.addKBArticle(
			null, UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			PortalUtil.getClassNameId(KBFolder.class.getName()), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, RandomTestUtil.nextDate(), null, null, null, serviceContext);
	}

	private KnowledgeBaseAttachment _addKnowledgeBaseAttachment()
		throws Exception {

		return knowledgeBaseAttachmentResource.
			postKnowledgeBaseArticleKnowledgeBaseAttachment(
				_kbArticle.getResourcePrimKey(),
				randomKnowledgeBaseAttachment(), getMultipartFiles());
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

	private KBArticle _kbArticle;
	private String _tempFileName;

}