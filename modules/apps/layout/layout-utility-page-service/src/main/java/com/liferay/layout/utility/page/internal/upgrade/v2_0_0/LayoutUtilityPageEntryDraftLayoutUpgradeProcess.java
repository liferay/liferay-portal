/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.internal.upgrade.v2_0_0;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Alberto Chaparro
 */
public class LayoutUtilityPageEntryDraftLayoutUpgradeProcess
	extends UpgradeProcess {

	public LayoutUtilityPageEntryDraftLayoutUpgradeProcess(
		LayoutLocalService layoutLocalService) {

		_layoutLocalService = layoutLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select distinct Layout.plid, Layout.classPK, ",
					"Layout.ctCollectionId from Layout inner join ",
					"LayoutUtilityPageEntry on ",
					"LayoutUtilityPageEntry.externalReferenceCode like ",
					"'LFR-%' and LayoutUtilityPageEntry.plid = Layout.classPK ",
					"where Layout.classNameId = ? and Layout.classPK in ",
					"(select classPK from Layout where classNameId = ? group ",
					"by classPK having count(distinct plid) > 1)"))) {

			long classNameId = PortalUtil.getClassNameId(Layout.class);

			preparedStatement.setLong(1, classNameId);
			preparedStatement.setLong(2, classNameId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					Layout draftLayout = _layoutLocalService.fetchDraftLayout(
						resultSet.getLong("classPK"));
					long plid = resultSet.getLong("plid");

					if ((draftLayout == null) ||
						(draftLayout.getPlid() == plid)) {

						continue;
					}

					try (SafeCloseable safeCloseable =
							CTCollectionThreadLocal.
								setCTCollectionIdWithSafeCloseable(
									resultSet.getLong("ctCollectionId"))) {

						_layoutLocalService.deleteLayout(plid);
					}
				}
			}
		}
	}

	private final LayoutLocalService _layoutLocalService;

}