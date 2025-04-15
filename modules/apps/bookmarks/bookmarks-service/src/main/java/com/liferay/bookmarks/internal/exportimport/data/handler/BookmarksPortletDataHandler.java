/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.internal.exportimport.data.handler;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Bruno Farache
 * @author Raymond Augé
 * @author Juan Fernández
 * @author Máté Thurzó
 * @author Daniel Kocsis
 * @author Gergely Mathe
 */
@Component(
	property = "jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
	service = PortletDataHandler.class
)
public class BookmarksPortletDataHandler extends BasePortletDataHandler {

	@Override
	public PortletPreferences deleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		return _bookmarksAdminPortletDataHandler.deleteData(
			portletDataContext, portletId, portletPreferences);
	}

	@Override
	public String exportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		return _bookmarksAdminPortletDataHandler.exportData(
			portletDataContext, portletId, portletPreferences);
	}

	@Override
	public String getNamespace() {
		return _bookmarksAdminPortletDataHandler.getNamespace();
	}

	@Override
	public String getSchemaVersion() {
		return _bookmarksAdminPortletDataHandler.getSchemaVersion();
	}

	@Override
	public String getServiceName() {
		return _bookmarksAdminPortletDataHandler.getServiceName();
	}

	@Override
	public PortletPreferences importData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws PortletDataException {

		return _bookmarksAdminPortletDataHandler.importData(
			portletDataContext, portletId, portletPreferences, data);
	}

	@Override
	public void prepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		_bookmarksAdminPortletDataHandler.prepareManifestSummary(
			portletDataContext, portletPreferences);
	}

	@Override
	public boolean validateSchemaVersion(String schemaVersion) {
		return _bookmarksAdminPortletDataHandler.validateSchemaVersion(
			schemaVersion);
	}

	@Activate
	protected void activate() {
		setDataLevel(DataLevel.PORTLET_INSTANCE);
		setDataPortletPreferences("rootFolderId");
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(BookmarksEntry.class),
			new StagedModelType(BookmarksFolder.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				getNamespace(), "folders", true, false, null,
				BookmarksFolder.class.getName()),
			new PortletDataHandlerBoolean(
				getNamespace(), "entries", true, false, null,
				BookmarksEntry.class.getName()));
		setStagingControls(getExportControls());
	}

	@Reference(
		target = "(jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN + ")"
	)
	private PortletDataHandler _bookmarksAdminPortletDataHandler;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}