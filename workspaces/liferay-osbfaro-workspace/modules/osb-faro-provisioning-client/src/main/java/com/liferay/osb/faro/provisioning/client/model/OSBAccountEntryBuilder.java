/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.provisioning.client.model;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class OSBAccountEntryBuilder {

	public static AfterNameStep setName(String name) {
		OSBAccountEntryStep osbAccountEntryStep = new OSBAccountEntryStep();

		return osbAccountEntryStep.setName(name);
	}

	public static class OSBAccountEntryStep
		implements AfterNameStep, AfterOfferingEntriesStep, BuildStep, NameStep,
				   OfferingEntriesStep {

		@Override
		public OSBAccountEntry build() {
			return _osbAccountEntry;
		}

		@Override
		public AfterNameStep setName(String name) {
			_osbAccountEntry.setName(name);

			return this;
		}

		@Override
		public AfterOfferingEntriesStep setOfferingEntries(
			List<OSBOfferingEntry> offeringEntries) {

			_osbAccountEntry.setOfferingEntries(offeringEntries);

			return this;
		}

		private final OSBAccountEntry _osbAccountEntry = new OSBAccountEntry();

	}

	public interface AfterNameStep extends OfferingEntriesStep {
	}

	public interface AfterOfferingEntriesStep extends BuildStep {
	}

	public interface BuildStep {

		public OSBAccountEntry build();

	}

	public interface NameStep {

		public AfterNameStep setName(String name);

	}

	public interface OfferingEntriesStep {

		public AfterOfferingEntriesStep setOfferingEntries(
			List<OSBOfferingEntry> offeringEntries);

	}

}