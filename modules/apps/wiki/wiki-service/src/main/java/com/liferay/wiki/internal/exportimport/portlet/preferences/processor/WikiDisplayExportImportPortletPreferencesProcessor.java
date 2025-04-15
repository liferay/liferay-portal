/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.service.persistence.WikiNodeUtil;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ReadOnlyException;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = "jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY,
	service = ExportImportPortletPreferencesProcessor.class
)
public class WikiDisplayExportImportPortletPreferencesProcessor
	implements ExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return null;
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(_capability);
	}

	@Override
	public boolean isPublishDisplayedContent() {
		return false;
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		String portletId = portletDataContext.getPortletId();

		long nodeId = GetterUtil.getLong(
			portletPreferences.getValue("nodeId", StringPool.BLANK));

		if (nodeId <= 0) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Node ID is not set for preferences of portlet " +
						portletId);
			}

			return portletPreferences;
		}

		String title = portletPreferences.getValue("title", null);

		if (title == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Title is not set for preferences of portlet " + portletId);
			}

			return portletPreferences;
		}

		WikiNode node = WikiNodeUtil.fetchByPrimaryKey(nodeId);

		if (node == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to find wiki node");
			}

			return portletPreferences;
		}

		try {
			portletDataContext.addPortletPermissions(
				WikiConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			throw new PortletDataException(
				"Unable to export portlet permissions", portalException);
		}

		StagedModelDataHandlerUtil.exportReferenceStagedModel(
			portletDataContext, portletId, node);

		ActionableDynamicQuery actionableDynamicQuery =
			_getPageActionableDynamicQuery(
				portletDataContext, node.getNodeId(), portletId);

		try {
			actionableDynamicQuery.performActions();
		}
		catch (PortalException portalException) {
			throw new PortletDataException(
				"Unable to export referenced pages", portalException);
		}

		return portletPreferences;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			portletDataContext.importPortletPermissions(
				WikiConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			throw new PortletDataException(
				"Unable to import portlet permissions", portalException);
		}

		long nodeId = GetterUtil.getLong(
			portletPreferences.getValue("nodeId", StringPool.BLANK));

		if (nodeId > 0) {
			Map<Long, Long> nodeIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					WikiNode.class);

			nodeId = MapUtil.getLong(nodeIds, nodeId, nodeId);

			try {
				portletPreferences.setValue("nodeId", String.valueOf(nodeId));
			}
			catch (ReadOnlyException readOnlyException) {
				throw new PortletDataException(
					"Unable to update portlet preferences during import",
					readOnlyException);
			}
		}

		return portletPreferences;
	}

	private ActionableDynamicQuery _getPageActionableDynamicQuery(
		PortletDataContext portletDataContext, long nodeId, String portletId) {

		ActionableDynamicQuery actionableDynamicQuery =
			_wikiPageLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		ActionableDynamicQuery.AddCriteriaMethod addCriteriaMethod =
			actionableDynamicQuery.getAddCriteriaMethod();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				addCriteriaMethod.addCriteria(dynamicQuery);

				Property property = PropertyFactoryUtil.forName("nodeId");

				dynamicQuery.add(property.eq(nodeId));
			});

		actionableDynamicQuery.setPerformActionMethod(
			(WikiPage page) ->
				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, portletId, page));

		return actionableDynamicQuery;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiDisplayExportImportPortletPreferencesProcessor.class);

	@Reference(target = "(name=ReferencedStagedModelImporter)")
	private Capability _capability;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

}