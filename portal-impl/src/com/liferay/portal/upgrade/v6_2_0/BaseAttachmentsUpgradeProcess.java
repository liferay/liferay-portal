/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.store.DLStoreRequest;
import com.liferay.document.library.kernel.store.DLStoreUtil;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.change.tracking.store.CTStoreFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.upgrade.v6_2_0.util.DLValidatorImpl;
import com.liferay.portal.upgrade.v6_2_0.util.DummyCTStoreFactoryImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseAttachmentsUpgradeProcess extends UpgradeProcess {

	protected long addDLFileEntry(
		long groupId, long companyId, long userId, String className,
		long classPK, String userName, Timestamp createDate, long repositoryId,
		long folderId, String name, String extension, String mimeType,
		String title, long size) {

		long fileEntryId = increment();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DLFileEntry (uuid_, fileEntryId, groupId, ",
					"companyId, userId, userName, createDate, modifiedDate, ",
					"classNameId, classPK, repositoryId, folderId, name, ",
					"extension, mimeType, title, description, extraSettings, ",
					"fileEntryTypeId, version, size_, smallImageId, ",
					"largeImageId, custom1ImageId, custom2ImageId) values (?, ",
					"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
					"?, ?, ?, ?, ?, ?)"))) {

			preparedStatement.setString(1, PortalUUIDUtil.generate());
			preparedStatement.setLong(2, fileEntryId);
			preparedStatement.setLong(3, groupId);
			preparedStatement.setLong(4, companyId);
			preparedStatement.setLong(5, userId);
			preparedStatement.setString(6, userName);
			preparedStatement.setTimestamp(7, createDate);
			preparedStatement.setTimestamp(8, createDate);
			preparedStatement.setLong(9, PortalUtil.getClassNameId(className));
			preparedStatement.setLong(10, classPK);
			preparedStatement.setLong(11, repositoryId);
			preparedStatement.setLong(12, folderId);
			preparedStatement.setString(13, name);
			preparedStatement.setString(14, extension);
			preparedStatement.setString(15, mimeType);
			preparedStatement.setString(16, title);
			preparedStatement.setString(17, StringPool.BLANK);
			preparedStatement.setString(18, StringPool.BLANK);
			preparedStatement.setLong(19, 0);
			preparedStatement.setString(20, "1.0");
			preparedStatement.setLong(21, size);
			preparedStatement.setInt(22, 0);
			preparedStatement.setLong(23, 0);
			preparedStatement.setLong(24, 0);
			preparedStatement.setLong(25, 0);

			preparedStatement.executeUpdate();

			long bitwiseValue = getBitwiseValue(
				getBitwiseValues(
					"com.liferay.portlet.documentlibrary.model.DLFileEntry"),
				ListUtil.fromArray(ActionKeys.VIEW));

			addResourcePermission(
				companyId,
				"com.liferay.portlet.documentlibrary.model.DLFileEntry",
				fileEntryId, getRoleId(companyId, RoleConstants.GUEST),
				bitwiseValue);
			addResourcePermission(
				companyId,
				"com.liferay.portlet.documentlibrary.model.DLFileEntry",
				fileEntryId, getRoleId(companyId, RoleConstants.SITE_MEMBER),
				bitwiseValue);

			return fileEntryId;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to add file entry " + name, exception);
			}

			return -1;
		}
	}

	protected void addDLFileVersion(
		long fileVersionId, long groupId, long companyId, long userId,
		String userName, Timestamp createDate, long repositoryId, long folderId,
		long fileEntryId, String extension, String mimeType, String title,
		long size) {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DLFileVersion (uuid_, fileVersionId, ",
					"groupId, companyId, userId, userName, createDate, ",
					"modifiedDate, repositoryId, folderId, fileEntryId, ",
					"extension, mimeType, title, description, changeLog, ",
					"extraSettings, fileEntryTypeId, version, size_, status, ",
					"statusByUserId, statusByUserName, statusDate) values (?, ",
					"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
					"?, ?, ?, ?)"))) {

			preparedStatement.setString(1, PortalUUIDUtil.generate());
			preparedStatement.setLong(2, fileVersionId);
			preparedStatement.setLong(3, groupId);
			preparedStatement.setLong(4, companyId);
			preparedStatement.setLong(5, userId);
			preparedStatement.setString(6, userName);
			preparedStatement.setTimestamp(7, createDate);
			preparedStatement.setTimestamp(8, createDate);
			preparedStatement.setLong(9, repositoryId);
			preparedStatement.setLong(10, folderId);
			preparedStatement.setLong(11, fileEntryId);
			preparedStatement.setString(12, extension);
			preparedStatement.setString(13, mimeType);
			preparedStatement.setString(14, title);
			preparedStatement.setString(15, StringPool.BLANK);
			preparedStatement.setString(16, StringPool.BLANK);
			preparedStatement.setString(17, StringPool.BLANK);
			preparedStatement.setLong(18, 0);
			preparedStatement.setString(19, "1.0");
			preparedStatement.setLong(20, size);
			preparedStatement.setInt(21, 0);
			preparedStatement.setLong(22, userId);
			preparedStatement.setString(23, userName);
			preparedStatement.setTimestamp(24, createDate);

			preparedStatement.executeUpdate();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to add file version 1.0 for file entry " + title,
					exception);
			}
		}
	}

	protected long addDLFolder(
		long folderId, long groupId, long companyId, long userId,
		String userName, Timestamp createDate, long repositoryId,
		boolean mountPoint, long parentFolderId, String name, boolean hidden) {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into DLFolder (uuid_, folderId, groupId, ",
					"companyId, userId, userName, createDate, modifiedDate, ",
					"repositoryId, mountPoint, parentFolderId, name, ",
					"description, lastPostDate, defaultFileEntryTypeId, ",
					"hidden_, overrideFileEntryTypes, status, statusByUserId, ",
					"statusByUserName, statusDate) values (?, ?, ?, ?, ?, ?, ",
					"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"))) {

			preparedStatement.setString(1, PortalUUIDUtil.generate());
			preparedStatement.setLong(2, folderId);
			preparedStatement.setLong(3, groupId);
			preparedStatement.setLong(4, companyId);
			preparedStatement.setLong(5, userId);
			preparedStatement.setString(6, userName);
			preparedStatement.setTimestamp(7, createDate);
			preparedStatement.setTimestamp(8, createDate);
			preparedStatement.setLong(9, repositoryId);
			preparedStatement.setBoolean(10, mountPoint);
			preparedStatement.setLong(11, parentFolderId);
			preparedStatement.setString(12, name);
			preparedStatement.setString(13, StringPool.BLANK);
			preparedStatement.setTimestamp(14, createDate);
			preparedStatement.setLong(15, 0);
			preparedStatement.setBoolean(16, hidden);
			preparedStatement.setBoolean(17, false);
			preparedStatement.setLong(18, 0);
			preparedStatement.setLong(19, userId);
			preparedStatement.setString(20, userName);
			preparedStatement.setTimestamp(21, createDate);

			preparedStatement.executeUpdate();

			Map<String, Long> bitwiseValues = getBitwiseValues(
				"com.liferay.portlet.documentlibrary.model.DLFolder");

			long guestBitwiseValue = getBitwiseValue(
				bitwiseValues, ListUtil.fromArray(ActionKeys.VIEW));

			addResourcePermission(
				companyId, "com.liferay.portlet.documentlibrary.model.DLFolder",
				folderId, getRoleId(companyId, RoleConstants.GUEST),
				guestBitwiseValue);

			List<String> siteMemberActionIds = new ArrayList<>();

			siteMemberActionIds.add(ActionKeys.ADD_DOCUMENT);
			siteMemberActionIds.add(ActionKeys.ADD_SUBFOLDER);
			siteMemberActionIds.add(ActionKeys.VIEW);

			long siteMemberBitwiseValue = getBitwiseValue(
				bitwiseValues, siteMemberActionIds);

			addResourcePermission(
				companyId, "com.liferay.portlet.documentlibrary.model.DLFolder",
				folderId, getRoleId(companyId, RoleConstants.SITE_MEMBER),
				siteMemberBitwiseValue);

			return folderId;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to add folder " + name, exception);
			}

			return -1;
		}
	}

	protected long addRepository(
		long groupId, long companyId, long userId, String userName,
		Timestamp createDate, long classNameId, String portletId) {

		long repositoryId = increment();

		long folderId = addDLFolder(
			increment(), groupId, companyId, userId, userName, createDate,
			repositoryId, true, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			portletId, true);

		if (folderId < 0) {
			return -1;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into Repository (uuid_, repositoryId, groupId, ",
					"companyId, userId, userName, createDate, modifiedDate, ",
					"classNameId, name, description, portletId, typeSettings, ",
					"dlFolderId) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
					"?, ?)"))) {

			preparedStatement.setString(1, PortalUUIDUtil.generate());
			preparedStatement.setLong(2, repositoryId);
			preparedStatement.setLong(3, groupId);
			preparedStatement.setLong(4, companyId);
			preparedStatement.setLong(5, userId);
			preparedStatement.setString(6, userName);
			preparedStatement.setTimestamp(7, createDate);
			preparedStatement.setTimestamp(8, createDate);
			preparedStatement.setLong(9, classNameId);
			preparedStatement.setString(10, portletId);
			preparedStatement.setString(11, StringPool.BLANK);
			preparedStatement.setString(12, portletId);
			preparedStatement.setString(13, StringPool.BLANK);
			preparedStatement.setLong(14, folderId);

			preparedStatement.executeUpdate();

			return repositoryId;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to add repository for portlet " + portletId,
					exception);
			}

			return -1;
		}
	}

	protected void addResourcePermission(
		long companyId, String className, long primKey, long roleId,
		long actionIds) {

		long resourcePermissionId = increment(
			ResourcePermission.class.getName());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into ResourcePermission (resourcePermissionId, ",
					"companyId, name, scope, primKey, roleId, ownerId, ",
					"actionIds) values (?, ?, ?, ?, ?, ?, ?, ?)"))) {

			preparedStatement.setLong(1, resourcePermissionId);
			preparedStatement.setLong(2, companyId);
			preparedStatement.setString(3, className);
			preparedStatement.setInt(4, ResourceConstants.SCOPE_INDIVIDUAL);
			preparedStatement.setLong(5, primKey);
			preparedStatement.setLong(6, roleId);
			preparedStatement.setLong(7, 0);
			preparedStatement.setLong(8, actionIds);

			preparedStatement.executeUpdate();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to add resource permission " + className,
					exception);
			}
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceRegistration<?> ctStoreFactoryServiceRegistration =
			bundleContext.registerService(
				CTStoreFactory.class, new DummyCTStoreFactoryImpl(), null);
		ServiceRegistration<?> dlValidatorServiceRegistration =
			bundleContext.registerService(
				DLValidator.class, new DLValidatorImpl(), null);

		updateAttachments();

		ctStoreFactoryServiceRegistration.unregister();
		dlValidatorServiceRegistration.unregister();
	}

	protected String[] getAttachments(
			long companyId, long containerModelId, long resourcePrimKey)
		throws Exception {

		return DLStoreUtil.getFileNames(
			companyId, CompanyConstants.SYSTEM,
			getDirName(containerModelId, resourcePrimKey));
	}

	protected long getBitwiseValue(
		Map<String, Long> bitwiseValues, List<String> actionIds) {

		long bitwiseValue = 0;

		for (String actionId : actionIds) {
			Long actionIdBitwiseValue = bitwiseValues.get(actionId);

			if (actionIdBitwiseValue == null) {
				continue;
			}

			bitwiseValue |= actionIdBitwiseValue;
		}

		return bitwiseValue;
	}

	protected Map<String, Long> getBitwiseValues(String name) throws Exception {
		Map<String, Long> bitwiseValues = _bitwiseValues.get(name);

		if (bitwiseValues != null) {
			return bitwiseValues;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select actionId, bitwiseValue from ResourceAction where " +
					"name = ?")) {

			preparedStatement.setString(1, name);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				bitwiseValues = new HashMap<>();

				while (resultSet.next()) {
					String actionId = resultSet.getString("actionId");
					long bitwiseValue = resultSet.getLong("bitwiseValue");

					bitwiseValues.put(actionId, bitwiseValue);
				}

				_bitwiseValues.put(name, bitwiseValues);

				return bitwiseValues;
			}
		}
	}

	protected abstract String getClassName();

	protected long getClassNameId() {
		return PortalUtil.getClassNameId(getClassName());
	}

	protected long getContainerModelFolderId(
			long groupId, long companyId, long resourcePrimKey,
			long containerModelId, long userId, String userName,
			Timestamp createDate)
		throws Exception {

		return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	}

	protected abstract String getDirName(
		long containerModelId, long resourcePrimKey);

	protected long getFolderId(
			long groupId, long companyId, long userId, String userName,
			Timestamp createDate, long repositoryId, long parentFolderId,
			String name, boolean hidden)
		throws Exception {

		if ((repositoryId < 0) || (parentFolderId < 0)) {
			return -1;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select folderId from DLFolder where repositoryId = ? and " +
					"parentFolderId = ? and name = ?")) {

			preparedStatement.setLong(1, repositoryId);
			preparedStatement.setLong(2, parentFolderId);
			preparedStatement.setString(3, name);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					return resultSet.getLong("folderId");
				}
			}
		}

		return addDLFolder(
			increment(), groupId, companyId, userId, userName, createDate,
			repositoryId, false, parentFolderId, name, hidden);
	}

	protected abstract String getPortletId();

	protected long getRepositoryId(
			long groupId, long companyId, long userId, String userName,
			Timestamp createDate, long classNameId, String portletId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select repositoryId from Repository where groupId = ? and " +
					"name = ? and portletId = ?")) {

			preparedStatement.setLong(1, groupId);
			preparedStatement.setString(2, portletId);
			preparedStatement.setString(3, portletId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					return resultSet.getLong("repositoryId");
				}
			}
		}

		return addRepository(
			groupId, companyId, userId, userName, createDate, classNameId,
			portletId);
	}

	protected long getRoleId(long companyId, String name) throws Exception {
		String roleIdsKey = companyId + StringPool.POUND + name;

		Long roleId = _roleIds.get(roleIdsKey);

		if (roleId != null) {
			return roleId;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select roleId from Role_ where companyId = ? and name = ?")) {

			preparedStatement.setLong(1, companyId);
			preparedStatement.setString(2, name);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					roleId = resultSet.getLong("roleId");
				}

				_roleIds.put(roleIdsKey, roleId);

				return roleId;
			}
		}
	}

	protected abstract void updateAttachments() throws Exception;

	protected void updateEntryAttachments(
			long companyId, long groupId, long resourcePrimKey,
			long containerModelId, long userId, String userName)
		throws Exception {

		String[] attachments = getAttachments(
			companyId, containerModelId, resourcePrimKey);

		if (ArrayUtil.isEmpty(attachments)) {
			return;
		}

		Timestamp createDate = new Timestamp(System.currentTimeMillis());

		long repositoryId = getRepositoryId(
			groupId, companyId, userId, userName, createDate,
			PortalUtil.getClassNameId(_CLASS_NAME_LIFERAY_REPOSITORY),
			getPortletId());

		if (repositoryId < 0) {
			return;
		}

		long containerModelFolderId = getContainerModelFolderId(
			groupId, companyId, resourcePrimKey, containerModelId, userId,
			userName, createDate);

		if (containerModelFolderId < 0) {
			return;
		}

		for (String attachment : attachments) {
			String name = String.valueOf(
				increment(
					"com.liferay.portlet.documentlibrary.model.DLFileEntry"));

			String title = FileUtil.getShortFileName(attachment);

			String extension = FileUtil.getExtension(title);

			String mimeType = MimeTypesUtil.getExtensionContentType(extension);

			try {
				long size = DLStoreUtil.getFileSize(
					companyId, CompanyConstants.SYSTEM, attachment);

				long fileEntryId = addDLFileEntry(
					groupId, companyId, userId, getClassName(), resourcePrimKey,
					userName, createDate, repositoryId, containerModelFolderId,
					name, extension, mimeType, title, size);

				if (fileEntryId < 0) {
					continue;
				}

				addDLFileVersion(
					increment(), groupId, companyId, userId, userName,
					createDate, repositoryId, containerModelFolderId,
					fileEntryId, extension, mimeType, title, size);

				byte[] bytes = StreamUtil.toByteArray(
					DLStoreUtil.getFileAsStream(
						companyId, CompanyConstants.SYSTEM, attachment,
						StringPool.BLANK));

				DLStoreUtil.addFile(
					DLStoreRequest.builder(
						companyId, containerModelFolderId, name
					).className(
						this
					).size(
						bytes.length
					).build(),
					bytes);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to add attachment " + attachment, exception);
				}
			}

			try {
				DLStoreUtil.deleteFile(
					companyId, CompanyConstants.SYSTEM, attachment);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to delete attachment " + attachment, exception);
				}
			}
		}
	}

	private static final String _CLASS_NAME_LIFERAY_REPOSITORY =
		"com.liferay.portal.repository.liferayrepository.LiferayRepository";

	private static final Log _log = LogFactoryUtil.getLog(
		BaseAttachmentsUpgradeProcess.class);

	private final Map<String, Map<String, Long>> _bitwiseValues =
		new HashMap<>();
	private final Map<String, Long> _roleIds = new HashMap<>();

}