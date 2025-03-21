/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.style.book.constants.StyleBookPortletKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/style_book/render_fragment_entry_link"
	},
	service = MVCResourceCommand.class
)
public class RenderFragmentEntryLinkMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		FragmentEntry fragmentEntry = _getFragmentEntry(resourceRequest);

		if (fragmentEntry == null) {
			return;
		}

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.createFragmentEntryLink(0);

		fragmentEntryLink.setFragmentEntryId(
			fragmentEntry.getFragmentEntryId());
		fragmentEntryLink.setCss(fragmentEntry.getCss());
		fragmentEntryLink.setHtml(fragmentEntry.getHtml());
		fragmentEntryLink.setJs(fragmentEntry.getJs());
		fragmentEntryLink.setConfiguration(fragmentEntry.getConfiguration());
		fragmentEntryLink.setNamespace(PortalUUIDUtil.generate());

		String configurationValues = ParamUtil.get(
			resourceRequest, "configurationValues", StringPool.BLANK);

		if (Validator.isNotNull(configurationValues)) {
			JSONObject configurationValuesJSONObject =
				_jsonFactory.createJSONObject(configurationValues);

			JSONObject editableValuesJSONObject = JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				configurationValuesJSONObject);

			fragmentEntryLink.setEditableValues(
				editableValuesJSONObject.toString());
		}

		fragmentEntryLink.setType(fragmentEntry.getType());

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String languageId = ParamUtil.getString(
			resourceRequest, "languageId", themeDisplay.getLanguageId());

		defaultFragmentRendererContext.setLocale(
			LocaleUtil.fromLanguageId(languageId));

		defaultFragmentRendererContext.setUseCachedContent(false);

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		boolean isolated = themeDisplay.isIsolated();

		themeDisplay.setIsolated(true);

		String content = StringPool.BLANK;

		try {
			content = _fragmentRendererController.render(
				defaultFragmentRendererContext, httpServletRequest,
				_portal.getHttpServletResponse(resourceResponse));
		}
		finally {
			themeDisplay.setIsolated(isolated);
		}

		InputStream inputStream = new ByteArrayInputStream(content.getBytes());

		PortletResponseUtil.write(resourceResponse, inputStream);
	}

	private FragmentEntry _getFragmentEntry(ResourceRequest resourceRequest) {
		long groupId = ParamUtil.getLong(resourceRequest, "groupId");

		String fragmentEntryKey = ParamUtil.getString(
			resourceRequest, "fragmentEntryKey");

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(
				groupId, fragmentEntryKey);

		if (fragmentEntry == null) {
			fragmentEntry =
				_fragmentCollectionContributorRegistry.getFragmentEntry(
					fragmentEntryKey);
		}

		return fragmentEntry;
	}

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private FragmentRendererController _fragmentRendererController;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}