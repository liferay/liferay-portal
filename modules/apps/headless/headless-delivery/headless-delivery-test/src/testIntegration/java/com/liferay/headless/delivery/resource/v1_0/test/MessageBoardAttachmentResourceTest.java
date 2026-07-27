/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.delivery.client.dto.v1_0.MessageBoardAttachment;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.resource.v1_0.MessageBoardAttachmentResource;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;

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
public class MessageBoardAttachmentResourceTest
	extends BaseMessageBoardAttachmentResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		MBMessage mbMessage = _addMBMessage();

		_mbThread = mbMessage.getThread();
	}

	@Override
	@Test
	public void testDeleteMessageBoardAttachment() throws Exception {
		super.testDeleteMessageBoardAttachment();

		// Message board attachment without permission

		MessageBoardAttachment messageBoardAttachment =
			_addRestrictedMessageBoardAttachment();

		MessageBoardAttachmentResource
			userWithoutPermissionsMessageBoardAttachmentResource =
				_getUserWithoutPermissionsMessageBoardAttachmentResource();

		assertHttpResponseStatusCode(
			403,
			userWithoutPermissionsMessageBoardAttachmentResource.
				deleteMessageBoardAttachmentHttpResponse(
					messageBoardAttachment.getId()));

		assertHttpResponseStatusCode(
			200,
			messageBoardAttachmentResource.
				getMessageBoardAttachmentHttpResponse(
					messageBoardAttachment.getId()));
	}

	@Override
	@Test
	public void testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode()
		throws Exception {

		super.
			testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode();

		MessageBoardAttachment messageBoardAttachment =
			testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment();

		// Message board attachment associated to a different message board
		// message

		MBMessage mbMessage = _mbMessage;

		MessageBoardAttachment newMessageBoardAttachment =
			testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment();

		assertHttpResponseStatusCode(
			404,
			messageBoardAttachmentResource.
				deleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId(),
					mbMessage.getExternalReferenceCode(),
					newMessageBoardAttachment.getExternalReferenceCode()));

		// Nonexistent message board message

		assertHttpResponseStatusCode(
			404,
			messageBoardAttachmentResource.
				deleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId(),
					RandomTestUtil.randomString(),
					messageBoardAttachment.getExternalReferenceCode()));

		// Nonexistent message board message attachment

		assertHttpResponseStatusCode(
			404,
			messageBoardAttachmentResource.
				deleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId(),
					testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode(),
					RandomTestUtil.randomString()));
	}

	@Override
	@Test
	public void testGetMessageBoardAttachment() throws Exception {
		super.testGetMessageBoardAttachment();

		// Message board attachment without permission

		MessageBoardAttachment messageBoardAttachment =
			_addRestrictedMessageBoardAttachment();

		MessageBoardAttachmentResource
			userWithoutPermissionsMessageBoardAttachmentResource =
				_getUserWithoutPermissionsMessageBoardAttachmentResource();

		assertHttpResponseStatusCode(
			404,
			userWithoutPermissionsMessageBoardAttachmentResource.
				getMessageBoardAttachmentHttpResponse(
					messageBoardAttachment.getId()));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetMessageBoardMessageMessageBoardAttachmentsPage()
		throws Exception {

		super.testGraphQLGetMessageBoardMessageMessageBoardAttachmentsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetMessageBoardThreadMessageBoardAttachmentsPage()
		throws Exception {

		super.testGraphQLGetMessageBoardThreadMessageBoardAttachmentsPage();
	}

	@Override
	@Test
	public void testPostMessageBoardMessageMessageBoardAttachment()
		throws Exception {

		super.testPostMessageBoardMessageMessageBoardAttachment();

		MessageBoardAttachment postMessageBoardAttachment =
			testPostMessageBoardThreadMessageBoardAttachment_addMessageBoardAttachment(
				randomMessageBoardAttachment(), getMultipartFiles());

		Assert.assertEquals(
			"text/plain", postMessageBoardAttachment.getEncodingFormat());
	}

	@Override
	protected void assertValid(
			MessageBoardAttachment messageBoardAttachment,
			Map<String, File> multipartFiles)
		throws Exception {

		Assert.assertEquals(
			new String(FileUtil.getBytes(multipartFiles.get("file"))),
			_read(
				"http://localhost:" + PortalUtil.getPortalServerPort(false) +
					messageBoardAttachment.getContentUrl()));
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
	protected MessageBoardAttachment randomMessageBoardAttachment()
		throws Exception {

		MessageBoardAttachment messageBoardAttachment =
			super.randomMessageBoardAttachment();

		_tempFileName = FileUtil.createTempFileName();

		File file = new File(_tempFileName);

		messageBoardAttachment.setTitle(file.getName());

		return messageBoardAttachment;
	}

	@Override
	protected MessageBoardAttachment
			testDeleteMessageBoardAttachment_addMessageBoardAttachment()
		throws Exception {

		return messageBoardAttachmentResource.
			postMessageBoardThreadMessageBoardAttachment(
				_mbThread.getThreadId(), randomMessageBoardAttachment(),
				getMultipartFiles());
	}

	@Override
	protected MessageBoardAttachment
			testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment()
		throws Exception {

		_mbMessage = _addMBMessage();

		return messageBoardAttachmentResource.
			postMessageBoardMessageMessageBoardAttachment(
				_mbMessage.getMessageId(), randomMessageBoardAttachment(),
				getMultipartFiles());
	}

	@Override
	protected String
			testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode()
		throws Exception {

		return _mbMessage.getExternalReferenceCode();
	}

	@Override
	protected MessageBoardAttachment
			testGetMessageBoardAttachment_addMessageBoardAttachment()
		throws Exception {

		return messageBoardAttachmentResource.
			postMessageBoardThreadMessageBoardAttachment(
				_mbThread.getThreadId(), randomMessageBoardAttachment(),
				getMultipartFiles());
	}

	@Override
	protected Long
		testGetMessageBoardMessageMessageBoardAttachmentsPage_getMessageBoardMessageId() {

		return _mbThread.getRootMessageId();
	}

	@Override
	protected Long
		testGetMessageBoardThreadMessageBoardAttachmentsPage_getMessageBoardThreadId() {

		return _mbThread.getThreadId();
	}

	@Override
	protected MessageBoardAttachment
			testGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment()
		throws Exception {

		_mbMessage = _addMBMessage();

		return messageBoardAttachmentResource.
			postMessageBoardMessageMessageBoardAttachment(
				_mbMessage.getMessageId(), randomMessageBoardAttachment(),
				getMultipartFiles());
	}

	@Override
	protected String
			testGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode()
		throws Exception {

		return _mbMessage.getExternalReferenceCode();
	}

	@Override
	protected String
			testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode()
		throws Exception {

		return _mbMessage.getExternalReferenceCode();
	}

	@Override
	protected MessageBoardAttachment
			testGraphQLGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment()
		throws Exception {

		return testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment();
	}

	@Override
	protected String
			testGraphQLGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode()
		throws Exception {

		return _mbMessage.getExternalReferenceCode();
	}

	@Override
	protected MessageBoardAttachment
			testGraphQLMessageBoardAttachment_addMessageBoardAttachment()
		throws Exception {

		return testDeleteMessageBoardAttachment_addMessageBoardAttachment();
	}

	@Override
	protected MessageBoardAttachment
			testGraphQLSiteMessageBoardAttachment_addMessageBoardAttachment()
		throws Exception {

		_mbMessage = _addMBMessage();

		return messageBoardAttachmentResource.
			postMessageBoardMessageMessageBoardAttachment(
				_mbMessage.getMessageId(), randomMessageBoardAttachment(),
				getMultipartFiles());
	}

	private MBMessage _addMBMessage() throws Exception {
		return MBTestUtil.addMessage(
			testGroup.getGroupId(),
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	private MessageBoardAttachment _addRestrictedMessageBoardAttachment()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		MBMessage mbMessage = MBTestUtil.addMessage(
			testGroup.getGroupId(), TestPropsValues.getUserId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		return messageBoardAttachmentResource.
			postMessageBoardMessageMessageBoardAttachment(
				mbMessage.getMessageId(), randomMessageBoardAttachment(),
				getMultipartFiles());
	}

	private MessageBoardAttachmentResource
			_getUserWithoutPermissionsMessageBoardAttachmentResource()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		return MessageBoardAttachmentResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
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

	private MBMessage _mbMessage;
	private MBThread _mbThread;
	private String _tempFileName;

}