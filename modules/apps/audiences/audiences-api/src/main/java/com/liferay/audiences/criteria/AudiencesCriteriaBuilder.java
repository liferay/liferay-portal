/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.criteria;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class AudiencesCriteriaBuilder {

	public static AfterIconStep setIcon(String icon) {
		AudiencesCriteriaStep audiencesCriteriaStep =
			new AudiencesCriteriaStep();

		return audiencesCriteriaStep.setIcon(icon);
	}

	public static class AudiencesCriteriaStep
		implements AfterIconStep, AfterInputTypeStep, AfterKeyStep,
				   AfterLabelStep, AfterOptionsStep, AfterTypeStep, BuildStep,
				   IconStep, InputTypeStep, KeyStep, LabelStep, OptionsStep,
				   TypeStep {

		@Override
		public AudiencesCriteria build() {
			return _audiencesCriteria;
		}

		@Override
		public AfterIconStep setIcon(String icon) {
			_audiencesCriteria.setIcon(icon);

			return this;
		}

		@Override
		public AfterInputTypeStep setInputType(
			AudiencesCriteria.InputType inputType) {

			_audiencesCriteria.setInputType(inputType);

			return this;
		}

		@Override
		public AfterKeyStep setKey(String key) {
			_audiencesCriteria.setKey(key);

			return this;
		}

		@Override
		public AfterLabelStep setLabel(String label) {
			_audiencesCriteria.setLabel(label);

			return this;
		}

		@Override
		public AfterOptionsStep setOptions(
			List<AudiencesCriteria.Option> options) {

			_audiencesCriteria.setOptions(options);

			return this;
		}

		@Override
		public AfterTypeStep setType(AudiencesCriteria.Type type) {
			_audiencesCriteria.setType(type);

			return this;
		}

		private final AudiencesCriteria _audiencesCriteria =
			new AudiencesCriteria();

	}

	public interface AfterIconStep extends InputTypeStep {
	}

	public interface AfterInputTypeStep extends KeyStep {
	}

	public interface AfterKeyStep extends LabelStep {
	}

	public interface AfterLabelStep extends OptionsStep, TypeStep {
	}

	public interface AfterOptionsStep extends TypeStep {
	}

	public interface AfterTypeStep extends BuildStep {
	}

	public interface BuildStep {

		public AudiencesCriteria build();

	}

	public interface IconStep {

		public AfterIconStep setIcon(String icon);

	}

	public interface InputTypeStep {

		public AfterInputTypeStep setInputType(
			AudiencesCriteria.InputType inputType);

	}

	public interface KeyStep {

		public AfterKeyStep setKey(String key);

	}

	public interface LabelStep {

		public AfterLabelStep setLabel(String label);

	}

	public interface OptionsStep {

		public AfterOptionsStep setOptions(
			List<AudiencesCriteria.Option> options);

	}

	public interface TypeStep {

		public AfterTypeStep setType(AudiencesCriteria.Type type);

	}

}