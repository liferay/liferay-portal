/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.security.permission.resource;

import com.liferay.exportimport.kernel.staging.permission.StagingPermission;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.BaseModelResourcePermissionWrapper;
import com.liferay.portal.kernel.security.permission.resource.DynamicInheritancePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.StagedModelPermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.WorkflowedModelPermissionLogic;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.PropsValues;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalArticle",
	service = ModelResourcePermission.class
)
public class JournalArticleModelResourcePermissionWrapper
	extends BaseModelResourcePermissionWrapper<JournalArticle> {

	@Override
	protected ModelResourcePermission<JournalArticle>
		doGetModelResourcePermission() {

		return ModelResourcePermissionFactory.create(
			JournalArticle.class, JournalArticle::getResourcePrimKey,
			classPK -> {
				JournalArticle article =
					_journalArticleLocalService.fetchLatestArticle(classPK);

				if (article != null) {
					return article;
				}

				return _journalArticleLocalService.getArticle(classPK);
			},
			_portletResourcePermission,
			(modelResourcePermission, consumer) -> {
				consumer.accept(
					new StagedModelPermissionLogic<JournalArticle>(
						_stagingPermission, JournalPortletKeys.JOURNAL,
						JournalArticle::getResourcePrimKey) {

						@Override
						public Boolean contains(
							PermissionChecker permissionChecker, String name,
							JournalArticle journalArticle, String actionId) {

							if (actionId.equals(ActionKeys.SUBSCRIBE)) {
								return null;
							}

							return super.contains(
								permissionChecker, name, journalArticle,
								actionId);
						}

					});
				consumer.accept(
					new WorkflowedModelPermissionLogic<>(
						modelResourcePermission, _groupLocalService,
						JournalArticle::getId));

				if (PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE) {
					consumer.accept(
						new DynamicInheritancePermissionLogic<>(
							_journalFolderModelResourcePermission,
							_getFetchParentFunction(), true));
				}
			});
	}

	private UnsafeFunction<JournalArticle, JournalFolder, PortalException>
		_getFetchParentFunction() {

		return article -> {
			long folderId = article.getFolderId();

			if (JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID == folderId) {
				return null;
			}

			if (article.isInTrash()) {
				return _journalFolderLocalService.fetchFolder(folderId);
			}

			return _journalFolderLocalService.getFolder(folderId);
		};
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private JournalFolderLocalService _journalFolderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalFolder)"
	)
	private ModelResourcePermission<JournalFolder>
		_journalFolderModelResourcePermission;

	@Reference(
		target = "(resource.name=" + JournalConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private StagingPermission _stagingPermission;

}