/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.exportimport.data.handler;

import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.data.handler.base.BaseStagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.segments.model.SegmentsExperience;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Pavel Savinov
 */
@Component(service = StagedModelDataHandler.class)
public class FragmentEntryLinkStagedModelDataHandler
	extends BaseStagedModelDataHandler<FragmentEntryLink> {

	public static final String[] CLASS_NAMES = {
		FragmentEntryLink.class.getName()
	};

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext,
			FragmentEntryLink fragmentEntryLink)
		throws Exception {

		Element fragmentEntryLinkElement =
			portletDataContext.getExportDataElement(fragmentEntryLink);

		if (!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.PORTLET_DATA) &&
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			portletDataContext.addClassedModel(
				fragmentEntryLinkElement,
				ExportImportPathUtil.getModelPath(fragmentEntryLink),
				fragmentEntryLink);

			return;
		}

		String html = fragmentEntryLink.getHtml();

		if (Validator.isNotNull(html)) {
			html =
				_dlReferencesExportImportContentProcessor.
					replaceExportContentReferences(
						portletDataContext, fragmentEntryLink, html, true,
						false);
		}

		fragmentEntryLink.setHtml(html);

		String editableValues = fragmentEntryLink.getEditableValues();

		if (Validator.isNotNull(editableValues)) {
			editableValues =
				_fragmentEntryLinkExportImportContentProcessor.
					replaceExportContentReferences(
						portletDataContext, fragmentEntryLink, editableValues,
						true, false);
		}

		fragmentEntryLink.setEditableValues(editableValues);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.
				fetchFragmentEntryByExternalReferenceCode(
					fragmentEntryLink.getFragmentEntryERC(),
					fragmentEntryLink.getFragmentEntryGroupId());

		if (fragmentEntry != null) {
			if (fragmentEntry.getGroupId() != fragmentEntryLink.getGroupId()) {
				Group group = _groupLocalService.fetchGroup(
					fragmentEntry.getGroupId());

				if (group != null) {
					Group companyGroup = _groupLocalService.getCompanyGroup(
						group.getCompanyId());

					if (group.getGroupId() == companyGroup.getGroupId()) {
						fragmentEntryLinkElement.addAttribute(
							"fragment-entry-group-global",
							Boolean.TRUE.toString());
					}

					fragmentEntryLinkElement.addAttribute(
						"fragment-entry-group-key", group.getGroupKey());
				}

				fragmentEntryLinkElement.addAttribute(
					"fragment-entry-key", fragmentEntry.getFragmentEntryKey());
			}
			else {
				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, fragmentEntryLink, fragmentEntry,
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
			}
		}

		portletDataContext.addClassedModel(
			fragmentEntryLinkElement,
			ExportImportPathUtil.getModelPath(fragmentEntryLink),
			fragmentEntryLink);
	}

	@Override
	protected void doImportMissingReference(
		PortletDataContext portletDataContext, String uuid, long groupId,
		long fragmentEntryLinkId) {

		FragmentEntryLink existingFragmentEntryLink = fetchMissingReference(
			uuid, groupId);

		if (existingFragmentEntryLink == null) {
			return;
		}

		Map<Long, Long> fragmentEntryLinkIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				FragmentEntryLink.class);

		fragmentEntryLinkIds.put(
			fragmentEntryLinkId,
			existingFragmentEntryLink.getFragmentEntryLinkId());
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext,
			FragmentEntryLink fragmentEntryLink)
		throws Exception {

		String fragmentEntryERC = fragmentEntryLink.getFragmentEntryERC();
		String fragmentEntryScopeERC =
			fragmentEntryLink.getFragmentEntryScopeERC();

		if (Validator.isNotNull(fragmentEntryERC)) {
			FragmentEntry fragmentEntry =
				_fragmentEntryLocalService.
					fetchFragmentEntryByExternalReferenceCode(
						fragmentEntryLink.getFragmentEntryERC(),
						fragmentEntryLink.getFragmentEntryGroupId());

			if (fragmentEntry != null) {
				FragmentEntry targetFragmentEntry =
					_fragmentEntryLocalService.
						fetchFragmentEntryByUuidAndGroupId(
							fragmentEntry.getUuid(),
							portletDataContext.getGroupId());

				if (Validator.isNull(fragmentEntryScopeERC) &&
					(targetFragmentEntry != null)) {

					fragmentEntryERC =
						targetFragmentEntry.getExternalReferenceCode();
				}
				else {
					fragmentEntryERC = fragmentEntryLink.getFragmentEntryERC();
				}
			}
			else {
				Element fragmentEntryLinkElement =
					portletDataContext.getImportDataStagedModelElement(
						fragmentEntryLink);

				Group group = _fetchGroup(
					portletDataContext.getCompanyId(),
					fragmentEntryLinkElement);

				if (group != null) {
					String fragmentEntryKey = GetterUtil.getString(
						fragmentEntryLinkElement.attributeValue(
							"fragment-entry-key"));

					fragmentEntry =
						_fragmentEntryLocalService.fetchFragmentEntry(
							group.getGroupId(), fragmentEntryKey);
				}

				if (fragmentEntry != null) {
					fragmentEntryERC = fragmentEntry.getExternalReferenceCode();
				}
			}
		}

		Map<Long, Long> referenceClassPKs =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				fragmentEntryLink.getClassName());

		long referenceClassPK = MapUtil.getLong(
			referenceClassPKs, fragmentEntryLink.getClassPK(),
			fragmentEntryLink.getClassPK());

		FragmentEntryLink importedFragmentEntryLink =
			(FragmentEntryLink)fragmentEntryLink.clone();

		importedFragmentEntryLink.setGroupId(
			portletDataContext.getScopeGroupId());
		importedFragmentEntryLink.setOriginalFragmentEntryLinkERC(
			fragmentEntryLink.getOriginalFragmentEntryLinkERC());
		importedFragmentEntryLink.setFragmentEntryERC(fragmentEntryERC);
		importedFragmentEntryLink.setFragmentEntryScopeERC(
			fragmentEntryScopeERC);

		Map<Long, Long> segmentsExperienceIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				SegmentsExperience.class);

		importedFragmentEntryLink.setSegmentsExperienceId(
			MapUtil.getLong(
				segmentsExperienceIds,
				fragmentEntryLink.getSegmentsExperienceId(),
				fragmentEntryLink.getSegmentsExperienceId()));

		importedFragmentEntryLink.setClassPK(referenceClassPK);
		importedFragmentEntryLink.setPlid(referenceClassPK);
		importedFragmentEntryLink.setHtml(
			_dlReferencesExportImportContentProcessor.
				replaceImportContentReferences(
					portletDataContext, fragmentEntryLink,
					fragmentEntryLink.getHtml()));
		importedFragmentEntryLink.setEditableValues(
			_fragmentEntryLinkExportImportContentProcessor.
				replaceImportContentReferences(
					portletDataContext, fragmentEntryLink,
					fragmentEntryLink.getEditableValues()));

		FragmentEntryLink existingFragmentEntryLink =
			_stagedModelRepository.fetchStagedModelByUuidAndGroupId(
				fragmentEntryLink.getUuid(),
				portletDataContext.getScopeGroupId());

		if ((existingFragmentEntryLink == null) ||
			!portletDataContext.isDataStrategyMirror()) {

			importedFragmentEntryLink = _stagedModelRepository.addStagedModel(
				portletDataContext, importedFragmentEntryLink);
		}
		else {
			importedFragmentEntryLink.setMvccVersion(
				existingFragmentEntryLink.getMvccVersion());
			importedFragmentEntryLink.setFragmentEntryLinkId(
				existingFragmentEntryLink.getFragmentEntryLinkId());

			importedFragmentEntryLink =
				_stagedModelRepository.updateStagedModel(
					portletDataContext, importedFragmentEntryLink);
		}

		portletDataContext.importClassedModel(
			fragmentEntryLink, importedFragmentEntryLink);
	}

	@Override
	protected StagedModelRepository<FragmentEntryLink>
		getStagedModelRepository() {

		return _stagedModelRepository;
	}

	private Group _fetchGroup(
		long companyId, Element fragmentEntryLinkElement) {

		boolean fragmentEntryGroupGlobal = GetterUtil.getBoolean(
			fragmentEntryLinkElement.attributeValue(
				"fragment-entry-group-global"));

		if (fragmentEntryGroupGlobal) {
			return _groupLocalService.fetchCompanyGroup(companyId);
		}

		String fragmentEntryGroupKey = GetterUtil.getString(
			fragmentEntryLinkElement.attributeValue(
				"fragment-entry-group-key"));

		return _groupLocalService.fetchGroup(companyId, fragmentEntryGroupKey);
	}

	@Reference(target = "(content.processor.type=DLReferences)")
	private ExportImportContentProcessor<String>
		_dlReferencesExportImportContentProcessor;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.fragment.model.FragmentEntryLink)"
	)
	private volatile ExportImportContentProcessor<String>
		_fragmentEntryLinkExportImportContentProcessor;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.fragment.model.FragmentEntryLink)",
		unbind = "-"
	)
	private StagedModelRepository<FragmentEntryLink> _stagedModelRepository;

}