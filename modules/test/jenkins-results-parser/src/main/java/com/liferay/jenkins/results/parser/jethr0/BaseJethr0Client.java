/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.jethr0;

import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseJethr0Client implements Jethr0Client {

	@Override
	public synchronized void close() throws IOException {
		if (_connection == null) {
			return;
		}

		synchronized (_messageConsumers) {
			try {
				for (MessageConsumer messageConsumer :
						_messageConsumers.values()) {

					if (messageConsumer != null) {
						messageConsumer.close();
					}
				}

				if (_connection != null) {
					_connection.close();
				}
			}
			catch (JMSException jmsException) {
				throw new IOException(jmsException);
			}
			finally {
				_connection = null;
				_messageConsumers.clear();
			}
		}
	}

	@Override
	public synchronized void connect() {
		if (_connection != null) {
			return;
		}

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
			getJMSBrokerURL());

		try {
			_connection = connectionFactory.createConnection(
				getJMSUserName(), getJMSUserPassword());

			_connection.start();
		}
		catch (JMSException jmsException) {
			throw new RuntimeException(jmsException);
		}
	}

	@Override
	public void createBuild(
		String jenkinsJobName, Map<String, String> jenkinsBuildParameters,
		long jobId) {

		createBuild(
			jenkinsJobName, jenkinsBuildParameters, jobId, jenkinsJobName);
	}

	@Override
	public void createBuild(
		String jenkinsJobName, Map<String, String> jenkinsBuildParameters,
		long jobId, String buildName) {

		JSONObject jobJSONObject = new JSONObject();

		jobJSONObject.put("id", Long.valueOf(jobId));

		JSONArray parametersJSONArray = new JSONArray();

		Set<String> parameterNames = new TreeSet<>(
			jenkinsBuildParameters.keySet());

		for (String parameterName : parameterNames) {
			if (!parameterName.matches("[A-Z0-9_]+")) {
				continue;
			}

			String parameterValue = jenkinsBuildParameters.get(parameterName);

			if (JenkinsResultsParserUtil.isNullOrEmpty(parameterValue)) {
				continue;
			}

			JSONObject parameterJSONObject = new JSONObject();

			parameterJSONObject.put(
				"name", parameterName
			).put(
				"value", parameterValue
			);

			parametersJSONArray.put(parameterJSONObject);
		}

		JSONObject buildJSONObject = new JSONObject();

		if (JenkinsResultsParserUtil.isNullOrEmpty(buildName)) {
			buildName = jenkinsJobName;
		}

		buildJSONObject.put(
			"jenkinsJobName", jenkinsJobName
		).put(
			"name", buildName
		).put(
			"parameters", parametersJSONArray.toString()
		);

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"build", buildJSONObject
		).put(
			"eventType", EventType.CREATE_BUILD
		).put(
			"job", jobJSONObject
		);

		sendJRPMessageToJethr0(jsonObject.toString());
	}

	@Override
	public void createBuildRun(long buildId) {
		JSONObject buildJSONObject = new JSONObject();

		buildJSONObject.put("id", buildId);

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"build", buildJSONObject
		).put(
			"eventType", EventType.CREATE_BUILD_RUN
		);

		sendJRPMessageToJethr0(jsonObject.toString());
	}

	@Override
	public Environment getEnvironment() {
		return _environment;
	}

	@Override
	public JenkinsMaster getJenkinsMaster() {
		return _jenkinsMaster;
	}

	@Override
	public JSONObject getJobJSONObject(long jobId) {
		return new JSONObject(
			springBootRequest(getSpringBootURL() + "/jobs/" + jobId, null));
	}

	@Override
	public String liferayDXPRequest(String urlPath) {
		return _requestLiferayDXPMessage(
			urlPath, null, JenkinsResultsParserUtil.HttpRequestMethod.GET);
	}

	@Override
	public String liferayDXPRequest(String urlPath, String message) {
		return _requestLiferayDXPMessage(
			urlPath, message, JenkinsResultsParserUtil.HttpRequestMethod.POST);
	}

	@Override
	public void sendGitHubMessageToJethr0(String message) {
		connect();

		try {
			Session session = _connection.createSession(
				false, Session.AUTO_ACKNOWLEDGE);

			Queue queue = session.createQueue(getJMSGitHubToJethr0QueueName());

			MessageProducer messageProducer = session.createProducer(queue);

			messageProducer.send(session.createTextMessage(message));
		}
		catch (JMSException jmsException) {
			throw new RuntimeException(jmsException);
		}
	}

	@Override
	public void sendJRPMessageToJethr0(String message) {
		connect();

		try {
			Session session = _connection.createSession(
				false, Session.AUTO_ACKNOWLEDGE);

			Queue queue = session.createQueue(getJMSJRPToJethr0QueueName());

			MessageProducer messageProducer = session.createProducer(queue);

			TextMessage textMessage = session.createTextMessage(message);

			textMessage.setStringProperty(
				"jenkinsMasterName", _jenkinsMaster.getName());

			messageProducer.send(textMessage);
		}
		catch (JMSException jmsException) {
			throw new RuntimeException(jmsException);
		}
	}

	@Override
	public String springBootRequest(String urlPath) {
		return _requestSpringBootMessage(
			urlPath, null, JenkinsResultsParserUtil.HttpRequestMethod.GET);
	}

	@Override
	public String springBootRequest(String urlPath, String message) {
		return _requestSpringBootMessage(
			urlPath, message, JenkinsResultsParserUtil.HttpRequestMethod.POST);
	}

	@Override
	public void subscribe(
			MessageListener messageListener, String messageSelector)
		throws JMSException {

		synchronized (_messageConsumers) {
			if (_messageConsumers.containsKey(messageSelector)) {
				return;
			}

			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				getJMSBrokerURL());

			Connection connection = connectionFactory.createConnection(
				getJMSUserName(), getJMSUserPassword());

			connection.start();

			Session session = connection.createSession(
				false, Session.AUTO_ACKNOWLEDGE);

			Queue queue = session.createQueue(getJMSJethr0ToJRPQueueName());

			MessageConsumer messageConsumer = session.createConsumer(
				queue, messageSelector);

			messageConsumer.setMessageListener(messageListener);

			_messageConsumers.put(messageSelector, messageConsumer);

			System.out.println(
				"Subscribed with selector \"" + messageSelector + "\"");
		}
	}

	@Override
	public void unsubscribe(
			MessageListener messageListener, String messageSelector)
		throws JMSException {

		synchronized (_messageConsumers) {
			if (!_messageConsumers.containsKey(messageSelector)) {
				return;
			}

			MessageConsumer messageConsumer = _messageConsumers.get(
				messageSelector);

			if (messageConsumer == null) {
				return;
			}

			messageConsumer.close();

			_messageConsumers.remove(messageSelector);

			System.out.println(
				"Unsubscribed with selector \"" + messageSelector + "\"");
		}
	}

	protected BaseJethr0Client(JenkinsMaster jenkinsMaster) {
		_jenkinsMaster = jenkinsMaster;

		_environment = _getEnvironment();
	}

	protected String getBuildPropertyString(String buildPropertyName) {
		Environment environment = getEnvironment();

		try {
			return JenkinsResultsParserUtil.getBuildProperty(
				buildPropertyName, environment.getKey());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected URL getBuildPropertyURL(String buildPropertyName) {
		try {
			return new URL(getBuildPropertyString(buildPropertyName));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	protected abstract String getJMSBrokerURL();

	protected abstract String getJMSGitHubToJethr0QueueName();

	protected abstract String getJMSJethr0ToJRPQueueName();

	protected abstract String getJMSJRPToJethr0QueueName();

	protected abstract String getJMSUserName();

	protected abstract String getJMSUserPassword();

	protected abstract URL getLiferayDXPURL();

	protected abstract String getOAuthClientSecret();

	protected abstract String getOAuthExternalReferenceCode();

	protected abstract URL getSpringBootURL();

	private Environment _getEnvironment() {
		try {
			Properties buildProperties =
				JenkinsResultsParserUtil.getBuildProperties();

			for (Environment environment : Environment.values()) {
				String jenkinsMasterNames =
					JenkinsResultsParserUtil.getProperty(
						buildProperties,
						"jethr0.jenkins.masters[" + environment.getKey() + "]");

				if (JenkinsResultsParserUtil.isNullOrEmpty(
						jenkinsMasterNames)) {

					continue;
				}

				for (String jenkinsMasterName : jenkinsMasterNames.split(",")) {
					if (jenkinsMasterName.equals(_jenkinsMaster.getName())) {
						return environment;
					}
				}
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return Environment.DEVELOPMENT;
	}

	private String _getOAuthAccessToken() {
		if (_oAuthAccessToken != null) {
			return _oAuthAccessToken;
		}

		try {
			JSONObject jsonObject = JenkinsResultsParserUtil.toJSONObject(
				JenkinsResultsParserUtil.combine(
					String.valueOf(getLiferayDXPURL()),
					"/o/oauth2/token?client_id=", _getOAuthClientId(),
					"&client_secret=", getOAuthClientSecret(),
					"&grant_type=client_credentials"),
				false, JenkinsResultsParserUtil.HttpRequestMethod.POST);

			_oAuthAccessToken = jsonObject.getString("access_token");
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return _oAuthAccessToken;
	}

	private String _getOAuthClientId() {
		if (_oAuthClientId != null) {
			return _oAuthClientId;
		}

		try {
			JSONObject jsonObject = JenkinsResultsParserUtil.toJSONObject(
				JenkinsResultsParserUtil.combine(
					String.valueOf(getLiferayDXPURL()),
					"/o/oauth2/application?externalReferenceCode=",
					getOAuthExternalReferenceCode()));

			_oAuthClientId = jsonObject.getString("client_id");
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return _oAuthClientId;
	}

	private JenkinsResultsParserUtil.HTTPAuthorization
		_getSpringBootHTTPAuthorization() {

		return new JenkinsResultsParserUtil.BearerHTTPAuthorization(
			_getOAuthAccessToken());
	}

	private String _requestLiferayDXPMessage(
		String urlPath, String message,
		JenkinsResultsParserUtil.HttpRequestMethod httpRequestMethod) {

		try {
			return JenkinsResultsParserUtil.toString(
				getLiferayDXPURL() + "/" + urlPath, false, httpRequestMethod,
				message, _getSpringBootHTTPAuthorization());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private String _requestSpringBootMessage(
		String urlPath, String message,
		JenkinsResultsParserUtil.HttpRequestMethod httpRequestMethod) {

		try {
			return JenkinsResultsParserUtil.toString(
				getSpringBootURL() + "/" + urlPath, false, httpRequestMethod,
				message, _getSpringBootHTTPAuthorization());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private Connection _connection;
	private final Environment _environment;
	private final JenkinsMaster _jenkinsMaster;
	private final Map<String, MessageConsumer> _messageConsumers =
		new HashMap<>();
	private String _oAuthAccessToken;
	private String _oAuthClientId;

}