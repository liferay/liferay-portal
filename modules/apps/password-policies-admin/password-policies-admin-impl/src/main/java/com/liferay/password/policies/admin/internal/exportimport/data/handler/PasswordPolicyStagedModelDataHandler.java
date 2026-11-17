/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.password.policies.admin.internal.exportimport.data.handler;

import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.util.ScopeUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniela Zapata Riesco
 */
@Component(service = StagedModelDataHandler.class)
public class PasswordPolicyStagedModelDataHandler
	extends BaseStagedModelDataHandler<PasswordPolicy> {

	public static final String[] CLASS_NAMES = {PasswordPolicy.class.getName()};

	@Override
	public void deleteStagedModel(PasswordPolicy passwordPolicy)
		throws PortalException {

		_passwordPolicyLocalService.deletePasswordPolicy(passwordPolicy);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		PasswordPolicy passwordPolicy =
			_passwordPolicyLocalService.fetchPasswordPolicyByUuidAndCompanyId(
				uuid, group.getCompanyId());

		if (passwordPolicy != null) {
			deleteStagedModel(passwordPolicy);
		}
	}

	@Override
	public List<PasswordPolicy> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return ListUtil.fromArray(
			_passwordPolicyLocalService.fetchPasswordPolicyByUuidAndCompanyId(
				uuid, companyId));
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			PasswordPolicy passwordPolicy)
		throws Exception {

		Element passwordPolicyElement = portletDataContext.getExportDataElement(
			passwordPolicy);

		portletDataContext.addClassedModel(
			passwordPolicyElement,
			ExportImportPathUtil.getModelPath(passwordPolicy), passwordPolicy);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			PasswordPolicy passwordPolicy)
		throws Exception {

		ScopeUtil.validateInstanceScoped(portletDataContext);

		long userId = portletDataContext.getUserId(
			passwordPolicy.getUserUuid());

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			passwordPolicy);

		PasswordPolicy existingPasswordPolicy =
			_passwordPolicyLocalService.fetchPasswordPolicyByUuidAndCompanyId(
				passwordPolicy.getUuid(), portletDataContext.getCompanyId());

		if (existingPasswordPolicy == null) {
			existingPasswordPolicy =
				_passwordPolicyLocalService.fetchPasswordPolicy(
					portletDataContext.getCompanyId(),
					passwordPolicy.getName());
		}

		PasswordPolicy importedPasswordPolicy = null;

		if (existingPasswordPolicy == null) {
			serviceContext.setUuid(passwordPolicy.getUuid());

			importedPasswordPolicy =
				_passwordPolicyLocalService.addPasswordPolicy(
					userId, passwordPolicy.isDefaultPolicy(),
					passwordPolicy.getName(), passwordPolicy.getDescription(),
					passwordPolicy.isChangeable(),
					passwordPolicy.isChangeRequired(),
					passwordPolicy.getMinAge(), passwordPolicy.isCheckSyntax(),
					passwordPolicy.isAllowDictionaryWords(),
					passwordPolicy.getMinAlphanumeric(),
					passwordPolicy.getMinLength(),
					passwordPolicy.getMinLowerCase(),
					passwordPolicy.getMinNumbers(),
					passwordPolicy.getMinSymbols(),
					passwordPolicy.getMinUpperCase(), passwordPolicy.getRegex(),
					passwordPolicy.isHistory(),
					passwordPolicy.getHistoryCount(),
					passwordPolicy.isExpireable(), passwordPolicy.getMaxAge(),
					passwordPolicy.getWarningTime(),
					passwordPolicy.getGraceLimit(), passwordPolicy.isLockout(),
					passwordPolicy.getMaxFailure(),
					passwordPolicy.getLockoutDuration(),
					passwordPolicy.getResetFailureCount(),
					passwordPolicy.getResetTicketMaxAge(), serviceContext);
		}
		else {
			importedPasswordPolicy =
				_passwordPolicyLocalService.updatePasswordPolicy(
					existingPasswordPolicy.getPasswordPolicyId(),
					passwordPolicy.getName(), passwordPolicy.getDescription(),
					passwordPolicy.isChangeable(),
					passwordPolicy.isChangeRequired(),
					passwordPolicy.getMinAge(), passwordPolicy.isCheckSyntax(),
					passwordPolicy.isAllowDictionaryWords(),
					passwordPolicy.getMinAlphanumeric(),
					passwordPolicy.getMinLength(),
					passwordPolicy.getMinLowerCase(),
					passwordPolicy.getMinNumbers(),
					passwordPolicy.getMinSymbols(),
					passwordPolicy.getMinUpperCase(), passwordPolicy.getRegex(),
					passwordPolicy.isHistory(),
					passwordPolicy.getHistoryCount(),
					passwordPolicy.isExpireable(), passwordPolicy.getMaxAge(),
					passwordPolicy.getWarningTime(),
					passwordPolicy.getGraceLimit(), passwordPolicy.isLockout(),
					passwordPolicy.getMaxFailure(),
					passwordPolicy.getLockoutDuration(),
					passwordPolicy.getResetFailureCount(),
					passwordPolicy.getResetTicketMaxAge(), serviceContext);
		}

		portletDataContext.importClassedModel(
			passwordPolicy, importedPasswordPolicy);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private PasswordPolicyLocalService _passwordPolicyLocalService;

}