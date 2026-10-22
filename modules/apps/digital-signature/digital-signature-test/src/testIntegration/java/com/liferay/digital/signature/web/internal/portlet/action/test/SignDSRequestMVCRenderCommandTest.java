/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.constants.DSObjectDefinitionConstants;
import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.digital.signature.manager.DSRecipientViewDefinitionManager;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.digital.signature.model.DSRequestRecipient;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Danny Situ
 */
@FeatureFlag("LPD-69290")
@RunWith(Arquillian.class)
public class SignDSRequestMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		BatchEngineTestUtil.processBatchEngineUnits(
			"com.liferay.digital.signature.impl",
			SignDSRequestMVCRenderCommandTest.class,
			new String[] {
				".com.liferay.digital.signature.internal.batch.01.object." +
					"definition",
				".com.liferay.digital.signature.internal.batch.02.object." +
					"relationship"
			});

		_companyConfigurationTemporarySwapper =
			new CompanyConfigurationTemporarySwapper(
				TestPropsValues.getCompanyId(),
				DigitalSignatureConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"embeddedViewEnabled", true
				).put(
					"enabled", true
				).put(
					"siteSettingsStrategy", "always-inherit"
				).build());
		_dsRequestDocumentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_DOCUMENT,
					TestPropsValues.getCompanyId());
		_dsRequestObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_REQUEST,
					TestPropsValues.getCompanyId());
		_dsRequestRecipientObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					DSObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_REQUEST_RECIPIENT,
					TestPropsValues.getCompanyId());

		_group = GroupTestUtil.addGroup();

		_portletLayout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@After
	public void tearDown() throws Exception {
		_companyConfigurationTemporarySwapper.close();
	}

	@Test
	public void testRender() throws Exception {
		Layout fileEntryLayout = LayoutTestUtil.addTypePortletLayout(_group);

		FileEntry fileEntry = _addFileEntry(fileEntryLayout.getPlid());

		_testRender(_addDSRequest(fileEntry.getFileEntryId()), fileEntryLayout);

		fileEntry = _addFileEntry(RandomTestUtil.randomLong());

		_testRender(_addDSRequest(fileEntry.getFileEntryId()), _portletLayout);

		_testRenderWhenStateIsPopUp();
		_testRenderWhenUserIsNotDSRequestRecipient();
	}

	private long _addDSRequest(long fileEntryId) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		ObjectEntry dsRequestObjectEntry = _addObjectEntry(
			_dsRequestObjectDefinition, serviceContext,
			HashMapBuilder.<String, Serializable>put(
				"emailSubject", RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"requestStatus", DSRequestConstants.STATUS_SENT
			).put(
				"siteGroupId", _group.getGroupId()
			).build());

		_addObjectEntry(
			_dsRequestDocumentObjectDefinition, serviceContext,
			HashMapBuilder.<String, Serializable>put(
				"fileEntryId", fileEntryId
			).put(
				"r_dsRequestToDSRequestDocuments_l_dsRequestId",
				dsRequestObjectEntry.getObjectEntryId()
			).build());

		_addObjectEntry(
			_dsRequestRecipientObjectDefinition, serviceContext,
			HashMapBuilder.<String, Serializable>put(
				"emailAddress",
				() -> {
					User user = TestPropsValues.getUser();

					return user.getEmailAddress();
				}
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"r_dsRequestToDSRequestRecipients_l_dsRequestId",
				dsRequestObjectEntry.getObjectEntryId()
			).put(
				"r_userToDSRequestRecipients_userId",
				TestPropsValues.getUserId()
			).put(
				"requestRecipientStatus", DSRequestConstants.STATUS_SENT
			).build());

		return dsRequestObjectEntry.getObjectEntryId();
	}

	private FileEntry _addFileEntry(long plid) throws Exception {
		return _portletFileRepository.addPortletFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			Layout.class.getName(), plid,
			DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			FileUtil.getBytes(getClass(), "dependencies/Document.pdf"),
			RandomTestUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF, false);
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, ServiceContext serviceContext,
			Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()), values,
			serviceContext);
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
		long dsRequestId, ThemeDisplay themeDisplay) {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
		mockLiferayPortletRenderRequest.setParameter(
			"dsRequestId", String.valueOf(dsRequestId));

		return mockLiferayPortletRenderRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		return ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(_group.getCompanyId()), _group,
			_portletLayout);
	}

	private void _testRender(long dsRequestId, Layout layout) throws Exception {
		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH,
			_mvcRenderCommand.render(
				_getMockLiferayPortletRenderRequest(
					dsRequestId, _getThemeDisplay()),
				mockLiferayPortletRenderResponse));

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletRenderResponse.getHttpServletResponse();

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertEquals(
			String.valueOf(dsRequestId),
			HttpComponentsUtil.getParameter(
				redirectedURL, "dsRequestId", false));
		Assert.assertFalse(
			redirectedURL,
			redirectedURL.contains("/-/digital_signature/sign/"));
		Assert.assertTrue(
			redirectedURL, redirectedURL.contains(layout.getFriendlyURL()));
	}

	private void _testRenderWhenStateIsPopUp() throws Exception {
		FileEntry fileEntry = _addFileEntry(_portletLayout.getPlid());

		ThemeDisplay themeDisplay = _getThemeDisplay();

		themeDisplay.setStatePopUp(true);

		long dsRequestId = _addDSRequest(fileEntry.getFileEntryId());

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(dsRequestId, themeDisplay);

		List<Object[]> argumentsList = new ArrayList<>();
		List<String> methodNames = new ArrayList<>();

		DSRecipientViewDefinitionManager dsRecipientViewDefinitionManager =
			(DSRecipientViewDefinitionManager)
				ReflectionTestUtil.getAndSetFieldValue(
					_mvcRenderCommand, "_dsRecipientViewDefinitionManager",
					ProxyUtil.newProxyInstance(
						DSRecipientViewDefinitionManager.class.getClassLoader(),
						new Class<?>[] {DSRecipientViewDefinitionManager.class},
						(proxy, method, arguments) -> {
							argumentsList.add(arguments);
							methodNames.add(method.getName());

							return RandomTestUtil.randomString();
						}));

		try {
			Assert.assertEquals(
				"/sign_digital_signature/sign_ds_request.jsp",
				_mvcRenderCommand.render(
					mockLiferayPortletRenderRequest,
					new MockLiferayPortletRenderResponse()));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_mvcRenderCommand, "_dsRecipientViewDefinitionManager",
				dsRecipientViewDefinitionManager);
		}

		Assert.assertEquals(
			Collections.singletonList("addDSRecipientViewDefinition"),
			methodNames);

		DSRequest dsRequest = _dsRequestManager.fetchDSRequest(
			TestPropsValues.getCompanyId(), dsRequestId);

		List<DSRequestRecipient> dsRequestRecipients =
			dsRequest.getDSRequestRecipients();

		DSRequestRecipient expectedDSRequestRecipient = dsRequestRecipients.get(
			0);

		Object[] arguments = argumentsList.get(0);

		DSRequestRecipient dsRequestRecipient =
			(DSRequestRecipient)arguments[2];

		Assert.assertEquals(
			expectedDSRequestRecipient.getEmailAddress(),
			dsRequestRecipient.getEmailAddress());
		Assert.assertEquals(
			expectedDSRequestRecipient.getName(), dsRequestRecipient.getName());

		Assert.assertNotNull(
			mockLiferayPortletRenderRequest.getAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT));
	}

	private void _testRenderWhenUserIsNotDSRequestRecipient() throws Exception {
		FileEntry fileEntry = _addFileEntry(_portletLayout.getPlid());

		ThemeDisplay themeDisplay = _getThemeDisplay();

		themeDisplay.setUser(UserTestUtil.addUser());

		Assert.assertEquals(
			"/sign_digital_signature/view.jsp",
			_mvcRenderCommand.render(
				_getMockLiferayPortletRenderRequest(
					_addDSRequest(fileEntry.getFileEntryId()), themeDisplay),
				new MockLiferayPortletRenderResponse()));
	}

	private CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;

	@Inject
	private CompanyLocalService _companyLocalService;

	private ObjectDefinition _dsRequestDocumentObjectDefinition;

	@Inject
	private DSRequestManager _dsRequestManager;

	private ObjectDefinition _dsRequestObjectDefinition;
	private ObjectDefinition _dsRequestRecipientObjectDefinition;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "mvc.command.name=/digital_signature/sign_ds_request")
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private PortletFileRepository _portletFileRepository;

	private Layout _portletLayout;

}