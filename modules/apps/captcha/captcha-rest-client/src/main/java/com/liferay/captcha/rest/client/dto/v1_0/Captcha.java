/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.rest.client.dto.v1_0;

import com.liferay.captcha.rest.client.function.UnsafeSupplier;
import com.liferay.captcha.rest.client.serdes.v1_0.CaptchaSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Loc Pham
 * @generated
 */
@Generated("")
public class Captcha implements Cloneable, Serializable {

	public static Captcha toDTO(String json) {
		return CaptchaSerDes.toDTO(json);
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public void setAnswer(
		UnsafeSupplier<String, Exception> answerUnsafeSupplier) {

		try {
			answer = answerUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String answer;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setImage(
		UnsafeSupplier<String, Exception> imageUnsafeSupplier) {

		try {
			image = imageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String image;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setToken(
		UnsafeSupplier<String, Exception> tokenUnsafeSupplier) {

		try {
			token = tokenUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String token;

	@Override
	public Captcha clone() throws CloneNotSupportedException {
		return (Captcha)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Captcha)) {
			return false;
		}

		Captcha captcha = (Captcha)object;

		return Objects.equals(toString(), captcha.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CaptchaSerDes.toJSON(this);
	}

}