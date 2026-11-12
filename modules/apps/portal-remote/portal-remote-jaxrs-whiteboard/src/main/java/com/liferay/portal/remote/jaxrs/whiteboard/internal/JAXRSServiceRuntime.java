/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.whiteboard.internal;

import com.liferay.portal.kernel.util.ArrayUtil;

import org.apache.aries.component.dsl.CachingServiceReference;
import org.apache.aries.jax.rs.whiteboard.internal.AriesJaxrsServiceRuntime;
import org.apache.aries.jax.rs.whiteboard.internal.Whiteboard;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jaxrs.runtime.JaxrsServiceRuntime;
import org.osgi.service.jaxrs.runtime.dto.BaseDTO;
import org.osgi.service.jaxrs.runtime.dto.DTOConstants;
import org.osgi.service.jaxrs.runtime.dto.FailedApplicationDTO;
import org.osgi.service.jaxrs.runtime.dto.FailedExtensionDTO;
import org.osgi.service.jaxrs.runtime.dto.FailedResourceDTO;
import org.osgi.service.jaxrs.runtime.dto.RuntimeDTO;

/**
 * @author Shuyang Zhou
 */
public class JAXRSServiceRuntime implements JaxrsServiceRuntime {

	public JAXRSServiceRuntime(
		AriesJaxrsServiceRuntime ariesJaxrsServiceRuntime,
		JAXRSServiceTracker jaxrsServiceTracker) {

		_ariesJaxrsServiceRuntime = ariesJaxrsServiceRuntime;
		_jaxrsServiceTracker = jaxrsServiceTracker;
	}

	@Override
	public RuntimeDTO getRuntimeDTO() {
		RuntimeDTO runtimeDTO = _ariesJaxrsServiceRuntime.getRuntimeDTO();

		for (CachingServiceReference<Object> cachingServiceReference :
				_jaxrsServiceTracker.
					getDuplicateNameCachingServiceReferences()) {

			if (JAXRSServiceTracker.isApplication(cachingServiceReference)) {
				FailedApplicationDTO failedApplicationDTO = _populateBaseDTO(
					new FailedApplicationDTO(), cachingServiceReference);

				failedApplicationDTO.failureReason =
					DTOConstants.FAILURE_REASON_DUPLICATE_NAME;

				runtimeDTO.failedApplicationDTOs = ArrayUtil.append(
					runtimeDTO.failedApplicationDTOs, failedApplicationDTO);
			}

			if (JAXRSServiceTracker.isExtension(cachingServiceReference)) {
				FailedExtensionDTO failedExtensionDTO = _populateBaseDTO(
					new FailedExtensionDTO(), cachingServiceReference);

				ServiceReference<Object> serviceReference =
					cachingServiceReference.getServiceReference();

				failedExtensionDTO.extensionTypes = ArrayUtil.filter(
					(String[])serviceReference.getProperty(
						Constants.OBJECTCLASS),
					Whiteboard.SUPPORTED_EXTENSION_INTERFACES::containsKey);

				failedExtensionDTO.failureReason =
					DTOConstants.FAILURE_REASON_DUPLICATE_NAME;

				runtimeDTO.failedExtensionDTOs = ArrayUtil.append(
					runtimeDTO.failedExtensionDTOs, failedExtensionDTO);
			}

			if (!JAXRSServiceTracker.isResource(cachingServiceReference)) {
				continue;
			}

			FailedResourceDTO failedResourceDTO = _populateBaseDTO(
				new FailedResourceDTO(), cachingServiceReference);

			failedResourceDTO.failureReason =
				DTOConstants.FAILURE_REASON_DUPLICATE_NAME;

			runtimeDTO.failedResourceDTOs = ArrayUtil.append(
				runtimeDTO.failedResourceDTOs, failedResourceDTO);
		}

		return runtimeDTO;
	}

	private <T extends BaseDTO> T _populateBaseDTO(
		T baseDTO, CachingServiceReference<Object> cachingServiceReference) {

		baseDTO.name = AriesJaxrsServiceRuntime.getServiceName(
			cachingServiceReference::getProperty);
		baseDTO.serviceId = (Long)cachingServiceReference.getProperty(
			Constants.SERVICE_ID);

		return baseDTO;
	}

	private final AriesJaxrsServiceRuntime _ariesJaxrsServiceRuntime;
	private final JAXRSServiceTracker _jaxrsServiceTracker;

}