/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Cavalcanti
 */
public class JavaUpgradeModelPermissionsCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		List<String> importNames = javaTerm.getImportNames();

		String javaTermContent = javaTerm.getContent();

		if (!importNames.contains(
				"com.liferay.portal.kernel.service.ServiceContext")) {

			return javaTermContent;
		}

		if (javaTerm.isJavaClass()) {
			return _formatClass(javaTermContent, importNames);
		}

		return _formatMethod(javaTerm, fileContent, fileName);
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS, JAVA_METHOD};
	}

	private String _formatClass(String content, List<String> importNames) {
		Matcher setGroupPermissionsMatcher =
			_setGroupPermissionsPattern.matcher(content);
		Matcher setGuestPermissionsMatcher =
			_setGuestPermissionsPattern.matcher(content);

		if (!(setGroupPermissionsMatcher.find() ||
			  setGuestPermissionsMatcher.find())) {

			return content;
		}

		String imports = "";

		for (String newImportName : _newImportNames) {
			if (importNames.contains(newImportName)) {
				continue;
			}

			imports = StringBundler.concat(
				imports, "import ", newImportName, ";\n\n");
		}

		return imports + content;
	}

	private String _formatMethod(
			JavaTerm javaTerm, String fileContent, String fileName)
		throws Exception {

		boolean hasSetGroupPermissions = false;

		String content = javaTerm.getContent();

		Matcher setGroupPermissionsMatcher =
			_setGroupPermissionsPattern.matcher(content);

		if (setGroupPermissionsMatcher.find()) {
			hasSetGroupPermissions = _isServiceContextMethodCall(
				javaTerm, fileContent, fileName,
				setGroupPermissionsMatcher.group(1));
		}

		boolean hasSetGuestPermissions = false;

		Matcher setGuestPermissionsMatcher =
			_setGuestPermissionsPattern.matcher(content);

		if (setGuestPermissionsMatcher.find()) {
			hasSetGuestPermissions = _isServiceContextMethodCall(
				javaTerm, fileContent, fileName,
				setGuestPermissionsMatcher.group(1));
		}

		if (hasSetGroupPermissions || hasSetGuestPermissions) {
			content = _formatSetGroupAndGuestPermissions(
				hasSetGroupPermissions, hasSetGuestPermissions,
				setGroupPermissionsMatcher, setGuestPermissionsMatcher,
				content);
		}

		return content;
	}

	private String _formatSetGroupAndGuestPermissions(
		boolean hasSetGroupPermissions, boolean hasSetGuestPermissions,
		Matcher setGroupPermissionsMatcher, Matcher setGuestPermissionsMatcher,
		String javaTermContent) {

		String groupPermissions = "new String[0]";
		String guestPermissions = "new String[0]";
		String oldSub = null;
		String serviceContext = null;

		if (hasSetGroupPermissions) {
			groupPermissions = setGroupPermissionsMatcher.group(2);
			oldSub = setGroupPermissionsMatcher.group(0);
			serviceContext = setGroupPermissionsMatcher.group(1);

			if (hasSetGuestPermissions) {
				guestPermissions = setGuestPermissionsMatcher.group(2);
				javaTermContent = StringUtil.removeSubstring(
					javaTermContent, setGuestPermissionsMatcher.group(0));
			}
		}
		else {
			guestPermissions = setGuestPermissionsMatcher.group(2);
			oldSub = setGuestPermissionsMatcher.group(0);
			serviceContext = setGuestPermissionsMatcher.group(1);
		}

		return StringUtil.replace(
			javaTermContent, oldSub,
			_getModelPermissionsImplementation(
				groupPermissions, guestPermissions,
				SourceUtil.getIndent(oldSub), serviceContext));
	}

	private String _getModelPermissionsImplementation(
		String groupPermissions, String guestPermissions, String indent,
		String serviceContext) {

		StringBundler sb = new StringBundler(35);

		sb.append(StringPool.NEW_LINE);
		sb.append(indent);
		sb.append("ModelPermissions modelPermissions = ");
		sb.append(serviceContext);
		sb.append(".getModelPermissions();\n\n");
		sb.append(indent);
		sb.append("if (modelPermissions == null) {\n");
		sb.append(indent);
		sb.append("\tmodelPermissions = ModelPermissionsFactory.create(");
		sb.append(groupPermissions);
		sb.append(StringPool.COMMA_AND_SPACE);
		sb.append(guestPermissions);
		sb.append(");\n");
		sb.append(indent);
		sb.append("}\n");
		sb.append(indent);
		sb.append("else {");

		if (!groupPermissions.equals("new String[0]")) {
			sb.append(StringPool.NEW_LINE);
			sb.append(indent);
			sb.append("\tmodelPermissions.addRolePermissions(");
			sb.append("RoleConstants.PLACEHOLDER_DEFAULT_GROUP_ROLE, ");
			sb.append(groupPermissions);
			sb.append(");");
		}

		if (!guestPermissions.equals("new String[0]")) {
			sb.append(StringPool.NEW_LINE);
			sb.append(indent);
			sb.append("\tmodelPermissions.addRolePermissions(");
			sb.append("RoleConstants.GUEST, ");
			sb.append(guestPermissions);
			sb.append(");");
		}

		sb.append(StringPool.NEW_LINE);
		sb.append(indent);
		sb.append("}\n\n");
		sb.append(indent);
		sb.append(serviceContext);
		sb.append(".setModelPermissions(modelPermissions);");

		return sb.toString();
	}

	private boolean _isServiceContextMethodCall(
			JavaTerm javaTerm, String fileContent, String fileName,
			String variableName)
		throws Exception {

		String variableTypeName = getVariableTypeName(
			javaTerm.getContent(), javaTerm, fileContent, fileName,
			variableName);

		if (variableTypeName == null) {
			return false;
		}

		return Objects.equals(variableTypeName, "ServiceContext");
	}

	private static final List<String> _newImportNames = Arrays.asList(
		"com.liferay.portal.kernel.model.role.RoleConstants",
		"com.liferay.portal.kernel.service.permission.ModelPermissions",
		"com.liferay.portal.kernel.service.permission.ModelPermissionsFactory");
	private static final Pattern _setGroupPermissionsPattern = Pattern.compile(
		"\\t*(\\w+)\\.setGroupPermissions\\(\\s*(\\w+)\\s*\\);");
	private static final Pattern _setGuestPermissionsPattern = Pattern.compile(
		"\\t*(\\w+)\\.setGuestPermissions\\(\\s*(\\w+)\\s*\\);");

}