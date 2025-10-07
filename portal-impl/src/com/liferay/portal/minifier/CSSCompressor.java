/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.minifier;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Iván Zaera Avellón
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class CSSCompressor {

	public CSSCompressor(Reader reader) throws IOException {
		int c = 0;

		while ((c = reader.read()) != -1) {
			_sb.append((char)c);
		}
	}

	public void compress(Writer writer, int lineBreakPosition)
		throws IOException {

		String css = _sb.toString();
		List<String> comments = new ArrayList<>();

		css = _preserveCandidateCommments(css, comments);

		List<String> preservedTokens = new ArrayList<>();

		css = _preserveParensToken(css, "url", preservedTokens);

		css = _preserveParensToken(css, "calc", preservedTokens);

		css = _preserveToken(
			css, "progid:DXImageTransform.Microsoft.Matrix",
			"(?i)progid:DXImageTransform.Microsoft.Matrix\\s*([\"']?)", false,
			preservedTokens);

		css = _preserveStrings(css, comments, preservedTokens);

		css = _removeComments(css, comments, preservedTokens);

		css = _preserveIE9Hack(css, preservedTokens);

		css = _collapseWhitespace(css);

		css = _removeUnneededLeadingSpaces(css);

		css = _restoreContainerQuerySpaces(css);

		css = _retainSpaceForSpecialIE6Cases(css);

		css = _removeWhitespaceAfterPreservedComments(css);

		css = _hoistCharsetDirectives(css);

		css = _collapseCharsetDirectives(css);

		css = _lowercaseDirectives(css);

		css = _lowercasePseudoElements(css);

		css = _lowercaseFunctions(css);

		css = _lowercaseFunctionsThatCanBeValues(css);

		css = _putSomeSpacesBack(css);

		css = _removeUnneededTrailingSpaces(css);

		css = _removeUnneededSemiColons(css);

		css = _replace0UnitWith0(css);

		css = _removeUnneededDecimals(css);

		css = _collapseMultipleZeroes(css);

		css = _restoreSomeMultipleZeroes(css);

		css = _removeIntegerZeroBeforeDecimals(css);

		css = _shortenRGBColors(css);

		css = _shortenTwinComponentDigitsColors(css);

		css = _shortenSymbolicNameColors(css);

		css = _replaceBorderNone(css);

		css = _shortenIEOpacityFilter(css);

		css = _removeEmptyRules(css);

		css = _applyLineBreak(css, lineBreakPosition);

		css = _removeMultipleSemicolons(css);

		css = _restorePreservedTokens(css, preservedTokens);

		css = css.trim();

		writer.write(css);
	}

	private String _applyLineBreak(String css, int lineBreakPosition) {
		if (lineBreakPosition >= 0) {
			StringBuffer sb = new StringBuffer(css);

			int i = 0;
			int lineStartPosition = 0;

			while (i < sb.length()) {
				char c = sb.charAt(i++);

				if ((c == '}') &&
					((i - lineStartPosition) > lineBreakPosition)) {

					sb.insert(i, '\n');
					lineStartPosition = i;
				}
			}

			css = sb.toString();
		}

		return css;
	}

	private String _collapseCharsetDirectives(String css) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _collapseCharsetDirectivesPattern.matcher(css);

		while (matcher.find()) {
			matcher.appendReplacement(
				sb,
				StringBundler.concat(
					matcher.group(2), StringUtil.toLowerCase(matcher.group(3)),
					matcher.group(4)));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private String _collapseMultipleZeroes(String css) {
		css = css.replaceAll(":0 0 0 0(;|})", ":0$1");

		css = css.replaceAll(":0 0 0(;|})", ":0$1");

		return css.replaceAll("(?<!flex):0 0(;|})", ":0$1");
	}

	private String _collapseWhitespace(String css) {
		return css.replaceAll("\\s+", " ");
	}

	private String _hoistCharsetDirectives(String css) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _hoistCharsetDirectivesPattern.matcher(css);

		while (matcher.find()) {
			String s = matcher.group(1);

			s = s.replaceAll("\\\\", "\\\\\\\\");

			s = s.replaceAll("\\$", "\\\\\\$");

			matcher.appendReplacement(
				sb,
				StringUtil.toLowerCase(matcher.group(2)) + matcher.group(3) +
					s);
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private String _lowercaseDirectives(String css) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _lowercaseDirectivesPattern.matcher(css);

		while (matcher.find()) {
			matcher.appendReplacement(
				sb, '@' + StringUtil.toLowerCase(matcher.group(1)));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private String _lowercaseFunctions(String css) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _lowercaseFunctionsPattern.matcher(css);

		while (matcher.find()) {
			matcher.appendReplacement(
				sb, ':' + StringUtil.toLowerCase(matcher.group(1)) + '(');
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private String _lowercaseFunctionsThatCanBeValues(String css) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _lowercaseFunctionsThatCanBeValuesPattern.matcher(
			css);

		while (matcher.find()) {
			matcher.appendReplacement(
				sb,
				matcher.group(1) + StringUtil.toLowerCase(matcher.group(2)));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private String _lowercasePseudoElements(String css) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _lowercasePseudoElementsPattern.matcher(css);

		while (matcher.find()) {
			matcher.appendReplacement(
				sb, ':' + StringUtil.toLowerCase(matcher.group(1)));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private String _preserveCandidateCommments(
		String css, List<String> comments) {

		StringBuffer sb = new StringBuffer(css);

		int cssLength = css.length();
		int startIndex = 0;

		while ((startIndex = sb.indexOf("/*", startIndex)) >= 0) {
			int endIndex = sb.indexOf("*/", startIndex + 2);

			if (endIndex < 0) {
				endIndex = cssLength;
			}

			comments.add(sb.substring(startIndex + 2, endIndex));

			sb.replace(
				startIndex + 2, endIndex,
				"___YUICSSMIN_PRESERVE_CANDIDATE_COMMENT_" +
					(comments.size() - 1) + "___");

			startIndex += 2;
		}

		return sb.toString();
	}

	private String _preserveIE9Hack(String css, List<String> preservedTokens) {
		while (css.indexOf(_BACKSLASH_9) > -1) {
			preservedTokens.add(_BACKSLASH_9);

			css = StringUtil.replace(
				css, _BACKSLASH_9,
				"___YUICSSMIN_PRESERVED_TOKEN_" + (preservedTokens.size() - 1) +
					"___");
		}

		return css;
	}

	private String _preserveParensToken(
		String css, String preservedToken, List<String> preservedTokens) {

		StringBuffer sb = new StringBuffer();

		int fromIndex = 0;
		int startIndex;

		while ((startIndex = css.indexOf(preservedToken + "(", fromIndex)) !=
					-1) {

			int index = startIndex + preservedToken.length() + 1;
			int nestingLevel = 1;

			while (nestingLevel > 0) {
				if (css.charAt(index) == '(') {
					nestingLevel++;
				}
				else if (css.charAt(index) == ')') {
					nestingLevel--;
				}

				index++;
			}

			sb.append(css.substring(fromIndex, startIndex));
			sb.append(preservedToken);
			sb.append("(___YUICSSMIN_PRESERVED_TOKEN_");
			sb.append(preservedTokens.size());
			sb.append("___)");

			preservedTokens.add(
				css.substring(
					startIndex + preservedToken.length() + 1, index - 1));

			fromIndex = index;
		}

		sb.append(css.substring(fromIndex));

		return sb.toString();
	}

	private String _preserveStrings(
		String css, List<String> comments, List<String> preservedTokens) {

		StringBuffer sb = new StringBuffer();

		Matcher matcher = _preserveStringsPattern.matcher(css);

		while (matcher.find()) {
			String token = matcher.group();

			char quote = token.charAt(0);

			token = token.substring(1, token.length() - 1);

			if (token.contains("___YUICSSMIN_PRESERVE_CANDIDATE_COMMENT_")) {
				for (int i = 0; i < comments.size(); i++) {
					token = StringUtil.replace(
						token,
						"___YUICSSMIN_PRESERVE_CANDIDATE_COMMENT_" + i + "___",
						comments.get(i));
				}
			}

			token = token.replaceAll(
				"(?i)progid:DXImageTransform.Microsoft.Alpha\\(Opacity=",
				"alpha(opacity=");

			preservedTokens.add(token);

			String preserver = StringBundler.concat(
				Character.toString(quote), "___YUICSSMIN_PRESERVED_TOKEN_",
				preservedTokens.size() - 1, "___", Character.toString(quote));

			matcher.appendReplacement(sb, preserver);
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private String _preserveToken(
		String css, String preservedToken, String tokenRegex,
		boolean removeWhiteSpace, List<String> preservedTokens) {

		StringBuffer sb = new StringBuffer();

		int appendIndex = 0;

		Pattern pattern = Pattern.compile(tokenRegex);

		Matcher matcher = pattern.matcher(css);

		int maxIndex = css.length() - 1;

		while (matcher.find()) {
			if (matcher.start() < appendIndex) {
				continue;
			}

			String terminator = matcher.group(1);

			if (terminator.length() == 0) {
				terminator = ")";
			}

			boolean foundTerminator = false;

			int endIndex = matcher.end() - 1;

			while (!foundTerminator && ((endIndex + 1) <= maxIndex)) {
				endIndex = css.indexOf(terminator, endIndex + 1);

				if (endIndex <= 0) {
					break;
				}

				if (css.charAt(endIndex - 1) != '\\') {
					foundTerminator = true;

					if (!Objects.equals(terminator, ")")) {
						endIndex = css.indexOf(")", endIndex);
					}
				}
			}

			sb.append(css.substring(appendIndex, matcher.start()));

			if (foundTerminator) {
				int startIndex =
					matcher.start() + (preservedToken.length() + 1);

				String token = css.substring(startIndex, endIndex);

				if (removeWhiteSpace) {
					token = token.replaceAll("\\s+", "");
				}

				preservedTokens.add(token);

				String preserver = StringBundler.concat(
					preservedToken, "(___YUICSSMIN_PRESERVED_TOKEN_",
					preservedTokens.size() - 1, "___)");

				sb.append(preserver);

				appendIndex = endIndex + 1;
			}
			else {
				sb.append(css.substring(matcher.start(), matcher.end()));
				appendIndex = matcher.end();
			}
		}

		sb.append(css.substring(appendIndex));

		return sb.toString();
	}

	private String _putSomeSpacesBack(String css) {
		return css.replaceAll("(?i)\\band\\(", "and (");
	}

	private String _removeComments(
		String css, List<String> comments, List<String> preservedTokens) {

		for (int i = 0; i < comments.size(); i++) {
			String comment = comments.get(i);

			String placeholder =
				"___YUICSSMIN_PRESERVE_CANDIDATE_COMMENT_" + i + "___";

			if (comment.startsWith("!")) {
				preservedTokens.add(comment);

				css = StringUtil.replace(
					css, placeholder,
					"___YUICSSMIN_PRESERVED_TOKEN_" +
						(preservedTokens.size() - 1) + "___");

				continue;
			}

			if (comment.endsWith("\\")) {
				preservedTokens.add("\\");

				css = StringUtil.replace(
					css, placeholder,
					"___YUICSSMIN_PRESERVED_TOKEN_" +
						(preservedTokens.size() - 1) + "___");

				i = i + 1;

				preservedTokens.add("");

				css = StringUtil.replace(
					css, "___YUICSSMIN_PRESERVE_CANDIDATE_COMMENT_" + i + "___",
					"___YUICSSMIN_PRESERVED_TOKEN_" +
						(preservedTokens.size() - 1) + "___");

				continue;
			}

			if (comment.length() == 0) {
				int startIndex = css.indexOf(placeholder);

				if ((startIndex > 2) && (css.charAt(startIndex - 3) == '>')) {
					preservedTokens.add("");

					css = StringUtil.replace(
						css, placeholder,
						"___YUICSSMIN_PRESERVED_TOKEN_" +
							(preservedTokens.size() - 1) + "___");
				}
			}

			css = StringUtil.removeSubstring(css, "/*" + placeholder + "*/");
		}

		return css;
	}

	private String _removeEmptyRules(String css) {
		css = css.replaceAll(
			"\\(([\\-A-Za-z]+):([0-9]+)\\/([0-9]+)\\)",
			"($1:$2___YUI_QUERY_FRACTION___$3)");

		css = css.replaceAll("[^\\}\\{/;]+\\{\\}", "");

		return css.replaceAll("___YUI_QUERY_FRACTION___", "/");
	}

	private String _removeIntegerZeroBeforeDecimals(String css) {
		return css.replaceAll("(:|\\s)0+\\.(\\d+)", "$1.$2");
	}

	private String _removeMultipleSemicolons(String css) {
		return css.replaceAll(";;+", ";");
	}

	private String _removeUnneededDecimals(String css) {
		return css.replaceAll(
			"([0-9])\\.0(px|em|%|in|cm|mm|pc|pt|ex|deg|g?rad|m?s|k?hz| |;)",
			"$1$2");
	}

	private String _removeUnneededLeadingSpaces(String css) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _removeUnneededLeadingSpacesPattern.matcher(css);

		while (matcher.find()) {
			String group = matcher.group();

			group = group.replaceAll(":", "___YUICSSMIN_PSEUDOCLASSCOLON___");

			group = group.replaceAll("\\\\", "\\\\\\\\");

			group = group.replaceAll("\\$", "\\\\\\$");

			matcher.appendReplacement(sb, group);
		}

		matcher.appendTail(sb);

		css = sb.toString();

		css = css.replaceAll("\\s+([!{};:>+\\(\\)\\],])", "$1");

		css = css.replaceAll("!important", " !important");

		css = css.replaceAll("___YUICSSMIN_PSEUDOCLASSCOLON___", ":");

		return css;
	}

	private String _removeUnneededSemiColons(String css) {
		return css.replaceAll(";+}", "}");
	}

	private String _removeUnneededTrailingSpaces(String css) {
		return css.replaceAll("([!{}:;>+\\(\\[,])\\s+", "$1");
	}

	private String _removeWhitespaceAfterPreservedComments(String css) {
		return css.replaceAll("\\*/ ", "*/");
	}

	private String _replace0UnitWith0(String css) {
		String oldCss = null;

		do {
			oldCss = css;

			Matcher matcher = _replace0UnitWith0Pattern1.matcher(css);

			css = matcher.replaceAll("$1$20");
		}
		while (!css.equals(oldCss));

		do {
			oldCss = css;

			Matcher matcher = _replace0UnitWith0Pattern2.matcher(css);

			css = matcher.replaceAll("($10");
		}
		while (!css.equals(oldCss));

		return css;
	}

	private String _replaceBorderNone(String css) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _replaceBorderNonePattern.matcher(css);

		while (matcher.find()) {
			matcher.appendReplacement(
				sb,
				StringUtil.toLowerCase(matcher.group(1)) + ":0" +
					matcher.group(2));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private String _restoreContainerQuerySpaces(String css) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _restoreContainerQuerySpacesPattern.matcher(css);

		while (matcher.find()) {
			String group = matcher.group();

			group = group.substring(0, group.length() - 1);

			group += " (";

			matcher.appendReplacement(sb, group);
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private String _restorePreservedTokens(
		String css, List<String> preservedTokens) {

		for (int i = 0; i < preservedTokens.size(); i++) {
			String preservedToken = preservedTokens.get(i);

			css = StringUtil.replace(
				css, "___YUICSSMIN_PRESERVED_TOKEN_" + i + "___",
				preservedToken);
		}

		return css;
	}

	private String _restoreSomeMultipleZeroes(String css) {
		Matcher matcher = _restoreSomeMultipleZeroesPattern.matcher(css);

		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			matcher.appendReplacement(
				sb,
				StringUtil.toLowerCase(matcher.group(1)) + ":0 0" +
					matcher.group(2));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private String _retainSpaceForSpecialIE6Cases(String css) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _retainSpaceForSpecialIE6CasesPattern.matcher(css);

		while (matcher.find()) {
			matcher.appendReplacement(
				sb,
				StringBundler.concat(
					":first-", StringUtil.toLowerCase(matcher.group(1)), " ",
					matcher.group(2)));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private String _shortenIEOpacityFilter(String css) {
		return css.replaceAll(
			"(?i)progid:DXImageTransform.Microsoft.Alpha\\(Opacity=",
			"alpha(opacity=");
	}

	private String _shortenRGBColors(String css) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _shortenRGBColorsPattern.matcher(css);

		while (matcher.find()) {
			String group = matcher.group(1);

			String[] rgbColors = group.split(",");

			StringBuffer hexColor = new StringBuffer("#");

			for (String rgbColor : rgbColors) {
				int val = GetterUtil.getInteger(rgbColor);

				if (val < 16) {
					hexColor.append("0");
				}

				if (val > 255) {
					val = 255;
				}

				hexColor.append(Integer.toHexString(val));
			}

			matcher.appendReplacement(sb, hexColor.toString());
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private String _shortenSymbolicNameColors(String css) {
		css = css.replaceAll("(:|\\s)(#f00)(;|})", "$1red$3");

		css = css.replaceAll("(:|\\s)(#000080)(;|})", "$1navy$3");

		css = css.replaceAll("(:|\\s)(#808080)(;|})", "$1gray$3");

		css = css.replaceAll("(:|\\s)(#808000)(;|})", "$1olive$3");

		css = css.replaceAll("(:|\\s)(#800080)(;|})", "$1purple$3");

		css = css.replaceAll("(:|\\s)(#c0c0c0)(;|})", "$1silver$3");

		css = css.replaceAll("(:|\\s)(#008080)(;|})", "$1teal$3");

		css = css.replaceAll("(:|\\s)(#ffa500)(;|})", "$1orange$3");

		css = css.replaceAll("(:|\\s)(#800000)(;|})", "$1maroon$3");

		return css;
	}

	private String _shortenTwinComponentDigitsColors(String css) {
		StringBuffer sb = new StringBuffer();

		int index = 0;
		Matcher matcher = _shortenTwinComponentDigitsColorsPattern.matcher(css);

		while (matcher.find(index)) {
			sb.append(css.substring(index, matcher.start()));

			boolean filter = false;

			if ((matcher.group(1) != null) &&
				!Objects.equals(matcher.group(1), "")) {

				filter = true;
			}

			if (filter) {
				sb.append(matcher.group(1));

				sb.append("#");

				sb.append(matcher.group(2));

				sb.append(matcher.group(3));

				sb.append(matcher.group(4));

				sb.append(matcher.group(5));

				sb.append(matcher.group(6));

				sb.append(matcher.group(7));
			}
			else {
				if (StringUtil.equalsIgnoreCase(
						matcher.group(2), matcher.group(3)) &&
					StringUtil.equalsIgnoreCase(
						matcher.group(4), matcher.group(5)) &&
					StringUtil.equalsIgnoreCase(
						matcher.group(6), matcher.group(7))) {

					sb.append("#");

					sb.append(StringUtil.toLowerCase(matcher.group(3)));

					sb.append(StringUtil.toLowerCase(matcher.group(5)));

					sb.append(StringUtil.toLowerCase(matcher.group(7)));
				}
				else {
					sb.append("#");

					sb.append(StringUtil.toLowerCase(matcher.group(2)));

					sb.append(StringUtil.toLowerCase(matcher.group(3)));

					sb.append(StringUtil.toLowerCase(matcher.group(4)));

					sb.append(StringUtil.toLowerCase(matcher.group(5)));

					sb.append(StringUtil.toLowerCase(matcher.group(6)));

					sb.append(StringUtil.toLowerCase(matcher.group(7)));
				}
			}

			index = matcher.end(7);
		}

		sb.append(css.substring(index));

		return sb.toString();
	}

	private static final String _BACKSLASH_9 = "\\9";

	private static final Pattern _collapseCharsetDirectivesPattern =
		Pattern.compile("(?i)^((\\s*)(@charset)( [^;]+;\\s*))+");
	private static final Pattern _hoistCharsetDirectivesPattern =
		Pattern.compile("(?i)^(.*)(@charset)( \"[^\"]*\";)");
	private static final Pattern _lowercaseDirectivesPattern = Pattern.compile(
		"(?i)@(font-face|import|(?:-(?:atsc|khtml|moz|ms|o|wap|webkit)-)?" +
			"keyframe|media|page|namespace)");
	private static final Pattern _lowercaseFunctionsPattern = Pattern.compile(
		"(?i):(lang|not|nth-child|nth-last-child|nth-last-of-type|" +
			"nth-of-type|(?:-(?:moz|webkit)-)?any)\\(");
	private static final Pattern _lowercaseFunctionsThatCanBeValuesPattern =
		Pattern.compile(
			StringBundler.concat(
				"(?i)([:,\\( ]\\s*)(attr|color-stop|from|rgba|to|url|",
				"(?:-(?:atsc|khtml|moz|ms|o|wap|webkit)-)?(?:calc|max|min|",
				"(?:repeating-)?(?:linear|radial)-gradient)",
				"|-webkit-gradient)"));
	private static final Pattern _lowercasePseudoElementsPattern =
		Pattern.compile(
			StringBundler.concat(
				"(?i):(active|after|before|checked|disabled|empty|enabled|",
				"first-(?:child|of-type)|focus|hover|last-(?:child|of-type)|",
				"link|only-(?:child|of-type)|root|:selection|target|visited)"));
	private static final Pattern _preserveStringsPattern = Pattern.compile(
		"(\"([^\\\\\"]|\\\\.|\\\\)*\")|(\'([^\\\\\']|\\\\.|\\\\)*\')");
	private static final Pattern _removeUnneededLeadingSpacesPattern =
		Pattern.compile("(^|\\})((^|([^\\{:])+):)+([^\\{]*\\{)");
	private static final Pattern _replace0UnitWith0Pattern1 = Pattern.compile(
		"(?i)(^|: ?)((?:[0-9a-z-.]+ )*?)?(?:0?\\.)?0(?:px|em|%|in|cm|mm|" +
			"pc|pt|ex|deg|g?rad|m?s|k?hz)");
	private static final Pattern _replace0UnitWith0Pattern2 = Pattern.compile(
		"(?i)\\( ?((?:[0-9a-z-.]+[ ,])*)?(?:0?\\.)?0(?:px|em|%|in|cm|mm|" +
			"pc|pt|ex|deg|g?rad|m?s|k?hz)");
	private static final Pattern _replaceBorderNonePattern = Pattern.compile(
		"(?i)(border|border-top|border-right|border-bottom|border-left|" +
			"outline|background):none(;|})");
	private static final Pattern _restoreContainerQuerySpacesPattern =
		Pattern.compile("@container\\s+([^\\s(]+\\()");
	private static final Pattern _restoreSomeMultipleZeroesPattern =
		Pattern.compile(
			StringBundler.concat(
				"(?i)(background-position|webkit-mask-position|",
				"transform-origin|webkit-transform-origin|",
				"moz-transform-origin|o-transform-origin|",
				"ms-transform-origin):0(;|})"));
	private static final Pattern _retainSpaceForSpecialIE6CasesPattern =
		Pattern.compile("(?i):first\\-(line|letter)(\\{|,)");
	private static final Pattern _shortenRGBColorsPattern = Pattern.compile(
		"rgb\\s*\\(\\s*([0-9,\\s]+)\\s*\\)");
	private static final Pattern _shortenTwinComponentDigitsColorsPattern =
		Pattern.compile(
			StringBundler.concat(
				"(\\=\\s*?[\"']?)?#([0-9a-fA-F])([0-9a-fA-F])",
				"([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])",
				"(:?\\}|[^0-9a-fA-F{][^{]*?\\})"));

	private final StringBuffer _sb = new StringBuffer();

}