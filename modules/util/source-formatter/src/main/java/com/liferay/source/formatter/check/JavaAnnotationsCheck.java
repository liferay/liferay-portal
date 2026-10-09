/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.JavaVariable;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaAnnotationsCheck extends BaseJavaTermCheck {

	protected void checkComponentName(
			String fileName, String absolutePath, JavaClass javaClass,
			String attributeValue, boolean forceOSGiComponent)
		throws Exception {

		String componentName = _getComponentName(
			absolutePath, javaClass, attributeValue);

		if (componentName == null) {
			return;
		}

		if (componentName.contains("+")) {
			componentName = componentName.replaceAll("[^\\w\\.]", "");
		}

		if (componentName.contains("*")) {
			addMessage(
				fileName,
				"Do not use globs for the \"component.name\". Use the fully " +
					"qualified name of the component class.");

			return;
		}

		if (!componentName.startsWith("com.liferay") || !forceOSGiComponent) {
			return;
		}

		JavaClass componentJavaClass = _getJavaClass(
			absolutePath, componentName);

		if (componentJavaClass != null) {
			List<String> importNames = componentJavaClass.getImportNames();

			if (componentJavaClass.hasAnnotation("Component") &&
				importNames.contains(
					"org.osgi.service.component.annotations.Component")) {

				return;
			}
		}

		addMessage(
			fileName,
			"The value \"" + componentName +
				"\" is not a valid OSGi component");
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		return formatAnnotations(
			fileName, absolutePath, (JavaClass)javaTerm, fileContent);
	}

	protected String formatAnnotation(
			String fileName, String absolutePath, JavaClass javaClass,
			String fileContent, String annotation, String indent)
		throws Exception {

		if (!annotation.contains(StringPool.OPEN_PARENTHESIS)) {
			return annotation;
		}

		annotation = _fixAnnotationLineBreaks(annotation);
		annotation = _fixSingleValueArray(annotation);
		annotation = _fixWhitespaceAroundPipe(annotation);

		return annotation;
	}

	protected String formatAnnotations(
			String fileName, String absolutePath, JavaClass javaClass,
			String fileContent)
		throws Exception {

		String content = javaClass.getContent();

		if (javaClass.getParentJavaClass() != null) {
			return content;
		}

		List<String> annotationsBlocks = SourceUtil.getAnnotationsBlocks(
			content);

		for (String annotationsBlock : annotationsBlocks) {
			String indent = SourceUtil.getIndent(annotationsBlock);

			String newAnnotationsBlock = _formatAnnotations(
				fileName, absolutePath, javaClass, fileContent,
				annotationsBlock, indent);

			content = StringUtil.replace(
				content, "\n" + annotationsBlock, "\n" + newAnnotationsBlock);
		}

		return content;
	}

	protected String getAnnotationAttributeValue(
		String annotation, String attributeName) {

		Pattern pattern = Pattern.compile("[^\\w\"]" + attributeName + "\\s*=");

		Matcher matcher = pattern.matcher(annotation);

		if (!matcher.find()) {
			return null;
		}

		int start = matcher.end() + 1;

		int end = start;

		while (true) {
			end = annotation.indexOf(CharPool.COMMA, end + 1);

			if (end == -1) {
				end = annotation.lastIndexOf(CharPool.CLOSE_PARENTHESIS);

				break;
			}

			if (!ToolsUtil.isInsideQuotes(annotation, end) &&
				(getLevel(annotation.substring(start, end), "{", "}") == 0)) {

				break;
			}
		}

		String attributeValue = StringUtil.trim(
			annotation.substring(start, end));

		if (!attributeValue.contains("\n")) {
			return attributeValue;
		}

		return StringUtil.replace(
			attributeValue, new String[] {"\t", ",\n", "\n"},
			new String[] {"", ", ", ""});
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private String _fixAnnotationLineBreaks(String annotation) {
		Matcher matcher = _annotationLineBreakPattern1.matcher(annotation);

		if (matcher.find()) {
			return StringUtil.replaceFirst(
				annotation, matcher.group(1), StringPool.BLANK,
				matcher.start());
		}

		return annotation;
	}

	private String _fixSingleValueArray(String annotation) {
		Matcher matcher = _arrayPattern.matcher(annotation);

		outerLoop:
		while (matcher.find()) {
			int x = matcher.start();

			if (ToolsUtil.isInsideQuotes(annotation, x)) {
				continue;
			}

			String arrayString = null;

			int y = x;

			while (true) {
				y = annotation.indexOf("}", y + 1);

				if (y == -1) {
					return annotation;
				}

				if (ToolsUtil.isInsideQuotes(annotation, y)) {
					continue;
				}

				arrayString = annotation.substring(matcher.end() - 1, y + 1);

				if (getLevel(arrayString, "{", "}") == 0) {
					break;
				}
			}

			y = -1;

			while (true) {
				y = arrayString.indexOf(",", y + 1);

				if (y == -1) {
					break;
				}

				if (!ToolsUtil.isInsideQuotes(arrayString, y)) {
					continue outerLoop;
				}
			}

			String replacement = StringUtil.trim(
				arrayString.substring(1, arrayString.length() - 1));

			if (Validator.isNotNull(replacement)) {
				return StringUtil.replace(annotation, arrayString, replacement);
			}
		}

		return annotation;
	}

	private String _fixWhitespaceAroundPipe(String annotation) {
		Matcher matcher = _pipePattern.matcher(annotation);

		return matcher.replaceFirst("$1|");
	}

	private String _formatAnnotations(
			String fileName, String absolutePath, JavaClass javaClass,
			String fileContent, String annotationsBlock, String indent)
		throws Exception {

		List<String> annotations = SourceUtil.splitAnnotations(
			annotationsBlock, indent);

		for (String annotation : annotations) {
			String newAnnotation = formatAnnotation(
				fileName, absolutePath, javaClass, fileContent, annotation,
				indent);

			if (newAnnotation.contains(StringPool.OPEN_PARENTHESIS)) {
				newAnnotation = _formatAnnotations(
					fileName, absolutePath, javaClass, fileContent,
					newAnnotation, indent + "\t\t");
			}

			annotationsBlock = StringUtil.replace(
				annotationsBlock, annotation, newAnnotation);
		}

		return annotationsBlock;
	}

	private String _getComponentName(
			String absolutePath, JavaClass javaClass, String attributeValue)
		throws Exception {

		Matcher classConstantMatcher = _classConstantPattern.matcher(
			attributeValue);

		if (!classConstantMatcher.find()) {
			Matcher componentNameMatcher = _componentNamePattern.matcher(
				attributeValue);

			if (componentNameMatcher.find()) {
				return componentNameMatcher.group(1);
			}

			return null;
		}

		String classConstantName = classConstantMatcher.group(1);
		JavaClass classConstantJavaClass = javaClass;

		if (classConstantMatcher.groupCount() == 2) {
			String className = classConstantMatcher.group(1);

			String fullyQualifiedName =
				javaClass.getPackageName() + "." + className;

			for (String importName : javaClass.getImportNames()) {
				if (importName.endsWith(className)) {
					fullyQualifiedName = importName;

					break;
				}
			}

			classConstantName = classConstantMatcher.group(2);
			classConstantJavaClass = _getJavaClass(
				absolutePath, fullyQualifiedName);
		}

		if (classConstantJavaClass != null) {
			for (JavaTerm javaTerm :
					classConstantJavaClass.getChildJavaTerms()) {

				if (!javaTerm.isJavaVariable()) {
					continue;
				}

				JavaVariable javaVariable = (JavaVariable)javaTerm;

				if (classConstantName.equals(javaVariable.getName())) {
					Matcher componentNameMatcher =
						_componentNamePattern.matcher(
							javaVariable.getContent());

					if (componentNameMatcher.find()) {
						return componentNameMatcher.group(1);
					}

					return null;
				}
			}
		}

		return null;
	}

	private JavaClass _getJavaClass(
			String absolutePath, String fullyQualifiedName)
		throws Exception {

		JavaClass javaClass = _javaClassMap.get(fullyQualifiedName);

		if (javaClass == null) {
			File javaFile = JavaSourceUtil.getJavaFile(
				fullyQualifiedName, _getRootDirName(absolutePath),
				getBundleSymbolicNamesMap(absolutePath));

			if (javaFile != null) {
				javaClass = JavaClassParser.parseJavaClass(
					javaFile.getName(), FileUtil.read(javaFile));

				_javaClassMap.put(fullyQualifiedName, javaClass);
			}
		}

		return javaClass;
	}

	private String _getRootDirName(String absolutePath) {
		String rootDirName = _rootDirName;

		if (rootDirName == null) {
			rootDirName = JavaSourceUtil.getRootDirName(absolutePath);

			_rootDirName = rootDirName;
		}

		return rootDirName;
	}

	private static final Pattern _annotationLineBreakPattern1 = Pattern.compile(
		"[{=]\n.*(\" \\+\n\t*\")");
	private static final Pattern _arrayPattern = Pattern.compile("=\\s+\\{");
	private static final Pattern _classConstantPattern = Pattern.compile(
		"^([A-Z]\\w+)\\.?([A-Z]\\w+)$");
	private static final Pattern _componentNamePattern = Pattern.compile(
		"component\\.name=(.+?\\.[A-Z]\\w*)");
	private static final Pattern _pipePattern = Pattern.compile(
		"(= \".*)( \\| | \\||\\| )");

	private final Map<String, JavaClass> _javaClassMap =
		new ConcurrentHashMap<>();
	private volatile String _rootDirName;

}