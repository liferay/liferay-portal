/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.lar;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerChoice;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 * @author Raymond Augé
 * @author Sampsa Sohlman
 * @author Máté Thurzó
 * @author Zsolt Berentey
 * @author Gergely Mathe
 */
@Component(
	property = "jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
	service = PortletDataHandler.class
)
public class DLPortletDataHandler extends BasePortletDataHandler {

	@Override
	public PortletPreferences deleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		return _dlAdminPortletDataHandler.deleteData(
			portletDataContext, portletId, portletPreferences);
	}

	@Override
	public String exportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		return _dlAdminPortletDataHandler.exportData(
			portletDataContext, portletId, portletPreferences);
	}

	@Override
	public String getNamespace() {
		return _dlAdminPortletDataHandler.getNamespace();
	}

	@Override
	public String getSchemaVersion() {
		return _dlAdminPortletDataHandler.getSchemaVersion();
	}

	@Override
	public String getServiceName() {
		return _dlAdminPortletDataHandler.getServiceName();
	}

	@Override
	public PortletPreferences importData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws PortletDataException {

		return _dlAdminPortletDataHandler.importData(
			portletDataContext, portletId, portletPreferences, data);
	}

	@Override
	public boolean isSupportsDataStrategyMirrorWithOverwriting() {
		return _dlAdminPortletDataHandler.
			isSupportsDataStrategyMirrorWithOverwriting();
	}

	@Override
	public void prepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		_dlAdminPortletDataHandler.prepareManifestSummary(
			portletDataContext, portletPreferences);
	}

	@Activate
	protected void activate() {
		setDataLevel(DataLevel.PORTLET_INSTANCE);
		setDataLocalized(true);
		setDataPortletPreferences(
			"rootFolderExternalReferenceCode",
			"selectedGroupExternalReferenceCode",
			"selectedRepositoryExternalReferenceCode");
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(DLFileEntryType.class),
			new StagedModelType(DLFileShortcut.class),
			new StagedModelType(DLFileEntryConstants.getClassName()),
			new StagedModelType(DLFolderConstants.getClassName()),
			new StagedModelType(
				Repository.class.getName(),
				StagedModelType.REFERRER_CLASS_NAME_ALL));
		setExportControls(
			new PortletDataHandlerBoolean(
				getNamespace(), "repositories", true, false, null,
				Repository.class.getName(),
				StagedModelType.REFERRER_CLASS_NAME_ALL),
			new PortletDataHandlerBoolean(
				getNamespace(), "folders", true, false, null,
				DLFolderConstants.getClassName()),
			new PortletDataHandlerBoolean(
				getNamespace(), "documents", true, false,
				new PortletDataHandlerControl[] {
					new PortletDataHandlerBoolean(
						getNamespace(), "previews-and-thumbnails"),
					new PortletDataHandlerBoolean(
						getNamespace(), "referenced-content", true, false,
						new PortletDataHandlerControl[] {
							new PortletDataHandlerChoice(
								getNamespace(), "referenced-content-behavior",
								0,
								new String[] {
									"include-always", "include-if-modified"
								})
						})
				},
				DLFileEntryConstants.getClassName()),
			new PortletDataHandlerBoolean(
				getNamespace(), "document-types", true, false, null,
				DLFileEntryType.class.getName()),
			new PortletDataHandlerBoolean(
				getNamespace(), "metadata", true, false, null,
				DDMStructure.class.getName(),
				DLFileEntryMetadata.class.getName()),
			new PortletDataHandlerBoolean(
				getNamespace(), "shortcuts", true, false, null,
				DLFileShortcutConstants.getClassName()));
		setPublishToLiveByDefault(PropsValues.DL_PUBLISH_TO_LIVE_BY_DEFAULT);
		setRank(90);
		setStagingControls(getExportControls());
	}

	@Reference(
		target = "(jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN + ")"
	)
	private PortletDataHandler _dlAdminPortletDataHandler;

}