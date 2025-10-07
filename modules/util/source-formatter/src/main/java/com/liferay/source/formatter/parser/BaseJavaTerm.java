/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.parser;

import com.liferay.petra.string.StringBundler;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public abstract class BaseJavaTerm implements JavaTerm {

	public BaseJavaTerm(
		String accessModifier, String content, boolean isAbstract,
		boolean isFinal, boolean isStatic, int lineNumber, String name) {

		_accessModifier = accessModifier;
		_content = content;
		_isAbstract = isAbstract;
		_isFinal = isFinal;
		_isStatic = isStatic;
		_lineNumber = lineNumber;
		_name = name;
	}

	@Override
	public String getAccessModifier() {
		return _accessModifier;
	}

	@Override
	public String getContent() {
		return _content;
	}

	@Override
	public List<String> getImportNames() {
		JavaClass parentJavaClass = _parentJavaClass;

		while (parentJavaClass.getParentJavaClass() != null) {
			parentJavaClass = parentJavaClass.getParentJavaClass();
		}

		return parentJavaClass.getImportNames();
	}

	@Override
	public int getLineNumber() {
		return _lineNumber;
	}

	@Override
	public int getLineNumber(int pos) {
		return _lineNumber + SourceUtil.getLineNumber(_content, pos) - 1;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getPackageName() {
		JavaClass parentJavaClass = _parentJavaClass;

		while (parentJavaClass.getParentJavaClass() != null) {
			parentJavaClass = parentJavaClass.getParentJavaClass();
		}

		return parentJavaClass.getPackageName();
	}

	@Override
	public JavaClass getParentJavaClass() {
		return _parentJavaClass;
	}

	@Override
	public JavaMethod getParentJavaMethod() {
		return _parentJavaMethod;
	}

	@Override
	public JavaSignature getSignature() {
		return null;
	}

	@Override
	public boolean hasAnnotation() {
		Pattern pattern = Pattern.compile(
			StringBundler.concat(
				"(\\A|\n)", SourceUtil.getIndent(_content), "@"));

		Matcher matcher = pattern.matcher(_content);

		return matcher.find();
	}

	@Override
	public boolean hasAnnotation(String... annotations) {
		for (String annotation : annotations) {
			Pattern pattern = Pattern.compile(
				StringBundler.concat(
					"(\\A|\n)", SourceUtil.getIndent(_content), "@", annotation,
					"(\\(|\n)"));

			Matcher matcher = pattern.matcher(_content);

			if (matcher.find()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isAbstract() {
		return _isAbstract;
	}

	@Override
	public boolean isDefault() {
		return Objects.equals(
			_accessModifier, JavaTerm.ACCESS_MODIFIER_DEFAULT);
	}

	@Override
	public boolean isFinal() {
		return _isFinal;
	}

	@Override
	public boolean isJavaClass() {
		if (this instanceof JavaClass) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isJavaConstructor() {
		if (this instanceof JavaConstructor) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isJavaMethod() {
		if (this instanceof JavaMethod) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isJavaStaticBlock() {
		if (this instanceof JavaStaticBlock) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isJavaVariable() {
		if (this instanceof JavaVariable) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isPrivate() {
		return Objects.equals(
			_accessModifier, JavaTerm.ACCESS_MODIFIER_PRIVATE);
	}

	@Override
	public boolean isProtected() {
		return Objects.equals(
			_accessModifier, JavaTerm.ACCESS_MODIFIER_PROTECTED);
	}

	@Override
	public boolean isPublic() {
		return Objects.equals(_accessModifier, JavaTerm.ACCESS_MODIFIER_PUBLIC);
	}

	@Override
	public boolean isStatic() {
		return _isStatic;
	}

	@Override
	public void setParentJavaClass(JavaClass javaClass) {
		_parentJavaClass = javaClass;
	}

	@Override
	public void setParentJavaMethod(JavaMethod javaMethod) {
		_parentJavaMethod = javaMethod;
	}

	private final String _accessModifier;
	private final String _content;
	private final boolean _isAbstract;
	private final boolean _isFinal;
	private final boolean _isStatic;
	private final int _lineNumber;
	private final String _name;
	private JavaClass _parentJavaClass;
	private JavaMethod _parentJavaMethod;

}