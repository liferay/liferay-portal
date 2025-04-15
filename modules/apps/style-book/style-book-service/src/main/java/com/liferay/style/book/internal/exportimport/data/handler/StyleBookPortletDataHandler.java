/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import jakarta.portlet.PortletPreferences;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
	service = PortletDataHandler.class
)
public class StyleBookPortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "style-books";

	@Override
	public boolean isConfigurationEnabled() {
		return false;
	}

	@Override
	public boolean isStaged() {
		return false;
	}

	@Activate
	protected void activate() {
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(StyleBookEntry.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "entries", true, false, null,
				StyleBookEntry.class.getName()));
		setPublishToLiveByDefault(true);
		setStagingControls(getExportControls());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				StyleBookPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_styleBookEntryLocalService.deleteStyleBookEntries(
			portletDataContext.getScopeGroupId());

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		Element rootElement = addExportDataRootElement(portletDataContext);

		if (!portletDataContext.getBooleanParameter(NAMESPACE, "entries")) {
			return getExportDataRootElementString(rootElement);
		}

		portletDataContext.addPortletPermissions(
			StyleBookConstants.RESOURCE_NAME);

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		ActionableDynamicQuery exportActionableDynamicQuery =
			_stagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		exportActionableDynamicQuery.performActions();

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		if (!portletDataContext.getBooleanParameter(NAMESPACE, "entries")) {
			return null;
		}

		portletDataContext.importPortletPermissions(
			StyleBookConstants.RESOURCE_NAME);

		Element styleBookEntriesElement =
			portletDataContext.getImportDataGroupElement(StyleBookEntry.class);

		List<Element> styleBookEntriesElements =
			styleBookEntriesElement.elements();

		for (Element styleBookEntryElement : styleBookEntriesElements) {
			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, styleBookEntryElement);
		}

		return null;
	}

	@Override
	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		if (ExportImportDateUtil.isRangeFromLastPublishDate(
				portletDataContext)) {

			_staging.populateLastPublishDateCounts(
				portletDataContext,
				new StagedModelType[] {
					new StagedModelType(StyleBookEntry.class.getName())
				});

			return;
		}

		ActionableDynamicQuery exportActionableDynamicQuery =
			_stagedModelRepository.getExportActionableDynamicQuery(
				portletDataContext);

		exportActionableDynamicQuery.performCount();
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference(
		target = "(model.class.name=com.liferay.style.book.model.StyleBookEntry)",
		unbind = "-"
	)
	private StagedModelRepository<StyleBookEntry> _stagedModelRepository;

	@Reference
	private Staging _staging;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}