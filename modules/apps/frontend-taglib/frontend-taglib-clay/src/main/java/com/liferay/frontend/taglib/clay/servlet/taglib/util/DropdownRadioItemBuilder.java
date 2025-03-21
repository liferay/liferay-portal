/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib.util;

import com.liferay.petra.function.UnsafeSupplier;

import jakarta.portlet.PortletURL;

import java.util.Map;

/**
 * @author Hugo Huijser
 */
public class DropdownRadioItemBuilder {

	public static AfterPutDataStep putData(String key, String value) {
		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.putData(key, value);
	}

	public static AfterPutDataStep putData(
		String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.putData(key, valueUnsafeSupplier);
	}

	public static AfterActiveStep setActive(boolean active) {
		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setActive(active);
	}

	public static AfterActiveStep setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setActive(activeUnsafeSupplier);
	}

	public static AfterCheckedStep setChecked(boolean checked) {
		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setChecked(checked);
	}

	public static AfterCheckedStep setChecked(
		UnsafeSupplier<Boolean, Exception> checkedUnsafeSupplier) {

		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setChecked(checkedUnsafeSupplier);
	}

	public static AfterSetDataStep setData(Map<String, Object> data) {
		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setData(data);
	}

	public static AfterDisabledStep setDisabled(boolean disabled) {
		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setDisabled(disabled);
	}

	public static AfterDisabledStep setDisabled(
		UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setDisabled(disabledUnsafeSupplier);
	}

	public static AfterHrefStep setHref(Object href) {
		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setHref(href);
	}

	public static AfterHrefStep setHref(
		PortletURL portletURL, Object... parameters) {

		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setHref(portletURL, parameters);
	}

	public static AfterHrefStep setHref(
		UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setHref(hrefUnsafeSupplier);
	}

	public static AfterIconStep setIcon(String icon) {
		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setIcon(icon);
	}

	public static AfterIconStep setIcon(
		UnsafeSupplier<String, Exception> iconUnsafeSupplier) {

		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setIcon(iconUnsafeSupplier);
	}

	public static AfterInputValueStep setInputValue(String inputValue) {
		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setInputValue(inputValue);
	}

	public static AfterInputValueStep setInputValue(
		UnsafeSupplier<String, Exception> inputValueUnsafeSupplier) {

		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setInputValue(inputValueUnsafeSupplier);
	}

	public static AfterLabelStep setLabel(String label) {
		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setLabel(label);
	}

	public static AfterLabelStep setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setLabel(labelUnsafeSupplier);
	}

	public static AfterQuickActionStep setQuickAction(boolean quickAction) {
		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setQuickAction(quickAction);
	}

	public static AfterQuickActionStep setQuickAction(
		UnsafeSupplier<Boolean, Exception> quickActionUnsafeSupplier) {

		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setQuickAction(quickActionUnsafeSupplier);
	}

	public static AfterSeparatorStep setSeparator(boolean separator) {
		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setSeparator(separator);
	}

	public static AfterSeparatorStep setSeparator(
		UnsafeSupplier<Boolean, Exception> separatorUnsafeSupplier) {

		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setSeparator(separatorUnsafeSupplier);
	}

	public static AfterTargetStep setTarget(String target) {
		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setTarget(target);
	}

	public static AfterTargetStep setTarget(
		UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setTarget(targetUnsafeSupplier);
	}

	public static AfterTypeStep setType(String type) {
		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setType(type);
	}

	public static AfterTypeStep setType(
		UnsafeSupplier<String, Exception> typeUnsafeSupplier) {

		DropdownRadioItemStep dropdownRadioItemStep =
			new DropdownRadioItemStep();

		return dropdownRadioItemStep.setType(typeUnsafeSupplier);
	}

	public static class DropdownRadioItemStep
		implements ActiveStep, AfterActiveStep, AfterCheckedStep,
				   AfterDisabledStep, AfterHrefStep, AfterIconStep,
				   AfterInputValueStep, AfterLabelStep, AfterPutDataStep,
				   AfterQuickActionStep, AfterSeparatorStep, AfterSetDataStep,
				   AfterTargetStep, AfterTypeStep, BuildStep, CheckedStep,
				   DisabledStep, HrefStep, IconStep, InputValueStep, LabelStep,
				   PutDataStep, QuickActionStep, SeparatorStep, SetDataStep,
				   TargetStep, TypeStep {

		@Override
		public DropdownRadioItem build() {
			return _dropdownRadioItem;
		}

		@Override
		public AfterPutDataStep putData(String key, String value) {
			_dropdownRadioItem.putData(key, value);

			return this;
		}

		@Override
		public AfterPutDataStep putData(
			String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

			try {
				String value = valueUnsafeSupplier.get();

				if (value != null) {
					_dropdownRadioItem.putData(key, value);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterActiveStep setActive(boolean active) {
			_dropdownRadioItem.setActive(active);

			return this;
		}

		@Override
		public AfterActiveStep setActive(
			UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

			try {
				Boolean active = activeUnsafeSupplier.get();

				if (active != null) {
					_dropdownRadioItem.setActive(active.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterCheckedStep setChecked(boolean checked) {
			_dropdownRadioItem.setChecked(checked);

			return this;
		}

		@Override
		public AfterCheckedStep setChecked(
			UnsafeSupplier<Boolean, Exception> checkedUnsafeSupplier) {

			try {
				Boolean checked = checkedUnsafeSupplier.get();

				if (checked != null) {
					_dropdownRadioItem.setChecked(checked.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterSetDataStep setData(Map<String, Object> data) {
			_dropdownRadioItem.setData(data);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(boolean disabled) {
			_dropdownRadioItem.setDisabled(disabled);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(
			UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

			try {
				Boolean disabled = disabledUnsafeSupplier.get();

				if (disabled != null) {
					_dropdownRadioItem.setDisabled(disabled.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterHrefStep setHref(Object href) {
			_dropdownRadioItem.setHref(href);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			PortletURL portletURL, Object... parameters) {

			_dropdownRadioItem.setHref(portletURL, parameters);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

			try {
				Object href = hrefUnsafeSupplier.get();

				if (href != null) {
					_dropdownRadioItem.setHref(href);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterIconStep setIcon(String icon) {
			_dropdownRadioItem.setIcon(icon);

			return this;
		}

		@Override
		public AfterIconStep setIcon(
			UnsafeSupplier<String, Exception> iconUnsafeSupplier) {

			try {
				String icon = iconUnsafeSupplier.get();

				if (icon != null) {
					_dropdownRadioItem.setIcon(icon);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterInputValueStep setInputValue(String inputValue) {
			_dropdownRadioItem.setInputValue(inputValue);

			return this;
		}

		@Override
		public AfterInputValueStep setInputValue(
			UnsafeSupplier<String, Exception> inputValueUnsafeSupplier) {

			try {
				String inputValue = inputValueUnsafeSupplier.get();

				if (inputValue != null) {
					_dropdownRadioItem.setInputValue(inputValue);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterLabelStep setLabel(String label) {
			_dropdownRadioItem.setLabel(label);

			return this;
		}

		@Override
		public AfterLabelStep setLabel(
			UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

			try {
				String label = labelUnsafeSupplier.get();

				if (label != null) {
					_dropdownRadioItem.setLabel(label);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterQuickActionStep setQuickAction(boolean quickAction) {
			_dropdownRadioItem.setQuickAction(quickAction);

			return this;
		}

		@Override
		public AfterQuickActionStep setQuickAction(
			UnsafeSupplier<Boolean, Exception> quickActionUnsafeSupplier) {

			try {
				Boolean quickAction = quickActionUnsafeSupplier.get();

				if (quickAction != null) {
					_dropdownRadioItem.setQuickAction(
						quickAction.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterSeparatorStep setSeparator(boolean separator) {
			_dropdownRadioItem.setSeparator(separator);

			return this;
		}

		@Override
		public AfterSeparatorStep setSeparator(
			UnsafeSupplier<Boolean, Exception> separatorUnsafeSupplier) {

			try {
				Boolean separator = separatorUnsafeSupplier.get();

				if (separator != null) {
					_dropdownRadioItem.setSeparator(separator.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterTargetStep setTarget(String target) {
			_dropdownRadioItem.setTarget(target);

			return this;
		}

		@Override
		public AfterTargetStep setTarget(
			UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

			try {
				String target = targetUnsafeSupplier.get();

				if (target != null) {
					_dropdownRadioItem.setTarget(target);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterTypeStep setType(String type) {
			_dropdownRadioItem.setType(type);

			return this;
		}

		@Override
		public AfterTypeStep setType(
			UnsafeSupplier<String, Exception> typeUnsafeSupplier) {

			try {
				String type = typeUnsafeSupplier.get();

				if (type != null) {
					_dropdownRadioItem.setType(type);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		private final DropdownRadioItem _dropdownRadioItem =
			new DropdownRadioItem();

	}

	public interface ActiveStep {

		public AfterActiveStep setActive(boolean active);

		public AfterActiveStep setActive(
			UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier);

	}

	public interface AfterActiveStep
		extends BuildStep, CheckedStep, DisabledStep, HrefStep, IconStep,
				InputValueStep, LabelStep, QuickActionStep, SeparatorStep,
				SetDataStep, TargetStep, TypeStep {
	}

	public interface AfterCheckedStep
		extends BuildStep, DisabledStep, HrefStep, IconStep, InputValueStep,
				LabelStep, QuickActionStep, SeparatorStep, SetDataStep,
				TargetStep, TypeStep {
	}

	public interface AfterDisabledStep
		extends BuildStep, HrefStep, IconStep, InputValueStep, LabelStep,
				QuickActionStep, SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterHrefStep
		extends BuildStep, IconStep, InputValueStep, LabelStep, QuickActionStep,
				SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterIconStep
		extends BuildStep, InputValueStep, LabelStep, QuickActionStep,
				SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterInputValueStep
		extends BuildStep, LabelStep, QuickActionStep, SeparatorStep,
				TargetStep, TypeStep {
	}

	public interface AfterLabelStep
		extends BuildStep, QuickActionStep, SeparatorStep, TargetStep,
				TypeStep {
	}

	public interface AfterPutDataStep
		extends ActiveStep, BuildStep, DisabledStep, HrefStep, IconStep,
				LabelStep, PutDataStep, QuickActionStep, SeparatorStep,
				SetDataStep, TargetStep, TypeStep {
	}

	public interface AfterQuickActionStep
		extends BuildStep, SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterSeparatorStep
		extends BuildStep, TargetStep, TypeStep {
	}

	public interface AfterSetDataStep
		extends BuildStep, DisabledStep, HrefStep, IconStep, LabelStep,
				QuickActionStep, SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterTargetStep extends BuildStep, TypeStep {
	}

	public interface AfterTypeStep extends BuildStep {
	}

	public interface BuildStep {

		public DropdownRadioItem build();

	}

	public interface CheckedStep {

		public AfterCheckedStep setChecked(boolean checked);

		public AfterCheckedStep setChecked(
			UnsafeSupplier<Boolean, Exception> checkedUnsafeSupplier);

	}

	public interface DisabledStep {

		public AfterDisabledStep setDisabled(boolean disabled);

		public AfterDisabledStep setDisabled(
			UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier);

	}

	public interface HrefStep {

		public AfterHrefStep setHref(Object href);

		public AfterHrefStep setHref(
			PortletURL portletURL, Object... parameters);

		public AfterHrefStep setHref(
			UnsafeSupplier<Object, Exception> hrefUnsafeSupplier);

	}

	public interface IconStep {

		public AfterIconStep setIcon(String icon);

		public AfterIconStep setIcon(
			UnsafeSupplier<String, Exception> iconUnsafeSupplier);

	}

	public interface InputValueStep {

		public AfterInputValueStep setInputValue(String inputValue);

		public AfterInputValueStep setInputValue(
			UnsafeSupplier<String, Exception> inputValueUnsafeSupplier);

	}

	public interface LabelStep {

		public AfterLabelStep setLabel(String label);

		public AfterLabelStep setLabel(
			UnsafeSupplier<String, Exception> labelUnsafeSupplier);

	}

	public interface PutDataStep {

		public AfterPutDataStep putData(String key, String value);

		public AfterPutDataStep putData(
			String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier);

	}

	public interface QuickActionStep {

		public AfterQuickActionStep setQuickAction(boolean quickAction);

		public AfterQuickActionStep setQuickAction(
			UnsafeSupplier<Boolean, Exception> quickActionUnsafeSupplier);

	}

	public interface SeparatorStep {

		public AfterSeparatorStep setSeparator(boolean separator);

		public AfterSeparatorStep setSeparator(
			UnsafeSupplier<Boolean, Exception> separatorUnsafeSupplier);

	}

	public interface SetDataStep {

		public AfterSetDataStep setData(Map<String, Object> data);

	}

	public interface TargetStep {

		public AfterTargetStep setTarget(String target);

		public AfterTargetStep setTarget(
			UnsafeSupplier<String, Exception> targetUnsafeSupplier);

	}

	public interface TypeStep {

		public AfterTypeStep setType(String type);

		public AfterTypeStep setType(
			UnsafeSupplier<String, Exception> typeUnsafeSupplier);

	}

}