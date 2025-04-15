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
	description = "The organization's contact information, which includes email addresses, postal addresses, phone numbers, and web URLs. This is modeled internally as a `Contact`.",
	value = "OrganizationContactInformation"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "OrganizationContactInformation")
public class OrganizationContactInformation implements Serializable {

	public static OrganizationContactInformation toDTO(String json) {
		return ObjectMapperUtil.readValue(
			OrganizationContactInformation.class, json);
	}

	public static OrganizationContactInformation unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			OrganizationContactInformation.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's email addresses, with one optionally marked as primary."
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
		description = "The organization's email addresses, with one optionally marked as primary."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected EmailAddress[] emailAddresses;

	@JsonIgnore
	private Supplier<EmailAddress[]> _emailAddressesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's postal addresses, with one optionally marked as primary."
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
		description = "The organization's postal addresses, with one optionally marked as primary."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PostalAddress[] postalAddresses;

	@JsonIgnore
	private Supplier<PostalAddress[]> _postalAddressesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's phones numbers, with one optionally marked as primary."
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
		description = "The organization's phones numbers, with one optionally marked as primary."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Phone[] telephones;

	@JsonIgnore
	private Supplier<Phone[]> _telephonesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The organization's web URLs, with one optionally marked as primary."
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
		description = "The organization's web URLs, with one optionally marked as primary."
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

		if (!(object instanceof OrganizationContactInformation)) {
			return false;
		}

		OrganizationContactInformation organizationContactInformation =
			(OrganizationContactInformation)object;

		return Objects.equals(
			toString(), organizationContactInformation.toString());
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
		defaultValue = "com.liferay.headless.admin.user.dto.v1_0.OrganizationContactInformation",
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