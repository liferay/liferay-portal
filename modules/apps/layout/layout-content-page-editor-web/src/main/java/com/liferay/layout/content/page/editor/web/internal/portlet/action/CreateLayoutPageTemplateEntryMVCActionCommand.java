/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryNameException;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.validator.LayoutValidator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_content_page_editor/create_layout_page_template_entry"
	},
	service = MVCActionCommand.class
)
public class CreateLayoutPageTemplateEntryMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long plid = ParamUtil.getLong(actionRequest, "plid");

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");
		Layout sourceLayout = themeDisplay.getLayout();

		if (plid > 0) {
			segmentsExperienceId =
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(plid);
			sourceLayout = _layoutLocalService.getLayout(plid);
		}

		long layoutPageTemplateCollectionId = ParamUtil.getLong(
			actionRequest, "layoutPageTemplateCollectionId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			LayoutPageTemplateEntry.class.getName(), actionRequest);

		if (layoutPageTemplateCollectionId <= 0) {
			String layoutPageTemplateCollectionName = ParamUtil.getString(
				actionRequest, "layoutPageTemplateCollectionName");
			String layoutPageTemplateCollectionDescription =
				ParamUtil.getString(
					actionRequest, "layoutPageTemplateCollectionDescription");

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_layoutPageTemplateCollectionService.
					addLayoutPageTemplateCollection(
						null, themeDisplay.getScopeGroupId(),
						LayoutPageTemplateConstants.
							PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
						null, layoutPageTemplateCollectionName,
						layoutPageTemplateCollectionDescription,
						LayoutPageTemplateCollectionTypeConstants.BASIC,
						serviceContext);

			layoutPageTemplateCollectionId =
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				createLayoutPageTemplateEntryFromLayout(
					segmentsExperienceId, sourceLayout,
					_getUniqueName(
						sourceLayout, layoutPageTemplateCollectionId,
						themeDisplay.getLocale()),
					layoutPageTemplateCollectionId, serviceContext);

		return JSONUtil.put(
			"layoutPageTemplateEntryId",
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).put(
			"url",
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					_portal.getHttpServletRequest(actionRequest),
					themeDisplay.getScopeGroup(),
					LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES, 0,
					0, PortletRequest.RENDER_PHASE)
			).setTabs1(
				"page-templates"
			).setParameter(
				"layoutPageTemplateCollectionId",
				layoutPageTemplateEntry.getLayoutPageTemplateCollectionId()
			).setParameter(
				"orderByType", "desc"
			).buildString()
		);
	}

	@Override
	protected JSONObject processException(
		ActionRequest actionRequest, Exception exception) {

		if (exception instanceof LockedLayoutException) {
			return processLockedLayoutException(actionRequest);
		}

		String errorMessage = null;

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (exception instanceof
				LayoutPageTemplateEntryNameException.MustNotBeDuplicate) {

			errorMessage = _language.get(
				themeDisplay.getLocale(),
				"a-page-template-entry-with-that-name-already-exists");
		}
		else if (exception instanceof
					LayoutPageTemplateEntryNameException.MustNotBeNull) {

			errorMessage = _language.get(
				themeDisplay.getLocale(), "name-must-not-be-empty");
		}
		else if (exception instanceof
					LayoutPageTemplateEntryNameException.
						MustNotContainInvalidCharacters) {

			LayoutPageTemplateEntryNameException.MustNotContainInvalidCharacters
				lptene =
					(LayoutPageTemplateEntryNameException.
						MustNotContainInvalidCharacters)exception;

			errorMessage = _language.format(
				themeDisplay.getLocale(),
				"name-cannot-contain-the-following-invalid-character-x",
				lptene.character);
		}
		else if (exception instanceof
					LayoutPageTemplateEntryNameException.
						MustNotExceedMaximumSize) {

			int nameMaxLength = ModelHintsUtil.getMaxLength(
				LayoutPageTemplateEntry.class.getName(), "name");

			errorMessage = _language.format(
				themeDisplay.getLocale(),
				"please-enter-a-name-with-fewer-than-x-characters",
				nameMaxLength);
		}

		if (Validator.isNull(errorMessage)) {
			errorMessage = _language.get(
				themeDisplay.getLocale(), "an-unexpected-error-occurred");
		}

		return JSONUtil.put("error", errorMessage);
	}

	private String _getUniqueName(
		Layout layout, long layoutPageTemplateCollectionId, Locale locale) {

		String layoutName = LayoutValidator.replaceBlacklistedChars(
			layout.getName(locale));

		String name = StringBundler.concat(
			layoutName, " - ", _language.get(locale, "page-template"));

		for (int i = 2;; i++) {
			LayoutPageTemplateEntry targetLayoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						layout.getGroupId(), layoutPageTemplateCollectionId,
						name, LayoutPageTemplateEntryTypeConstants.BASIC);

			if (targetLayoutPageTemplateEntry == null) {
				break;
			}

			name = StringBundler.concat(
				layoutName, " - ", _language.get(locale, "page-template"),
				StringPool.SPACE, i);
		}

		return name;
	}

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}