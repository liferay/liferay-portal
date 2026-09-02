/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.osb.patcher.constants.PatcherBuildConstants;
import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.util.PatcherBuildUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michael Prigge
 */
@RunWith(Arquillian.class)
public class PatcherBuildUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.osb.patcher.service"));

	@Before
	public void setUp() throws Exception {
		_user = TestPropsValues.getUser();
	}

	@Test
	public void testUpdatePatcherBuildFixesReusingExistingHotfixSkipsCompile()
		throws Exception {

		PatcherBuild patcherBuild = _addPatcherBuild();

		PatcherBuildUtil.updatePatcherBuildFixes(
			_user, patcherBuild,
			Collections.singletonList(patcherBuild.getPatcherFixId()), true,
			true);

		PatcherBuild reloadedPatcherBuild =
			PatcherBuildLocalServiceUtil.getPatcherBuild(
				patcherBuild.getPatcherBuildId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_BUILD_MERGING,
			reloadedPatcherBuild.getStatus());
	}

	@Test
	public void testUpdatePatcherBuildFixesSendsJenkinsRequest()
		throws Exception {

		PatcherBuild patcherBuild = _addPatcherBuild();

		PatcherBuildUtil.updatePatcherBuildFixes(
			_user, patcherBuild,
			Collections.singletonList(patcherBuild.getPatcherFixId()), false,
			true);

		PatcherBuild reloadedPatcherBuild =
			PatcherBuildLocalServiceUtil.getPatcherBuild(
				patcherBuild.getPatcherBuildId());

		Assert.assertTrue(
			Validator.isNotNull(reloadedPatcherBuild.getRequestKey()));
		Assert.assertEquals(
			WorkflowConstants.STATUS_BUILD_COMPILING,
			reloadedPatcherBuild.getStatus());
	}

	@Test
	public void testUpdatePatcherBuildFixesSkipsJenkinsRequest()
		throws Exception {

		PatcherBuild patcherBuild = _addPatcherBuild();

		PatcherBuildUtil.updatePatcherBuildFixes(
			_user, patcherBuild,
			Collections.singletonList(patcherBuild.getPatcherFixId()), false,
			false);

		PatcherBuild reloadedPatcherBuild =
			PatcherBuildLocalServiceUtil.getPatcherBuild(
				patcherBuild.getPatcherBuildId());

		Assert.assertTrue(
			Validator.isNull(reloadedPatcherBuild.getRequestKey()));
		Assert.assertEquals(
			WorkflowConstants.STATUS_BUILD_COMPILING,
			reloadedPatcherBuild.getStatus());
	}

	private PatcherAccount _addPatcherAccount() throws Exception {
		PatcherAccount patcherAccount =
			PatcherAccountLocalServiceUtil.createPatcherAccount(
				CounterLocalServiceUtil.increment());

		patcherAccount.setCompanyId(_user.getCompanyId());
		patcherAccount.setUserId(_user.getUserId());
		patcherAccount.setUserName(_user.getFullName());
		patcherAccount.setCreateDate(new Date());
		patcherAccount.setModifiedDate(new Date());
		patcherAccount.setAccountEntryCode(RandomTestUtil.randomString());

		return PatcherAccountLocalServiceUtil.addPatcherAccount(patcherAccount);
	}

	private PatcherBuild _addPatcherBuild() throws Exception {
		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.createPatcherBuild(
				CounterLocalServiceUtil.increment());

		PatcherAccount patcherAccount = _addPatcherAccount();

		PatcherProjectVersion patcherProjectVersion =
			_addPatcherProjectVersion();

		PatcherFix patcherFix = _addPatcherFix(patcherProjectVersion);

		patcherBuild.setCompanyId(_user.getCompanyId());
		patcherBuild.setUserId(_user.getUserId());
		patcherBuild.setUserName(_user.getFullName());
		patcherBuild.setCreateDate(new Date());
		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setPatcherAccountId(patcherAccount.getPatcherAccountId());
		patcherBuild.setPatcherFixId(patcherFix.getPatcherFixId());
		patcherBuild.setPatcherProjectVersionId(
			patcherProjectVersion.getPatcherProjectVersionId());
		patcherBuild.setChildBuild(false);
		patcherBuild.setName(RandomTestUtil.randomString());
		patcherBuild.setSupportTicket(RandomTestUtil.randomString());
		patcherBuild.setType(PatcherBuildConstants.TYPE_HOTFIX);
		patcherBuild.setStatus(WorkflowConstants.STATUS_BUILD_MERGING);
		patcherBuild.setStatusByUserId(_user.getUserId());
		patcherBuild.setStatusByUserName(_user.getFullName());
		patcherBuild.setStatusDate(new Date());

		return PatcherBuildLocalServiceUtil.addPatcherBuild(patcherBuild);
	}

	private PatcherFix _addPatcherFix(
			PatcherProjectVersion patcherProjectVersion)
		throws Exception {

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.createPatcherFix(
			CounterLocalServiceUtil.increment());

		patcherFix.setCompanyId(_user.getCompanyId());
		patcherFix.setUserId(_user.getUserId());
		patcherFix.setUserName(_user.getFullName());
		patcherFix.setCreateDate(new Date());
		patcherFix.setModifiedDate(new Date());
		patcherFix.setPatcherProjectVersionId(
			patcherProjectVersion.getPatcherProjectVersionId());
		patcherFix.setGitHash(RandomTestUtil.randomString());
		patcherFix.setName(RandomTestUtil.randomString());

		return PatcherFixLocalServiceUtil.addPatcherFix(patcherFix);
	}

	private PatcherProductVersion _addPatcherProductVersion() throws Exception {
		PatcherProductVersion patcherProductVersion =
			PatcherProductVersionLocalServiceUtil.createPatcherProductVersion(
				CounterLocalServiceUtil.increment());

		patcherProductVersion.setCompanyId(_user.getCompanyId());
		patcherProductVersion.setUserId(_user.getUserId());
		patcherProductVersion.setUserName(_user.getFullName());
		patcherProductVersion.setCreateDate(new Date());
		patcherProductVersion.setModifiedDate(new Date());
		patcherProductVersion.setFixDeliveryMethod(
			PatcherProductVersionConstants.
				TYPE_FIX_DELIVERY_METHOD_FIX_PACK_30);
		patcherProductVersion.setModuleFolderName(
			RandomTestUtil.randomString());
		patcherProductVersion.setName(RandomTestUtil.randomString());

		return PatcherProductVersionLocalServiceUtil.addPatcherProductVersion(
			patcherProductVersion);
	}

	private PatcherProjectVersion _addPatcherProjectVersion() throws Exception {
		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.createPatcherProjectVersion(
				CounterLocalServiceUtil.increment());

		PatcherProductVersion patcherProductVersion =
			_addPatcherProductVersion();

		patcherProjectVersion.setCompanyId(_user.getCompanyId());
		patcherProjectVersion.setUserId(_user.getUserId());
		patcherProjectVersion.setUserName(_user.getFullName());
		patcherProjectVersion.setCreateDate(new Date());
		patcherProjectVersion.setModifiedDate(new Date());
		patcherProjectVersion.setPatcherProductVersionId(
			patcherProductVersion.getPatcherProductVersionId());
		patcherProjectVersion.setCombinedBranch(false);
		patcherProjectVersion.setCommittish(RandomTestUtil.randomString());
		patcherProjectVersion.setName(RandomTestUtil.randomString());

		return PatcherProjectVersionLocalServiceUtil.addPatcherProjectVersion(
			patcherProjectVersion);
	}

	private User _user;

}