/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.util.FragmentCollectionContributorRegistryUtil;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.headless.admin.site.dto.v1_0.DefaultFragmentReference;
import com.liferay.headless.admin.site.dto.v1_0.FragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FragmentItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.FragmentReference;
import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.dto.v1_0.Scope;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.context.LayoutStructureItemImporterContext;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.GroupUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutStructureUtil;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
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

		FragmentEntry fragmentEntry = _getFragmentEntry(
			fragmentInstancePageElementDefinition,
			layoutStructureItemImporterContext);

		if (fragmentEntry == null) {
			throw new UnsupportedOperationException();
		}

		Layout layout = layoutStructureItemImporterContext.getLayout();

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
				_getOriginalFragmentEntryLinkId(
					fragmentInstancePageElementDefinition,
					layoutStructureItemImporterContext),
				fragmentEntry.getFragmentEntryId(),
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
				StringPool.BLANK,
				fragmentInstancePageElementDefinition.getNamespace(), 0,
				fragmentEntry.getFragmentEntryKey(),
				_getType(fragmentInstancePageElementDefinition),
				serviceContext);
		}
		finally {
			serviceContext.setCreateDate(createDate);
			serviceContext.setUuid(uuid);
		}
	}

	private FragmentEntry _getFragmentEntry(
			FragmentInstancePageElementDefinition
				fragmentInstancePageElementDefinition,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext)
		throws Exception {

		FragmentReference fragmentReference =
			fragmentInstancePageElementDefinition.getFragmentReference();

		if (fragmentReference == null) {
			throw new UnsupportedOperationException();
		}

		if (Objects.equals(
				fragmentReference.getFragmentReferenceType(),
				FragmentReference.FragmentReferenceType.
					FRAGMENT_ITEM_EXTERNAL_REFERENCE)) {

			FragmentItemExternalReference fragmentItemExternalReference =
				(FragmentItemExternalReference)fragmentReference;

			long scopeGroupId = layoutStructureItemImporterContext.getGroupId();

			Scope scope = fragmentItemExternalReference.getScope();

			if (scope != null) {
				scopeGroupId = GroupUtil.getGroupId(
					true, true,
					layoutStructureItemImporterContext.getCompanyId(),
					GetterUtil.getString(scope.getExternalReferenceCode()));
			}

			return FragmentEntryLocalServiceUtil.
				fetchFragmentEntryByExternalReferenceCode(
					GetterUtil.getString(
						fragmentItemExternalReference.
							getExternalReferenceCode()),
					scopeGroupId);
		}

		DefaultFragmentReference defaultFragmentReference =
			(DefaultFragmentReference)fragmentReference;

		return FragmentCollectionContributorRegistryUtil.getFragmentEntry(
			GetterUtil.getString(
				defaultFragmentReference.getDefaultFragmentKey()));
	}

	private long _getOriginalFragmentEntryLinkId(
		FragmentInstancePageElementDefinition
			fragmentInstancePageElementDefinition,
		LayoutStructureItemImporterContext layoutStructureItemImporterContext) {

		if (Validator.isNull(
				fragmentInstancePageElementDefinition.
					getDraftFragmentInstanceExternalReferenceCode())) {

			return 0;
		}

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryLinkLocalServiceUtil.
				fetchFragmentEntryLinkByExternalReferenceCode(
					fragmentInstancePageElementDefinition.
						getDraftFragmentInstanceExternalReferenceCode(),
					layoutStructureItemImporterContext.getGroupId());

		if (fragmentEntryLink == null) {
			return 0;
		}

		return fragmentEntryLink.getFragmentEntryLinkId();
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

		FragmentEntry fragmentEntry = _getFragmentEntry(
			fragmentInstancePageElementDefinition,
			layoutStructureItemImporterContext);
		Layout layout = layoutStructureItemImporterContext.getLayout();

		if ((fragmentEntry == null) ||
			(fragmentEntryLink.getPlid() != layout.getPlid()) ||
			(fragmentEntryLink.getSegmentsExperienceId() !=
				layoutStructureItemImporterContext.getSegmentsExperienceId())) {

			throw new UnsupportedOperationException();
		}

		fragmentEntryLink.setOriginalFragmentEntryLinkId(
			_getOriginalFragmentEntryLinkId(
				fragmentInstancePageElementDefinition,
				layoutStructureItemImporterContext));

		fragmentEntryLink.setFragmentEntryId(
			fragmentEntry.getFragmentEntryId());
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
		fragmentEntryLink.setNamespace(
			fragmentInstancePageElementDefinition.getNamespace());
		fragmentEntryLink.setRendererKey(fragmentEntry.getFragmentEntryKey());
		fragmentEntryLink.setType(
			_getType(fragmentInstancePageElementDefinition));
		fragmentEntryLink.setLastPropagationDate(
			fragmentInstancePageElementDefinition.getDatePropagated());

		return FragmentEntryLinkLocalServiceUtil.updateFragmentEntryLink(
			fragmentEntryLink);
	}

}