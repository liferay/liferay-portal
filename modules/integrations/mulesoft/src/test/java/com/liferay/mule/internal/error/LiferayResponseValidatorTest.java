/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.error;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.http.api.HttpConstants;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;

/**
 * @author Matija Petanjek
 */
public class LiferayResponseValidatorTest {

	@Before
	public void setUp() {
		liferayResponseValidator = new LiferayResponseValidator();
	}

	@Test(expected = ModuleException.class)
	public void testValidateNoResponse() throws Exception {
		assertThatResponseValidationProducesLiferayError(
			null, LiferayError.SERVER_ERROR);
	}

	@Test
	public void testValidateResponseStatus200() throws Exception {
		HttpConstants.HttpStatus httpStatus = HttpConstants.HttpStatus.OK;

		liferayResponseValidator.validate(
			getHttpResponse(
				httpStatus.getReasonPhrase(), httpStatus.getStatusCode()));
	}

	@Test(expected = ModuleException.class)
	public void testValidateResponseStatus400() throws Exception {
		assertThatResponseValidationProducesLiferayError(
			HttpConstants.HttpStatus.BAD_REQUEST, LiferayError.BAD_REQUEST);
	}

	@Test(expected = ModuleException.class)
	public void testValidateResponseStatus404() throws Exception {
		assertThatResponseValidationProducesLiferayError(
			HttpConstants.HttpStatus.NOT_FOUND, LiferayError.NOT_FOUND);
	}

	@Test(expected = ModuleException.class)
	public void testValidateResponseStatus405() throws Exception {
		assertThatResponseValidationProducesLiferayError(
			HttpConstants.HttpStatus.METHOD_NOT_ALLOWED,
			LiferayError.NOT_ALLOWED);
	}

	@Test(expected = ModuleException.class)
	public void testValidateResponseStatus406() throws Exception {
		assertThatResponseValidationProducesLiferayError(
			HttpConstants.HttpStatus.NOT_ACCEPTABLE,
			LiferayError.NOT_ACCEPTABLE);
	}

	@Test(expected = ModuleException.class)
	public void testValidateResponseStatus415() throws Exception {
		assertThatResponseValidationProducesLiferayError(
			HttpConstants.HttpStatus.UNSUPPORTED_MEDIA_TYPE,
			LiferayError.UNSUPPORTED_MEDIA_TYPE);
	}

	@Test(expected = ModuleException.class)
	public void testValidateResponseStatus500() throws Exception {
		assertThatResponseValidationProducesLiferayError(
			HttpConstants.HttpStatus.INTERNAL_SERVER_ERROR,
			LiferayError.SERVER_ERROR);
	}

	@Test(expected = ModuleException.class)
	public void testValidateResponseStatus501() throws Exception {
		assertThatResponseValidationProducesLiferayError(
			HttpConstants.HttpStatus.NOT_IMPLEMENTED,
			LiferayError.NOT_IMPLEMENTED);
	}

	private void assertThatResponseValidationProducesLiferayError(
			HttpConstants.HttpStatus httpStatus, LiferayError liferayError)
		throws Exception {

		try {
			if (httpStatus != null) {
				liferayResponseValidator.validate(
					getHttpResponse(
						httpStatus.getReasonPhrase(),
						httpStatus.getStatusCode()));
			}
			else {
				liferayResponseValidator.validate(null);
			}

			Assert.fail();
		}
		catch (ModuleException moduleException) {
			Assert.assertEquals(liferayError, moduleException.getType());

			throw moduleException;
		}
	}

	private HttpResponse getHttpResponse(String reasonPhrase, int status) {
		return HttpResponse.builder(
		).statusCode(
			status
		).reasonPhrase(
			reasonPhrase
		).build();
	}

	private LiferayResponseValidator liferayResponseValidator;

}