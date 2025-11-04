/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.model.listener;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = ModelListener.class)
public class LayoutModelListener extends BaseModelListener<Layout> {

	@Override
	public void onAfterCreate(Layout layout) throws ModelListenerException {
		if (!layout.isTypeContent() && !layout.isTypeAssetDisplay()) {
			return;
		}

		if (layout.isTypeContent() && !layout.isTypeUtility()) {
			_reindexLayout(layout);
		}

		if ((ExportImportThreadLocal.isImportInProcess() ||
			 ExportImportThreadLocal.isStagingInProcess()) &&
			!LazyReferencingThreadLocal.isEnabled()) {

			return;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		try {
			SegmentsExperience segmentsExperience =
				_addDefaultSegmentsExperience(layout, serviceContext);

			_layoutPageTemplateStructureLocalService.
				addLayoutPageTemplateStructure(
					layout.getUserId(), layout.getGroupId(), layout.getPlid(),
					segmentsExperience.getSegmentsExperienceId(),
					_generateContentLayoutStructure(), serviceContext);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_getLayoutPageTemplateEntry(layout);

		if (layoutPageTemplateEntry != null) {
			TransactionCommitCallbackUtil.registerCallback(
				() -> _copyStructure(layoutPageTemplateEntry, layout));
		}
	}

	@Override
	public void onAfterRemove(Layout layout) throws ModelListenerException {
		if (!(layout.isTypeAssetDisplay() || layout.isTypeContent())) {
			return;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry == null) {
			return;
		}

		try {
			_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
				layoutPageTemplateEntry);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			throw new ModelListenerException(portalException);
		}
	}

	@Override
	public void onAfterUpdate(Layout originalLayout, Layout layout)
		throws ModelListenerException {

		if (layout.isTypeContent() && !layout.isTypeUtility()) {
			_reindexLayout(layout);
		}
	}

	@Override
	public void onBeforeRemove(Layout layout) throws ModelListenerException {
		if (!(layout.isTypeAssetDisplay() || layout.isTypeContent())) {
			return;
		}

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		if (layoutPageTemplateStructure != null) {
			_layoutPageTemplateStructureLocalService.
				deleteLayoutPageTemplateStructure(layoutPageTemplateStructure);
		}

		try {
			_segmentsExperienceLocalService.deleteSegmentsExperiences(
				layout.getGroupId(), layout.getPlid());

			if (!layout.isTypeContent()) {
				return;
			}

			Indexer<Layout> indexer = IndexerRegistryUtil.getIndexer(
				Layout.class);

			indexer.delete(layout);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			throw new ModelListenerException(portalException);
		}
	}

	private SegmentsExperience _addDefaultSegmentsExperience(
			Layout layout, ServiceContext serviceContext)
		throws PortalException {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				layout.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				layout.getPlid());

		if (segmentsExperience != null) {
			return segmentsExperience;
		}

		return _segmentsExperienceLocalService.addDefaultSegmentsExperience(
			GetterUtil.getString(
				serviceContext.getAttribute(
					"defaultSegmentsExperienceExternalReferenceCode"),
				null),
			layout.getUserId(), layout.getPlid(), serviceContext);
	}

	private void _copySiteNavigationMenuId(
		Layout layout, UnicodeProperties unicodeProperties) {

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		if (typeSettingsUnicodeProperties.containsKey("siteNavigationMenuId")) {
			String siteNavigationMenuId =
				typeSettingsUnicodeProperties.getProperty(
					"siteNavigationMenuId");

			unicodeProperties.put("siteNavigationMenuId", siteNavigationMenuId);
		}

		if (typeSettingsUnicodeProperties.containsKey(
				"siteNavigationMenuName")) {

			String siteNavigationMenuName =
				typeSettingsUnicodeProperties.getProperty(
					"siteNavigationMenuName");

			unicodeProperties.put(
				"siteNavigationMenuName", siteNavigationMenuName);
		}
	}

	private Void _copyStructure(
			LayoutPageTemplateEntry layoutPageTemplateEntry, Layout layout)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		Layout layoutPageTemplateEntryLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		_layoutPageTemplateStructureLocalService.
			fetchLayoutPageTemplateStructure(
				layoutPageTemplateEntryLayout.getGroupId(),
				layoutPageTemplateEntryLayout.getPlid());

		draftLayout = _layoutLocalService.copyLayoutContent(
			layoutPageTemplateEntryLayout, draftLayout);

		draftLayout.setStatus(WorkflowConstants.STATUS_APPROVED);

		UnicodeProperties unicodeProperties =
			draftLayout.getTypeSettingsProperties();

		unicodeProperties.put(
			LayoutTypeSettingsConstants.KEY_PUBLISHED,
			Boolean.FALSE.toString());

		_copySiteNavigationMenuId(layout, unicodeProperties);

		_layoutLocalService.updateLayout(draftLayout);

		_layoutLocalService.copyLayoutContent(
			layoutPageTemplateEntryLayout, layout);

		return null;
	}

	private String _generateContentLayoutStructure() {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		int layoutPageTemplateEntryType = GetterUtil.getInteger(
			serviceContext.getAttribute("layout.page.template.entry.type"),
			LayoutPageTemplateEntryTypeConstants.BASIC);

		if (!Objects.equals(
				layoutPageTemplateEntryType,
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)) {

			return layoutStructure.toString();
		}

		layoutStructure.addDropZoneLayoutStructureItem(
			rootLayoutStructureItem.getItemId(), 0);

		return layoutStructure.toString();
	}

	private LayoutPageTemplateEntry _getLayoutPageTemplateEntry(Layout layout) {
		long classNameId = _portal.getClassNameId(
			LayoutPageTemplateEntry.class);

		if (layout.getClassNameId() != classNameId) {
			return null;
		}

		return _layoutPageTemplateEntryLocalService.
			fetchLayoutPageTemplateEntry(layout.getClassPK());
	}

	private void _reindexLayout(Layout layout) {
		Indexer<Layout> indexer = IndexerRegistryUtil.getIndexer(Layout.class);

		if ((indexer == null) || !layout.isApproved() || layout.isSystem()) {
			return;
		}

		try {
			indexer.reindex(layout);
		}
		catch (SearchException searchException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to reindex layout " + layout.getPlid(),
					searchException);
			}

			throw new ModelListenerException(searchException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutModelListener.class);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}