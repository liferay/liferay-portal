/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.model.listener;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.search.experiences.internal.util.SXPElementUtil;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;
import com.liferay.search.experiences.service.SXPElementLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(enabled = false, service = ModelListener.class)
public class CompanyModelListener extends BaseModelListener<Company> {

	@Override
	public void onAfterCreate(Company company) {
		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				try {
					SXPElementUtil.addSXPElements(
						company, _sxpElementLocalService);
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}

				return null;
			});
	}

	@Override
	public void onAfterRemove(Company company) {
		try {
			_sxpBlueprintLocalService.deleteCompanySXPBlueprints(
				company.getCompanyId());

			_sxpElementLocalService.deleteCompanySXPElements(
				company.getCompanyId());
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyModelListener.class);

	@Reference
	private SXPBlueprintLocalService _sxpBlueprintLocalService;

	@Reference
	private SXPElementLocalService _sxpElementLocalService;

}