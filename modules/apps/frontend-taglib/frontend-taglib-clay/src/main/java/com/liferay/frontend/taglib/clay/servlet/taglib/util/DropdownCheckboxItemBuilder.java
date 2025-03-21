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
public class DropdownCheckboxItemBuilder {

	public static AfterPutDataStep putData(String key, String value) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.putData(key, value);
	}

	public static AfterPutDataStep putData(
		String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.putData(key, valueUnsafeSupplier);
	}

	public static AfterActiveStep setActive(boolean active) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setActive(active);
	}

	public static AfterActiveStep setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setActive(activeUnsafeSupplier);
	}

	public static AfterCheckedStep setChecked(boolean checked) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setChecked(checked);
	}

	public static AfterCheckedStep setChecked(
		UnsafeSupplier<Boolean, Exception> checkedUnsafeSupplier) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setChecked(checkedUnsafeSupplier);
	}

	public static AfterSetDataStep setData(Map<String, Object> data) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setData(data);
	}

	public static AfterDisabledStep setDisabled(boolean disabled) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setDisabled(disabled);
	}

	public static AfterDisabledStep setDisabled(
		UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setDisabled(disabledUnsafeSupplier);
	}

	public static AfterHrefStep setHref(Object href) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setHref(href);
	}

	public static AfterHrefStep setHref(
		PortletURL portletURL, Object... parameters) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setHref(portletURL, parameters);
	}

	public static AfterHrefStep setHref(
		UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setHref(hrefUnsafeSupplier);
	}

	public static AfterIconStep setIcon(String icon) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setIcon(icon);
	}

	public static AfterIconStep setIcon(
		UnsafeSupplier<String, Exception> iconUnsafeSupplier) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setIcon(iconUnsafeSupplier);
	}

	public static AfterInputNameStep setInputName(String inputName) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setInputName(inputName);
	}

	public static AfterInputNameStep setInputName(
		UnsafeSupplier<String, Exception> inputNameUnsafeSupplier) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setInputName(inputNameUnsafeSupplier);
	}

	public static AfterInputValueStep setInputValue(String inputValue) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setInputValue(inputValue);
	}

	public static AfterInputValueStep setInputValue(
		UnsafeSupplier<String, Exception> inputValueUnsafeSupplier) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setInputValue(inputValueUnsafeSupplier);
	}

	public static AfterLabelStep setLabel(String label) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setLabel(label);
	}

	public static AfterLabelStep setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setLabel(labelUnsafeSupplier);
	}

	public static AfterQuickActionStep setQuickAction(boolean quickAction) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setQuickAction(quickAction);
	}

	public static AfterQuickActionStep setQuickAction(
		UnsafeSupplier<Boolean, Exception> quickActionUnsafeSupplier) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setQuickAction(
			quickActionUnsafeSupplier);
	}

	public static AfterSeparatorStep setSeparator(boolean separator) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setSeparator(separator);
	}

	public static AfterSeparatorStep setSeparator(
		UnsafeSupplier<Boolean, Exception> separatorUnsafeSupplier) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setSeparator(separatorUnsafeSupplier);
	}

	public static AfterTargetStep setTarget(String target) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setTarget(target);
	}

	public static AfterTargetStep setTarget(
		UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setTarget(targetUnsafeSupplier);
	}

	public static AfterTypeStep setType(String type) {
		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setType(type);
	}

	public static AfterTypeStep setType(
		UnsafeSupplier<String, Exception> typeUnsafeSupplier) {

		DropdownCheckboxItemStep dropdownCheckboxItemStep =
			new DropdownCheckboxItemStep();

		return dropdownCheckboxItemStep.setType(typeUnsafeSupplier);
	}

	public static class DropdownCheckboxItemStep
		implements ActiveStep, AfterActiveStep, AfterCheckedStep,
				   AfterDisabledStep, AfterHrefStep, AfterIconStep,
				   AfterInputNameStep, AfterInputValueStep, AfterLabelStep,
				   AfterPutDataStep, AfterQuickActionStep, AfterSeparatorStep,
				   AfterSetDataStep, AfterTargetStep, AfterTypeStep, BuildStep,
				   CheckedStep, DisabledStep, HrefStep, IconStep, InputNameStep,
				   InputValueStep, LabelStep, PutDataStep, QuickActionStep,
				   SeparatorStep, SetDataStep, TargetStep, TypeStep {

		@Override
		public DropdownCheckboxItem build() {
			return _dropdownCheckboxItem;
		}

		@Override
		public AfterPutDataStep putData(String key, String value) {
			_dropdownCheckboxItem.putData(key, value);

			return this;
		}

		@Override
		public AfterPutDataStep putData(
			String key, UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

			try {
				String value = valueUnsafeSupplier.get();

				if (value != null) {
					_dropdownCheckboxItem.putData(key, value);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterActiveStep setActive(boolean active) {
			_dropdownCheckboxItem.setActive(active);

			return this;
		}

		@Override
		public AfterActiveStep setActive(
			UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

			try {
				Boolean active = activeUnsafeSupplier.get();

				if (active != null) {
					_dropdownCheckboxItem.setActive(active.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterCheckedStep setChecked(boolean checked) {
			_dropdownCheckboxItem.setChecked(checked);

			return this;
		}

		@Override
		public AfterCheckedStep setChecked(
			UnsafeSupplier<Boolean, Exception> checkedUnsafeSupplier) {

			try {
				Boolean checked = checkedUnsafeSupplier.get();

				if (checked != null) {
					_dropdownCheckboxItem.setChecked(checked.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterSetDataStep setData(Map<String, Object> data) {
			_dropdownCheckboxItem.setData(data);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(boolean disabled) {
			_dropdownCheckboxItem.setDisabled(disabled);

			return this;
		}

		@Override
		public AfterDisabledStep setDisabled(
			UnsafeSupplier<Boolean, Exception> disabledUnsafeSupplier) {

			try {
				Boolean disabled = disabledUnsafeSupplier.get();

				if (disabled != null) {
					_dropdownCheckboxItem.setDisabled(disabled.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterHrefStep setHref(Object href) {
			_dropdownCheckboxItem.setHref(href);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			PortletURL portletURL, Object... parameters) {

			_dropdownCheckboxItem.setHref(portletURL, parameters);

			return this;
		}

		@Override
		public AfterHrefStep setHref(
			UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {

			try {
				Object href = hrefUnsafeSupplier.get();

				if (href != null) {
					_dropdownCheckboxItem.setHref(href);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterIconStep setIcon(String icon) {
			_dropdownCheckboxItem.setIcon(icon);

			return this;
		}

		@Override
		public AfterIconStep setIcon(
			UnsafeSupplier<String, Exception> iconUnsafeSupplier) {

			try {
				String icon = iconUnsafeSupplier.get();

				if (icon != null) {
					_dropdownCheckboxItem.setIcon(icon);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterInputNameStep setInputName(String inputName) {
			_dropdownCheckboxItem.setInputName(inputName);

			return this;
		}

		@Override
		public AfterInputNameStep setInputName(
			UnsafeSupplier<String, Exception> inputNameUnsafeSupplier) {

			try {
				String inputName = inputNameUnsafeSupplier.get();

				if (inputName != null) {
					_dropdownCheckboxItem.setInputName(inputName);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterInputValueStep setInputValue(String inputValue) {
			_dropdownCheckboxItem.setInputValue(inputValue);

			return this;
		}

		@Override
		public AfterInputValueStep setInputValue(
			UnsafeSupplier<String, Exception> inputValueUnsafeSupplier) {

			try {
				String inputValue = inputValueUnsafeSupplier.get();

				if (inputValue != null) {
					_dropdownCheckboxItem.setInputValue(inputValue);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterLabelStep setLabel(String label) {
			_dropdownCheckboxItem.setLabel(label);

			return this;
		}

		@Override
		public AfterLabelStep setLabel(
			UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

			try {
				String label = labelUnsafeSupplier.get();

				if (label != null) {
					_dropdownCheckboxItem.setLabel(label);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterQuickActionStep setQuickAction(boolean quickAction) {
			_dropdownCheckboxItem.setQuickAction(quickAction);

			return this;
		}

		@Override
		public AfterQuickActionStep setQuickAction(
			UnsafeSupplier<Boolean, Exception> quickActionUnsafeSupplier) {

			try {
				Boolean quickAction = quickActionUnsafeSupplier.get();

				if (quickAction != null) {
					_dropdownCheckboxItem.setQuickAction(
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
			_dropdownCheckboxItem.setSeparator(separator);

			return this;
		}

		@Override
		public AfterSeparatorStep setSeparator(
			UnsafeSupplier<Boolean, Exception> separatorUnsafeSupplier) {

			try {
				Boolean separator = separatorUnsafeSupplier.get();

				if (separator != null) {
					_dropdownCheckboxItem.setSeparator(
						separator.booleanValue());
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterTargetStep setTarget(String target) {
			_dropdownCheckboxItem.setTarget(target);

			return this;
		}

		@Override
		public AfterTargetStep setTarget(
			UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

			try {
				String target = targetUnsafeSupplier.get();

				if (target != null) {
					_dropdownCheckboxItem.setTarget(target);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		@Override
		public AfterTypeStep setType(String type) {
			_dropdownCheckboxItem.setType(type);

			return this;
		}

		@Override
		public AfterTypeStep setType(
			UnsafeSupplier<String, Exception> typeUnsafeSupplier) {

			try {
				String type = typeUnsafeSupplier.get();

				if (type != null) {
					_dropdownCheckboxItem.setType(type);
				}

				return this;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		private final DropdownCheckboxItem _dropdownCheckboxItem =
			new DropdownCheckboxItem();

	}

	public interface ActiveStep {

		public AfterActiveStep setActive(boolean active);

		public AfterActiveStep setActive(
			UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier);

	}

	public interface AfterActiveStep
		extends BuildStep, CheckedStep, DisabledStep, HrefStep, IconStep,
				InputNameStep, InputValueStep, LabelStep, QuickActionStep,
				SeparatorStep, SetDataStep, TargetStep, TypeStep {
	}

	public interface AfterCheckedStep
		extends BuildStep, DisabledStep, HrefStep, IconStep, InputNameStep,
				InputValueStep, LabelStep, QuickActionStep, SeparatorStep,
				SetDataStep, TargetStep, TypeStep {
	}

	public interface AfterDisabledStep
		extends BuildStep, HrefStep, IconStep, InputNameStep, InputValueStep,
				LabelStep, QuickActionStep, SeparatorStep, TargetStep,
				TypeStep {
	}

	public interface AfterHrefStep
		extends BuildStep, IconStep, InputNameStep, InputValueStep, LabelStep,
				QuickActionStep, SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterIconStep
		extends BuildStep, InputNameStep, InputValueStep, LabelStep,
				QuickActionStep, SeparatorStep, TargetStep, TypeStep {
	}

	public interface AfterInputNameStep
		extends BuildStep, InputValueStep, QuickActionStep, SeparatorStep,
				TargetStep, TypeStep {
	}

	public interface AfterInputValueStep
		extends BuildStep, QuickActionStep, SeparatorStep, TargetStep,
				TypeStep {
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

		public DropdownCheckboxItem build();

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

	public interface InputNameStep {

		public AfterInputNameStep setInputName(String inputName);

		public AfterInputNameStep setInputName(
			UnsafeSupplier<String, Exception> inputNameUnsafeSupplier);

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