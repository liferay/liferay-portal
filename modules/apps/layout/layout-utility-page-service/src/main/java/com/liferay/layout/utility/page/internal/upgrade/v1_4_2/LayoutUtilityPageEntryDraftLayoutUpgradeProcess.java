/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.internal.upgrade.v1_4_2;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

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
		long classNameId = PortalUtil.getClassNameId(Layout.class);

		for (long classPK : _getClassPKs(classNameId)) {
			_deleteDuplicatedDraftLayouts(classNameId, classPK);
		}
	}

	private void _deleteDuplicatedDraftLayouts(long classNameId, long classPK)
		throws Exception {

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(classPK);

		if (draftLayout == null) {
			return;
		}

		for (long plid : _getPlids(classNameId, classPK)) {
			if (plid == draftLayout.getPlid()) {
				continue;
			}

			_layoutLocalService.deleteLayout(plid);
		}
	}

	private List<Long> _getClassPKs(long classNameId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select Layout.classPK from Layout inner join ",
					"LayoutUtilityPageEntry on ",
					"LayoutUtilityPageEntry.externalReferenceCode like ",
					"'LFR-%' and LayoutUtilityPageEntry.plid = Layout.classPK ",
					"where Layout.classNameId = ? group by Layout.classPK ",
					"having count(distinct Layout.plid) > 1"))) {

			preparedStatement.setLong(1, classNameId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				List<Long> classPKs = new ArrayList<>();

				while (resultSet.next()) {
					classPKs.add(resultSet.getLong("classPK"));
				}

				return classPKs;
			}
		}
	}

	private List<Long> _getPlids(long classNameId, long classPK)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select distinct plid from Layout where classNameId = ? and " +
					"classPK = ?")) {

			preparedStatement.setLong(1, classNameId);
			preparedStatement.setLong(2, classPK);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				List<Long> plids = new ArrayList<>();

				while (resultSet.next()) {
					plids.add(resultSet.getLong("plid"));
				}

				return plids;
			}
		}
	}

	private final LayoutLocalService _layoutLocalService;

}