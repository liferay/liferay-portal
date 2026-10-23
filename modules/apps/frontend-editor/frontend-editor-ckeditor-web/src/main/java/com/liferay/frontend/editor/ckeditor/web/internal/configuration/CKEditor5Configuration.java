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
		deflt = "eyJhbGciOiJFUzI1NiJ9.eyJleHAiOjE3NjcyMjU1OTksImp0aSI6IjNjOTQxM2FhLWI1MDctNGU4ZC05ZTAwLTNhY2UyNGY5MTU4ZiIsImRpc3RyaWJ1dGlvbkNoYW5uZWwiOlsic2giLCJkcnVwYWwiXSwid2hpdGVMYWJlbCI6dHJ1ZSwiZmVhdHVyZXMiOlsiRFJVUCJdLCJ2YyI6ImMyMWZmYTJhIn0.GgM-msNhbUOmXDImVGf7FO9ueeLgtB_0aGDgEygAhI3k5TyAlusZzhFzPmTDQpXiPQ1V5SI3UMAoxH5u-MRYew",
		required = false
	)
	public String licenseKey();

}