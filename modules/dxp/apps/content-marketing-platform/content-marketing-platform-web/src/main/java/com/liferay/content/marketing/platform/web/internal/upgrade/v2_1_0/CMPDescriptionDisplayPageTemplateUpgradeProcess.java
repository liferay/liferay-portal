/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.upgrade.v2_1_0;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.StyledLayoutStructureItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Objects;

/**
 * @author Larissa Ribeiro
 */
public class CMPDescriptionDisplayPageTemplateUpgradeProcess
	extends UpgradeProcess {

	public CMPDescriptionDisplayPageTemplateUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		CompanyLocalService companyLocalService,
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		GroupLocalService groupLocalService,
		LayoutLocalService layoutLocalService,
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		LayoutPageTemplateStructureLocalService
			layoutPageTemplateStructureLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		SegmentsExperienceLocalService segmentsExperienceLocalService) {

		_classNameLocalService = classNameLocalService;
		_companyLocalService = companyLocalService;
		_fragmentEntryLinkLocalService = fragmentEntryLinkLocalService;
		_groupLocalService = groupLocalService;
		_layoutLocalService = layoutLocalService;
		_layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
		_layoutPageTemplateStructureLocalService =
			layoutPageTemplateStructureLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_segmentsExperienceLocalService = segmentsExperienceLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				Group group = _groupLocalService.fetchGroup(
					companyId, GroupConstants.CMS);

				if (group == null) {
					return;
				}

				_upgradeDisplayPageTemplates(companyId, group, "L_CMP_PROJECT");
				_upgradeDisplayPageTemplates(companyId, group, "L_CMP_TASK");
			});
	}

	private void _upgradeDisplayPageTemplates(
			long companyId, Group group,
			String objectDefinitionExternalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode, companyId);

		if (objectDefinition == null) {
			return;
		}

		long classNameId = _classNameLocalService.getClassNameId(
			objectDefinition.getClassName());

		for (LayoutPageTemplateEntry layoutPageTemplateEntry :
				_layoutPageTemplateEntryLocalService.
					getLayoutPageTemplateEntries(group.getGroupId())) {

			if ((layoutPageTemplateEntry.getClassNameId() != classNameId) ||
				(layoutPageTemplateEntry.getType() !=
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE)) {

				continue;
			}

			Layout layout = _layoutLocalService.fetchLayout(
				layoutPageTemplateEntry.getPlid());

			if (layout == null) {
				continue;
			}

			_upgradeFragmentEntryLinks(layout);
			_upgradeLayoutPageTemplateStructure(layout);

			Layout draftLayout = layout.fetchDraftLayout();

			if (draftLayout == null) {
				continue;
			}

			_upgradeFragmentEntryLinks(draftLayout);
			_upgradeLayoutPageTemplateStructure(draftLayout);
		}
	}

	private void _upgradeFragmentEntryLinks(Layout layout) throws Exception {
		for (FragmentEntryLink fragmentEntryLink :
				_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
					layout.getGroupId(), layout.getPlid())) {

			if (!Objects.equals(
					fragmentEntryLink.getRendererKey(), "INPUTS-textarea")) {

				continue;
			}

			JSONObject editableValuesJSONObject =
				JSONFactoryUtil.createJSONObject(
					fragmentEntryLink.getEditableValues());

			JSONObject jsonObject = editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

			if ((jsonObject == null) ||
				!Objects.equals(
					jsonObject.getString("inputFieldId"),
					"ObjectField_description")) {

				continue;
			}

			jsonObject.remove("numberOfLines");
			jsonObject.remove("placeholder");
			jsonObject.remove("showCharactersCount");

			fragmentEntryLink.setEditableValues(
				editableValuesJSONObject.toString());
			fragmentEntryLink.setRendererKey("INPUTS-rich-text-input");

			_fragmentEntryLinkLocalService.updateFragmentEntryLink(
				fragmentEntryLink);
		}
	}

	private void _upgradeLayoutPageTemplateStructure(Layout layout)
		throws Exception {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		if (layoutPageTemplateStructure == null) {
			return;
		}

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		boolean updated = false;

		for (FragmentEntryLink fragmentEntryLink :
				_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
					layout.getGroupId(), layout.getPlid())) {

			if (!Objects.equals(
					fragmentEntryLink.getRendererKey(),
					"BASIC_COMPONENT-paragraph")) {

				continue;
			}

			JSONObject editableValuesJSONObject =
				JSONFactoryUtil.createJSONObject(
					fragmentEntryLink.getEditableValues());

			JSONObject jsonObject = editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

			if (jsonObject == null) {
				continue;
			}

			JSONObject elementTextJSONObject = jsonObject.getJSONObject(
				"element-text");

			if ((elementTextJSONObject == null) ||
				!Objects.equals(
					elementTextJSONObject.getString("mappedField"),
					"ObjectField_description")) {

				continue;
			}

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
					fragmentEntryLink.getFragmentEntryLinkId());

			if (!(layoutStructureItem instanceof
					StyledLayoutStructureItem styledLayoutStructureItem)) {

				continue;
			}

			String customCSS = styledLayoutStructureItem.getCustomCSS();

			if (Validator.isNotNull(customCSS) &&
				customCSS.contains(_CUSTOM_CSS)) {

				continue;
			}

			if (Validator.isNull(customCSS)) {
				styledLayoutStructureItem.setCustomCSS(_CUSTOM_CSS);
			}
			else {
				styledLayoutStructureItem.setCustomCSS(
					StringBundler.concat(
						customCSS, StringPool.NEW_LINE, _CUSTOM_CSS));
			}

			updated = true;
		}

		if (!updated) {
			return;
		}

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				layout.getUserId(), layout.getGroupId(), layout.getPlid(),
				segmentsExperienceId, layoutStructure.toString());
	}

	private static final String _CUSTOM_CSS =
		".[$FRAGMENT_CLASS$] figure.media {\ndisplay: block;\n}";

	private final ClassNameLocalService _classNameLocalService;
	private final CompanyLocalService _companyLocalService;
	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;
	private final GroupLocalService _groupLocalService;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;
	private final LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final SegmentsExperienceLocalService
		_segmentsExperienceLocalService;

}