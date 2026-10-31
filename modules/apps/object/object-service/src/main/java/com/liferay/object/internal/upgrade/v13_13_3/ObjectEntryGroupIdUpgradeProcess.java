/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_13_3;

import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class ObjectEntryGroupIdUpgradeProcess extends UpgradeProcess {

	public ObjectEntryGroupIdUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		CompanyLocalService companyLocalService,
		FriendlyURLEntryLocalService friendlyURLEntryLocalService) {

		_classNameLocalService = classNameLocalService;
		_companyLocalService = companyLocalService;
		_friendlyURLEntryLocalService = friendlyURLEntryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ObjectDefinition.className, ",
					"ObjectEntry.companyId, ",
					"ObjectEntry.externalReferenceCode, ",
					"ObjectEntry.objectDefinitionId, ",
					"ObjectEntry.objectEntryId from ObjectEntry inner join ",
					"ObjectDefinition on ObjectDefinition.objectDefinitionId ",
					"= ObjectEntry.objectDefinitionId where ",
					"ObjectDefinition.scope = ? and ObjectEntry.groupId != 0"));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"select count(*) as count from ObjectEntry where " +
					"objectDefinitionId = ? and groupId = 0 and companyId = " +
						"? and externalReferenceCode = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update ObjectEntry set externalReferenceCode = ?, " +
						"groupId = 0, objectEntryFolderId = 0, treePath = " +
							"'/' where objectEntryId = ?");
			PreparedStatement preparedStatement4 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update AssetEntry set groupId = ? where classNameId = ? " +
						"and classPK = ?");
			PreparedStatement preparedStatement5 = connection.prepareStatement(
				"select entryId, typeSettings from TrashEntry where " +
					"classNameId = ? and classPK = ?");
			PreparedStatement preparedStatement6 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update TrashEntry set groupId = 0, typeSettings = ? " +
						"where entryId = ?")) {

			preparedStatement1.setString(
				1, ObjectDefinitionConstants.SCOPE_COMPANY);

			try (ResultSet resultSet1 = preparedStatement1.executeQuery()) {
				Set<String> externalReferenceCodes = new HashSet<>();

				while (resultSet1.next()) {
					long companyId = resultSet1.getLong("companyId");
					String externalReferenceCode = resultSet1.getString(
						"externalReferenceCode");
					long objectDefinitionId = resultSet1.getLong(
						"objectDefinitionId");

					if (!externalReferenceCodes.add(
							StringBundler.concat(
								objectDefinitionId, StringPool.POUND,
								StringUtil.toLowerCase(
									externalReferenceCode))) ||
						_hasExternalReferenceCode(
							companyId, externalReferenceCode,
							objectDefinitionId, preparedStatement2)) {

						externalReferenceCode = PortalUUIDUtil.generate();
					}

					long objectEntryId = resultSet1.getLong("objectEntryId");

					preparedStatement3.setString(1, externalReferenceCode);
					preparedStatement3.setLong(2, objectEntryId);

					preparedStatement3.addBatch();

					Company company = _companyLocalService.getCompany(
						companyId);
					long classNameId = _classNameLocalService.getClassNameId(
						resultSet1.getString("className"));

					preparedStatement4.setLong(1, company.getGroupId());
					preparedStatement4.setLong(2, classNameId);
					preparedStatement4.setLong(3, objectEntryId);

					preparedStatement4.addBatch();

					_moveFriendlyURLEntry(
						classNameId, company.getGroupId(), objectEntryId);

					preparedStatement5.setLong(1, classNameId);
					preparedStatement5.setLong(2, objectEntryId);

					try (ResultSet resultSet5 =
							preparedStatement5.executeQuery()) {

						if (!resultSet5.next()) {
							continue;
						}

						preparedStatement6.setString(
							1,
							UnicodePropertiesBuilder.create(
								true
							).fastLoad(
								resultSet5.getString("typeSettings")
							).put(
								"objectEntryFolderId",
								ObjectEntryFolderConstants.
									PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT
							).buildString());
						preparedStatement6.setLong(
							2, resultSet5.getLong("entryId"));

						preparedStatement6.addBatch();
					}
				}
			}

			preparedStatement3.executeBatch();

			preparedStatement4.executeBatch();

			preparedStatement6.executeBatch();
		}
	}

	private boolean _hasExternalReferenceCode(
			long companyId, String externalReferenceCode,
			long objectDefinitionId, PreparedStatement preparedStatement)
		throws Exception {

		preparedStatement.setLong(1, objectDefinitionId);
		preparedStatement.setLong(2, companyId);
		preparedStatement.setString(3, externalReferenceCode);

		try (ResultSet resultSet = preparedStatement.executeQuery()) {
			resultSet.next();

			if (resultSet.getLong("count") > 0) {
				return true;
			}

			return false;
		}
	}

	private void _moveFriendlyURLEntry(
			long classNameId, long companyGroupId, long objectEntryId)
		throws Exception {

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				classNameId, objectEntryId);

		if ((friendlyURLEntry == null) ||
			(friendlyURLEntry.getGroupId() == companyGroupId)) {

			return;
		}

		Map<String, String> languageIdToUrlTitleMap =
			friendlyURLEntry.getLanguageIdToUrlTitleMap();

		Map<String, String> urlTitleMap = new HashMap<>();

		for (Map.Entry<String, String> entry :
				languageIdToUrlTitleMap.entrySet()) {

			urlTitleMap.put(
				entry.getKey(),
				_friendlyURLEntryLocalService.getUniqueUrlTitle(
					companyGroupId, classNameId, objectEntryId,
					entry.getValue()));
		}

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			friendlyURLEntry.getGroupId(), classNameId, objectEntryId);

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			companyGroupId, classNameId, objectEntryId,
			friendlyURLEntry.getDefaultLanguageId(), urlTitleMap,
			new ServiceContext());
	}

	private final ClassNameLocalService _classNameLocalService;
	private final CompanyLocalService _companyLocalService;
	private final FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

}