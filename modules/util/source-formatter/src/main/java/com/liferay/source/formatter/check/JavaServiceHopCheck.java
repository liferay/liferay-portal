/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class JavaServiceHopCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		String className = JavaSourceUtil.getClassName(fileName);

		if (!className.endsWith("ServiceImpl")) {
			return javaTerm.getContent();
		}

		String content = javaTerm.getContent();

		// A method that disables transaction management has no transaction
		// executor of its own, so a hop to a transactional local service is
		// what opens the transaction the persistence access requires. Calling
		// persistence directly here would run outside a transaction, so leave
		// every hop in such a method untouched.

		if (content.contains("@Transactional(enabled = false)")) {
			return javaTerm.getContent();
		}

		Matcher matcher = _localServiceCallPattern.matcher(content);

		if (!matcher.find()) {
			return javaTerm.getContent();
		}

		String rootDirName = SourceUtil.getRootDirName(absolutePath);

		matcher.reset();

		while (matcher.find()) {
			String reference = matcher.group(1);

			String entityName = StringUtil.upperCaseFirstLetter(
				StringUtil.replaceFirst(reference, '_', ""));

			entityName = _getAllCapsName(entityName);

			String localServiceInterface = entityName + "LocalService";

			String localServiceFullyQualifiedName = _getImportedName(
				getImportNames(javaTerm), localServiceInterface);

			// The caller's own service interface is not imported, so derive it
			// from the caller's package.

			if (localServiceFullyQualifiedName == null) {
				String packageName = JavaSourceUtil.getPackageName(fileContent);

				if (packageName.endsWith(".service.impl")) {
					localServiceFullyQualifiedName = StringUtil.replaceLast(
						packageName, ".impl", "." + localServiceInterface);
				}
			}

			if (localServiceFullyQualifiedName == null) {
				continue;
			}

			File baseImplFile = JavaSourceUtil.getJavaFile(
				_getBaseImplName(localServiceFullyQualifiedName), rootDirName,
				getBundleSymbolicNamesMap(absolutePath));

			if ((baseImplFile == null) ||
				!Objects.equals(
					_getModuleKey(absolutePath),
					_getModuleKey(baseImplFile.getAbsolutePath()))) {

				continue;
			}

			List<String> parameterList = JavaSourceUtil.getParameterList(
				content.substring(matcher.start()));

			String methodName = matcher.group(2);

			String persistenceCall = _getPassthroughPersistenceCall(
				baseImplFile, methodName, parameterList.size());

			if (persistenceCall == null) {
				continue;
			}

			// A lambda runs in the transaction of whatever invokes it. When the
			// invoker disables transaction management (for example an iterator
			// like forEachCompanyId), the hop inside the lambda is what opens
			// the transaction the persistence access requires, so leave it in
			// place.

			if (content.contains("->") &&
				_isInTransactionDisabledLambda(
					absolutePath, content, fileContent, fileName,
					getImportNames(javaTerm), matcher.start(), rootDirName)) {

				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Avoid the AOP proxy hop: \"", reference, "LocalService.",
					methodName, "\" is a passthrough, call \"", persistenceCall,
					"\" directly"),
				javaTerm.getLineNumber(matcher.start()));
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_METHOD};
	}

	private void _collectMatchedMethods(
			File file, List<JavaMethod> matchedMethods, String methodName,
			int parametersCount)
		throws Exception {

		if (!file.exists()) {
			return;
		}

		String content = FileUtil.read(file);

		if (_hasSideEffectingClassAnnotation(content)) {
			return;
		}

		JavaClass javaClass = JavaClassParser.parseJavaClass(
			file.getName(), content);

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!(childJavaTerm instanceof JavaMethod)) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			String accessModifier = javaMethod.getAccessModifier();

			if (accessModifier.equals("private") ||
				!Objects.equals(methodName, javaMethod.getName())) {

				continue;
			}

			JavaSignature javaSignature = javaMethod.getSignature();

			List<JavaParameter> javaParameters = javaSignature.getParameters();

			if (javaParameters.size() != parametersCount) {
				continue;
			}

			matchedMethods.add(javaMethod);
		}
	}

	private String _getAllCapsName(String name) {
		for (String[] array : _ALL_CAPS_STRINGS) {
			String s = array[1];

			int x = -1;

			while (true) {
				x = name.indexOf(s, x + 1);

				if (x == -1) {
					break;
				}

				int y = x + s.length();

				if ((y != name.length()) &&
					!Character.isUpperCase(name.charAt(y))) {

					continue;
				}

				return name.substring(0, x) + array[0] + name.substring(y);
			}
		}

		return name;
	}

	private String _getBaseImplName(String localServiceFullyQualifiedName) {

		// A kernel service interface is implemented in portal-impl, where the
		// base impl package drops the ".kernel" segment.

		if (localServiceFullyQualifiedName.contains(".kernel.service.")) {
			String baseName = StringUtil.replace(
				localServiceFullyQualifiedName, ".kernel.service.",
				".service.base.");

			return baseName + "BaseImpl";
		}

		int index = localServiceFullyQualifiedName.lastIndexOf('.');

		String className = localServiceFullyQualifiedName.substring(index + 1);
		String packageName = localServiceFullyQualifiedName.substring(0, index);

		return StringBundler.concat(
			packageName, ".base.", className, "BaseImpl");
	}

	private List<String> _getEnclosingLambdaTargets(String content, int pos) {
		List<String> targets = new ArrayList<>();

		int braceDepth = 0;

		for (int i = pos - 1; i >= 0; i--) {
			char c = content.charAt(i);

			if (c == '}') {
				braceDepth++;
			}
			else if (c == '{') {
				if (braceDepth > 0) {
					braceDepth--;

					continue;
				}

				// This is an enclosing block opener. It is a lambda body when
				// the preceding nonwhitespace characters are the "->" arrow.

				int j = i - 1;

				while ((j >= 0) && Character.isWhitespace(content.charAt(j))) {
					j--;
				}

				if ((j >= 1) && (content.charAt(j) == '>') &&
					(content.charAt(j - 1) == '-')) {

					String target = _getLambdaTargetReference(content, j - 2);

					if (target != null) {
						targets.add(target);
					}
				}
			}
		}

		return targets;
	}

	private File _getImplFile(
		String absolutePath, String localServiceFullyQualifiedName,
		String rootDirName) {

		File baseImplFile = JavaSourceUtil.getJavaFile(
			_getBaseImplName(localServiceFullyQualifiedName), rootDirName,
			getBundleSymbolicNamesMap(absolutePath));

		if (baseImplFile == null) {
			return null;
		}

		return new File(
			StringUtil.replace(
				StringUtil.replace(
					baseImplFile.getAbsolutePath(), "/base/", "/impl/"),
				"BaseImpl.java", "Impl.java"));
	}

	private String _getImportedName(
		List<String> importNames, String className) {

		for (String importName : importNames) {
			if (importName.endsWith("." + className)) {
				return importName;
			}
		}

		return null;
	}

	private String _getLambdaTargetReference(String content, int i) {

		// Walk back over the lambda parameter list, then over any preceding
		// arguments, to the open parenthesis of the call the lambda is passed
		// to, and return that call's method reference.

		while ((i >= 0) && Character.isWhitespace(content.charAt(i))) {
			i--;
		}

		if (i < 0) {
			return null;
		}

		if (content.charAt(i) == ')') {
			int parenDepth = 0;

			while (i >= 0) {
				char c = content.charAt(i);

				if (c == ')') {
					parenDepth++;
				}
				else if (c == '(') {
					parenDepth--;

					if (parenDepth == 0) {
						i--;

						break;
					}
				}

				i--;
			}
		}
		else {
			while ((i >= 0) &&
				   Character.isJavaIdentifierPart(content.charAt(i))) {

				i--;
			}
		}

		while ((i >= 0) && Character.isWhitespace(content.charAt(i))) {
			i--;
		}

		int parenDepth = 0;

		while (i >= 0) {
			char c = content.charAt(i);

			if (c == ')') {
				parenDepth++;
			}
			else if (c == '(') {
				if (parenDepth == 0) {
					break;
				}

				parenDepth--;
			}

			i--;
		}

		if (i < 0) {
			return null;
		}

		i--;

		while ((i >= 0) && Character.isWhitespace(content.charAt(i))) {
			i--;
		}

		int nameEnd = i + 1;

		while ((i >= 0) &&
			   (Character.isJavaIdentifierPart(content.charAt(i)) ||
				(content.charAt(i) == '.'))) {

			i--;
		}

		if ((i + 1) >= nameEnd) {
			return null;
		}

		return content.substring(i + 1, nameEnd);
	}

	private String _getModuleKey(String absolutePath) {
		int index = absolutePath.indexOf("/portal-impl/");

		if (index != -1) {
			return "portal-impl";
		}

		index = absolutePath.indexOf("-service/");

		if (index != -1) {
			return absolutePath.substring(0, index + 8);
		}

		return absolutePath;
	}

	private String _getPassthroughPersistenceCall(
			File baseImplFile, String methodName, int parametersCount)
		throws Exception {

		// Collect matching overloads from BOTH the generated base impl and the
		// handwritten impl. A handwritten overload of the same name and
		// argument count (for example fetchRelease(String) alongside the
		// generated fetchRelease(long)) makes the target ambiguous without type
		// resolution, so conservatively skip.

		List<JavaMethod> matchedMethods = new ArrayList<>();

		_collectMatchedMethods(
			baseImplFile, matchedMethods, methodName, parametersCount);

		File implFile = new File(
			StringUtil.replace(
				StringUtil.replace(
					baseImplFile.getAbsolutePath(), "/base/", "/impl/"),
				"BaseImpl.java", "Impl.java"));

		_collectMatchedMethods(
			implFile, matchedMethods, methodName, parametersCount);

		if (matchedMethods.size() != 1) {
			return null;
		}

		return _getPassthroughPersistenceCall(matchedMethods.get(0));
	}

	private String _getPassthroughPersistenceCall(JavaMethod javaMethod) {
		String content = javaMethod.getContent();

		if (_hasSideEffectingAnnotation(content)) {
			return null;
		}

		Matcher matcher = _passthroughBodyPattern.matcher(content);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group(1) + "." + matcher.group(2);
	}

	private boolean _hasSideEffectingAnnotation(String content) {
		Matcher matcher = _sideEffectingAnnotationPattern.matcher(content);

		return matcher.find();
	}

	private boolean _hasSideEffectingClassAnnotation(String content) {
		Matcher matcher = _classDeclarationPattern.matcher(content);

		if (!matcher.find()) {
			return false;
		}

		return _hasSideEffectingAnnotation(matcher.group(1));
	}

	private boolean _hasTransactionDisabledMethod(
			String fileName, String fileContent, String methodName)
		throws Exception {

		JavaClass javaClass = JavaClassParser.parseJavaClass(
			fileName, fileContent);

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!(childJavaTerm instanceof JavaMethod)) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			String accessModifier = javaMethod.getAccessModifier();

			if (accessModifier.equals("private") ||
				!Objects.equals(methodName, javaMethod.getName())) {

				continue;
			}

			String methodContent = javaMethod.getContent();

			if (methodContent.contains("@Transactional(enabled = false)")) {
				return true;
			}
		}

		return false;
	}

	private boolean _isInTransactionDisabledLambda(
			String absolutePath, String content, String fileContent,
			String fileName, List<String> importNames, int pos,
			String rootDirName)
		throws Exception {

		for (String target : _getEnclosingLambdaTargets(content, pos)) {
			if (_isTransactionDisabledTarget(
					absolutePath, fileContent, fileName, importNames, target,
					rootDirName)) {

				return true;
			}
		}

		return false;
	}

	private boolean _isTransactionDisabledTarget(
			String absolutePath, String fileContent, String fileName,
			List<String> importNames, String methodCall, String rootDirName)
		throws Exception {

		int index = methodCall.lastIndexOf('.');

		// A call with no receiver runs on the caller's own service, so the
		// method is declared in this file.

		if (index == -1) {
			return _hasTransactionDisabledMethod(
				fileName, fileContent, methodCall);
		}

		String methodName = methodCall.substring(index + 1);

		String variableName = methodCall.substring(0, index);

		index = variableName.lastIndexOf('.');

		variableName = variableName.substring(index + 1);

		if (!variableName.endsWith("LocalService")) {
			return false;
		}

		String localServiceInterface = StringUtil.upperCaseFirstLetter(
			StringUtil.removeChar(variableName, '_'));

		String localServiceFullyQualifiedName = _getImportedName(
			importNames, localServiceInterface);

		if (localServiceFullyQualifiedName == null) {
			return false;
		}

		File implFile = _getImplFile(
			absolutePath, localServiceFullyQualifiedName, rootDirName);

		if ((implFile == null) || !implFile.exists()) {
			return false;
		}

		return _hasTransactionDisabledMethod(
			implFile.getName(), FileUtil.read(implFile), methodName);
	}

	private static final String[][] _ALL_CAPS_STRINGS = {
		{"COR", "Cor"}, {"CP", "Cp"}, {"CS", "Cs"}, {"CT", "Ct"},
		{"DDL", "Ddl"}, {"DDM", "Ddm"}, {"DL", "Dl"}, {"KB", "Kb"},
		{"MB", "Mb"}, {"PK", "Pk"}, {"PLO", "Plo"}, {"SXP", "Sxp"},
		{"URL", "Url"}
	};

	private static final Pattern _classDeclarationPattern = Pattern.compile(
		"((?:@\\w+(?:\\([^)]*\\))?\\s*)*)public\\s+(?:abstract\\s+)?class\\s");
	private static final Pattern _localServiceCallPattern = Pattern.compile(
		"\\b(\\w+?)LocalService\\.\\s*(\\w+)\\s*\\(");
	private static final Pattern _passthroughBodyPattern = Pattern.compile(
		"\\)\\s*(?:throws[\\w\\s,.]*)?\\{\\s*return\\s+(\\w+Persistence)\\.\\" +
			"s*(\\s*(?:count|fetch|filterCount|filterFind|find)\\w*)\\([^;]*" +
				"\\);\\s*\\}\\s*\\z");
	private static final Pattern _sideEffectingAnnotationPattern =
		Pattern.compile(
			"@(AccessControlled|Async|BufferedIncrement|Clusterable|" +
				"Indexable|Retry|SystemEvent|ThreadLocalCachable)\\b");

}