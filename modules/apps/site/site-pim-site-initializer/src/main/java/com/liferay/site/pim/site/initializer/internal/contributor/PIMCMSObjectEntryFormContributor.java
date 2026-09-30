/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.contributor;

import com.liferay.fragment.contributor.util.FragmentCollectionContributorRegistryUtil;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.cms.site.initializer.contributor.CMSObjectEntryFormContributor;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;
import com.liferay.site.pim.site.initializer.internal.fragment.renderer.ProductRelationshipsSectionFragmentRenderer;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = CMSObjectEntryFormContributor.class)
public class PIMCMSObjectEntryFormContributor
	implements CMSObjectEntryFormContributor {

	@Override
	public void contribute(
			Layout layout, ObjectDefinition objectDefinition,
			ServiceContext serviceContext)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				objectDefinition.getCompanyId(), "LPD-96666") ||
			!Objects.equals(
				objectDefinition.getObjectFolderExternalReferenceCode(),
				PIMObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES)) {

			return;
		}

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		String parentItemId = _getParentItemId(
			layout, segmentsExperienceId, serviceContext);

		if (Validator.isNull(parentItemId)) {
			return;
		}

		String accordionItemId = _addFragmentEntryLinkToLayoutStructure(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"accordion-title", _getLocalizedNameJSONObject(layout))),
			"BASIC_COMPONENT-accordion", layout, parentItemId,
			segmentsExperienceId, serviceContext,
			JSONUtil.put(
				"marginBottom", "16px"
			).put(
				"marginTop", "24px"
			));

		if (Validator.isNull(accordionItemId)) {
			return;
		}

		String accordionContentItemId = _getLastChildItemId(
			accordionItemId, layout, segmentsExperienceId);

		if (Validator.isNull(accordionContentItemId)) {
			return;
		}

		_addFragmentEntryLinkToLayoutStructure(
			null, ProductRelationshipsSectionFragmentRenderer.class.getName(),
			layout, accordionContentItemId, segmentsExperienceId,
			serviceContext, null);
	}

	private FragmentEntryLink _addFragmentEntryLink(
			JSONObject editableValuesJSONObject, String fragmentEntryKey,
			Layout layout, long segmentsExperienceId,
			ServiceContext serviceContext)
		throws Exception {

		String editableValues = StringPool.BLANK;

		if (editableValuesJSONObject != null) {
			editableValues = editableValuesJSONObject.toString();
		}

		FragmentRenderer fragmentRenderer =
			_fragmentRendererRegistry.getFragmentRenderer(fragmentEntryKey);

		if (fragmentRenderer != null) {
			return _fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, serviceContext.getUserId(), layout.getGroupId(), null,
				null, null, segmentsExperienceId, layout.getPlid(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				String.valueOf(
					fragmentRenderer.getConfigurationJSONObject(
						new DefaultFragmentRendererContext(null))),
				editableValues, StringPool.BLANK, 0, fragmentEntryKey,
				fragmentRenderer.getType(), serviceContext);
		}

		FragmentEntry fragmentEntry =
			FragmentCollectionContributorRegistryUtil.getFragmentEntry(
				fragmentEntryKey);

		if (fragmentEntry == null) {
			return null;
		}

		String rendererKey = null;

		if (fragmentEntry.getFragmentEntryId() == 0) {
			rendererKey = fragmentEntryKey;
		}

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, serviceContext.getUserId(), layout.getGroupId(), null,
			fragmentEntry.getExternalReferenceCode(), null,
			segmentsExperienceId, layout.getPlid(), fragmentEntry.getCss(),
			fragmentEntry.getHtml(), fragmentEntry.getJs(),
			fragmentEntry.getConfiguration(), editableValues, StringPool.BLANK,
			0, rendererKey, fragmentEntry.getType(), serviceContext);
	}

	private String _addFragmentEntryLinkToLayoutStructure(
			JSONObject editableValuesJSONObject, String fragmentEntryKey,
			Layout layout, String parentItemId, long segmentsExperienceId,
			ServiceContext serviceContext, JSONObject stylesJSONObject)
		throws Exception {

		LayoutStructure layoutStructure = _getLayoutStructure(
			layout, segmentsExperienceId);

		if (layoutStructure == null) {
			return null;
		}

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			editableValuesJSONObject, fragmentEntryKey, layout,
			segmentsExperienceId, serviceContext);

		if (fragmentEntryLink == null) {
			return null;
		}

		LayoutStructureItem layoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(), parentItemId, -1);

		if (stylesJSONObject != null) {
			layoutStructureItem.updateItemConfig(
				JSONUtil.put("styles", stylesJSONObject));
		}

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				serviceContext.getUserId(), layout.getGroupId(),
				layout.getPlid(), segmentsExperienceId,
				layoutStructure.toString());

		_notifyFragmentEntryLinkListeners(fragmentEntryLink);

		return layoutStructureItem.getItemId();
	}

	private String _getItemId(LayoutStructure layoutStructure) {
		for (LayoutStructureItem layoutStructureItem :
				layoutStructure.getLayoutStructureItems()) {

			if (layoutStructureItem instanceof FormStyledLayoutStructureItem) {
				return layoutStructureItem.getItemId();
			}
		}

		return layoutStructure.getMainItemId();
	}

	private String _getLastChildItemId(
		String itemId, Layout layout, long segmentsExperienceId) {

		LayoutStructure layoutStructure = _getLayoutStructure(
			layout, segmentsExperienceId);

		if (layoutStructure == null) {
			return null;
		}

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(itemId);

		if (layoutStructureItem == null) {
			return null;
		}

		List<String> childrenItemIds = layoutStructureItem.getChildrenItemIds();

		if (childrenItemIds.isEmpty()) {
			return null;
		}

		return childrenItemIds.get(childrenItemIds.size() - 1);
	}

	private LayoutStructure _getLayoutStructure(
		Layout layout, long segmentsExperienceId) {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		if (layoutPageTemplateStructure == null) {
			return null;
		}

		return LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));
	}

	private JSONObject _getLocalizedNameJSONObject(Layout layout) {
		JSONObject jsonObject = _jsonFactory.createJSONObject();

		for (Locale locale :
				_language.getAvailableLocales(layout.getGroupId())) {

			jsonObject.put(
				LocaleUtil.toLanguageId(locale),
				_language.get(locale, "relationships"));
		}

		return jsonObject;
	}

	private String _getParentItemId(
			Layout layout, long segmentsExperienceId,
			ServiceContext serviceContext)
		throws Exception {

		LayoutStructure layoutStructure = _getLayoutStructure(
			layout, segmentsExperienceId);

		if (layoutStructure == null) {
			return null;
		}

		FragmentEntryLink fragmentEntryLink = _getTabsFragmentEntryLink(
			layoutStructure);

		if (fragmentEntryLink != null) {
			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
					fragmentEntryLink.getFragmentEntryLinkId());

			if ((layoutStructureItem != null) &&
				_updateTabFragmentEntryLink(
					fragmentEntryLink, layout, serviceContext)) {

				return _getLastChildItemId(
					layoutStructureItem.getItemId(), layout,
					segmentsExperienceId);
			}
		}

		return _getItemId(layoutStructure);
	}

	private FragmentEntryLink _getTabsFragmentEntryLink(
		LayoutStructure layoutStructure) {

		for (LayoutStructureItem layoutStructureItem :
				layoutStructure.getLayoutStructureItems()) {

			if (!(layoutStructureItem instanceof
					FragmentStyledLayoutStructureItem)) {

				continue;
			}

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

			if ((fragmentEntryLink != null) &&
				Objects.equals(
					fragmentEntryLink.getRendererKey(),
					"BASIC_COMPONENT-tabs")) {

				return fragmentEntryLink;
			}
		}

		return null;
	}

	private void _notifyFragmentEntryLinkListeners(
		FragmentEntryLink fragmentEntryLink) {

		for (FragmentEntryLinkListener fragmentEntryLinkListener :
				_fragmentEntryLinkListenerRegistry.
					getFragmentEntryLinkListeners()) {

			fragmentEntryLinkListener.onAddFragmentEntryLink(fragmentEntryLink);
		}
	}

	private boolean _updateTabFragmentEntryLink(
			FragmentEntryLink fragmentEntryLink, Layout layout,
			ServiceContext serviceContext)
		throws Exception {

		JSONObject editableValuesJSONObject = _jsonFactory.createJSONObject(
			fragmentEntryLink.getEditableValues());

		JSONObject editableJSONObject = editableValuesJSONObject.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);
		JSONObject freeMarkerJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		if ((editableJSONObject == null) || (freeMarkerJSONObject == null)) {
			return false;
		}

		int numberOfTabs = GetterUtil.getInteger(
			freeMarkerJSONObject.getString("numberOfTabs"));

		if (numberOfTabs < 1) {
			return false;
		}

		editableJSONObject.put(
			"title" + (numberOfTabs + 1), _getLocalizedNameJSONObject(layout));

		freeMarkerJSONObject.put(
			"numberOfTabs", String.valueOf(numberOfTabs + 1));

		_notifyFragmentEntryLinkListeners(
			_fragmentEntryLinkLocalService.updateFragmentEntryLink(
				serviceContext.getUserId(),
				fragmentEntryLink.getFragmentEntryLinkId(),
				editableValuesJSONObject.toString(), false));

		return true;
	}

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}