/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.web.internal.portlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.provider.PLOOriginalTranslationProvider;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.language.override.service.PLOEntryService;
import com.liferay.portal.language.override.web.internal.constants.PLOPortletKeys;
import com.liferay.portal.language.override.web.internal.display.context.EditDisplayContextFactory;
import com.liferay.portal.language.override.web.internal.display.context.ViewDisplayContextFactory;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.single-page-application=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Portal Language Override",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + PLOPortletKeys.PORTAL_LANGUAGE_OVERRIDE,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class PLOPortlet extends MVCPortlet {

	public void deletePLOEntries(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortalException {

		for (String key : ParamUtil.getStringValues(actionRequest, "key")) {
			_ploEntryService.deletePLOEntries(key);
		}
	}

	public void deletePLOEntry(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortalException {

		String key = ParamUtil.getString(actionRequest, "key");
		String selectedLanguageId = ParamUtil.getString(
			actionRequest, "selectedLanguageId");

		_ploEntryService.deletePLOEntry(key, selectedLanguageId);
	}

	public void editPLOEntry(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortalException {

		_ploEntryService.setPLOEntries(
			ParamUtil.getString(actionRequest, "key"),
			_localization.getLocalizationMap(actionRequest, "value"));
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		String resourceID = resourceRequest.getResourceID();

		if (resourceID.equals("exportPLOEntries")) {
			_exportPLOEntries(resourceRequest, resourceResponse);
		}
	}

	@Activate
	protected void activate() {
		_editDisplayContextFactory = new EditDisplayContextFactory(
			_ploEntryLocalService, _ploOriginalTranslationProvider, _portal);
		_viewDisplayContextFactory = new ViewDisplayContextFactory(
			_permissionCheckerFactory, _ploEntryLocalService, _portal);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		_setAttributes(renderRequest, renderResponse);

		super.doDispatch(renderRequest, renderResponse);
	}

	private void _exportPLOEntries(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

			Map<String, List<PLOEntry>> keyPLOEntries = new HashMap<>();

			for (PLOEntry ploEntry :
					_ploEntryService.getPLOEntries(
						_portal.getCompanyId(resourceRequest))) {

				List<PLOEntry> ploEntries = keyPLOEntries.computeIfAbsent(
					ploEntry.getLanguageId(), key -> new ArrayList<>());

				ploEntries.add(ploEntry);
			}

			for (Map.Entry<String, List<PLOEntry>> entry :
					keyPLOEntries.entrySet()) {

				StringBundler sb = new StringBundler();

				for (PLOEntry ploEntry : entry.getValue()) {
					sb.append(ploEntry.getKey());
					sb.append(StringPool.EQUAL);
					sb.append(ploEntry.getValue());
					sb.append(StringPool.NEW_LINE);
				}

				zipWriter.addEntry(
					"Language_" + entry.getKey() + ".properties",
					sb.toString());
			}

			PortletResponseUtil.sendFile(
				resourceRequest, resourceResponse,
				StringUtil.randomString() + ".zip",
				new FileInputStream(zipWriter.getFile()),
				ContentTypes.APPLICATION_ZIP);
		}
		catch (IOException | PortalException exception) {
			throw new PortletException(exception);
		}
	}

	private Object _getPortletDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		String path = getPath(renderRequest, renderResponse);

		if ((path == null) || path.equals("/view.jsp")) {
			return _viewDisplayContextFactory.create(
				renderRequest, renderResponse);
		}
		else if (path.equals("/edit_plo_entry.jsp")) {
			return _editDisplayContextFactory.create(renderRequest);
		}

		return null;
	}

	private void _setAttributes(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		Object portletDisplayContext = _getPortletDisplayContext(
			renderRequest, renderResponse);

		if (portletDisplayContext != null) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT, portletDisplayContext);
		}
	}

	private EditDisplayContextFactory _editDisplayContextFactory;

	@Reference
	private Localization _localization;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private PLOEntryLocalService _ploEntryLocalService;

	@Reference
	private PLOEntryService _ploEntryService;

	@Reference
	private PLOOriginalTranslationProvider _ploOriginalTranslationProvider;

	@Reference
	private Portal _portal;

	private ViewDisplayContextFactory _viewDisplayContextFactory;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}