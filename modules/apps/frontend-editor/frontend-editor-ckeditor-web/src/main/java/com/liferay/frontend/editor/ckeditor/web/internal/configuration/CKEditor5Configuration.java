/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.ckeditor.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Marko Cikos
 */
@ExtendedObjectClassDefinition(generateUI = false)
@Meta.OCD(
	id = "com.liferay.frontend.editor.ckeditor.web.internal.configuration.CKEditor5Configuration"
)
public interface CKEditor5Configuration {

	@Meta.AD(
		deflt = "eyJhbGciOiJFUzI1NiJ9.eyJleHAiOjE4NjE5MTk5OTksImp0aSI6ImQ2ZmZiOGFiLThiZDAtNGZiYi04YTQ4LWJiYTM3NTA3ZWNjZSIsImRpc3RyaWJ1dGlvbkNoYW5uZWwiOlsic2giLCJkcnVwYWwiXSwid2hpdGVMYWJlbCI6dHJ1ZSwiZmVhdHVyZXMiOlsiRFJVUCJdLCJ2YyI6IjdmZWUwYzIxIn0.CFZZPn8RnAJOLlOpd83uSAxiNhIQZrgYD28ge5PCstUi9yG7-M27L9wA_jcGuPx-yJokwR0EXyBzje5Xl0D71A",
		required = false
	)
	public String licenseKey();

}