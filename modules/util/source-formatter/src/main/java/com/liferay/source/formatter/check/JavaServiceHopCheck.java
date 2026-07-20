/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
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
public class JavaServiceHopCheck extends BaseServiceImplCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return false;
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

		for (String allowedFileName :
				getAttributeValues(_ALLOWED_FILE_NAMES_KEY, absolutePath)) {

			if (absolutePath.endsWith(allowedFileName)) {
				return javaTerm.getContent();
			}
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

		int contentStart = fileContent.indexOf(content);

		boolean hasLambda = content.contains("->");

		Matcher matcher = _callPattern.matcher(content);

		while (matcher.find()) {
			String reference = matcher.group(1);

			String entityName = StringUtil.upperCaseFirstLetter(
				StringUtil.removeChar(reference, '_'));

			String localServiceInterface = entityName + "LocalService";

			String localServiceFullyQualifiedName = _getImportedName(
				fileContent, localServiceInterface);

			if (localServiceFullyQualifiedName == null) {

				// Self-hop: the caller's own service interface is not imported,
				// so derive it from the caller's package

				String packageName = _getPackageName(fileContent);

				if (packageName.endsWith(".service.impl")) {
					localServiceFullyQualifiedName = StringUtil.replaceLast(
						packageName, ".impl", "." + localServiceInterface);
				}
			}

			if (localServiceFullyQualifiedName == null) {
				continue;
			}

			String baseImplFullyQualifiedName = _toBaseImplName(
				localServiceFullyQualifiedName);

			String rootDirName = getBaseDirName();

			if (rootDirName.isEmpty()) {
				rootDirName = _getRootDirName(absolutePath);
			}

			File baseImplFile = JavaSourceUtil.getJavaFile(
				baseImplFullyQualifiedName, rootDirName,
				getBundleSymbolicNamesMap(absolutePath));

			if ((baseImplFile == null) ||
				!_isSameBundle(absolutePath, baseImplFile.getAbsolutePath())) {

				continue;
			}

			int callArgsCount = _getArgumentsCount(content, matcher.end() - 1);

			String methodName = matcher.group(2);

			String persistenceCall = _getPassthroughPersistenceCall(
				baseImplFile, methodName, callArgsCount);

			if (persistenceCall == null) {
				continue;
			}

			// A lambda runs in the transaction of whatever invokes it. When the
			// invoker disables transaction management (for example an iterator
			// like forEachCompanyId), the hop inside the lambda is what opens
			// the transaction the persistence access requires, so leave it in
			// place.

			if (hasLambda &&
				_isInTransactionDisabledLambda(
					content, matcher.start(), fileName, fileContent,
					absolutePath, rootDirName)) {

				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Avoid the AOP proxy hop: \"", reference, "LocalService.",
					methodName, "\" is a pass-through, call \"",
					persistenceCall, "\" directly"),
				getLineNumber(fileContent, contentStart + matcher.start()));
		}

		return javaTerm.getContent();
	}

	private String _bundleRoot(String absolutePath) {
		int x = absolutePath.indexOf("/portal-impl/");

		if (x != -1) {
			return "portal-impl";
		}

		x = absolutePath.indexOf("-service/");

		if (x != -1) {
			return absolutePath.substring(0, x + 8);
		}

		return absolutePath;
	}

	private void _collectMatchingMethods(
			File file, String methodName, int callArgsCount,
			List<JavaMethod> matchedMethods)
		throws Exception {

		if (!file.exists()) {
			return;
		}

		String fileContent = FileUtil.read(file);

		if (_hasSideEffectingClassAnnotation(fileContent)) {
			return;
		}

		JavaClass javaClass = JavaClassParser.parseJavaClass(
			file.getName(), fileContent);

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!(childJavaTerm instanceof JavaMethod)) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			if (!Objects.equals(methodName, javaMethod.getName())) {
				continue;
			}

			JavaSignature javaSignature = javaMethod.getSignature();

			List<JavaParameter> javaParameters = javaSignature.getParameters();

			if (javaParameters.size() == callArgsCount) {
				matchedMethods.add(javaMethod);
			}
		}
	}

	private int _getArgumentsCount(String content, int openParenPos) {
		int depth = 0;
		int count = 0;
		boolean sawArgument = false;

		for (int i = openParenPos; i < content.length(); i++) {
			char c = content.charAt(i);

			if ((c == '(') || (c == '<') || (c == '[')) {
				depth++;
			}
			else if ((c == ')') || (c == '>') || (c == ']')) {
				depth--;

				if (depth == 0) {
					if (sawArgument) {
						return count + 1;
					}

					return 0;
				}
			}
			else if ((c == ',') && (depth == 1)) {
				count++;
			}
			else if ((depth == 1) && !Character.isWhitespace(c)) {
				sawArgument = true;
			}
		}

		return count;
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
				// the preceding non-whitespace characters are the "->" arrow.

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
		String localServiceFullyQualifiedName, String rootDirName,
		String absolutePath) {

		String baseImplFullyQualifiedName = _toBaseImplName(
			localServiceFullyQualifiedName);

		File baseImplFile = JavaSourceUtil.getJavaFile(
			baseImplFullyQualifiedName, rootDirName,
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

	private String _getImportedName(String fileContent, String simpleName) {
		for (String importName : JavaSourceUtil.getImportNames(fileContent)) {
			if (importName.endsWith("." + simpleName)) {
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

	private String _getPackageName(String fileContent) {
		Matcher matcher = _packagePattern.matcher(fileContent);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return "";
	}

	private String _getPassthroughPersistenceCall(
			File baseImplFile, String methodName, int callArgsCount)
		throws Exception {

		// Collect matching overloads from BOTH the generated base impl and the
		// hand-written impl. A hand-written overload of the same name and
		// argument count (for example fetchRelease(String) alongside the
		// generated fetchRelease(long)) makes the target ambiguous without type
		// resolution, so conservatively skip.

		List<JavaMethod> matchedMethods = new ArrayList<>();

		_collectMatchingMethods(
			baseImplFile, methodName, callArgsCount, matchedMethods);

		File implFile = new File(
			StringUtil.replace(
				StringUtil.replace(
					baseImplFile.getAbsolutePath(), "/base/", "/impl/"),
				"BaseImpl.java", "Impl.java"));

		_collectMatchingMethods(
			implFile, methodName, callArgsCount, matchedMethods);

		if (matchedMethods.size() != 1) {
			return null;
		}

		return _passthroughPersistenceCall(matchedMethods.get(0));
	}

	private String _getRootDirName(String absolutePath) {
		int x = absolutePath.indexOf("/portal-impl/");

		if (x == -1) {
			x = absolutePath.indexOf("/modules/");
		}

		if (x == -1) {
			return "";
		}

		return absolutePath.substring(0, x + 1);
	}

	private boolean _hasSideEffectingAnnotation(String methodContent) {
		for (String annotation : _SIDE_EFFECTING_ANNOTATIONS) {
			if (methodContent.contains(annotation)) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasSideEffectingClassAnnotation(String fileContent) {
		Matcher matcher = _classDeclarationPattern.matcher(fileContent);

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

			String methodContent = javaMethod.getContent();

			if (methodName.equals(javaMethod.getName()) &&
				methodContent.contains("@Transactional(enabled = false)")) {

				return true;
			}
		}

		return false;
	}

	private boolean _isInTransactionDisabledLambda(
			String content, int pos, String fileName, String fileContent,
			String absolutePath, String rootDirName)
		throws Exception {

		for (String target : _getEnclosingLambdaTargets(content, pos)) {
			if (_isTransactionDisabledTarget(
					target, fileName, fileContent, absolutePath, rootDirName)) {

				return true;
			}
		}

		return false;
	}

	private boolean _isSameBundle(
		String callerAbsolutePath, String targetAbsolutePath) {

		return Objects.equals(
			_bundleRoot(callerAbsolutePath), _bundleRoot(targetAbsolutePath));
	}

	private boolean _isTransactionDisabledTarget(
			String reference, String fileName, String fileContent,
			String absolutePath, String rootDirName)
		throws Exception {

		int x = reference.lastIndexOf('.');

		String methodName = reference.substring(x + 1);

		if (x == -1) {

			// A call with no receiver runs on the caller's own service, so the
			// method is declared in this file.

			return _hasTransactionDisabledMethod(
				fileName, fileContent, methodName);
		}

		String receiver = reference.substring(0, x);

		int y = receiver.lastIndexOf('.');

		String fieldName = receiver.substring(y + 1);

		String localServiceInterface = StringUtil.upperCaseFirstLetter(
			StringUtil.removeChar(fieldName, '_'));

		if (!localServiceInterface.endsWith("LocalService")) {
			return false;
		}

		String localServiceFullyQualifiedName = _getImportedName(
			fileContent, localServiceInterface);

		if (localServiceFullyQualifiedName == null) {
			return false;
		}

		File implFile = _getImplFile(
			localServiceFullyQualifiedName, rootDirName, absolutePath);

		if ((implFile == null) || !implFile.exists()) {
			return false;
		}

		return _hasTransactionDisabledMethod(
			implFile.getName(), FileUtil.read(implFile), methodName);
	}

	private String _passthroughPersistenceCall(JavaMethod javaMethod) {
		String methodContent = javaMethod.getContent();

		if (_hasSideEffectingAnnotation(methodContent)) {
			return null;
		}

		Matcher matcher = _passthroughBodyPattern.matcher(methodContent);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group(1) + "." + matcher.group(2);
	}

	private String _toBaseImplName(String localServiceFullyQualifiedName) {

		// A kernel service interface is implemented in portal-impl, where the
		// base impl package drops the ".kernel" segment

		if (localServiceFullyQualifiedName.contains(".kernel.service.")) {
			String baseName = StringUtil.replace(
				localServiceFullyQualifiedName, ".kernel.service.",
				".service.base.");

			return baseName + "BaseImpl";
		}

		int x = localServiceFullyQualifiedName.lastIndexOf('.');

		String packageName = localServiceFullyQualifiedName.substring(0, x);
		String simpleName = localServiceFullyQualifiedName.substring(x + 1);

		return StringBundler.concat(
			packageName, ".base.", simpleName, "BaseImpl");
	}

	private static final String _ALLOWED_FILE_NAMES_KEY = "allowedFileNames";

	private static final String[] _SIDE_EFFECTING_ANNOTATIONS = {
		"@AccessControlled", "@Async", "@BufferedIncrement", "@Clusterable",
		"@Indexable", "@Retry", "@SystemEvent", "@ThreadLocalCachable"
	};

	private static final Pattern _callPattern = Pattern.compile(
		"\\b(_?\\w+?)LocalService\\.(\\w+)\\s*\\(");
	private static final Pattern _classDeclarationPattern = Pattern.compile(
		"((?:@\\w+(?:\\([^)]*\\))?\\s*)*)public\\s+(?:abstract\\s+)?class\\s");
	private static final Pattern _packagePattern = Pattern.compile(
		"(?m)^package\\s+([\\w.]+);");
	private static final Pattern _passthroughBodyPattern = Pattern.compile(
		"\\)\\s*(?:throws[\\w\\s,.]*)?\\{\\s*return\\s+(\\w+Persistence)\\.(" +
			"(?:fetch|find|count|filterFind|filterCount)\\w*)\\([^;]*\\);" +
				"\\s*\\}\\s*\\z");

}