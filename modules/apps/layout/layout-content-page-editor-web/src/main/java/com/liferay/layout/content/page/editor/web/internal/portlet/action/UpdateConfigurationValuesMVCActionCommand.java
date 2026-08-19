/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/update_configuration_values"
	},
	service = MVCActionCommand.class
)
public class UpdateConfigurationValuesMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		return _processUpdateConfigurationValues(actionRequest, actionResponse);
	}

	private void _addDefaultEditableValues(
		JSONObject defaultEditableValuesJSONObject,
		JSONObject editableValuesJSONObject) {

		Set<String> fragmentEntryProcessorKeys =
			editableValuesJSONObject.keySet();

		for (String fragmentEntryProcessorKey : fragmentEntryProcessorKeys) {
			JSONObject editableFragmentEntryProcessorJSONObject =
				editableValuesJSONObject.getJSONObject(
					fragmentEntryProcessorKey);

			JSONObject defaultEditableFragmentEntryProcessorJSONObject =
				defaultEditableValuesJSONObject.getJSONObject(
					fragmentEntryProcessorKey);

			if (defaultEditableFragmentEntryProcessorJSONObject == null) {
				defaultEditableValuesJSONObject.put(
					fragmentEntryProcessorKey,
					editableFragmentEntryProcessorJSONObject);

				continue;
			}

			Set<String> editableIds =
				editableFragmentEntryProcessorJSONObject.keySet();

			for (String editableId : editableIds) {
				if (!defaultEditableFragmentEntryProcessorJSONObject.has(
						editableId)) {

					defaultEditableFragmentEntryProcessorJSONObject.put(
						editableId,
						editableFragmentEntryProcessorJSONObject.get(
							editableId));
				}
			}
		}
	}

	private JSONObject _getDefaultEditableValuesJSONObject(
			ActionRequest actionRequest, ActionResponse actionResponse,
			FragmentEntryLink fragmentEntryLink, ThemeDisplay themeDisplay)
		throws Exception {

		JSONObject defaultEditableValuesJSONObject =
			_jsonFactory.createJSONObject();

		JSONObject configurationJSONObject =
			fragmentEntryLink.getConfigurationJSONObject();

		for (Locale locale :
				_getLocales(configurationJSONObject, themeDisplay)) {

			FragmentEntryProcessorContext fragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					fragmentEntryLink.getCompanyId(),
					_portal.getHttpServletRequest(actionRequest),
					_portal.getHttpServletResponse(actionResponse), locale,
					FragmentEntryLinkConstants.EDIT,
					fragmentEntryLink.getGroupId());

			_addDefaultEditableValues(
				defaultEditableValuesJSONObject,
				_fragmentEntryProcessorRegistry.
					getDefaultEditableValuesJSONObject(
						_fragmentEntryProcessorRegistry.
							processFragmentEntryLinkHTML(
								fragmentEntryLink,
								fragmentEntryProcessorContext),
						configurationJSONObject));
		}

		return defaultEditableValuesJSONObject;
	}

	private Collection<Locale> _getLocales(
			JSONObject configurationJSONObject, ThemeDisplay themeDisplay)
		throws Exception {

		Locale locale = _portal.getSiteDefaultLocale(
			themeDisplay.getSiteGroupId());

		if (!ListUtil.exists(
				_fragmentEntryConfigurationParser.
					getFragmentConfigurationFields(configurationJSONObject),
				FragmentConfigurationField::isLocalizable)) {

			return Collections.singletonList(locale);
		}

		Collection<Locale> locales = new LinkedHashSet<>();

		locales.add(locale);

		locales.addAll(
			_language.getAvailableLocales(themeDisplay.getSiteGroupId()));

		return locales;
	}

	private JSONObject _mergeEditableValuesJSONObject(
			JSONObject defaultEditableValuesJSONObject, String editableValues)
		throws Exception {

		return _fragmentEntryLinkManager.mergeEditableValuesJSONObject(
			defaultEditableValuesJSONObject,
			_jsonFactory.createJSONObject(editableValues));
	}

	private JSONObject _processUpdateConfigurationValues(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long fragmentEntryLinkId = ParamUtil.getLong(
			actionRequest, "fragmentEntryLinkId");

		String editableValues = ParamUtil.getString(
			actionRequest, "editableValues");

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkService.updateFragmentEntryLink(
				fragmentEntryLinkId, editableValues);

		JSONObject newEditableValuesJSONObject = _mergeEditableValuesJSONObject(
			_getDefaultEditableValuesJSONObject(
				actionRequest, actionResponse, fragmentEntryLink, themeDisplay),
			editableValues);

		fragmentEntryLink = _fragmentEntryLinkService.updateFragmentEntryLink(
			fragmentEntryLinkId, newEditableValuesJSONObject.toString());

		for (FragmentEntryLinkListener fragmentEntryLinkListener :
				_fragmentEntryLinkListenerRegistry.
					getFragmentEntryLinkListeners()) {

			fragmentEntryLinkListener.
				onUpdateFragmentEntryLinkConfigurationValues(fragmentEntryLink);
		}

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
				fragmentEntryLink.getSegmentsExperienceId());

		return JSONUtil.put(
			"fragmentEntryLink",
			_fragmentEntryLinkManager.getFragmentEntryLinkJSONObject(
				fragmentEntryLink, _portal.getHttpServletRequest(actionRequest),
				_portal.getHttpServletResponse(actionResponse), layoutStructure)
		).put(
			"layoutData", layoutStructure.toJSONObject()
		);
	}

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryLinkManager _fragmentEntryLinkManager;

	@Reference
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}