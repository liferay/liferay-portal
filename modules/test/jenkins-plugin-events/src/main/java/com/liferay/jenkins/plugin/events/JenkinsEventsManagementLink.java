/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.plugin.events;

import hudson.Extension;

import hudson.model.ManagementLink;

import hudson.security.Permission;

import jakarta.servlet.ServletException;

import java.io.IOException;

import jenkins.model.Jenkins;

import org.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * @author Michael Hashimoto
 */
@Extension
public class JenkinsEventsManagementLink extends ManagementLink {

	public JenkinsEventsManagementLink() {
		jenkinsEventsDescriptor = new JenkinsEventsDescriptor();

		jenkinsEventsDescriptor.load();
	}

	public void doJenkinsEventsConfiguration(
			StaplerRequest staplerRequest, StaplerResponse staplerResponse)
		throws IOException, ServletException {

		JSONObject jsonObject = new JSONObject(
			staplerRequest.getParameter("json"));

		jenkinsEventsDescriptor.setInboundQueueName(
			jsonObject.getString("inboundQueueName"));
		jenkinsEventsDescriptor.setOutboundQueueName(
			jsonObject.getString("outboundQueueName"));
		jenkinsEventsDescriptor.setUrl(jsonObject.getString("url"));
		jenkinsEventsDescriptor.setUserName(jsonObject.getString("userName"));
		jenkinsEventsDescriptor.setUserPassword(
			jsonObject.getString("userPassword"));

		jenkinsEventsDescriptor.clearEventTypes();

		for (JenkinsEventsDescriptor.EventType eventType :
				JenkinsEventsDescriptor.EventType.values()) {

			if (jsonObject.optBoolean(eventType.toString())) {
				jenkinsEventsDescriptor.addEventType(eventType);
			}
		}

		jenkinsEventsDescriptor.save();

		JenkinsEventsUtil.setJenkinsEventsDescriptor(jenkinsEventsDescriptor);

		jenkinsEventsDescriptor.subscribe();

		Jenkins jenkins = Jenkins.getInstanceOrNull();

		if (jenkins != null) {
			staplerResponse.sendRedirect(jenkins.getRootUrl());
		}
	}

	@Override
	public String getDescription() {
		return "Configure Jenkins event listeners and publishers";
	}

	@Override
	public String getDisplayName() {
		return "Jenkins Events";
	}

	@Override
	public String getIconFileName() {
		return "clipboard.png";
	}

	@Override
	public Permission getRequiredPermission() {
		return Jenkins.ADMINISTER;
	}

	@Override
	public String getUrlName() {
		return "jenkins-events-configuration";
	}

	public JenkinsEventsDescriptor jenkinsEventsDescriptor;

}