/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.engine.html.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.Router;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.engine.BaseWikiEngine;
import com.liferay.wiki.engine.WikiEngine;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.exception.PageContentException;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Zsigmond Rab
 */
@Component(
	configurationPid = "com.liferay.wiki.configuration.WikiGroupServiceConfiguration",
	service = WikiEngine.class
)
public class HtmlEngine extends BaseWikiEngine {

	@Override
	public String getEditorName() {
		return _wikiGroupServiceConfiguration.getHTMLEditor();
	}

	@Override
	public String getFormat() {
		return "html";
	}

	@Override
	public String getHelpPageTitle(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public String getHelpURL() {
		return null;
	}

	@Override
	public Map<String, Boolean> getOutgoingLinks(WikiPage page)
		throws PageContentException {

		try {
			return _getOutgoingLinks(page);
		}
		catch (PortalException portalException) {
			throw new PageContentException(portalException);
		}
	}

	@Override
	public String getToolbarSet() {
		return null;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_wikiGroupServiceConfiguration = ConfigurableUtil.createConfigurable(
			WikiGroupServiceConfiguration.class, properties);

		_friendlyURLMapping =
			Portal.FRIENDLY_URL_SEPARATOR + _friendlyURLMapper.getMapping();

		_router = _friendlyURLMapper.getRouter();
	}

	@Override
	protected ServletContext getEditPageServletContext() {
		return _servletContext;
	}

	@Override
	protected ServletContext getHelpPageServletContext() {
		return null;
	}

	@Override
	protected ResourceBundleLoader getResourceBundleLoader() {
		return ResourceBundleLoaderUtil.getPortalResourceBundleLoader();
	}

	private Map<String, Boolean> _getOutgoingLinks(WikiPage page)
		throws PortalException {

		if (Validator.isNull(page.getContent())) {
			return Collections.emptyMap();
		}

		Map<String, Boolean> links = new HashMap<>();

		Source source = new Source(page.getContent());

		List<StartTag> startTags = source.getAllStartTags("a");

		for (StartTag startTag : startTags) {
			String href = startTag.getAttributeValue("href");

			if (Validator.isNull(href)) {
				continue;
			}

			int pos = href.lastIndexOf(_friendlyURLMapping);

			if (pos == -1) {
				continue;
			}

			String friendlyURL = href.substring(
				pos + _friendlyURLMapping.length());

			if (friendlyURL.endsWith(StringPool.SLASH)) {
				friendlyURL = friendlyURL.substring(
					0, friendlyURL.length() - 1);
			}

			Map<String, String> routeParameters = new HashMap<>();

			if (!_router.urlToParameters(friendlyURL, routeParameters)) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No route could be found to match URL " + friendlyURL);
				}

				continue;
			}

			String title = routeParameters.get("title");
			String nodeName = routeParameters.get("nodeName");

			if (Validator.isNull(title) || Validator.isNull(nodeName)) {
				continue;
			}

			try {
				_wikiNodeLocalService.getNode(page.getGroupId(), nodeName);

				links.put(StringUtil.toLowerCase(title), Boolean.TRUE);
			}
			catch (NoSuchNodeException noSuchNodeException) {
				if (_log.isWarnEnabled()) {
					_log.warn(noSuchNodeException);
				}
			}
		}

		return links;
	}

	private static final Log _log = LogFactoryUtil.getLog(HtmlEngine.class);

	@Reference(target = "(jakarta.portlet.name=" + WikiPortletKeys.WIKI + ")")
	private FriendlyURLMapper _friendlyURLMapper;

	private String _friendlyURLMapping;
	private Router _router;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.wiki.engine.input.editor.common)"
	)
	private ServletContext _servletContext;

	private WikiGroupServiceConfiguration _wikiGroupServiceConfiguration;

	@Reference
	private WikiNodeLocalService _wikiNodeLocalService;

}