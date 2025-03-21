/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.upload.UniqueFileNameProvider;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/add_fragment_collection_resource"
	},
	service = MVCActionCommand.class
)
public class AddFragmentCollectionResourceMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long fragmentCollectionId = ParamUtil.getLong(
			actionRequest, "fragmentCollectionId");

		FragmentCollection fragmentCollection =
			_fragmentCollectionService.fetchFragmentCollection(
				fragmentCollectionId);

		if (fragmentCollection == null) {
			return;
		}

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		String uniqueFileName = _uniqueFileNameProvider.provide(
			fileEntry.getFileName(),
			curFileName -> _exists(
				serviceContext, fragmentCollection, curFileName));

		PortletFileRepositoryUtil.addPortletFileEntry(
			null, serviceContext.getScopeGroupId(), serviceContext.getUserId(),
			FragmentCollection.class.getName(),
			fragmentCollection.getFragmentCollectionId(),
			FragmentPortletKeys.FRAGMENT,
			fragmentCollection.getResourcesFolderId(),
			fileEntry.getContentStream(), uniqueFileName,
			fileEntry.getMimeType(), false);

		TempFileEntryUtil.deleteTempFileEntry(fileEntry.getFileEntryId());

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		sendRedirect(actionRequest, actionResponse, redirect);
	}

	private boolean _exists(
		ServiceContext serviceContext, FragmentCollection fragmentCollection,
		String curFileName) {

		try {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(
				serviceContext.getScopeGroupId(),
				fragmentCollection.getResourcesFolderId(), curFileName);

			if (fileEntry != null) {
				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddFragmentCollectionResourceMVCActionCommand.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

}