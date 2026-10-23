/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian I. Kim
 */
public class DSCommerceOrderAttachmentException extends PortalException {

	public static class MustHaveDSRequest
		extends DSCommerceOrderAttachmentException {

		public MustHaveDSRequest(long commerceOrderAttachmentId) {
			super(
				"Commerce order attachment " + commerceOrderAttachmentId +
					" has no digital signature request");
		}

	}

	public static class MustHaveRecipient
		extends DSCommerceOrderAttachmentException {

		public MustHaveRecipient() {
			super(
				"A digital signature request must have at least one recipient");
		}

	}

	public static class MustHaveSupportedFileExtension
		extends DSCommerceOrderAttachmentException {

		public MustHaveSupportedFileExtension(long commerceOrderAttachmentId) {
			super(
				"The file extension of commerce order attachment " +
					commerceOrderAttachmentId + " is not supported");
		}

	}

	public static class MustNotHaveMultipleCountersigners
		extends DSCommerceOrderAttachmentException {

		public MustNotHaveMultipleCountersigners() {
			super("Only one internal countersigner is allowed");
		}

	}

	private DSCommerceOrderAttachmentException(String message) {
		super(message);
	}

}