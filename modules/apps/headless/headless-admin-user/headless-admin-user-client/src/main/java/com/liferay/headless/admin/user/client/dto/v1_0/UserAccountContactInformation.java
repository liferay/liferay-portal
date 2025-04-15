/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.dto.v1_0;

import com.liferay.headless.admin.user.client.function.UnsafeSupplier;
import com.liferay.headless.admin.user.client.serdes.v1_0.UserAccountContactInformationSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class UserAccountContactInformation implements Cloneable, Serializable {

	public static UserAccountContactInformation toDTO(String json) {
		return UserAccountContactInformationSerDes.toDTO(json);
	}

	public EmailAddress[] getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(EmailAddress[] emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	public void setEmailAddresses(
		UnsafeSupplier<EmailAddress[], Exception>
			emailAddressesUnsafeSupplier) {

		try {
			emailAddresses = emailAddressesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected EmailAddress[] emailAddresses;

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public void setFacebook(
		UnsafeSupplier<String, Exception> facebookUnsafeSupplier) {

		try {
			facebook = facebookUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String facebook;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public String getJabber() {
		return jabber;
	}

	public void setJabber(String jabber) {
		this.jabber = jabber;
	}

	public void setJabber(
		UnsafeSupplier<String, Exception> jabberUnsafeSupplier) {

		try {
			jabber = jabberUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String jabber;

	public PostalAddress[] getPostalAddresses() {
		return postalAddresses;
	}

	public void setPostalAddresses(PostalAddress[] postalAddresses) {
		this.postalAddresses = postalAddresses;
	}

	public void setPostalAddresses(
		UnsafeSupplier<PostalAddress[], Exception>
			postalAddressesUnsafeSupplier) {

		try {
			postalAddresses = postalAddressesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PostalAddress[] postalAddresses;

	public String getSkype() {
		return skype;
	}

	public void setSkype(String skype) {
		this.skype = skype;
	}

	public void setSkype(
		UnsafeSupplier<String, Exception> skypeUnsafeSupplier) {

		try {
			skype = skypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String skype;

	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public void setSms(UnsafeSupplier<String, Exception> smsUnsafeSupplier) {
		try {
			sms = smsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sms;

	public Phone[] getTelephones() {
		return telephones;
	}

	public void setTelephones(Phone[] telephones) {
		this.telephones = telephones;
	}

	public void setTelephones(
		UnsafeSupplier<Phone[], Exception> telephonesUnsafeSupplier) {

		try {
			telephones = telephonesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Phone[] telephones;

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public void setTwitter(
		UnsafeSupplier<String, Exception> twitterUnsafeSupplier) {

		try {
			twitter = twitterUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String twitter;

	public WebUrl[] getWebUrls() {
		return webUrls;
	}

	public void setWebUrls(WebUrl[] webUrls) {
		this.webUrls = webUrls;
	}

	public void setWebUrls(
		UnsafeSupplier<WebUrl[], Exception> webUrlsUnsafeSupplier) {

		try {
			webUrls = webUrlsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected WebUrl[] webUrls;

	@Override
	public UserAccountContactInformation clone()
		throws CloneNotSupportedException {

		return (UserAccountContactInformation)super.clone();
	}

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
		return UserAccountContactInformationSerDes.toJSON(this);
	}

}