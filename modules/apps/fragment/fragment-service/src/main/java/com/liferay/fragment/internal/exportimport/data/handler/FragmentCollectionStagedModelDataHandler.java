/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.exportimport.data.handler;

import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.xml.Element;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = StagedModelDataHandler.class)
public class FragmentCollectionStagedModelDataHandler
	extends BaseStagedModelDataHandler<FragmentCollection> {

	public static final String[] CLASS_NAMES = {
		FragmentCollection.class.getName()
	};

	@Override
	public void deleteStagedModel(FragmentCollection fragmentCollection)
		throws PortalException {

		_stagedModelRepository.deleteStagedModel(fragmentCollection);
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
	public String getDisplayName(FragmentCollection fragmentCollection) {
		return fragmentCollection.getName();
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			FragmentCollection fragmentCollection)
		throws Exception {

		if (fragmentCollection.isMarketplace() &&
			!ExportImportThreadLocal.isStagingInProcess()) {

			return;
		}

		Element fragmentCollectionElement =
			portletDataContext.getExportDataElement(fragmentCollection);

		portletDataContext.addClassedModel(
			fragmentCollectionElement,
			ExportImportPathUtil.getModelPath(fragmentCollection),
			fragmentCollection);

		for (FileEntry fileEntry : fragmentCollection.getResources()) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, fragmentCollection, fileEntry,
				PortletDataContext.REFERENCE_TYPE_WEAK);
		}
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			FragmentCollection fragmentCollection)
		throws Exception {

		FragmentCollection importedFragmentCollection =
			(FragmentCollection)fragmentCollection.clone();

		importedFragmentCollection.setGroupId(
			portletDataContext.getScopeGroupId());

		FragmentCollection existingFragmentCollection =
			_stagedModelRepository.fetchStagedModelByUuidAndGroupId(
				fragmentCollection.getUuid(),
				portletDataContext.getScopeGroupId());

		if ((existingFragmentCollection == null) ||
			!portletDataContext.isDataStrategyMirror()) {

			importedFragmentCollection = _stagedModelRepository.addStagedModel(
				portletDataContext, importedFragmentCollection);
		}
		else {
			importedFragmentCollection.setMvccVersion(
				existingFragmentCollection.getMvccVersion());
			importedFragmentCollection.setFragmentCollectionId(
				existingFragmentCollection.getFragmentCollectionId());

			importedFragmentCollection =
				_stagedModelRepository.updateStagedModel(
					portletDataContext, importedFragmentCollection);
		}

		portletDataContext.importClassedModel(
			fragmentCollection, importedFragmentCollection);
	}

	@Override
	protected StagedModelRepository<FragmentCollection>
		getStagedModelRepository() {

		return _stagedModelRepository;
	}

	@Reference(
		target = "(model.class.name=com.liferay.fragment.model.FragmentCollection)",
		unbind = "-"
	)
	private StagedModelRepository<FragmentCollection> _stagedModelRepository;

}