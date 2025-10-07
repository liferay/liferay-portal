/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;

/**
 * @author Javier Moral
 */
public class LayoutLayoutSetPrototypeLayoutERCUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		processConcurrently(
			StringBundler.concat(
				"select layout1.ctCollectionId, layout1.plid, ",
				"layout2.externalReferenceCode from Layout layout1 inner join ",
				"LayoutSet on layout1.groupId = LayoutSet.groupId and ",
				"layout1.privateLayout = LayoutSet.privateLayout inner join ",
				"LayoutSetPrototype on LayoutSet.layoutSetPrototypeUuid = ",
				"LayoutSetPrototype.uuid_ and LayoutSet.companyId = ",
				"LayoutSetPrototype.companyId inner join Group_ on ",
				"Group_.classPK = LayoutSetPrototype.layoutSetPrototypeId and ",
				"Group_.classNameId = ",
				PortalUtil.getClassNameId(LayoutSetPrototype.class),
				" inner join Layout layout2 on layout2.uuid_ = ",
				"layout1.layoutSetPrototypeLayoutERC and layout2.groupId = ",
				"Group_.groupId and layout2.companyId = Group_.companyId ",
				"where layout1.layoutSetPrototypeLayoutERC is not null and ",
				"layout1.layoutSetPrototypeLayoutERC != ",
				"layout2.externalReferenceCode"),
			"update Layout set layoutSetPrototypeLayoutERC = ? where " +
				"ctCollectionId = ? and plid = ?",
			resultSet -> new Object[] {
				resultSet.getLong("ctCollectionId"), resultSet.getLong("plid"),
				GetterUtil.getString(
					resultSet.getString("externalReferenceCode"))
			},
			(values, preparedStatement) -> {
				preparedStatement.setString(1, GetterUtil.getString(values[2]));
				preparedStatement.setLong(2, GetterUtil.getLong(values[0]));
				preparedStatement.setLong(3, GetterUtil.getLong(values[1]));

				preparedStatement.addBatch();
			},
			null);
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.alterColumnName(
				"Layout", "sourcePrototypeLayoutUuid",
				"layoutSetPrototypeLayoutERC VARCHAR(75) null")
		};
	}

}