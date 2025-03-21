/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.plugin.events.listener;

import com.liferay.jenkins.plugin.events.JenkinsEventsDescriptor;
import com.liferay.jenkins.plugin.events.JenkinsEventsUtil;

import hudson.Extension;

import hudson.model.Computer;
import hudson.model.TaskListener;

import hudson.slaves.ComputerListener;
import hudson.slaves.OfflineCause;

import jakarta.annotation.Nonnull;

/**
 * @author Michael Hashimoto
 */
@Extension
public class JenkinsComputerListener extends ComputerListener {

	@Override
	public void onOffline(
		@Nonnull Computer computer, OfflineCause offlineCause) {

		JenkinsEventsUtil.publish(
			JenkinsEventsDescriptor.EventType.COMPUTER_OFFLINE, computer);
	}

	@Override
	public void onOnline(Computer computer, TaskListener taskListener) {
		JenkinsEventsUtil.publish(
			JenkinsEventsDescriptor.EventType.COMPUTER_ONLINE, computer);
	}

	@Override
	public void onTemporarilyOffline(
		Computer computer, OfflineCause offlineCause) {

		JenkinsEventsUtil.publish(
			JenkinsEventsDescriptor.EventType.COMPUTER_TEMPORARILY_OFFLINE,
			computer);
	}

	@Override
	public void onTemporarilyOnline(Computer computer) {
		JenkinsEventsUtil.publish(
			JenkinsEventsDescriptor.EventType.COMPUTER_TEMPORARILY_ONLINE,
			computer);
	}

}