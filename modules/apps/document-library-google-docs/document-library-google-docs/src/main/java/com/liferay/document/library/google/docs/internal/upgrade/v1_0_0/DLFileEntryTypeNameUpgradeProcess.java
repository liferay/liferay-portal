/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.google.docs.internal.upgrade.v1_0_0;

import com.liferay.document.library.google.docs.internal.util.constants.GoogleDocsConstants;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class DLFileEntryTypeNameUpgradeProcess extends UpgradeProcess {

	public DLFileEntryTypeNameUpgradeProcess(
		DLFileEntryTypeLocalService dlFileEntryTypeLocalService) {

		_dlFileEntryTypeLocalService = dlFileEntryTypeLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try {
			ActionableDynamicQuery actionableDynamicQuery =
				_dlFileEntryTypeLocalService.getActionableDynamicQuery();

			actionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> dynamicQuery.add(
					RestrictionsFactoryUtil.eq(
						"fileEntryTypeKey",
						GoogleDocsConstants.DL_FILE_ENTRY_TYPE_KEY)));
			actionableDynamicQuery.setPerformActionMethod(
				(DLFileEntryType dlFileEntryType) ->
					_upgradeGoogleDocsDLFileEntryType(dlFileEntryType));

			actionableDynamicQuery.performActions();
		}
		catch (PortalException portalException) {
			throw new UpgradeException(portalException);
		}
	}

	private void _upgradeGoogleDocsDLFileEntryType(
		DLFileEntryType dlFileEntryType) {

		Locale locale = LocaleUtil.fromLanguageId(
			UpgradeProcessUtil.getDefaultLanguageId(
				dlFileEntryType.getCompanyId()));

		boolean hasDefaultName = Objects.equals(
			dlFileEntryType.getName(locale), "Google Docs");

		if (hasDefaultName) {
			dlFileEntryType.setName(
				GoogleDocsConstants.DL_FILE_ENTRY_TYPE_NAME, locale);
		}

		boolean hasDefaultDescription = Objects.equals(
			dlFileEntryType.getDescription(locale), "Google Docs");

		if (hasDefaultDescription) {
			dlFileEntryType.setDescription(StringPool.BLANK, locale);
		}

		if (hasDefaultName || hasDefaultDescription) {
			_dlFileEntryTypeLocalService.updateDLFileEntryType(dlFileEntryType);
		}
	}

	private final DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

}