package com.liferay.production.readiness;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author lily
 */
@ProviderType
public class Result {

	public enum Status {
		FAIL, PASS
	}

	public enum Severity {
		CRITICAL, HIGH, LOW, MEDIUM
	}

	public Result(
		Status status, Severity severity, String category, String currentValue,
		String recommendedValue, String messageKey, Object[] messageParameters,
		String docsLink) {

		_status = status;
		_severity = severity;
		_category = category;
		_currentValue = currentValue;
		_recommendedValue = recommendedValue;
		_messageKey = messageKey;
		_messageParameters = messageParameters;
		_docsLink = docsLink;
	}

	public String getCategory() {
		return _category;
	}

	public String getCurrentValue() {
		return _currentValue;
	}

	public String getDocsLink() {
		return _docsLink;
	}

	public String getMessageKey() {
		return _messageKey;
	}

	public Object[] getMessageParameters() {
		return _messageParameters;
	}

	public String getRecommendedValue() {
		return _recommendedValue;
	}

	public Severity getSeverity() {
		return _severity;
	}

	public Status getStatus() {
		return _status;
	}

	private final String _category;
	private final String _currentValue;
	private final String _docsLink;
	private final String _messageKey;
	private final Object[] _messageParameters;
	private final String _recommendedValue;
	private final Severity _severity;
	private final Status _status;

}
