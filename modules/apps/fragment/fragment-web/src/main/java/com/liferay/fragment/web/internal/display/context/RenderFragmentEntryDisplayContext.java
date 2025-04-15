/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.upload.UploadRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;

/**
 * @author Jürgen Kappler
 */
public class RenderFragmentEntryDisplayContext {

	public RenderFragmentEntryDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletRequest = liferayPortletRequest;

		_fragmentCollectionContributorRegistry =
			(FragmentCollectionContributorRegistry)
				httpServletRequest.getAttribute(
					FragmentCollectionContributorRegistry.class.getName());
	}

	public DefaultFragmentRendererContext getDefaultFragmentRendererContext()
		throws Exception {

		FragmentEntry fragmentEntry = _getFragmentEntry();

		UploadRequest uploadRequest = _getUploadRequest();

		String css = _readParameter(fragmentEntry, "css", uploadRequest);
		String html = _readParameter(fragmentEntry, "html", uploadRequest);
		String js = _readParameter(fragmentEntry, "js", uploadRequest);

		String configuration = BeanParamUtil.getString(
			fragmentEntry, _httpServletRequest, "configuration");

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryLinkLocalServiceUtil.createFragmentEntryLink(0);

		long fragmentEntryId = 0;

		if (fragmentEntry != null) {
			fragmentEntryId = fragmentEntry.getFragmentEntryId();
		}

		fragmentEntryLink.setFragmentEntryId(fragmentEntryId);

		fragmentEntryLink.setCss(css);
		fragmentEntryLink.setHtml(html);
		fragmentEntryLink.setJs(js);
		fragmentEntryLink.setConfiguration(configuration);
		fragmentEntryLink.setNamespace("namespace");

		String rendererKey = null;

		if ((fragmentEntry != null) && (fragmentEntryId == 0)) {
			rendererKey = fragmentEntry.getFragmentEntryKey();
		}

		fragmentEntryLink.setRendererKey(rendererKey);

		int type = FragmentConstants.TYPE_COMPONENT;

		if (fragmentEntry != null) {
			type = fragmentEntry.getType();
		}

		fragmentEntryLink.setType(type);

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		defaultFragmentRendererContext.setMode(
			FragmentEntryLinkConstants.PREVIEW);
		defaultFragmentRendererContext.setUseCachedContent(false);

		return defaultFragmentRendererContext;
	}

	private FragmentEntry _getFragmentEntry() {
		long fragmentCollectionId = ParamUtil.getLong(
			_httpServletRequest, "fragmentCollectionId");
		long fragmentEntryId = ParamUtil.getLong(
			_httpServletRequest, "fragmentEntryId");
		String fragmentEntryKey = ParamUtil.getString(
			_httpServletRequest, "fragmentEntryKey");

		FragmentEntry fragmentEntry =
			FragmentEntryLocalServiceUtil.fetchFragmentEntry(fragmentEntryId);

		FragmentCollection fragmentCollection =
			FragmentCollectionLocalServiceUtil.fetchFragmentCollection(
				fragmentCollectionId);

		if ((fragmentEntry == null) && (fragmentCollection != null)) {
			fragmentEntry = FragmentEntryLocalServiceUtil.fetchFragmentEntry(
				fragmentCollection.getGroupId(), fragmentEntryKey);
		}

		if (fragmentEntry == null) {
			fragmentEntry =
				_fragmentCollectionContributorRegistry.getFragmentEntry(
					fragmentEntryKey);
		}

		return fragmentEntry;
	}

	private UploadRequest _getUploadRequest() {
		if (_liferayPortletRequest != null) {
			return PortalUtil.getUploadPortletRequest(_liferayPortletRequest);
		}

		return PortalUtil.getUploadServletRequest(_httpServletRequest);
	}

	private String _readParameter(
			FragmentEntry fragmentEntry, String parameterName,
			UploadRequest uploadRequest)
		throws Exception {

		File file = uploadRequest.getFile(parameterName);

		if (file != null) {
			return FileUtil.read(file);
		}

		return BeanParamUtil.getString(
			fragmentEntry, _httpServletRequest, parameterName);
	}

	private final FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;

}