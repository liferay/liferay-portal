/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.object.system.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.test.util.BaseSystemObjectDefinitionManagerTestCase;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Sousa
 */
@RunWith(Arquillian.class)
public class AccountEntrySystemObjectDefinitionManagerTest
	extends BaseSystemObjectDefinitionManagerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	@Test
	@TestInfo("LPD-62555")
	public void testGetOrAddEmptyBaseModel() throws Exception {
		super.testGetOrAddEmptyBaseModel();
	}

	@Override
	protected void assertGetOrAddEmptyBaseModelWithoutPermissions(
		BaseModel<?> baseModel, User user) {

		AccountEntry accountEntry = (AccountEntry)baseModel;

		AssertUtils.assertFailure(
			PortalException.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have ", PortletKeys.PORTAL,
				", ADD_ACCOUNT_ENTRY permission for null "),
			() -> systemObjectDefinitionManager.getOrAddEmptyBaseModel(
				RandomTestUtil.randomString(), user));
		AssertUtils.assertFailure(
			PortalException.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have VIEW permission for ",
				AccountEntry.class.getName(), " ",
				accountEntry.getAccountEntryId()),
			() -> systemObjectDefinitionManager.getOrAddEmptyBaseModel(
				accountEntry.getExternalReferenceCode(), user));
	}

	@Override
	protected void assertGetOrAddEmptyBaseModelWithPermissions(
		BaseModel<?> baseModel) {

		AccountEntry accountEntry = (AccountEntry)baseModel;

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, accountEntry.getStatus());
	}

	@Override
	protected String getSystemObjectDefinitionName() {
		return "AccountEntry";
	}

}