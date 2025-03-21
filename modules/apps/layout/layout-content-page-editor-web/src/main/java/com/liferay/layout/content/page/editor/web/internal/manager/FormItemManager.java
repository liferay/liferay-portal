/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.manager;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.helper.DefaultInputFragmentEntryConfigurationProvider;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.layout.content.page.editor.web.internal.exception.FormContainerParentItemRequiredException;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FormStepContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItemUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.segments.constants.SegmentsExperienceConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = FormItemManager.class)
public class FormItemManager {

	public LayoutStructureItemChanges addFormStepLayoutStructureItems(
			List<FragmentEntryLink> addedFragmentEntryLinks,
			FormStyledLayoutStructureItem formStyledLayoutStructureItem,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Layout layout,
			LayoutStructure layoutStructure, int numberOfSteps,
			long segmentsExperienceId, ServiceContext serviceContext)
		throws PortalException {

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			findFormStepContainerStyledLayoutStructureItem(
				formStyledLayoutStructureItem, layoutStructure);

		LayoutStructureItemChanges layoutStructureItemChanges =
			new LayoutStructureItemChanges();

		if (formStepContainerStyledLayoutStructureItem == null) {
			return layoutStructureItemChanges;
		}

		List<String> childrenItemIds =
			formStepContainerStyledLayoutStructureItem.getChildrenItemIds();

		LayoutStructureItem lastFormStepLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				childrenItemIds.get(childrenItemIds.size() - 1));

		_addFormStepLayoutStructureItems(
			addedFragmentEntryLinks, formStepContainerStyledLayoutStructureItem,
			httpServletRequest, httpServletResponse,
			layoutStructure.getLayoutStructureItem(
				childrenItemIds.get(childrenItemIds.size() - 1)),
			layout, layoutStructure, layoutStructureItemChanges,
			numberOfSteps - childrenItemIds.size(), segmentsExperienceId,
			serviceContext);

		childrenItemIds =
			formStepContainerStyledLayoutStructureItem.getChildrenItemIds();

		String parentItemId = childrenItemIds.get(childrenItemIds.size() - 1);

		for (String childrenItemId :
				new ArrayList<>(
					lastFormStepLayoutStructureItem.getChildrenItemIds())) {

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(childrenItemId);

			if (!(layoutStructureItem instanceof
					FragmentStyledLayoutStructureItem)) {

				continue;
			}

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			if (Objects.equals(
					_getFragmentEntryLinkFormButtonType(
						fragmentStyledLayoutStructureItem.
							getFragmentEntryLinkId()),
					"submit")) {

				LayoutStructureItem parentLayoutStructureItem =
					layoutStructure.getLayoutStructureItem(
						layoutStructureItem.getParentItemId());

				List<String> parentChildrenItemIds =
					parentLayoutStructureItem.getChildrenItemIds();

				layoutStructureItemChanges.addMovedLayoutStructureItems(
					layoutStructureItem.clone(),
					parentChildrenItemIds.indexOf(
						layoutStructureItem.getItemId()));

				layoutStructure.moveLayoutStructureItem(
					layoutStructureItem.getItemId(), parentItemId, -1);
			}
		}

		return layoutStructureItemChanges;
	}

	public LayoutStructureItemChanges changeToMultistepFormType(
			List<FragmentEntryLink> addedFragmentEntryLinks,
			FormStyledLayoutStructureItem formStyledLayoutStructureItem,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Layout layout,
			LayoutStructure layoutStructure, int numberOfSteps,
			long segmentsExperienceId, ServiceContext serviceContext,
			long stepperFragmentEntryLinkId)
		throws PortalException {

		LayoutStructureItemChanges layoutStructureItemChanges =
			new LayoutStructureItemChanges();

		List<String> originalChildrenItemIds = new ArrayList<>(
			formStyledLayoutStructureItem.getChildrenItemIds());

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			layoutStructure.addFormStepContainerStyledLayoutStructureItem(
				formStyledLayoutStructureItem.getItemId(), -1);

		layoutStructureItemChanges.addAddedLayoutStructureItems(
			formStepContainerStyledLayoutStructureItem);

		LayoutStructureItem firstFormStepLayoutStructureItem =
			layoutStructure.addFormStepLayoutStructureItem(
				formStepContainerStyledLayoutStructureItem.getItemId(), 0);

		layoutStructureItemChanges.addAddedLayoutStructureItems(
			firstFormStepLayoutStructureItem);

		_addFormStepLayoutStructureItems(
			addedFragmentEntryLinks, formStepContainerStyledLayoutStructureItem,
			httpServletRequest, httpServletResponse,
			firstFormStepLayoutStructureItem, layout, layoutStructure,
			layoutStructureItemChanges, numberOfSteps - 1, segmentsExperienceId,
			serviceContext);

		List<String> childrenItemIds =
			formStepContainerStyledLayoutStructureItem.getChildrenItemIds();

		LayoutStructureItem lastFormStepLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				childrenItemIds.get(childrenItemIds.size() - 1));

		for (String childrenItemId : originalChildrenItemIds) {
			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(childrenItemId);

			String parentItemId = firstFormStepLayoutStructureItem.getItemId();

			if (layoutStructureItem instanceof
					FragmentStyledLayoutStructureItem) {

				FragmentStyledLayoutStructureItem
					fragmentStyledLayoutStructureItem =
						(FragmentStyledLayoutStructureItem)layoutStructureItem;

				long fragmentEntryLinkId =
					fragmentStyledLayoutStructureItem.getFragmentEntryLinkId();

				if (fragmentEntryLinkId == stepperFragmentEntryLinkId) {
					continue;
				}

				if (Objects.equals(
						_getFragmentEntryLinkFormButtonType(
							fragmentStyledLayoutStructureItem.
								getFragmentEntryLinkId()),
						"submit")) {

					parentItemId = lastFormStepLayoutStructureItem.getItemId();
				}
			}

			LayoutStructureItem parentLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(
					layoutStructureItem.getParentItemId());

			List<String> parentChildrenItemIds =
				parentLayoutStructureItem.getChildrenItemIds();

			layoutStructureItemChanges.addMovedLayoutStructureItems(
				layoutStructureItem.clone(),
				parentChildrenItemIds.indexOf(layoutStructureItem.getItemId()));

			layoutStructure.moveLayoutStructureItem(
				childrenItemId, parentItemId, -1);
		}

		return layoutStructureItemChanges;
	}

	public LayoutStructureItemChanges changeToSimpleFormType(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem,
		LayoutStructure layoutStructure) {

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			findFormStepContainerStyledLayoutStructureItem(
				formStyledLayoutStructureItem, layoutStructure);

		if (formStepContainerStyledLayoutStructureItem == null) {
			return new LayoutStructureItemChanges();
		}

		LayoutStructureItemChanges layoutStructureItemChanges =
			new LayoutStructureItemChanges();

		List<String> initialFormChildrenItemIds = new ArrayList<>(
			formStyledLayoutStructureItem.getChildrenItemIds());

		for (String childrenItemId :
				new ArrayList<>(
					formStepContainerStyledLayoutStructureItem.
						getChildrenItemIds())) {

			LayoutStructureItem formStepLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(childrenItemId);

			for (String formStepLayoutStructureItemChildrenItemId :
					new ArrayList<>(
						formStepLayoutStructureItem.getChildrenItemIds())) {

				LayoutStructureItem layoutStructureItem =
					layoutStructure.getLayoutStructureItem(
						formStepLayoutStructureItemChildrenItemId);

				if (layoutStructureItem instanceof
						FragmentStyledLayoutStructureItem) {

					FragmentStyledLayoutStructureItem
						fragmentStyledLayoutStructureItem =
							(FragmentStyledLayoutStructureItem)
								layoutStructureItem;

					String type = _getFragmentEntryLinkFormButtonType(
						fragmentStyledLayoutStructureItem.
							getFragmentEntryLinkId());

					if (Objects.equals(type, "next") ||
						Objects.equals(type, "previous")) {

						continue;
					}
				}

				LayoutStructureItem parentLayoutStructureItem =
					layoutStructure.getLayoutStructureItem(
						layoutStructureItem.getParentItemId());

				List<String> parentChildrenItemIds =
					parentLayoutStructureItem.getChildrenItemIds();

				layoutStructureItemChanges.addMovedLayoutStructureItems(
					layoutStructureItem.clone(),
					parentChildrenItemIds.indexOf(
						layoutStructureItem.getItemId()));

				layoutStructure.moveLayoutStructureItem(
					formStepLayoutStructureItemChildrenItemId,
					formStyledLayoutStructureItem.getItemId(), -1);
			}
		}

		layoutStructure.markLayoutStructureItemForDeletion(
			initialFormChildrenItemIds, Collections.emptyList());

		for (String childrenItemId : initialFormChildrenItemIds) {
			layoutStructureItemChanges.addRemovedLayoutStructureItems(
				layoutStructure.getLayoutStructureItem(childrenItemId));
		}

		for (String childrenItemId :
				LayoutStructureItemUtil.getChildrenItemIds(
					formStyledLayoutStructureItem.getItemId(),
					layoutStructure)) {

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(childrenItemId);

			if (!(layoutStructureItem instanceof
					FragmentStyledLayoutStructureItem)) {

				continue;
			}

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			String type = _getFragmentEntryLinkFormButtonType(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

			if (Objects.equals(type, "next") ||
				Objects.equals(type, "previous")) {

				layoutStructure.markLayoutStructureItemForDeletion(
					Collections.singletonList(childrenItemId),
					Collections.emptyList());

				layoutStructureItemChanges.addRemovedLayoutStructureItems(
					layoutStructureItem);
			}
		}

		return layoutStructureItemChanges;
	}

	public void checkFormContainerParentItemRequired(
			List<FragmentEntryLink> fragmentEntryLinks,
			LayoutStructure layoutStructure, String parentItemId)
		throws PortalException {

		if (_hasParentFormStyledLayoutStructureItem(
				parentItemId, layoutStructure)) {

			return;
		}

		if (_hasTypeInputFragmentEntryLink(fragmentEntryLinks)) {
			throw new FormContainerParentItemRequiredException();
		}
	}

	public void checkFormContainerParentItemRequired(
			String[] itemIds, LayoutStructure layoutStructure,
			String parentItemId)
		throws PortalException {

		for (String itemId : itemIds) {
			if (_hasChildFormStyledLayoutStructureItem(
					itemId, layoutStructure)) {

				continue;
			}

			checkFormContainerParentItemRequired(
				_fragmentEntryLinkManager.getChildrenFragmentEntryLinks(
					Collections.singletonList(itemId), layoutStructure),
				layoutStructure, parentItemId);
		}
	}

	public LayoutStructureItem findFormStepContainerStyledLayoutStructureItem(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem,
		LayoutStructure layoutStructure) {

		for (String childrenItemId :
				formStyledLayoutStructureItem.getChildrenItemIds()) {

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(childrenItemId);

			if (layoutStructureItem instanceof
					FormStepContainerStyledLayoutStructureItem) {

				return layoutStructureItem;
			}
		}

		return null;
	}

	public JSONObject getLayoutStructureItemChangesJSONObject(
			List<FragmentEntryLink> fragmentEntryLinks,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, JSONObject jsonObject,
			LayoutStructure layoutStructure,
			FormItemManager.LayoutStructureItemChanges
				layoutStructureItemChanges)
		throws PortalException {

		JSONObject fragmentEntryLinksJSONObject =
			_jsonFactory.createJSONObject();

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			fragmentEntryLinksJSONObject.put(
				String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
				_fragmentEntryLinkManager.getFragmentEntryLinkJSONObject(
					fragmentEntryLink, httpServletRequest, httpServletResponse,
					layoutStructure));
		}

		return jsonObject.put(
			"addedItemIds",
			layoutStructureItemChanges.getAddedLayoutStructureItemsJSONArray()
		).put(
			"fragmentEntryLinks", fragmentEntryLinksJSONObject
		).put(
			"layoutData", layoutStructure.toJSONObject()
		).put(
			"movedItemIds",
			layoutStructureItemChanges.getMovedLayoutStructureItemsJSONArray()
		).put(
			"removedItemIds",
			layoutStructureItemChanges.getRemovedLayoutStructureItemsJSONArray()
		);
	}

	public LayoutStructureItemChanges removeFormStepLayoutStructureItem(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem,
		String itemId, LayoutStructure layoutStructure) {

		LayoutStructureItemChanges layoutStructureItemChanges =
			new LayoutStructureItemChanges();

		int numberOfSteps = formStyledLayoutStructureItem.getNumberOfSteps();

		if (numberOfSteps > 2) {
			formStyledLayoutStructureItem.setNumberOfSteps(numberOfSteps - 1);

			layoutStructure.markLayoutStructureItemForDeletion(
				Collections.singletonList(itemId), Collections.emptyList());

			layoutStructureItemChanges.addRemovedLayoutStructureItems(
				layoutStructure.getLayoutStructureItem(itemId));

			return layoutStructureItemChanges;
		}

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			findFormStepContainerStyledLayoutStructureItem(
				formStyledLayoutStructureItem, layoutStructure);

		if (formStepContainerStyledLayoutStructureItem == null) {
			return new LayoutStructureItemChanges();
		}

		formStyledLayoutStructureItem.setFormType("simple");
		formStyledLayoutStructureItem.setNumberOfSteps(0);

		List<String> initialFormChildrenItemIds = new ArrayList<>(
			formStyledLayoutStructureItem.getChildrenItemIds());

		LayoutStructureItem formStepLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				formStepContainerStyledLayoutStructureItem.getChildrenItemId(
					0));

		for (String formStepLayoutStructureItemChildrenItemId :
				new ArrayList<>(
					formStepLayoutStructureItem.getChildrenItemIds())) {

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(
					formStepLayoutStructureItemChildrenItemId);

			LayoutStructureItem parentLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(
					layoutStructureItem.getParentItemId());

			List<String> parentChildrenItemIds =
				parentLayoutStructureItem.getChildrenItemIds();

			layoutStructureItemChanges.addMovedLayoutStructureItems(
				layoutStructureItem.clone(),
				parentChildrenItemIds.indexOf(layoutStructureItem.getItemId()));

			layoutStructure.moveLayoutStructureItem(
				formStepLayoutStructureItemChildrenItemId,
				formStyledLayoutStructureItem.getItemId(), -1);
		}

		layoutStructure.markLayoutStructureItemForDeletion(
			initialFormChildrenItemIds, Collections.emptyList());

		for (String childrenItemId : initialFormChildrenItemIds) {
			layoutStructureItemChanges.addRemovedLayoutStructureItems(
				layoutStructure.getLayoutStructureItem(childrenItemId));
		}

		return layoutStructureItemChanges;
	}

	public LayoutStructureItemChanges removeFormStepLayoutStructureItems(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem,
		LayoutStructure layoutStructure, int numberOfSteps) {

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			findFormStepContainerStyledLayoutStructureItem(
				formStyledLayoutStructureItem, layoutStructure);

		LayoutStructureItemChanges layoutStructureItemChanges =
			new LayoutStructureItemChanges();

		if (formStepContainerStyledLayoutStructureItem == null) {
			return layoutStructureItemChanges;
		}

		List<String> childrenItemIds = new ArrayList<>(
			formStepContainerStyledLayoutStructureItem.getChildrenItemIds());

		String previousFormStepLayoutStructureItemId = childrenItemIds.get(
			numberOfSteps - 1);

		for (int i = numberOfSteps; i < childrenItemIds.size(); i++) {
			LayoutStructureItem formStepLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(childrenItemIds.get(i));

			for (String childrenItemId :
					new ArrayList<>(
						formStepLayoutStructureItem.getChildrenItemIds())) {

				LayoutStructureItem layoutStructureItem =
					layoutStructure.getLayoutStructureItem(childrenItemId);

				if (layoutStructureItem instanceof
						FragmentStyledLayoutStructureItem) {

					FragmentStyledLayoutStructureItem
						fragmentStyledLayoutStructureItem =
							(FragmentStyledLayoutStructureItem)
								layoutStructureItem;

					String type = _getFragmentEntryLinkFormButtonType(
						fragmentStyledLayoutStructureItem.
							getFragmentEntryLinkId());

					if (Objects.equals(type, "next")) {
						continue;
					}
				}

				LayoutStructureItem parentLayoutStructureItem =
					layoutStructure.getLayoutStructureItem(
						layoutStructureItem.getParentItemId());

				List<String> parentChildrenItemIds =
					parentLayoutStructureItem.getChildrenItemIds();

				layoutStructureItemChanges.addMovedLayoutStructureItems(
					layoutStructureItem.clone(),
					parentChildrenItemIds.indexOf(
						layoutStructureItem.getItemId()));

				layoutStructure.moveLayoutStructureItem(
					childrenItemId, previousFormStepLayoutStructureItemId, -1);
			}

			layoutStructureItemChanges.addRemovedLayoutStructureItems(
				formStepLayoutStructureItem);

			layoutStructure.markLayoutStructureItemForDeletion(
				Collections.singletonList(
					formStepLayoutStructureItem.getItemId()),
				Collections.emptyList());
		}

		for (String childrenItemId :
				LayoutStructureItemUtil.getChildrenItemIds(
					previousFormStepLayoutStructureItemId, layoutStructure)) {

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(childrenItemId);

			if (!(layoutStructureItem instanceof
					FragmentStyledLayoutStructureItem)) {

				continue;
			}

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			String type = _getFragmentEntryLinkFormButtonType(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

			if (Objects.equals(type, "next")) {
				layoutStructure.markLayoutStructureItemForDeletion(
					Collections.singletonList(childrenItemId),
					Collections.emptyList());

				layoutStructureItemChanges.addRemovedLayoutStructureItems(
					layoutStructureItem);
			}
		}

		return layoutStructureItemChanges;
	}

	public List<LayoutStructureItem> removeLayoutStructureItems(
		FormStyledLayoutStructureItem formStyledLayoutStructureItem,
		LayoutStructure layoutStructure, List<String> initialRemovedItemIds) {

		List<LayoutStructureItem> layoutStructureItems = new ArrayList<>();

		for (String itemId :
				LayoutStructureItemUtil.getChildrenItemIds(
					formStyledLayoutStructureItem.getItemId(),
					layoutStructure)) {

			if (ListUtil.isNotEmpty(initialRemovedItemIds) &&
				!initialRemovedItemIds.contains(itemId)) {

				continue;
			}

			layoutStructure.markLayoutStructureItemForDeletion(
				Collections.singletonList(itemId), Collections.emptyList());

			layoutStructureItems.add(
				layoutStructure.getLayoutStructureItem(itemId));
		}

		return layoutStructureItems;
	}

	public FragmentEntryLink updateNumberOfStepps(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, int numberOfSteps,
			FragmentEntryLink stepperFragmentEntryLink)
		throws Exception {

		JSONObject editableValuesJSONObject =
			_fragmentEntryLinkManager.mergeEditableValuesJSONObject(
				_jsonFactory.createJSONObject(
					stepperFragmentEntryLink.getEditableValues()),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put("numberOfSteps", numberOfSteps)));

		stepperFragmentEntryLink =
			_fragmentEntryLinkService.updateFragmentEntryLink(
				stepperFragmentEntryLink.getFragmentEntryLinkId(),
				editableValuesJSONObject.toString());

		FragmentEntryProcessorContext fragmentEntryProcessorContext =
			new DefaultFragmentEntryProcessorContext(
				httpServletRequest, httpServletResponse,
				FragmentEntryLinkConstants.EDIT,
				LocaleUtil.getMostRelevantLocale());

		String processedHTML =
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				stepperFragmentEntryLink, fragmentEntryProcessorContext);

		JSONObject newEditableValuesJSONObject =
			_fragmentEntryLinkManager.mergeEditableValuesJSONObject(
				_fragmentEntryProcessorRegistry.
					getDefaultEditableValuesJSONObject(
						processedHTML,
						stepperFragmentEntryLink.getConfiguration()),
				editableValuesJSONObject);

		stepperFragmentEntryLink =
			_fragmentEntryLinkService.updateFragmentEntryLink(
				stepperFragmentEntryLink.getFragmentEntryLinkId(),
				newEditableValuesJSONObject.toString());

		for (FragmentEntryLinkListener fragmentEntryLinkListener :
				_fragmentEntryLinkListenerRegistry.
					getFragmentEntryLinkListeners()) {

			fragmentEntryLinkListener.
				onUpdateFragmentEntryLinkConfigurationValues(
					stepperFragmentEntryLink);
		}

		return stepperFragmentEntryLink;
	}

	public static class LayoutStructureItemChanges {

		public LayoutStructureItemChanges() {
			_addedLayoutStructureItemsJSONArray =
				JSONFactoryUtil.createJSONArray();
			_movedLayoutStructureItemsJSONArray =
				JSONFactoryUtil.createJSONArray();
			_removedLayoutStructureItemsJSONArray =
				JSONFactoryUtil.createJSONArray();
		}

		public void addAddedLayoutStructureItems(
			LayoutStructureItem layoutStructureItem) {

			if (layoutStructureItem != null) {
				_addedLayoutStructureItemsJSONArray.put(
					layoutStructureItem.getItemId());
			}
		}

		public void addAddedLayoutStructureItems(
			List<LayoutStructureItem> layoutStructureItems) {

			for (LayoutStructureItem layoutStructureItem :
					layoutStructureItems) {

				addAddedLayoutStructureItems(layoutStructureItem);
			}
		}

		public void addMovedLayoutStructureItems(
			LayoutStructureItem layoutStructureItem, int position) {

			if (layoutStructureItem != null) {
				_movedLayoutStructureItemsJSONArray.put(
					JSONUtil.put(
						"itemId", layoutStructureItem.getItemId()
					).put(
						"parentId", layoutStructureItem.getParentItemId()
					).put(
						"position", position
					));
			}
		}

		public void addRemovedLayoutStructureItems(
			LayoutStructureItem layoutStructureItem) {

			if (layoutStructureItem != null) {
				_removedLayoutStructureItemsJSONArray.put(
					layoutStructureItem.getItemId());
			}
		}

		public void addRemovedLayoutStructureItems(
			List<LayoutStructureItem> layoutStructureItems) {

			for (LayoutStructureItem layoutStructureItem :
					layoutStructureItems) {

				addRemovedLayoutStructureItems(layoutStructureItem);
			}
		}

		public JSONArray getAddedLayoutStructureItemsJSONArray() {
			return _addedLayoutStructureItemsJSONArray;
		}

		public JSONArray getMovedLayoutStructureItemsJSONArray() {
			return _movedLayoutStructureItemsJSONArray;
		}

		public JSONArray getRemovedLayoutStructureItemsJSONArray() {
			return _removedLayoutStructureItemsJSONArray;
		}

		private final JSONArray _addedLayoutStructureItemsJSONArray;
		private final JSONArray _movedLayoutStructureItemsJSONArray;
		private final JSONArray _removedLayoutStructureItemsJSONArray;

	}

	private LayoutStructureItem _addFormButtonFragmentStyledLayoutStructureItem(
			List<FragmentEntryLink> addedFragmentEntryLinks,
			JSONObject defaultInputFragmentEntryKeysJSONObject,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Layout layout,
			LayoutStructure layoutStructure,
			LayoutStructureItem parentLayoutStructureItem,
			long segmentsExperienceId, ServiceContext serviceContext,
			String type)
		throws PortalException {

		FragmentEntry fragmentEntry = _getFragmentEntry(
			layout.getCompanyId(), defaultInputFragmentEntryKeysJSONObject,
			DefaultInputFragmentEntryConfigurationProvider.
				FORM_INPUT_SUBMIT_BUTTON);

		if ((fragmentEntry == null) ||
			!_isAllowedFragmentEntryKey(
				fragmentEntry.getFragmentEntryKey(),
				_getMasterDropZoneLayoutStructureItem(layout))) {

			return null;
		}

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkService.addFragmentEntryLink(
				null, layout.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(), segmentsExperienceId,
				layout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(), null, StringPool.BLANK, 0,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				serviceContext);

		JSONObject editableValuesJSONObject =
			_fragmentEntryLinkManager.mergeEditableValuesJSONObject(
				_jsonFactory.createJSONObject(
					fragmentEntryLink.getEditableValues()),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put("type", type)));

		fragmentEntryLink = _fragmentEntryLinkService.updateFragmentEntryLink(
			fragmentEntryLink.getFragmentEntryLinkId(),
			editableValuesJSONObject.toString());

		FragmentEntryProcessorContext fragmentEntryProcessorContext =
			new DefaultFragmentEntryProcessorContext(
				httpServletRequest, httpServletResponse,
				FragmentEntryLinkConstants.EDIT,
				LocaleUtil.getMostRelevantLocale());

		String processedHTML =
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, fragmentEntryProcessorContext);

		JSONObject newEditableValuesJSONObject =
			_fragmentEntryLinkManager.mergeEditableValuesJSONObject(
				_fragmentEntryProcessorRegistry.
					getDefaultEditableValuesJSONObject(
						processedHTML, fragmentEntryLink.getConfiguration()),
				editableValuesJSONObject);

		fragmentEntryLink = _fragmentEntryLinkService.updateFragmentEntryLink(
			fragmentEntryLink.getFragmentEntryLinkId(),
			newEditableValuesJSONObject.toString());

		addedFragmentEntryLinks.add(fragmentEntryLink);

		return layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(),
			parentLayoutStructureItem.getItemId(), -1);
	}

	private void _addFormStepLayoutStructureItems(
			List<FragmentEntryLink> addedFragmentEntryLinks,
			LayoutStructureItem formStepContainerStyledLayoutStructureItem,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			LayoutStructureItem lastFormStepLayoutStructureItem, Layout layout,
			LayoutStructure layoutStructure,
			LayoutStructureItemChanges layoutStructureItemChanges,
			int numberOfSteps, long segmentsExperienceId,
			ServiceContext serviceContext)
		throws PortalException {

		JSONObject defaultInputFragmentEntryKeysJSONObject =
			_defaultInputFragmentEntryConfigurationProvider.
				getDefaultInputFragmentEntryKeysJSONObject(layout.getGroupId());

		layoutStructureItemChanges.addAddedLayoutStructureItems(
			_addFormButtonFragmentStyledLayoutStructureItem(
				addedFragmentEntryLinks,
				defaultInputFragmentEntryKeysJSONObject, httpServletRequest,
				httpServletResponse, layout, layoutStructure,
				lastFormStepLayoutStructureItem, segmentsExperienceId,
				serviceContext, "next"));

		for (int i = 0; i < numberOfSteps; i++) {
			LayoutStructureItem layoutStructureItem =
				layoutStructure.addFormStepLayoutStructureItem(
					formStepContainerStyledLayoutStructureItem.getItemId(), -1);

			layoutStructureItemChanges.addAddedLayoutStructureItems(
				layoutStructureItem);

			layoutStructureItemChanges.addAddedLayoutStructureItems(
				_addFormButtonFragmentStyledLayoutStructureItem(
					addedFragmentEntryLinks,
					defaultInputFragmentEntryKeysJSONObject, httpServletRequest,
					httpServletResponse, layout, layoutStructure,
					layoutStructureItem, segmentsExperienceId, serviceContext,
					"previous"));

			if (i < (numberOfSteps - 1)) {
				layoutStructureItemChanges.addAddedLayoutStructureItems(
					_addFormButtonFragmentStyledLayoutStructureItem(
						addedFragmentEntryLinks,
						defaultInputFragmentEntryKeysJSONObject,
						httpServletRequest, httpServletResponse, layout,
						layoutStructure, layoutStructureItem,
						segmentsExperienceId, serviceContext, "next"));
			}
		}
	}

	private FragmentEntry _getFragmentEntry(
		long companyId, JSONObject defaultInputFragmentEntryKeysJSONObject,
		String name) {

		JSONObject jsonObject =
			defaultInputFragmentEntryKeysJSONObject.getJSONObject(name);

		if (jsonObject == null) {
			return null;
		}

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				jsonObject.getString("key"));

		if (fragmentEntry != null) {
			return fragmentEntry;
		}

		Group group = _groupLocalService.fetchGroup(
			companyId, jsonObject.getString("groupKey"));

		if (group == null) {
			return null;
		}

		return _fragmentEntryLocalService.fetchFragmentEntry(
			group.getGroupId(), jsonObject.getString("key"));
	}

	private String _getFragmentEntryLinkFormButtonType(
		long fragmentEntryLinkId) {

		Set<String> fieldTypes =
			_fragmentEntryLinkManager.getFragmentEntryLinkFieldTypes(
				fragmentEntryLinkId);

		if (!fieldTypes.contains("formButton")) {
			return null;
		}

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLinkId);

		return GetterUtil.getString(
			_fragmentEntryConfigurationParser.getFieldValue(
				fragmentEntryLink.getConfiguration(),
				fragmentEntryLink.getEditableValues(),
				LocaleUtil.getMostRelevantLocale(), "type"),
			null);
	}

	private DropZoneLayoutStructureItem _getMasterDropZoneLayoutStructureItem(
		Layout layout) {

		if (layout.getMasterLayoutPlid() <= 0) {
			return null;
		}

		try {
			LayoutStructure masterLayoutStructure =
				LayoutStructureUtil.getLayoutStructure(
					layout.getGroupId(), layout.getMasterLayoutPlid(),
					SegmentsExperienceConstants.KEY_DEFAULT);

			return (DropZoneLayoutStructureItem)
				masterLayoutStructure.getDropZoneLayoutStructureItem();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get master layout structure", exception);
			}
		}

		return null;
	}

	private boolean _hasChildFormStyledLayoutStructureItem(
		String itemId, LayoutStructure layoutStructure) {

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(itemId);

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_FORM)) {

			return true;
		}

		for (String childrenItemId : layoutStructureItem.getChildrenItemIds()) {
			if (_hasChildFormStyledLayoutStructureItem(
					childrenItemId, layoutStructure)) {

				return true;
			}
		}

		return false;
	}

	private boolean _hasParentFormStyledLayoutStructureItem(
		String itemId, LayoutStructure layoutStructure) {

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(itemId);

		if ((layoutStructureItem == null) ||
			Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_ROOT)) {

			return false;
		}

		if (Objects.equals(
				layoutStructureItem.getItemType(),
				LayoutDataItemTypeConstants.TYPE_FORM)) {

			return true;
		}

		return _hasParentFormStyledLayoutStructureItem(
			layoutStructureItem.getParentItemId(), layoutStructure);
	}

	private boolean _hasTypeInputFragmentEntryLink(
		List<FragmentEntryLink> fragmentEntryLinks) {

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			if (!Objects.equals(
					fragmentEntryLink.getType(),
					FragmentConstants.TYPE_INPUT)) {

				continue;
			}

			Set<String> fragmentEntryLinkFieldTypes =
				_fragmentEntryLinkManager.getFragmentEntryLinkFieldTypes(
					fragmentEntryLink.getFragmentEntryLinkId());

			if (!fragmentEntryLinkFieldTypes.contains("localizationSelect")) {
				return true;
			}
		}

		return false;
	}

	private boolean _isAllowedFragmentEntryKey(
		String fragmentEntryKey,
		DropZoneLayoutStructureItem masterDropZoneLayoutStructureItem) {

		if (masterDropZoneLayoutStructureItem == null) {
			return true;
		}

		List<String> fragmentEntryKeys =
			masterDropZoneLayoutStructureItem.getFragmentEntryKeys();

		if (masterDropZoneLayoutStructureItem.isAllowNewFragmentEntries()) {
			if (ListUtil.isEmpty(fragmentEntryKeys) ||
				!fragmentEntryKeys.contains(fragmentEntryKey)) {

				return true;
			}

			return false;
		}

		if (ListUtil.isNotEmpty(fragmentEntryKeys) &&
			fragmentEntryKeys.contains(fragmentEntryKey)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FormItemManager.class);

	@Reference
	private DefaultInputFragmentEntryConfigurationProvider
		_defaultInputFragmentEntryConfigurationProvider;

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLinkManager _fragmentEntryLinkManager;

	@Reference
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

}