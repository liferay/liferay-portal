/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.ai.hub.rest.client.dto.v1_0.Chatbot;
import com.liferay.ai.hub.rest.client.resource.v1_0.ChatbotResource;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@FeatureFlag("LPD-62272")
@RunWith(Arquillian.class)
public class ChatbotResourceTest extends BaseChatbotResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_accountEntry = _accountEntryLocalService.addAccountEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_accountEntry.getAccountEntryId(), TestPropsValues.getUserId());

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.ai.hub.site.initializer");

		siteInitializer.initialize(TestPropsValues.getGroupId());
	}

	@AfterClass
	public static void tearDownClass() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);
		ServiceContextThreadLocal.popServiceContext();
	}

	@Override
	@Test
	public void testGetChatbotByExternalReferenceCode() throws Exception {
		_testGetChatbotByExternalReferenceCode();
		_testGetChatbotByExternalReferenceCodeAsGuestUser();
	}

	@Override
	protected Chatbot testGetChatbotByExternalReferenceCode_addChatbot()
		throws Exception {

		String chatbotExternalReferenceCode = RandomTestUtil.randomString();
		String titleValue = RandomTestUtil.randomString();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_CHATBOT", TestPropsValues.getCompanyId());

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, _DEFAULT_LANGUAGE_ID,
			HashMapBuilder.<String, Serializable>put(
				"active", true
			).put(
				"avatar",
				() -> {
					FileEntry fileEntry = TempFileEntryUtil.addTempFileEntry(
						TestPropsValues.getGroupId(),
						TestPropsValues.getUserId(), StringUtil.randomString(),
						TempFileEntryUtil.getTempFileName(
							StringUtil.randomString() + ".png"),
						FileUtil.createTempFile(
							DLTestUtil.randomTextFileBytes()),
						ContentTypes.IMAGE_PNG);

					return fileEntry.getFileEntryId();
				}
			).put(
				"externalReferenceCode", chatbotExternalReferenceCode
			).put(
				"r_accountToAIHubChatbots_accountEntryId",
				_accountEntry.getAccountEntryId()
			).put(
				"title_i18n",
				HashMapBuilder.put(
					_DEFAULT_LANGUAGE_ID, titleValue
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		return new Chatbot() {
			{
				active = true;
				externalReferenceCode = chatbotExternalReferenceCode;
				title = titleValue;
			}
		};
	}

	private void _testGetChatbotByExternalReferenceCode() throws Exception {
		Chatbot postChatbot =
			testGetChatbotByExternalReferenceCode_addChatbot();

		Chatbot getChatbot = chatbotResource.getChatbotByExternalReferenceCode(
			postChatbot.getExternalReferenceCode());

		Assert.assertTrue(getChatbot.getActive());
		Assert.assertNotNull(
			MapUtil.getString(getChatbot.getAvatar(), "fileURL"));
		Assert.assertEquals(postChatbot.getTitle(), getChatbot.getTitle());
	}

	private void _testGetChatbotByExternalReferenceCodeAsGuestUser()
		throws Exception {

		Chatbot postChatbot =
			testGetChatbotByExternalReferenceCode_addChatbot();

		ChatbotResource guestChatbotResource = ChatbotResource.builder(
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		Chatbot getChatbot =
			guestChatbotResource.getChatbotByExternalReferenceCode(
				postChatbot.getExternalReferenceCode());

		Assert.assertEquals(postChatbot.getTitle(), getChatbot.getTitle());
	}

	private static final String _DEFAULT_LANGUAGE_ID = LocaleUtil.toLanguageId(
		LocaleUtil.getDefault());

	private static AccountEntry _accountEntry;

	@Inject
	private static AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private static AccountEntryUserRelLocalService
		_accountEntryUserRelLocalService;

	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;

	@Inject
	private static SiteInitializerRegistry _siteInitializerRegistry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}