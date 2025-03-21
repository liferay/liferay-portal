/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.drop.zone.listener;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.util.CheckUnlockedLayoutThreadLocal;
import com.liferay.layout.util.structure.DeletedLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = FragmentEntryLinkListener.class)
public class DropZoneFragmentEntryLinkListener
	implements FragmentEntryLinkListener {

	@Override
	public void onAddFragmentEntryLink(FragmentEntryLink fragmentEntryLink) {
		try {
			updateLayoutPageTemplateStructure(fragmentEntryLink, null);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to update layout page template structure",
					exception);
			}
		}
	}

	@Override
	public void onCopyFragmentEntryLink(
		FragmentEntryLink fragmentEntryLink,
		FragmentEntryLink originalFragmentEntryLink) {

		try {
			updateLayoutPageTemplateStructure(
				fragmentEntryLink, originalFragmentEntryLink);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to update layout page template structure",
					exception);
			}
		}
	}

	@Override
	public void onDeleteFragmentEntryLink(FragmentEntryLink fragmentEntryLink) {
	}

	@Override
	public void onDuplicateFragmentEntryLink(
		FragmentEntryLink fragmentEntryLink,
		FragmentEntryLink originalFragmentEntryLink) {

		try {
			updateLayoutPageTemplateStructure(
				fragmentEntryLink, originalFragmentEntryLink);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to update layout page template structure",
					exception);
			}
		}
	}

	@Override
	public void onUpdateFragmentEntryLink(FragmentEntryLink fragmentEntryLink) {
	}

	@Override
	public void onUpdateFragmentEntryLinkConfigurationValues(
		FragmentEntryLink fragmentEntryLink) {

		try {
			updateLayoutPageTemplateStructure(fragmentEntryLink, null);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to update layout page template structure",
					exception);
			}
		}
	}

	protected void updateLayoutPageTemplateStructure(
			FragmentEntryLink fragmentEntryLink,
			FragmentEntryLink originalFragmentEntryLink)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return;
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();
		HttpServletResponse httpServletResponse = serviceContext.getResponse();
		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if ((httpServletRequest == null) && (themeDisplay != null)) {
			httpServletRequest = themeDisplay.getRequest();
		}

		if ((httpServletResponse == null) && (themeDisplay != null)) {
			httpServletResponse = themeDisplay.getResponse();
		}

		String processedHTML =
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink,
				new DefaultFragmentEntryProcessorContext(
					httpServletRequest, httpServletResponse,
					FragmentEntryLinkConstants.EDIT,
					serviceContext.getLocale()));

		Document document = _getDocument(processedHTML);

		Elements elements = document.getElementsByTag("lfr-drop-zone");

		if (elements.isEmpty()) {
			return;
		}

		LayoutStructure layoutStructure = _getLayoutStructure(
			fragmentEntryLink);

		if (layoutStructure == null) {
			return;
		}

		LayoutStructureItem parentLayoutStructureItem =
			layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
				fragmentEntryLink.getFragmentEntryLinkId());

		if (parentLayoutStructureItem == null) {
			return;
		}

		List<String> elementDropZoneIds = new LinkedList<>();

		for (Element element : elements) {
			String dropZoneId = element.attr("data-lfr-drop-zone-id");

			if (Validator.isBlank(dropZoneId)) {
				break;
			}

			elementDropZoneIds.add(dropZoneId);
		}

		if (elementDropZoneIds.size() < elements.size()) {
			List<String> childrenItemIds =
				parentLayoutStructureItem.getChildrenItemIds();

			if (childrenItemIds.size() == elements.size()) {
				return;
			}

			if (childrenItemIds.size() > elements.size()) {
				layoutStructure.markLayoutStructureItemForDeletion(
					new ArrayList<>(
						childrenItemIds.subList(
							elements.size(), childrenItemIds.size())),
					Collections.emptyList());
			}
			else {
				for (int i = childrenItemIds.size(); i < elements.size(); i++) {
					_addOrRestoreDropZoneLayoutStructureItem(
						layoutStructure, parentLayoutStructureItem);
				}
			}

			try (SafeCloseable safeCloseable =
					CheckUnlockedLayoutThreadLocal.
						setCheckUnlockedLayoutWithSafeCloseable(false)) {

				_layoutPageTemplateStructureLocalService.
					updateLayoutPageTemplateStructureData(
						fragmentEntryLink.getGroupId(),
						fragmentEntryLink.getPlid(),
						fragmentEntryLink.getSegmentsExperienceId(),
						layoutStructure.toString());
			}

			return;
		}

		List<String> childrenItemIds = new LinkedList<>(
			parentLayoutStructureItem.getChildrenItemIds());

		Map<String, FragmentDropZoneLayoutStructureItem>
			fragmentDropZoneLayoutStructureItemsMap = new LinkedHashMap<>();

		Map<String, String> dropZoneIdsReplaceValuesMap = new LinkedHashMap<>();

		List<FragmentDropZoneLayoutStructureItem>
			noExistingIdFragmentDropZoneLayoutStructureItems =
				new LinkedList<>();

		List<FragmentDropZoneLayoutStructureItem>
			noIdFragmentDropZoneLayoutStructureItems = new LinkedList<>();

		List<String> originalElementDropZoneIds = _getElementDropZoneIds(
			originalFragmentEntryLink, httpServletRequest, httpServletResponse,
			serviceContext);

		for (String childrenItemId : childrenItemIds) {
			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(childrenItemId);

			if (!(layoutStructureItem instanceof
					FragmentDropZoneLayoutStructureItem)) {

				continue;
			}

			FragmentDropZoneLayoutStructureItem
				fragmentDropZoneLayoutStructureItem =
					(FragmentDropZoneLayoutStructureItem)layoutStructureItem;

			String fragmentDropZoneId =
				fragmentDropZoneLayoutStructureItem.getFragmentDropZoneId();

			if (Validator.isBlank(fragmentDropZoneId)) {
				noIdFragmentDropZoneLayoutStructureItems.add(
					fragmentDropZoneLayoutStructureItem);
			}
			else if (elementDropZoneIds.contains(fragmentDropZoneId)) {
				fragmentDropZoneLayoutStructureItemsMap.put(
					fragmentDropZoneId, fragmentDropZoneLayoutStructureItem);
			}
			else if (originalElementDropZoneIds.contains(fragmentDropZoneId)) {
				int index = originalElementDropZoneIds.indexOf(
					fragmentDropZoneId);

				String dropZoneId = null;

				if ((index >= 0) && (elementDropZoneIds.size() > index)) {
					dropZoneId = elementDropZoneIds.get(index);
				}

				if (Validator.isNotNull(dropZoneId)) {
					dropZoneIdsReplaceValuesMap.put(
						dropZoneId, fragmentDropZoneId);
				}

				fragmentDropZoneLayoutStructureItemsMap.put(
					fragmentDropZoneId, fragmentDropZoneLayoutStructureItem);
			}
			else {
				noExistingIdFragmentDropZoneLayoutStructureItems.add(
					fragmentDropZoneLayoutStructureItem);
			}
		}

		boolean update = false;

		for (int index = 0; index < elementDropZoneIds.size(); index++) {
			String dropZoneId = elementDropZoneIds.get(index);

			FragmentDropZoneLayoutStructureItem
				fragmentDropZoneLayoutStructureItem =
					fragmentDropZoneLayoutStructureItemsMap.remove(dropZoneId);

			String dropZoneIdsReplaceValue = dropZoneIdsReplaceValuesMap.get(
				dropZoneId);

			if ((fragmentDropZoneLayoutStructureItem == null) &&
				Validator.isNotNull(dropZoneIdsReplaceValue)) {

				fragmentDropZoneLayoutStructureItem =
					fragmentDropZoneLayoutStructureItemsMap.remove(
						dropZoneIdsReplaceValue);
			}

			if (fragmentDropZoneLayoutStructureItem != null) {
				if (!Objects.equals(
						dropZoneId,
						fragmentDropZoneLayoutStructureItem.
							getFragmentDropZoneId())) {

					fragmentDropZoneLayoutStructureItem.setFragmentDropZoneId(
						dropZoneId);

					update = true;
				}

				String itemId = fragmentDropZoneLayoutStructureItem.getItemId();

				if (index != childrenItemIds.indexOf(itemId)) {
					layoutStructure.moveLayoutStructureItem(
						itemId, parentLayoutStructureItem.getItemId(), index);

					update = true;
				}

				continue;
			}

			fragmentDropZoneLayoutStructureItem =
				_getDeletedFragmentDropZoneStructureItem(
					dropZoneId, layoutStructure,
					parentLayoutStructureItem.getItemId());

			if (fragmentDropZoneLayoutStructureItem != null) {
				String itemId = fragmentDropZoneLayoutStructureItem.getItemId();

				layoutStructure.unmarkLayoutStructureItemForDeletion(itemId);

				layoutStructure.moveLayoutStructureItem(
					itemId, parentLayoutStructureItem.getItemId(), index);

				update = true;

				continue;
			}

			if (ListUtil.isNotEmpty(noIdFragmentDropZoneLayoutStructureItems)) {
				fragmentDropZoneLayoutStructureItem =
					noIdFragmentDropZoneLayoutStructureItems.remove(0);

				fragmentDropZoneLayoutStructureItem.setFragmentDropZoneId(
					dropZoneId);

				String itemId = fragmentDropZoneLayoutStructureItem.getItemId();

				if (index != childrenItemIds.indexOf(itemId)) {
					layoutStructure.moveLayoutStructureItem(
						itemId, parentLayoutStructureItem.getItemId(), index);
				}

				update = true;

				continue;
			}

			fragmentDropZoneLayoutStructureItem =
				(FragmentDropZoneLayoutStructureItem)
					layoutStructure.addFragmentDropZoneLayoutStructureItem(
						parentLayoutStructureItem.getItemId(), index);

			fragmentDropZoneLayoutStructureItem.setFragmentDropZoneId(
				dropZoneId);

			update = true;
		}

		for (FragmentDropZoneLayoutStructureItem
				fragmentDropZoneLayoutStructureItem :
					noExistingIdFragmentDropZoneLayoutStructureItems) {

			layoutStructure.markLayoutStructureItemForDeletion(
				Collections.singletonList(
					fragmentDropZoneLayoutStructureItem.getItemId()),
				Collections.emptyList());

			update = true;
		}

		for (FragmentDropZoneLayoutStructureItem
				fragmentDropZoneLayoutStructureItem :
					noIdFragmentDropZoneLayoutStructureItems) {

			layoutStructure.markLayoutStructureItemForDeletion(
				Collections.singletonList(
					fragmentDropZoneLayoutStructureItem.getItemId()),
				Collections.emptyList());

			update = true;
		}

		if (update) {
			try (SafeCloseable safeCloseable =
					CheckUnlockedLayoutThreadLocal.
						setCheckUnlockedLayoutWithSafeCloseable(false)) {

				_layoutPageTemplateStructureLocalService.
					updateLayoutPageTemplateStructureData(
						fragmentEntryLink.getGroupId(),
						fragmentEntryLink.getPlid(),
						fragmentEntryLink.getSegmentsExperienceId(),
						layoutStructure.toString());
			}
		}
	}

	private void _addOrRestoreDropZoneLayoutStructureItem(
		LayoutStructure layoutStructure,
		LayoutStructureItem parentLayoutStructureItem) {

		LayoutStructureItem existingLayoutStructureItem = null;

		List<DeletedLayoutStructureItem> deletedLayoutStructureItems =
			layoutStructure.getDeletedLayoutStructureItems();

		for (DeletedLayoutStructureItem deletedLayoutStructureItem :
				deletedLayoutStructureItems) {

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(
					deletedLayoutStructureItem.getItemId());

			if (Objects.equals(
					layoutStructureItem.getParentItemId(),
					parentLayoutStructureItem.getItemId())) {

				existingLayoutStructureItem = layoutStructureItem;

				break;
			}
		}

		if (existingLayoutStructureItem != null) {
			layoutStructure.unmarkLayoutStructureItemForDeletion(
				existingLayoutStructureItem.getItemId());
		}
		else {
			layoutStructure.addFragmentDropZoneLayoutStructureItem(
				parentLayoutStructureItem.getItemId(), -1);
		}
	}

	private FragmentDropZoneLayoutStructureItem
		_getDeletedFragmentDropZoneStructureItem(
			String fragmentDropZoneId, LayoutStructure layoutStructure,
			String parentItemId) {

		List<DeletedLayoutStructureItem> deletedLayoutStructureItems =
			layoutStructure.getDeletedLayoutStructureItems();

		for (DeletedLayoutStructureItem deletedLayoutStructureItem :
				deletedLayoutStructureItems) {

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(
					deletedLayoutStructureItem.getItemId());

			if (!(layoutStructureItem instanceof
					FragmentDropZoneLayoutStructureItem)) {

				continue;
			}

			FragmentDropZoneLayoutStructureItem
				fragmentDropZoneLayoutStructureItem =
					(FragmentDropZoneLayoutStructureItem)layoutStructureItem;

			if (Objects.equals(
					fragmentDropZoneLayoutStructureItem.getParentItemId(),
					parentItemId) &&
				(Validator.isBlank(fragmentDropZoneId) ||
				 Objects.equals(
					 fragmentDropZoneId,
					 fragmentDropZoneLayoutStructureItem.
						 getFragmentDropZoneId()))) {

				return fragmentDropZoneLayoutStructureItem;
			}
		}

		return null;
	}

	private Document _getDocument(String html) {
		Document document = Jsoup.parseBodyFragment(html);

		Document.OutputSettings outputSettings = new Document.OutputSettings();

		outputSettings.prettyPrint(false);

		document.outputSettings(outputSettings);

		return document;
	}

	private List<String> _getElementDropZoneIds(
			FragmentEntryLink fragmentEntryLink,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			ServiceContext serviceContext)
		throws PortalException {

		if (fragmentEntryLink == null) {
			return Collections.emptyList();
		}

		String processedHTML =
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink,
				new DefaultFragmentEntryProcessorContext(
					httpServletRequest, httpServletResponse,
					FragmentEntryLinkConstants.EDIT,
					serviceContext.getLocale()));

		Document document = _getDocument(processedHTML);

		List<String> elementDropZoneIds = new LinkedList<>();

		for (Element element : document.getElementsByTag("lfr-drop-zone")) {
			String dropZoneId = element.attr("data-lfr-drop-zone-id");

			if (Validator.isBlank(dropZoneId)) {
				break;
			}

			elementDropZoneIds.add(dropZoneId);
		}

		return elementDropZoneIds;
	}

	private LayoutStructure _getLayoutStructure(
		FragmentEntryLink fragmentEntryLink) {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					fragmentEntryLink.getGroupId(),
					fragmentEntryLink.getPlid());

		if (layoutPageTemplateStructure == null) {
			return null;
		}

		String data = layoutPageTemplateStructure.getData(
			fragmentEntryLink.getSegmentsExperienceId());

		if (Validator.isNull(data)) {
			return null;
		}

		return LayoutStructure.of(data);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DropZoneFragmentEntryLinkListener.class);

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

}