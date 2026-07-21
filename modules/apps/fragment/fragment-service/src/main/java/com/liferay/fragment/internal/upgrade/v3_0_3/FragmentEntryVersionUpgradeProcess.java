/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v3_0_3;

import com.liferay.fragment.configuration.FragmentEntryVersionConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Georgel Pop
 */
public class FragmentEntryVersionUpgradeProcess extends UpgradeProcess {

	public FragmentEntryVersionUpgradeProcess(
		CompanyLocalService companyLocalService,
		ConfigurationProvider configurationProvider) {

		_companyLocalService = companyLocalService;
		_configurationProvider = configurationProvider;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(
			this::_deleteExcessFragmentEntryVersions);
	}

	private void _deleteExcessFragmentEntryVersions(long companyId)
		throws Exception {

		FragmentEntryVersionConfiguration fragmentEntryVersionConfiguration =
			_configurationProvider.getCompanyConfiguration(
				FragmentEntryVersionConfiguration.class, companyId);

		int maximumVersionsPerEntry =
			fragmentEntryVersionConfiguration.maximumVersionsPerEntry();

		if (maximumVersionsPerEntry <= 0) {
			return;
		}

		List<long[]> fragmentEntryPrimaryKeysList = new ArrayList<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ctCollectionId, fragmentEntryId from ",
					"FragmentEntryVersion where companyId = ? group by ",
					"ctCollectionId, fragmentEntryId having count(*) > ?"))) {

			preparedStatement.setLong(1, companyId);
			preparedStatement.setInt(2, maximumVersionsPerEntry);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					fragmentEntryPrimaryKeysList.add(
						new long[] {
							resultSet.getLong("ctCollectionId"),
							resultSet.getLong("fragmentEntryId")
						});
				}
			}
		}

		for (long[] fragmentEntryPrimaryKeys : fragmentEntryPrimaryKeysList) {
			long ctCollectionId = fragmentEntryPrimaryKeys[0];
			long fragmentEntryId = fragmentEntryPrimaryKeys[1];

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"delete from FragmentEntryVersion where " +
							"ctCollectionId = ? and fragmentEntryId = ? and " +
								"version < ?")) {

				preparedStatement.setLong(1, ctCollectionId);
				preparedStatement.setLong(2, fragmentEntryId);
				preparedStatement.setInt(
					3,
					_getMinimumVersion(
						ctCollectionId, fragmentEntryId,
						maximumVersionsPerEntry));

				preparedStatement.executeUpdate();
			}
		}
	}

	private int _getMinimumVersion(
			long ctCollectionId, long fragmentEntryId,
			int maximumVersionsPerEntry)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select version from FragmentEntryVersion where " +
					"ctCollectionId = ? and fragmentEntryId = ? order by " +
						"version desc")) {

			preparedStatement.setLong(1, ctCollectionId);
			preparedStatement.setLong(2, fragmentEntryId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				int count = 0;

				while (resultSet.next()) {
					count++;

					if (count == maximumVersionsPerEntry) {
						return resultSet.getInt("version");
					}
				}
			}
		}

		return 0;
	}

	private final CompanyLocalService _companyLocalService;
	private final ConfigurationProvider _configurationProvider;

}