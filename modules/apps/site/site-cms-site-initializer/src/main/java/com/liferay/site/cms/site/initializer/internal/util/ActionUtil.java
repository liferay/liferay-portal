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
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.manager.FormManager;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;
import com.liferay.site.cms.site.initializer.internal.fragment.renderer.SpaceListComponentSectionFragmentRenderer;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class ActionUtil {

	public static void generateLayoutStructure(
			FormManager formManager,
			FragmentEntryLinkListenerRegistry fragmentEntryLinkListenerRegistry,
			FragmentEntryLinkService fragmentEntryLinkService,
			FragmentRendererRegistry fragmentRendererRegistry, Layout layout,
			LayoutPageTemplateEntry layoutPageTemplateEntry,
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
					"paddingBottom", "6"
				).put(
					"paddingTop", "6"
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

		List<FragmentEntryLink> addedFragmentEntryLinks = new ArrayList<>();

		FragmentEntryLink spaceListFragmentEntryLink = _addFragmentEntryLink(
			StringPool.BLANK, fragmentEntryLinkService,
			fragmentRendererRegistry,
			SpaceListComponentSectionFragmentRenderer.class.getName(), layout,
			segmentsExperienceId, serviceContext);

		if (spaceListFragmentEntryLink != null) {
			layoutStructure.addFragmentStyledLayoutStructureItem(
				spaceListFragmentEntryLink.getFragmentEntryLinkId(),
				childContainerStyledLayoutStructureItem.getItemId(), 0);

			addedFragmentEntryLinks.add(spaceListFragmentEntryLink);
		}

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

		formManager.addFragmentEntryLinksLayoutStructureItems(
			addedFragmentEntryLinks, JSONFactoryUtil.createJSONObject(),
			formStyledLayoutStructureItem, false, layout, layoutStructure,
			LocaleUtil.getMostRelevantLocale(), segmentsExperienceId,
			serviceContext, null);

		LayoutPageTemplateStructureLocalServiceUtil.
			updateLayoutPageTemplateStructureData(
				layout.getGroupId(), layout.getPlid(), segmentsExperienceId,
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

	public static String getBaseSpaceURL(ThemeDisplay themeDisplay) {
		return StringBundler.concat(
			themeDisplay.getPathFriendlyURLPublic(),
			GroupConstants.CMS_FRIENDLY_URL, "/e/space/",
			PortalUtil.getClassNameId(DepotEntry.class), StringPool.SLASH);
	}

	public static String getBaseViewFolderURL(ThemeDisplay themeDisplay) {
		return StringBundler.concat(
			themeDisplay.getPathFriendlyURLPublic(),
			GroupConstants.CMS_FRIENDLY_URL, "/e/view-folder/",
			PortalUtil.getClassNameId(ObjectEntryFolder.class),
			StringPool.SLASH);
	}

	public static String getDisplayPageEditURL(
		FormManager formManager,
		FragmentEntryLinkListenerRegistry fragmentEntryLinkListenerRegistry,
		FragmentEntryLinkService fragmentEntryLinkService,
		FragmentRendererRegistry fragmentRendererRegistry,
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Layout layout = _getLayout(
				PortalUtil.getClassNameId(objectDefinition.getClassName()),
				formManager, fragmentEntryLinkListenerRegistry,
				fragmentEntryLinkService, fragmentRendererRegistry,
				GroupLocalServiceUtil.getGroup(
					themeDisplay.getCompanyId(), GroupConstants.CMS),
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
		ObjectDefinition objectDefinition) {

		try {
			long classNameId = PortalUtil.getClassNameId(
				objectDefinition.getClassName());

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Group group = GroupLocalServiceUtil.getGroup(
				themeDisplay.getCompanyId(), GroupConstants.CMS);

			Layout layout = _getLayout(
				classNameId, formManager, fragmentEntryLinkListenerRegistry,
				fragmentEntryLinkService, fragmentRendererRegistry, group,
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

	public static String getSpaceURL(long classPK, ThemeDisplay themeDisplay) {
		return getBaseSpaceURL(themeDisplay) + classPK;
	}

	public static String geViewFolderURL(
		long objectEntryFolderId, ThemeDisplay themeDisplay) {

		return getBaseViewFolderURL(themeDisplay) + objectEntryFolderId;
	}

	private static LayoutPageTemplateEntry _addDefaultLayoutPageTemplateEntry(
			long classNameId, FormManager formManager,
			FragmentEntryLinkListenerRegistry fragmentEntryLinkListenerRegistry,
			FragmentEntryLinkService fragmentEntryLinkService,
			FragmentRendererRegistry fragmentRendererRegistry, long groupId,
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

		generateLayoutStructure(
			formManager, fragmentEntryLinkListenerRegistry,
			fragmentEntryLinkService, fragmentRendererRegistry, draftLayout,
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
			fragmentRenderer.getConfiguration(defaultFragmentRendererContext),
			editableValues, StringPool.BLANK, 0, fragmentEntryKey,
			fragmentRenderer.getType(), serviceContext);
	}

	private static Layout _getLayout(
			long classNameId, FormManager formManager,
			FragmentEntryLinkListenerRegistry fragmentEntryLinkListenerRegistry,
			FragmentEntryLinkService fragmentEntryLinkService,
			FragmentRendererRegistry fragmentRendererRegistry, Group group,
			ObjectDefinition objectDefinition, ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchDefaultLayoutPageTemplateEntry(
					group.getGroupId(), classNameId, 0);

		if (layoutPageTemplateEntry == null) {
			layoutPageTemplateEntry = _addDefaultLayoutPageTemplateEntry(
				classNameId, formManager, fragmentEntryLinkListenerRegistry,
				fragmentEntryLinkService, fragmentRendererRegistry,
				group.getGroupId(), objectDefinition.getName(), serviceContext);
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

	private static final Log _log = LogFactoryUtil.getLog(ActionUtil.class);

}