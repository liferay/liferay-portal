/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.password.policies.admin.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.lar.BaseCompanyStagedModelDataHandlerTestCase;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.PasswordPolicyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.passwordpoliciesadmin.util.test.PasswordPolicyTestUtil;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Daniela Zapata Riesco
 */
@RunWith(Arquillian.class)
public class PasswordPolicyCompanyStagedModelDataHandlerTest
	extends BaseCompanyStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return PasswordPolicyTestUtil.addPasswordPolicy(
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return PasswordPolicyLocalServiceUtil.
			getPasswordPolicyByUuidAndCompanyId(uuid, group.getCompanyId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return PasswordPolicy.class;
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		// super.validateImportedStagedModel(stagedModel, importedStagedModel);

		PasswordPolicy passwordPolicy = (PasswordPolicy)stagedModel;
		PasswordPolicy importedPasswordPolicy =
			(PasswordPolicy)importedStagedModel;

		Assert.assertEquals(
			passwordPolicy.isDefaultPolicy(),
			importedPasswordPolicy.isDefaultPolicy());
		Assert.assertEquals(
			passwordPolicy.getName(), importedPasswordPolicy.getName());
		Assert.assertEquals(
			passwordPolicy.getDescription(),
			importedPasswordPolicy.getDescription());
		Assert.assertEquals(
			passwordPolicy.isChangeable(),
			importedPasswordPolicy.isChangeable());
		Assert.assertEquals(
			passwordPolicy.isChangeRequired(),
			importedPasswordPolicy.isChangeRequired());
		Assert.assertEquals(
			passwordPolicy.getMinAge(), importedPasswordPolicy.getMinAge());
		Assert.assertEquals(
			passwordPolicy.isCheckSyntax(),
			importedPasswordPolicy.isCheckSyntax());
		Assert.assertEquals(
			passwordPolicy.isAllowDictionaryWords(),
			importedPasswordPolicy.isAllowDictionaryWords());
		Assert.assertEquals(
			passwordPolicy.getMinAlphanumeric(),
			importedPasswordPolicy.getMinAlphanumeric());
		Assert.assertEquals(
			passwordPolicy.getMinLength(),
			importedPasswordPolicy.getMinLength());
		Assert.assertEquals(
			passwordPolicy.getMinLowerCase(),
			importedPasswordPolicy.getMinLowerCase());
		Assert.assertEquals(
			passwordPolicy.getMinNumbers(),
			importedPasswordPolicy.getMinNumbers());
		Assert.assertEquals(
			passwordPolicy.getMinSymbols(),
			importedPasswordPolicy.getMinSymbols());
		Assert.assertEquals(
			passwordPolicy.getMinUpperCase(),
			importedPasswordPolicy.getMinUpperCase());
		Assert.assertEquals(
			passwordPolicy.getRegex(), importedPasswordPolicy.getRegex());
		Assert.assertEquals(
			passwordPolicy.isHistory(), importedPasswordPolicy.isHistory());
		Assert.assertEquals(
			passwordPolicy.getHistoryCount(),
			importedPasswordPolicy.getHistoryCount());
		Assert.assertEquals(
			passwordPolicy.isExpireable(),
			importedPasswordPolicy.isExpireable());
		Assert.assertEquals(
			passwordPolicy.getMaxAge(), importedPasswordPolicy.getMaxAge());
		Assert.assertEquals(
			passwordPolicy.getWarningTime(),
			importedPasswordPolicy.getWarningTime());
		Assert.assertEquals(
			passwordPolicy.getGraceLimit(),
			importedPasswordPolicy.getGraceLimit());
		Assert.assertEquals(
			passwordPolicy.isLockout(), importedPasswordPolicy.isLockout());
		Assert.assertEquals(
			passwordPolicy.getMaxFailure(),
			importedPasswordPolicy.getMaxFailure());
		Assert.assertEquals(
			passwordPolicy.getLockoutDuration(),
			importedPasswordPolicy.getLockoutDuration());
		Assert.assertEquals(
			passwordPolicy.isRequireUnlock(),
			importedPasswordPolicy.isRequireUnlock());
		Assert.assertEquals(
			passwordPolicy.getResetFailureCount(),
			importedPasswordPolicy.getResetFailureCount());
		Assert.assertEquals(
			passwordPolicy.getResetTicketMaxAge(),
			importedPasswordPolicy.getResetTicketMaxAge());
	}

}