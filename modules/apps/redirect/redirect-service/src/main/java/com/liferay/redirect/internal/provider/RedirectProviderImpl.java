/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.internal.provider;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.redirect.constants.RedirectConstants;
import com.liferay.redirect.internal.configuration.RedirectPatternConfiguration;
import com.liferay.redirect.internal.util.PatternUtil;
import com.liferay.redirect.matcher.UserAgentMatcher;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.model.RedirectPatternEntry;
import com.liferay.redirect.provider.RedirectProvider;
import com.liferay.redirect.service.RedirectEntryLocalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = RedirectProvider.class)
public class RedirectProviderImpl implements RedirectProvider {

	@Override
	public Redirect getRedirect(
		long groupId, String friendlyURL, String fullURL, String userAgent) {

		if (friendlyURL.contains("/control_panel/manage")) {
			return null;
		}

		RedirectEntry redirectEntry =
			_redirectEntryLocalService.fetchRedirectEntry(
				groupId, fullURL, false);

		if (redirectEntry == null) {
			redirectEntry = _redirectEntryLocalService.fetchRedirectEntry(
				groupId, friendlyURL, true);
		}

		if (redirectEntry != null) {
			return new RedirectImpl(
				redirectEntry.getDestinationURL(), redirectEntry.isPermanent());
		}

		List<RedirectPatternEntry> redirectPatternEntries =
			_redirectPatternEntries.getOrDefault(
				groupId, Collections.emptyList());

		for (RedirectPatternEntry redirectPatternEntry :
				redirectPatternEntries) {

			if (_isUserAgentMatch(redirectPatternEntry, userAgent)) {
				Pattern pattern = redirectPatternEntry.getPattern();

				Matcher matcher = pattern.matcher(friendlyURL);

				if (matcher.matches()) {
					return new RedirectImpl(
						matcher.replaceFirst(
							redirectPatternEntry.getDestinationURL()),
						false);
				}
			}
		}

		return null;
	}

	@Override
	public List<RedirectPatternEntry> getRedirectPatternEntries(long groupId) {
		List<RedirectPatternEntry> redirectPatternEntries =
			_redirectPatternEntries.get(groupId);

		if (redirectPatternEntries != null) {
			return redirectPatternEntries;
		}

		return new ArrayList<>();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistration = bundleContext.registerService(
			ManagedServiceFactory.class,
			new RedirectProviderManagedServiceFactory(),
			HashMapDictionaryBuilder.put(
				Constants.SERVICE_PID,
				"com.liferay.redirect.internal.configuration." +
					"RedirectPatternConfiguration.scoped"
			).build());
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	protected void setCrawlerUserAgentsMatcher(
		UserAgentMatcher userAgentMatcher) {

		_userAgentMatcher = userAgentMatcher;
	}

	protected void setRedirectEntryLocalService(
		RedirectEntryLocalService redirectEntryLocalService) {

		_redirectEntryLocalService = redirectEntryLocalService;
	}

	protected void setRedirectPatternEntries(
		Map<Long, List<RedirectPatternEntry>> redirectPatternEntries) {

		_redirectPatternEntries = redirectPatternEntries;
	}

	private boolean _isUserAgentMatch(
		RedirectPatternEntry redirectPatternEntry, String userAgent) {

		if (Validator.isNull(redirectPatternEntry.getUserAgent()) ||
			Validator.isNull(userAgent) ||
			Objects.equals(
				RedirectConstants.USER_AGENT_ALL,
				redirectPatternEntry.getUserAgent())) {

			return true;
		}

		boolean crawlerUserAgent = _userAgentMatcher.isCrawlerUserAgent(
			userAgent);

		if (crawlerUserAgent &&
			Objects.equals(
				RedirectConstants.USER_AGENT_BOT,
				redirectPatternEntry.getUserAgent())) {

			return true;
		}

		if (!crawlerUserAgent &&
			Objects.equals(
				RedirectConstants.USER_AGENT_HUMAN,
				redirectPatternEntry.getUserAgent())) {

			return true;
		}

		return false;
	}

	private void _unmapPid(String pid) {
		Long groupId = _groupIds.remove(pid);

		if (groupId != null) {
			_redirectPatternEntries.remove(groupId);
		}
	}

	private final Map<String, Long> _groupIds = new ConcurrentHashMap<>();

	@Reference
	private RedirectEntryLocalService _redirectEntryLocalService;

	private Map<Long, List<RedirectPatternEntry>> _redirectPatternEntries =
		new ConcurrentHashMap<>();
	private ServiceRegistration<ManagedServiceFactory> _serviceRegistration;

	@Reference
	private UserAgentMatcher _userAgentMatcher;

	private static class RedirectImpl implements Redirect {

		public RedirectImpl(String destinationURL, boolean permanent) {
			_destinationURL = destinationURL;
			_permanent = permanent;
		}

		@Override
		public String getDestinationURL() {
			return _destinationURL;
		}

		@Override
		public boolean isPermanent() {
			return _permanent;
		}

		private final String _destinationURL;
		private final boolean _permanent;

	}

	private class RedirectProviderManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			_unmapPid(pid);
		}

		@Override
		public String getName() {
			return "com.liferay.redirect.internal.configuration." +
				"RedirectPatternConfiguration.scoped";
		}

		@Override
		public void updated(String pid, Dictionary<String, ?> dictionary)
			throws ConfigurationException {

			_unmapPid(pid);

			long groupId = GetterUtil.getLong(
				dictionary.get("groupId"),
				GroupConstants.DEFAULT_PARENT_GROUP_ID);

			if (groupId == GroupConstants.DEFAULT_PARENT_GROUP_ID) {
				return;
			}

			_groupIds.put(pid, groupId);

			RedirectPatternConfiguration redirectPatternConfiguration =
				ConfigurableUtil.createConfigurable(
					RedirectPatternConfiguration.class, dictionary);

			_redirectPatternEntries.put(
				groupId,
				PatternUtil.parse(
					redirectPatternConfiguration.patternStrings()));
		}

	}

}