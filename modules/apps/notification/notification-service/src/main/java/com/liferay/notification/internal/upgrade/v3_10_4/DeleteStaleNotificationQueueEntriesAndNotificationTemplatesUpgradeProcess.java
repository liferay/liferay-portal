/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.upgrade.v3_10_4;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.notification.constants.NotificationPortletKeys;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Paulo Albuquerque
 */
public class
	DeleteStaleNotificationQueueEntriesAndNotificationTemplatesUpgradeProcess
		extends UpgradeProcess {

	public DeleteStaleNotificationQueueEntriesAndNotificationTemplatesUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		GroupLocalService groupLocalService,
		PortletFileRepository portletFileRepository,
		ResourcePermissionLocalService resourcePermissionLocalService) {

		_classNameLocalService = classNameLocalService;
		_groupLocalService = groupLocalService;
		_portletFileRepository = portletFileRepository;
		_resourcePermissionLocalService = resourcePermissionLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_deleteNotificationQueueEntries();
		_deleteNotificationTemplates();
	}

	private void _deleteNotificationQueueEntries() throws Exception {
		try (PreparedStatement deletePreparedStatement1 =
				 _getDeletePreparedStatement("notificationQueueEntryId",
					 "NQueueEntryAttachment");
			 PreparedStatement deletePreparedStatement2 =
				 _getDeletePreparedStatement("notificationQueueEntryId",
					 "NotificationQueueEntry");
			 PreparedStatement selectPreparedStatement1 =
				 _getSelectPreparedStatement();
			 PreparedStatement selectPreparedStatement2 =
				 _getSelectPreparedStatement("NotificationQueueEntry");

			 ResultSet resultSet1 = selectPreparedStatement2.executeQuery()) {

			while (resultSet1.next()) {
				long notificationQueueEntryId = resultSet1.getLong(
					"notificationQueueEntryId");
				long companyId = resultSet1.getLong("companyId");

				_deleteResourcePermissions(
					companyId, NotificationQueueEntry.class.getName(),
					notificationQueueEntryId);

				deletePreparedStatement1.setLong(1, notificationQueueEntryId);

				deletePreparedStatement1.addBatch();

				deletePreparedStatement2.setLong(1, notificationQueueEntryId);

				deletePreparedStatement2.addBatch();

				selectPreparedStatement1.setLong(
					1,
					PortalUtil.getClassNameId(
						NotificationQueueEntry.class.getName()));
				selectPreparedStatement1.setLong(2, notificationQueueEntryId);

				ResultSet resultSet2 = selectPreparedStatement1.executeQuery();

				while (resultSet2.next()) {
					_deleteNotificationRecipient(resultSet2.getLong(1));
				}

				Repository repository = _getRepository(companyId);

				if (repository != null) {
					try {
						Folder folder = _portletFileRepository.getPortletFolder(
							repository.getRepositoryId(),
							DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
							String.valueOf(notificationQueueEntryId));

						_portletFileRepository.deletePortletFolder(
							folder.getFolderId());
					}
					catch (PortalException portalException) {
						if (_log.isDebugEnabled()) {
							_log.debug(portalException);
						}
					}
				}
			}

			deletePreparedStatement1.executeBatch();

			deletePreparedStatement2.executeBatch();
		}
	}

	private void _deleteNotificationRecipient(long notificationRecipientId)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"delete from NotificationRecipient where ",
				"notificationRecipientId = ", notificationRecipientId));
		runSQL(
			StringBundler.concat(
				"delete from NotificationRecipientSetting where ",
				"notificationRecipientId = ", notificationRecipientId));
	}

	private void _deleteNotificationTemplates() throws Exception {
		try (PreparedStatement deletePreparedStatement1 =
				 _getDeletePreparedStatement("notificationTemplateId",
					 "NTemplateAttachment");
			 PreparedStatement deletePreparedStatement2 =
			 _getDeletePreparedStatement("notificationTemplateId",
				 "NotificationTemplate");
			 PreparedStatement preparedStatement1 =
				 _getSelectPreparedStatement();
			 PreparedStatement preparedStatement2 =
				 _getSelectPreparedStatement("NotificationTemplate");

			 ResultSet resultSet1 = preparedStatement2.executeQuery();
			) {

			while (resultSet1.next()) {
				long notificationTemplateId = resultSet1.getLong(
					"notificationTemplateId");

				_deleteResourcePermissions(
					resultSet1.getLong("companyId"),
					NotificationTemplate.class.getName(),
					notificationTemplateId);

				deletePreparedStatement1.setLong(1, notificationTemplateId);
				deletePreparedStatement2.setLong(1, notificationTemplateId);

				deletePreparedStatement1.addBatch();
				deletePreparedStatement2.addBatch();

				preparedStatement1.setLong(
					1,
					PortalUtil.getClassNameId(
						NotificationTemplate.class.getName()));
				preparedStatement1.setLong(2, notificationTemplateId);

				ResultSet resultSet2 = preparedStatement1.executeQuery();

				while (resultSet2.next()) {
					_deleteNotificationRecipient(resultSet2.getLong(1));
				}
			}

			deletePreparedStatement1.executeBatch();

			deletePreparedStatement2.executeBatch();
		}
	}

	private void _deleteResourcePermissions(
			long companyId, String className, long primarykey)
		throws Exception {

		_resourcePermissionLocalService.deleteResourcePermissions(
			companyId, className, ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(primarykey));
	}

	private PreparedStatement _getDeletePreparedStatement(
			String columnName, String tableName)
		throws Exception {

		return AutoBatchPreparedStatementUtil.concurrentAutoBatch(
			connection,
			StringBundler.concat(
				"delete from ", tableName, " where ", columnName, " = ?"));
	}

	private Repository _getRepository(long companyId) {
		try {
			Group group = _groupLocalService.getCompanyGroup(companyId);

			return _portletFileRepository.addPortletRepository(
				group.getGroupId(),
				NotificationPortletKeys.NOTIFICATION_TEMPLATES,
				new ServiceContext());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	private PreparedStatement _getSelectPreparedStatement() throws Exception {
		return connection.prepareStatement(
			"select notificationRecipientId from NotificationRecipient where " +
				"classNameId = ? and classPK = ?");
	}

	private PreparedStatement _getSelectPreparedStatement(String tableName)
		throws SQLException {

		return connection.prepareStatement(
			StringBundler.concat(
				"select * from ", tableName,
				" where not exists (select 1 from Company where ",
				"Company.companyId = ", tableName, ".companyId)"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteStaleNotificationQueueEntriesAndNotificationTemplatesUpgradeProcess.class);

	private final ClassNameLocalService _classNameLocalService;
	private final GroupLocalService _groupLocalService;
	private final PortletFileRepository _portletFileRepository;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;

}