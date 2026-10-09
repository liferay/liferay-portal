/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.diff.internal;

import com.liferay.diff.DiffResult;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.FileUtil;

import java.io.Reader;

import java.util.ArrayList;
import java.util.List;

import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

import org.osgi.service.component.annotations.Component;

/**
 * This class can compare two different versions of a text. Source refers to the
 * earliest version of the text and target refers to a modified version of
 * source. Changes are considered either as a removal from the source or as an
 * addition to the target. This class detects changes to an entire line and also
 * detects changes within lines, such as, removal or addition of characters.
 * Take a look at <code>DiffTest</code> to see the expected inputs and outputs.
 *
 * @author Bruno Farache
 */
@Component(service = com.liferay.diff.Diff.class)
public class DiffImpl implements com.liferay.diff.Diff {

	/**
	 * This is a diff method with default values.
	 *
	 * @param  source the source text
	 * @param  target the modified version of the source text
	 * @return an array containing two lists of <code>DiffResults</code>, the
	 *         first element contains DiffResults related to changes in source
	 *         and the second element to changes in target
	 */
	@Override
	public List<DiffResult>[] diff(Reader source, Reader target) {
		int margin = 2;

		return diff(
			source, target, OPEN_INS, CLOSE_INS, OPEN_DEL, CLOSE_DEL, margin);
	}

	/**
	 * The main entrance of this class. This method will compare the two texts,
	 * highlight the changes by enclosing them with markers and return a list of
	 * <code>DiffResults</code>.
	 *
	 * @param  source the source text
	 * @param  target the modified version of the source text
	 * @param  addedMarkerStart the marker to indicate the start of text added
	 *         to the source
	 * @param  addedMarkerEnd the marker to indicate the end of text added to
	 *         the source
	 * @param  deletedMarkerStart the marker to indicate the start of text
	 *         deleted from the source
	 * @param  deletedMarkerEnd the marker to indicate the end of text deleted
	 *         from the source
	 * @param  margin the vertical margin to use in displaying differences
	 *         between changed line changes
	 * @return an array containing two lists of <code>DiffResults</code>, the
	 *         first element contains DiffResults related to changes in source
	 *         and the second element to changes in target
	 */
	@Override
	public List<DiffResult>[] diff(
		Reader source, Reader target, String addedMarkerStart,
		String addedMarkerEnd, String deletedMarkerStart,
		String deletedMarkerEnd, int margin) {

		List<DiffResult> sourceResults = new ArrayList<>();
		List<DiffResult> targetResults = new ArrayList<>();

		List<DiffResult>[] results = new List[] {sourceResults, targetResults};

		// Convert the texts to Lists where each element are lines of the texts.

		List<String> sourceStringList = FileUtil.toList(source);
		List<String> targetStringList = FileUtil.toList(target);

		// Make a a Diff of these lines and iterate over their Differences.

		Diff<String> diff = new Diff(sourceStringList, targetStringList);

		List<Difference> differences = diff.diff();

		for (Difference difference : differences) {
			if (difference.getAddedEnd() == Difference.NONE) {

				// Lines were deleted from source only.

				_highlightLines(
					sourceStringList, deletedMarkerStart, deletedMarkerEnd,
					difference.getDeletedStart(), difference.getDeletedEnd());

				margin = _calculateMargin(
					sourceResults, targetResults, difference.getDeletedStart(),
					difference.getAddedStart(), margin);

				List<String> changedLines = _addMargins(
					sourceStringList, difference.getDeletedStart(), margin);

				_addResults(
					sourceResults, sourceStringList, changedLines,
					difference.getDeletedStart(), difference.getDeletedEnd());

				changedLines = _addMargins(
					targetStringList, difference.getAddedStart(), margin);

				int deletedLines =
					difference.getDeletedEnd() + 1 -
						difference.getDeletedStart();

				for (int i = 0; i < deletedLines; i++) {
					changedLines.add(CONTEXT_LINE);
				}

				DiffResult diffResult = new DiffResult(
					difference.getDeletedStart(), changedLines);

				targetResults.add(diffResult);
			}
			else if (difference.getDeletedEnd() == Difference.NONE) {

				// Lines were added to target only.

				_highlightLines(
					targetStringList, addedMarkerStart, addedMarkerEnd,
					difference.getAddedStart(), difference.getAddedEnd());

				margin = _calculateMargin(
					sourceResults, targetResults, difference.getDeletedStart(),
					difference.getAddedStart(), margin);

				List<String> changedLines = _addMargins(
					sourceStringList, difference.getDeletedStart(), margin);

				int addedLines =
					difference.getAddedEnd() + 1 - difference.getAddedStart();

				for (int i = 0; i < addedLines; i++) {
					changedLines.add(CONTEXT_LINE);
				}

				DiffResult diffResult = new DiffResult(
					difference.getAddedStart(), changedLines);

				sourceResults.add(diffResult);

				changedLines = _addMargins(
					targetStringList, difference.getAddedStart(), margin);

				_addResults(
					targetResults, targetStringList, changedLines,
					difference.getAddedStart(), difference.getAddedEnd());
			}
			else {

				// Lines were deleted from source and added to target at the
				// same position. It needs to check for characters differences.

				_checkCharDiffs(
					sourceResults, targetResults, sourceStringList,
					targetStringList, addedMarkerStart, addedMarkerEnd,
					deletedMarkerStart, deletedMarkerEnd, difference);
			}
		}

		return results;
	}

	private List<String> _addMargins(
		List<String> stringList, int startPos, int margin) {

		List<String> changedLines = new ArrayList<>();

		if ((margin == 0) || (startPos == 0)) {
			return changedLines;
		}

		int i = startPos - margin;

		for (; i < 0; i++) {
			changedLines.add(CONTEXT_LINE);
		}

		for (; i < startPos; i++) {
			if (i >= stringList.size()) {
				continue;
			}

			changedLines.add(stringList.get(i));
		}

		return changedLines;
	}

	private void _addResults(
		List<DiffResult> results, List<String> stringList,
		List<String> changedLines, int start, int end) {

		changedLines.addAll(stringList.subList(start, end + 1));

		DiffResult diffResult = new DiffResult(start, changedLines);

		results.add(diffResult);
	}

	private int _calculateMargin(
		List<DiffResult> sourceResults, List<DiffResult> targetResults,
		int sourceBeginPos, int targetBeginPos, int margin) {

		int sourceMargin = _checkOverlapping(
			sourceResults, sourceBeginPos, margin);
		int targetMargin = _checkOverlapping(
			targetResults, targetBeginPos, margin);

		if (sourceMargin < targetMargin) {
			return sourceMargin;
		}

		return targetMargin;
	}

	private void _checkCharDiffs(
		List<DiffResult> sourceResults, List<DiffResult> targetResults,
		List<String> sourceStringList, List<String> targetStringList,
		String addedMarkerStart, String addedMarkerEnd,
		String deletedMarkerStart, String deletedMarkerEnd,
		Difference difference) {

		boolean aligned = false;

		int i = difference.getDeletedStart();
		int j = difference.getAddedStart();

		// A line with changed characters may have its position shifted some
		// lines above or below. These for loops will try to align these lines.
		// While these lines are not aligned, highlight them as either additions
		// or deletions.

		for (; i <= difference.getDeletedEnd(); i++) {
			for (; j <= difference.getAddedEnd(); j++) {
				if (!_isMaxLineLengthExceeded(
						sourceStringList.get(i), targetStringList.get(j)) &&
					_lineDiff(
						sourceResults, targetResults, sourceStringList,
						targetStringList, addedMarkerStart, addedMarkerEnd,
						deletedMarkerStart, deletedMarkerEnd, i, j, false)) {

					aligned = true;

					break;
				}

				_highlightLines(
					targetStringList, addedMarkerStart, addedMarkerEnd, j, j);

				DiffResult targetResult = new DiffResult(
					j, targetStringList.subList(j, j + 1));

				targetResults.add(targetResult);

				sourceResults.add(new DiffResult(j, CONTEXT_LINE));
			}

			if (aligned) {
				break;
			}

			_highlightLines(
				sourceStringList, deletedMarkerStart, deletedMarkerEnd, i, i);

			DiffResult sourceResult = new DiffResult(
				i, sourceStringList.subList(i, i + 1));

			sourceResults.add(sourceResult);

			targetResults.add(new DiffResult(i, CONTEXT_LINE));
		}

		i = i + 1;
		j = j + 1;

		// Lines are aligned, check for differences of the following lines.

		for (;
			 (i <= difference.getDeletedEnd()) &&
			 (j <= difference.getAddedEnd());
			 i++, j++) {

			if (!_isMaxLineLengthExceeded(
					sourceStringList.get(i), targetStringList.get(j))) {

				_lineDiff(
					sourceResults, targetResults, sourceStringList,
					targetStringList, addedMarkerStart, addedMarkerEnd,
					deletedMarkerStart, deletedMarkerEnd, i, j, true);
			}
			else {
				_highlightLines(
					sourceStringList, deletedMarkerStart, deletedMarkerEnd, i,
					i);

				DiffResult sourceResult = new DiffResult(
					i, sourceStringList.subList(i, i + 1));

				sourceResults.add(sourceResult);

				targetResults.add(new DiffResult(i, CONTEXT_LINE));

				_highlightLines(
					targetStringList, addedMarkerStart, addedMarkerEnd, j, j);

				DiffResult targetResult = new DiffResult(
					j, targetStringList.subList(j, j + 1));

				targetResults.add(targetResult);

				sourceResults.add(new DiffResult(j, CONTEXT_LINE));
			}
		}

		// After the for loop above, some lines might remained unchecked. They
		// are considered as deletions or additions.

		for (; i <= difference.getDeletedEnd(); i++) {
			_highlightLines(
				sourceStringList, deletedMarkerStart, deletedMarkerEnd, i, i);

			DiffResult sourceResult = new DiffResult(
				i, sourceStringList.subList(i, i + 1));

			sourceResults.add(sourceResult);

			targetResults.add(new DiffResult(i, CONTEXT_LINE));
		}

		for (; j <= difference.getAddedEnd(); j++) {
			_highlightLines(
				targetStringList, addedMarkerStart, addedMarkerEnd, j, j);

			DiffResult targetResult = new DiffResult(
				j, targetStringList.subList(j, j + 1));

			targetResults.add(targetResult);

			sourceResults.add(new DiffResult(j, CONTEXT_LINE));
		}
	}

	private int _checkOverlapping(
		List<DiffResult> results, int startPos, int margin) {

		if (results.isEmpty() || ((startPos - margin) < 0)) {
			return margin;
		}

		DiffResult lastDiff = results.get(results.size() - 1);

		List<String> changedLines = lastDiff.getChangedLines();

		if (changedLines.isEmpty()) {
			return margin;
		}

		int lastChangedLine =
			(lastDiff.getLineNumber() - 1) + changedLines.size();

		int currentChangedLine = startPos - margin;

		if (changedLines.size() == 1) {
			String changedLine = changedLines.get(0);

			if (changedLine.equals(CONTEXT_LINE)) {
				currentChangedLine = currentChangedLine + 1;
			}
		}

		if (currentChangedLine < lastChangedLine) {
			return margin + currentChangedLine - lastChangedLine;
		}

		return margin;
	}

	private void _highlightChars(
		List<String> stringList, String markerStart, String markerEnd,
		int startPos, int endPos) {

		String start = markerStart + stringList.get(startPos);

		stringList.set(startPos, start);

		String end = stringList.get(endPos) + markerEnd;

		stringList.set(endPos, end);
	}

	private void _highlightLines(
		List<String> stringList, String markerStart, String markerEnd,
		int startPos, int endPos) {

		for (int i = startPos; i <= endPos; i++) {
			stringList.set(i, markerStart + stringList.get(i) + markerEnd);
		}
	}

	private boolean _isMaxLineLengthExceeded(
		String sourceString, String targetString) {

		if ((sourceString.length() > _DIFF_MAX_LINE_LENGTH) ||
			(targetString.length() > _DIFF_MAX_LINE_LENGTH)) {

			return true;
		}

		return false;
	}

	private boolean _lineDiff(
		List<DiffResult> sourceResults, List<DiffResult> targetResults,
		List<String> sourceStringList, List<String> targetStringList,
		String addedMarkerStart, String addedMarkerEnd,
		String deletedMarkerStart, String deletedMarkerEnd,
		int sourceChangedLine, int targetChangedLine, boolean aligned) {

		String source = sourceStringList.get(sourceChangedLine);
		String target = targetStringList.get(targetChangedLine);

		// Convert the lines to lists where each element are chars of the lines.

		List<String> sourceList = _toList(source);
		List<String> targetList = _toList(target);

		Diff<String> diff = new Diff<>(sourceList, targetList);

		List<Difference> differences = diff.diff();

		int deletedChars = 0;
		int addedChars = 0;

		// The following while loop will calculate how many characters of the
		// source line need to be changed to be equals to the target line.

		if (!aligned) {
			for (Difference difference : differences) {
				if (difference.getDeletedEnd() != Difference.NONE) {
					deletedChars +=
						difference.getDeletedEnd() -
							difference.getDeletedStart() + 1;
				}

				if (difference.getAddedEnd() == Difference.NONE) {
					continue;
				}

				addedChars +=
					difference.getAddedEnd() - difference.getAddedStart() + 1;
			}
		}

		// If a lot of changes were needed (more than half of the source line
		// length), consider this as not aligned yet.

		if ((deletedChars > (sourceList.size() / 2)) ||
			(addedChars > (sourceList.size() / 2))) {

			return false;
		}

		boolean sourceChanged = false;
		boolean targetChanged = false;

		// Iterate over Differences between chars of these lines.

		for (Difference difference : differences) {
			if (difference.getAddedEnd() == Difference.NONE) {

				// Chars were deleted from source only.

				_highlightChars(
					sourceList, deletedMarkerStart, deletedMarkerEnd,
					difference.getDeletedStart(), difference.getDeletedEnd());

				sourceChanged = true;
			}
			else if (difference.getDeletedEnd() == Difference.NONE) {

				// Chars were added to target only.

				_highlightChars(
					targetList, addedMarkerStart, addedMarkerEnd,
					difference.getAddedStart(), difference.getAddedEnd());

				targetChanged = true;
			}
			else {

				// Chars were both deleted and added.

				_highlightChars(
					sourceList, deletedMarkerStart, deletedMarkerEnd,
					difference.getDeletedStart(), difference.getDeletedEnd());

				sourceChanged = true;

				_highlightChars(
					targetList, addedMarkerStart, addedMarkerEnd,
					difference.getAddedStart(), difference.getAddedEnd());

				targetChanged = true;
			}
		}

		if (sourceChanged) {
			DiffResult sourceResult = new DiffResult(
				sourceChangedLine, _toString(sourceList));

			sourceResults.add(sourceResult);

			if (!targetChanged) {
				DiffResult targetResult = new DiffResult(
					targetChangedLine, target);

				targetResults.add(targetResult);
			}
		}

		if (targetChanged) {
			if (!sourceChanged) {
				DiffResult sourceResult = new DiffResult(
					sourceChangedLine, source);

				sourceResults.add(sourceResult);
			}

			DiffResult targetResult = new DiffResult(
				targetChangedLine, _toString(targetList));

			targetResults.add(targetResult);
		}

		return true;
	}

	private List<String> _toList(String line) {
		List<String> result = new ArrayList<>(line.length());

		for (int i = 0; i < line.length(); i++) {
			result.add(line.substring(i, i + 1));
		}

		return result;
	}

	private String _toString(List<String> line) {
		if (line.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(line.size());

		for (String linePart : line) {
			sb.append(linePart);
		}

		return sb.toString();
	}

	private static final int _DIFF_MAX_LINE_LENGTH = 5000;

}