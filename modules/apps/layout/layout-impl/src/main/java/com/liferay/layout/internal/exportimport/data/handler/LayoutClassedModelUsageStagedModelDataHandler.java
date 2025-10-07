/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.exportimport.data.handler;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = StagedModelDataHandler.class)
public class LayoutClassedModelUsageStagedModelDataHandler
	extends BaseStagedModelDataHandler<LayoutClassedModelUsage> {

	public static final String[] CLASS_NAMES = {
		LayoutClassedModelUsage.class.getName()
	};

	@Override
	public void deleteStagedModel(
			LayoutClassedModelUsage layoutClassedModelUsage)
		throws PortalException {

		_stagedModelRepository.deleteStagedModel(layoutClassedModelUsage);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		_stagedModelRepository.deleteStagedModel(
			uuid, groupId, className, extraData);
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			LayoutClassedModelUsage layoutClassedModelUsage)
		throws Exception {

		Element element = portletDataContext.getExportDataElement(
			layoutClassedModelUsage);

		element.addAttribute(
			"layout-classed-model-container-class-name",
			_portal.fetchClassName(layoutClassedModelUsage.getContainerType()));

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				layoutClassedModelUsage.getClassName());

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (ExportImportThreadLocal.isStagingInProcess() &&
			(assetRendererFactory != null) &&
			stagingGroupHelper.isStagedPortlet(
				portletDataContext.getScopeGroupId(),
				assetRendererFactory.getPortletId())) {

			AssetRenderer<?> assetRenderer = null;

			try {
				assetRenderer = assetRendererFactory.getAssetRenderer(
					layoutClassedModelUsage.getClassPK());
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}

			if ((assetRenderer != null) &&
				(assetRenderer.getStatus() ==
					WorkflowConstants.STATUS_APPROVED)) {

				if (ExportImportThreadLocal.isStagingInProcess()) {
					portletDataContext.addReferenceElement(
						layoutClassedModelUsage, element,
						(StagedModel)assetRenderer.getAssetObject(),
						PortletDataContext.REFERENCE_TYPE_WEAK, true);
				}
				else {
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, layoutClassedModelUsage,
						(StagedModel)assetRenderer.getAssetObject(),
						PortletDataContext.REFERENCE_TYPE_DEPENDENCY,
						assetRendererFactory.getPortletId());
				}
			}
		}

		portletDataContext.addClassedModel(
			element, ExportImportPathUtil.getModelPath(layoutClassedModelUsage),
			layoutClassedModelUsage);
	}

	@Override
	protected void doImportMissingReference(
			PortletDataContext portletDataContext, String uuid, long groupId,
			long layoutClassedModelUsageId)
		throws Exception {

		LayoutClassedModelUsage existingLayoutClassedModelUsage =
			fetchMissingReference(uuid, groupId);

		if (existingLayoutClassedModelUsage == null) {
			return;
		}

		Map<Long, Long> layoutClassedModelUsageids =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				LayoutClassedModelUsage.class);

		layoutClassedModelUsageids.put(
			layoutClassedModelUsageId,
			existingLayoutClassedModelUsage.getLayoutClassedModelUsageId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			LayoutClassedModelUsage layoutClassedModelUsage)
		throws Exception {

		LayoutClassedModelUsage importedLayoutClassedModelUsage =
			(LayoutClassedModelUsage)layoutClassedModelUsage.clone();

		importedLayoutClassedModelUsage.setGroupId(
			portletDataContext.getScopeGroupId());
		importedLayoutClassedModelUsage.setCompanyId(
			portletDataContext.getCompanyId());

		Map<Long, Long> plids =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				Layout.class);

		long plid = MapUtil.getLong(
			plids, layoutClassedModelUsage.getPlid(),
			layoutClassedModelUsage.getPlid());

		importedLayoutClassedModelUsage.setPlid(plid);

		Map<Long, Long> classPKs =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				layoutClassedModelUsage.getClassName());

		long classPK = MapUtil.getLong(
			classPKs, layoutClassedModelUsage.getClassPK(),
			layoutClassedModelUsage.getClassPK());

		importedLayoutClassedModelUsage.setClassPK(classPK);

		Element element = portletDataContext.getImportDataStagedModelElement(
			layoutClassedModelUsage);

		long containerTypeClassNameId = _portal.getClassNameId(
			element.attributeValue(
				"layout-classed-model-container-class-name"));

		importedLayoutClassedModelUsage.setContainerType(
			containerTypeClassNameId);

		if (containerTypeClassNameId == _portal.getClassNameId(
				FragmentEntryLink.class)) {

			long containerKey = GetterUtil.getLong(
				layoutClassedModelUsage.getContainerKey());

			if (containerKey != 0) {
				Map<Long, Long> fragmentLinkEntryIds =
					(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
						FragmentEntryLink.class.getName());

				containerKey = MapUtil.getLong(
					fragmentLinkEntryIds, containerKey, containerKey);

				importedLayoutClassedModelUsage.setContainerKey(
					String.valueOf(containerKey));
			}
		}

		LayoutClassedModelUsage existingLayoutClassedModelUsage =
			_layoutClassedModelUsageLocalService.fetchLayoutClassedModelUsage(
				portletDataContext.getScopeGroupId(),
				importedLayoutClassedModelUsage.getClassExternalReferenceCode(),
				importedLayoutClassedModelUsage.getClassNameId(), classPK,
				importedLayoutClassedModelUsage.getContainerKey(),
				containerTypeClassNameId, plid);

		if (existingLayoutClassedModelUsage == null) {
			existingLayoutClassedModelUsage =
				_stagedModelRepository.fetchStagedModelByUuidAndGroupId(
					layoutClassedModelUsage.getUuid(),
					portletDataContext.getScopeGroupId());
		}

		if ((existingLayoutClassedModelUsage == null) ||
			!portletDataContext.isDataStrategyMirror()) {

			importedLayoutClassedModelUsage =
				_stagedModelRepository.addStagedModel(
					portletDataContext, importedLayoutClassedModelUsage);
		}
		else {
			importedLayoutClassedModelUsage.setMvccVersion(
				existingLayoutClassedModelUsage.getMvccVersion());
			importedLayoutClassedModelUsage.setLayoutClassedModelUsageId(
				existingLayoutClassedModelUsage.getLayoutClassedModelUsageId());

			importedLayoutClassedModelUsage =
				_stagedModelRepository.updateStagedModel(
					portletDataContext, importedLayoutClassedModelUsage);
		}

		portletDataContext.importClassedModel(
			layoutClassedModelUsage, importedLayoutClassedModelUsage);
	}

	@Override
	protected StagedModelRepository<LayoutClassedModelUsage>
		getStagedModelRepository() {

		return _stagedModelRepository;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutClassedModelUsageStagedModelDataHandler.class);

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(model.class.name=com.liferay.layout.model.LayoutClassedModelUsage)",
		unbind = "-"
	)
	private StagedModelRepository<LayoutClassedModelUsage>
		_stagedModelRepository;

}