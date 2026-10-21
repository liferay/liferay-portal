/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.constants.AuditEventConstants;
import com.liferay.portal.security.audit.storage.exception.AuditEventPseudonymFieldValueException;
import com.liferay.portal.security.audit.storage.model.AuditEventPseudonymField;
import com.liferay.portal.security.audit.storage.service.base.AuditEventPseudonymFieldLocalServiceBaseImpl;

import java.util.Date;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.portal.security.audit.storage.model.AuditEventPseudonymField",
	service = AopService.class
)
public class AuditEventPseudonymFieldLocalServiceImpl
	extends AuditEventPseudonymFieldLocalServiceBaseImpl {

	@Override
	public AuditEventPseudonymField addAuditEventPseudonymField(
			long companyId, String contextName, String name, String value)
		throws PortalException {

		if (Validator.isBlank(value)) {
			throw new AuditEventPseudonymFieldValueException("Value is blank");
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			AuditEventPseudonymField.class.getName(), "value");

		if (value.length() > maxLength) {
			throw new AuditEventPseudonymFieldValueException(
				"Maximum length of value exceeded");
		}

		if (Validator.isBlank(contextName)) {
			contextName = AuditEventConstants.CONTEXT_NAME_INSTANCE;
		}

		String valueHash = DigesterUtil.digestHex(DigesterUtil.SHA_256, value);

		AuditEventPseudonymField auditEventPseudonymField =
			auditEventPseudonymFieldPersistence.fetchByC_CN_N_VH(
				companyId, contextName, name, valueHash);

		if (auditEventPseudonymField != null) {
			return auditEventPseudonymField;
		}

		long auditEventPseudonymFieldId = counterLocalService.increment();

		auditEventPseudonymField = auditEventPseudonymFieldPersistence.create(
			auditEventPseudonymFieldId);

		auditEventPseudonymField.setCompanyId(companyId);
		auditEventPseudonymField.setCreateDate(new Date());
		auditEventPseudonymField.setContextName(contextName);
		auditEventPseudonymField.setName(name);
		auditEventPseudonymField.setValue(value);
		auditEventPseudonymField.setValueHash(valueHash);

		return auditEventPseudonymFieldPersistence.update(
			auditEventPseudonymField);
	}

}