/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.client.dto.v1_0;

import com.liferay.ai.hub.rest.client.function.UnsafeSupplier;
import com.liferay.ai.hub.rest.client.serdes.v1_0.ChatbotSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public class Chatbot implements Cloneable, Serializable {

	public static Chatbot toDTO(String json) {
		return ChatbotSerDes.toDTO(json);
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		try {
			active = activeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean active;

	public Map<String, ?> getAvatar() {
		return avatar;
	}

	public void setAvatar(Map<String, ?> avatar) {
		this.avatar = avatar;
	}

	public void setAvatar(
		UnsafeSupplier<Map<String, ?>, Exception> avatarUnsafeSupplier) {

		try {
			avatar = avatarUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, ?> avatar;

	public String getDisclaimerMessage() {
		return disclaimerMessage;
	}

	public void setDisclaimerMessage(String disclaimerMessage) {
		this.disclaimerMessage = disclaimerMessage;
	}

	public void setDisclaimerMessage(
		UnsafeSupplier<String, Exception> disclaimerMessageUnsafeSupplier) {

		try {
			disclaimerMessage = disclaimerMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String disclaimerMessage;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public String getIntroMessage() {
		return introMessage;
	}

	public void setIntroMessage(String introMessage) {
		this.introMessage = introMessage;
	}

	public void setIntroMessage(
		UnsafeSupplier<String, Exception> introMessageUnsafeSupplier) {

		try {
			introMessage = introMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String introMessage;

	public String getNotificationMessage() {
		return notificationMessage;
	}

	public void setNotificationMessage(String notificationMessage) {
		this.notificationMessage = notificationMessage;
	}

	public void setNotificationMessage(
		UnsafeSupplier<String, Exception> notificationMessageUnsafeSupplier) {

		try {
			notificationMessage = notificationMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String notificationMessage;

	public String getPlaceholderMessage() {
		return placeholderMessage;
	}

	public void setPlaceholderMessage(String placeholderMessage) {
		this.placeholderMessage = placeholderMessage;
	}

	public void setPlaceholderMessage(
		UnsafeSupplier<String, Exception> placeholderMessageUnsafeSupplier) {

		try {
			placeholderMessage = placeholderMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String placeholderMessage;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(
		UnsafeSupplier<String, Exception> titleUnsafeSupplier) {

		try {
			title = titleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String title;

	@Override
	public Chatbot clone() throws CloneNotSupportedException {
		return (Chatbot)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Chatbot)) {
			return false;
		}

		Chatbot chatbot = (Chatbot)object;

		return Objects.equals(toString(), chatbot.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ChatbotSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1342302770