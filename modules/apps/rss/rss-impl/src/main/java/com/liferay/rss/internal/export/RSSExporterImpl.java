/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.rss.internal.export;

import com.liferay.normalizer.Normalizer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.rss.export.RSSExporter;
import com.liferay.rss.model.SyndContent;
import com.liferay.rss.model.SyndEnclosure;
import com.liferay.rss.model.SyndEntry;
import com.liferay.rss.model.SyndFeed;
import com.liferay.rss.model.SyndLink;

import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEnclosureImpl;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.feed.synd.SyndLinkImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

import java.util.List;

import org.jdom2.IllegalDataException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = RSSExporter.class)
public class RSSExporterImpl implements RSSExporter {

	@Override
	public String export(SyndFeed syndFeed) {
		com.rometools.rome.feed.synd.SyndFeed realSyndFeed = _toRealSyndFeed(
			syndFeed);

		SyndFeedOutput output = new SyndFeedOutput();

		try {
			return output.outputString(realSyndFeed);
		}
		catch (IllegalDataException illegalDataException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalDataException);
			}

			// LEP-4450

			_regexpStrip(realSyndFeed);

			try {
				return output.outputString(realSyndFeed);
			}
			catch (FeedException feedException) {
				throw new SystemException(feedException);
			}
		}
		catch (FeedException feedException) {
			throw new SystemException(feedException);
		}
	}

	private String _regexpStrip(String text) {
		text = _normalizer.normalizeToAscii(text);

		char[] array = text.toCharArray();

		for (int i = 0; i < array.length; i++) {
			String s = String.valueOf(array[i]);

			if (s.matches(_REGEXP_STRIP)) {
				continue;
			}

			array[i] = CharPool.SPACE;
		}

		return new String(array);
	}

	private void _regexpStrip(com.rometools.rome.feed.synd.SyndFeed syndFeed) {
		syndFeed.setTitle(_regexpStrip(syndFeed.getTitle()));
		syndFeed.setDescription(_regexpStrip(syndFeed.getDescription()));

		@SuppressWarnings("unchecked")
		List<com.rometools.rome.feed.synd.SyndEntry> syndEntries =
			syndFeed.getEntries();

		for (com.rometools.rome.feed.synd.SyndEntry syndEntry : syndEntries) {
			syndEntry.setTitle(_regexpStrip(syndEntry.getTitle()));

			com.rometools.rome.feed.synd.SyndContent syndContent =
				syndEntry.getDescription();

			syndContent.setValue(_regexpStrip(syndContent.getValue()));
		}
	}

	private com.rometools.rome.feed.synd.SyndContent _toRealSyncContent(
		SyndContent syndContent) {

		com.rometools.rome.feed.synd.SyndContent realSyndContent =
			new SyndContentImpl();

		realSyndContent.setType(syndContent.getType());
		realSyndContent.setValue(syndContent.getValue());

		return realSyndContent;
	}

	private List<com.rometools.rome.feed.synd.SyndEnclosure>
		_toRealSyndEnclosures(List<SyndEnclosure> syndEnclosures) {

		if (syndEnclosures == null) {
			return null;
		}

		return TransformUtil.transform(
			syndEnclosures,
			syndEnclosure -> {
				com.rometools.rome.feed.synd.SyndEnclosure realSyndEnclosure =
					new SyndEnclosureImpl();

				realSyndEnclosure.setLength(syndEnclosure.getLength());
				realSyndEnclosure.setType(syndEnclosure.getType());
				realSyndEnclosure.setUrl(syndEnclosure.getUrl());

				return realSyndEnclosure;
			});
	}

	private List<com.rometools.rome.feed.synd.SyndEntry> _toRealSyndEntries(
		List<SyndEntry> syndEntries) {

		if (syndEntries == null) {
			return null;
		}

		return TransformUtil.transform(
			syndEntries,
			syndEntry -> {
				com.rometools.rome.feed.synd.SyndEntry realSyndEntry =
					new SyndEntryImpl();

				realSyndEntry.setAuthor(syndEntry.getAuthor());
				realSyndEntry.setDescription(
					_toRealSyncContent(syndEntry.getDescription()));
				realSyndEntry.setEnclosures(
					_toRealSyndEnclosures(syndEntry.getEnclosures()));
				realSyndEntry.setLink(syndEntry.getLink());
				realSyndEntry.setLinks(_toRealSyndLinks(syndEntry.getLinks()));
				realSyndEntry.setPublishedDate(syndEntry.getPublishedDate());
				realSyndEntry.setTitle(syndEntry.getTitle());
				realSyndEntry.setUpdatedDate(syndEntry.getUpdatedDate());
				realSyndEntry.setUri(syndEntry.getUri());

				return realSyndEntry;
			});
	}

	private com.rometools.rome.feed.synd.SyndFeed _toRealSyndFeed(
		SyndFeed syndFeed) {

		com.rometools.rome.feed.synd.SyndFeed realSyndFeed = new SyndFeedImpl();

		realSyndFeed.setDescription(syndFeed.getDescription());
		realSyndFeed.setEntries(_toRealSyndEntries(syndFeed.getEntries()));
		realSyndFeed.setFeedType(syndFeed.getFeedType());
		realSyndFeed.setLinks(_toRealSyndLinks(syndFeed.getLinks()));
		realSyndFeed.setPublishedDate(syndFeed.getPublishedDate());
		realSyndFeed.setTitle(syndFeed.getTitle());
		realSyndFeed.setUri(syndFeed.getUri());

		return realSyndFeed;
	}

	private List<com.rometools.rome.feed.synd.SyndLink> _toRealSyndLinks(
		List<SyndLink> syndLinks) {

		if (syndLinks == null) {
			return null;
		}

		return TransformUtil.transform(
			syndLinks,
			syndLink -> {
				com.rometools.rome.feed.synd.SyndLink realSyndLink =
					new SyndLinkImpl();

				realSyndLink.setHref(syndLink.getHref());
				realSyndLink.setLength(syndLink.getLength());
				realSyndLink.setRel(syndLink.getRel());
				realSyndLink.setType(syndLink.getType());

				return realSyndLink;
			});
	}

	private static final String _REGEXP_STRIP = "[\\d\\w]";

	private static final Log _log = LogFactoryUtil.getLog(
		RSSExporterImpl.class);

	@Reference
	private Normalizer _normalizer;

}