/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.jethr0;

import com.liferay.jenkins.results.parser.JenkinsMaster;

import jakarta.jms.JMSException;
import jakarta.jms.MessageListener;

import java.io.Closeable;
import java.io.IOException;

import java.util.Map;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public interface Jethr0Client extends Closeable {

	public void close() throws IOException;

	public void connect();

	public void createBuild(
		String jenkinsJobName, Map<String, String> jenkinsBuildParameters,
		long jobId);

	public void createBuild(
		String jenkinsJobName, Map<String, String> jenkinsBuildParameters,
		long jobId, String buildName);

	public void createBuildRun(long buildId);

	public Environment getEnvironment();

	public JenkinsMaster getJenkinsMaster();

	public JSONObject getJobJSONObject(long jobId);

	public String liferayDXPRequest(String urlPath);

	public String liferayDXPRequest(String urlPath, String message);

	public void sendGitHubMessageToJethr0(String message);

	public void sendJRPMessageToJethr0(String message);

	public String springBootRequest(String urlPath);

	public String springBootRequest(String urlPath, String message);

	public void subscribe(
			MessageListener messageListener, String messageSelector)
		throws JMSException;

	public void unsubscribe(
			MessageListener messageListener, String messageSelector)
		throws JMSException;

	public enum Environment {

		DEVELOPMENT("dev"), PRODUCTION("prd"), UAT("uat");

		public String getKey() {
			return _key;
		}

		private Environment(String key) {
			_key = key;
		}

		private final String _key;

	}

	public enum EventType {

		CREATE_BUILD, CREATE_BUILD_RUN

	}

}