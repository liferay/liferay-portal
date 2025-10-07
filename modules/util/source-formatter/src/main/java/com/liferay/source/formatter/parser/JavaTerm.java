/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.parser;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public interface JavaTerm {

	public static final String ACCESS_MODIFIER_DEFAULT = "default";

	public static final String ACCESS_MODIFIER_PRIVATE = "private";

	public static final String ACCESS_MODIFIER_PROTECTED = "protected";

	public static final String ACCESS_MODIFIER_PUBLIC = "public";

	public static final String[] ACCESS_MODIFIERS = {
		ACCESS_MODIFIER_DEFAULT, ACCESS_MODIFIER_PRIVATE,
		ACCESS_MODIFIER_PROTECTED, ACCESS_MODIFIER_PUBLIC
	};

	public String getAccessModifier();

	public String getContent();

	public List<String> getImportNames();

	public int getLineNumber();

	public int getLineNumber(int pos);

	public String getName();

	public String getPackageName();

	public JavaClass getParentJavaClass();

	public JavaMethod getParentJavaMethod();

	public JavaSignature getSignature();

	public boolean hasAnnotation();

	public boolean hasAnnotation(String... annotations);

	public boolean isAbstract();

	public boolean isDefault();

	public boolean isFinal();

	public boolean isJavaClass();

	public boolean isJavaConstructor();

	public boolean isJavaMethod();

	public boolean isJavaStaticBlock();

	public boolean isJavaVariable();

	public boolean isPrivate();

	public boolean isProtected();

	public boolean isPublic();

	public boolean isStatic();

	public void setParentJavaClass(JavaClass javaClass);

	public void setParentJavaMethod(JavaMethod javaMethod);

}