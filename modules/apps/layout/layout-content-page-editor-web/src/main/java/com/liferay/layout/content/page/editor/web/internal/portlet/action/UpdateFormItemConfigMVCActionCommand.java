/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FormItemManager;
import com.liferay.layout.manager.FormManager;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureService;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItemUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/update_form_item_config"
	},
	service = MVCActionCommand.class
)
public class UpdateFormItemConfigMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		return _updateFormStyledLayoutStructureItemConfig(
			actionRequest, actionResponse);
	}

	private Map<String, String> _getCurrentInputFields(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem,
		LayoutStructure layoutStructure, ThemeDisplay themeDisplay) {

		Map<String, String> inputFields = new HashMap<>();

		for (String itemId :
				LayoutStructureItemUtil.getChildrenItemIds(
					formStyledLayoutStructureItem.getItemId(),
					layoutStructure)) {

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(itemId);

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

			if (fragmentEntryLink == null) {
				continue;
			}

			String inputFieldId = GetterUtil.getString(
				_fragmentEntryConfigurationParser.getFieldValue(
					fragmentEntryLink.getEditableValues(),
					new FragmentConfigurationField(
						"inputFieldId", "string", "", false, "text"),
					themeDisplay.getLocale()));

			if (Validator.isNotNull(inputFieldId)) {
				inputFields.put(
					inputFieldId,
					fragmentStyledLayoutStructureItem.getItemId());
			}
		}

		return inputFields;
	}

	private JSONObject _updateFormStyledLayoutStructureItemConfig(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");
		String itemConfig = ParamUtil.getString(actionRequest, "itemConfig");
		String formItemId = ParamUtil.getString(actionRequest, "itemId");
		long stepperFragmentEntryLinkId = ParamUtil.getLong(
			actionRequest, "stepperFragmentEntryLinkId");

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					themeDisplay.getScopeGroupId(), themeDisplay.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		FormStyledLayoutStructureItem previousFormStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(formItemId);

		long previousClassNameId =
			previousFormStyledLayoutStructureItem.getClassNameId();
		long previousClassTypeId =
			previousFormStyledLayoutStructureItem.getClassTypeId();
		String previousFormType =
			previousFormStyledLayoutStructureItem.getFormType();
		int previousNumberOfSteps =
			previousFormStyledLayoutStructureItem.getNumberOfSteps();

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)layoutStructure.updateItemConfig(
				_jsonFactory.createJSONObject(itemConfig), formItemId);

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		List<FragmentEntryLink> addedFragmentEntryLinks = new ArrayList<>();

		FormItemManager.LayoutStructureItemChanges layoutStructureItemChanges =
			_updateFormStyledLayoutStructureItemFormType(
				addedFragmentEntryLinks, formStyledLayoutStructureItem,
				_portal.getHttpServletRequest(actionRequest),
				_portal.getHttpServletResponse(actionResponse),
				formStyledLayoutStructureItem.getFormType(),
				themeDisplay.getLayout(), layoutStructure,
				formStyledLayoutStructureItem.getNumberOfSteps(),
				previousFormType, previousNumberOfSteps, segmentsExperienceId,
				ServiceContextFactory.getInstance(actionRequest),
				stepperFragmentEntryLinkId);

		if (!Objects.equals(
				formStyledLayoutStructureItem.getClassNameId(),
				previousClassNameId) ||
			!Objects.equals(
				formStyledLayoutStructureItem.getClassTypeId(),
				previousClassTypeId)) {

			layoutStructureItemChanges.addRemovedLayoutStructureItems(
				_formItemManager.removeLayoutStructureItems(
					formStyledLayoutStructureItem, layoutStructure, null));

			if (formStyledLayoutStructureItem.getClassNameId() > 0) {
				String[] uniqueInfoFieldIds = StringUtil.split(
					ParamUtil.getString(actionRequest, "fields"));

				if (ArrayUtil.isNotEmpty(uniqueInfoFieldIds)) {
					layoutStructureItemChanges.addAddedLayoutStructureItems(
						_formManager.addFragmentEntryLinksLayoutStructureItems(
							addedFragmentEntryLinks, jsonObject,
							formStyledLayoutStructureItem, true,
							themeDisplay.getLayout(), layoutStructure,
							themeDisplay.getLocale(), segmentsExperienceId,
							ServiceContextFactory.getInstance(
								httpServletRequest),
							uniqueInfoFieldIds));
				}
			}
		}
		else {
			Map<String, String[]> parameterMap =
				actionRequest.getParameterMap();

			if (parameterMap.containsKey("fields") &&
				(formStyledLayoutStructureItem.getClassNameId() > 0)) {

				List<String> newUniqueInfoFieldIds = new ArrayList<>();

				Map<String, String> currentInputFields = _getCurrentInputFields(
					formStyledLayoutStructureItem, layoutStructure,
					themeDisplay);

				Set<String> currentUniqueInfoFieldIds =
					currentInputFields.keySet();

				String[] uniqueInfoFieldIds = StringUtil.split(
					ParamUtil.getString(actionRequest, "fields"));

				for (String uniqueInfoFieldId : uniqueInfoFieldIds) {
					if (!currentUniqueInfoFieldIds.contains(
							uniqueInfoFieldId)) {

						newUniqueInfoFieldIds.add(uniqueInfoFieldId);
					}
				}

				if (ListUtil.isNotEmpty(newUniqueInfoFieldIds)) {
					layoutStructureItemChanges.addAddedLayoutStructureItems(
						_formManager.addFragmentEntryLinksLayoutStructureItems(
							addedFragmentEntryLinks, jsonObject,
							formStyledLayoutStructureItem, false,
							themeDisplay.getLayout(), layoutStructure,
							themeDisplay.getLocale(), segmentsExperienceId,
							ServiceContextFactory.getInstance(
								httpServletRequest),
							newUniqueInfoFieldIds.toArray(new String[0])));
				}

				List<String> removedItemIds = new ArrayList<>();

				for (String currentUniqueInfoFieldId :
						currentUniqueInfoFieldIds) {

					if (!ArrayUtil.contains(
							uniqueInfoFieldIds, currentUniqueInfoFieldId)) {

						removedItemIds.add(
							currentInputFields.get(currentUniqueInfoFieldId));
					}
				}

				if (ListUtil.isNotEmpty(removedItemIds)) {
					layoutStructureItemChanges.addRemovedLayoutStructureItems(
						_formItemManager.removeLayoutStructureItems(
							formStyledLayoutStructureItem, layoutStructure,
							removedItemIds));
				}
			}
		}

		_layoutPageTemplateStructureService.
			updateLayoutPageTemplateStructureData(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
				segmentsExperienceId, layoutStructure.toString());

		for (FragmentEntryLink addedFragmentEntryLink :
				addedFragmentEntryLinks) {

			for (FragmentEntryLinkListener fragmentEntryLinkListener :
					_fragmentEntryLinkListenerRegistry.
						getFragmentEntryLinkListeners()) {

				fragmentEntryLinkListener.onAddFragmentEntryLink(
					addedFragmentEntryLink);
			}
		}

		FragmentEntryLink stepperFragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				stepperFragmentEntryLinkId);

		if (stepperFragmentEntryLink != null) {
			stepperFragmentEntryLink = _formItemManager.updateNumberOfStepps(
				httpServletRequest,
				_portal.getHttpServletResponse(actionResponse),
				formStyledLayoutStructureItem.getNumberOfSteps(),
				stepperFragmentEntryLink);

			addedFragmentEntryLinks.add(stepperFragmentEntryLink);
		}

		return _formItemManager.getLayoutStructureItemChangesJSONObject(
			addedFragmentEntryLinks, httpServletRequest,
			_portal.getHttpServletResponse(actionResponse), jsonObject,
			layoutStructure, layoutStructureItemChanges);
	}

	private FormItemManager.LayoutStructureItemChanges
			_updateFormStyledLayoutStructureItemFormType(
				List<FragmentEntryLink> addedFragmentEntryLinks,
				FormStyledLayoutStructureItem formStyledLayoutStructureItem,
				HttpServletRequest httpServletRequest,
				HttpServletResponse httpServletResponse, String formType,
				Layout layout, LayoutStructure layoutStructure,
				int numberOfSteps, String previousFormType,
				int previousNumberOfSteps, long segmentsExperienceId,
				ServiceContext serviceContext, long stepperFragmentEntryLinkId)
		throws Exception {

		if (!Objects.equals(formType, previousFormType)) {
			if (Objects.equals(formType, "multistep")) {
				return _formItemManager.changeToMultistepFormType(
					addedFragmentEntryLinks, formStyledLayoutStructureItem,
					httpServletRequest, httpServletResponse, layout,
					layoutStructure, numberOfSteps, segmentsExperienceId,
					serviceContext, stepperFragmentEntryLinkId);
			}

			return _formItemManager.changeToSimpleFormType(
				formStyledLayoutStructureItem, layoutStructure);
		}

		if (numberOfSteps != previousNumberOfSteps) {
			if (numberOfSteps > previousNumberOfSteps) {
				return _formItemManager.addFormStepLayoutStructureItems(
					addedFragmentEntryLinks, formStyledLayoutStructureItem,
					httpServletRequest, httpServletResponse, layout,
					layoutStructure, numberOfSteps, segmentsExperienceId,
					serviceContext);
			}

			return _formItemManager.removeFormStepLayoutStructureItems(
				formStyledLayoutStructureItem, layoutStructure, numberOfSteps);
		}

		return new FormItemManager.LayoutStructureItemChanges();
	}

	@Reference
	private FormItemManager _formItemManager;

	@Reference
	private FormManager _formManager;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private LayoutPageTemplateStructureService
		_layoutPageTemplateStructureService;

	@Reference
	private Portal _portal;

}