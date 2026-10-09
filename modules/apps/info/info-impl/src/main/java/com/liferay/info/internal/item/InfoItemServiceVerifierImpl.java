/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.internal.item;

import com.liferay.info.exception.CapabilityVerificationException;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.InfoItemServiceVerifier;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(service = InfoItemServiceVerifier.class)
public class InfoItemServiceVerifierImpl implements InfoItemServiceVerifier {

	@Override
	public List<Class<?>> getMissingServiceClasses(
			Class<?>[] requiredInfoItemServiceClasses, String itemClassName)
		throws CapabilityVerificationException {

		List<Class<?>> missingServiceClasses = new ArrayList<>();

		for (Class<?> serviceClass : requiredInfoItemServiceClasses) {
			Object infoItemService =
				_infoItemServiceRegistry.getFirstInfoItemService(
					serviceClass, itemClassName);

			if (infoItemService != null) {
				continue;
			}

			missingServiceClasses.add(serviceClass);
		}

		return missingServiceClasses;
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}