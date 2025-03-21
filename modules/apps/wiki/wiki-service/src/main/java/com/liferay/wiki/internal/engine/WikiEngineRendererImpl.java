/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.engine;

import com.liferay.diff.DiffHtml;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapListener;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.wiki.engine.WikiEngine;
import com.liferay.wiki.engine.WikiEngineRenderer;
import com.liferay.wiki.exception.PageContentException;
import com.liferay.wiki.exception.WikiFormatException;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageDisplay;

import jakarta.portlet.PortletURL;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
@Component(service = WikiEngineRenderer.class)
public class WikiEngineRendererImpl implements WikiEngineRenderer {

	@Override
	public String convert(
			WikiPage page, PortletURL viewPageURL, PortletURL editPageURL,
			String attachmentURLPrefix)
		throws PageContentException, WikiFormatException {

		WikiEngine wikiEngine = fetchWikiEngine(page.getFormat());

		if (wikiEngine == null) {
			throw new WikiFormatException();
		}

		LiferayPortletURL liferayViewPageURL = (LiferayPortletURL)viewPageURL;
		LiferayPortletURL liferayEditPageURL = (LiferayPortletURL)editPageURL;

		String content = wikiEngine.convert(
			page, viewPageURL, editPageURL, attachmentURLPrefix);

		String editPageURLString = StringPool.BLANK;

		if (editPageURL != null) {
			liferayEditPageURL.setParameter("title", "__REPLACEMENT__", false);

			editPageURLString = editPageURL.toString();

			editPageURLString = StringUtil.replace(
				editPageURLString, "__REPLACEMENT__", "$1");
		}

		Matcher matcher = _editPageURLPattern.matcher(content);

		content = _convertURLs(editPageURLString, matcher);

		String viewPageURLString = StringPool.BLANK;

		if (viewPageURL != null) {
			liferayViewPageURL.setParameter("title", "__REPLACEMENT__", false);

			viewPageURLString = viewPageURL.toString();

			viewPageURLString = StringUtil.replace(
				viewPageURLString, "__REPLACEMENT__", "$1");
		}

		matcher = _viewPageURLPattern.matcher(content);

		content = _convertURLs(viewPageURLString, matcher);

		content = _replaceAttachments(
			content, page.getTitle(), attachmentURLPrefix);

		return content;
	}

	@Override
	public String diffHtml(
			WikiPage sourcePage, WikiPage targetPage, PortletURL viewPageURL,
			PortletURL editPageURL, String attachmentURLPrefix)
		throws Exception {

		String sourceContent = StringPool.BLANK;
		String targetContent = StringPool.BLANK;

		if (sourcePage != null) {
			sourceContent = convert(
				sourcePage, viewPageURL, editPageURL, attachmentURLPrefix);
		}

		if (targetPage != null) {
			targetContent = convert(
				targetPage, viewPageURL, editPageURL, attachmentURLPrefix);
		}

		return _diffHtml.diff(
			new UnsyncStringReader(sourceContent),
			new UnsyncStringReader(targetContent));
	}

	@Override
	public WikiEngine fetchWikiEngine(String format) {
		List<WikiEngine> wikiEngines = _serviceTrackerMap.getService(format);

		if (wikiEngines == null) {
			return null;
		}

		return wikiEngines.get(0);
	}

	@Override
	public Collection<String> getFormats() {
		return _serviceTrackerMap.keySet();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_portalCache = _multiVMPool.getPortalCache(
			WikiPageDisplay.class.getName());

		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundleContext, WikiEngine.class, null,
			(serviceReference, emitter) -> {
				WikiEngine wikiEngine = _bundleContext.getService(
					serviceReference);

				try {
					emitter.emit(wikiEngine.getFormat());
				}
				finally {
					_bundleContext.ungetService(serviceReference);
				}
			},
			new ServiceTrackerMapListener
				<String, WikiEngine, List<WikiEngine>>() {

				@Override
				public void keyEmitted(
					ServiceTrackerMap<String, List<WikiEngine>>
						serviceTrackerMap,
					String key, WikiEngine serviceWikiEngine,
					List<WikiEngine> contentWikiEngines) {

					_portalCache.removeAll();
				}

				@Override
				public void keyRemoved(
					ServiceTrackerMap<String, List<WikiEngine>>
						serviceTrackerMap,
					String key, WikiEngine serviceWikiEngine,
					List<WikiEngine> contentWikiEngines) {

					_portalCache.removeAll();
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private String _convertURLs(String url, Matcher matcher) {
		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			String replacement = null;

			if (matcher.groupCount() >= 1) {
				String encodedTitle = URLCodec.encodeURL(
					HtmlUtil.unescape(matcher.group(1)));

				replacement = StringUtil.replace(url, "$1", encodedTitle);
			}
			else {
				replacement = url;
			}

			matcher.appendReplacement(sb, replacement);
		}

		sb = matcher.appendTail(sb);

		return sb.toString();
	}

	private String _replaceAttachments(
		String content, String title, String attachmentURLPrefix) {

		content = StringUtil.replace(content, "[$WIKI_PAGE_NAME$]", title);

		content = StringUtil.replace(
			content, "[$ATTACHMENT_URL_PREFIX$]", attachmentURLPrefix);

		return content;
	}

	private static final Pattern _editPageURLPattern = Pattern.compile(
		"\\[\\$BEGIN_PAGE_TITLE_EDIT\\$\\](.*?)" +
			"\\[\\$END_PAGE_TITLE_EDIT\\$\\]");
	private static final Pattern _viewPageURLPattern = Pattern.compile(
		"\\[\\$BEGIN_PAGE_TITLE\\$\\](.*?)\\[\\$END_PAGE_TITLE\\$\\]");

	private BundleContext _bundleContext;

	@Reference
	private DiffHtml _diffHtml;

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<?, ?> _portalCache;
	private ServiceTrackerMap<String, List<WikiEngine>> _serviceTrackerMap;

}