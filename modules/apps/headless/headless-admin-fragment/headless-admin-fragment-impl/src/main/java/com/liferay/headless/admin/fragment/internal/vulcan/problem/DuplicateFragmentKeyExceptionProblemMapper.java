/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.vulcan.problem;

import com.liferay.fragment.exception.DuplicateFragmentEntryKeyException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(service = ProblemMapper.class)
public class DuplicateFragmentKeyExceptionProblemMapper
	implements ProblemMapper<DuplicateFragmentEntryKeyException> {

	@Override
	public Problem getProblem(
		DuplicateFragmentEntryKeyException duplicateFragmentEntryKeyException) {

		String fragmentEntryKey =
			duplicateFragmentEntryKeyException.getFragmentEntryKey();

		return ProblemUtil.getProblem(
			Problem.Status.CONFLICT, duplicateFragmentEntryKeyException,
			locale -> _language.format(
				locale, "a-fragment-entry-with-the-key-x-already-exists",
				fragmentEntryKey));
	}

	@Reference
	private Language _language;

}