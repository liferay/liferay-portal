/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Raymond Augé
 * @author Jorge Ferrer
 * @author Iván Zaera
 */
public interface Settings {

	public ModifiableSettings getModifiableSettings();

	public Settings getParentSettings();

	public String getValue(String key, String defaultValue);

	public String[] getValues(String key, String[] defaultValue);

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Config {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Property {

		public boolean ignore() default false;

		public String name() default "";

	}

}