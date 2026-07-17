/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.check.util.TaglibUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class JSPTagAttributesCheck extends BaseTagAttributesCheck {

	@Override
	public void setAllFileNames(List<String> allFileNames) {
		_allFileNames = allFileNames;
	}

	@Override
	protected Tag doFormatLineBreaks(Tag tag, String absolutePath) {
		Map<String, String> attributesMap = tag.getAttributesMap();

		if (attributesMap.isEmpty()) {
			tag.setMultiLine(false);

			return tag;
		}

		String taglibName = tag.getTaglibName();

		if ((taglibName == null) || taglibName.equals("aui") ||
			taglibName.equals("c") || taglibName.equals("portlet") ||
			ArrayUtil.contains(_SINGLE_LINE_TAG_WHITELIST, tag.getFullName())) {

			tag.setMultiLine(false);
		}
		else {
			tag.setMultiLine(true);
		}

		return tag;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		content = formatIncorrectLineBreak(fileName, content);

		content = _formatSingleLineTagAttributes(absolutePath, content);

		content = formatMultiLinesTagAttributes(absolutePath, content, false);

		return content;
	}

	@Override
	protected Tag formatTagAttributeType(String absolutePath, Tag tag)
		throws Exception {

		if (absolutePath.endsWith(".jspx")) {
			return tag;
		}

		Map<String, String> setMethodsMap = _getSetMethodsMap(
			tag.getFullName());

		Map<String, String> attributesMap = tag.getAttributesMap();

		Set<Map.Entry<String, String>> entrySet = attributesMap.entrySet();

		Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();

			String attributeName = entry.getKey();
			String attributeValue = entry.getValue();

			String tagFullName = tag.getFullName();

			Matcher matcher = _taglibNamePattern.matcher(tagFullName);

			if (matcher.find()) {
				attributeValue = _moveLiteralStringInsideJPSExpression(
					attributeValue);

				tag.putAttribute(attributeName, attributeValue);
			}

			if (tagFullName.matches("\\w+")) {
				tag.putAttribute(
					attributeName,
					_formatPortletNamespaceValue(attributeValue));
			}

			if (tagFullName.equals("aui:button") &&
				attributeName.equals("type") &&
				attributeValue.equals("button")) {

				iterator.remove();

				continue;
			}

			if ((tagFullName.equals("aui:form") ||
				 tagFullName.equals("liferay-frontend:edit-form")) &&
				attributeName.equals("action") &&
				StringUtil.endsWith(attributeValue, "url.toString() %>")) {

				tag.putAttribute(
					attributeName,
					StringUtil.replaceLast(attributeValue, ".toString()", ""));
			}

			if (isAttributeValue(_CHECK_CLAY_ALERT_MESSAGE_KEY, absolutePath) &&
				tagFullName.equals("clay:alert") &&
				attributeName.equals("message")) {

				matcher = _languageUtilPattern.matcher(attributeValue);

				if (matcher.find()) {
					String languageKey = matcher.group(1);

					if (languageKey.matches("\"[^\"]+\"")) {
						tag.putAttribute(
							attributeName, StringUtil.unquote(languageKey));
					}
					else {
						tag.putAttribute(
							attributeName, "<%= " + languageKey + " %>");
					}
				}
			}

			if (tagFullName.equals("liferay-ui:message") &&
				attributeName.equals("arguments")) {

				tag.putAttribute(
					attributeName,
					_formatMessageArgumentsValue(attributeValue));
			}

			if (attributeName.equals("style") &&
				(!tagFullName.contains(StringPool.COLON) ||
				 tagFullName.startsWith("aui:"))) {

				tag.putAttribute(
					attributeName, _formatStyleAttributeValue(attributeValue));
			}

			if (attributeValue.matches("<%= Boolean\\.(FALSE|TRUE) %>")) {
				attributeValue = StringUtil.replace(
					attributeValue,
					new String[] {"Boolean.FALSE", "Boolean.TRUE"},
					new String[] {"false", "true"});

				tag.putAttribute(attributeName, attributeValue);
			}

			if (attributeValue.matches("<%=.*%>")) {
				continue;
			}

			if ((setMethodsMap == null) ||
				(!isPortalSource() && !isSubrepository())) {

				continue;
			}

			String setAttributeMethodName =
				"set" + TextFormatter.format(attributeName, TextFormatter.G);

			String dataType = setMethodsMap.get(setAttributeMethodName);

			if (dataType == null) {
				continue;
			}

			Set<String> primitiveTagAttributeDataType =
				_getPrimitiveTagAttributeDataTypes();

			if (primitiveTagAttributeDataType.contains(dataType)) {
				if (!_isValidTagAttributeValue(attributeValue, dataType)) {
					continue;
				}

				tag.putAttribute(
					attributeName, "<%= " + attributeValue + " %>");
			}

			if (dataType.equals("java.lang.String") ||
				dataType.equals("String")) {

				attributeValue = StringUtil.replace(
					attributeValue, new String[] {"=\"false\"", "=\"true\""},
					new String[] {
						"=\"<%= Boolean.FALSE.toString() %>\"",
						"=\"<%= Boolean.TRUE.toString() %>\""
					});

				tag.putAttribute(attributeName, attributeValue);
			}
		}

		return tag;
	}

	private String _formatMessageArgumentsValue(String attributeValue) {
		Matcher matcher = _messageArgumentArrayPattern.matcher(attributeValue);

		if (!matcher.find()) {
			return attributeValue;
		}

		List<String> parametersList = JavaSourceUtil.splitParameters(
			matcher.group(2));

		if (parametersList.size() == 1) {
			return matcher.replaceFirst("$1$2$3");
		}

		return attributeValue;
	}

	private String _formatPortletNamespaceValue(String attributeValue) {
		return attributeValue.replaceFirst(
			"<%= liferayPortletResponse\\.getNamespace\\(\\) %>(\\w+)",
			"<portlet:namespace />$1");
	}

	private String _formatSingleLineTagAttributes(
			String absolutePath, String content)
		throws Exception {

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				String trimmedLine = StringUtil.trimLeading(line);

				if (trimmedLine.matches("<\\w+ .*>.*")) {
					String htmlTag = getTag(trimmedLine, 0);

					if (htmlTag != null) {
						String newHTMLTag = formatTagAttributes(
							absolutePath, htmlTag, false, true);

						line = StringUtil.replace(line, htmlTag, newHTMLTag);
					}
				}

				for (String jspTag : getJSPTags(line)) {
					boolean forceSingleLine = false;

					if (!line.equals(jspTag)) {
						forceSingleLine = true;
					}

					String newJSPTag = formatTagAttributes(
						absolutePath, jspTag, false, forceSingleLine);

					line = StringUtil.replace(line, jspTag, newJSPTag);
				}

				sb.append(line);
				sb.append("\n");
			}
		}

		content = sb.toString();

		if (content.endsWith("\n")) {
			content = content.substring(0, content.length() - 1);
		}

		return content;
	}

	private String _formatStyleAttributeAttributeValue(
		String styleAttributeAttributeValue) {

		styleAttributeAttributeValue = StringUtil.trim(
			styleAttributeAttributeValue);

		if (styleAttributeAttributeValue.endsWith(_JAVA_SOURCE_REPLACEMENT) ||
			styleAttributeAttributeValue.endsWith(StringPool.SEMICOLON)) {

			return styleAttributeAttributeValue;
		}

		return styleAttributeAttributeValue + StringPool.SEMICOLON;
	}

	private String _formatStyleAttributeValue(String attributeValue) {
		List<String> javaSourceList = new ArrayList<>();

		Matcher matcher = _javaSourceInsideTagPattern.matcher(attributeValue);

		while (matcher.find()) {
			javaSourceList.add(matcher.group());
		}

		String newAttributeValue = matcher.replaceAll(_JAVA_SOURCE_REPLACEMENT);

		if (newAttributeValue.contains(StringPool.LESS_THAN)) {
			return attributeValue;
		}

		Map<String, String> styleAttributesMap = new TreeMap<>();

		String styleAttributeAttributeName = null;
		int x = -1;

		matcher = _styleAttributePattern.matcher(newAttributeValue);

		while (matcher.find()) {
			if (styleAttributeAttributeName != null) {
				styleAttributesMap.put(
					styleAttributeAttributeName,
					_formatStyleAttributeAttributeValue(
						newAttributeValue.substring(x, matcher.start(2))));
			}

			x = matcher.end();

			styleAttributeAttributeName = matcher.group(2);
		}

		if (styleAttributeAttributeName != null) {
			styleAttributesMap.put(
				styleAttributeAttributeName,
				_formatStyleAttributeAttributeValue(
					newAttributeValue.substring(x)));
		}

		if (styleAttributesMap.isEmpty()) {
			return attributeValue;
		}

		StringBundler sb = new StringBundler(styleAttributesMap.size() * 4);

		for (Map.Entry<String, String> entry : styleAttributesMap.entrySet()) {
			sb.append(entry.getKey());
			sb.append(": ");
			sb.append(entry.getValue());
			sb.append(StringPool.SPACE);
		}

		newAttributeValue = StringUtil.trim(sb.toString());

		for (String javaSource : javaSourceList) {
			newAttributeValue = StringUtil.replaceFirst(
				newAttributeValue, _JAVA_SOURCE_REPLACEMENT, javaSource);
		}

		return newAttributeValue;
	}

	private synchronized Set<String> _getPrimitiveTagAttributeDataTypes() {
		if (_primitiveTagAttributeDataTypes == null) {
			_primitiveTagAttributeDataTypes = SetUtil.fromArray(
				"java.lang.Boolean", "Boolean", "boolean", "java.lang.Double",
				"Double", "double", "java.lang.Integer", "Integer", "int",
				"java.lang.Long", "Long", "long");
		}

		return _primitiveTagAttributeDataTypes;
	}

	private synchronized Map<String, String> _getSetMethodsMap(String tagName)
		throws Exception {

		if (_tagSetMethodsMap != null) {
			Map<String, String> tagSetMethodsMap = _tagSetMethodsMap.get(
				tagName);

			if (tagSetMethodsMap != null) {
				return tagSetMethodsMap;
			}

			return _tagSetMethodsMap.get("liferay-" + tagName);
		}

		_tagSetMethodsMap = new HashMap<>();

		List<String> tldFileNames = TaglibUtil.getTLDFileNames(
			getBaseDirName(), _allFileNames, getSourceFormatterExcludes(),
			isPortalSource(), getMaxDirLevel());

		if (tldFileNames.isEmpty()) {
			return _tagSetMethodsMap.get(tagName);
		}

		String utilTaglibSrcDirName = TaglibUtil.getUtilTaglibSrcDirName(
			getBaseDirName(), getMaxDirLevel());

		outerLoop:
		for (String tldFileName : tldFileNames) {
			File tldFile = new File(tldFileName);

			String content = FileUtil.read(tldFile);

			Document document = SourceUtil.readXML(content);

			if (document == null) {
				continue;
			}

			Element rootElement = document.getRootElement();

			Element shortNameElement = rootElement.element("short-name");

			if (shortNameElement == null) {
				continue;
			}

			String shortName = shortNameElement.getStringValue();

			List<Element> tagElements = rootElement.elements("tag");

			String srcDir = null;

			for (Element tagElement : tagElements) {
				Element tagClassElement = tagElement.element("tag-class");

				String tagClassName = tagClassElement.getStringValue();

				if (!tagClassName.startsWith("com.liferay")) {
					continue;
				}

				Element tagNameElement = tagElement.element("name");

				String curTagName = tagNameElement.getStringValue();

				if (_tagSetMethodsMap.containsKey(
						shortName + StringPool.COLON + curTagName)) {

					continue;
				}

				if (srcDir == null) {
					String absolutePath = SourceUtil.getAbsolutePath(tldFile);

					int x = absolutePath.lastIndexOf("/src/");

					if (x != -1) {
						srcDir = absolutePath.substring(0, x + 5);

						if (tldFileName.contains("/modules/")) {
							srcDir += "main/java/";
						}
					}
					else {
						srcDir = utilTaglibSrcDirName;

						if (Validator.isNull(srcDir)) {
							continue outerLoop;
						}
					}
				}

				StringBundler sb = new StringBundler(3);

				sb.append(srcDir);
				sb.append(
					StringUtil.replace(
						tagClassName, CharPool.PERIOD, CharPool.SLASH));
				sb.append(".java");

				Map<String, String> setMethodsMap = _getSetMethodsMap(
					sb.toString(), utilTaglibSrcDirName);

				if (setMethodsMap.isEmpty()) {
					continue;
				}

				_tagSetMethodsMap.put(
					shortName + StringPool.COLON + curTagName, setMethodsMap);
			}
		}

		return _tagSetMethodsMap.get(tagName);
	}

	private Map<String, String> _getSetMethodsMap(
			String tagFileName, String utilTaglibSrcDirName)
		throws Exception {

		Map<String, String> setMethodsMap = _classSetMethodsMap.get(
			tagFileName);

		if (setMethodsMap != null) {
			return setMethodsMap;
		}

		setMethodsMap = new HashMap<>();

		File tagFile = new File(tagFileName);

		if (!tagFile.exists()) {
			return setMethodsMap;
		}

		String tagFileContent = FileUtil.read(tagFile);

		JavaClass javaClass = JavaClassParser.parseJavaClass(
			tagFileName, tagFileContent);

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (!javaTerm.isJavaMethod()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)javaTerm;

			String methodName = javaMethod.getName();

			if (!methodName.startsWith("set")) {
				continue;
			}

			JavaSignature javaSignature = javaMethod.getSignature();

			List<JavaParameter> javaParameters = javaSignature.getParameters();

			if (javaParameters.size() != 1) {
				continue;
			}

			JavaParameter javaParameter = javaParameters.get(0);

			setMethodsMap.put(methodName, javaParameter.getParameterType());
		}

		List<String> extendedTagFileNames = TaglibUtil.getExtendedTagFileNames(
			javaClass, tagFileName, utilTaglibSrcDirName);

		for (String extendedTagFileName : extendedTagFileNames) {
			setMethodsMap.putAll(
				_getSetMethodsMap(extendedTagFileName, utilTaglibSrcDirName));
		}

		_classSetMethodsMap.put(tagFileName, setMethodsMap);

		return setMethodsMap;
	}

	private boolean _isValidTagAttributeValue(String value, String dataType) {
		if (dataType.endsWith("Boolean") || dataType.equals("boolean")) {
			return Validator.isBoolean(value);
		}

		if (dataType.endsWith("Double") || dataType.equals("double")) {
			try {
				Double.parseDouble(value);
			}
			catch (NumberFormatException numberFormatException) {
				if (_log.isDebugEnabled()) {
					_log.debug(numberFormatException);
				}

				return false;
			}

			return true;
		}

		if (dataType.endsWith("Integer") || dataType.equals("int") ||
			dataType.endsWith("Long") || dataType.equals("long")) {

			return Validator.isNumber(value);
		}

		return false;
	}

	private String _moveLiteralStringInsideJPSExpression(
		String attributeValue) {

		if ((!attributeValue.contains("<%=") &&
			 !attributeValue.contains("%>")) ||
			(attributeValue.startsWith("<%=") &&
			 attributeValue.endsWith("%>")) ||
			(getLevel(attributeValue, "<%", "%>") != 0)) {

			return attributeValue;
		}

		attributeValue = attributeValue.trim();

		if (attributeValue.startsWith("<%=") && attributeValue.endsWith("%>")) {
			return attributeValue;
		}

		int x = attributeValue.indexOf("<%=");

		if (x > 0) {
			String literalString = StringUtil.quote(
				attributeValue.substring(0, x), StringPool.QUOTE);

			attributeValue = StringBundler.concat(
				"<%= ", literalString, " +", attributeValue.substring(x + 3));
		}

		x = attributeValue.lastIndexOf("%>");

		if ((x != -1) && (x < (attributeValue.length() - 2))) {
			String literalString = StringUtil.quote(
				attributeValue.substring(x + 2), StringPool.QUOTE);

			attributeValue = StringBundler.concat(
				attributeValue.substring(0, x), "+ ", literalString, " %>");
		}

		return attributeValue;
	}

	private static final String _CHECK_CLAY_ALERT_MESSAGE_KEY =
		"checkClayAlertMessage";

	private static final String _JAVA_SOURCE_REPLACEMENT = "__JAVA_SOURCE__";

	private static final String[] _SINGLE_LINE_TAG_WHITELIST = {
		"liferay-frontend:defineObjects", "liferay-portlet:actionURL",
		"liferay-portlet:param", "liferay-portlet:renderURL",
		"liferay-portlet:renderURLParams", "liferay-portlet:resourceURL",
		"liferay-staging:defineObjects", "liferay-theme:defineObjects",
		"liferay-ui:error", "liferay-ui:icon-help", "liferay-ui:message",
		"liferay-ui:success", "liferay-util:dynamic-include",
		"liferay-util:include", "liferay-util:param"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		JSPTagAttributesCheck.class);

	private static final Pattern _javaSourceInsideTagPattern = Pattern.compile(
		"<%.*?%>");
	private static final Pattern _languageUtilPattern = Pattern.compile(
		"<%= LanguageUtil\\.get\\(\\w+, (.+)\\) %>");
	private static final Pattern _messageArgumentArrayPattern = Pattern.compile(
		"^(<%= )new \\w+\\[\\] \\{([^<>]+)\\}( %>)$");
	private static final Pattern _styleAttributePattern = Pattern.compile(
		"(\\A|\\W)([a-z\\-]+)\\s*:");
	private static final Pattern _taglibNamePattern = Pattern.compile(
		"(aui|c|chart|clay|display|liferay(-[\\w-]+)|portlet|soy):.+");

	private List<String> _allFileNames;
	private final Map<String, Map<String, String>> _classSetMethodsMap =
		new HashMap<>();
	private Set<String> _primitiveTagAttributeDataTypes;
	private Map<String, Map<String, String>> _tagSetMethodsMap;

}