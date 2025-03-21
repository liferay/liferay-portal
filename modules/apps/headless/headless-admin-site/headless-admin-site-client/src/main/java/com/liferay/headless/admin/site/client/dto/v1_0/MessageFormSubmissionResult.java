/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.MessageFormSubmissionResultSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class MessageFormSubmissionResult implements Cloneable, Serializable {

	public static MessageFormSubmissionResult toDTO(String json) {
		return MessageFormSubmissionResultSerDes.toDTO(json);
	}

	public FragmentInlineValue getMessage() {
		return message;
	}

	public void setMessage(FragmentInlineValue message) {
		this.message = message;
	}

	public void setMessage(
		UnsafeSupplier<FragmentInlineValue, Exception> messageUnsafeSupplier) {

		try {
			message = messageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentInlineValue message;

	public MessageType getMessageType() {
		return messageType;
	}

	public String getMessageTypeAsString() {
		if (messageType == null) {
			return null;
		}

		return messageType.toString();
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public void setMessageType(
		UnsafeSupplier<MessageType, Exception> messageTypeUnsafeSupplier) {

		try {
			messageType = messageTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MessageType messageType;

	public Boolean getShowNotification() {
		return showNotification;
	}

	public void setShowNotification(Boolean showNotification) {
		this.showNotification = showNotification;
	}

	public void setShowNotification(
		UnsafeSupplier<Boolean, Exception> showNotificationUnsafeSupplier) {

		try {
			showNotification = showNotificationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean showNotification;

	@Override
	public MessageFormSubmissionResult clone()
		throws CloneNotSupportedException {

		return (MessageFormSubmissionResult)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof MessageFormSubmissionResult)) {
			return false;
		}

		MessageFormSubmissionResult messageFormSubmissionResult =
			(MessageFormSubmissionResult)object;

		return Objects.equals(
			toString(), messageFormSubmissionResult.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return MessageFormSubmissionResultSerDes.toJSON(this);
	}

	public static enum MessageType {

		EMBEDDED("Embedded"), NONE("None");

		public static MessageType create(String value) {
			for (MessageType messageType : values()) {
				if (Objects.equals(messageType.getValue(), value) ||
					Objects.equals(messageType.name(), value)) {

					return messageType;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private MessageType(String value) {
			_value = value;
		}

		private final String _value;

	}

}