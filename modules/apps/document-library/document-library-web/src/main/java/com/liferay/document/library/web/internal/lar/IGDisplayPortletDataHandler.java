/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.lar;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Raymond Augé
 */
@Component(
	property = "jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
	service = PortletDataHandler.class
)
public class IGDisplayPortletDataHandler extends BasePortletDataHandler {

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Activate
	protected void activate() {
		setDataLevel(DataLevel.PORTLET_INSTANCE);
		setDataPortletPreferences(
			"rootFolderExternalReferenceCode",
			"selectedGroupExternalReferenceCode",
			"selectedRepositoryExternalReferenceCode");
		setExportControls(new PortletDataHandlerControl[0]);
		setPublishToLiveByDefault(PropsValues.DL_PUBLISH_TO_LIVE_BY_DEFAULT);
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletPreferences == null) {
			return portletPreferences;
		}

		portletPreferences.setValue("fileEntriesPerPage", StringPool.BLANK);
		portletPreferences.setValue("fileEntryColumns", StringPool.BLANK);
		portletPreferences.setValue("folderColumns", StringPool.BLANK);
		portletPreferences.setValue("foldersPerPage", StringPool.BLANK);
		portletPreferences.setValue(
			"rootFolderExternalReferenceCode", StringPool.BLANK);
		portletPreferences.setValue(
			"selectedGroupExternalReferenceCode", StringPool.BLANK);
		portletPreferences.setValue(
			"selectedRepositoryExternalReferenceCode", StringPool.BLANK);
		portletPreferences.setValue("showSubfolders", StringPool.BLANK);

		return portletPreferences;
	}

}