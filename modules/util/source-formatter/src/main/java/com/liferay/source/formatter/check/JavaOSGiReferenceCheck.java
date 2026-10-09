/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.BNDSettings;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaOSGiReferenceCheck extends BaseFileCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!content.contains("@Component")) {
			return content;
		}

		String packageName = JavaSourceUtil.getPackageName(content);

		if (!packageName.startsWith("com.liferay")) {
			return content;
		}

		_checkMissingReference(fileName, content);

		String moduleSuperClassContent = _getModuleSuperClassContent(
			content, JavaSourceUtil.getClassName(fileName), packageName);

		content = _formatDuplicateReferenceMethods(
			fileName, content, moduleSuperClassContent);

		if (!isExcludedPath("service.reference.util.excludes", absolutePath)) {
			for (String serviceProxyFactoryUtilClassName :
					_getServiceProxyFactoryUtilClassNames()) {

				_checkUtilUsage(
					fileName, content, serviceProxyFactoryUtilClassName,
					moduleSuperClassContent);
			}
		}

		List<String> serviceReferenceUtilClassNames = getAttributeValues(
			_SERVICE_REFERENCE_UTIL_CLASS_NAMES_KEY, absolutePath);

		for (String serviceReferenceUtilClassName :
				serviceReferenceUtilClassNames) {

			_checkUtilUsage(
				fileName, content, serviceReferenceUtilClassName,
				moduleSuperClassContent);
		}

		_checkUnnecessaryVariableInjection(fileName, absolutePath, content);

		return content;
	}

	private void _checkMissingReference(String fileName, String content) {
		String moduleServicePackageName = null;

		Matcher matcher = _serviceUtilImportPattern.matcher(content);

		while (matcher.find()) {
			String serviceUtilClassName = matcher.group(2);

			if (serviceUtilClassName.equals("IdentifiableOSGiServiceUtil") ||
				serviceUtilClassName.equals("SystemExecutorServiceUtil")) {

				continue;
			}

			if (moduleServicePackageName == null) {
				moduleServicePackageName = _getModuleServicePackageName(
					fileName);
			}

			if (Validator.isNotNull(moduleServicePackageName)) {
				String serviceUtilClassPackageName = matcher.group(1);

				if (serviceUtilClassPackageName.startsWith(
						moduleServicePackageName)) {

					continue;
				}
			}

			addMessage(
				fileName,
				"Use @Reference instead of calling " + serviceUtilClassName +
					" directly");
		}
	}

	private void _checkUnnecessaryVariableInjection(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!absolutePath.contains("-service/") ||
			!absolutePath.contains("/service/impl/") ||
			!fileName.endsWith("ServiceImpl.java")) {

			return;
		}

		BNDSettings bndSettings = getBNDSettings(fileName);

		String bndSettingsContent = bndSettings.getContent();

		if (!bndSettingsContent.contains("-dsannotations-options: inherit")) {
			return;
		}

		String serviceBaseClassPath = StringUtil.replace(
			absolutePath, new String[] {"/service/impl/", "Impl.java"},
			new String[] {"/service/base/", "BaseImpl.java"});

		File file = new File(serviceBaseClassPath);

		if (!file.exists()) {
			return;
		}

		JavaClass serviceBaseJavaClass = JavaClassParser.parseJavaClass(
			serviceBaseClassPath, FileUtil.read(file));

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (!javaTerm.hasAnnotation() || !javaTerm.isJavaVariable() ||
				!javaTerm.isPrivate()) {

				continue;
			}

			String fieldTypeClassName = _getFieldTypeClassName(
				javaTerm, JavaTerm.ACCESS_MODIFIER_PRIVATE, javaClass);

			if (Validator.isNull(fieldTypeClassName)) {
				continue;
			}

			for (JavaTerm serviceBaseJavaTerm :
					serviceBaseJavaClass.getChildJavaTerms()) {

				if (!serviceBaseJavaTerm.hasAnnotation() ||
					!serviceBaseJavaTerm.isJavaVariable() ||
					!serviceBaseJavaTerm.isProtected()) {

					continue;
				}

				String serviceBaseFieldTypeClassName = _getFieldTypeClassName(
					serviceBaseJavaTerm, JavaTerm.ACCESS_MODIFIER_PROTECTED,
					serviceBaseJavaClass);

				if (Validator.isNull(serviceBaseFieldTypeClassName) ||
					!fieldTypeClassName.equals(serviceBaseFieldTypeClassName)) {

					continue;
				}

				addMessage(
					fileName,
					"Use super class variable \"" +
						serviceBaseJavaTerm.getName() +
							"\" instead of injection",
					javaTerm.getLineNumber());
			}
		}
	}

	private void _checkUtilUsage(
			String fileName, String content,
			String serviceReferenceUtilClassName,
			String moduleSuperClassContent)
		throws Exception {

		if (!content.contains(serviceReferenceUtilClassName) ||
			(Validator.isNotNull(moduleSuperClassContent) &&
			 moduleSuperClassContent.contains("@Component"))) {

			return;
		}

		int pos = serviceReferenceUtilClassName.lastIndexOf(StringPool.PERIOD);

		String shortClassName = serviceReferenceUtilClassName.substring(
			pos + 1);

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (javaTerm.isStatic() ||
				(!javaTerm.isJavaConstructor() && !javaTerm.isJavaMethod()) ||
				javaTerm.hasAnnotation("Reference")) {

				continue;
			}

			String javaTermContent = javaTerm.getContent();

			if (javaTermContent.matches(
					StringBundler.concat(
						"(?s).*", shortClassName, "\\.(?!class).*"))) {

				addMessage(
					fileName,
					"Use portal service reference instead of \"" +
						serviceReferenceUtilClassName + "\" in modules");

				return;
			}
		}
	}

	private String _formatDuplicateReferenceMethods(
			String fileName, String content, String moduleSuperClassContent)
		throws Exception {

		if (Validator.isNull(moduleSuperClassContent) ||
			!moduleSuperClassContent.contains("@Component") ||
			!moduleSuperClassContent.contains("@Reference")) {

			return content;
		}

		Matcher matcher = _referenceMethodPattern.matcher(
			moduleSuperClassContent);

		boolean bndInheritRequired = false;

		while (matcher.find()) {
			String referenceMethod = matcher.group();

			int pos = content.indexOf(referenceMethod);

			if (pos != -1) {
				String referenceMethodContent = matcher.group(6);

				Matcher referenceMethodContentMatcher =
					_referenceMethodContentPattern.matcher(
						referenceMethodContent);

				if (referenceMethodContentMatcher.find()) {
					String variableName = referenceMethodContentMatcher.group(
						1);

					if (StringUtil.count(content, variableName) > 1) {
						continue;
					}
				}

				int x = content.lastIndexOf("\n\n", pos);
				int y = pos + referenceMethod.length();

				String entireMethod = content.substring(x + 1, y);

				content = StringUtil.removeSubstring(content, entireMethod);

				bndInheritRequired = true;
			}
			else {
				String referenceMethodModifierAndName = matcher.group(2);

				Pattern duplicateReferenceMethodPattern = Pattern.compile(
					referenceMethodModifierAndName +
						"\\(\\s*([ ,<>\\w]+)\\s+\\w+\\) \\{\\s+([\\s\\S]*?)" +
							"\\s*?\n\t\\}\n");

				Matcher duplicateReferenceMethodMatcher =
					duplicateReferenceMethodPattern.matcher(content);

				if (!duplicateReferenceMethodMatcher.find()) {
					bndInheritRequired = true;

					continue;
				}

				String methodContent = duplicateReferenceMethodMatcher.group(2);
				String referenceMethodName = matcher.group(4);

				if (methodContent.startsWith("super." + referenceMethodName)) {
					int x = content.lastIndexOf(
						"\n\n", duplicateReferenceMethodMatcher.start());
					int y = duplicateReferenceMethodMatcher.end();

					String entireMethod = content.substring(x + 1, y);

					content = StringUtil.removeSubstring(content, entireMethod);

					bndInheritRequired = true;
				}
			}
		}

		if (bndInheritRequired) {
			BNDSettings bndSettings = getBNDSettings(fileName);

			String bndSettingsContent = bndSettings.getContent();

			if (!bndSettingsContent.contains(
					"-dsannotations-options: inherit") &&
				_bndFileNames.add(bndSettings.getFileName())) {

				addMessage(
					fileName,
					"Add \"-dsannotations-options: inherit\" to " +
						bndSettings.getFileName());
			}
		}

		return content;
	}

	private String _getFieldTypeClassName(
		JavaTerm javaTerm, String accessModifier, JavaClass javaClass) {

		Pattern pattern = Pattern.compile(
			"@Reference\n\t+" + accessModifier + "\\s(\\S+)\\s+(\\S+\\.)?\\w+");

		Matcher matcher = pattern.matcher(javaTerm.getContent());

		if (!matcher.find()) {
			return null;
		}

		String fieldTypeClassName = matcher.group(1);

		if (!fieldTypeClassName.contains(StringPool.PERIOD)) {
			fieldTypeClassName = _getFullyQualifiedName(
				fieldTypeClassName, javaClass);
		}

		return fieldTypeClassName;
	}

	private String _getFullyQualifiedName(
		String className, JavaClass javaClass) {

		for (String importName : javaClass.getImportNames()) {
			if (importName.endsWith(StringPool.PERIOD + className)) {
				return importName;
			}
		}

		return javaClass.getPackageName() + StringPool.PERIOD + className;
	}

	private String _getModuleClassContent(String fullClassName)
		throws Exception {

		String classContent = _moduleFileContentsMap.get(fullClassName);

		if (classContent != null) {
			return classContent;
		}

		Map<String, String> moduleFileNamesMap = _getModuleFileNamesMap();

		String moduleFileName = moduleFileNamesMap.get(fullClassName);

		if (moduleFileName == null) {
			_moduleFileContentsMap.put(fullClassName, StringPool.BLANK);

			return StringPool.BLANK;
		}

		File file = new File(moduleFileName);

		classContent = FileUtil.read(file);

		if (classContent != null) {
			_moduleFileContentsMap.put(fullClassName, classContent);
		}

		return classContent;
	}

	private synchronized Map<String, String> _getModuleFileNamesMap()
		throws Exception {

		if (_moduleFileNamesMap != null) {
			return _moduleFileNamesMap;
		}

		_moduleFileNamesMap = new HashMap<>();

		List<String> fileNames = new ArrayList<>();

		String moduleRootDirLocation = "modules/";

		for (int i = 0; i < getMaxDirLevel(); i++) {
			File file = new File(getBaseDirName() + moduleRootDirLocation);

			if (file.exists()) {
				fileNames = SourceFormatterUtil.scanForFileNames(
					file.getCanonicalPath(), new String[] {"**/*.java"});

				break;
			}

			moduleRootDirLocation = "../" + moduleRootDirLocation;
		}

		for (String fileName : fileNames) {
			String className = StringUtil.replace(
				fileName, CharPool.SLASH, CharPool.PERIOD);

			int pos = className.lastIndexOf(".com.liferay.");

			className = className.substring(pos + 1, fileName.length() - 5);

			_moduleFileNamesMap.put(className, fileName);
		}

		return _moduleFileNamesMap;
	}

	private String _getModuleServicePackageName(String fileName) {
		String serviceDirLocation = fileName;

		while (true) {
			int pos = serviceDirLocation.lastIndexOf(StringPool.SLASH);

			if (pos == -1) {
				return StringPool.BLANK;
			}

			serviceDirLocation = serviceDirLocation.substring(0, pos + 1);

			File file = new File(serviceDirLocation + "service");

			if (file.exists()) {
				serviceDirLocation = serviceDirLocation + "service";

				break;
			}

			file = new File(serviceDirLocation + "liferay");

			if (file.exists()) {
				return StringPool.BLANK;
			}

			serviceDirLocation = StringUtil.replaceLast(
				serviceDirLocation, CharPool.SLASH, StringPool.BLANK);
		}

		serviceDirLocation = StringUtil.replace(
			serviceDirLocation, CharPool.SLASH, CharPool.PERIOD);

		int pos = serviceDirLocation.lastIndexOf(".com.");

		return serviceDirLocation.substring(pos + 1);
	}

	private String _getModuleSuperClassContent(
			String content, String className, String packageName)
		throws Exception {

		Pattern pattern = Pattern.compile(
			" class " + className + "\\s+extends\\s+([\\w.]+) ");

		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			return null;
		}

		String superClassName = matcher.group(1);

		if (superClassName.contains(StringPool.PERIOD)) {
			if (!superClassName.startsWith("com.liferay")) {
				return null;
			}

			return _getModuleClassContent(superClassName);
		}

		String superClassPackageName = packageName;

		pattern = Pattern.compile("\nimport (.+?)\\." + superClassName + ";");

		matcher = pattern.matcher(content);

		if (matcher.find()) {
			superClassPackageName = matcher.group(1);
		}

		if (!superClassPackageName.startsWith("com.liferay")) {
			return null;
		}

		String superClassFullClassName =
			superClassPackageName + StringPool.PERIOD + superClassName;

		return _getModuleClassContent(superClassFullClassName);
	}

	private synchronized List<String> _getServiceProxyFactoryUtilClassNames()
		throws Exception {

		if (_serviceProxyFactoryUtilClassNames != null) {
			return _serviceProxyFactoryUtilClassNames;
		}

		if (!isPortalSource()) {
			_serviceProxyFactoryUtilClassNames = Collections.emptyList();

			return _serviceProxyFactoryUtilClassNames;
		}

		String portalKernelLocation = "portal-kernel/";

		for (int i = 0; i < (getMaxDirLevel() - 1); i++) {
			File file = new File(getBaseDirName() + portalKernelLocation);

			if (!file.exists()) {
				portalKernelLocation = "../" + portalKernelLocation;

				continue;
			}

			_serviceProxyFactoryUtilClassNames = new ArrayList<>();

			List<String> utilFileNames = getFileNames(
				getBaseDirName() + portalKernelLocation, new String[0],
				new String[] {"**/*Util.java"});

			for (String fileName : utilFileNames) {
				String content = FileUtil.read(new File(fileName));

				if (!content.contains(
						"com.liferay.portal.kernel.util.ServiceProxyFactory")) {

					continue;
				}

				_serviceProxyFactoryUtilClassNames.add(
					JavaSourceUtil.getPackageName(content) + "." +
						JavaSourceUtil.getClassName(fileName));
			}

			return _serviceProxyFactoryUtilClassNames;
		}

		_serviceProxyFactoryUtilClassNames = Collections.emptyList();

		return _serviceProxyFactoryUtilClassNames;
	}

	private static final String _SERVICE_REFERENCE_UTIL_CLASS_NAMES_KEY =
		"serviceReferenceUtilClassNames";

	private static final Pattern _referenceMethodContentPattern =
		Pattern.compile("^(\\w+) =\\s+\\w+;$");
	private static final Pattern _referenceMethodPattern = Pattern.compile(
		"\n\t@Reference([\\s\\S]*?)\\s+((protected|public) void (\\w+?))\\(" +
			"\\s*([ ,<>?\\w]+)\\s+\\w+\\) \\{\\s+([\\s\\S]*?)\\s*?\n\t\\}\n");
	private static final Pattern _serviceUtilImportPattern = Pattern.compile(
		"\nimport ([A-Za-z1-9\\.]*)\\.([A-Za-z1-9]*ServiceUtil);");

	private final Set<String> _bndFileNames = new CopyOnWriteArraySet<>();
	private final Map<String, String> _moduleFileContentsMap =
		new ConcurrentHashMap<>();
	private Map<String, String> _moduleFileNamesMap;
	private List<String> _serviceProxyFactoryUtilClassNames;

}