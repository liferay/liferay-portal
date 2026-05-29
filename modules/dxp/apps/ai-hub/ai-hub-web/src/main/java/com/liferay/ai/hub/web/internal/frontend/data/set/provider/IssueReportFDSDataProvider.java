/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.frontend.data.set.provider;

import com.liferay.ai.hub.web.internal.constants.AIHubFDSNames;
import com.liferay.ai.hub.web.internal.frontend.data.set.model.IssueReportFDSEntry;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * Serves canned data for the Issue Reports admin page until LPD-91817 ships
 * the real GET /o/ai-hub-cell/v1.0/ai-issue-reports endpoint. Once that lands,
 * delete this provider and switch view_issue_reports.jsp to
 * &lt;frontend-data-set:headless-display apiURL="..."/&gt;.
 *
 * @author Davyson Melo
 */
@Component(
	property = "fds.data.provider.key=" + AIHubFDSNames.ISSUE_REPORTS,
	service = FDSDataProvider.class
)
public class IssueReportFDSDataProvider
	implements FDSDataProvider<IssueReportFDSEntry> {

	public static List<IssueReportFDSEntry> getSampleEntries() {
		return _SAMPLE_ENTRIES;
	}

	@Override
	public List<IssueReportFDSEntry> getItems(
		FDSKeywords fdsKeywords, FDSPagination fdsPagination,
		HttpServletRequest httpServletRequest, Sort sort) {

		List<IssueReportFDSEntry> filteredEntries = _sort(
			_filter(fdsKeywords.getKeywords()), sort);

		int fromIndex = Math.min(
			fdsPagination.getStartPosition(), filteredEntries.size());
		int toIndex = Math.min(
			fdsPagination.getEndPosition(), filteredEntries.size());

		return filteredEntries.subList(fromIndex, toIndex);
	}

	@Override
	public int getItemsCount(
		FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest) {

		List<IssueReportFDSEntry> filteredEntries = _filter(
			fdsKeywords.getKeywords());

		return filteredEntries.size();
	}

	private Comparator<IssueReportFDSEntry> _comparator(String fieldName) {
		return switch (fieldName) {
			case "agentName" -> Comparator.comparing(IssueReportFDSEntry::getAgentName, Comparator.nullsLast(Comparator.naturalOrder()));
			case "date" -> Comparator.comparing(IssueReportFDSEntry::getDate, Comparator.nullsLast(Comparator.naturalOrder()));
			case "feedbackType" -> Comparator.comparing(IssueReportFDSEntry::getFeedbackType, Comparator.nullsLast(Comparator.naturalOrder()));
			case "issueType" -> Comparator.comparing(IssueReportFDSEntry::getIssueType, Comparator.nullsLast(Comparator.naturalOrder()));
			case "level" -> Comparator.comparing(IssueReportFDSEntry::getLevel, Comparator.nullsLast(Comparator.naturalOrder()));
			case "surface" -> Comparator.comparing(IssueReportFDSEntry::getSurface, Comparator.nullsLast(Comparator.naturalOrder()));
			case "userEmail" -> Comparator.comparing(IssueReportFDSEntry::getUserEmail, Comparator.nullsLast(Comparator.naturalOrder()));
			case "userMessage" -> Comparator.comparing(IssueReportFDSEntry::getUserMessage, Comparator.nullsLast(Comparator.naturalOrder()));
			default -> null;
		};
	}

	private boolean _containsIgnoreCase(String value, String lowerCaseNeedle) {
		if (value == null) {
			return false;
		}

		return StringUtil.toLowerCase(
			value
		).contains(
			lowerCaseNeedle
		);
	}

	private List<IssueReportFDSEntry> _filter(String keywords) {
		if (Validator.isNull(keywords)) {
			return _SAMPLE_ENTRIES;
		}

		String lowerCaseKeywords = StringUtil.toLowerCase(keywords);

		return _SAMPLE_ENTRIES.stream(
		).filter(
			entry -> _matches(entry, lowerCaseKeywords)
		).toList();
	}

	private boolean _matches(
		IssueReportFDSEntry entry, String lowerCaseKeywords) {

		if (_containsIgnoreCase(entry.getUserEmail(), lowerCaseKeywords) ||
			_containsIgnoreCase(entry.getUserMessage(), lowerCaseKeywords) ||
			_containsIgnoreCase(entry.getAgentName(), lowerCaseKeywords)) {

			return true;
		}

		return false;
	}

	private List<IssueReportFDSEntry> _sort(
		List<IssueReportFDSEntry> entries, Sort sort) {

		if (sort == null) {
			return entries;
		}

		String fieldName = sort.getFieldName();

		if (Validator.isNull(fieldName)) {
			return entries;
		}

		Comparator<IssueReportFDSEntry> comparator = _comparator(fieldName);

		if (comparator == null) {
			return entries;
		}

		if (sort.isReverse()) {
			comparator = comparator.reversed();
		}

		return entries.stream(
		).sorted(
			comparator
		).toList();
	}

	private static final List<IssueReportFDSEntry> _SAMPLE_ENTRIES = List.of(
		new IssueReportFDSEntry(
			new Date(1748180072000L), "AI_ASSISTANT_CHAT", "AI_ASSISTANT_CHAT",
			"POSITIVE", null, null, null, "first.user@example.com"),
		new IssueReportFDSEntry(
			new Date(1748180097000L), "AI_ASSISTANT_CHAT", "AI_ASSISTANT_CHAT",
			"NEGATIVE", "PII_EXPOSURE", "CRITICAL",
			"Agent revealed personal or sensitive data that should not be accessible",
			"amelia.cortez@example.com"),
		new IssueReportFDSEntry(
			new Date(1748183014000L), "AI_ASSISTANT_CHAT", "AI_ASSISTANT_CHAT",
			"NEGATIVE", "INAPPROPRIATE_OR_HARMFUL_CONTENT", "CRITICAL",
			"AI recommended an unsafe medication dosage with no clinical warnings",
			"anonymous@example.com"),
		new IssueReportFDSEntry(
			new Date(1748186614000L), "AI_ASSISTANT_CHAT", "CLICK_TO_CHAT",
			"POSITIVE", null, null, null, "fourth.user@example.com"),
		new IssueReportFDSEntry(
			new Date(1748190214000L), "CMS_ASSISTANT", "CMS_ASSISTANT",
			"NEGATIVE", "INCORRECT_OR_INACCURATE_RESPONSE", "HIGH",
			"HR Helper stated PTO carryover cap is 80 hours, actual policy is 40 hrs",
			"marcus.johnson@example.com"),
		new IssueReportFDSEntry(
			new Date(1748193814000L), "AI_ASSISTANT_CHAT", "AI_ASSISTANT_CHAT",
			"NEGATIVE", "AGENT_ERROR_OR_MALFUNCTION", "MEDIUM",
			"Asked for structured JSON output; received unformatted plain text",
			"sofia.reyes@example.com"),
		new IssueReportFDSEntry(
			new Date(1748197414000L), "CMS_ASSISTANT", "CMS_ASSISTANT",
			"NEGATIVE", "OTHER", "LOW",
			"Response was entirely off-topic and unrelated to the original question",
			"david.kim@example.com"),
		new IssueReportFDSEntry(
			new Date(1748201014000L), "AI_ASSISTANT_CHAT", "AI_ASSISTANT_CHAT",
			"NEGATIVE", "INCORRECT_OR_INACCURATE_RESPONSE", "MEDIUM",
			"Repeated the same incorrect calculation result 3 times without correction",
			"priya.singh@example.com"));

}