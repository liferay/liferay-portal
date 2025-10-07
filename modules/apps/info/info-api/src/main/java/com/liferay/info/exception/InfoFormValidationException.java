/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.exception;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.language.LanguageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Lourdes Fernández Besada
 */
public class InfoFormValidationException extends InfoFormException {

	public InfoFormValidationException() {
		_infoFieldUniqueId = StringPool.BLANK;
	}

	public InfoFormValidationException(String infoFieldUniqueId) {
		_infoFieldUniqueId = infoFieldUniqueId;
	}

	public String getInfoFieldUniqueId() {
		return _infoFieldUniqueId;
	}

	public String getLocalizedMessage(String fieldLabel, Locale locale) {
		return LanguageUtil.format(
			locale, "x-an-error-occurred", fieldLabel, false);
	}

	public static class CustomValidation extends InfoFormValidationException {

		public CustomValidation(String infoFieldUniqueId, String message) {
			super(infoFieldUniqueId);

			_message = message;
		}

		@Override
		public String getLocalizedMessage(Locale locale) {
			if (_message != null) {
				return _message;
			}

			return super.getLocalizedMessage(locale);
		}

		private final String _message;

	}

	public static class ExceedsMaxEntries extends InfoFormValidationException {

		public ExceedsMaxEntries(String infoFormLabel, String messageKey) {
			_infoFormLabel = infoFormLabel;
			_messageKey = messageKey;
		}

		@Override
		public String getLocalizedMessage(Locale locale) {
			return LanguageUtil.format(
				locale, _messageKey, new String[] {_infoFormLabel}, false);
		}

		@Override
		public String getLocalizedMessage(String fieldLabel, Locale locale) {
			return getLocalizedMessage(locale);
		}

		private final String _infoFormLabel;
		private final String _messageKey;

	}

	public static class ExceedsMaxLength extends InvalidInfoFieldValue {

		public ExceedsMaxLength(String infoFieldUniqueId, int maxLength) {
			super(infoFieldUniqueId);

			_maxLength = maxLength;
		}

		@Override
		public String getLocalizedMessage(Locale locale) {
			return LanguageUtil.format(
				locale, "value-exceeds-maximum-length-of-x", _maxLength);
		}

		@Override
		public String getLocalizedMessage(String fieldLabel, Locale locale) {
			return LanguageUtil.format(
				locale, "value-exceeds-maximum-length-of-x-for-field-x",
				new String[] {String.valueOf(_maxLength), fieldLabel}, false);
		}

		private final int _maxLength;

	}

	public static class ExceedsMaxValue extends InvalidInfoFieldValue {

		public ExceedsMaxValue(String infoFieldUniqueId, long maxValue) {
			super(infoFieldUniqueId);

			_maxValue = maxValue;
		}

		@Override
		public String getLocalizedMessage(Locale locale) {
			return LanguageUtil.get(locale, "value-exceeds-maximum-value-of-x");
		}

		@Override
		public String getLocalizedMessage(String fieldLabel, Locale locale) {
			return LanguageUtil.format(
				locale, "value-exceeds-maximum-value-of-x-for-field-x",
				new String[] {String.valueOf(_maxValue), fieldLabel}, false);
		}

		private final long _maxValue;

	}

	public static class ExceedsMinValue extends InvalidInfoFieldValue {

		public ExceedsMinValue(String infoFieldUniqueId, long minValue) {
			super(infoFieldUniqueId);

			_minValue = minValue;
		}

		@Override
		public String getLocalizedMessage(Locale locale) {
			return LanguageUtil.get(
				locale, "value-falls-bellow-the-minimum-value-of-x");
		}

		@Override
		public String getLocalizedMessage(String fieldLabel, Locale locale) {
			return LanguageUtil.format(
				locale, "value-falls-bellow-the-minimum-value-of-x-for-field-x",
				new String[] {String.valueOf(_minValue), fieldLabel}, false);
		}

		private final long _minValue;

	}

	public static class FileSize extends InfoFormValidationException {

		public FileSize(String infoFieldUniqueId, String maximumSizeAllowed) {
			super(infoFieldUniqueId);

			_maximumSizeAllowed = maximumSizeAllowed;
		}

		@Override
		public String getLocalizedMessage(Locale locale) {
			return LanguageUtil.format(
				locale,
				"file-size-is-larger-than-the-allowed-overall-maximum-upload-" +
					"request-size-x",
				_maximumSizeAllowed);
		}

		@Override
		public String getLocalizedMessage(String fieldLabel, Locale locale) {
			return LanguageUtil.format(
				locale,
				"x-file-size-is-larger-than-the-allowed-maximum-upload-size-x",
				new String[] {fieldLabel, _maximumSizeAllowed}, false);
		}

		public String getMaximumSizeAllowed() {
			return _maximumSizeAllowed;
		}

		private final String _maximumSizeAllowed;

	}

	public static class InvalidCaptcha extends InfoFormValidationException {

		public InvalidCaptcha(
			CaptchaException captchaException, long fragmentEntryLinkId) {

			_captchaException = captchaException;
			_fragmentEntryLinkId = fragmentEntryLinkId;
		}

		public CaptchaException getCaptchaException() {
			return _captchaException;
		}

		public long getFragmentEntryLinkId() {
			return _fragmentEntryLinkId;
		}

		@Override
		public String getLocalizedMessage(Locale locale) {
			return LanguageUtil.get(locale, "captcha-verification-failed");
		}

		@Override
		public String getLocalizedMessage(String fieldLabel, Locale locale) {
			return getLocalizedMessage(locale);
		}

		private final CaptchaException _captchaException;
		private final long _fragmentEntryLinkId;

	}

	public static class InvalidExpirationDate extends InvalidInfoFieldValue {

		public InvalidExpirationDate(
			String infoFieldUniqueId, String messageKey) {

			super(infoFieldUniqueId);

			_messageKey = messageKey;
		}

		@Override
		public String getLocalizedMessage(Locale locale) {
			return LanguageUtil.get(locale, _messageKey);
		}

		private final String _messageKey;

	}

	public static class InvalidFileExtension
		extends InfoFormValidationException {

		public InvalidFileExtension(
			String infoFieldUniqueId, String validFileExtensions) {

			super(infoFieldUniqueId);

			_validFileExtensions = validFileExtensions;
		}

		@Override
		public String getLocalizedMessage(Locale locale) {
			return LanguageUtil.format(
				locale, "please-enter-a-file-with-a-valid-extension-x",
				_validFileExtensions);
		}

		@Override
		public String getLocalizedMessage(String fieldLabel, Locale locale) {
			return LanguageUtil.format(
				locale, "x-please-enter-a-file-with-a-valid-extension-x",
				new String[] {fieldLabel, _validFileExtensions}, false);
		}

		public String getValidFileExtensions() {
			return _validFileExtensions;
		}

		private final String _validFileExtensions;

	}

	public static class InvalidInfoFieldValue
		extends InfoFormValidationException {

		public InvalidInfoFieldValue(String infoFieldUniqueId) {
			super(infoFieldUniqueId);
		}

		@Override
		public String getLocalizedMessage(Locale locale) {
			return LanguageUtil.get(locale, "this-field-is-invalid");
		}

		@Override
		public String getLocalizedMessage(String fieldLabel, Locale locale) {
			return LanguageUtil.format(
				locale, "the-x-is-invalid", fieldLabel, false);
		}

	}

	public static class RequiredInfoField extends InfoFormValidationException {

		public RequiredInfoField(String infoFieldUniqueId) {
			super(infoFieldUniqueId);
		}

		@Override
		public String getLocalizedMessage(Locale locale) {
			return LanguageUtil.get(locale, "this-field-is-required");
		}

		@Override
		public String getLocalizedMessage(String fieldLabel, Locale locale) {
			return LanguageUtil.format(
				locale, "the-x-is-required", fieldLabel, false);
		}

	}

	public static class RuleValidation extends InfoFormValidationException {

		public RuleValidation(String message) {
			_message = message;
		}

		public void addCustomValidation(
			String infoFieldUniqueId, String message) {

			_customValidations.add(
				new CustomValidation(infoFieldUniqueId, message));
		}

		public List<CustomValidation> getCustomValidations() {
			return _customValidations;
		}

		@Override
		public String getLocalizedMessage(Locale locale) {
			if (_message != null) {
				return _message;
			}

			return super.getLocalizedMessage(locale);
		}

		private final List<CustomValidation> _customValidations =
			new ArrayList<>();
		private final String _message;

	}

	public static class UniqueValueConstraintViolation
		extends InfoFormValidationException {

		public UniqueValueConstraintViolation(String infoFieldLabel) {
			_infoFieldLabel = infoFieldLabel;
		}

		@Override
		public String getLocalizedMessage(Locale locale) {
			return LanguageUtil.format(
				locale, "the-x-is-already-in-use",
				new String[] {_infoFieldLabel, _infoFieldLabel}, false);
		}

		@Override
		public String getLocalizedMessage(String fieldLabel, Locale locale) {
			return super.getLocalizedMessage(locale);
		}

		private final String _infoFieldLabel;

	}

	private final String _infoFieldUniqueId;

}