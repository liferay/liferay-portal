/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.util.DLAppHelperThreadLocal;
import com.liferay.object.field.attachment.AttachmentManager;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.repository.temporaryrepository.TemporaryFileEntryRepository;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fábio Alves
 */
@Component(service = ModelListener.class)
public class DepotEntryModelListener extends BaseModelListener<DepotEntry> {

	@Override
	public void onAfterCreate(DepotEntry depotEntry)
		throws ModelListenerException {

		if (!FeatureFlagManagerUtil.isEnabled(
				depotEntry.getCompanyId(), "LPD-17564") ||
			(depotEntry.getType() != DepotConstants.TYPE_SPACE)) {

			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", depotEntry.getCompanyId());

		if (objectDefinition == null) {
			return;
		}

		try {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			if (serviceContext == null) {
				serviceContext = new ServiceContext();
			}

			Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

			_attachmentManager.getDLFolder(
				objectDefinition.getCompanyId(), group.getGroupId(),
				objectDefinition.getPortletId(), serviceContext,
				PrincipalThreadLocal.getUserId());

			try (SafeCloseable safeCloseable =
					DLAppHelperThreadLocal.setEnabledWithSafeCloseable(false)) {

				Repository repository = _repositoryLocalService.fetchRepository(
					group.getGroupId(), TempFileEntryUtil.class.getName(),
					TempFileEntryUtil.class.getName());

				if (repository != null) {
					return;
				}

				_repositoryLocalService.addRepository(
					null, PrincipalThreadLocal.getUserId(), group.getGroupId(),
					_portal.getClassNameId(
						TemporaryFileEntryRepository.class.getName()),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					TempFileEntryUtil.class.getName(), StringPool.BLANK,
					TempFileEntryUtil.class.getName(), new UnicodeProperties(),
					true, serviceContext);
			}
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Reference
	private AttachmentManager _attachmentManager;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RepositoryLocalService _repositoryLocalService;

}