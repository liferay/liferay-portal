/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.internal.model.listener;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Díaz
 */
@Component(service = ModelListener.class)
public class CompanyModelListener extends BaseModelListener<Company> {

	@Override
	public void onAfterRemove(Company company) throws ModelListenerException {
		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				_deleteAuditEvents(company);

				return null;
			});
	}

	private void _deleteAuditEvents(Company company) throws PortalException {
		ActionableDynamicQuery actionableDynamicQuery =
			_auditEventLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"companyId", company.getCompanyId())));
		actionableDynamicQuery.setPerformActionMethod(
			auditEvent -> _auditEventLocalService.deleteAuditEvent(
				(AuditEvent)auditEvent));

		actionableDynamicQuery.performActions();
	}

	@Reference
	private AuditEventLocalService _auditEventLocalService;

}