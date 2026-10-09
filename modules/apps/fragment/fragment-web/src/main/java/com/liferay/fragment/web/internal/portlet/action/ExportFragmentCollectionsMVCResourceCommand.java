/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.FileInputStream;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/export_fragment_collections"
	},
	service = MVCResourceCommand.class
)
public class ExportFragmentCollectionsMVCResourceCommand
	implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		long[] exportFragmentCollectionIds = null;

		long fragmentCollectionId = ParamUtil.getLong(
			resourceRequest, "fragmentCollectionId");

		if (fragmentCollectionId > 0) {
			exportFragmentCollectionIds = new long[] {fragmentCollectionId};
		}
		else {
			exportFragmentCollectionIds = ParamUtil.getLongValues(
				resourceRequest, "rowIds");
		}

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_portletResourcePermission.check(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroup(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

			List<FragmentCollection> fragmentCollections =
				_fragmentCollectionLocalService.
					getExportableFragmentCollections(
						exportFragmentCollectionIds);

			ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

			for (FragmentCollection fragmentCollection : fragmentCollections) {
				if ((fragmentCollection.getGroupId() !=
						themeDisplay.getCompanyGroupId()) &&
					(fragmentCollection.getGroupId() !=
						themeDisplay.getScopeGroupId())) {

					continue;
				}

				fragmentCollection.populateZipWriter(zipWriter);
			}

			PortletResponseUtil.sendFile(
				resourceRequest, resourceResponse,
				"collections-" + Time.getTimestamp() + ".zip",
				new FileInputStream(zipWriter.getFile()),
				ContentTypes.APPLICATION_ZIP);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		return false;
	}

	@Reference
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Reference(
		target = "(resource.name=" + FragmentConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}