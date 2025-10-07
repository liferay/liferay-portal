/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.sanitizer;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import java.util.Collections;
import java.util.Map;

/**
 * @author Zsolt Balogh
 * @author Brian Wing Shun Chan
 */
public class SanitizerUtil {

	public static String sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, String content)
		throws SanitizerException {

		return sanitize(
			companyId, groupId, userId, className, classPK, contentType,
			Sanitizer.MODE_ALL, content, null);
	}

	public static String sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, String mode, String s,
			Map<String, Object> options)
		throws SanitizerException {

		return sanitize(
			companyId, groupId, userId, className, classPK, contentType,
			new String[] {mode}, s, options);
	}

	public static String sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, String[] modes, String content,
			Map<String, Object> options)
		throws SanitizerException {

		for (Sanitizer sanitizer : _sanitizers) {
			content = sanitizer.sanitize(
				companyId, groupId, userId, className, classPK, contentType,
				modes, content, options);
		}

		return content;
	}

	private static final ServiceTrackerList<Sanitizer> _sanitizers =
		ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(), Sanitizer.class,
			Collections.reverseOrder(
				new PropertyServiceReferenceComparator<>("sanitizer.order")));

}