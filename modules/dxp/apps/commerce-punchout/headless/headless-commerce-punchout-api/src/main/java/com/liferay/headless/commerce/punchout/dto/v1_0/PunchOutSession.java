/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.punchout.dto.v1_0;

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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Jaclyn Ong
 * @generated
 */
@Generated("")
@GraphQLName("PunchOutSession")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {
		"buyerAccountReferenceCode", "buyerGroup", "buyerUser", "cart",
		"punchOutReturnURL", "punchOutSessionType"
	}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PunchOutSession")
public class PunchOutSession implements Serializable {

	public static PunchOutSession toDTO(String json) {
		return ObjectMapperUtil.readValue(PunchOutSession.class, json);
	}

	public static PunchOutSession unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PunchOutSession.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getBuyerAccountReferenceCode() {
		if (_buyerAccountReferenceCodeSupplier != null) {
			buyerAccountReferenceCode =
				_buyerAccountReferenceCodeSupplier.get();

			_buyerAccountReferenceCodeSupplier = null;
		}

		return buyerAccountReferenceCode;
	}

	public void setBuyerAccountReferenceCode(String buyerAccountReferenceCode) {
		this.buyerAccountReferenceCode = buyerAccountReferenceCode;

		_buyerAccountReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setBuyerAccountReferenceCode(
		UnsafeSupplier<String, Exception>
			buyerAccountReferenceCodeUnsafeSupplier) {

		_buyerAccountReferenceCodeSupplier = () -> {
			try {
				return buyerAccountReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String buyerAccountReferenceCode;

	@JsonIgnore
	private Supplier<String> _buyerAccountReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Group getBuyerGroup() {
		if (_buyerGroupSupplier != null) {
			buyerGroup = _buyerGroupSupplier.get();

			_buyerGroupSupplier = null;
		}

		return buyerGroup;
	}

	public void setBuyerGroup(Group buyerGroup) {
		this.buyerGroup = buyerGroup;

		_buyerGroupSupplier = null;
	}

	@JsonIgnore
	public void setBuyerGroup(
		UnsafeSupplier<Group, Exception> buyerGroupUnsafeSupplier) {

		_buyerGroupSupplier = () -> {
			try {
				return buyerGroupUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Group buyerGroup;

	@JsonIgnore
	private Supplier<Group> _buyerGroupSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Organization getBuyerOrganization() {
		if (_buyerOrganizationSupplier != null) {
			buyerOrganization = _buyerOrganizationSupplier.get();

			_buyerOrganizationSupplier = null;
		}

		return buyerOrganization;
	}

	public void setBuyerOrganization(Organization buyerOrganization) {
		this.buyerOrganization = buyerOrganization;

		_buyerOrganizationSupplier = null;
	}

	@JsonIgnore
	public void setBuyerOrganization(
		UnsafeSupplier<Organization, Exception>
			buyerOrganizationUnsafeSupplier) {

		_buyerOrganizationSupplier = () -> {
			try {
				return buyerOrganizationUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Organization buyerOrganization;

	@JsonIgnore
	private Supplier<Organization> _buyerOrganizationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public User getBuyerUser() {
		if (_buyerUserSupplier != null) {
			buyerUser = _buyerUserSupplier.get();

			_buyerUserSupplier = null;
		}

		return buyerUser;
	}

	public void setBuyerUser(User buyerUser) {
		this.buyerUser = buyerUser;

		_buyerUserSupplier = null;
	}

	@JsonIgnore
	public void setBuyerUser(
		UnsafeSupplier<User, Exception> buyerUserUnsafeSupplier) {

		_buyerUserSupplier = () -> {
			try {
				return buyerUserUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected User buyerUser;

	@JsonIgnore
	private Supplier<User> _buyerUserSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Cart getCart() {
		if (_cartSupplier != null) {
			cart = _cartSupplier.get();

			_cartSupplier = null;
		}

		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;

		_cartSupplier = null;
	}

	@JsonIgnore
	public void setCart(UnsafeSupplier<Cart, Exception> cartUnsafeSupplier) {
		_cartSupplier = () -> {
			try {
				return cartUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Cart cart;

	@JsonIgnore
	private Supplier<Cart> _cartSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPunchOutReturnURL() {
		if (_punchOutReturnURLSupplier != null) {
			punchOutReturnURL = _punchOutReturnURLSupplier.get();

			_punchOutReturnURLSupplier = null;
		}

		return punchOutReturnURL;
	}

	public void setPunchOutReturnURL(String punchOutReturnURL) {
		this.punchOutReturnURL = punchOutReturnURL;

		_punchOutReturnURLSupplier = null;
	}

	@JsonIgnore
	public void setPunchOutReturnURL(
		UnsafeSupplier<String, Exception> punchOutReturnURLUnsafeSupplier) {

		_punchOutReturnURLSupplier = () -> {
			try {
				return punchOutReturnURLUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String punchOutReturnURL;

	@JsonIgnore
	private Supplier<String> _punchOutReturnURLSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPunchOutSessionType() {
		if (_punchOutSessionTypeSupplier != null) {
			punchOutSessionType = _punchOutSessionTypeSupplier.get();

			_punchOutSessionTypeSupplier = null;
		}

		return punchOutSessionType;
	}

	public void setPunchOutSessionType(String punchOutSessionType) {
		this.punchOutSessionType = punchOutSessionType;

		_punchOutSessionTypeSupplier = null;
	}

	@JsonIgnore
	public void setPunchOutSessionType(
		UnsafeSupplier<String, Exception> punchOutSessionTypeUnsafeSupplier) {

		_punchOutSessionTypeSupplier = () -> {
			try {
				return punchOutSessionTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String punchOutSessionType;

	@JsonIgnore
	private Supplier<String> _punchOutSessionTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPunchOutStartURL() {
		if (_punchOutStartURLSupplier != null) {
			punchOutStartURL = _punchOutStartURLSupplier.get();

			_punchOutStartURLSupplier = null;
		}

		return punchOutStartURL;
	}

	public void setPunchOutStartURL(String punchOutStartURL) {
		this.punchOutStartURL = punchOutStartURL;

		_punchOutStartURLSupplier = null;
	}

	@JsonIgnore
	public void setPunchOutStartURL(
		UnsafeSupplier<String, Exception> punchOutStartURLUnsafeSupplier) {

		_punchOutStartURLSupplier = () -> {
			try {
				return punchOutStartURLUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String punchOutStartURL;

	@JsonIgnore
	private Supplier<String> _punchOutStartURLSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PunchOutSession)) {
			return false;
		}

		PunchOutSession punchOutSession = (PunchOutSession)object;

		return Objects.equals(toString(), punchOutSession.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String buyerAccountReferenceCode = getBuyerAccountReferenceCode();

		if (buyerAccountReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"buyerAccountReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(buyerAccountReferenceCode));

			sb.append("\"");
		}

		Group buyerGroup = getBuyerGroup();

		if (buyerGroup != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"buyerGroup\": ");

			sb.append(String.valueOf(buyerGroup));
		}

		Organization buyerOrganization = getBuyerOrganization();

		if (buyerOrganization != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"buyerOrganization\": ");

			sb.append(String.valueOf(buyerOrganization));
		}

		User buyerUser = getBuyerUser();

		if (buyerUser != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"buyerUser\": ");

			sb.append(String.valueOf(buyerUser));
		}

		Cart cart = getCart();

		if (cart != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cart\": ");

			sb.append(String.valueOf(cart));
		}

		String punchOutReturnURL = getPunchOutReturnURL();

		if (punchOutReturnURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"punchOutReturnURL\": ");

			sb.append("\"");

			sb.append(_escape(punchOutReturnURL));

			sb.append("\"");
		}

		String punchOutSessionType = getPunchOutSessionType();

		if (punchOutSessionType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"punchOutSessionType\": ");

			sb.append("\"");

			sb.append(_escape(punchOutSessionType));

			sb.append("\"");
		}

		String punchOutStartURL = getPunchOutStartURL();

		if (punchOutStartURL != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"punchOutStartURL\": ");

			sb.append("\"");

			sb.append(_escape(punchOutStartURL));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.punchout.dto.v1_0.PunchOutSession",
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