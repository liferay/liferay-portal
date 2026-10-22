/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.ByteArrayOutputStream;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian I. Kim
 */
@FeatureFlag("LPD-69290")
@RunWith(Arquillian.class)
public class AddDSEnvelopeMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testDoTransactionalCommand() throws Exception {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		int days = RandomTestUtil.randomInt();

		mockLiferayResourceRequest.setParameter(
			"expireAfter", String.valueOf(days));

		mockLiferayResourceRequest.setParameter("recipients", "[]");

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						DigitalSignatureConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"embeddedViewEnabled", true
						).build())) {

			Queue<Object[]> argumentsQueue = new LinkedList<>();
			String dsEnvelopeId = RandomTestUtil.randomString();
			long dsRequestId = RandomTestUtil.randomLong();

			DSRequestManager dsRequestManager =
				(DSRequestManager)ReflectionTestUtil.getAndSetFieldValue(
					_mvcResourceCommand, "_dsRequestManager",
					ProxyUtil.newProxyInstance(
						DSRequestManager.class.getClassLoader(),
						new Class<?>[] {DSRequestManager.class},
						(proxy, method, arguments) -> {
							if (Objects.equals(
									method.getName(), "addDSRequest")) {

								DSEnvelope dsEnvelope =
									(DSEnvelope)arguments[1];

								Assert.assertEquals(
									days, dsEnvelope.getExpireAfter());

								return new DSRequest(
									TestPropsValues.getCompanyId(), null,
									dsRequestId, Collections.emptyList(),
									dsEnvelopeId, Collections.emptyList(), 0,
									null, Collections.emptyMap());
							}

							if (Objects.equals(
									method.getName(),
									"sendDSRequestNotifications")) {

								argumentsQueue.add(arguments);
							}

							return null;
						}));

			MockLiferayResourceResponse mockLiferayResourceResponse =
				new MockLiferayResourceResponse();

			try {
				_invokeTransactionalCommand(
					mockLiferayResourceRequest, mockLiferayResourceResponse);
			}
			finally {
				ReflectionTestUtil.setFieldValue(
					_mvcResourceCommand, "_dsRequestManager", dsRequestManager);
			}

			ByteArrayOutputStream byteArrayOutputStream =
				(ByteArrayOutputStream)
					mockLiferayResourceResponse.getPortletOutputStream();

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				byteArrayOutputStream.toString());

			Assert.assertEquals(
				dsEnvelopeId, jsonObject.getString("dsEnvelopeId"));

			Assert.assertEquals(
				argumentsQueue.toString(), 1, argumentsQueue.size());

			Object[] arguments = argumentsQueue.poll();

			Assert.assertEquals(dsRequestId, arguments[0]);
		}

		_testDoTransactionalCommandWithFeatureFlagDisabled();
		_testDoTransactionalCommandWithoutFileEntryPermission();
	}

	private void _invokeTransactionalCommand(
			MockLiferayResourceRequest mockLiferayResourceRequest,
			MockLiferayResourceResponse mockLiferayResourceResponse)
		throws Exception {

		ReflectionTestUtil.invoke(
			_mvcResourceCommand, "doTransactionalCommand",
			new Class<?>[] {ResourceRequest.class, ResourceResponse.class},
			mockLiferayResourceRequest, mockLiferayResourceResponse);
	}

	private void _testDoTransactionalCommandWithFeatureFlagDisabled()
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.setParameter("expireAfter", "5");
		mockLiferayResourceRequest.setParameter("recipients", "[]");

		String dsEnvelopeId = RandomTestUtil.randomString();

		DSEnvelopeManager dsEnvelopeManager =
			(DSEnvelopeManager)ReflectionTestUtil.getAndSetFieldValue(
				_mvcResourceCommand, "_dsEnvelopeManager",
				ProxyUtil.newProxyInstance(
					DSEnvelopeManager.class.getClassLoader(),
					new Class<?>[] {DSEnvelopeManager.class},
					(proxy, method, arguments) -> {
						DSEnvelope dsEnvelope = (DSEnvelope)arguments[2];

						Assert.assertEquals(0, dsEnvelope.getExpireAfter());

						dsEnvelope.setDSEnvelopeId(dsEnvelopeId);

						return dsEnvelope;
					}));

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-69290");
			CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						DigitalSignatureConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"embeddedViewEnabled", true
						).build())) {

			_invokeTransactionalCommand(
				mockLiferayResourceRequest, mockLiferayResourceResponse);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_mvcResourceCommand, "_dsEnvelopeManager", dsEnvelopeManager);
		}

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			byteArrayOutputStream.toString());

		Assert.assertEquals(dsEnvelopeId, jsonObject.getString("dsEnvelopeId"));
	}

	private void _testDoTransactionalCommandWithoutFileEntryPermission()
		throws Exception {

		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(_user);

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF,
			FileUtil.getBytes(getClass(), "dependencies/Document.pdf"), null,
			null, null, serviceContext);

		mockLiferayResourceRequest.setParameter(
			"fileEntryIds", String.valueOf(fileEntry.getFileEntryId()));

		mockLiferayResourceRequest.setParameter("recipients", "[]");

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						DigitalSignatureConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"embeddedViewEnabled", false
						).build())) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _invokeTransactionalCommand(
					mockLiferayResourceRequest,
					new MockLiferayResourceResponse()));
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						DigitalSignatureConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"embeddedViewEnabled", true
						).build())) {

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _invokeTransactionalCommand(
					mockLiferayResourceRequest,
					new MockLiferayResourceResponse()));

			Role role = _roleLocalService.getRole(
				TestPropsValues.getCompanyId(), RoleConstants.USER);

			_resourcePermissionLocalService.setResourcePermissions(
				TestPropsValues.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()), role.getRoleId(),
				new String[] {ActionKeys.VIEW});

			themeDisplay.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user));

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _invokeTransactionalCommand(
					mockLiferayResourceRequest,
					new MockLiferayResourceResponse()));

			_resourcePermissionLocalService.setResourcePermissions(
				TestPropsValues.getCompanyId(), DLFileEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(fileEntry.getFileEntryId()), role.getRoleId(),
				new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

			themeDisplay.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user));

			User recipientUser = UserTestUtil.addUser();

			mockLiferayResourceRequest.setParameter(
				"recipients",
				JSONUtil.putAll(
					JSONUtil.put(
						"email", recipientUser.getEmailAddress()
					).put(
						"fullName", recipientUser.getFullName()
					)
				).toString());

			Assert.assertThrows(
				PrincipalException.MustHavePermission.class,
				() -> _invokeTransactionalCommand(
					mockLiferayResourceRequest,
					new MockLiferayResourceResponse()));

			mockLiferayResourceRequest.setParameter("recipients", "[]");

			String dsEnvelopeId = RandomTestUtil.randomString();

			DSRequestManager dsRequestManager =
				(DSRequestManager)ReflectionTestUtil.getAndSetFieldValue(
					_mvcResourceCommand, "_dsRequestManager",
					ProxyUtil.newProxyInstance(
						DSRequestManager.class.getClassLoader(),
						new Class<?>[] {DSRequestManager.class},
						(proxy, method, arguments) -> {
							if (Objects.equals(
									method.getName(), "addDSRequest")) {

								return new DSRequest(
									TestPropsValues.getCompanyId(), null, 0,
									Collections.emptyList(), dsEnvelopeId,
									Collections.emptyList(), 0, null,
									Collections.emptyMap());
							}

							return null;
						}));

			MockLiferayResourceResponse mockLiferayResourceResponse =
				new MockLiferayResourceResponse();

			try {
				_invokeTransactionalCommand(
					mockLiferayResourceRequest, mockLiferayResourceResponse);
			}
			finally {
				ReflectionTestUtil.setFieldValue(
					_mvcResourceCommand, "_dsRequestManager", dsRequestManager);
			}

			ByteArrayOutputStream byteArrayOutputStream =
				(ByteArrayOutputStream)
					mockLiferayResourceResponse.getPortletOutputStream();

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				byteArrayOutputStream.toString());

			Assert.assertEquals(
				dsEnvelopeId, jsonObject.getString("dsEnvelopeId"));
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject(filter = "mvc.command.name=/digital_signature/add_ds_envelope")
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

}