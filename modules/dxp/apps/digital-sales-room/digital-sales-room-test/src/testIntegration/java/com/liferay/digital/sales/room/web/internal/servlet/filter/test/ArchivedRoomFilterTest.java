/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.servlet.filter.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.sales.room.test.util.DSRTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class ArchivedRoomFilterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		DSRTestUtil.getOrAddGroup();

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, ServiceContextTestUtil.getServiceContext());
		_objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", TestPropsValues.getCompanyId());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_portalURL = StringBundler.concat(
			"http://", company.getVirtualHostname(), ":",
			PortalUtil.getPortalServerPort(false));
	}

	@FeatureFlag("LPD-82960")
	@Test
	@TestInfo("LPD-97751")
	public void testProcessFilter() throws Exception {
		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"name",
				StringUtil.toLowerCase("A" + RandomTestUtil.randomString())
			).put(
				"r_accountToDSRRooms_accountEntryId",
				_accountEntry.getAccountEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Group group = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(),
			_classNameLocalService.getClassNameId(
				_objectDefinition.getClassName()),
			objectEntry.getObjectEntryId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		String friendlyURL =
			PortalUtil.getPathFriendlyURLPublic() + group.getFriendlyURL();

		Assert.assertNotEquals(_portalURL, _getLocation(friendlyURL));

		String layoutURL =
			PortalUtil.getPathMain() + "/portal/layout?p_l_id=" +
				layout.getPlid();

		Assert.assertNotEquals(_portalURL, _getLocation(layoutURL));

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"roomStatus", WorkflowConstants.STATUS_INACTIVE
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(_portalURL, _getLocation(friendlyURL));
		Assert.assertEquals(_portalURL, _getLocation(layoutURL));
	}

	private String _getLocation(String path) throws Exception {
		Http.Options options = new Http.Options();

		options.setFollowRedirects(false);
		options.setLocation(_portalURL + path);

		_http.URLtoByteArray(options);

		Http.Response response = options.getResponse();

		if (response.getResponseCode() !=
				HttpServletResponse.SC_MOVED_TEMPORARILY) {

			return null;
		}

		return response.getHeader("Location");
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Http _http;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private String _portalURL;

}