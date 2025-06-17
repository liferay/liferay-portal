/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.GitUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.BNDSettings;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.processor.SourceProcessor;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaComponentAnnotationsCheck extends JavaAnnotationsCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String formatAnnotation(
			String fileName, String absolutePath, JavaClass javaClass,
			String fileContent, String annotation, String indent)
		throws Exception {

		String trimmedAnnotation = StringUtil.trim(annotation);

		if (!trimmedAnnotation.equals("@Component") &&
			!trimmedAnnotation.startsWith("@Component(")) {

			return annotation;
		}

		List<String> importNames = javaClass.getImportNames();

		if (!importNames.contains(
				"org.osgi.service.component.annotations.Component")) {

			return annotation;
		}

		_checkImmediateAttribute(fileName, absolutePath, annotation);

		annotation = _formatAnnotationParameterProperties(annotation);
		annotation = _formatConfigurationAttributes(
			fileName, absolutePath, javaClass, annotation);
		annotation = _formatEnabledAttribute(absolutePath, annotation);
		annotation = _formatServiceAttribute(
			fileName, absolutePath, javaClass, annotation);

		List<String> extendedClassNames = javaClass.getExtendedClassNames(
			false);

		if (extendedClassNames.contains("MVCPortlet")) {
			annotation = _formatMVCPortletPropertyAttribute(
				absolutePath, annotation);
		}

		if (fileName.endsWith("ResourceImpl.java")) {
			annotation = _formatResourceImplPropertyAttribute(
				absolutePath, javaClass, annotation);
		}

		return annotation;
	}

	private String _addAttribute(
		String annotation, String attributeName, String attributeValue) {

		if (!annotation.contains("(")) {
			return StringBundler.concat(
				annotation.substring(0, annotation.length() - 1), "(",
				attributeName, " = ", attributeValue, ")\n");
		}

		Matcher matcher = _attributePattern.matcher(annotation);

		while (matcher.find()) {
			if (!ToolsUtil.isInsideQuotes(annotation, matcher.end(1)) &&
				(getLevel(annotation.substring(0, matcher.end()), "{", "}") ==
					0)) {

				String curAttributeName = matcher.group(1);

				if (curAttributeName.compareTo(attributeName) > 0) {
					return StringUtil.insert(
						annotation,
						StringBundler.concat(
							attributeName, " = ", attributeValue, ", "),
						matcher.start(1));
				}
			}
		}

		String indent = SourceUtil.getIndent(annotation);

		if (annotation.endsWith("\n" + indent + ")\n")) {
			int pos = annotation.lastIndexOf("\n", annotation.length() - 2);

			return StringUtil.insert(
				annotation,
				StringBundler.concat(
					",\n\t", indent, attributeName, " = ", attributeValue),
				pos);
		}

		return StringUtil.replaceLast(
			annotation, ')',
			StringBundler.concat(
				", ", attributeName, " = ", attributeValue, ")"));
	}

	private String _addNewProperties(String newProperties, String properties) {
		newProperties = StringUtil.trimTrailing(newProperties);

		if (!newProperties.endsWith(StringPool.COMMA)) {
			newProperties += StringPool.COMMA;
		}

		return newProperties + properties;
	}

	private void _checkHasMultipleServiceTypes(
			String fileName, String absolutePath)
		throws Exception {

		SourceProcessor sourceProcessor = getSourceProcessor();

		SourceFormatterArgs sourceFormatterArgs =
			sourceProcessor.getSourceFormatterArgs();

		if (!sourceFormatterArgs.isFormatCurrentBranch()) {
			return;
		}

		List<String> allowedMultipleServicesClassNames = getAttributeValues(
			_ALLOWED_MULTIPLE_SERVICE_TYPES_CLASS_NAMES_KEY, absolutePath);

		for (String allowedMultipleServicesClassName :
				allowedMultipleServicesClassNames) {

			if (absolutePath.contains(allowedMultipleServicesClassName)) {
				return;
			}
		}

		for (String currentBranchRenamedFileName :
				sourceFormatterArgs.getCurrentBranchRenamedFileNames()) {

			if (absolutePath.endsWith(currentBranchRenamedFileName)) {
				return;
			}
		}

		String currentBranchFileDiff = GitUtil.getCurrentBranchFileDiff(
			sourceFormatterArgs.getBaseDirName(),
			sourceFormatterArgs.getGitWorkingBranchName(), absolutePath);

		for (String currentBranchFileDiffBlock :
				StringUtil.split(currentBranchFileDiff, "\n@@")) {

			if (currentBranchFileDiffBlock.startsWith("diff") ||
				!currentBranchFileDiffBlock.contains("@Component")) {

				continue;
			}

			for (String line :
					StringUtil.splitLines(currentBranchFileDiffBlock)) {

				if (!line.startsWith(StringPool.PLUS)) {
					continue;
				}

				if (line.contains("service = {") &&
					!line.contains("service = {}")) {

					addMessage(
						fileName,
						"@Component classes should only specify one service " +
							"type in the \"service\" attribute, see " +
								"LPS-180838");

					break;
				}
			}
		}
	}

	private void _checkImmediateAttribute(
		String fileName, String absolutePath, String annotation) {

		if (absolutePath.contains("/modules/apps/archived/") ||
			!isAttributeValue(_CHECK_IMMEDIATE_ATTRIBUTE_KEY, absolutePath)) {

			return;
		}

		List<String> allowedImmediateAttributeClassNames = getAttributeValues(
			_ALLOWED_IMMEDIATE_ATTRIBUTE_CLASS_NAMES_KEY, absolutePath);

		for (String allowedImmediateAttributeClassName :
				allowedImmediateAttributeClassNames) {

			if (absolutePath.contains(allowedImmediateAttributeClassName)) {
				return;
			}
		}

		String immediateAttributeValue = getAnnotationAttributeValue(
			annotation, "immediate");

		if ((immediateAttributeValue != null) &&
			immediateAttributeValue.equals("true")) {

			addMessage(
				fileName, "Do not use \"immediate = true\" in @Component");
		}
	}

	private void _checkUsesInternalService(
			String fileName, String absolutePath, JavaClass javaClass,
			String serviceAttributeValue)
		throws Exception {

		if ((!absolutePath.contains("/modules/apps/") &&
			 !absolutePath.contains("/modules/dxp/apps/")) ||
			absolutePath.contains("/modules/apps/archived/")) {

			return;
		}

		List<String> allowedUsesInternalServiceClassNames = getAttributeValues(
			_ALLOWED_USES_INTERNAL_SERVICE_CLASS_NAMES_KEY, absolutePath);

		for (String allowedUsesInternalServiceClassName :
				allowedUsesInternalServiceClassNames) {

			if (absolutePath.contains(allowedUsesInternalServiceClassName)) {
				return;
			}
		}

		String className = serviceAttributeValue.substring(
			0, serviceAttributeValue.indexOf(CharPool.PERIOD));

		if (className.equals(javaClass.getName())) {
			return;
		}

		String packageName = JavaSourceUtil.getPackageName(
			className, javaClass.getPackageName(), javaClass.getImportNames());

		if (!packageName.startsWith("com.liferay.")) {
			return;
		}

		String fullyQualifiedName = StringBundler.concat(
			packageName, StringPool.PERIOD, className);

		if (packageName.contains(".internal.")) {
			addMessage(
				fileName,
				StringBundler.concat(
					"The \"service\" attribute points to \"",
					fullyQualifiedName,
					"\", which is an internal class or interface"));

			return;
		}

		File javaFile = JavaSourceUtil.getJavaFile(
			fullyQualifiedName, _getRootDirName(absolutePath),
			_getBundleSymbolicNamesMap(absolutePath));

		if (javaFile == null) {
			return;
		}

		BNDSettings currentBNDSettings = getBNDSettings(absolutePath);
		BNDSettings serviceBNDSettings = getBNDSettings(
			SourceUtil.getAbsolutePath(javaFile));

		if (!Objects.equals(
				currentBNDSettings.getFileName(),
				serviceBNDSettings.getFileName())) {

			return;
		}

		if (_isInternalPackageName(
				packageName, serviceBNDSettings.getExportPackageNames())) {

			addMessage(
				fileName,
				StringBundler.concat(
					"The \"service\" attribute points to \"",
					fullyQualifiedName,
					"\", which is an internal class or interface"));
		}
	}

	private String _formatAnnotationParameterProperties(String annotation) {
		Matcher matcher = _annotationParameterPropertyPattern.matcher(
			annotation);

		while (matcher.find()) {
			int x = matcher.end() - 1;

			while (true) {
				x = annotation.indexOf(CharPool.CLOSE_CURLY_BRACE, x + 1);

				if (!ToolsUtil.isInsideQuotes(annotation, x)) {
					break;
				}
			}

			String parameterProperties = annotation.substring(matcher.end(), x);

			String newParameterProperties = StringUtil.replace(
				parameterProperties, new String[] {" =", "= "},
				new String[] {"=", "="});

			if (!parameterProperties.equals(newParameterProperties)) {
				return StringUtil.replaceFirst(
					annotation, parameterProperties, newParameterProperties);
			}

			parameterProperties = StringUtil.replace(
				parameterProperties,
				new String[] {
					StringPool.TAB, StringPool.FOUR_SPACES, StringPool.NEW_LINE
				},
				new String[] {
					StringPool.BLANK, StringPool.BLANK, StringPool.SPACE
				});

			parameterProperties = StringUtil.trim(parameterProperties);

			if (parameterProperties.startsWith(StringPool.AT)) {
				continue;
			}

			String[] parameterPropertiesArray = StringUtil.split(
				parameterProperties, StringPool.COMMA_AND_SPACE);

			AnnotationParameterPropertyComparator comparator =
				new AnnotationParameterPropertyComparator(matcher.group(1));

			for (int i = 1; i < parameterPropertiesArray.length; i++) {
				String parameterProperty = parameterPropertiesArray[i];
				String previousParameterProperty =
					parameterPropertiesArray[i - 1];

				int compare = comparator.compare(
					previousParameterProperty, parameterProperty);

				if (compare > 0) {
					annotation = StringUtil.replaceFirst(
						annotation, previousParameterProperty,
						parameterProperty);
					annotation = StringUtil.replaceLast(
						annotation, parameterProperty,
						previousParameterProperty);

					return annotation;
				}
			}
		}

		return annotation;
	}

	private String _formatConfigurationAttributes(
		String fileName, String absolutePath, JavaClass javaClass,
		String annotation) {

		String configurationPid = getAnnotationAttributeValue(
			annotation, "configurationPid");

		if (configurationPid != null) {
			return _formatConfigurationPid(
				fileName, absolutePath, javaClass, annotation,
				configurationPid);
		}

		for (JavaMethod javaMethod :
				_getJavaMethods(javaClass, "Activate", "Modified")) {

			String javaMethodContent = javaMethod.getContent();

			if (javaMethodContent.contains(
					"ConfigurableUtil.createConfigurable")) {

				addMessage(
					fileName,
					"Missing @Component \"configurationPid\" attribute, see " +
						"LPS-88783");

				break;
			}
		}

		if (!isAttributeValue(
				_CHECK_CONFIGURATION_POLICY_ATTRIBUTE_KEY, absolutePath)) {

			return annotation;
		}

		List<String> imports = javaClass.getImportNames();

		if (imports.contains(
				"org.osgi.service.component.annotations.Modified") ||
			(getAnnotationAttributeValue(annotation, "configurationPolicy ") !=
				null)) {

			return annotation;
		}

		for (JavaMethod javaMethod : _getJavaMethods(javaClass, "Activate")) {
			JavaSignature signature = javaMethod.getSignature();

			for (JavaParameter parameter : signature.getParameters()) {
				String parameterType = parameter.getParameterType();

				if (parameterType.equals("ComponentContext") ||
					parameterType.startsWith("Map<")) {

					return annotation;
				}
			}
		}

		return _addAttribute(
			annotation, "configurationPolicy", "ConfigurationPolicy.IGNORE");
	}

	private String _formatConfigurationPid(
		String fileName, String absolutePath, JavaClass javaClass,
		String annotation, String configurationPid) {

		if (!isAttributeValue(
				_CHECK_CONFIGURATION_PID_ATTRIBUTE_KEY, absolutePath)) {

			return annotation;
		}

		List<String> configurationClasses = new ArrayList<>();

		if (StringUtil.startsWith(configurationPid, '{')) {
			configurationPid = configurationPid.substring(
				1, configurationPid.length() - 1);

			Collections.addAll(
				configurationClasses, StringUtil.split(configurationPid, ", "));
		}
		else {
			configurationClasses.add(configurationPid);
		}

		if (isAttributeValue(
				_CHECK_HAS_MULTIPLE_CONFIGURATION_PIDS_KEY, absolutePath) &&
			(configurationClasses.size() > 1)) {

			addMessage(
				fileName,
				"Component classes cannot have multiple configuration PIDs");

			return annotation;
		}

		List<String> importNames = javaClass.getImportNames();

		for (String configurationClass : configurationClasses) {
			configurationClass = StringUtil.unquote(configurationClass);

			if (!configurationClass.startsWith("com.liferay")) {
				continue;
			}

			int pos = configurationClass.lastIndexOf(".scoped");

			if (pos != -1) {
				configurationClass = configurationClass.substring(0, pos);
			}

			if (importNames.contains(configurationClass)) {
				continue;
			}

			File javaFile = JavaSourceUtil.getJavaFile(
				configurationClass, _getRootDirName(absolutePath),
				_getBundleSymbolicNamesMap(absolutePath));

			if (javaFile == null) {
				String message = StringBundler.concat(
					"Remove \"", configurationClass,
					"\" from \"configurationPid\" as the configuration class ",
					"does not exist");

				addMessage(fileName, message);
			}
		}

		return annotation;
	}

	private String _formatEnabledAttribute(
		String absolutePath, String annotation) {

		if (absolutePath.contains("-test/") ||
			absolutePath.contains("-test-util/")) {

			return annotation;
		}

		List<String> enterpriseAppModulePathNames = getAttributeValues(
			_ENTERPRISE_APP_MODULE_PATH_NAMES_KEY, absolutePath);

		if (enterpriseAppModulePathNames.isEmpty()) {
			return annotation;
		}

		for (String enterpriseAppModulePathName :
				enterpriseAppModulePathNames) {

			if (!absolutePath.contains(enterpriseAppModulePathName)) {
				continue;
			}

			String enabledAttributeValue = getAnnotationAttributeValue(
				annotation, "enabled");

			if (enabledAttributeValue == null) {
				return _addAttribute(annotation, "enabled", "false");
			}
		}

		return annotation;
	}

	private String _formatMVCPortletPropertyAttribute(
		String absolutePath, String annotation) {

		String propertyAttribute = _getPropertyAttribute(annotation);

		if (propertyAttribute == null) {
			return annotation;
		}

		String portletInitParamConfigTemplateString =
			"javax.portlet.init-param.config-template=";
		String portletPortletModeString = "javax.portlet.portlet-mode=";
		String portletSupportsMimeTypeString =
			"javax.portlet.supports.mime-type=text/html";
		String portletVersionString = "javax.portlet.version=3.0";

		if (isAttributeValue(
				SourceFormatterUtil.JAKARTA_USED_BRANCH, absolutePath)) {

			portletInitParamConfigTemplateString = StringUtil.replaceFirst(
				portletInitParamConfigTemplateString, "javax.", "jakarta.");
			portletPortletModeString = StringUtil.replaceFirst(
				portletPortletModeString, "javax.", "jakarta.");
			portletSupportsMimeTypeString = StringUtil.replaceFirst(
				portletSupportsMimeTypeString, "javax.", "jakarta.");
			portletVersionString = StringUtil.replace(
				portletVersionString, new String[] {"javax.", "3.0"},
				new String[] {"jakarta.", "4.0"});
		}

		String newPropertyAttribute = StringUtil.replace(
			propertyAttribute,
			new String[] {
				"\"" + portletSupportsMimeTypeString + "\",",
				"\"" + portletSupportsMimeTypeString + "\""
			},
			new String[] {StringPool.BLANK, StringPool.BLANK});

		if (newPropertyAttribute.contains(
				"\"" + portletInitParamConfigTemplateString) &&
			!newPropertyAttribute.contains(portletPortletModeString)) {

			newPropertyAttribute = _addNewProperties(
				newPropertyAttribute,
				"\"" + portletPortletModeString + "text/html;config\"");
		}

		if (isAttributeValue(_CHECK_PORTLET_VERSION_KEY, absolutePath) &&
			!absolutePath.contains("/modules/apps/archived/") &&
			!absolutePath.contains("/modules/sdk/") &&
			!newPropertyAttribute.contains(
				"\"" + portletVersionString + "\"")) {

			String serviceAttributeValue = getAnnotationAttributeValue(
				annotation, "service");

			if (serviceAttributeValue.startsWith(StringPool.OPEN_CURLY_BRACE) &&
				serviceAttributeValue.endsWith(StringPool.CLOSE_CURLY_BRACE)) {

				serviceAttributeValue = serviceAttributeValue.substring(
					1, serviceAttributeValue.length() - 1);
			}

			List<String> serviceAttributeValues = ListUtil.fromString(
				serviceAttributeValue, StringPool.COMMA);

			if (serviceAttributeValues.contains("Portlet.class")) {
				newPropertyAttribute = _addNewProperties(
					newPropertyAttribute, "\"" + portletVersionString + "\"");
			}
		}

		return StringUtil.replace(
			annotation, propertyAttribute, newPropertyAttribute);
	}

	private String _formatResourceImplPropertyAttribute(
		String absolutePath, JavaClass javaClass, String annotation) {

		if (!isAttributeValue(_CHECK_RESOURCE_IMPL_KEY, absolutePath)) {
			return annotation;
		}

		boolean hasNestedField = false;

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (childJavaTerm.hasAnnotation("NestedField")) {
				hasNestedField = true;

				break;
			}
		}

		String propertyAttributeValue = getAnnotationAttributeValue(
			annotation, "property");

		if (hasNestedField) {
			if (propertyAttributeValue == null) {
				annotation = _addAttribute(
					annotation, "property", "\"nested.field.support=true\"");
			}
			else if (propertyAttributeValue.contains(
						"\"nested.field.support")) {

				annotation = annotation.replaceFirst(
					"\"nested.field.support=false\"",
					"\"nested.field.support=true\"");
			}
			else {
				String property = _getPropertyAttribute(annotation);

				if (property == null) {
					return annotation;
				}

				annotation = StringUtil.replace(
					annotation, property,
					_addNewProperties(
						property, "\"nested.field.support=true\""));
			}
		}
		else if ((propertyAttributeValue != null) &&
				 propertyAttributeValue.contains("\"nested.field.support")) {

			List<String> propertyValues = ListUtil.fromString(
				propertyAttributeValue, StringPool.COMMA_AND_SPACE);

			if (propertyValues.size() == 1) {
				return _removePropertyAttribute(annotation);
			}

			return annotation.replaceFirst(
				"\"nested.field.support=\\w+\",?\\s*", StringPool.BLANK);
		}

		return annotation;
	}

	private String _formatServiceAttribute(
			String fileName, String absolutePath, JavaClass javaClass,
			String annotation)
		throws Exception {

		String expectedServiceAttributeValue =
			_getExpectedServiceAttributeValue(
				javaClass.getImplementedClassNames());

		String serviceAttributeValue = getAnnotationAttributeValue(
			annotation, "service");

		if (serviceAttributeValue == null) {
			return _addAttribute(
				annotation, "service", expectedServiceAttributeValue);
		}

		boolean checkMismatchedServiceAttribute = isAttributeValue(
			_CHECK_MISMATCHED_SERVICE_ATTRIBUTE_KEY, absolutePath);
		boolean checkSelfRegistration = isAttributeValue(
			_CHECK_SELF_REGISTRATION_KEY, absolutePath);
		boolean checkHasMultipleServiceTypes = isAttributeValue(
			_CHECK_HAS_MULTIPLE_SERVICE_TYPES_KEY, absolutePath);
		boolean checkUsesInternalService = isAttributeValue(
			_CHECK_USES_INTERNAL_SERVICE_KEY, absolutePath);

		if (checkMismatchedServiceAttribute &&
			!serviceAttributeValue.equals(expectedServiceAttributeValue)) {

			addMessage(fileName, "Mismatched @Component \"service\" attribute");
		}

		String className = javaClass.getName();

		if (checkSelfRegistration &&
			serviceAttributeValue.matches(".*\\b" + className + "\\.class.*")) {

			List<String> allowedSelfRegistrationClassNames = getAttributeValues(
				_ALLOWED_SELF_REGISTRATION_CLASS_NAMES_KEY, absolutePath);

			boolean allowed = false;

			for (String allowedSelfRegistrationClassName :
					allowedSelfRegistrationClassNames) {

				if (absolutePath.contains(allowedSelfRegistrationClassName)) {
					allowed = true;

					break;
				}
			}

			if (!allowed) {
				addMessage(
					fileName,
					"No need to register \"" + className +
						"\" in @Component \"service\" attribute");
			}
		}

		if (checkHasMultipleServiceTypes) {
			_checkHasMultipleServiceTypes(fileName, absolutePath);
		}

		if (checkUsesInternalService &&
			serviceAttributeValue.endsWith(".class")) {

			_checkUsesInternalService(
				fileName, absolutePath, javaClass, serviceAttributeValue);
		}

		return annotation;
	}

	private synchronized Map<String, String> _getBundleSymbolicNamesMap(
		String absolutePath) {

		if (_bundleSymbolicNamesMap == null) {
			_bundleSymbolicNamesMap = BNDSourceUtil.getBundleSymbolicNamesMap(
				_getRootDirName(absolutePath));
		}

		return _bundleSymbolicNamesMap;
	}

	private String _getExpectedServiceAttributeValue(
		List<String> implementedClassNames) {

		if (implementedClassNames.isEmpty()) {
			return "{}";
		}

		if (implementedClassNames.size() == 1) {
			return implementedClassNames.get(0) + ".class";
		}

		StringBundler sb = new StringBundler(
			(implementedClassNames.size() * 3) + 1);

		sb.append("{");

		for (String implementedClassName : implementedClassNames) {
			sb.append(implementedClassName);
			sb.append(".class");
			sb.append(", ");
		}

		sb.setIndex(sb.index() - 1);

		sb.append("}");

		return sb.toString();
	}

	private List<JavaMethod> _getJavaMethods(
		JavaClass javaClass, String... annotations) {

		List<JavaMethod> javaMethods = new ArrayList<>();

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (!(javaTerm instanceof JavaMethod)) {
				continue;
			}

			for (String annotation : annotations) {
				if (javaTerm.hasAnnotation(annotation)) {
					javaMethods.add((JavaMethod)javaTerm);

					break;
				}
			}
		}

		return javaMethods;
	}

	private String _getPropertyAttribute(String annotation) {
		int x = annotation.indexOf("property = {");

		if (x == -1) {
			return null;
		}

		int y = x;

		while (true) {
			y = annotation.indexOf(CharPool.CLOSE_CURLY_BRACE, y + 1);

			if (!ToolsUtil.isInsideQuotes(annotation, y)) {
				break;
			}
		}

		return annotation.substring(x, y);
	}

	private synchronized String _getRootDirName(String absolutePath) {
		if (_rootDirName == null) {
			_rootDirName = SourceUtil.getRootDirName(absolutePath);
		}

		return _rootDirName;
	}

	private boolean _isInternalPackageName(
		String packageName, List<String> exportPackageNames) {

		for (String exportPackageName : exportPackageNames) {
			if (packageName.equals(exportPackageName)) {
				return false;
			}

			boolean negation = false;

			if (exportPackageName.startsWith(StringPool.EXCLAMATION)) {
				negation = true;
				exportPackageName = exportPackageName.substring(1);
			}

			if (negation && packageName.equals(exportPackageName)) {
				break;
			}

			if (exportPackageName.endsWith(StringPool.STAR)) {
				exportPackageName = exportPackageName.substring(
					0, exportPackageName.length() - 1);

				if (packageName.startsWith(exportPackageName)) {
					if (negation) {
						break;
					}

					return false;
				}
			}
		}

		return true;
	}

	private String _removePropertyAttribute(String annotation) {
		if (!annotation.contains("(")) {
			return annotation;
		}

		int x = annotation.indexOf("property = {");
		char closingChar = CharPool.CLOSE_CURLY_BRACE;

		if (x == -1) {
			x = annotation.indexOf("property = \"");
			closingChar = CharPool.QUOTE;
		}

		if (x == -1) {
			return annotation;
		}

		int y = x;

		while (true) {
			y = annotation.indexOf(closingChar, y + 1);

			if (!ToolsUtil.isInsideQuotes(annotation, y)) {
				break;
			}
		}

		return annotation.replaceFirst(
			annotation.substring(x, y + 1) + ",\\s*", StringPool.BLANK);
	}

	private static final String _ALLOWED_IMMEDIATE_ATTRIBUTE_CLASS_NAMES_KEY =
		"allowedImmediateAttributeClassNames";

	private static final String
		_ALLOWED_MULTIPLE_SERVICE_TYPES_CLASS_NAMES_KEY =
			"allowedMultipleServiceTypesClassNames";

	private static final String _ALLOWED_SELF_REGISTRATION_CLASS_NAMES_KEY =
		"allowedSelfRegistrationClassNames";

	private static final String _ALLOWED_USES_INTERNAL_SERVICE_CLASS_NAMES_KEY =
		"allowedUsesInternalServiceClassNames";

	private static final String _CHECK_CONFIGURATION_PID_ATTRIBUTE_KEY =
		"checkConfigurationPidAttribute";

	private static final String _CHECK_CONFIGURATION_POLICY_ATTRIBUTE_KEY =
		"checkConfigurationPolicyAttribute";

	private static final String _CHECK_HAS_MULTIPLE_CONFIGURATION_PIDS_KEY =
		"checkHasMultipleConfigurationPids";

	private static final String _CHECK_HAS_MULTIPLE_SERVICE_TYPES_KEY =
		"checkHasMultipleServiceTypes";

	private static final String _CHECK_IMMEDIATE_ATTRIBUTE_KEY =
		"checkImmediateAttribute";

	private static final String _CHECK_MISMATCHED_SERVICE_ATTRIBUTE_KEY =
		"checkMismatchedServiceAttribute";

	private static final String _CHECK_PORTLET_VERSION_KEY =
		"checkPortletVersion";

	private static final String _CHECK_RESOURCE_IMPL_KEY = "checkResourceImpl";

	private static final String _CHECK_SELF_REGISTRATION_KEY =
		"checkSelfRegistration";

	private static final String _CHECK_USES_INTERNAL_SERVICE_KEY =
		"checkUsesInternalService";

	private static final String _ENTERPRISE_APP_MODULE_PATH_NAMES_KEY =
		"enterpriseAppModulePathNames";

	private static final Pattern _annotationParameterPropertyPattern =
		Pattern.compile("\\s(\\w+) = \\{");
	private static final Pattern _attributePattern = Pattern.compile(
		"\\W(\\w+)\\s*=");

	private Map<String, String> _bundleSymbolicNamesMap;
	private String _rootDirName;

	private class AnnotationParameterPropertyComparator
		extends NaturalOrderStringComparator {

		public AnnotationParameterPropertyComparator(String parameterName) {
			_parameterName = parameterName;
		}

		public int compare(String property1, String property2) {
			if (!_parameterName.equals("property")) {
				return super.compare(property1, property2);
			}

			String propertyName1 = _getPropertyName(property1);
			String propertyName2 = _getPropertyName(property2);

			if (propertyName1.equals(propertyName2)) {
				return super.compare(property1, property2);
			}

			int value = super.compare(propertyName1, propertyName2);

			if (propertyName1.startsWith(StringPool.QUOTE) ^
				propertyName2.startsWith(StringPool.QUOTE)) {

				return -value;
			}

			return value;
		}

		private String _getPropertyName(String property) {
			int x = property.indexOf(StringPool.EQUAL);

			if (x != -1) {
				return property.substring(0, x);
			}

			return property;
		}

		private final String _parameterName;

	}

}