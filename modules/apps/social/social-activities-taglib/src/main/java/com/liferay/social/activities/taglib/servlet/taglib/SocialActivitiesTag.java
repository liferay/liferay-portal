/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.activities.taglib.servlet.taglib;

import com.liferay.petra.string.StringPool;
import com.liferay.social.activities.taglib.internal.servlet.ServletContextUtil;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivitySet;
import com.liferay.social.kernel.util.SocialActivityDescriptor;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Raymond Augé
 */
public class SocialActivitiesTag extends IncludeTag {

	public List<SocialActivityDescriptor> getActivityDescriptors() {
		return _activityDescriptors;
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public int getFeedDelta() {
		return _feedDelta;
	}

	public String getFeedDisplayStyle() {
		return _feedDisplayStyle;
	}

	public ResourceURL getFeedResourceURL() {
		return _feedResourceURL;
	}

	public String getFeedTitle() {
		return _feedTitle;
	}

	public String getFeedType() {
		return _feedType;
	}

	public String getFeedURL() {
		return _feedURL;
	}

	public String getFeedURLMessage() {
		return _feedURLMessage;
	}

	public boolean isDisplayRSSFeed() {
		return _displayRSSFeed;
	}

	public boolean isFeedEnabled() {
		return _feedEnabled;
	}

	public void setActivities(List<SocialActivity> activities) {
		List<SocialActivityDescriptor> activityDescriptors = new ArrayList<>(
			activities.size());

		for (SocialActivity activity : activities) {
			activityDescriptors.add(new SocialActivityDescriptor(activity));
		}

		_activityDescriptors = activityDescriptors;
	}

	public void setActivitySets(List<SocialActivitySet> activitySets) {
		List<SocialActivityDescriptor> activityDescriptors = new ArrayList<>(
			activitySets.size());

		for (SocialActivitySet activitySet : activitySets) {
			activityDescriptors.add(new SocialActivityDescriptor(activitySet));
		}

		_activityDescriptors = activityDescriptors;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setDisplayRSSFeed(boolean displayRSSFeed) {
		_displayRSSFeed = displayRSSFeed;
	}

	public void setFeedDelta(int feedDelta) {
		_feedDelta = feedDelta;
	}

	public void setFeedDisplayStyle(String feedDisplayStyle) {
		_feedDisplayStyle = feedDisplayStyle;
	}

	public void setFeedEnabled(boolean feedEnabled) {
		_feedEnabled = feedEnabled;
	}

	public void setFeedResourceURL(ResourceURL feedResourceURL) {
		_feedResourceURL = feedResourceURL;
	}

	public void setFeedTitle(String feedTitle) {
		_feedTitle = feedTitle;
	}

	public void setFeedType(String feedType) {
		_feedType = feedType;
	}

	public void setFeedURL(String feedURL) {
		_feedURL = feedURL;
	}

	public void setFeedURLMessage(String feedURLMessage) {
		_feedURLMessage = feedURLMessage;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_activityDescriptors = null;
		_className = StringPool.BLANK;
		_classPK = 0;
		_displayRSSFeed = false;
		_feedDelta = 0;
		_feedDisplayStyle = null;
		_feedEnabled = false;
		_feedResourceURL = null;
		_feedTitle = null;
		_feedType = null;
		_feedURL = StringPool.BLANK;
		_feedURLMessage = StringPool.BLANK;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-social-activities:social-activities:activityDescriptors",
			_activityDescriptors);
		httpServletRequest.setAttribute(
			"liferay-social-activities:social-activities:className",
			_className);
		httpServletRequest.setAttribute(
			"liferay-social-activities:social-activities:classPK",
			String.valueOf(_classPK));
		httpServletRequest.setAttribute(
			"liferay-social-activities:social-activities:displayRSSFeed",
			String.valueOf(_displayRSSFeed));
		httpServletRequest.setAttribute(
			"liferay-social-activities:social-activities:feedDelta",
			String.valueOf(_feedDelta));
		httpServletRequest.setAttribute(
			"liferay-social-activities:social-activities:feedDisplayStyle",
			_feedDisplayStyle);
		httpServletRequest.setAttribute(
			"liferay-social-activities:social-activities:feedEnabled",
			String.valueOf(_feedEnabled));
		httpServletRequest.setAttribute(
			"liferay-social-activities:social-activities:feedResourceURL",
			_feedResourceURL);
		httpServletRequest.setAttribute(
			"liferay-social-activities:social-activities:feedTitle",
			_feedTitle);
		httpServletRequest.setAttribute(
			"liferay-social-activities:social-activities:feedType", _feedType);
		httpServletRequest.setAttribute(
			"liferay-social-activities:social-activities:feedURL", _feedURL);
		httpServletRequest.setAttribute(
			"liferay-social-activities:social-activities:feedURLMessage",
			_feedURLMessage);
	}

	private static final String _PAGE = "/social_activities/page.jsp";

	private List<SocialActivityDescriptor> _activityDescriptors;
	private String _className = StringPool.BLANK;
	private long _classPK;
	private boolean _displayRSSFeed;
	private int _feedDelta;
	private String _feedDisplayStyle;
	private boolean _feedEnabled;
	private ResourceURL _feedResourceURL;
	private String _feedTitle;
	private String _feedType;
	private String _feedURL = StringPool.BLANK;
	private String _feedURLMessage = StringPool.BLANK;

}