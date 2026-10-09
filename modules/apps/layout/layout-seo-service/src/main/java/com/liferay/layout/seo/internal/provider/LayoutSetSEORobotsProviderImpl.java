/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.internal.provider;

import com.liferay.layout.seo.contributor.LayoutSetSEORobotsContributor;
import com.liferay.layout.seo.provider.LayoutSetSEORobotsProvider;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.Set;
import java.util.TreeSet;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Amos Fong
 * @author David Truong
 * @author Jesse Rao
 */
@Component(service = LayoutSetSEORobotsProvider.class)
public class LayoutSetSEORobotsProviderImpl
	implements LayoutSetSEORobotsProvider {

	@Override
	public String getRobots(LayoutSet layoutSet, boolean secure)
		throws IOException, PortalException {

		if (layoutSet == null) {
			return StringUtil.read(
				PortalClassLoaderUtil.getClassLoader(),
				PropsValues.ROBOTS_TXT_WITHOUT_SITEMAP);
		}

		String robotsTxt = GetterUtil.getString(
			layoutSet.getSettingsProperty(
				layoutSet.isPrivateLayout() + "-robots.txt"),
			StringUtil.read(
				PortalClassLoaderUtil.getClassLoader(),
				PropsValues.ROBOTS_TXT_WITH_SITEMAP));

		robotsTxt = _replaceWildcards(
			robotsTxt,
			GetterUtil.getString(
				PortalUtil.getDefaultVirtualHostname(true, layoutSet)),
			secure, PortalUtil.getPortalServerPort(secure));

		String robotsContributions = getRobotsContributions(layoutSet);

		if (Validator.isNotNull(robotsContributions)) {
			robotsTxt = StringBundler.concat(
				robotsTxt, "\n\n", robotsContributions);
		}

		return robotsTxt;
	}

	@Override
	public String getRobotsContributions(LayoutSet layoutSet) {
		Set<String> disallowURLEntries = new TreeSet<>();

		for (LayoutSetSEORobotsContributor layoutSetSEORobotsContributor :
				_serviceTrackerList.toList()) {

			Set<String> contributedDisallowURLEntries =
				layoutSetSEORobotsContributor.contributeDisallowURLEntries(
					layoutSet);

			if (SetUtil.isEmpty(contributedDisallowURLEntries)) {
				continue;
			}

			disallowURLEntries.addAll(contributedDisallowURLEntries);
		}

		if (SetUtil.isEmpty(disallowURLEntries)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(
			(disallowURLEntries.size() * 2) + 1);

		sb.append("User-Agent: *");

		for (String disallowURLEntry : disallowURLEntries) {
			sb.append("\nDisallow: ");
			sb.append(disallowURLEntry);
		}

		return sb.toString();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, LayoutSetSEORobotsContributor.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private String _replaceWildcards(
		String robotsTxt, String virtualHostname, boolean secure, int port) {

		if (Validator.isNotNull(virtualHostname)) {
			robotsTxt = StringUtil.replace(
				robotsTxt, "[$HOST$]", virtualHostname);
		}
		else if (_log.isWarnEnabled()) {
			_log.warn(
				"Placeholder [$HOST$] could not be replaced with the actual " +
					"host");
		}

		robotsTxt = StringUtil.replace(
			robotsTxt, "[$PORT$]", String.valueOf(port));

		if (secure) {
			return StringUtil.replace(robotsTxt, "[$PROTOCOL$]", "https");
		}

		return StringUtil.replace(robotsTxt, "[$PROTOCOL$]", "http");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetSEORobotsProviderImpl.class);

	private ServiceTrackerList<LayoutSetSEORobotsContributor>
		_serviceTrackerList;

}