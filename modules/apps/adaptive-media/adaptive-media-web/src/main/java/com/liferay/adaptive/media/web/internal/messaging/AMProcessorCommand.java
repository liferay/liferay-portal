/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.messaging;

import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Adolfo Pérez
 */
public enum AMProcessorCommand {

	PROCESS {

		@Override
		protected <M> void execute(
				AMProcessor<M> amProcessor, M model, String modelId)
			throws PortalException {

			amProcessor.process(model);
		}

	};

	protected abstract <M> void execute(
			AMProcessor<M> amProcessor, M model, String modelId)
		throws PortalException;

}