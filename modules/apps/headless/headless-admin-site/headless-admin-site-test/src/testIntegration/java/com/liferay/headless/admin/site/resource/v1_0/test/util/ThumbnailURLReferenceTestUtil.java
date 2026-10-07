/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.headless.admin.site.client.dto.v1_0.ThumbnailURLReference;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;

/**
 * @author Lourdes Fernández Besada
 */
public class ThumbnailURLReferenceTestUtil {

	public static ThumbnailURLReference getThumbnailURLReference(String url) {
		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(
			RandomTestUtil.randomString());
		thumbnailURLReference.setUrl(url);

		return thumbnailURLReference;
	}

	public static <T> void testThumbnailURLReferenceURLFetchSecurity(
			UnsafeBiConsumer<ThumbnailURLReference, T, Exception>
				assertUnsafeBiConsumer,
			T t1, T t2,
			UnsafeFunction<T, ThumbnailURLReference, Exception>
				thumbnailURLReferenceUnsafeFunction,
			UnsafeFunction<T, T, Exception> unsafeFunction)
		throws Exception {

		_assertRestrictedHost(
			new String[0], false, t1, thumbnailURLReferenceUnsafeFunction,
			unsafeFunction);
		_assertRestrictedHost(
			new String[] {"example.test"}, false, t1,
			thumbnailURLReferenceUnsafeFunction, unsafeFunction);
		_assertRestrictedHost(
			new String[] {"127.0.0.1"}, false, t1,
			thumbnailURLReferenceUnsafeFunction, unsafeFunction);
		_assertAllowedHost(
			new String[0], true, assertUnsafeBiConsumer, t1,
			thumbnailURLReferenceUnsafeFunction, unsafeFunction);

		_assertRestrictedHost(
			new String[] {"example.test"}, true, t2,
			thumbnailURLReferenceUnsafeFunction, unsafeFunction);
		_assertAllowedHost(
			new String[] {"127.0.0.1"}, true, assertUnsafeBiConsumer, t2,
			thumbnailURLReferenceUnsafeFunction, unsafeFunction);
	}

	private static <T> void _assertAllowedHost(
			String[] urlHostsAllowed, boolean urlLocalNetworkAccessEnabled,
			UnsafeBiConsumer<ThumbnailURLReference, T, Exception>
				assertUnsafeBiConsumer,
			T t,
			UnsafeFunction<T, ThumbnailURLReference, Exception>
				thumbnailURLReferenceUnsafeFunction,
			UnsafeFunction<T, T, Exception> unsafeFunction)
		throws Exception {

		URLFetchSecurityCompanyConfigurationUtil.swap(
			urlHostsAllowed, urlLocalNetworkAccessEnabled,
			() -> {
				assertUnsafeBiConsumer.accept(
					thumbnailURLReferenceUnsafeFunction.apply(t),
					unsafeFunction.apply(t));

				return null;
			});
	}

	private static <T> void _assertRestrictedHost(
			String[] urlHostsAllowed, boolean urlLocalNetworkAccessEnabled, T t,
			UnsafeFunction<T, ThumbnailURLReference, Exception>
				thumbnailURLReferenceUnsafeFunction,
			UnsafeFunction<T, T, Exception> unsafeFunction)
		throws Exception {

		URLFetchSecurityCompanyConfigurationUtil.swap(
			urlHostsAllowed, urlLocalNetworkAccessEnabled,
			() -> {
				try {
					unsafeFunction.apply(t);

					Assert.fail();
				}
				catch (Problem.ProblemException problemException) {
					Problem problem = problemException.getProblem();

					Assert.assertEquals("BAD_REQUEST", problem.getStatus());

					ThumbnailURLReference thumbnailURLReference =
						thumbnailURLReferenceUnsafeFunction.apply(t);

					Assert.assertEquals(
						"Unable to download file from " +
							thumbnailURLReference.getUrl() +
								" because of restricted host 127.0.0.1",
						problem.getTitle());
				}

				return null;
			});
	}

}