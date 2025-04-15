/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.ccpp.internal;

import com.liferay.portal.ccpp.PortalProfileFactory;

import com.sun.ccpp.ProfileFactoryImpl;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;

import javax.ccpp.Attribute;
import javax.ccpp.Profile;
import javax.ccpp.ProfileDescription;
import javax.ccpp.ProfileFactory;
import javax.ccpp.ValidationMode;

import org.osgi.service.component.annotations.Component;

/**
 * @author Shuyang Zhou
 */
@Component(service = PortalProfileFactory.class)
public class PortalProfileFactoryImpl implements PortalProfileFactory {

	@Override
	public Profile getCCPPProfile(HttpServletRequest httpServletRequest) {
		ProfileFactory profileFactory = ProfileFactory.getInstance();

		if (profileFactory == null) {
			profileFactory = ProfileFactoryImpl.getInstance();

			ProfileFactory.setInstance(profileFactory);
		}

		Profile profile = profileFactory.newProfile(
			httpServletRequest, ValidationMode.VALIDATIONMODE_NONE);

		if (profile == null) {
			profile = _profile;
		}

		return profile;
	}

	private static final Profile _profile = new Profile() {

		@Override
		public Attribute getAttribute(String name) {
			return null;
		}

		@Override
		public Set<Attribute> getAttributes() {
			return null;
		}

		@Override
		public javax.ccpp.Component getComponent(String localtype) {
			return null;
		}

		@Override
		public Set<javax.ccpp.Component> getComponents() {
			return null;
		}

		@Override
		public ProfileDescription getDescription() {
			return null;
		}

	};

}