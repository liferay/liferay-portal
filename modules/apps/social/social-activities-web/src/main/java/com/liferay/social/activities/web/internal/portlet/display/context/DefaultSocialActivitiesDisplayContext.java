/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.activities.web.internal.portlet.display.context;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.social.activities.web.internal.helper.SocialActivitiesQueryHelper;
import com.liferay.social.activities.web.internal.portlet.display.context.helper.SocialActivitiesRequestHelper;
import com.liferay.social.kernel.model.SocialActivitySet;

import jakarta.portlet.ResourceURL;

import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Adolfo Pérez
 */
public class DefaultSocialActivitiesDisplayContext
	implements SocialActivitiesDisplayContext {

	public DefaultSocialActivitiesDisplayContext(
		SocialActivitiesRequestHelper socialActivitiesRequestHelper,
		SocialActivitiesQueryHelper socialActivitiesQueryHelper) {

		_socialActivitiesRequestHelper = socialActivitiesRequestHelper;
		_socialActivitiesQueryHelper = socialActivitiesQueryHelper;
	}

	@Override
	public int getMax() {
		return _socialActivitiesRequestHelper.getMax();
	}

	@Override
	public String getPaginationURL() {
		return PortletURLBuilder.createRenderURL(
			_socialActivitiesRequestHelper.getLiferayPortletResponse()
		).setTabs1(
			getSelectedTabName()
		).setParameter(
			"end",
			_socialActivitiesRequestHelper.getEnd() +
				_socialActivitiesRequestHelper.getMax()
		).buildString();
	}

	@Override
	public int getRSSDelta() {
		return _socialActivitiesRequestHelper.getRSSDelta();
	}

	@Override
	public String getRSSDisplayStyle() {
		return _socialActivitiesRequestHelper.getRSSDisplayStyle();
	}

	@Override
	public String getRSSFeedType() {
		return _socialActivitiesRequestHelper.getRSSFeedType();
	}

	@Override
	public ResourceURL getRSSResourceURL() throws PortalException {
		Group group = _socialActivitiesRequestHelper.getScopeGroup();

		String groupDescriptiveName = HtmlUtil.escape(
			group.getDescriptiveName(
				_socialActivitiesRequestHelper.getLocale()));

		String feedTitle = LanguageUtil.format(
			_getResourceBundle(), "x's-activities", groupDescriptiveName,
			false);

		LiferayPortletResponse liferayPortletResponse =
			_socialActivitiesRequestHelper.getLiferayPortletResponse();

		ResourceURL rssURL = liferayPortletResponse.createResourceURL();

		rssURL.setParameter("feedTitle", feedTitle);
		rssURL.setParameter(
			"max",
			String.valueOf(_socialActivitiesRequestHelper.getRSSDelta()));
		rssURL.setResourceID("/social_activities/rss");

		return rssURL;
	}

	@Override
	public String getSelectedTabName() {
		return _socialActivitiesRequestHelper.getTabs1();
	}

	@Override
	public List<SocialActivitySet> getSocialActivitySets() {
		if (_socialActivitySets != null) {
			return _socialActivitySets;
		}

		Group group = _socialActivitiesRequestHelper.getScopeGroup();
		SocialActivitiesQueryHelper.Scope scope =
			SocialActivitiesQueryHelper.Scope.fromValue(getSelectedTabName());

		_socialActivitySets =
			_socialActivitiesQueryHelper.getSocialActivitySets(
				group, _socialActivitiesRequestHelper.getLayout(), scope, 0,
				_socialActivitiesRequestHelper.getEnd());

		return _socialActivitySets;
	}

	@Override
	public String getTabsNames() {
		return "all,connections,following,my-sites,me";
	}

	@Override
	public String getTabsURL() {
		return PortletURLBuilder.createRenderURL(
			_socialActivitiesRequestHelper.getLiferayPortletResponse()
		).setTabs1(
			getSelectedTabName()
		).buildString();
	}

	@Override
	public String getTaglibFeedTitle() throws PortalException {
		return LanguageUtil.get(_getResourceBundle(), "rss");
	}

	@Override
	public boolean isRSSEnabled() {
		return _socialActivitiesRequestHelper.isRSSEnabled();
	}

	@Override
	public boolean isSeeMoreControlVisible() {
		Group group = _socialActivitiesRequestHelper.getScopeGroup();
		SocialActivitiesQueryHelper.Scope scope =
			SocialActivitiesQueryHelper.Scope.fromValue(getSelectedTabName());

		int count = _socialActivitiesQueryHelper.getSocialActivitySetsCount(
			group, _socialActivitiesRequestHelper.getLayout(), scope);

		if (_socialActivitiesRequestHelper.getEnd() < count) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isTabsVisible() {
		Group group = _socialActivitiesRequestHelper.getScopeGroup();

		Layout layout = _socialActivitiesRequestHelper.getLayout();

		if (group.isUser() && layout.isPrivateLayout()) {
			return true;
		}

		return false;
	}

	private ResourceBundle _getResourceBundle() {
		if (_resourceBundle != null) {
			return _resourceBundle;
		}

		ResourceBundleLoader resourceBundleLoader =
			LanguageUtil.getResourceBundleLoader();

		_resourceBundle = resourceBundleLoader.loadResourceBundle(
			_socialActivitiesRequestHelper.getLocale());

		return _resourceBundle;
	}

	private ResourceBundle _resourceBundle;
	private final SocialActivitiesQueryHelper _socialActivitiesQueryHelper;
	private final SocialActivitiesRequestHelper _socialActivitiesRequestHelper;
	private List<SocialActivitySet> _socialActivitySets;

}