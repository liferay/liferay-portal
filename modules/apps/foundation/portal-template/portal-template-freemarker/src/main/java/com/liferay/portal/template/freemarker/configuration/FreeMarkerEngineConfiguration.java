/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.template.freemarker.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Peter Fellwock
 */
@ExtendedObjectClassDefinition(category = "foundation")
@Meta.OCD(
	id = "com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration",
	localization = "content/Language",
	name = "freemarker-engine-configuration-name"
)
public interface FreeMarkerEngineConfiguration {

	@Meta.AD(deflt = "false", name = "localized-lookup", required = false)
	public boolean localizedLookup();

	@Meta.AD(
		deflt = "60000", name = "resource-modification-check", required = false
	)
	public int resourceModificationCheck();

	@Meta.AD(name = "allowed-classes", required = false)
	public String[] allowedClasses();

	@Meta.AD(
		deflt = "com.liferay.portal.json.jabsorb.serializer.LiferayJSONDeserializationWhitelist|java.lang.Class|java.lang.ClassLoader|java.lang.Compiler|java.lang.Package|java.lang.Process|java.lang.Runtime|java.lang.RuntimePermission|java.lang.SecurityManager|java.lang.System|java.lang.Thread|java.lang.ThreadGroup|java.lang.ThreadLocal",
		name = "restricted-classes", required = false
	)
	public String[] restrictedClasses();

	@Meta.AD(
		deflt = "httpUtilUnsafe|objectUtil|serviceLocator|staticFieldGetter|staticUtil|utilLocator",
		name = "restricted-variables", required = false
	)
	public String[] restrictedVariables();

	@Meta.AD(
		deflt = "rethrow", name = "template-exception-handler", required = false
	)
	public String templateExceptionHandler();

	@Meta.AD(
		deflt = "FTL_liferay.ftl as liferay", name = "macro-library",
		required = false
	)
	public String[] macroLibrary();

}