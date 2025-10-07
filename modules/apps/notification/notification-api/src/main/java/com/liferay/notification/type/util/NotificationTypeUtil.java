/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.type.util;

import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.term.evaluator.NotificationTermEvaluator;
import com.liferay.notification.term.evaluator.NotificationTermEvaluatorTracker;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Carolina Barbosa
 */
public class NotificationTypeUtil {

	public static String evaluateTerms(
			String content, NotificationContext notificationContext,
			NotificationTermEvaluatorTracker notificationTermEvaluatorTracker)
		throws PortalException {

		if (Validator.isNull(content)) {
			return StringPool.BLANK;
		}

		List<String> termNames = new ArrayList<>();

		Matcher matcher = _termNamePattern.matcher(content);

		while (matcher.find()) {
			termNames.add(matcher.group());
		}

		for (NotificationTermEvaluator notificationTermEvaluator :
				notificationTermEvaluatorTracker.getNotificationTermEvaluators(
					notificationContext.getClassName())) {

			for (String termName : termNames) {
				content = StringUtil.replace(
					content, termName,
					notificationTermEvaluator.evaluate(
						NotificationTermEvaluator.Context.CONTENT,
						notificationContext, termName));
			}
		}

		return content;
	}

	public static Set<String> getEmailAddresses(String value) {
		if (Validator.isNull(value)) {
			return Collections.emptySet();
		}

		Set<String> emailAddresses = new HashSet<>();

		Matcher matcher = _emailAddressPattern.matcher(value);

		while (matcher.find()) {
			emailAddresses.add(matcher.group());
		}

		return emailAddresses;
	}

	public static boolean isEmailAddress(String value) {
		Matcher matcher = _emailAddressPattern.matcher(value);

		return matcher.find();
	}

	public static boolean isTermValue(String value) {
		Matcher matcher = _termNamePattern.matcher(value);

		return matcher.find();
	}

	private static final Pattern _emailAddressPattern = Pattern.compile(
		"[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@" +
			"(?:\\w(?:[\\w-]*\\w)?\\.)+(\\w(?:[\\w-]*\\w))");
	private static final Pattern _termNamePattern = Pattern.compile(
		"\\[%[^\\[%]+%\\]", Pattern.CASE_INSENSITIVE);

}