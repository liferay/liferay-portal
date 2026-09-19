/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.file.install;

import com.liferay.fragment.importer.FragmentsImportStrategy;
import com.liferay.fragment.importer.FragmentsImporter;
import com.liferay.layout.importer.LayoutsImportStrategy;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.file.install.FileInstaller;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.staging.StagingGroupHelper;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.Enumeration;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = FileInstaller.class)
public class FragmentFileInstaller implements FileInstaller {

	@Override
	public boolean canTransformURL(File file) {
		String fileName = file.getName();

		if (!StringUtil.endsWith(fileName, ".zip")) {
			return false;
		}

		try {
			JSONObject deployJSONObject = _getDeployJSONObject(file);

			if (deployJSONObject != null) {
				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	@Override
	public URL transformURL(File file) throws Exception {
		try {
			if (_log.isInfoEnabled()) {
				_log.info("Deploying " + file.getName());
			}

			_deploy(file);

			if (_log.isInfoEnabled()) {
				_log.info("Deployed " + file.getName() + " successfully");
			}
		}
		finally {
			file.delete();
		}

		return null;
	}

	@Override
	public void uninstall(File file) {
	}

	private void _deploy(File file) throws Exception {
		JSONObject deployJSONObject = _getDeployJSONObject(file);

		if (deployJSONObject == null) {
			throw new Exception();
		}

		Company company = null;
		Group group = null;

		String companyWebId = deployJSONObject.getString("companyWebId");

		if (Validator.isNotNull(companyWebId) &&
			!Objects.equals(companyWebId, StringPool.STAR)) {

			company = _companyLocalService.getCompanyByWebId(companyWebId);
		}

		if ((company != null) && deployJSONObject.has("groupKey")) {
			group = _getDeploymentGroup(
				company.getCompanyId(), deployJSONObject.getString("groupKey"));

			_importFragmentEntriesAndLayouts(company, file, group);
		}
		else if (company != null) {
			group = _groupLocalService.getCompanyGroup(company.getCompanyId());

			_importFragmentEntriesAndLayouts(company, file, group);
		}
		else {
			_companyLocalService.forEachCompany(
				currentCompany -> {
					try {
						_importFragmentEntriesAndLayouts(
							currentCompany, file, null);
					}
					catch (Exception exception) {
						_log.error(exception);
					}
				});
		}
	}

	private JSONObject _getDeployJSONObject(File file)
		throws IOException, JSONException {

		try (ZipFile zipFile = new ZipFile(file)) {
			ZipEntry zipEntry = _getDeployZipEntry(zipFile);

			if (zipEntry == null) {
				return null;
			}

			return _jsonFactory.createJSONObject(
				StringUtil.read(zipFile.getInputStream(zipEntry)));
		}
	}

	private ZipEntry _getDeployZipEntry(ZipFile zipFile) {
		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			if (Objects.equals(
					_getFileName(zipEntry.getName()),
					"liferay-deploy-fragments.json")) {

				return zipEntry;
			}
		}

		return null;
	}

	private Group _getDeploymentGroup(long companyId, String groupKey)
		throws Exception {

		Group group = _groupLocalService.getGroup(companyId, groupKey);

		if (group == null) {
			return null;
		}

		if (_stagingGroupHelper.isLocalLiveGroup(group) ||
			_stagingGroupHelper.isRemoteLiveGroup(group)) {

			return group.getStagingGroup();
		}

		return group;
	}

	private String _getFileName(String path) {
		int pos = path.lastIndexOf(CharPool.SLASH);

		if (pos > 0) {
			return path.substring(pos + 1);
		}

		return path;
	}

	private User _getUser(Company company, Group group) throws Exception {
		long companyId = CompanyConstants.SYSTEM;
		long userId = 0;

		if (group != null) {
			companyId = group.getCompanyId();
			userId = group.getCreatorUserId();
		}
		else if (company != null) {
			companyId = company.getCompanyId();
		}

		User user = _userLocalService.fetchUserById(userId);

		if ((user == null) || user.isGuestUser()) {
			Role role = _roleLocalService.getRole(
				companyId, RoleConstants.ADMINISTRATOR);

			long[] userIds = _userLocalService.getRoleUserIds(role.getRoleId());

			return _userLocalService.getUser(userIds[0]);
		}

		return user;
	}

	private void _importFragmentEntriesAndLayouts(
			Company company, File file, Group group)
		throws Exception {

		User user = _getUser(company, group);

		if (user == null) {
			throw new Exception();
		}

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					company, user)) {

			long groupId = 0;

			if (group != null) {
				groupId = group.getGroupId();
			}

			_fragmentsImporter.importFragmentEntries(
				user.getUserId(), groupId, 0, file,
				FragmentsImportStrategy.OVERWRITE, false);

			if ((group != null) &&
				(company.getGroupId() != group.getGroupId())) {

				_layoutsImporter.importFile(
					user.getUserId(), groupId, 0L, file,
					LayoutsImportStrategy.OVERWRITE, true);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentFileInstaller.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private FragmentsImporter _fragmentsImporter;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Reference
	private LayoutsImporter _layoutsImporter;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

	@Reference
	private UserLocalService _userLocalService;

}