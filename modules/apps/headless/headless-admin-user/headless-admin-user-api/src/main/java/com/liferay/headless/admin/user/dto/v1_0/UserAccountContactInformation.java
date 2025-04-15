/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "The user's contact information.",
	value = "UserAccountContactInformation"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "UserAccountContactInformation")
public class UserAccountContactInformation implements Serializable {

	public static UserAccountContactInformation toDTO(String json) {
		return ObjectMapperUtil.readValue(
			UserAccountContactInformation.class, json);
	}

	public static UserAccountContactInformation unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			UserAccountContactInformation.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the user's email addresses, with one optionally marked as primary."
	)
	@Valid
	public EmailAddress[] getEmailAddresses() {
		if (_emailAddressesSupplier != null) {
			emailAddresses = _emailAddressesSupplier.get();

			_emailAddressesSupplier = null;
		}

		return emailAddresses;
	}

	public void setEmailAddresses(EmailAddress[] emailAddresses) {
		this.emailAddresses = emailAddresses;

		_emailAddressesSupplier = null;
	}

	@JsonIgnore
	public void setEmailAddresses(
		UnsafeSupplier<EmailAddress[], Exception>
			emailAddressesUnsafeSupplier) {

		_emailAddressesSupplier = () -> {
			try {
				return emailAddressesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A list of the user's email addresses, with one optionally marked as primary."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected EmailAddress[] emailAddresses;

	@JsonIgnore
	private Supplier<EmailAddress[]> _emailAddressesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's Facebook account."
	)
	public String getFacebook() {
		if (_facebookSupplier != null) {
			facebook = _facebookSupplier.get();

			_facebookSupplier = null;
		}

		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;

		_facebookSupplier = null;
	}

	@JsonIgnore
	public void setFacebook(
		UnsafeSupplier<String, Exception> facebookUnsafeSupplier) {

		_facebookSupplier = () -> {
			try {
				return facebookUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's Facebook account.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String facebook;

	@JsonIgnore
	private Supplier<String> _facebookSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the `contactInformation`."
	)
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The ID of the `contactInformation`.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's Jabber handle."
	)
	public String getJabber() {
		if (_jabberSupplier != null) {
			jabber = _jabberSupplier.get();

			_jabberSupplier = null;
		}

		return jabber;
	}

	public void setJabber(String jabber) {
		this.jabber = jabber;

		_jabberSupplier = null;
	}

	@JsonIgnore
	public void setJabber(
		UnsafeSupplier<String, Exception> jabberUnsafeSupplier) {

		_jabberSupplier = () -> {
			try {
				return jabberUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's Jabber handle.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String jabber;

	@JsonIgnore
	private Supplier<String> _jabberSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of user's postal addresses, with one optionally marked as primary."
	)
	@Valid
	public PostalAddress[] getPostalAddresses() {
		if (_postalAddressesSupplier != null) {
			postalAddresses = _postalAddressesSupplier.get();

			_postalAddressesSupplier = null;
		}

		return postalAddresses;
	}

	public void setPostalAddresses(PostalAddress[] postalAddresses) {
		this.postalAddresses = postalAddresses;

		_postalAddressesSupplier = null;
	}

	@JsonIgnore
	public void setPostalAddresses(
		UnsafeSupplier<PostalAddress[], Exception>
			postalAddressesUnsafeSupplier) {

		_postalAddressesSupplier = () -> {
			try {
				return postalAddressesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A list of user's postal addresses, with one optionally marked as primary."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PostalAddress[] postalAddresses;

	@JsonIgnore
	private Supplier<PostalAddress[]> _postalAddressesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's Skype handle."
	)
	public String getSkype() {
		if (_skypeSupplier != null) {
			skype = _skypeSupplier.get();

			_skypeSupplier = null;
		}

		return skype;
	}

	public void setSkype(String skype) {
		this.skype = skype;

		_skypeSupplier = null;
	}

	@JsonIgnore
	public void setSkype(
		UnsafeSupplier<String, Exception> skypeUnsafeSupplier) {

		_skypeSupplier = () -> {
			try {
				return skypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's Skype handle.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String skype;

	@JsonIgnore
	private Supplier<String> _skypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's SMS number."
	)
	public String getSms() {
		if (_smsSupplier != null) {
			sms = _smsSupplier.get();

			_smsSupplier = null;
		}

		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;

		_smsSupplier = null;
	}

	@JsonIgnore
	public void setSms(UnsafeSupplier<String, Exception> smsUnsafeSupplier) {
		_smsSupplier = () -> {
			try {
				return smsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's SMS number.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String sms;

	@JsonIgnore
	private Supplier<String> _smsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the user's phone numbers, with one optionally marked as primary."
	)
	@Valid
	public Phone[] getTelephones() {
		if (_telephonesSupplier != null) {
			telephones = _telephonesSupplier.get();

			_telephonesSupplier = null;
		}

		return telephones;
	}

	public void setTelephones(Phone[] telephones) {
		this.telephones = telephones;

		_telephonesSupplier = null;
	}

	@JsonIgnore
	public void setTelephones(
		UnsafeSupplier<Phone[], Exception> telephonesUnsafeSupplier) {

		_telephonesSupplier = () -> {
			try {
				return telephonesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A list of the user's phone numbers, with one optionally marked as primary."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Phone[] telephones;

	@JsonIgnore
	private Supplier<Phone[]> _telephonesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The user's Twitter handle."
	)
	public String getTwitter() {
		if (_twitterSupplier != null) {
			twitter = _twitterSupplier.get();

			_twitterSupplier = null;
		}

		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;

		_twitterSupplier = null;
	}

	@JsonIgnore
	public void setTwitter(
		UnsafeSupplier<String, Exception> twitterUnsafeSupplier) {

		_twitterSupplier = () -> {
			try {
				return twitterUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The user's Twitter handle.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String twitter;

	@JsonIgnore
	private Supplier<String> _twitterSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the user's web URLs, with one optionally marked as primary."
	)
	@Valid
	public WebUrl[] getWebUrls() {
		if (_webUrlsSupplier != null) {
			webUrls = _webUrlsSupplier.get();

			_webUrlsSupplier = null;
		}

		return webUrls;
	}

	public void setWebUrls(WebUrl[] webUrls) {
		this.webUrls = webUrls;

		_webUrlsSupplier = null;
	}

	@JsonIgnore
	public void setWebUrls(
		UnsafeSupplier<WebUrl[], Exception> webUrlsUnsafeSupplier) {

		_webUrlsSupplier = () -> {
			try {
				return webUrlsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "A list of the user's web URLs, with one optionally marked as primary."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected WebUrl[] webUrls;

	@JsonIgnore
	private Supplier<WebUrl[]> _webUrlsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UserAccountContactInformation)) {
			return false;
		}

		UserAccountContactInformation userAccountContactInformation =
			(UserAccountContactInformation)object;

		return Objects.equals(
			toString(), userAccountContactInformation.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		EmailAddress[] emailAddresses = getEmailAddresses();

		if (emailAddresses != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddresses\": ");

			sb.append("[");

			for (int i = 0; i < emailAddresses.length; i++) {
				sb.append(String.valueOf(emailAddresses[i]));

				if ((i + 1) < emailAddresses.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String facebook = getFacebook();

		if (facebook != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"facebook\": ");

			sb.append("\"");

			sb.append(_escape(facebook));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String jabber = getJabber();

		if (jabber != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"jabber\": ");

			sb.append("\"");

			sb.append(_escape(jabber));

			sb.append("\"");
		}

		PostalAddress[] postalAddresses = getPostalAddresses();

		if (postalAddresses != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"postalAddresses\": ");

			sb.append("[");

			for (int i = 0; i < postalAddresses.length; i++) {
				sb.append(String.valueOf(postalAddresses[i]));

				if ((i + 1) < postalAddresses.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String skype = getSkype();

		if (skype != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skype\": ");

			sb.append("\"");

			sb.append(_escape(skype));

			sb.append("\"");
		}

		String sms = getSms();

		if (sms != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sms\": ");

			sb.append("\"");

			sb.append(_escape(sms));

			sb.append("\"");
		}

		Phone[] telephones = getTelephones();

		if (telephones != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"telephones\": ");

			sb.append("[");

			for (int i = 0; i < telephones.length; i++) {
				sb.append(String.valueOf(telephones[i]));

				if ((i + 1) < telephones.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String twitter = getTwitter();

		if (twitter != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"twitter\": ");

			sb.append("\"");

			sb.append(_escape(twitter));

			sb.append("\"");
		}

		WebUrl[] webUrls = getWebUrls();

		if (webUrls != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"webUrls\": ");

			sb.append("[");

			for (int i = 0; i < webUrls.length; i++) {
				sb.append(String.valueOf(webUrls[i]));

				if ((i + 1) < webUrls.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.user.dto.v1_0.UserAccountContactInformation",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}