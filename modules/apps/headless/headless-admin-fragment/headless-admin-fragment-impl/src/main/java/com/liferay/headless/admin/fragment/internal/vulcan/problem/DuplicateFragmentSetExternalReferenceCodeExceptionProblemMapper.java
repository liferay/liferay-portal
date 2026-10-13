/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.vulcan.problem;

import com.liferay.fragment.exception.DuplicateFragmentCollectionExternalReferenceCodeException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(service = ProblemMapper.class)
public class DuplicateFragmentSetExternalReferenceCodeExceptionProblemMapper
	implements ProblemMapper
		<DuplicateFragmentCollectionExternalReferenceCodeException> {

	@Override
	public Problem getProblem(
		DuplicateFragmentCollectionExternalReferenceCodeException
			duplicateFragmentCollectionExternalReferenceCodeException) {

		return new Problem() {

			@Override
			public String getDetail(Locale locale) {
				return _language.get(
					locale, "this-external-reference-code-is-already-in-use");
			}

			@Override
			public Status getStatus() {
				return Status.CONFLICT;
			}

			@Override
			public String getTitle(Locale locale) {
				return _language.get(
					locale, "this-external-reference-code-is-already-in-use");
			}

			@Override
			public String getType() {
				return DuplicateFragmentCollectionExternalReferenceCodeException.class.
					getName();
			}

		};
	}

	@Reference
	private Language _language;

}