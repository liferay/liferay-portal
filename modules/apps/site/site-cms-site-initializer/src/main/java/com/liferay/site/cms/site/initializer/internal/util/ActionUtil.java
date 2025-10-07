/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.fragment.service.FragmentEntryLinkServiceUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.manager.FormManager;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormRelationshipStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectDefinitionServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;
import com.liferay.site.cms.site.initializer.internal.fragment.renderer.SpacesComponentSectionFragmentRenderer;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Eudaldo Alonso
 */
public class ActionUtil {

	public static void generateEditContentLayoutStructure(
			FormManager formManager,
			FragmentEntryLinkListenerRegistry fragmentEntryLinkListenerRegistry,
			FragmentEntryLinkService fragmentEntryLinkService,
			FragmentRendererRegistry fragmentRendererRegistry,
			InfoItemServiceRegistry infoItemServiceRegistry,
			InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
			Layout layout, LayoutPageTemplateEntry layoutPageTemplateEntry,
			ServiceContext serviceContext)
		throws Exception {

		long segmentsExperienceId =
			SegmentsExperienceLocalServiceUtil.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		LayoutStructure layoutStructure = new LayoutStructure();

		layoutStructure.addRootLayoutStructureItem();

		ContainerStyledLayoutStructureItem
			parentContainerStyledLayoutStructureItem =
				(ContainerStyledLayoutStructureItem)
					layoutStructure.addContainerStyledLayoutStructureItem(
						layoutStructure.getMainItemId(), 0);

		parentContainerStyledLayoutStructureItem.updateItemConfig(
			JSONUtil.put(
				"styles",
				JSONUtil.put(
					"paddingBottom", "40px"
				).put(
					"paddingLeft", "12px"
				).put(
					"paddingRight", "12px"
				).put(
					"paddingTop", "40px"
				)));

		ContainerStyledLayoutStructureItem
			childContainerStyledLayoutStructureItem =
				(ContainerStyledLayoutStructureItem)
					layoutStructure.addContainerStyledLayoutStructureItem(
						parentContainerStyledLayoutStructureItem.getItemId(),
						0);

		childContainerStyledLayoutStructureItem.setWidthType("fixed");

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.addFormStyledLayoutStructureItem(
					childContainerStyledLayoutStructureItem.getItemId(), 0);

		formStyledLayoutStructureItem.setClassNameId(
			layoutPageTemplateEntry.getClassNameId());

		formStyledLayoutStructureItem.updateItemConfig(
			JSONUtil.put(
				"cssClasses", JSONUtil.put("lfr-main-form-container")));

		List<FragmentEntryLink> addedFragmentEntryLinks = new ArrayList<>();

		FragmentEntryLink localizationSelectFragmentEntryLink =
			_addFragmentEntryLink(
				JSONUtil.toString(
					JSONUtil.put(
						FragmentEntryProcessorConstants.
							KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
						JSONUtil.put("size", "small"))),
				fragmentEntryLinkService, fragmentRendererRegistry,
				"localization-select", layout, segmentsExperienceId,
				serviceContext);

		if (localizationSelectFragmentEntryLink != null) {
			LayoutStructureItem layoutStructureItem =
				layoutStructure.addFragmentStyledLayoutStructureItem(
					localizationSelectFragmentEntryLink.
						getFragmentEntryLinkId(),
					childContainerStyledLayoutStructureItem.getItemId(), 0);

			layoutStructureItem.updateItemConfig(
				JSONUtil.put("styles", JSONUtil.put("marginBottom", "5")));

			addedFragmentEntryLinks.add(localizationSelectFragmentEntryLink);
		}

		InfoForm infoForm = _getInfoForm(
			layoutPageTemplateEntry.getClassNameId(), layout.getGroupId(),
			infoItemServiceRegistry, infoSearchClassMapperRegistry);

		_addInputFragmentEntryLink(
			addedFragmentEntryLinks,
			JSONUtil.put(
				"placeholder",
				JSONUtil.put(
					LocaleUtil.US.toString(),
					LanguageUtil.format(
						serviceContext.getLocale(), "new-x",
						infoForm.getLabel(serviceContext.getLocale())))),
			formManager, "INPUTS-inline-text-input",
			infoForm.getInfoField("title"), layout, layoutStructure,
			formStyledLayoutStructureItem, false, segmentsExperienceId,
			serviceContext,
			JSONUtil.put(
				"marginBottom", "5"
			).put(
				"marginLeft", "-16px"
			));

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			StringPool.BLANK, fragmentEntryLinkService,
			fragmentRendererRegistry,
			SpacesComponentSectionFragmentRenderer.class.getName(), layout,
			segmentsExperienceId, serviceContext);

		if (fragmentEntryLink != null) {
			LayoutStructureItem layoutStructureItem =
				layoutStructure.addFragmentStyledLayoutStructureItem(
					fragmentEntryLink.getFragmentEntryLinkId(),
					formStyledLayoutStructureItem.getItemId(), -1);

			layoutStructureItem.updateItemConfig(
				JSONUtil.put("styles", JSONUtil.put("marginBottom", "16px")));

			addedFragmentEntryLinks.add(fragmentEntryLink);
		}

		_addInputFragmentEntryLink(
			addedFragmentEntryLinks, null, formManager,
			"INPUTS-friendly-url-input",
			infoForm.getInfoField("objectEntryFriendlyURL"), layout,
			layoutStructure, formStyledLayoutStructureItem, false,
			segmentsExperienceId, serviceContext,
			JSONUtil.put("marginBottom", "5"));

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.fetchObjectDefinitionByClassName(
				layout.getCompanyId(), layoutPageTemplateEntry.getClassName());

		_addInputFragmentEntryLinks(
			addedFragmentEntryLinks, formManager,
			(InfoFieldSet)infoForm.getInfoFieldSetEntry(
				objectDefinition.getName()),
			layout, layoutStructure, formStyledLayoutStructureItem,
			objectDefinition.getName(), segmentsExperienceId, serviceContext);

		LayoutPageTemplateStructureLocalServiceUtil.
			updateLayoutPageTemplateStructureData(
				serviceContext.getUserId(), layout.getGroupId(),
				layout.getPlid(), segmentsExperienceId,
				layoutStructure.toString());

		for (FragmentEntryLink addedFragmentEntryLink :
				addedFragmentEntryLinks) {

			for (FragmentEntryLinkListener fragmentEntryLinkListener :
					fragmentEntryLinkListenerRegistry.
						getFragmentEntryLinkListeners()) {

				fragmentEntryLinkListener.onAddFragmentEntryLink(
					addedFragmentEntryLink);
			}
		}
	}

	public static void generateTranslateContentLayoutStructure(
			FormManager formManager,
			FragmentEntryLinkListenerRegistry fragmentEntryLinkListenerRegistry,
			FragmentEntryLinkService fragmentEntryLinkService,
			FragmentRendererRegistry fragmentRendererRegistry,
			InfoItemServiceRegistry infoItemServiceRegistry,
			InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
			Layout layout, LayoutPageTemplateEntry layoutPageTemplateEntry,
			ServiceContext serviceContext)
		throws Exception {

		List<FragmentEntryLink> addedFragmentEntryLinks = new ArrayList<>();

		LayoutStructure layoutStructure = new LayoutStructure();

		layoutStructure.addRootLayoutStructureItem();

		ContainerStyledLayoutStructureItem
			parentContainerStyledLayoutStructureItem =
				(ContainerStyledLayoutStructureItem)
					layoutStructure.addContainerStyledLayoutStructureItem(
						layoutStructure.getMainItemId(), 0);

		parentContainerStyledLayoutStructureItem.updateItemConfig(
			JSONUtil.put(
				"styles",
				JSONUtil.put(
					"paddingBottom", "40px"
				).put(
					"paddingLeft", "12px"
				).put(
					"paddingRight", "12px"
				).put(
					"paddingTop", "40px"
				)));

		RowStyledLayoutStructureItem rowStyledLayoutStructureItem =
			(RowStyledLayoutStructureItem)
				layoutStructure.addRowStyledLayoutStructureItem(
					parentContainerStyledLayoutStructureItem.getItemId(), 0, 2);

		ColumnLayoutStructureItem firstColumnLayoutStructureItem =
			(ColumnLayoutStructureItem)
				layoutStructure.addColumnLayoutStructureItem(
					rowStyledLayoutStructureItem.getItemId(), 0);

		firstColumnLayoutStructureItem.setSize(6);

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.addFormStyledLayoutStructureItem(
					firstColumnLayoutStructureItem.getItemId(), 0);

		formStyledLayoutStructureItem.setClassNameId(
			layoutPageTemplateEntry.getClassNameId());

		InfoForm infoForm = _getInfoForm(
			formStyledLayoutStructureItem.getClassNameId(), layout.getGroupId(),
			infoItemServiceRegistry, infoSearchClassMapperRegistry);

		long segmentsExperienceId =
			SegmentsExperienceLocalServiceUtil.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		_addInputFragmentEntryLink(
			addedFragmentEntryLinks, null, formManager, "INPUTS-text-input",
			infoForm.getInfoField("objectEntryFriendlyURL"), layout,
			layoutStructure, formStyledLayoutStructureItem, true,
			segmentsExperienceId, serviceContext,
			JSONUtil.put("marginBottom", "24px"));

		Set<String> localizableInfoFieldIds = new HashSet<>();

		for (InfoField<?> infoField : infoForm.getAllInfoFields()) {
			if (infoField.isLocalizable() &&
				!Objects.equals(
					infoField.getName(), "objectEntryFriendlyURL")) {

				localizableInfoFieldIds.add(infoField.getUniqueId());
			}
		}

		List<LayoutStructureItem> layoutStructureItems =
			formManager.addFragmentEntryLinksLayoutStructureItems(
				addedFragmentEntryLinks, JSONFactoryUtil.createJSONObject(),
				formStyledLayoutStructureItem, false, layout, layoutStructure,
				LocaleUtil.getMostRelevantLocale(), true, segmentsExperienceId,
				serviceContext, localizableInfoFieldIds.toArray(new String[0]));

		FragmentEntryLink localizationSelectFragmentEntryLink =
			_addFragmentEntryLink(
				JSONUtil.toString(
					JSONUtil.put(
						FragmentEntryProcessorConstants.
							KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
						JSONUtil.put("size", "small"))),
				fragmentEntryLinkService, fragmentRendererRegistry,
				"localization-select", layout, segmentsExperienceId,
				serviceContext);

		if (localizationSelectFragmentEntryLink != null) {
			LayoutStructureItem layoutStructureItem =
				layoutStructure.addFragmentStyledLayoutStructureItem(
					localizationSelectFragmentEntryLink.
						getFragmentEntryLinkId(),
					formStyledLayoutStructureItem.getItemId(), 0);

			layoutStructureItem.updateItemConfig(
				JSONUtil.put("styles", JSONUtil.put("marginBottom", "5")));

			addedFragmentEntryLinks.add(localizationSelectFragmentEntryLink);
		}

		ColumnLayoutStructureItem secondColumnLayoutStructureItem =
			(ColumnLayoutStructureItem)
				layoutStructure.addColumnLayoutStructureItem(
					rowStyledLayoutStructureItem.getItemId(), 1);

		secondColumnLayoutStructureItem.setSize(6);

		formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.addFormStyledLayoutStructureItem(
					secondColumnLayoutStructureItem.getItemId(), 0);

		formStyledLayoutStructureItem.setClassNameId(
			layoutPageTemplateEntry.getClassNameId());
		formStyledLayoutStructureItem.updateItemConfig(
			JSONUtil.put(
				"cssClasses", JSONUtil.put("lfr-main-form-container")));

		_addInputFragmentEntryLink(
			addedFragmentEntryLinks, null, formManager, "INPUTS-text-input",
			infoForm.getInfoField("objectEntryFriendlyURL"), layout,
			layoutStructure, formStyledLayoutStructureItem, false,
			segmentsExperienceId, serviceContext,
			JSONUtil.put("marginBottom", "24px"));

		layoutStructureItems.addAll(
			formManager.addFragmentEntryLinksLayoutStructureItems(
				addedFragmentEntryLinks, JSONFactoryUtil.createJSONObject(),
				formStyledLayoutStructureItem, false, layout, layoutStructure,
				LocaleUtil.getMostRelevantLocale(), false, segmentsExperienceId,
				serviceContext,
				localizableInfoFieldIds.toArray(new String[0])));

		localizationSelectFragmentEntryLink = _addFragmentEntryLink(
			JSONUtil.toString(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"allowLocalizationManagement", "true"
					).put(
						"size", "small"
					))),
			fragmentEntryLinkService, fragmentRendererRegistry,
			"localization-select", layout, segmentsExperienceId,
			serviceContext);

		if (localizationSelectFragmentEntryLink != null) {
			LayoutStructureItem layoutStructureItem =
				layoutStructure.addFragmentStyledLayoutStructureItem(
					localizationSelectFragmentEntryLink.
						getFragmentEntryLinkId(),
					formStyledLayoutStructureItem.getItemId(), 0);

			layoutStructureItem.updateItemConfig(
				JSONUtil.put("styles", JSONUtil.put("marginBottom", "5")));

			addedFragmentEntryLinks.add(localizationSelectFragmentEntryLink);
		}

		for (LayoutStructureItem layoutStructureItem : layoutStructureItems) {
			layoutStructureItem.updateItemConfig(
				JSONUtil.put("styles", JSONUtil.put("marginBottom", "24px")));
		}

		LayoutPageTemplateStructureLocalServiceUtil.
			updateLayoutPageTemplateStructureData(
				serviceContext.getUserId(), layout.getGroupId(),
				layout.getPlid(), segmentsExperienceId,
				layoutStructure.toString());

		for (FragmentEntryLink addedFragmentEntryLink :
				addedFragmentEntryLinks) {

			for (FragmentEntryLinkListener fragmentEntryLinkListener :
					fragmentEntryLinkListenerRegistry.
						getFragmentEntryLinkListeners()) {

				fragmentEntryLinkListener.onAddFragmentEntryLink(
					addedFragmentEntryLink);
			}
		}
	}

	public static List<DropdownItem> getAllSectionCreationMenuDropdownItems(
		HttpServletRequest httpServletRequest) {

		List<DropdownItem> dropdownItems = new ArrayList<>(
			List.of(
				getBasicWebContentDropdownItem(
					httpServletRequest,
					ObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENTS),
				getBasicDocumentDropdownItem(
					httpServletRequest,
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES),
				getUploadMultipleFilesDropdownItem(
					httpServletRequest,
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES),
				getBlogDropdownItem(
					httpServletRequest,
					ObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENTS),
				getExternalVideoShortcutDropdownItem(
					httpServletRequest,
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)));

		List<DropdownItem> contentsCustomDropdownItems =
			getContentsCustomDropdownItems(
				httpServletRequest,
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS);

		contentsCustomDropdownItems.addAll(
			getFilesCustomDropdownItems(
				httpServletRequest,
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES));

		contentsCustomDropdownItems.sort(
			Comparator.comparing(
				dropdownItem -> (String)dropdownItem.get("label"),
				String.CASE_INSENSITIVE_ORDER));

		dropdownItems.addAll(contentsCustomDropdownItems);

		return dropdownItems;
	}

	public static String getBaseAddSpaceMembersURL(ThemeDisplay themeDisplay) {
		return StringBundler.concat(
			themeDisplay.getPathFriendlyURLPublic(),
			GroupConstants.CMS_FRIENDLY_URL, "/add-space-members");
	}

	public static String getBaseBulkActionTaskReportURL(
		String className, ThemeDisplay themeDisplay) {

		return StringBundler.concat(
			themeDisplay.getPathFriendlyURLPublic(),
			GroupConstants.CMS_FRIENDLY_URL, "/e/bulk-action-task/",
			PortalUtil.getClassNameId(className));
	}

	public static String getBaseSpaceSettingsURL(ThemeDisplay themeDisplay) {
		return StringBundler.concat(
			themeDisplay.getPathFriendlyURLPublic(),
			GroupConstants.CMS_FRIENDLY_URL, "/e/space-settings/",
			PortalUtil.getClassNameId(DepotEntry.class), StringPool.SLASH);
	}

	public static String getBaseSpaceURL(ThemeDisplay themeDisplay) {
		return StringBundler.concat(
			themeDisplay.getPathFriendlyURLPublic(),
			GroupConstants.CMS_FRIENDLY_URL, "/e/space/",
			PortalUtil.getClassNameId(DepotEntry.class), StringPool.SLASH);
	}

	public static String getBaseStructureBuilderURL(ThemeDisplay themeDisplay) {
		return StringBundler.concat(
			themeDisplay.getPathFriendlyURLPublic(),
			GroupConstants.CMS_FRIENDLY_URL, "/structure-builder");
	}

	public static String getBaseStructureUsagesURL(ThemeDisplay themeDisplay) {
		return StringBundler.concat(
			themeDisplay.getPathFriendlyURLPublic(),
			GroupConstants.CMS_FRIENDLY_URL,
			"/structure-usages?objectDefinitionId=");
	}

	public static String getBaseViewFolderRecycleBinURL(
		ThemeDisplay themeDisplay) {

		return StringBundler.concat(
			themeDisplay.getPathFriendlyURLPublic(),
			GroupConstants.CMS_FRIENDLY_URL, "/e/recycle-bin/",
			PortalUtil.getClassNameId(ObjectEntryFolder.class),
			StringPool.SLASH);
	}

	public static String getBaseViewFolderURL(ThemeDisplay themeDisplay) {
		return StringBundler.concat(
			themeDisplay.getPathFriendlyURLPublic(),
			GroupConstants.CMS_FRIENDLY_URL, "/e/view-folder/",
			PortalUtil.getClassNameId(ObjectEntryFolder.class),
			StringPool.SLASH);
	}

	public static DropdownItem getBasicDocumentDropdownItem(
		HttpServletRequest httpServletRequest,
		String objectEntryFolderExternalReferenceCode) {

		return getStructuredContentDropdownItem(
			httpServletRequest, "upload", "single-file", "L_BASIC_DOCUMENT",
			objectEntryFolderExternalReferenceCode);
	}

	public static DropdownItem getBasicWebContentDropdownItem(
		HttpServletRequest httpServletRequest,
		String objectEntryFolderExternalReferenceCode) {

		return getStructuredContentDropdownItem(
			httpServletRequest, "forms", "basic-content", "L_BASIC_WEB_CONTENT",
			objectEntryFolderExternalReferenceCode);
	}

	public static DropdownItem getBlogDropdownItem(
		HttpServletRequest httpServletRequest,
		String objectEntryFolderExternalReferenceCode) {

		return getStructuredContentDropdownItem(
			httpServletRequest, "blogs", "blog", "L_BLOG",
			objectEntryFolderExternalReferenceCode);
	}

	public static List<DropdownItem> getContentsCustomDropdownItems(
		HttpServletRequest httpServletRequest,
		String objectEntryFolderExternalReferenceCode) {

		List<DropdownItem> dropdownItems = new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		for (ObjectDefinition objectDefinition :
				ObjectDefinitionServiceUtil.getCMSObjectDefinitions(
					themeDisplay.getCompanyId(),
					new String[] {
						ObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES
					})) {

			if (objectDefinition.isSystem()) {
				continue;
			}

			dropdownItems.add(
				getStructuredContentDropdownItem(
					httpServletRequest, "web-content", null, objectDefinition,
					objectEntryFolderExternalReferenceCode));
		}

		return dropdownItems;
	}

	public static List<DropdownItem>
		getContentsSectionCreationMenuDropdownItems(
			HttpServletRequest httpServletRequest,
			ObjectEntryFolder objectEntryFolder) {

		String objectEntryFolderExternalReferenceCode =
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS;

		if (objectEntryFolder != null) {
			objectEntryFolderExternalReferenceCode =
				objectEntryFolder.getExternalReferenceCode();
		}

		List<DropdownItem> dropdownItems = new ArrayList<>(
			List.of(
				getCreateFolderDropdownItem(
					httpServletRequest, objectEntryFolderExternalReferenceCode),
				getBasicWebContentDropdownItem(
					httpServletRequest, objectEntryFolderExternalReferenceCode),
				getBlogDropdownItem(
					httpServletRequest,
					objectEntryFolderExternalReferenceCode)));

		List<DropdownItem> contentsCustomDropdownItems =
			getContentsCustomDropdownItems(
				httpServletRequest, objectEntryFolderExternalReferenceCode);

		contentsCustomDropdownItems.sort(
			Comparator.comparing(
				dropdownItem -> (String)dropdownItem.get("label"),
				String.CASE_INSENSITIVE_ORDER));

		dropdownItems.addAll(contentsCustomDropdownItems);

		return dropdownItems;
	}

	public static DropdownItem getCreateFolderDropdownItem(
		HttpServletRequest httpServletRequest,
		String parentObjectEntryFolderExternalReferenceCode) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return DropdownItemBuilder.putData(
			"action", "createFolder"
		).putData(
			"baseAssetLibraryViewURL", getBaseSpaceURL(themeDisplay)
		).putData(
			"baseFolderViewURL", getBaseViewFolderURL(themeDisplay)
		).putData(
			"parentObjectEntryFolderExternalReferenceCode",
			parentObjectEntryFolderExternalReferenceCode
		).setIcon(
			"folder"
		).setLabel(
			LanguageUtil.get(httpServletRequest, "folder")
		).build();
	}

	public static String getDisplayPageEditURL(
		FormManager formManager,
		FragmentEntryLinkListenerRegistry fragmentEntryLinkListenerRegistry,
		FragmentEntryLinkService fragmentEntryLinkService,
		FragmentRendererRegistry fragmentRendererRegistry,
		HttpServletRequest httpServletRequest,
		InfoItemServiceRegistry infoItemServiceRegistry,
		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
		ObjectDefinition objectDefinition) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Layout layout = _getEditContentLayout(
				PortalUtil.getClassNameId(objectDefinition.getClassName()),
				formManager, fragmentEntryLinkListenerRegistry,
				fragmentEntryLinkService, fragmentRendererRegistry,
				GroupLocalServiceUtil.getGroup(
					themeDisplay.getCompanyId(), GroupConstants.CMS),
				infoItemServiceRegistry, infoSearchClassMapperRegistry,
				objectDefinition,
				ServiceContextFactory.getInstance(httpServletRequest));

			String editURL = HttpComponentsUtil.addParameters(
				PortalUtil.getLayoutFullURL(
					layout.fetchDraftLayout(), themeDisplay),
				"p_l_mode", Constants.EDIT);

			String backURL = ParamUtil.getString(httpServletRequest, "backURL");

			if (Validator.isNotNull(backURL)) {
				editURL = HttpComponentsUtil.addParameters(
					editURL, "backURL", backURL, "p_l_back_url", backURL);
			}

			return editURL;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	public static String getEditURL(
		FormManager formManager,
		FragmentEntryLinkListenerRegistry fragmentEntryLinkListenerRegistry,
		FragmentEntryLinkService fragmentEntryLinkService,
		FragmentRendererRegistry fragmentRendererRegistry,
		HttpServletRequest httpServletRequest, String id,
		InfoItemServiceRegistry infoItemServiceRegistry,
		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
		ObjectDefinition objectDefinition) {

		try {
			long classNameId = PortalUtil.getClassNameId(
				objectDefinition.getClassName());

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Group group = GroupLocalServiceUtil.getGroup(
				themeDisplay.getCompanyId(), GroupConstants.CMS);

			Layout layout = _getEditContentLayout(
				classNameId, formManager, fragmentEntryLinkListenerRegistry,
				fragmentEntryLinkService, fragmentRendererRegistry, group,
				infoItemServiceRegistry, infoSearchClassMapperRegistry,
				objectDefinition,
				ServiceContextFactory.getInstance(httpServletRequest));

			String editURL = PortalUtil.addPreservedParameters(
				themeDisplay,
				StringBundler.concat(
					PortalUtil.getGroupFriendlyURL(
						group.getPublicLayoutSet(), themeDisplay, false, false),
					_getURLSeparator(),
					layout.getFriendlyURL(themeDisplay.getLocale()),
					StringPool.SLASH, classNameId, StringPool.SLASH, id));

			String backURL = ParamUtil.getString(
				httpServletRequest, "redirect");

			if (Validator.isNotNull(backURL)) {
				editURL = HttpComponentsUtil.addParameter(
					editURL, "redirect", backURL);
			}

			return editURL;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	public static DropdownItem getExternalVideoShortcutDropdownItem(
		HttpServletRequest httpServletRequest,
		String objectEntryFolderExternalReferenceCode) {

		return getStructuredContentDropdownItem(
			httpServletRequest, "video", "external-video-shortcut",
			"L_EXTERNAL_VIDEO", objectEntryFolderExternalReferenceCode);
	}

	public static List<DropdownItem> getFilesCustomDropdownItems(
		HttpServletRequest httpServletRequest,
		String objectEntryFolderExternalReferenceCode) {

		List<DropdownItem> dropdownItems = new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		for (ObjectDefinition objectDefinition :
				ObjectDefinitionServiceUtil.getCMSObjectDefinitions(
					themeDisplay.getCompanyId(),
					new String[] {
						ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
					})) {

			if (objectDefinition.isSystem()) {
				continue;
			}

			dropdownItems.add(
				getStructuredContentDropdownItem(
					httpServletRequest, "document-default", null,
					objectDefinition, objectEntryFolderExternalReferenceCode));
		}

		return dropdownItems;
	}

	public static List<DropdownItem> getFilesSectionCreationMenuDropdownItems(
		HttpServletRequest httpServletRequest,
		ObjectEntryFolder objectEntryFolder) {

		String objectEntryFolderExternalReferenceCode =
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES;

		if (objectEntryFolder != null) {
			objectEntryFolderExternalReferenceCode =
				objectEntryFolder.getExternalReferenceCode();
		}

		List<DropdownItem> dropdownItems = new ArrayList<>(
			List.of(
				getBasicDocumentDropdownItem(
					httpServletRequest, objectEntryFolderExternalReferenceCode),
				getUploadMultipleFilesDropdownItem(
					httpServletRequest, objectEntryFolderExternalReferenceCode),
				getCreateFolderDropdownItem(
					httpServletRequest, objectEntryFolderExternalReferenceCode),
				getExternalVideoShortcutDropdownItem(
					httpServletRequest,
					objectEntryFolderExternalReferenceCode)));

		List<DropdownItem> filesCustomDropdownItems =
			getFilesCustomDropdownItems(
				httpServletRequest, objectEntryFolderExternalReferenceCode);

		filesCustomDropdownItems.sort(
			Comparator.comparing(
				dropdownItem -> (String)dropdownItem.get("label"),
				String.CASE_INSENSITIVE_ORDER));

		dropdownItems.addAll(filesCustomDropdownItems);

		return dropdownItems;
	}

	public static String getRecycleBinURL(ThemeDisplay themeDisplay) {
		return StringBundler.concat(
			themeDisplay.getPathFriendlyURLPublic(),
			GroupConstants.CMS_FRIENDLY_URL, "/recycle-bin");
	}

	public static String getSpaceSettingsURL(
		long classPK, String redirectURL, ThemeDisplay themeDisplay) {

		String url = getBaseSpaceSettingsURL(themeDisplay) + classPK;

		if (Validator.isNotNull(redirectURL)) {
			return url + "?redirect=" + redirectURL;
		}

		return url;
	}

	public static String getSpaceURL(long classPK, ThemeDisplay themeDisplay) {
		return getBaseSpaceURL(themeDisplay) + classPK;
	}

	public static DropdownItem getStructuredContentDropdownItem(
		HttpServletRequest httpServletRequest, String icon, String labelKey,
		ObjectDefinition objectDefinition,
		String objectEntryFolderExternalReferenceCode) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return DropdownItemBuilder.putData(
			"action", "createAsset"
		).putData(
			"objectDefinitionId",
			String.valueOf(objectDefinition.getObjectDefinitionId())
		).putData(
			"redirect",
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/add_structured_content_item?objectDefinitionId=",
				objectDefinition.getObjectDefinitionId(),
				"&objectEntryFolderExternalReferenceCode=",
				objectEntryFolderExternalReferenceCode, "&plid=",
				themeDisplay.getPlid(), "&redirect=",
				themeDisplay.getURLCurrent())
		).putData(
			"title", objectDefinition.getLabel(themeDisplay.getLocale())
		).setIcon(
			icon
		).setLabel(
			() -> {
				if (Validator.isNull(labelKey)) {
					return objectDefinition.getLabel(themeDisplay.getLocale());
				}

				return LanguageUtil.get(httpServletRequest, labelKey);
			}
		).build();
	}

	public static DropdownItem getStructuredContentDropdownItem(
		HttpServletRequest httpServletRequest, String icon, String labelKey,
		String objectDefinitionExternalReferenceCode,
		String objectEntryFolderExternalReferenceCode) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode,
					themeDisplay.getCompanyId());

		if (objectDefinition == null) {
			return null;
		}

		return getStructuredContentDropdownItem(
			httpServletRequest, icon, labelKey, objectDefinition,
			objectEntryFolderExternalReferenceCode);
	}

	public static String getTranslateURL(
		FormManager formManager,
		FragmentEntryLinkListenerRegistry fragmentEntryLinkListenerRegistry,
		FragmentEntryLinkService fragmentEntryLinkService,
		FragmentRendererRegistry fragmentRendererRegistry,
		HttpServletRequest httpServletRequest, String id,
		InfoItemServiceRegistry infoItemServiceRegistry,
		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
		ObjectDefinition objectDefinition) {

		try {
			long classNameId = PortalUtil.getClassNameId(
				objectDefinition.getClassName());

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Group group = GroupLocalServiceUtil.getGroup(
				themeDisplay.getCompanyId(), GroupConstants.CMS);

			Layout layout = _getTranslateContentLayout(
				classNameId, formManager, fragmentEntryLinkListenerRegistry,
				fragmentEntryLinkService, fragmentRendererRegistry, group,
				infoItemServiceRegistry, infoSearchClassMapperRegistry,
				objectDefinition,
				ServiceContextFactory.getInstance(httpServletRequest));

			String translateURL = PortalUtil.addPreservedParameters(
				themeDisplay,
				StringBundler.concat(
					PortalUtil.getGroupFriendlyURL(
						group.getPublicLayoutSet(), themeDisplay, false, false),
					_getURLSeparator(),
					layout.getFriendlyURL(themeDisplay.getLocale()),
					StringPool.SLASH, classNameId, StringPool.SLASH, id));

			String backURL = ParamUtil.getString(
				httpServletRequest, "redirect");

			if (Validator.isNotNull(backURL)) {
				translateURL = HttpComponentsUtil.addParameter(
					translateURL, "redirect", backURL);
			}

			return translateURL;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	public static DropdownItem getUploadMultipleFilesDropdownItem(
		HttpServletRequest httpServletRequest,
		String parentObjectEntryFolderExternalReferenceCode) {

		return DropdownItemBuilder.putData(
			"action", "uploadMultipleFiles"
		).putData(
			"parentObjectEntryFolderExternalReferenceCode",
			parentObjectEntryFolderExternalReferenceCode
		).setIcon(
			"upload-multiple"
		).setLabel(
			LanguageUtil.get(httpServletRequest, "multiple-files")
		).build();
	}

	public static String getViewFolderRecycleBinURL(
		long objectEntryFolderId, ThemeDisplay themeDisplay) {

		return getBaseViewFolderRecycleBinURL(themeDisplay) +
			objectEntryFolderId;
	}

	public static String getViewFolderURL(
		long objectEntryFolderId, ThemeDisplay themeDisplay) {

		return getBaseViewFolderURL(themeDisplay) + objectEntryFolderId;
	}

	private static LayoutPageTemplateEntry
			_addEditContentDefaultLayoutPageTemplateEntry(
				long classNameId, FormManager formManager,
				FragmentEntryLinkListenerRegistry
					fragmentEntryLinkListenerRegistry,
				FragmentEntryLinkService fragmentEntryLinkService,
				FragmentRendererRegistry fragmentRendererRegistry, long groupId,
				InfoItemServiceRegistry infoItemServiceRegistry,
				InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
				String name, ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.addLayoutPageTemplateEntry(
				null, serviceContext.getUserId(), groupId, 0, null, classNameId,
				0, name, LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				true, 0, 0, 0, WorkflowConstants.STATUS_APPROVED,
				serviceContext);

		Layout layout = LayoutLocalServiceUtil.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		generateEditContentLayoutStructure(
			formManager, fragmentEntryLinkListenerRegistry,
			fragmentEntryLinkService, fragmentRendererRegistry,
			infoItemServiceRegistry, infoSearchClassMapperRegistry, draftLayout,
			layoutPageTemplateEntry, serviceContext);

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntry(groupId, "content-editor-master");

		if (masterLayoutPageTemplateEntry != null) {
			draftLayout.setMasterLayoutPlid(
				masterLayoutPageTemplateEntry.getPlid());
		}

		LayoutLocalServiceUtil.copyLayoutContent(draftLayout, layout);

		draftLayout = LayoutLocalServiceUtil.getLayout(draftLayout.getPlid());

		if (masterLayoutPageTemplateEntry != null) {
			draftLayout.setMasterLayoutPlid(
				masterLayoutPageTemplateEntry.getPlid());
		}

		draftLayout.setStatus(WorkflowConstants.STATUS_APPROVED);

		draftLayout = LayoutLocalServiceUtil.updateLayout(draftLayout);

		UnicodeProperties typeSettingsUnicodeProperties =
			draftLayout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put(
			LayoutTypeSettingsConstants.KEY_AUTOGENERATED,
			Boolean.TRUE.toString());
		typeSettingsUnicodeProperties.put(
			LayoutTypeSettingsConstants.KEY_PUBLISHED, Boolean.TRUE.toString());

		LayoutLocalServiceUtil.updateLayout(
			draftLayout.getGroupId(), draftLayout.isPrivateLayout(),
			draftLayout.getLayoutId(),
			typeSettingsUnicodeProperties.toString());

		return layoutPageTemplateEntry;
	}

	private static FragmentEntryLink _addFragmentEntryLink(
			String editableValues,
			FragmentEntryLinkService fragmentEntryLinkService,
			FragmentRendererRegistry fragmentRendererRegistry,
			String fragmentEntryKey, Layout layout, long segmentsExperienceId,
			ServiceContext serviceContext)
		throws Exception {

		FragmentRenderer fragmentRenderer =
			fragmentRendererRegistry.getFragmentRenderer(fragmentEntryKey);

		if (fragmentRenderer == null) {
			return null;
		}

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(null);

		return fragmentEntryLinkService.addFragmentEntryLink(
			null, layout.getGroupId(), 0, 0, segmentsExperienceId,
			layout.getPlid(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK,
			JSONFactoryUtil.toString(
				fragmentRenderer.getConfigurationJSONObject(
					defaultFragmentRendererContext)),
			editableValues, StringPool.BLANK, 0, fragmentEntryKey,
			fragmentRenderer.getType(), serviceContext);
	}

	private static void _addInputFragmentEntryLink(
			List<FragmentEntryLink> addedFragmentEntryLinks,
			JSONObject configurationJSONObject, FormManager formManager,
			String fragmentEntryKey, InfoField<?> infoField, Layout layout,
			LayoutStructure layoutStructure,
			LayoutStructureItem layoutStructureItem, boolean readOnly,
			long segmentsExperienceId, ServiceContext serviceContext,
			JSONObject stylesJSONObject)
		throws Exception {

		if (infoField == null) {
			return;
		}

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			formManager.addFragmentEntryLinksLayoutStructureItem(
				fragmentEntryKey, infoField, layout, layoutStructure,
				layoutStructureItem, readOnly, segmentsExperienceId,
				serviceContext);

		if (fragmentStyledLayoutStructureItem == null) {
			return;
		}

		fragmentStyledLayoutStructureItem.updateItemConfig(
			JSONUtil.put("styles", stylesJSONObject));

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryLinkLocalServiceUtil.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		if (configurationJSONObject != null) {
			JSONObject editableValuesJSONObject =
				fragmentEntryLink.getEditableValuesJSONObject();

			JSONObject jsonObject = editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

			for (String key : configurationJSONObject.keySet()) {
				jsonObject.put(key, configurationJSONObject.get(key));
			}

			fragmentEntryLink =
				FragmentEntryLinkServiceUtil.updateFragmentEntryLink(
					fragmentEntryLink.getFragmentEntryLinkId(),
					editableValuesJSONObject.toString());
		}

		if (fragmentEntryLink != null) {
			addedFragmentEntryLinks.add(fragmentEntryLink);
		}
	}

	private static void _addInputFragmentEntryLinks(
			List<FragmentEntryLink> addedFragmentEntryLinks,
			FormManager formManager, InfoFieldSet infoFieldSet, Layout layout,
			LayoutStructure layoutStructure,
			LayoutStructureItem layoutStructureItem,
			String objectDefinitionName, long segmentsExperienceId,
			ServiceContext serviceContext)
		throws Exception {

		if (infoFieldSet.isRelationship()) {
			FormRelationshipStyledLayoutStructureItem
				formRelationshipStyledLayoutStructureItem =
					(FormRelationshipStyledLayoutStructureItem)
						layoutStructure.
							addFormRelationshipStyledLayoutStructureItem(
								PortalUUIDUtil.generate(),
								layoutStructureItem.getItemId(), -1);

			formRelationshipStyledLayoutStructureItem.setContentType(
				infoFieldSet.getName());

			layoutStructureItem = formRelationshipStyledLayoutStructureItem;
		}

		for (InfoFieldSetEntry infoFieldSetEntry :
				infoFieldSet.getInfoFieldSetEntries()) {

			if (Objects.equals(infoFieldSet.getName(), objectDefinitionName) &&
				ArrayUtil.contains(
					_HIDDEN_INFO_FIELDS, infoFieldSetEntry.getName())) {

				continue;
			}

			if (infoFieldSetEntry instanceof InfoFieldSet) {
				_addInputFragmentEntryLinks(
					addedFragmentEntryLinks, formManager,
					(InfoFieldSet)infoFieldSetEntry, layout, layoutStructure,
					layoutStructureItem, objectDefinitionName,
					segmentsExperienceId, serviceContext);
			}
			else {
				_addInputFragmentEntryLink(
					addedFragmentEntryLinks, null, formManager, null,
					(InfoField<?>)infoFieldSetEntry, layout, layoutStructure,
					layoutStructureItem, false, segmentsExperienceId,
					serviceContext,
					JSONUtil.put(
						"styles", JSONUtil.put("marginBottom", "16px")));
			}
		}
	}

	private static LayoutPageTemplateEntry
			_addTranslateContentDefaultLayoutPageTemplateEntry(
				long classNameId, FormManager formManager,
				FragmentEntryLinkListenerRegistry
					fragmentEntryLinkListenerRegistry,
				FragmentEntryLinkService fragmentEntryLinkService,
				FragmentRendererRegistry fragmentRendererRegistry, long groupId,
				InfoItemServiceRegistry infoItemServiceRegistry,
				InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
				String name, ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.addLayoutPageTemplateEntry(
				null, serviceContext.getUserId(), groupId, 0,
				_TRANSLATION_LAYOUT_PAGE_TEMPLATE_ENTRY_KEY_PREFIX +
					classNameId,
				classNameId, 0, name,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0, true, 0,
				0, 0, WorkflowConstants.STATUS_APPROVED, serviceContext);

		Layout layout = LayoutLocalServiceUtil.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		generateTranslateContentLayoutStructure(
			formManager, fragmentEntryLinkListenerRegistry,
			fragmentEntryLinkService, fragmentRendererRegistry,
			infoItemServiceRegistry, infoSearchClassMapperRegistry, draftLayout,
			layoutPageTemplateEntry, serviceContext);

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntry(groupId, "cms-translation-master");

		if (masterLayoutPageTemplateEntry != null) {
			draftLayout.setMasterLayoutPlid(
				masterLayoutPageTemplateEntry.getPlid());
		}

		LayoutLocalServiceUtil.copyLayoutContent(draftLayout, layout);

		draftLayout = LayoutLocalServiceUtil.getLayout(draftLayout.getPlid());

		if (masterLayoutPageTemplateEntry != null) {
			draftLayout.setMasterLayoutPlid(
				masterLayoutPageTemplateEntry.getPlid());
		}

		draftLayout.setStatus(WorkflowConstants.STATUS_APPROVED);

		LayoutLocalServiceUtil.updateLayout(draftLayout);

		return layoutPageTemplateEntry;
	}

	private static Layout _getEditContentLayout(
			long classNameId, FormManager formManager,
			FragmentEntryLinkListenerRegistry fragmentEntryLinkListenerRegistry,
			FragmentEntryLinkService fragmentEntryLinkService,
			FragmentRendererRegistry fragmentRendererRegistry, Group group,
			InfoItemServiceRegistry infoItemServiceRegistry,
			InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
			ObjectDefinition objectDefinition, ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchDefaultLayoutPageTemplateEntry(
					group.getGroupId(), classNameId, 0);

		if (layoutPageTemplateEntry == null) {
			layoutPageTemplateEntry =
				_addEditContentDefaultLayoutPageTemplateEntry(
					classNameId, formManager, fragmentEntryLinkListenerRegistry,
					fragmentEntryLinkService, fragmentRendererRegistry,
					group.getGroupId(), infoItemServiceRegistry,
					infoSearchClassMapperRegistry, objectDefinition.getName(),
					serviceContext);
		}

		return LayoutLocalServiceUtil.fetchLayout(
			layoutPageTemplateEntry.getPlid());
	}

	private static InfoForm _getInfoForm(
			long classNameId, long groupId,
			InfoItemServiceRegistry infoItemServiceRegistry,
			InfoSearchClassMapperRegistry infoSearchClassMapperRegistry)
		throws Exception {

		ClassName className = ClassNameLocalServiceUtil.fetchClassName(
			classNameId);

		String itemClassName = infoSearchClassMapperRegistry.getClassName(
			className.getClassName());

		InfoItemFormProvider<?> infoItemFormProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, itemClassName);

		if (infoItemFormProvider == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get info item form provider for class " +
						itemClassName);
			}

			return null;
		}

		return infoItemFormProvider.getInfoForm(StringPool.BLANK, groupId);
	}

	private static Layout _getTranslateContentLayout(
			long classNameId, FormManager formManager,
			FragmentEntryLinkListenerRegistry fragmentEntryLinkListenerRegistry,
			FragmentEntryLinkService fragmentEntryLinkService,
			FragmentRendererRegistry fragmentRendererRegistry, Group group,
			InfoItemServiceRegistry infoItemServiceRegistry,
			InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
			ObjectDefinition objectDefinition, ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntry(
					group.getGroupId(),
					_TRANSLATION_LAYOUT_PAGE_TEMPLATE_ENTRY_KEY_PREFIX +
						classNameId);

		if (layoutPageTemplateEntry == null) {
			layoutPageTemplateEntry =
				_addTranslateContentDefaultLayoutPageTemplateEntry(
					classNameId, formManager, fragmentEntryLinkListenerRegistry,
					fragmentEntryLinkService, fragmentRendererRegistry,
					group.getGroupId(), infoItemServiceRegistry,
					infoSearchClassMapperRegistry,
					_TRANSLATION_LAYOUT_PAGE_TEMPLATE_ENTRY_KEY_PREFIX +
						objectDefinition.getName(),
					serviceContext);
		}

		return LayoutLocalServiceUtil.fetchLayout(
			layoutPageTemplateEntry.getPlid());
	}

	private static String _getURLSeparator() {
		FriendlyURLResolver friendlyURLResolver =
			FriendlyURLResolverRegistryUtil.
				getFriendlyURLResolverByDefaultURLSeparator(
					FriendlyURLResolverConstants.URL_SEPARATOR_CUSTOM_ASSET);

		if (friendlyURLResolver != null) {
			String urlSeparator = friendlyURLResolver.getURLSeparator();

			return urlSeparator.substring(0, urlSeparator.length() - 1);
		}

		return FriendlyURLResolverConstants.URL_SEPARATOR_X_CUSTOM_ASSET;
	}

	private static final String[] _HIDDEN_INFO_FIELDS = {
		"displayDate", "expirationDate", "externalReferenceCode",
		"objectEntryFriendlyURL", "reviewDate", "title"
	};

	private static final String
		_TRANSLATION_LAYOUT_PAGE_TEMPLATE_ENTRY_KEY_PREFIX =
			"LFR_CMS_TRANSLATION_";

	private static final Log _log = LogFactoryUtil.getLog(ActionUtil.class);

}