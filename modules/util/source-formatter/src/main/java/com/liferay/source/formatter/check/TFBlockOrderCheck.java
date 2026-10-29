/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.TFSourceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class TFBlockOrderCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		String[] lines = content.split("\n", -1);

		_sortNestedEntries(lines);

		List<List<Entry>> entryGroups = _getEntryGroups(
			lines, 0, lines.length, true);

		if (entryGroups != null) {
			for (List<Entry> entries : entryGroups) {
				_sort(lines, entries);
			}
		}

		StringBundler sb = new StringBundler(lines.length * 2);

		for (String line : lines) {
			sb.append(line);
			sb.append(CharPool.NEW_LINE);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private int _getBlockEndLineIndex(
		String[] lines, int startLineIndex, int level) {

		boolean blockComment = false;

		for (int i = startLineIndex; i < lines.length; i++) {
			StringBundler sb = new StringBundler();

			blockComment = TFSourceUtil.appendBrackets(
				lines[i], blockComment, sb);

			String brackets = sb.toString();

			for (char c : brackets.toCharArray()) {
				if ((c == CharPool.OPEN_BRACKET) ||
					(c == CharPool.OPEN_CURLY_BRACE) ||
					(c == CharPool.OPEN_PARENTHESIS)) {

					level++;
				}
				else {
					level--;
				}

				if (level < 0) {
					return -1;
				}
			}

			if (blockComment) {
				continue;
			}

			String heredocMarker = TFSourceUtil.getHeredocMarker(lines[i]);

			if (heredocMarker != null) {
				i = TFSourceUtil.getHeredocEndLineIndex(
					lines, i, heredocMarker);

				if (i == -1) {
					return -1;
				}
			}

			if (level == 0) {
				return i;
			}
		}

		return -1;
	}

	private List<List<Entry>> _getEntryGroups(
		String[] lines, int startLineIndex, int endLineIndex,
		boolean topLevel) {

		List<List<Entry>> entryGroups = new ArrayList<>();

		List<Entry> entries = new ArrayList<>();

		for (int i = startLineIndex; i < endLineIndex; i++) {
			String trimmedLine = StringUtil.trim(lines[i]);

			if (Validator.isBlank(trimmedLine) ||
				TFSourceUtil.isCommentLine(lines[i])) {

				if (!entries.isEmpty()) {
					entryGroups.add(entries);

					entries = new ArrayList<>();
				}

				i = TFSourceUtil.getCommentEndLineIndex(lines, i);

				if (i == -1) {
					return null;
				}

				continue;
			}

			if (topLevel) {
				Matcher matcher = _topLevelBlockPattern.matcher(lines[i]);

				if (!matcher.matches()) {
					return null;
				}
			}

			String sortKey = _getSortKey(trimmedLine);

			if (sortKey == null) {
				return null;
			}

			int entryEndLineIndex = _getBlockEndLineIndex(lines, i, 0);

			if ((entryEndLineIndex == -1) ||
				(entryEndLineIndex >= endLineIndex)) {

				return null;
			}

			Entry entry = new Entry(entryEndLineIndex, lines, i);

			entry.setSortKey(sortKey);

			entries.add(entry);

			i = entryEndLineIndex;
		}

		if (!entries.isEmpty()) {
			entryGroups.add(entries);
		}

		return entryGroups;
	}

	private String _getSortKey(String trimmedLine) {
		Matcher matcher = _keyPattern.matcher(trimmedLine);

		if (matcher.find()) {
			return StringUtil.removeChars(matcher.group(1), CharPool.QUOTE);
		}

		matcher = _blockHeaderPattern.matcher(trimmedLine);

		if (matcher.matches()) {
			return StringUtil.removeChars(matcher.group(1), CharPool.QUOTE);
		}

		return null;
	}

	private void _sort(String[] lines, List<Entry> entries) {
		List<Entry> sortedEntries = new ArrayList<>(entries);

		Collections.sort(sortedEntries, new EntryComparator());

		if (sortedEntries.equals(entries)) {
			return;
		}

		Entry firstEntry = entries.get(0);

		int index = firstEntry.getStartLineIndex();

		for (Entry sortedEntry : sortedEntries) {
			for (String line : sortedEntry.getLines()) {
				lines[index++] = line;
			}
		}
	}

	private void _sortNestedEntries(String[] lines) {
		boolean blockComment = false;

		for (int i = 0; i < lines.length; i++) {
			boolean previousBlockComment = blockComment;

			blockComment = TFSourceUtil.appendBrackets(
				lines[i], blockComment, new StringBundler());

			if (previousBlockComment || blockComment) {
				continue;
			}

			String heredocMarker = TFSourceUtil.getHeredocMarker(lines[i]);

			if (heredocMarker != null) {
				int heredocEndLineIndex = TFSourceUtil.getHeredocEndLineIndex(
					lines, i, heredocMarker);

				if (heredocEndLineIndex == -1) {
					return;
				}

				i = heredocEndLineIndex;

				continue;
			}

			String trimmedLine = StringUtil.trim(
				TFSourceUtil.removeComment(lines[i]));

			if (!trimmedLine.endsWith(StringPool.OPEN_CURLY_BRACE)) {
				continue;
			}

			int blockEndLineIndex = _getBlockEndLineIndex(lines, i + 1, 1);

			if (blockEndLineIndex == -1) {
				continue;
			}

			List<List<Entry>> entryGroups = _getEntryGroups(
				lines, i + 1, blockEndLineIndex, false);

			if (entryGroups == null) {
				continue;
			}

			for (List<Entry> entries : entryGroups) {
				_sort(lines, entries);
			}
		}
	}

	private static final Pattern _blockHeaderPattern = Pattern.compile(
		"([\\w-]+(\\s+\"[^\"]*\")*)\\s*\\{(\\})?");
	private static final Pattern _keyPattern = Pattern.compile(
		"^(\"[^\"]+\"|[\\w.-]+)\\s*[:=](?!=)");
	private static final Pattern _topLevelBlockPattern = Pattern.compile(
		"\\w.*\\{(\\})?");

	private class Entry {

		public Entry(int endLineIndex, String[] lines, int startLineIndex) {
			_startLineIndex = startLineIndex;

			for (int i = startLineIndex; i <= endLineIndex; i++) {
				_lines.add(lines[i]);
			}
		}

		public List<String> getLines() {
			return _lines;
		}

		public String getSortKey() {
			return _sortKey;
		}

		public int getStartLineIndex() {
			return _startLineIndex;
		}

		public void setSortKey(String sortKey) {
			_sortKey = sortKey;
		}

		private final List<String> _lines = new ArrayList<>();
		private String _sortKey;
		private final int _startLineIndex;

	}

	private class EntryComparator implements Comparator<Entry> {

		@Override
		public int compare(Entry entry1, Entry entry2) {
			return _naturalOrderStringComparator.compare(
				entry1.getSortKey(), entry2.getSortKey());
		}

		private final NaturalOrderStringComparator
			_naturalOrderStringComparator = new NaturalOrderStringComparator(
				true, true);

	}

}