/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.verify.model.VerifiableAuditedModel;
import com.liferay.portal.verify.model.LayoutFriendlyURLVerifiableAuditedModel;
import com.liferay.portal.verify.model.LayoutPrototypeVerifiableModel;
import com.liferay.portal.verify.model.LayoutSetPrototypeVerifiableModel;
import com.liferay.portal.verify.model.LayoutVerifiableAuditedModel;
import com.liferay.portal.verify.model.OrganizationVerifiableModel;
import com.liferay.portal.verify.model.RepositoryEntryVerifiableAuditedModel;
import com.liferay.portal.verify.model.RoleVerifiableModel;
import com.liferay.portal.verify.model.UserGroupVerifiableModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Michael C. Han
 * @author Shinn Lok
 */
public class VerifyAuditedModel extends VerifyProcess {

	public void verify(VerifiableAuditedModel... verifiableAuditedModels)
		throws Exception {

		List<String> unverifiedTableNames = new ArrayList<>();

		for (VerifiableAuditedModel verifiableAuditedModel :
				verifiableAuditedModels) {

			unverifiedTableNames.add(verifiableAuditedModel.getTableName());
		}

		List<VerifyAuditedModelCallable> verifyAuditedModelCallables =
			new ArrayList<>(unverifiedTableNames.size());

		while (!unverifiedTableNames.isEmpty()) {
			int count = unverifiedTableNames.size();

			for (VerifiableAuditedModel verifiableAuditedModel :
					verifiableAuditedModels) {

				if (unverifiedTableNames.contains(
						verifiableAuditedModel.getJoinByTableName()) ||
					!unverifiedTableNames.contains(
						verifiableAuditedModel.getTableName())) {

					continue;
				}

				VerifyAuditedModelCallable verifyAuditedModelCallable =
					new VerifyAuditedModelCallable(verifiableAuditedModel);

				verifyAuditedModelCallables.add(verifyAuditedModelCallable);

				unverifiedTableNames.remove(
					verifiableAuditedModel.getTableName());
			}

			if (unverifiedTableNames.size() == count) {
				throw new VerifyException(
					"Circular dependency detected " + unverifiedTableNames);
			}
		}

		doVerify(verifyAuditedModelCallables);
	}

	@Override
	protected void doVerify() throws Exception {
		verify(
			new LayoutFriendlyURLVerifiableAuditedModel(),
			new LayoutPrototypeVerifiableModel(),
			new LayoutSetPrototypeVerifiableModel(),
			new LayoutVerifiableAuditedModel(),
			new OrganizationVerifiableModel(),
			new RepositoryEntryVerifiableAuditedModel(),
			new RoleVerifiableModel(), new UserGroupVerifiableModel());
	}

	protected Object[] getAuditedModelArray(
			Connection connection, String tableName, String pkColumnName,
			long primKey, boolean allowAnonymousUser, long previousUserId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select companyId, userId, createDate, modifiedDate from ",
					tableName, " where ", pkColumnName, " = ?"))) {

			preparedStatement.setLong(1, primKey);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					long companyId = resultSet.getLong("companyId");

					long userId = 0;
					String userName = null;

					if (allowAnonymousUser) {
						userId = previousUserId;
						userName = "Anonymous";
					}
					else {
						userId = resultSet.getLong("userId");

						userName = getUserName(connection, userId);
					}

					Timestamp createDate = resultSet.getTimestamp("createDate");
					Timestamp modifiedDate = resultSet.getTimestamp(
						"modifiedDate");

					return new Object[] {
						companyId, userId, userName, createDate, modifiedDate
					};
				}

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Unable to find ", tableName, " ", primKey));
				}

				return null;
			}
		}
	}

	protected Object[] getGuestUserArray(Connection connection, long companyId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select userId, firstName, middleName, lastName from User_ " +
					"where companyId = ? and type_ = ?")) {

			preparedStatement.setLong(1, companyId);
			preparedStatement.setInt(2, UserConstants.TYPE_GUEST);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					long userId = resultSet.getLong("userId");
					String firstName = resultSet.getString("firstName");
					String middleName = resultSet.getString("middleName");
					String lastName = resultSet.getString("lastName");

					FullNameGenerator fullNameGenerator =
						FullNameGeneratorFactory.getInstance();

					String userName = fullNameGenerator.getFullName(
						firstName, middleName, lastName);

					Timestamp createDate = new Timestamp(
						System.currentTimeMillis());

					return new Object[] {
						companyId, userId, userName, createDate, createDate
					};
				}

				return null;
			}
		}
	}

	protected String getUserName(Connection connection, long userId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select firstName, middleName, lastName from User_ where " +
					"userId = ?")) {

			preparedStatement.setLong(1, userId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					String firstName = resultSet.getString("firstName");
					String middleName = resultSet.getString("middleName");
					String lastName = resultSet.getString("lastName");

					FullNameGenerator fullNameGenerator =
						FullNameGeneratorFactory.getInstance();

					return fullNameGenerator.getFullName(
						firstName, middleName, lastName);
				}

				return StringPool.BLANK;
			}
		}
	}

	protected void verifyAuditedModel(
		Connection connection, PreparedStatement preparedStatement,
		String tableName, long primKey, Object[] auditedModelArray,
		boolean updateDates) {

		try {
			long companyId = (Long)auditedModelArray[0];

			if (auditedModelArray[2] == null) {
				auditedModelArray = getGuestUserArray(connection, companyId);

				if (auditedModelArray == null) {
					return;
				}
			}

			long userId = (Long)auditedModelArray[1];
			String userName = (String)auditedModelArray[2];

			preparedStatement.setLong(1, companyId);
			preparedStatement.setLong(2, userId);
			preparedStatement.setString(3, userName);

			if (updateDates) {
				Timestamp createDate = (Timestamp)auditedModelArray[3];

				preparedStatement.setTimestamp(4, createDate);

				Timestamp modifiedDate = (Timestamp)auditedModelArray[4];

				preparedStatement.setTimestamp(5, modifiedDate);

				preparedStatement.setLong(6, primKey);
			}
			else {
				preparedStatement.setLong(4, primKey);
			}

			preparedStatement.addBatch();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to verify model " + tableName, exception);
			}
		}
	}

	protected void verifyAuditedModel(
			VerifiableAuditedModel verifiableAuditedModel)
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer(
				verifiableAuditedModel.getTableName())) {

			StringBundler sb = new StringBundler(8);

			sb.append("select ");
			sb.append(verifiableAuditedModel.getPrimaryKeyColumnName());
			sb.append(", companyId, userId");

			if (verifiableAuditedModel.getJoinByTableName() != null) {
				sb.append(StringPool.COMMA_AND_SPACE);
				sb.append(verifiableAuditedModel.getJoinByTableName());
			}

			sb.append(" from ");
			sb.append(verifiableAuditedModel.getTableName());
			sb.append(" where userName is null order by companyId");

			Object[] auditedModelArray = null;

			long previousCompanyId = 0;

			try (Connection connection = DataAccess.getConnection();

				PreparedStatement preparedStatement1 =
					connection.prepareStatement(sb.toString());

				ResultSet resultSet = preparedStatement1.executeQuery();

				PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						_getSQL(
							verifiableAuditedModel.getTableName(),
							verifiableAuditedModel.getPrimaryKeyColumnName(),
							verifiableAuditedModel.isUpdateDates()))) {

				while (resultSet.next()) {
					long companyId = resultSet.getLong("companyId");

					if (verifiableAuditedModel.getJoinByTableName() != null) {
						long previousUserId = resultSet.getLong("userId");
						long relatedPrimKey = resultSet.getLong(
							verifiableAuditedModel.getJoinByTableName());

						auditedModelArray = getAuditedModelArray(
							connection,
							verifiableAuditedModel.getRelatedModelName(),
							verifiableAuditedModel.getRelatedPKColumnName(),
							relatedPrimKey,
							verifiableAuditedModel.isAnonymousUserAllowed(),
							previousUserId);
					}
					else if (previousCompanyId != companyId) {
						auditedModelArray = getGuestUserArray(
							connection, companyId);

						previousCompanyId = companyId;
					}

					if (auditedModelArray == null) {
						continue;
					}

					long primKey = resultSet.getLong(
						verifiableAuditedModel.getPrimaryKeyColumnName());

					verifyAuditedModel(
						connection, preparedStatement2,
						verifiableAuditedModel.getTableName(), primKey,
						auditedModelArray,
						verifiableAuditedModel.isUpdateDates());
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private String _getSQL(
		String tableName, String primaryKeyColumnName, boolean updateDates) {

		StringBundler sb = new StringBundler(7);

		sb.append("update ");
		sb.append(tableName);
		sb.append(" set companyId = ?, userId = ?, userName = ?");

		if (updateDates) {
			sb.append(", createDate = ?, modifiedDate = ?");
		}

		sb.append(" where ");
		sb.append(primaryKeyColumnName);
		sb.append(" = ?");

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		VerifyAuditedModel.class);

	private class VerifyAuditedModelCallable implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			verifyAuditedModel(_verifiableAuditedModel);

			return null;
		}

		private VerifyAuditedModelCallable(
			VerifiableAuditedModel verifiableAuditedModel) {

			_verifiableAuditedModel = verifiableAuditedModel;
		}

		private final VerifiableAuditedModel _verifiableAuditedModel;

	}

}