/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.seo.studio.rest.client.dto.v1_0.Autofix;
import com.liferay.seo.studio.rest.client.http.HttpInvoker;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brooke Dalton
 */
@FeatureFlag("LPD-44511")
@RunWith(Arquillian.class)
public class AutofixResourceTest extends BaseAutofixResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.seo.studio.site.initializer");

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
	public void testPostAutofix() throws Exception {
		_testPostAutofixNotFoundWhenInstanceNotRegistered();
		_testPostAutofixUnrecognizedInsightType();
	}

	private void _addSEOStudioInstanceObjectEntry(String hostname)
		throws Exception {

		long companyId = testCompany.getCompanyId();

		User user = UserTestUtil.getAdminUser(companyId);

		if (_accountEntry == null) {
			_accountEntry = _accountEntryLocalService.addAccountEntry(
				null, user.getUserId(),
				AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				RandomTestUtil.randomString(), null, new String[0], null, null,
				null, AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext());
		}

		ObjectDefinition instanceObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_INSTANCE", companyId);

		ObjectEntry seoStudioInstanceObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				0L, user.getUserId(),
				instanceObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"hostname", hostname
				).put(
					"name", RandomTestUtil.randomString()
				).put(
					"r_accountToSEOStudioInstances_accountEntryId",
					_accountEntry.getAccountEntryId()
				).put(
					"state", "active"
				).build(),
				ServiceContextTestUtil.getServiceContext());

		_objectEntries.add(seoStudioInstanceObjectEntry);
	}

	private String _randomHostname() {
		return StringUtil.toLowerCase(RandomTestUtil.randomString());
	}

	private void _testPostAutofixNotFoundWhenInstanceNotRegistered()
		throws Exception {

		Autofix autofix = randomAutofix();

		autofix.setInsightType("missingOrEmptyTitleTag");
		autofix.setPageURL("https://" + _randomHostname() + "/page");

		HttpInvoker.HttpResponse httpResponse =
			autofixResource.postAutofixHttpResponse(autofix);

		assertHttpResponseStatusCode(404, httpResponse);
	}

	private void _testPostAutofixUnrecognizedInsightType() throws Exception {
		String hostname = _randomHostname();

		_addSEOStudioInstanceObjectEntry(hostname);

		Autofix autofix = randomAutofix();

		autofix.setInsightType(RandomTestUtil.randomString());
		autofix.setPageURL("https://" + hostname + "/page");

		HttpInvoker.HttpResponse httpResponse =
			autofixResource.postAutofixHttpResponse(autofix);

		assertHttpResponseStatusCode(400, httpResponse);
	}

	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;

	@Inject
	private static SiteInitializerRegistry _siteInitializerRegistry;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private final List<ObjectEntry> _objectEntries = new ArrayList<>();

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}