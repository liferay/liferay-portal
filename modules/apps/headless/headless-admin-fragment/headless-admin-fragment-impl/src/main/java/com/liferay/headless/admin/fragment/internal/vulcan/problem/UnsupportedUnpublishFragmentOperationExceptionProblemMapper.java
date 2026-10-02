/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.vulcan.problem;

import com.liferay.fragment.exception.UnsupportedUnpublishFragmentEntryOperationException;
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
public class UnsupportedUnpublishFragmentOperationExceptionProblemMapper
	implements ProblemMapper
		<UnsupportedUnpublishFragmentEntryOperationException> {

	@Override
	public Problem getProblem(
		UnsupportedUnpublishFragmentEntryOperationException
			unsupportedUnpublishFragmentEntryOperationException) {

		return new Problem() {

			@Override
			public String getDetail(Locale locale) {
				return _language.get(
					locale, "unpublishing-a-fragment-entry-is-not-supported");
			}

			@Override
			public Status getStatus() {
				return Status.BAD_REQUEST;
			}

			@Override
			public String getTitle(Locale locale) {
				return _language.get(
					locale, "unpublishing-a-fragment-entry-is-not-supported");
			}

			@Override
			public String getType() {
				return UnsupportedUnpublishFragmentEntryOperationException.
					class.getName();
			}

		};
	}

	@Reference
	private Language _language;

}