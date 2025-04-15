/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.client.dto.v1_0;

import com.liferay.headless.commerce.admin.channel.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.PaymentMethodGroupRelTermSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class PaymentMethodGroupRelTerm implements Cloneable, Serializable {

	public static PaymentMethodGroupRelTerm toDTO(String json) {
		return PaymentMethodGroupRelTermSerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public Long getPaymentMethodGroupRelId() {
		return paymentMethodGroupRelId;
	}

	public void setPaymentMethodGroupRelId(Long paymentMethodGroupRelId) {
		this.paymentMethodGroupRelId = paymentMethodGroupRelId;
	}

	public void setPaymentMethodGroupRelId(
		UnsafeSupplier<Long, Exception> paymentMethodGroupRelIdUnsafeSupplier) {

		try {
			paymentMethodGroupRelId =
				paymentMethodGroupRelIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long paymentMethodGroupRelId;

	public Long getPaymentMethodGroupRelTermId() {
		return paymentMethodGroupRelTermId;
	}

	public void setPaymentMethodGroupRelTermId(
		Long paymentMethodGroupRelTermId) {

		this.paymentMethodGroupRelTermId = paymentMethodGroupRelTermId;
	}

	public void setPaymentMethodGroupRelTermId(
		UnsafeSupplier<Long, Exception>
			paymentMethodGroupRelTermIdUnsafeSupplier) {

		try {
			paymentMethodGroupRelTermId =
				paymentMethodGroupRelTermIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long paymentMethodGroupRelTermId;

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public void setTerm(UnsafeSupplier<Term, Exception> termUnsafeSupplier) {
		try {
			term = termUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Term term;

	public String getTermExternalReferenceCode() {
		return termExternalReferenceCode;
	}

	public void setTermExternalReferenceCode(String termExternalReferenceCode) {
		this.termExternalReferenceCode = termExternalReferenceCode;
	}

	public void setTermExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			termExternalReferenceCodeUnsafeSupplier) {

		try {
			termExternalReferenceCode =
				termExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String termExternalReferenceCode;

	public Long getTermId() {
		return termId;
	}

	public void setTermId(Long termId) {
		this.termId = termId;
	}

	public void setTermId(
		UnsafeSupplier<Long, Exception> termIdUnsafeSupplier) {

		try {
			termId = termIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long termId;

	@Override
	public PaymentMethodGroupRelTerm clone() throws CloneNotSupportedException {
		return (PaymentMethodGroupRelTerm)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PaymentMethodGroupRelTerm)) {
			return false;
		}

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm =
			(PaymentMethodGroupRelTerm)object;

		return Objects.equals(toString(), paymentMethodGroupRelTerm.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PaymentMethodGroupRelTermSerDes.toJSON(this);
	}

}