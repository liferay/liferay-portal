/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v3_0_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

/**
 * @author Georgel Pop
 */
public class FragmentEntryLinkUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		processConcurrently(
			SQLTransformer.transform(
				StringBundler.concat(
					"select FragmentEntryLink1.ctCollectionId, ",
					"FragmentEntryLink1.fragmentEntryLinkId, ",
					"FragmentEntryLink1.groupId, FragmentEntry.groupId, ",
					"FragmentEntry.externalReferenceCode, ",
					"FragmentEntryLink2.externalReferenceCode, ",
					"Group_.externalReferenceCode from FragmentEntryLink ",
					"FragmentEntryLink1 left join FragmentEntryLink ",
					"FragmentEntryLink2 on FragmentEntryLink2.ctCollectionId ",
					"= FragmentEntryLink1.ctCollectionId and ",
					"FragmentEntryLink2.fragmentEntryLinkId = ",
					"FragmentEntryLink1.originalFragmentEntryLinkId left join ",
					"FragmentEntry on FragmentEntry.ctCollectionId = ",
					"FragmentEntryLink1.ctCollectionId and ",
					"FragmentEntry.fragmentEntryId = ",
					"FragmentEntryLink1.fragmentEntryId left join Group_ on ",
					"Group_.ctCollectionId = FragmentEntry.ctCollectionId and ",
					"Group_.groupId = FragmentEntry.groupId")),
			StringBundler.concat(
				"update FragmentEntryLink set originalFragmentEntryLinkERC = ",
				"?, fragmentEntryERC = ?, fragmentEntryScopeERC = ? where ",
				"ctCollectionId = ? and fragmentEntryLinkId = ?"),
			resultSet -> new Object[] {
				resultSet.getLong(1), resultSet.getLong(2),
				resultSet.getLong(3), resultSet.getLong(4),
				resultSet.getString(5), resultSet.getString(6),
				resultSet.getString(7)
			},
			(values, preparedStatement) -> {
				long ctCollectionId = (Long)values[0];
				long fragmentEntryLinkId = (Long)values[1];
				long fragmentEntryLinkGroupId = (Long)values[2];
				long fragmentEntryGroupId = (Long)values[3];
				String fragmentEntryERC = (String)values[4];
				String fragmentEntryLinkERC = (String)values[5];
				String fragmentEntryScopeERC = (String)values[6];

				if ((fragmentEntryERC == null) ||
					(fragmentEntryGroupId == fragmentEntryLinkGroupId)) {

					fragmentEntryScopeERC = null;
				}

				preparedStatement.setString(1, fragmentEntryLinkERC);
				preparedStatement.setString(2, fragmentEntryERC);
				preparedStatement.setString(3, fragmentEntryScopeERC);
				preparedStatement.setLong(4, ctCollectionId);
				preparedStatement.setLong(5, fragmentEntryLinkId);

				preparedStatement.addBatch();
			},
			null);
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.dropColumns(
				"FragmentEntryLink", "originalFragmentEntryLinkId",
				"fragmentEntryId")
		};
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"FragmentEntryLink",
				"originalFragmentEntryLinkERC VARCHAR(75) null",
				"fragmentEntryERC VARCHAR(75) null",
				"fragmentEntryScopeERC VARCHAR(75) null")
		};
	}

}