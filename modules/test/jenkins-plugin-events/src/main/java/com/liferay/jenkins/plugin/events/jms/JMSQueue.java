/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.plugin.events.jms;

import com.liferay.jenkins.plugin.events.JenkinsEventsUtil;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Michael Hashimoto
 */
public class JMSQueue {

	public void connect() {
		synchronized (_log) {
			if (_connection != null) {
				return;
			}

			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				_jmsBrokerURL);

			try {
				_connection = connectionFactory.createConnection(
					_userName, _userPassword);

				_connection.start();

				_session = _connection.createSession(
					false, Session.AUTO_ACKNOWLEDGE);

				_queue = _session.createQueue(_queueName);

				if (_log.isInfoEnabled()) {
					_log.info(
						"Connected to " + _jmsBrokerURL + " at " + _queueName);
				}
			}
			catch (JMSException jmsException) {
				throw new RuntimeException(jmsException);
			}
		}
	}

	public void disconnect() {
		synchronized (_log) {
			try {
				if (_messageConsumer != null) {
					_messageConsumer.close();
				}

				if (_connection != null) {
					_connection.close();
				}

				if (_log.isInfoEnabled()) {
					_log.info(
						"Disconnected from " + _jmsBrokerURL + " at " +
							_queueName);
				}
			}
			catch (JMSException jmsException) {
				throw new RuntimeException(jmsException);
			}
			finally {
				_connection = null;
				_messageConsumer = null;
				_queue = null;
				_session = null;
			}
		}
	}

	public String getQueueName() {
		return _queueName;
	}

	public void publish(String message) {
		connect();

		try {
			MessageProducer messageProducer = _session.createProducer(_queue);

			TextMessage textMessage = _session.createTextMessage();

			String masterHostname = JenkinsEventsUtil.getMasterHostname();

			if (masterHostname != null) {
				textMessage.setStringProperty(
					"jenkinsMasterName", masterHostname);
			}

			textMessage.setText(message);

			messageProducer.send(textMessage);
		}
		catch (JMSException jmsException) {
			throw new RuntimeException(jmsException);
		}
	}

	public void setBrokerURL(String jmsBrokerURL) {
		_jmsBrokerURL = jmsBrokerURL;
	}

	public void setQueueName(String queueName) {
		_queueName = queueName;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	public void setUserPassword(String userPassword) {
		_userPassword = userPassword;
	}

	public void subscribe(MessageListener messageListener) {
		connect();

		synchronized (_log) {
			try {
				if (_messageConsumer != null) {
					_messageConsumer.close();
				}

				String masterHostname = JenkinsEventsUtil.getMasterHostname();

				if (masterHostname != null) {
					_messageConsumer = _session.createConsumer(
						_queue,
						"(jenkinsMasterName = '" + masterHostname + "')");
				}
				else {
					_messageConsumer = _session.createConsumer(_queue);
				}

				_messageConsumer.setMessageListener(messageListener);

				if (_log.isInfoEnabled()) {
					_log.info(
						"Subscribed to " + _jmsBrokerURL + " at " + _queueName +
							" for " + masterHostname);
				}
			}
			catch (JMSException jmsException) {
				throw new RuntimeException(jmsException);
			}
		}
	}

	public void unsubscribe() {
		synchronized (_log) {
			if (_messageConsumer == null) {
				return;
			}

			try {
				_messageConsumer.close();

				_messageConsumer = null;

				if (_log.isInfoEnabled()) {
					_log.info(
						"Unsubscribed from " + _jmsBrokerURL + " at " +
							_queueName);
				}
			}
			catch (JMSException jmsException) {
				throw new RuntimeException(jmsException);
			}
		}
	}

	protected JMSQueue(String jmsBrokerURL, String queueName) {
		_jmsBrokerURL = jmsBrokerURL;
		_queueName = queueName;
	}

	private static final Log _log = LogFactory.getLog(JMSQueue.class);

	private Connection _connection;
	private String _jmsBrokerURL;
	private MessageConsumer _messageConsumer;
	private Queue _queue;
	private String _queueName;
	private Session _session;
	private String _userName;
	private String _userPassword;

}