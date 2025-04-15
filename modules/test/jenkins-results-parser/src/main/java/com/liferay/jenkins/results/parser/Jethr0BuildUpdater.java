/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.jethr0.Jethr0Client;
import com.liferay.jenkins.results.parser.jethr0.Jethr0ClientFactory;
import com.liferay.jenkins.results.parser.jethr0.Jethr0MessageListener;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class Jethr0BuildUpdater extends BaseBuildUpdater {

	public String getJenkinsBuildId() {
		return _jenkinsBuildId;
	}

	@Override
	public void invoke() {
		Build build = getBuild();

		_invoke(build.getMaximumSlavesPerHost(), build.getMinimumSlaveRAM());
	}

	public void processMessage(Message message)
		throws JMSException, JSONException {

		if (!_isCompatibleMessage(message)) {
			return;
		}

		TextMessage textMessage = (TextMessage)message;

		JSONObject jsonObject = new JSONObject(textMessage.getText());

		String status = jsonObject.getString("status");

		if (Objects.equals(status, "completed")) {
			_processCompletedBuild(jsonObject);
		}
		else if (Objects.equals(status, "queued")) {
			_processQueuedBuild(jsonObject);
		}
		else if (Objects.equals(status, "running")) {
			_processRunningBuild(jsonObject);
		}
	}

	@Override
	public void reinvoke() {
		Build build = getBuild();

		_invoke(build.getMaximumSlavesPerHost(), 24);
	}

	protected Jethr0BuildUpdater(Build build, long jethr0JobId) {
		super(build);

		_jethr0JobId = jethr0JobId;

		_jenkinsBuildId = jethr0JobId + "__" + build.getBuildName();

		try {
			_jethr0Client = Jethr0ClientFactory.newJethr0Client(
				build.getJenkinsMaster());

			_jethr0MessageListener = Jethr0MessageListener.getInstance(
				_jethr0Client, jethr0JobId);
		}
		catch (Exception exception) {
			exception.printStackTrace();

			throw new RuntimeException(exception);
		}
	}

	@Override
	protected boolean isBuildCompleted() {
		return Objects.equals(_jethr0Status, "completed");
	}

	@Override
	protected boolean isBuildFailing() {
		return !Objects.equals(_jethr0Result, "passed");
	}

	@Override
	protected boolean isBuildQueued() {
		return Objects.equals(_jethr0Status, "queued");
	}

	@Override
	protected boolean isBuildRunning() {
		return Objects.equals(_jethr0Status, "running");
	}

	@Override
	protected void runCompleted() {
		super.runCompleted();

		try {
			_jethr0MessageListener.unsubscribe(this);
		}
		catch (JMSException jmsException) {
			throw new RuntimeException(jmsException);
		}
	}

	@Override
	protected void runQueued() {
		Build build = getBuild();

		build.setStatus("queued");

		if (isBuildRunning()) {
			runRunning();
		}
	}

	@Override
	protected void runStarting() {
		try {
			_jethr0MessageListener.subscribe(this);
		}
		catch (JMSException jmsException) {
			throw new RuntimeException(jmsException);
		}

		_jethr0Result = null;
		_jethr0Status = null;

		super.runStarting();
	}

	private JenkinsMaster _getJenkinsMaster(String buildURL) {
		if (JenkinsResultsParserUtil.isURL(buildURL)) {
			return null;
		}

		Matcher matcher = _jenkinsBuildURLPattern.matcher(buildURL);

		if (!matcher.find()) {
			return null;
		}

		return JenkinsMaster.getInstance(matcher.group("masterHostname"));
	}

	private void _invoke(int maximumSlavesPerHost, int minimumSlaveRAM) {
		Build build = getBuild();

		Map<String, String> buildParameters = new HashMap<>(
			build.getParameters());

		buildParameters.put("JENKINS_BUILD_ID", getJenkinsBuildId());
		buildParameters.put(
			"MAX_NODE_COUNT", String.valueOf(maximumSlavesPerHost));
		buildParameters.put("MIN_NODE_RAM", String.valueOf(minimumSlaveRAM));

		if (_jethr0BuildId > 0) {
			_jethr0Client.createBuildRun(_jethr0BuildId);
		}
		else {
			_jethr0Client.createBuild(
				build.getJobName(), buildParameters, _jethr0JobId,
				build.getBuildName());
		}

		build.addInvocation(new Build.Invocation(build));
	}

	private boolean _isCompatibleMessage(Message message)
		throws JMSException, JSONException {

		TextMessage textMessage = (TextMessage)message;

		if ((_jethr0JobId != textMessage.getLongProperty("jethr0JobId")) ||
			!_jenkinsBuildId.equals(
				textMessage.getStringProperty("jenkinsBuildId"))) {

			return false;
		}

		return true;
	}

	private void _processCompletedBuild(JSONObject jsonObject) {
		String jenkinsBuildURL = jsonObject.getString("jenkinsBuildURL");

		JenkinsMaster jenkinsMaster = _getJenkinsMaster(jenkinsBuildURL);

		Build build = getBuild();

		Build.Invocation buildInvocation = build.getCurrentInvocation();

		buildInvocation.setBuildURL(jenkinsBuildURL);
		buildInvocation.setJenkinsMaster(jenkinsMaster);

		build.setBuildURL(jenkinsBuildURL);
		build.setJenkinsMaster(jenkinsMaster);

		build.saveBuildURLInBuildDatabase();

		_jethr0BuildId = jsonObject.getLong("jethr0BuildId");
		_jethr0Result = jsonObject.getString("result");
		_jethr0Status = "completed";
	}

	private void _processQueuedBuild(JSONObject jsonObject) {
		Build build = getBuild();

		Build.Invocation buildInvocation = build.getCurrentInvocation();

		String jethr0BuildURL = jsonObject.getString("jethr0BuildURL");

		buildInvocation.setBuildURL(jethr0BuildURL);

		build.setBuildURL(jethr0BuildURL);

		build.saveBuildURLInBuildDatabase();

		_jethr0BuildId = jsonObject.getLong("jethr0BuildId");
		_jethr0Status = "queued";
	}

	private void _processRunningBuild(JSONObject jsonObject) {
		Build build = getBuild();

		Build.Invocation buildInvocation = build.getCurrentInvocation();

		String jenkinsBuildURL = jsonObject.getString("jenkinsBuildURL");

		JenkinsMaster jenkinsMaster = _getJenkinsMaster(jenkinsBuildURL);

		buildInvocation.setBuildURL(jenkinsBuildURL);

		buildInvocation.setJenkinsMaster(jenkinsMaster);

		build.setBuildURL(jenkinsBuildURL);

		build.setJenkinsMaster(jenkinsMaster);

		build.saveBuildURLInBuildDatabase();

		_jethr0BuildId = jsonObject.getLong("jethr0BuildId");
		_jethr0Status = "running";
	}

	private static final Pattern _jenkinsBuildURLPattern = Pattern.compile(
		"https?://(?<masterHostname>test-\\d+-\\d+)(.liferay.com)?/.+");

	private final String _jenkinsBuildId;
	private long _jethr0BuildId;
	private final Jethr0Client _jethr0Client;
	private final long _jethr0JobId;
	private final Jethr0MessageListener _jethr0MessageListener;
	private String _jethr0Result;
	private String _jethr0Status;

}