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

	public static Builder builder() {
		return new Builder();
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

	public static class Builder {

		public Result build() {
			return new Result(
				_status, _severity, _category, _currentValue, _recommendedValue,
				_messageKey, _messageParameters, _docsLink);
		}

		public Builder category(String category) {
			_category = category;

			return this;
		}

		public Builder currentValue(String currentValue) {
			_currentValue = currentValue;

			return this;
		}

		public Builder docsLink(String docsLink) {
			_docsLink = docsLink;

			return this;
		}

		public Builder messageKey(String messageKey) {
			_messageKey = messageKey;

			return this;
		}

		public Builder messageParameters(Object[] messageParameters) {
			_messageParameters = messageParameters;

			return this;
		}

		public Builder recommendedValue(String recommendedValue) {
			_recommendedValue = recommendedValue;

			return this;
		}

		public Builder severity(Severity severity) {
			_severity = severity;

			return this;
		}

		public Builder status(Status status) {
			_status = status;

			return this;
		}

		private String _category;
		private String _currentValue;
		private String _docsLink;
		private String _messageKey;
		private Object[] _messageParameters;
		private String _recommendedValue;
		private Severity _severity;
		private Status _status;

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
