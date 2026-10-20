/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.workflow;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 * @author Jorge Ferrer
 * @author Alexander Chow
 */
@Component(
	property = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = WorkflowHandler.class
)
public class DLFileEntryWorkflowHandler
	extends BaseWorkflowHandler<DLFileEntry> {

	@Override
	public void contributeWorkflowContext(
			Map<String, Serializable> workflowContext)
		throws PortalException {

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			WorkflowConstants.CONTEXT_SERVICE_CONTEXT);

		Map<String, Serializable> attributes = serviceContext.getAttributes();

		Set<String> keys = attributes.keySet();

		keys.removeIf(
			key -> StringUtil.startsWith(key, DDMFormValues.class.getName()));

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		boolean hasAssetDisplayPage = GetterUtil.getBoolean(
			serviceContext.getAttribute("hasAssetDisplayPage"));

		if ((themeDisplay == null) || !hasAssetDisplayPage) {
			return;
		}

		long classPK = GetterUtil.getLong(
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		DLFileVersion dlFileVersion = _dlFileVersionLocalService.getFileVersion(
			classPK);

		String friendlyURL =
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				new InfoItemReference(
					FileEntry.class.getName(),
					new ClassPKInfoItemIdentifier(
						dlFileVersion.getFileEntryId())),
				themeDisplay);

		if (Validator.isNotNull(friendlyURL)) {
			serviceContext.setAttribute("friendlyURL", friendlyURL);

			workflowContext.put(WorkflowConstants.CONTEXT_URL, friendlyURL);
		}
	}

	@Override
	public AssetRenderer<DLFileEntry> getAssetRenderer(long classPK)
		throws PortalException {

		AssetRendererFactory<DLFileEntry> assetRendererFactory =
			getAssetRendererFactory();

		if (assetRendererFactory == null) {
			return null;
		}

		return assetRendererFactory.getAssetRenderer(
			classPK, AssetRendererFactory.TYPE_LATEST);
	}

	@Override
	public AssetRendererFactory<DLFileEntry> getAssetRendererFactory() {
		return AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
			DLFileEntry.class);
	}

	@Override
	public String getClassName() {
		return DLFileEntry.class.getName();
	}

	@Override
	public long getDiscussionClassPK(
		Map<String, Serializable> workflowContext) {

		try {
			AssetRenderer<DLFileEntry> dlFileEntryAssetRenderer =
				getAssetRenderer(super.getDiscussionClassPK(workflowContext));

			return dlFileEntryAssetRenderer.getClassPK();
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return super.getDiscussionClassPK(workflowContext);
	}

	@Override
	public String getType(Locale locale) {
		return ResourceActionsUtil.getModelResource(locale, getClassName());
	}

	@Override
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, long classPK)
		throws PortalException {

		DLFileVersion dlFileVersion = _dlFileVersionLocalService.getFileVersion(
			classPK);

		long folderId = dlFileVersion.getFolderId();

		while (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			DLFolder dlFolder = _dlFolderLocalService.getFolder(folderId);

			if (dlFolder.getRestrictionType() !=
					DLFolderConstants.RESTRICTION_TYPE_INHERIT) {

				break;
			}

			folderId = dlFolder.getParentFolderId();
		}

		WorkflowDefinitionLink workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
				companyId, groupId, DLFolder.class.getName(), folderId,
				dlFileVersion.getFileEntryTypeId(), true);

		if (workflowDefinitionLink == null) {
			workflowDefinitionLink =
				_workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
					companyId, groupId, DLFolder.class.getName(), folderId,
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_ALL, true);
		}

		return workflowDefinitionLink;
	}

	@Override
	public boolean isVisible() {
		return _VISIBLE;
	}

	@Override
	public DLFileEntry updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		long userId = GetterUtil.getLong(
			(String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID));
		long classPK = GetterUtil.getLong(
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			"serviceContext");

		return _dlFileEntryLocalService.updateStatus(
			userId, classPK, status, serviceContext, workflowContext);
	}

	private static final boolean _VISIBLE = false;

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryWorkflowHandler.class);

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}