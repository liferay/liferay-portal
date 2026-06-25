/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.spi.reindexer;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Bryan Engler
 */
public interface IndexReindexer {

	public void reindex(long companyId, ExecutionMode executionMode)
		throws Exception;

	public default void reindex(long companyId, String executionMode)
		throws Exception {

		reindex(companyId, ExecutionMode.parse(executionMode));
	}

	public enum ExecutionMode {

		CONCURRENT("concurrent"), FULL("full"), SYNC("sync");

		public static ExecutionMode parse(String value) {
			if (Validator.isBlank(value)) {
				return FULL;
			}

			for (ExecutionMode executionMode : values()) {
				if (StringUtil.equalsIgnoreCase(
						executionMode.getValue(), value) ||
					StringUtil.equalsIgnoreCase(executionMode.name(), value)) {

					return executionMode;
				}
			}

			return FULL;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private ExecutionMode(String value) {
			_value = value;
		}

		private final String _value;

	}

}