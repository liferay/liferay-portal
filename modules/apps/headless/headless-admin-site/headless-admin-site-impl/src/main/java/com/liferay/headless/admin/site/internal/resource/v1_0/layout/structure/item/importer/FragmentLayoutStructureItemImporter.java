/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.util.FragmentCollectionContributorRegistryUtil;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.util.FragmentRendererRegistryUtil;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.headless.admin.site.dto.v1_0.DefaultFragmentReference;
import com.liferay.headless.admin.site.dto.v1_0.FragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FragmentItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.FragmentReference;
import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ItemScopeUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.context.LayoutStructureItemImporterContext;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.util.FragmentConfigurationFieldValuesUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutStructureUtil;
import com.liferay.headless.admin.site.internal.util.LogUtil;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class FragmentLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement)
		throws Exception {

		FragmentInstancePageElementDefinition
			fragmentInstancePageElementDefinition =
				(FragmentInstancePageElementDefinition)
					pageElement.getPageElementDefinition();

		if (fragmentInstancePageElementDefinition == null) {
			return null;
		}

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryLinkLocalServiceUtil.
				fetchFragmentEntryLinkByExternalReferenceCode(
					fragmentInstancePageElementDefinition.
						getFragmentInstanceExternalReferenceCode(),
					layoutStructureItemImporterContext.getGroupId());

		if (fragmentEntryLink == null) {
			fragmentEntryLink = _addFragmentEntryLink(
				fragmentInstancePageElementDefinition,
				layoutStructureItemImporterContext);
		}
		else {
			fragmentEntryLink = _updateFragmentEntryLink(
				fragmentEntryLink, fragmentInstancePageElementDefinition,
				layoutStructureItemImporterContext);
		}

		if (fragmentEntryLink == null) {
			return null;
		}

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.addFragmentStyledLayoutStructureItem(
					fragmentEntryLink.getFragmentEntryLinkId(),
					pageElement.getExternalReferenceCode(),
					LayoutStructureUtil.getParentExternalReferenceCode(
						pageElement, layoutStructure),
					pageElement.getPosition());

		fragmentStyledLayoutStructureItem.setCssClasses(
			SetUtil.fromArray(
				fragmentInstancePageElementDefinition.getCssClasses()));
		fragmentStyledLayoutStructureItem.setCustomCSS(
			fragmentInstancePageElementDefinition.getCustomCSS());
		fragmentStyledLayoutStructureItem.setIndexed(
			GetterUtil.getBoolean(
				fragmentInstancePageElementDefinition.getIndexed(), true));
		fragmentStyledLayoutStructureItem.setName(
			fragmentInstancePageElementDefinition.getName());

		return fragmentStyledLayoutStructureItem;
	}

	private FragmentEntryLink _addFragmentEntryLink(
			FragmentInstancePageElementDefinition
				fragmentInstancePageElementDefinition,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext)
		throws Exception {

		Layout layout = layoutStructureItemImporterContext.getLayout();

		FragmentEntryReference fragmentEntryReference =
			_getFragmentEntryReference(
				layoutStructureItemImporterContext.getCompanyId(),
				fragmentInstancePageElementDefinition.getFragmentReference(),
				layoutStructureItemImporterContext.getGroupId());

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date createDate = serviceContext.getCreateDate();
		String uuid = serviceContext.getUuid();

		try {
			serviceContext.setCreateDate(
				fragmentInstancePageElementDefinition.getDatePropagated());
			serviceContext.setUuid(
				fragmentInstancePageElementDefinition.getUuid());

			return FragmentEntryLinkLocalServiceUtil.addFragmentEntryLink(
				fragmentInstancePageElementDefinition.
					getFragmentInstanceExternalReferenceCode(),
				layoutStructureItemImporterContext.getUserId(),
				layout.getGroupId(),
				_getOriginalFragmentEntryLinkERC(
					fragmentInstancePageElementDefinition,
					layoutStructureItemImporterContext),
				fragmentEntryReference.getFragmentEntryERC(),
				fragmentEntryReference.getFragmentEntryScopeERC(),
				layoutStructureItemImporterContext.getSegmentsExperienceId(),
				layout.getPlid(),
				GetterUtil.getString(
					fragmentInstancePageElementDefinition.getCss()),
				GetterUtil.getString(
					fragmentInstancePageElementDefinition.getHtml()),
				GetterUtil.getString(
					fragmentInstancePageElementDefinition.getJs()),
				GetterUtil.getString(
					fragmentInstancePageElementDefinition.getConfiguration()),
				_getEditableValues(
					fragmentInstancePageElementDefinition,
					layoutStructureItemImporterContext),
				fragmentInstancePageElementDefinition.getNamespace(), 0,
				fragmentEntryReference.getRendererKey(),
				_getType(fragmentInstancePageElementDefinition),
				serviceContext);
		}
		finally {
			serviceContext.setCreateDate(createDate);
			serviceContext.setUuid(uuid);
		}
	}

	private String _getEditableValues(
			FragmentInstancePageElementDefinition
				fragmentInstancePageElementDefinition,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext)
		throws Exception {

		return JSONUtil.put(
			FragmentEntryProcessorConstants.
				KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
			FragmentConfigurationFieldValuesUtil.
				getFreeMarkerFragmentEntryProcessorJSONObject(
					fragmentInstancePageElementDefinition.getConfiguration(),
					fragmentInstancePageElementDefinition.
						getFragmentConfigurationFieldValues(),
					layoutStructureItemImporterContext)
		).toString();
	}

	private FragmentEntryReference _getFragmentEntryReference(
			long companyId, FragmentReference fragmentReference,
			long scopeGroupId)
		throws Exception {

		if (fragmentReference == null) {
			throw new UnsupportedOperationException();
		}

		if (Objects.equals(
				fragmentReference.getFragmentReferenceType(),
				FragmentReference.FragmentReferenceType.
					FRAGMENT_ITEM_EXTERNAL_REFERENCE)) {

			FragmentItemExternalReference fragmentItemExternalReference =
				(FragmentItemExternalReference)fragmentReference;

			if (Validator.isNull(
					fragmentItemExternalReference.getExternalReferenceCode())) {

				throw new UnsupportedOperationException();
			}

			FragmentEntry fragmentEntry = null;

			Long groupId = ItemScopeUtil.getGroupId(
				companyId, fragmentItemExternalReference.getScope(),
				scopeGroupId);

			if (groupId != null) {
				fragmentEntry =
					FragmentEntryLocalServiceUtil.
						fetchFragmentEntryByExternalReferenceCode(
							GetterUtil.getString(
								fragmentItemExternalReference.
									getExternalReferenceCode()),
							groupId);
			}

			if (fragmentEntry == null) {
				LogUtil.logOptionalReference(
					fragmentItemExternalReference.getClassName(),
					fragmentItemExternalReference.getExternalReferenceCode(),
					fragmentItemExternalReference.getScope(), scopeGroupId);
			}

			return new FragmentEntryReference(
				fragmentItemExternalReference.getExternalReferenceCode(),
				ItemScopeUtil.getItemScopeExternalReferenceCode(
					fragmentItemExternalReference.getScope(), scopeGroupId),
				null);
		}

		DefaultFragmentReference defaultFragmentReference =
			(DefaultFragmentReference)fragmentReference;

		if (Validator.isNull(
				defaultFragmentReference.getDefaultFragmentKey())) {

			throw new UnsupportedOperationException();
		}

		FragmentEntry fragmentEntry =
			FragmentCollectionContributorRegistryUtil.getFragmentEntry(
				defaultFragmentReference.getDefaultFragmentKey());
		FragmentRenderer fragmentRenderer = null;

		if (fragmentEntry == null) {
			fragmentRenderer = FragmentRendererRegistryUtil.getFragmentRenderer(
				defaultFragmentReference.getDefaultFragmentKey());
		}

		if ((fragmentEntry == null) && (fragmentRenderer == null)) {
			LogUtil.logOptionalReference(
				DefaultFragmentReference.class,
				defaultFragmentReference.getDefaultFragmentKey(), scopeGroupId);
		}

		return new FragmentEntryReference(
			null, null, defaultFragmentReference.getDefaultFragmentKey());
	}

	private String _getOriginalFragmentEntryLinkERC(
		FragmentInstancePageElementDefinition
			fragmentInstancePageElementDefinition,
		LayoutStructureItemImporterContext layoutStructureItemImporterContext) {

		if (Validator.isNull(
				fragmentInstancePageElementDefinition.
					getDraftFragmentInstanceExternalReferenceCode())) {

			return null;
		}

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryLinkLocalServiceUtil.
				fetchFragmentEntryLinkByExternalReferenceCode(
					fragmentInstancePageElementDefinition.
						getDraftFragmentInstanceExternalReferenceCode(),
					layoutStructureItemImporterContext.getGroupId());

		if (fragmentEntryLink == null) {
			return null;
		}

		return fragmentEntryLink.getExternalReferenceCode();
	}

	private int _getType(
		FragmentInstancePageElementDefinition
			fragmentInstancePageElementDefinition) {

		int type = FragmentConstants.TYPE_COMPONENT;

		if (Objects.equals(
				FragmentInstancePageElementDefinition.FragmentType.FORM,
				fragmentInstancePageElementDefinition.getFragmentType())) {

			type = FragmentConstants.TYPE_INPUT;
		}

		return type;
	}

	private FragmentEntryLink _updateFragmentEntryLink(
			FragmentEntryLink fragmentEntryLink,
			FragmentInstancePageElementDefinition
				fragmentInstancePageElementDefinition,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext)
		throws Exception {

		Layout layout = layoutStructureItemImporterContext.getLayout();

		if ((fragmentEntryLink.getPlid() != layout.getPlid()) ||
			(fragmentEntryLink.getSegmentsExperienceId() !=
				layoutStructureItemImporterContext.getSegmentsExperienceId())) {

			throw new UnsupportedOperationException();
		}

		FragmentEntryReference fragmentEntryReference =
			_getFragmentEntryReference(
				layoutStructureItemImporterContext.getCompanyId(),
				fragmentInstancePageElementDefinition.getFragmentReference(),
				layoutStructureItemImporterContext.getGroupId());

		fragmentEntryLink.setOriginalFragmentEntryLinkERC(
			_getOriginalFragmentEntryLinkERC(
				fragmentInstancePageElementDefinition,
				layoutStructureItemImporterContext));
		fragmentEntryLink.setFragmentEntryERC(
			fragmentEntryReference.getFragmentEntryERC());
		fragmentEntryLink.setFragmentEntryScopeERC(
			fragmentEntryReference.getFragmentEntryScopeERC());
		fragmentEntryLink.setCss(
			GetterUtil.getString(
				fragmentInstancePageElementDefinition.getCss()));
		fragmentEntryLink.setHtml(
			GetterUtil.getString(
				fragmentInstancePageElementDefinition.getHtml()));
		fragmentEntryLink.setJs(
			GetterUtil.getString(
				fragmentInstancePageElementDefinition.getJs()));
		fragmentEntryLink.setConfiguration(
			GetterUtil.getString(
				fragmentInstancePageElementDefinition.getConfiguration()));
		fragmentEntryLink.setEditableValues(
			_getEditableValues(
				fragmentInstancePageElementDefinition,
				layoutStructureItemImporterContext));
		fragmentEntryLink.setNamespace(
			fragmentInstancePageElementDefinition.getNamespace());
		fragmentEntryLink.setRendererKey(
			fragmentEntryReference.getRendererKey());
		fragmentEntryLink.setType(
			_getType(fragmentInstancePageElementDefinition));
		fragmentEntryLink.setLastPropagationDate(
			fragmentInstancePageElementDefinition.getDatePropagated());

		return FragmentEntryLinkLocalServiceUtil.updateFragmentEntryLink(
			fragmentEntryLink);
	}

	private static class FragmentEntryReference {

		public FragmentEntryReference(
			String fragmentEntryERC, String fragmentEntryScopeERC,
			String rendererKey) {

			_fragmentEntryERC = fragmentEntryERC;
			_fragmentEntryScopeERC = fragmentEntryScopeERC;
			_rendererKey = rendererKey;
		}

		public String getFragmentEntryERC() {
			return _fragmentEntryERC;
		}

		public String getFragmentEntryScopeERC() {
			return _fragmentEntryScopeERC;
		}

		public String getRendererKey() {
			return _rendererKey;
		}

		private final String _fragmentEntryERC;
		private final String _fragmentEntryScopeERC;
		private final String _rendererKey;

	}

}