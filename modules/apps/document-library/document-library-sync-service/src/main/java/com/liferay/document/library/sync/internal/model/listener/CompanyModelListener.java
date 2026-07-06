/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.sync.internal.model.listener;

import com.liferay.document.library.sync.model.DLSyncEvent;
import com.liferay.document.library.sync.service.DLSyncEventLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;

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
				_deleteDLSyncEvent(company);

				return null;
			});
	}

	private void _deleteDLSyncEvent(Company company) throws PortalException {
		ActionableDynamicQuery actionableDynamicQuery =
			_dlSyncEventLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"companyId", company.getCompanyId())));
		actionableDynamicQuery.setPerformActionMethod(
			dlSyncEvent -> _dlSyncEventLocalService.deleteDLSyncEvent(
				(DLSyncEvent)dlSyncEvent));

		actionableDynamicQuery.performActions();
	}

	@Reference
	private DLSyncEventLocalService _dlSyncEventLocalService;

}