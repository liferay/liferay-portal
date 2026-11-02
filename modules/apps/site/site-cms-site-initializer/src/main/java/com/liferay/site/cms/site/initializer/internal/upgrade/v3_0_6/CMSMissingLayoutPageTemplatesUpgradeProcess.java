/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_6;

import com.liferay.layout.importer.LayoutsImportStrategy;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;

import java.io.File;
import java.io.InputStream;

import java.net.URL;

import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Jan Brychta
 */
public class CMSMissingLayoutPageTemplatesUpgradeProcess
	extends UpgradeProcess {

	public CMSMissingLayoutPageTemplatesUpgradeProcess(
		CompanyLocalService companyLocalService,
		GroupLocalService groupLocalService,
		LayoutLocalService layoutLocalService,
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		LayoutsImporter layoutsImporter, RoleLocalService roleLocalService,
		UserLocalService userLocalService, ZipWriterFactory zipWriterFactory) {

		_companyLocalService = companyLocalService;
		_groupLocalService = groupLocalService;
		_layoutLocalService = layoutLocalService;
		_layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
		_layoutsImporter = layoutsImporter;
		_roleLocalService = roleLocalService;
		_userLocalService = userLocalService;
		_zipWriterFactory = zipWriterFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private void _backfillContentEditorMasterExternalReferenceCode(long groupId)
		throws Exception {

		LayoutPageTemplateEntry contentEditorMasterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				groupId, "content-editor-master");

		if (contentEditorMasterLayoutPageTemplateEntry == null) {
			return;
		}

		String externalReferenceCode =
			contentEditorMasterLayoutPageTemplateEntry.
				getExternalReferenceCode();

		for (LayoutPageTemplateEntry layoutPageTemplateEntry :
				_layoutPageTemplateEntryLocalService.
					getLayoutPageTemplateEntries(groupId)) {

			if (layoutPageTemplateEntry.getType() !=
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {

				continue;
			}

			Layout layout = _layoutLocalService.fetchLayout(
				layoutPageTemplateEntry.getPlid());

			if (layout == null) {
				continue;
			}

			Layout draftLayout = layout.fetchDraftLayout();

			if ((draftLayout == null) ||
				Validator.isNotNull(
					draftLayout.getMasterLayoutPageTemplateEntryERC())) {

				continue;
			}

			draftLayout.setMasterLayoutPageTemplateEntryERC(
				externalReferenceCode);

			_layoutLocalService.updateLayout(draftLayout);
		}
	}

	private File _getLayoutPageTemplatesFile() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		if (bundle == null) {
			return null;
		}

		BundleContext bundleContext = bundle.getBundleContext();

		Bundle siteInitializerCMSBundle = null;

		for (Bundle candidateBundle : bundleContext.getBundles()) {
			if (Objects.equals(
					candidateBundle.getSymbolicName(),
					_SITE_INITIALIZER_CMS_SYMBOLIC_NAME)) {

				siteInitializerCMSBundle = candidateBundle;

				break;
			}
		}

		if (siteInitializerCMSBundle == null) {
			return null;
		}

		Enumeration<URL> enumeration = siteInitializerCMSBundle.findEntries(
			"/site-initializer/layout-page-templates", "*", true);

		if (enumeration == null) {
			return null;
		}

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();

			String urlPath = url.getPath();

			if (urlPath.endsWith("/")) {
				continue;
			}

			try (InputStream inputStream = url.openStream()) {
				zipWriter.addEntry(
					_removeFirst(
						urlPath, "/site-initializer/layout-page-templates"),
					inputStream);
			}
		}

		return zipWriter.getFile();
	}

	private User _getUser(long companyId) throws Exception {
		Role role = _roleLocalService.fetchRole(
			companyId, RoleConstants.ADMINISTRATOR);

		if (role == null) {
			return _userLocalService.fetchGuestUser(companyId);
		}

		List<User> adminUsers = _userLocalService.getRoleUsers(
			role.getRoleId(), 0, 1);

		if (adminUsers.isEmpty()) {
			return _userLocalService.fetchGuestUser(companyId);
		}

		return adminUsers.get(0);
	}

	private String _removeFirst(String s, String oldSub) {
		int index = s.indexOf(oldSub);

		return s.substring(index + oldSub.length());
	}

	private void _upgradeCompany(long companyId) throws Exception {
		Group group = _groupLocalService.fetchGroup(
			companyId, GroupConstants.CMS);

		if (group == null) {
			return;
		}

		User user = _getUser(companyId);

		if (user == null) {
			return;
		}

		File file = _getLayoutPageTemplatesFile();

		if (file == null) {
			return;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setCompanyId(companyId);
		serviceContext.setScopeGroupId(group.getGroupId());
		serviceContext.setUserId(user.getUserId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			_layoutsImporter.importFile(
				user.getUserId(), group.getGroupId(), file,
				LayoutsImportStrategy.DO_NOT_OVERWRITE, true);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		_backfillContentEditorMasterExternalReferenceCode(group.getGroupId());
	}

	private static final String _SITE_INITIALIZER_CMS_SYMBOLIC_NAME =
		"com.liferay.site.initializer.cms";

	private final CompanyLocalService _companyLocalService;
	private final GroupLocalService _groupLocalService;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;
	private final LayoutsImporter _layoutsImporter;
	private final RoleLocalService _roleLocalService;
	private final UserLocalService _userLocalService;
	private final ZipWriterFactory _zipWriterFactory;

}